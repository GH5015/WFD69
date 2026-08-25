package io.github.some_example_name.model;

/** Perfil oculto que altera o momento em que o atleta tende a evoluir. */
public enum DevelopmentCurve {
    EARLY("Precoce"),
    NORMAL("Normal"),
    LATE("Tardio"),
    VOLATILE("Instável");

    private final String label;

    DevelopmentCurve(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
