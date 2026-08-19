package io.github.some_example_name.model;

public class StandingsRow {
    public Club club;
    public int points = 0;
    public int matches = 0;
    public int wins = 0;
    public int draws = 0;
    public int losses = 0;
    public int goalsFor = 0;
    public int goalsAgainst = 0;
    public int goalDifference = 0;
    public int cleanSheets = 0; // Jogos sem sofrer gols

    public StandingsRow(Club club) {
        this.club = club;
    }

    public void addResult(int gf, int ga) {
        matches++;
        goalsFor += gf;
        goalsAgainst += ga;
        goalDifference = goalsFor - goalsAgainst;

        if (ga == 0) {
            cleanSheets++;
        }

        if (gf > ga) {
            points += 3;
            wins++;
        } else if (gf == ga) {
            points += 1;
            draws++;
        } else {
            losses++;
        }
    }
}
