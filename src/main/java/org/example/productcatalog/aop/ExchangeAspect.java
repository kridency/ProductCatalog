package org.example.productcatalog.aop;

import com.sun.net.httpserver.HttpExchange;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.repository.InvocationRepository;
import org.example.productcatalog.service.UserService;

import jakarta.servlet.http.Cookie;
import java.util.Collections;
import java.util.Optional;
import java.util.Vector;

@Aspect
public class ExchangeAspect {
    private final UserService userService = UserService.getInstance();
    private final InvocationRepository invocationRepository = InvocationRepository.getInstance();

    @Around(value = "execution(* org.example.productcatalog.web.handler.AbstractHandler.handle(com.sun.net.httpserver.HttpExchange, ..))" +
            "&& args(exchange, ..)", argNames = "pjp, exchange")
    public Object httpExchangeCheckToHandle(ProceedingJoinPoint pjp, HttpExchange exchange) throws Throwable {
        User user;
        String endpoint = exchange.getRequestURI().getPath();
        String sessionId = Optional.ofNullable(exchange.getRequestHeaders().get("Cookie"))
                .map(headers -> new Vector<>(headers).elements()).flatMap(headers -> Collections.list(headers).stream()
                        .map(header -> {
                            String[] cookie = header.split("=");
                            return new Cookie(cookie[0], cookie[1]);
                        }).filter(cookie -> cookie.getName().equals("JSESSIONID"))
                        .map(Cookie::getValue).reduce((a, b) -> b)).orElse(null);
        if (sessionId != null && (user = userService.findByEmail(sessionId)) != null) {
            invocationRepository.add(new Invocation(endpoint, user));
        }
        return pjp.proceed(pjp.getArgs());
    }
}
