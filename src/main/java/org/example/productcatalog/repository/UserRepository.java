package org.example.productcatalog.repository;

import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class UserRepository implements CrudRepository<User> {
    private static final UserRepository INSTANCE = new UserRepository();
    private final PGConnectionPoolDataSource datasource;
    private static String SCHEMA;

    private UserRepository() {
        datasource = PostgreSQLClient.getInstance().getDataSource();
        SCHEMA = datasource.getCurrentSchema();
    }

    public synchronized static UserRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public User add(User user) {
        var query = "INSERT INTO " + SCHEMA + ".\"user\" (email, password, role) VALUES (?,?,?)";

        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(user).map(value -> {
                try {
                    statement.setString(1, value.getEmail());
                    statement.setString(2, value.getPassword());
                    statement.setString(3, value.getRole().toString());
                    return getEntity(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан пользователь"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        var query = "UPDATE " + SCHEMA + ".\"user\" SET password=? WHERE email=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query)) {
            return Optional.ofNullable(user).map(value -> {
                try {
                    statement.setString(1, value.getPassword());
                    statement.setString(2, value.getEmail());
                    return getEntity(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан пользователь"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public User delete(User user) {
        var query = "DELETE FROM " + SCHEMA + ".\"user\" WHERE email=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(user).map(value -> {
                try {
                    statement.setString(1, value.getEmail());
                    if(statement.executeUpdate() == 1) {
                        try (var resultSet = statement.getGeneratedKeys()) {
                            while (resultSet.next()) {
                                value.setId(resultSet.getInt(1));
                            }
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
            }).orElseThrow(() -> new ApplicationException("Не указан пользователь"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Collection<User> getAll() {
        var query = "SELECT * FROM " + SCHEMA + ".\"user\"";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var entity = new User(
                                resultSet.getString("email"),
                                resultSet.getString("password"));
                        entity.setId(resultSet.getLong("id"));
                        entity.setRole(RoleType.valueOf(resultSet.getString("role")));
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

    public Optional<User> getByEmail(String email) {
        var query = "SELECT * FROM " + SCHEMA + ".\"user\" WHERE email=?";
        return Optional.ofNullable(email).map(value -> {
            try (var connection = datasource.getConnection();
                 var statement = connection.prepareStatement(query, new String[] {"id"})) {
                statement.setString(1, email);
                try (var resultSet = statement.executeQuery()) {
                    User entity = null;
                    while (resultSet.next()) {
                        entity = new User(
                                resultSet.getString("email"),
                                resultSet.getString("password"));
                        entity.setId(resultSet.getLong("id"));
                        entity.setRole(RoleType.valueOf(resultSet.getString("role")));
                    }
                    return Optional.ofNullable(entity);
                } catch (Exception e) {
                    throw new ApplicationException(e.getMessage());
                }
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        }).orElseThrow(() -> new ApplicationException("Не указан адрес электронной почты."));
    }

    public synchronized Optional<User> getById(long id) {
        var query = "SELECT * FROM " + SCHEMA + ".\"user\" WHERE id=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                User entity = null;
                while (resultSet.next()) {
                    entity = new User(
                            resultSet.getString("email"),
                            resultSet.getString("password"));
                    entity.setId(resultSet.getLong("id"));
                    entity.setRole(RoleType.valueOf(resultSet.getString("role")));
                }
                return Optional.ofNullable(entity);
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
