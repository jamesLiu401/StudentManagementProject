import apiClient from './client';

export const getStudents = (params) => {
    return apiClient.get('/students', { params });
};

export const getStudentsPage = (params) => {
    return apiClient.get('/students/page', { params });
};

export const getStudentById = (id) => {
    return apiClient.get(`/students/${id}`);
};

export const createStudent = (studentData) => {
    return apiClient.post('/students', studentData);
};

export const updateStudent = (id, studentData) => {
    return apiClient.put(`/students/${id}`, studentData);
};

export const deleteStudent = (id) => {
    return apiClient.delete(`/students/${id}`);
};

export const searchStudents = (name) => {
    return apiClient.get('/students/search', { params: { name } });
};

export const importStudents = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/students/batch/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
};