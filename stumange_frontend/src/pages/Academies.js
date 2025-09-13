import React, { useState, useEffect } from 'react';
import { Container, Card, Table, Button, Form, Row, Col, Modal, Alert, Spinner, Pagination } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as academyApi from '../api/academy';

const Academies = () => {
    const { isAdmin } = useAuth();
    const [academies, setAcademies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingAcademy, setEditingAcademy] = useState(null);
    const [formData, setFormData] = useState({
        academyName: '',
        academyCode: '',
        description: '',
        deanName: '',
        contactPhone: '',
        address: ''
    });
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize] = useState(10);

    useEffect(() => {
        loadAcademies();
    }, [currentPage]);

    const loadAcademies = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await academyApi.getAcademiesPage({
                page: currentPage,
                size: pageSize,
                sortBy: 'academyId',
                sortDir: 'asc'
            });

            // 解包ResponseMessage
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setAcademies(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载学院数据失败');
            }
        } catch (err) {
            console.error('加载学院数据失败:', err);
            setError('加载学院数据失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingAcademy) {
                await academyApi.updateAcademy(editingAcademy.academyId, formData);
            } else {
                await academyApi.createAcademy(formData);
            }
            setShowModal(false);
            setEditingAcademy(null);
            setFormData({
                academyName: '',
                academyCode: '',
                description: '',
                deanName: '',
                contactPhone: '',
                address: ''
            });
            loadAcademies();
        } catch (err) {
            console.error('保存学院失败:', err);
            setError('保存学院失败，请稍后重试');
        }
    };

    const handleEdit = (academy) => {
        setEditingAcademy(academy);
        setFormData({
            academyName: academy.academyName || '',
            academyCode: academy.academyCode || '',
            description: academy.description || '',
            deanName: academy.deanName || '',
            contactPhone: academy.contactPhone || '',
            address: academy.address || ''
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('确定要删除这个学院吗？')) {
            try {
                await academyApi.deleteAcademy(id);
                loadAcademies();
            } catch (err) {
                console.error('删除学院失败:', err);
                setError('删除学院失败，请稍后重试');
            }
        }
    };

    const handlePageChange = (page) => {
        setCurrentPage(page - 1);
    };

    if (loading && academies.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载学院数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>学院管理</h2>
                {isAdmin() && (
                    <Button
                        variant="primary"
                        onClick={() => {
                            setEditingAcademy(null);
                            setFormData({
                                academyName: '',
                                academyCode: '',
                                description: '',
                                deanName: '',
                                contactPhone: '',
                                address: ''
                            });
                            setShowModal(true);
                        }}
                    >
                        <i className="fas fa-plus me-2"></i>
                        添加学院
                    </Button>
                )}
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Card>
                <Card.Header>
                    <h5 className="mb-0">学院列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>学院ID</th>
                                <th>学院名称</th>
                                <th>学院代码</th>
                                <th>院长</th>
                                <th>联系电话</th>
                                <th>地址</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {academies.map((academy) => (
                                <tr key={academy.academyId}>
                                    <td>{academy.academyId}</td>
                                    <td>{academy.academyName}</td>
                                    <td>{academy.academyCode}</td>
                                    <td>{academy.deanName}</td>
                                    <td>{academy.contactPhone}</td>
                                    <td>{academy.address}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button
                                                variant="outline-primary"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => handleEdit(academy)}
                                            >
                                                <i className="fas fa-edit"></i>
                                            </Button>
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDelete(academy.academyId)}
                                            >
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
                                <Pagination.Prev
                                    disabled={currentPage === 0}
                                    onClick={() => setCurrentPage(currentPage - 1)}
                                />
                                {Array.from({ length: totalPages }, (_, i) => (
                                    <Pagination.Item
                                        key={i}
                                        active={i === currentPage}
                                        onClick={() => setCurrentPage(i)}
                                    >
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next
                                    disabled={currentPage === totalPages - 1}
                                    onClick={() => setCurrentPage(currentPage + 1)}
                                />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>

            {/* 添加/编辑学院模态框 */}
            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>
                        {editingAcademy ? '编辑学院' : '添加学院'}
                    </Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleSubmit}>
                    <Modal.Body>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>学院名称 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="academyName"
                                        value={formData.academyName}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>学院代码 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="academyCode"
                                        value={formData.academyCode}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>院长姓名</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="deanName"
                                        value={formData.deanName}
                                        onChange={handleInputChange}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>联系电话</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="contactPhone"
                                        value={formData.contactPhone}
                                        onChange={handleInputChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Form.Group className="mb-3">
                            <Form.Label>地址</Form.Label>
                            <Form.Control
                                type="text"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>描述</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                            取消
                        </Button>
                        <Button variant="primary" type="submit">
                            {editingAcademy ? '更新' : '添加'}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    );
};

export default Academies;
