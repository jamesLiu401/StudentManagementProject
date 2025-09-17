import React, { useState, useEffect } from 'react';
import { Container, Card, Table, Button, Form, Row, Col, Modal, Alert, Spinner, Pagination, Badge, ProgressBar, Tabs, Tab, InputGroup } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import { useNavigate } from 'react-router-dom';
import * as scoreApi from '../api/score';
import * as studentApi from '../api/student';
import * as subjectApi from '../api/subject';

const Scores = () => {
    const { isAdmin } = useAuth();
    const navigate = useNavigate();
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
    const [statistics, setStatistics] = useState({
        totalScores: 0,
        averageScore: 0,
        maxScore: 0,
        minScore: 0,
        passingCount: 0,
        failingCount: 0
    });
    const [filters, setFilters] = useState({
        studentId: '',
        subjectId: '',
        minScore: '',
        maxScore: '',
        grade: ''
    });
    const [searchKeyword, setSearchKeyword] = useState('');
    const [activeTab, setActiveTab] = useState('list');
    const [selectedScores, setSelectedScores] = useState([]);
    const [showBatchModal, setShowBatchModal] = useState(false);
    const [batchData, setBatchData] = useState('');
    const [sortBy, setSortBy] = useState('scoreId');
    const [sortDir, setSortDir] = useState('desc');
    const [confirmDelete, setConfirmDelete] = useState(null);
    const [deleting, setDeleting] = useState(false);
    const [success, setSuccess] = useState('');

    useEffect(() => {
        loadScores();
        loadStudents();
        loadSubjects();
    }, [currentPage, sortBy, sortDir]);

    useEffect(() => {
        loadScores();
    }, [filters, searchKeyword]);

    useEffect(() => {
        if (scores.length > 0) {
            loadStatistics();
        }
    }, [scores]);

    // 自动清除成功消息
    useEffect(() => {
        if (success) {
            const timer = setTimeout(() => {
                setSuccess('');
            }, 3000);
            return () => clearTimeout(timer);
        }
    }, [success]);

    const loadScores = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await scoreApi.getScoresPage({
                page: currentPage,
                size: pageSize,
                sortBy,
                sortDir
            });

            // 正确解析ResponseMessage结构
            if (response && response.status === 200 && response.data) {
                const responseData = response.data;
                if (responseData.success && responseData.data) {
                    const pageData = responseData.data;
                    // 处理成绩数据，统一字段映射
                    const processedScores = Array.isArray(pageData.content) ? 
                        pageData.content.map(score => ({
                            ...score,
                            // 统一字段映射：后端可能返回stuScore，前端使用score
                            score: score.score || score.stuScore || 0,
                            // 确保学生和课程名称存在
                            stuName: score.stuName || (score.student && score.student.stuName) || '未知学生',
                            subjectName: score.subjectName || (score.subject && score.subject.subjectName) || '未知课程'
                        })) : [];
                    setScores(processedScores);
                    setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
                } else {
                    setError(responseData.message || '加载成绩数据失败');
                }
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
                const responseData = response.data;
                if (responseData.success && responseData.data) {
                    setStudents(Array.isArray(responseData.data) ? responseData.data : []);
                }
            }
        } catch (err) {
            console.error('加载学生数据失败:', err);
        }
    };

    const loadSubjects = async () => {
        try {
            const response = await subjectApi.getSubjects();
            if (response && response.status === 200) {
                const responseData = response.data;
                if (responseData.success && responseData.data) {
                    setSubjects(Array.isArray(responseData.data) ? responseData.data : []);
                }
            }
        } catch (err) {
            console.error('加载课程数据失败:', err);
        }
    };

    const loadStatistics = async () => {
        try {
            // 这里可以添加统计信息的加载逻辑
            // 由于后端没有提供总体统计接口，我们基于当前数据计算
            if (scores.length > 0) {
                const totalScores = scores.length;
                const totalScore = scores.reduce((sum, score) => sum + (score.score || 0), 0);
                const averageScore = totalScore / totalScores;
                const maxScore = Math.max(...scores.map(s => s.score || 0));
                const minScore = Math.min(...scores.map(s => s.score || 0));
                const passingCount = scores.filter(s => (s.score || 0) >= 60).length;
                const failingCount = totalScores - passingCount;

                setStatistics({
                    totalScores,
                    averageScore,
                    maxScore,
                    minScore,
                    passingCount,
                    failingCount
                });
            }
        } catch (err) {
            console.error('加载统计信息失败:', err);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const clearFilters = () => {
        setFilters({
            studentId: '',
            subjectId: '',
            minScore: '',
            maxScore: '',
            grade: ''
        });
        setSearchKeyword('');
    };

    const filteredScores = scores.filter(score => {
        // 学生筛选
        if (filters.studentId && score.stuId !== parseInt(filters.studentId)) {
            return false;
        }
        // 课程筛选
        if (filters.subjectId && score.subjectId !== parseInt(filters.subjectId)) {
            return false;
        }
        // 分数范围筛选
        if (filters.minScore && score.score < parseFloat(filters.minScore)) {
            return false;
        }
        if (filters.maxScore && score.score > parseFloat(filters.maxScore)) {
            return false;
        }
        // 等级筛选
        if (filters.grade) {
            const grade = getScoreGrade(score.score);
            if (grade !== filters.grade) {
                return false;
            }
        }
        // 关键词搜索
        if (searchKeyword) {
            const keyword = searchKeyword.toLowerCase();
            return (
                (score.stuName && score.stuName.toLowerCase().includes(keyword)) ||
                (score.subjectName && score.subjectName.toLowerCase().includes(keyword))
            );
        }
        return true;
    });

    const handleSelectScore = (scoreId) => {
        setSelectedScores(prev => 
            prev.includes(scoreId) 
                ? prev.filter(id => id !== scoreId)
                : [...prev, scoreId]
        );
    };

    const handleSelectAll = () => {
        if (selectedScores.length === filteredScores.length) {
            setSelectedScores([]);
        } else {
            setSelectedScores(filteredScores.map(score => score.scoreId));
        }
    };

    const handleBatchDelete = async () => {
        if (selectedScores.length === 0) {
            setError('请选择要删除的成绩');
            return;
        }
        
        if (window.confirm(`确定要删除选中的 ${selectedScores.length} 条成绩记录吗？`)) {
            try {
                setError('');
                for (const scoreId of selectedScores) {
                    await scoreApi.deleteScore(scoreId);
                }
                setSelectedScores([]);
                setSuccess(`成功删除 ${selectedScores.length} 条成绩记录！`);
                loadScores();
            } catch (err) {
                console.error('批量删除失败:', err);
                setError('批量删除失败，请稍后重试');
            }
        }
    };

    const handleBatchImport = async () => {
        if (!batchData.trim()) {
            setError('请输入批量数据');
            return;
        }

        try {
            setError('');
            const lines = batchData.trim().split('\n');
            const scores = [];
            
            for (const line of lines) {
                const [studentId, subjectId, score] = line.split(',').map(s => s.trim());
                if (studentId && subjectId && score) {
                    scores.push({
                        studentId: parseInt(studentId),
                        subjectId: parseInt(subjectId),
                        score: parseFloat(score)
                    });
                }
            }

            if (scores.length === 0) {
                setError('没有有效的成绩数据');
                return;
            }

            // 这里可以调用批量创建API
            for (const scoreData of scores) {
                await scoreApi.createScoreByParams(
                    scoreData.studentId,
                    scoreData.subjectId,
                    scoreData.score
                );
            }

            setShowBatchModal(false);
            setBatchData('');
            setSuccess(`成功导入 ${scores.length} 条成绩记录！`);
            loadScores();
        } catch (err) {
            console.error('批量导入失败:', err);
            setError('批量导入失败，请检查数据格式');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setError('');
            if (editingScore) {
                await scoreApi.updateScore(editingScore.scoreId, parseFloat(formData.score));
                setSuccess('成绩更新成功！');
            } else {
                await scoreApi.createScoreByParams(
                    parseInt(formData.studentId),
                    parseInt(formData.subjectId),
                    parseFloat(formData.score)
                );
                setSuccess('成绩添加成功！');
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
        setConfirmDelete(id);
    };

    const confirmDeleteScore = async () => {
        if (!confirmDelete) return;
        
        try {
            setDeleting(true);
            setError('');
            await scoreApi.deleteScore(confirmDelete);
            setConfirmDelete(null);
            setSuccess('成绩删除成功！');
            loadScores();
        } catch (err) {
            console.error('删除成绩失败:', err);
            setError('删除成绩失败，请稍后重试');
        } finally {
            setDeleting(false);
        }
    };

    const handleSort = (field) => {
        if (sortBy === field) {
            setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(field);
            setSortDir('asc');
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
                <div>
                    <h2 className="mb-1">成绩管理</h2>
                    <p className="text-muted mb-0">管理学生成绩信息，支持录入、查询、统计等功能</p>
                </div>
                <div className="d-flex gap-2">
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
                    {isAdmin() && selectedScores.length > 0 && (
                        <Button
                            variant="danger"
                            onClick={handleBatchDelete}
                        >
                            <i className="fas fa-trash me-2"></i>
                            批量删除 ({selectedScores.length})
                        </Button>
                    )}
                    {isAdmin() && (
                        <Button
                            variant="info"
                            onClick={() => setShowBatchModal(true)}
                        >
                            <i className="fas fa-upload me-2"></i>
                            批量导入
                        </Button>
                    )}
                    <Button
                        variant="outline-secondary"
                        onClick={() => {
                            loadScores();
                            loadStatistics();
                        }}
                    >
                        <i className="fas fa-sync-alt me-2"></i>
                        刷新
                    </Button>
                </div>
            </div>

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

            {/* 统计信息卡片 */}
            <Row className="mb-4">
                <Col md={2}>
                    <Card className="text-center border-primary">
                        <Card.Body>
                            <i className="fas fa-chart-bar fa-2x text-primary mb-2"></i>
                            <h6 className="card-title">总成绩数</h6>
                            <h4 className="text-primary">{statistics.totalScores}</h4>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={2}>
                    <Card className="text-center border-success">
                        <Card.Body>
                            <i className="fas fa-chart-line fa-2x text-success mb-2"></i>
                            <h6 className="card-title">平均分</h6>
                            <h4 className="text-success">{statistics.averageScore.toFixed(1)}</h4>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={2}>
                    <Card className="text-center border-info">
                        <Card.Body>
                            <i className="fas fa-arrow-up fa-2x text-info mb-2"></i>
                            <h6 className="card-title">最高分</h6>
                            <h4 className="text-info">{statistics.maxScore}</h4>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={2}>
                    <Card className="text-center border-warning">
                        <Card.Body>
                            <i className="fas fa-arrow-down fa-2x text-warning mb-2"></i>
                            <h6 className="card-title">最低分</h6>
                            <h4 className="text-warning">{statistics.minScore}</h4>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={2}>
                    <Card className="text-center border-success">
                        <Card.Body>
                            <i className="fas fa-check-circle fa-2x text-success mb-2"></i>
                            <h6 className="card-title">及格数</h6>
                            <h4 className="text-success">{statistics.passingCount}</h4>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={2}>
                    <Card className="text-center border-danger">
                        <Card.Body>
                            <i className="fas fa-times-circle fa-2x text-danger mb-2"></i>
                            <h6 className="card-title">不及格数</h6>
                            <h4 className="text-danger">{statistics.failingCount}</h4>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 筛选和搜索 */}
            <Card className="mb-4">
                <Card.Header>
                    <div className="d-flex justify-content-between align-items-center">
                        <h6 className="mb-0">
                            <i className="fas fa-filter me-2"></i>
                            筛选和搜索
                        </h6>
                        <div className="d-flex gap-2">
                            <Form.Select
                                size="sm"
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                                style={{ width: 'auto' }}
                            >
                                <option value="scoreId">按ID排序</option>
                                <option value="score">按分数排序</option>
                                <option value="stuName">按学生姓名排序</option>
                                <option value="subjectName">按课程名称排序</option>
                            </Form.Select>
                            <Button
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => setSortDir(sortDir === 'asc' ? 'desc' : 'asc')}
                            >
                                <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'}`}></i>
                            </Button>
                        </div>
                    </div>
                </Card.Header>
                <Card.Body>
                    <Row>
                        <Col md={3}>
                            <Form.Group>
                                <Form.Label>学生</Form.Label>
                                <Form.Select
                                    name="studentId"
                                    value={filters.studentId}
                                    onChange={handleFilterChange}
                                >
                                    <option value="">全部学生</option>
                                    {students.map((student) => (
                                        <option key={student.stuId} value={student.stuId}>
                                            {student.stuName} (ID: {student.stuId})
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group>
                                <Form.Label>课程</Form.Label>
                                <Form.Select
                                    name="subjectId"
                                    value={filters.subjectId}
                                    onChange={handleFilterChange}
                                >
                                    <option value="">全部课程</option>
                                    {subjects.map((subject) => (
                                        <option key={subject.subjectId} value={subject.subjectId}>
                                            {subject.subjectName} ({subject.credit}学分)
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>最低分</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="minScore"
                                    value={filters.minScore}
                                    onChange={handleFilterChange}
                                    min="0"
                                    max="100"
                                    placeholder="0"
                                />
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>最高分</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="maxScore"
                                    value={filters.maxScore}
                                    onChange={handleFilterChange}
                                    min="0"
                                    max="100"
                                    placeholder="100"
                                />
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>等级</Form.Label>
                                <Form.Select
                                    name="grade"
                                    value={filters.grade}
                                    onChange={handleFilterChange}
                                >
                                    <option value="">全部等级</option>
                                    <option value="优秀">优秀 (90+)</option>
                                    <option value="良好">良好 (80-89)</option>
                                    <option value="中等">中等 (70-79)</option>
                                    <option value="及格">及格 (60-69)</option>
                                    <option value="不及格">不及格 (&lt;60)</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mt-3">
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label>关键词搜索</Form.Label>
                                <InputGroup>
                                    <Form.Control
                                        type="text"
                                        placeholder="搜索学生姓名或课程名称..."
                                        value={searchKeyword}
                                        onChange={(e) => setSearchKeyword(e.target.value)}
                                    />
                                    <Button
                                        variant="outline-secondary"
                                        onClick={() => setSearchKeyword('')}
                                    >
                                        <i className="fas fa-times"></i>
                                    </Button>
                                </InputGroup>
                            </Form.Group>
                        </Col>
                        <Col md={6} className="d-flex align-items-end">
                            <Button
                                variant="outline-secondary"
                                onClick={clearFilters}
                                className="me-2"
                            >
                                <i className="fas fa-times me-2"></i>
                                清除筛选
                            </Button>
                            <Badge bg="info" className="align-self-center">
                                显示 {filteredScores.length} 条记录
                            </Badge>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            <Card>
                <Card.Header>
                    <div className="d-flex justify-content-between align-items-center">
                        <h5 className="mb-0">成绩列表</h5>
                        <div>
                            <Badge bg="secondary" className="me-2">
                                共 {filteredScores.length} 条记录
                            </Badge>
                            {filteredScores.length !== scores.length && (
                                <Badge bg="info">
                                    已筛选
                                </Badge>
                            )}
                        </div>
                    </div>
                </Card.Header>
                <Card.Body>
                    {filteredScores.length === 0 ? (
                        <div className="text-center py-5">
                            <i className="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h5 className="text-muted">暂无成绩数据</h5>
                            <p className="text-muted">
                                {scores.length === 0 ? '还没有录入任何成绩' : '没有符合筛选条件的成绩'}
                            </p>
                        </div>
                    ) : (
                        <Table responsive striped hover>
                            <thead>
                                <tr>
                                    {isAdmin() && (
                                        <th>
                                            <Form.Check
                                                type="checkbox"
                                                checked={selectedScores.length === filteredScores.length && filteredScores.length > 0}
                                                onChange={handleSelectAll}
                                                title="全选"
                                            />
                                        </th>
                                    )}
                                    <th 
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleSort('scoreId')}
                                        className="user-select-none"
                                    >
                                        成绩ID
                                        {sortBy === 'scoreId' && (
                                            <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                        )}
                                    </th>
                                    <th 
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleSort('stuName')}
                                        className="user-select-none"
                                    >
                                        学生姓名
                                        {sortBy === 'stuName' && (
                                            <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                        )}
                                    </th>
                                    <th 
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleSort('subjectName')}
                                        className="user-select-none"
                                    >
                                        课程名称
                                        {sortBy === 'subjectName' && (
                                            <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                        )}
                                    </th>
                                    <th 
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleSort('score')}
                                        className="user-select-none"
                                    >
                                        分数
                                        {sortBy === 'score' && (
                                            <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                        )}
                                    </th>
                                    <th>等级</th>
                                    <th>进度条</th>
                                    <th>状态</th>
                                    {isAdmin() && <th>操作</th>}
                                </tr>
                            </thead>
                            <tbody>
                                {filteredScores.map((score) => (
                                    <tr key={score.scoreId}>
                                        {isAdmin() && (
                                            <td>
                                                <Form.Check
                                                    type="checkbox"
                                                    checked={selectedScores.includes(score.scoreId)}
                                                    onChange={() => handleSelectScore(score.scoreId)}
                                                />
                                            </td>
                                        )}
                                        <td>
                                            <Badge bg="light" text="dark">
                                                #{score.scoreId}
                                            </Badge>
                                        </td>
                                        <td>
                                            <div className="d-flex align-items-center">
                                                <strong className="me-2">{score.stuName || '未知'}</strong>
                                                <Button
                                                    variant="link"
                                                    size="sm"
                                                    className="p-0"
                                                    onClick={() => navigate(`/students/${score.stuId}`)}
                                                    title="查看学生详情"
                                                >
                                                    <i className="fas fa-external-link-alt text-muted"></i>
                                                </Button>
                                            </div>
                                        </td>
                                        <td>
                                            <div className="d-flex align-items-center">
                                                <span className="me-2">{score.subjectName || '未知'}</span>
                                                <Button
                                                    variant="link"
                                                    size="sm"
                                                    className="p-0"
                                                    onClick={() => navigate(`/subjects/${score.subjectId}`)}
                                                    title="查看课程详情"
                                                >
                                                    <i className="fas fa-external-link-alt text-muted"></i>
                                                </Button>
                                            </div>
                                        </td>
                                        <td>
                                            <span className={`badge bg-${getScoreColor(score.score)} fs-6`}>
                                                {score.score}
                                            </span>
                                        </td>
                                        <td>
                                            <span className={`badge bg-${getScoreColor(score.score)}`}>
                                                {getScoreGrade(score.score)}
                                            </span>
                                        </td>
                                        <td>
                                            <ProgressBar
                                                now={score.score}
                                                variant={getScoreColor(score.score)}
                                                style={{ width: '100px', height: '20px' }}
                                                label={`${score.score}分`}
                                            />
                                        </td>
                                        <td>
                                            <Badge bg={score.score >= 60 ? 'success' : 'danger'}>
                                                {score.score >= 60 ? '及格' : '不及格'}
                                            </Badge>
                                        </td>
                                        {isAdmin() && (
                                            <td>
                                                <div className="d-flex gap-1">
                                                    <Button
                                                        variant="outline-primary"
                                                        size="sm"
                                                        onClick={() => handleEdit(score)}
                                                        title="编辑成绩"
                                                    >
                                                        <i className="fas fa-edit"></i>
                                                    </Button>
                                                    <Button
                                                        variant="outline-danger"
                                                        size="sm"
                                                        onClick={() => handleDelete(score.scoreId)}
                                                        title="删除成绩"
                                                    >
                                                        <i className="fas fa-trash"></i>
                                                    </Button>
                                                </div>
                                            </td>
                                        )}
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}

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
                                        {student.stuName} (ID: {student.stuId})
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

            {/* 批量导入模态框 */}
            <Modal show={showBatchModal} onHide={() => setShowBatchModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>批量导入成绩</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="info">
                        <h6>数据格式说明：</h6>
                        <p>每行一条记录，格式为：学生ID,课程ID,分数</p>
                        <p>示例：</p>
                        <pre className="mb-0">
{`1,1,85.5
2,1,92.0
1,2,78.5`}
                        </pre>
                    </Alert>
                    <Form.Group>
                        <Form.Label>批量数据</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={10}
                            value={batchData}
                            onChange={(e) => setBatchData(e.target.value)}
                            placeholder="请输入批量数据，每行一条记录..."
                        />
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowBatchModal(false)}>
                        取消
                    </Button>
                    <Button variant="primary" onClick={handleBatchImport}>
                        导入
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* 删除确认模态框 */}
            <Modal show={!!confirmDelete} onHide={() => setConfirmDelete(null)}>
                <Modal.Header closeButton>
                    <Modal.Title>确认删除</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>确定要删除这条成绩记录吗？此操作不可撤销。</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setConfirmDelete(null)}>
                        取消
                    </Button>
                    <Button 
                        variant="danger" 
                        onClick={confirmDeleteScore}
                        disabled={deleting}
                    >
                        {deleting ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                删除中...
                            </>
                        ) : (
                            '确认删除'
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default Scores;
