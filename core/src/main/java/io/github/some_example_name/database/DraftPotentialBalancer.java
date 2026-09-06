package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;

import java.util.List;

/** Aplica uma curva de potencial mais seletiva a todas as classes do Draft. */
public final class DraftPotentialBalancer {
    private DraftPotentialBalancer() {
    }

    /**
     * Mantém intactos os dez principais prospectos e reduz progressivamente
     * o teto dos demais. A ordem recebida é o ranking oficial da classe.
     */
    public static List<Player> apply(List<Player> players) {
        if (players == null) return null;

        for (int index = 0; index < players.size(); index++) {
            Player player = players.get(index);
            if (player == null) continue;

            int originalPotential = player.getTruePotential();
            int adjustedPotential = adjustedPotential(
                originalPotential,
                player.getOverall(),
                index + 1
            );
            if (adjustedPotential != originalPotential) {
                player.rebalanceDraftPotential(adjustedPotential);
            }
        }
        return players;
    }

    static int adjustedPotential(int originalPotential, int overall, int rank) {
        int reduction;
        if (rank <= 10) reduction = 0;
        else if (rank <= 20) reduction = 5;
        else if (rank <= 30) reduction = 9;
        else if (rank <= 40) reduction = 13;
        else if (rank <= 50) reduction = 17;
        else reduction = 21;

        int developmentFloor = Math.min(originalPotential, overall + 2);
        return Math.max(developmentFloor, originalPotential - reduction);
    }
}
