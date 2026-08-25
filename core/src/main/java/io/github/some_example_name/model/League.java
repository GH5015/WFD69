package io.github.some_example_name.model;

import java.util.*;
import java.util.stream.Collectors;

public class League {
    private String name;
    private List<Club> clubs;
    private List<Match> schedule;
    private int currentSeason;
    private int currentMatchIndex = 0;
    private String currentStage = "REGULAR";
    private Date lastProcessedDate;
    private Date currentDate;
    private final Map<String, PlayoffSeries> playoffSeries = new LinkedHashMap<>();
    private final Map<Club, Integer> playoffSeeds = new HashMap<>();

    public void nextSeason() {
        this.currentSeason++;
        this.currentStage = "REGULAR";
        this.playoffSeries.clear();
        this.playoffSeeds.clear();
    }
    public League(String name, int initialSeason) {
        this.name = name;
        this.clubs = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.currentSeason = initialSeason;
    }

    public void addClub(Club club) { this.clubs.add(club); }
    public List<Club> getClubs() { return clubs; }
    public List<Match> getSchedule() { return schedule; }

    public void setSchedule(List<Match> schedule) {
        this.schedule = schedule;
        this.currentMatchIndex = 0;
        if (!schedule.isEmpty()) {
            this.lastProcessedDate = schedule.get(0).getDate();
            Calendar firstDay = Calendar.getInstance();
            firstDay.setTime(schedule.get(0).getDate());
            firstDay.add(Calendar.DATE, -1);
            this.currentDate = firstDay.getTime();
        }
    }

    public int getCurrentSeason() { return currentSeason; }

    public Match getNextMatch() {
        return (currentMatchIndex < schedule.size()) ? schedule.get(currentMatchIndex) : null;
    }

    // Retorna o número da rodada atual baseado nas partidas jogadas
    public int getCurrentRound() {
        if (schedule == null || schedule.isEmpty()) return 1;

        int playedCount = 0;
        for (Match m : schedule) {
            if (m.isPlayed()) playedCount++;
        }
        int matchesPerRound = Math.max(1, clubs.size() / 2);
        return (playedCount / matchesPerRound) + 1;
    }

    // Retorna as partidas pertencentes à rodada/data do próximo confronto
    public List<Match> getCurrentRoundMatches() {
        List<Match> roundMatches = new ArrayList<>();
        Match nextMatch = getNextMatch();

        if (nextMatch == null || nextMatch.getDate() == null) {
            return roundMatches;
        }

        // Filtra todas as partidas da agenda que ocorrem na mesma data do próximo jogo
        for (Match m : schedule) {
            if (m.getDate() != null && m.getDate().equals(nextMatch.getDate())) {
                roundMatches.add(m);
            }
        }
        return roundMatches;
    }

    public Match getNextMatchForClub(Club club) {
        for (int i = currentMatchIndex; i < schedule.size(); i++) {
            Match m = schedule.get(i);
            if (m.getHomeTeam() == club || m.getAwayTeam() == club) return m;
        }
        return null;
    }

    public void advanceMatch() {
        if (currentMatchIndex < schedule.size()) {
            Match completedMatch = schedule.get(currentMatchIndex);
            lastProcessedDate = completedMatch.getDate();
            if (currentDate == null || currentDate.before(lastProcessedDate)) currentDate = lastProcessedDate;
            currentMatchIndex++;

            if ("PLAYOFFS".equals(currentStage) && completedMatch.isPlayed()) {
                advancePlayoffBracket(completedMatch);
            }
        }

        // Ao fim da fase regular, a chave é criada imediatamente. Ao fim dos
        // playoffs, a nova temporada só começa quando o usuário avançar o dia.
        if (getNextMatch() == null) {
            if ("REGULAR".equals(this.currentStage)) {
                setupPlayoffs();
            }
        }
    }

    public void checkAndAdvanceStage() {
        if ("REGULAR".equals(this.currentStage)) {
            setupPlayoffs();
        } else if ("PLAYOFFS".equals(this.currentStage)) {
            beginOffseason();
        }
    }

    private void setupPlayoffs() {
        if (!"REGULAR".equals(currentStage)) return;

        this.currentStage = "PLAYOFFS";
        List<Club> west = getQualifiedClubs("Ocidental", 6);
        List<Club> east = getQualifiedClubs("Oriental", 2);

        if (west.size() < 6 || east.size() < 2) return;

        playoffSeries.clear();
        playoffSeeds.clear();
        for (int i = 0; i < west.size(); i++) playoffSeeds.put(west.get(i), i + 1);
        playoffSeeds.put(east.get(0), 7);
        playoffSeeds.put(east.get(1), 8);

        // Lado ocidental: 1x6 e 4x5. Lado oriental: cada classificado
        // enfrenta um ocidental, impedindo um confronto Oriental x Oriental nas quartas.
        createSeries("QF1", "QUARTAS", west.get(0), west.get(5), 3);
        createSeries("QF2", "QUARTAS", west.get(3), west.get(4), 3);
        createSeries("QF3", "QUARTAS", east.get(0), west.get(2), 3);
        createSeries("QF4", "QUARTAS", west.get(1), east.get(1), 3);
    }

    /** Entrada pública usada pelo botão de início dos playoffs. */
    public void beginPlayoffs() {
        setupPlayoffs();
    }

    private List<Club> getQualifiedClubs(String conference, int limit) {
        return getFullStandings(conference).stream()
            .map(row -> row.club)
            .limit(limit)
            .collect(Collectors.toList());
    }

    private void createSeries(String id, String round, Club firstSeed, Club secondSeed, int bestOf) {
        PlayoffSeries series = new PlayoffSeries(id, round, firstSeed, secondSeed, bestOf);
        playoffSeries.put(id, series);
        addNextSeriesGame(series);
    }

    private void addNextSeriesGame(PlayoffSeries series) {
        int gameNumber = series.getGamesPlayed() + 1;
        Club home = series.getBestOf() == 1 || gameNumber > 1
            ? series.getFirstSeed()
            : series.getSecondSeed();
        Club away = home == series.getFirstSeed() ? series.getSecondSeed() : series.getFirstSeed();

        Match match = new Match(home, away);
        match.setStage(series.getRound());
        match.setPlayoffSeriesId(series.getId());
        match.setPlayoffGameNumber(gameNumber);
        match.setDate(getPlayoffGameDate(series.getRound(), gameNumber));
        schedule.add(match);
    }

    private Date getPlayoffGameDate(String round, int gameNumber) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(currentSeason, Calendar.OCTOBER, 1, 12, 0, 0);
        int startOffset = "QUARTAS".equals(round) ? 0 : "SEMIFINAIS".equals(round) ? 13 : 28;
        date.add(Calendar.DATE, startOffset + Math.max(0, gameNumber - 1) * 4);
        return date.getTime();
    }

    private void advancePlayoffBracket(Match match) {
        PlayoffSeries series = playoffSeries.get(match.getPlayoffSeriesId());
        if (series == null || series.isComplete()) return;

        Club gameWinner = match.getHomeGoals() == match.getAwayGoals()
            ? betterSeed(match.getHomeTeam(), match.getAwayTeam())
            : match.getHomeGoals() > match.getAwayGoals() ? match.getHomeTeam() : match.getAwayTeam();
        series.recordGame(gameWinner);

        if (!series.isComplete()) {
            addNextSeriesGame(series);
            return;
        }

        if ("QUARTAS".equals(series.getRound()) && allSeriesComplete("QUARTAS")) {
            createSeries("SF1", "SEMIFINAIS", winnerOf("QF1"), winnerOf("QF2"), 3);
            createSeries("SF2", "SEMIFINAIS", winnerOf("QF3"), winnerOf("QF4"), 3);
        } else if ("SEMIFINAIS".equals(series.getRound()) && allSeriesComplete("SEMIFINAIS")) {
            createSeries("F", "FINAL", winnerOf("SF1"), winnerOf("SF2"), 1);
        }
    }

    private Club betterSeed(Club first, Club second) {
        return playoffSeeds.getOrDefault(first, Integer.MAX_VALUE) <= playoffSeeds.getOrDefault(second, Integer.MAX_VALUE)
            ? first
            : second;
    }

    private boolean allSeriesComplete(String round) {
        boolean found = false;
        for (PlayoffSeries series : playoffSeries.values()) {
            if (round.equals(series.getRound())) {
                found = true;
                if (!series.isComplete()) return false;
            }
        }
        return found;
    }

    private Club winnerOf(String id) {
        PlayoffSeries series = playoffSeries.get(id);
        return series != null ? series.getWinner() : null;
    }

    public void beginOffseason() {
        this.currentStage = "OFFSEASON";
    }

    public boolean isClubStillInPlayoffs(Club club) {
        if (club == null || !"PLAYOFFS".equals(currentStage)) return false;
        for (PlayoffSeries series : playoffSeries.values()) {
            if (!series.isComplete() && (series.getFirstSeed() == club || series.getSecondSeed() == club)) {
                return true;
            }
        }
        return false;
    }

    public void startNewSeason() {
        this.currentSeason++;
        this.currentStage = "REGULAR";

        // Limpa cartões e estatísticas da temporada passada
        for (Club club : clubs) {
            for (Player p : club.getSquad()) {
                p.resetSeasonStats();
            }
        }


        // Reinicia a agenda mantendo o calendário zerado
        this.currentMatchIndex = 0;
        this.schedule.clear();
        this.playoffSeries.clear();
        this.playoffSeeds.clear();
    }

    public Date getLastProcessedDate() { return lastProcessedDate; }
    public void setLastProcessedDate(Date date) { this.lastProcessedDate = date; }
    public Date getCurrentDate() { return currentDate; }
    public void setCurrentDate(Date date) { this.currentDate = date; }

    public void advanceDateOneDay() {
        if (currentDate == null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 1);
        currentDate = calendar.getTime();
    }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String stage) { this.currentStage = stage; }

    public List<StandingsRow> getFullStandings(String conferenceName) {
        Map<Club, StandingsRow> map = new HashMap<>();
        for (Club club : clubs) {
            if (conferenceName == null || club.getConference().equals(conferenceName)) {
                map.put(club, new StandingsRow(club));
            }
        }
        for (Match match : schedule) {
            if (match.isPlayed() && "REGULAR".equals(match.getStage())) {
                if (map.containsKey(match.getHomeTeam())) map.get(match.getHomeTeam()).addResult(match.getHomeGoals(), match.getAwayGoals());
                if (map.containsKey(match.getAwayTeam())) map.get(match.getAwayTeam()).addResult(match.getAwayGoals(), match.getHomeGoals());
            }
        }
        return map.values().stream().sorted((r1, r2) -> {
            if (r1.points != r2.points) return r2.points - r1.points;
            if (r1.goalDifference != r2.goalDifference) return r2.goalDifference - r1.goalDifference;
            if (r1.goalsFor != r2.goalsFor) return r2.goalsFor - r1.goalsFor;
            return r1.club.getName().compareTo(r2.club.getName());
        }).collect(Collectors.toList());
    }

    public List<String> getPlayoffSeriesSummaries() {
        List<String> summaries = new ArrayList<>();
        Map<String, int[]> seriesScores = new HashMap<>();
        for (Match m : schedule) {
            if (m.getStage() != null && !m.getStage().equals("REGULAR")) {
                String pairId = getPairId(m.getHomeTeam(), m.getAwayTeam());
                seriesScores.putIfAbsent(pairId, new int[2]);
                if (m.isPlayed()) {
                    int[] score = seriesScores.get(pairId);
                    if (m.getHomeGoals() > m.getAwayGoals()) score[0]++;
                    else if (m.getAwayGoals() > m.getHomeGoals()) score[1]++;
                }
            }
        }
        for (Map.Entry<String, int[]> entry : seriesScores.entrySet()) {
            summaries.add(entry.getKey() + " (" + entry.getValue()[0] + " - " + entry.getValue()[1] + ")");
        }
        return summaries;
    }

    public List<PlayoffSeries> getPlayoffSeries() {
        return new ArrayList<>(playoffSeries.values());
    }

    private String getPairId(Club c1, Club c2) {
        List<String> names = Arrays.asList(c1.getName(), c2.getName());
        Collections.sort(names);
        return names.get(0) + " vs " + names.get(1);
    }

    public List<PlayerStats> getPlayerStats(String type) {
        Map<Player, PlayerStats> map = new HashMap<>();
        for (Club club : clubs) {
            for (Player p : club.getSquad()) map.put(p, new PlayerStats(p));
        }
        for (Match match : schedule) {
            if (match.isPlayed()) {
                for (Player p : match.getGoalScorers()) map.get(p).goals++;
                for (Player p : match.getAssisters()) map.get(p).assists++;
                for (Map.Entry<Player, String> entry : match.getCards().entrySet()) {
                    if (entry.getValue().equals("Amarelo")) map.get(entry.getKey()).yellowCards++;
                    else map.get(entry.getKey()).redCards++;
                }
            }
        }
        return map.values().stream()
            .filter(ps -> (ps.goals > 0 || ps.assists > 0 || ps.yellowCards > 0 || ps.redCards > 0))
            .sorted((ps1, ps2) -> {
                if (type.equals("Gols")) return ps2.goals - ps1.goals;
                if (type.equals("Assists")) return ps2.assists - ps1.assists;
                if (type.equals("Amarelos")) return ps2.yellowCards - ps1.yellowCards;
                return ps2.redCards - ps1.redCards;
            })
            .limit(20)
            .collect(Collectors.toList());
    }
}
