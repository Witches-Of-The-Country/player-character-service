package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.core.config.SecurityConfig;
import com.dungeoneer.core.web.GlobalExceptionHandler;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.RollHpRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterRequest;
import com.dungeoneer.playerCharacter.application.dto.AddCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;
import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterCommand;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterNoteCommand;
import com.dungeoneer.playerCharacter.application.port.in.AddCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.DeleteCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharacterNotesUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharactersUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollHitPointsUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RestValidationException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.RestType;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    @MockitoBean
    private AddCharacterNoteUseCase addCharacterNoteUseCase;
    @MockitoBean
    private ListCharacterNotesUseCase listCharacterNotesUseCase;
    @MockitoBean
    private UpdateCharacterNoteUseCase updateCharacterNoteUseCase;
    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.AddExperienceUseCase addExperienceUseCase;
    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.LevelUpUseCase levelUpUseCase;
    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.ManageInventoryUseCase manageInventoryUseCase;
    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.UseConsumableUseCase useConsumableUseCase;
    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.RestUseCase restUseCase;

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

        // Test PATCH with partial payload (e.g. only hitPointsCurrent)
        UpdateCharacterRequest patchRequest = new UpdateCharacterRequest(null, 5, null, null);
        when(updateCharacterUseCase.updateCharacter(any(UpdateCharacterCommand.class)))
                .thenReturn(createMockSheet(characterId, userId, "Galdor the Brave"));

        mockMvc.perform(patch("/api/characters/{id}", characterId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
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

    @Test
    @DisplayName("Bug 3: POST /api/characters/{id}/notes creates note and returns 201 Created")
    void shouldAddCharacterNoteSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();
        Instant now = Instant.now();

        CreateCharacterNoteRequest request = new CreateCharacterNoteRequest("Clue", "Found in library");
        CharacterNoteResult result = new CharacterNoteResult(noteId, characterId, "Clue", "Found in library", now);

        when(addCharacterNoteUseCase.addNote(eq(characterId), eq(userId), any(AddCharacterNoteCommand.class)))
                .thenReturn(result);

        mockMvc.perform(post("/api/characters/" + characterId + "/notes")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/characters/" + characterId + "/notes/" + noteId)))
                .andExpect(jsonPath("$.id").value(noteId.toString()))
                .andExpect(jsonPath("$.characterId").value(characterId.toString()))
                .andExpect(jsonPath("$.title").value("Clue"))
                .andExpect(jsonPath("$.body").value("Found in library"));
    }

    @Test
    @DisplayName("Bug 3: POST /api/characters/{id}/notes rejects blank title with 422 Unprocessable Entity")
    void shouldReturn400WhenAddingNoteWithBlankTitle() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();

        CreateCharacterNoteRequest request = new CreateCharacterNoteRequest("", "Valid body");

        mockMvc.perform(post("/api/characters/" + characterId + "/notes")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Bug 3: POST /api/characters/{id}/notes rejects body > 10000 chars with 422 Unprocessable Entity")
    void shouldReturn400WhenAddingNoteWithOversizedBody() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();

        CreateCharacterNoteRequest request = new CreateCharacterNoteRequest("Valid title", "A".repeat(10001));

        mockMvc.perform(post("/api/characters/" + characterId + "/notes")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("PUT /api/characters/{id}/notes/{noteId} updates note and returns 200 OK")
    void shouldUpdateCharacterNoteSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();
        Instant now = Instant.now();

        UpdateCharacterNoteRequest request = new UpdateCharacterNoteRequest("Updated Title", "Updated Body");
        CharacterNoteResult result = new CharacterNoteResult(noteId, characterId, "Updated Title", "Updated Body", now);

        when(updateCharacterNoteUseCase.updateNote(eq(characterId), eq(noteId), eq(userId), any(UpdateCharacterNoteCommand.class)))
                .thenReturn(result);

        mockMvc.perform(put("/api/characters/" + characterId + "/notes/" + noteId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(noteId.toString()))
                .andExpect(jsonPath("$.characterId").value(characterId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.body").value("Updated Body"));
    }

    @Test
    @DisplayName("PUT /api/characters/{id}/notes/{noteId} rejects blank title with 422 Unprocessable Entity")
    void shouldReturn422WhenUpdatingNoteWithBlankTitle() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        UpdateCharacterNoteRequest request = new UpdateCharacterNoteRequest("", "Valid body");

        mockMvc.perform(put("/api/characters/" + characterId + "/notes/" + noteId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("PUT /api/characters/{id}/notes/{noteId} returns 404 Not Found when character or note not found")
    void shouldReturn404WhenUpdatingNonExistentNote() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        UpdateCharacterNoteRequest request = new UpdateCharacterNoteRequest("Title", "Body");

        when(updateCharacterNoteUseCase.updateNote(eq(characterId), eq(noteId), eq(userId), any(UpdateCharacterNoteCommand.class)))
                .thenThrow(new CharacterNotFoundException("Character note not found: " + noteId));

        mockMvc.perform(put("/api/characters/" + characterId + "/notes/" + noteId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Bug 3: GET /api/characters/{id}/notes returns 200 OK with list of notes")
    void shouldListCharacterNotesSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID n1 = UUID.randomUUID();
        UUID n2 = UUID.randomUUID();
        Instant now = Instant.now();

        List<CharacterNoteResult> notes = List.of(
                new CharacterNoteResult(n1, characterId, "First", "Body 1", now),
                new CharacterNoteResult(n2, characterId, "Second", "Body 2", now.minusSeconds(60))
        );

        when(listCharacterNotesUseCase.listNotes(characterId, userId)).thenReturn(notes);

        mockMvc.perform(get("/api/characters/" + characterId + "/notes")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("First"))
                .andExpect(jsonPath("$[1].title").value("Second"));
    }

    @Test
    @DisplayName("Bug 1: POST /api/characters/hp-rolls with null archetypeId returns 422 Unprocessable Entity")
    void shouldReturn400WhenRollingHpWithNullArchetype() throws Exception {
        UUID userId = UUID.randomUUID();
        RollHpRequest request = new RollHpRequest(null, 2);

        mockMvc.perform(post("/api/characters/hp-rolls")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("TP-03: POST /api/characters/{id}/rest returns 200 OK with updated character sheet")
    void shouldTakeRestSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        RestCommand command = new RestCommand(RestType.LONG);

        when(restUseCase.takeRest(eq(characterId), eq(userId), any(RestCommand.class)))
                .thenReturn(createMockSheet(characterId, userId, "Galdor"));

        mockMvc.perform(post("/api/characters/" + characterId + "/rest")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(characterId.toString()))
                .andExpect(jsonPath("$.name").value("Galdor"));
    }

    @Test
    @DisplayName("TE-02: POST /api/characters/{id}/rest with overspending Hit Dice returns 400 Bad Request ProblemDetail")
    void shouldReturn400WhenOverspendingHitDice() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID cpId = UUID.randomUUID();
        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(cpId, 5))
        );

        when(restUseCase.takeRest(eq(characterId), eq(userId), any(RestCommand.class)))
                .thenThrow(new RestValidationException("Cannot spend 5 Hit Dice. Only 1 available for class progression " + cpId));

        mockMvc.perform(post("/api/characters/" + characterId + "/rest")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Cannot spend 5 Hit Dice")));
    }

    @Test
    @DisplayName("TE-03: POST /api/characters/{id}/rest for dead character (0 HP) returns 422 ProblemDetail")
    void shouldReturn422WhenRestingWhileDead() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        RestCommand command = new RestCommand(RestType.LONG);

        when(restUseCase.takeRest(eq(characterId), eq(userId), any(RestCommand.class)))
                .thenThrow(new CharacterValidationException("Dead or unconscious characters (0 HP) cannot take a rest."));

        mockMvc.perform(post("/api/characters/" + characterId + "/rest")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("0 HP")));
    }

    @Test
    @DisplayName("TE-04: POST /api/characters/{id}/rest with foreign class progression returns 400 ProblemDetail")
    void shouldReturn400WhenClassProgressionNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        UUID foreignCpId = UUID.randomUUID();
        RestCommand command = new RestCommand(
                RestType.SHORT,
                null,
                List.of(new RestCommand.HitDiceSpendRequest(foreignCpId, 1))
        );

        when(restUseCase.takeRest(eq(characterId), eq(userId), any(RestCommand.class)))
                .thenThrow(new RestValidationException("Class progression not found: " + foreignCpId));

        mockMvc.perform(post("/api/characters/" + characterId + "/rest")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Class progression not found")));
    }
}
