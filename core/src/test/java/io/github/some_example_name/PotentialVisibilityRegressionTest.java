package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class PotentialVisibilityRegressionTest {
    public static void main(String[] args) {
        Club user = club("Usuário");
        Club opponent = club("Adversário");
        Player player = new Player("Prospecto", "Brasil", Position.CM, null, 20,
            new TechnicalAttributes(65, 70, 60, 70, 68, 60), 87, 10_000);
        int potential = player.getPotential();
        int truePotential = player.getTruePotential();
        String grade = PlayerPotentialDisplay.grade(potential);
        player.transferTo(opponent);
        require(PlayerPotentialDisplay.forViewer(player, user).equals(grade), "Rival potential leaked");
        require(PlayerPotentialDisplay.forViewer(player, null).equals(grade), "Missing viewer revealed potential");
        player.transferTo(null);
        require(PlayerPotentialDisplay.forViewer(player, user).equals(grade), "Free agent potential leaked");
        require(PlayerPotentialDisplay.forViewer(player, null).equals(grade), "Null owner/viewer mistaken for ownership");
        player.transferTo(user);
        require(PlayerPotentialDisplay.forViewer(player, user).equals(String.valueOf(potential)), "Own roster lost exact potential");
        player.transferTo(opponent);
        require(PlayerPotentialDisplay.forViewer(player, user).equals(grade), "Former player potential leaked after transfer");
        require(PlayerPotentialDisplay.forViewer(null, user).equals("?"), "Missing player");

        int[] thresholds = {49, 50, 53, 57, 60, 63, 67, 70, 73, 77, 80, 85, 90, 99};
        String[] expected = {"F", "D-", "D", "D+", "C-", "C", "C+", "B-", "B", "B+", "A-", "A", "A+", "A+"};
        for (int i = 0; i < thresholds.length; i++)
            require(PlayerPotentialDisplay.grade(thresholds[i]).equals(expected[i]), "Wrong band at " + thresholds[i]);
        for (int knowledge : new int[]{0, 29, 30, 60, 70, 80, 99, 100}) {
            ScoutTarget target = new ScoutTarget(player);
            target.advanceKnowledge(knowledge);
            String display = target.getDisplayPotential();
            require(display.matches("\\?|[ABCD][+-]?|F"), "Scouting leaked numeric potential at " + knowledge);
            if (knowledge < 30) require(display.equals("?"), "Unknown scouting revealed grade");
            if (knowledge >= 70) require(display.equals(grade), "Completed grade inconsistent");
            if (knowledge == 100) require(target.getDisplayOverall().equals(String.valueOf(player.getOverall())), "Overall should remain exact");
        }
        require(player.getPotential() == potential && player.getTruePotential() == truePotential, "Presentation altered simulation");
        System.out.println("Potential visibility: ownership, transfers, free agents, scouting 0-100%, grade bands and unchanged simulation OK.");
    }
    private static Club club(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
