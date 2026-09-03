package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;

/** Atalhos de interface: apenas preenchem os cinco controles táticos existentes. */
public enum TacticalPreset {
    CONTROL_GAME("CONTROLAR JOGO", 38f, 50f, 25f, 50f, 45f),
    PROTECT_LEAD("DEFENDER VANTAGEM", 35f, 30f, 55f, 42f, 38f),
    COUNTER_ATTACK("CONTRA-ATACAR", 65f, 35f, 72f, 62f, 42f),
    PRESS("PRESSIONAR", 75f, 62f, 42f, 58f, 82f),
    SEEK_GOAL("BUSCAR GOL", 78f, 78f, 48f, 70f, 74f),
    ALL_OR_NOTHING("TUDO OU NADA", 92f, 94f, 65f, 82f, 92f);

    private final String label;
    private final float tempo;
    private final float mentality;
    private final float passing;
    private final float width;
    private final float pressure;

    TacticalPreset(
        String label,
        float tempo,
        float mentality,
        float passing,
        float width,
        float pressure
    ) {
        this.label = label;
        this.tempo = tempo;
        this.mentality = mentality;
        this.passing = passing;
        this.width = width;
        this.pressure = pressure;
    }

    public void applyTo(Club club) {
        if (club == null) return;
        club.setTempo(tempo);
        club.setMentalityValue(mentality);
        club.setPassing(passing);
        club.setWidth(width);
        club.setPressure(pressure);
    }

    public boolean matches(Club club) {
        return club != null
            && close(club.getTempo(), tempo)
            && close(club.getMentalityValue(), mentality)
            && close(club.getPassing(), passing)
            && close(club.getWidth(), width)
            && close(club.getPressure(), pressure);
    }

    private static boolean close(float value, float expected) {
        return Math.abs(value - expected) < .5f;
    }

    public String getLabel() { return label; }
    public float getTempo() { return tempo; }
    public float getMentality() { return mentality; }
    public float getPassing() { return passing; }
    public float getWidth() { return width; }
    public float getPressure() { return pressure; }
}
