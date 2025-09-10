package com.jamesliu.stumanagement.student_management.Entity.User;

import jakarta.persistence.*;

@Entity
@Table(name="table_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer userId;
    
    @Column(name="username", unique = true, nullable = false)
    private String username;
    
    @Column(name="password", nullable = false)
    private String password;
    
    @Column(name="role", nullable = false)
    private String role; // ADMIN 或 TEACHER
    
    @Column(name="teacher_id")
    private Integer teacherId; // 关联教师ID，管理员可为null

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
