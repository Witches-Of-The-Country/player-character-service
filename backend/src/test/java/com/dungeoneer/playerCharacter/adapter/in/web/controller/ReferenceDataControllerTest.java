package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.core.config.SecurityConfig;
import com.dungeoneer.core.web.GlobalExceptionHandler;
import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReferenceDataController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ReferenceDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetReferenceDataUseCase getReferenceDataUseCase;

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

        mockMvc.perform(get("/api/reference/classes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Fighter"))
                .andExpect(jsonPath("$[0].hitDice").value("d10"))
                .andExpect(jsonPath("$[0].ruleset").value("SRD_2024"));

        verify(getReferenceDataUseCase).getClasses(null);
    }

    @Test
    @DisplayName("GET /api/reference/classes?ruleset=SRD_2024 returns 200 OK and filtered archetypes")
    void shouldReturnClassesWithRulesetFilter() throws Exception {
        ArchetypeDto wizard = new ArchetypeDto(
                UUID.randomUUID(), "Wizard", "d6", "INTELLIGENCE",
                List.of("INTELLIGENCE", "WISDOM"),
                List.of(),
                List.of("DAGGERS", "DARTS", "SLINGS", "QUARTERSTAFFS", "LIGHT_CROSSBOWS"),
                SpellcastingType.FULL, "INTELLIGENCE",
                Ruleset.SRD_2024, "SRD"
        );
        when(getReferenceDataUseCase.getClasses(Ruleset.SRD_2024)).thenReturn(List.of(wizard));

        mockMvc.perform(get("/api/reference/classes").param("ruleset", "SRD_2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Wizard"))
                .andExpect(jsonPath("$[0].ruleset").value("SRD_2024"));

        verify(getReferenceDataUseCase).getClasses(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("GET /api/reference/lineages?ruleset=SRD_2014 returns 200 OK and lineages with fixed ASI")
    void shouldReturnLineagesWithRulesetFilter() throws Exception {
        LineageDto dwarf = new LineageDto(
                UUID.randomUUID(), "Dwarf", "Bold and hardy", 25, "MEDIUM",
                Map.of("CON", 2),
                List.of("Darkvision", "Dwarven Resilience"),
                Ruleset.SRD_2014, "SRD"
        );
        when(getReferenceDataUseCase.getLineages(Ruleset.SRD_2014)).thenReturn(List.of(dwarf));

        mockMvc.perform(get("/api/reference/lineages").param("ruleset", "SRD_2014"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Dwarf"))
                .andExpect(jsonPath("$[0].abilityBonuses.CON").value(2))
                .andExpect(jsonPath("$[0].ruleset").value("SRD_2014"));

        verify(getReferenceDataUseCase).getLineages(Ruleset.SRD_2014);
    }

    @Test
    @DisplayName("GET /api/reference/backgrounds?ruleset=SRD_2024 returns 200 OK and backgrounds with origin feats")
    void shouldReturnBackgroundsWithRulesetFilter() throws Exception {
        BackgroundDto soldier = new BackgroundDto(
                UUID.randomUUID(), "Soldier", "Trained warrior",
                List.of("Athletics", "Intimidation"),
                List.of("Gaming Set", "Vehicles"),
                List.of("Common"),
                List.of("STR", "DEX", "CON"),
                "Savage Attacker", "Once per turn reroll damage dice",
                Ruleset.SRD_2024, "SRD"
        );
        when(getReferenceDataUseCase.getBackgrounds(Ruleset.SRD_2024)).thenReturn(List.of(soldier));

        mockMvc.perform(get("/api/reference/backgrounds").param("ruleset", "SRD_2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Soldier"))
                .andExpect(jsonPath("$[0].originFeatName").value("Savage Attacker"))
                .andExpect(jsonPath("$[0].allowedAbilityBonuses[0]").value("STR"))
                .andExpect(jsonPath("$[0].ruleset").value("SRD_2024"));

        verify(getReferenceDataUseCase).getBackgrounds(Ruleset.SRD_2024);
    }

    @Test
    @DisplayName("GET /api/reference/classes?ruleset=INVALID returns 422 ProblemDetail (TE-01)")
    void shouldReturn422WhenInvalidRulesetOnClasses() throws Exception {
        mockMvc.perform(get("/api/reference/classes").param("ruleset", "INVALID"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/validation"))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Invalid ruleset: 'INVALID'")))
                .andExpect(jsonPath("$.instance").value("/api/reference/classes"));
    }

    @Test
    @DisplayName("GET /api/reference/lineages?ruleset=UNKNOWN returns 422 ProblemDetail")
    void shouldReturn422WhenInvalidRulesetOnLineages() throws Exception {
        mockMvc.perform(get("/api/reference/lineages").param("ruleset", "UNKNOWN"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.instance").value("/api/reference/lineages"));
    }

    @Test
    @DisplayName("GET /api/reference/backgrounds?ruleset=FOO returns 422 ProblemDetail")
    void shouldReturn422WhenInvalidRulesetOnBackgrounds() throws Exception {
        mockMvc.perform(get("/api/reference/backgrounds").param("ruleset", "FOO"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.instance").value("/api/reference/backgrounds"));
    }
}
