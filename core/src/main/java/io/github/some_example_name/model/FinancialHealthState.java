package io.github.some_example_name.model;

import com.badlogic.gdx.graphics.Color;

public enum FinancialHealthState {
    HEALTHY("Saudável", "🟢", Color.GREEN, "Caixa positivo e folha sob controle."),
    WARNING("Atenção", "🟡", Color.YELLOW, "Caixa baixo ou folha próxima do limite."),
    CRITICAL("Crítico", "🟠", Color.ORANGE, "Caixa muito baixo ou déficit recorrente."),
    CRISIS("Crise", "🔴", Color.RED, "Caixa negativo. Intervenção da diretoria da WFL!");

    private final String label;
    private final String icon;
    private final Color color;
    private final String description;

    FinancialHealthState(String label, String icon, Color color, String description) {
        this.label = label;
        this.icon = icon;
        this.color = color;
        this.description = description;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public Color getColor() { return color; }
    public String getDescription() { return description; }
    public String getFormattedStatus() { return icon + " " + label; }
}
