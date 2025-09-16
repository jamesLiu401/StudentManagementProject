import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Form, Row, Col, Badge, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';
import * as majorApi from '../api/major';

const TotalClasses = () => {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [totalClasses, setTotalClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    // 专业名称缓存映射
    const [majorNameMap, setMajorNameMap] = useState({});

    // 查询与排序
    const [keyword, setKeyword] = useState('');
    const [sortBy, setSortBy] = useState('totalClassId');
    const [sortDir, setSortDir] = useState('asc');

    // 删除确认
    const [confirmId, setConfirmId] = useState(null);
    const [deleting, setDeleting] = useState(false);

    const loadTotalClasses = async () => {
        try {
            setLoading(true);
            setError('');
            let response;
            if (keyword && keyword.trim() !== '') {
                response = await classApi.searchTotalClassesGeneral(keyword.trim());
                // 搜索接口返回的是 List，需要手动分页
                const allData = response?.data?.data || [];
                const start = currentPage * pageSize;
                const end = start + pageSize;
                const pageData = allData.slice(start, end);
                setTotalClasses(pageData);
                setTotalPages(Math.ceil(allData.length / pageSize));
            } else {
                response = await classApi.getTotalClassesPage({
                    page: currentPage,
                    size: pageSize,
                    sortBy,
                    sortDir
                });
                if (response && response.status === 200) {
                    const pageData = response?.data?.data || {};
                    setTotalClasses(Array.isArray(pageData.content) ? pageData.content : []);
                    setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
                } else {
                    setError('加载总班级数据失败');
                }
            }
        } catch (err) {
            console.error('加载总班级数据失败:', err);
            setError('加载总班级数据失败，请稍后重试');
            setTotalClasses([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadTotalClasses();
    }, [currentPage, sortBy, sortDir]);

    // 查询专业名称（带错误处理）
    const q_major_name = async (major_id) => {
        let response_major = await majorApi.getMajorById(parseInt(major_id.toString(), 10));
        try {
            if (response_major && response_major.status === 200) {
                const major = response_major?.data?.data || {};
                return major.majorName;
            } else {
                setError('加载专业数据失败');
            }
        } catch (err) {
            console.error('加载专业数据失败:', err);
            setError('加载专业数据失败，请稍后重试');
        }
        return null;
    };

    // 根据总班级列表批量加载专业名称并缓存
    useEffect(() => {
        const fetchMissingMajorNames = async () => {
            try {
                const ids = Array.from(new Set(totalClasses
                    .map(tc => tc.majorId)
                    .filter(id => id !== null && id !== undefined)));
                const missing = ids.filter(id => !(id in majorNameMap));
                if (missing.length === 0) return;
                const updates = {};
                for (const id of missing) {
                    const name = await q_major_name(id);
                    if (name) updates[id] = name;
                }
                if (Object.keys(updates).length > 0) {
                    setMajorNameMap(prev => ({ ...prev, ...updates }));
                }
            } catch (e) {
                // 已有全局 error 处理
            }
        };
        if (totalClasses && totalClasses.length > 0) {
            fetchMissingMajorNames();
        }
    }, [totalClasses]);

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setCurrentPage(0);
        loadTotalClasses();
    };

    const handleViewDetail = (totalClassId) => {
        navigate(`/total-classes/${totalClassId}`);
    };

    const handleAdd = () => {
        navigate('/total-classes/add');
    };

    const handleEdit = (totalClassId) => {
        navigate(`/total-classes/edit/${totalClassId}`);
    };

    const handleDelete = async (id) => {
        try {
            setDeleting(true);
            setError('');
            const resp = await classApi.deleteTotalClass(id);
            if (resp && resp.status === 200) {
                await loadTotalClasses();
            } else {
                setError('删除失败');
            }
        } catch (e) {
            console.error('删除失败:', e);
            setError(e?.response?.data?.message || '删除失败，请稍后重试');
        } finally {
            setDeleting(false);
            setConfirmId(null);
        }
    };

    if (loading && totalClasses.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载总班级数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>总班级管理</h2>
                {isAdmin() && (
                    <Button variant="primary" onClick={handleAdd}>
                        <i className="fas fa-plus me-2"></i>
                        添加总班级
                    </Button>
                )}
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Card className="mb-3">
                <Card.Header>
                    <h5 className="mb-0">查询与排序</h5>
                </Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSearchSubmit}>
                        <Row className="g-3 align-items-end">
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>总班级名称（模糊）</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="输入总班级名称关键词"
                                        value={keyword}
                                        onChange={(e) => setKeyword(e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group>
                                    <Form.Label>排序字段</Form.Label>
                                    <Form.Select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                                        <option value="totalClassId">ID</option>
                                        <option value="totalClassName">总班级名称</option>
                                        <option value="majorId">专业ID</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            <Col md={2}>
                                <Form.Group>
                                    <Form.Label>方向</Form.Label>
                                    <Form.Select value={sortDir} onChange={(e) => setSortDir(e.target.value)}>
                                        <option value="asc">升序</option>
                                        <option value="desc">降序</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            <Col md={3} className="d-flex gap-2">
                                <Button type="submit" variant="primary">查询</Button>
                                <Button
                                    type="button"
                                    variant="secondary"
                                    onClick={() => {
                                        setKeyword('');
                                        setSortBy('totalClassId');
                                        setSortDir('asc');
                                        setCurrentPage(0);
                                        loadTotalClasses();
                                    }}
                                >
                                    重置
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>

            <Card>
                <Card.Header>
                    <h5 className="mb-0">总班级列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>总班级名称</th>
                                <th>专业</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {totalClasses.map(tc => (
                                <tr key={tc.totalClassId}>
                                    <td>{tc.totalClassId}</td>
                                    <td>{tc.totalClassName}</td>
                                    <td>{majorNameMap[tc.majorId] || tc.majorId || '-'}</td>
                                    <td>
                                        <Button
                                            variant="outline-info"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleViewDetail(tc.totalClassId)}
                                        >
                                            <i className="fas fa-eye"></i>
                                        </Button>
                                        {isAdmin() && (
                                            <Button
                                                variant="outline-warning"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => handleEdit(tc.totalClassId)}
                                            >
                                                <i className="fas fa-edit"></i>
                                            </Button>
                                        )}
                                        {isAdmin() && (
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => setConfirmId(tc.totalClassId)}
                                            >
                                                <i className="fas fa-trash"></i>
                                            </Button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {totalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Pagination>
                                <Pagination.Prev disabled={currentPage === 0} onClick={() => setCurrentPage(currentPage - 1)} />
                                {Array.from({ length: totalPages }, (_, i) => (
                                    <Pagination.Item key={i} active={i === currentPage} onClick={() => setCurrentPage(i)}>
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next disabled={currentPage === totalPages - 1} onClick={() => setCurrentPage(currentPage + 1)} />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>

            <Modal show={confirmId !== null} onHide={() => setConfirmId(null)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>确认删除</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    确定要删除该总班级吗？此操作不可恢复。
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setConfirmId(null)} disabled={deleting}>取消</Button>
                    <Button variant="danger" onClick={() => handleDelete(confirmId)} disabled={deleting}>
                        {deleting ? '删除中...' : '删除'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default TotalClasses;
