package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreatePlayerCharacterRequest;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dungeoneer.core.config.SecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(PlayerCharacterController.class)
@Import(SecurityConfig.class)
public class PlayerCharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateCharacterUseCase createCharacterUseCase;

    @Test
    void whenPostValidRequest_thenReturns201AndLocationHeader() throws Exception {
        // Arrange
        UUID characterId = UUID.randomUUID();
        CreatePlayerCharacterRequest request = new CreatePlayerCharacterRequest(
                "Galdor",
                1,
                "SRD_2024",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Map.of("strength", 15, "dexterity", 14, "constitution", 13, "intelligence", 12, "wisdom", 10, "charisma", 8)
        );

        PlayerCharacter mockCharacter = new PlayerCharacter(
                characterId,
                "Galdor",
                1,
                11,
                11,
                0,
                12,
                2,
                30,
                2,
                com.dungeoneer.playerCharacter.domain.model.Ruleset.SRD_2024,
                com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod.POINT_BUY,
                request.lineageId(),
                request.backgroundId(),
                request.userId(),
                new com.dungeoneer.playerCharacter.domain.model.AbilityScores(
                        15, 14, 13, 12, 10, 8
                ),
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(createCharacterUseCase.createCharacter(any(CreateCharacterCommand.class)))
                .thenReturn(mockCharacter);

        // Act & Assert
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/characters/" + characterId))
                .andExpect(jsonPath("$.id").value(characterId.toString()))
                .andExpect(jsonPath("$.name").value("Galdor"))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.ruleset").value("SRD_2024"));
    }

    @Test
    void whenPostInvalidRequest_thenReturns422UnprocessableEntity() throws Exception {
        // Arrange
        CreatePlayerCharacterRequest invalidRequest = new CreatePlayerCharacterRequest(
                "", // Blank name should fail
                -1, // Negative level should fail
                "INVALID", // Maybe fail depending on validation
                null,
                null,
                null,
                Map.of() // Empty abilities should fail
        );

        // Act & Assert
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }
}
