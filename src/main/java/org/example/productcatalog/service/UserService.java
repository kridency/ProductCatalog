package org.example.productcatalog.service;

import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.util.specification.Specification;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserService implements CrudService<UserDto, String> {
    private final UserMapper mapper;
    private final CrudRepository<User> repository;

    public UserService() {
        mapper = UserMapper.getInstance();
        repository = new UserRepository();
    }

    @Override
    public UserDto create(UserDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository)).map(repository::add)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public UserDto update(UserDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository)).map(repository::update)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public UserDto remove(UserDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository)).map(repository::delete)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public Collection<UserDto> findAll() { return repository.getAll().stream().map(mapper::toDto).toList(); }

    @Override
    public Collection<UserDto> findFiltered(UserDto data) {
        return new Specification<>(mapper.fromDto(data, repository)).apply(repository.getAll()).stream()
                .map(mapper::toDto).toList();
    }

    @Override
    public UserDto find(String email) {
        return Optional.ofNullable(email).flatMap(repository::getByKey).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    @Override
    public UserDto findById(long id) {
        return repository.getById(id).map(mapper::toDto).orElse(null);
    }
}
