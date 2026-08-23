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
    private TechnicalAttributes technicalAttributes;
    private int overall;
    private int potential;
    double salary;

    // --- VÍNCULO DE CLUB E CONTRATO ---
    private Club currentClub; 
    private int contractYears;
    private int contractEndYear;

    private int seasonGoals = 0, seasonAssists = 0, seasonYellowCards = 0, seasonRedCards = 0;

    // --- CAMPOS PARA CARTÕES NA PARTIDA EM ANDAMENTO ---
    private int matchYellowCards = 0;
    private int matchRedCards = 0;

    private int fatigue = 100;

    // Sistema de Suspensão e Lesão
    private int suspendedMatches = 0;
    private int injuredMatches = 0;
    private String injuryType = null;

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
     * Calcula o Salário Mensal do jogador (WFL$ 1969).
     * Formula: Base = 6.000 * e^(0.11 * (OVR - 60))
     * Bônus = 1% para cada ponto de Potencial acima do OVR
     */
    public long getMonthlySalary() {
        // Se já tiver um salário negociado salvo em 'salary', utiliza-o
        if (this.salary > 0) {
            return Math.round(this.salary);
        }

        // Caso contrário, calcula pela fórmula de regulamento WFL 1969
        double baseSalary = 6000.0 * Math.exp(0.11 * (this.overall - 60));
        int potentialDiff = Math.max(0, this.potential - this.overall);
        double potentialBonus = 1.0 + (potentialDiff * 0.01);

        double finalMonthlySalary = baseSalary * potentialBonus;
        return Math.max(6000L, Math.round(finalMonthlySalary));
    }

    /**
     * Retorna o Salário Anual do jogador.
     */
    public long getAnnualSalary() {
        return getMonthlySalary() * 12L;
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
        this.salary = salary;
        calculateOverall();
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
        this.fatigue = Math.min(100, this.fatigue + (days * 8));
    }

    public void addGoal() { this.seasonGoals++; }
    public void addAssist() { this.seasonAssists++; }

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
    }

    public void setInjuryDuration(int matches) {
        injure(matches, "Muscular");
    }

    public int getInjuryDuration() {
        return injuredMatches;
    }

    public void decreaseInjury() { this.injuredMatches = Math.max(0, this.injuredMatches - 1); }
    public boolean isInjured() { return injuredMatches > 0; }
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
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getNationality() { return nationality; }
    public Position getPrimaryPosition() { return primaryPosition; }
    public Position getSecondaryPosition() { return secondaryPosition; }
    public int getOverall() { return overall; }
    public int getEffectiveOverall() { return getEffectiveOverallForPosition(primaryPosition); }
    public int getPotential() { return potential; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public int getFatigue() { return fatigue; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getSeasonGoals() { return seasonGoals; }
    public int getSeasonAssists() { return seasonAssists; }
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
        this.seasonYellowCards = 0;
        this.seasonRedCards = 0;
        this.suspendedMatches = 0;
        this.injuredMatches = 0;
        this.fatigue = 100;
    }
    public int getMatchRedCards() {
        return matchRedCards;
    }
}
