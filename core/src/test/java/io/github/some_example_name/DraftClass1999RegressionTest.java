package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1999RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1999), "1999 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1999);
        require(players.size() == 60, "1999 should provide the 60 historical prospects exactly");
        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1998; year++) for (Player player : DraftClassRepository.getClassForYear(year)) earlierNames.add(normalize(player.getName()));
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Zlatan Ibrahimović", Position.ST, 78, 68, 29, 89, 84, 98);
        expect(byName, "Samuel Eto'o", Position.ST, 79, 62, 28, 92, 82, 98);
        expect(byName, "Iker Casillas", Position.GK, 12, 43, 82, 88, 19, 98);
        expect(byName, "Xabi Alonso", Position.CM, 56, 82, 68, 81, 72, 97);
        expect(byName, "Mauro Rosales", Position.RW, 67, 68, 36, 87, 78, 90);
        require(byName.get("Zlatan Ibrahimović").getSecondaryPosition() == Position.CF, "Zlatan secondary position");
        require(byName.get("Iker Casillas").getSecondaryPosition() == null, "Casillas secondary position");
        System.out.println("Draft class 1999: 60 unique eligible prospects, attributes and positions OK.");
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
