package org.example.productcatalog.client;

import org.example.productcatalog.property.ApplicationProperties;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;


public class PostgreSQLClient {
    private final PGSimpleDataSource datasource;

    public PostgreSQLClient(ApplicationProperties applicationProperties) {
        var url = System.getenv("POSTGRES_DATASOURCE_URL");

        datasource = new PGSimpleDataSource();
        datasource.setURL(url == null ? applicationProperties.getProperty("spring.datasource.url") : url);
        datasource.setPortNumbers(new int[]{Integer.parseInt(applicationProperties.getProperty("spring.datasource.port"))});
        datasource.setDatabaseName(applicationProperties.getProperty("spring.datasource.database"));
        datasource.setStringType("unspecified");
        datasource.setUser(applicationProperties.getProperty("spring.datasource.username"));
        datasource.setPassword(applicationProperties.getProperty("spring.datasource.password"));
        datasource.setCurrentSchema(applicationProperties.getProperty("spring.datasource.currentSchema"));
    }

    public DataSource getDataSource() {
        return datasource;
    }
}
