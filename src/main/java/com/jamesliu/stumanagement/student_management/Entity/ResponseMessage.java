package com.jamesliu.stumanagement.student_management.Entity;

import org.springframework.http.HttpStatus;

/**
 * 统一响应消息实体类
 * 用于封装API接口的响应数据，提供统一的响应格式
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>统一响应格式 - 包含消息、状态码、数据</li>
 *   <li>成功响应封装 - 提供便捷的成功响应方法</li>
 *   <li>错误响应封装 - 提供便捷的错误响应方法</li>
 *   <li>泛型支持 - 支持任意类型的数据封装</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 成功响应
 * ResponseMessage.success("操作成功", data)
 * ResponseMessage.success(data)
 * 
 * // 错误响应
 * ResponseMessage.error("操作失败")
 * ResponseMessage.error("操作失败", 500)
 * </pre>
 * 
 * @param <T> 响应数据的类型
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-07
 */
public class ResponseMessage<T> {
    private String message;
    private Integer status;
    private T data;

    public ResponseMessage(String message, Integer status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static<U> ResponseMessage<U> success(U data){
        return new ResponseMessage<>("操作成功", HttpStatus.OK.value(), data);
    }
    
    public static<U> ResponseMessage<U> success(String message, U data){
        return new ResponseMessage<>(message, HttpStatus.OK.value(), data);
    }

    public static<U> ResponseMessage<U> error(String message){
        return new ResponseMessage<>(message, HttpStatus.BAD_REQUEST.value(), null);
    }
    
    public static<U> ResponseMessage<U> unauthorized(String message){
        return new ResponseMessage<>(message, HttpStatus.UNAUTHORIZED.value(), null);
    }
    
    public static<U> ResponseMessage<U> forbidden(String message){
        return new ResponseMessage<>(message, HttpStatus.FORBIDDEN.value(), null);
    }
    
    public static<U> ResponseMessage<U> error(String message, int status){
        return new ResponseMessage<>(message, status, null);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
