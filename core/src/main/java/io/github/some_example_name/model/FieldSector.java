package io.github.some_example_name.model;

import java.io.Serializable;

/** Grade lógica 3x3 usada pelo motor sem exigir coordenadas físicas exatas. */
public enum FieldSector implements Serializable {
    DEF_LEFT(Line.DEFENSE, Lane.LEFT),
    DEF_CENTER(Line.DEFENSE, Lane.CENTER),
    DEF_RIGHT(Line.DEFENSE, Lane.RIGHT),
    MID_LEFT(Line.MIDFIELD, Lane.LEFT),
    MID_CENTER(Line.MIDFIELD, Lane.CENTER),
    MID_RIGHT(Line.MIDFIELD, Lane.RIGHT),
    ATT_LEFT(Line.ATTACK, Lane.LEFT),
    ATT_CENTER(Line.ATTACK, Lane.CENTER),
    ATT_RIGHT(Line.ATTACK, Lane.RIGHT);

    public enum Line { DEFENSE, MIDFIELD, ATTACK }
    public enum Lane { LEFT, CENTER, RIGHT }

    private final Line line;
    private final Lane lane;

    FieldSector(Line line, Lane lane) {
        this.line = line;
        this.lane = lane;
    }

    public Line getLine() { return line; }
    public Lane getLane() { return lane; }

    public static FieldSector of(Line line, Lane lane) {
        for (FieldSector sector : values()) {
            if (sector.line == line && sector.lane == lane) return sector;
        }
        return MID_CENTER;
    }
}
