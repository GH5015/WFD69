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

    public void nextSeason() {
        this.currentSeason++;
        this.currentStage = "REGULAR";
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
            lastProcessedDate = schedule.get(currentMatchIndex).getDate();
            if (currentDate == null || currentDate.before(lastProcessedDate)) currentDate = lastProcessedDate;
            currentMatchIndex++;
        }

        // Se acabaram os jogos da fase atual, verifica se precisa disparar os Playoffs ou Nova Temporada
        if (getNextMatch() == null) {
            checkAndAdvanceStage();
        }
    }

    public void checkAndAdvanceStage() {
        if ("REGULAR".equals(this.currentStage)) {
            setupPlayoffs();
        } else if ("PLAYOFFS".equals(this.currentStage)) {
            startNewSeason();
        }
    }

    private void setupPlayoffs() {
        this.currentStage = "PLAYOFFS";
        List<StandingsRow> standings = getFullStandings(null);
        if (standings.size() < 4) return;

        // Pega os 4 melhores colocados da fase regular
        Club c1 = standings.get(0).club;
        Club c2 = standings.get(1).club;
        Club c3 = standings.get(2).club;
        Club c4 = standings.get(3).club;

        Calendar cal = Calendar.getInstance();
        if (currentDate != null) {
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, 3); // Dá 3 dias de descanso antes do playoff
        }
        Date playoffDate = cal.getTime();

        Match semi1 = new Match(c1, c4);
        semi1.setDate(playoffDate);
        semi1.setStage("PLAYOFFS");

        Match semi2 = new Match(c2, c3);
        semi2.setDate(playoffDate);
        semi2.setStage("PLAYOFFS");

        this.schedule.add(semi1);
        this.schedule.add(semi2);
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
            return r2.goalDifference - r1.goalDifference;
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
