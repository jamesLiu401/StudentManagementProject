import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as subjectApi from '../api/subject';

const Subjects = () => {
    const { isAdmin } = useAuth();
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    const loadSubjects = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await subjectApi.getSubjectsPage({ page: currentPage, size: pageSize, sortBy: 'subjectId', sortDir: 'asc' });
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setSubjects(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载课程数据失败');
            }
        } catch (err) {
            console.error('加载课程数据失败:', err);
            setError('加载课程数据失败，请稍后重试');
            setSubjects([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadSubjects();
    }, [currentPage]);

    const handleDelete = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这门课程吗？')) return;
        try {
            await subjectApi.deleteSubject(id);
            loadSubjects();
        } catch (err) {
            console.error('删除课程失败:', err);
            setError('删除课程失败，请稍后重试');
        }
    };

    if (loading && subjects.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载课程数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>课程管理</h2>
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Card>
                <Card.Header>
                    <h5 className="mb-0">课程列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>课程名</th>
                                <th>学院</th>
                                <th>学分</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {subjects.map(s => (
                                <tr key={s.subjectId}>
                                    <td>{s.subjectId}</td>
                                    <td>{s.subjectName}</td>
                                    <td>{s.academy}</td>
                                    <td>{s.credit}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDelete(s.subjectId)}
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

export default Subjects;
