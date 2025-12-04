package org.example.productcatalog.service;

import lombok.NonNull;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.util.specification.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@Service
public class UserService implements CrudService<UserDto, String>, UserDetailsService {
    private final UserMapper mapper;
    private final CrudRepository<User> repository;

    @Autowired
    public UserService(CrudRepository<User> repository, UserMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
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

    /**
     * Converts user account database record description object to spring security object.
     * Overloaded method for receiving spring security object.
     * @param username  email address of the user requested for authentication
     *
     * @return  spring security user account description object
     */
    @Override
    @NonNull
    public UserDto loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return repository.getByKey(username).map(mapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Email is: " + username));
    }
}
