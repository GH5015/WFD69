package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1990RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1990), "1990 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1990);
        require(players.size() >= 60, "1990 needs 60 prospects for 30 clubs");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        expect(byName, "Zinedine Zidane", Position.CAM, 70, 82, 41, 77, 88, 99);
        expect(byName, "Rivaldo", Position.CAM, 75, 77, 31, 81, 87, 98);
        expect(byName, "Jaap Stam", Position.CB, 34, 57, 78, 93, 50, 96);
        expect(byName, "Lilian Thuram", Position.RB, 43, 62, 78, 90, 61, 97);
        expect(byName, "Abel Xavier", Position.RB, 47, 63, 69, 86, 64, 91);
        require(byName.get("Zinedine Zidane").getSecondaryPosition() == Position.CM, "Zidane secondary position");
        require(byName.get("Richard Sosa").getPrimaryPosition() == Position.CB, "Generic DF should map to CB");
        require(!byName.containsKey("Daniel Borimirov"), "Borimirov already belongs to 1988");
        System.out.println("Draft class 1990: 59 historical prospects, no Borimirov duplicate, DF mapping, registration and 60-pick supplementation OK.");
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
