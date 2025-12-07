package org.example.productcatalog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Catalog API",
                version = "1.0.0",
                description = "This is a sample API for demonstrating OpenAPI documentation.",
                contact = @Contact(
                        name = "API Support",
                        email = "support@example.com",
                        url = "http://www.example.com/support"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080/api/v1", description = "Development Server"),
        },
        tags = {
                @Tag(name = "Users", description = "Operations related to user management"),
                @Tag(name = "Products", description = "Operations related to product catalog")
        }
)
@SecurityScheme(
        name = "basicScheme",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class DocConfiguration implements WebMvcConfigurer {
    @Value("${app.swagger-ui-version}")
    private String swaggerUiVersion;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/" + swaggerUiVersion)
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry
                .addViewController("/swagger-ui/openapi.json")
                .setViewName("forward:/webjars/swagger-ui/openapi.json");
        registry
                .addViewController("/swagger-ui/swagger-initializer.js")
                .setViewName("forward:/webjars/swagger-ui/swagger-initializer.js");
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    OpenAPI getOpenApi() {
        return new OpenAPI();
    }
}
