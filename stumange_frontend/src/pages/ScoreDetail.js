import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge, ProgressBar } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as scoreApi from '../api/score';
import * as studentApi from '../api/student';
import * as subjectApi from '../api/subject';

const ScoreDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [score, setScore] = useState(null);
    const [student, setStudent] = useState(null);
    const [subject, setSubject] = useState(null);

    useEffect(() => {
        const loadScoreDetail = async () => {
            try {
                setLoading(true);
                setError('');
                
                // 加载成绩详情
                const scoreResponse = await scoreApi.getScoreById(parseInt(id));
                if (scoreResponse && scoreResponse.status === 200) {
                    const scoreData = scoreResponse.data.data;
                    setScore(scoreData);

                    // 加载学生信息
                    if (scoreData.stuId) {
                        const studentResponse = await studentApi.getStudentById(scoreData.stuId);
                        if (studentResponse && studentResponse.status === 200) {
                            setStudent(studentResponse.data.data);
                        }
                    }

                    // 加载课程信息
                    if (scoreData.subjectId) {
                        const subjectResponse = await subjectApi.getSubjectById(scoreData.subjectId);
                        if (subjectResponse && subjectResponse.status === 200) {
                            setSubject(subjectResponse.data.data);
                        }
                    }
                } else {
                    setError('加载成绩详情失败');
                }
            } catch (err) {
                console.error('加载成绩详情失败:', err);
                setError('加载成绩详情失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadScoreDetail();
        }
    }, [id]);

    const handleEdit = () => {
        navigate(`/scores/edit/${id}`);
    };

    const handleBack = () => {
        navigate('/scores');
    };

    const getScoreGrade = (score) => {
        if (score >= 90) return '优秀';
        if (score >= 80) return '良好';
        if (score >= 70) return '中等';
        if (score >= 60) return '及格';
        return '不及格';
    };

    const getScoreColor = (score) => {
        if (score >= 90) return 'success';
        if (score >= 80) return 'info';
        if (score >= 70) return 'warning';
        if (score >= 60) return 'primary';
        return 'danger';
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载成绩详情...</p>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="py-5">
                <Alert variant="danger">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回列表
                </Button>
            </Container>
        );
    }

    if (!score) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    成绩不存在
                </Alert>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">成绩详情</h2>
                    <p className="text-muted mb-0">查看成绩详细信息</p>
                </div>
                <div className="d-flex gap-2">
                    {isAdmin() && (
                        <Button variant="primary" onClick={handleEdit}>
                            <i className="fas fa-edit me-2"></i>
                            编辑成绩
                        </Button>
                    )}
                    <Button variant="secondary" onClick={handleBack}>
                        <i className="fas fa-arrow-left me-2"></i>
                        返回列表
                    </Button>
                </div>
            </div>

            <Row>
                <Col md={8}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-chart-line me-2"></i>
                                成绩信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>成绩ID：</strong>
                                        <span className="ms-2">
                                            <Badge bg="light" text="dark">
                                                #{score.scoreId}
                                            </Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>分数：</strong>
                                        <span className="ms-2">
                                            <Badge bg={getScoreColor(score.score)} className="fs-6">
                                                {score.score} 分
                                            </Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>等级：</strong>
                                        <span className="ms-2">
                                            <Badge bg={getScoreColor(score.score)}>
                                                {getScoreGrade(score.score)}
                                            </Badge>
                                        </span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>状态：</strong>
                                        <span className="ms-2">
                                            <Badge bg={score.score >= 60 ? 'success' : 'danger'}>
                                                {score.score >= 60 ? '及格' : '不及格'}
                                            </Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>进度：</strong>
                                        <div className="mt-2">
                                            <ProgressBar
                                                now={score.score}
                                                variant={getScoreColor(score.score)}
                                                style={{ height: '25px' }}
                                                label={`${score.score}分`}
                                            />
                                        </div>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    {student && (
                        <Card className="mb-4">
                            <Card.Header>
                                <h5 className="mb-0">
                                    <i className="fas fa-user me-2"></i>
                                    学生信息
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
                                            <span className="ms-2">
                                                <Badge bg="primary">{student.stuName}</Badge>
                                            </span>
                                        </div>
                                        <div className="mb-3">
                                            <strong>性别：</strong>
                                            <span className="ms-2">
                                                <Badge bg={student.stuGender ? 'info' : 'warning'}>
                                                    {student.stuGender ? '男' : '女'}
                                                </Badge>
                                            </span>
                                        </div>
                                    </Col>
                                    <Col md={6}>
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
                                        <div className="mb-3">
                                            <strong>班级：</strong>
                                            <span className="ms-2">
                                                {student.subClassName ? (
                                                    <Badge bg="info">{student.subClassName}</Badge>
                                                ) : (
                                                    <span className="text-muted">未设置</span>
                                                )}
                                            </span>
                                        </div>
                                    </Col>
                                </Row>
                                <div className="mt-3">
                                    <Button
                                        variant="outline-primary"
                                        onClick={() => navigate(`/students/${student.stuId}`)}
                                    >
                                        <i className="fas fa-external-link-alt me-2"></i>
                                        查看学生详情
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    )}

                    {subject && (
                        <Card>
                            <Card.Header>
                                <h5 className="mb-0">
                                    <i className="fas fa-book me-2"></i>
                                    课程信息
                                </h5>
                            </Card.Header>
                            <Card.Body>
                                <Row>
                                    <Col md={6}>
                                        <div className="mb-3">
                                            <strong>课程ID：</strong>
                                            <span className="ms-2">{subject.subjectId}</span>
                                        </div>
                                        <div className="mb-3">
                                            <strong>课程名称：</strong>
                                            <span className="ms-2">
                                                <Badge bg="success">{subject.subjectName}</Badge>
                                            </span>
                                        </div>
                                    </Col>
                                    <Col md={6}>
                                        <div className="mb-3">
                                            <strong>学分：</strong>
                                            <span className="ms-2">
                                                <Badge bg="info" className="fs-6">
                                                    {subject.credit} 学分
                                                </Badge>
                                            </span>
                                        </div>
                                        <div className="mb-3">
                                            <strong>学院：</strong>
                                            <span className="ms-2">
                                                {subject.academyName ? (
                                                    <Badge bg="warning">{subject.academyName}</Badge>
                                                ) : (
                                                    <span className="text-muted">未设置</span>
                                                )}
                                            </span>
                                        </div>
                                    </Col>
                                </Row>
                                <div className="mt-3">
                                    <Button
                                        variant="outline-primary"
                                        onClick={() => navigate(`/subjects/${subject.subjectId}`)}
                                    >
                                        <i className="fas fa-external-link-alt me-2"></i>
                                        查看课程详情
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    )}
                </Col>

                <Col md={4}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">成绩统计</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <div className="display-4 text-primary">{score.score}</div>
                                    <div className="text-muted">分数</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-success">
                                        {getScoreGrade(score.score)}
                                    </div>
                                    <div className="text-muted">等级</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-info">
                                        {score.score >= 60 ? '100%' : '0%'}
                                    </div>
                                    <div className="text-muted">及格率</div>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">快速操作</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="d-grid gap-2">
                                {isAdmin() && (
                                    <Button variant="primary" onClick={handleEdit}>
                                        <i className="fas fa-edit me-2"></i>
                                        编辑成绩
                                    </Button>
                                )}
                                <Button variant="outline-secondary" onClick={handleBack}>
                                    <i className="fas fa-list me-2"></i>
                                    返回成绩列表
                                </Button>
                                {student && (
                                    <Button 
                                        variant="outline-info" 
                                        onClick={() => navigate(`/students/${student.stuId}`)}
                                    >
                                        <i className="fas fa-user me-2"></i>
                                        查看学生详情
                                    </Button>
                                )}
                                {subject && (
                                    <Button 
                                        variant="outline-success" 
                                        onClick={() => navigate(`/subjects/${subject.subjectId}`)}
                                    >
                                        <i className="fas fa-book me-2"></i>
                                        查看课程详情
                                    </Button>
                                )}
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ScoreDetail;
