package io.github.some_example_name.model;

public class ClubFinance {
    private final Club club;
    private long balance = 48_250_000L;

    // Acumulador de premiações conquistadas no ano esportivo
    private long seasonPrizeEarnings = 0;

    public ClubFinance(Club club) {
        this.club = club;
    }

    // --- REGRAS DE SALARY CAP DINÂMICO (WFL$) ---

    /**
     * Retorna a receita média estimada por clube na liga WFL.
     */
    public long getLeagueAverageRevenue() {
        return 100_000_000L; // Média base da WFL ($100M)
    }

    /**
     * Retorna o Multiplicador de Desempenho do clube (baseado na reputação e campanha).
     * Padrão = 1.0 (pode variar entre 0.95 e 1.10).
     */
    public double getPerformanceMultiplier() {
        // Exemplo: pequenas variações com base na reputação do clube
        double baseFactor = 0.95 + (club.getReputation() / 70.0) * 0.10;
        return Math.min(1.10, Math.max(0.95, baseFactor));
    }

    /**
     * CALCULA O SALARY CAP ANUAL PROPORCIONAL À RECEITA DA LIGA.
     * Travado para ficar firme em torno de WFL$ 45.000.000.
     */
    /**
     * CALCULA O SALARY CAP ANUAL (REGULAMENTO WFL 1969)
     *
     * Índice Esportivo = (OVR do Clube x 70%) + (OVR Médio do XI titular x 30%)
     * Salary Cap = 1.450.000 + (Índice Esportivo - 80) x 15.000
     * Arredondado para os WFL$ 10.000 mais próximos.
     */
    public long getSalaryCap() {
        double clubOvr = club.getOverall();
        double best11Ovr = club.getSquad().stream()
            .mapToInt(Player::getOverall)
            .boxed()
            .sorted((a, b) -> Integer.compare(b, a))
            .limit(11)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(clubOvr);

        double sportsIndex = (clubOvr * 0.70) + (best11Ovr * 0.30);

        long rawCap = 1_450_000L *10 + Math.round((sportsIndex - 80.0) * 15_000.0);

        // Arredonda para os WFL$ 10.000 mais próximos
        return Math.round(rawCap / 10_000.0) * 10_000L;
    }

    // --- PREMIAÇÕES ESPORTIVAS (em WFL$) ---
    public static final long PRIZE_MATCH_WIN = 50_000L;
    public static final long PRIZE_PLAYOFFS_QUAL = 500_000L;
    public static final long PRIZE_FINAL_QUAL = 1_000_000L;
    public static final long PRIZE_CHAMPION = 3_000_000L;

    public void addPrizeMoney(long amount) {
        this.seasonPrizeEarnings += amount;
        this.balance += amount;
    }

    public long getSeasonPrizeEarnings() { return seasonPrizeEarnings; }
    public void resetSeasonPrizes() { this.seasonPrizeEarnings = 0; }

    // --- RECEITAS MENSAIS (WFL$) ---
    public long getTicketRevenue() {
        return (long) (club.getStadiumCapacity() * 46.6667);
    }

    /**
     * Retorna a receita mensal de direitos de TV (em WFL$).
     * Pool mensal total de WFL$ 8.333.333 (~WFL$ 100M anuais na liga).
     * 70% dividido igualmente + 30% baseado na reputação do clube.
     */
    public long getTvRevenue() {
        int totalLeagueClubs = 20;
        long totalTvPoolMensal = 500_000_000L / 12; // ~WFL$ 8.333.333/mês

        // Cota Igualitária (70%): ~WFL$ 291.666/mês para cada clube
        long equalShare = (long) ((totalTvPoolMensal * 0.70) / totalLeagueClubs);

        // Cota de Mérito/Reputação (30%): Escala conforme o nível do clube
        double reputationFactor = club.getReputation() / 70.0;
        long meritShare = (long) (((totalTvPoolMensal * 0.30) / totalLeagueClubs) * reputationFactor);

        return equalShare + meritShare;
    }

    public long getShirtSalesRevenue() {
        double squadOverall = club.getOverall();
        return (long) (Math.pow(squadOverall, 2) * 222.22);
    }

    public long getPrizeMoneyRevenue() {
        return seasonPrizeEarnings > 0 ? seasonPrizeEarnings / 12 : 0;
    }

    public long getTotalMonthlyRevenue() {
        return getTicketRevenue() + getTvRevenue() + getShirtSalesRevenue() + getPrizeMoneyRevenue();
    }

    public long getTotalAnnualRevenue() {
        return getTotalMonthlyRevenue() * 12;
    }

    // --- DESPESAS (WFL$) ---
    public long getAnnualPayroll() {
        return club.getSquad().stream()
            .mapToLong(Player::getAnnualSalary)
            .sum();
    }

    public long getPlayerSalariesExpense() {
        return getAnnualPayroll() / 12;
    }

    public long getInfrastructureExpense() {
        return (long) (club.getReputation() * 12_000L);
    }

    public long getMedicalExpense() {
        return 420_000L;
    }

    public long getScoutingExpense() {
        return 250_000L;
    }

    public long getTotalMonthlyExpenses() {
        return getPlayerSalariesExpense() + getInfrastructureExpense() + getMedicalExpense() + getScoutingExpense();
    }

    public long getMonthlyNetResult() {
        return getTotalMonthlyRevenue() - getTotalMonthlyExpenses();
    }

    // --- CONTROLE DE SALARY CAP ---
    public long getAvailableCapSpace() {
        return getSalaryCap() - getAnnualPayroll();
    }

    public boolean canAffordSalary(long additionalAnnualSalary) {
        return (getAnnualPayroll() + additionalAnnualSalary) <= getSalaryCap();
    }

    // --- SAÚDE FINANCEIRA ---
    public FinancialHealthState getHealthState() {
        long currentBalance = getBalance();
        long annualPayroll = getAnnualPayroll();
        long cap = getSalaryCap();
        long monthlyNet = getMonthlyNetResult();

        if (currentBalance < 0) {
            return FinancialHealthState.CRISIS;
        }

        long monthlyExpenses = getTotalMonthlyExpenses();
        if (currentBalance < monthlyExpenses || (monthlyNet < 0 && (currentBalance + (monthlyNet * 3)) < 0)) {
            return FinancialHealthState.CRITICAL;
        }

        double payrollUsage = (double) annualPayroll / cap;
        if (payrollUsage >= 0.90 || currentBalance < (monthlyExpenses * 3) || monthlyNet < 0) {
            return FinancialHealthState.WARNING;
        }

        return FinancialHealthState.HEALTHY;
    }

    // --- SALDO E CAIXA ---
    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
    public void applyMonthlyBalance() { this.balance += getMonthlyNetResult(); }

    public long getClubValuation() {
        return EconomicPower.calculateClubValuation(club);
    }

    public String getFormattedEconomicPower() {
        return EconomicPower.getFormattedStars(club);
    }

    public static String formatWFL(long amount) {
        return String.format("WFL$ %,d", amount).replace(',', '.');
    }

    public static String formatWFLShort(long amount) {
        return String.format("WFL$ %,dM", amount / 1_000_000);
    }
}
