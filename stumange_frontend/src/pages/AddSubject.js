import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as subjectApi from '../api/subject';
import * as academyApi from '../api/academy';

const AddSubject = () => {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [academies, setAcademies] = useState([]);
    const [academiesLoading, setAcademiesLoading] = useState(true);

    const [formData, setFormData] = useState({
        subjectName: '',
        academyId: '',
        credit: ''
    });

    // 加载学院列表
    useEffect(() => {
        const loadAcademies = async () => {
            try {
                setAcademiesLoading(true);
                const response = await academyApi.getAcademies();
                if (response && response.status === 200) {
                    setAcademies(response.data.data || []);
                } else {
                    setError('加载学院列表失败');
                }
            } catch (err) {
                console.error('加载学院列表失败:', err);
                setError('加载学院列表失败，请稍后重试');
            } finally {
                setAcademiesLoading(false);
            }
        };

        loadAcademies();
    }, []);

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
        
        if (!formData.academyId) {
            setError('请选择学院');
            return;
        }

        if (!formData.credit || isNaN(formData.credit) || parseFloat(formData.credit) <= 0) {
            setError('请输入有效的学分（大于0的数字）');
            return;
        }

        try {
            setLoading(true);
            setError('');
            const response = await subjectApi.createSubjectByParams(
                formData.subjectName.trim(),
                parseInt(formData.academyId,10),
                parseFloat(formData.credit)
            );
            
            if (response && response.status === 200) {
                navigate('/subjects');
            } else {
                setError('创建课程失败');
            }
        } catch (err) {
            console.error('创建课程失败:', err);
            setError(err?.response?.data?.message || '创建课程失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate('/subjects');
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

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>添加课程</h2>
                <Button variant="secondary" onClick={handleCancel}>
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
                                            name="academyId"
                                            value={formData.academyId}
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
                                        step="any"
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
                                        创建中...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        创建课程
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

export default AddSubject;
