import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge, Table } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';
import * as majorApi from '../api/major';

const TotalClassDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [totalClass, setTotalClass] = useState(null);
    const [major, setMajor] = useState(null);
    const [subClasses, setSubClasses] = useState([]);
    const [subClassesLoading, setSubClassesLoading] = useState(false);

    useEffect(() => {
        const loadTotalClassDetail = async () => {
            try {
                setLoading(true);
                setError('');
                
                // 加载总班级详情
                const totalClassResponse = await classApi.getTotalClassById(parseInt(id));
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
                    
                    // 加载子班级列表
                    setSubClassesLoading(true);
                    const subClassesResponse = await classApi.getSubClassesByTotalClass(totalClassData.totalClassId);
                    if (subClassesResponse && subClassesResponse.status === 200) {
                        setSubClasses(subClassesResponse.data.data || []);
                    }
                } else {
                    setError('加载总班级详情失败');
                }
            } catch (err) {
                console.error('加载总班级详情失败:', err);
                setError('加载总班级详情失败，请稍后重试');
            } finally {
                setLoading(false);
                setSubClassesLoading(false);
            }
        };

        if (id) {
            loadTotalClassDetail();
        }
    }, [id]);

    const handleEdit = () => {
        navigate(`/total-classes/edit/${id}`);
    };

    const handleBack = () => {
        navigate('/total-classes');
    };

    const handleViewSubClass = (subClassId) => {
        navigate(`/sub-classes/${subClassId}`);
    };

    const handleAddSubClass = () => {
        navigate(`/sub-classes/add?totalClassId=${id}`);
    };

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载总班级详情...</p>
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

    if (!totalClass) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    总班级不存在
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
                <h2>总班级详情</h2>
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
                                        <strong>总班级ID：</strong>
                                        <span className="ms-2">{totalClass.totalClassId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>总班级名称：</strong>
                                        <span className="ms-2">{totalClass.totalClassName}</span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>所属专业：</strong>
                                        <span className="ms-2">
                                            {major ? (
                                                <Badge bg="info">
                                                    {major.majorName} (年级: {major.grade})
                                                </Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>专业ID：</strong>
                                        <span className="ms-2">{totalClass.majorId || '-'}</span>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <h5 className="mb-0">子班级列表</h5>
                            {isAdmin() && (
                                <Button variant="success" size="sm" onClick={handleAddSubClass}>
                                    <i className="fas fa-plus me-1"></i>
                                    添加子班级
                                </Button>
                            )}
                        </Card.Header>
                        <Card.Body>
                            {subClassesLoading ? (
                                <div className="text-center py-3">
                                    <Spinner size="sm" className="me-2" />
                                    加载子班级列表...
                                </div>
                            ) : subClasses.length > 0 ? (
                                <Table responsive striped hover>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>子班级名称</th>
                                            <th>操作</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {subClasses.map(subClass => (
                                            <tr key={subClass.subClassId}>
                                                <td>{subClass.subClassId}</td>
                                                <td>{subClass.subClassName}</td>
                                                <td>
                                                    <Button
                                                        variant="outline-info"
                                                        size="sm"
                                                        onClick={() => handleViewSubClass(subClass.subClassId)}
                                                    >
                                                        <i className="fas fa-eye"></i>
                                                    </Button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            ) : (
                                <div className="text-center py-4 text-muted">
                                    <i className="fas fa-inbox fa-2x mb-2"></i>
                                    <p>暂无子班级</p>
                                    {isAdmin() && (
                                        <Button variant="outline-primary" onClick={handleAddSubClass}>
                                            <i className="fas fa-plus me-1"></i>
                                            添加第一个子班级
                                        </Button>
                                    )}
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={4}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">统计信息</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <div className="display-6 text-primary">{subClasses.length}</div>
                                    <div className="text-muted">子班级数量</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-success">
                                        {subClasses.length > 0 ? '100%' : '0%'}
                                    </div>
                                    <div className="text-muted">子班级覆盖率</div>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default TotalClassDetail;
