import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col, Modal, Badge, InputGroup } from 'react-bootstrap';
import { useNavigate, useSearchParams, Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as paymentApi from '../api/payment';
import * as studentApi from '../api/student';

const AddPayment = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { isAdmin, currentUser } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [students, setStudents] = useState([]);
    const [filteredStudents, setFilteredStudents] = useState([]);
    const [loadingStudents, setLoadingStudents] = useState(true);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [studentSearchTerm, setStudentSearchTerm] = useState('');
    const [formData, setFormData] = useState({
        stuId: searchParams.get('studentId') || '',
        paymentType: '学费',
        amount: '',
        paymentStatus: '未缴费',
        description: '',
        paymentDate: new Date().toISOString().split('T')[0]
    });

    // 缴费类型选项
    const paymentTypes = [
        '学费', '住宿费', '教材费', '实验费', '考试费', '其他'
    ];

    // 常用缴费类型（快速选择）
    const commonPaymentTypes = [
        { type: '学费', amount: 5000, description: '学期学费' },
        { type: '住宿费', amount: 1200, description: '学期住宿费' },
        { type: '教材费', amount: 300, description: '教材费用' },
        { type: '实验费', amount: 200, description: '实验课程费用' }
    ];

    // 缴费状态选项
    const paymentStatuses = [
        '未缴费', '已缴费', '部分缴费', '已退费'
    ];

    // 权限检查
    if (!currentUser) {
        return <Navigate to="/login" />;
    }

    if (!isAdmin()) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    您没有权限访问此页面
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/payments')}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回缴费列表
                </Button>
            </Container>
        );
    }

    // 加载学生列表
    useEffect(() => {
        const loadStudents = async () => {
            try {
                setLoadingStudents(true);
                const response = await studentApi.getStudents();
                if (response && response.status === 200) {
                    const studentsData = response.data.data || [];
                    setStudents(studentsData);
                    setFilteredStudents(studentsData);
                }
            } catch (err) {
                console.error('加载学生列表失败:', err);
                setError('加载学生列表失败，请稍后重试');
            } finally {
                setLoadingStudents(false);
            }
        };

        loadStudents();
    }, []);

    // 学生搜索过滤
    useEffect(() => {
        if (!studentSearchTerm.trim()) {
            setFilteredStudents(students);
        } else {
            const filtered = students.filter(student => 
                student.stuName.toLowerCase().includes(studentSearchTerm.toLowerCase()) ||
                student.stuId.toString().includes(studentSearchTerm) ||
                (student.subClassName && student.subClassName.toLowerCase().includes(studentSearchTerm.toLowerCase()))
            );
            setFilteredStudents(filtered);
        }
    }, [studentSearchTerm, students]);

    // 处理表单输入变化
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // 快速选择缴费类型
    const handleQuickSelect = (commonType) => {
        setFormData(prev => ({
            ...prev,
            paymentType: commonType.type,
            amount: commonType.amount.toString(),
            description: commonType.description
        }));
    };

    // 清除表单
    const handleClearForm = () => {
        setFormData({
            stuId: searchParams.get('studentId') || '',
            paymentType: '学费',
            amount: '',
            paymentStatus: '未缴费',
            description: '',
            paymentDate: new Date().toISOString().split('T')[0]
        });
        setError('');
        setSuccess('');
    };

    // 验证表单
    const validateForm = () => {
        if (!formData.stuId) {
            setError('请选择学生');
            return false;
        }
        if (!formData.amount || formData.amount <= 0) {
            setError('请输入有效的缴费金额（必须大于0）');
            return false;
        }
        if (parseFloat(formData.amount) > 100000) {
            setError('缴费金额不能超过100,000元');
            return false;
        }
        if (!formData.paymentType) {
            setError('请选择缴费类型');
            return false;
        }
        if (!formData.paymentDate) {
            setError('请选择缴费日期');
            return false;
        }
        return true;
    };

    // 提交表单
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!isAdmin()) {
            setError('您没有权限添加缴费记录');
            return;
        }

        if (!validateForm()) {
            return;
        }

        setShowConfirmModal(true);
    };

    // 确认提交
    const handleConfirmSubmit = async () => {
        try {
            setLoading(true);
            setError('');
            setSuccess('');

            const paymentData = {
                ...formData,
                amount: parseFloat(formData.amount)
            };

            const response = await paymentApi.createPayment(paymentData);
            
            if (response && response.status === 200) {
                setSuccess('缴费记录添加成功！');
                setFormData({
                    stuId: '',
                    paymentType: '学费',
                    amount: '',
                    paymentStatus: '未缴费',
                    description: '',
                    paymentDate: new Date().toISOString().split('T')[0]
                });
                setShowConfirmModal(false);
                
                // 2秒后跳转到缴费列表页面
                setTimeout(() => {
                    navigate('/payments');
                }, 2000);
            } else {
                setError('添加缴费记录失败，请稍后重试');
            }
        } catch (err) {
            console.error('添加缴费记录失败:', err);
            setError(err.response?.data?.message || '添加缴费记录失败，请稍后重试');
        } finally {
            setLoading(false);
            setShowConfirmModal(false);
        }
    };

    // 获取选中学生的信息
    const selectedStudent = students.find(s => s.stuId === parseInt(formData.stuId));

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>
                    <i className="fas fa-plus-circle me-2"></i>
                    添加缴费记录
                </h2>
                <Button 
                    variant="outline-secondary" 
                    onClick={() => navigate('/payments')}
                >
                    <i className="fas fa-arrow-left me-2"></i>
                    返回列表
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
                                                <i className="fas fa-user me-2"></i>
                                                选择学生 <span className="text-danger">*</span>
                                            </Form.Label>
                                            <InputGroup>
                                                <Form.Control
                                                    type="text"
                                                    placeholder="搜索学生姓名、学号或班级..."
                                                    value={studentSearchTerm}
                                                    onChange={(e) => setStudentSearchTerm(e.target.value)}
                                                    disabled={loadingStudents}
                                                />
                                                <Button 
                                                    variant="outline-secondary" 
                                                    onClick={() => setStudentSearchTerm('')}
                                                    disabled={loadingStudents || !studentSearchTerm}
                                                >
                                                    <i className="fas fa-times"></i>
                                                </Button>
                                            </InputGroup>
                                            <Form.Select
                                                name="stuId"
                                                value={formData.stuId}
                                                onChange={handleInputChange}
                                                required
                                                disabled={loadingStudents}
                                                className="mt-2"
                                            >
                                                <option value="">请选择学生</option>
                                                {filteredStudents.map(student => (
                                                    <option key={student.stuId} value={student.stuId}>
                                                        {student.stuName} (ID: {student.stuId})
                                                        {student.subClassName && ` - ${student.subClassName}`}
                                                    </option>
                                                ))}
                                            </Form.Select>
                                            {loadingStudents && (
                                                <div className="mt-2">
                                                    <Spinner animation="border" size="sm" />
                                                    <span className="ms-2">加载学生列表中...</span>
                                                </div>
                                            )}
                                            {!loadingStudents && filteredStudents.length === 0 && studentSearchTerm && (
                                                <div className="mt-2 text-muted">
                                                    <i className="fas fa-search me-1"></i>
                                                    未找到匹配的学生
                                                </div>
                                            )}
                                            {!loadingStudents && filteredStudents.length > 0 && (
                                                <div className="mt-2 text-muted">
                                                    <i className="fas fa-info-circle me-1"></i>
                                                    找到 {filteredStudents.length} 个学生
                                                </div>
                                            )}
                                        </Form.Group>
                                    </Col>
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
                                                {paymentTypes.map(type => (
                                                    <option key={type} value={type}>{type}</option>
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
                                                <i className="fas fa-info-circle me-2"></i>
                                                缴费状态
                                            </Form.Label>
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
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                <i className="fas fa-comment me-2"></i>
                                                备注说明
                                            </Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="description"
                                                value={formData.description}
                                                onChange={handleInputChange}
                                                placeholder="请输入备注说明（可选）"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <div className="d-flex justify-content-between align-items-center">
                                    <Button 
                                        variant="outline-warning" 
                                        onClick={handleClearForm}
                                        disabled={loading}
                                        size="sm"
                                    >
                                        <i className="fas fa-eraser me-2"></i>
                                        清除表单
                                    </Button>
                                    <div className="d-flex gap-2">
                                        <Button 
                                            variant="outline-secondary" 
                                            onClick={() => navigate('/payments')}
                                            disabled={loading}
                                        >
                                            取消
                                        </Button>
                                        <Button 
                                            variant="primary" 
                                            type="submit"
                                            disabled={loading}
                                        >
                                            {loading ? (
                                                <>
                                                    <Spinner animation="border" size="sm" className="me-2" />
                                                    添加中...
                                                </>
                                            ) : (
                                                <>
                                                    <i className="fas fa-save me-2"></i>
                                                    添加缴费记录
                                                </>
                                            )}
                                        </Button>
                                    </div>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={4}>
                    {/* 快速选择面板 */}
                    <Card className="mb-3">
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-bolt me-2"></i>
                                快速选择
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <p className="text-muted mb-3">点击快速填充常用缴费信息</p>
                            <div className="d-grid gap-2">
                                {commonPaymentTypes.map((commonType, index) => (
                                    <Button
                                        key={index}
                                        variant="outline-primary"
                                        size="sm"
                                        onClick={() => handleQuickSelect(commonType)}
                                        disabled={loading}
                                        className="text-start"
                                    >
                                        <div className="d-flex justify-content-between align-items-center">
                                            <div>
                                                <strong>{commonType.type}</strong>
                                                <br />
                                                <small className="text-muted">{commonType.description}</small>
                                            </div>
                                            <Badge bg="primary">¥{commonType.amount}</Badge>
                                        </div>
                                    </Button>
                                ))}
                            </div>
                        </Card.Body>
                    </Card>

                    {selectedStudent && (
                        <Card>
                            <Card.Header>
                                <h6 className="mb-0">
                                    <i className="fas fa-user-circle me-2"></i>
                                    学生信息
                                </h6>
                            </Card.Header>
                            <Card.Body>
                                <div className="mb-2">
                                    <strong>姓名：</strong> {selectedStudent.stuName}
                                </div>
                                <div className="mb-2">
                                    <strong>学号：</strong> {selectedStudent.stuId}
                                </div>
                                <div className="mb-2">
                                    <strong>性别：</strong> {selectedStudent.stuGender ? '男' : '女'}
                                </div>
                                <div className="mb-2">
                                    <strong>年级：</strong> {selectedStudent.stuGrade ? `${selectedStudent.stuGrade}级` : '未设置'}
                                </div>
                                <div className="mb-2">
                                    <strong>班级：</strong> {selectedStudent.subClassName || '未设置'}
                                </div>
                                <div className="mb-2">
                                    <strong>电话：</strong> {selectedStudent.stuTel || selectedStudent.stuTelephoneNo || '未填写'}
                                </div>
                                <div className="mb-2">
                                    <strong>地址：</strong> {selectedStudent.stuAddress || '未填写'}
                                </div>
                                <div className="mt-3">
                                    <Button 
                                        variant="outline-primary" 
                                        size="sm"
                                        onClick={() => navigate(`/students/${selectedStudent.stuId}`)}
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
                                <i className="fas fa-lightbulb me-2"></i>
                                操作提示
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <ul className="list-unstyled mb-0">
                                <li className="mb-2">
                                    <i className="fas fa-search text-info me-2"></i>
                                    使用搜索框快速找到学生
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-bolt text-warning me-2"></i>
                                    点击快速选择按钮填充常用信息
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-check text-success me-2"></i>
                                    仔细核对学生信息和缴费金额
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-calendar text-primary me-2"></i>
                                    选择合适的缴费日期和状态
                                </li>
                                <li className="mb-2">
                                    <i className="fas fa-comment text-secondary me-2"></i>
                                    添加备注说明便于后续管理
                                </li>
                                <li>
                                    <i className="fas fa-eraser text-danger me-2"></i>
                                    使用清除表单按钮重置所有字段
                                </li>
                            </ul>
                        </Card.Body>
                    </Card>

                    {/* 表单状态显示 */}
                    <Card className="mt-3">
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-info-circle me-2"></i>
                                表单状态
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-2">
                                <small className="text-muted">学生选择：</small>
                                <Badge bg={formData.stuId ? 'success' : 'secondary'} className="ms-2">
                                    {formData.stuId ? '已选择' : '未选择'}
                                </Badge>
                            </div>
                            <div className="mb-2">
                                <small className="text-muted">缴费类型：</small>
                                <Badge bg="info" className="ms-2">{formData.paymentType}</Badge>
                            </div>
                            <div className="mb-2">
                                <small className="text-muted">缴费金额：</small>
                                <Badge bg={formData.amount ? 'success' : 'secondary'} className="ms-2">
                                    {formData.amount ? `¥${formData.amount}` : '未填写'}
                                </Badge>
                            </div>
                            <div className="mb-2">
                                <small className="text-muted">缴费状态：</small>
                                <Badge bg={formData.paymentStatus === '已缴费' ? 'success' : 
                                         formData.paymentStatus === '部分缴费' ? 'warning' : 'danger'} className="ms-2">
                                    {formData.paymentStatus}
                                </Badge>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 确认对话框 */}
            <Modal show={showConfirmModal} onHide={() => setShowConfirmModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <i className="fas fa-question-circle me-2"></i>
                        确认添加缴费记录
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>请确认以下缴费信息：</p>
                    <div className="bg-light p-3 rounded">
                        <div><strong>学生：</strong> {selectedStudent?.stuName} (ID: {formData.stuId})</div>
                        <div><strong>缴费类型：</strong> {formData.paymentType}</div>
                        <div><strong>缴费金额：</strong> ¥{formData.amount}</div>
                        <div><strong>缴费状态：</strong> {formData.paymentStatus}</div>
                        <div><strong>缴费日期：</strong> {formData.paymentDate}</div>
                        {formData.description && (
                            <div><strong>备注：</strong> {formData.description}</div>
                        )}
                    </div>
                    <p className="mt-3 text-muted">确认无误后点击"确认添加"按钮。</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="outline-secondary" onClick={() => setShowConfirmModal(false)}>
                        取消
                    </Button>
                    <Button variant="primary" onClick={handleConfirmSubmit} disabled={loading}>
                        {loading ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                添加中...
                            </>
                        ) : (
                            '确认添加'
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default AddPayment;
