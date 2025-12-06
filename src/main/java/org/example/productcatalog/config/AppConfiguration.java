package org.example.productcatalog.config;

import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"org.example.productcatalog"})
@EnableWebMvc
@EnableJpaRepositories(
        basePackages = "org.example.productcatalog.repository",
        entityManagerFactoryRef = "entityManager")
@EnableTransactionManagement
@PropertySources(
        value = {
                @PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class),
                @PropertySource(value = "classpath:liquibase.yml", factory = YamlPropertySourceFactory.class)
        }
)
public class AppConfiguration {
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

    @Value("${spring.datasource.currentSchema}")
    private String dataSchema;

    @Value("${changeLogFile}")
    private String changeLogFile;

    @Value("${defaultSchemaName}")
    private String defaultSchemaName;

    @Value("${liquibaseSchemaName}")
    private String liquibaseSchemaName;

    @Value("${parameter.schemaName}")
    private String schemaName;

    private final String url = System.getenv("POSTGRES_DATASOURCE_URL");

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
        entityManager.setDataSource(dataSource());
        entityManager.setPersistenceUnitName("productcatalog");

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter(){
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

    @Bean
    public ConversionService conversionService() {
        return DefaultConversionService.getSharedInstance();
    }
}
