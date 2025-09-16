import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';

const EditSubClass = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);
    const [error, setError] = useState('');
    const [totalClasses, setTotalClasses] = useState([]);
    const [totalClassesLoading, setTotalClassesLoading] = useState(true);

    const [formData, setFormData] = useState({
        subClassName: '',
        totalClassId: ''
    });

    // 加载子班级详情和总班级列表
    useEffect(() => {
        const loadData = async () => {
            try {
                setInitialLoading(true);
                setError('');

                // 并行加载子班级详情和总班级列表
                const [subClassResponse, totalClassesResponse] = await Promise.all([
                    classApi.getSubClassById(parseInt(id)),
                    classApi.getTotalClasses()
                ]);

                // 处理子班级详情
                if (subClassResponse && subClassResponse.status === 200) {
                    const subClassData = subClassResponse.data.data;
                    setFormData({
                        subClassName: subClassData.subClassName || '',
                        totalClassId: subClassData.totalClassId || ''
                    });
                } else {
                    setError('加载子班级详情失败');
                    return;
                }

                // 处理总班级列表
                if (totalClassesResponse && totalClassesResponse.status === 200) {
                    setTotalClasses(totalClassesResponse.data.data || []);
                } else {
                    setError('加载总班级列表失败');
                }
            } catch (err) {
                console.error('加载数据失败:', err);
                setError('加载数据失败，请稍后重试');
            } finally {
                setInitialLoading(false);
                setTotalClassesLoading(false);
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
        
        if (!formData.subClassName.trim()) {
            setError('请输入子班级名称');
            return;
        }
        
        if (!formData.totalClassId) {
            setError('请选择总班级');
            return;
        }

        try {
            setLoading(true);
            setError('');
            
            const response = await classApi.updateSubClass(parseInt(id), {
                subClassName: formData.subClassName.trim(),
                totalClassId: parseInt(formData.totalClassId, 10)
            });
            
            if (response && response.status === 200) {
                navigate(`/sub-classes/${id}`);
            } else {
                setError('更新子班级失败');
            }
        } catch (err) {
            console.error('更新子班级失败:', err);
            setError(err?.response?.data?.message || '更新子班级失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate(`/sub-classes/${id}`);
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
                <h2>编辑子班级</h2>
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
                    <h5 className="mb-0">子班级信息</h5>
                </Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSubmit}>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>子班级名称 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="subClassName"
                                        value={formData.subClassName}
                                        onChange={handleInputChange}
                                        placeholder="请输入子班级名称"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>所属总班级 *</Form.Label>
                                    {totalClassesLoading ? (
                                        <div className="d-flex align-items-center">
                                            <Spinner size="sm" className="me-2" />
                                            <span>加载总班级列表...</span>
                                        </div>
                                    ) : (
                                        <Form.Select
                                            name="totalClassId"
                                            value={formData.totalClassId}
                                            onChange={handleInputChange}
                                            required
                                        >
                                            <option value="">请选择总班级</option>
                                            {totalClasses.map(totalClass => (
                                                <option key={totalClass.totalClassId} value={totalClass.totalClassId}>
                                                    {totalClass.totalClassName}
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
                                disabled={loading || totalClassesLoading}
                            >
                                {loading ? (
                                    <>
                                        <Spinner size="sm" className="me-2" />
                                        更新中...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        更新子班级
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

export default EditSubClass;
