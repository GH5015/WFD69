package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class AutomaticInjurySubstitutionRegressionTest {
    public static void main(String[] args) {
        Club home = club("Casa"), away = club("Visitante");
        home.setFormation(Formation.F_433); away.setFormation(Formation.F_433);
        Map<Integer, Player> lineup = new HashMap<>();
        for (int slot = 0; slot < 11; slot++) {
            Position position = Position.valueOf(Formation.F_433.getPositionSlots().get(slot));
            Player starter = player("Titular " + slot, position, 76);
            starter.transferTo(home); lineup.put(slot, starter);
        }
        home.setTacticsMap(lineup);
        Player defender = player("Zagueiro reserva", Position.CB, 72);
        Player striker = player("Atacante estrela", Position.ST, 90);
        defender.transferTo(home); striker.transferTo(home);

        Match match = new Match(home, away);
        match.recordStartingLineups(new ArrayList<>(home.getStartingXI()), Collections.emptyList());
        Map<Integer, Player> before = new HashMap<>(home.getTacticsMap());
        Player injured = before.get(2);
        injured.injureForDays(20, "Contusão");
        home.removeUnavailablePlayersFromStartingXI();

        AutomaticInjurySubstitutionService.Result result =
            AutomaticInjurySubstitutionService.replace(match, home, before,
                Arrays.asList(striker, defender), Collections.emptyList(), 37);
        require(result != null, "No automatic replacement was made");
        require(result.injured == injured && result.replacement == defender && result.slot == 2,
            "Replacement did not preserve the injured player's tactical role");
        require(home.getTacticsMap().get(2) == defender && home.getStartingXI().size() == 11,
            "Replacement was not inserted into the active lineup");
        require(match.getParticipantsForClub(home).contains(defender),
            "Substitute was not registered as a match participant");
        System.out.println("Simulated injury: positional bench replacement and match registration OK.");
    }

    private static Club club(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }
    private static Player player(String name, Position position, int quality) {
        return new Player(name, "Brasil", position, null, 25,
            new TechnicalAttributes(quality, quality, quality, quality, quality, 60), 90, 20_000);
    }
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
