import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge, Table, Tabs, Tab, ProgressBar, Form } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getStudentById } from '../api/student';
import { useAuth } from '../contexts/AuthContexts';
import { getMajorById } from "../api/major";
import { getAcademyById } from "../api/academy";
import { getTeacherById } from "../api/teacher";
import { getPaymentsByStudent, getTotalAmountByStudent } from '../api/payment';
import { getScoresByStudentId, getAverageScoreByStudent, getPassingScoresByStudent, getFailingScoresByStudent, createScoreByParams } from '../api/score';
import { getSubjects } from '../api/subject';

const StudentDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [major, setMajor] = useState(null);
    const [academyName, setAcademyName] = useState('');
    const [counselor, setCounselor] = useState(null);
    const [payments, setPayments] = useState([]);
    const [scores, setScores] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [averageScore, setAverageScore] = useState(0);
    const [passingCount, setPassingCount] = useState(0);
    const [failingCount, setFailingCount] = useState(0);
    const [loadingPayments, setLoadingPayments] = useState(false);
    const [loadingScores, setLoadingScores] = useState(false);
    
    // 添加成绩相关状态
    const [showAddScoreForm, setShowAddScoreForm] = useState(false);
    const [subjects, setSubjects] = useState([]);
    const [loadingSubjects, setLoadingSubjects] = useState(false);
    const [addScoreForm, setAddScoreForm] = useState({
        subjectId: '',
        score: ''
    });
    const [addScoreLoading, setAddScoreLoading] = useState(false);
    const [addScoreError, setAddScoreError] = useState('');
    const [addScoreSuccess, setAddScoreSuccess] = useState('');
    useEffect(() => {
        const loadStudent = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await getStudentById(id);
                if (response?.status === 200 && response?.data?.data) {
                    const s = response.data.data || {};
                    // 兼容后端可能的懒加载/字段差异
                    const className = s.stuClassName || '-';
                    const majorId = s.majorId || '';
                    const majorResponse = await getMajorById(majorId);
                    if(majorResponse?.status === 200 && majorResponse?.data?.data) {
                        setMajor(majorResponse.data.data);
                        const m =majorResponse.data.data;
                        const acaId = m.academyId || (m.academy && m.academy.academyId);
                        if (acaId) {
                            try {
                                const acaResp = await getAcademyById(acaId);
                                if (acaResp?.status === 200 && acaResp?.data?.data?.academyName) {
                                    setAcademyName(acaResp.data.data.academyName);
                                }
                            } catch {}
                        }
                        // 获取辅导员信息
                        if (m.counselorId) {
                            try {
                                const counselorResp = await getTeacherById(m.counselorId);
                                if (counselorResp?.status === 200 && counselorResp?.data?.data) {
                                    setCounselor(counselorResp.data.data);
                                }
                            } catch (err) {
                                console.warn('获取辅导员信息失败:', err);
                            }
                        }
                    }
                    setStudent({ ...s, subClassName: className });
                } else {
                    setError('加载学生信息失败');
                }
            } catch (err) {
                console.error('加载学生信息失败', err);
                setError('加载学生信息失败：' + (err.response?.data?.message || '未知错误'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadStudent();
        }
    }, [id]);

    // 加载缴费记录
    const loadPayments = async () => {
        if (!id) return;
        try {
            setLoadingPayments(true);
            const [paymentsResp, totalAmountResp] = await Promise.all([
                getPaymentsByStudent(id),
                getTotalAmountByStudent(id)
            ]);
            
            if (paymentsResp?.status === 200 && paymentsResp?.data?.data) {
                setPayments(paymentsResp.data.data);
            }
            
            if (totalAmountResp?.status === 200 && totalAmountResp?.data?.data) {
                setTotalAmount(totalAmountResp.data.data);
            }
        } catch (err) {
            console.error('加载缴费记录失败', err);
        } finally {
            setLoadingPayments(false);
        }
    };

    // 加载成绩记录
    const loadScores = async () => {
        if (!id) return;
        try {
            setLoadingScores(true);
            const [scoresResp, averageResp, passingResp, failingResp] = await Promise.all([
                getScoresByStudentId(id),
                getAverageScoreByStudent(id),
                getPassingScoresByStudent(id),
                getFailingScoresByStudent(id)
            ]);
            
            if (scoresResp?.status === 200 && scoresResp?.data?.data) {
                setScores(scoresResp.data.data);
            }
            
            if (averageResp?.status === 200 && averageResp?.data?.data) {
                setAverageScore(averageResp.data.data);
            }
            
            if (passingResp?.status === 200 && passingResp?.data?.data) {
                setPassingCount(passingResp.data.data.length);
            }
            
            if (failingResp?.status === 200 && failingResp?.data?.data) {
                setFailingCount(failingResp.data.data.length);
            }
        } catch (err) {
            console.error('加载成绩记录失败', err);
        } finally {
            setLoadingScores(false);
        }
    };

    // 加载课程数据
    const loadSubjects = async () => {
        try {
            setLoadingSubjects(true);
            const response = await getSubjects();
            if (response?.status === 200 && response?.data?.data) {
                setSubjects(response.data.data);
            }
        } catch (err) {
            console.error('加载课程数据失败', err);
        } finally {
            setLoadingSubjects(false);
        }
    };

    // 处理添加成绩表单输入
    const handleAddScoreInputChange = (e) => {
        const { name, value } = e.target;
        setAddScoreForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // 提交添加成绩
    const handleAddScoreSubmit = async (e) => {
        e.preventDefault();
        
        if (!addScoreForm.subjectId || !addScoreForm.score) {
            setAddScoreError('请填写所有必填字段');
            return;
        }

        const score = parseFloat(addScoreForm.score);
        if (isNaN(score) || score < 0 || score > 100) {
            setAddScoreError('分数必须在0-100之间');
            return;
        }

        try {
            setAddScoreLoading(true);
            setAddScoreError('');
            setAddScoreSuccess('');

            await createScoreByParams(
                parseInt(id),
                parseInt(addScoreForm.subjectId),
                score
            );

            setAddScoreSuccess('成绩添加成功！');
            setAddScoreForm({ subjectId: '', score: '' });
            
            // 重新加载成绩数据
            await loadScores();
            
            setTimeout(() => {
                setAddScoreSuccess('');
                setShowAddScoreForm(false);
            }, 2000);
        } catch (err) {
            console.error('添加成绩失败:', err);
            setAddScoreError('添加成绩失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setAddScoreLoading(false);
        }
    };

    // 取消添加成绩
    const handleCancelAddScore = () => {
        setShowAddScoreForm(false);
        setAddScoreForm({ subjectId: '', score: '' });
        setAddScoreError('');
        setAddScoreSuccess('');
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载学生信息...</p>
            </Container>
        );
    }

    if (error) {
        return (
            <Container fluid className="mt-4">
                <Alert variant="danger">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/students')}>
                    返回学生列表
                </Button>
            </Container>
        );
    }

    if (!student) {
        return (
            <Container fluid className="mt-4">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    未找到该学生信息
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/students')}>
                    返回学生列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>学生详情</h2>
                <div>
                    {isAdmin() && (
                        <>
                            <Button 
                                variant="warning" 
                                className="me-2"
                                onClick={() => navigate(`/students/edit/${id}`)}
                            >
                                <i className="fas fa-edit me-2"></i>
                                编辑学生
                            </Button>
                            <Button 
                                variant="success" 
                                className="me-2"
                                onClick={() => navigate(`/payments/add?studentId=${id}`)}
                            >
                                <i className="fas fa-plus me-2"></i>
                                添加缴费
                            </Button>
                            <Button 
                                variant="info" 
                                className="me-2"
                                onClick={() => {
                                    setShowAddScoreForm(!showAddScoreForm);
                                    if (!showAddScoreForm && subjects.length === 0) {
                                        loadSubjects();
                                    }
                                }}
                            >
                                <i className="fas fa-chart-line me-2"></i>
                                {showAddScoreForm ? '取消添加' : '添加成绩'}
                            </Button>
                            <Button 
                                variant="outline-primary" 
                                className="me-2"
                                onClick={() => {
                                    loadPayments();
                                    loadScores();
                                }}
                            >
                                <i className="fas fa-sync-alt me-2"></i>
                                刷新数据
                            </Button>
                        </>
                    )}
                    <Button 
                        variant="secondary" 
                        onClick={() => navigate('/students')}
                    >
                        <i className="fas fa-arrow-left me-2"></i>
                        返回学生列表
                    </Button>
                </div>
            </div>

            {/* 统计信息卡片 */}
            <Row className="mb-4">
                <Col md={3}>
                    <Card className="text-center border-primary">
                        <Card.Body>
                            <i className="fas fa-graduation-cap fa-2x text-primary mb-2"></i>
                            <h5 className="card-title">平均成绩</h5>
                            <h3 className="text-primary">{averageScore.toFixed(1)}</h3>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3}>
                    <Card className="text-center border-success">
                        <Card.Body>
                            <i className="fas fa-check-circle fa-2x text-success mb-2"></i>
                            <h5 className="card-title">及格科目</h5>
                            <h3 className="text-success">{passingCount}</h3>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3}>
                    <Card className="text-center border-warning">
                        <Card.Body>
                            <i className="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                            <h5 className="card-title">不及格科目</h5>
                            <h3 className="text-warning">{failingCount}</h3>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3}>
                    <Card className="text-center border-info">
                        <Card.Body>
                            <i className="fas fa-money-bill-wave fa-2x text-info mb-2"></i>
                            <h5 className="card-title">总缴费金额</h5>
                            <h3 className="text-info">¥{totalAmount.toFixed(2)}</h3>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 基本信息 */}
            <Row className="mb-4">
                <Col md={8}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-user me-2"></i>
                                基本信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>学生ID：</strong>
                                        <span className="ms-2">{student.stuId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>姓名：</strong>
                                        <span className="ms-2">{student.stuName}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>性别：</strong>
                                        <span className="ms-2">
                                            <Badge bg={student.stuGender ? 'primary' : 'danger'}>
                                                {student.stuGender ? '男' : '女'}
                                            </Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>年级：</strong>
                                        <span className="ms-2">
                                            {student.stuGrade ? (
                                                <Badge bg="secondary">{student.stuGrade}级</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>班级：</strong>
                                        <span className="ms-2">
                                            {student.stuClassName ? (
                                                <Badge bg="info">{student.stuClassName}</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>专业：</strong>
                                        <span className="ms-2">
                                            {major?.majorName ? (
                                                <Badge bg="success">{major.majorName}</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>学院：</strong>
                                        <span className="ms-2">
                                            {academyName ? (
                                                <Badge bg="warning">{academyName}</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>辅导员：</strong>
                                        <span className="ms-2">
                                            {counselor?.teacherName ? (
                                                <Badge bg="dark">{counselor.teacherName}</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-address-card me-2"></i>
                                联系信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-3">
                                <strong>电话：</strong>
                                <div className="mt-1">
                                    {student.stuTel ? (
                                        <a href={`tel:${student.stuTel}`} className="text-decoration-none">
                                            <i className="fas fa-phone me-1"></i>
                                            {student.stuTel}
                                        </a>
                                    ) : (
                                        <span className="text-muted">未填写</span>
                                    )}
                                </div>
                            </div>
                            <div className="mb-3">
                                <strong>地址：</strong>
                                <div className="mt-1">
                                    {student.stuAddress ? (
                                        <span>
                                            <i className="fas fa-map-marker-alt me-1"></i>
                                            {student.stuAddress}
                                        </span>
                                    ) : (
                                        <span className="text-muted">未填写</span>
                                    )}
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 详细信息标签页 */}
            <Row>
                <Col>
                    <Card>
                        <Card.Body>
                            <Tabs defaultActiveKey="scores" id="student-detail-tabs" onSelect={(key) => {
                                if (key === 'scores' && scores.length === 0) {
                                    loadScores();
                                } else if (key === 'payments' && payments.length === 0) {
                                    loadPayments();
                                }
                            }}>
                                <Tab eventKey="scores" title={
                                    <span>
                                        <i className="fas fa-chart-line me-2"></i>
                                        成绩记录
                                    </span>
                                }>
                                    <div className="mt-3">
                                        {/* 添加成绩表单 */}
                                        {showAddScoreForm && (
                                            <Card className="mb-4 border-info">
                                                <Card.Header className="bg-info text-white">
                                                    <h6 className="mb-0">
                                                        <i className="fas fa-plus me-2"></i>
                                                        添加新成绩
                                                    </h6>
                                                </Card.Header>
                                                <Card.Body>
                                                    {addScoreError && (
                                                        <Alert variant="danger">
                                                            <i className="fas fa-exclamation-triangle me-2"></i>
                                                            {addScoreError}
                                                        </Alert>
                                                    )}

                                                    {addScoreSuccess && (
                                                        <Alert variant="success">
                                                            <i className="fas fa-check-circle me-2"></i>
                                                            {addScoreSuccess}
                                                        </Alert>
                                                    )}

                                                    <Form onSubmit={handleAddScoreSubmit}>
                                                        <Row>
                                                            <Col md={6}>
                                                                <Form.Group className="mb-3">
                                                                    <Form.Label>课程 <span className="text-danger">*</span></Form.Label>
                                                                    <Form.Select
                                                                        name="subjectId"
                                                                        value={addScoreForm.subjectId}
                                                                        onChange={handleAddScoreInputChange}
                                                                        required
                                                                        disabled={loadingSubjects}
                                                                    >
                                                                        <option value="">请选择课程</option>
                                                                        {subjects.map((subject) => (
                                                                            <option key={subject.subjectId} value={subject.subjectId}>
                                                                                {subject.subjectName} ({subject.credit}学分)
                                                                                {subject.academyName && ` - ${subject.academyName}`}
                                                                            </option>
                                                                        ))}
                                                                    </Form.Select>
                                                                    {loadingSubjects && (
                                                                        <Form.Text className="text-muted">
                                                                            <Spinner animation="border" size="sm" className="me-2" />
                                                                            加载课程中...
                                                                        </Form.Text>
                                                                    )}
                                                                </Form.Group>
                                                            </Col>
                                                            <Col md={4}>
                                                                <Form.Group className="mb-3">
                                                                    <Form.Label>分数 <span className="text-danger">*</span></Form.Label>
                                                                    <Form.Control
                                                                        type="number"
                                                                        name="score"
                                                                        value={addScoreForm.score}
                                                                        onChange={handleAddScoreInputChange}
                                                                        min="0"
                                                                        max="100"
                                                                        step="0.1"
                                                                        placeholder="请输入分数 (0-100)"
                                                                        required
                                                                    />
                                                                </Form.Group>
                                                            </Col>
                                                            <Col md={2} className="d-flex align-items-end">
                                                                <div className="d-grid gap-2 w-100">
                                                                    <Button
                                                                        variant="success"
                                                                        type="submit"
                                                                        disabled={addScoreLoading || loadingSubjects}
                                                                        size="sm"
                                                                    >
                                                                        {addScoreLoading ? (
                                                                            <Spinner animation="border" size="sm" />
                                                                        ) : (
                                                                            <>
                                                                                <i className="fas fa-save me-1"></i>
                                                                                保存
                                                                            </>
                                                                        )}
                                                                    </Button>
                                                                    <Button
                                                                        variant="outline-secondary"
                                                                        onClick={handleCancelAddScore}
                                                                        disabled={addScoreLoading}
                                                                        size="sm"
                                                                    >
                                                                        <i className="fas fa-times me-1"></i>
                                                                        取消
                                                                    </Button>
                                                                </div>
                                                            </Col>
                                                        </Row>
                                                    </Form>
                                                </Card.Body>
                                            </Card>
                                        )}
                                        {loadingScores ? (
                                            <div className="text-center py-4">
                                                <Spinner animation="border" size="sm" />
                                                <span className="ms-2">加载成绩记录中...</span>
                                            </div>
                                        ) : (
                                            <div>
                                                {scores.length > 0 ? (
                                                    <div>
                                                        {/* 成绩统计概览 */}
                                                        <Row className="mb-3">
                                                            <Col md={6}>
                                                                <div className="mb-2">
                                                                    <small className="text-muted">及格率</small>
                                                                    <ProgressBar 
                                                                        now={(passingCount / (passingCount + failingCount)) * 100} 
                                                                        variant="success" 
                                                                        className="mt-1"
                                                                        label={`${((passingCount / (passingCount + failingCount)) * 100).toFixed(1)}%`}
                                                                    />
                                                                </div>
                                                            </Col>
                                                            <Col md={6}>
                                                                <div className="mb-2">
                                                                    <small className="text-muted">平均成绩</small>
                                                                    <ProgressBar 
                                                                        now={averageScore} 
                                                                        variant={averageScore >= 80 ? 'success' : averageScore >= 60 ? 'warning' : 'danger'} 
                                                                        className="mt-1"
                                                                        label={`${averageScore.toFixed(1)}分`}
                                                                    />
                                                                </div>
                                                            </Col>
                                                        </Row>
                                                        
                                                        <Table responsive striped hover>
                                                            <thead>
                                                                <tr>
                                                                    <th>科目名称</th>
                                                                    <th>成绩</th>
                                                                    <th>等级</th>
                                                                    <th>状态</th>
                                                                    <th>进度</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                {scores.map((score, index) => (
                                                                    <tr key={index}>
                                                                        <td>{score.subjectName || '未知科目'}</td>
                                                                        <td>
                                                                            <strong className={score.score >= 60 ? 'text-success' : 'text-danger'}>
                                                                                {score.score}
                                                                            </strong>
                                                                        </td>
                                                                        <td>
                                                                            <Badge bg={score.score >= 90 ? 'success' : score.score >= 80 ? 'info' : score.score >= 70 ? 'warning' : score.score >= 60 ? 'secondary' : 'danger'}>
                                                                                {score.score >= 90 ? '优秀' : score.score >= 80 ? '良好' : score.score >= 70 ? '中等' : score.score >= 60 ? '及格' : '不及格'}
                                                                            </Badge>
                                                                        </td>
                                                                        <td>
                                                                            <Badge bg={score.score >= 60 ? 'success' : 'danger'}>
                                                                                {score.score >= 60 ? '及格' : '不及格'}
                                                                            </Badge>
                                                                        </td>
                                                                        <td>
                                                                            <ProgressBar 
                                                                                now={score.score} 
                                                                                variant={score.score >= 90 ? 'success' : score.score >= 80 ? 'info' : score.score >= 70 ? 'warning' : score.score >= 60 ? 'secondary' : 'danger'}
                                                                                style={{ width: '80px', height: '20px' }}
                                                                            />
                                                                        </td>
                                                                    </tr>
                                                                ))}
                                                            </tbody>
                                                        </Table>
                                                    </div>
                                                ) : (
                                                    <Alert variant="info">
                                                        <i className="fas fa-info-circle me-2"></i>
                                                        暂无成绩记录
                                                    </Alert>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                </Tab>
                                
                                <Tab eventKey="payments" title={
                                    <span>
                                        <i className="fas fa-credit-card me-2"></i>
                                        缴费记录
                                    </span>
                                }>
                                    <div className="mt-3">
                                        {loadingPayments ? (
                                            <div className="text-center py-4">
                                                <Spinner animation="border" size="sm" />
                                                <span className="ms-2">加载缴费记录中...</span>
                                            </div>
                                        ) : (
                                            <div>
                                                {payments.length > 0 ? (
                                                    <Table responsive striped hover>
                                                        <thead>
                                                            <tr>
                                                                <th>缴费类型</th>
                                                                <th>金额</th>
                                                                <th>状态</th>
                                                                <th>缴费日期</th>
                                                                <th>描述</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            {payments.map((payment, index) => (
                                                                <tr key={index}>
                                                                    <td>
                                                                        <Badge bg="primary">{payment.paymentType}</Badge>
                                                                    </td>
                                                                    <td>
                                                                        <strong className="text-success">¥{payment.amount}</strong>
                                                                    </td>
                                                                    <td>
                                                                        <Badge bg={payment.paymentStatus === '已缴费' ? 'success' : payment.paymentStatus === '未缴费' ? 'danger' : 'warning'}>
                                                                            {payment.paymentStatus}
                                                                        </Badge>
                                                                    </td>
                                                                    <td>{payment.paymentDate ? new Date(payment.paymentDate).toLocaleDateString() : '-'}</td>
                                                                    <td>{payment.description || '-'}</td>
                                                                </tr>
                                                            ))}
                                                        </tbody>
                                                    </Table>
                                                ) : (
                                                    <Alert variant="info">
                                                        <i className="fas fa-info-circle me-2"></i>
                                                        暂无缴费记录
                                                    </Alert>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                </Tab>
                            </Tabs>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default StudentDetail;
