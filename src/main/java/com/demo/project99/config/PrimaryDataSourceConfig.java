package com.demo.project99.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.demo.project99.repository.primary",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource(Environment env) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.datasource.primary.url"));
        config.setUsername(env.getProperty("spring.datasource.primary.username"));
        config.setPassword(env.getProperty("spring.datasource.primary.password"));
        config.setDriverClassName(env.getProperty("spring.datasource.primary.driver-class-name"));
        config.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.primary.hikari.maximum-pool-size")));
        config.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.primary.hikari.minimum-idle")));
        config.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.primary.hikari.idle-timeout")));
        config.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.primary.hikari.max-lifetime")));
        config.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.primary.hikari.connection-timeout")));
        return new HikariDataSource(config);
    }

    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource primaryDataSource, Environment env) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(primaryDataSource);
        factory.setPackagesToScan("com.demo.project99.domain.primary");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(hibernateProperties(env));
        return factory;
    }

    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }

    @Bean
    public SpringLiquibase primaryLiquibase(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(primaryDataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-primary.yaml");
        liquibase.setContexts("primary");
        return liquibase;
    }

    private Map<String, Object> hibernateProperties(Environment env) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
        return properties;
    }
}


