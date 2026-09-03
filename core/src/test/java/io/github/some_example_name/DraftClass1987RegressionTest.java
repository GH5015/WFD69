package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1987RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1987), "1987 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1987);
        require(players.size() >= 56, "1987 needs at least 56 prospects for 28 clubs");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) {
            require(byName.put(player.getName(), player) == null, "Duplicated prospect " + player.getName());
        }
        expect(byName, "Dennis Bergkamp", Position.CF, 76, 79, 30, 76, 88, 98);
        expect(byName, "Gabriel Batistuta", Position.ST, 78, 57, 29, 88, 72, 97);
        expect(byName, "Oliver Kahn", Position.GK, 11, 40, 79, 89, 18, 97);
        expect(byName, "Hong Myung-bo", Position.CB, 42, 70, 76, 82, 60, 94);
        expect(byName, "Leonardo", Position.LB, 57, 73, 66, 80, 79, 94);
        for (String name : new String[]{"Dennis Bergkamp", "Gabriel Batistuta", "Fernando Redondo", "Oliver Kahn", "Brian Laudrup", "Viola"})
            require(byName.get(name).getAge() == 18, "Age convention broken for " + name);
        require(byName.get("Dennis Bergkamp").getSecondaryPosition() == Position.CAM, "Bergkamp secondary position");
        require(byName.get("Dion Dublin").getSecondaryPosition() == Position.CB, "Dublin secondary position");
        require(byName.get("Viola").getSecondaryPosition() == null, "Viola should have no secondary position");
        require(DraftClassRepository.getClassForYear(1989).size() >= 56 && !DraftClassRepository.hasHistoricalClass(1989),
            "Future procedural fallback broken");
        System.out.println("Draft class 1987: 52 historical prospects, attributes, positions, registration and procedural supplementation OK.");
    }
    private static void expect(Map<String, Player> players, String name, Position pos, int a, int p, int d, int f, int dr, int pot) {
        Player player = players.get(name);
        require(player != null && player.getPrimaryPosition() == pos && player.getPotential() == pot
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
