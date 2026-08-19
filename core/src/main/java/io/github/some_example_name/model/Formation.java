package io.github.some_example_name.model;

import java.util.Arrays;
import java.util.List;

public enum Formation {
    F424("4-2-4", Arrays.asList("GK", "LB", "CB", "CB", "RB", "CM", "CM", "LW", "ST", "ST", "RW")),
    F_433("4-3-3", Arrays.asList("GK", "LB", "CB", "CB", "RB", "CDM", "CM", "CM", "LW", "ST", "RW")),
    F_442("4-4-2", Arrays.asList("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CM", "RM", "ST", "ST")),
    F_4231("4-2-3-1", Arrays.asList("GK", "LB", "CB", "CB", "RB", "CDM", "CDM", "LM", "CAM", "RM", "ST")),
    F_41212("4-1-2-1-2 (Diamond)", Arrays.asList("GK", "LB", "CB", "CB", "RB", "CDM", "LM", "RM", "CAM", "ST", "ST")),
    F_352("3-5-2", Arrays.asList("GK", "CB", "CB", "CB", "LWB", "CDM", "CDM", "RWB", "CAM", "ST", "ST")),
    F_532("5-3-2", Arrays.asList("GK", "LWB", "CB", "CB", "CB", "RWB", "CM", "CM", "CM", "ST", "ST")),
    F_343("3-4-3", Arrays.asList("GK", "CB", "CB", "CB", "LM", "CM", "CM", "RM", "LW", "ST", "RW")),
    F_451("4-5-1", Arrays.asList("GK", "LB", "CB", "CB", "RB", "LM", "CM", "CDM", "CM", "RM", "ST")),
    F_433_FALSE9("4-3-3 (Falso 9)", Arrays.asList("GK", "LB", "CB", "CB", "RB", "CDM", "CM", "CM", "LW", "CF", "RW"));

    private final String name;
    private final List<String> positionSlots;

    Formation(String name, List<String> positionSlots) {
        this.name = name;
        this.positionSlots = positionSlots;
    }

    public String getName() { return name; }
    public List<String> getPositionSlots() { return positionSlots; }

    @Override
    public String toString() { return name; }
}
