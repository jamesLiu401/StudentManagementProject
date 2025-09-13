import React, { useState, useEffect } from 'react';
import { Card, Button, Alert, Container } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContexts';
import * as studentApi from '../api/student';

const AuthTest = () => {
    const { currentUser } = useAuth();
    const [testResult, setTestResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const testAuth = async () => {
        setLoading(true);
        setTestResult(null);

        try {
            // 检查本地存储
            const token = localStorage.getItem('token');
            const user = localStorage.getItem('user');
            
            console.log('=== 认证测试开始 ===');
            console.log('Token存在:', !!token);
            console.log('Token内容:', token ? token.substring(0, 50) + '...' : '无');
            console.log('用户信息:', user);
            console.log('当前用户上下文:', currentUser);

            // 测试API调用
            const response = await studentApi.getStudents();
            console.log('API调用成功:', response);
            
            setTestResult({
                success: true,
                message: '认证测试成功！',
                details: {
                    tokenExists: !!token,
                    userExists: !!user,
                    currentUser: currentUser,
                    apiResponse: response
                }
            });

        } catch (error) {
            console.error('认证测试失败:', error);
            setTestResult({
                success: false,
                message: error.message || '认证测试失败',
                details: {
                    error: error,
                    response: error.response?.data
                }
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-4">
            <Card>
                <Card.Header>
                    <h4>认证状态测试</h4>
                </Card.Header>
                <Card.Body>
                    <div className="mb-3">
                        <h5>当前认证状态</h5>
                        <p><strong>用户:</strong> {currentUser?.username || '未登录'}</p>
                        <p><strong>角色:</strong> {currentUser?.role || '无'}</p>
                        <p><strong>Token:</strong> {localStorage.getItem('token') ? '存在' : '不存在'}</p>
                    </div>

                    <Button 
                        variant="primary" 
                        onClick={testAuth} 
                        disabled={loading}
                        className="mb-3"
                    >
                        {loading ? '测试中...' : '测试认证状态'}
                    </Button>

                    {testResult && (
                        <Alert variant={testResult.success ? 'success' : 'danger'}>
                            <Alert.Heading>
                                {testResult.success ? '测试成功' : '测试失败'}
                            </Alert.Heading>
                            <p>{testResult.message}</p>
                            <details>
                                <summary>详细信息</summary>
                                <pre>{JSON.stringify(testResult.details, null, 2)}</pre>
                            </details>
                        </Alert>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default AuthTest;
