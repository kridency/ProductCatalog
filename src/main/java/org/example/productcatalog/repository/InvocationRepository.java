package org.example.productcatalog.repository;

import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.example.productcatalog.preset.ProductCatalogInit.DATETIME_FORMATTER;

public class InvocationRepository implements CrudRepository<Invocation> {
    private static final InvocationRepository INSTANCE = new InvocationRepository();
    private final CrudRepository<User> userRepository;
    private final DataSource datasource;
    private static final String INSERT_QUERY = "INSERT INTO \"invocation\" (date, endpoint, user_id) VALUES (?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE \"invocation\" SET endpoint=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM \"invocation\" WHERE id=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM \"invocation\"";
    private static final String GET_BY_KEY_QUERY = "SELECT * FROM \"invocation\" WHERE date=? AND user_id=?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM \"invokation\" WHERE id=?";

    private InvocationRepository() {
        userRepository = UserRepository.getInstance();
        datasource = PostgreSQLClient.getInstance().getDataSource();
    }

    public static InvocationRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Invocation add(Invocation invocation) {

        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(INSERT_QUERY, new String[] {"id"})) {
            return Optional.ofNullable(invocation).map(value -> {
                try {
                    statement.setTimestamp(1, Timestamp.from(value.getDate()));
                    statement.setString(2, value.getEndpoint());
                    statement.setLong(3, value.getUser().getId());
                    return setEntityId(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан вызов"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Invocation update(Invocation invocation) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(UPDATE_QUERY, new String[] {"id"})) {
            return Optional.ofNullable(invocation).map(value -> {
                try {
                    statement.setString(1, value.getEndpoint());
                    statement.setLong(2, value.getId());
                    return setEntityId(statement, value, value::setId);
                } catch (SQLException e) {
                    throw new ApplicationException(e.getMessage());
                }
            }).orElseThrow(() -> new ApplicationException("Не указан вызов"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Invocation delete(Invocation invocation) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(DELETE_QUERY)) {
            try {
                statement.setLong(1, invocation.getId());
                if (statement.executeUpdate() == 1) {
                    try (var resultSet = statement.getGeneratedKeys()) {
                        while (resultSet.next()) {
                            invocation.setId(resultSet.getInt(1));
                        }
                        invocation.setDate(resultSet.getTimestamp("date").toInstant());
                        invocation.setEndpoint(resultSet.getString("endpoint"));
                        return invocation;
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
    public Collection<Invocation> getAll() {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_ALL_QUERY);
             var resultSet = statement.executeQuery()) {
            return Stream.generate(() -> {
                try {
                    if (resultSet.next()) {
                        var entity = new Invocation(
                                resultSet.getString("endpoint"),
                                userRepository.getById(resultSet.getLong("user_id")).orElse(null));
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

    public Optional<Invocation> getByDateAndUser(Instant instant, User user) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_KEY_QUERY)) {
            String timestamp = instant.atOffset(ZoneOffset.UTC).format(DATETIME_FORMATTER);
            statement.setString(1, timestamp);
            statement.setLong(2, user.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                Invocation entity = null;
                while (resultSet.next()) {
                    entity = new Invocation(resultSet.getString("endpoint"), user);
                    entity.setId(resultSet.getLong("id"));
                    entity.setDate(resultSet.getTimestamp("date").toInstant());
                }
                return Optional.ofNullable(entity);
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public Optional<Invocation> getById(long id) {
        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(GET_BY_ID_QUERY)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                Invocation entity = null;
                while (resultSet.next()) {
                    var user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
                    entity = new Invocation(resultSet.getString("endpoint"), user);
                    entity.setId(resultSet.getLong("id"));
                    entity.setDate(resultSet.getTimestamp("date").toInstant());
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
