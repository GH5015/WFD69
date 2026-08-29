package io.github.some_example_name.model;

public class TradeValueCalculator {

    /**
     * Calcula o Trade Value final do jogador (0 a 100)
     */
    public static int calculateTradeValue(Player player) {
        return calculateTradeValue(player, -1);
    }

    /**
     * Nota visual de 0–99. A negociação usa {@link #calculateMarketPoints}
     * para não permitir que duas notas medianas sejam somadas como uma estrela.
     */
    public static int calculateTradeValue(Player player, int currentSeasonYear) {
        long points = calculateMarketPoints(player, currentSeasonYear);
        double normalized = 4d + 95d * (1d - Math.exp(-points / 310d));
        return (int) Math.min(99, Math.max(3, Math.round(normalized)));
    }

    public static long calculateMarketPoints(Player player) {
        return calculateMarketPoints(player, -1);
    }

    /**
     * Escala interna aberta. OVR é exponencial e, portanto, o salto de 88
     * para 94 vale muito mais que o salto de 74 para 80. Idade, potencial,
     * posição e contrato apenas modulam essa base.
     */
    public static long calculateMarketPoints(Player player, int currentSeasonYear) {
        if (player == null) return 0L;

        int overall = Math.max(40, Math.min(99, player.getOverall()));
        double overallBase = 8d * Math.pow(1.18d, overall - 65d);

        int gap = Math.max(0, player.getTruePotential() - overall);
        double potentialMultiplier = 1d;
        if (player.getAge() <= 23) {
            potentialMultiplier += Math.min(0.62d, gap * 0.028d);
        } else if (player.getAge() <= 27) {
            potentialMultiplier += Math.min(0.24d, gap * 0.012d);
        }

        double ageMultiplier;
        if (player.getAge() <= 20) ageMultiplier = 1.08d;
        else if (player.getAge() <= 24) ageMultiplier = 1.12d;
        else if (player.getAge() <= 28) ageMultiplier = 1.04d;
        else if (player.getAge() <= 30) ageMultiplier = 0.94d;
        else if (player.getAge() <= 32) ageMultiplier = 0.82d;
        else if (player.getAge() <= 34) ageMultiplier = 0.66d;
        else ageMultiplier = 0.50d;

        double positionMultiplier = positionMultiplier(player.getPosition());
        double contractMultiplier = 1d;
        if (currentSeasonYear >= 0) {
            int remaining = player.getRemainingContractYears(currentSeasonYear);
            if (remaining <= 0) contractMultiplier = 0.45d;
            else if (remaining == 1) contractMultiplier = 0.68d;
            else if (remaining == 2) contractMultiplier = 0.90d;
            else if (remaining == 3) contractMultiplier = 1.05d;
            else contractMultiplier = 1.14d;
        }

        return Math.max(2L, Math.round(
            overallBase * potentialMultiplier * ageMultiplier
                * positionMultiplier * contractMultiplier
        ));
    }

    private static double positionMultiplier(String position) {
        if (position == null) return 1d;
        if (position.matches("ST|CAM|CB")) return 1.05d;
        if (position.matches("RW|LW|CDM|GK")) return 1.02d;
        return 1d;
    }
}
