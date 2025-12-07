package org.example.productcatalog.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Aspect
@Component
public class ExchangeAspect {
    private final CrudRepository<Invocation> invocationRepository;

    @Autowired
    public ExchangeAspect(CrudRepository<Invocation> invocationRepository) {
        this.invocationRepository = invocationRepository;

    }

    @Before(value = "@annotation(mapping)", argNames = "mapping")
    public void httpExchangeCheckToHandle(RequestMapping mapping) {
        var endpoints = mapping.value();
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(Object::toString).ifPresent(sessionId -> {
            Invocation event = new Invocation();
            event.setEndpoint(endpoints[0]);
            event.setEmail(sessionId);
            invocationRepository.add(event);});
    }
}
