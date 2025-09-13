import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContexts';
import 'bootstrap/dist/css/bootstrap.min.css';

// 布局组件
import MainLayout from './components/MainLayout';

// 认证页面
import Login from './pages/login';
import Register from './pages/Register';

// 学生管理页面
import Students from './pages/students';
import StudentDetail from './pages/StudentDetail';
import AddStudent from './pages/AddStudent';
import EditStudent from './pages/EditStudent';
import ImportStudents from './pages/ImportStudents';

// 教师管理页面
import Teachers from './pages/Teachers';
import TeacherDetail from './pages/TeacherDetail';
import AddTeacher from './pages/AddTeacher';
import EditTeacher from './pages/EditTeacher';

// 缴费管理页面
import Payments from './pages/Payments';
import PaymentDetail from './pages/PaymentDetail';
import AddPayment from './pages/AddPayment';
import EditPayment from './pages/EditPayment';

// 成绩管理页面
import Scores from './pages/Scores';

// 学院管理页面
import Academies from './pages/Academies';

// 专业管理页面
import Majors from './pages/Majors';

// 班级管理页面
import Classes from './pages/Classes';

// 课程管理页面
import Subjects from './pages/Subjects';

// 仪表板页面
import Dashboard from './pages/Dashboard';

// 保护路由组件
const ProtectedRoute = ({ children, requireAdmin = false }) => {
    const { currentUser, loading, isAdmin } = useAuth();

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <div className="text-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">加载中...</span>
                    </div>
                    <div className="mt-3">加载中...</div>
                </div>
            </div>
        );
    }

    if (!currentUser) {
        return <Navigate to="/login" />;
    }

    if (requireAdmin && !isAdmin()) {
        return <Navigate to="/dashboard" />;
    }

    return children;
};

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* 公开路由 */}
                    <Route path="/login" element={<Login />} />
                    <Route
                        path="/register"
                        element={
                            <ProtectedRoute requireAdmin={true}>
                                <Register />
                            </ProtectedRoute>
                        }
                    />

                    {/* 需要认证的路由 */}
                    <Route
                        path="/"
                        element={
                            <ProtectedRoute>
                                <MainLayout />
                            </ProtectedRoute>
                        }
                    >
                        <Route index element={<Navigate to="/dashboard" />} />
                        <Route path="dashboard" element={<Dashboard />} />

                        {/* 学生管理路由 */}
                        <Route path="students" element={<Students />} />
                        <Route path="students/:id" element={<StudentDetail />} />
                        <Route
                            path="students/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddStudent />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="students/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditStudent />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="students/import"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <ImportStudents />
                                </ProtectedRoute>
                            }
                        />

                        {/* 教师管理路由 */}
                        <Route path="teachers" element={<Teachers />} />
                        <Route path="teachers/:id" element={<TeacherDetail />} />
                        <Route
                            path="teachers/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddTeacher />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="teachers/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditTeacher />
                                </ProtectedRoute>
                            }
                        />

                        {/* 缴费管理路由 */}
                        <Route path="payments" element={<Payments />} />
                        <Route path="payments/:id" element={<PaymentDetail />} />
                        <Route
                            path="payments/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddPayment />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="payments/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditPayment />
                                </ProtectedRoute>
                            }
                        />

                        {/* 成绩管理路由 */}
                        <Route path="scores" element={<Scores />} />

                        {/* 学院管理路由 */}
                        <Route path="academies" element={<Academies />} />

                        {/* 专业管理路由 */}
                        <Route path="majors" element={<Majors />} />

                        {/* 班级管理路由 */}
                        <Route path="classes" element={<Classes />} />

                        {/* 课程管理路由 */}
                        <Route path="subjects" element={<Subjects />} />
                    </Route>

                    {/* 其他路由重定向到登录 */}
                    <Route path="*" element={<Navigate to="/login" />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;