package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1994RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1994), "1994 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1994);
        require(players.size() == 60, "1994 should provide the 60 historical prospects exactly");

        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1993; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) {
                earlierNames.add(normalize(player.getName()));
            }
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() >= 18 && player.getAge() <= 21, "Ineligible age for " + player.getName());
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }

        expect(byName, "Ronaldo", 18, Position.ST, 83, 65, 28, 90, 92, 99);
        expect(byName, "Francesco Totti", 18, Position.CF, 74, 82, 35, 78, 86, 98);
        expect(byName, "Alessandro Nesta", 18, Position.CB, 38, 62, 81, 88, 58, 97);
        expect(byName, "Javier Zanetti", 21, Position.RB, 62, 74, 77, 91, 76, 96);
        expect(byName, "Seigo Narazaki", 18, Position.GK, 11, 38, 74, 86, 17, 90);
        require(byName.get("Ronaldo").getSecondaryPosition() == Position.CF, "Ronaldo secondary position");
        require(byName.get("Javier Zanetti").getSecondaryPosition() == Position.RM, "Zanetti secondary position");
        System.out.println("Draft class 1994: 60 unique eligible prospects, attributes and positions OK.");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void expect(Map<String, Player> players, String name, int age, Position pos,
                               int a, int p, int d, int f, int dr, int potential) {
        Player player = players.get(name);
        require(player != null && player.getAge() == age && player.getPrimaryPosition() == pos
            && player.getPotential() == potential
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
