package org.example.productcatalog.mapper;

import javax.annotation.processing.Generated;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T04:24:45+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Red Hat, Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User data) {
        if ( data == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setEmail( data.getEmail() );
        userDto.setPassword( data.getPassword() );
        userDto.setRole( data.getRole() );

        return userDto;
    }

    @Override
    public User fromDto(UserDto data) {
        if ( data == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( data.getEmail() );
        user.setPassword( data.getPassword() );
        user.setRole( getRole( data.getRole() ) );

        return user;
    }
}
