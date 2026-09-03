package io.github.some_example_name.model;

/** Fórmulas únicas dos efeitos da comissão técnica em todos os sistemas. */
public final class StaffImpact {
    private StaffImpact() { }

    public static int clampStars(int stars) { return Math.max(1, Math.min(5, stars)); }

    /** Treinador: de -3% a +3% no desempenho tático. */
    public static double coachPerformance(int stars) {
        return 0.97d + (clampStars(stars) - 1) * 0.015d;
    }

    /** Scout: 1,0%; 1,2%; 1,5%; 1,8%; 2,1% de conhecimento por dia. */
    public static double scoutingDailyProgress(int stars) {
        double[] rates = {1.0d, 1.2d, 1.5d, 1.8d, 2.1d};
        return rates[clampStars(stars) - 1];
    }

    /** Scout: reduz progressivamente a margem de erro dos relatórios. */
    public static double scoutingErrorMultiplier(int stars) {
        double[] factors = {1.30d, 1.15d, 1.0d, 0.80d, 0.62d};
        return factors[clampStars(stars) - 1];
    }

    /** Treinador + diretor de desenvolvimento: influência na evolução semanal. */
    public static double developmentMultiplier(int coachStars, int developmentStars) {
        return 0.90d + ((clampStars(coachStars) + clampStars(developmentStars)) * 0.025d);
    }

    /** Preparador físico: acelera a recuperação entre jogos. */
    public static double fitnessRecoveryMultiplier(int stars) {
        return 0.78d + clampStars(stars) * 0.075d;
    }

    /** Preparador físico: reduz o desgaste sofrido durante uma partida. */
    public static double matchFatigueMultiplier(int stars) {
        return 1.12d - clampStars(stars) * 0.06d;
    }

    /** Médico: recuperação adicional aplicada semanalmente. */
    public static int medicalRecoveryBonus(int stars) {
        return Math.max(0, clampStars(stars) - 3);
    }

    /** Médico: reduz a probabilidade base de uma nova lesão. */
    public static double injuryRiskMultiplier(int stars) {
        return 1.15d - (clampStars(stars) - 1) * 0.0875d;
    }

    /** Médico: reduz o risco durante a janela de recaída. */
    public static double relapseRiskMultiplier(int stars) {
        return Math.max(1.04d, 1.34d - clampStars(stars) * 0.06d);
    }
}
