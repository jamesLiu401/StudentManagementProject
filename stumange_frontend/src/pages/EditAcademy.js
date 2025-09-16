import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getAcademyById, updateAcademy } from '../api/academy';

const EditAcademy = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState({
        academyId: '',
        academyName: '',
        academyCode: '',
        description: '',
        deanName: '',
        contactPhone: '',
        address: ''
    });
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // 加载学院数据
    useEffect(() => {
        loadAcademyData();
    }, [id]);

    const loadAcademyData = async () => {
        try {
            setLoadingData(true);
            setError('');
            const response = await getAcademyById(id);
            
            if (response && response.status === 200) {
                const academy = response.data.data;
                setFormData({
                    academyId: academy.academyId,
                    academyName: academy.academyName || '',
                    academyCode: academy.academyCode || '',
                    description: academy.description || '',
                    deanName: academy.deanName || '',
                    contactPhone: academy.contactPhone || '',
                    address: academy.address || ''
                });
            } else {
                setError('加载学院数据失败');
            }
        } catch (err) {
            console.error('加载学院数据失败:', err);
            setError('加载学院数据失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoadingData(false);
        }
    };

    // 处理表单输入变化
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // 清除错误和成功提示
        setError('');
        setSuccess('');
    };

    // 处理表单提交
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await updateAcademy(id, formData);
            if (response?.status === 200) {
                setSuccess('更新学院成功！');
                // 3秒后返回学院列表
                setTimeout(() => {
                    navigate('/academies');
                }, 3000);
            } else {
                setError('更新学院失败，请重试');
            }
        } catch (err) {
            console.error('更新学院失败', err);
            setError('更新学院失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    if (loadingData) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载学院数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="mb-4">
                <h2>编辑学院</h2>
                <Button 
                    variant="secondary" 
                    className="mt-2" 
                    onClick={() => navigate('/academies')}
                >
                    返回学院列表
                </Button>
            </div>
            
            <Card>
                <Card.Body>
                    {error && (
                        <Alert variant="danger" className="mb-4">
                            {error}
                        </Alert>
                    )}
                    {success && (
                        <Alert variant="success" className="mb-4">
                            {success}
                        </Alert>
                    )}

                    <Form onSubmit={handleSubmit}>
                        <div className="row">
                            <div className="col-md-6">
                                {/* 学院名称 */}
                                <Form.Group controlId="formAcademyName" className="mb-3">
                                    <Form.Label>学院名称 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="academyName"
                                        value={formData.academyName}
                                        onChange={handleChange}
                                        placeholder="请输入学院名称"
                                        required
                                    />
                                </Form.Group>
                            </div>
                            <div className="col-md-6">
                                {/* 学院代码 */}
                                <Form.Group controlId="formAcademyCode" className="mb-3">
                                    <Form.Label>学院代码 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="academyCode"
                                        value={formData.academyCode}
                                        onChange={handleChange}
                                        placeholder="请输入学院代码，如：CS、EE等"
                                        required
                                    />
                                </Form.Group>
                            </div>
                        </div>

                        <div className="row">
                            <div className="col-md-6">
                                {/* 院长姓名 */}
                                <Form.Group controlId="formDeanName" className="mb-3">
                                    <Form.Label>院长姓名</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="deanName"
                                        value={formData.deanName}
                                        onChange={handleChange}
                                        placeholder="请输入院长姓名"
                                    />
                                </Form.Group>
                            </div>
                            <div className="col-md-6">
                                {/* 联系电话 */}
                                <Form.Group controlId="formContactPhone" className="mb-3">
                                    <Form.Label>联系电话</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="contactPhone"
                                        value={formData.contactPhone}
                                        onChange={handleChange}
                                        placeholder="请输入联系电话"
                                    />
                                </Form.Group>
                            </div>
                        </div>

                        {/* 地址 */}
                        <Form.Group controlId="formAddress" className="mb-3">
                            <Form.Label>地址</Form.Label>
                            <Form.Control
                                type="text"
                                name="address"
                                value={formData.address}
                                onChange={handleChange}
                                placeholder="请输入学院地址"
                            />
                        </Form.Group>

                        {/* 描述 */}
                        <Form.Group controlId="formDescription" className="mb-3">
                            <Form.Label>描述</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                                placeholder="请输入学院描述"
                            />
                        </Form.Group>

                        <div className="d-flex justify-content-end">
                            <Button 
                                variant="primary" 
                                type="submit" 
                                disabled={loading}
                                className="mr-2"
                            >
                                {loading ? '更新中...' : '更新学院'}
                            </Button>
                            <Button 
                                variant="secondary" 
                                onClick={() => navigate('/academies')}
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

export default EditAcademy;
