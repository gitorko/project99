package com.demo.project99.config;

import java.util.Properties;
import javax.sql.DataSource;

import com.demo.project99.properties.HibernateProperties;
import com.demo.project99.properties.PrimaryDataSourceProperties;
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
        basePackages = "com.demo.project99.repository.primary",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource(PrimaryDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            HibernateProperties hibernateProperties) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(primaryDataSource);
        factory.setPackagesToScan("com.demo.project99.domain.primary");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(hibernatePropertiesToProperties(hibernateProperties));

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

    private Properties hibernatePropertiesToProperties(HibernateProperties hibernateProperties) {
        Properties properties = new Properties();
        if (hibernateProperties.getProperties() != null) {
            properties.putAll(hibernateProperties.getProperties());
        }
        return properties;
    }
}


