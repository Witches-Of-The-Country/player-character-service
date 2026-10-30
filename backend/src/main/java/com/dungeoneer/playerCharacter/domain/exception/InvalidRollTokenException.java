package com.dungeoneer.playerCharacter.domain.exception;

public class InvalidRollTokenException extends RuntimeException {

    public InvalidRollTokenException(String message) {
        super(message);
    }

    public InvalidRollTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
