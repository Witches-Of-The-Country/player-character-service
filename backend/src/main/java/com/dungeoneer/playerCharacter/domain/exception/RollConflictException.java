package com.dungeoneer.playerCharacter.domain.exception;

public class RollConflictException extends RuntimeException {

    public RollConflictException(String message) {
        super(message);
    }

    public RollConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
