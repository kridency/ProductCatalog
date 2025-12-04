package org.example.productcatalog.repository;

import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements CrudRepository<User> {
    private final DataSource datasource;
    private static final String INSERT_QUERY = "INSERT INTO \"user\" (email, password, role) VALUES (?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"user\" SET email=?, password=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"user\" WHERE email=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"user\"";
    private static final String GET_BY_EMAIL_QUERY = "SELECT * FROM \"user\" WHERE email=?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"user\" WHERE id=?";

    @Autowired
    public UserRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public User add(User user) {
        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(INSERT_QUERY, new String[] {"id"})) {
            try {
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getRole().toString());
                return setEntityId(statement, user, user::setId);
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(UPDATE_QUERY)) {
            try {
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setLong(3, user.getId());
                return setEntityId(statement, user, user::setId);
            } catch (SQLException e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public User delete(User user) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(DELETE_QUERY, new String[] {"id"})) {
            try {
                statement.setString(1, user.getEmail());
                if(statement.executeUpdate() == 1) {
                    try (var resultSet = statement.getGeneratedKeys()) {
                        while (resultSet.next()) {
                            user.setId(resultSet.getInt(1));
                        }
                        return user;
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

    @Override
    public Collection<User> getAll() {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var entity = new User();
                        entity.setEmail(resultSet.getString("email"));
                        entity.setPassword(resultSet.getString("password"));
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

    @Override
    public Optional<User> getByKey(String email) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_EMAIL_QUERY, new String[] {"id"})) {
            statement.setString(1, email);
            return getUser(statement);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public synchronized Optional<User> getById(long id) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
            statement.setLong(1, id);
            return getUser(statement);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @NonNull
    private Optional<User> getUser(PreparedStatement statement) {
        try (var resultSet = statement.executeQuery()) {
            User entity = null;
            while (resultSet.next()) {
                entity = new User();
                entity.setEmail(resultSet.getString("email"));
                entity.setPassword(resultSet.getString("password"));
                entity.setId(resultSet.getLong("id"));
                entity.setRole(RoleType.valueOf(resultSet.getString("role")));
            }
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
