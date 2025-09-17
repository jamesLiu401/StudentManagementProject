import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge, Modal, Form } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as paymentApi from '../api/payment';
import * as studentApi from '../api/student';

const PaymentDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [payment, setPayment] = useState(null);
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showEditModal, setShowEditModal] = useState(false);
    const [editing, setEditing] = useState(false);
    const [formData, setFormData] = useState({
        paymentType: '',
        amount: '',
        paymentStatus: '',
        description: '',
        paymentDate: ''
    });

    // 缴费类型和状态选项
    const paymentTypes = ['学费', '住宿费', '教材费', '实验费', '考试费', '其他'];
    const paymentStatuses = ['未缴费', '已缴费', '部分缴费', '已退费'];

    useEffect(() => {
        const loadPaymentDetail = async () => {
            try {
                setLoading(true);
                setError('');

                const response = await paymentApi.getPaymentById(id);
                if (response && response.status === 200) {
                    const paymentData = response.data.data;
                    setPayment(paymentData);
                    setFormData({
                        paymentType: paymentData.paymentType || '',
                        amount: paymentData.amount || '',
                        paymentStatus: paymentData.paymentStatus || '',
                        description: paymentData.description || '',
                        paymentDate: paymentData.paymentDate || ''
                    });

                    // 加载学生信息
                    if (paymentData.stuId) {
                        try {
                            const studentResponse = await studentApi.getStudentById(paymentData.stuId);
                            if (studentResponse && studentResponse.status === 200) {
                                setStudent(studentResponse.data.data);
                            }
                        } catch (err) {
                            console.error('加载学生信息失败:', err);
                        }
                    }
                } else {
                    setError('缴费记录不存在');
                }
            } catch (err) {
                console.error('加载缴费详情失败:', err);
                setError('加载缴费详情失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadPaymentDetail();
        }
    }, [id]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleEditSubmit = async (e) => {
        e.preventDefault();
        
        if (!isAdmin()) {
            setError('您没有权限编辑缴费记录');
            return;
        }

        try {
            setEditing(true);
            setError('');

            const updateData = {
                ...formData,
                amount: parseFloat(formData.amount)
            };

            const response = await paymentApi.updatePayment(id, updateData);
            
            if (response && response.status === 200) {
                setPayment(response.data.data);
                setShowEditModal(false);
            } else {
                setError('更新缴费记录失败，请稍后重试');
            }
        } catch (err) {
            console.error('更新缴费记录失败:', err);
            setError(err.response?.data?.message || '更新缴费记录失败，请稍后重试');
        } finally {
            setEditing(false);
        }
    };

    const handleDelete = async () => {
        if (!isAdmin() || !window.confirm('确定要删除这条缴费记录吗？')) return;
        
        try {
            await paymentApi.deletePayment(id);
            navigate('/payments');
        } catch (err) {
            console.error('删除缴费记录失败:', err);
            setError('删除缴费记录失败，请稍后重试');
        }
    };

    const statusColor = (status) => {
        switch (status) {
            case '已缴费': return 'success';
            case '部分缴费': return 'warning';
            case '已退费': return 'info';
            default: return 'danger';
        }
    };

    const formatAmount = (amount) => {
        return `¥${parseFloat(amount || 0).toFixed(2)}`;
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('zh-CN');
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载缴费详情...</p>
            </Container>
        );
    }

    if (error && !payment) {
        return (
            <Container>
                <Alert variant="danger" className="mt-4">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
                <div className="text-center mt-4">
                    <Button variant="outline-primary" onClick={() => navigate('/payments')}>
                        <i className="fas fa-arrow-left me-2"></i>
                        返回缴费列表
                    </Button>
                </div>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>
                    <i className="fas fa-credit-card me-2"></i>
                    缴费详情
                </h2>
                <div>
                    <Button 
                        variant="outline-secondary" 
                        onClick={() => navigate('/payments')}
                        className="me-2"
                    >
                        <i className="fas fa-arrow-left me-2"></i>
                        返回列表
                    </Button>
                    {isAdmin() && (
                        <>
                            <Button 
                                variant="outline-primary" 
                                onClick={() => setShowEditModal(true)}
                                className="me-2"
                            >
                                <i className="fas fa-edit me-2"></i>
                                编辑
                            </Button>
                            <Button 
                                variant="outline-danger" 
                                onClick={handleDelete}
                            >
                                <i className="fas fa-trash me-2"></i>
                                删除
                            </Button>
                        </>
                    )}
                </div>
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Row>
                <Col lg={8}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-info-circle me-2"></i>
                                缴费信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>缴费ID：</strong>
                                        <span className="ms-2">{payment?.paymentId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>缴费类型：</strong>
                                        <Badge bg="secondary" className="ms-2">
                                            {payment?.paymentType}
                                        </Badge>
                                    </div>
                                    <div className="mb-3">
                                        <strong>缴费金额：</strong>
                                        <span className="ms-2 fw-bold text-primary fs-5">
                                            {formatAmount(payment?.amount)}
                                        </span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>缴费状态：</strong>
                                        <Badge bg={statusColor(payment?.paymentStatus)} className="ms-2">
                                            {payment?.paymentStatus}
                                        </Badge>
                                    </div>
                                    <div className="mb-3">
                                        <strong>缴费日期：</strong>
                                        <span className="ms-2">{formatDate(payment?.paymentDate)}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>创建时间：</strong>
                                        <span className="ms-2">{formatDate(payment?.createdAt)}</span>
                                    </div>
                                </Col>
                            </Row>
                            
                            {payment?.description && (
                                <div className="mt-3">
                                    <strong>备注说明：</strong>
                                    <div className="mt-2 p-3 bg-light rounded">
                                        {payment.description}
                                    </div>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={4}>
                    {student && (
                        <Card>
                            <Card.Header>
                                <h6 className="mb-0">
                                    <i className="fas fa-user-circle me-2"></i>
                                    学生信息
                                </h6>
                            </Card.Header>
                            <Card.Body>
                                <div className="mb-2">
                                    <strong>姓名：</strong> {student.stuName}
                                </div>
                                <div className="mb-2">
                                    <strong>学号：</strong> {student.stuId}
                                </div>
                                <div className="mb-2">
                                    <strong>性别：</strong> {student.stuGender}
                                </div>
                                <div className="mb-2">
                                    <strong>年级：</strong> {student.stuGrade}
                                </div>
                                <div className="mb-2">
                                    <strong>电话：</strong> {student.stuTelephoneNo}
                                </div>
                                <div className="mb-2">
                                    <strong>地址：</strong> {student.stuAddress}
                                </div>
                                <div className="mt-3">
                                    <Button 
                                        variant="outline-primary" 
                                        size="sm"
                                        onClick={() => navigate(`/students/${student.stuId}`)}
                                    >
                                        <i className="fas fa-eye me-2"></i>
                                        查看学生详情
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    )}

                    <Card className="mt-3">
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-chart-line me-2"></i>
                                操作历史
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-muted">
                                <small>
                                    <i className="fas fa-info-circle me-2"></i>
                                    操作历史功能正在开发中
                                </small>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 编辑对话框 */}
            <Modal show={showEditModal} onHide={() => setShowEditModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>
                        <i className="fas fa-edit me-2"></i>
                        编辑缴费记录
                    </Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleEditSubmit}>
                    <Modal.Body>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>缴费类型</Form.Label>
                                    <Form.Select
                                        name="paymentType"
                                        value={formData.paymentType}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        {paymentTypes.map(type => (
                                            <option key={type} value={type}>{type}</option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>缴费状态</Form.Label>
                                    <Form.Select
                                        name="paymentStatus"
                                        value={formData.paymentStatus}
                                        onChange={handleInputChange}
                                    >
                                        {paymentStatuses.map(status => (
                                            <option key={status} value={status}>{status}</option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>缴费金额</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="amount"
                                        value={formData.amount}
                                        onChange={handleInputChange}
                                        placeholder="请输入缴费金额"
                                        min="0"
                                        step="0.01"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>缴费日期</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="paymentDate"
                                        value={formData.paymentDate}
                                        onChange={handleInputChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-3">
                            <Form.Label>备注说明</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                                placeholder="请输入备注说明（可选）"
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="outline-secondary" onClick={() => setShowEditModal(false)}>
                            取消
                        </Button>
                        <Button variant="primary" type="submit" disabled={editing}>
                            {editing ? (
                                <>
                                    <Spinner animation="border" size="sm" className="me-2" />
                                    保存中...
                                </>
                            ) : (
                                '保存更改'
                            )}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    );
};

export default PaymentDetail;