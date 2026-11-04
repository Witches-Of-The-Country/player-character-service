package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.AbilityScores;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.ClassProgression;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterQueryServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;

    private CharacterQueryService service;
    private UUID userId;
    private UUID characterId;
    private UUID archetypeId;
    private UUID lineageId;
    private UUID backgroundId;

    @BeforeEach
    void setUp() {
        CharacterSheetHydrator hydrator = new CharacterSheetHydrator(referenceDataRepository);
        service = new CharacterQueryService(playerCharacterRepository, referenceDataRepository, hydrator);
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();
        lineageId = UUID.randomUUID();
        backgroundId = UUID.randomUUID();
    }

    @Test
    @DisplayName("TP-03: Successfully retrieves owned character sheet")
    void shouldReturnCharacterSheetWhenOwnedByUser() {
        PlayerCharacter character = new PlayerCharacter(
                characterId, "Legolas", 3, 25, 25, 0, 15, 3, 30, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, userId,
                new AbilityScores(10, 16, 14, 12, 14, 8), "Elven notes",
                HpCalculationMethod.MAX, true,
                List.of(new ClassProgression(UUID.randomUUID(), characterId, archetypeId, null, 3)),
                Instant.now(), Instant.now()
        );

        Archetype archetype = new Archetype(
                archetypeId, "Ranger", HitDiceEnum.D10, "DEX",
                List.of("Strength", "Dexterity"), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null
        );

        when(playerCharacterRepository.findByIdAndUserId(characterId, userId)).thenReturn(Optional.of(character));
        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.empty());
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.empty());

        CharacterSheetResult result = service.getCharacter(characterId, userId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Legolas");
        assertThat(result.savingThrows()).containsKey("DEXTERITY");
        // DEX modifier = +3, proficiency = +2 -> 5
        assertThat(result.savingThrows().get("DEXTERITY")).isEqualTo(5);
    }

    @Test
    @DisplayName("TE-02: Returns 404/CharacterNotFoundException when accessing character of another user (IDOR)")
    void shouldThrowCharacterNotFoundWhenUserDoesNotOwnCharacter() {
        UUID otherUserId = UUID.randomUUID();
        when(playerCharacterRepository.findByIdAndUserId(characterId, otherUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCharacter(characterId, otherUserId))
                .isInstanceOf(CharacterNotFoundException.class);
    }

    @Test
    @DisplayName("TP-03: Lists all characters belonging to user")
    void shouldListCharactersBelongingToUser() {
        PlayerCharacter character = new PlayerCharacter(
                characterId, "Gimli", 3, 30, 30, 0, 16, 1, 25, 2,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, userId,
                new AbilityScores(16, 12, 16, 10, 12, 8), "",
                HpCalculationMethod.MAX, true,
                List.of(new ClassProgression(UUID.randomUUID(), characterId, archetypeId, null, 3)),
                Instant.now(), Instant.now()
        );

        when(playerCharacterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId))
                .thenReturn(List.of(character));

        List<CharacterSummaryResult> list = service.listCharacters(userId);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).name()).isEqualTo("Gimli");
        assertThat(list.get(0).id()).isEqualTo(characterId);
    }

    @Test
    @DisplayName("Spec 006: listCharacters provides primary subclass and multiclass progression details")
    void shouldIncludeSubclassAndMulticlassDetailsInSummary() {
        UUID fighterId = UUID.randomUUID();
        UUID battleMasterSubclassId = UUID.randomUUID();
        UUID wizardId = UUID.randomUUID();

        Archetype fighter = new Archetype(fighterId, "Fighter", HitDiceEnum.D10, "STR", List.of(), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null);
        Archetype wizard = new Archetype(wizardId, "Wizard", HitDiceEnum.D6, "INT", List.of(), List.of(), List.of(), SpellcastingType.FULL, null, Ruleset.SRD_2024, "SRD", null);
        com.dungeoneer.playerCharacter.domain.model.Subclass battleMaster = new com.dungeoneer.playerCharacter.domain.model.Subclass(
                battleMasterSubclassId, fighterId, Ruleset.SRD_2024, "battle-master", "Battle Master", "Battle Master", "Battle Master desc", "SRD", null
        );

        when(referenceDataRepository.findArchetypeById(fighterId)).thenReturn(Optional.of(fighter));
        when(referenceDataRepository.findArchetypeById(wizardId)).thenReturn(Optional.of(wizard));
        when(referenceDataRepository.findSubclassById(battleMasterSubclassId)).thenReturn(Optional.of(battleMaster));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.empty());
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.empty());

        PlayerCharacter multiclassChar = new PlayerCharacter(
                characterId, "Valeros", 7, 50, 50, 0, 18, 1, 30, 3,
                Ruleset.SRD_2024, AbilityScoreMethod.STANDARD_ARRAY, lineageId, backgroundId, userId,
                new AbilityScores(16, 12, 14, 14, 10, 8), "",
                HpCalculationMethod.MAX, false,
                List.of(
                        new ClassProgression(UUID.randomUUID(), characterId, fighterId, battleMasterSubclassId, 5),
                        new ClassProgression(UUID.randomUUID(), characterId, wizardId, null, 2)
                ),
                Instant.now(), Instant.now()
        );

        when(playerCharacterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId))
                .thenReturn(List.of(multiclassChar));

        List<CharacterSummaryResult> summaries = service.listCharacters(userId);

        assertThat(summaries).hasSize(1);
        CharacterSummaryResult summary = summaries.get(0);
        assertThat(summary.className()).isEqualTo("Fighter");
        assertThat(summary.subclassName()).isEqualTo("Battle Master");
        assertThat(summary.classes()).hasSize(2);
        assertThat(summary.classes().get(0).className()).isEqualTo("Fighter");
        assertThat(summary.classes().get(0).subclassName()).isEqualTo("Battle Master");
        assertThat(summary.classes().get(0).classLevel()).isEqualTo(5);
        assertThat(summary.classes().get(1).className()).isEqualTo("Wizard");
        assertThat(summary.classes().get(1).subclassName()).isNull();
        assertThat(summary.classes().get(1).classLevel()).isEqualTo(2);
    }
}
