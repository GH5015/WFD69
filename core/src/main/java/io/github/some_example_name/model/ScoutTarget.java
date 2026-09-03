package io.github.some_example_name.model;

public class ScoutTarget {
    private final Player player;
    private double scoutingKnowledge; // 0.0 a 100.0
    private int scoutStars = 3;

    public ScoutTarget(Player player) {
        this(player, 3);
    }

    public ScoutTarget(Player player, int scoutStars) {
        this.player = player;
        this.scoutingKnowledge = 0.0; // Inicia zerado
        setScoutStars(scoutStars);
    }

    public Player getPlayer() { return player; }
    public double getKnowledgePercentage() { return scoutingKnowledge; }
    public int getScoutStars() { return scoutStars; }
    public void setScoutStars(int stars) { scoutStars = Math.max(1, Math.min(5, stars)); }

    private double errorMultiplier() {
        return StaffImpact.scoutingErrorMultiplier(scoutStars);
    }

    public void advanceKnowledge(double dailyProgress) {
        this.scoutingKnowledge = Math.min(100.0, this.scoutingKnowledge + dailyProgress);
    }

    public boolean isFullyScouted() {
        return scoutingKnowledge >= 100.0;
    }

    /**
     * Converte um valor numérico (0-99) em nota com letras (A+, A, A-, B+, etc.)
     */
    public String getLetterGrade(int value, boolean addMarginError) {
        int evaluated = value;
        
        // Aplica margem de imprecisão dependendo do nível de conhecimento
        if (addMarginError && scoutingKnowledge < 80.0) {
            int maxOffset = Math.max(1, (int) Math.ceil(((100.0 - scoutingKnowledge) / 10.0) * errorMultiplier()));
            int offset = (int) (Math.random() * (maxOffset * 2 + 1)) - maxOffset;
            evaluated = Math.max(1, Math.min(99, evaluated + offset));
        }

        return PlayerPotentialDisplay.grade(evaluated);
    }

    /**
     * OVR em Camadas: ? -> Grade -> Intervalo -> Valor Exato
     */
    public String getDisplayOverall() {
        int ovr = player.getOverall();
        
        if (scoutingKnowledge < 20.0) {
            return "?";
        } else if (scoutingKnowledge < 60.0) {
            return getLetterGrade(ovr, true);
        } else if (scoutingKnowledge < 100.0) {
            // Exibe intervalo de variação ex: "78-82"
            int margin = Math.max(1, (int) Math.ceil(((100.0 - scoutingKnowledge) / 5.0) * errorMultiplier()));
            int min = Math.max(1, ovr - margin);
            int max = Math.min(99, ovr + margin);
            return min + "-" + max;
        } else {
            return String.valueOf(ovr); // 100% Relatório Exato
        }
    }

    /**
     * Potencial permanece em grades, inclusive após completar o scouting.
     */
    public String getDisplayPotential() {
        int pot = player.getPotential();

        if (scoutingKnowledge < 30.0) { // Exige mais conhecimento que o OVR
            return "?";
        } else {
            return getLetterGrade(pot, scoutingKnowledge < 70.0);
        }
    }

    /**
     * Atributos de Posição/Grupo (Ataque, Passe, Drible, Físico, Defesa)
     */
    public String getAttributeDisplay(int realAttributeValue) {
        if (scoutingKnowledge < 20.0) {
            return "?";
        } else if (scoutingKnowledge < 80.0) {
            return getLetterGrade(realAttributeValue, true);
        } else if (scoutingKnowledge < 100.0) {
            return getLetterGrade(realAttributeValue, false);
        } else {
            return String.valueOf(realAttributeValue);
        }
    }
}
