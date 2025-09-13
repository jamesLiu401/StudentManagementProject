import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Container, Spinner, Alert } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as studentApi from '../api/student';
import * as teacherApi from '../api/teacher';
import * as paymentApi from '../api/payment';
import * as scoreApi from '../api/score';

const Dashboard = () => {
    const { currentUser, isAdmin } = useAuth();
    const [stats, setStats] = useState({
        totalStudents: 0,
        totalTeachers: 0,
        totalPayments: 0,
        totalScores: 0,
        pendingPayments: 0,
        averageScore: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);

            // 检查用户认证状态
            const token = localStorage.getItem('token');
            const user = localStorage.getItem('user');
            
            if (!token || !user) {
                throw new Error('用户未登录，请重新登录');
            }

            console.log('当前用户信息:', JSON.parse(user));
            console.log('Token存在:', !!token);

            // 并行加载所有统计数据
            const [
                studentsResponse,
                teachersResponse,
                paymentsResponse,
                scoresResponse
            ] = await Promise.all([
                studentApi.getStudents(),
                teacherApi.getTeachers(),
                paymentApi.getPayments(),
                scoreApi.getScores()
            ]);

            // 统一解包后端的ResponseMessage：真正数据在 data.data
            const students = studentsResponse?.data?.data || [];
            const teachers = teachersResponse?.data?.data || [];
            const payments = paymentsResponse?.data?.data || [];
            const scores = scoresResponse?.data?.data || [];

            // 计算待缴费数量
            const pendingPayments = Array.isArray(payments)
                ? payments.filter(p => p.paymentStatus === '未缴费').length
                : 0;

            // 计算平均分
            const averageScore = Array.isArray(scores) && scores.length > 0
                ? scores.reduce((sum, s) => sum + (s.score || 0), 0) / scores.length
                : 0;

            setStats({
                totalStudents: Array.isArray(students) ? students.length : 0,
                totalTeachers: Array.isArray(teachers) ? teachers.length : 0,
                totalPayments: Array.isArray(payments) ? payments.length : 0,
                totalScores: Array.isArray(scores) ? scores.length : 0,
                pendingPayments,
                averageScore: Math.round(averageScore * 100) / 100
            });

        } catch (err) {
            console.error('加载仪表板数据失败:', err);
            setError('加载数据失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const StatCard = ({ title, value, icon, color, subtitle }) => (
        <Card className="h-100 shadow-sm">
            <Card.Body className="d-flex align-items-center">
                <div className={`bg-${color} text-white rounded-circle d-flex align-items-center justify-content-center me-3`}
                     style={{ width: '60px', height: '60px' }}>
                    <i className={`fas ${icon} fs-4`}></i>
                </div>
                <div>
                    <h3 className="mb-1">{value}</h3>
                    <h6 className="text-muted mb-0">{title}</h6>
                    {subtitle && <small className="text-muted">{subtitle}</small>}
                </div>
            </Card.Body>
        </Card>
    );

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载仪表板数据...</p>
            </Container>
        );
    }

    if (error) {
        return (
            <Container>
                <Alert variant="danger">
                    <Alert.Heading>加载失败</Alert.Heading>
                    <p>{error}</p>
                    <button className="btn btn-outline-danger" onClick={loadDashboardData}>
                        重新加载
                    </button>
                </Alert>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">仪表板</h2>
                    <p className="text-muted mb-0">
                        欢迎回来，{currentUser?.username}！
                    </p>
                </div>
                <div className="text-end">
                    <small className="text-muted">
                        最后更新: {new Date().toLocaleString()}
                    </small>
                </div>
            </div>

            <Row className="g-4 mb-4">
                <Col md={6} lg={3}>
                    <StatCard
                        title="学生总数"
                        value={stats.totalStudents}
                        icon="fa-user-graduate"
                        color="primary"
                        subtitle="在校学生"
                    />
                </Col>
                <Col md={6} lg={3}>
                    <StatCard
                        title="教师总数"
                        value={stats.totalTeachers}
                        icon="fa-chalkboard-teacher"
                        color="success"
                        subtitle="在职教师"
                    />
                </Col>
                <Col md={6} lg={3}>
                    <StatCard
                        title="缴费记录"
                        value={stats.totalPayments}
                        icon="fa-credit-card"
                        color="info"
                        subtitle={`待缴费: ${stats.pendingPayments}`}
                    />
                </Col>
                <Col md={6} lg={3}>
                    <StatCard
                        title="成绩记录"
                        value={stats.totalScores}
                        icon="fa-chart-line"
                        color="warning"
                        subtitle={`平均分: ${stats.averageScore}`}
                    />
                </Col>
            </Row>

            <Row className="g-4">
                <Col lg={8}>
                    <Card className="h-100">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-chart-bar me-2"></i>
                                系统概览
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row className="text-center">
                                <Col md={4}>
                                    <div className="p-3">
                                        <h4 className="text-primary">{stats.totalStudents}</h4>
                                        <p className="text-muted mb-0">学生</p>
                                    </div>
                                </Col>
                                <Col md={4}>
                                    <div className="p-3">
                                        <h4 className="text-success">{stats.totalTeachers}</h4>
                                        <p className="text-muted mb-0">教师</p>
                                    </div>
                                </Col>
                                <Col md={4}>
                                    <div className="p-3">
                                        <h4 className="text-info">{stats.totalPayments}</h4>
                                        <p className="text-muted mb-0">缴费记录</p>
                                    </div>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </Col>
                <Col lg={4}>
                    <Card className="h-100">
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-info-circle me-2"></i>
                                系统信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-3">
                                <strong>当前用户:</strong>
                                <p className="text-muted mb-0">{currentUser?.username}</p>
                            </div>
                            <div className="mb-3">
                                <strong>用户角色:</strong>
                                <p className="text-muted mb-0">
                                    {currentUser?.role === 'ROLE_ADMIN' ? '管理员' : '教师'}
                                </p>
                            </div>
                            <div className="mb-3">
                                <strong>系统版本:</strong>
                                <p className="text-muted mb-0">v1.0.0</p>
                            </div>
                            <div>
                                <strong>最后登录:</strong>
                                <p className="text-muted mb-0">
                                    {new Date().toLocaleString()}
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Dashboard;
