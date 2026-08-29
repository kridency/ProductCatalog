package org.example.productcatalog.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.service.CrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@RestController
@Path("/product")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {
    private final CrudService<ProductDto, String> service;
    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageSize;

    @Inject
    public ProductController(@Qualifier("ProductService") CrudService<ProductDto, String> service) { this.service = service; }

    @POST
    @Operation(summary = "Register product",
            description = "Register new product.")
    @RequestMapping(method = RequestMethod.POST, path = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto register(@RequestBody @Valid ProductDto data) {
        return new MessageDto(Optional.of(service.create(data))
                .map(x -> CREATED)
                .orElse(PRODUCT_NOT_CREATED), data.getItem());
    }

    @PUT
    @Operation(summary = "Update product details",
            description = "Updates product details.")
    @RequestMapping(method = RequestMethod.PUT, path = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageDto update(@RequestBody @Valid ProductDto data) {
        return new MessageDto(Optional.of(service.update(data))
                .map(value -> UPDATED)
                .orElse(PRODUCT_NOT_UPDATED), data.getItem());
    }

    @DELETE
    @Operation(summary = "Delete product",
            description = "Deletes product.")
    @RequestMapping(method = RequestMethod.DELETE, path = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public MessageDto delete(@RequestBody ProductDto data) {
        return new MessageDto(Optional.of(service.remove(data))
                .map(value -> DELETED)
                .orElse(PRODUCT_NOT_DELETED), data.getItem());
    }

    @GET
    @Operation(summary = "List products",
            description = "List products according to template.")
    @RequestMapping(method = RequestMethod.GET, path = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageDto list(@RequestBody(required = false) ProductDto data) {
        return new MessageDto(Optional.of(service.findFiltered(Optional.ofNullable(data)
                .map(x -> Map.of("item", data.getItem())).orElse(Map.of()), PageRequest.of(0, pageSize)))
                .filter(Predicate.not(Slice::isEmpty))
                .map(list -> {
                    try {
                        return objectMapper.writeValueAsString(list);
                    } catch (JsonProcessingException e) {
                        return e.getMessage();
                    }
                }).orElse(PRODUCT_NOT_FOUND), Optional.ofNullable(data).map(ProductDto::getItem).orElse(null));
    }
}
