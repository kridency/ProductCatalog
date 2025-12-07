package org.example.productcatalog.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@RestController
public class UserController {
    private final CrudService<UserDto, String> service;

    @Autowired
    public UserController(CrudService<UserDto, String> service) {
        this.service = service;
    }

    @Operation(summary = "Register user",
            description = "Register new user.")
    @PostMapping(value = "/auth/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto register(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.create(data))
                .map(x -> CREATED)
                .orElse(USER_NOT_CREATED), data.getEmail());
    }

    @Operation(summary = "Update user credentials",
            description = "Updates password and role.")
    @PutMapping(value = "/identity/update")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto update(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.update(data))
                .map(value -> UPDATED)
                .orElse(USER_NOT_UPDATED), data.getEmail());
    }

    @Operation(summary = "Delete user credentials",
            description = "Deletes user credentials.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/identity/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto delete(@RequestBody UserDto data) {
        return new MessageDto(Optional.of(service.remove(data))
                .map(value -> DELETED)
                .orElse(USER_NOT_DELETED), data.getEmail());
    }

    @Operation(summary = "List users",
            description = "List users according to template.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/identity/list")
    public MessageDto list(@RequestBody UserDto data) {
        return new MessageDto(Optional.of(service.findFiltered(data))
                .filter(Predicate.not(Collection::isEmpty))
                .map(list -> {
                    try {
                        return objectMapper.writeValueAsString(list);
                    } catch (JsonProcessingException e) {
                        return e.getMessage();
                    }
                }).orElse(USER_NOT_FOUND), data.getEmail());
    }
}
