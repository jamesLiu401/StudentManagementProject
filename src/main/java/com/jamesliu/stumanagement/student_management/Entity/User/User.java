package com.jamesliu.stumanagement.student_management.Entity.User;

import jakarta.persistence.*;

/**
 * 用户实体类
 * 用于管理系统用户的基本信息，包括管理员和教师
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>用户身份验证 - 存储用户名和密码</li>
 *   <li>角色管理 - 区分管理员(ADMIN)和教师(TEACHER)角色</li>
 *   <li>权限控制 - 基于角色的访问控制</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：table_user</li>
 *   <li>主键：user_id (自增)</li>
 *   <li>唯一约束：username</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-01
 */
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
