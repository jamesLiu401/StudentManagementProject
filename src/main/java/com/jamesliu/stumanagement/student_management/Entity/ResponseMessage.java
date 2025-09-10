package com.jamesliu.stumanagement.student_management.Entity;

import org.springframework.http.HttpStatus;

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
