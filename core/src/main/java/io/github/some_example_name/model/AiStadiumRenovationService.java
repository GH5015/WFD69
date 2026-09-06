package io.github.some_example_name.model;

/** Decide investimentos de capacidade para as franquias controladas pela IA. */
public final class AiStadiumRenovationService {
    private static final double MINIMUM_OCCUPANCY = .78d;
    private static final int MINIMUM_HOME_MATCHES = 3;
    private static final long MINIMUM_CASH_RESERVE = 8_000_000L;

    private AiStadiumRenovationService() { }

    /**
     * Executado no fechamento mensal. Cada franquia pode aprovar no máximo uma
     * ampliação por temporada, mesmo que uma obra curta termine no mesmo ano.
     */
    public static void processMonthly(League league) {
        if (league == null) return;
        for (Club club : league.getClubs()) {
            StadiumRenovationPlan plan = choosePlan(league, club);
            if (plan == null || !club.startStadiumRenovation(plan)) continue;
            club.setLastAiStadiumRenovationSeason(league.getCurrentSeason());
            resetFutureHomeAttendance(league, club);
        }
    }

    /** Retorna o projeto que a diretoria da IA aprovaria neste momento. */
    public static StadiumRenovationPlan choosePlan(League league, Club club) {
        if (league == null || club == null || club.isUserControlled()) return null;
        if (club.isStadiumRenovationInProgress()) return null;
        if (club.getLastAiStadiumRenovationSeason() >= league.getCurrentSeason()) return null;
        if (club.getStadiumCapacity() >= StadiumRenovationPlan.MAX_CAPACITY) return null;

        OccupancySample sample = occupancySample(league, club);
        if (sample.matches < MINIMUM_HOME_MATCHES || sample.rate < MINIMUM_OCCUPANCY) return null;

        long balance = club.getFinance().getBalance();
        long reserve = Math.max(
            MINIMUM_CASH_RESERVE,
            club.getFinance().getTotalMonthlyExpenses() * 4L
        );
        if (balance < StadiumRenovationPlan.STANDS.getCost() + reserve) return null;

        int score = occupancyScore(sample.rate)
            + cashScore(balance, reserve)
            + reputationScore(club.getReputation())
            + capacityScore(club.getStadiumCapacity())
            + phaseScore(ClubNeedEvaluator.getTeamPhase(club));

        if (score >= 72 && canBuild(club, StadiumRenovationPlan.NEW_RING, reserve)) {
            return StadiumRenovationPlan.NEW_RING;
        }
        if (score >= 54 && canBuild(club, StadiumRenovationPlan.STRUCTURE, reserve)) {
            return StadiumRenovationPlan.STRUCTURE;
        }
        if (score >= 42 && canBuild(club, StadiumRenovationPlan.STANDS, reserve)) {
            return StadiumRenovationPlan.STANDS;
        }
        return null;
    }

    public static double calculateOccupancy(League league, Club club) {
        return occupancySample(league, club).rate;
    }

    private static OccupancySample occupancySample(League league, Club club) {
        if (league == null || club == null || club.getStadiumCapacity() <= 0) {
            return new OccupancySample(0, 0d);
        }
        long attendance = 0L;
        int matches = 0;
        for (Match match : league.getSchedule()) {
            if (match.getHomeTeam() != club || !match.isPlayed() || !match.isAttendanceCalculated()) continue;
            attendance += match.getAttendance();
            matches++;
        }
        if (matches == 0) return new OccupancySample(0, 0d);
        double rate = attendance / (double) (matches * club.getStadiumCapacity());
        return new OccupancySample(matches, Math.max(0d, Math.min(1d, rate)));
    }

    private static int occupancyScore(double occupancy) {
        return (int) Math.round(Math.max(0d, occupancy - .70d) * 100d);
    }

    private static int cashScore(long balance, long reserve) {
        if (balance >= reserve * 4L) return 16;
        if (balance >= reserve * 3L) return 12;
        if (balance >= reserve * 2L) return 8;
        return 4;
    }

    private static int reputationScore(int reputation) {
        return Math.max(-6, Math.min(18, (int) Math.round((reputation - 65) * .6d)));
    }

    private static int capacityScore(int capacity) {
        if (capacity <= 30_000) return 18;
        if (capacity <= 45_000) return 14;
        if (capacity <= 60_000) return 10;
        if (capacity <= 75_000) return 5;
        return 0;
    }

    private static int phaseScore(ClubNeedEvaluator.TeamPhase phase) {
        if (phase == null) return 0;
        switch (phase) {
            case CONTENDER: return 12;
            case BUYER: return 8;
            case SELLER: return -2;
            case REBUILDING:
            default: return -6;
        }
    }

    private static boolean canBuild(Club club, StadiumRenovationPlan plan, long reserve) {
        return club.getStadiumCapacity() + plan.getAdditionalCapacity()
                <= StadiumRenovationPlan.MAX_CAPACITY
            && club.getFinance().getBalance() - plan.getCost() >= reserve;
    }

    private static void resetFutureHomeAttendance(League league, Club club) {
        for (Match match : league.getSchedule()) {
            if (!match.isPlayed() && match.getHomeTeam() == club) match.resetAttendanceProjection();
        }
    }

    private static final class OccupancySample {
        final int matches;
        final double rate;

        OccupancySample(int matches, double rate) {
            this.matches = matches;
            this.rate = rate;
        }
    }
}
