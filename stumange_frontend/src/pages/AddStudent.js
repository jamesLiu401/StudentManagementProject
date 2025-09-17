import React, { useState, useEffect } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { createStudent } from '../api/student';
import {getSubClassById, getSubClasses} from '../api/class';
import {getMajors} from "../api/major";

const AddStudent = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        stuName: '',
        stuGender: true, // 默认男
        stuTel: '',
        stuAddress: '',
        majorId: '',
        stuGrade: '',
        stuClassId: '',
        stuClassName:''
    });
    const [subClasses, setSubClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [majors, setMajors] = useState([]);
    const [majorsLoading, setMajorsLoading] = useState(true);

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
        const fetchMajors = async () => {
            try{
                setMajorsLoading(true);
                const response = await getMajors();
                if (response?.data?.data) {
                    setMajors(response.data.data);
                }
            }catch(err) {
                console.error('获取专业列表失败',err);
                setError('获取专业列表失败');
            } finally {
                setMajorsLoading(false);
            }
        };

        fetchSubClasses();
        fetchMajors();
    }, []);

    // 处理表单输入变化
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        
        if (name === 'majorId') {
            // 当选择专业时，自动填充年级
            const selectedMajor = majors.find(major => major.majorId === parseInt(value, 10));
            setFormData(prev => ({
                ...prev,
                [name]: value,
                stuGrade: selectedMajor ? selectedMajor.grade : ''
            }));
        } else if(name === 'stuClassId') {
            const selectedClass = subClasses.find(subClass => subClass.subClassId === parseInt(value, 10));
            setFormData(prev =>({
                ...prev,
                [name]: value,
                stuClassName : selectedClass.subClassName,
            }));
        } else{
            setFormData(prev => ({
                ...prev,
                [name]: type === 'checkbox' ? checked : value
            }));
        }
        
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
            const selectedMajor = majors.find(major => major.majorId === parseInt(formData.majorId, 10));
            const selectedSubClass = subClasses.find(subClass => subClass.subClassId === parseInt(formData.subClassId, 10));
            
            const studentData = {
                stuName: formData.stuName,
                stuGender: formData.stuGender,
                stuTel: formData.stuTel,
                stuAddress: formData.stuAddress,
                majorId: parseInt(formData.majorId, 10),
                stuGrade: parseInt(formData.stuGrade, 10),
                stuClassId: parseInt(formData.stuClassId, 10),
                stuClassName: formData.stuClassName.trim(),
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
                    majorId: '',
                    stuGrade: '',
                    stuClassId: '',
                    stuClassName: ''
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
                                name="stuClassId"
                                value={formData.stuClassId}
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
                        <Form.Group controlId="formMajorId" className="mb-3">
                            <Form.Label>专业 <span className="text-danger">*</span></Form.Label>
                            {majorsLoading ? (
                                <Form.Control as="select" disabled>
                                    <option>加载专业列表中...</option>
                                </Form.Control>
                            ) : (
                                <Form.Control
                                    as="select"
                                    name="majorId"
                                    value={formData.majorId}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="">请选择专业</option>
                                    {majors.map(major => (
                                        <option key={major.majorId} value={major.majorId}>
                                            {major.majorName} ({major.grade}级)
                                        </option>
                                    ))}
                                </Form.Control>
                            )}
                        </Form.Group>

                        {/* 年级 */}
                        <Form.Group controlId="formGrade" className="mb-3">
                            <Form.Label>年级 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="grade"
                                value={formData.stuGrade}
                                onChange={handleChange}
                                placeholder={formData.majorId ? "已根据专业自动填充" : "请先选择专业"}
                                readOnly={!!formData.majorId}
                                required
                            />
                            {formData.majorId && (
                                <Form.Text className="text-muted">
                                    <i className="fas fa-info-circle me-1"></i>
                                    年级已根据所选专业自动填充
                                </Form.Text>
                            )}
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
