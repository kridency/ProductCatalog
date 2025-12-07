package org.example.productcatalog.property;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

public class LiquibaseProperties extends AbstractProperties {
    private static final LiquibaseProperties INSTANCE = new LiquibaseProperties();

    private LiquibaseProperties() {
        try {
            properties = new Yaml().load(getClass().getResourceAsStream("/liquibase.yml"));
        } catch (NullPointerException e) {
            LOGGER.info("Could not load liquibase.yml");
        }
    }

    public static LiquibaseProperties getInstance() {
        return INSTANCE;
    }
}
