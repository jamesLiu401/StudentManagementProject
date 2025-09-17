package com.jamesliu.stumanagement.student_management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson配置类
 * 配置JSON序列化和反序列化规则，处理Hibernate代理对象和Java 8时间类型
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>Hibernate代理对象处理 - 避免序列化时的LazyInitializationException</li>
 *   <li>Java 8时间类型支持 - 支持LocalDate、LocalDateTime等时间类型</li>
 *   <li>JSON格式配置 - 配置日期格式、空值处理等</li>
 *   <li>性能优化 - 优化序列化性能</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.1
 * @since 2025-01-12
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 配置Hibernate模块
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        hibernateModule.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
        hibernateModule.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        hibernateModule.enable(Hibernate5JakartaModule.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        mapper.registerModule(hibernateModule);
        
        // 配置Java 8时间模块
        mapper.registerModule(new JavaTimeModule());
        
        // 配置序列化特性
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        return mapper;
    }
}