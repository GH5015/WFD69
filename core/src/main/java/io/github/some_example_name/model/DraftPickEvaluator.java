package io.github.some_example_name.model;

public class DraftPickEvaluator {

    /**
     * Calcula o valor em pontos de troca de uma Pick do Draft.
     */
    public static long calculatePickValue(DraftPick pick, int currentSeasonYear) {
        int projected = Math.max(1, Math.min(20, pick.getProjectedPosition()));
        long baseValue = calculateBaseValue(pick.getRound(), projected);

        /* Uma projeção distante do fim da temporada não vale o mesmo que uma pick definida. */
        double confidence = pick.getProjectedPositionConfidence();
        baseValue = Math.round(baseValue * (0.55 + (confidence * 0.45)));

        int yearsInFuture = pick.getYear() - currentSeasonYear;
        if (yearsInFuture > 0) {
            baseValue *= Math.pow(0.85, yearsInFuture);
        }

        return Math.round(baseValue);
    }

    private static long calculateBaseValue(int round, int projected) {
        if (round == 1) {
            // Uma escolha de 1ª rodada precisa poder competir no mercado por
            // jogadores consolidados; as primeiras posições são premium.
            if (projected == 1) return 120L;
            if (projected <= 3) return 112L - ((projected - 2) * 5L);
            if (projected <= 5) return 96L - ((projected - 4) * 5L);
            if (projected <= 10) return 83L - ((projected - 6) * 5L);
            if (projected <= 15) return 58L - ((projected - 11) * 3L);
            return 40L - ((projected - 16) * 3L);
        }

        if (projected <= 3) return 46L - ((projected - 1) * 3L);
        if (projected <= 10) return 36L - ((projected - 4) * 2L);
        return Math.max(14L, 24L - ((projected - 11) * 1L));
    }

    /**
     * Retorna o valor percebido da Pick considerando a fase da franquia alvo.
     */
    public static long getPerceivedPickValue(Club targetClub, DraftPick pick, int currentYear) {
        long rawValue = calculatePickValue(pick, currentYear);
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(targetClub);

        switch (phase) {
            case REBUILDING:
                return Math.round(rawValue * 1.50);
            case SELLER:
                return Math.round(rawValue * 1.20);
            case BUYER:
                return Math.round(rawValue * 0.90);
            case CONTENDER:
            default:
                return Math.round(rawValue * 0.75);
        }
    }
}
