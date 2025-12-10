package org.example.productcatalog;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.productcatalog.config.ApplicationConfiguration;
import org.example.productcatalog.config.DocumentConfiguration;
import org.example.productcatalog.config.SecurityConfiguration;
import org.example.productcatalog.exception.ApplicationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.sql.DriverManager;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, DocumentConfiguration.class, SecurityConfiguration.class})
public class AbstractTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));

    /*
    static {
        postgreSQLContainer
                .withDatabaseName(applicationProperties.getProperty("spring.datasource.database"))
                .withUsername(applicationProperties.getProperty("spring.datasource.username"))
                .withPassword(applicationProperties.getProperty("spring.datasource.password"))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("🐳 " + "postgres")))
                .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql")
                .withExposedPorts(Integer.parseInt(applicationProperties.getProperty("spring.datasource.port")))
                .withReuse(true).start();

        try(var connection = DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
                )) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(liquibaseProperties.getProperty("defaultSchemaName"));
            database.setLiquibaseSchemaName(liquibaseProperties.getProperty("liquibaseSchemaName"));

            var liquibase  = new Liquibase(liquibaseProperties.getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);
            liquibase.setChangeLogParameter("schemaName", liquibaseProperties.getProperty("defaultSchemaName"));
            liquibase.update(new Contexts("test"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
    */

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.hikari.schema",() -> "custom");
        registry.add("spring.jpa.properties.hibernate.jakarta.persistence.create-database-schemas",() -> "true");
        registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers",() -> "true");
        registry.add("spring.jpa.properties.hibernate.default_schema",() -> "custom");
    }

    @BeforeAll
    public static void start() {
        postgreSQLContainer
                .withUsername("intern")
                .withPassword("1gjAVnJ")
                .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql")
                .withReuse(true).start();

        try(var connection = DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        )) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName("custom");
            database.setLiquibaseSchemaName("auxiliary");

            var liquibase  = new Liquibase("db/migration/changelog/dbChangeLog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.setChangeLogParameter("schemaName", "custom");
            liquibase.update(new Contexts("test"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
