package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1996RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1996), "1996 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1996);
        require(players.size() == 60, "1996 should provide the 60 historical prospects exactly");
        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1995; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) earlierNames.add(normalize(player.getName()));
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Gianluigi Buffon", Position.GK, 12, 43, 83, 89, 19, 98);
        expect(byName, "Juan Román Riquelme", Position.CAM, 69, 83, 33, 73, 88, 97);
        expect(byName, "Frank Lampard", Position.CM, 65, 78, 56, 84, 75, 97);
        expect(byName, "Didier Drogba", Position.ST, 73, 58, 29, 92, 70, 96);
        expect(byName, "Michael Ricketts", Position.ST, 69, 53, 28, 90, 64, 88);
        require(byName.get("Gianluigi Buffon").getSecondaryPosition() == null, "Buffon secondary position");
        require(byName.get("Lúcio").getSecondaryPosition() == Position.CDM, "Lúcio secondary position");
        System.out.println("Draft class 1996: 60 unique eligible prospects, attributes and positions OK.");
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
