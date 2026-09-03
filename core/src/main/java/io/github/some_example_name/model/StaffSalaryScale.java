package io.github.some_example_name.model;

/** Shared annual asking salaries for initial staff and the market, not signed contracts. */
public final class StaffSalaryScale {
    private static final int[] QUALITY = {50, 58, 64, 72, 80, 88, 95, 100};
    private static final long[] ANNUAL = {120_000L, 200_000L, 320_000L, 500_000L,
        760_000L, 1_100_000L, 1_600_000L, 2_000_000L};

    private StaffSalaryScale() { }

    /** Continuous rising curve: elite quality carries a substantially larger premium. */
    public static long annualSalary(StaffRole role, int quality) {
        int q = Math.max(50, Math.min(100, quality));
        int upper = 1;
        while (upper < QUALITY.length - 1 && q > QUALITY[upper]) upper++;
        double progress = (q - QUALITY[upper - 1]) / (double) (QUALITY[upper] - QUALITY[upper - 1]);
        double base = ANNUAL[upper - 1] + progress * (ANNUAL[upper] - ANNUAL[upper - 1]);
        double roleFactor;
        switch (role) {
            case COACH: roleFactor = 1.25d; break;
            case DOCTOR: roleFactor = 1.10d; break;
            case FITNESS_COACH: roleFactor = .90d; break;
            case SCOUT: roleFactor = .85d; break;
            default: roleFactor = 1d;
        }
        return Math.round(base * roleFactor / 10_000d) * 10_000L;
    }
}
