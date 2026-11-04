package com.dungeoneer.playerCharacter.domain.exception;

public class CharacterNotFoundException extends RuntimeException {

    public CharacterNotFoundException(String message) {
        super(message);
    }

    public CharacterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
