package io.github.some_example_name;

import io.github.some_example_name.model.*;
import io.github.some_example_name.engine.*;
import java.util.*;

public final class TemporaryTacticsRegressionTest {
    public static void main(String[] args) {
        Club home = team("Casa"), away = team("Outro");
        Match match = new Match(home, away);
        MatchEngine engine = new MatchEngine();
        engine.prepareMatchLineups(match);
        List<Player> starters = new ArrayList<>(home.getStartingXI());
        Map<Integer, Player> slots = new HashMap<>(home.getTacticsMap());
        List<Player> bench = home.getBenchPlayers();
        Formation formation = home.getFormation();
        Player out = starters.get(1), in = bench.get(0);
        home.getTacticsMap().put(1, in);
        home.setStartingXI(new ArrayList<>(home.getTacticsMap().values()));
        match.registerSubstitution(out, in, 60, home);
        home.setFormation(Formation.F_532);
        home.setTempo(95); home.setMentalityValue(94); home.setPassing(83);
        home.setWidth(86); home.setPressure(98);
        away.setTempo(99);
        home.getBenchPlayers();
        match.capturePrematchTactics(); // Must not replace the original snapshot.
        match.restorePrematchTactics();
        require(home.getTempo() == 95, "Restored before final whistle");
        engine.finalizeMatch(match);
        require(home.getFormation() == formation && home.getTacticsMap().equals(slots), "Formation/slots not restored");
        require(home.getStartingXI().equals(starters) && home.getBenchPlayers().equals(bench), "Starters/bench not restored");
        require(home.getTempo() == 38 && home.getMentalityValue() == 50 && home.getPassing() == 25
            && home.getWidth() == 50 && home.getPressure() == 45 && away.getTempo() == 38, "Sliders not restored");
        require(match.getParticipantsForClub(home).contains(in), "Substitute erased from match history");
        require(match.getFinalTacticalReport(home) != null, "Actual tactical report missing");
        require(in.getFatigue() < 100, "Restoration erased fatigue");
        home.setTempo(71);
        match.restorePrematchTactics();
        require(home.getTempo() == 71, "Repeated restoration overwrote next plan");
        System.out.println("Temporary tactics: formation, lineup, bench, sliders, both teams, actual participants, fatigue and idempotence OK.");
    }
    private static Club team(String name) {
        Club club = new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
        club.setUserControlled(true);
        club.setFormation(Formation.F_433);
        for (int i = 0; i < 23; i++) {
            Player player = new Player(name + i, "Brasil", Position.CM, null, 24,
                new TechnicalAttributes(75, 75, 75, 75, 75, 75), 90, 10_000);
            player.transferTo(club);
            if (i < 11) club.getTacticsMap().put(i, player);
        }
        club.setStartingXI(new ArrayList<>(club.getTacticsMap().values()));
        club.setTempo(38); club.setMentalityValue(50); club.setPassing(25); club.setWidth(50); club.setPressure(45);
        return club;
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
