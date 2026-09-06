package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1998RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1998), "1998 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1998);
        require(players.size() == 60, "1998 should provide the 60 historical prospects exactly");
        Set<String> earlierNames = new HashSet<>();
        for (int year = 1970; year <= 1997; year++) {
            for (Player player : DraftClassRepository.getClassForYear(year)) earlierNames.add(normalize(player.getName()));
        }
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(player.getAge() == 18, player.getName() + " should enter at age 18");
            require(!earlierNames.contains(normalize(player.getName())), "Repeated from earlier class: " + player.getName());
            require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        }
        expect(byName, "Ronaldinho", Position.CAM, 78, 82, 29, 81, 94, 99);
        expect(byName, "Xavi", Position.CM, 61, 84, 54, 73, 83, 98);
        expect(byName, "Steven Gerrard", Position.CM, 66, 79, 63, 88, 77, 98);
        expect(byName, "John Terry", Position.CB, 39, 63, 80, 91, 54, 97);
        expect(byName, "Peer Kluge", Position.CM, 53, 68, 61, 83, 66, 88);
        require(byName.get("Ronaldinho").getSecondaryPosition() == Position.LW, "Ronaldinho secondary position");
        require(byName.get("Ashley Cole").getSecondaryPosition() == Position.LWB, "Ashley Cole secondary position");
        System.out.println("Draft class 1998: 60 unique eligible prospects, attributes and positions OK.");
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
