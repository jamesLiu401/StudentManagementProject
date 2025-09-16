import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as classApi from '../api/class';

const AddSubClass = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [totalClasses, setTotalClasses] = useState([]);
    const [totalClassesLoading, setTotalClassesLoading] = useState(true);

    const [formData, setFormData] = useState({
        subClassName: '',
        totalClassId: searchParams.get('totalClassId') || ''
    });

    // 加载总班级列表
    useEffect(() => {
        const loadTotalClasses = async () => {
            try {
                setTotalClassesLoading(true);
                const response = await classApi.getTotalClasses();
                if (response && response.status === 200) {
                    setTotalClasses(response.data.data || []);
                } else {
                    setError('加载总班级列表失败');
                }
            } catch (err) {
                console.error('加载总班级列表失败:', err);
                setError('加载总班级列表失败，请稍后重试');
            } finally {
                setTotalClassesLoading(false);
            }
        };

        loadTotalClasses();
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
            
            const response = await classApi.createSubClass({
                subClassName: formData.subClassName.trim(),
                totalClassId: parseInt(formData.totalClassId,10)
            });
            
            if (response && response.status === 200) {
                navigate('/sub-classes');
            } else {
                setError('创建子班级失败');
            }
        } catch (err) {
            console.error('创建子班级失败:', err);
            setError(err?.response?.data?.message || '创建子班级失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        navigate('/sub-classes');
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
                <h2>添加子班级</h2>
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
                                        创建中...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-save me-2"></i>
                                        创建子班级
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

export default AddSubClass;
