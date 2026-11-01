package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.core.config.SecurityConfig;
import com.dungeoneer.core.web.GlobalExceptionHandler;
import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.CursorPageResponse;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReferenceDataController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ReferenceDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetReferenceDataUseCase getReferenceDataUseCase;

    @MockitoBean
    private com.dungeoneer.playerCharacter.application.port.in.CreateHomebrewUseCase createHomebrewUseCase;

    @Test
    @DisplayName("GET /api/reference/classes returns 200 OK and list of archetypes")
    void shouldReturnClassesWithoutFilter() throws Exception {
        ArchetypeDto fighter = new ArchetypeDto(
                UUID.randomUUID(), "Fighter", "d10", "STRENGTH",
                List.of("STRENGTH", "CONSTITUTION"),
                List.of("ALL_ARMOR", "SHIELDS"),
                List.of("SIMPLE", "MARTIAL"),
                SpellcastingType.NONE, null,
                Ruleset.SRD_2024, "SRD"
        );
        when(getReferenceDataUseCase.getClasses(null)).thenReturn(List.of(fighter));

        mockMvc.perform(get("/api/reference/classes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fighter"))
                .andExpect(jsonPath("$[0].hitDice").value("d10"));

        verify(getReferenceDataUseCase).getClasses(null);
    }

    @Test
    @DisplayName("GET /api/reference/spells returns 200 OK with CursorPageResponse")
    void shouldReturnSpellsPaginated() throws Exception {
        SpellDto spell = new SpellDto(
                UUID.randomUUID(), "SRD_2024", "magic-missile-2024", "Magic Missile", "Mísseis Mágicos",
                1, "Evocação", "1 ação", "36 metros", "V, S", "Instantânea", "Dardos de energia", "SRD"
        );
        CursorPageResponse<SpellDto> page = new CursorPageResponse<>(List.of(spell), "cursor-token", true, 1);
        when(getReferenceDataUseCase.getSpells(any(), eq(10), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reference/spells")
                        .param("limit", "10")
                        .param("q", "míssil")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Mísseis Mágicos"))
                .andExpect(jsonPath("$.nextCursor").value("cursor-token"))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.pageSize").value(1));
    }

    @Test
    @DisplayName("GET /api/reference/feats returns 200 OK with CursorPageResponse")
    void shouldReturnFeatsPaginated() throws Exception {
        FeatDto feat = new FeatDto(
                UUID.randomUUID(), "SRD_2024", "alert-2024", "Alert", "Alerta", "Nível 1", "Iniciativa", "SRD"
        );
        CursorPageResponse<FeatDto> page = new CursorPageResponse<>(List.of(feat), null, false, 1);
        when(getReferenceDataUseCase.getFeats(any(), eq(20), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reference/feats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Alerta"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("GET /api/reference/items returns 200 OK with CursorPageResponse")
    void shouldReturnItemsPaginated() throws Exception {
        ItemDto item = new ItemDto(
                UUID.randomUUID(), "SRD_2024", "longsword-2024", "Longsword", "Espada Longa",
                "WEAPON", "Arma Marcial", "NONE", false, "15 po", "1,5 kg", "Espada versátil", "SRD"
        );
        CursorPageResponse<ItemDto> page = new CursorPageResponse<>(List.of(item), null, false, 1);
        when(getReferenceDataUseCase.getItems(any(), eq(20), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/reference/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Espada Longa"))
                .andExpect(jsonPath("$.items[0].itemCategory").value("WEAPON"));
    }

    @Test
    @DisplayName("GET /api/reference/spells with invalid limit (> 100) returns 400 Bad Request")
    void shouldReturnBadRequestWhenLimitExceedsMax() throws Exception {
        mockMvc.perform(get("/api/reference/spells")
                        .param("limit", "150")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reference/items with mechanicalProperties returns 201 Created")
    void shouldCreateHomebrewItemWithMechanicalProperties() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ItemDto createdItem = new ItemDto(
                itemId, "HOMEBREW", "espada-flamejante", "Flame Blade", "Espada Flamejante",
                "WEAPON", "Espada Longa", "RARE", true, "1500 po", "1.5 kg", "Espada em chamas", "HOMEBREW"
        );

        when(createHomebrewUseCase.createItem(eq(userId), any())).thenReturn(createdItem);

        String jsonPayload = """
                {
                    "name": "Espada Flamejante",
                    "nameEn": "Flame Blade",
                    "itemCategory": "WEAPON",
                    "itemType": "Espada Longa",
                    "rarity": "RARE",
                    "attunement": true,
                    "cost": "1500 po",
                    "weight": "1.5 kg",
                    "description": "Espada em chamas",
                    "mechanicalProperties": {
                        "type": "WEAPON",
                        "damageRolls": [
                            {"diceFormula": "1d8", "damageType": "SLASHING"}
                        ],
                        "versatileDice": "1d10",
                        "magicBonus": 1,
                        "properties": ["VERSATILE"]
                    }
                }
                """;

        mockMvc.perform(post("/api/reference/items")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/reference/items/" + itemId)))
                .andExpect(jsonPath("$.name").value("Espada Flamejante"))
                .andExpect(jsonPath("$.itemCategory").value("WEAPON"));

        verify(createHomebrewUseCase).createItem(eq(userId), any());
    }

    @Test
    @DisplayName("PUT /api/reference/items/{id} returns 200 OK on valid update")
    void shouldUpdateHomebrewItem() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ItemDto updatedItem = new ItemDto(
                itemId, "HOMEBREW", "espada-flamejante", "Flame Blade", "Espada Flamejante +1",
                "WEAPON", "Espada Longa", "VERY_RARE", true, "5000 po", "1.5 kg", "Espada em chamas azuis", "HOMEBREW"
        );

        when(createHomebrewUseCase.updateItem(eq(itemId), eq(userId), any())).thenReturn(updatedItem);

        String jsonPayload = """
                {
                    "name": "Espada Flamejante +1",
                    "nameEn": "Flame Blade",
                    "itemCategory": "WEAPON",
                    "itemType": "Espada Longa",
                    "rarity": "VERY_RARE",
                    "attunement": true,
                    "cost": "5000 po",
                    "weight": "1.5 kg",
                    "description": "Espada em chamas azuis"
                }
                """;

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/reference/items/" + itemId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Espada Flamejante +1"))
                .andExpect(jsonPath("$.rarity").value("VERY_RARE"));

        verify(createHomebrewUseCase).updateItem(eq(itemId), eq(userId), any());
    }

    @Test
    @DisplayName("PUT /api/reference/items/{id} returns 422 Unprocessable Entity when update violates ruleset immutability")
    void shouldReturnBadRequestWhenUpdatingOfficialItem() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(createHomebrewUseCase.updateItem(eq(itemId), eq(userId), any()))
                .thenThrow(new CharacterValidationException("Official SRD items cannot be modified"));

        String jsonPayload = """
                {
                    "name": "Espada Modificada",
                    "itemCategory": "WEAPON",
                    "itemType": "Espada Longa",
                    "description": "Tentativa de modificar item oficial"
                }
                """;

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/reference/items/" + itemId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Official SRD items cannot be modified"));
    }
}
