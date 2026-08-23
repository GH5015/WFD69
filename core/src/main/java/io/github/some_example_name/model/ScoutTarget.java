package io.github.some_example_name.model;

public class ScoutTarget {
    private final Player player;
    private double scoutingKnowledge; // 0.0 a 100.0

    public ScoutTarget(Player player) {
        this.player = player;
        this.scoutingKnowledge = 0.0; // Inicia zerado
    }

    public Player getPlayer() { return player; }
    public double getKnowledgePercentage() { return scoutingKnowledge; }

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
            int maxOffset = (int) ((100.0 - scoutingKnowledge) / 10.0);
            int offset = (int) (Math.random() * (maxOffset * 2 + 1)) - maxOffset;
            evaluated = Math.max(1, Math.min(99, evaluated + offset));
        }

        if (evaluated >= 90) return "A+";
        if (evaluated >= 85) return "A";
        if (evaluated >= 80) return "A-";
        if (evaluated >= 77) return "B+";
        if (evaluated >= 73) return "B";
        if (evaluated >= 70) return "B-";
        if (evaluated >= 67) return "C+";
        if (evaluated >= 63) return "C";
        if (evaluated >= 60) return "C-";
        if (evaluated >= 57) return "D+";
        if (evaluated >= 53) return "D";
        if (evaluated >= 50) return "D-";
        return "F";
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
            int margin = (int) ((100.0 - scoutingKnowledge) / 5.0) + 1;
            int min = Math.max(1, ovr - margin);
            int max = Math.min(99, ovr + margin);
            return min + "-" + max;
        } else {
            return String.valueOf(ovr); // 100% Relatório Exato
        }
    }

    /**
     * Potencial (Mais difícil de revelar e com maior imprecisão)
     */
    public String getDisplayPotential() {
        int pot = player.getPotential();

        if (scoutingKnowledge < 30.0) { // Exige mais conhecimento que o OVR
            return "?";
        } else if (scoutingKnowledge < 70.0) {
            return getLetterGrade(pot, true);
        } else if (scoutingKnowledge < 100.0) {
            int margin = (int) ((100.0 - scoutingKnowledge) / 4.0) + 1;
            int min = Math.max(1, pot - margin);
            int max = Math.min(99, pot + margin);
            return min + "-" + max;
        } else {
            return String.valueOf(pot); // 100% Relatório Exato
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
