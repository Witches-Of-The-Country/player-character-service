package com.dungeoneer.playerCharacter.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCharacterNoteCommand(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @NotBlank(message = "Body is required")
        @Size(max = 10000, message = "Body cannot exceed 10000 characters")
        String body
) {
}
