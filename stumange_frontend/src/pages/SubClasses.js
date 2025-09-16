import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Form, Row, Col, Badge, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';

const SubClasses = () => {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [subClasses, setSubClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    // 总班级名称缓存映射
    const [totalClassNameMap, setTotalClassNameMap] = useState({});

    // 查询与排序
    const [keyword, setKeyword] = useState('');
    const [sortBy, setSortBy] = useState('subClassId');
    const [sortDir, setSortDir] = useState('asc');

    // 删除确认
    const [confirmId, setConfirmId] = useState(null);
    const [deleting, setDeleting] = useState(false);

    const loadSubClasses = async () => {
        try {
            setLoading(true);
            setError('');
            let response;
            if (keyword && keyword.trim() !== '') {
                response = await classApi.searchSubClassesGeneral(keyword.trim());
                // 搜索接口返回的是 List，需要手动分页
                const allData = response?.data?.data || [];
                const start = currentPage * pageSize;
                const end = start + pageSize;
                const pageData = allData.slice(start, end);
                setSubClasses(pageData);
                setTotalPages(Math.ceil(allData.length / pageSize));
            } else {
                response = await classApi.getSubClassesPage({
                    page: currentPage,
                    size: pageSize,
                    sortBy,
                    sortDir
                });
                if (response && response.status === 200) {
                    const pageData = response?.data?.data || {};
                    setSubClasses(Array.isArray(pageData.content) ? pageData.content : []);
                    setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
                } else {
                    setError('加载子班级数据失败');
                }
            }
        } catch (err) {
            console.error('加载子班级数据失败:', err);
            setError('加载子班级数据失败，请稍后重试');
            setSubClasses([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadSubClasses();
    }, [currentPage, sortBy, sortDir]);

    // 查询总班级名称（带错误处理）
    const q_total_class_name = async (total_class_id) => {
        let response_tc = await classApi.getTotalClassById(parseInt(total_class_id.toString(), 10));
        try {
            if (response_tc && response_tc.status === 200) {
                const totalClass = response_tc?.data?.data || {};
                return totalClass.totalClassName;
            } else {
                setError('加载总班级数据失败');
            }
        } catch (err) {
            console.error('加载总班级数据失败:', err);
            setError('加载总班级数据失败，请稍后重试');
        }
        return null;
    };

    // 根据子班级列表批量加载总班级名称并缓存
    useEffect(() => {
        const fetchMissingTotalClassNames = async () => {
            try {
                const ids = Array.from(new Set(subClasses
                    .map(sc => sc.totalClassId)
                    .filter(id => id !== null && id !== undefined)));
                const missing = ids.filter(id => !(id in totalClassNameMap));
                if (missing.length === 0) return;
                const updates = {};
                for (const id of missing) {
                    const name = await q_total_class_name(id);
                    if (name) updates[id] = name;
                }
                if (Object.keys(updates).length > 0) {
                    setTotalClassNameMap(prev => ({ ...prev, ...updates }));
                }
            } catch (e) {
                // 已有全局 error 处理
            }
        };
        if (subClasses && subClasses.length > 0) {
            fetchMissingTotalClassNames();
        }
    }, [subClasses]);

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setCurrentPage(0);
        loadSubClasses();
    };

    const handleViewDetail = (subClassId) => {
        navigate(`/sub-classes/${subClassId}`);
    };

    const handleAdd = () => {
        navigate('/sub-classes/add');
    };

    const handleEdit = (subClassId) => {
        navigate(`/sub-classes/edit/${subClassId}`);
    };

    const handleDelete = async (id) => {
        try {
            setDeleting(true);
            setError('');
            const resp = await classApi.deleteSubClass(id);
            if (resp && resp.status === 200) {
                await loadSubClasses();
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

    if (loading && subClasses.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载子班级数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>子班级管理</h2>
                {isAdmin() && (
                    <Button variant="primary" onClick={handleAdd}>
                        <i className="fas fa-plus me-2"></i>
                        添加子班级
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
                                    <Form.Label>子班级名称（模糊）</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="输入子班级名称关键词"
                                        value={keyword}
                                        onChange={(e) => setKeyword(e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group>
                                    <Form.Label>排序字段</Form.Label>
                                    <Form.Select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                                        <option value="subClassId">ID</option>
                                        <option value="subClassName">子班级名称</option>
                                        <option value="totalClassId">总班级ID</option>
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
                                        setSortBy('subClassId');
                                        setSortDir('asc');
                                        setCurrentPage(0);
                                        loadSubClasses();
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
                    <h5 className="mb-0">子班级列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>子班级名称</th>
                                <th>总班级</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {subClasses.map(sc => (
                                <tr key={sc.subClassId}>
                                    <td>{sc.subClassId}</td>
                                    <td>{sc.subClassName}</td>
                                    <td>{totalClassNameMap[sc.totalClassId] || sc.totalClassId || '-'}</td>
                                    <td>
                                        <Button
                                            variant="outline-info"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleViewDetail(sc.subClassId)}
                                        >
                                            <i className="fas fa-eye"></i>
                                        </Button>
                                        {isAdmin() && (
                                            <Button
                                                variant="outline-warning"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => handleEdit(sc.subClassId)}
                                            >
                                                <i className="fas fa-edit"></i>
                                            </Button>
                                        )}
                                        {isAdmin() && (
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => setConfirmId(sc.subClassId)}
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
                    确定要删除该子班级吗？此操作不可恢复。
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

export default SubClasses;
