import apiClient from './client';

// 课程管理API
export const getSubjects = () => {
    return apiClient.get('/subjects');
};

export const getSubjectsPage = (params) => {
    return apiClient.get('/subjects/page', { params });
};

export const getSubjectById = (id) => {
    return apiClient.get(`/subjects/${id}`);
};

export const createSubject = (subjectData) => {
    return apiClient.post('/subjects', subjectData);
};

export const createSubjectByParams = (subjectName, academyId, credit) => {
    return apiClient.post('/subjects/create', null, {
        params: { subjectName, academyId, credit }
    });
};

export const updateSubject = (id, subjectData) => {
    return apiClient.put(`/subjects/${id}`, subjectData);
};

export const deleteSubject = (id) => {
    return apiClient.delete(`/subjects/${id}`);
};

export const getSubjectsByAcademy = (academy) => {
    return apiClient.get(`/subjects/academy/${academy}`);
};

export const getSubjectsByAcademyPage = (academy, params) => {
    return apiClient.get(`/subjects/academy/${academy}/page`, { params });
};

export const searchSubjectsByName = (name) => {
    return apiClient.get('/subjects/search/name', { params: { name } });
};

export const searchSubjectsByNamePage = (name, params) => {
    return apiClient.get('/subjects/search/name/page', { 
        params: { name, ...params } 
    });
};

export const getSubjectByName = (subjectName) => {
    return apiClient.get(`/subjects/name/${subjectName}`);
};

export const getSubjectByAcademyAndName = (academy, subjectName) => {
    return apiClient.get(`/subjects/academy/${academy}/name/${subjectName}`);
};

export const getSubjectsByCredit = (credit) => {
    return apiClient.get(`/subjects/credit/${credit}`);
};

export const getSubjectsByCreditPage = (credit, params) => {
    return apiClient.get(`/subjects/credit/${credit}/page`, { params });
};

export const getSubjectsByCreditRange = (minCredit, maxCredit) => {
    return apiClient.get('/subjects/credit-range', { 
        params: { minCredit, maxCredit } 
    });
};

export const getSubjectsByCreditRangePage = (minCredit, maxCredit, params) => {
    return apiClient.get('/subjects/credit-range/page', { 
        params: { minCredit, maxCredit, ...params } 
    });
};

export const getSubjectsByAcademyAndCredit = (academy, credit) => {
    return apiClient.get(`/subjects/academy/${academy}/credit/${credit}`);
};

export const getSubjectsByAcademyAndCreditRange = (academy, minCredit, maxCredit) => {
    return apiClient.get(`/subjects/academy/${academy}/credit-range`, { 
        params: { minCredit, maxCredit } 
    });
};

export const searchSubjectsByMultipleConditions = (academy, subjectName, minCredit, maxCredit) => {
    return apiClient.get('/subjects/search/multiple', { 
        params: { academy, subjectName, minCredit, maxCredit } 
    });
};

export const searchSubjectsByMultipleConditionsPage = (academy, subjectName, minCredit, maxCredit, params) => {
    return apiClient.get('/subjects/search/multiple/page', { 
        params: { academy, subjectName, minCredit, maxCredit, ...params } 
    });
};

export const searchSubjects = (keyword) => {
    return apiClient.get('/subjects/search', { params: { keyword } });
};

// 统计相关API
export const countSubjectsByAcademy = (academy) => {
    return apiClient.get(`/subjects/academy/${academy}/count`);
};

export const countSubjectsByCredit = (credit) => {
    return apiClient.get(`/subjects/credit/${credit}/count`);
};

export const countSubjectsByCreditRange = (minCredit, maxCredit) => {
    return apiClient.get('/subjects/credit-range/count', { 
        params: { minCredit, maxCredit } 
    });
};

export const countSubjectsByAcademyAndCreditRange = (academy, minCredit, maxCredit) => {
    return apiClient.get(`/subjects/academy/${academy}/credit-range/count`, { 
        params: { minCredit, maxCredit } 
    });
};

// 聚合操作API
export const getTotalCreditByAcademy = (academy) => {
    return apiClient.get(`/subjects/academy/${academy}/total-credit`);
};

export const getAllAcademies = () => {
    return apiClient.get('/subjects/academies');
};

export const getCreditsByAcademy = (academy) => {
    return apiClient.get(`/subjects/academy/${academy}/credits`);
};

// 业务逻辑API
export const checkSubjectExists = (academy, subjectName) => {
    return apiClient.get('/subjects/exists', { 
        params: { academy, subjectName } 
    });
};
