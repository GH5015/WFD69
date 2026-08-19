package io.github.some_example_name.engine;

import java.util.Random;

public enum WeatherCondition {
    ENSOLARADO("Ensolarado", 1.0, 1.0, 1.0),
    CHUVA_LEVE("Chuva Leve", 0.95, 1.05, 1.10),
    CHUVA_FORTE("Chuva Forte", 0.80, 1.25, 1.35),
    CALOR_EXTREMO("Calor Extremo", 1.0, 1.40, 1.15);

    private final String description;
    private final double passAccuracyMultiplier;
    private final double fatigueMultiplier;
    private final double injuryRiskMultiplier;

    WeatherCondition(String description, double passAccuracyMultiplier, double fatigueMultiplier, double injuryRiskMultiplier) {
        this.description = description;
        this.passAccuracyMultiplier = passAccuracyMultiplier;
        this.fatigueMultiplier = fatigueMultiplier;
        this.injuryRiskMultiplier = injuryRiskMultiplier;
    }

    public static WeatherCondition getRandomCondition() {
        WeatherCondition[] values = values();
        return values[new Random().nextInt(values.length)];
    }

    public String getDescription() { return description; }
    public double getPassAccuracyMultiplier() { return passAccuracyMultiplier; }
    public double getFatigueMultiplier() { return fatigueMultiplier; }
    public double getInjuryRiskMultiplier() { return injuryRiskMultiplier; }
}
