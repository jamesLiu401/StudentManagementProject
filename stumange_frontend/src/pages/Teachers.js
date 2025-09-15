import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Form, Row, Col, InputGroup } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as teacherApi from '../api/teacher';

const Teachers = () => {
    const { isAdmin } = useAuth();
    const navigate = useNavigate();
    const [teachers, setTeachers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchName, setSearchName] = useState('');
    const [searchDepartment, setSearchDepartment] = useState('');
    const [searchTitle, setSearchTitle] = useState('');
    const pageSize = 10;

    const loadTeachers = async () => {
        try {
            setLoading(true);
            setError('');
            let response;
            
            if (searchName.trim()) {
                // 按姓名搜索
                response = await teacherApi.searchTeachersByNamePage(searchName.trim(), { 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'teacherId', 
                    sortDir: 'asc' 
                });
            } else {
                // 普通分页查询
                response = await teacherApi.getTeachersPage({ 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'teacherId', 
                    sortDir: 'asc' 
                });
            }
            
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setTeachers(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载教师数据失败');
            }
        } catch (err) {
            console.error('加载教师数据失败:', err);
            setError('加载教师数据失败，请稍后重试');
            setTeachers([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadTeachers();
    }, [currentPage, searchName]);

    const handleDelete = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这个教师吗？')) return;
        try {
            await teacherApi.deleteTeacher(id);
            loadTeachers();
        } catch (err) {
            console.error('删除教师失败:', err);
            setError('删除教师失败，请稍后重试');
        }
    };

    const handleSearch = () => {
        setCurrentPage(0);
        loadTeachers();
    };

    const handleClearSearch = () => {
        setSearchName('');
        setSearchDepartment('');
        setSearchTitle('');
        setCurrentPage(0);
    };

    const handleViewDetail = (teacherId) => {
        navigate(`/teachers/${teacherId}`);
    };

    const handleEdit = (teacherId) => {
        navigate(`/teachers/edit/${teacherId}`);
    };

    if (loading && teachers.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载教师数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>教师管理</h2>
                {isAdmin() && (
                    <Button 
                        variant="primary" 
                        onClick={() => navigate('/teachers/add')}
                    >
                        <i className="fas fa-plus me-2"></i>
                        添加教师
                    </Button>
                )}
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            {/* 搜索区域 */}
            <Card className="mb-4">
                <Card.Header>
                    <h5 className="mb-0">搜索教师</h5>
                </Card.Header>
                <Card.Body>
                    <Row>
                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>教师姓名</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="请输入教师姓名"
                                    value={searchName}
                                    onChange={(e) => setSearchName(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                />
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>部门</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="请输入部门名称"
                                    value={searchDepartment}
                                    onChange={(e) => setSearchDepartment(e.target.value)}
                                />
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>职称</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="请输入职称"
                                    value={searchTitle}
                                    onChange={(e) => setSearchTitle(e.target.value)}
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    <div className="mt-3">
                        <Button variant="primary" onClick={handleSearch} className="me-2">
                            <i className="fas fa-search me-2"></i>
                            搜索
                        </Button>
                        <Button variant="secondary" onClick={handleClearSearch}>
                            <i className="fas fa-times me-2"></i>
                            清空
                        </Button>
                    </div>
                </Card.Body>
            </Card>

            <Card>
                <Card.Header>
                    <h5 className="mb-0">教师列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>姓名</th>
                                <th>工号</th>
                                <th>职称</th>
                                <th>部门</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {teachers.map(t => (
                                <tr key={t.teacherId}>
                                    <td>{t.teacherId}</td>
                                    <td>{t.teacherName}</td>
                                    <td>{t.teacherNo}</td>
                                    <td>{t.title}</td>
                                    <td>{t.department}</td>
                                    <td>
                                        <Button
                                            variant="outline-info"
                                            size="sm"
                                            onClick={() => handleViewDetail(t.teacherId)}
                                            className="me-2"
                                            title="查看详情"
                                        >
                                            <i className="fas fa-eye"></i>
                                        </Button>
                                        {isAdmin() && (
                                            <>
                                                <Button
                                                    variant="outline-warning"
                                                    size="sm"
                                                    onClick={() => handleEdit(t.teacherId)}
                                                    className="me-2"
                                                    title="编辑"
                                                >
                                                    <i className="fas fa-edit"></i>
                                                </Button>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(t.teacherId)}
                                                    title="删除"
                                                >
                                                    <i className="fas fa-trash"></i>
                                                </Button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {totalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Pagination>
                                <Pagination.Prev disabled={currentPage === 0} onClick={() => setCurrentPage(currentPage - 1)} />
                                {Array.from({ length: totalPages }, (_, i) => (
                                    <Pagination.Item key={i} active={i === currentPage} onClick={() => setCurrentPage(i)}>
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next disabled={currentPage === totalPages - 1} onClick={() => setCurrentPage(currentPage + 1)} />
                            </Pagination>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Teachers;
