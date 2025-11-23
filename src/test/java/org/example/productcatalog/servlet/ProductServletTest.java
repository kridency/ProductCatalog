package org.example.productcatalog.servlet;

import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.entity.User;
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
import java.math.BigDecimal;
import java.time.Instant;

import static org.example.productcatalog.preset.ProductCatalogInit.objectMapper;

public class ProductServletTest extends AbstractTest {
    private static final ProductService productService = Mockito.spy(ProductService.getInstance());

    @Test
    @DisplayName("Печать транзакций отфильтрованных по шаблону")
    void givenCurrentUserAndTransaction_whenTryToListWithTemplate_thenReturnCorrectResult() throws IOException {
        User user = userService.findByEmail("name@hostname");
        String transactionList = objectMapper.writeValueAsString(productService.findAllByUser(user).stream()
                .map(value -> ProductMapper.getInstance().transactionToTransactionDto(value)).toList());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("DBAD912E791C54015954BC519E8EEFA925F91945");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/list");
        Mockito.when(response.getWriter()).thenReturn(writer);

        ProductServlet.getInstance().doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(transactionList);
    }

    @Test
    @DisplayName("Попытка изменить транзакцию")
    void givenUserAndTransaction_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        User user = userService.findByEmail("name@hostname");
        Product transaction = productService
                .findByDateAndUser(Instant.parse("2024-12-12T12:00:00.00Z"), user);
        Product newTransaction = new Transaction(
                TransactionType.DEPOSIT,
                "Interest",
                new BigDecimal("50"),
                "Receive income from investment", user);
        newTransaction.setDate(transaction.getDate());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("DBAD912E791C54015954BC519E8EEFA925F91945");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(
                        TransactionMapper.getInstance().transactionToTransactionDto(newTransaction)).getBytes()
                )
        )));
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/update");
        Mockito.when(response.getWriter()).thenReturn(writer);

        TransactionServlet.getInstance().doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Транзакция " + transaction.getId() + " успешно изменена.");
    }

    @Test
    @DisplayName("Попытка удалить транзакцию")
    void givenUserAndTransaction_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        User user = userService.findByEmail("name@hostname");
        Transaction transaction = transactionService
                .findByDateAndUser(Instant.parse("2024-12-18T12:00:00.00Z"), user);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("DBAD912E791C54015954BC519E8EEFA925F91945");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(
                new RequestStream(objectMapper.writeValueAsString(
                        TransactionMapper.getInstance().transactionToTransactionDto(transaction)).getBytes()
                )
        )));
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/transaction/delete");
        Mockito.when(response.getWriter()).thenReturn(writer);

        TransactionServlet.getInstance().doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Транзакция " + transaction.getId() + " успешно удалена.");
    }
}
