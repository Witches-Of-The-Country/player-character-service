package com.dungeoneer.playerCharacter.application.dto;

public record AddCharacterNoteCommand(
        String title,
        String body
) {
}
