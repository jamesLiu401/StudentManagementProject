import apiClient from './client';

// 成绩管理API
export const getScores = () => {
    return apiClient.get('/scores');
};

export const getScoresPage = (params) => {
    return apiClient.get('/scores/page', { params });
};

export const getScoreById = (id) => {
    return apiClient.get(`/scores/${id}`);
};

export const createScore = (scoreData) => {
    return apiClient.post('/scores', scoreData);
};

export const createScoreByParams = (studentId, subjectId, score) => {
    return apiClient.post('/scores/create', null, {
        params: { studentId, subjectId, score }
    });
};

export const updateScore = (id, newScore) => {
    return apiClient.put(`/scores/${id}`, null, {
        params: { newScore }
    });
};

export const updateScoreByStudentAndSubject = (studentId, subjectId, newScore) => {
    return apiClient.put('/scores/update', null, {
        params: { studentId, subjectId, newScore }
    });
};

export const deleteScore = (id) => {
    return apiClient.delete(`/scores/${id}`);
};

export const getScoresByStudentId = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}`);
};

export const getScoresBySubjectId = (subjectId) => {
    return apiClient.get(`/scores/subject/${subjectId}`);
};

export const getScoreByStudentAndSubject = (studentId, subjectId) => {
    return apiClient.get(`/scores/student/${studentId}/subject/${subjectId}`);
};

// 统计分析API
export const getAverageScoreByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/average`);
};

export const getAverageScoreBySubject = (subjectId) => {
    return apiClient.get(`/scores/subject/${subjectId}/average`);
};

export const getMaxScoreByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/max`);
};

export const getMinScoreByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/min`);
};

export const getPassingScoresByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/passing`);
};

export const getFailingScoresByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/failing`);
};

export const countPassingScoresByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/passing/count`);
};

export const countFailingScoresByStudent = (studentId) => {
    return apiClient.get(`/scores/student/${studentId}/failing/count`);
};

// 批量操作
export const batchCreateScores = (scores) => {
    return apiClient.post('/scores/batch', scores);
};

// 成绩等级
export const getScoreGrade = (score) => {
    return apiClient.get('/scores/grade', { params: { score } });
};
