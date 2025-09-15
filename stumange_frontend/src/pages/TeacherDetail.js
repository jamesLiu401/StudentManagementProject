import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getTeacherById } from '../api/teacher';
import { useAuth } from '../contexts/AuthContexts';

const TeacherDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [teacher, setTeacher] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // 加载教师详情
    useEffect(() => {
        const loadTeacher = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await getTeacherById(id);
                if (response?.status === 200 && response?.data?.data) {
                    setTeacher(response.data.data);
                } else {
                    setError('加载教师信息失败');
                }
            } catch (err) {
                console.error('加载教师信息失败', err);
                setError('加载教师信息失败：' + (err.response?.data?.message || '未知错误'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadTeacher();
        }
    }, [id]);

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载教师信息...</p>
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
                <Button variant="secondary" onClick={() => navigate('/teachers')}>
                    返回教师列表
                </Button>
            </Container>
        );
    }

    if (!teacher) {
        return (
            <Container fluid className="mt-4">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    未找到该教师信息
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/teachers')}>
                    返回教师列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>教师详情</h2>
                <div>
                    {isAdmin() && (
                        <Button 
                            variant="warning" 
                            className="me-2"
                            onClick={() => navigate(`/teachers/edit/${id}`)}
                        >
                            <i className="fas fa-edit me-2"></i>
                            编辑教师
                        </Button>
                    )}
                    <Button 
                        variant="secondary" 
                        onClick={() => navigate('/teachers')}
                    >
                        <i className="fas fa-arrow-left me-2"></i>
                        返回教师列表
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
                                        <strong>教师ID：</strong>
                                        <span className="ms-2">{teacher.teacherId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>教师姓名：</strong>
                                        <span className="ms-2">{teacher.teacherName}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>工号：</strong>
                                        <span className="ms-2">{teacher.teacherNo}</span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>部门：</strong>
                                        <span className="ms-2">
                                            {teacher.department ? (
                                                <Badge bg="info">{teacher.department}</Badge>
                                            ) : (
                                                <span className="text-muted">未设置</span>
                                            )}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>职称：</strong>
                                        <span className="ms-2">
                                            {teacher.title ? (
                                                <Badge bg="success">{teacher.title}</Badge>
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
                                <i className="fas fa-chart-bar me-2"></i>
                                统计信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <i className="fas fa-graduation-cap fa-3x text-primary mb-2"></i>
                                    <h6>专业管理</h6>
                                    <p className="text-muted">
                                        {teacher.majors && teacher.majors.length > 0 
                                            ? `管理 ${teacher.majors.length} 个专业`
                                            : '暂无管理的专业'
                                        }
                                    </p>
                                </div>
                                <div className="mb-3">
                                    <i className="fas fa-book fa-3x text-success mb-2"></i>
                                    <h6>课程教学</h6>
                                    <p className="text-muted">
                                        {teacher.subjectClasses && teacher.subjectClasses.length > 0 
                                            ? `教授 ${teacher.subjectClasses.length} 门课程`
                                            : '暂无教授的课程'
                                        }
                                    </p>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 专业管理信息 */}
            {teacher.majors && teacher.majors.length > 0 && (
                <Card className="mt-4">
                    <Card.Header>
                        <h5 className="mb-0">
                            <i className="fas fa-graduation-cap me-2"></i>
                            管理的专业
                        </h5>
                    </Card.Header>
                    <Card.Body>
                        <Row>
                            {teacher.majors.map((major, index) => (
                                <Col md={6} key={index} className="mb-3">
                                    <div className="border rounded p-3">
                                        <h6>{major.majorName}</h6>
                                        <p className="text-muted mb-1">
                                            <i className="fas fa-university me-1"></i>
                                            学院ID：{major.academyId || '未设置'}
                                        </p>
                                        <p className="text-muted mb-0">
                                            <i className="fas fa-calendar me-1"></i>
                                            年级：{major.grade || '未设置'}
                                        </p>
                                    </div>
                                </Col>
                            ))}
                        </Row>
                    </Card.Body>
                </Card>
            )}

            {/* 课程教学信息 */}
            {teacher.subjectClasses && teacher.subjectClasses.length > 0 && (
                <Card className="mt-4">
                    <Card.Header>
                        <h5 className="mb-0">
                            <i className="fas fa-book me-2"></i>
                            教授的课程
                        </h5>
                    </Card.Header>
                    <Card.Body>
                        <Row>
                            {teacher.subjectClasses.map((subjectClass, index) => (
                                <Col md={6} key={index} className="mb-3">
                                    <div className="border rounded p-3">
                                        <h6>{subjectClass.subjectName || '未知课程'}</h6>
                                        <p className="text-muted mb-1">
                                            <i className="fas fa-university me-1"></i>
                                            {subjectClass.academy || '未知学院'}
                                        </p>
                                        <p className="text-muted mb-1">
                                            <i className="fas fa-star me-1"></i>
                                            学分：{subjectClass.credit || '未设置'}
                                        </p>
                                        <p className="text-muted mb-1">
                                            <i className="fas fa-users me-1"></i>
                                            班级：{subjectClass.subClassName || '未设置'}
                                        </p>
                                        <p className="text-muted mb-0">
                                            <i className="fas fa-calendar me-1"></i>
                                            学期：{subjectClass.semester || '未设置'} | 学年：{subjectClass.schoolYear || '未设置'}
                                        </p>
                                    </div>
                                </Col>
                            ))}
                        </Row>
                    </Card.Body>
                </Card>
            )}
        </Container>
    );
};

export default TeacherDetail;
