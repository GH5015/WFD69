package io.github.some_example_name.model;

/** Registro compacto do histórico profissional do treinador do usuário. */
public final class ManagerSeasonRecord {
    private final int season;
    private final String clubName;
    private final boolean champion;
    private final boolean reachedPlayoffs;
    private final int objectivesCompleted;
    private final int developedPlayers;
    private final int finalScore;
    private final boolean dismissed;

    public ManagerSeasonRecord(int season, String clubName, boolean champion,
                               boolean reachedPlayoffs, int objectivesCompleted,
                               int developedPlayers, int finalScore, boolean dismissed) {
        this.season = season;
        this.clubName = clubName;
        this.champion = champion;
        this.reachedPlayoffs = reachedPlayoffs;
        this.objectivesCompleted = objectivesCompleted;
        this.developedPlayers = developedPlayers;
        this.finalScore = finalScore;
        this.dismissed = dismissed;
    }

    public int getSeason() { return season; }
    public String getClubName() { return clubName; }
    public boolean isChampion() { return champion; }
    public boolean hasReachedPlayoffs() { return reachedPlayoffs; }
    public int getObjectivesCompleted() { return objectivesCompleted; }
    public int getDevelopedPlayers() { return developedPlayers; }
    public int getFinalScore() { return finalScore; }
    public boolean isDismissed() { return dismissed; }
}
