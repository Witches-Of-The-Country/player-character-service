package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassProgressionTest {

    private final UUID archetypeId = UUID.randomUUID();
    private final UUID subclassId = UUID.randomUUID();

    @Test
    @DisplayName("Should create ClassProgression with default 0 hit dice spent")
    void shouldCreateWithDefaultZeroHitDiceSpent() {
        ClassProgression progression = ClassProgression.createInitial(archetypeId, subclassId, 5);

        assertThat(progression.getClassLevel()).isEqualTo(5);
        assertThat(progression.getHitDiceSpent()).isEqualTo(0);
        assertThat(progression.getAvailableHitDice()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should create ClassProgression with custom hit dice spent within bounds")
    void shouldCreateWithValidHitDiceSpent() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), UUID.randomUUID(), archetypeId, subclassId, 5, 3);

        assertThat(progression.getClassLevel()).isEqualTo(5);
        assertThat(progression.getHitDiceSpent()).isEqualTo(3);
        assertThat(progression.getAvailableHitDice()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when hit dice spent is negative or exceeds class level")
    void shouldRejectInvalidHitDiceSpentInConstructor() {
        assertThatThrownBy(() -> new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, 5, -1))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice spent must be between 0 and class level");

        assertThatThrownBy(() -> new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, 5, 6))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice spent must be between 0 and class level");
    }

    @Test
    @DisplayName("Should correctly spend available hit dice")
    void shouldSpendHitDiceSuccessfully() {
        ClassProgression progression = ClassProgression.createInitial(archetypeId, subclassId, 6);

        ClassProgression spent1 = progression.spendHitDice(2);
        assertThat(spent1.getHitDiceSpent()).isEqualTo(2);
        assertThat(spent1.getAvailableHitDice()).isEqualTo(4);

        ClassProgression spent2 = spent1.spendHitDice(4);
        assertThat(spent2.getHitDiceSpent()).isEqualTo(6);
        assertThat(spent2.getAvailableHitDice()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reject spending more hit dice than available or non-positive amount")
    void shouldRejectInvalidSpendHitDice() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, 4, 3);

        // only 1 available
        assertThatThrownBy(() -> progression.spendHitDice(2))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Cannot spend 2 hit dice. Only 1 available");

        assertThatThrownBy(() -> progression.spendHitDice(0))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice spend count must be greater than 0");

        assertThatThrownBy(() -> progression.spendHitDice(-2))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice spend count must be greater than 0");
    }

    @Test
    @DisplayName("Should correctly recover spent hit dice clamping to 0 spent")
    void shouldRecoverHitDiceSuccessfully() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, 6, 4);

        ClassProgression recovered1 = progression.recoverHitDice(2);
        assertThat(recovered1.getHitDiceSpent()).isEqualTo(2);
        assertThat(recovered1.getAvailableHitDice()).isEqualTo(4);

        // Recovering more than spent clamps spent to 0
        ClassProgression recoveredAll = recovered1.recoverHitDice(5);
        assertThat(recoveredAll.getHitDiceSpent()).isEqualTo(0);
        assertThat(recoveredAll.getAvailableHitDice()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should reject non-positive recovery count")
    void shouldRejectInvalidRecoverHitDice() {
        ClassProgression progression = new ClassProgression(UUID.randomUUID(), null, archetypeId, subclassId, 4, 2);

        assertThatThrownBy(() -> progression.recoverHitDice(0))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice recover count must be greater than 0");

        assertThatThrownBy(() -> progression.recoverHitDice(-1))
                .isInstanceOf(CharacterValidationException.class)
                .hasMessageContaining("Hit dice recover count must be greater than 0");
    }
}
