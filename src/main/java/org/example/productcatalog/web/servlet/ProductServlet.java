package org.example.productcatalog.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.ProductService;
import org.example.productcatalog.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductServlet extends AbstractServlet<Product> {
    private final CrudService<User, String> userService;
    private final ProductMapper productMapper;

    public ProductServlet() {
        userService = new UserService();
        service = new ProductService();
        productMapper = ProductMapper.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
                BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId ->
                    Optional.ofNullable(userService.find(sessionId.toString())).ifPresentOrElse(principal -> {
                        try {
                            ProductDto product = objectMapper.readValue(
                                    reader.lines().collect(Collectors.joining()),
                                    ProductDto.class
                            );
                            Product entity = productMapper.fromDto(product);
                            switch (path.substring(path.lastIndexOf('/'))) {
                                case "/list" -> list(response, entity);
                                default -> {
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    writer.println(BAD_ENDPOINT);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            writer.println(BAD_REQUEST);
                        }
                    }, unauthorized::get), unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId ->
                Optional.ofNullable(userService.find(sessionId.toString())).ifPresentOrElse(principal -> {
                    try {
                        ProductDto productDto = objectMapper.readValue(
                                reader.lines().collect(Collectors.joining()),
                                ProductDto.class
                        );
                        Product entity = productMapper.fromDto(productDto);
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/create" -> create(response, entity);
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writer.println(BAD_REQUEST);
                    }
                }, unauthorized::get), unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId ->
                Optional.ofNullable(userService.find(sessionId.toString())).ifPresentOrElse(principal -> {
                    try {
                        ProductDto productDto = objectMapper.readValue(
                                reader.lines().collect(Collectors.joining()),
                                ProductDto.class
                        );
                        Product entity = productMapper.fromDto(productDto);
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/update" -> update(response, entity);
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writer.println(BAD_REQUEST);
                    }
                }, unauthorized::get), unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId ->
                Optional.ofNullable(userService.find(sessionId.toString())).ifPresentOrElse(principal -> {
                    try {
                        ProductDto productDto = objectMapper.readValue(
                                reader.lines().collect(Collectors.joining()),
                                ProductDto.class
                        );
                        Product entity = productMapper.fromDto(productDto);
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/delete" -> delete(response, entity);
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writer.println(BAD_REQUEST);
                    }
                }, unauthorized::get), unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
