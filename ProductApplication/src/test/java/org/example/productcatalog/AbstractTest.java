package org.example.productcatalog;

import org.example.productcatalog.config.ApplicationConfiguration;
import org.example.productcatalog.config.AspectModuleConfiguration;
import org.example.productcatalog.config.DocumentConfiguration;
import org.example.productcatalog.config.SecurityConfiguration;

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

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class,
        DocumentConfiguration.class,
        SecurityConfiguration.class,
        AspectModuleConfiguration.class})
public class AbstractTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    @BeforeAll
    public static void start() {
        postgreSQLContainer.withCopyFileToContainer(MountableFile.forHostPath("../docker/init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql").start();
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
