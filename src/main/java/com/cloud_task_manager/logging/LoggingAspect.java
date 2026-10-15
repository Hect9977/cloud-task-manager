package com.cloud_task_manager.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cloud_task_manager.exception.TaskNotFoundException;

@Aspect
@Component
@Order(0)
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("(execution(public * com.cloud_task_manager..*(..)) || " +
            "(this(org.springframework.data.repository.Repository) && " +
            "execution(public * *(..)))) && " +
            "!within(com.cloud_task_manager.logging..*)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.nanoTime();
        String outcome = "SUCCESS";

        log.debug(
                "event=ENTER class={} method={}",
                className, methodName);

        try {
            return joinPoint.proceed();

        } catch (TaskNotFoundException exception) {
            outcome = "FAILURE";

            log.warn(
                    "event=EXCEPTION class={} method={} "
                            + "type=TaskNotFoundException message={}",
                    className, methodName, exception.getMessage());

            throw exception;

        } catch (Throwable exception) {
            outcome = "FAILURE";

            log.error(
                    "event=EXCEPTION class={} method={} "
                            + "message=Method execution failed",
                    className, methodName, exception);

            throw exception;

        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;

            log.debug(
                    "event=EXIT class={} method={} "
                            + "outcome={} durationMs={}",
                    className, methodName, outcome, durationMs);
        }
    }
}