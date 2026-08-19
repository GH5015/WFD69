package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;

public class Season {
    private int year;
    private List<Match> matches;
    private Club champion;
    private Player topScorer; // Futuramente, pode ser uma lista ou um objeto mais complexo
    // private Map<String, Object> statistics; // Para estatísticas mais detalhadas

    public Season(int year) {
        this.year = year;
        this.matches = new ArrayList<>();
        this.champion = null;
        this.topScorer = null;
        // this.statistics = new HashMap<>();
    }

    // Getters
    public int getYear() { return year; }
    public List<Match> getMatches() { return matches; }
    public Club getChampion() { return champion; }
    public Player getTopScorer() { return topScorer; }
    // public Map<String, Object> getStatistics() { return statistics; }

    // Setters
    public void addMatch(Match match) { this.matches.add(match); }
    public void setChampion(Club champion) { this.champion = champion; }
    public void setTopScorer(Player topScorer) { this.topScorer = topScorer; }
    // public void addStatistic(String key, Object value) { this.statistics.put(key, value); }

    @Override
    public String toString() {
        return "Season{" +
               "year=" + year +
               ", matchesPlayed=" + matches.size() +
               ", champion=" + (champion != null ? champion.getName() : "N/A") +
               '}';
    }
}
