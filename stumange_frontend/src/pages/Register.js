import React, { useState } from 'react';
import { Container, Card, Form, Button, Alert, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { register } from '../api/auth';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        role: 'TEACHER'
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // 清除错误信息
        if (error) setError('');
    };

    const validateForm = () => {
        if (!formData.username.trim()) {
            setError('请输入用户名');
            return false;
        }
        if (formData.username.length < 3) {
            setError('用户名至少需要3个字符');
            return false;
        }
        if (!formData.password) {
            setError('请输入密码');
            return false;
        }
        if (formData.password.length < 6) {
            setError('密码至少需要6个字符');
            return false;
        }
        if (formData.password !== formData.confirmPassword) {
            setError('两次输入的密码不一致');
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await register({
                username: formData.username,
                password: formData.password,
                role: formData.role
            });

            // 检查响应状态
            if (response.status === 200) {
                setSuccess('用户注册成功！');
                setTimeout(() => {
                    navigate('/login');
                }, 2000);
            } else {
                setError(response.message || '注册失败');
            }
        } catch (err) {
            console.error('注册错误:', err);
            setError(err.response?.data?.message || err.message || '注册失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={6} lg={5}>
                    <Card className="shadow">
                        <Card.Header className="text-center bg-primary text-white">
                            <h4 className="mb-0">
                                <i className="fas fa-user-plus me-2"></i>
                                用户注册
                            </h4>
                        </Card.Header>
                        <Card.Body className="p-4">
                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    <i className="fas fa-exclamation-triangle me-2"></i>
                                    {error}
                                </Alert>
                            )}
                            
                            {success && (
                                <Alert variant="success" className="mb-3">
                                    <i className="fas fa-check-circle me-2"></i>
                                    {success}
                                </Alert>
                            )}

                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        <i className="fas fa-user me-2"></i>
                                        用户名
                                    </Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="username"
                                        value={formData.username}
                                        onChange={handleChange}
                                        placeholder="请输入用户名"
                                        disabled={loading}
                                        required
                                    />
                                    <Form.Text className="text-muted">
                                        用户名至少3个字符，支持字母、数字和下划线
                                    </Form.Text>
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        <i className="fas fa-lock me-2"></i>
                                        密码
                                    </Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        placeholder="请输入密码"
                                        disabled={loading}
                                        required
                                    />
                                    <Form.Text className="text-muted">
                                        密码至少6个字符
                                    </Form.Text>
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        <i className="fas fa-lock me-2"></i>
                                        确认密码
                                    </Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="confirmPassword"
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        placeholder="请再次输入密码"
                                        disabled={loading}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label>
                                        <i className="fas fa-user-tag me-2"></i>
                                        用户角色
                                    </Form.Label>
                                    <Form.Select
                                        name="role"
                                        value={formData.role}
                                        onChange={handleChange}
                                        disabled={loading}
                                    >
                                        <option value="TEACHER">教师</option>
                                        <option value="ADMIN">管理员</option>
                                    </Form.Select>
                                    <Form.Text className="text-muted">
                                        选择用户角色，管理员拥有更多权限
                                    </Form.Text>
                                </Form.Group>

                                <div className="d-grid gap-2">
                                    <Button
                                        type="submit"
                                        variant="primary"
                                        size="lg"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                                注册中...
                                            </>
                                        ) : (
                                            <>
                                                <i className="fas fa-user-plus me-2"></i>
                                                注册
                                            </>
                                        )}
                                    </Button>
                                </div>
                            </Form>

                            <div className="text-center mt-4">
                                <p className="text-muted mb-0">
                                    已有账号？
                                    <Button
                                        variant="link"
                                        className="p-0 ms-1"
                                        onClick={() => navigate('/login')}
                                        disabled={loading}
                                    >
                                        立即登录
                                    </Button>
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Register;
