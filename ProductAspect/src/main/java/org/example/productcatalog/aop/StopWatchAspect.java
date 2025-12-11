package org.example.productcatalog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.annotation.EnableXXX;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.Instant;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Configurable
@Slf4j
public class StopWatchAspect {
    private static final Logger LOGGER = Logger.getLogger(StopWatchAspect.class.getName());

    @Around(value = "execution(@org.example.productcatalog.annotation.EnableXXX * *(..)) && @annotation(enableXXX)",
            argNames = "joinPoint, enableXXX")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint, EnableXXX enableXXX) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        if (enableXXX.logArgs()) {
            LOGGER.log(Level.INFO, "Entering " + methodName + " with args: " + Arrays.toString(joinPoint.getArgs()) + "," );
        }

        long startTime = Instant.now().toEpochMilli();

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in " + methodName +": " + e.getMessage() +",");
            throw e;
        } finally {
            if (enableXXX.logExecutionTime()) {
                LOGGER.log(Level.INFO, joinPoint + " -> " + (Instant.now().toEpochMilli() - startTime) / 1_000_000 + " ms");
            }
        }
    }
}
