package io.github.some_example_name.model;

/**
 * Regras de negociação de renovação de contratos.
 * A avaliação é deliberadamente uma estimativa: a tela mostra a leitura do
 * staff, enquanto a resposta final considera salário, duração, desempenho e
 * espaço no teto salarial.
 */
public final class ContractRenewalService {

    public enum Outcome {
        ACCEPTED,
        COUNTER_OFFER,
        REJECTED,
        FREE_AGENCY
    }

    public static final class Demand {
        public final long minimumAnnualSalary;
        public final long desiredAnnualSalary;
        public final long maximumComfortableAnnualSalary;
        public final int preferredYears;
        public final int interestStars;
        public final String performanceSummary;

        private Demand(
            long minimumAnnualSalary,
            long desiredAnnualSalary,
            long maximumComfortableAnnualSalary,
            int preferredYears,
            int interestStars,
            String performanceSummary
        ) {
            this.minimumAnnualSalary = minimumAnnualSalary;
            this.desiredAnnualSalary = desiredAnnualSalary;
            this.maximumComfortableAnnualSalary = maximumComfortableAnnualSalary;
            this.preferredYears = preferredYears;
            this.interestStars = interestStars;
            this.performanceSummary = performanceSummary;
        }
    }

    public static final class Decision {
        public final Outcome outcome;
        public final String message;
        public final long counterAnnualSalary;
        public final int counterYears;
        public final int estimatedAcceptanceChance;

        private Decision(
            Outcome outcome,
            String message,
            long counterAnnualSalary,
            int counterYears,
            int estimatedAcceptanceChance
        ) {
            this.outcome = outcome;
            this.message = message;
            this.counterAnnualSalary = counterAnnualSalary;
            this.counterYears = counterYears;
            this.estimatedAcceptanceChance = estimatedAcceptanceChance;
        }
    }

    private ContractRenewalService() {
    }

    public static Demand calculateDemand(
        Player player,
        Club club,
        int currentYear
    ) {
        double performanceFactor =
            calculatePerformanceFactor(
                player
            );

        int tradeValue =
            TradeValueCalculator.calculateTradeValue(
                player
            );

        double marketFactor =
            Math.max(
                -0.04,
                Math.min(
                    0.08,
                    (tradeValue - 50) *
                        0.0016
                )
            );

        double ageFactor =
            calculateAgeDemandFactor(
                player
            );

        double moraleFactor =
            (player.getMorale() - 50) *
                0.0008;

        double desiredMultiplier =
            1.06 +
                performanceFactor +
                marketFactor +
                ageFactor +
                moraleFactor;

        long desired =
            roundAnnual(
                Math.max(
                    player.getAnnualSalary() *
                        getSalaryFloorMultiplier(
                            player
                        ),
                    Math.max(player.getAnnualSalary(), player.getMarketAnnualSalary()) *
                        desiredMultiplier
                )
            );

        int preferredYears =
            calculatePreferredYears(
                player
            );

        long minimum =
            roundAnnual(
                desired *
                    0.90
            );

        long maximumComfortable =
            roundAnnual(
                desired *
                    1.10
            );

        int remainingYears =
            player.getRemainingContractYears(
                currentYear
            );

        int interest =
            3 +
                (player.getMorale() >= 70
                    ? 1
                    : 0) +
                (remainingYears <= 1
                    ? 1
                    : 0) -
                (player.getAge() <= 21 &&
                    player.getPotential() -
                        player.getOverall() >= 8
                    ? 1
                    : 0);

        interest =
            Math.max(
                1,
                Math.min(
                    5,
                    interest
                )
            );

        return new Demand(
            minimum,
            desired,
            maximumComfortable,
            preferredYears,
            interest,
            getPerformanceSummary(
                player
            )
        );
    }

    public static int estimateAcceptanceChance(
        Player player,
        Club club,
        int currentYear,
        long offeredAnnualSalary,
        int offeredYears
    ) {
        Demand demand =
            calculateDemand(
                player,
                club,
                currentYear
            );

        long projectedPayroll =
            club.getFinance()
                .getAnnualPayroll() -
                player.getAnnualSalary() +
                offeredAnnualSalary;

        if (!club.getFinance().isWithinHardCap(projectedPayroll)) {
            return 0;
        }

        double salaryScore =
            (offeredAnnualSalary -
                (demand.minimumAnnualSalary *
                    0.75)) /
                (double) Math.max(
                    1L,
                    demand.desiredAnnualSalary -
                        (demand.minimumAnnualSalary *
                            0.75)
                );

        int chance =
            (int) Math.round(
                15 +
                    (Math.max(
                        0.0,
                        Math.min(
                            1.0,
                            salaryScore
                        )
                    ) * 65.0)
            );

        chance +=
            Math.max(
                -16,
                12 -
                    (Math.abs(
                        offeredYears -
                            demand.preferredYears
                    ) * 8)
            );

        chance +=
            (demand.interestStars - 3) *
                5;

        /* Veteranos tendem a aceitar redução para manter estabilidade. */
        if (
            offeredAnnualSalary <=
                player.getAnnualSalary()
        ) {
            chance += getVeteranSalaryFlexibility(
                player
            );
        }

        return Math.max(
            1,
            Math.min(
                98,
                chance
            )
        );
    }

    public static Decision evaluateProposal(
        Player player, Club club, int currentYear, long offeredAnnualSalary, int offeredYears
    ) {
        Demand demand = calculateDemand(player, club, currentYear);
        int chance = estimateAcceptanceChance(player, club, currentYear, offeredAnnualSalary, offeredYears);
        long payroll = club.getFinance().getAnnualPayroll() - player.getAnnualSalary() + offeredAnnualSalary;
        if (!club.getFinance().isWithinHardCap(payroll) || offeredAnnualSalary <= 0 || offeredYears < 1 || offeredYears > 5) {
            return new Decision(Outcome.REJECTED, "Proposta inválida ou acima do Hard Cap.", 0, 0, 0);
        }
        PlayerNegotiation.Response response = PlayerNegotiation.respond(
            player.negotiationSession(club, currentYear, "RENEWAL"),
            demand.desiredAnnualSalary, offeredAnnualSalary, offeredYears, demand.preferredYears,
            demand.interestStars, player.getId() + ":" + club.getName() + ":" + currentYear
        );
        Outcome outcome = response.accepted ? Outcome.ACCEPTED
            : response.rejected ? Outcome.REJECTED : Outcome.COUNTER_OFFER;
        return new Decision(outcome, player.getName() + ": " + response.message,
            response.salary, response.years, chance);
    }

    public static boolean isAttackingOrMidfieldPlayer(
        Player player
    ) {
        String position =
            player.getPosition();

        return position != null &&
            position.matches(
                "CM|CDM|CAM|LM|RM|LW|RW|CF|ST"
            );
    }

    public static String getPerformanceSummary(
        Player player
    ) {
        if (
            isAttackingOrMidfieldPlayer(
                player
            )
        ) {
            return player.getSeasonGoals() +
                " gols • " +
                player.getSeasonAssists() +
                " assistências";
        }

        return player.getSeasonCleanSheets() +
            " jogos sem sofrer gols";
    }

    private static double calculatePerformanceFactor(
        Player player
    ) {
        if (
            isAttackingOrMidfieldPlayer(
                player
            )
        ) {
            return Math.min(
                0.24,
                (player.getSeasonGoals() *
                    0.014) +
                    (player.getSeasonAssists() *
                        0.009)
            );
        }

        return Math.min(
            0.20,
            player.getSeasonCleanSheets() *
                0.014
        );
    }

    private static double calculateAgeDemandFactor(
        Player player
    ) {
        if (
            player.getAge() <= 21
        ) {
            return 0.08;
        }

        if (
            player.getAge() <= 25
        ) {
            return 0.04;
        }

        if (
            player.getAge() >= 35
        ) {
            return -0.20;
        }

        if (player.getAge() >= 34) {
            return -0.16;
        }

        if (player.getAge() >= 32) {
            return -0.11;
        }

        if (player.getAge() >= 31) {
            return -0.07;
        }

        return 0.0;
    }

    private static int calculatePreferredYears(
        Player player
    ) {
        if (
            player.getAge() <= 21
        ) {
            return 5;
        }

        if (
            player.getAge() <= 27
        ) {
            return 4;
        }

        if (
            player.getAge() <= 32
        ) {
            return 3;
        }

        return 2;
    }

    private static double getSalaryFloorMultiplier(
        Player player
    ) {
        if (player.getAge() >= 35) return 0.75;
        if (player.getAge() >= 34) return 0.80;
        if (player.getAge() >= 32) return 0.85;
        if (player.getAge() >= 31) return 0.89;
        return 0.92;
    }

    private static int getVeteranSalaryFlexibility(
        Player player
    ) {
        if (player.getAge() >= 35) return 18;
        if (player.getAge() >= 34) return 14;
        if (player.getAge() >= 32) return 9;
        if (player.getAge() >= 31) return 5;
        return 0;
    }

    private static long roundAnnual(
        double value
    ) {
        return Math.max(
            12_000L,
            Math.round(
                value / 1_000.0
            ) * 1_000L
        );
    }
}
