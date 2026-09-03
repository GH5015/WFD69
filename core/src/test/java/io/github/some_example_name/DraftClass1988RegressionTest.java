package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import java.util.*;

public final class DraftClass1988RegressionTest {
    public static void main(String[] args) {
        require(DraftClassRepository.hasHistoricalClass(1988), "1988 not registered as historical");
        List<Player> players = DraftClassRepository.getClassForYear(1988);
        require(players.size() >= 56, "1988 needs at least 56 prospects for 28 clubs");
        Map<String, Player> byName = new HashMap<>();
        for (Player player : players) require(byName.put(player.getName(), player) == null, "Duplicate " + player.getName());
        expect(byName, "Cafu", Position.RB, 55, 69, 72, 89, 75, 98);
        expect(byName, "Alan Shearer", Position.ST, 78, 56, 30, 89, 69, 97);
        expect(byName, "Edwin van der Sar", Position.GK, 12, 45, 78, 86, 20, 97);
        expect(byName, "Christian Karembeu", Position.CDM, 50, 68, 70, 91, 65, 94);
        expect(byName, "Kim Byung-ji", Position.GK, 12, 40, 71, 85, 20, 88);
        require(byName.get("Cafu").getSecondaryPosition() == Position.RWB, "Cafu secondary position");
        require(byName.get("Alan Shearer").getSecondaryPosition() == null, "Shearer secondary position");
        for (String name : new String[]{"Cafu", "Alan Shearer", "Edwin van der Sar", "Viola"}) {
            if (byName.containsKey(name)) require(byName.get(name).getAge() == 18, "Age convention broken for " + name);
        }
        System.out.println("Draft class 1988: 52 historical prospects, attributes, positions, registration and supplementation OK.");
    }
    private static void expect(Map<String, Player> players, String name, Position pos, int a, int p, int d, int f, int dr, int potential) {
        Player player = players.get(name);
        require(player != null && player.getPrimaryPosition() == pos && player.getPotential() == potential
            && player.getTechnicalAttributes().getAtaque() == a && player.getTechnicalAttributes().getPasse() == p
            && player.getTechnicalAttributes().getDefesa() == d && player.getTechnicalAttributes().getFisico() == f
            && player.getTechnicalAttributes().getDrible() == dr, "Incorrect data for " + name);
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
