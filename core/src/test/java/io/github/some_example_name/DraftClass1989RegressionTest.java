package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1989RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1989), "1989 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1989);
        require(players.size() >= 56, "1989 needs at least 56 prospects for 28 clubs");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        expect(byName, "Roy Keane", Position.CM, 57, 70, 72, 91, 66, 97);
        expect(byName, "Pep Guardiola", Position.CDM, 48, 78, 68, 73, 68, 96);
        expect(byName, "Fabien Barthez", Position.GK, 12, 43, 78, 87, 20, 95);
        expect(byName, "Yoo Sang-chul", Position.CM, 60, 68, 68, 89, 67, 93);
        expect(byName, "Elivélton", Position.LW, 65, 67, 34, 80, 78, 88);
        require(byName.get("Roy Keane").getSecondaryPosition() == Position.CDM, "Keane secondary position");
        require(byName.get("Carlos Gamarra").getSecondaryPosition() == null, "Gamarra secondary position");
        require(!byName.containsKey("Thomas Helmer"), "Thomas Helmer already belongs to the 1986 class");
        System.out.println("Draft class 1989: historical prospects, no Thomas Helmer duplicate, attributes, positions, registration and supplementation OK.");
    }
    private static void expect(Map<String, Player> players, String name, Position pos, int a, int p, int d, int f, int dr, int potential) {
        Player player = players.get(name);
        require(player != null && player.getAge() == 18 && player.getPrimaryPosition() == pos && player.getPotential() == potential
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
