import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getMajorById, updateMajor } from '../api/major';

const EditMajor = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState({
        majorId: '',
        majorName: '',
        grade: '',
        academyId: '',
        counselorId: ''
    });
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const loadMajor = async () => {
            try {
                setInitialLoading(true);
                setError('');
                const response = await getMajorById(id);
                if (response?.status === 200 && response?.data?.data) {
                    const m = response.data.data;
                    setFormData({
                        majorId: m.majorId,
                        majorName: m.majorName || '',
                        grade: m.grade || '',
                        academyId: m.academyId || (m.academy && m.academy.academyId) || '',
                        counselorId: m.counselorId || (m.counselor && m.counselor.teacherId) || ''
                    });
                } else {
                    setError('加载专业信息失败');
                }
            } catch (err) {
                console.error('加载专业信息失败', err);
                setError('加载专业信息失败：' + (err.response?.data?.message || '未知错误'));
            } finally {
                setInitialLoading(false);
            }
        };
        if (id) loadMajor();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            const gradeVal = parseInt(formData.grade.toString() || 0, 10);
            const academyIdVal = formData.academyId !== '' ? parseInt(formData.academyId, 10) : null;
            const counselorIdVal = formData.counselorId !== '' ? parseInt(formData.counselorId, 10) : null;
            const payload = {
                majorName: formData.majorName,
                grade: Number.isNaN(gradeVal) ? 0 : gradeVal,
                academy: academyIdVal ? { academyId: academyIdVal } : null,
                counselor: counselorIdVal ? { teacherId: counselorIdVal } : null
            };
            const resp = await updateMajor(id, payload);
            if (resp?.status === 200) {
                setSuccess('更新专业信息成功！');
                setTimeout(() => navigate('/majors'), 2000);
            } else {
                setError('更新专业信息失败，请重试');
            }
        } catch (err) {
            console.error('更新专业信息失败', err);
            setError('更新专业信息失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) {
        return (
            <Container className="text-center py-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">加载中...</span>
                </Spinner>
                <p className="mt-3">正在加载专业信息...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="mb-4">
                <h2>编辑专业</h2>
                <Button variant="secondary" className="mt-2" onClick={() => navigate('/majors')}>
                    返回专业列表
                </Button>
            </div>

            <Card>
                <Card.Body>
                    {error && <Alert variant="danger" className="mb-4">{error}</Alert>}
                    {success && <Alert variant="success" className="mb-4">{success}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId="formMajorName" className="mb-3">
                            <Form.Label>专业名称 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="majorName"
                                value={formData.majorName}
                                onChange={handleChange}
                                placeholder="请输入专业名称"
                                required
                            />
                        </Form.Group>

                        <Form.Group controlId="formGrade" className="mb-3">
                            <Form.Label>年级 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="number"
                                name="grade"
                                value={formData.grade}
                                onChange={handleChange}
                                placeholder="请输入年级，如：2023"
                                required
                            />
                        </Form.Group>

                        <Form.Group controlId="formAcademyId" className="mb-3">
                            <Form.Label>学院ID</Form.Label>
                            <Form.Control
                                type="number"
                                name="academyId"
                                value={formData.academyId}
                                onChange={handleChange}
                                placeholder="请输入学院ID（可选）"
                            />
                        </Form.Group>

                        <Form.Group controlId="formCounselorId" className="mb-3">
                            <Form.Label>辅导员ID</Form.Label>
                            <Form.Control
                                type="number"
                                name="counselorId"
                                value={formData.counselorId}
                                onChange={handleChange}
                                placeholder="请输入辅导员教师ID（可选）"
                            />
                        </Form.Group>

                        <div className="d-flex justify-content-end">
                            <Button variant="primary" type="submit" disabled={loading} className="mr-2">
                                {loading ? '更新中...' : '更新专业信息'}
                            </Button>
                            <Button variant="secondary" onClick={() => navigate('/majors')}>
                                取消
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default EditMajor;
