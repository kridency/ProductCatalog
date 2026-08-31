package org.example.productcatalog.aop;

import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.InvocationRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Aspect
@Configurable(autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ExchangeAspect {
    @Autowired
    private InvocationRepository invocationRepository;

    @Transactional
    @Before(value = "execution(* *(..)) && @annotation(mapping)", argNames = "mapping")
    public void httpExchangeCheckToHandle(RequestMapping mapping) {
        var endpoints = mapping.path();
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(Object::toString).ifPresent(sessionId ->
                    invocationRepository.save(new Invocation(endpoints[0], sessionId)));
    }
}
