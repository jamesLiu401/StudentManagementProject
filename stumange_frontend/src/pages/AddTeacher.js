import React, { useState } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { createTeacher } from '../api/teacher';

const AddTeacher = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        teacherName: '',
        teacherNo: '',
        department: '',
        title: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // 处理表单输入变化
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // 清除错误和成功提示
        setError('');
        setSuccess('');
    };

    // 处理表单提交
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await createTeacher(formData);
            if (response?.status === 201 || response?.status === 200) {
                setSuccess('添加教师成功！');
                // 重置表单
                setFormData({
                    teacherName: '',
                    teacherNo: '',
                    department: '',
                    title: ''
                });
                // 3秒后返回教师列表
                setTimeout(() => {
                    navigate('/teachers');
                }, 3000);
            } else {
                setError('添加教师失败，请重试');
            }
        } catch (err) {
            console.error('添加教师失败', err);
            setError('添加教师失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container fluid className="mt-4">
            <div className="mb-4">
                <h2>添加教师</h2>
                <Button 
                    variant="secondary" 
                    className="mt-2" 
                    onClick={() => navigate('/teachers')}
                >
                    返回教师列表
                </Button>
            </div>
            
            <Card>
                <Card.Body>
                    {error && (
                        <Alert variant="danger" className="mb-4">
                            {error}
                        </Alert>
                    )}
                    {success && (
                        <Alert variant="success" className="mb-4">
                            {success}
                        </Alert>
                    )}

                    <Form onSubmit={handleSubmit}>
                        {/* 教师姓名 */}
                        <Form.Group controlId="formTeacherName" className="mb-3">
                            <Form.Label>教师姓名 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="teacherName"
                                value={formData.teacherName}
                                onChange={handleChange}
                                placeholder="请输入教师姓名"
                                required
                            />
                        </Form.Group>

                        {/* 工号 */}
                        <Form.Group controlId="formTeacherNo" className="mb-3">
                            <Form.Label>工号 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="teacherNo"
                                value={formData.teacherNo}
                                onChange={handleChange}
                                placeholder="请输入工号"
                                required
                            />
                        </Form.Group>

                        {/* 部门 */}
                        <Form.Group controlId="formDepartment" className="mb-3">
                            <Form.Label>部门</Form.Label>
                            <Form.Control
                                type="text"
                                name="department"
                                value={formData.department}
                                onChange={handleChange}
                                placeholder="请输入部门名称"
                            />
                        </Form.Group>

                        {/* 职称 */}
                        <Form.Group controlId="formTitle" className="mb-3">
                            <Form.Label>职称</Form.Label>
                            <Form.Control
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                                placeholder="请输入职称，如：教授、副教授、讲师等"
                            />
                        </Form.Group>

                        <div className="d-flex justify-content-end">
                            <Button 
                                variant="primary" 
                                type="submit" 
                                disabled={loading}
                                className="mr-2"
                            >
                                {loading ? '提交中...' : '添加教师'}
                            </Button>
                            <Button 
                                variant="secondary" 
                                onClick={() => navigate('/teachers')}
                            >
                                取消
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default AddTeacher;
