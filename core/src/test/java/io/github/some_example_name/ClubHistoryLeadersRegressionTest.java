package io.github.some_example_name;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.Arrays;

/** Verifica que a aba História recebe recordes individuais após cada jogo. */
public final class ClubHistoryLeadersRegressionTest {
    public static void main(String[] args) {
        Club club = new Club("Histórico FC");
        Club opponent = new Club("Visitante FC");
        Player scorer = player("Artilheiro", Position.ST);
        Player creator = player("Garçom", Position.CAM);
        club.addPlayerToSquad(scorer);
        club.addPlayerToSquad(creator);

        Match firstMatch = new Match(club, opponent);
        firstMatch.recordStartingLineups(Arrays.asList(scorer, creator), java.util.Collections.emptyList());
        firstMatch.finishPlayerMinuteTracking();
        firstMatch.addGoalScorer(scorer);
        firstMatch.addGoalScorer(scorer);
        firstMatch.addAssister(creator);
        firstMatch.addAssister(creator);
        club.recordPlayerMatchStatistics(firstMatch);

        Match secondMatch = new Match(club, opponent);
        secondMatch.recordStartingLineups(Arrays.asList(scorer, creator), java.util.Collections.emptyList());
        secondMatch.finishPlayerMinuteTracking();
        secondMatch.addGoalScorer(creator);
        secondMatch.addAssister(scorer);
        club.recordPlayerMatchStatistics(secondMatch);

        require("Artilheiro".equals(club.getTopScorerName()) && club.getTopScorerGoals() == 2,
            "Top scorer was not persisted across matches");
        require("Garçom".equals(club.getTopAssisterName()) && club.getTopAssisterCount() == 2,
            "Top assister was not persisted across matches");
        require(club.getMostGamesCount() == 2,
            "Appearance leader must receive every played match");
        require(!"Sem registros".equals(club.getMostGamesPlayerName()),
            "Appearance leader name was not registered");
        System.out.println("Club history: scorers, assisters and appearances are recorded OK.");
    }

    private static Player player(String name, Position position) {
        return new Player(name, "Brasil", position, null, 22,
            new TechnicalAttributes(70, 70, 60, 70, 70, 50), 80, 10_000);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
