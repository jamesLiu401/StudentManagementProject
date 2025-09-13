import apiClient from './client';

// 缴费管理API
export const getPayments = () => {
    return apiClient.get('/payments');
};

export const getPaymentsPage = (params) => {
    return apiClient.get('/payments/page', { params });
};

export const getPaymentById = (id) => {
    return apiClient.get(`/payments/${id}`);
};

export const createPayment = (paymentData) => {
    return apiClient.post('/payments', paymentData);
};

export const updatePayment = (id, paymentData) => {
    return apiClient.put(`/payments/${id}`, paymentData);
};

export const updatePaymentStatus = (id, statusData) => {
    return apiClient.patch(`/payments/${id}/status`, statusData);
};

export const deletePayment = (id) => {
    return apiClient.delete(`/payments/${id}`);
};

export const getPaymentsByStudent = (studentId) => {
    return apiClient.get(`/payments/student/${studentId}`);
};

export const getPaymentsByStatus = (status) => {
    return apiClient.get(`/payments/status/${status}`);
};

export const getPaymentsByType = (type) => {
    return apiClient.get(`/payments/type/${type}`);
};

export const getPaymentsByTypePage = (type, params) => {
    return apiClient.get(`/payments/type/${type}/page`, { params });
};

export const getPaymentsByStatusPage = (status, params) => {
    return apiClient.get(`/payments/status/${status}/page`, { params });
};

export const getPaymentsByDateRange = (startDate, endDate) => {
    return apiClient.get('/payments/date-range', { 
        params: { startDate, endDate } 
    });
};

export const getPaymentsByAmountRange = (minAmount, maxAmount) => {
    return apiClient.get('/payments/amount-range', { 
        params: { minAmount, maxAmount } 
    });
};

export const getPaymentsByStudentAndType = (studentId, type) => {
    return apiClient.get(`/payments/student/${studentId}/type/${type}`);
};

export const getPaymentsByStudentAndStatus = (studentId, status) => {
    return apiClient.get(`/payments/student/${studentId}/status/${status}`);
};

export const getPaymentsByTypeAndStatus = (type, status) => {
    return apiClient.get(`/payments/type/${type}/status/${status}`);
};

export const getPaymentsByStudentAndDateRange = (studentId, startDate, endDate) => {
    return apiClient.get(`/payments/student/${studentId}/date-range`, { 
        params: { startDate, endDate } 
    });
};

export const getPaymentsByStudentAndAmountRange = (studentId, minAmount, maxAmount) => {
    return apiClient.get(`/payments/student/${studentId}/amount-range`, { 
        params: { minAmount, maxAmount } 
    });
};

// 统计相关API
export const countPaymentsByStudent = (studentId) => {
    return apiClient.get(`/payments/student/${studentId}/count`);
};

export const countPaymentsByType = (type) => {
    return apiClient.get(`/payments/type/${type}/count`);
};

export const countPaymentsByStatus = (status) => {
    return apiClient.get(`/payments/status/${status}/count`);
};

export const countPaymentsByStudentAndStatus = (studentId, status) => {
    return apiClient.get(`/payments/student/${studentId}/status/${status}/count`);
};

// 金额统计API
export const getTotalAmountByStudent = (studentId) => {
    return apiClient.get(`/payments/student/${studentId}/total-amount`);
};

export const getTotalAmountByStudentId = (studentId) => {
    return apiClient.get(`/payments/student/${studentId}/total-amount-by-id`);
};

export const getTotalAmountByType = (type) => {
    return apiClient.get(`/payments/type/${type}/total-amount`);
};

export const getTotalAmountByStatus = (status) => {
    return apiClient.get(`/payments/status/${status}/total-amount`);
};

export const getTotalAmountByStudentAndType = (studentId, type) => {
    return apiClient.get(`/payments/student/${studentId}/type/${type}/total-amount`);
};

// 搜索功能
export const searchPayments = (keyword) => {
    return apiClient.get('/payments/search', { params: { keyword } });
};
