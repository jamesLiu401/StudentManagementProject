import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import { getAcademyById, deleteAcademy } from '../api/academy';

const AcademyDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [academy, setAcademy] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadAcademyData();
    }, [id]);

    const loadAcademyData = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await getAcademyById(id);
            
            if (response && response.status === 200) {
                setAcademy(response.data.data);
            } else {
                setError('加载学院数据失败');
            }
        } catch (err) {
            console.error('加载学院数据失败:', err);
            setError('加载学院数据失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        if (!isAdmin() || !window.confirm('确定要删除这个学院吗？删除后无法恢复！')) return;
        
        try {
            await deleteAcademy(id);
            alert('学院删除成功！');
            navigate('/academies');
        } catch (err) {
            console.error('删除学院失败:', err);
            setError('删除学院失败：' + (err.response?.data?.message || '未知错误'));
        }
    };

    const handleEdit = () => {
        navigate(`/academies/edit/${id}`);
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载学院数据...</p>
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
                <Button variant="secondary" onClick={() => navigate('/academies')}>
                    返回学院列表
                </Button>
            </Container>
        );
    }

    if (!academy) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    未找到指定的学院信息
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/academies')}>
                    返回学院列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>学院详情</h2>
                <div>
                    <Button 
                        variant="secondary" 
                        className="me-2" 
                        onClick={() => navigate('/academies')}
                    >
                        <i className="fas fa-arrow-left me-2"></i>
                        返回列表
                    </Button>
                    {isAdmin() && (
                        <>
                            <Button 
                                variant="warning" 
                                className="me-2" 
                                onClick={handleEdit}
                            >
                                <i className="fas fa-edit me-2"></i>
                                编辑
                            </Button>
                            <Button 
                                variant="danger" 
                                onClick={handleDelete}
                            >
                                <i className="fas fa-trash me-2"></i>
                                删除
                            </Button>
                        </>
                    )}
                </div>
            </div>

            <Row>
                <Col lg={8}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-university me-2"></i>
                                基本信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>学院ID：</strong>
                                        <Badge bg="primary" className="ms-2">{academy.academyId}</Badge>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>学院代码：</strong>
                                        <Badge bg="info" className="ms-2">{academy.academyCode || '未设置'}</Badge>
                                    </div>
                                </Col>
                            </Row>
                            
                            <div className="mb-3">
                                <strong>学院名称：</strong>
                                <p className="mt-1 mb-0 fs-5 text-primary">{academy.academyName}</p>
                            </div>

                            {academy.description && (
                                <div className="mb-3">
                                    <strong>学院描述：</strong>
                                    <p className="mt-1 mb-0">{academy.description}</p>
                                </div>
                            )}
                        </Card.Body>
                    </Card>

                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-user-tie me-2"></i>
                                院长信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>院长姓名：</strong>
                                        <p className="mt-1 mb-0">{academy.deanName || '未设置'}</p>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>联系电话：</strong>
                                        <p className="mt-1 mb-0">
                                            {academy.contactPhone ? (
                                                <a href={`tel:${academy.contactPhone}`} className="text-decoration-none">
                                                    <i className="fas fa-phone me-1"></i>
                                                    {academy.contactPhone}
                                                </a>
                                            ) : (
                                                '未设置'
                                            )}
                                        </p>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    {academy.address && (
                        <Card className="mb-4">
                            <Card.Header>
                                <h5 className="mb-0">
                                    <i className="fas fa-map-marker-alt me-2"></i>
                                    地址信息
                                </h5>
                            </Card.Header>
                            <Card.Body>
                                <div className="mb-3">
                                    <strong>学院地址：</strong>
                                    <p className="mt-1 mb-0">{academy.address}</p>
                                </div>
                            </Card.Body>
                        </Card>
                    )}
                </Col>

                <Col lg={4}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-chart-bar me-2"></i>
                                统计信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <i className="fas fa-graduation-cap fa-3x text-primary mb-2"></i>
                                    <h4>专业数量</h4>
                                    <Badge bg="primary" className="fs-6">
                                        {academy.majors ? academy.majors.length : 0}
                                    </Badge>
                                </div>
                                
                                {academy.majors && academy.majors.length > 0 && (
                                    <div className="mt-3">
                                        <h6>下属专业：</h6>
                                        <div className="list-group list-group-flush">
                                            {academy.majors.slice(0, 5).map((major) => (
                                                <div key={major.majorId} className="list-group-item px-0 py-2">
                                                    <small className="text-muted">
                                                        {major.majorName}
                                                    </small>
                                                </div>
                                            ))}
                                            {academy.majors.length > 5 && (
                                                <div className="list-group-item px-0 py-2">
                                                    <small className="text-muted">
                                                        还有 {academy.majors.length - 5} 个专业...
                                                    </small>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </div>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-cogs me-2"></i>
                                快速操作
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="d-grid gap-2">
                                {isAdmin() && (
                                    <>
                                        <Button 
                                            variant="outline-primary" 
                                            onClick={handleEdit}
                                        >
                                            <i className="fas fa-edit me-2"></i>
                                            编辑学院信息
                                        </Button>
                                        <Button 
                                            variant="outline-success"
                                            onClick={() => navigate('/majors')}
                                        >
                                            <i className="fas fa-plus me-2"></i>
                                            添加专业
                                        </Button>
                                        <Button 
                                            variant="outline-info"
                                            onClick={() => navigate('/teachers')}
                                        >
                                            <i className="fas fa-users me-2"></i>
                                            查看教师
                                        </Button>
                                    </>
                                )}
                                <Button 
                                    variant="outline-secondary"
                                    onClick={() => navigate('/students')}
                                >
                                    <i className="fas fa-user-graduate me-2"></i>
                                    查看学生
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default AcademyDetail;
