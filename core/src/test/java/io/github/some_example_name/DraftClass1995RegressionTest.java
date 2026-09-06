package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1995RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1995), "1995 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1995);
        require(players.size() == 60, "1995 should provide the 60 historical prospects exactly");
        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1994; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) earlierNames.add(normalize(player.getName()));
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Thierry Henry", Position.ST, 78, 68, 30, 88, 86, 98);
        expect(byName, "Raúl", Position.CF, 79, 70, 31, 82, 83, 98);
        expect(byName, "Deco", Position.CAM, 67, 81, 48, 78, 84, 96);
        expect(byName, "Christian Abbiati", Position.GK, 11, 39, 77, 90, 17, 93);
        expect(byName, "Roy Carroll", Position.GK, 11, 37, 72, 88, 17, 89);
        require(byName.get("Thierry Henry").getSecondaryPosition() == Position.LW, "Henry secondary position");
        require(byName.get("Raúl").getSecondaryPosition() == Position.ST, "Raúl secondary position");
        System.out.println("Draft class 1995: 60 unique eligible prospects, attributes and positions OK.");
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
