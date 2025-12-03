package org.example.productcatalog.controller;

import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.dto.ProductDto;
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
import java.util.List;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductServletTest extends AbstractTest {
    private final ProductServlet productServlet = new ProductServlet();

    @Test
    @DisplayName("Попытка создать новый товар")
    public void givenNewProduct_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        ProductDto productDto = new ProductDto("I12", "Puma", "Flip-flop", "Shoes", 12.99);
        String productString = objectMapper.writeValueAsString(productDto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/product/create");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println(CREATED);
    }

    @Test
    @DisplayName("Печать товаров отфильтрованных по шаблону")
    public void givenCurrentUserAndProductTemplate_whenTryToList_thenReturnCorrectResult() throws IOException {
        ProductDto productDto = new ProductDto("I21", "Nike", "Sneakers", "Shoes", 120.0);
        String productString = objectMapper.writeValueAsString(productDto);
        String productList = objectMapper.writeValueAsString(List.of(productDto));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/product/list");
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
        ProductDto newProduct = new ProductDto("I11", "Puma", "Sneakers", "Shoes", 75.0);
        String productString = objectMapper.writeValueAsString(newProduct);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/product/update");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println(UPDATED);
    }

    @Test
    @DisplayName("Попытка удалить товар")
    public void givenUserAndProduct_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        ProductDto productDto = new ProductDto("I11", null, null, null, 0.0);
        String productString = objectMapper.writeValueAsString(productDto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(productString.getBytes()))));
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/product/delete");
        Mockito.when(response.getWriter()).thenReturn(writer);

        productServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(DELETED);
    }
}
