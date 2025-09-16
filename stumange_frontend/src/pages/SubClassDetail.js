import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';
import * as majorApi from '../api/major';

const SubClassDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [subClass, setSubClass] = useState(null);
    const [totalClass, setTotalClass] = useState(null);
    const [major, setMajor] = useState(null);

    useEffect(() => {
        const loadSubClassDetail = async () => {
            try {
                setLoading(true);
                setError('');
                
                // 加载子班级详情
                const subClassResponse = await classApi.getSubClassById(parseInt(id));
                if (subClassResponse && subClassResponse.status === 200) {
                    const subClassData = subClassResponse.data.data;
                    setSubClass(subClassData);
                    
                    // 加载总班级信息
                    if (subClassData.totalClassId) {
                        const totalClassResponse = await classApi.getTotalClassById(subClassData.totalClassId);
                        if (totalClassResponse && totalClassResponse.status === 200) {
                            const totalClassData = totalClassResponse.data.data;
                            setTotalClass(totalClassData);
                            
                            // 加载专业信息
                            if (totalClassData.majorId) {
                                const majorResponse = await majorApi.getMajorById(totalClassData.majorId);
                                if (majorResponse && majorResponse.status === 200) {
                                    setMajor(majorResponse.data.data);
                                }
                            }
                        }
                    }
                } else {
                    setError('加载子班级详情失败');
                }
            } catch (err) {
                console.error('加载子班级详情失败:', err);
                setError('加载子班级详情失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadSubClassDetail();
        }
    }, [id]);

    const handleEdit = () => {
        navigate(`/sub-classes/edit/${id}`);
    };

    const handleBack = () => {
        navigate('/sub-classes');
    };

    const handleViewTotalClass = () => {
        if (totalClass) {
            navigate(`/total-classes/${totalClass.totalClassId}`);
        }
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载子班级详情...</p>
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

    if (!subClass) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    子班级不存在
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
                <h2>子班级详情</h2>
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
                                        <strong>子班级ID：</strong>
                                        <span className="ms-2">{subClass.subClassId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>子班级名称：</strong>
                                        <span className="ms-2">{subClass.subClassName}</span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>所属总班级：</strong>
                                        <span className="ms-2">
                                            {totalClass ? (
                                                <Badge 
                                                    bg="info" 
                                                    style={{ cursor: 'pointer' }}
                                                    onClick={handleViewTotalClass}
                                                >
                                                    {totalClass.totalClassName}
                                                </Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>总班级ID：</strong>
                                        <span className="ms-2">{subClass.totalClassId || '-'}</span>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">关联信息</h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>所属专业：</strong>
                                        <span className="ms-2">
                                            {major ? (
                                                <Badge bg="success">
                                                    {major.majorName} (年级: {major.grade})
                                                </Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>专业ID：</strong>
                                        <span className="ms-2">{major?.majorId || '-'}</span>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={4}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">层级关系</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <div className="hierarchy-item">
                                        <i className="fas fa-university text-primary fa-2x mb-2"></i>
                                        <div className="fw-bold">
                                            {major?.majorName || '未设置专业'}
                                        </div>
                                        <small className="text-muted">专业</small>
                                    </div>
                                </div>
                                
                                <div className="mb-3">
                                    <i className="fas fa-arrow-down text-muted"></i>
                                </div>
                                
                                <div className="mb-3">
                                    <div className="hierarchy-item">
                                        <i className="fas fa-building text-info fa-2x mb-2"></i>
                                        <div className="fw-bold">
                                            {totalClass?.totalClassName || '未设置总班级'}
                                        </div>
                                        <small className="text-muted">总班级</small>
                                    </div>
                                </div>
                                
                                <div className="mb-3">
                                    <i className="fas fa-arrow-down text-muted"></i>
                                </div>
                                
                                <div className="mb-3">
                                    <div className="hierarchy-item">
                                        <i className="fas fa-users text-success fa-2x mb-2"></i>
                                        <div className="fw-bold">{subClass.subClassName}</div>
                                        <small className="text-muted">子班级</small>
                                    </div>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default SubClassDetail;
