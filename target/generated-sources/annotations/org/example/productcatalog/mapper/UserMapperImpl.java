package org.example.productcatalog.mapper;

import javax.annotation.processing.Generated;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.repository.CrudRepository;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T22:51:35+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Red Hat, Inc.)"
)
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
    public User fromDto(UserDto data, CrudRepository<User> repository) {
        if ( data == null ) {
            return null;
        }

        User user = new User();

        user.setId( getUserId( data.getEmail(), repository ) );
        user.setEmail( data.getEmail() );
        user.setPassword( data.getPassword() );
        user.setRole( getRole( data.getRole() ) );

        return user;
    }
}
