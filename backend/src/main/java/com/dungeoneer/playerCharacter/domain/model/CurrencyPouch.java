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

        int remCp = this.cp;
        int remSp = this.sp;
        int remEp = this.ep;
        int remGp = this.gp;
        int remPp = this.pp;

        // 1. Single-denomination exact match priority: GP, SP, CP, EP, PP
        if (costInCopper % COPPER_PER_GP == 0 && remGp >= (int) (costInCopper / COPPER_PER_GP)) {
            return new CurrencyPouch(remCp, remSp, remEp, remGp - (int) (costInCopper / COPPER_PER_GP), remPp);
        }
        if (costInCopper % COPPER_PER_SP == 0 && remSp >= (int) (costInCopper / COPPER_PER_SP)) {
            return new CurrencyPouch(remCp, remSp - (int) (costInCopper / COPPER_PER_SP), remEp, remGp, remPp);
        }
        if (remCp >= costInCopper) {
            return new CurrencyPouch(remCp - (int) costInCopper, remSp, remEp, remGp, remPp);
        }
        if (costInCopper % COPPER_PER_EP == 0 && remEp >= (int) (costInCopper / COPPER_PER_EP)) {
            return new CurrencyPouch(remCp, remSp, remEp - (int) (costInCopper / COPPER_PER_EP), remGp, remPp);
        }
        if (costInCopper % COPPER_PER_PP == 0 && remPp >= (int) (costInCopper / COPPER_PER_PP)) {
            return new CurrencyPouch(remCp, remSp, remEp, remGp, remPp - (int) (costInCopper / COPPER_PER_PP));
        }

        long remainingCost = costInCopper;

        while (remainingCost > 0) {
            // 2. Exact payment check across available whole coins without breaking any
            int[] exact = tryExactPayment(remainingCost, remCp, remSp, remEp, remGp, remPp);
            if (exact != null) {
                return new CurrencyPouch(
                        remCp - exact[0],
                        remSp - exact[1],
                        remEp - exact[2],
                        remGp - exact[3],
                        remPp - exact[4]
                );
            }

            // 3. Break single smallest sufficient higher coin if available
            if (remSp > 0 && COPPER_PER_SP >= remainingCost) {
                return breakCoinAndReturnChange(remCp, remSp - 1, remEp, remGp, remPp, COPPER_PER_SP - remainingCost);
            } else if (remEp > 0 && COPPER_PER_EP >= remainingCost) {
                return breakCoinAndReturnChange(remCp, remSp, remEp - 1, remGp, remPp, COPPER_PER_EP - remainingCost);
            } else if (remGp > 0 && COPPER_PER_GP >= remainingCost) {
                return breakCoinAndReturnChange(remCp, remSp, remEp, remGp - 1, remPp, COPPER_PER_GP - remainingCost);
            } else if (remPp > 0 && COPPER_PER_PP >= remainingCost) {
                return breakCoinAndReturnChange(remCp, remSp, remEp, remGp, remPp - 1, COPPER_PER_PP - remainingCost);
            }

            // 4. Spend maximum whole coins from largest available denomination <= remainingCost
            if (remPp > 0 && remainingCost >= COPPER_PER_PP) {
                int take = Math.min(remPp, (int) (remainingCost / COPPER_PER_PP));
                remPp -= take;
                remainingCost -= take * COPPER_PER_PP;
            } else if (remGp > 0 && remainingCost >= COPPER_PER_GP) {
                int take = Math.min(remGp, (int) (remainingCost / COPPER_PER_GP));
                remGp -= take;
                remainingCost -= take * COPPER_PER_GP;
            } else if (remEp > 0 && remainingCost >= COPPER_PER_EP) {
                int take = Math.min(remEp, (int) (remainingCost / COPPER_PER_EP));
                remEp -= take;
                remainingCost -= take * COPPER_PER_EP;
            } else if (remSp > 0 && remainingCost >= COPPER_PER_SP) {
                int take = Math.min(remSp, (int) (remainingCost / COPPER_PER_SP));
                remSp -= take;
                remainingCost -= take * COPPER_PER_SP;
            } else if (remCp > 0 && remainingCost >= COPPER_PER_CP) {
                int take = Math.min(remCp, (int) (remainingCost / COPPER_PER_CP));
                remCp -= take;
                remainingCost -= take * COPPER_PER_CP;
            }
        }

        return new CurrencyPouch(remCp, remSp, remEp, remGp, remPp);
    }

    private static int[] tryExactPayment(long cost, int cp, int sp, int ep, int gp, int pp) {
        long remaining = cost;

        int takePp = Math.min(pp, (int) (remaining / COPPER_PER_PP));
        remaining -= takePp * COPPER_PER_PP;

        int takeGp = Math.min(gp, (int) (remaining / COPPER_PER_GP));
        remaining -= takeGp * COPPER_PER_GP;

        int takeEp = Math.min(ep, (int) (remaining / COPPER_PER_EP));
        remaining -= takeEp * COPPER_PER_EP;

        int takeSp = Math.min(sp, (int) (remaining / COPPER_PER_SP));
        remaining -= takeSp * COPPER_PER_SP;

        int takeCp = Math.min(cp, (int) (remaining / COPPER_PER_CP));
        remaining -= takeCp * COPPER_PER_CP;

        if (remaining == 0) {
            return new int[]{takeCp, takeSp, takeEp, takeGp, takePp};
        }
        return null;
    }

    private static CurrencyPouch breakCoinAndReturnChange(int cp, int sp, int ep, int gp, int pp, long changeCopper) {
        int changeGp = (int) (changeCopper / COPPER_PER_GP);
        long remainder = changeCopper % COPPER_PER_GP;

        int changeSp = (int) (remainder / COPPER_PER_SP);
        remainder %= COPPER_PER_SP;

        int changeCp = (int) remainder;

        return new CurrencyPouch(
                cp + changeCp,
                sp + changeSp,
                ep,
                gp + changeGp,
                pp
        );
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
