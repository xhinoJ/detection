package org.thfabric.threatmanagement.aop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Slf4j
public class ControllerExceptionHandling {

    @Around("within(org.thfabric.threatmanagement.controller.*)")
    @SneakyThrows
    public Object logAroundExec(ProceedingJoinPoint pjp) {
        log.info("before {}", constructLogMsg(pjp));
        var proceed = pjp.proceed();
        log.info("after {} wiht result: {}", constructLogMsg(pjp), proceed.toString());
        return proceed;
    }

    private String constructLogMsg(JoinPoint jp) {
        var args = Arrays.stream(jp.getArgs()).map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        return "@" + method.getName() +
                ":" +
                args;
    }

    @AfterThrowing(pointcut = "within(org.thfabric.threatmanagement.service.*)", throwing = "e")
    public Object logAfterException(JoinPoint jp, Exception e) {
        log.error("Exception during: {} with ex: {}", constructLogMsg(jp), e.toString());

        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
