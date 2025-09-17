import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Badge, Row, Col, Form, InputGroup, Tabs, Tab, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import * as paymentApi from '../api/payment';
import * as studentApi from '../api/student';

const Payments = () => {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const pageSize = 10;

    // 筛选和搜索状态
    const [filters, setFilters] = useState({
        paymentType: '',
        paymentStatus: '',
        studentId: '',
        keyword: ''
    });
    const [sortBy, setSortBy] = useState('paymentId');
    const [sortDir, setSortDir] = useState('desc');
    const [activeTab, setActiveTab] = useState('list');

    // 统计信息
    const [statistics, setStatistics] = useState({
        totalPayments: 0,
        totalAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0
    });

    // 删除确认
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deletingPayment, setDeletingPayment] = useState(null);
    const [deleting, setDeleting] = useState(false);

    // 缴费类型和状态选项
    const paymentTypes = ['', '学费', '住宿费', '教材费', '实验费', '考试费', '其他'];
    const paymentStatuses = ['', '未缴费', '已缴费', '部分缴费', '已退费'];

    const loadPayments = async () => {
        try {
            setLoading(true);
            setError('');
            
            const params = {
                page: currentPage,
                size: pageSize,
                sortBy: sortBy,
                sortDir: sortDir
            };

            // 添加筛选参数
            if (filters.paymentType) params.paymentType = filters.paymentType;
            if (filters.paymentStatus) params.paymentStatus = filters.paymentStatus;
            if (filters.studentId) params.studentId = filters.studentId;

            const response = await paymentApi.getPaymentsPage(params);
            
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setPayments(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
                setTotalElements(typeof pageData.totalElements === 'number' ? pageData.totalElements : 0);
            } else {
                setError('加载缴费数据失败');
            }
        } catch (err) {
            console.error('加载缴费数据失败:', err);
            setError('加载缴费数据失败，请稍后重试');
            setPayments([]);
            setTotalPages(0);
            setTotalElements(0);
        } finally {
            setLoading(false);
        }
    };

    // 加载统计信息
    const loadStatistics = async () => {
        try {
            const [allPaymentsRes, paidRes, unpaidRes] = await Promise.all([
                paymentApi.getPayments(),
                paymentApi.getPaymentsByStatus('已缴费'),
                paymentApi.getPaymentsByStatus('未缴费')
            ]);

            if (allPaymentsRes?.status === 200) {
                const allPayments = allPaymentsRes.data.data || [];
                const paidPayments = paidRes?.status === 200 ? paidRes.data.data || [] : [];
                const unpaidPayments = unpaidRes?.status === 200 ? unpaidRes.data.data || [] : [];

                const totalAmount = allPayments.reduce((sum, p) => sum + (parseFloat(p.amount) || 0), 0);
                const paidAmount = paidPayments.reduce((sum, p) => sum + (parseFloat(p.amount) || 0), 0);
                const unpaidAmount = unpaidPayments.reduce((sum, p) => sum + (parseFloat(p.amount) || 0), 0);

                setStatistics({
                    totalPayments: allPayments.length,
                    totalAmount: totalAmount,
                    paidAmount: paidAmount,
                    unpaidAmount: unpaidAmount
                });
            }
        } catch (err) {
            console.error('加载统计信息失败:', err);
        }
    };

    useEffect(() => {
        loadPayments();
    }, [currentPage, sortBy, sortDir, filters]);

    useEffect(() => {
        if (activeTab === 'statistics') {
            loadStatistics();
        }
    }, [activeTab]);

    const handleFilterChange = (name, value) => {
        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
        setCurrentPage(0); // 重置到第一页
    };

    const handleSort = (field) => {
        if (sortBy === field) {
            setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(field);
            setSortDir('asc');
        }
    };

    const handleDeleteClick = (payment) => {
        setDeletingPayment(payment);
        setShowDeleteModal(true);
    };

    const handleDeleteConfirm = async () => {
        if (!isAdmin() || !deletingPayment) return;
        
        try {
            setDeleting(true);
            await paymentApi.deletePayment(deletingPayment.paymentId);
            setShowDeleteModal(false);
            setDeletingPayment(null);
            loadPayments();
            if (activeTab === 'statistics') {
                loadStatistics();
            }
        } catch (err) {
            console.error('删除缴费记录失败:', err);
            setError('删除缴费记录失败，请稍后重试');
        } finally {
            setDeleting(false);
        }
    };

    const statusColor = (status) => {
        switch (status) {
            case '已缴费': return 'success';
            case '部分缴费': return 'warning';
            case '已退费': return 'info';
            default: return 'danger';
        }
    };

    const formatAmount = (amount) => {
        return `¥${parseFloat(amount || 0).toFixed(2)}`;
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('zh-CN');
    };

    if (loading && payments.length === 0) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载缴费数据...</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>
                    <i className="fas fa-credit-card me-2"></i>
                    缴费管理
                </h2>
                {isAdmin() && (
                    <Button 
                        variant="primary" 
                        onClick={() => navigate('/payments/add')}
                    >
                        <i className="fas fa-plus me-2"></i>
                        添加缴费记录
                    </Button>
                )}
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Tabs activeKey={activeTab} onSelect={setActiveTab} className="mb-3">
                <Tab eventKey="list" title={
                    <span>
                        <i className="fas fa-list me-2"></i>
                        缴费列表
                    </span>
                }>
                    <Card>
                        <Card.Header>
                            <Row className="align-items-center">
                                <Col>
                                    <h5 className="mb-0">缴费记录</h5>
                                    <small className="text-muted">
                                        共 {totalElements} 条记录，第 {currentPage + 1} 页，共 {totalPages} 页
                                    </small>
                                </Col>
                                <Col md="auto">
                                    <Button
                                        variant="outline-secondary"
                                        size="sm"
                                        onClick={loadPayments}
                                        disabled={loading}
                                    >
                                        <i className="fas fa-sync-alt me-1"></i>
                                        刷新
                                    </Button>
                                </Col>
                            </Row>
                        </Card.Header>
                        <Card.Body>
                            {/* 筛选器 */}
                            <Row className="mb-3">
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label>缴费类型</Form.Label>
                                        <Form.Select
                                            value={filters.paymentType}
                                            onChange={(e) => handleFilterChange('paymentType', e.target.value)}
                                        >
                                            {paymentTypes.map(type => (
                                                <option key={type} value={type}>
                                                    {type || '全部类型'}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label>缴费状态</Form.Label>
                                        <Form.Select
                                            value={filters.paymentStatus}
                                            onChange={(e) => handleFilterChange('paymentStatus', e.target.value)}
                                        >
                                            {paymentStatuses.map(status => (
                                                <option key={status} value={status}>
                                                    {status || '全部状态'}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label>学生ID</Form.Label>
                                        <Form.Control
                                            type="number"
                                            placeholder="输入学生ID"
                                            value={filters.studentId}
                                            onChange={(e) => handleFilterChange('studentId', e.target.value)}
                                        />
                                    </Form.Group>
                                </Col>
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label>关键词搜索</Form.Label>
                                        <InputGroup>
                                            <Form.Control
                                                type="text"
                                                placeholder="搜索备注或类型"
                                                value={filters.keyword}
                                                onChange={(e) => handleFilterChange('keyword', e.target.value)}
                                            />
                                            <Button 
                                                variant="outline-secondary"
                                                onClick={() => handleFilterChange('keyword', '')}
                                            >
                                                <i className="fas fa-times"></i>
                                            </Button>
                                        </InputGroup>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Table responsive striped hover>
                                <thead>
                                    <tr>
                                        <th 
                                            style={{ cursor: 'pointer' }}
                                            onClick={() => handleSort('paymentId')}
                                        >
                                            ID
                                            {sortBy === 'paymentId' && (
                                                <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                            )}
                                        </th>
                                        <th>学生ID</th>
                                        <th 
                                            style={{ cursor: 'pointer' }}
                                            onClick={() => handleSort('amount')}
                                        >
                                            金额
                                            {sortBy === 'amount' && (
                                                <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                            )}
                                        </th>
                                        <th>类型</th>
                                        <th>状态</th>
                                        <th 
                                            style={{ cursor: 'pointer' }}
                                            onClick={() => handleSort('paymentDate')}
                                        >
                                            日期
                                            {sortBy === 'paymentDate' && (
                                                <i className={`fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'} ms-1`}></i>
                                            )}
                                        </th>
                                        <th>备注</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {payments.map(p => (
                                        <tr key={p.paymentId}>
                                            <td>{p.paymentId}</td>
                                            <td>
                                                <Button
                                                    variant="link"
                                                    className="p-0 text-decoration-none"
                                                    onClick={() => navigate(`/students/${p.stuId}`)}
                                                >
                                                    {p.stuId || '-'}
                                                </Button>
                                            </td>
                                            <td className="fw-bold text-primary">
                                                {formatAmount(p.amount)}
                                            </td>
                                            <td>
                                                <Badge bg="secondary">
                                                    {p.paymentType}
                                                </Badge>
                                            </td>
                                            <td>
                                                <Badge bg={statusColor(p.paymentStatus)}>
                                                    {p.paymentStatus}
                                                </Badge>
                                            </td>
                                            <td>{formatDate(p.paymentDate)}</td>
                                            <td>
                                                <span 
                                                    className="text-truncate d-inline-block" 
                                                    style={{ maxWidth: '150px' }}
                                                    title={p.description}
                                                >
                                                    {p.description || '-'}
                                                </span>
                                            </td>
                                            <td>
                                                <div className="d-flex gap-1">
                                                    <Button
                                                        variant="outline-info"
                                                        size="sm"
                                                        onClick={() => navigate(`/payments/${p.paymentId}`)}
                                                        title="查看详情"
                                                    >
                                                        <i className="fas fa-eye"></i>
                                                    </Button>
                                                    {isAdmin() && (
                                                        <>
                                                            <Button
                                                                variant="outline-primary"
                                                                size="sm"
                                                                onClick={() => navigate(`/payments/edit/${p.paymentId}`)}
                                                                title="编辑"
                                                            >
                                                                <i className="fas fa-edit"></i>
                                                            </Button>
                                                            <Button
                                                                variant="outline-danger"
                                                                size="sm"
                                                                onClick={() => handleDeleteClick(p)}
                                                                title="删除"
                                                            >
                                                                <i className="fas fa-trash"></i>
                                                            </Button>
                                                        </>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>

                            {payments.length === 0 && !loading && (
                                <div className="text-center py-4">
                                    <i className="fas fa-inbox fa-3x text-muted mb-3"></i>
                                    <p className="text-muted">暂无缴费记录</p>
                                </div>
                            )}

                            {totalPages > 1 && (
                                <div className="d-flex justify-content-center mt-3">
                                    <Pagination>
                                        <Pagination.Prev 
                                            disabled={currentPage === 0} 
                                            onClick={() => setCurrentPage(currentPage - 1)} 
                                        />
                                        {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => {
                                            const pageNum = currentPage < 3 ? i : currentPage - 2 + i;
                                            if (pageNum >= totalPages) return null;
                                            return (
                                                <Pagination.Item 
                                                    key={pageNum} 
                                                    active={pageNum === currentPage} 
                                                    onClick={() => setCurrentPage(pageNum)}
                                                >
                                                    {pageNum + 1}
                                                </Pagination.Item>
                                            );
                                        })}
                                        <Pagination.Next 
                                            disabled={currentPage === totalPages - 1} 
                                            onClick={() => setCurrentPage(currentPage + 1)} 
                                        />
                                    </Pagination>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Tab>

                <Tab eventKey="statistics" title={
                    <span>
                        <i className="fas fa-chart-bar me-2"></i>
                        统计信息
                    </span>
                }>
                    <Row>
                        <Col md={3}>
                            <Card className="text-center">
                                <Card.Body>
                                    <i className="fas fa-receipt fa-2x text-primary mb-2"></i>
                                    <h4>{statistics.totalPayments}</h4>
                                    <p className="text-muted mb-0">总缴费记录</p>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={3}>
                            <Card className="text-center">
                                <Card.Body>
                                    <i className="fas fa-dollar-sign fa-2x text-success mb-2"></i>
                                    <h4>{formatAmount(statistics.totalAmount)}</h4>
                                    <p className="text-muted mb-0">总缴费金额</p>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={3}>
                            <Card className="text-center">
                                <Card.Body>
                                    <i className="fas fa-check-circle fa-2x text-success mb-2"></i>
                                    <h4>{formatAmount(statistics.paidAmount)}</h4>
                                    <p className="text-muted mb-0">已缴费金额</p>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={3}>
                            <Card className="text-center">
                                <Card.Body>
                                    <i className="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                                    <h4>{formatAmount(statistics.unpaidAmount)}</h4>
                                    <p className="text-muted mb-0">未缴费金额</p>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Tab>
            </Tabs>

            {/* 删除确认对话框 */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <i className="fas fa-exclamation-triangle me-2"></i>
                        确认删除
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>确定要删除以下缴费记录吗？</p>
                    {deletingPayment && (
                        <div className="bg-light p-3 rounded">
                            <div><strong>缴费ID：</strong> {deletingPayment.paymentId}</div>
                            <div><strong>学生ID：</strong> {deletingPayment.stuId}</div>
                            <div><strong>缴费类型：</strong> {deletingPayment.paymentType}</div>
                            <div><strong>缴费金额：</strong> {formatAmount(deletingPayment.amount)}</div>
                            <div><strong>缴费状态：</strong> {deletingPayment.paymentStatus}</div>
                        </div>
                    )}
                    <p className="mt-3 text-danger">
                        <i className="fas fa-warning me-2"></i>
                        此操作不可撤销！
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="outline-secondary" onClick={() => setShowDeleteModal(false)}>
                        取消
                    </Button>
                    <Button variant="danger" onClick={handleDeleteConfirm} disabled={deleting}>
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

export default Payments;
