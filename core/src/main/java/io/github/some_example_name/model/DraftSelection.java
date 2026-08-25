package io.github.some_example_name.model;

/** Registro definitivo de uma escolha realizada no Draft. */
public class DraftSelection {
    private final DraftPick pick;
    private final Player player;
    public DraftSelection(DraftPick pick, Player player) { this.pick = pick; this.player = player; }
    public DraftPick getPick() { return pick; }
    public Player getPlayer() { return player; }
}
