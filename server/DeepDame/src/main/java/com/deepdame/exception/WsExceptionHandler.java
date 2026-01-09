package com.deepdame.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class WsExceptionHandler {
    @MessageExceptionHandler(WsUnauthorized.class)
    @SendToUser("/queue/errors")
    public String handleWsUnauthorizedException(WsUnauthorized e) {
        return "Error: " + e.getMessage();
    }

    @MessageExceptionHandler(IllegalStateException.class)
    @SendToUser("/queue/errors")
    public String handleIllegalState(IllegalStateException e) {
        return "BUSINESS_ERROR: " + e.getMessage();
    }

    @MessageExceptionHandler(NotFoundException.class)
    @SendToUser("/queue/errors")
    public String handleNotFound(NotFoundException e) {
        return "NOT_FOUND: " + e.getMessage();
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public String handleGeneralException(Exception e) {
        log.error("Unhandled WebSocket Exception: ", e);
        return "SERVER_ERROR: An internal error occurred.";
    }

    @MessageExceptionHandler(IllegalMoveException.class)
    @SendToUser("/queue/errors")
    public String handleIllegalMove(IllegalMoveException e) {
        return "INVALID_MOVE: " + e.getMessage();
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public String handleIllegalArgument(IllegalArgumentException e) {
        return "BAD_REQUEST: " + e.getMessage();
    }
}
