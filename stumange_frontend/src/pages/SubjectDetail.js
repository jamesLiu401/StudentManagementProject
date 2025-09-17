import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as subjectApi from '../api/subject';
import * as academyApi from '../api/academy';

const SubjectDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [subject, setSubject] = useState(null);
    const [academy, setAcademy] = useState(null);

    useEffect(() => {
        const loadSubjectDetail = async () => {
            try {
                setLoading(true);
                setError('');
                
                // 加载课程详情
                const subjectResponse = await subjectApi.getSubjectById(parseInt(id));
                if (subjectResponse && subjectResponse.status === 200) {
                    const subjectData = subjectResponse.data.data;
                    setSubject(subjectData);

                    // 加载学院信息
                    if (subjectData.academyId) {
                        const academyResponse = await academyApi.getAcademyById(subjectData.academyId);
                        if (academyResponse && academyResponse.status === 200) {
                            setAcademy(academyResponse.data.data);
                        }
                    }
                } else {
                    setError('加载课程详情失败');
                }
            } catch (err) {
                console.error('加载课程详情失败:', err);
                setError('加载课程详情失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadSubjectDetail();
        }
    }, [id]);

    const handleEdit = () => {
        navigate(`/subjects/edit/${id}`);
    };

    const handleBack = () => {
        navigate('/subjects');
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载课程详情...</p>
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

    if (!subject) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    课程不存在
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
                <h2>课程详情</h2>
                <div className="d-flex gap-2">
                    {isAdmin() && (
                        <Button variant="primary" onClick={handleEdit}>
                            <i className="fas fa-edit me-2"></i>
                            编辑
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
                            <h5 className="mb-0">基本信息</h5>
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
                                        <span className="ms-2">{subject.subjectName}</span>
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
                                        <strong>学院ID：</strong>
                                        <span className="ms-2">{subject.academy || '-'}</span>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">学院信息</h5>
                        </Card.Header>
                        <Card.Body>
                            {academy ? (
                                <Row>
                                    <Col md={6}>
                                        <div className="mb-3">
                                            <strong>学院名称：</strong>
                                            <span className="ms-2">
                                                <Badge bg="success" className="fs-6">
                                                    {academy.academyName}
                                                </Badge>
                                            </span>
                                        </div>
                                        <div className="mb-3">
                                            <strong>学院代码：</strong>
                                            <span className="ms-2">{academy.academyCode}</span>
                                        </div>
                                    </Col>
                                    <Col md={6}>
                                        <div className="mb-3">
                                            <strong>院长：</strong>
                                            <span className="ms-2">{academy.deanName || '-'}</span>
                                        </div>
                                        <div className="mb-3">
                                            <strong>联系电话：</strong>
                                            <span className="ms-2">{academy.contactPhone || '-'}</span>
                                        </div>
                                    </Col>
                                    {academy.description && (
                                        <Col md={12}>
                                            <div className="mb-3">
                                                <strong>学院描述：</strong>
                                                <p className="ms-2 mt-2 text-muted">{academy.description}</p>
                                            </div>
                                        </Col>
                                    )}
                                </Row>
                            ) : (
                                <div className="text-center py-4 text-muted">
                                    <i className="fas fa-university fa-2x mb-2"></i>
                                    <p>学院信息未设置或加载失败</p>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={4}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">课程统计</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <div className="display-6 text-primary">{subject.credit}</div>
                                    <div className="text-muted">学分</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-success">
                                        {academy ? '100%' : '0%'}
                                    </div>
                                    <div className="text-muted">学院信息完整度</div>
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
                                        编辑课程
                                    </Button>
                                )}
                                <Button variant="outline-secondary" onClick={handleBack}>
                                    <i className="fas fa-list me-2"></i>
                                    返回课程列表
                                </Button>
                                {academy && (
                                    <Button 
                                        variant="outline-info" 
                                        onClick={() => navigate(`/academies/${academy.academyId}`)}
                                    >
                                        <i className="fas fa-university me-2"></i>
                                        查看学院详情
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

export default SubjectDetail;
