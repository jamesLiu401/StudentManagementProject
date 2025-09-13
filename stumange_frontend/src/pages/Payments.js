import React, { useEffect, useState } from 'react';
import { Container, Card, Table, Button, Alert, Spinner, Pagination, Badge } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as paymentApi from '../api/payment';

const Payments = () => {
    const { isAdmin } = useAuth();
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    const loadPayments = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await paymentApi.getPaymentsPage({ page: currentPage, size: pageSize, sortBy: 'paymentId', sortDir: 'desc' });
            if (response && response.status === 200) {
                const pageData = response?.data?.data || {};
                setPayments(Array.isArray(pageData.content) ? pageData.content : []);
                setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
            } else {
                setError('加载缴费数据失败');
            }
        } catch (err) {
            console.error('加载缴费数据失败:', err);
            setError('加载缴费数据失败，请稍后重试');
            setPayments([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadPayments();
    }, [currentPage]);

    const handleDelete = async (id) => {
        if (!isAdmin() || !window.confirm('确定要删除这条缴费记录吗？')) return;
        try {
            await paymentApi.deletePayment(id);
            loadPayments();
        } catch (err) {
            console.error('删除缴费记录失败:', err);
            setError('删除缴费记录失败，请稍后重试');
        }
    };

    const statusColor = (status) => {
        if (status === '已缴费') return 'success';
        if (status === '部分缴费') return 'warning';
        return 'danger';
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
                <h2>缴费管理</h2>
            </div>

            {error && (
                <Alert variant="danger" className="mb-3">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    {error}
                </Alert>
            )}

            <Card>
                <Card.Header>
                    <h5 className="mb-0">缴费列表</h5>
                </Card.Header>
                <Card.Body>
                    <Table responsive striped hover>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>学生ID</th>
                                <th>金额</th>
                                <th>类型</th>
                                <th>状态</th>
                                <th>日期</th>
                                {isAdmin() && <th>操作</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {payments.map(p => (
                                <tr key={p.paymentId}>
                                    <td>{p.paymentId}</td>
                                    <td>{p.stuId || '-'}</td>
                                    <td>{p.amount}</td>
                                    <td>{p.paymentType}</td>
                                    <td>
                                        <Badge bg={statusColor(p.paymentStatus)}>
                                            {p.paymentStatus}
                                        </Badge>
                                    </td>
                                    <td>{p.paymentDate ? new Date(p.paymentDate).toLocaleDateString() : '-'}</td>
                                    {isAdmin() && (
                                        <td>
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDelete(p.paymentId)}
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

export default Payments;
