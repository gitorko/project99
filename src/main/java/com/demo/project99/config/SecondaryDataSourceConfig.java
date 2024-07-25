package com.demo.project99.config;

import java.util.Properties;
import javax.sql.DataSource;

import com.demo.project99.properties.HibernateProperties;
import com.demo.project99.properties.SecondaryDataSourceProperties;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public DataSource secondaryDataSource(SecondaryDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource,
            HibernateProperties hibernateProperties) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(secondaryDataSource);
        factory.setPackagesToScan("com.demo.project99.domain.secondary");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(hibernatePropertiesToProperties(hibernateProperties));

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

    private Properties hibernatePropertiesToProperties(HibernateProperties hibernateProperties) {
        Properties properties = new Properties();
        if (hibernateProperties.getProperties() != null) {
            properties.putAll(hibernateProperties.getProperties());
        }
        return properties;
    }
}