package io.github.some_example_name.model;

public class DraftPickEvaluator {

    /**
     * Calcula o valor em pontos de troca de uma Pick do Draft.
     */
    public static long calculatePickValue(DraftPick pick, int currentSeasonYear) {
        long baseValue = (pick.getRound() == 1) ? 3_000_000L : 800_000L;

        if (pick.getRound() == 1) {
            int projected = pick.getProjectedPosition();
            if (projected <= 2) {
                baseValue *= 3.5;
            } else if (projected <= 5) {
                baseValue *= 2.2;
            } else if (projected <= 10) {
                baseValue *= 1.5;
            } else {
                baseValue *= 0.9;
            }
        }

        int yearsInFuture = pick.getYear() - currentSeasonYear;
        if (yearsInFuture > 0) {
            baseValue *= Math.pow(0.85, yearsInFuture);
        }

        return Math.round(baseValue);
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
