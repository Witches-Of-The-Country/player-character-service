package com.dungeoneer.playerCharacter.domain.model;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record CurrencyPouch(
        int cp,
        int sp,
        int ep,
        int gp,
        int pp
) {
    public static final long COPPER_PER_CP = 1L;
    public static final long COPPER_PER_SP = 10L;
    public static final long COPPER_PER_EP = 50L;
    public static final long COPPER_PER_GP = 100L;
    public static final long COPPER_PER_PP = 1000L;

    public CurrencyPouch {
        if (cp < 0 || sp < 0 || ep < 0 || gp < 0 || pp < 0) {
            throw new CharacterValidationException("Quantidades de moedas não podem ser negativas.");
        }
    }

    public static CurrencyPouch empty() {
        return new CurrencyPouch(0, 0, 0, 0, 0);
    }

    public static CurrencyPouch ofGold(int gp) {
        return new CurrencyPouch(0, 0, 0, gp, 0);
    }

    public static CurrencyPouch fromCopper(long totalCopper) {
        if (totalCopper < 0) {
            throw new CharacterValidationException("Total de cobre não pode ser negativo.");
        }
        int plat = (int) (totalCopper / COPPER_PER_PP);
        long remainder = totalCopper % COPPER_PER_PP;

        int gold = (int) (remainder / COPPER_PER_GP);
        remainder %= COPPER_PER_GP;

        int silver = (int) (remainder / COPPER_PER_SP);
        remainder %= COPPER_PER_SP;

        int copper = (int) remainder;

        return new CurrencyPouch(copper, silver, 0, gold, plat);
    }

    public long toTotalCopper() {
        return (long) cp * COPPER_PER_CP
                + (long) sp * COPPER_PER_SP
                + (long) ep * COPPER_PER_EP
                + (long) gp * COPPER_PER_GP
                + (long) pp * COPPER_PER_PP;
    }

    public double toTotalGold() {
        return toTotalCopper() / 100.0;
    }

    public boolean canAfford(CurrencyPouch cost) {
        if (cost == null) {
            return true;
        }
        return canAffordCopper(cost.toTotalCopper());
    }

    public boolean canAfford(long costCopper) {
        return canAffordCopper(costCopper);
    }

    public boolean canAffordCopper(long costCopper) {
        return this.toTotalCopper() >= costCopper;
    }

    public CurrencyPouch add(CurrencyPouch other) {
        if (other == null) {
            return this;
        }
        return new CurrencyPouch(
                this.cp + other.cp,
                this.sp + other.sp,
                this.ep + other.ep,
                this.gp + other.gp,
                this.pp + other.pp
        );
    }

    public CurrencyPouch deductWithConversion(CurrencyPouch cost) {
        if (cost == null) {
            return this;
        }
        return deductWithConversion(cost.toTotalCopper());
    }

    public CurrencyPouch deductWithConversion(long costInCopper) {
        if (costInCopper <= 0) {
            return this;
        }
        long currentTotal = toTotalCopper();
        if (currentTotal < costInCopper) {
            throw new CharacterValidationException(
                    "Fundos insuficientes. Custo: " + (costInCopper / 100.0) + " PO, Saldo disponível: " + (currentTotal / 100.0) + " PO."
            );
        }

        long remaining = currentTotal - costInCopper;
        return fromCopper(remaining);
    }

    public CurrencyPouch consolidate() {
        return fromCopper(toTotalCopper());
    }

    public static CurrencyPouch parseCostString(String costString) {
        if (costString == null || costString.trim().isEmpty()) {
            return empty();
        }

        int copper = 0;
        int silver = 0;
        int electrum = 0;
        int gold = 0;
        int platinum = 0;

        Pattern pattern = Pattern.compile("(\\d+)\\s*([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(costString.trim().toLowerCase());

        boolean found = false;
        while (matcher.find()) {
            found = true;
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "po", "gp", "ouro", "gold" -> gold += amount;
                case "pl", "plat", "platina", "platinum" -> platinum += amount;
                case "pp", "sp", "prata", "silver" -> silver += amount;
                case "pe", "ep", "electro", "electrum" -> electrum += amount;
                case "pc", "cp", "cobre", "copper" -> copper += amount;
                default -> {
                    // Default fallback: treat as gold
                    gold += amount;
                }
            }
        }

        if (!found) {
            try {
                int numeric = Integer.parseInt(costString.replaceAll("[^0-9]", ""));
                return ofGold(numeric);
            } catch (Exception ignored) {
                return empty();
            }
        }

        return new CurrencyPouch(copper, silver, electrum, gold, platinum);
    }
}
