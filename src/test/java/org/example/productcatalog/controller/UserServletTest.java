package org.example.productcatalog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.UserService;
import org.example.productcatalog.web.listener.RequestStream;
import org.example.productcatalog.web.listener.RequestWrapper;
import org.example.productcatalog.web.listener.ResponseWrapper;
import org.example.productcatalog.web.controller.UserServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserServletTest extends AbstractTest {
    @Mock
    private CrudService<UserDto, String> userService;

    private static final UserMapper userMapper = Mockito.spy(UserMapper.getInstance());

    @Autowired
    private UserServlet userServlet;

    @Test
    @DisplayName("Попытка создать нового пользователя")
    public void givenNewUserCredentials_whenTryToCreate_thenReturnCorrectResult() throws IOException {
        UserDto userDto = new UserDto();
        userDto.setEmail("new@hostname");
        userDto.setPassword("111111");
        String userString = objectMapper.writeValueAsString(userDto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/auth/create");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println(CREATED);
    }

    @Test
    @DisplayName("Печать пользователей отфильтрованных по шаблону")
    public void givenCurrentUserAndUserTemplate_whenTryToList_thenReturnCorrectResult() throws IOException {
        var userDto = userService.find("name@hostname");
        String userString = objectMapper.writeValueAsString(userDto);
        String userList = objectMapper.writeValueAsString(userService.findFiltered(userDto).stream().toList());

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("admin@hostname");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/administration/list");
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
        User newUser = new User();
        newUser.setEmail("name@hostname");
        newUser.setPassword("111111");
        String userString = objectMapper.writeValueAsString(userMapper.toDto(newUser));

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("name@hostname");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/identity/update");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(writer).println(UPDATED);
    }

    @Test
    @DisplayName("Попытка удалить пользователя")
    public void givenUser_whenTryToDelete_thenReturnCorrectResult() throws IOException {
        var userDto = userService.find("test@hostname");
        String userString = objectMapper.writeValueAsString(userDto);

        final PrintWriter writer = Mockito.mock(PrintWriter.class);
        HttpServletResponse response = Mockito.mock(ResponseWrapper.class);
        HttpServletRequest request = Mockito.mock(RequestWrapper.class);

        Mockito.when(request.getAttribute("JSESSIONID")).thenReturn("test@hostname");
        Mockito.when(request.getReader())
                .thenReturn(new BufferedReader(new InputStreamReader(new RequestStream(userString.getBytes()))));
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/identity/delete");
        Mockito.when(response.getWriter()).thenReturn(writer);

        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(writer).println(DELETED);
    }
}
