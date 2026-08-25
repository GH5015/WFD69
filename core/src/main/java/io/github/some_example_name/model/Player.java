package io.github.some_example_name.model;

import java.util.UUID;

public class Player {
    private int morale = 75; // Moral inicial padrão (0 a 100)
    private String id;
    private String name;
    private String nationality;
    private Position primaryPosition;
    private Position secondaryPosition;
    private int age;
    private double height = 1.80;
    private TechnicalAttributes technicalAttributes;
    private int overall;
    private int potential;
    private PlayerDevelopment development;
    double salary;
    private double negotiatedMonthlySalary = -1.0;

    // --- VÍNCULO DE CLUB E CONTRATO ---
    private Club currentClub;
    private int contractYears;
    private int contractEndYear;

    private int seasonGoals = 0, seasonAssists = 0, seasonYellowCards = 0, seasonRedCards = 0;
    private int seasonAppearances = 0;
    private int seasonCleanSheets = 0;
    private double seasonRatingTotal = 0.0;
    private int seasonRatingMatches = 0;
    private int nextContractNegotiationYear = 0;
    private int tradeBlockedDays = 0;

    // --- CAMPOS PARA CARTÕES NA PARTIDA EM ANDAMENTO ---
    private int matchYellowCards = 0;
    private int matchRedCards = 0;

    private int fatigue = 100;

    // Sistema de Suspensão e Lesão
    private int suspendedMatches = 0;
    private int injuredMatches = 0;
    private String injuryType = null;
    private boolean injuredInCurrentMatch = false;

    // --- SISTEMA DE MORAL ---
    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = Math.max(0, Math.min(100, morale));
    }

    public void adjustMorale(int delta) {
        setMorale(this.morale + delta);
    }

    /**
     * Calcula o salário mensal desejado (WFL$ 1969).
     *
     * A remuneração combina valor de mercado, overall, idade e potencial.
     * Mantém uma parcela suavizada da curva exponencial anterior para que os
     * atletas de elite continuem valorizados, sem criar saltos irreais acima
     * de 90 de OVR.
     */
    public long getMonthlySalary() {
        if (this.negotiatedMonthlySalary >= 0.0) {
            return Math.max(
                1L,
                Math.round(this.negotiatedMonthlySalary)
            );
        }

        int marketValue =
            TradeValueCalculator.calculateTradeValue(
                this
            );

        int potentialGap =
            Math.max(
                0,
                this.potential -
                    this.overall
            );

        double overallComponent =
            calculateOverallSalaryBase();

        double ageComponent =
            calculateAgeSalaryAdjustment();

        /* Mercado e potencial refinam a proposta, sem superar OVR e idade. */
        double marketComponent =
            marketValue * 25.0;

        double potentialComponent =
            Math.min(
                3_000.0,
                potentialGap * 150.0
            );

        /* Curva anterior preservada apenas como pequena referência histórica. */
        double legacyComponent =
            6_000.0 *
                Math.exp(
                    0.04 *
                        (this.overall - 60)
                );

        double recalibratedSalary =
            overallComponent +
                ageComponent +
                marketComponent +
                potentialComponent +
                (legacyComponent * 0.02);

        /*
         * Contratos existentes continuam relevantes, mas não perpetuam a
         * discrepância antiga de salários entre atletas semelhantes.
         */
        if (
            this.salary > 0
        ) {

            double contractWeight =
                this.overall >= 90
                    ? 0.005
                    : 0.03;

            recalibratedSalary =
                (recalibratedSalary *
                    (1.0 - contractWeight)) +
                    (this.salary *
                        contractWeight);
        }

        return Math.max(
            8_000L,
            Math.round(
                recalibratedSalary / 100.0
            ) * 100L
        );
    }

    private double calculateOverallSalaryBase() {

        if (
            this.overall <= 60
        ) {

            return 15_000.0 +
                (Math.max(
                    40,
                    this.overall
                ) - 40) *
                    700.0;
        }

        if (
            this.overall <= 75
        ) {

            return 29_000.0 +
                ((this.overall - 60) *
                    800.0);
        }

        if (this.overall <= 82) {
            return 41_000.0 +
                ((this.overall - 75) * 1_400.0);
        }

        if (this.overall <= 89) {
            /* Faixa de estrela: cada ponto de OVR passa a ter peso bem maior. */
            return 50_800.0 +
                ((this.overall - 82) * 2_400.0);
        }

        /* Elite continua cara, sem criar um salto desproporcional acima de 90. */
        return 67_600.0 +
            ((this.overall - 89) * 850.0);
    }

    private double calculateAgeSalaryAdjustment() {

        if (
            this.age <= 20
        ) {

            return 8_000.0;
        }

        if (
            this.age <= 23
        ) {

            return 4_500.0;
        }

        if (
            this.age <= 27
        ) {

            return 1_500.0;
        }

        if (
            this.age <= 30
        ) {

            return 0.0;
        }

        if (
            this.age <= 33
        ) {

            return -4_500.0;
        }

        if (
            this.age <= 34
        ) {

            return -8_500.0;
        }

        return -12_000.0;
    }

    /**
     * Retorna o Salário Anual do jogador.
     */
    public long getAnnualSalary() {
        return getMonthlySalary() * 12L;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Player(String name, String nationality, Position primaryPosition, Position secondaryPosition, int age,
                  TechnicalAttributes technicalAttributes, int potential, double salary) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.nationality = nationality;
        this.primaryPosition = primaryPosition;
        this.secondaryPosition = secondaryPosition;
        this.age = age;
        this.technicalAttributes = technicalAttributes != null ? technicalAttributes : new TechnicalAttributes();
        this.potential = potential;
        this.development = new PlayerDevelopment(
            potential,
            selectDevelopmentCurve(name)
        );
        this.salary = salary;
        calculateOverall();
        development.initialize(this.technicalAttributes, this.overall);
    }

    private DevelopmentCurve selectDevelopmentCurve(String playerName) {
        DevelopmentCurve[] curves = DevelopmentCurve.values();
        int index = Math.abs((playerName != null ? playerName : "").hashCode()) % curves.length;
        return curves[index];
    }

    // --- LÓGICA DE GERENCIAMENTO DE CONTRATO E TRANSFERÊNCIA ---

    /**
     * Transfere o jogador para um novo clube mantendo os termos do contrato ativos.
     */
    public void transferTo(Club newClub) {
        if (this.currentClub != null) {
            this.currentClub.getSquad().remove(this);
        }
        this.currentClub = newClub;
        if (newClub != null && !newClub.getSquad().contains(this)) {
            newClub.getSquad().add(this);
        }
    }

    public boolean isFreeAgent(int currentYear) {
        return currentClub == null || contractEndYear <= currentYear;
    }

    public int getRemainingContractYears(int currentYear) {
        return Math.max(0, contractEndYear - currentYear);
    }

    public void calculateOverall() {
        this.overall = calculateOverallForPosition(this.primaryPosition);
    }

    public int calculateOverallForPosition(Position targetPos) {
        if (targetPos == null || technicalAttributes == null) {
            return this.overall;
        }

        int atk = technicalAttributes.getAtaque();
        int pas = technicalAttributes.getPasse();
        int def = technicalAttributes.getDefesa();
        int fis = technicalAttributes.getFisico();
        int dri = technicalAttributes.getDrible();
        int gk  = technicalAttributes.getGoleiro();

        switch (targetPos) {
            case GK:
                return (int) Math.round(gk * 0.76 + fis * 0.12 + pas * 0.12);

            case CB:
                return (int) Math.round(def * 0.45 + fis * 0.30 + pas * 0.15 + atk * 0.05 + dri * 0.05);

            case LB:
            case RB:
                return (int) Math.round(def * 0.30 + pas * 0.25 + fis * 0.25 + atk * 0.10 + dri * 0.10);

            case LWB:
            case RWB:
                return (int) Math.round(pas * 0.25 + atk * 0.20 + def * 0.20 + fis * 0.20 + dri * 0.15);

            case CDM:
                return (int) Math.round(pas * 0.30 + def * 0.30 + fis * 0.25 + dri * 0.10 + atk * 0.05);

            case CM:
                return (int) Math.round(pas * 0.35 + atk * 0.20 + def * 0.15 + fis * 0.15 + dri * 0.15);

            case CAM:
                return (int) Math.round(pas * 0.30 + atk * 0.25 + dri * 0.25 + fis * 0.10 + def * 0.10);

            case LM:
            case RM:
                return (int) Math.round(pas * 0.25 + dri * 0.25 + atk * 0.20 + fis * 0.20 + def * 0.10);

            case LW:
            case RW:
                return (int) Math.round(atk * 0.30 + dri * 0.30 + fis * 0.20 + pas * 0.15 + def * 0.05);

            case CF:
                return (int) Math.round(atk * 0.35 + dri * 0.20 + pas * 0.20 + fis * 0.15 + def * 0.10);

            case ST:
                return (int) Math.round(atk * 0.45 + fis * 0.25 + pas * 0.15 + dri * 0.10 + def * 0.05);

            default:
                return (atk + pas + def + fis + dri) / 5;
        }
    }

    public int getEffectiveOverallForPosition(Position targetPos) {
        if (targetPos == null || this.primaryPosition == null) {
            return this.overall;
        }

        boolean targetIsGK = targetPos.isGoalkeeper();
        boolean playerIsGK = this.primaryPosition.isGoalkeeper();

        int base;

        if (playerIsGK != targetIsGK) {
            base = Math.max(15, (int) (this.overall * 0.25f));
        } else if (this.primaryPosition == targetPos) {
            base = this.overall;
        } else if (this.secondaryPosition == targetPos) {
            base = (int) Math.round(calculateOverallForPosition(targetPos) * 0.95);
        } else {
            base = (int) Math.round(calculateOverallForPosition(targetPos) * 0.85);
        }

        if (fatigue >= 60) {
            return base;
        }

        double penalty = ((60 - fatigue) / 60.0) * 0.20;
        return (int) Math.round(base * (1.0 - penalty));
    }

    public int getPositionWeight() {
        if (primaryPosition.isGoalkeeper()) return 1;
        if (primaryPosition.name().matches("CB|RB|LB|RWB|LWB")) return 2;
        if (primaryPosition.name().matches("CDM|CM|CAM|RM|LM")) return 3;
        return 4;
    }

    public void applyMatchFatigue() {
        int loss = 25 + (int) (Math.random() * 20);
        if (primaryPosition.isGoalkeeper()) loss = 10;
        this.fatigue = Math.max(0, this.fatigue - loss);
    }

    public void recover(int days) {
        recover(days, 1d);
    }

    public void recover(int days, double multiplier) {
        this.fatigue = Math.min(100, this.fatigue + (int) Math.round(days * 8d * multiplier));
    }

    public void addGoal() { this.seasonGoals++; }
    public void addAssist() { this.seasonAssists++; }
    public void addSeasonAppearance() { this.seasonAppearances++; }
    public void addCleanSheet() { this.seasonCleanSheets++; }
    public void addSeasonRating(double rating) {
        this.seasonRatingTotal += Math.max(1.0, Math.min(10.0, rating));
        this.seasonRatingMatches++;
    }

    // --- MÉTODOS DE CARTÕES E SUSPENSÃO ---
    public int getYellowCards() { return matchYellowCards; }
    public int getRedCards() { return matchRedCards; }

    public void addYellowCard() {
        this.matchYellowCards++;
        this.seasonYellowCards++;

        if (this.matchYellowCards == 2) {
            addRedCard();
        }
    }

    public void addRedCard() {
        if (this.matchRedCards == 0) {
            this.matchRedCards = 1;
            this.seasonRedCards++;
        }
        suspend(1);
    }

    public void resetMatchStats() {
        this.matchYellowCards = 0;
        this.matchRedCards = 0;
        this.injuredInCurrentMatch = false;
    }

    // Sistema de Suspensão
    public void suspend(int matches) { this.suspendedMatches = Math.max(this.suspendedMatches, matches); }
    public void decreaseSuspension() { this.suspendedMatches = Math.max(0, this.suspendedMatches - 1); }
    public boolean isSuspended() { return suspendedMatches > 0; }
    public int getSuspendedMatches() { return suspendedMatches; }

    // --- SISTEMA DE LESÃO ---
    public void injure(int matches, String type) {
        this.injuredMatches = Math.max(0, matches);
        this.injuryType = type;
        this.injuredInCurrentMatch = this.injuredMatches > 0;
    }

    public void setInjuryDuration(int matches) {
        injure(matches, "Muscular");
    }

    public int getInjuryDuration() {
        return injuredMatches;
    }

    public void decreaseInjury() { this.injuredMatches = Math.max(0, this.injuredMatches - 1); }
    public boolean isInjured() { return injuredMatches > 0; }
    public boolean wasInjuredInCurrentMatch() { return injuredInCurrentMatch; }
    public int getInjuredMatches() { return injuredMatches; }
    public String getInjuryType() { return injuryType; }
    public boolean canPlay() { return !isSuspended() && !isInjured(); }

    // Getters & Setters
    public Club getCurrentClub() { return currentClub; }
    public void setCurrentClub(Club currentClub) { this.currentClub = currentClub; }
    public int getContractYears() { return contractYears; }
    public void setContractYears(int contractYears) { this.contractYears = contractYears; }
    public int getContractEndYear() { return contractEndYear; }
    public void setContractEndYear(int contractEndYear) { this.contractEndYear = contractEndYear; }
    public int getNextContractNegotiationYear() { return nextContractNegotiationYear; }
    public void setNextContractNegotiationYear(int year) { this.nextContractNegotiationYear = year; }
    public boolean canNegotiateContract(int currentYear) { return currentYear >= nextContractNegotiationYear; }

    public void renewContract(long annualSalary, int years, int currentYear) {
        int safeYears = Math.max(1, Math.min(5, years));
        this.salary = Math.max(0L, annualSalary) / 12.0;
        this.negotiatedMonthlySalary = this.salary;
        this.contractYears = safeYears;
        this.contractEndYear = currentYear + safeYears;
        this.nextContractNegotiationYear = currentYear + Math.max(1, safeYears - 2);
        this.tradeBlockedDays = 30;
    }
    public int getTradeBlockedDays() { return tradeBlockedDays; }
    public void setTradeBlockedDays(int days) { tradeBlockedDays = Math.max(0, days); }
    public void advanceTradeEligibilityDay() { if (tradeBlockedDays > 0) tradeBlockedDays--; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getNationality() { return nationality; }
    public Position getPrimaryPosition() { return primaryPosition; }
    public Position getSecondaryPosition() { return secondaryPosition; }
    public int getOverall() { return overall; }
    public int getEffectiveOverall() { return getEffectiveOverallForPosition(primaryPosition); }
    public int getPotential() {
        return development != null
            ? development.getPerceivedPotential()
            : potential;
    }
    public int getTruePotential() {
        return development != null
            ? development.getTruePotential()
            : potential;
    }
    public PlayerDevelopment getDevelopment() { return development; }
    public DevelopmentFocus getDevelopmentFocus() { return development.getFocus(); }
    public void setDevelopmentFocus(DevelopmentFocus focus) { development.setFocus(focus); }
    public DevelopmentCurve getDevelopmentCurve() { return development.getCurve(); }
    public int getDevelopmentPercent() { return development.getDevelopmentPercent(overall); }
    public java.util.List<Integer> getOverallDevelopmentHistory() { return development.getWeeklyOverallHistory(); }
    public java.util.Map<Integer, Integer> getYearlyDevelopmentHistory() { return development.getYearlyOverallHistory(); }
    public int getLastAttackDevelopment() { return development.getLastAttackChange(); }
    public int getLastPassingDevelopment() { return development.getLastPassingChange(); }
    public int getLastDefenseDevelopment() { return development.getLastDefenseChange(); }
    public int getLastPhysicalDevelopment() { return development.getLastPhysicalChange(); }
    public int getLastDribblingDevelopment() { return development.getLastDribblingChange(); }
    public void applyDevelopmentGrowth(double attack, double passing, double defense, double physical, double dribbling, double goalkeeper) {
        development.applyAttributeGrowth(technicalAttributes, attack, passing, defense, physical, dribbling, goalkeeper);
        calculateOverall();
        development.recordWeeklyOverall(overall);
    }
    public void recordDevelopmentYear(int year) { development.recordYearOverall(year, overall); }
    public double getSalary() { return salary; }
    public void setSalary(double salary) {
        this.salary = salary;
        this.negotiatedMonthlySalary = -1.0;
    }
    public int getFatigue() { return fatigue; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getSeasonGoals() { return seasonGoals; }
    public int getSeasonAssists() { return seasonAssists; }
    public int getSeasonAppearances() { return seasonAppearances; }
    public int getSeasonCleanSheets() { return seasonCleanSheets; }
    public int getSeasonRatingMatches() { return seasonRatingMatches; }
    public double getSeasonAverageRating() {
        return seasonRatingMatches == 0 ? 0.0 : seasonRatingTotal / seasonRatingMatches;
    }
    public int getSeasonYellowCards() { return seasonYellowCards; }
    public int getSeasonRedCards() { return seasonRedCards; }
    public TechnicalAttributes getTechnicalAttributes() { return technicalAttributes; }
    public void setTechnicalAttributes(TechnicalAttributes technicalAttributes) {
        this.technicalAttributes = technicalAttributes;
        calculateOverall();
    }
    public void resetSeasonStats() {
        this.seasonGoals = 0;
        this.seasonAssists = 0;
        this.seasonAppearances = 0;
        this.seasonCleanSheets = 0;
        this.seasonRatingTotal = 0.0;
        this.seasonRatingMatches = 0;
        this.seasonYellowCards = 0;
        this.seasonRedCards = 0;
        this.suspendedMatches = 0;
        this.injuredMatches = 0;
        this.fatigue = 100;
    }
    public int getMatchRedCards() {
        return matchRedCards;
    }
    public int getAttackStat() {
        return technicalAttributes != null
            ? technicalAttributes.getAtaque()
            : 0;
    }

    public int getPassStat() {
        return technicalAttributes != null
            ? technicalAttributes.getPasse()
            : 0;
    }

    public int getDefenseStat() {
        return technicalAttributes != null
            ? technicalAttributes.getDefesa()
            : 0;
    }

    public int getPhysicalStat() {
        return technicalAttributes != null
            ? technicalAttributes.getFisico()
            : 0;
    }

    public int getDribbleStat() {
        return technicalAttributes != null
            ? technicalAttributes.getDrible()
            : 0;
    }

    public String getPosition() {
        return primaryPosition != null ? primaryPosition.name() : "";
    }
    public int getEffectiveOverallForPosition(String targetPos) {
        if (targetPos == null || targetPos.isEmpty()) {
            return this.overall;
        }

        try {
            return getEffectiveOverallForPosition(
                Position.valueOf(targetPos.toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            return this.overall;
        }
    }
}
