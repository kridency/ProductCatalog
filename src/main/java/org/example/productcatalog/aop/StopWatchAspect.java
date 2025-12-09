package org.example.productcatalog.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Configurable
public class StopWatchAspect {
    protected final Logger LOGGER = Logger.getLogger(StopWatchAspect.class.getName());

    @Around("execution(* *(..)) && within(org.example.productcatalog..*) " +
            "&& !within(org.example.productcatalog.aop..*)" +
            "&& !within(org.example.productcatalog.mapper..*)")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = Instant.now().toEpochMilli();
        try {
            return joinPoint.proceed();
        }
        finally {
            LOGGER.log(Level.INFO, joinPoint + " -> " + (Instant.now().toEpochMilli() - startTime) / 1000000 + " ms");
        }
    }
}
