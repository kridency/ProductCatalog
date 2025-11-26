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

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductServlet extends HttpServlet {
    private final CrudService<User, String> userService;
    private final CrudService<Product, String> productService;
    private final ProductMapper productMapper;

    public ProductServlet() {
        userService = new UserService();
        productService = new ProductService();
        productMapper = ProductMapper.getInstance();
    }

    private void create(HttpServletResponse response, Product product) {
        try (PrintWriter writer = response.getWriter()) {
            String responseText, overdraft = "";

            Product newEntity = productService.create(product);
            if (newEntity != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseText = "Транзакция " + newEntity.getId() + " успешно создана." + overdraft;
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseText = PRODUCT_NOT_CREATED;
            }
            writer.println(responseText);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ApplicationException(e.getMessage());
        }
    }

    private void list(HttpServletResponse response, Product product) {
        try (PrintWriter writer = response.getWriter()) {
            Collection<ProductDto> list = productService.findFiltered(product).stream()
                    .map(productMapper::productToProductDto)
                    .toList();
            writer.println(objectMapper.writeValueAsString(list));
            writer.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    private void update(HttpServletResponse response, Product product) {
        try (PrintWriter writer = response.getWriter()) {
            String responseText;
            Product newEntity = productService.update(product);
            if (newEntity != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseText = "Товар " + product.getId() + " успешно изменен.";
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseText = PRODUCT_NOT_UPDATED;
            }
            writer.println(responseText);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    private void delete(HttpServletResponse response, Product transaction) {
        try (PrintWriter writer = response.getWriter()) {
            Product newEntity = productService.remove(transaction);
            response.setStatus(HttpServletResponse.SC_OK);
            writer.println("Товар " + newEntity.getId() + " успешно удален.");
            writer.flush();
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
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
                            Product entity = productMapper.productDtoToProduct(product);
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
                        Product entity = productMapper.productDtoToProduct(productDto);
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
                        Product entity = productMapper.productDtoToProduct(productDto);
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
                        Product entity = productMapper.productDtoToProduct(productDto);
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
