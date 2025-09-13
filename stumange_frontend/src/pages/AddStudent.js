import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { createStudent } from '../api/student';
import { getSubClasses } from '../api/class';

const AddStudent = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        stuName: '',
        stuGender: true, // 默认男
        stuTel: '',
        stuAddress: '',
        stuMajor: '',
        grade: '',
        subClassId: ''
    });
    const [subClasses, setSubClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // 获取班级列表
    useEffect(() => {
        const fetchSubClasses = async () => {
            try {
                const response = await getSubClasses();
                if (response?.data?.data) {
                    setSubClasses(response.data.data);
                }
            } catch (err) {
                console.error('获取班级列表失败', err);
                setError('获取班级列表失败');
            }
        };

        fetchSubClasses();
    }, []);

    // 处理表单输入变化
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
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
            // 构建请求数据 - 只包含后端接受的字段
            const studentData = {
                stuName: formData.stuName,
                stuGender: formData.stuGender,
                stuTel: formData.stuTel,
                stuAddress: formData.stuAddress,
                stuMajor: formData.stuMajor,
                grade: formData.grade,
                // 尝试使用与后端实体类字段名匹配的格式
                stuClassId: { subClassId: formData.subClassId }
            };

            const response = await createStudent(studentData);
            if (response?.status === 201 || response?.status === 200) {
                setSuccess('添加学生成功！');
                // 重置表单
                setFormData({
                    stuName: '',
                    stuGender: true,
                    stuTel: '',
                    stuAddress: '',
                    stuMajor: '',
                    grade: '',
                    subClassId: ''
                });
                // 3秒后返回学生列表
                setTimeout(() => {
                    navigate('/students');
                }, 3000);
            } else {
                setError('添加学生失败，请重试');
            }
        } catch (err) {
            console.error('添加学生失败', err);
            setError('添加学生失败：' + (err.response?.data?.message || '未知错误'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container fluid className="mt-4">
            <div className="mb-4">
                <h2>添加学生</h2>
                <Button 
                    variant="secondary" 
                    className="mt-2" 
                    onClick={() => navigate('/students')}
                >
                    返回学生列表
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
                        {/* 姓名 */}
                        <Form.Group controlId="formStuName" className="mb-3">
                            <Form.Label>姓名 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="stuName"
                                value={formData.stuName}
                                onChange={handleChange}
                                placeholder="请输入学生姓名"
                                required
                            />
                        </Form.Group>

                        {/* 性别 */}
                        <Form.Group controlId="formStuGender" className="mb-3">
                            <Form.Label>性别 <span className="text-danger">*</span></Form.Label>
                            <div className="d-flex">
                                <Form.Check
                                    type="radio"
                                    label="男"
                                    name="stuGender"
                                    value="true"
                                    checked={formData.stuGender === true}
                                    onChange={(e) => setFormData(prev => ({ ...prev, stuGender: true }))}
                                    className="me-4"
                                />
                                <Form.Check
                                    type="radio"
                                    label="女"
                                    name="stuGender"
                                    value="false"
                                    checked={formData.stuGender === false}
                                    onChange={(e) => setFormData(prev => ({ ...prev, stuGender: false }))}
                                />
                            </div>
                        </Form.Group>

                        {/* 班级 */}
                        <Form.Group controlId="formSubClassId" className="mb-3">
                            <Form.Label>班级 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                as="select"
                                name="subClassId"
                                value={formData.subClassId}
                                onChange={handleChange}
                                required
                            >
                                <option value="">请选择班级</option>
                                {subClasses.map(subClass => (
                                    <option key={subClass.subClassId} value={subClass.subClassId}>
                                        {subClass.subClassName}
                                    </option>
                                ))}
                            </Form.Control>
                        </Form.Group>

                        {/* 专业 */}
                        <Form.Group controlId="formStuMajor" className="mb-3">
                            <Form.Label>专业 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="stuMajor"
                                value={formData.stuMajor}
                                onChange={handleChange}
                                placeholder="请输入专业名称"
                                required
                            />
                        </Form.Group>

                        {/* 年级 */}
                        <Form.Group controlId="formGrade" className="mb-3">
                            <Form.Label>年级 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="grade"
                                value={formData.grade}
                                onChange={handleChange}
                                placeholder="请输入年级，如：2023"
                                required
                            />
                        </Form.Group>

                        {/* 电话 */}
                        <Form.Group controlId="formStuTel" className="mb-3">
                            <Form.Label>电话</Form.Label>
                            <Form.Control
                                type="tel"
                                name="stuTel"
                                value={formData.stuTel}
                                onChange={handleChange}
                                placeholder="请输入联系电话"
                            />
                        </Form.Group>

                        {/* 地址 */}
                        <Form.Group controlId="formStuAddress" className="mb-3">
                            <Form.Label>地址</Form.Label>
                            <Form.Control
                                type="text"
                                name="stuAddress"
                                value={formData.stuAddress}
                                onChange={handleChange}
                                placeholder="请输入家庭地址"
                            />
                        </Form.Group>

                        <div className="d-flex justify-content-end">
                            <Button 
                                variant="primary" 
                                type="submit" 
                                disabled={loading}
                                className="mr-2"
                            >
                                {loading ? '提交中...' : '添加学生'}
                            </Button>
                            <Button 
                                variant="secondary" 
                                onClick={() => navigate('/students')}
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

export default AddStudent;
