package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class BenchSelectionRegressionTest {
    public static void main(String[] args) {
        Club club = new Club();
        for (int i = 0; i < 23; i++) {
            Player player = new Player("Jogador " + i, "Brasil", Position.CM, null, 24,
                new TechnicalAttributes(50 + i, 50 + i, 50 + i, 70, 65, 60), 90, 10_000);
            player.transferTo(club);
            if (i < 11) { club.getTacticsMap().put(i, player); club.getStartingXI().add(player); }
        }
        List<Player> original = club.getBenchPlayers();
        require(original.size() == 7, "Default bench size");
        Player reserve = original.get(0), outside = club.getSquad().get(11);
        Map<Integer, Player> lineup = new HashMap<>(club.getTacticsMap());
        require(club.swapBenchPlayers(outside, reserve), "Unselected -> bench failed");
        require(club.getBenchPlayers().contains(outside) && !club.getBenchPlayers().contains(reserve), "Manual selection overwritten by overall");
        require(club.swapBenchPlayers(outside, reserve), "Bench -> unselected failed");
        require(club.getBenchPlayers().equals(original), "Inverse swap did not restore bench order");
        require(club.getTacticsMap().equals(lineup), "Bench swap changed starting lineup");
        require(!club.swapBenchPlayers(reserve, club.getSquad().get(0)), "Starter accepted as unselected");
        require(!club.swapBenchPlayers(original.get(0), original.get(1)), "Same-list swap accepted");
        require(!club.swapBenchPlayers(reserve, reserve), "Self swap accepted");
        outside.setInjuryDays(4);
        require(!club.swapBenchPlayers(reserve, outside), "Injured player admitted");
        outside.setInjuryDays(0);
        List<Player> copy = club.getBenchPlayers(); copy.clear();
        require(club.getBenchPlayers().size() == 7, "Getter exposed mutable bench");
        reserve.setInjuryDays(5);
        require(!club.getBenchPlayers().contains(reserve) && club.getBenchPlayers().size() == 7, "Injury did not refill bench");
        Player promoted = club.getBenchPlayers().get(0); club.getTacticsMap().put(0, promoted);
        require(!club.getBenchPlayers().contains(promoted), "Promoted starter remains on bench");
        Player transferred = club.getBenchPlayers().get(0); transferred.transferTo(null);
        require(!club.getBenchPlayers().contains(transferred), "Transferred player remains on bench");
        require(new HashSet<>(club.getBenchPlayers()).size() == 7, "Duplicates in bench");
        System.out.println("Bench selection: both directions, persistence, lineup preservation, availability and transfers OK.");
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
