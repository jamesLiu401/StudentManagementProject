import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col, Modal } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as paymentApi from '../api/payment';
import * as studentApi from '../api/student';

const EditPayment = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [payment, setPayment] = useState(null);
    const [student, setStudent] = useState(null);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [formData, setFormData] = useState({
        paymentType: '',
        amount: '',
        paymentStatus: '',
        description: '',
        paymentDate: ''
    });

    // 缴费类型选项
    const paymentTypes = [
        '学费', '住宿费', '教材费', '实验费', '考试费', '其他'
    ];

    // 缴费状态选项
    const paymentStatuses = [
        '未缴费', '已缴费', '部分缴费', '已退费'
    ];

    // 加载缴费记录详情
    useEffect(() => {
        const loadPayment = async () => {
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
                console.error('加载缴费记录失败:', err);
                setError('加载缴费记录失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadPayment();
        }
    }, [id]);

    // 处理表单输入变化
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // 验证表单
    const validateForm = () => {
        if (!formData.amount || formData.amount <= 0) {
            setError('请输入有效的缴费金额');
            return false;
        }
        if (!formData.paymentType) {
            setError('请选择缴费类型');
            return false;
        }
        if (!formData.paymentStatus) {
            setError('请选择缴费状态');
            return false;
        }
        return true;
    };

    // 提交表单
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!isAdmin()) {
            setError('您没有权限编辑缴费记录');
            return;
        }

        if (!validateForm()) {
            return;
        }

        setShowConfirmModal(true);
    };

    // 确认保存
    const handleConfirmSave = async () => {
        try {
            setSaving(true);
            setError('');
            setSuccess('');

            const updateData = {
                ...formData,
                amount: parseFloat(formData.amount)
            };

            const response = await paymentApi.updatePayment(id, updateData);
            
            if (response && response.status === 200) {
                setSuccess('缴费记录更新成功！');
                setPayment(response.data.data);
                setShowConfirmModal(false);
                
                // 2秒后跳转到缴费详情页面
                setTimeout(() => {
                    navigate(`/payments/${id}`);
                }, 2000);
            } else {
                setError('更新缴费记录失败，请稍后重试');
            }
        } catch (err) {
            console.error('更新缴费记录失败:', err);
            setError(err.response?.data?.message || '更新缴费记录失败，请稍后重试');
        } finally {
            setSaving(false);
            setShowConfirmModal(false);
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

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载缴费记录...</p>
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
                    <i className="fas fa-edit me-2"></i>
                    编辑缴费记录
                </h2>
                <Button 
                    variant="outline-secondary" 
                    onClick={() => navigate(`/payments/${id}`)}
                >
                    <i className="fas fa-arrow-left me-2"></i>
                    返回详情
                </Button>
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            {success && (
                <Alert variant="success" className="mb-3">
                    <i className="fas fa-check-circle me-2"></i>
                    {success}
                </Alert>
            )}

            <Row>
                <Col lg={8}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-credit-card me-2"></i>
                                缴费信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Form onSubmit={handleSubmit}>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                <i className="fas fa-tag me-2"></i>
                                                缴费类型 <span className="text-danger">*</span>
                                            </Form.Label>
                                            <Form.Select
                                                name="paymentType"
                                                value={formData.paymentType}
                                                onChange={handleInputChange}
                                                required
                                            >
                                                <option value="">请选择缴费类型</option>
                                                {paymentTypes.map(type => (
                                                    <option key={type} value={type}>{type}</option>
                                                ))}
                                            </Form.Select>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                <i className="fas fa-info-circle me-2"></i>
                                                缴费状态 <span className="text-danger">*</span>
                                            </Form.Label>
                                            <Form.Select
                                                name="paymentStatus"
                                                value={formData.paymentStatus}
                                                onChange={handleInputChange}
                                                required
                                            >
                                                <option value="">请选择缴费状态</option>
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
                                            <Form.Label>
                                                <i className="fas fa-dollar-sign me-2"></i>
                                                缴费金额 <span className="text-danger">*</span>
                                            </Form.Label>
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
                                            <Form.Label>
                                                <i className="fas fa-calendar me-2"></i>
                                                缴费日期
                                            </Form.Label>
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
                                    <Form.Label>
                                        <i className="fas fa-comment me-2"></i>
                                        备注说明
                                    </Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        name="description"
                                        value={formData.description}
                                        onChange={handleInputChange}
                                        placeholder="请输入备注说明（可选）"
                                    />
                                </Form.Group>

                                <div className="d-flex justify-content-end gap-2">
                                    <Button 
                                        variant="outline-secondary" 
                                        onClick={() => navigate(`/payments/${id}`)}
                                        disabled={saving}
                                    >
                                        取消
                                    </Button>
                                    <Button 
                                        variant="primary" 
                                        type="submit"
                                        disabled={saving}
                                    >
                                        {saving ? (
                                            <>
                                                <Spinner animation="border" size="sm" className="me-2" />
                                                保存中...
                                            </>
                                        ) : (
                                            <>
                                                <i className="fas fa-save me-2"></i>
                                                保存更改
                                            </>
                                        )}
                                    </Button>
                                </div>
                            </Form>
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
                            </Card.Body>
                        </Card>
                    )}

                    <Card className="mt-3">
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-info-circle me-2"></i>
                                当前信息
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-2">
                                <strong>缴费ID：</strong> {payment?.paymentId}
                            </div>
                            <div className="mb-2">
                                <strong>学生ID：</strong> {payment?.stuId}
                            </div>
                            <div className="mb-2">
                                <strong>当前状态：</strong>
                                <span className={`badge bg-${statusColor(payment?.paymentStatus)} ms-2`}>
                                    {payment?.paymentStatus}
                                </span>
                            </div>
                            <div className="mb-2">
                                <strong>当前金额：</strong> {formatAmount(payment?.amount)}
                            </div>
                        </Card.Body>
                    </Card>

                    <Card className="mt-3">
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-lightbulb me-2"></i>
                                编辑提示
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <ul className="list-unstyled mb-0">
                                <li className="mb-2">
                                    <i className="fas fa-check text-success me-2"></i>
                                    请仔细核对修改信息
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-check text-success me-2"></i>
                                    确保金额和状态准确无误
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-check text-success me-2"></i>
                                    修改后会自动更新记录
                                </li>
                                <li>
                                    <i className="fas fa-check text-success me-2"></i>
                                    建议添加备注说明修改原因
                                </li>
                            </ul>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 确认对话框 */}
            <Modal show={showConfirmModal} onHide={() => setShowConfirmModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <i className="fas fa-question-circle me-2"></i>
                        确认保存更改
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>请确认以下修改信息：</p>
                    <div className="bg-light p-3 rounded">
                        <div><strong>缴费ID：</strong> {payment?.paymentId}</div>
                        <div><strong>学生：</strong> {student?.stuName} (ID: {payment?.stuId})</div>
                        <div><strong>缴费类型：</strong> {formData.paymentType}</div>
                        <div><strong>缴费金额：</strong> ¥{formData.amount}</div>
                        <div><strong>缴费状态：</strong> {formData.paymentStatus}</div>
                        <div><strong>缴费日期：</strong> {formData.paymentDate}</div>
                        {formData.description && (
                            <div><strong>备注：</strong> {formData.description}</div>
                        )}
                    </div>
                    <p className="mt-3 text-muted">确认无误后点击"确认保存"按钮。</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="outline-secondary" onClick={() => setShowConfirmModal(false)}>
                        取消
                    </Button>
                    <Button variant="primary" onClick={handleConfirmSave} disabled={saving}>
                        {saving ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                保存中...
                            </>
                        ) : (
                            '确认保存'
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default EditPayment;