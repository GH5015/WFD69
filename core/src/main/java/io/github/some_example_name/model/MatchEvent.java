package io.github.some_example_name.model;

public class MatchEvent implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public int minute;
    public String description;
    public String type; // "GOL", "CHUTE", "CARTAO", etc.
    public boolean isHomeTeam; // Indica se a ação foi realizada pelo mandante

    public MatchEvent(int minute, String description, String type, boolean isHomeTeam) {
        this.minute = minute;
        this.description = description;
        this.type = type;
        this.isHomeTeam = isHomeTeam;
    }
}
