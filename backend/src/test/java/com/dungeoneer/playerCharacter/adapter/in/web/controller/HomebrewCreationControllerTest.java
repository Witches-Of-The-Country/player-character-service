package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.core.config.SecurityConfig;
import com.dungeoneer.core.web.GlobalExceptionHandler;
import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.in.CreateHomebrewUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReferenceDataController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class HomebrewCreationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetReferenceDataUseCase getReferenceDataUseCase;

    @MockitoBean
    private CreateHomebrewUseCase createHomebrewUseCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("POST /api/reference/spells with valid payload and X-User-Id returns 201 Created")
    void shouldCreateSpellSuccessfully() throws Exception {
        UUID spellId = UUID.randomUUID();
        SpellDto spellDto = new SpellDto(
                spellId, "HOMEBREW", "teletransporte-sombrio", "Shadow Teleport", "Teletransporte Sombrio",
                4, "Conjuração", "1 ação bônus", "18 metros", "V", "Instantânea", "Salte pelas sombras", "HOMEBREW"
        );

        when(createHomebrewUseCase.createSpell(eq(userId), any(CreateHomebrewSpellRequest.class))).thenReturn(spellDto);

        String json = """
                {
                    "name": "Teletransporte Sombrio",
                    "level": 4,
                    "school": "Conjuração",
                    "description": "Salte pelas sombras"
                }
                """;

        mockMvc.perform(post("/api/reference/spells")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(spellId.toString()))
                .andExpect(jsonPath("$.name").value("Teletransporte Sombrio"))
                .andExpect(jsonPath("$.ruleset").value("HOMEBREW"));
    }

    @Test
    @DisplayName("POST /api/reference/spells with invalid payload returns 400 Bad Request with RFC 7807")
    void shouldReturn400WhenSpellPayloadIsInvalid() throws Exception {
        String json = """
                {
                    "name": "",
                    "level": 15,
                    "school": "",
                    "description": ""
                }
                """;

        mockMvc.perform(post("/api/reference/spells")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("POST /api/reference/spells missing X-User-Id returns 401 Unauthorized")
    void shouldReturn401WhenUserIdHeaderIsMissing() throws Exception {
        String json = """
                {
                    "name": "Magia Válida",
                    "level": 1,
                    "school": "Evocação",
                    "description": "Descrição válida"
                }
                """;

        mockMvc.perform(post("/api/reference/spells")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/reference/feats returns 201 Created")
    void shouldCreateFeatSuccessfully() throws Exception {
        UUID featId = UUID.randomUUID();
        FeatDto featDto = new FeatDto(
                featId, "HOMEBREW", "atleta-sombrio", "Shadow Athlete", "Atleta Sombrio",
                "Destreza 13+", "Aumenta agilidade nas sombras", "HOMEBREW"
        );

        when(createHomebrewUseCase.createFeat(eq(userId), any(CreateHomebrewFeatRequest.class))).thenReturn(featDto);

        String json = """
                {
                    "name": "Atleta Sombrio",
                    "description": "Aumenta agilidade nas sombras"
                }
                """;

        mockMvc.perform(post("/api/reference/feats")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Atleta Sombrio"));
    }

    @Test
    @DisplayName("TP-03 MockMvc: POST /api/reference/feats with custom charges returns 201 Created")
    void shouldCreateFeatWithCustomCharges() throws Exception {
        UUID featId = UUID.randomUUID();
        com.dungeoneer.playerCharacter.domain.model.MechanicalEffects effects = new com.dungeoneer.playerCharacter.domain.model.MechanicalEffects(
                Map.of(), 0, 0, 0, 0, List.of(), null, null, null, null, null,
                null, false, null, List.of(), null,
                com.dungeoneer.playerCharacter.domain.model.ChargeType.ABILITY_MODIFIER, "CHA", null,
                com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.FIXED, 1,
                com.dungeoneer.playerCharacter.domain.model.ChargeRecoveryType.ALL, null
        );
        FeatDto featDto = new FeatDto(
                featId, "HOMEBREW", "orador-mistico", "Mystic Speaker", "Orador Místico",
                null, "Usa carisma para influenciar", "HOMEBREW",
                com.dungeoneer.playerCharacter.domain.model.FeatType.GENERAL, effects
        );

        when(createHomebrewUseCase.createFeat(eq(userId), any(CreateHomebrewFeatRequest.class))).thenReturn(featDto);

        String json = """
                {
                    "name": "Orador Místico",
                    "description": "Usa carisma para influenciar",
                    "mechanicalEffects": {
                        "maxChargesType": "ABILITY_MODIFIER",
                        "chargeAbility": "CHA",
                        "shortRestRecoveryType": "FIXED",
                        "shortRestRecoveryFixed": 1,
                        "longRestRecoveryType": "ALL"
                    }
                }
                """;

        mockMvc.perform(post("/api/reference/feats")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Orador Místico"))
                .andExpect(jsonPath("$.mechanicalEffects.maxChargesType").value("ABILITY_MODIFIER"))
                .andExpect(jsonPath("$.mechanicalEffects.chargeAbility").value("CHA"))
                .andExpect(jsonPath("$.mechanicalEffects.shortRestRecoveryType").value("FIXED"))
                .andExpect(jsonPath("$.mechanicalEffects.shortRestRecoveryFixed").value(1))
                .andExpect(jsonPath("$.mechanicalEffects.longRestRecoveryType").value("ALL"));
    }

    @Test
    @DisplayName("TE-03 MockMvc: POST /api/reference/feats with malformed enum returns 400 Bad Request ProblemDetail")
    void shouldReturn400WhenFeatPayloadHasMalformedEnum() throws Exception {
        String json = """
                {
                    "name": "Talento Inválido",
                    "description": "Descrição",
                    "mechanicalEffects": {
                        "maxChargesType": "INVALID_CHARGE_TYPE"
                    }
                }
                """;

        mockMvc.perform(post("/api/reference/feats")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("TE-03 MockMvc: POST /api/reference/feats with validation error returns 422 ProblemDetail")
    void shouldReturn422WhenFeatChargeValidationFails() throws Exception {
        when(createHomebrewUseCase.createFeat(eq(userId), any(CreateHomebrewFeatRequest.class)))
                .thenThrow(new com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException("O atributo da fórmula de cargas é obrigatório"));

        String json = """
                {
                    "name": "Talento Sem Atributo",
                    "description": "Descrição",
                    "mechanicalEffects": {
                        "maxChargesType": "ABILITY_MODIFIER"
                    }
                }
                """;

        mockMvc.perform(post("/api/reference/feats")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value("O atributo da fórmula de cargas é obrigatório"));
    }

    @Test
    @DisplayName("POST /api/reference/items returns 201 Created")
    void shouldCreateItemSuccessfully() throws Exception {
        UUID itemId = UUID.randomUUID();
        ItemDto itemDto = new ItemDto(
                itemId, "HOMEBREW", "elmo-arcano", "Arcane Helm", "Elmo Arcano",
                "ARMOR", "Armadura Leve", "RARE", true, "300 po", "1 kg", "Concede visão no escuro", "HOMEBREW"
        );

        when(createHomebrewUseCase.createItem(eq(userId), any(CreateHomebrewItemRequest.class))).thenReturn(itemDto);

        String json = """
                {
                    "name": "Elmo Arcano",
                    "itemCategory": "ARMOR",
                    "itemType": "Armadura Leve",
                    "description": "Concede visão no escuro"
                }
                """;

        mockMvc.perform(post("/api/reference/items")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Elmo Arcano"));
    }
}
