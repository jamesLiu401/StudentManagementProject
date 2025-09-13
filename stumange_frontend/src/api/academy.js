import apiClient from './client';

// 学院管理API
export const getAcademies = () => {
    return apiClient.get('/academies');
};

export const getAcademiesPage = (params) => {
    return apiClient.get('/academies/page', { params });
};

export const getAcademyById = (id) => {
    return apiClient.get(`/academies/${id}`);
};

export const createAcademy = (academyData) => {
    return apiClient.post('/academies', academyData);
};

export const updateAcademy = (id, academyData) => {
    return apiClient.put(`/academies/${id}`, academyData);
};

export const deleteAcademy = (id) => {
    return apiClient.delete(`/academies/${id}`);
};

export const searchAcademiesByName = (name) => {
    return apiClient.get('/academies/search/name', { params: { name } });
};

export const searchAcademiesByNamePage = (name, params) => {
    return apiClient.get('/academies/search/name/page', { 
        params: { name, ...params } 
    });
};

export const getAcademiesByDean = (deanName) => {
    return apiClient.get(`/academies/dean/${deanName}`);
};

export const searchAcademiesByDean = (deanName) => {
    return apiClient.get('/academies/search/dean', { params: { deanName } });
};

export const searchAcademiesByDeanPage = (deanName, params) => {
    return apiClient.get('/academies/search/dean/page', { 
        params: { deanName, ...params } 
    });
};

export const getAcademyByName = (name) => {
    return apiClient.get(`/academies/name/${name}`);
};

export const getAcademyByCode = (code) => {
    return apiClient.get(`/academies/code/${code}`);
};

export const countAcademiesByNameContaining = (name) => {
    return apiClient.get('/academies/search/name/count', { params: { name } });
};

export const countAcademiesByDeanName = (deanName) => {
    return apiClient.get('/academies/search/dean/count', { params: { deanName } });
};
