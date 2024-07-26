package com.demo.project99.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
        basePackages = "com.demo.project99.repository.secondary",
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDataSourceConfig {

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource(Environment env) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.datasource.secondary.url"));
        config.setUsername(env.getProperty("spring.datasource.secondary.username"));
        config.setPassword(env.getProperty("spring.datasource.secondary.password"));
        config.setDriverClassName(env.getProperty("spring.datasource.secondary.driver-class-name"));
        config.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.secondary.hikari.maximum-pool-size")));
        config.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.secondary.hikari.minimum-idle")));
        config.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.secondary.hikari.idle-timeout")));
        config.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.secondary.hikari.max-lifetime")));
        config.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.secondary.hikari.connection-timeout")));
        return new HikariDataSource(config);
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource, Environment env) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(secondaryDataSource);
        factory.setPackagesToScan("com.demo.project99.domain.secondary");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(hibernateProperties(env));
        return factory;
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
        return new JpaTransactionManager(secondaryEntityManagerFactory.getObject());
    }

    @Bean
    public SpringLiquibase secondaryLiquibase(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(secondaryDataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-secondary.yaml");
        liquibase.setContexts("secondary");
        return liquibase;
    }

    private Map<String, Object> hibernateProperties(Environment env) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
        return properties;
    }

    /**
     * Non connection pool approach to get data source.
     */
    private DataSource createDataSource(Environment env) {
        return DataSourceBuilder.create()
                .url(env.getProperty("spring.datasource.secondary.url"))
                .username(env.getProperty("spring.datasource.secondary.username"))
                .password(env.getProperty("spring.datasource.secondary.password"))
                .driverClassName(env.getProperty("spring.datasource.secondary.driver-class-name"))
                .build();

    }
}