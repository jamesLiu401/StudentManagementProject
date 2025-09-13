import React, { createContext, useState, useEffect, useContext } from 'react';
import { login as apiLogin, register as apiRegister, logout as apiLogout } from '../api/auth';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // 检查本地存储中的token
        const token = localStorage.getItem('token');
        const userData = localStorage.getItem('user');

        if (token && userData) {
            setCurrentUser(JSON.parse(userData));
        }

        setLoading(false);
    }, []);

    const login = async (credentials) => {
        try {
            setLoading(true);
            const response = await apiLogin(credentials);
            // 现在HTTP状态码和响应体状态码应该一致
            if (response.status === 200) {
                // 正确获取data字段中的数据
                const responseData = response.data.data; // response.data.data 才是真正的数据
                const { token, ...user } = responseData;
                localStorage.setItem('token', token);
                localStorage.setItem('user', JSON.stringify(user));
                setCurrentUser(user);
                return user;
            } else {
                throw new Error(response.data?.message || '登录失败');
            }
        } catch (error) {
            // 处理401未授权错误（用户名密码错误）
            if (error.response && error.response.status === 401) {
                const errorMessage = error.response?.data?.message || '用户名或密码错误';
                throw new Error(errorMessage);
            }
            // 处理403禁止访问错误
            if (error.response && error.response.status === 403) {
                throw new Error('访问被禁止，请检查您的权限');
            }
            // 处理400错误（兼容旧版本）
            if (error.response && error.response.status === 400) {
                const errorMessage = error.response?.data?.message || '用户名或密码错误';
                throw new Error(errorMessage);
            }
            // 处理其他错误
            const errorMessage = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码';
            throw new Error(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const register = async (userData) => {
        try {
            const response = await apiRegister(userData);
            // 适配后端响应格式：status: 200 表示成功
            if (response.status === 200) {
                return response.data;
            } else {
                throw new Error(response.message || '注册失败');
            }
        } catch (error) {
            throw error;
        }
    };

    const logout = async () => {
        try {
            await apiLogout();
        } finally {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            setCurrentUser(null);
        }
    };

    const isAdmin = () => {
        return currentUser && currentUser.role === 'ROLE_ADMIN';
    };

    const isTeacher = () => {
        return currentUser && currentUser.role === 'ROLE_TEACHER';
    };

    return (
        <AuthContext.Provider value={{
            currentUser,
            login,
            register,
            logout,
            isAdmin,
            isTeacher,
            loading
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);