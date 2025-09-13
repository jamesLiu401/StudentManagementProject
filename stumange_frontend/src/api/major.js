import apiClient from './client';

// 专业管理API
export const getMajors = () => {
    return apiClient.get('/majors');
};

export const getMajorsPage = (params) => {
    return apiClient.get('/majors/page', { params });
};

export const getMajorById = (id) => {
    return apiClient.get(`/majors/${id}`);
};

export const createMajor = (majorData) => {
    return apiClient.post('/majors', majorData);
};

export const updateMajor = (id, majorData) => {
    return apiClient.put(`/majors/${id}`, majorData);
};

export const deleteMajor = (id) => {
    return apiClient.delete(`/majors/${id}`);
};

export const getMajorsByAcademy = (academyId) => {
    return apiClient.get(`/majors/academy/${academyId}`);
};

export const getMajorsByAcademyPage = (academyId, params) => {
    return apiClient.get(`/majors/academy/${academyId}/page`, { params });
};

export const getMajorsByGrade = (grade) => {
    return apiClient.get(`/majors/grade/${grade}`);
};

export const getMajorsByGradePage = (grade, params) => {
    return apiClient.get(`/majors/grade/${grade}/page`, { params });
};

export const getMajorsByName = (majorName) => {
    return apiClient.get(`/majors/name/${majorName}`);
};

export const getMajorsByNamePage = (majorName, params) => {
    return apiClient.get(`/majors/name/${majorName}/page`, { params });
};

export const getMajorsByCounselor = (counselorId) => {
    return apiClient.get(`/majors/counselor/${counselorId}`);
};

export const getMajorsByCounselorPage = (counselorId, params) => {
    return apiClient.get(`/majors/counselor/${counselorId}/page`, { params });
};

export const getMajorsByAcademyAndGrade = (academyId, grade) => {
    return apiClient.get(`/majors/academy/${academyId}/grade/${grade}`);
};

// 统计相关API
export const countMajorsByCounselor = (counselorId) => {
    return apiClient.get(`/majors/counselor/${counselorId}/count`);
};

export const countMajorsByAcademy = (academyId) => {
    return apiClient.get(`/majors/academy/${academyId}/count`);
};

export const countMajorsByGrade = (grade) => {
    return apiClient.get(`/majors/grade/${grade}/count`);
};

export const countMajorsByAcademyAndGrade = (academyId, grade) => {
    return apiClient.get(`/majors/academy/${academyId}/grade/${grade}/count`);
};
