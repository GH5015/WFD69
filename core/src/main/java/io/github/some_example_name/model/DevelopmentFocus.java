package io.github.some_example_name.model;

/** Foco de treino individual escolhido pelo clube. */
public enum DevelopmentFocus {
    BALANCED("Equilibrado"),
    ATTACK("Ataque"),
    PASSING("Passe"),
    DEFENSE("Defesa"),
    PHYSICAL("Físico"),
    DRIBBLING("Drible");

    private final String label;

    DevelopmentFocus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
