package com.dungeoneer.playerCharacter.domain.model;

public sealed interface ItemMechanics permits WeaponMechanics, ArmorMechanics, ConsumableMechanics, EmptyItemMechanics {
}
