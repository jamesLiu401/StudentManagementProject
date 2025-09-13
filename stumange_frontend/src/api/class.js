import apiClient from './client';

// 总班级管理API
export const getTotalClasses = () => {
    return apiClient.get('/classes/total');
};

export const getTotalClassesPage = (params) => {
    return apiClient.get('/classes/total/page', { params });
};

export const getTotalClassById = (id) => {
    return apiClient.get(`/classes/total/${id}`);
};

export const createTotalClass = (totalClassData) => {
    return apiClient.post('/classes/total', totalClassData);
};

export const createTotalClassByParams = (totalClassName, majorId) => {
    return apiClient.post('/classes/total/create', null, {
        params: { totalClassName, majorId }
    });
};

export const updateTotalClass = (id, totalClassData) => {
    return apiClient.put(`/classes/total/${id}`, totalClassData);
};

export const deleteTotalClass = (id) => {
    return apiClient.delete(`/classes/total/${id}`);
};

export const getTotalClassesByMajor = (majorId) => {
    return apiClient.get(`/classes/total/major/${majorId}`);
};

export const searchTotalClasses = (name) => {
    return apiClient.get('/classes/total/search', { params: { name } });
};

export const getTotalClassByNameAndMajor = (totalClassName, majorId) => {
    return apiClient.get(`/classes/total/name/${totalClassName}/major/${majorId}`);
};

export const checkTotalClassExists = (totalClassName, majorId) => {
    return apiClient.get('/classes/total/exists', {
        params: { totalClassName, majorId }
    });
};

export const searchTotalClassesGeneral = (keyword) => {
    return apiClient.get('/classes/total/search/general', { params: { keyword } });
};

// 子班级管理API
export const getSubClasses = () => {
    return apiClient.get('/classes/sub');
};

export const getSubClassesPage = (params) => {
    return apiClient.get('/classes/sub/page', { params });
};

export const getSubClassById = (id) => {
    return apiClient.get(`/classes/sub/${id}`);
};

export const createSubClass = (subClassData) => {
    return apiClient.post('/classes/sub', subClassData);
};

export const createSubClassByParams = (subClassName, totalClassId) => {
    return apiClient.post('/classes/sub/create', null, {
        params: { subClassName, totalClassId }
    });
};

export const updateSubClass = (id, subClassData) => {
    return apiClient.put(`/classes/sub/${id}`, subClassData);
};

export const deleteSubClass = (id) => {
    return apiClient.delete(`/classes/sub/${id}`);
};

export const getSubClassesByTotalClass = (totalClassId) => {
    return apiClient.get(`/classes/sub/total/${totalClassId}`);
};

export const searchSubClasses = (name) => {
    return apiClient.get('/classes/sub/search', { params: { name } });
};

export const getSubClassByNameAndTotalClass = (subClassName, totalClassId) => {
    return apiClient.get(`/classes/sub/name/${subClassName}/total/${totalClassId}`);
};

export const checkSubClassExists = (subClassName, totalClassId) => {
    return apiClient.get('/classes/sub/exists', {
        params: { subClassName, totalClassId }
    });
};

export const searchSubClassesGeneral = (keyword) => {
    return apiClient.get('/classes/sub/search/general', { params: { keyword } });
};
