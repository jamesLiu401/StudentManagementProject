import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getStudentById } from '../api/student';
import { useAuth } from '../contexts/AuthContexts';

const StudentDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const loadStudent = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await getStudentById(id);
                if (response?.status === 200 && response?.data?.data) {
                    const s = response.data.data || {};
                    // 兼容后端可能的懒加载/字段差异
                    const className = s.subClassName || (s.subClass && s.subClass.subClassName) || (s.stuClass && s.stuClass.subClassName) || '-';
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
                        <Button 
                            variant="warning" 
                            className="me-2"
                            onClick={() => navigate(`/students/edit/${id}`)}
                        >
                            <i className="fas fa-edit me-2"></i>
                            编辑学生
                        </Button>
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

            <Row>
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
                                        <span className="ms-2">{student.stuGender ? '男' : '女'}</span>
                                    </div>
                                </Col>
                                <Col md={6}>
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
                                    <div className="mb-3">
                                        <strong>年级：</strong>
                                        <span className="ms-2">{student.grade || '未设置'}</span>
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
                            <div className="mb-2">
                                <strong>电话：</strong>
                                <span className="ms-2">{student.stuTel || '未填写'}</span>
                            </div>
                            <div className="mb-2">
                                <strong>地址：</strong>
                                <span className="ms-2">{student.stuAddress || '未填写'}</span>
                            </div>
                            <div className="mb-2">
                                <strong>专业：</strong>
                                <span className="ms-2">{student.stuMajor || '未填写'}</span>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default StudentDetail;
