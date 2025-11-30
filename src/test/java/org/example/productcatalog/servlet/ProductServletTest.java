package org.example.productcatalog.servlet;

import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.service.ProductService;
import org.example.productcatalog.web.listener.RequestStream;
import org.example.productcatalog.web.listener.RequestWrapper;
import org.example.productcatalog.web.listener.ResponseWrapper;
import org.example.productcatalog.web.servlet.ProductServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static org.example.productcatalog.preset.ProductCatalogInit.objectMapper;

public class ProductServletTest extends AbstractTest {
    private static final ProductService productService = Mockito.spy(ProductService.class);
    private static final ProductMapper productMapper = Mockito.spy(ProductMapper.getInstance());
    private static final ProductServlet productServlet = Mockito.spy(ProductServlet.class);

    @Test
    @DisplayName("Попытка создать новый товар")
    public void givenNewProduct_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        ProductDto productDto = new ProductDto();
        productDto.setItem("I12");
        productDto.setBrand("Puma");
        productDto.setTitle("Flip-flop");
        productDto.setCategory("Shoes");
        productDto.setPrice(12.99);
        String productString = objectMapper.writeValueAsString(productDto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/product/create");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Товар " + productService.find(productDto.getItem()).getId() + " успешно создан.");
    }

    @Test
    @DisplayName("Печать товаров отфильтрованных по шаблону")
    public void givenCurrentUserAndProductTemplate_whenTryToList_thenReturnCorrectResult() throws IOException {
        Product product = productService.find("I11");
        String productString = objectMapper.writeValueAsString(productMapper.productToProductDto(product));
        String productList = objectMapper.writeValueAsString(productService.findFiltered(product).stream()
                .map(productMapper::productToProductDto).toList());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/product/list");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(productList);
    }

    @Test
    @DisplayName("Попытка изменить товар")
    public void givenUserAndProduct_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        Product product = productService.find("I11");
        Product newProduct = new Product("I11", "Puma", "Sneakers", "Shoes", 75.0);
        String productString = objectMapper.writeValueAsString(productMapper.productToProductDto(newProduct));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/product/update");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Товар " + product.getId() + " успешно изменен.");
    }

    @Test
    @DisplayName("Попытка удалить товар")
    public void givenUserAndProduct_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        Product product = productService.find("I11");
        String productString = objectMapper.writeValueAsString(productMapper.productToProductDto(product));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/product/delete");
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Товар " + product.getId() + " успешно удален.");
    }
}
