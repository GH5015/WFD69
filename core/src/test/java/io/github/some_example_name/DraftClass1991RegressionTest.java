package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1991RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1991), "1991 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1991);
        require(players.size() == 60, "1991 should provide the 60 historical prospects exactly");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        expect(byName, "Ryan Giggs", Position.LW, 70, 75, 38, 86, 88, 97);
        expect(byName, "Roberto Carlos", Position.LB, 60, 70, 73, 91, 79, 97);
        expect(byName, "Fabio Cannavaro", Position.CB, 35, 59, 79, 88, 52, 97);
        expect(byName, "Jay-Jay Okocha", Position.CAM, 67, 75, 32, 77, 89, 96);
        expect(byName, "Rogério Ceni", Position.GK, 22, 54, 75, 84, 24, 92);
        expect(byName, "Marcos", Position.GK, 11, 38, 74, 87, 17, 91);
        require(byName.get("Ryan Giggs").getSecondaryPosition() == Position.LM, "Giggs secondary position");
        require(byName.get("Filippo Inzaghi").getSecondaryPosition() == null, "Inzaghi secondary position");
        System.out.println("Draft class 1991: 60 historical prospects, attributes, positions and registration OK.");
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
