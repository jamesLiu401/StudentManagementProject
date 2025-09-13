import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContexts';
import { getStudentsPage, deleteStudent } from '../api/student';

const Students = () => {
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const { isAdmin } = useAuth();

    const fetchStudents = async () => {
        try {
            setLoading(true);
            const response = await getStudentsPage({ page, size: 10 });
            const pageData = response?.data?.data || {};
            
            // 处理学生数据，避免Hibernate懒加载问题
            const processedStudents = Array.isArray(pageData.content) ? 
                pageData.content.map(student => {
                    // 处理班级名称，确保即使subClass是懒加载对象也能正确显示
                    const className = student.subClassName || 
                        (student.subClass && student.subClass.subClassName) || 
                        (student.stuClass && student.stuClass.subClassName) || 
                        '-';
                    
                    return {
                        ...student,
                        // 确保班级名称字段存在且可用
                        subClassName: className
                    };
                }) : [];
            
            setStudents(processedStudents);
            setTotalPages(typeof pageData.totalPages === 'number' ? pageData.totalPages : 0);
        } catch (error) {
            console.error('获取学生列表失败', error);
            setStudents([]);
            setTotalPages(0);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStudents();
    }, [page]);

    const handleDelete = async (id) => {
        if (window.confirm('确定要删除这个学生吗？')) {
            try {
                await deleteStudent(id);
                fetchStudents(); // 重新加载列表
            } catch (error) {
                console.error('删除学生失败', error);
                alert('删除失败: ' + (error.response?.data?.message || '未知错误'));
            }
        }
    };

    if (loading) return <div className="text-center p-5">加载中...</div>;

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>学生管理</h2>
                {isAdmin() && (
                    <Link to="/students/add" className="btn btn-primary">
                        添加学生
                    </Link>
                )}
            </div>

            <div className="card">
                <div className="card-body">
                    <table className="table table-striped">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>姓名</th>
                            <th>性别</th>
                            <th>专业</th>
                            <th>年级</th>
                            <th>班级</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        {(!Array.isArray(students) || students.length === 0) ? (
                            <tr>
                                <td colSpan="7" className="text-center">没有找到学生数据</td>
                            </tr>
                        ) : (
                            students.map((student) => (
                                <tr key={student.stuId}>
                                    <td>{student.stuId}</td>
                                    <td>{student.stuName}</td>
                                    <td>{student.stuGender ? '男' : '女'}</td>
                                    <td>{student.stuMajor}</td>
                                    <td>{student.grade}</td>
                                    <td>{student.subClassName || '-'}</td>
                                    <td>
                                        <Link to={`/students/${student.stuId}`} className="btn btn-sm btn-info me-2">
                                            查看
                                        </Link>
                                        {isAdmin() && (
                                            <>
                                                <Link to={`/students/edit/${student.stuId}`} className="btn btn-sm btn-warning me-2">
                                                    编辑
                                                </Link>
                                                <button
                                                    className="btn btn-sm btn-danger"
                                                    onClick={() => handleDelete(student.stuId)}
                                                >
                                                    删除
                                                </button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* 分页控件 */}
            <nav className="mt-4">
                <ul className="pagination justify-content-center">
                    <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                        <button className="page-link" onClick={() => setPage(page - 1)}>上一页</button>
                    </li>
                    {[...Array(totalPages)].map((_, i) => (
                        <li key={i} className={`page-item ${page === i ? 'active' : ''}`}>
                            <button className="page-link" onClick={() => setPage(i)}>{i + 1}</button>
                        </li>
                    ))}
                    <li className={`page-item ${page === totalPages - 1 ? 'disabled' : ''}`}>
                        <button className="page-link" onClick={() => setPage(page + 1)}>下一页</button>
                    </li>
                </ul>
            </nav>
        </div>
    );
};

export default Students;