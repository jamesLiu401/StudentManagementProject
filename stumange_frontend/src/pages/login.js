import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { useAuth } from '../contexts/AuthContexts';

const LoginSchema = Yup.object().shape({
    username: Yup.string().required('用户名不能为空'),
    password: Yup.string().required('密码不能为空')
});

const Login = () => {
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { login } = useAuth();

    const formik = useFormik({
        initialValues: {
            username: '',
            password: ''
        },
        validationSchema: LoginSchema,
        onSubmit: async (values, { setSubmitting }) => {
            try {
                setError('');
                setIsLoading(true);
                await login({
                    username: values.username,
                    password: values.password
                });
                navigate('/dashboard');
            } catch (err) {
                console.error('登录错误:', err);
                // 显示更详细的错误信息
                const errorMessage = err.message || err.response?.data?.message || '登录失败，请检查用户名和密码';
                setError(errorMessage);
            } finally {
                setIsLoading(false);
                setSubmitting(false);
            }
        }
    });

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">用户登录</div>
                        <div className="card-body">
                            {error && (
                                <div className="alert alert-danger" role="alert">
                                    {error}
                                </div>
                            )}
                            <form onSubmit={formik.handleSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="username" className="form-label">用户名</label>
                                    <input
                                        type="text"
                                        className={`form-control ${formik.touched.username && formik.errors.username ? 'is-invalid' : ''}`}
                                        id="username"
                                        {...formik.getFieldProps('username')}
                                    />
                                    {formik.touched.username && formik.errors.username && (
                                        <div className="invalid-feedback">{formik.errors.username}</div>
                                    )}
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">密码</label>
                                    <input
                                        type="password"
                                        className={`form-control ${formik.touched.password && formik.errors.password ? 'is-invalid' : ''}`}
                                        id="password"
                                        {...formik.getFieldProps('password')}
                                    />
                                    {formik.touched.password && formik.errors.password && (
                                        <div className="invalid-feedback">{formik.errors.password}</div>
                                    )}
                                </div>
                                <button 
                                    type="submit" 
                                    className="btn btn-primary"
                                    disabled={isLoading || formik.isSubmitting}
                                >
                                    {isLoading || formik.isSubmitting ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            登录中...
                                        </>
                                    ) : (
                                        '登录'
                                    )}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;