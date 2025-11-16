package org.example.productcatalog.repository;

import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class ProductRepository implements CrudRepository<Product> {
    private static final ProductRepository INSTANCE = new ProductRepository();
    private final PGConnectionPoolDataSource datasource;
    private static String SCHEMA;

    private ProductRepository() {
        datasource = PostgreSQLClient.getInstance().getDataSource();
        SCHEMA = datasource.getCurrentSchema();
    }

    public static ProductRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Product add(Product product) {
        var query = "INSERT INTO " + SCHEMA + ".\"product\" (item, brand, title, category, price) " + "VALUES (?,?,?,?,?)";

        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(product).map(value -> {
                try {
                    statement.setString(1, value.getItem());
                    statement.setString(2, value.getBrand());
                    statement.setString(3, value.getTitle());
                    statement.setString(4, value.getCategory());
                    statement.setDouble(5, value.getPrice());
                    return getEntity(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан товар"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Product update(Product product) {
        var query = "UPDATE " + SCHEMA + ".\"product\" SET brand=?, title=?, category=?, price=? WHERE id=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(product).map(value -> {
                try {
                    statement.setString(1, value.getBrand());
                    statement.setString(2, value.getTitle());
                    statement.setString(3, value.getCategory());
                    statement.setDouble(4, value.getPrice());
                    return getEntity(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан товар"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Product delete(Product product) {
        var query = "DELETE FROM " + SCHEMA + ".\"product\" WHERE id=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query)) {
            return Optional.ofNullable(product).map(value -> {
                try {
                    statement.setLong(1, value.getId());
                    if (statement.executeUpdate() == 1) {
                        try (var resultSet = statement.getGeneratedKeys()) {
                            while (resultSet.next()) {
                                value.setId(resultSet.getInt(1));
                            }
                            value.setItem(resultSet.getString("item"));
                            value.setBrand(resultSet.getString("brand"));
                            value.setTitle(resultSet.getString("title"));
                            value.setCategory(resultSet.getString("category"));
                            value.setPrice(resultSet.getDouble("price"));
                            return value;
                        } catch (Exception e) {
                            throw new ApplicationException(e.getMessage());
                        }
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан товар"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Collection<Product> getAll() { var query = "SELECT * FROM " + SCHEMA + ".\"product\"";
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var entity = new Product(
                                resultSet.getString("item"),
                                resultSet.getString("brand"),
                                resultSet.getString("title"),
                                resultSet.getString("category"),
                                resultSet.getDouble("price")
                        );
                        entity.setId(resultSet.getLong("id"));
                        return entity;
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).takeWhile(Objects::nonNull).toList();
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Optional<Product> getByItem(String item) {
        var query = "SELECT * FROM " + SCHEMA + ".\"product\" WHERE item=?";

        return Optional.ofNullable(item).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(query)) {
                statement.setString(1, item);
                try (ResultSet resultSet = statement.executeQuery()) {
                    Product entity = null;
                    while (resultSet.next()) {
                        entity = new Product(
                                resultSet.getString("item"),
                                resultSet.getString("brand"),
                                resultSet.getString("title"),
                                resultSet.getString("category"),
                                resultSet.getDouble("price")
                        );
                        entity.setId(resultSet.getLong("id"));
                    }
                    return entity;
                } catch (Exception e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        });
    }
}
