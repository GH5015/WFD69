package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1992RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1992), "1992 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1992);
        require(players.size() == 60, "1992 should provide the 60 historical prospects exactly");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Alessandro Del Piero", Position.CF, 75, 78, 29, 76, 88, 98);
        expect(byName, "Paul Scholes", Position.CM, 62, 80, 54, 78, 79, 97);
        expect(byName, "Sol Campbell", Position.CB, 37, 60, 80, 91, 54, 96);
        expect(byName, "Ariel Ortega", Position.CAM, 70, 77, 32, 78, 89, 96);
        expect(byName, "Hans-Jörg Butt", Position.GK, 20, 48, 76, 87, 19, 93);
        expect(byName, "Nuno Espírito Santo", Position.GK, 11, 38, 72, 85, 17, 88);
        require(byName.get("Alessandro Del Piero").getSecondaryPosition() == Position.CAM,
            "Del Piero secondary position");
        require(byName.get("Sol Campbell").getSecondaryPosition() == null,
            "Sol Campbell secondary position");
        System.out.println("Draft class 1992: 60 historical prospects, attributes, positions and registration OK.");
    }

    private static void expect(Map<String, Player> players, String name, Position pos,
                               int a, int p, int d, int f, int dr, int potential) {
        Player player = players.get(name);
        require(player != null && player.getPrimaryPosition() == pos && player.getPotential() == potential
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
