import React, { useState, useEffect } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Form, Row, Col, InputGroup } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as academyApi from '../api/academy';

const Academies = () => {
    const { isAdmin } = useAuth();
    const navigate = useNavigate();
    const [academies, setAcademies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchName, setSearchName] = useState('');
    const [searchDean, setSearchDean] = useState('');
    const pageSize = 10;

    useEffect(() => {
        loadAcademies();
    }, [currentPage, searchName, searchDean]);

    const loadAcademies = async () => {
        try {
            setLoading(true);
            setError('');
            let response;
            
            if (searchName.trim() && searchDean.trim()) {
                // 按学院名称和院长姓名搜索
                response = await academyApi.searchAcademiesByNamePage(searchName.trim(), { 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'academyId', 
                    sortDir: 'asc' 
                });
                // 过滤院长姓名
                if (response && response.status === 200) {
                    const pageData = response?.data?.data || {};
                    const filteredContent = pageData.content?.filter(academy => 
                        academy.deanName && academy.deanName.includes(searchDean.trim())
                    ) || [];
                    pageData.content = filteredContent;
                    pageData.totalElements = filteredContent.length;
                    pageData.totalPages = Math.ceil(filteredContent.length / pageSize);
                }
            } else if (searchName.trim()) {
                // 按学院名称搜索
                response = await academyApi.searchAcademiesByNamePage(searchName.trim(), { 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'academyId', 
                    sortDir: 'asc' 
                });
            } else if (searchDean.trim()) {
                // 按院长姓名搜索
                response = await academyApi.searchAcademiesByDeanPage(searchDean.trim(), { 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'academyId', 
                    sortDir: 'asc' 
                });
            } else {
                // 普通分页查询
                response = await academyApi.getAcademiesPage({ 
                    page: currentPage, 
                    size: pageSize, 
                    sortBy: 'academyId', 
                    sortDir: 'asc' 
                });
            }
            
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setAcademies(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载学院数据失败');
            }
        } catch (err) {
            console.error('加载学院数据失败:', err);
            setError('加载学院数据失败，请稍后重试');
            setAcademies([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这个学院吗？')) return;
        try {
            await academyApi.deleteAcademy(id);
            loadAcademies();
        } catch (err) {
            console.error('删除学院失败:', err);
            setError('删除学院失败，请稍后重试');
        }
    };

    const handleSearch = () => {
        setCurrentPage(0);
        loadAcademies();
    };

    const handleClearSearch = () => {
        setSearchName('');
        setSearchDean('');
        setCurrentPage(0);
    };

    const handleViewDetail = (academyId) => {
        navigate(`/academies/${academyId}`);
    };

    const handleEdit = (academyId) => {
        navigate(`/academies/edit/${academyId}`);
    };

    if (loading && academies.length === 0) {
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
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>学院管理</h2>
                {isAdmin() && (
                    <Button 
                        variant="primary" 
                        onClick={() => navigate('/academies/add')}
                    >
                        <i className="fas fa-plus me-2"></i>
                        添加学院
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
                    <h5 className="mb-0">搜索学院</h5>
                </Card.Header>
                <Card.Body>
                    <Row>
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label>学院名称</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="请输入学院名称"
                                    value={searchName}
                                    onChange={(e) => setSearchName(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label>院长姓名</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="请输入院长姓名"
                                    value={searchDean}
                                    onChange={(e) => setSearchDean(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
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
                    <h5 className="mb-0">学院列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>学院ID</th>
                                <th>学院名称</th>
                                <th>学院代码</th>
                                <th>院长</th>
                                <th>联系电话</th>
                                <th>地址</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {academies.map((academy) => (
                                <tr key={academy.academyId}>
                                    <td>{academy.academyId}</td>
                                    <td>{academy.academyName}</td>
                                    <td>{academy.academyCode}</td>
                                    <td>{academy.deanName}</td>
                                    <td>{academy.contactPhone}</td>
                                    <td>{academy.address}</td>
                                    <td>
                                        <Button
                                            variant="outline-info"
                                            size="sm"
                                            onClick={() => handleViewDetail(academy.academyId)}
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
                                                    onClick={() => handleEdit(academy.academyId)}
                                                    className="me-2"
                                                    title="编辑"
                                                >
                                                    <i className="fas fa-edit"></i>
                                                </Button>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(academy.academyId)}
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
        </Container>
    );
};

export default Academies;
