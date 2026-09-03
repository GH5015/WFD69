package io.github.some_example_name.model;

/** Presentation policy only: the simulation retains the player's numeric potential. */
public final class PlayerPotentialDisplay {
    private PlayerPotentialDisplay() { }

    public static String forViewer(Player player, Club viewer) {
        if (player == null) return "?";
        return viewer != null && player.getCurrentClub() == viewer
            ? String.valueOf(player.getPotential()) : grade(player.getPotential());
    }

    /** Same bands used by the scouting reports and player profiles. */
    public static String grade(int value) {
        if (value >= 90) return "A+";
        if (value >= 85) return "A";
        if (value >= 80) return "A-";
        if (value >= 77) return "B+";
        if (value >= 73) return "B";
        if (value >= 70) return "B-";
        if (value >= 67) return "C+";
        if (value >= 63) return "C";
        if (value >= 60) return "C-";
        if (value >= 57) return "D+";
        if (value >= 53) return "D";
        if (value >= 50) return "D-";
        return "F";
    }
}
