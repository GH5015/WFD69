package io.github.some_example_name.model;

/** Pacotes de expansão disponíveis para os estádios da WFL. */
public enum StadiumRenovationPlan {
    STANDS("AMPLIAR ARQUIBANCADAS", 5_000, 120, 6_000_000L,
        "Nova arquibancada e melhoria dos acessos."),
    STRUCTURE("MODERNIZAÇÃO ESTRUTURAL", 12_000, 240, 15_000_000L,
        "Ampliação completa, cobertura parcial e novas áreas internas."),
    NEW_RING("CONSTRUIR NOVO ANEL", 25_000, 420, 32_500_000L,
        "Grande expansão com um novo anel de arquibancadas.");

    public static final int MAX_CAPACITY = 100_000;

    private final String displayName;
    private final int additionalCapacity;
    private final int durationDays;
    private final long cost;
    private final String description;

    StadiumRenovationPlan(String displayName, int additionalCapacity, int durationDays,
                          long cost, String description) {
        this.displayName = displayName;
        this.additionalCapacity = additionalCapacity;
        this.durationDays = durationDays;
        this.cost = cost;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public int getAdditionalCapacity() { return additionalCapacity; }
    public int getDurationDays() { return durationDays; }
    public long getCost() { return cost; }
    public String getDescription() { return description; }
}
