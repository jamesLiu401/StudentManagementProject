import React, { useEffect, useState } from 'react';
import { Container, Card, Button, Alert, Spinner, Row, Col, Badge } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getMajorById } from '../api/major';
import { getAcademyById } from '../api/academy';
import { useAuth } from '../contexts/AuthContexts';
import {getTeacherById} from "../api/teacher";

const MajorDetail = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const { isAdmin } = useAuth();
    const [major, setMajor] = useState(null);
    const [academyName, setAcademyName] = useState('');
    const [counselor, setCounselor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    useEffect(() => {
        const loadMajor = async () => {
            try {
                setLoading(true);
                setError('');
                const response = await getMajorById(id);
                if (response?.status === 200 && response?.data?.data) {
                    const m = response.data.data;
                    setMajor(m);
                    const acaId = m.academyId || (m.academy && m.academy.academyId);
                    if (acaId) {
                        try {
                            const acaResp = await getAcademyById(acaId);
                            if (acaResp?.status === 200 && acaResp?.data?.data?.academyName) {
                                setAcademyName(acaResp.data.data.academyName);
                            }
                        } catch {}
                    }
                    // 获取辅导员信息
                    if (m.counselorId) {
                        try {
                            const counselorResp = await getTeacherById(m.counselorId);
                            if (counselorResp?.status === 200 && counselorResp?.data?.data) {
                                setCounselor(counselorResp.data.data);
                            }
                        } catch (err) {
                            console.warn('获取辅导员信息失败:', err);
                        }
                    }
                } else {
                    setError('加载专业信息失败');
                }
            } catch (err) {
                console.error('加载专业信息失败', err);
                setError('加载专业信息失败：' + (err.response?.data?.message || '未知错误'));
            } finally {
                setLoading(false);
            }
        };
        if (id) loadMajor();
    }, [id]);

    if (loading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载专业信息...</p>
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
                <Button variant="secondary" onClick={() => navigate('/majors')}>
                    返回专业列表
                </Button>
            </Container>
        );
    }

    if (!major) {
        return (
            <Container fluid className="mt-4">
                <Alert variant="warning">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    未找到该专业信息
                </Alert>
                <Button variant="secondary" onClick={() => navigate('/majors')}>
                    返回专业列表
                </Button>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>专业详情</h2>
                <div>
                    {isAdmin() && (
                        <Button
                            variant="warning"
                            className="me-2"
                            onClick={() => navigate(`/majors/edit/${id}`)}
                        >
                            <i className="fas fa-edit me-2"></i>
                            编辑专业
                        </Button>
                    )}
                    <Button variant="secondary" onClick={() => navigate('/majors')}>
                        <i className="fas fa-arrow-left me-2"></i>
                        返回专业列表
                    </Button>
                </div>
            </div>

            <Row>
                <Col md={8}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-layer-group me-2"></i>
                                基本信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            <Row>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>专业ID：</strong>
                                        <span className="ms-2">{major.majorId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>专业名称：</strong>
                                        <span className="ms-2">{major.majorName}</span>
                                    </div>
                                </Col>
                                <Col md={6}>
                                    <div className="mb-3">
                                        <strong>年级：</strong>
                                        <span className="ms-2">{major.grade || '未设置'}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>学院：</strong>
                                        <span className="ms-2">{academyName ? (<Badge bg="info">{academyName}</Badge>) : (major.academyId || '未设置')}</span>
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
                                <i className="fas fa-user-tie me-2"></i>
                                辅导员信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            {counselor ? (
                                <>
                                    <div className="text-center mb-3">
                                        <div className="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center" 
                                             style={{width: '60px', height: '60px'}}>
                                            <i className="fas fa-user fa-lg"></i>
                                        </div>
                                    </div>
                                    <div className="mb-3">
                                        <strong>姓名：</strong>
                                        <span className="ms-2">{counselor.teacherName || '未设置'}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>工号：</strong>
                                        <span className="ms-2">
                                            <Badge bg="secondary">{counselor.teacherNo || '未设置'}</Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>部门：</strong>
                                        <span className="ms-2">{counselor.department || '未设置'}</span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>职称：</strong>
                                        <span className="ms-2">
                                            <Badge bg="info">{counselor.title || '未设置'}</Badge>
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <strong>教师ID：</strong>
                                        <span className="ms-2 text-muted">{counselor.teacherId}</span>
                                    </div>
                                    <div className="d-grid">
                                        <Button 
                                            variant="outline-primary" 
                                            size="sm"
                                            onClick={() => navigate(`/teachers/${counselor.teacherId}`)}
                                        >
                                            <i className="fas fa-eye me-2"></i>
                                            查看详情
                                        </Button>
                                    </div>
                                </>
                            ) : (
                                <div className="text-center text-muted">
                                    <i className="fas fa-user-slash fa-3x mb-3"></i>
                                    <p>暂未分配辅导员</p>
                                    {isAdmin() && (
                                        <Button 
                                            variant="outline-primary" 
                                            size="sm"
                                            onClick={() => navigate(`/majors/edit/${id}`)}
                                        >
                                            <i className="fas fa-plus me-2"></i>
                                            分配辅导员
                                        </Button>
                                    )}
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default MajorDetail;
