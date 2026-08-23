package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private Club homeTeam;
    private Club awayTeam;
    private int homeGoals;
    private int awayGoals;
    private boolean played;
    private List<Player> goalScorers;
    private List<Player> assisters;
    private Map<Player, String> cards;
    private Date date;
    private String stage = "REGULAR";

    private int homeShots, awayShots;
    private int homeShotsOnTarget, awayShotsOnTarget;
    private int homePossession = 50, awayPossession = 50;
    private double homeXG = 0, awayXG = 0;
    private float homeMomentum = 0.5f, awayMomentum = 0.5f;
    private double accumulatedHomePossession = 0;
    private int possessionSamples = 0;

    // Estatísticas de Faltas e Expulsões
    private int homeFouls = 0, awayFouls = 0;
    private int homeRedCards = 0, awayRedCards = 0;

    public Match(Club homeTeam, Club awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.played = false;
        this.goalScorers = new ArrayList<>();
        this.assisters = new ArrayList<>();
        this.cards = new HashMap<>();
    }

    // Getters
    public Club getHomeTeam() { return homeTeam; }
    public Club getAwayTeam() { return awayTeam; }
    public int getHomeGoals() { return homeGoals; }
    public int getAwayGoals() { return awayGoals; }
    public boolean isPlayed() { return played; }
    public List<Player> getGoalScorers() { return goalScorers; }
    public List<Player> getAssisters() { return assisters; }
    public Map<Player, String> getCards() { return cards; }
    public Date getDate() { return date; }
    public String getStage() { return stage; }
    public int getHomeShots() { return homeShots; }
    public int getAwayShots() { return awayShots; }
    public int getHomeShotsOnTarget() { return homeShotsOnTarget; }
    public int getAwayShotsOnTarget() { return awayShotsOnTarget; }
    public int getHomePossession() { return homePossession; }
    public int getAwayPossession() { return awayPossession; }
    public double getHomeXG() { return homeXG; }
    public double getAwayXG() { return awayXG; }
    public float getHomeMomentum() { return homeMomentum; }
    public float getAwayMomentum() { return awayMomentum; }

    public int getHomeFouls() { return homeFouls; }
    public int getAwayFouls() { return awayFouls; }
    public void addHomeFoul() { this.homeFouls++; }
    public void addAwayFoul() { this.awayFouls++; }

    public int getHomeRedCards() { return homeRedCards; }
    public int getAwayRedCards() { return awayRedCards; }
    public void addHomeRedCard() { this.homeRedCards++; }
    public void addAwayRedCard() { this.awayRedCards++; }

    // Setters
    public void setPlayed(boolean played) { this.played = played; }
    public void setHomeGoals(int g) { this.homeGoals = g; }
    public void setAwayGoals(int g) { this.awayGoals = g; }
    public void addHomeShot(boolean onTarget) { homeShots++; if(onTarget) homeShotsOnTarget++; }
    public void addAwayShot(boolean onTarget) { awayShots++; if(onTarget) awayShotsOnTarget++; }
    public void setPossession(int hP) {
        int safePossession = Math.max(0, Math.min(100, hP));
        accumulatedHomePossession += safePossession;
        possessionSamples++;
        this.homePossession = (int) Math.round(accumulatedHomePossession / possessionSamples);
        this.awayPossession = 100 - this.homePossession;
    }
    public void setMomentum(float hM) { this.homeMomentum = hM; this.awayMomentum = 1.0f - hM; }
    public void setDate(Date date) { this.date = date; }
    public void setStage(String stage) { this.stage = stage; }
    public void setResult(int h, int a) { this.homeGoals = h; this.awayGoals = a; this.played = true; }
    public void addGoalScorer(Player p) { this.goalScorers.add(p); }
    public void addAssister(Player p) { this.assisters.add(p); }
    public void addCard(Player p, String t) { this.cards.put(p, t); }

    private int homeCorners = 0;
    private int awayCorners = 0;

    public int getHomeCorners() { return homeCorners; }
    public int getAwayCorners() { return awayCorners; }
    public void addHomeCorner() { this.homeCorners++; }
    public void addAwayCorner() { this.awayCorners++; }

    public void addHomeXG(float xg) { this.homeXG += xg; }
    public void addAwayXG(float xg) { this.awayXG += xg; }

    // Adicionar em io.github.some_example_name.model.Match

    public boolean isPlayoffs() {
        return "PLAYOFFS".equalsIgnoreCase(stage) || "FINALS".equalsIgnoreCase(stage) || "FINAL".equalsIgnoreCase(stage);
    }

    public boolean isFinalMatch() {
        return "FINALS".equalsIgnoreCase(stage) || "FINAL".equalsIgnoreCase(stage);
    }

    @Override
    public String toString() {
        return homeTeam.getName() + " x " + awayTeam.getName();
    }
}
