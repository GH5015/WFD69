package io.github.some_example_name.model;

public class DraftPickEvaluator {

    /**
     * Calcula o valor em pontos de troca de uma Pick do Draft.
     */
    public static long calculatePickValue(DraftPick pick, int currentSeasonYear) {
        int projected = Math.max(1, Math.min(pick.getPicksPerRound(), pick.getProjectedPosition()));
        long baseValue = calculateBaseValue(pick.getRound(), projected);

        /* Enquanto a posição não é definida, a incerteza reduz fortemente o
         * valor. A Lottery concluída aproxima a confiança de 100%. */
        double confidence = pick.getProjectedPositionConfidence();
        baseValue = Math.round(baseValue * (0.52 + (confidence * 0.48)));

        /* A escolha do próximo Draft vale integralmente. Só anos além dele
         * sofrem desconto de valor presente e maior incerteza. */
        if (currentSeasonYear >= 0) {
            int yearsBeyondNextDraft = Math.max(0, pick.getYear() - currentSeasonYear - 1);
            if (yearsBeyondNextDraft > 0) {
                baseValue *= Math.pow(0.78, yearsBeyondNextDraft);
            }
        }

        return Math.round(baseValue);
    }

    private static long calculateBaseValue(int round, int projected) {
        if (round == 1) {
            if (projected == 1) return 520L;
            if (projected == 2) return 450L;
            if (projected == 3) return 405L;
            if (projected <= 5) return 365L - ((projected - 4) * 38L);
            if (projected <= 10) return 292L - ((projected - 6) * 21L);
            if (projected <= 15) return 180L - ((projected - 11) * 12L);
            if (projected <= 20) return 116L - ((projected - 16) * 11L);
            return Math.max(32L, 72L - (projected - 20) * 4L);
        }

        if (projected <= 3) return 108L - ((projected - 1) * 10L);
        if (projected <= 10) return 80L - ((projected - 4) * 7L);
        return Math.max(18L, 38L - ((projected - 11) * 2L));
    }

    /**
     * Retorna o valor percebido da Pick considerando a fase da franquia alvo.
     */
    public static long getPerceivedPickValue(Club targetClub, DraftPick pick, int currentYear) {
        long rawValue = calculatePickValue(pick, currentYear);
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(targetClub);

        switch (phase) {
            case REBUILDING:
                return Math.round(rawValue * 1.38);
            case SELLER:
                return Math.round(rawValue * 1.18);
            case BUYER:
                return Math.round(rawValue * 0.92);
            case CONTENDER:
            default:
                return Math.round(rawValue * 0.78);
        }
    }
}
