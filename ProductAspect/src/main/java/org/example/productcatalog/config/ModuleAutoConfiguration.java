package org.example.productcatalog.config;

import org.example.productcatalog.config.property.ModuleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@Configuration
@EnableSpringConfigured
@EnableConfigurationProperties(ModuleProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityScan("org.example.productcatalog.entity")
public class ModuleAutoConfiguration {
}
