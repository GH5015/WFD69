package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1997RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1997), "1997 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1997);
        require(players.size() == 60, "1997 should provide the 60 historical prospects exactly");
        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1996; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) earlierNames.add(normalize(player.getName()));
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Michael Owen", Position.ST, 80, 58, 27, 90, 81, 98);
        expect(byName, "Andrea Pirlo", Position.CM, 61, 82, 48, 74, 81, 98);
        expect(byName, "Júlio César Soares", Position.GK, 12, 42, 79, 87, 18, 96);
        expect(byName, "Rafael Márquez", Position.CB, 45, 69, 78, 86, 62, 97);
        expect(byName, "Jean-Alain Boumsong", Position.CB, 37, 60, 72, 91, 53, 91);
        require(byName.get("Michael Owen").getSecondaryPosition() == Position.CF, "Owen secondary position");
        require(byName.get("Júlio César Soares").getSecondaryPosition() == null, "Júlio César secondary position");
        System.out.println("Draft class 1997: 60 unique eligible prospects, attributes and positions OK.");
    }
    private static String normalize(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT); }
    private static void expect(Map<String, Player> players, String name, Position pos, int a, int p, int d, int f, int dr, int potential) {
        Player player = players.get(name);
        require(player != null && player.getPrimaryPosition() == pos && player.getPotential() == potential
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
