package org.example.productcatalog.aop;

import com.sun.net.httpserver.HttpExchange;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class ExchangeAspect {
    private final CrudRepository<Invocation> invocationRepository;

    @Autowired
    public ExchangeAspect(CrudRepository<Invocation> invocationRepository) {
        this.invocationRepository = invocationRepository;

    }

    @After(value = "execution(* org.example.productcatalog.web.handler.AbstractHandler.handle(com.sun.net.httpserver.HttpExchange, ..))" +
            "&& args(exchange, ..)", argNames = "exchange")
    public void httpExchangeCheckToHandle(HttpExchange exchange) {
        var endpoint = exchange.getRequestURI().getPath();
        Optional.ofNullable(exchange.getAttribute("JSESSIONID")).map(Object::toString).ifPresent(sessionId -> {
            Invocation event = new Invocation();
            event.setEndpoint(endpoint);
            event.setEmail(sessionId);
            invocationRepository.add(event);});
        exchange.close();
    }
}
