package org.example.productcatalog.config;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"org.example.productcatalog"})
@EnableSpringConfigured
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableJpaRepositories(
        basePackages = "org.example.productcatalog.repository",
        entityManagerFactoryRef = "entityManager")
@EnableAutoConfiguration
@EnableTransactionManagement
@ConfigurationPropertiesScan
@PropertySources(
        value = {
                @PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class),
                @PropertySource(value = "classpath:liquibase.yml", factory = YamlPropertySourceFactory.class)
        }
)
public class ApplicationConfiguration {
    @Value("${spring.datasource.url}")
    private String dataUrl;

    private final String url = System.getenv("POSTGRES_DATASOURCE_URL");

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url == null ? dataUrl : url);
        dataSource.setPortNumbers(new int[]{Integer.parseInt("5432")});
        dataSource.setDatabaseName("product_db");
        dataSource.setStringType("unspecified");
        dataSource.setUser("intern");
        dataSource.setPassword("1gjAVnJ");
        dataSource.setCurrentSchema("custom");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager(){
        final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPackagesToScan("org.example.productcatalog");
        entityManager.setDataSource(dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            {
                setDatabase(Database.POSTGRESQL);
                setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
                setShowSql(true);
                setGenerateDdl(true);
            }
        };
        entityManager.setJpaVendorAdapter(vendorAdapter);
        return entityManager;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:" + "db/migration/changelog/dbChangeLog.xml");
        liquibase.setDefaultSchema("custom");
        liquibase.setLiquibaseSchema("auxiliary");
        liquibase.setChangeLogParameters(Map.of("schemaName", "custom"));
        liquibase.setDropFirst(true);
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
