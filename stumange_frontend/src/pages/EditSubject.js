import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as subjectApi from '../api/subject';
import * as academyApi from '../api/academy';

const EditSubject = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);
    const [error, setError] = useState('');
    const [academies, setAcademies] = useState([]);
    const [academiesLoading, setAcademiesLoading] = useState(true);

    const [formData, setFormData] = useState({
        subjectName: '',
        academy: '',
        credit: ''
    });

    // 加载课程详情和学院列表
    useEffect(() => {
        const loadData = async () => {
            try {
                setInitialLoading(true);
                setError('');

                // 并行加载课程详情和学院列表
                const [subjectResponse, academiesResponse] = await Promise.all([
                    subjectApi.getSubjectById(parseInt(id)),
                    academyApi.getAcademies()
                ]);

                // 处理课程详情
                if (subjectResponse && subjectResponse.status === 200) {
                    const subjectData = subjectResponse.data.data;
                    setFormData({
                        subjectName: subjectData.subjectName || '',
                        academy: subjectData.academy || '',
                        credit: subjectData.credit || ''
                    });
                } else {
                    setError('加载课程详情失败');
                    return;
                }

                // 处理学院列表
                if (academiesResponse && academiesResponse.status === 200) {
                    setAcademies(academiesResponse.data.data || []);
                } else {
                    setError('加载学院列表失败');
                }
            } catch (err) {
                console.error('加载数据失败:', err);
                setError('加载数据失败，请稍后重试');
            } finally {
                setInitialLoading(false);
                setAcademiesLoading(false);
            }
        };

        if (id) {
            loadData();
        }
    }, [id]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.subjectName.trim()) {
            setError('请输入课程名称');
            return;
        }
        
        if (!formData.academy) {
            setError('请选择学院');
            return;
        }

        if (!formData.credit || isNaN(formData.credit) || parseInt(formData.credit) <= 0) {
            setError('请输入有效的学分（大于0的数字）');
            return;
        }

        try {
            setLoading(true);
            setError('');
            
            const response = await subjectApi.updateSubject(parseInt(id), {
                subjectName: formData.subjectName.trim(),
                academy: parseInt(formData.academy),
                credit: parseInt(formData.credit)
            });
            
            if (response && response.status === 200) {
                navigate(`/subjects/${id}`);
            } else {
                setError('更新课程失败');
            }
        } catch (err) {
            console.error('更新课程失败:', err);
            setError(err?.response?.data?.message || '更新课程失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate(`/subjects/${id}`);
    };

    if (!isAdmin()) {
        return (
            <Container className="text-center py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    您没有权限访问此页面
                </Alert>
            </Container>
        );
    }

    if (initialLoading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>编辑课程</h2>
                <Button variant="secondary" onClick={handleCancel}>
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

            <Card>
                <Card.Header>
                    <h5 className="mb-0">课程信息</h5>
                </Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSubmit}>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>课程名称 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="subjectName"
                                        value={formData.subjectName}
                                        onChange={handleInputChange}
                                        placeholder="请输入课程名称"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group className="mb-3">
                                    <Form.Label>所属学院 *</Form.Label>
                                    {academiesLoading ? (
                                        <div className="d-flex align-items-center">
                                            <Spinner size="sm" className="me-2" />
                                            <span>加载学院列表...</span>
                                        </div>
                                    ) : (
                                        <Form.Select
                                            name="academy"
                                            value={formData.academy}
                                            onChange={handleInputChange}
                                            required
                                        >
                                            <option value="">请选择学院</option>
                                            {academies.map(academy => (
                                                <option key={academy.academyId} value={academy.academyId}>
                                                    {academy.academyName}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    )}
                                </Form.Group>
                            </Col>
                            <Col md={3}>
                                <Form.Group className="mb-3">
                                    <Form.Label>学分 *</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="credit"
                                        value={formData.credit}
                                        onChange={handleInputChange}
                                        placeholder="请输入学分"
                                        min="1"
                                        max="10"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <div className="d-flex gap-2">
                            <Button
                                type="submit"
                                variant="primary"
                                disabled={loading || academiesLoading}
                            >
                                {loading ? (
                                    <>
                                        <Spinner size="sm" className="me-2" />
                                        更新中...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        更新课程
                                    </>
                                )}
                            </Button>
                            <Button
                                type="button"
                                variant="secondary"
                                onClick={handleCancel}
                                disabled={loading}
                            >
                                取消
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default EditSubject;
