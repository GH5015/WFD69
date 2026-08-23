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
            if (projected == 1) return 74L;
            if (projected <= 3) return 68L - ((projected - 2) * 3L);
            if (projected <= 5) return 58L - ((projected - 4) * 3L);
            if (projected <= 10) return 51L - ((projected - 6) * 3L);
            if (projected <= 15) return 35L - ((projected - 11) * 2L);
            return 24L - ((projected - 16) * 2L);
        }

        if (projected <= 3) return 26L - ((projected - 1) * 2L);
        if (projected <= 10) return 20L - ((projected - 4) * 1L);
        return Math.max(6L, 13L - ((projected - 11) / 2L));
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
