package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Duas escolhas por franquia, com numeração ajustada às expansões. */
public final class DraftOrderService {
    public static final int TOTAL_ROUNDS = 2;

    private DraftOrderService() { }

    public static int picksPerRound(League league, int year) {
        return Math.max(league.getClubs().size(), LeagueExpansionService.projectedClubCount(year));
    }

    public static void initializeDraftPicks(League league, int draftYear) {
        for (Club club : league.getClubs()) {
            for (int round = 1; round <= TOTAL_ROUNDS; round++) {
                if (!hasPick(league, draftYear, round, club)) {
                    club.getDraftPicks().add(new DraftPick(draftYear, round, club));
                }
            }
        }
    }

    public static List<DraftPick> getCurrentDraftOrder(League league, int draftYear) {
        List<Club> orderByStanding = new ArrayList<>();
        boolean upcomingDraft = draftYear == league.getCurrentSeason() + 1;
        if (upcomingDraft && league.isDraftLotteryCompleted()) {
            orderByStanding.addAll(league.getDraftLotteryOrder());
        } else {
            if (upcomingDraft) orderByStanding.addAll(league.getDraftLotteryParticipants());
            List<StandingsRow> standings = getStandingsForDraftOrder(league, true);
            for (StandingsRow row : standings) if (!orderByStanding.contains(row.club)) orderByStanding.add(row.club);
        }

        // Saves antigos e clubes adicionados depois de uma loteria também precisam de posição única.
        for (Club club : league.getClubs()) if (!orderByStanding.contains(club)) orderByStanding.add(club);
        List<DraftPick> picks = new ArrayList<>();
        for (Club club : league.getClubs()) {
            for (DraftPick pick : club.getDraftPicks()) {
                if (pick.getYear() == draftYear && pick.getRound() >= 1 && pick.getRound() <= TOTAL_ROUNDS) {
                    picks.add(pick);
                }
            }
        }

        double confidence = calculateProjectionConfidence(league);
        for (DraftPick pick : picks) {
            int position = orderByStanding.indexOf(pick.getOriginalOwner()) + 1;
            pick.setPicksPerRound(picksPerRound(league, draftYear));
            pick.setProjectedPosition(position <= 0 ? pick.getPicksPerRound() : position);
            pick.setProjectedPositionConfidence(confidence);
        }

        picks.sort(Comparator
            .comparingInt(DraftPick::getRound)
            .thenComparingInt(DraftPick::getProjectedPosition));
        return picks;
    }

    /** Atualiza todas as picks negociáveis, inclusive anos futuros já adquiridos em trocas. */
    public static void refreshAllPickProjections(League league) {
        if (league == null) return;
        Set<Integer> years = new TreeSet<>();
        for (Club club : league.getClubs()) {
            if (club == null) continue;
            for (DraftPick pick : club.getDraftPicks()) {
                if (pick != null && pick.getYear() >= league.getCurrentSeason() + 1) years.add(pick.getYear());
            }
        }
        if (years.isEmpty()) years.add(league.getCurrentSeason() + 1);
        for (Integer year : years) getCurrentDraftOrder(league, year);
    }

    private static double calculateProjectionConfidence(League league) {
        List<StandingsRow> standings = league.getFullStandings(null);
        if (standings.isEmpty()) return 0.35;
        double averageMatches = standings.stream().mapToInt(row -> row.matches).average().orElse(0.0);
        double totalRegularMatches = Math.max(1.0, (league.getClubs().size() - 1) * 2.0);
        double seasonProgress = Math.max(0.0, Math.min(1.0, averageMatches / totalRegularMatches));
        return 0.35 + (seasonProgress * 0.65);
    }

    /** Retorna a colocação de liga tradicional: 1º é o melhor colocado. */
    public static int getLeagueStandingPosition(League league, Club club) {
        List<StandingsRow> standings = getStandingsForDraftOrder(league, false);
        for (int index = 0; index < standings.size(); index++) {
            if (standings.get(index).club == club) return index + 1;
        }
        return 0;
    }

    private static List<StandingsRow> getStandingsForDraftOrder(League league, boolean worstFirst) {
        List<StandingsRow> standings = new ArrayList<>(league.getFullStandings(null));
        Comparator<StandingsRow> comparator;
        if (worstFirst) {
            comparator = Comparator
                .comparingInt((StandingsRow row) -> row.points)
                .thenComparingInt(row -> row.goalDifference)
                .thenComparingInt(row -> row.goalsFor)
                .thenComparing(row -> row.club.getName());
        } else {
            comparator = Comparator
                .comparingInt((StandingsRow row) -> row.points).reversed()
                .thenComparing(Comparator.comparingInt((StandingsRow row) -> row.goalDifference).reversed())
                .thenComparing(Comparator.comparingInt((StandingsRow row) -> row.goalsFor).reversed())
                .thenComparing(row -> row.club.getName());
        }
        standings.sort(comparator);
        return standings;
    }

    private static boolean hasPick(League league, int year, int round, Club originalOwner) {
        for (Club club : league.getClubs()) {
            for (DraftPick pick : club.getDraftPicks()) {
                if (pick.getYear() == year && pick.getRound() == round && pick.getOriginalOwner() == originalOwner) {
                    return true;
                }
            }
        }
        return false;
    }
}
