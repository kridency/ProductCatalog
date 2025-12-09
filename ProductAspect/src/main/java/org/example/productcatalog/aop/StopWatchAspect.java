package org.example.productcatalog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.productcatalog.annotation.StopWatch;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.Instant;
import java.util.Arrays;

@Aspect
@Configurable
@Slf4j
public class StopWatchAspect {
    @Around("@annotation(stopWatch)")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint, StopWatch stopWatch) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        if (stopWatch.logArgs()) {
            log.info("Entering {} with args: {}", methodName, Arrays.toString(joinPoint.getArgs()));
        }

        long startTime = Instant.now().toEpochMilli();

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Exception in {}: {}", methodName, e.getMessage());
            throw e;
        } finally {
            if (stopWatch.logExecutionTime()) {
                log.info("{} -> {} ms", joinPoint, (Instant.now().toEpochMilli() - startTime) / 1_000_000);
            }
        }
    }
}
