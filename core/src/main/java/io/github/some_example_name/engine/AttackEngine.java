package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import java.util.Random;

public class AttackEngine {
    private static final Random random = new Random();

    /**
     * Retorna a força total de ataque para o lance atual.
     */
    public static double calculateAttackPower(Club team, TacticalModifiers mods, boolean isCounterAttack) {
        double baseAttack = team.getAttackingRating();

        // 1. Aplica o modificador principal de ataque (Ritmo + Mentalidade + Amplitude)
        double totalPower = baseAttack * mods.attackMultiplier;

        // 2. Bônus por Contra-Ataque (se a jogada nasceu de um roubo rápido)
        if (isCounterAttack) {
            totalPower *= mods.counterAttackMultiplier;
        }

        // 3. Variação momentânea da jogada
        double playQuality = 0.90 + (random.nextDouble() * 0.20); // 90% a 110%
        return totalPower * playQuality;
    }

    /**
     * Decide o tipo de chance gerada (Cruzamento na área vs Infiltração central)
     */
    public static boolean isCrossingPlay(TacticalModifiers mods) {
        // Quanto maior a amplitude, maior a chance de ser cruzamento pelas pontas
        double chance = 0.35 * mods.crossingMultiplier;
        return random.nextDouble() < chance;
    }
}
