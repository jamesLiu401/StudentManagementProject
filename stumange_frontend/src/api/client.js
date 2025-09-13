import axios from 'axios';
import config from '../config/config';

const apiClient = axios.create({
    baseURL: config.api.baseURL,
    timeout: config.api.timeout,
    headers: config.api.headers
});

// 请求拦截器添加token
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log('请求头已设置Authorization:', `Bearer ${token.substring(0, 20)}...`);
        } else {
            console.warn('未找到token，请求可能被拒绝');
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器处理错误
apiClient.interceptors.response.use(
    (response) => {
        // 保持完整的响应对象，包括状态码
        return {
            data: response.data,
            status: response.status,
            statusText: response.statusText,
            headers: response.headers
        };
    },
    (error) => {
        // 处理401未授权错误（但不包括登录接口）
        if (error.response && error.response.status === 401) {
            // 检查是否是登录接口，如果是则不重定向
            const isLoginRequest = error.config && error.config.url && error.config.url.includes('/auth/login');
            if (!isLoginRequest) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        }
        // 处理403禁止访问错误
        if (error.response && error.response.status === 403) {
            console.error('访问被禁止:', error.response.data);
            console.error('请求URL:', error.config?.url);
            console.error('请求方法:', error.config?.method);
            console.error('请求头:', error.config?.headers);
            
            // 检查是否是权限问题
            const errorMessage = error.response?.data?.message || '访问被禁止，请检查您的权限';
            throw new Error(errorMessage);
        }
        return Promise.reject(error);
    }
);

export default apiClient;