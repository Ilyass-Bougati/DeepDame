package com.deepdame.exception;

public class WsUnauthorized extends RuntimeException {
    public WsUnauthorized(String message) {
        super(message);
    }
}
