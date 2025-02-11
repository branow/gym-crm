package dev.branow.aspects;

import dev.branow.annotations.Log;
import dev.branow.log.LogBuilder;
import dev.branow.log.LogBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogBuilderFactory logBuilderFactory;

    @Around("@annotation(log)")
    public Object logExecution(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        var logger = getLogBuilder(log, joinPoint);
        logger.started();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception exception) {
            logger.failed(exception);
            throw exception; // Rethrow the exception to preserve the stack trace
        }

        logger.finished();
        return result;
    }

    private LogBuilder getLogBuilder(Log log, ProceedingJoinPoint joinPoint) {
        var source = joinPoint.getSignature().toString();
        var operation = log.value();
        var args = joinPoint.getArgs();
        for (var i = 0; i < args.length; i++) {
            operation = operation.replaceAll("%" + i, args[i].toString());
        }
        return logBuilderFactory.newLogBuilder(log.level(), source, operation);
    }
}
