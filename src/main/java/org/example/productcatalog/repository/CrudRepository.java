package org.example.productcatalog.repository;

import org.example.productcatalog.exception.ApplicationException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public interface CrudRepository<T> {
    T add(T t);
    T update(T t);

    T delete(T entity);

    Collection<T> getAll();

    Optional<T> getById(long id);

    default T setEntityId(PreparedStatement statement, T entity, Consumer<Long> consumer) throws SQLException {
        if (statement.executeUpdate() == 1) {
            try (var resultSet = statement.getGeneratedKeys()) {
                while (resultSet.next()) {
                    consumer.accept(resultSet.getLong(1));
                }
                return entity;
            } catch (Exception e) {
                throw new ApplicationException(e.getMessage());
            }
        } else {
            return null;
        }
    }
}