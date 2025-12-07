package org.example.productcatalog.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import static org.example.productcatalog.preset.ProductCatalogInit.*;
import static org.example.productcatalog.preset.ProductCatalogInit.USER_NOT_FOUND;

@RestController
public class ProductController {
    private final CrudService<ProductDto, String> service;

    @Autowired
    public ProductController(CrudService<ProductDto, String> service) {
        this.service = service;
    }

    @Operation(summary = "Register product",
            description = "Register new product.")
    @PostMapping(value = "/product/create")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto register(@RequestBody @Valid ProductDto data) {
        return new MessageDto(Optional.of(service.create(data))
                .map(x -> CREATED)
                .orElse(PRODUCT_NOT_CREATED), data.getItem());
    }

    @Operation(summary = "Update product details",
            description = "Updates product details.")
    @PutMapping(value = "/product/update")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto update(@RequestBody @Valid ProductDto data) {
        return new MessageDto(Optional.of(service.update(data))
                .map(value -> UPDATED)
                .orElse(PRODUCT_NOT_UPDATED), data.getItem());
    }

    @Operation(summary = "Delete product",
            description = "Deletes product.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/product/delete")
    public MessageDto delete(@RequestBody ProductDto data) {
        return new MessageDto(Optional.of(service.remove(data))
                .map(value -> DELETED)
                .orElse(USER_NOT_DELETED), data.getItem());
    }

    @Operation(summary = "List products",
            description = "List products according to template.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/product/list")
    public MessageDto list(@RequestBody ProductDto data) {
        return new MessageDto(Optional.of(service.findFiltered(data))
                .filter(Predicate.not(Collection::isEmpty))
                .map(list -> {
                    try {
                        return objectMapper.writeValueAsString(list);
                    } catch (JsonProcessingException e) {
                        return e.getMessage();
                    }
                }).orElse(USER_NOT_FOUND), data.getItem());
    }
}
