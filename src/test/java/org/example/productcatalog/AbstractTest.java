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
import org.example.productcatalog.property.ApplicationProperties;
import org.example.productcatalog.property.LiquibaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
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
    protected static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    protected static final LiquibaseProperties liquibaseProperties = LiquibaseProperties.getInstance();

    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));

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

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
