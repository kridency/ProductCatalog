package org.example.productcatalog.service;

import jakarta.transaction.Transactional;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.util.specification.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@Service("UserService")
public class UserService implements CrudService<UserDto, String> {
    private final UserMapper mapper;
    private final CrudRepository<User> repository;

    @Autowired
    public UserService(CrudRepository<User> repository, UserMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
    }

    /**
     * Requests user account database for new record creation.
     * Main user account database record creation.
     * @param data   user account data transfer object
     *
     * @return  user account data transfer object
     */
    @Transactional
    @Override
    public UserDto create(UserDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto).map(repository::add)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    /**
     * Requests user account database to update existing record.
     * Main user account database record update method.
     * @param data   user account data transfer object
     *
     * @return  user account data transfer object
     */
    @Transactional
    @Override
    public UserDto update(UserDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto).map(repository::update)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    /**
     * Requests user account database to delete existing record.
     * Main user account database record delete method.
     * @param data  user account data transfer object
     *
     * @return  user account data transfer object
     */
    @Transactional
    @Override
    public UserDto remove(UserDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto).map(repository::delete)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    /**
     * Requests user account database for the record matching specified template dto.
     * Main user account database record receiving method.
     * @param data  sought user account email address
     *
     * @return  set of user account data transfer objects those have matched criteria
     */
    @Override
    public Collection<UserDto> findFiltered(UserDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
    }

    /**
     * Requests user account database for all records.
     * Supplementary user account database record receiving method.
     *
     * @return  set of user account data transfer objects
     */
    @Override
    public Collection<UserDto> findAll() { return repository.getAll().stream().map(mapper::toDto).toList(); }

    /**
     * Requests user account database for the record matching specified email address.
     * Supplementary user account database record receiving method.
     * @param email  sought user account email address
     *
     * @return  user account data transfer object
     */
    @Override
    public UserDto find(String email) {
        return Optional.ofNullable(email).flatMap(repository::getByKey).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    /**
     * Requests user account database for the record matching specified record id.
     * Supplementary user account database record receiving method.
     * @param id  sought user account id
     *
     * @return  user account data transfer object
     */
    @Override
    public UserDto findById(long id) {
        return repository.getById(id).map(mapper::toDto).orElse(null);
    }
}
