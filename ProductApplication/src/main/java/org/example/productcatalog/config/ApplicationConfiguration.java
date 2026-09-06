package org.example.productcatalog.config;

import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.hibernate.SpringBeanContainer;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {"org.example.productcatalog"})
@EnableSpringConfigured
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableJpaRepositories(
        basePackages = "org.example.productcatalog.repository",
        entityManagerFactoryRef = "entityManager")
@EnableAutoConfiguration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@PropertySources(
        value = {
                @PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
        }
)
public class ApplicationConfiguration {
    @Value("${spring.datasource.url}")
    private String dataUrl;

    @Value("${app.datasource.data-schema}")
    private String dataSchema;

    @Value("${app.datasource.liquibase-schema}")
    private String liquibaseSchema;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(dataUrl);
        dataSource.setStringType("unspecified");
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setCurrentSchema(dataSchema);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager(ConfigurableListableBeanFactory beanFactory){
        final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPackagesToScan("org.example.productcatalog");
        entityManager.setDataSource(dataSource());
        entityManager.setJpaProperties(new Properties() {
            {
                put ("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
            }
        });

        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {
            {
                setDatabase(Database.POSTGRESQL);
                setShowSql(true);
                setGenerateDdl(true);
            }
        });
        return entityManager;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    @DependsOn("entityManager")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:" + "db/changelog/dbChangeLog.xml");
        liquibase.setDefaultSchema(dataSchema);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        liquibase.setChangeLogParameters(Map.of("schemaName", dataSchema));
        return liquibase;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("org.hibernate.validator.ValidationMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
