package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Named("UserMapper")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Named("getUserMapper")
    static UserMapper getInstance() {
        return INSTANCE;
    }

    @Named("getRole")
    default RoleType getRole(RoleType role) {
        return Optional.ofNullable(role).orElse(RoleType.ROLE_USER);
    }

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role")
    })
    UserDto toDto(User data);

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    })
    User fromDto(UserDto data);
}
