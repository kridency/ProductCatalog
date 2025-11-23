package org.example.productcatalog.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class StopWatchAspect {
    @Around("execution(* *(..)) && within(org.example..*) && !within(org.example.aop..*)" +
            "&& !within(org.example.*Test..*) && !within(org.example.mapper..*)")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        }
        finally {
            System.out.println(joinPoint + " -> " + (System.nanoTime() - startTime) / 1000000 + " ms");
        }
    }
}
