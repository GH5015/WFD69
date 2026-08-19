package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import java.util.Random;

public class PossessionEngine {
    private static final Random random = new Random();

    /**
     * Calcula quem mantém a posse no minuto atual com base na qualidade do meio-campo
     * e nos multiplicadores de posse de cada equipe.
     */
    public static Club determinePossession(Club home, TacticalModifiers homeMods, Club away, TacticalModifiers awayMods) {
        // Habilidade base de meio-campo de cada time (0 a 100)
        double homeBaseMidfield = home.getMidfieldRating();
        double awayBaseMidfield = away.getMidfieldRating();

        // Aplica o modificador vindo do TacticalEngine (ex: Passe Curto aumenta posse)
        double homePower = homeBaseMidfield * homeMods.possessionMultiplier;
        double awayPower = awayBaseMidfield * awayMods.possessionMultiplier;

        // Fator de aleatoriedade moderado do futebol (±15%)
        double homeRoll = homePower * (0.85 + random.nextDouble() * 0.30);
        double awayRoll = awayPower * (0.85 + random.nextDouble() * 0.30);

        return (homeRoll >= awayRoll) ? home : away;
    }
}
