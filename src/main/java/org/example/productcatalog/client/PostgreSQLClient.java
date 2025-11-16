package org.example.productcatalog.client;

import org.example.productcatalog.property.ApplicationProperties;
import org.postgresql.ds.PGConnectionPoolDataSource;


public class PostgreSQLClient {
    private static PostgreSQLClient INSTANCE;

    private static PGConnectionPoolDataSource datasource;

    private PostgreSQLClient() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        datasource = new PGConnectionPoolDataSource();
        datasource.setURL(applicationProperties.getProperty("datasource.url"));
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

    public PGConnectionPoolDataSource getDataSource() {
        return datasource;
    }
}
