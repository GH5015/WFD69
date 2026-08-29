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
    private final Map<Player, Integer> playerMinutes = new HashMap<>();
    private final Map<Player, Integer> activeMinuteStarts = new HashMap<>();
    private final Map<Player, Club> playerMatchClubs = new HashMap<>();
    private boolean minuteTrackingInitialized;
    private Date date;
    private String stage = "REGULAR";
    private String playoffSeriesId;
    private int playoffGameNumber;

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
    public String getPlayoffSeriesId() { return playoffSeriesId; }
    public int getPlayoffGameNumber() { return playoffGameNumber; }
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
    public void setPlayoffSeriesId(String playoffSeriesId) { this.playoffSeriesId = playoffSeriesId; }
    public void setPlayoffGameNumber(int playoffGameNumber) { this.playoffGameNumber = playoffGameNumber; }
    public void setResult(int h, int a) { this.homeGoals = h; this.awayGoals = a; this.played = true; }
    public void addGoalScorer(Player p) { this.goalScorers.add(p); }
    public void addAssister(Player p) { this.assisters.add(p); }
    public void addCard(Player p, String t) { this.cards.put(p, t); }

    /** Registra os atletas em campo no pontapé inicial sem reiniciar um jogo carregado. */
    public void recordStartingLineups(List<Player> homePlayers, List<Player> awayPlayers) {
        if (minuteTrackingInitialized) return;
        minuteTrackingInitialized = true;
        recordStartingLineup(homePlayers, homeTeam);
        recordStartingLineup(awayPlayers, awayTeam);
    }

    private void recordStartingLineup(List<Player> players, Club club) {
        if (players == null) return;
        for (Player player : players) {
            if (player == null) continue;
            playerMinutes.putIfAbsent(player, 0);
            activeMinuteStarts.put(player, 0);
            playerMatchClubs.put(player, club);
        }
    }

    /** Fecha os minutos do substituído e abre a contagem do atleta que entrou. */
    public void registerSubstitution(Player outPlayer, Player inPlayer, int minute, Club club) {
        registerPlayerExit(outPlayer, minute);
        if (inPlayer == null) return;
        int safeMinute = Math.max(0, Math.min(90, minute));
        playerMinutes.putIfAbsent(inPlayer, 0);
        activeMinuteStarts.put(inPlayer, safeMinute);
        playerMatchClubs.put(inPlayer, club != null ? club : inPlayer.getCurrentClub());
    }

    /** Usado também em lesões e expulsões para não creditar os 90 minutos completos. */
    public void registerPlayerExit(Player player, int minute) {
        if (player == null) return;
        Integer start = activeMinuteStarts.remove(player);
        if (start == null) return;
        int safeMinute = Math.max(start, Math.min(90, minute));
        playerMinutes.put(player, playerMinutes.getOrDefault(player, 0) + safeMinute - start);
    }

    public void finishPlayerMinuteTracking() {
        for (Player player : new ArrayList<>(activeMinuteStarts.keySet())) {
            registerPlayerExit(player, 90);
        }
    }

    public Map<Player, Integer> getPlayerMinutesForClub(Club club) {
        Map<Player, Integer> result = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : playerMinutes.entrySet()) {
            if (playerMatchClubs.get(entry.getKey()) == club && entry.getValue() > 0) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

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
        return !"REGULAR".equalsIgnoreCase(stage);
    }

    public boolean isFinalMatch() {
        return "FINALS".equalsIgnoreCase(stage) || "FINAL".equalsIgnoreCase(stage);
    }

    @Override
    public String toString() {
        return homeTeam.getName() + " x " + awayTeam.getName();
    }
}
