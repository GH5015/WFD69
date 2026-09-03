package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class MatchMoraleRegressionTest {
    public static void main(String[] args) {
        for (int goals : new int[]{0, 1, 2}) {
            Club home = team("Casa"), away = team("Visitante");
            Match match = new Match(home, away);
            match.recordStartingLineups(home.getStartingXI(), away.getStartingXI());
            Player out = home.getSquad().get(0), in = home.getSquad().get(11), late = home.getSquad().get(12);
            match.registerSubstitution(out, in, 60, home);
            match.registerSubstitution(home.getSquad().get(1), late, 90, home);
            match.registerPlayerExit(home.getSquad().get(2), 30);
            // A late change to the final lineup must not determine who gains morale.
            home.getTacticsMap().clear();
            home.getStartingXI().remove(out);
            home.getStartingXI().add(home.getSquad().get(15));
            match.finishPlayerMinuteTracking();
            match.setResult(goals, 1);
            match.applyPostMatchMorale();
            require(home.getSquad().get(15).getMorale() == 48, "Unused player gained from final lineup");
            for (int i = 13; i < 23; i++) require(home.getSquad().get(i).getMorale() == 48, "Unused home player not penalized");
            for (int i = 11; i < 23; i++) require(away.getSquad().get(i).getMorale() == 48, "Unused away player not penalized");
            if (goals == 2) {
                for (int i = 0; i <= 12; i++) require(home.getSquad().get(i).getMorale() > 50, "Entrant did not benefit from win");
            } else if (goals == 0) require(out.getMorale() < 50, "Loss no longer affects participants");
            int morale = out.getMorale();
            match.applyPostMatchMorale();
            require(out.getMorale() == morale && home.getSquad().get(15).getMorale() == 48, "Morale applied twice");
            require(match.getParticipantsForClub(home).contains(late), "Last-minute entrant missing");
        }
        Club low = team("Limite"), rival = team("Outro");
        for (Player player : low.getSquad()) player.setMorale(1);
        Match bounds = new Match(low, rival); bounds.recordStartingLineups(low.getStartingXI(), rival.getStartingXI());
        low.getSquad().get(0).setMorale(99);
        bounds.setResult(2, 0); bounds.applyPostMatchMorale();
        require(low.getSquad().get(0).getMorale() == 100 && low.getSquad().get(22).getMorale() == 0, "Morale bounds exceeded");
        System.out.println("Match morale: starters, substitutes, exits, 90-minute entrant, unused players, both teams, results and idempotence OK.");
    }
    private static Club team(String name) {
        Club club = new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
        for (int i = 0; i < 23; i++) {
            Player player = new Player(name + i, "Brasil", Position.CM, null, 24,
                new TechnicalAttributes(70, 70, 70, 70, 70, 60), 90, 10_000);
            player.transferTo(club); player.setMorale(50);
            if (i < 11) { club.getStartingXI().add(player); club.getTacticsMap().put(i, player); }
        }
        return club;
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
