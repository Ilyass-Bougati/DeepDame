package com.deepdame.service.username;

public interface UsernameService {
    Boolean reserveUsername(String username);
    Boolean isTaken(String username);
    void releaseUsername(String username);
}
