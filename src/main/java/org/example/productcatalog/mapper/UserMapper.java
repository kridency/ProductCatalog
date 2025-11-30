package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserService.class})
@Named("UserMapper")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Named("getUserMapper")
    static UserMapper getInstance() {
        return INSTANCE;
    }

    @Named("getUserId")
    default long getUserId(UserDto data) {
        try {
            return new UserService().find(data.getEmail()).getId();
        } catch (ApplicationException e) {
            return 0L;
        }
    }

    @Named("getRole")
    default RoleType getRole(UserDto data) {
        try {
            return new UserService().find(data.getEmail()).getRole();
        } catch (ApplicationException e) {
            return RoleType.ROLE_USER;
        }
    }

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password")
    })
    UserDto toDto(User data);

    @Mappings({
            @Mapping(target = "id", expression = "java(getUserId(data))", dependsOn = {"email"}),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(target = "role", expression = "java(getRole(data))", dependsOn = {"email"})
    })
    User fromDto(UserDto data);
}
