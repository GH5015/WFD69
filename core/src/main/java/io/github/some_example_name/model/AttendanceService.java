package io.github.some_example_name.model;

import java.util.List;

/** Calcula e congela o público oficial de cada partida da WFL. */
public final class AttendanceService {
    private AttendanceService() { }

    public static void ensureAttendance(League league, Match match) {
        if (match == null || match.isAttendanceCalculated()) return;
        int demand = estimateDemand(league, match);
        int capacity = match.getHomeTeam() == null ? 0 : match.getHomeTeam().getOperationalStadiumCapacity();
        int ticketPrice = match.getHomeTeam() == null ? 0 : match.getHomeTeam().getAverageTicketPrice();
        match.setAttendance(Math.min(capacity, demand), demand, ticketPrice);
    }

    public static int estimateDemand(League league, Match match) {
        if (match == null || match.getHomeTeam() == null || match.getAwayTeam() == null) return 0;
        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        int reputationDemand = 7_500 + Math.max(0, home.getReputation() - 70) * 600;
        int opponentDemand = 750 + Math.max(0, away.getReputation() - 70) * 180;
        int performanceDemand = performanceDemand(league, home);
        int recentFormDemand = Math.min(5, home.getWinStreak()) * 850
            - Math.min(4, home.getLossStreak()) * 1_200;
        int rivalryDemand = ClubProfile.rivalryLevel(home, away) * 1_600;
        int importanceDemand = importanceDemand(league, match);

        int variation = Math.floorMod((home.getName() + "|" + away.getName() + "|" + match.getDate()).hashCode(), 3_001) - 1_500;
        int demand = reputationDemand + opponentDemand + performanceDemand
            + recentFormDemand + rivalryDemand + importanceDemand + variation;
        demand = Math.round(demand * ticketDemandMultiplier(home));
        return Math.max(1_000, (int) Math.round(demand / 100d) * 100);
    }

    /**
     * Descontos ajudam pouco; preços acima da referência reduzem a procura
     * proporcionalmente ao aumento, até um piso de 20% da demanda.
     */
    public static float ticketDemandMultiplier(Club club) {
        if (club == null) return 1f;
        double priceRatio = club.getAverageTicketPrice() / (double) club.getSuggestedTicketPrice();
        if (priceRatio <= 1d) return (float) Math.min(1.08d, 1d + (1d - priceRatio) * .20d);
        return (float) Math.max(.20d, Math.pow(priceRatio, -1.65d));
    }

    public static Match findNextHomeMatch(League league, Club club) {
        if (league == null || club == null) return null;
        for (Match match : league.getSchedule()) {
            if (!match.isPlayed() && match.getHomeTeam() == club) return match;
        }
        return null;
    }

    private static int performanceDemand(League league, Club home) {
        int demand = 0;
        if (home.getTotalGames() > 0) {
            double winRate = home.getTotalWins() / (double) home.getTotalGames();
            double confidence = Math.min(1d, home.getTotalGames() / 10d);
            demand += Math.round((float) ((winRate - .50d) * 10_000d * confidence));
        }
        if (league == null) return demand;
        List<StandingsRow> rows = league.getFullStandings(null);
        for (int index = 0; index < rows.size(); index++) {
            StandingsRow row = rows.get(index);
            if (row.club != home || row.matches == 0) continue;
            int position = index + 1;
            int positionBonus = position == 1 ? 5_000 : position <= 4 ? 3_000 : position <= 8 ? 1_200 : 0;
            demand += Math.round(positionBonus * Math.min(1f, row.matches / 10f));
            break;
        }
        return demand;
    }

    private static int importanceDemand(League league, Match match) {
        String stage = match.getStage() == null ? "REGULAR" : match.getStage().toUpperCase();
        if ("F".equals(stage) || "FINAL".equals(stage) || "FINALS".equals(stage)) return 18_000;
        if ("SF".equals(stage) || "SEMIFINAL".equals(stage)) return 12_000;
        if (!"REGULAR".equals(stage)) return 8_000;
        if (league != null && league.getRoundNumberForDate(match.getDate()) >= 32) return 3_000;
        return 0;
    }
}
