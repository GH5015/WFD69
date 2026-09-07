package io.github.some_example_name.model;

import java.io.Serializable;

/** Categorias editoriais que explicam o contexto de cada lance. */
public enum NarrativeCategory implements Serializable {
    CONSTRUCAO("CONSTRUÇÃO", "⚙"),
    PRESSAO("PRESSÃO", "⬆"),
    TRANSICAO("TRANSIÇÃO", "↗"),
    DUELO("DUELO", "⚔"),
    CHANCE("CHANCE", "◎"),
    DEFESA("DEFESA", "✋"),
    ERRO("ERRO", "!"),
    FALTA("FALTA", "■"),
    LESAO("LESÃO", "+"),
    SUBSTITUICAO("SUBSTITUIÇÃO", "↔"),
    GOL("GOL", "⚽");

    private final String label;
    private final String icon;

    NarrativeCategory(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }

    public static NarrativeCategory fromEventType(String type) {
        if (type == null) return CONSTRUCAO;
        switch (type) {
            case "GOL": return GOL;
            case "CHUTE":
            case "ESCANTEIO":
            case "REBOTE_OFENSIVO": return CHANCE;
            case "DEFESA": return DEFESA;
            case "RECUPERACAO_ALTA": return PRESSAO;
            case "TRANSICAO":
            case "ROUBADA":
            case "DESARME":
            case "INTERCEPTACAO": return TRANSICAO;
            case "DUELO": return DUELO;
            case "ERRO":
            case "FADIGA": return ERRO;
            case "FALTA":
            case "TIRO_LIVRE":
            case "CARTAO": return FALTA;
            case "LESIONADO": return LESAO;
            case "SUBSTITUICAO": return SUBSTITUICAO;
            default: return CONSTRUCAO;
        }
    }
}
