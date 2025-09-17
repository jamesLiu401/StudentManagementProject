import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as scoreApi from '../api/score';
import * as studentApi from '../api/student';
import * as subjectApi from '../api/subject';

const AddScore = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [students, setStudents] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [formData, setFormData] = useState({
        studentId: searchParams.get('studentId') || '',
        subjectId: '',
        score: ''
    });

    useEffect(() => {
        const loadAllData = async () => {
            setLoadingData(true);
            setError('');
            try {
                await Promise.all([loadStudents(), loadSubjects()]);
            } catch (err) {
                console.error('加载数据失败:', err);
                setError('加载数据失败，请刷新页面重试');
            } finally {
                setLoadingData(false);
            }
        };
        
        loadAllData();
    }, []);

    const loadStudents = async () => {
        console.log('开始加载学生数据...');
        const response = await studentApi.getStudents();
        console.log('学生数据响应:', response);
        
        if (response && response.status === 200) {
            const responseData = response.data;
            console.log('学生数据解析:', responseData);
            
            if (responseData.status === 200 && responseData.data) {
                const studentsData = Array.isArray(responseData.data) ? responseData.data : [];
                console.log('设置学生数据:', studentsData);
                setStudents(studentsData);
            } else {
                console.warn('学生数据格式不正确:', responseData);
                throw new Error('学生数据格式错误');
            }
        } else {
            console.warn('学生数据请求失败:', response);
            throw new Error('学生数据请求失败');
        }
    };

    const loadSubjects = async () => {
        console.log('开始加载课程数据...');
        const response = await subjectApi.getSubjects();
        console.log('课程数据响应:', response);
        
        if (response && response.status === 200) {
            const responseData = response.data;
            console.log('课程数据解析:', responseData);
            console.log('responseData.status:', responseData.status);
            console.log('responseData.data:', responseData.data);
            
            if (responseData.status === 200 && responseData.data) {
                const subjectsData = Array.isArray(responseData.data) ? responseData.data : [];
                console.log('设置课程数据:', subjectsData);
                console.log('课程数据长度:', subjectsData.length);
                setSubjects(subjectsData);
            } else {
                console.warn('课程数据格式不正确:', responseData);
                throw new Error('课程数据格式错误');
            }
        } else {
            console.warn('课程数据请求失败:', response);
            throw new Error('课程数据请求失败');
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.studentId || !formData.subjectId || !formData.score) {
            setError('请填写所有必填字段');
            return;
        }

        const score = parseFloat(formData.score);
        if (isNaN(score) || score < 0 || score > 100) {
            setError('分数必须在0-100之间');
            return;
        }

        try {
            setLoading(true);
            setError('');
            setSuccess('');

            await scoreApi.createScoreByParams(
                parseInt(formData.studentId),
                parseInt(formData.subjectId),
                score
            );

            setSuccess('成绩添加成功！');
            setTimeout(() => {
                navigate('/scores');
            }, 1500);
        } catch (err) {
            console.error('添加成绩失败:', err);
            setError('添加成绩失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    const handleBack = () => {
        navigate('/scores');
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

    if (loadingData) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载学生和课程数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="mb-1">添加成绩</h2>
                    <p className="text-muted mb-0">录入新的学生成绩信息</p>
                </div>
                <Button variant="secondary" onClick={handleBack}>
                    <i className="fas fa-arrow-left me-2"></i>
                    返回列表
                </Button>
            </div>

            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">
                                <i className="fas fa-plus me-2"></i>
                                成绩信息
                            </h5>
                        </Card.Header>
                        <Card.Body>
                            {error && (
                                <Alert variant="danger">
                                    <i className="fas fa-exclamation-triangle me-2"></i>
                                    {error}
                                    <div className="mt-2">
                                        <Button 
                                            variant="outline-danger" 
                                            size="sm"
                                            onClick={() => {
                                                setError('');
                                                setLoadingData(true);
                                                Promise.all([loadStudents(), loadSubjects()])
                                                    .finally(() => setLoadingData(false));
                                            }}
                                        >
                                            <i className="fas fa-redo me-1"></i>
                                            重试
                                        </Button>
                                    </div>
                                </Alert>
                            )}

                            {success && (
                                <Alert variant="success">
                                    <i className="fas fa-check-circle me-2"></i>
                                    {success}
                                </Alert>
                            )}

                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>学生 <span className="text-danger">*</span></Form.Label>
                                    <Form.Select
                                        name="studentId"
                                        value={formData.studentId}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="">请选择学生</option>
                                        {students.map((student) => (
                                            <option key={student.stuId} value={student.stuId}>
                                                {student.stuName} (ID: {student.stuId})
                                                {student.subClassName && ` - ${student.subClassName}`}
                                            </option>
                                        ))}
                                    </Form.Select>
                                    <Form.Text className="text-muted">
                                        选择要录入成绩的学生 (共 {students.length} 个学生)
                                    </Form.Text>
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>课程 <span className="text-danger">*</span></Form.Label>
                                    <Form.Select
                                        name="subjectId"
                                        value={formData.subjectId}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="">请选择课程</option>
                                        {subjects.map((subject) => (
                                            <option key={subject.subjectId} value={subject.subjectId}>
                                                {subject.subjectName} ({subject.credit}学分)
                                                {subject.academyName && ` - ${subject.academyName}`}
                                            </option>
                                        ))}
                                    </Form.Select>
                                    <Form.Text className="text-muted">
                                        选择要录入成绩的课程 (共 {subjects.length} 个课程)
                                    </Form.Text>
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label>分数 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="score"
                                        value={formData.score}
                                        onChange={handleInputChange}
                                        min="0"
                                        max="100"
                                        step="0.1"
                                        placeholder="请输入分数 (0-100)"
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
                                        disabled={loading}
                                        size="lg"
                                    >
                                        {loading ? (
                                            <>
                                                <Spinner animation="border" size="sm" className="me-2" />
                                                添加中...
                                            </>
                                        ) : (
                                            <>
                                                <i className="fas fa-save me-2"></i>
                                                添加成绩
                                            </>
                                        )}
                                    </Button>
                                    <Button
                                        variant="outline-secondary"
                                        onClick={handleBack}
                                        disabled={loading}
                                    >
                                        <i className="fas fa-times me-2"></i>
                                        取消
                                    </Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 帮助信息 */}
            <Row className="mt-4">
                <Col md={8} lg={6} className="mx-auto">
                    <Card>
                        <Card.Header>
                            <h6 className="mb-0">
                                <i className="fas fa-info-circle me-2"></i>
                                录入说明
                            </h6>
                        </Card.Header>
                        <Card.Body>
                            <ul className="mb-0">
                                <li>请确保选择正确的学生和课程</li>
                                <li>分数范围为0-100分，支持小数点后一位</li>
                                <li>系统会自动计算成绩等级（优秀、良好、中等、及格、不及格）</li>
                                <li>如果该学生已有该课程的成绩，系统会提示是否覆盖</li>
                            </ul>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default AddScore;
