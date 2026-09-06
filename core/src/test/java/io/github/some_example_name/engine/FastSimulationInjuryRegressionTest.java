package io.github.some_example_name.engine;

import io.github.some_example_name.model.AutomaticInjurySubstitutionService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class FastSimulationInjuryRegressionTest {
    public static void main(String[] args) {
        Club home = club("Casa");
        Club away = club("Visitante");
        home.setFormation(Formation.F_433);
        away.setFormation(Formation.F_433);

        Map<Integer, Player> lineup = new HashMap<>();
        for (int slot = 0; slot < 11; slot++) {
            Position position = Position.valueOf(Formation.F_433.getPositionSlots().get(slot));
            Player starter = player("Titular " + slot, position, 75);
            starter.transferTo(home);
            lineup.put(slot, starter);
        }
        home.setTacticsMap(lineup);

        Player replacement = player("Reserva", Position.CB, 70);
        replacement.transferTo(home);
        Match match = new Match(home, away);
        match.recordStartingLineups(new ArrayList<>(home.getStartingXI()), new ArrayList<>());

        Map<Integer, Player> beforeMinute = new HashMap<>(home.getTacticsMap());
        Player injured = beforeMinute.get(2);
        injured.injureForDays(12, "Distensão muscular");
        match.registerPlayerExit(injured, 31);
        home.removeUnavailablePlayersFromStartingXI();

        AutomaticInjurySubstitutionService.Result result =
            MatchEngine.replaceInjuredPlayerDuringFastSimulation(
                match, home, beforeMinute, new ArrayList<>(), 31
            );

        require(result != null, "A simulacao rapida nao substituiu o lesionado");
        require(result.injured == injured && result.replacement == replacement,
            "A simulacao rapida escolheu o jogador errado");
        require(home.getTacticsMap().get(2) == replacement,
            "O reserva nao ocupou o slot do lesionado");
        require(match.getParticipantsForClub(home).contains(replacement),
            "O reserva nao foi registrado como participante");

        System.out.println("Fast simulation injury: automatic replacement and participation OK.");
    }

    private static Club club(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }

    private static Player player(String name, Position position, int quality) {
        return new Player(name, "Brasil", position, null, 25,
            new TechnicalAttributes(quality, quality, quality, quality, quality, 60), 85, 20_000);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
