import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';
import * as majorApi from '../api/major';

const EditTotalClass = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);
    const [error, setError] = useState('');
    const [majors, setMajors] = useState([]);
    const [majorsLoading, setMajorsLoading] = useState(true);

    const [formData, setFormData] = useState({
        totalClassName: '',
        majorId: ''
    });

    // 加载总班级详情和专业列表
    useEffect(() => {
        const loadData = async () => {
            try {
                setInitialLoading(true);
                setError('');

                // 并行加载总班级详情和专业列表
                const [totalClassResponse, majorsResponse] = await Promise.all([
                    classApi.getTotalClassById(parseInt(id)),
                    majorApi.getMajors()
                ]);

                // 处理总班级详情
                if (totalClassResponse && totalClassResponse.status === 200) {
                    const totalClassData = totalClassResponse.data.data;
                    setFormData({
                        totalClassName: totalClassData.totalClassName || '',
                        majorId: totalClassData.majorId || ''
                    });
                } else {
                    setError('加载总班级详情失败');
                    return;
                }

                // 处理专业列表
                if (majorsResponse && majorsResponse.status === 200) {
                    setMajors(majorsResponse.data.data || []);
                } else {
                    setError('加载专业列表失败');
                }
            } catch (err) {
                console.error('加载数据失败:', err);
                setError('加载数据失败，请稍后重试');
            } finally {
                setInitialLoading(false);
                setMajorsLoading(false);
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
        
        if (!formData.totalClassName.trim()) {
            setError('请输入总班级名称');
            return;
        }
        
        if (!formData.majorId) {
            setError('请选择专业');
            return;
        }

        try {
            setLoading(true);
            setError('');
            
            const response = await classApi.updateTotalClass(parseInt(id), {
                totalClassName: formData.totalClassName.trim(),
                majorId: parseInt(formData.majorId)
            });
            
            if (response && response.status === 200) {
                navigate(`/total-classes/${id}`);
            } else {
                setError('更新总班级失败');
            }
        } catch (err) {
            console.error('更新总班级失败:', err);
            setError(err?.response?.data?.message || '更新总班级失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate(`/total-classes/${id}`);
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
                <h2>编辑总班级</h2>
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
                    <h5 className="mb-0">总班级信息</h5>
                </Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSubmit}>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>总班级名称 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="totalClassName"
                                        value={formData.totalClassName}
                                        onChange={handleInputChange}
                                        placeholder="请输入总班级名称"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>所属专业 *</Form.Label>
                                    {majorsLoading ? (
                                        <div className="d-flex align-items-center">
                                            <Spinner size="sm" className="me-2" />
                                            <span>加载专业列表...</span>
                                        </div>
                                    ) : (
                                        <Form.Select
                                            name="majorId"
                                            value={formData.majorId}
                                            onChange={handleInputChange}
                                            required
                                        >
                                            <option value="">请选择专业</option>
                                            {majors.map(major => (
                                                <option key={major.majorId} value={major.majorId}>
                                                    {major.majorName} (年级: {major.grade})
                                                </option>
                                            ))}
                                        </Form.Select>
                                    )}
                                </Form.Group>
                            </Col>
                        </Row>

                        <div className="d-flex gap-2">
                            <Button
                                type="submit"
                                variant="primary"
                                disabled={loading || majorsLoading}
                            >
                                {loading ? (
                                    <>
                                        <Spinner size="sm" className="me-2" />
                                        更新中...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        更新总班级
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

export default EditTotalClass;
