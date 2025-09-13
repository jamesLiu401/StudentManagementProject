import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Form, Modal, Row, Col } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as majorApi from '../api/major';
import * as AcademyApi from '../api/academy';

const Majors = () => {
    const { isAdmin } = useAuth();
    const [majors, setMajors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    const [showModal, setShowModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [formData, setFormData] = useState({ majorName: '', grade: '', academyId: '', counselorId: '' });

    // 学院名称缓存映射
    const [academyNameMap, setAcademyNameMap] = useState({});

    // 查询与排序
    const [keyword, setKeyword] = useState('');
    const [gradeFilter, setGradeFilter] = useState('');
    const [sortBy, setSortBy] = useState('majorId');
    const [sortDir, setSortDir] = useState('asc');

    const loadMajors = async () => {
        try {
            setLoading(true);
            setError('');
            let response;
            if (keyword && keyword.trim() !== '') {
                response = await majorApi.getMajorsByNamePage(keyword.trim(), {
                    page: currentPage,
                    size: pageSize,
                    sortBy,
                    sortDir
                });
            } else if (gradeFilter !== '') {
                const gradeVal = parseInt(gradeFilter, 10);
                response = await majorApi.getMajorsByGradePage(Number.isNaN(gradeVal) ? 0 : gradeVal, {
                    page: currentPage,
                    size: pageSize,
                    sortBy,
                    sortDir
                });
            } else {
                response = await majorApi.getMajorsPage({ page: currentPage, size: pageSize, sortBy, sortDir });
            }
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setMajors(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载专业数据失败');
            }
        } catch (err) {
            console.error('加载专业数据失败:', err);
            setError('加载专业数据失败，请稍后重试');
            setMajors([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadMajors();
    }, [currentPage, sortBy, sortDir]);

    // 查询学院名称（带错误处理）
    const q_academy_name = async (academy_id) => {
        let response_aca = await AcademyApi.getAcademyById(parseInt(academy_id.toString(), 10));
        try {
            if (response_aca && response_aca.status === 200) {
                const academy = response_aca?.data?.data || {};
                return academy.academyName;
            } else {
                setError('加载学院数据失败');
            }
        } catch (err) {
            console.error('加载学院数据失败:', err);
            setError('加载学院数据失败，请稍后重试');
        }
        return null;
    };

    // 根据专业列表批量加载学院名称并缓存
    useEffect(() => {
        const fetchMissingAcademyNames = async () => {
            try {
                const ids = Array.from(new Set(majors
                    .map(m => m.academyId)
                    .filter(id => id !== null && id !== undefined)));
                const missing = ids.filter(id => !(id in academyNameMap));
                if (missing.length === 0) return;
                const updates = {};
                for (const id of missing) {
                    const name = await q_academy_name(id);
                    if (name) updates[id] = name;
                }
                if (Object.keys(updates).length > 0) {
                    setAcademyNameMap(prev => ({ ...prev, ...updates }));
                }
            } catch (e) {
                // 已有全局 error 处理，不重复提示
            }
        };
        if (majors && majors.length > 0) {
            fetchMissingAcademyNames();
        }
    }, [majors]);

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setCurrentPage(0);
        loadMajors();
    };

    const handleDelete = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这个专业吗？')) return;
        try {
            await majorApi.deleteMajor(id);
            loadMajors();
        } catch (err) {
            console.error('删除专业失败:', err);
            setError('删除专业失败，请稍后重试');
        }
    };

    const handleEdit = (item) => {
        setEditing(item);
        setFormData({
            majorName: item.majorName || '',
            grade: item.grade || '',
            academyId: item.academyId || '',
            counselorId: item.counselorId || '',
        });
        setShowModal(true);
    };

    const handleCreate = () => {
        setEditing(null);
        setFormData({ majorName: '', grade: '', academyId: '', counselorId: ''});
        setShowModal(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const gradeVal = parseInt(formData.grade.toString() || 0, 10);
            const academyIdVal = formData.academyId !== '' ? parseInt(formData.academyId, 10) : null;
            const counselorIdVal = formData.counselorId !== '' ? parseInt(formData.counselorId, 10) : null;
            const payload = {
                majorName: formData.majorName,
                grade: Number.isNaN(gradeVal) ? 0 : gradeVal,
                academy: academyIdVal ? { academyId: academyIdVal } : null,
                counselor: counselorIdVal ? { teacherId: counselorIdVal } : null
            };
            if (editing) {
                await majorApi.updateMajor(editing.majorId, payload);
            } else {
                await majorApi.createMajor(payload);
            }
            setShowModal(false);
            setEditing(null);
            loadMajors();
        } catch (err) {
            console.error('保存专业失败:', err);
            setError('保存专业失败，请稍后重试');
        }
    };

    if (loading && majors.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载专业数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>专业管理</h2>
                {isAdmin() && (
                    <Button variant="primary" onClick={handleCreate}>
                        <i className="fas fa-plus me-2"></i>
                        添加专业
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
                                    <Form.Label>专业名称（模糊）</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="输入专业名称关键词"
                                        value={keyword}
                                        onChange={(e) => setKeyword(e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group>
                                    <Form.Label>年级筛选</Form.Label>
                                    <Form.Control
                                        type="number"
                                        placeholder="例如 2023"
                                        value={gradeFilter}
                                        onChange={(e) => setGradeFilter(e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group>
                                    <Form.Label>排序字段</Form.Label>
                                    <Form.Select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                                        <option value="majorId">ID</option>
                                        <option value="majorName">专业名称</option>
                                        <option value="grade">年级</option>
                                        <option value="academy.academyId">学院ID</option>
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
                            <Col md={12} className="d-flex gap-2">
                                <Button type="submit" variant="primary">查询</Button>
                                <Button
                                    type="button"
                                    variant="secondary"
                                    onClick={() => {
                                        setKeyword('');
                                        setGradeFilter('');
                                        setSortBy('majorId');
                                        setSortDir('asc');
                                        setCurrentPage(0);
                                        loadMajors();
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
                    <h5 className="mb-0">专业列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>专业名称</th>
                                <th>年级</th>
                                <th>辅导员</th>
                                <th>学院</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {majors.map(m => (
                                <tr key={m.majorId}>
                                    <td>{m.majorId}</td>
                                    <td>{m.majorName}</td>
                                    <td>{m.grade}</td>
                                    <td>{m.counselorId || '-'}</td>
                                    <td>{academyNameMap[m.academyId] || m.academyId || '-'}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button variant="outline-primary" size="sm" className="me-2" onClick={() => handleEdit(m)}>
                                                <i className="fas fa-edit"></i>
                                            </Button>
                                            <Button variant="outline-danger" size="sm" onClick={() => handleDelete(m.majorId)}>
                                                <i className="fas fa-trash"></i>
                                            </Button>
                                        </td>
                                    )}
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

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Form onSubmit={handleSubmit}>
                    <Modal.Header closeButton>
                        <Modal.Title>{editing ? '编辑专业' : '添加专业'}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>专业名称 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={formData.majorName}
                                        onChange={(e) => setFormData({ ...formData, majorName: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>年级 *</Form.Label>
                                    <Form.Control
                                        type="number"
                                        value={formData.grade}
                                        onChange={(e) => setFormData({ ...formData, grade: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Form.Group className="mb-3">
                            <Form.Label>学院ID</Form.Label>
                            <Form.Control
                                type="number"
                                value={formData.academyId}
                                onChange={(e) => setFormData({ ...formData, academyId: e.target.value })}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>辅导员ID</Form.Label>
                            <Form.Control
                                type="number"
                                value={formData.counselorId}
                                onChange={(e) => setFormData({ ...formData, counselorId: e.target.value })}
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>取消</Button>
                        <Button variant="primary" type="submit">{editing ? '更新' : '添加'}</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    );
};

export default Majors;
