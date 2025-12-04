package org.example.productcatalog.web.controller;

import jakarta.validation.Valid;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final CrudService<UserDto, String> service;

    @Autowired
    public UserController(CrudService<UserDto, String> service) {
        this.service = service;
    }

    @PostMapping(value = "/auth/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto login(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.find(data.getEmail()))
                .filter(value -> value.getPassword().equals(data.getPassword()))
                .map(value -> "Пользователь " + data.getEmail() + " успешно аутентифицирован.")
                .orElseGet(() -> "Не удалось аутентифицировать пользователя " + data.getEmail() + "."), data.getEmail());
    }

    @PostMapping(value = "/auth/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto register(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.create(data))
                .map(value -> "Пользователь " + data.getEmail() + " успешно аутентифицирован.")
                .orElseGet(() -> "Не удалось аутентифицировать пользователя " + data.getEmail() + "."), data.getEmail());
    }
}
