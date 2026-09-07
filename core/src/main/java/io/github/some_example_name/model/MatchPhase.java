package io.github.some_example_name.model;

import java.io.Serializable;

/** Estado esportivo de uma posse, independente de como ela é apresentada na tela. */
public enum MatchPhase implements Serializable {
    DEFENSIVE_BUILDUP,
    BUILDUP,
    MIDFIELD,
    FINAL_THIRD,
    CHANCE,
    SHOT,
    RESET
}
