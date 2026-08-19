package io.github.some_example_name.model;

public class PlayerStats {
    public Player player;
    public int goals = 0;
    public int assists = 0;
    public int yellowCards = 0;
    public int redCards = 0;

    public PlayerStats(Player player) {
        this.player = player;
    }
}
