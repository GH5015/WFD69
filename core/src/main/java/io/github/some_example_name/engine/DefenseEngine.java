package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import java.util.Random;

public class DefenseEngine {
    private static final Random random = new Random();

    /**
     * Retorna a resistência defensiva do time sem a bola.
     */
    public static double calculateDefensePower(Club team, TacticalModifiers mods) {
        double baseDefense = team.getDefensiveRating();

        // 1. Aplica o multiplicador de defesa (diminui se for muito ofensivo)
        double totalPower = baseDefense * mods.defenseMultiplier
            * mods.defensiveCoverageMultiplier;

        // Pressão deixou de ser bônus defensivo fixo. Recuperações e quebras
        // são resolvidas como eventos de posse pela MatchEngine.
        double defensePerformance = 0.88 + (random.nextDouble() * 0.24);
        return totalPower * defensePerformance;
    }

    /**
     * Avalanche de risco: Verifica se o time cometeu falta perigosa ou levou cartão.
     */
    public static String checkFoulOrCard(TacticalModifiers defMods) {
        double cardChance = 0.02 * defMods.cardRiskMultiplier; // Chance base por minuto de falta pesada

        if (random.nextDouble() < cardChance) {
            if (random.nextDouble() < 0.25) {
                return "CARTAO_AMARELO";
            } else {
                return "FALTA";
            }
        }
        return "NENHUM";
    }
}
