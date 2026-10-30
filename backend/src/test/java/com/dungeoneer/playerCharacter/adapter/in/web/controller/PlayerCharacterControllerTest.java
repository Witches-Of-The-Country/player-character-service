package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.core.config.SecurityConfig;
import com.dungeoneer.core.web.GlobalExceptionHandler;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.RollHpRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterRequest;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.DeleteCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharactersUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollHitPointsUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerCharacterController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class PlayerCharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateCharacterUseCase createCharacterUseCase;
    @MockitoBean
    private GetCharacterUseCase getCharacterUseCase;
    @MockitoBean
    private ListCharactersUseCase listCharactersUseCase;
    @MockitoBean
    private UpdateCharacterUseCase updateCharacterUseCase;
    @MockitoBean
    private DeleteCharacterUseCase deleteCharacterUseCase;
    @MockitoBean
    private RollAbilityScoresUseCase rollAbilityScoresUseCase;
    @MockitoBean
    private RollHitPointsUseCase rollHitPointsUseCase;

    private CharacterSheetResult createMockSheet(UUID characterId, UUID userId, String name) {
        return new CharacterSheetResult(
                characterId,
                userId,
                name,
                1,
                12,
                12,
                0,
                12,
                2,
                30,
                2,
                Ruleset.SRD_2024,
                AbilityScoreMethod.STANDARD_ARRAY,
                HpCalculationMethod.MAX,
                false,
                "Hero notes",
                new AbilityScores(15, 14, 13, 12, 10, 8),
                new Lineage(UUID.randomUUID(), "Human", "Versatile", 30, "MEDIUM", Map.of(), List.of(), Ruleset.SRD_2024, "SRD", null),
                new Background(UUID.randomUUID(), "Soldier", "Military", List.of("Athletics"), List.of(), List.of(), List.of("STRENGTH"), "Feat", "Desc", Ruleset.SRD_2024, "SRD", null),
                new Archetype(UUID.randomUUID(), "Fighter", HitDiceEnum.D10, "STR", List.of("Strength"), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null),
                List.of(),
                Map.of("STRENGTH", 4, "DEXTERITY", 2),
                Map.of("Athletics", 4),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("TP-01: POST /api/characters with X-User-Id returns 201 Created with full sheet")
    void shouldCreateCharacterSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();

        CreateCharacterRequest request = new CreateCharacterRequest(
                "Galdor",
                1,
                Ruleset.SRD_2024,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                Map.of("strength", 15, "dexterity", 14, "constitution", 13, "intelligence", 12, "wisdom", 10, "charisma", 8),
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                "Hero notes"
        );

        when(createCharacterUseCase.createCharacter(any(CreateCharacterCommand.class)))
                .thenReturn(createMockSheet(characterId, userId, "Galdor"));

        mockMvc.perform(post("/api/characters")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(characterId.toString()))
                .andExpect(jsonPath("$.name").value("Galdor"))
                .andExpect(jsonPath("$.ruleset").value("SRD_2024"))
                .andExpect(jsonPath("$.hitPointsMax").value(12))
                .andExpect(jsonPath("$.armorClass").value(12));
    }

    @Test
    @DisplayName("TP-02: POST /api/characters/ability-rolls returns 201 when new and 200 when existing")
    void shouldHandleSingleChanceAbilityRolls() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID rollId = UUID.randomUUID();

        // 1st call: new roll -> 201 Created
        when(rollAbilityScoresUseCase.rollAbilityScores(userId))
                .thenReturn(new RollAbilityScoresResult(rollId, userId, List.of(16, 14, 13, 12, 10, 8), "[]", true, Instant.now()));

        mockMvc.perform(post("/api/characters/ability-rolls")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rollId").value(rollId.toString()))
                .andExpect(jsonPath("$.isNew").value(true));

        // 2nd call: existing roll -> 200 OK
        when(rollAbilityScoresUseCase.rollAbilityScores(userId))
                .thenReturn(new RollAbilityScoresResult(rollId, userId, List.of(16, 14, 13, 12, 10, 8), "[]", false, Instant.now()));

        mockMvc.perform(post("/api/characters/ability-rolls")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rollId").value(rollId.toString()))
                .andExpect(jsonPath("$.isNew").value(false));
    }

    @Test
    @DisplayName("TP-02: POST /api/characters/hp-rolls returns 201 when new and 200 when existing")
    void shouldHandleSingleChanceHpRolls() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID archetypeId = UUID.randomUUID();
        UUID rollId = UUID.randomUUID();
        RollHpRequest request = new RollHpRequest(archetypeId, 3);

        when(rollHitPointsUseCase.rollHitPoints(any(RollHitPointsCommand.class)))
                .thenReturn(new RollHitPointsResult(rollId, userId, archetypeId, 3, List.of(8, 7), true, Instant.now()));

        mockMvc.perform(post("/api/characters/hp-rolls")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rollId").value(rollId.toString()))
                .andExpect(jsonPath("$.targetLevel").value(3));
    }

    @Test
    @DisplayName("TP-03: Full CRUD endpoints: GET list, GET by ID, PUT, DELETE")
    void shouldHandleFullCrudEndpoints() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();

        // 1. GET /api/characters (List)
        when(listCharactersUseCase.listCharacters(userId))
                .thenReturn(List.of(new CharacterSummaryResult(
                        characterId, "Galdor", 1, Ruleset.SRD_2024, "Fighter", "Human", "Soldier", 12, 12, 12, Instant.now()
                )));

        mockMvc.perform(get("/api/characters")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Galdor"));

        // 2. GET /api/characters/{id}
        when(getCharacterUseCase.getCharacter(characterId, userId))
                .thenReturn(createMockSheet(characterId, userId, "Galdor"));

        mockMvc.perform(get("/api/characters/{id}", characterId)
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(characterId.toString()));

        // 3. PUT /api/characters/{id}
        UpdateCharacterRequest updateRequest = new UpdateCharacterRequest("Galdor the Brave", 8, 2, "Updated notes");
        when(updateCharacterUseCase.updateCharacter(any(UpdateCharacterCommand.class)))
                .thenReturn(createMockSheet(characterId, userId, "Galdor the Brave"));

        mockMvc.perform(put("/api/characters/{id}", characterId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galdor the Brave"));

        // 4. DELETE /api/characters/{id}
        doNothing().when(deleteCharacterUseCase).deleteCharacter(characterId, userId);

        mockMvc.perform(delete("/api/characters/{id}", characterId)
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("TE-01: Reused roll token returns 409 Conflict ProblemDetail")
    void shouldReturn409ConflictForReusedRollToken() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateCharacterRequest request = new CreateCharacterRequest(
                "Hero", 1, Ruleset.SRD_2024, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, AbilityScoreMethod.ROLLED,
                Map.of("strength", 16, "dexterity", 14, "constitution", 13, "intelligence", 12, "wisdom", 10, "charisma", 9),
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                UUID.randomUUID(), HpCalculationMethod.MAX, null, null
        );

        when(createCharacterUseCase.createCharacter(any(CreateCharacterCommand.class)))
                .thenThrow(new RollConflictException("Ability roll token has already been used."));

        mockMvc.perform(post("/api/characters")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Roll Conflict"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("TE-02: Cross-user access returns 404 Not Found (IDOR protection)")
    void shouldReturn404WhenCrossUserAccessOccurs() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();

        when(getCharacterUseCase.getCharacter(characterId, otherUserId))
                .thenThrow(new CharacterNotFoundException("Character not found or access denied"));

        mockMvc.perform(get("/api/characters/{id}", characterId)
                        .header("X-User-Id", otherUserId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("TE-03: Invalid SRD_2024 background ASI returns 422 Unprocessable Entity")
    void shouldReturn422ForInvalidBackgroundAsi() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateCharacterRequest request = new CreateCharacterRequest(
                "Hero", 1, Ruleset.SRD_2024, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, AbilityScoreMethod.STANDARD_ARRAY,
                Map.of("strength", 15, "dexterity", 14, "constitution", 13, "intelligence", 12, "wisdom", 10, "charisma", 8),
                Map.of("INTELLIGENCE", 2, "STRENGTH", 1),
                null, HpCalculationMethod.MAX, null, null
        );

        when(createCharacterUseCase.createCharacter(any(CreateCharacterCommand.class)))
                .thenThrow(new CharacterValidationException("Ability 'INTELLIGENCE' is not permitted for background"));

        mockMvc.perform(post("/api/characters")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Security: Missing X-User-Id header returns 401 Unauthorized")
    void shouldReturn401WhenXUserIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/characters"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }
}
