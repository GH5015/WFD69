package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/** Memória persistente das temporadas, carreiras, recordes e lendas da WFL. */
public class LeagueHistory {
    private final List<SeasonRecord> seasons = new ArrayList<>();
    private final Map<String, PlayerCareer> playerCareers = new LinkedHashMap<>();
    private final Map<Integer, DraftRecord> firstOverallPicks = new LinkedHashMap<>();
    private final List<HallOfFameEntry> hallOfFame = new ArrayList<>();

    public void captureSeason(League league) {
        if (league == null || findSeason(league.getCurrentSeason()) != null) return;

        int year = league.getCurrentSeason();
        Club champion = null;
        Club runnerUp = null;
        for (PlayoffSeries series : league.getPlayoffSeries()) {
            if (!"FINAL".equals(series.getRound()) || !series.isComplete()) continue;
            champion = series.getWinner();
            runnerUp = champion == series.getFirstSeed() ? series.getSecondSeed() : series.getFirstSeed();
            break;
        }
        List<StandingsRow> standings = league.getFullStandings(null);
        if (champion == null && !standings.isEmpty()) champion = standings.get(0).club;
        if (runnerUp == null && standings.size() > 1) runnerUp = standings.get(1).club;

        List<Player> players = new ArrayList<>();
        for (Club club : league.getClubs()) players.addAll(club.getSquad());
        Player mvp = players.stream().max(Comparator.comparingDouble(this::mvpScore)).orElse(null);
        Player topScorer = players.stream().max(Comparator
            .comparingInt(Player::getSeasonGoals)
            .thenComparingDouble(Player::getSeasonAverageRating)
            .thenComparingInt(Player::getOverall)).orElse(null);
        Player assistLeader = players.stream().max(Comparator
            .comparingInt(Player::getSeasonAssists)
            .thenComparingDouble(Player::getSeasonAverageRating)
            .thenComparingInt(Player::getOverall)).orElse(null);
        Player bestYoung = players.stream().filter(player -> player.getAge() <= 21).max(Comparator
            .comparingDouble(this::youngPlayerScore)).orElse(null);
        Player rookie = players.stream()
            .filter(player -> player.getDraftedYear() == year)
            .max(Comparator.comparingDouble(this::youngPlayerScore)).orElse(null);
        if (rookie == null) {
            rookie = players.stream().filter(player -> player.getAge() <= 20)
                .max(Comparator.comparingDouble(this::youngPlayerScore)).orElse(bestYoung);
        }
        Player bestGoalkeeper = players.stream()
            .filter(player -> player.getPrimaryPosition() == Position.GK)
            .max(Comparator.comparingDouble(this::goalkeeperScore)).orElse(null);
        Player bestDefender = players.stream()
            .filter(player -> isDefender(player.getPrimaryPosition()))
            .max(Comparator.comparingDouble(this::defenderScore)).orElse(null);
        List<TeamOfYearMember> teamOfYear = buildTeamOfYear(players);

        for (Player player : players) {
            PlayerCareer career = playerCareers.computeIfAbsent(playerKey(player), key ->
                new PlayerCareer(key, player.getName(), player.getNationality()));
            Club club = player.getCurrentClub();
            career.addSeason(new PlayerSeason(
                year,
                club != null ? club.getName() : "Sem clube",
                player.getSeasonAppearances(),
                player.getSeasonGoals(),
                player.getSeasonAssists(),
                player.getSeasonCleanSheets(),
                player.getSeasonYellowCards(),
                player.getSeasonRedCards(),
                player.getSeasonAverageRating(),
                player.getOverall(),
                player.getAge()
            ));
            if (club == champion) career.titles++;
        }
        careerOf(mvp).incrementMvp();
        careerOf(topScorer).incrementTopScorerAwards();

        // O Draft exibido no resumo é o realizado na Off Season imediatamente
        // posterior (temporada 1970 -> Draft 1971).
        DraftRecord firstPick = firstOverallPicks.get(year + 1);
        seasons.add(new SeasonRecord(
            year,
            nameOf(champion),
            nameOf(runnerUp),
            nameOf(mvp),
            nameOf(topScorer),
            topScorer != null ? topScorer.getSeasonGoals() : 0,
            nameOf(bestYoung),
            nameOf(assistLeader),
            assistLeader != null ? assistLeader.getSeasonAssists() : 0,
            nameOf(rookie),
            nameOf(bestGoalkeeper),
            nameOf(bestDefender),
            teamOfYear,
            firstPick
        ));
        seasons.sort(Comparator.comparingInt(SeasonRecord::getYear).reversed());
    }

    public void recordDraftSelection(DraftPick pick, Player player) {
        if (pick == null || player == null || pick.getRound() != 1 || pick.getProjectedPosition() != 1) return;
        Club owner = pick.getCurrentOwner();
        Club original = pick.getOriginalOwner();
        DraftRecord draftRecord = new DraftRecord(
            pick.getYear(), player.getName(), nameOf(owner), nameOf(original)
        );
        firstOverallPicks.putIfAbsent(pick.getYear(), draftRecord);
        SeasonRecord completedSeason = findSeason(pick.getYear() - 1);
        if (completedSeason != null && completedSeason.firstOverallPick == null) {
            completedSeason.firstOverallPick = draftRecord;
        }
    }

    public void considerForHallOfFame(Player player, int inductionYear) {
        PlayerCareer career = playerCareers.get(playerKey(player));
        if (career == null || findHallEntry(career.getPlayerId()) != null) return;
        int legacy = career.getLegacyPoints();
        if (legacy < 220 && career.getTitles() < 2 && career.getMvpAwards() < 1
            && career.getGoals() < 100 && career.getAppearances() < 180) return;
        hallOfFame.add(new HallOfFameEntry(career.getPlayerId(), career.getPlayerName(), inductionYear, legacy));
        hallOfFame.sort(Comparator.comparingInt(HallOfFameEntry::getInductionYear).reversed()
            .thenComparing(Comparator.comparingInt(HallOfFameEntry::getLegacyPoints).reversed()));
    }

    private double mvpScore(Player player) {
        double rating = player.getSeasonAverageRating() > 0 ? player.getSeasonAverageRating() : player.getOverall() / 10.0;
        return rating * 10d + player.getSeasonGoals() * .42d + player.getSeasonAssists() * .30d
            + player.getSeasonAppearances() * .06d + player.getOverall() * .04d;
    }

    private double youngPlayerScore(Player player) {
        return mvpScore(player) + player.getPotential() * .12d + (22 - player.getAge()) * .8d;
    }

    private double goalkeeperScore(Player player) {
        return mvpScore(player) + player.getSeasonCleanSheets() * 1.8d;
    }

    private double defenderScore(Player player) {
        return mvpScore(player) + player.getSeasonCleanSheets() * .55d;
    }

    private boolean isDefender(Position position) {
        return position != null && position.name().matches("CB|SW|LB|RB|LWB|RWB");
    }

    private List<TeamOfYearMember> buildTeamOfYear(List<Player> players) {
        Position[] formation = {
            Position.GK, Position.RB, Position.CB, Position.CB, Position.LB,
            Position.CM, Position.CM, Position.CAM, Position.RW, Position.ST, Position.LW
        };
        List<TeamOfYearMember> result = new ArrayList<>();
        Set<Player> selected = new HashSet<>();
        for (Position slot : formation) {
            Player choice = players.stream().filter(player -> !selected.contains(player))
                .max(Comparator.comparingDouble(player -> mvpScore(player) + positionFit(player.getPrimaryPosition(), slot)))
                .orElse(null);
            if (choice == null) continue;
            selected.add(choice);
            result.add(new TeamOfYearMember(slot.name(), choice.getName(), nameOf(choice.getCurrentClub()),
                choice.getOverall(), choice.getSeasonAverageRating()));
        }
        return result;
    }

    private double positionFit(Position actual, Position slot) {
        if (actual == slot) return 100d;
        if (actual == null) return -100d;
        String a = actual.name();
        String s = slot.name();
        if (s.equals("CB") && a.matches("SW|LB|RB|CDM")) return 65d;
        if (s.equals("RB") && a.matches("RWB|CB")) return 65d;
        if (s.equals("LB") && a.matches("LWB|CB")) return 65d;
        if (s.equals("CM") && a.matches("CDM|CAM|LM|RM")) return 65d;
        if (s.equals("CAM") && a.matches("CM|CF|LM|RM")) return 65d;
        if (s.equals("RW") && a.matches("RM|CF|ST")) return 60d;
        if (s.equals("LW") && a.matches("LM|CF|ST")) return 60d;
        if (s.equals("ST") && a.matches("CF|RW|LW")) return 60d;
        return -80d;
    }

    private PlayerCareer careerOf(Player player) {
        return player == null ? PlayerCareer.EMPTY : playerCareers.getOrDefault(playerKey(player), PlayerCareer.EMPTY);
    }

    private static String playerKey(Player player) {
        if (player == null) return "";
        return player.getId() != null ? player.getId() : player.getName();
    }

    private static String nameOf(Club club) { return club != null ? club.getName() : "—"; }
    private static String nameOf(Player player) { return player != null ? player.getName() : "—"; }

    public SeasonRecord findSeason(int year) {
        for (SeasonRecord record : seasons) if (record.year == year) return record;
        return null;
    }

    private HallOfFameEntry findHallEntry(String playerId) {
        for (HallOfFameEntry entry : hallOfFame) if (entry.playerId.equals(playerId)) return entry;
        return null;
    }

    public List<SeasonRecord> getSeasons() { return new ArrayList<>(seasons); }
    public List<HallOfFameEntry> getHallOfFame() { return new ArrayList<>(hallOfFame); }
    public DraftRecord getFirstOverallPick(int year) { return firstOverallPicks.get(year); }

    public List<PlayerCareer> getPlayerCareers() {
        List<PlayerCareer> result = new ArrayList<>(playerCareers.values());
        result.sort(Comparator.comparingInt(PlayerCareer::getLegacyPoints).reversed()
            .thenComparing(PlayerCareer::getPlayerName));
        return result;
    }

    public PlayerCareer getPlayerCareer(String playerId) { return playerCareers.get(playerId); }

    public PlayerCareer leaderByGoals() { return getPlayerCareers().stream().max(Comparator.comparingInt(PlayerCareer::getGoals)).orElse(null); }
    public PlayerCareer leaderByAssists() { return getPlayerCareers().stream().max(Comparator.comparingInt(PlayerCareer::getAssists)).orElse(null); }
    public PlayerCareer leaderByTitles() { return getPlayerCareers().stream().max(Comparator.comparingInt(PlayerCareer::getTitles)).orElse(null); }
    public PlayerCareer leaderByAppearances() { return getPlayerCareers().stream().max(Comparator.comparingInt(PlayerCareer::getAppearances)).orElse(null); }

    public static final class SeasonRecord {
        private final int year;
        private final String champion;
        private final String runnerUp;
        private final String mvp;
        private final String topScorer;
        private final int topScorerGoals;
        private final String bestYoung;
        private final String assistLeader;
        private final int assistLeaderAssists;
        private final String rookieOfYear;
        private final String bestGoalkeeper;
        private final String bestDefender;
        private final List<TeamOfYearMember> teamOfYear;
        private DraftRecord firstOverallPick;

        private SeasonRecord(int year, String champion, String runnerUp, String mvp, String topScorer,
                             int topScorerGoals, String bestYoung, String assistLeader, int assistLeaderAssists,
                             String rookieOfYear, String bestGoalkeeper, String bestDefender,
                             List<TeamOfYearMember> teamOfYear, DraftRecord firstOverallPick) {
            this.year = year;
            this.champion = champion;
            this.runnerUp = runnerUp;
            this.mvp = mvp;
            this.topScorer = topScorer;
            this.topScorerGoals = topScorerGoals;
            this.bestYoung = bestYoung;
            this.assistLeader = assistLeader;
            this.assistLeaderAssists = assistLeaderAssists;
            this.rookieOfYear = rookieOfYear;
            this.bestGoalkeeper = bestGoalkeeper;
            this.bestDefender = bestDefender;
            this.teamOfYear = new ArrayList<>(teamOfYear);
            this.firstOverallPick = firstOverallPick;
        }
        public int getYear() { return year; }
        public String getChampion() { return champion; }
        public String getRunnerUp() { return runnerUp; }
        public String getMvp() { return mvp; }
        public String getTopScorer() { return topScorer; }
        public int getTopScorerGoals() { return topScorerGoals; }
        public String getBestYoung() { return bestYoung; }
        public String getAssistLeader() { return assistLeader; }
        public int getAssistLeaderAssists() { return assistLeaderAssists; }
        public String getRookieOfYear() { return rookieOfYear; }
        public String getBestGoalkeeper() { return bestGoalkeeper; }
        public String getBestDefender() { return bestDefender; }
        public List<TeamOfYearMember> getTeamOfYear() { return new ArrayList<>(teamOfYear); }
        public DraftRecord getFirstOverallPick() { return firstOverallPick; }
    }

    public static final class TeamOfYearMember {
        private final String slot;
        private final String playerName;
        private final String clubName;
        private final int overall;
        private final double averageRating;
        private TeamOfYearMember(String slot, String playerName, String clubName, int overall, double averageRating) {
            this.slot = slot;
            this.playerName = playerName;
            this.clubName = clubName;
            this.overall = overall;
            this.averageRating = averageRating;
        }
        public String getSlot() { return slot; }
        public String getPlayerName() { return playerName; }
        public String getClubName() { return clubName; }
        public int getOverall() { return overall; }
        public double getAverageRating() { return averageRating; }
    }

    public static final class DraftRecord {
        private final int year;
        private final String playerName;
        private final String ownerClub;
        private final String originalClub;
        private DraftRecord(int year, String playerName, String ownerClub, String originalClub) {
            this.year = year;
            this.playerName = playerName;
            this.ownerClub = ownerClub;
            this.originalClub = originalClub;
        }
        public int getYear() { return year; }
        public String getPlayerName() { return playerName; }
        public String getOwnerClub() { return ownerClub; }
        public String getOriginalClub() { return originalClub; }
        public boolean isViaTrade() { return !ownerClub.equals(originalClub); }
    }

    public static final class PlayerSeason {
        private final int year;
        private final String clubName;
        private final int appearances;
        private final int goals;
        private final int assists;
        private final int cleanSheets;
        private final int yellowCards;
        private final int redCards;
        private final double averageRating;
        private final int overall;
        private final int age;
        private PlayerSeason(int year, String clubName, int appearances, int goals, int assists, int cleanSheets,
                             int yellowCards, int redCards, double averageRating, int overall, int age) {
            this.year = year;
            this.clubName = clubName;
            this.appearances = appearances;
            this.goals = goals;
            this.assists = assists;
            this.cleanSheets = cleanSheets;
            this.yellowCards = yellowCards;
            this.redCards = redCards;
            this.averageRating = averageRating;
            this.overall = overall;
            this.age = age;
        }
        public int getYear() { return year; }
        public String getClubName() { return clubName; }
        public int getAppearances() { return appearances; }
        public int getGoals() { return goals; }
        public int getAssists() { return assists; }
        public int getCleanSheets() { return cleanSheets; }
        public int getYellowCards() { return yellowCards; }
        public int getRedCards() { return redCards; }
        public double getAverageRating() { return averageRating; }
        public int getOverall() { return overall; }
        public int getAge() { return age; }
    }

    public static final class PlayerCareer {
        private static final PlayerCareer EMPTY = new PlayerCareer("", "", "");
        private final String playerId;
        private final String playerName;
        private final String nationality;
        private final List<PlayerSeason> seasons = new ArrayList<>();
        private int titles;
        private int mvpAwards;
        private int topScorerAwards;
        private PlayerCareer(String playerId, String playerName, String nationality) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.nationality = nationality;
        }
        private void addSeason(PlayerSeason season) {
            seasons.removeIf(existing -> existing.year == season.year);
            seasons.add(season);
            seasons.sort(Comparator.comparingInt(PlayerSeason::getYear));
        }
        private void incrementMvp() { if (this != EMPTY) mvpAwards++; }
        private void incrementTopScorerAwards() { if (this != EMPTY) topScorerAwards++; }
        public String getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public String getNationality() { return nationality; }
        public List<PlayerSeason> getSeasons() { return new ArrayList<>(seasons); }
        public int getTitles() { return titles; }
        public int getMvpAwards() { return mvpAwards; }
        public int getTopScorerAwards() { return topScorerAwards; }
        public int getAppearances() { return seasons.stream().mapToInt(PlayerSeason::getAppearances).sum(); }
        public int getGoals() { return seasons.stream().mapToInt(PlayerSeason::getGoals).sum(); }
        public int getAssists() { return seasons.stream().mapToInt(PlayerSeason::getAssists).sum(); }
        public int getLegacyPoints() {
            return getAppearances() + getGoals() * 4 + getAssists() * 3 + titles * 40
                + mvpAwards * 55 + topScorerAwards * 35 + seasons.size() * 5;
        }
    }

    public static final class HallOfFameEntry {
        private final String playerId;
        private final String playerName;
        private final int inductionYear;
        private final int legacyPoints;
        private HallOfFameEntry(String playerId, String playerName, int inductionYear, int legacyPoints) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.inductionYear = inductionYear;
            this.legacyPoints = legacyPoints;
        }
        public String getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getInductionYear() { return inductionYear; }
        public int getLegacyPoints() { return legacyPoints; }
    }
}
