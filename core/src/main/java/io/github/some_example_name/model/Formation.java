package io.github.some_example_name.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Formation {
    // Três defensores
    F_3142("3-1-4-2", slots("GK", "CB", "CB", "CB", "CDM", "LM", "CM", "CM", "RM", "ST", "ST")),
    F_3412("3-4-1-2", slots("GK", "CB", "CB", "CB", "LM", "CM", "CM", "RM", "CAM", "ST", "ST")),
    F_3421("3-4-2-1", slots("GK", "CB", "CB", "CB", "LM", "CM", "CM", "RM", "CAM", "CAM", "ST")),
    F_343("3-4-3", slots("GK", "CB", "CB", "CB", "LM", "CM", "CM", "RM", "LW", "ST", "RW")),
    F_352("3-5-2", slots("GK", "CB", "CB", "CB", "LWB", "CDM", "CDM", "RWB", "CAM", "ST", "ST")),

    // Quatro defensores
    F_41212("4-1-2-1-2", slots("GK", "LB", "CB", "CB", "RB", "CDM", "LM", "RM", "CAM", "ST", "ST")),
    F_41212_2("4-1-2-1-2 (2)", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CM", "CM", "CAM", "ST", "ST")),
    F_4132("4-1-3-2", slots("GK", "LB", "CB", "CB", "RB", "CDM", "LM", "CM", "RM", "ST", "ST")),
    F_4141("4-1-4-1", slots("GK", "LB", "CB", "CB", "RB", "CDM", "LM", "CM", "CM", "RM", "ST")),
    F_4213("4-2-1-3", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CDM", "CAM", "LW", "ST", "RW")),
    F_4222("4-2-2-2", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CDM", "CAM", "CAM", "ST", "ST")),
    F_4231("4-2-3-1", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CDM", "LM", "CAM", "RM", "ST")),
    F_4231_2("4-2-3-1 (2)", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "LW", "CAM", "RW", "ST")),
    F424("4-2-4", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "LW", "ST", "ST", "RW")),
    F_4312("4-3-1-2", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "CM", "CAM", "ST", "ST")),
    F_4321("4-3-2-1", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "CM", "CF", "CF", "ST")),
    F_433("4-3-3", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "CM", "LW", "ST", "RW")),
    F_433_2("4-3-3 (2)", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CM", "CM", "LW", "ST", "RW")),
    F_433_3("4-3-3 (3)", slots("GK", "LB", "CB", "CB", "RB", "CDM", "CDM", "CM", "LW", "ST", "RW")),
    // Identificador histórico preservado para saves que usavam o antigo Falso 9.
    F_433_FALSE9("4-3-3 (4)", slots("GK", "LB", "CB", "CB", "RB", "CM", "CM", "CAM", "LW", "ST", "RW")),
    F_4411_2("4-4-1-1 (2)", slots("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CM", "RM", "CF", "ST")),
    F_442("4-4-2", slots("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CM", "RM", "ST", "ST")),
    F_442_2("4-4-2 (2)", slots("GK", "LB", "CB", "CB", "RB", "LM", "CDM", "CDM", "RM", "ST", "ST")),
    F_451("4-5-1", slots("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CDM", "CM", "RM", "ST")),
    F_451_2("4-5-1 (2)", slots("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CAM", "CM", "RM", "ST")),

    // Cinco defensores
    F_5212("5-2-1-2", slots("GK", "LWB", "CB", "CB", "CB", "RWB", "CM", "CM", "CAM", "ST", "ST")),
    F_523("5-2-3", slots("GK", "LWB", "CB", "CB", "CB", "RWB", "CM", "CM", "LW", "ST", "RW")),
    F_532("5-3-2", slots("GK", "LWB", "CB", "CB", "CB", "RWB", "CM", "CM", "CM", "ST", "ST")),
    F_541("5-4-1", slots("GK", "LB", "CB", "CB", "CB", "RB", "LM", "CM", "CM", "RM", "ST"));

    private final String name;
    private final List<String> positionSlots;

    Formation(String name, List<String> positionSlots) {
        this.name = name;
        this.positionSlots = positionSlots;
    }

    private static List<String> slots(String... positions) {
        return Collections.unmodifiableList(Arrays.asList(positions));
    }

    public String getName() { return name; }
    public List<String> getPositionSlots() { return positionSlots; }

    public static Formation fromName(String value) {
        if (value == null) return null;
        String normalized = normalize(value);
        for (Formation formation : values()) {
            if (normalize(formation.name).equals(normalized)
                || normalize(formation.name()).equals(normalized)) return formation;
        }
        if (normalized.equals("433falso9") || normalized.equals("433false9")) return F_433_FALSE9;
        if (normalized.equals("41212diamond")) return F_41212;
        return null;
    }

    private static String normalize(String value) {
        return value.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    @Override
    public String toString() { return name; }
}
