package com.jamesliu.stumanagement.student_management.config;

import com.jamesliu.stumanagement.student_management.crypto.CredentialTools;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "UserEntityManagerFactory",
        transactionManagerRef = "UserTransactionManager",
        basePackages = {"com.jamesliu.stumanagement.student_management.repository.UserRepo"}
)
public class UserDatabaseConfig {

    @Value("${spring.datasource.usermanage.url}")
    private String url;

    @Value("${spring.datasource.usermanage.username}")
    private String encryptedUsername;

    @Value("${spring.datasource.usermanage.password}")
    private String encryptedPassword;

    @Value("${spring.datasource.usermanage.driver-class-name}")
    private String driverClassName;
    
    @Value("${environment_private_key_name}")
    private String privateKeyName;

    private final CredentialTools credentialTools;

    public UserDatabaseConfig(CredentialTools credentialTools) {
        this.credentialTools = credentialTools;
    }

    @Primary
    @Bean(name = "UserDataSource")
    public DataSource dataSource() throws Exception {
        // 解密用户名和密码
        String username = new String(credentialTools.decrypt(stripEnc(encryptedUsername), privateKeyName));
        String password = new String(credentialTools.decrypt(stripEnc(encryptedPassword), privateKeyName));

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Primary
    @Bean(name = "UserEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("UserDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.jamesliu.stumanagement.student_management.Entity.User")
                .persistenceUnit("UserDatabaseConfig")
                .build();
    }

    @Primary
    @Bean(name = "UserTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("UserEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
