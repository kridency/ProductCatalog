package org.example.productcatalog.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.service.UserService;
import org.example.productcatalog.web.listener.RequestStream;
import org.example.productcatalog.web.listener.RequestWrapper;
import org.example.productcatalog.web.listener.ResponseWrapper;
import org.example.productcatalog.web.servlet.UserServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static org.example.productcatalog.preset.ProductCatalogInit.objectMapper;

public class UserServletTest extends AbstractTest {
    private static final UserService userService = Mockito.spy(UserService.class);
    private static final UserMapper userMapper = Mockito.spy(UserMapper.getInstance());
    private static final UserServlet userServlet = Mockito.spy(UserServlet.class);

    @Test
    @DisplayName("Печать пользователей отфильтрованных по шаблону")
    public void givenCurrentUserAndUserTemplate_whenTryToList_thenReturnCorrectResult() throws IOException {
        User user = userService.find("name@hostname");
        String userString = objectMapper.writeValueAsString(userMapper.userToUserDto(user));
        String userList = objectMapper.writeValueAsString(userService.findFiltered(user).stream()
                .map(userMapper::userToUserDto).toList());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("admin@hostname");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/administration/list");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(userList);
    }

    @Test
    @DisplayName("Попытка изменить пароль пользователя")
    public void givenUserAndNewPassword_whenTryToUpdate_thenReturnCorrectResult() throws IOException {
        User user = userService.find("name@hostname");
        User newUser = new User("name@hostname", "111111");
        String userString = objectMapper.writeValueAsString(userMapper.userToUserDto(newUser));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/identity/update");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println("Пользователь " + user.getEmail() + " успешно изменен.");
    }

    @Test
    @DisplayName("Попытка удалить пользователя")
    public void givenUser_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        User user = userService.find("test@hostname");
        String userString = objectMapper.writeValueAsString(userMapper.userToUserDto(user));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("test@hostname");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(request.getPathInfo()).thenReturn("/api/v1/identity/delete");
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println("Пользователь " + user.getEmail() + " успешно удален.");
    }
}
