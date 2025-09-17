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
import ScoreDetail from './pages/ScoreDetail';
import AddScore from './pages/AddScore';
import EditScore from './pages/EditScore';

// 学院管理页面
import Academies from './pages/Academies';
import AddAcademy from './pages/AddAcademy';
import EditAcademy from './pages/EditAcademy';
import AcademyDetail from './pages/AcademyDetail';

// 专业管理页面
import Majors from './pages/Majors';
import MajorDetail from './pages/MajorDetail';
import AddMajor from './pages/AddMajor';
import EditMajor from './pages/EditMajor';

// 班级管理页面
import Classes from './pages/Classes';
import TotalClasses from './pages/TotalClasses';
import SubClasses from './pages/SubClasses';
import AddTotalClass from './pages/AddTotalClass';
import AddSubClass from './pages/AddSubClass';
import TotalClassDetail from './pages/TotalClassDetail';
import EditTotalClass from './pages/EditTotalClass';
import SubClassDetail from './pages/SubClassDetail';
import EditSubClass from './pages/EditSubClass';

// 课程管理页面
import Subjects from './pages/Subjects';
import SubjectDetail from './pages/SubjectDetail';
import AddSubject from './pages/AddSubject';
import EditSubject from './pages/EditSubject';

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
                        <Route path="scores/:id" element={<ScoreDetail />} />
                        <Route
                            path="scores/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddScore />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="scores/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditScore />
                                </ProtectedRoute>
                            }
                        />

                        {/* 学院管理路由 */}
                        <Route path="academies" element={<Academies />} />
                        <Route path="academies/:id" element={<AcademyDetail />} />
                        <Route
                            path="academies/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddAcademy />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="academies/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditAcademy />
                                </ProtectedRoute>
                            }
                        />

                        {/* 专业管理路由 */}
                        <Route path="majors" element={<Majors />} />
                        <Route path="majors/:id" element={<MajorDetail />} />
                        <Route
                            path="majors/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddMajor />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="majors/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditMajor />
                                </ProtectedRoute>
                            }
                        />

                        {/* 班级管理路由 */}
                        <Route path="classes" element={<Classes />} />
                        
                        {/* 总班级管理路由 */}
                        <Route path="total-classes" element={<TotalClasses />} />
                        <Route path="total-classes/:id" element={<TotalClassDetail />} />
                        <Route
                            path="total-classes/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddTotalClass />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="total-classes/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditTotalClass />
                                </ProtectedRoute>
                            }
                        />
                        
                        {/* 子班级管理路由 */}
                        <Route path="sub-classes" element={<SubClasses />} />
                        <Route path="sub-classes/:id" element={<SubClassDetail />} />
                        <Route
                            path="sub-classes/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddSubClass />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="sub-classes/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditSubClass />
                                </ProtectedRoute>
                            }
                        />

                        {/* 课程管理路由 */}
                        <Route path="subjects" element={<Subjects />} />
                        <Route path="subjects/:id" element={<SubjectDetail />} />
                        <Route
                            path="subjects/add"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <AddSubject />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="subjects/edit/:id"
                            element={
                                <ProtectedRoute requireAdmin={true}>
                                    <EditSubject />
                                </ProtectedRoute>
                            }
                        />
                    </Route>

                    {/* 其他路由重定向到登录 */}
                    <Route path="*" element={<Navigate to="/login" />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;