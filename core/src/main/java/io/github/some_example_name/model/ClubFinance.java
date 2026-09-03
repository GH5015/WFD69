package io.github.some_example_name.model;

public class ClubFinance {
    public static final double CLUB_GATE_REVENUE_SHARE = 0.25d;
    public static final long BASE_SALARY_CAP = 14_500_000L;
    public static final long LUXURY_TAX_OFFSET = 1_500_000L;
    public static final long HARD_CAP_OFFSET = 3_000_000L;
    public static final double LUXURY_TAX_RATE = 2.5d;
    private final Club club;
    private long balance = 48_250_000L;
    private int consecutiveNegativeSeasons;
    private int lastSolvencyReviewYear;

    public int getConsecutiveNegativeSeasons() { return consecutiveNegativeSeasons; }

    /** Uma avaliação por temporada; a sanção reinicia o ciclo de três anos. */
    public boolean closeSeasonSolvency(int year) {
        if (year <= lastSolvencyReviewYear) return false;
        if (lastSolvencyReviewYear != year - 1) consecutiveNegativeSeasons = 0;
        lastSolvencyReviewYear = year;
        consecutiveNegativeSeasons = balance < 0 ? consecutiveNegativeSeasons + 1 : 0;
        if (consecutiveNegativeSeasons < 3) return false;
        consecutiveNegativeSeasons = 0;
        return true;
    }

    // Acumulador de premiações conquistadas no ano esportivo
    private long seasonPrizeEarnings = 0;
    private long currentMonthTicketRevenue;
    private long previousMonthTicketRevenue;
    private long seasonTicketRevenue;

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
    /** Teto-base igual para todas as franquias; qualidade do elenco não concede vantagem financeira. */
    public long getSalaryCap() {
        return BASE_SALARY_CAP;
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
        return currentMonthTicketRevenue;
    }

    public long getPreviousMonthTicketRevenue() { return previousMonthTicketRevenue; }
    public long getSeasonTicketRevenue() { return seasonTicketRevenue; }

    /** Receita líquida do clube: 25% da arrecadação bruta da partida. */
    public void recordGateRevenue(long amount) {
        if (amount <= 0L) return;
        currentMonthTicketRevenue += amount;
        seasonTicketRevenue += amount;
    }

    public void resetSeasonTicketRevenue() {
        currentMonthTicketRevenue = 0L;
        previousMonthTicketRevenue = 0L;
        seasonTicketRevenue = 0L;
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
        return seasonTicketRevenue
            + (getTvRevenue() + getShirtSalesRevenue() + getPrizeMoneyRevenue()) * 12;
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

    /**
     * Estádios maiores custam mais. O multiplicador chega a 3x quando a
     * condição cai de 100% para 0%, representando reparos emergenciais.
     */
    public long getStadiumMaintenanceExpense() {
        long base = club.getStadiumCapacity() * 6L;
        double conditionMultiplier = 1d + (100 - club.getStadiumCondition()) / 50d;
        return Math.round(base * conditionMultiplier);
    }

    /** Folha anual real dos profissionais atualmente vinculados ao clube. */
    public long getAnnualStaffPayroll() {
        long annual = 0L;
        for (StaffRole role : StaffRole.values()) {
            StaffMember member = club.getStaffMember(role);
            if (member != null) annual += member.getAnnualSalary();
        }
        return annual;
    }

    public int getStaffMemberCount() {
        int count = 0;
        for (StaffRole role : StaffRole.values()) if (club.getStaffMember(role) != null) count++;
        return count;
    }

    public long getStaffExpense() { return getAnnualStaffPayroll() / 12L; }

    public long getTotalMonthlyExpenses() {
        return getPlayerSalariesExpense() + getInfrastructureExpense() + getMedicalExpense()
            + getScoutingExpense() + getStaffExpense() + getStadiumMaintenanceExpense()
            + getMonthlyLuxuryTaxExpense();
    }

    public long getMonthlyNetResult() {
        return getTotalMonthlyRevenue() - getTotalMonthlyExpenses();
    }

    // --- CONTROLE DE SALARY CAP ---
    public long getAvailableCapSpace() {
        return getSalaryCap() - getAnnualPayroll();
    }

    public long getLuxuryTaxThreshold() { return getSalaryCap() + LUXURY_TAX_OFFSET; }
    public long getHardCap() { return getSalaryCap() + HARD_CAP_OFFSET; }
    public long getAvailableHardCapSpace() { return getHardCap() - getAnnualPayroll(); }
    public boolean isWithinHardCap(long projectedPayroll) { return projectedPayroll <= getHardCap(); }

    public boolean canAffordSalary(long additionalAnnualSalary) {
        return isWithinHardCap(getAnnualPayroll() + additionalAnnualSalary);
    }

    /** Taxa anual pesada: 250% de tudo que exceder o limite de Luxury Tax. */
    public long getLuxuryTaxAmount(long projectedPayroll) {
        long taxable = Math.max(0L, projectedPayroll - getLuxuryTaxThreshold());
        return Math.round(taxable * LUXURY_TAX_RATE);
    }

    public long getAnnualLuxuryTax() { return getLuxuryTaxAmount(getAnnualPayroll()); }
    public long getMonthlyLuxuryTaxExpense() { return getAnnualLuxuryTax() / 12L; }

    public String getPayrollStatus(long projectedPayroll) {
        if (projectedPayroll > getHardCap()) return "HARD CAP EXCEDIDO";
        if (projectedPayroll > getLuxuryTaxThreshold()) return "LUXURY TAX";
        if (projectedPayroll > getSalaryCap()) return "ACIMA DO SOFT CAP";
        return "DENTRO DO SALARY CAP";
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
    public void applyMonthlyBalance() {
        club.autoMaintainStadiumPitch();
        this.balance += getMonthlyNetResult();
        previousMonthTicketRevenue = currentMonthTicketRevenue;
        currentMonthTicketRevenue = 0L;
    }

    /** Débito único para investimentos do clube, sem permitir caixa negativo. */
    public boolean spend(long amount) {
        if (amount <= 0L || amount > balance) return false;
        balance -= amount;
        return true;
    }

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
