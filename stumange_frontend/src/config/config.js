// 应用配置文件
export const config = {
    // API配置
    api: {
        baseURL: process.env.REACT_APP_API_URL || '/api',
        timeout: 10000,
        headers: {
            'Content-Type': 'application/json'
        }
    },
    
    // 应用配置
    app: {
        name: '学生管理系统',
        version: '1.0.0',
        author: 'James Liu'
    },
    
    // 分页配置
    pagination: {
        defaultPageSize: 10,
        pageSizeOptions: [5, 10, 20, 50]
    },
    
    // 主题配置
    theme: {
        primaryColor: '#007bff',
        secondaryColor: '#6c757d',
        successColor: '#28a745',
        dangerColor: '#dc3545',
        warningColor: '#ffc107',
        infoColor: '#17a2b8'
    },
    
    // 功能开关
    features: {
        enableBatchImport: true,
        enableExport: true,
        enableAdvancedSearch: true,
        enableStatistics: true
    }
};

export default config;
