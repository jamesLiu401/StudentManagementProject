package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.User.User;
import com.jamesliu.stumanagement.student_management.repository.UserRepo.UserRepository;
import com.jamesliu.stumanagement.student_management.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
            return ResponseMessage.error("用户名或密码错误");
        }
    }

    @GetMapping("/logout")
    public ResponseMessage<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseMessage.success("登出成功");
    }

    @PostMapping("/register")
    public ResponseMessage<User> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseMessage.error("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认角色设置为TEACHER，管理员角色需要手动设置
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_TEACHER");
        } else {
            user.setRole("ROLE_" + user.getRole());
        }
        
        User savedUser = userRepository.save(user);
        return ResponseMessage.success(savedUser);
    }
}
