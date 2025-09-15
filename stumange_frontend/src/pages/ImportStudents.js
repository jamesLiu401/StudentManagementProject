import React, { useState } from 'react';
import { Container, Card, Alert, Button, Form, ProgressBar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { importStudents } from '../api/student';

const ImportStudents = () => {
    const navigate = useNavigate();
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [progress, setProgress] = useState(0);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [result, setResult] = useState(null);

    const handleFileChange = (e) => {
        setFile(e.target.files?.[0] || null);
        setError('');
        setSuccess('');
        setResult(null);
        setProgress(0);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!file) {
            setError('请先选择要导入的文件');
            return;
        }
        setUploading(true);
        setError('');
        setSuccess('');
        setResult(null);
        setProgress(10);
        try {
            const response = await importStudents(file);
            if (response?.status === 200) {
                setSuccess('导入成功');
                setResult(response.data?.data || null);
                setProgress(100);
            } else {
                setError('导入失败，请检查文件格式后重试');
                setProgress(0);
            }
        } catch (err) {
            console.error('导入学生失败', err);
            setError('导入失败：' + (err.response?.data?.message || '未知错误'));
            setProgress(0);
        } finally {
            setUploading(false);
        }
    };

    return (
        <Container fluid className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>批量导入学生</h2>
                <div>
                    <Button variant="secondary" onClick={() => navigate('/students')}>
                        <i className="fas fa-arrow-left me-2"></i>
                        返回学生列表
                    </Button>
                </div>
            </div>

            <Card>
                <Card.Body>
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

                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId="formFile" className="mb-3">
                            <Form.Label>选择文件（支持 Excel/CSV）</Form.Label>
                            <Form.Control
                                type="file"
                                accept=".xls,.xlsx,.csv"
                                onChange={handleFileChange}
                                disabled={uploading}
                            />
                            <Form.Text className="text-muted">
                                请按照模板准备数据，包含姓名、性别、班级、年级、专业等字段。
                            </Form.Text>
                        </Form.Group>

                        {uploading && (
                            <div className="mb-3">
                                <ProgressBar now={progress} animated label={`${progress}%`} />
                            </div>
                        )}

                        <div className="d-flex justify-content-end">
                            <Button type="submit" variant="primary" disabled={uploading}>
                                {uploading ? '正在导入...' : '开始导入'}
                            </Button>
                        </div>
                    </Form>

                    {result && (
                        <Card className="mt-4">
                            <Card.Header>
                                <strong>导入结果</strong>
                            </Card.Header>
                            <Card.Body>
                                <pre className="mb-0" style={{ whiteSpace: 'pre-wrap' }}>
{JSON.stringify(result, null, 2)}
                                </pre>
                            </Card.Body>
                        </Card>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ImportStudents;
