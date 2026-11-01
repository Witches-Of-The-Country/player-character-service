package com.dungeoneer.playerCharacter.application.dto;

import com.dungeoneer.playerCharacter.domain.model.CurrencyPouch;

public record CurrencyPouchDto(
        int cp,
        int sp,
        int ep,
        int gp,
        int pp,
        long totalCopper,
        double totalGold
) {
    public static CurrencyPouchDto fromDomain(CurrencyPouch pouch) {
        if (pouch == null) {
            return new CurrencyPouchDto(0, 0, 0, 0, 0, 0L, 0.0);
        }
        return new CurrencyPouchDto(
                pouch.cp(),
                pouch.sp(),
                pouch.ep(),
                pouch.gp(),
                pouch.pp(),
                pouch.toTotalCopper(),
                pouch.toTotalGold()
        );
    }
}
