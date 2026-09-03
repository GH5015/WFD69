package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private Club.TacticalSetup homePrematchSetup, awayPrematchSetup;
    private boolean tacticalSetupCaptured, tacticalSetupRestored;
    private transient io.github.some_example_name.engine.TacticalPerformanceReport homeFinalReport, awayFinalReport;

    public void capturePrematchTactics() {
        if (tacticalSetupCaptured || played) return;
        homePrematchSetup = homeTeam.captureTacticalSetup();
        awayPrematchSetup = awayTeam.captureTacticalSetup();
        tacticalSetupCaptured = true;
    }

    public void restorePrematchTactics() {
        if (!played || !tacticalSetupCaptured || tacticalSetupRestored) return;
        // O relatório deve avaliar a tática da partida, não o plano restaurado.
        homeFinalReport = io.github.some_example_name.engine.TacticalPerformanceReport.analyze(this, homeTeam);
        awayFinalReport = io.github.some_example_name.engine.TacticalPerformanceReport.analyze(this, awayTeam);
        homeTeam.restoreTacticalSetup(homePrematchSetup);
        awayTeam.restoreTacticalSetup(awayPrematchSetup);
        tacticalSetupRestored = true;
        homePrematchSetup = null;
        awayPrematchSetup = null;
    }

    public io.github.some_example_name.engine.TacticalPerformanceReport getFinalTacticalReport(Club club) {
        return club == homeTeam ? homeFinalReport : club == awayTeam ? awayFinalReport : null;
    }
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
    private boolean moraleProcessed;
    private Date date;
    private String stage = "REGULAR";
    private String playoffSeriesId;
    private int playoffGameNumber;
    private int attendance;
    private int attendanceDemand;
    private boolean attendanceCalculated;
    private int averageTicketPrice;
    private long gateRevenue;
    private boolean gateRevenueRecorded;

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
    private int homeHighRegains = 0, awayHighRegains = 0;
    private int homeTransitions = 0, awayTransitions = 0;
    private double homeTempoSum, awayTempoSum;
    private double homeMentalitySum, awayMentalitySum;
    private double homePassingSum, awayPassingSum;
    private double homeWidthSum, awayWidthSum;
    private double homePressureSum, awayPressureSum;
    private double homeTacticalFitSum, awayTacticalFitSum;
    private int homeTacticalSamples, awayTacticalSamples;
    private int homeIntensityDropMinute = -1, awayIntensityDropMinute = -1;

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
    public int getAttendance() { return attendance; }
    public int getAttendanceDemand() { return attendanceDemand; }
    public boolean isAttendanceCalculated() { return attendanceCalculated; }
    public int getAverageTicketPrice() { return averageTicketPrice; }
    public long getGateRevenue() { return gateRevenue; }
    public boolean isGateRevenueRecorded() { return gateRevenueRecorded; }
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
    public int getHomeHighRegains() { return homeHighRegains; }
    public int getAwayHighRegains() { return awayHighRegains; }
    public int getHomeTransitions() { return homeTransitions; }
    public int getAwayTransitions() { return awayTransitions; }
    public void addHomeHighRegain() { homeHighRegains++; }
    public void addAwayHighRegain() { awayHighRegains++; }
    public void addHomeTransition() { homeTransitions++; }
    public void addAwayTransition() { awayTransitions++; }

    public void recordTacticalSample(
        boolean home,
        Club club,
        int fitScore,
        double sustainability,
        int minute
    ) {
        if (club == null) return;
        if (home) {
            homeTempoSum += club.getTempo();
            homeMentalitySum += club.getMentalityValue();
            homePassingSum += club.getPassing();
            homeWidthSum += club.getWidth();
            homePressureSum += club.getPressure();
            homeTacticalFitSum += fitScore;
            homeTacticalSamples++;
            if (homeIntensityDropMinute < 0 && sustainability < .86d) homeIntensityDropMinute = minute;
        } else {
            awayTempoSum += club.getTempo();
            awayMentalitySum += club.getMentalityValue();
            awayPassingSum += club.getPassing();
            awayWidthSum += club.getWidth();
            awayPressureSum += club.getPressure();
            awayTacticalFitSum += fitScore;
            awayTacticalSamples++;
            if (awayIntensityDropMinute < 0 && sustainability < .86d) awayIntensityDropMinute = minute;
        }
    }

    private boolean isHomeClub(Club club) { return homeTeam == club; }
    private double average(double homeSum, double awaySum, Club club, double fallback) {
        boolean home = isHomeClub(club);
        int samples = home ? homeTacticalSamples : awayTacticalSamples;
        return samples == 0 ? fallback : (home ? homeSum : awaySum) / samples;
    }
    public float getAverageTacticalTempo(Club club) {
        return (float) average(homeTempoSum, awayTempoSum, club, club.getTempo());
    }
    public float getAverageTacticalMentality(Club club) {
        return (float) average(homeMentalitySum, awayMentalitySum, club, club.getMentalityValue());
    }
    public float getAverageTacticalPassing(Club club) {
        return (float) average(homePassingSum, awayPassingSum, club, club.getPassing());
    }
    public float getAverageTacticalWidth(Club club) {
        return (float) average(homeWidthSum, awayWidthSum, club, club.getWidth());
    }
    public float getAverageTacticalPressure(Club club) {
        return (float) average(homePressureSum, awayPressureSum, club, club.getPressure());
    }
    public int getAverageTacticalFit(Club club, int fallback) {
        return (int) Math.round(average(homeTacticalFitSum, awayTacticalFitSum, club, fallback));
    }
    public int getIntensityDropMinute(Club club) {
        return isHomeClub(club) ? homeIntensityDropMinute : awayIntensityDropMinute;
    }

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
    public void setAttendance(int attendance, int demand) {
        setAttendance(attendance, demand, homeTeam != null ? homeTeam.getAverageTicketPrice() : 0);
    }
    public void setAttendance(int attendance, int demand, int ticketPrice) {
        this.attendance = Math.max(0, attendance);
        this.attendanceDemand = Math.max(this.attendance, demand);
        this.averageTicketPrice = Math.max(0, ticketPrice);
        this.attendanceCalculated = true;
    }
    public void recordGateRevenue(long revenue) {
        gateRevenue = Math.max(0L, revenue);
        gateRevenueRecorded = true;
    }
    public void resetAttendanceProjection() {
        if (played || gateRevenueRecorded) return;
        attendance = 0;
        attendanceDemand = 0;
        averageTicketPrice = 0;
        attendanceCalculated = false;
    }
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

    /** Includes all entrants, even a substitution at 90' recorded as zero whole minutes. */
    public java.util.Set<Player> getParticipantsForClub(Club club) {
        java.util.Set<Player> participants = new java.util.HashSet<>();
        for (Map.Entry<Player, Club> entry : playerMatchClubs.entrySet()) {
            if (entry.getValue() == club) participants.add(entry.getKey());
        }
        return participants;
    }

    /** Apply once, based on actual match participation, not the final lineup or bench. */
    public void applyPostMatchMorale() {
        if (!played || moraleProcessed) return;
        double homeOverall = homeTeam.getOverall(), awayOverall = awayTeam.getOverall();
        int result = Integer.compare(homeGoals, awayGoals);
        homeTeam.updateSquadMorale(result, awayOverall, getParticipantsForClub(homeTeam));
        awayTeam.updateSquadMorale(-result, homeOverall, getParticipantsForClub(awayTeam));
        moraleProcessed = true;
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
