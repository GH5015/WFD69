package io.github.some_example_name;

import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;

public final class PlayoffPenaltyShootoutRegressionTest {
    public static void main(String[] args) {
        for (int attempt = 0; attempt < 40; attempt++) {
            Club home = team("Casa " + attempt);
            Club away = team("Visitante " + attempt);
            Match tiedPlayoff = new Match(home, away);
            tiedPlayoff.setStage("QUARTAS");
            tiedPlayoff.setHomeGoals(attempt % 3);
            tiedPlayoff.setAwayGoals(attempt % 3);

            new MatchEngine().finalizeMatch(tiedPlayoff);

            require(tiedPlayoff.hasPenaltyShootout(), "Empate de playoff terminou sem pênaltis");
            require(
                tiedPlayoff.getHomePenaltyGoals() != tiedPlayoff.getAwayPenaltyGoals(),
                "Disputa de pênaltis terminou empatada"
            );
            require(tiedPlayoff.getWinningClub() != null, "Playoff ficou sem vencedor");
        }

        Match regular = new Match(team("Regular A"), team("Regular B"));
        regular.setStage("REGULAR");
        regular.setHomeGoals(1);
        regular.setAwayGoals(1);
        new MatchEngine().finalizeMatch(regular);
        require(!regular.hasPenaltyShootout(), "Partida regular recebeu disputa de pênaltis");
        require(regular.getWinningClub() == null, "Empate regular recebeu vencedor");

        System.out.println("Playoffs: empates resolvidos nos pênaltis e empates regulares preservados.");
    }

    private static Club team(String name) {
        return new Club(name, "Brasil", "Ocidental", 75, 20_000_000, "Arena", "santos.png");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
