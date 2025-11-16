package org.example.productcatalog.repository;

import org.example.productcatalog.client.PostgreSQLClient;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class InvocationRepository implements CrudRepository<Invocation> {
    private static final InvocationRepository INSTANCE = new InvocationRepository();
    private final UserRepository userRepository;
    private final PGConnectionPoolDataSource datasource;
    private static String SCHEMA;

    private InvocationRepository() {
        userRepository = UserRepository.getInstance();
        datasource = PostgreSQLClient.getInstance().getDataSource();
        SCHEMA = datasource.getCurrentSchema();
    }

    public static InvocationRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Invocation add(Invocation invocation) {
        var query = "INSERT INTO " + SCHEMA + ".\"invocation\" (date, endpoint, user_id) " + "VALUES (?,?,?)";

        try(var connection = datasource.getConnection();
            var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(invocation).map(value -> {
                try {
                    statement.setTimestamp(1, Timestamp.from(value.getDate()));
                    statement.setString(2, value.getEndpoint());
                    statement.setLong(3, value.getUser().getId());
                    return getEntity(statement, value, value::setId);
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
        var query = "UPDATE " + SCHEMA + ".\"invocation\" SET endpoint=? WHERE id=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query, new String[] {"id"})) {
            return Optional.ofNullable(invocation).map(value -> {
                try {
                    statement.setString(1, value.getEndpoint());
                    statement.setLong(2, value.getId());
                    return getEntity(statement, value, value::setId);
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
        var query = "DELETE FROM " + SCHEMA + ".\"invocation\" WHERE id=?";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query)) {
            return Optional.ofNullable(invocation).map(value -> {
                try {
                    statement.setLong(1, value.getId());
                    if (statement.executeUpdate() == 1) {
                        try (var resultSet = statement.getGeneratedKeys()) {
                            while (resultSet.next()) {
                                value.setId(resultSet.getInt(1));
                            }
                            value.setDate(resultSet.getTimestamp("date").toInstant());
                            value.setEndpoint(resultSet.getString("endpoint"));
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
            }).orElseThrow(() -> new ApplicationException("Не указана транзакция"));
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Collection<Invocation> getAll() {
        var query = "SELECT * FROM " + SCHEMA + ".\"invocation\"";

        try (var connection = datasource.getConnection();
             var statement = connection.prepareStatement(query);
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
        var query = "SELECT * FROM " + SCHEMA + ".\"invocation\" WHERE date=? AND user_id=?";

        return Optional.ofNullable(instant).map(date ->
                Optional.ofNullable(user).map(principal -> {
                    try (var connection = datasource.getConnection();
                         var statement = connection.prepareStatement(query)) {
                        String timestamp = date.atOffset(ZoneOffset.UTC)
                                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS"));
                        statement.setString(1, timestamp);
                        statement.setLong(2, principal.getId());
                        try (ResultSet resultSet = statement.executeQuery()) {
                            Invocation entity = null;
                            while (resultSet.next()) {
                                entity = new Invocation(
                                        resultSet.getString("endpoint"),
                                        principal
                                );
                                entity.setId(resultSet.getLong("id"));
                                entity.setDate(resultSet.getTimestamp("date")
                                        .toLocalDateTime().toInstant(ZoneOffset.UTC));
                            }
                            return entity;
                        } catch (Exception e) {
                            throw new ApplicationException(e.getMessage());
                        }
                    } catch (Exception e) {
                        throw new ApplicationException(e.getMessage());
                    }
                })
        ).orElseThrow(() -> new ApplicationException("Не указана дата"));
    }
}
