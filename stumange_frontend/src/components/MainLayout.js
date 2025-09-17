import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Navbar, Nav, Container, Offcanvas, Button } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';

const MainLayout = () => {
    const [show, setShow] = useState(false);
    const { currentUser, logout, isAdmin } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
        } catch (error) {
            console.error('登出失败:', error);
        }
    };

    const handleNavClick = (path) => {
        navigate(path);
        setShow(false);
    };

    const isActive = (path) => {
        return location.pathname === path;
    };

    const menuItems = [
        { path: '/dashboard', label: '仪表板', icon: '📊' },
        { path: '/students', label: '学生管理', icon: '👨‍🎓' },
        { path: '/teachers', label: '教师管理', icon: '👨‍🏫' },
        { path: '/payments', label: '缴费管理', icon: '💰' },
        { path: '/scores', label: '成绩管理', icon: '📝' },
        { path: '/academies', label: '学院管理', icon: '🏛️' },
        { path: '/majors', label: '专业管理', icon: '📚' },
        { path: '/classes', label: '班级管理', icon: '👥' },
        { path: '/total-classes', label: '总班级管理', icon: '🏫' },
        { path: '/sub-classes', label: '子班级管理', icon: '👥' },
        { path: '/subjects', label: '课程管理', icon: '📖' },
    ];

    // 管理员专用菜单项
    const adminMenuItems = [
        { path: '/register', label: '用户注册', icon: '👤' },
    ];

    return (
        <div className="d-flex">
            {/* 侧边栏 */}
            <div className="bg-dark text-white d-flex flex-column" style={{ width: '250px', minHeight: '100vh' }}>
                <div className="p-3 flex-grow-1">
                    <h4 className="text-center mb-4">
                        <i className="fas fa-graduation-cap me-2"></i>
                        学生管理系统
                    </h4>
                    
                    <div className="mb-4">
                        <div className="text-center">
                            <div className="bg-primary rounded-circle d-inline-flex align-items-center justify-content-center" 
                                 style={{ width: '50px', height: '50px' }}>
                                <i className="fas fa-user text-white"></i>
                            </div>
                            <div className="mt-2">
                                <div className="fw-bold">{currentUser?.username}</div>
                                <small className="text-muted">
                                    {currentUser?.role === 'ROLE_ADMIN' ? '管理员' : '教师'}
                                </small>
                            </div>
                        </div>
                    </div>

                    <nav className="nav flex-column">
                        {menuItems.map((item) => (
                            <button
                                key={item.path}
                                className={`btn btn-link text-start text-white p-2 mb-1 ${
                                    isActive(item.path) ? 'bg-primary' : ''
                                }`}
                                onClick={() => handleNavClick(item.path)}
                                style={{ border: 'none', textDecoration: 'none' }}
                            >
                                <span className="me-2">{item.icon}</span>
                                {item.label}
                            </button>
                        ))}
                        
                        {/* 管理员专用菜单 */}
                        {isAdmin() && (
                            <>
                                <hr className="my-2 text-secondary" />
                                {adminMenuItems.map((item) => (
                                    <button
                                        key={item.path}
                                        className={`btn btn-link text-start text-white p-2 mb-1 ${
                                            isActive(item.path) ? 'bg-primary' : ''
                                        }`}
                                        onClick={() => handleNavClick(item.path)}
                                        style={{ border: 'none', textDecoration: 'none' }}
                                    >
                                        <span className="me-2">{item.icon}</span>
                                        {item.label}
                                    </button>
                                ))}
                            </>
                        )}
                    </nav>
                </div>

                <div className="p-3 border-top border-secondary">
                    <Button 
                        variant="outline-light" 
                        className="w-100"
                        onClick={handleLogout}
                    >
                        <i className="fas fa-sign-out-alt me-2"></i>
                        退出登录
                    </Button>
                </div>
            </div>

            {/* 主内容区域 */}
            <div className="flex-grow-1">
                {/* 顶部导航栏 */}
                <Navbar bg="light" expand="lg" className="border-bottom">
                    <Container fluid>
                        <Navbar.Brand>
                            <Button
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => setShow(true)}
                                className="d-lg-none"
                            >
                                <i className="fas fa-bars"></i>
                            </Button>
                            <span className="ms-2">
                                {menuItems.find(item => item.path === location.pathname)?.label || '学生管理系统'}
                            </span>
                        </Navbar.Brand>
                        
                    </Container>
                </Navbar>

                {/* 页面内容 */}
                <main className="p-4">
                    <Outlet />
                </main>
            </div>

            {/* 移动端侧边栏 */}
            <Offcanvas show={show} onHide={() => setShow(false)} placement="start">
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>学生管理系统</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <div className="mb-4">
                        <div className="text-center">
                            <div className="bg-primary rounded-circle d-inline-flex align-items-center justify-content-center" 
                                 style={{ width: '50px', height: '50px' }}>
                                <i className="fas fa-user text-white"></i>
                            </div>
                            <div className="mt-2">
                                <div className="fw-bold">{currentUser?.username}</div>
                                <small className="text-muted">
                                    {currentUser?.role === 'ROLE_ADMIN' ? '管理员' : '教师'}
                                </small>
                            </div>
                        </div>
                    </div>

                    <nav className="nav flex-column">
                        {menuItems.map((item) => (
                            <button
                                key={item.path}
                                className={`btn btn-link text-start p-2 mb-1 ${
                                    isActive(item.path) ? 'bg-primary text-white' : ''
                                }`}
                                onClick={() => handleNavClick(item.path)}
                                style={{ border: 'none', textDecoration: 'none' }}
                            >
                                <span className="me-2">{item.icon}</span>
                                {item.label}
                            </button>
                        ))}
                        
                        {/* 管理员专用菜单 */}
                        {isAdmin() && (
                            <>
                                <hr className="my-2" />
                                {adminMenuItems.map((item) => (
                                    <button
                                        key={item.path}
                                        className={`btn btn-link text-start p-2 mb-1 ${
                                            isActive(item.path) ? 'bg-primary text-white' : ''
                                        }`}
                                        onClick={() => handleNavClick(item.path)}
                                        style={{ border: 'none', textDecoration: 'none' }}
                                    >
                                        <span className="me-2">{item.icon}</span>
                                        {item.label}
                                    </button>
                                ))}
                            </>
                        )}
                    </nav>
                </Offcanvas.Body>
            </Offcanvas>
        </div>
    );
};

export default MainLayout;
