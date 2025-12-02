package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserService.class})
@Named("UserMapper")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Named("getUserMapper")
    static UserMapper getInstance() {
        return INSTANCE;
    }

    @Named("getUserId")
    default long getUserId(String email, @Context CrudRepository<User> repository) {
        return repository.getByKey(email).map(User::getId).orElse(0L);
    }

    @Named("getRole")
    default RoleType getRole(RoleType role) {
        return Optional.ofNullable(role).orElse(RoleType.ROLE_USER);
    }

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password")
    })
    UserDto toDto(User data);

    @Mappings({
            @Mapping(source = "email", target = "id", qualifiedByName = "getUserId"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    })
    User fromDto(UserDto data, @Context CrudRepository<User> repository);
}
