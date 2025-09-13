import React, { useState, useEffect } from 'react';
import { Container, Card, Table, Button, Form, Row, Col, Modal, Alert, Spinner, Pagination } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as scoreApi from '../api/score';
import * as studentApi from '../api/student';
import * as subjectApi from '../api/subject';

const Scores = () => {
    const { isAdmin } = useAuth();
    const [scores, setScores] = useState([]);
    const [students, setStudents] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingScore, setEditingScore] = useState(null);
    const [formData, setFormData] = useState({
        studentId: '',
        subjectId: '',
        score: ''
    });
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize] = useState(10);

    useEffect(() => {
        loadScores();
        loadStudents();
        loadSubjects();
    }, [currentPage]);

    const loadScores = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await scoreApi.getScoresPage({
                page: currentPage,
                size: pageSize,
                sortBy: 'scoreId',
                sortDir: 'desc'
            });

            // 解包ResponseMessage
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setScores(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载成绩数据失败');
            }
        } catch (err) {
            console.error('加载成绩数据失败:', err);
            setError('加载成绩数据失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    const loadStudents = async () => {
        try {
            const response = await studentApi.getStudents();
            if (response && response.status === 200) {
                const list = response?.data?.data || [];
                setStudents(Array.isArray(list) ? list : []);
            }
        } catch (err) {
            console.error('加载学生数据失败:', err);
        }
    };

    const loadSubjects = async () => {
        try {
            const response = await subjectApi.getSubjects();
            if (response && response.status === 200) {
                const list = response?.data?.data || [];
                setSubjects(Array.isArray(list) ? list : []);
            }
        } catch (err) {
            console.error('加载课程数据失败:', err);
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
        try {
            if (editingScore) {
                await scoreApi.updateScore(editingScore.scoreId, parseFloat(formData.score));
            } else {
                await scoreApi.createScoreByParams(
                    parseInt(formData.studentId),
                    parseInt(formData.subjectId),
                    parseFloat(formData.score)
                );
            }
            setShowModal(false);
            setEditingScore(null);
            setFormData({
                studentId: '',
                subjectId: '',
                score: ''
            });
            loadScores();
        } catch (err) {
            console.error('保存成绩失败:', err);
            setError('保存成绩失败，请稍后重试');
        }
    };

    const handleEdit = (score) => {
        setEditingScore(score);
        setFormData({
            studentId: score.stuId || '',
            subjectId: score.subjectId || '',
            score: score.score || ''
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('确定要删除这个成绩吗？')) {
            try {
                await scoreApi.deleteScore(id);
                loadScores();
            } catch (err) {
                console.error('删除成绩失败:', err);
                setError('删除成绩失败，请稍后重试');
            }
        }
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

    if (loading && scores.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载成绩数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>成绩管理</h2>
                {isAdmin() && (
                    <Button
                        variant="primary"
                        onClick={() => {
                            setEditingScore(null);
                            setFormData({
                                studentId: '',
                                subjectId: '',
                                score: ''
                            });
                            setShowModal(true);
                        }}
                    >
                        <i className="fas fa-plus me-2"></i>
                        添加成绩
                    </Button>
                )}
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Card>
                <Card.Header>
                    <h5 className="mb-0">成绩列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>成绩ID</th>
                                <th>学生姓名</th>
                                <th>课程名称</th>
                                <th>分数</th>
                                <th>等级</th>
                                <th>录入时间</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {scores.map((score) => (
                                <tr key={score.scoreId}>
                                    <td>{score.scoreId}</td>
                                    <td>{score.stuName || '未知'}</td>
                                    <td>{score.subjectName || '未知'}</td>
                                    <td>
                                        <span className={`badge bg-${getScoreColor(score.score)}`}>
                                            {score.score}
                                        </span>
                                    </td>
                                    <td>
                                        <span className={`badge bg-${getScoreColor(score.score)}`}>
                                            {getScoreGrade(score.score)}
                                        </span>
                                    </td>
                                    <td>-</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button
                                                variant="outline-primary"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => handleEdit(score)}
                                            >
                                                <i className="fas fa-edit"></i>
                                            </Button>
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDelete(score.scoreId)}
                                            >
                                                <i className="fas fa-trash"></i>
                                            </Button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {totalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Pagination>
                                <Pagination.Prev
                                    disabled={currentPage === 0}
                                    onClick={() => setCurrentPage(currentPage - 1)}
                                />
                                {Array.from({ length: totalPages }, (_, i) => (
                                    <Pagination.Item
                                        key={i}
                                        active={i === currentPage}
                                        onClick={() => setCurrentPage(i)}
                                    >
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next
                                    disabled={currentPage === totalPages - 1}
                                    onClick={() => setCurrentPage(currentPage + 1)}
                                />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>

            {/* 添加/编辑成绩模态框 */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {editingScore ? '编辑成绩' : '添加成绩'}
                    </Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleSubmit}>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>学生 *</Form.Label>
                            <Form.Select
                                name="studentId"
                                value={formData.studentId}
                                onChange={handleInputChange}
                                required
                                disabled={!!editingScore}
                            >
                                <option value="">请选择学生</option>
                                {students.map((student) => (
                                    <option key={student.stuId} value={student.stuId}>
                                        {student.stuName} ({student.stuNo})
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>课程 *</Form.Label>
                            <Form.Select
                                name="subjectId"
                                value={formData.subjectId}
                                onChange={handleInputChange}
                                required
                                disabled={!!editingScore}
                            >
                                <option value="">请选择课程</option>
                                {subjects.map((subject) => (
                                    <option key={subject.subjectId} value={subject.subjectId}>
                                        {subject.subjectName} ({subject.credit}学分)
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>分数 *</Form.Label>
                            <Form.Control
                                type="number"
                                name="score"
                                value={formData.score}
                                onChange={handleInputChange}
                                min="0"
                                max="100"
                                step="0.1"
                                required
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                            取消
                        </Button>
                        <Button variant="primary" type="submit">
                            {editingScore ? '更新' : '添加'}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    );
};

export default Scores;
