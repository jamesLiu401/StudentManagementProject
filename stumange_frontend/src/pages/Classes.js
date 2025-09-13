import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Modal, Form, Row, Col } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';

const Classes = () => {
    const { isAdmin } = useAuth();

    // 总班级
    const [totals, setTotals] = useState([]);
    const [totalsLoading, setTotalsLoading] = useState(true);
    const [totalsError, setTotalsError] = useState('');
    const [totalsPage, setTotalsPage] = useState(0);
    const [totalsTotalPages, setTotalsTotalPages] = useState(0);

    // 子班级
    const [subs, setSubs] = useState([]);
    const [subsLoading, setSubsLoading] = useState(true);
    const [subsError, setSubsError] = useState('');
    const [subsPage, setSubsPage] = useState(0);
    const [subsTotalPages, setSubsTotalPages] = useState(0);

    const pageSize = 10;

    // 表单
    const [showModal, setShowModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [isEditingTotal, setIsEditingTotal] = useState(true);
    const [formData, setFormData] = useState({ totalClassName: '', majorId: '', subClassName: '', totalClassId: '' });

    const loadTotals = async () => {
        try {
            setTotalsLoading(true);
            setTotalsError('');
            const response = await classApi.getTotalClassesPage({ page: totalsPage, size: pageSize, sortBy: 'totalClassId', sortDir: 'asc' });
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setTotals(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalsTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setTotalsError('加载总班级数据失败');
            }
        } catch (err) {
            console.error('加载总班级数据失败:', err);
            setTotalsError('加载总班级数据失败，请稍后重试');
            setTotals([]);
            setTotalsTotalPages(0);
        } finally {
            setTotalsLoading(false);
        }
    };

    const loadSubs = async () => {
        try {
            setSubsLoading(true);
            setSubsError('');
            const response = await classApi.getSubClassesPage({ page: subsPage, size: pageSize, sortBy: 'subClassId', sortDir: 'asc' });
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setSubs(Array.isArray(pageData.content) ? pageData.content : []);
                setSubsTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setSubsError('加载子班级数据失败');
            }
        } catch (err) {
            console.error('加载子班级数据失败:', err);
            setSubsError('加载子班级数据失败，请稍后重试');
            setSubs([]);
            setSubsTotalPages(0);
        } finally {
            setSubsLoading(false);
        }
    };

    useEffect(() => { loadTotals(); }, [totalsPage]);
    useEffect(() => { loadSubs(); }, [subsPage]);

    const handleDeleteTotal = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这个总班级吗？')) return;
        try {
            await classApi.deleteTotalClass(id);
            loadTotals();
        } catch (err) {
            console.error('删除总班级失败:', err);
            setTotalsError('删除总班级失败，请稍后重试');
        }
    };

    const handleDeleteSub = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这个子班级吗？')) return;
        try {
            await classApi.deleteSubClass(id);
            loadSubs();
        } catch (err) {
            console.error('删除子班级失败:', err);
            setSubsError('删除子班级失败，请稍后重试');
        }
    };

    const openEditTotal = (item) => {
        setIsEditingTotal(true);
        setEditing(item);
        setFormData({ totalClassName: item.totalClassName || '', majorId: item.majorId || '', subClassName: '', totalClassId: '' });
        setShowModal(true);
    };

    const openEditSub = (item) => {
        setIsEditingTotal(false);
        setEditing(item);
        setFormData({ totalClassName: '', majorId: '', subClassName: item.subClassName || '', totalClassId: item.totalClassId || '' });
        setShowModal(true);
    };

    const openCreateTotal = () => {
        setIsEditingTotal(true);
        setEditing(null);
        setFormData({ totalClassName: '', majorId: '', subClassName: '', totalClassId: '' });
        setShowModal(true);
    };

    const openCreateSub = () => {
        setIsEditingTotal(false);
        setEditing(null);
        setFormData({ totalClassName: '', majorId: '', subClassName: '', totalClassId: '' });
        setShowModal(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEditingTotal) {
                const payload = { totalClassName: formData.totalClassName, majorId: parseInt(formData.majorId || 0, 10) };
                if (editing) {
                    await classApi.updateTotalClass(editing.totalClassId, payload);
                } else {
                    await classApi.createTotalClass(payload);
                }
                loadTotals();
            } else {
                const payload = { subClassName: formData.subClassName, totalClassId: parseInt(formData.totalClassId || 0, 10) };
                if (editing) {
                    await classApi.updateSubClass(editing.subClassId, payload);
                } else {
                    await classApi.createSubClass(payload);
                }
                loadSubs();
            }
            setShowModal(false);
            setEditing(null);
        } catch (err) {
            console.error('保存失败:', err);
            if (isEditingTotal) setTotalsError('保存总班级失败，请稍后重试'); else setSubsError('保存子班级失败，请稍后重试');
        }
    };

    if (totalsLoading && totals.length === 0 && subsLoading && subs.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载班级数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>班级管理</h2>
                {isAdmin() && (
                    <div>
                        <Button variant="primary" className="me-2" onClick={openCreateTotal}><i className="fas fa-plus me-2"></i>添加总班级</Button>
                        <Button variant="primary" onClick={openCreateSub}><i className="fas fa-plus me-2"></i>添加子班级</Button>
                    </div>
                )}
            </div>

            {totalsError && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {totalsError}
                </Alert>
            )}

            <Card className="mb-4">
                <Card.Header><h5 className="mb-0">总班级</h5></Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>名称</th>
                                <th>专业ID</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {totals.map(tc => (
                                <tr key={tc.totalClassId}>
                                    <td>{tc.totalClassId}</td>
                                    <td>{tc.totalClassName}</td>
                                    <td>{tc.majorId || '-'}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button variant="outline-primary" size="sm" className="me-2" onClick={() => openEditTotal(tc)}><i className="fas fa-edit"></i></Button>
                                            <Button variant="outline-danger" size="sm" onClick={() => handleDeleteTotal(tc.totalClassId)}><i className="fas fa-trash"></i></Button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {totalsTotalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Pagination>
                                <Pagination.Prev disabled={totalsPage === 0} onClick={() => setTotalsPage(totalsPage - 1)} />
                                {Array.from({ length: totalsTotalPages }, (_, i) => (
                                    <Pagination.Item key={i} active={i === totalsPage} onClick={() => setTotalsPage(i)}>
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next disabled={totalsPage === totalsTotalPages - 1} onClick={() => setTotalsPage(totalsPage + 1)} />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>

            {subsError && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {subsError}
                </Alert>
            )}

            <Card>
                <Card.Header><h5 className="mb-0">子班级</h5></Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>名称</th>
                                <th>总班级ID</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {subs.map(sc => (
                                <tr key={sc.subClassId}>
                                    <td>{sc.subClassId}</td>
                                    <td>{sc.subClassName}</td>
                                    <td>{sc.totalClassId || '-'}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button variant="outline-primary" size="sm" className="me-2" onClick={() => openEditSub(sc)}><i className="fas fa-edit"></i></Button>
                                            <Button variant="outline-danger" size="sm" onClick={() => handleDeleteSub(sc.subClassId)}><i className="fas fa-trash"></i></Button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {subsTotalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Pagination>
                                <Pagination.Prev disabled={subsPage === 0} onClick={() => setSubsPage(subsPage - 1)} />
                                {Array.from({ length: subsTotalPages }, (_, i) => (
                                    <Pagination.Item key={i} active={i === subsPage} onClick={() => setSubsPage(i)}>
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next disabled={subsPage === subsTotalPages - 1} onClick={() => setSubsPage(subsPage + 1)} />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Form onSubmit={handleSubmit}>
                    <Modal.Header closeButton>
                        <Modal.Title>{editing ? (isEditingTotal ? '编辑总班级' : '编辑子班级') : (isEditingTotal ? '添加总班级' : '添加子班级')}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {isEditingTotal ? (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>总班级名称 *</Form.Label>
                                    <Form.Control type="text" value={formData.totalClassName} onChange={(e) => setFormData({ ...formData, totalClassName: e.target.value })} required />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>专业ID *</Form.Label>
                                    <Form.Control type="number" value={formData.majorId} onChange={(e) => setFormData({ ...formData, majorId: e.target.value })} required />
                                </Form.Group>
                            </>
                        ) : (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>子班级名称 *</Form.Label>
                                    <Form.Control type="text" value={formData.subClassName} onChange={(e) => setFormData({ ...formData, subClassName: e.target.value })} required />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>总班级ID *</Form.Label>
                                    <Form.Control type="number" value={formData.totalClassId} onChange={(e) => setFormData({ ...formData, totalClassId: e.target.value })} required />
                                </Form.Group>
                            </>
                        )}
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

export default Classes;
