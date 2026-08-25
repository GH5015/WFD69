package io.github.some_example_name.model;

/** Estado de uma série eliminatória da pós-temporada. */
public class PlayoffSeries {
    private final String id;
    private final String round;
    private final Club firstSeed;
    private final Club secondSeed;
    private final int bestOf;

    private int firstSeedWins;
    private int secondSeedWins;
    private int gamesPlayed;
    private Club winner;

    public PlayoffSeries(
        String id,
        String round,
        Club firstSeed,
        Club secondSeed,
        int bestOf
    ) {
        this.id = id;
        this.round = round;
        this.firstSeed = firstSeed;
        this.secondSeed = secondSeed;
        this.bestOf = bestOf;
    }

    public void recordGame(Club gameWinner) {
        if (isComplete() || gameWinner == null) {
            return;
        }

        if (gameWinner == firstSeed) {
            firstSeedWins++;
        } else if (gameWinner == secondSeed) {
            secondSeedWins++;
        } else {
            return;
        }

        gamesPlayed++;
        if (firstSeedWins >= winsRequired()) {
            winner = firstSeed;
        } else if (secondSeedWins >= winsRequired()) {
            winner = secondSeed;
        }
    }

    public int winsRequired() {
        return (bestOf / 2) + 1;
    }

    public boolean isComplete() {
        return winner != null;
    }

    public String getId() { return id; }
    public String getRound() { return round; }
    public Club getFirstSeed() { return firstSeed; }
    public Club getSecondSeed() { return secondSeed; }
    public int getBestOf() { return bestOf; }
    public int getFirstSeedWins() { return firstSeedWins; }
    public int getSecondSeedWins() { return secondSeedWins; }
    public int getGamesPlayed() { return gamesPlayed; }
    public Club getWinner() { return winner; }
}
