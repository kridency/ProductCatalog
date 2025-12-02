package org.example.productcatalog.aop;

import com.sun.net.httpserver.HttpExchange;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.repository.InvocationRepository;

import java.util.Optional;

@Aspect
public class ExchangeAspect {
    private final CrudRepository<Invocation> invocationRepository = new InvocationRepository();

    @After(value = "execution(* org.example.productcatalog.web.handler.AbstractHandler.handle(com.sun.net.httpserver.HttpExchange, ..))" +
            "&& args(exchange, ..)", argNames = "exchange")
    public void httpExchangeCheckToHandle(HttpExchange exchange) {
        var endpoint = exchange.getRequestURI().getPath();
        Optional.ofNullable(exchange.getAttribute("JSESSIONID")).map(Object::toString).ifPresent(sessionId ->
                invocationRepository.add(new Invocation(endpoint, sessionId)));
        exchange.close();
    }
}
