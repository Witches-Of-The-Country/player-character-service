package com.dungeoneer.playerCharacter.domain.exception;

public class RestValidationException extends CharacterValidationException {

    public RestValidationException(String message) {
        super(message);
    }

    public RestValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
