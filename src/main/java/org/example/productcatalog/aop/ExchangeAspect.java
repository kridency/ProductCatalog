package org.example.productcatalog.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Aspect
@Configurable(autowire= Autowire.BY_TYPE, preConstruction = true, dependencyCheck = true)
public class ExchangeAspect {
    @Autowired
    private CrudRepository<Invocation> invocationRepository;

    @After(value = "@annotation(mapping)", argNames = "mapping")
    public void httpExchangeCheckToHandle(RequestMapping mapping) {
        var endpoints = mapping.path();
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(Object::toString).ifPresent(sessionId -> {
                    Invocation event = new Invocation();
                    event.setEndpoint(endpoints[0]);
                    event.setEmail(sessionId);
                    invocationRepository.add(event);
                });
    }
}
