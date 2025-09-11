package com.jamesliu.stumanagement.student_management.config;

import com.jamesliu.stumanagement.student_management.crypto.CredentialTools;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static com.jamesliu.stumanagement.student_management.utils.StripEnc.stripEnc;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "StuEntityManagerFactory",
        transactionManagerRef = "StuTransactionManager",
        basePackages = {
            "com.jamesliu.stumanagement.student_management.repository.StuRepo",
            "com.jamesliu.stumanagement.student_management.repository.TeacherRepo"
        }
       )
public class StuDatabaseConfig {

    @Value("${spring.datasource.stumanage.url}")
    private String url;

    @Value("${spring.datasource.stumanage.username}")
    private String encryptedUsername;

    @Value("${spring.datasource.stumanage.password}")
    private String encryptedPassword;

    @Value("${spring.datasource.stumanage.driver-class-name}")
    private String driverClassName;

    @Value("${environment_private_key_name}")
    private String privateKeyName;

    private final CredentialTools credentialTools;

    public StuDatabaseConfig(CredentialTools credentialTools) {
        this.credentialTools = credentialTools;
    }

    @Bean(name = "StuDataSource")
    public DataSource dataSource() throws Exception {
        String username = new String(credentialTools.decrypt(stripEnc(encryptedUsername), privateKeyName));
        String password = new String(credentialTools.decrypt(stripEnc(encryptedPassword), privateKeyName));

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean (name = "StuEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory (
            EntityManagerFactoryBuilder builder,
            @Qualifier ("StuDataSource") DataSource dataSource) {
        return builder
                .dataSource (dataSource)// 关键：同时指定学生和教师实体的包路径，让它们属于同一个持久化单元
                .packages(
                "com.jamesliu.stumanagement.student_management.Entity.Student",
                "com.jamesliu.stumanagement.student_management.Entity.Teacher",
                "com.jamesliu.stumanagement.student_management.Entity.Finance"
                )
                .persistenceUnit ("StuDatabaseConfig") // 持久化单元名称（统一）
                .build ();
    }

    @Bean(name = "StuTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("StuEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
