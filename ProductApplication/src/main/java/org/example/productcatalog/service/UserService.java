package org.example.productcatalog.service;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.util.specification.GetSpecification;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@Service
public class UserService implements CrudService<UserDto, String> {
    private final UserMapper mapper;
    private final UserRepository repository;

    @Inject
    public UserService(UserRepository repository, UserMapper mapper) {
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
        return Optional.ofNullable(data).map(UserDto::getEmail).flatMap(repository::getByKey)
                .map(x -> {
                    mapper.updateEntityFromDto(data, x);
                    return repository.update(x);
                }).map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
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
        return Optional.ofNullable(data).map(UserDto::getEmail).flatMap(repository::getByKey).map(repository::delete)
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    /**
     * Requests user account database for the record matching specified template dto.
     * Main user account database record receiving method.
     * @param criteria   set of sought values for filter attributes
     * @param pageable  product list pagination criteria object
     *
     * @return  set of user account data transfer objects those have matched criteria
     */
    @Override
    public Slice<UserDto> findFiltered(Map<String, ? extends Comparable<?>> criteria, Pageable pageable) {
        List<UserDto> result = repository.findAll(new GetSpecification<>(criteria), pageable).stream()
                .map(mapper::toDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Requests user account database for all records.
     * Supplementary user account database record receiving method.
     *
     * @return  set of user account data transfer objects
     */
    @Override
    public Collection<UserDto> findAll() {
        return repository.findAll(new GetSpecification<>(Map.of()), PageRequest.of(0, 20)).stream()
                .map(mapper::toDto).toList();
    }

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
