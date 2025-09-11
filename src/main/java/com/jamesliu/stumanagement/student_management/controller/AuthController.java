package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.User.User;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.repository.UserRepo.UserRepository;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
import com.jamesliu.stumanagement.student_management.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 负责用户登录、注册、登出等认证相关功能
 * 
 * 主要功能：
 * 1. 用户登录 - 验证用户名密码，返回JWT令牌
 * 2. 用户注册 - 创建新用户，支持角色权限控制
 * 3. 用户登出 - 清除安全上下文
 * 4. 智能教师关联 - 注册教师用户时自动创建Teacher实体
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         TeacherRepository teacherRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录接口
     * 验证用户名和密码，成功后返回JWT令牌和用户信息
     * 
     * @param loginUser 包含用户名和密码的登录对象
     * @return 包含JWT令牌、用户名、角色等信息的响应
     */
    @PostMapping("/login")
    public ResponseMessage<Map<String, Object>> login(@RequestBody User loginUser) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );

            // 生成JWT令牌
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
            String token = jwtUtils.generateToken(userDetails);
            
            // 获取用户信息
            User user = userRepository.findByUsername(loginUser.getUsername()).orElse(null);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("username", user.getUsername());
            responseData.put("role", user.getRole());
            responseData.put("userId", user.getUserId());
            
            return ResponseMessage.success(responseData);
        } catch (Exception e) {
            // 记录详细错误信息用于调试
            System.err.println("登录错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseMessage.error("用户名或密码错误");
        }
    }

    @GetMapping("/logout")
    public ResponseMessage<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseMessage.success("登出成功");
    }

    /**
     * 用户注册接口
     * 创建新用户，支持角色权限控制
     * 
     * 权限规则：
     * - ADMIN: 可以注册ADMIN和TEACHER角色
     * - TEACHER: 不能注册任何新用户
     * 
     * 智能特性：
     * - 注册TEACHER角色时自动创建Teacher实体
     * - 自动生成教师编号和默认信息
     * 
     * @param user 包含用户名、密码、角色的用户对象
     * @return 创建成功的用户信息
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<User> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseMessage.error("用户名已存在");
        }
        
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserRole = authentication.getAuthorities().iterator().next().getAuthority();
        
        // 角色权限控制
        String requestedRole = user.getRole();
        if (requestedRole == null || requestedRole.isEmpty()) {
            requestedRole = "TEACHER"; // 默认角色
        }
        
        // 权限验证：教师不能注册教师，管理员可以注册管理员和教师
        if ("ROLE_TEACHER".equals(currentUserRole) && "TEACHER".equals(requestedRole)) {
            return ResponseMessage.error("教师不能注册其他教师账号");
        }
        
        if ("ROLE_TEACHER".equals(currentUserRole) && "ADMIN".equals(requestedRole)) {
            return ResponseMessage.error("教师不能注册管理员账号");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_" + requestedRole);
        
        // 如果是教师角色，智能创建Teacher实体
        if ("TEACHER".equals(requestedRole)) {
            Teacher teacher = createTeacherForUser(user);
            user.setTeacherId(teacher.getTeacherId());
        }
        
        User savedUser = userRepository.save(user);
        return ResponseMessage.success(savedUser);
    }
    
    /**
     * 为教师用户创建Teacher实体
     */
    private Teacher createTeacherForUser(User user) {
        Teacher teacher = new Teacher();
        
        // 生成教师编号
        String teacherNo = generateTeacherNo();
        teacher.setTeacherNo(teacherNo);
        
        // 设置教师姓名（从用户名生成或使用默认值）
        String teacherName = extractTeacherNameFromUsername(user.getUsername());
        teacher.setTeacherName(teacherName);
        
        // 设置默认部门
        teacher.setDepartment("默认部门");
        
        // 设置默认职称
        teacher.setTitle("讲师");
        
        return teacherRepository.save(teacher);
    }
    
    /**
     * 生成教师编号
     */
    private String generateTeacherNo() {
        // 生成格式：T + 年份 + 4位随机数
        String year = String.valueOf(java.time.Year.now().getValue()).substring(2);
        int randomNum = (int) (Math.random() * 9000) + 1000;
        return "T" + year + randomNum;
    }
    
    /**
     * 从用户名提取教师姓名
     */
    private String extractTeacherNameFromUsername(String username) {
        // 简单的姓名提取逻辑，可以根据实际需求调整
        if (username.contains("_")) {
            return username.split("_")[0] + "老师";
        }
        return username + "老师";
    }
}
