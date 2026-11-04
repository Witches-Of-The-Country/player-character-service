package com.dungeoneer.playerCharacter.domain.exception;

public class CharacterValidationException extends RuntimeException {

    public CharacterValidationException(String message) {
        super(message);
    }

    public CharacterValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
