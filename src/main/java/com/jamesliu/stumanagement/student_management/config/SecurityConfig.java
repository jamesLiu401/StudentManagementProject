package com.jamesliu.stumanagement.student_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Spring Security 安全配置类
 * 配置系统的安全策略，包括认证、授权、JWT处理等
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>JWT认证配置 - 配置JWT Token的验证和处理</li>
 *   <li>路径权限配置 - 配置不同路径的访问权限</li>
 *   <li>角色权限控制 - 基于角色的访问控制(RBAC)</li>
 *   <li>方法级权限 - 启用方法级权限控制</li>
 *   <li>密码编码器 - 配置密码加密方式</li>
 * </ul>
 * 
 * <p>权限配置：</p>
 * <ul>
 *   <li>公开路径 - /auth/login</li>
 *   <li>管理员路径 - /admin/**, /cascade/**</li>
 *   <li>教师路径 - /teacher/**, /students/**, /teachers/**, /payments/**</li>
 *   <li>通用路径 - /academies/**, /majors/**, /classes/**, /subjects/**</li>
 * </ul>
 * 
 * <p>安全特性：</p>
 * <ul>
 *   <li>CSRF保护 - 禁用CSRF（API服务）</li>
 *   <li>表单登录 - 禁用表单登录（使用JWT）</li>
 *   <li>HTTP Basic - 禁用HTTP Basic认证</li>
 *   <li>JWT过滤器 - 自定义JWT认证过滤器</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-16
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/students/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/payments/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/academies/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/majors/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/classes/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/cascade/**").hasRole("ADMIN")
                .requestMatchers("/subjects/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/scores/**").hasAnyRole("ADMIN", "TEACHER")
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
