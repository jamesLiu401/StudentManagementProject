import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getStudentById, updateStudent } from '../api/student';
import { getSubClasses } from '../api/class';

const EditStudent = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [formData, setFormData] = useState({
        stuId: '',
        stuName: '',
        stuGender: true,
        stuTel: '',
        stuAddress: '',
        stuMajor: '',
        grade: '',
        subClassId: ''
    });
    const [subClasses, setSubClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const loadStudent = async () => {
            try {
                setInitialLoading(true);
                setError('');
                const response = await getStudentById(id);
                if (response?.status === 200 && response?.data?.data) {
                    const s = response.data.data || {};
                    const subClassId = s.subClassId || (s.subClass && s.subClass.subClassId) || (s.stuClass && s.stuClass.subClassId) || '';
                    setFormData({
                        stuId: s.stuId,
                        stuName: s.stuName || '',
                        stuGender: !!s.stuGender,
                        stuTel: s.stuTel || '',
                        stuAddress: s.stuAddress || '',
                        stuMajor: s.stuMajor || '',
                        grade: s.grade || '',
                        subClassId: subClassId
                    });
                } else {
                    setError('加载学生信息失败');
                }
            } catch (err) {
                console.error('加载学生信息失败', err);
                setError('加载学生信息失败：' + (err.response?.data?.message || '未知错误'));
            } finally {
                setInitialLoading(false);
            }
        };

        const loadSubClasses = async () => {
            try {
                const resp = await getSubClasses();
                if (resp?.data?.data) {
                    setSubClasses(resp.data.data);
                }
            } catch (e) {
                console.error('获取班级列表失败', e);
            }
        };

        if (id) {
            loadStudent();
            loadSubClasses();
        }
    }, [id]);

    const handleChange = (e) => {
        const { name, value, type } = e.target;
        if (name === 'stuGender') {
            setFormData(prev => ({ ...prev, stuGender: value === 'true' }));
        } else {
            setFormData(prev => ({ ...prev, [name]: type === 'number' ? Number(value) : value }));
        }
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            const payload = {
                stuName: formData.stuName,
                stuGender: formData.stuGender,
                stuTel: formData.stuTel,
                stuAddress: formData.stuAddress,
                stuMajor: formData.stuMajor,
                grade: formData.grade,
                stuClassId: { subClassId: formData.subClassId }
            };
            const response = await updateStudent(id, payload);
            if (response?.status === 200) {
                setSuccess('更新学生信息成功！');
                setTimeout(() => navigate('/students'), 3000);
            } else {
                setError('更新学生信息失败，请重试');
            }
        } catch (err) {
            console.error('更新学生信息失败', err);
            setError('更新学生信息失败：' + (err.response?.data?.message || '未知错误'));
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
                <p className="mt-3">正在加载学生信息...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="mt-4">
            <div className="mb-4">
                <h2>编辑学生</h2>
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
                        <Alert variant="danger" className="mb-4">{error}</Alert>
                    )}
                    {success && (
                        <Alert variant="success" className="mb-4">{success}</Alert>
                    )}

                    <Form onSubmit={handleSubmit}>
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

                        <Form.Group controlId="formStuGender" className="mb-3">
                            <Form.Label>性别 <span className="text-danger">*</span></Form.Label>
                            <div className="d-flex">
                                <Form.Check
                                    type="radio"
                                    label="男"
                                    name="stuGender"
                                    value="true"
                                    checked={formData.stuGender === true}
                                    onChange={handleChange}
                                    className="me-4"
                                />
                                <Form.Check
                                    type="radio"
                                    label="女"
                                    name="stuGender"
                                    value="false"
                                    checked={formData.stuGender === false}
                                    onChange={handleChange}
                                />
                            </div>
                        </Form.Group>

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
                                {subClasses.map(sc => (
                                    <option key={sc.subClassId} value={sc.subClassId}>{sc.subClassName}</option>
                                ))}
                            </Form.Control>
                        </Form.Group>

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
                            <Button variant="primary" type="submit" disabled={loading} className="mr-2">
                                {loading ? '更新中...' : '更新学生信息'}
                            </Button>
                            <Button variant="secondary" onClick={() => navigate('/students')}>
                                取消
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default EditStudent;
