package com.deepdame.aspect;

import com.sefault.redis.annotation.RedisListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RedisListenerLogAspect {
    @Around("@annotation(trace)")
    public Object logEvent(ProceedingJoinPoint joinPoint, RedisListener trace) throws Throwable {
        log.debug("Message from {} handled by method {}", trace.topic(), joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}
