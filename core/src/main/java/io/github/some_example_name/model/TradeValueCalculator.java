package io.github.some_example_name.model;

public class TradeValueCalculator {

    /**
     * Calcula o Trade Value final do jogador (0 a 100)
     */
    public static int calculateTradeValue(Player player) {
        int ovrScore = calculateOvrScore(player.getOverall());
        int ageScore = calculateAgeScore(player.getAge());
        int potentialScore = calculatePotentialScore(player.getOverall(), player.getPotential());
        int positionScore = calculatePositionScore(player.getPosition());

        // Pesos de Ponderação
        // OVR (45%) | Idade (25%) | Potencial (20%) | Posição (10%)
        double finalScore = (ovrScore * 0.45)
            + (ageScore * 0.25)
            + (potentialScore * 0.20)
            + (positionScore * 0.10);

        return (int) Math.min(99, Math.max(10, Math.round(finalScore)));
    }

    private static int calculateOvrScore(int overall) {
        // Jogadores de elite (85+) sobram exponencialmente em valor
        if (overall >= 90) return 95 + (overall - 90);
        if (overall >= 80) return 75 + (int) ((overall - 80) * 2.0);
        if (overall >= 70) return 45 + (int) ((overall - 70) * 3.0);
        return Math.max(10, overall - 20);
    }

    private static int calculateAgeScore(int age) {
        // Curva de idade de franquia: Auge de valor entre 20 e 24 anos
        if (age <= 21) return 100; // Máximo valor futuro
        if (age <= 24) return 92;
        if (age <= 27) return 80;
        if (age <= 29) return 65;
        if (age <= 32) return 40;
        return 15; // Veteranos em fase final de contrato/carreira
    }

    private static int calculatePotentialScore(int overall, int potential) {
        int gap = Math.max(0, potential - overall);
        // Teto de evolução aumenta drasticamente o valor de troca
        return Math.min(100, (potential) + (gap * 3));
    }

    private static int calculatePositionScore(String position) {
        // Posições escassas/de alto impacto valem mais no mercado
        if (position.matches("ST|CAM|CB")) return 90;  // Artilheiros, Maestros e Zagueiros
        if (position.matches("RW|LW|CDM|GK")) return 80;
        return 70; // Laterais / Meias de lado
    }
}
