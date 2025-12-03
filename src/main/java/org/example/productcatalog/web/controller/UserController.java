package org.example.productcatalog.web.controller;

import jakarta.validation.Valid;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/auth/login")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto login(@RequestBody @Valid UserDto data) {
        return new MessageDto("User successfully created!", data.getEmail());
    }
}
