package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.ClassProgressionCommand;
import com.dungeoneer.playerCharacter.application.dto.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.out.AbilityRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.HpRollRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.PlayerCharacterRepositoryPort;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import com.dungeoneer.playerCharacter.domain.model.AbilityRoll;
import com.dungeoneer.playerCharacter.domain.model.AbilityScoreMethod;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Background;
import com.dungeoneer.playerCharacter.domain.model.CharacterFeat;
import com.dungeoneer.playerCharacter.domain.model.Feat;
import com.dungeoneer.playerCharacter.domain.model.FeatPrerequisite;
import com.dungeoneer.playerCharacter.domain.model.FeatType;
import com.dungeoneer.playerCharacter.domain.model.HitDiceEnum;
import com.dungeoneer.playerCharacter.domain.model.HpCalculationMethod;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.MechanicalEffects;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import com.dungeoneer.playerCharacter.domain.model.RestType;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import com.dungeoneer.playerCharacter.domain.model.SpellcastingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCharacterServiceTest {

    @Mock
    private PlayerCharacterRepositoryPort playerCharacterRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;
    @Mock
    private AbilityRollRepositoryPort abilityRollRepository;
    @Mock
    private HpRollRepositoryPort hpRollRepository;

    private CreateCharacterService service;

    private UUID userId;
    private UUID archetypeId;
    private UUID lineageId;
    private UUID backgroundId;

    private Archetype archetype;
    private Lineage lineage;
    private Background background;

    @BeforeEach
    void setUp() {
        service = new CreateCharacterService(
                playerCharacterRepository,
                referenceDataRepository,
                abilityRollRepository,
                hpRollRepository
        );

        userId = UUID.randomUUID();
        archetypeId = UUID.randomUUID();
        lineageId = UUID.randomUUID();
        backgroundId = UUID.randomUUID();

        archetype = new Archetype(
                archetypeId,
                "Fighter",
                HitDiceEnum.D10,
                "Strength",
                List.of("Strength", "Constitution"),
                List.of("All Armor"),
                List.of("Martial Weapons"),
                SpellcastingType.NONE,
                null,
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        lineage = new Lineage(
                lineageId,
                "Human",
                "Versatile",
                30,
                "MEDIUM",
                Map.of(),
                List.of(),
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        background = new Background(
                backgroundId,
                "Soldier",
                "Military training",
                List.of("Athletics", "Intimidation"),
                List.of("Vehicles"),
                List.of("Common"),
                List.of("STRENGTH", "CONSTITUTION", "DEXTERITY"),
                "Savage Attacker",
                "Extra damage",
                Ruleset.SRD_2024,
                "SRD",
                null
        );

        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.of(lineage));
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.of(background));
    }

    @Test
    @DisplayName("TP-01: Creates character with STANDARD_ARRAY successfully")
    void shouldCreateCharacterWithStandardArray() {
        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Sir Galahad",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                "Noble knight"
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Sir Galahad");
        // Base 15 STR + 2 bonus = 17
        assertThat(result.abilityScores().getStrength()).isEqualTo(17);
        // Base 13 CON + 1 bonus = 14 (+2 mod)
        assertThat(result.abilityScores().getConstitution()).isEqualTo(14);
        // Level 1 Fighter with CON mod (+2): 10 + 2 = 12 HP
        assertThat(result.hitPointsMax()).isEqualTo(12);
        assertThat(result.classProgressions()).hasSize(1);
        assertThat(result.classProgressions().get(0).getArchetypeId()).isEqualTo(archetypeId);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Creates character above level 1 by initializing at Level 1 with target level XP floor and pending level choices")
    void shouldInitializeAtLevel1WithXpFloorWhenCreatedAboveLevel1() {
        UUID originFeatId = UUID.randomUUID();
        Feat toughFeat = new Feat(
                originFeatId, Ruleset.SRD_2024, "tough", "Tough", "Vigoroso", null,
                "+2 PV por nível", "SRD", null, FeatType.ORIGIN,
                new MechanicalEffects(Map.of(), 0, 0, 0, 2, List.of(), null, null, null, null, RestType.NONE)
        );
        when(referenceDataRepository.findFeatById(originFeatId)).thenReturn(Optional.of(toughFeat));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Sir Lancelot",
                5,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.ROLLED,
                null,
                List.of(),
                "Starting at target level 5",
                List.of(),
                List.of(),
                originFeatId
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result.level()).isEqualTo(1);
        assertThat(result.experiencePoints()).isEqualTo(6500);
        assertThat(result.hasPendingLevelChoices()).isTrue();
        assertThat(result.hpCalculationMethod()).isEqualTo(HpCalculationMethod.ROLLED);
        // Level 1 Fighter: 10 (d10) + 2 (CON) + 2 (Tough) = 14 HP
        assertThat(result.hitPointsMax()).isEqualTo(14);
        assertThat(result.feats()).hasSize(1);
    }

    @Test
    @DisplayName("TP-01: Creates character with ROLLED scores and consumes token atomically")
    void shouldCreateCharacterWithRolledScoresAndConsumeToken() {
        UUID abilityRollId = UUID.randomUUID();
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", false, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Rolled Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                16, 14, 13, 12, 10, 9,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                "Rolled notes"
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        verify(abilityRollRepository).markAsUsed(abilityRollId);
    }

    @Test
    @DisplayName("TE-01: Reused ability roll token throws RollConflictException")
    void shouldRejectReusedAbilityRollToken() {
        UUID abilityRollId = UUID.randomUUID();
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", true, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Reused Roll Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                16, 14, 13, 12, 10, 9,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(RollConflictException.class);
    }

    @Test
    @DisplayName("TE-01: Tampered score permutation with ROLLED method throws CharacterValidationException")
    void shouldRejectTamperedScoresWithRolledMethod() {
        UUID abilityRollId = UUID.randomUUID();
        AbilityRoll roll = new AbilityRoll(abilityRollId, userId, List.of(16, 14, 13, 12, 10, 9), "[]", false, Instant.now());
        when(abilityRollRepository.findById(abilityRollId)).thenReturn(Optional.of(roll));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Tampered Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.ROLLED,
                18, 14, 13, 12, 10, 9,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                abilityRollId,
                HpCalculationMethod.MAX,
                null,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(CharacterValidationException.class);
    }

    @Test
    @DisplayName("TP-02: Creates multiclass character with multiple class progressions and skill proficiencies")
    void shouldCreateMulticlassCharacterSuccessfully() {
        UUID secondaryArchetypeId = UUID.randomUUID();
        Archetype rogue = new Archetype(
                secondaryArchetypeId, "Rogue", HitDiceEnum.D8, "DEX",
                List.of(), List.of(), List.of(), SpellcastingType.NONE, null, Ruleset.SRD_2024, "SRD", null
        );
        when(referenceDataRepository.findArchetypeById(secondaryArchetypeId)).thenReturn(Optional.of(rogue));

        List<ClassProgressionCommand> multiclassProgressions = List.of(
                new ClassProgressionCommand(archetypeId, null, 3),
                new ClassProgressionCommand(secondaryArchetypeId, null, 2)
        );
        List<String> chosenSkills = List.of("Acrobatics", "Survival");

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Multiclass Veteran",
                5,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.AVERAGE,
                null,
                "Multiclass notes",
                multiclassProgressions,
                chosenSkills
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        assertThat(result.classProgressions()).hasSize(2);
        assertThat(result.classProgressions().get(0).getClassLevel()).isEqualTo(3);
        assertThat(result.classProgressions().get(1).getClassLevel()).isEqualTo(2);

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter savedChar = captor.getValue();
        assertThat(savedChar.getSkillProficiencies()).containsExactlyInAnyOrder("Acrobatics", "Survival");
    }

    @Test
    @DisplayName("TE-02: Throws CharacterValidationException when multiclass level sum does not match total character level")
    void shouldThrowValidationExceptionWhenMulticlassLevelsDoNotMatchTotalLevel() {
        UUID secondaryArchetypeId = UUID.randomUUID();
        List<ClassProgressionCommand> multiclassProgressions = List.of(
                new ClassProgressionCommand(archetypeId, null, 2),
                new ClassProgressionCommand(secondaryArchetypeId, null, 1)
        );

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Invalid Multiclass Hero",
                5,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.AVERAGE,
                null,
                "Invalid multiclass",
                multiclassProgressions,
                List.of()
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Sum of class progression levels");
    }

    @Test
    @DisplayName("BUGFIX-001: Character creation with Origin Feat produces valid JSON choices and avoids duplicate saveCharacterFeat")
    void shouldCreateCharacterWithOriginFeatAndValidJsonChoices() {
        UUID originFeatId = UUID.randomUUID();
        Feat originFeat = new Feat(
                originFeatId,
                Ruleset.SRD_2024,
                "tough",
                "Tough",
                "Robusto",
                null,
                "Hit points increase",
                "SRD_2024",
                null,
                FeatType.ORIGIN,
                new MechanicalEffects(
                        Map.of(),
                        0,
                        0,
                        0,
                        2,
                        List.of(),
                        null,
                        null,
                        null,
                        null,
                        RestType.NONE
                )
        );
        when(referenceDataRepository.findFeatById(originFeatId)).thenReturn(Optional.of(originFeat));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Hero With Feat",
                5,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 1, "CONSTITUTION", 1, "DEXTERITY", 1),
                null,
                HpCalculationMethod.AVERAGE,
                null,
                List.of(),
                "Level 5 hero with origin feat",
                List.of(new ClassProgressionCommand(archetypeId, null, 5)),
                List.of("Athletics", "Survival"),
                originFeatId
        );

        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CharacterSheetResult result = service.createCharacter(command);

        assertThat(result).isNotNull();
        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        PlayerCharacter savedChar = captor.getValue();

        assertThat(savedChar.getFeats()).isNotEmpty();
        for (CharacterFeat cf : savedChar.getFeats()) {
            assertThat(cf.getChoicesJson())
                    .startsWith("{")
                    .endsWith("}")
                    .contains("\"source\"");
        }
    }

    @Test
    @DisplayName("Should save character with valid avatar and reject SVG or script injection")
    void shouldHandleAvatarUrlOnCreation() {
        UUID originFeatId = UUID.randomUUID();
        Feat originFeat = new Feat(
                originFeatId,
                Ruleset.SRD_2024,
                "tough",
                "Tough",
                "Robusto",
                null,
                "Gain extra HP",
                "SRD",
                null,
                FeatType.ORIGIN,
                new MechanicalEffects(
                        Map.of(),
                        0,
                        0,
                        0,
                        2,
                        List.of(),
                        null,
                        null,
                        null,
                        null,
                        RestType.NONE
                )
        );

        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.of(lineage));
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.of(background));
        when(referenceDataRepository.findFeatById(originFeatId)).thenReturn(Optional.of(originFeat));
        when(playerCharacterRepository.save(any(PlayerCharacter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String validAvatar = "data:image/webp;base64,UklGRkAAAABXRUJQVlA4...";
        CreateCharacterCommand validCommand = new CreateCharacterCommand(
                userId,
                "Avatar Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 1, "CONSTITUTION", 1, "DEXTERITY", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                List.of(),
                "Notes",
                List.of(),
                List.of("Athletics"),
                originFeatId,
                validAvatar
        );

        CharacterSheetResult result = service.createCharacter(validCommand);
        assertThat(result).isNotNull();

        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(playerCharacterRepository).save(captor.capture());
        assertThat(captor.getValue().getAvatarUrl()).isEqualTo(validAvatar);

        // SVG rejection
        CreateCharacterCommand svgCommand = new CreateCharacterCommand(
                userId,
                "Malicious Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 1, "CONSTITUTION", 1, "DEXTERITY", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                List.of(),
                "Notes",
                List.of(),
                List.of("Athletics"),
                originFeatId,
                "data:image/svg+xml;base64,PHN2Zz48L3N2Zz4="
        );

        assertThatThrownBy(() -> service.createCharacter(svgCommand))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("SVG");
    }

    @Test
    @DisplayName("Should reject character creation when origin feat prerequisites are not met")
    void shouldRejectOriginFeatWhenPrerequisitesNotMet() {
        UUID originFeatId = UUID.randomUUID();
        Feat featWithPrereq = new Feat(
                originFeatId,
                Ruleset.SRD_2024,
                "high-str-origin-feat",
                "High Strength Feat",
                "Talento de Força Alta",
                "Força 18+",
                "Requer força extraordinária",
                "SRD",
                null,
                FeatType.ORIGIN,
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(),
                        null, null, null, null, RestType.NONE,
                        null, false, null,
                        List.of(new FeatPrerequisite("ABILITY_SCORE", "STRENGTH", 18, "Força 18+"))
                )
        );

        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.of(lineage));
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.of(background));
        when(referenceDataRepository.findFeatById(originFeatId)).thenReturn(Optional.of(featWithPrereq));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Weak Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8, // STR 15 < 18
                Map.of("DEXTERITY", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                List.of(),
                "Notes",
                List.of(),
                List.of("Athletics"),
                originFeatId,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Pré-requisito do talento não atendido");
    }

    @Test
    @DisplayName("TR-01: Should reject character creation when originFeatId has FeatType != ORIGIN")
    void shouldRejectNonOriginFeatAsOriginFeat() {
        UUID generalFeatId = UUID.randomUUID();
        Feat generalFeat = new Feat(
                generalFeatId,
                Ruleset.SRD_2024,
                "general-feat",
                "General Feat",
                "Talento Geral",
                null,
                "A general feat",
                "SRD",
                null,
                FeatType.GENERAL,
                new MechanicalEffects(
                        Map.of(), 0, 0, 0, 0, List.of(),
                        null, null, null, null, RestType.NONE,
                        null, false, null, List.of()
                )
        );

        when(referenceDataRepository.findArchetypeById(archetypeId)).thenReturn(Optional.of(archetype));
        when(referenceDataRepository.findLineageById(lineageId)).thenReturn(Optional.of(lineage));
        when(referenceDataRepository.findBackgroundById(backgroundId)).thenReturn(Optional.of(background));
        when(referenceDataRepository.findFeatById(generalFeatId)).thenReturn(Optional.of(generalFeat));

        CreateCharacterCommand command = new CreateCharacterCommand(
                userId,
                "Invalid Feat Hero",
                1,
                Ruleset.SRD_2024,
                lineageId,
                backgroundId,
                archetypeId,
                null,
                AbilityScoreMethod.STANDARD_ARRAY,
                15, 14, 13, 12, 10, 8,
                Map.of("STRENGTH", 2, "CONSTITUTION", 1),
                null,
                HpCalculationMethod.MAX,
                null,
                List.of(),
                "Notes",
                List.of(),
                List.of("Athletics"),
                generalFeatId,
                null
        );

        assertThatThrownBy(() -> service.createCharacter(command))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("is not an ORIGIN feat");
    }
}
