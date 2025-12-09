package org.example.productcatalog.config;

import jakarta.annotation.Nonnull;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"org.example.productcatalog"})
@EnableSpringConfigured
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(
        basePackages = "org.example.productcatalog.repository",
        entityManagerFactoryRef = "entityManager")
@EnableTransactionManagement(mode=AdviceMode.ASPECTJ)
@ConfigurationPropertiesScan
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
public class ApplicationConfiguration implements LoadTimeWeavingConfigurer {
    @Value("${spring.datasource.url}")
    private String dataUrl;

    @Value("${spring.datasource.port}")
    private String dataPort;

    @Value("${spring.datasource.database}")
    private String dataBase;

    @Value("${spring.datasource.username}")
    private String dataUsername;

    @Value("${spring.datasource.password}")
    private String dataPassword;

    @Value("${spring.datasource.hikari.schema}")
    private String dataSchema;

    @Value("${spring.liquibase.changeLogFile}")
    private String changeLogFile;

    @Value("${spring.datasource.hikari.schema}")
    private String defaultSchemaName;

    @Value("${spring.liquibase.liquibaseSchemaName}")
    private String liquibaseSchemaName;

    @Value("${spring.datasource.hikari.schema}")
    private String schemaName;

    private final String url = System.getenv("POSTGRES_DATASOURCE_URL");

    @Override
    @Nonnull
    public LoadTimeWeaver getLoadTimeWeaver() {
        return new InstrumentationLoadTimeWeaver();
    }

    @Bean
    public InstrumentationLoadTimeWeaver instrumentationLoadTimeWeaver(ApplicationContext applicationContext) {
        return new InstrumentationLoadTimeWeaver(applicationContext.getClassLoader());
    }

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url == null ? dataUrl : url);
        dataSource.setPortNumbers(new int[]{Integer.parseInt(dataPort)});
        dataSource.setDatabaseName(dataBase);
        dataSource.setStringType("unspecified");
        dataSource.setUser(dataUsername);
        dataSource.setPassword(dataPassword);
        dataSource.setCurrentSchema(dataSchema);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager(){
        final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPersistenceUnitName("productcatalog");
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
    public JpaTransactionManager transactionManager(){
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getNativeEntityManagerFactory());
        return transactionManager;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:" + changeLogFile);
        liquibase.setDefaultSchema(defaultSchemaName);
        liquibase.setLiquibaseSchema(liquibaseSchemaName);
        liquibase.setChangeLogParameters(Map.of("schemaName", schemaName));
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
