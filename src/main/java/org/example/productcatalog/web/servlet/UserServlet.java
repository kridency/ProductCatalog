package org.example.productcatalog.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserServlet extends AbstractServlet<UserDto> {

    public UserServlet() {
        this.service = new UserService(new UserRepository(null), UserMapper.getInstance());
    }

    private void login(HttpServletResponse response, UserDto data) {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(Optional.of(service.find(data.getEmail()))
                    .filter(value -> value.getPassword().equals(data.getPassword()))
                    .map(value -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        Cookie cookie = new Cookie("JSESSIONID", data.getEmail());
                        cookie.setPath("/api/v1");
                        cookie.setMaxAge(2592000);
                        response.addCookie(cookie);
                        return "Пользователь " + data.getEmail() + " успешно аутентифицирован.";
                    }).orElseGet(() -> {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return "Не удалось аутентифицировать пользователя " + data.getEmail() + ".";
                    }));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ApplicationException(e.getMessage());
        }
    }

    private void logout(HttpServletResponse response, UserDto data) {
        try (PrintWriter writer = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            Cookie cookie = new Cookie("JSESSIONID", data.getEmail());
            cookie.setPath("/api/v1");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            writer.println("Пользователь " + data.getEmail() + " успешно завершил сеанс.");
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
        String path = request.getRequestURI();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId ->
                Optional.ofNullable(service.find(sessionId.toString())).ifPresentOrElse(principal -> {
                    if (path.contains("/api/v1/administration")) {
                        if (principal.getRole().equals(RoleType.ROLE_ADMIN)) {
                            try {
                                UserDto userDto = objectMapper.readValue(
                                        reader.lines().collect(Collectors.joining()),
                                        UserDto.class);
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/list" -> list(response, userDto);
                                    default -> {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        writer.println(BAD_ENDPOINT);
                                    }
                                }
                            } catch (JsonProcessingException e) {
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                writer.println(BAD_REQUEST);
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            writer.println("Недостаточно прав доступа.");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.println(BAD_ENDPOINT);
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
        String path = request.getRequestURI();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId -> {
                if (path.contains("/api/v1/auth")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.println(SESSION_EXIST);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.println(BAD_ENDPOINT);
                }
            }, () -> {
                if (path.contains("/api/v1/auth")) {
                    try {
                        UserDto userDto = objectMapper.readValue(
                                reader.lines().collect(Collectors.joining()),
                                UserDto.class
                        );
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/create" -> create(response, userDto);
                            case "/login" -> login(response, userDto);
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    } catch (ApplicationException e) {
                        writer.println(e.getMessage());
                    } catch(JsonProcessingException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writer.println(BAD_REQUEST);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.println(BAD_ENDPOINT);
                }
            });
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        String path = request.getRequestURI();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId -> {
                if (path.contains("/api/v1/identity")) {
                    Optional.ofNullable(service.find(sessionId.toString())).ifPresentOrElse(principal -> {
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/update" -> {
                                try {
                                    UserDto userDto = objectMapper.readValue(
                                            reader.lines().collect(Collectors.joining()),
                                            UserDto.class
                                    );
                                    update(response, userDto);
                                } catch (JsonProcessingException e) {
                                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    writer.println(BAD_REQUEST);
                                }
                            }
                            default -> {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.println(BAD_ENDPOINT);
                            }
                        }
                    }, unauthorized::get);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.println(BAD_ENDPOINT);
                }
            }, unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        String path = request.getRequestURI();
        try (PrintWriter writer = response.getWriter()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).map(Objects::toString).map(service::find)
                    .ifPresentOrElse(principal -> {
                        if (path.contains("/api/v1/administration")) {
                            if (principal.getRole().equals(RoleType.ROLE_ADMIN)) {
                                String id = request.getParameter("id");
                                UserDto userDto = service.findById(id == null ? 0L : Long.parseLong(id));
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/delete" -> delete(response, userDto);
                                    default -> {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        writer.println(BAD_ENDPOINT);
                                    }
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                writer.println("Недостаточно прав доступа.");
                            }
                        } else if (path.contains("/api/v1/identity")) {
                            switch (path.substring(path.lastIndexOf('/'))) {
                                case "/logout" -> logout(response, principal);
                                case "/delete" -> delete(response, principal);
                                default -> {
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    writer.println(BAD_ENDPOINT);
                                }
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            writer.println(BAD_ENDPOINT);
                        }
                    }, unauthorized::get);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }
}
