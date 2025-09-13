import apiClient from './client';

export const login = (credentials) => {
    return apiClient.post('/auth/login', credentials);
};

export const register = (userData) => {
    return apiClient.post('/auth/register', userData);
};

export const logout = () => {
    return apiClient.get('/auth/logout');
};