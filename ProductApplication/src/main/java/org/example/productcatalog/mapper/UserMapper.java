package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Named("UserMapper")
public interface UserMapper {

    @Named("getRole")
    default RoleType getRole(RoleType role) {
        return Optional.ofNullable(role).orElse(RoleType.ROLE_USER);
    }

    UserDto toDto(User data);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    })
    User fromDto(UserDto data);

    void updateEntityFromDto(UserDto data, @MappingTarget User entity);
}
