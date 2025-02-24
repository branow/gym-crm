package dev.branow.aspects;

import dev.branow.annotations.Log;
import dev.branow.log.LogBuilder;
import dev.branow.log.LogBuilderFactory;
import dev.branow.log.UUIDProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class LoggingAspect {

    private final LogBuilderFactory logBuilderFactory;
    private final UUIDProvider uuidProvider;
    private final ApplicationContext applicationContext;

    public LoggingAspect(
            LogBuilderFactory logBuilderFactory,
            @Autowired(required = false) UUIDProvider uuidProvider,
            @Lazy ApplicationContext applicationContext
    ) {
        this.logBuilderFactory = logBuilderFactory;
        this.uuidProvider = uuidProvider;
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(log)")
    public Object logExecution(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        var logger = getLogBuilder(log, joinPoint);
        logger.started();

        try {
            Object result = joinPoint.proceed();
            logger.finished();
            return result;
        } catch (Exception exception) {
            logger.failed(exception);
            throw exception; // Rethrow the exception to preserve the stack trace
        }
    }

    private LogBuilder getLogBuilder(Log log, ProceedingJoinPoint joinPoint) {
        String source = getSource(joinPoint);
        String operation = log.value();
        Object[] args = joinPoint.getArgs();
        for (var i = 0; i < args.length; i++) {
            operation = operation.replaceAll("%" + i, args[i].toString());
        }
        return logBuilderFactory.newLogBuilder(log.level(), source, operation);
    }

    private String getSource(ProceedingJoinPoint joinPoint) {
        String method = joinPoint.getSignature().toString();

        String uuid = Optional.ofNullable(uuidProvider)
                .map((provider) -> provider.getUUID().toString())
                .map((uuidStr) -> uuidStr + ": ")
                .orElse("");

        return uuid + method;
    }

}
