package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.some_example_name.model.DraftPick;

public class Club {
    private String name;
    private String nickname = "";
    private String country;
    private String confederation;
    private int reputation;
    private double budget;
    private String stadiumName;
    private int stadiumCapacity = 30000;
    private String philosophy = "Desenvolver Jovens";
    private String logoPath;
    private int winStreak = 0;
    private int lossStreak = 0;

    private List<Player> squad;
    private List<Player> startingXI;
    private List<DraftPick> draftPicks = new ArrayList<>();
    public List<DraftPick> getDraftPicks() {
        return draftPicks;
    }

    public void setDraftPicks(List<DraftPick> draftPicks) {
        this.draftPicks = draftPicks;
    }

    // Táticas e Formação
    private Formation formation = null;
    private Map<Integer, Player> tacticsMap;

    // Atributos Táticos
    private String mentality = "Equilibrada";
    private float tempo = 50f;
    private float passing = 50f;
    private float width = 50f;
    private float pressure = 50f;

    // Estatísticas Históricas
    private int startYear = 1969;
    private int currentYear = 1969;
    private int titlesCount = 0;

    private int totalGames = 0;
    private int totalWins = 0;
    private int totalDraws = 0;
    private int totalLosses = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;

    private int maxUnbeatenStreak = 0;
    private int currentUnbeatenStreak = 0;
    private String biggestWin = "-";
    private int maxWinMargin = 0;

    private String topScorerName = "Sem registros";
    private int topScorerGoals = 0;

    private String mostGamesPlayerName = "Sem registros";
    private int mostGamesCount = 0;

    private String topAssisterName = "Sem registros";
    private int topAssisterCount = 0;
    private ClubFinance finance;

    public ClubFinance getFinance() {
        if (finance == null) {
            finance = new ClubFinance(this);
        }
        return finance;
    }

    private List<SeasonHistory> seasonHistories = new ArrayList<>();

    /**
     * Atualiza a moral do elenco com base no resultado, favoritismo e sequências.
     * @param resultType 1 = Vitória, 0 = Empate, -1 = Derrota
     * @param opponentOverall Overall do time adversário
     */
    public void updateSquadMorale(int resultType, double opponentOverall) {
        // Difference > 0 significa que o seu clube é superior ao adversário
        double overallDiff = this.getOverall() - opponentOverall;

        for (Player player : this.squad) {
            int currentMorale = player.getMorale();
            int moraleChange = 0;

            if (resultType > 0) {
                // --- VITÓRIA ---
                if (overallDiff < -3.0) {
                    // Vitória sendo zebra dá um grande ganho de moral
                    moraleChange = 8 + (int) (Math.abs(overallDiff) * 0.5);
                } else if (overallDiff > 5.0) {
                    // Vitória muito esperada dá ganho leve
                    moraleChange = 3;
                } else {
                    moraleChange = 5;
                }
            } else if (resultType == 0) {
                // --- EMPATE ---
                if (overallDiff > 6.0) {
                    // Empate contra time muito inferior é visto como tropeço
                    moraleChange = - (int) (overallDiff * 0.4);
                } else if (overallDiff < -6.0) {
                    // Empate contra time bem mais forte dá um pequeno ganho
                    moraleChange = 3;
                }
            } else {
                // --- DERROTA ---
                if (overallDiff > 0) {
                    // Derrota para time pior (Zebra sofrida): Perda base mais penalidade por diferença
                    int zebraPenalty = (int) (overallDiff * 2.85);
                    moraleChange = -(7 + zebraPenalty);
                } else {
                    // Derrota esperada (para time superior)
                    int mitigation = (int) (Math.abs(overallDiff) * 0.3);
                    moraleChange = -Math.max(2, 7 - mitigation);
                }
            }

            // Aplica o ajuste garantindo que a moral fique entre 0 e 100
            int finalMorale = Math.max(0, Math.min(100, currentMorale + moraleChange));
            player.setMorale(finalMorale);
        }
    }

    public int getWinStreak() { return winStreak; }
    public int getLossStreak() { return lossStreak; }


    public Club() {
        this.squad = new ArrayList<>();
        this.startingXI = new ArrayList<>();
        this.tacticsMap = new HashMap<>();
    }

    public Club(String name) {
        this();
        this.name = name;
    }

    public Club(String name, String country, String confederation, int reputation, double budget, String stadiumName, String logoPath) {
        this();
        this.name = name;
        this.country = country;
        this.confederation = confederation;
        this.reputation = reputation;
        this.budget = budget;
        this.stadiumName = stadiumName;
        this.logoPath = logoPath;
    }

    public void recordMatchResult(int goalsScored, int goalsConceded) {
        this.totalGames++;
        this.goalsFor += goalsScored;
        this.goalsAgainst += goalsConceded;

        if (goalsScored > goalsConceded) {
            this.totalWins++;
            this.currentUnbeatenStreak++;
            if (this.currentUnbeatenStreak > this.maxUnbeatenStreak) {
                this.maxUnbeatenStreak = this.currentUnbeatenStreak;
            }

            int margin = goalsScored - goalsConceded;
            if (margin > this.maxWinMargin) {
                this.maxWinMargin = margin;
                this.biggestWin = goalsScored + " x " + goalsConceded;
            }
        } else if (goalsScored == goalsConceded) {
            this.totalDraws++;
            this.currentUnbeatenStreak++;
            if (this.currentUnbeatenStreak > this.maxUnbeatenStreak) {
                this.maxUnbeatenStreak = this.currentUnbeatenStreak;
            }
        } else {
            this.totalLosses++;
            this.currentUnbeatenStreak = 0;
        }
    }

    public void finishSeason(String ligaResult, String copaResult) {
        seasonHistories.add(0, new SeasonHistory(this.currentYear, ligaResult, copaResult));
        this.currentYear++;
    }

    public void addPlayerToSquad(Player player) {
        if (player == null) return;
        this.squad.add(player);
        if (this.startingXI.size() < 11) {
            this.startingXI.add(player);
            this.tacticsMap.put(this.startingXI.size() - 1, player);
        }
    }

    public void assignPlayerToSlot(int targetSlot, Player player) {
        if (player == null) {
            tacticsMap.remove(targetSlot);
            syncStartingXIFromTacticsMap();
            return;
        }

        Integer currentSlotOfPlayer = null;
        for (Map.Entry<Integer, Player> entry : tacticsMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(player)) {
                currentSlotOfPlayer = entry.getKey();
                break;
            }
        }

        Player occupantInTarget = tacticsMap.get(targetSlot);

        if (currentSlotOfPlayer != null) {
            if (occupantInTarget != null) {
                tacticsMap.put(currentSlotOfPlayer, occupantInTarget);
            } else {
                tacticsMap.remove(currentSlotOfPlayer);
            }
        }

        tacticsMap.put(targetSlot, player);
        syncStartingXIFromTacticsMap();
    }

    public void autoSelectXI() {
        tacticsMap.clear();
        if (formation == null) return;

        int slotsCount = formation.getPositionSlots().size();
        int limit = Math.min(slotsCount, squad.size());

        for (int i = 0; i < limit; i++) {
            tacticsMap.put(i, squad.get(i));
        }
        syncStartingXIFromTacticsMap();
    }

    private void syncStartingXIFromTacticsMap() {
        startingXI.clear();
        for (Player p : tacticsMap.values()) {
            if (p != null && !startingXI.contains(p)) {
                startingXI.add(p);
            }
        }
    }

    // Getters & Setters
    public List<Player> getPlayers() { return getSquad(); } // Alias de compatibilidade
    public List<Player> getSquad() { return squad; }
    public void setSquad(List<Player> squad) { this.squad = squad; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getConfederation() { return confederation; }
    public String getConference() { return confederation; }
    public void setConfederation(String confederation) { this.confederation = confederation; }

    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getStadiumName() { return stadiumName; }
    public String getStadium() { return stadiumName != null ? stadiumName : "Estádio Municipal"; }
    public void setStadiumName(String stadiumName) { this.stadiumName = stadiumName; }

    public int getStadiumCapacity() { return stadiumCapacity; }
    public void setStadiumCapacity(int stadiumCapacity) { this.stadiumCapacity = stadiumCapacity; }

    public String getPhilosophy() { return philosophy; }
    public void setPhilosophy(String philosophy) { this.philosophy = philosophy; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public List<Player> getStartingXI() {
        if (startingXI.isEmpty() && !tacticsMap.isEmpty()) {
            syncStartingXIFromTacticsMap();
        }
        return startingXI;
    }

    public void setStartingXI(List<Player> startingXI) {
        this.startingXI = startingXI;
    }

    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }

    public Map<Integer, Player> getTacticsMap() { return tacticsMap; }
    public void setTacticsMap(Map<Integer, Player> tacticsMap) {
        this.tacticsMap = tacticsMap;
        syncStartingXIFromTacticsMap();
    }

    public String getMentality() { return mentality; }
    public void setMentality(String mentality) { this.mentality = mentality; }

    public float getMentalityValue() {
        if (mentality == null) return 50f;
        switch (mentality.toLowerCase()) {
            case "ultra defensiva":
            case "retranca": return 10f;
            case "defensiva": return 30f;
            case "equilibrada": return 50f;
            case "ofensiva": return 70f;
            case "ultra ofensiva":
            case "ataque total": return 90f;
            default:
                try { return Float.parseFloat(mentality); }
                catch (NumberFormatException e) { return 50f; }
        }
    }

    public float getTempo() { return tempo; }
    public void setTempo(float tempo) { this.tempo = tempo; }

    public float getPassing() { return passing; }
    public void setPassing(float passing) { this.passing = passing; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getPressure() { return pressure; }
    public void setPressure(float pressure) { this.pressure = pressure; }

    public int getCurrentYear() { return currentYear; }
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }

    public int getTitlesCount() { return titlesCount; }
    public void setTitlesCount(int titlesCount) { this.titlesCount = titlesCount; }

    public int getTotalGames() { return totalGames; }
    public int getTotalWins() { return totalWins; }
    public int getTotalDraws() { return totalDraws; }
    public int getTotalLosses() { return totalLosses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalsFor - goalsAgainst; }

    public int getWinPercentage() {
        if (totalGames == 0) return 0;
        return (int) (((totalWins * 3.0 + totalDraws) / (totalGames * 3.0)) * 100);
    }

    public int getMaxUnbeatenStreak() { return maxUnbeatenStreak; }
    public String getBiggestWin() { return biggestWin; }

    public String getTopScorerName() { return topScorerName; }
    public int getTopScorerGoals() { return topScorerGoals; }
    public void setTopScorer(String name, int goals) { this.topScorerName = name; this.topScorerGoals = goals; }

    public String getMostGamesPlayerName() { return mostGamesPlayerName; }
    public int getMostGamesCount() { return mostGamesCount; }
    public void setMostGamesPlayer(String name, int games) { this.mostGamesPlayerName = name; this.mostGamesCount = games; }

    public String getTopAssisterName() { return topAssisterName; }
    public int getTopAssisterCount() { return topAssisterCount; }
    public void setTopAssister(String name, int assists) { this.topAssisterName = name; this.topAssisterCount = assists; }

    public List<SeasonHistory> getSeasonHistories() { return seasonHistories; }

    public double getOverall() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream().mapToInt(Player::getOverall).average().orElse(60.0);
    }

    public double getAttackingRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPrimaryPosition() != null && p.getPrimaryPosition().name().matches("ST|CF|RW|LW|CAM"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(0.0);
    }

    public double getDefensiveRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPosition() != null && p.getPosition().matches("CB|RB|LB|CDM|GK"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(getOverall());
    }

    public double getMidfieldRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPosition() != null && p.getPosition().matches("CM|CAM|CDM|LM|RM"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(getOverall());
    }

    public void drainSquadFatigue(float amount) {
        for (Player player : getStartingXI()) {
            player.applyMatchFatigue();
        }
    }

    @Override
    public String toString() {
        return name != null ? name : "Clube Sem Nome";
    }
}
