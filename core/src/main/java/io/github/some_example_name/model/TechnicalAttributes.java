package io.github.some_example_name.model;

import java.util.Map;

public class TechnicalAttributes {
    private int ataque;
    private int passe;
    private int defesa;
    private int fisico;
    private int drible;
    private int goleiro;

    public TechnicalAttributes() {
        this(50, 50, 50, 50, 50, 50);
    }

    public TechnicalAttributes(int ataque, int passe, int defesa, int fisico, int drible, int goleiro) {
        this.ataque = ataque;
        this.passe = passe;
        this.defesa = defesa;
        this.fisico = fisico;
        this.drible = drible;
        this.goleiro = goleiro;
    }

    // Construtor auxiliar para aceitar Map<String, Integer> antigo da database/JSON
    public TechnicalAttributes(Map<String, Integer> map) {
        if (map != null) {
            this.ataque = map.getOrDefault("ataque", 50);
            this.passe = map.getOrDefault("passe", 50);
            this.defesa = map.getOrDefault("defesa", 50);
            this.fisico = map.getOrDefault("fisico", 80);
            this.drible = map.getOrDefault("drible", 80);
            this.goleiro = map.getOrDefault("goleiro", map.getOrDefault("defesa", 50));
        } else {
            this.ataque = 50;
            this.passe = 50;
            this.defesa = 50;
            this.fisico = 50;
            this.drible = 50;
            this.goleiro = 50;
        }
    }

    /**
     * Permite buscar o atributo de forma dinâmica via String
     * para retrocompatibilidade com a MatchEngine e buscas dinâmicas.
     */
    public int getAttributeByName(String name) {
        if (name == null) return 50;
        switch (name.toLowerCase()) {
            case "ataque": return ataque;
            case "passe": return passe;
            case "defesa": return defesa;
            case "fisico": return fisico;
            case "drible": return drible;
            case "goleiro": return goleiro;
            default: return 50;
        }
    }

    // Método substituto seguro para getOrDefault da MatchEngine
    public int getOrDefault(String name, int defaultValue) {
        if (name == null) return defaultValue;
        switch (name.toLowerCase()) {
            case "ataque": return ataque;
            case "passe": return passe;
            case "defesa": return defesa;
            case "fisico": return fisico;
            case "drible": return drible;
            case "goleiro": return goleiro;
            default: return defaultValue;
        }
    }

    // Getters e Setters
    public int getAtaque() { return ataque; }
    public void setAtaque(int ataque) { this.ataque = ataque; }

    public int getPasse() { return passe; }
    public void setPasse(int passe) { this.passe = passe; }

    public int getDefesa() { return defesa; }
    public void setDefesa(int defesa) { this.defesa = defesa; }

    public int getFisico() { return fisico; }
    public void setFisico(int fisico) { this.fisico = fisico; }

    public int getDrible() { return drible; }
    public void setDrible(int drible) { this.drible = drible; }

    public int getGoleiro() { return goleiro; }
    public void setGoleiro(int goleiro) { this.goleiro = goleiro; }
}
