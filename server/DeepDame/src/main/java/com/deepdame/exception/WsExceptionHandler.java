package com.deepdame.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class WsExceptionHandler {
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleWsUnauthorizedException(WsUnauthorized e) {
        log.error("CHECKING");
        return "Error: " + e.getMessage();
    }
}
