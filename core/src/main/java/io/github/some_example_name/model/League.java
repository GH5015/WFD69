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
    private final List<TradeRecord> tradeHistory = new ArrayList<>();
    private final List<Club> draftLotteryOrder = new ArrayList<>();
    private final List<DraftSelection> draftSelections = new ArrayList<>();
    private boolean draftFinalized;
    private String finalHostCity;
    private boolean offseasonTransitionProcessed;

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
    public List<TradeRecord> getTradeHistory() { return new ArrayList<>(tradeHistory); }
    public void recordTrade(TradeRecord record) { if (record != null) tradeHistory.add(0, record); }
    public boolean isDraftLotteryCompleted() { return !draftLotteryOrder.isEmpty(); }
    public List<Club> getDraftLotteryOrder() { return new ArrayList<>(draftLotteryOrder); }
    public List<DraftSelection> getDraftSelections() { return new ArrayList<>(draftSelections); }
    public boolean isDraftFinalized() { return draftFinalized; }
    public void finalizeDraft() { draftFinalized = true; }
    public boolean isDraftPickUsed(DraftPick pick) { for (DraftSelection selection : draftSelections) if (selection.getPick() == pick) return true; return false; }
    public void recordDraftSelection(DraftPick pick, Player player) {
        if (pick == null || player == null || isDraftPickUsed(pick)) return;
        draftSelections.add(new DraftSelection(pick, player));
        // Salário rookie parte de OVR, idade e potencial; a posição da pick
        // só aplica um prêmio moderado. Assim um prospecto não supera uma
        // estrela consolidada apenas por ter sido escolhido cedo.
        long annualSalary = calculateRookieAnnualSalary(pick, player);
        player.renewContract(annualSalary, pick.getRound() == 1 ? 4 : 2, currentSeason);
        player.transferTo(pick.getCurrentOwner());
    }
    private long calculateRookieAnnualSalary(DraftPick pick, Player player) {
        int overallPick = (pick.getRound() - 1) * DraftOrderService.PICKS_PER_ROUND + pick.getProjectedPosition();
        int potentialGap = Math.max(0, player.getPotential() - player.getOverall());
        double marketMonthly = 7_000d
            + Math.pow(Math.max(0, player.getOverall() - 55), 2) * 30d
            + potentialGap * 200d
            + (player.getAge() <= 20 ? 1_500d : 0d);
        double pickPremium = pick.getRound() == 1
            ? 1.22d - (Math.min(20, overallPick) - 1) * 0.018d
            : 0.70d - (Math.min(40, overallPick) - 21) * 0.012d;
        long annual = Math.round(marketMonthly * Math.max(0.45d, pickPremium) * 12d / 10_000d) * 10_000L;
        return pick.getRound() == 1
            ? Math.max(260_000L, Math.min(780_000L, annual))
            : Math.max(120_000L, Math.min(360_000L, annual));
    }
    public List<Club> getDraftLotteryParticipants() {
        List<Club> participants = new ArrayList<>();
        for (Club club : clubs) if (!playoffSeeds.containsKey(club)) participants.add(club);
        if (participants.size() == clubs.size() && clubs.size() >= 8) {
            List<StandingsRow> standings = getFullStandings(null); Set<Club> qualified = new HashSet<>();
            for (int i = 0; i < Math.min(8, standings.size()); i++) qualified.add(standings.get(i).club);
            participants.removeIf(qualified::contains);
        }
        participants.sort(Comparator.comparingInt((Club c) -> getClubRecordValues(c)[3]).thenComparingInt(c -> getClubRecordValues(c)[4]).thenComparing(Club::getName));
        return participants;
    }
    /** Chances próximas no fundo da tabela para desestimular tanking. */
    public Map<Club, Integer> getDraftLotteryOdds() {
        List<Club> participants = getDraftLotteryParticipants(); int[] base = {20,17,15,14,10,7,5,4,3,2,2,1};
        Map<Club, Integer> odds = new LinkedHashMap<>();
        if (participants.size() == 12) for (int i=0;i<participants.size();i++) odds.put(participants.get(i), base[i]);
        else { int remaining=100; for(int i=0;i<participants.size();i++){int chance=i==participants.size()-1?remaining:Math.max(1,(int)Math.round(100d*(participants.size()-i)/(participants.size()*(participants.size()+1)/2d)));chance=Math.min(chance,remaining-Math.max(0,participants.size()-i-1));odds.put(participants.get(i),chance);remaining-=chance;} }
        return odds;
    }
    public String getClubRecord(Club club) { int[] r=getClubRecordValues(club); return r[0]+"-"+r[1]+"-"+r[2]; }
    // vitórias, empates, derrotas, pontos e saldo da temporada regular
    private int[] getClubRecordValues(Club club) {
        int wins=0,draws=0,losses=0,difference=0;
        for (Match match:schedule) { if(!match.isPlayed()||!"REGULAR".equals(match.getStage())) continue; boolean home=match.getHomeTeam()==club,away=match.getAwayTeam()==club; if(!home&&!away)continue; int scored=home?match.getHomeGoals():match.getAwayGoals(), conceded=home?match.getAwayGoals():match.getHomeGoals(); difference+=scored-conceded; if(scored>conceded)wins++;else if(scored==conceded)draws++;else losses++; }
        return new int[]{wins,draws,losses,wins*3+draws,difference};
    }
    public List<Club> runDraftLottery() {
        if (!draftLotteryOrder.isEmpty()) return getDraftLotteryOrder();
        Map<Club,Integer> odds=getDraftLotteryOdds(); List<Club> remaining=new ArrayList<>(odds.keySet()); Random random=new Random((long)currentSeason*31L+1971L);
        // Apenas as quatro primeiras escolhas são sorteadas. A cada prêmio os
        // pesos dos clubes restantes são normalizados novamente pelo sorteio
        // ponderado; o restante preserva a ordem da campanha.
        for (int pick = 0; pick < Math.min(4, remaining.size()); pick++) {
            int total=0;for(Club c:remaining)total+=odds.get(c);int draw=random.nextInt(Math.max(1,total));Club winner=remaining.get(0);
            for(Club c:remaining){draw-=odds.get(c);if(draw<0){winner=c;break;}}
            draftLotteryOrder.add(winner);remaining.remove(winner);
        }
        draftLotteryOrder.addAll(remaining);
        List<Club> playoffClubs=new ArrayList<>(clubs);playoffClubs.removeAll(draftLotteryOrder);
        playoffClubs.sort(Comparator.comparingInt((Club c)->getClubRecordValues(c)[3]).thenComparingInt(c->getClubRecordValues(c)[4]).thenComparing(Club::getName));
        draftLotteryOrder.addAll(playoffClubs);
        return getDraftLotteryOrder();
    }
    public String getFinalHostCity() { return finalHostCity; }
    public void drawFinalHostCity() {
        if (finalHostCity != null) return;
        String[] cities = {"New York", "London", "Rio de Janeiro", "München", "Paris", "Tokyo", "Milano"};
        finalHostCity = cities[new Random((long) currentSeason * 53L + 71L).nextInt(cities.length)];
    }

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
        this.finalHostCity = null;
        processOffseasonTransition();

        // A transição da final para a Off Season sempre começa em novembro,
        // deixando as janelas de renovação e Free Agency disponíveis no
        // painel de operações logo após os playoffs.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(currentSeason, Calendar.NOVEMBER, 1, 12, 0, 0);
        this.currentDate = calendar.getTime();
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
            // Sem decisão do usuário até o início da temporada: a diretoria
            // contrata automaticamente uma opção básica para não quebrar sistemas.
            club.replaceExpiredStaff(this.currentSeason);
            for (Player p : club.getSquad()) {
                p.resetSeasonStats();
            }
        }


        // Reinicia a agenda mantendo o calendário zerado
        this.currentMatchIndex = 0;
        this.schedule.clear();
        this.playoffSeries.clear();
        this.playoffSeeds.clear();
        this.draftLotteryOrder.clear();
        this.draftSelections.clear();
        this.draftFinalized = false;
        this.offseasonTransitionProcessed = false;
        DraftOrderService.initializeDraftPicks(this, currentSeason + 1);
    }

    /** Fechamento automático: caixa, envelhecimento, aposentadorias e picks. */
    private void processOffseasonTransition() {
        if (offseasonTransitionProcessed) return;
        offseasonTransitionProcessed = true;
        for (Club club : clubs) {
            club.getFinance().applyMonthlyBalance();
            List<Player> retiring = new ArrayList<>();
            for (Player player : club.getSquad()) {
                player.recover(30);
                player.setAge(player.getAge() + 1);
                if (player.getAge() >= 40 && club.getSquad().size() - retiring.size() > TradeRulesValidator.MIN_ROSTER_SIZE) {
                    retiring.add(player);
                }
            }
            for (Player player : retiring) player.transferTo(null);
        }
        DraftOrderService.initializeDraftPicks(this, currentSeason + 1);
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
