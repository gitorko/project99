package com.demo.project99.properties;

import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.jpa.properties.hibernate")
@Data
public class HibernateProperties {
    private Map<String, String> properties;
}

