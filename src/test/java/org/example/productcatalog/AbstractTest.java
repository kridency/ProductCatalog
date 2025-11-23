package org.example.productcatalog;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.property.ApplicationProperties;
import org.example.productcatalog.property.LiquibaseProperties;
import org.example.productcatalog.service.UserService;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class AbstractTest {
    protected static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    protected static final LiquibaseProperties liquibaseProperties = LiquibaseProperties.getInstance();

    @Container
    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));
    protected static PGSimpleDataSource datasource;
    protected final static UserService userService;

    static {
        postgreSQLContainer
                .withDatabaseName(applicationProperties.getProperty("datasource.database"))
                .withUsername(applicationProperties.getProperty("datasource.username"))
                .withPassword(applicationProperties.getProperty("datasource.password"))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("🐳 " + "postgres")))
                .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql")
                .withReuse(true).start();
        datasource = PostgreSQLClient.getInstance().getDataSource();
        datasource.setPortNumbers(new int[]{
                postgreSQLContainer
                        .getMappedPort(Integer.parseInt(applicationProperties.getProperty("datasource.port")))
        });
        userService = UserService.getInstance();

        try(var connection = datasource.getConnection()) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(liquibaseProperties.getProperty("defaultSchemaName"));
            database.setLiquibaseSchemaName(liquibaseProperties.getProperty("liquibaseSchemaName"));
            var liquibase  = new Liquibase(liquibaseProperties.getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);
            liquibase.setChangeLogParameter("schemaName", liquibaseProperties.getProperty("defaultSchemaName"));
            liquibase.update(new Contexts("test"));
            //objectMapper.registerModule(new JavaTimeModule());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
