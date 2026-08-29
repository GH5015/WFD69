package io.github.some_example_name.model;

/** Registro exibido no fechamento da temporada para um atleta aposentado. */
public final class RetirementRecord {
    private final Player player;
    private final String lastClubName;
    private final int season;

    public RetirementRecord(Player player, String lastClubName, int season) {
        this.player = player;
        this.lastClubName = lastClubName;
        this.season = season;
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastClubName() {
        return lastClubName;
    }

    public int getSeason() {
        return season;
    }
}
