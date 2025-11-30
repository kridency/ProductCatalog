package org.example.productcatalog.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.productcatalog.dto.UserDto;
import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserServlet extends AbstractServlet<User> {
    private final CrudService<User, String> userService;
    private final UserMapper userMapper;

    public UserServlet() {
        userService = new UserService();
        userMapper = UserMapper.getInstance();
    }

    protected void create(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            String responseText;
            if (userService.create(user) != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseText = "Пользователь " + user.getEmail() + " успешно зарегистрирован.";
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseText = "Не удалось зарегистрировать пользователя " + user.getEmail() + ".";
            }
            writer.println(responseText);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ApplicationException(e.getMessage());
        }
    }

    protected void update(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            String responseText;
            User newUser = userService.update(user);
            if (newUser != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseText = "Пользователь " + newUser.getEmail() + " успешно изменен.";
                Cookie cookie = new Cookie("JSESSIONID", user.getEmail());
                cookie.setPath("/api/v1");
                cookie.setMaxAge(2592000);
                response.addCookie(cookie);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseText = "Не удалось изменить пользователя " + user.getEmail() + ".";
            }
            writer.println(responseText);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    protected void delete(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            User oldUser = userService.remove(user);
            response.setStatus(HttpServletResponse.SC_OK);
            writer.println("Пользователь " + oldUser.getEmail() + " успешно удален.");
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/api/v1");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    private void login(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(Optional.of(userService.find(user.getEmail()))
                    .filter(value -> value.getPassword().equals(user.getPassword()))
                    .map(value -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        Cookie cookie = new Cookie("JSESSIONID", user.getEmail());
                        cookie.setPath("/api/v1");
                        cookie.setMaxAge(2592000);
                        response.addCookie(cookie);
                        return "Пользователь " + user.getEmail() + " успешно аутентифицирован.";
                    }).orElseGet(() -> {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return "Не удалось аутентифицировать пользователя " + user.getEmail() + ".";
                    }));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ApplicationException(e.getMessage());
        }
    }

    private void logout(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            writer.println("Пользователь " + user.getEmail() + " успешно завершил сеанс.");
            Cookie cookie = new Cookie("JSESSIONID", user.getEmail());
            cookie.setPath("/api/v1");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    protected void list(HttpServletResponse response, User user) {
        try (PrintWriter writer = response.getWriter()) {
            Collection<UserDto> list = userService.findFiltered(user).stream()
                    .map(userMapper::userToUserDto)
                    .toList();
            writer.println(objectMapper.writeValueAsString(list));
            response.setStatus(HttpServletResponse.SC_OK);
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
                    if (path.contains("/api/v1/administration")) {
                        if (principal.getRole().equals(RoleType.ROLE_ADMIN)) {
                            try {
                                UserDto userDto = objectMapper.readValue(
                                        reader.lines().collect(Collectors.joining()),
                                        UserDto.class);
                                User entity = userMapper.userDtoToUser(userDto);
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
        String path = request.getPathInfo();
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
                        User principal = userMapper.userDtoToUser(userDto);
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/create" -> create(response, principal);
                            case "/login" -> login(response, principal);
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
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter();
             BufferedReader reader = request.getReader()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).ifPresentOrElse(sessionId -> {
                if (path.contains("/api/v1/identity")) {
                    Optional.ofNullable(userService.find(sessionId.toString())).ifPresentOrElse(principal -> {
                        switch (path.substring(path.lastIndexOf('/'))) {
                            case "/update" -> {
                                try {
                                    User newUser = userMapper.userDtoToUser(objectMapper.readValue(
                                            reader.lines().collect(Collectors.joining()),
                                            UserDto.class
                                    ));
                                    newUser.setId(principal.getId());
                                    update(response, newUser);
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
        String path = request.getPathInfo();
        try (PrintWriter writer = response.getWriter()) {
            Supplier<Void> unauthorized = () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writer.println(UNAUTHORIZED);
                return null;
            };

            Optional.ofNullable(request.getAttribute("JSESSIONID")).map(Objects::toString).map(userService::find)
                    .ifPresentOrElse(principal -> {
                        if (path.contains("/api/v1/administration")) {
                            if (principal.getRole().equals(RoleType.ROLE_ADMIN)) {
                                String id = request.getParameter("id");
                                User user = userService.findById(id == null ? 0L : Long.parseLong(id));
                                switch (path.substring(path.lastIndexOf('/'))) {
                                    case "/delete" -> delete(response, user);
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
