import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as scoreApi from '../api/score';

const EditScore = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [score, setScore] = useState(null);
    const [formData, setFormData] = useState({
        score: ''
    });

    useEffect(() => {
        const loadScore = async () => {
            try {
                setLoading(true);
                setError('');
                
                const response = await scoreApi.getScoreById(parseInt(id));
                if (response && response.status === 200) {
                    const scoreData = response.data.data;
                    setScore(scoreData);
                    setFormData({
                        score: scoreData.score || scoreData.stuScore || ''
                    });
                } else {
                    setError('加载成绩信息失败');
                }
            } catch (err) {
                console.error('加载成绩信息失败:', err);
                setError('加载成绩信息失败，请稍后重试');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            loadScore();
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
        
        if (!formData.score) {
            setError('请输入分数');
            return;
        }

        const newScore = parseFloat(formData.score);
        if (isNaN(newScore) || newScore < 0 || newScore > 100) {
            setError('分数必须在0-100之间');
            return;
        }

        try {
            setSaving(true);
            setError('');
            setSuccess('');

            await scoreApi.updateScore(parseInt(id), newScore);

            setSuccess('成绩更新成功！');
            setTimeout(() => {
                navigate('/scores');
            }, 1500);
        } catch (err) {
            console.error('更新成绩失败:', err);
            setError('更新成绩失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setSaving(false);
        }
    };

    const handleBack = () => {
        navigate('/scores');
    };

    const getScoreGrade = (score) => {
        if (score >= 90) return '优秀';
        if (score >= 80) return '良好';
        if (score >= 70) return '中等';
        if (score >= 60) return '及格';
        return '不及格';
    };

    const getScoreColor = (score) => {
        if (score >= 90) return 'success';
        if (score >= 80) return 'info';
        if (score >= 70) return 'warning';
        if (score >= 60) return 'primary';
        return 'danger';
    };

    if (!isAdmin()) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    您没有权限执行此操作
                </Alert>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回成绩列表
                </Button>
            </Container>
        );
    }

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载成绩信息...</p>
            </Container>
        );
    }

    if (error && !score) {
        return (
            <Container className="py-5">
                <Alert variant="danger">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回成绩列表
                </Button>
            </Container>
        );
    }

    if (!score) {
        return (
            <Container className="py-5">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    成绩不存在
                </Alert>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回成绩列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">编辑成绩</h2>
                    <p className="text-muted mb-0">修改学生成绩信息</p>
                </div>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回列表
                </Button>
            </div>

            <Row>
                <Col md={8}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-edit me-2"></i>
                                成绩信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            {error && (
                                <Alert variant="danger">
                                    <i className="fas fa-exclamation-triangle me-2"></i>
                                    {error}
                                </Alert>
                            )}

                            {success && (
                                <Alert variant="success">
                                    <i className="fas fa-check-circle me-2"></i>
                                    {success}
                                </Alert>
                            )}

                            <Form onSubmit={handleSubmit}>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>成绩ID</Form.Label>
                                            <Form.Control
                                                type="text"
                                                value={score.scoreId}
                                                disabled
                                                className="bg-light"
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>学生姓名</Form.Label>
                                            <Form.Control
                                                type="text"
                                                value={score.stuName || '未知'}
                                                disabled
                                                className="bg-light"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>课程名称</Form.Label>
                                            <Form.Control
                                                type="text"
                                                value={score.subjectName || '未知'}
                                                disabled
                                                className="bg-light"
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>当前分数</Form.Label>
                                            <Form.Control
                                                type="text"
                                                value={score.score || score.stuScore || 0}
                                                disabled
                                                className="bg-light"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Form.Group className="mb-4">
                                    <Form.Label>新分数 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="score"
                                        value={formData.score}
                                        onChange={handleInputChange}
                                        min="0"
                                        max="100"
                                        step="0.1"
                                        placeholder="请输入新的分数 (0-100)"
                                        required
                                    />
                                    <Form.Text className="text-muted">
                                        请输入0-100之间的分数，支持小数点后一位
                                    </Form.Text>
                                </Form.Group>

                                <div className="d-grid gap-2">
                                    <Button
                                        variant="primary"
                                        type="submit"
                                        disabled={saving}
                                        size="lg"
                                    >
                                        {saving ? (
                                            <>
                                                <Spinner animation="border" size="sm" className="me-2" />
                                                保存中...
                                            </>
                                        ) : (
                                            <>
                                                <i className="fas fa-save me-2"></i>
                                                保存修改
                                            </>
                                        )}
                                    </Button>
                                    <Button
                                        variant="outline-secondary"
                                        onClick={handleBack}
                                        disabled={saving}
                                    >
                                        <i className="fas fa-times me-2"></i>
                                        取消
                                    </Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={4}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">当前成绩</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="text-center">
                                <div className="mb-3">
                                    <div className="display-4 text-primary">
                                        {score.score || score.stuScore || 0}
                                    </div>
                                    <div className="text-muted">分数</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-success">
                                        {getScoreGrade(score.score || score.stuScore || 0)}
                                    </div>
                                    <div className="text-muted">等级</div>
                                </div>
                                <div className="mb-3">
                                    <div className="display-6 text-info">
                                        {(score.score || score.stuScore || 0) >= 60 ? '及格' : '不及格'}
                                    </div>
                                    <div className="text-muted">状态</div>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>

                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">快速操作</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="d-grid gap-2">
                                <Button 
                                    variant="outline-info" 
                                    onClick={() => navigate(`/scores/${id}`)}
                                >
                                    <i className="fas fa-eye me-2"></i>
                                    查看成绩详情
                                </Button>
                                <Button 
                                    variant="outline-secondary" 
                                    onClick={handleBack}
                                >
                                    <i className="fas fa-list me-2"></i>
                                    返回成绩列表
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default EditScore;
