package org.example.productcatalog.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@RestController
@Path("/identity")
@Tag(name = "User Controller", description = "APIs for managing users")
public class UserController {
    private final CrudService<UserDto, String> service;
    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageSize;

    @Inject
    public UserController(UserService service) { this.service = service; }

    @POST
    @Operation(summary = "Register user",
            description = "Register new user.")
    @RequestMapping(method = RequestMethod.POST, path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto register(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.create(data))
                .map(x -> CREATED)
                .orElse(USER_NOT_CREATED), data.getEmail());
    }

    @PUT
    @Operation(summary = "Update user credentials",
            description = "Updates password and role.")
    @RequestMapping(method = RequestMethod.PUT, path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageDto update(@RequestBody @Valid UserDto data) {
        return new MessageDto(Optional.of(service.update(data))
                .map(value -> UPDATED)
                .orElse(USER_NOT_UPDATED), data.getEmail());
    }

    @DELETE
    @Operation(summary = "Delete user credentials",
            description = "Deletes user credentials.")
    @RequestMapping(method = RequestMethod.DELETE, path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto delete(@RequestBody UserDto data) {
        return new MessageDto(Optional.of(service.remove(data))
                .map(value -> DELETED)
                .orElse(USER_NOT_DELETED), data.getEmail());
    }

    @GET
    @Operation(summary = "List users",
            description = "List users according to template.")
    @RequestMapping(method = RequestMethod.GET, path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto list(@RequestBody(required = false) UserDto data) {
        return new MessageDto(Optional.of(service.findFiltered(Optional.ofNullable(data)
                        .map(x -> Map.of("email", data.getEmail())).orElse(Map.of()), PageRequest.of(0, pageSize)))
                .filter(Predicate.not(Slice::isEmpty))
                .map(list -> {
                    try {
                        return objectMapper.writeValueAsString(list);
                    } catch (JsonProcessingException e) {
                        return e.getMessage();
                    }
                }).orElse(USER_NOT_FOUND), "");
    }
}
