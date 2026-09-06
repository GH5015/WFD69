package io.github.some_example_name;

import io.github.some_example_name.database.DraftClass1993;
import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1993RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1993), "1993 not registered as historical");
        List<Player> historical = DraftClass1993.getPlayers();
        require(historical.size() == 59, "1993 should contain 59 unique supplied historical prospects");

        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1992; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) {
                earlierNames.add(normalize(player.getName()));
            }
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : historical) {
            require(player.getAge() >= 18 && player.getAge() <= 21, "Ineligible age for " + player.getName());
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        require(!byName.containsKey("Míchel"), "Míchel was already used in 1981");

        List<Player> complete = DraftClassRepository.getClassForYear(1993);
        require(complete.size() == 60, "1993 should still supply two rounds for 30 clubs");
        expect(byName, "David Beckham", 18, Position.RM, 68, 84, 47, 80, 80, 97);
        expect(byName, "Edgar Davids", 20, Position.CDM, 60, 76, 75, 89, 78, 96);
        expect(byName, "Christian Wörns", 21, Position.CB, 38, 62, 78, 89, 54, 94);
        expect(byName, "Paul Okon", 21, Position.CDM, 49, 70, 70, 83, 65, 91);
        expect(byName, "Andrés Palop", 20, Position.GK, 11, 39, 75, 86, 17, 91);
        require(byName.get("David Beckham").getSecondaryPosition() == Position.CM,
            "Beckham secondary position");
        System.out.println("Draft class 1993: eligibility, unique history, attributes and 60-pick depth OK.");
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
