package io.github.some_example_name.model;

public enum Position {
    // Goleiro
    GK("Goleiro", true),

    // Defensores
    CB("Zagueiro", false),
    LB("Lateral Esquerdo", false),
    RB("Lateral Direito", false),
    LWB("Ala Esquerdo", false),
    RWB("Ala Direito", false),

    // Meias
    CDM("Volante", false),
    CM("Meia Central", false),
    CAM("Meia Atacante", false),
    LM("Meia Esquerda", false),
    RM("Meia Direita", false),

    // Atacantes
    LW("Ponta Esquerda", false),
    RW("Ponta Direita", false),
    CF("Segundo Atacante", false),
    ST("Centroavante", false);

    private final String label;
    private final boolean goalkeeper;

    Position(String label, boolean goalkeeper) {
        this.label = label;
        this.goalkeeper = goalkeeper;
    }

    public String getLabel() { return label; }
    public boolean isGoalkeeper() { return goalkeeper; }
}
