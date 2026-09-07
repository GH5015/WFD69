package io.github.some_example_name.model;

public class MatchEvent implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public int minute;
    public String description;
    public String type; // "GOL", "CHUTE", "CARTAO", etc.
    public boolean isHomeTeam; // Indica se a ação foi realizada pelo mandante
    private PossessionSequence possessionSequence;
    private NarrativeCategory narrativeCategory;
    private Player goalScorer;
    private Player goalAssister;
    private int scorerSeasonGoals;
    private int scorerCareerGoals;
    private Player injuredPlayer;
    private String suspectedInjury;
    private String diagnosedInjury;
    private int diagnosedDaysOut;

    public MatchEvent(int minute, String description, String type, boolean isHomeTeam) {
        this.minute = minute;
        this.description = description;
        this.type = type;
        this.isHomeTeam = isHomeTeam;
        this.narrativeCategory = NarrativeCategory.fromEventType(type);
    }

    public MatchEvent withPossessionSequence(PossessionSequence sequence) {
        this.possessionSequence = sequence;
        return this;
    }

    public PossessionSequence getPossessionSequence() {
        return possessionSequence;
    }

    public MatchEvent withNarrativeCategory(NarrativeCategory category) {
        this.narrativeCategory = category;
        return this;
    }

    public NarrativeCategory getNarrativeCategory() {
        // Saves anteriores não possuem o novo campo; a inferência mantém compatibilidade.
        return narrativeCategory != null
            ? narrativeCategory
            : NarrativeCategory.fromEventType(type);
    }

    public MatchEvent withGoalDetails(
        Player scorer,
        Player assister,
        int seasonGoals,
        int careerGoals
    ) {
        this.goalScorer = scorer;
        this.goalAssister = assister;
        this.scorerSeasonGoals = Math.max(0, seasonGoals);
        this.scorerCareerGoals = Math.max(this.scorerSeasonGoals, careerGoals);
        return this;
    }

    public Player getGoalScorer() { return goalScorer; }
    public Player getGoalAssister() { return goalAssister; }
    public int getScorerSeasonGoals() { return scorerSeasonGoals; }
    public int getScorerCareerGoals() { return scorerCareerGoals; }

    public boolean hasCareerGoalMilestone() {
        return scorerCareerGoals == 50
            || (scorerCareerGoals >= 100 && scorerCareerGoals % 100 == 0);
    }

    /**
     * Mantém o laudo médico junto do evento sem expô-lo na narração ao vivo.
     * Assim, saves antigos continuam válidos e o diagnóstico pode ser revelado
     * somente no alerta pós-partida.
     */
    public MatchEvent withInjuryDetails(
        Player player,
        String suspectedCondition,
        String diagnosis,
        int daysOut
    ) {
        this.injuredPlayer = player;
        this.suspectedInjury = suspectedCondition;
        this.diagnosedInjury = diagnosis;
        this.diagnosedDaysOut = Math.max(0, daysOut);
        return this;
    }

    public Player getInjuredPlayer() { return injuredPlayer; }

    public String getSuspectedInjury() {
        return suspectedInjury != null && !suspectedInjury.trim().isEmpty()
            ? suspectedInjury
            : "LESÃO A SER AVALIADA";
    }

    public String getDiagnosedInjury() { return diagnosedInjury; }
    public int getDiagnosedDaysOut() { return diagnosedDaysOut; }

    public boolean hasInjuryDiagnosis() {
        return diagnosedInjury != null && !diagnosedInjury.trim().isEmpty()
            && diagnosedDaysOut > 0;
    }
}
