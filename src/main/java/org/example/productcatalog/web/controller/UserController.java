package org.example.productcatalog.web.controller;

import jakarta.validation.Valid;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @PostMapping(value = "/auth/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto login(@RequestBody @Valid UserDto data) {
        return new MessageDto("User successfully created!", data.getEmail());
    }
}
