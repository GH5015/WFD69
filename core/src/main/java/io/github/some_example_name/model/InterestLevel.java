package io.github.some_example_name.model;

import com.badlogic.gdx.graphics.Color;

public enum InterestLevel {
    REJECT("Rejeitaria", Color.valueOf("E74C3C"), 0, 29),       // 🔴 Vermelho
    LOW("Pouco interesse", Color.valueOf("E67E22"), 30, 49),    // 🟠 Laranja
    CONSIDER("Consideraria", Color.valueOf("F1C40F"), 50, 69),  // 🟡 Amarelo
    INTERESTED("Interessado", Color.valueOf("2ECC71"), 70, 89), // 🟢 Verde
    VERY_INTERESTED("Muito interessado", Color.valueOf("3498DB"), 90, 100); // 🔵 Azul

    private final String label;
    private final Color color;
    private final int minScore;
    private final int maxScore;

    InterestLevel(String label, Color color, int minScore, int maxScore) {
        this.label = label;
        this.color = color;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static InterestLevel fromScore(int score) {
        int clamped = Math.min(100, Math.max(0, score));
        for (InterestLevel level : values()) {
            if (clamped >= level.minScore && clamped <= level.maxScore) {
                return level;
            }
        }
        return REJECT;
    }

    public String getLabel() { return label; }
    public Color getColor() { return color; }
}
