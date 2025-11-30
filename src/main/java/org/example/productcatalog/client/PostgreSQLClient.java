package org.example.productcatalog.client;

import org.example.productcatalog.property.ApplicationProperties;
import org.postgresql.ds.PGSimpleDataSource;

public class PostgreSQLClient {
    private static PostgreSQLClient INSTANCE;

    private static PGSimpleDataSource datasource;

    private PostgreSQLClient() {
        var url = System.getenv("POSTGRES_DATASOURCE_URL");

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        datasource = new PGSimpleDataSource();
        datasource.setURL(url == null ? applicationProperties.getProperty("datasource.url") : url);
        datasource.setPortNumbers(new int[]{Integer.parseInt(applicationProperties.getProperty("datasource.port"))});
        datasource.setDatabaseName(applicationProperties.getProperty("datasource.database"));
        datasource.setStringType("unspecified");
        datasource.setUser(applicationProperties.getProperty("datasource.username"));
        datasource.setPassword(applicationProperties.getProperty("datasource.password"));
        datasource.setCurrentSchema(applicationProperties.getProperty("datasource.currentSchema"));
    }

    public static PostgreSQLClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PostgreSQLClient();
        }
        return INSTANCE;
    }

    public PGSimpleDataSource getDataSource() {
        return datasource;
    }
}
