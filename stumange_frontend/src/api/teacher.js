import apiClient from './client';

// 教师管理API
export const getTeachers = () => {
    return apiClient.get('/teachers');
};

export const getTeachersPage = (params) => {
    return apiClient.get('/teachers/page', { params });
};

export const getTeacherById = (id) => {
    return apiClient.get(`/teachers/${id}`);
};

export const createTeacher = (teacherData) => {
    return apiClient.post('/teachers', teacherData);
};

export const updateTeacher = (id, teacherData) => {
    return apiClient.put(`/teachers/${id}`, teacherData);
};

export const deleteTeacher = (id) => {
    return apiClient.delete(`/teachers/${id}`);
};

export const searchTeachersByName = (name) => {
    return apiClient.get('/teachers/search', { params: { name } });
};

export const searchTeachersByNamePage = (name, params) => {
    return apiClient.get('/teachers/search/page', { 
        params: { name, ...params } 
    });
};

export const getTeachersByDepartment = (department) => {
    return apiClient.get(`/teachers/department/${department}`);
};

export const getTeachersByTitle = (title) => {
    return apiClient.get(`/teachers/title/${title}`);
};

export const getTeachersByDepartmentAndTitle = (department, title) => {
    return apiClient.get(`/teachers/department/${department}/title/${title}`);
};

export const countTeachersByDepartment = (department) => {
    return apiClient.get(`/teachers/department/${department}/count`);
};

export const countTeachersByTitle = (title) => {
    return apiClient.get(`/teachers/title/${title}/count`);
};

export const countTeachersByDepartmentAndTitle = (department, title) => {
    return apiClient.get(`/teachers/department/${department}/title/${title}/count`);
};
