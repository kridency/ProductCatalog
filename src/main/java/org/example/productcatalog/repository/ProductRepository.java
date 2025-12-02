package org.example.productcatalog.repository;

import lombok.NonNull;
import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class ProductRepository implements CrudRepository<Product> {
    private final DataSource datasource;
    private static final String INSERT_QUERY = "INSERT INTO \"product\" (item, brand, title, category, price) " + "VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"product\" SET item=?, brand=?, title=?, category=?, price=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"product\" WHERE item=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"product\"";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"product\" WHERE id=?";
    private static final String GET_BY_ITEM_QUERY = "SELECT * FROM \"product\" WHERE item=?";

    public ProductRepository() {
        datasource = PostgreSQLClient.getInstance().getDataSource();
    }

    @Override
    public Product add(Product product) {
        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(INSERT_QUERY, new String[] {"id"})) {
            try {
                statement.setString(1, product.getItem());
                statement.setString(2, product.getBrand());
                statement.setString(3, product.getTitle());
                statement.setString(4, product.getCategory());
                statement.setDouble(5, product.getPrice());
                return setEntityId(statement, product, product::setId);
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Product update(Product product) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(UPDATE_QUERY, new String[] {"id"})) {
            try {
                statement.setString(1, product.getItem());
                statement.setString(2, product.getBrand());
                statement.setString(3, product.getTitle());
                statement.setString(4, product.getCategory());
                statement.setDouble(5, product.getPrice());
                statement.setLong(6, product.getId());
                return setEntityId(statement, product, product::setId);
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Product delete(Product product) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(DELETE_QUERY,
                     new String[] {"id", "item", "brand", "title", "category", "price"})) {
            try {
                statement.setString(1, product.getItem());
                if (statement.executeUpdate() == 1) {
                    try (var resultSet = statement.getGeneratedKeys()) {
                        while (resultSet.next()) {
                            product.setId(resultSet.getLong(1));
                            product.setItem(resultSet.getString("item"));
                            product.setBrand(resultSet.getString("brand"));
                            product.setTitle(resultSet.getString("title"));
                            product.setCategory(resultSet.getString("category"));
                            product.setPrice(resultSet.getDouble("price"));
                        }
                        return product;
                    } catch (Exception e) {
                        throw new ApplicationException(e.getMessage());
                    }
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Collection<Product> getAll() {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
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

    @Override
    public Optional<Product> getByKey(String item) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_ITEM_QUERY)) {
            statement.setString(1, item);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getProduct(resultSet);
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @NonNull
    private Optional<Product> getProduct(ResultSet resultSet) throws SQLException {
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
        return Optional.ofNullable(entity);
    }

    public synchronized Optional<Product> getById(long id) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return getProduct(resultSet);
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
