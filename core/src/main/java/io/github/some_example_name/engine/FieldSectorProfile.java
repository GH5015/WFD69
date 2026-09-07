package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.FieldSector;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Player;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Traduz uma formação e seu XI em ocupação da grade lógica 3x3. */
public final class FieldSectorProfile {
    private final EnumMap<FieldSector, Float> occupancy = new EnumMap<>(FieldSector.class);

    private FieldSectorProfile() {
        for (FieldSector sector : FieldSector.values()) occupancy.put(sector, 0f);
    }

    public static FieldSectorProfile from(Formation formation) {
        FieldSectorProfile profile = new FieldSectorProfile();
        if (formation != null) {
            for (String position : formation.getPositionSlots()) profile.addPosition(position);
        }
        return profile;
    }

    public static FieldSectorProfile from(Club club) {
        if (club == null) return new FieldSectorProfile();
        Formation formation = club.getFormation();
        if (formation == null) return fromPlayers(club.getStartingXI());

        FieldSectorProfile profile = new FieldSectorProfile();
        List<String> slots = formation.getPositionSlots();
        Map<Integer, Player> lineup = club.getTacticsMap();
        for (int index = 0; index < slots.size(); index++) {
            Player player = lineup.get(index);
            if (player != null && player.canPlay() && player.getMatchRedCards() == 0) {
                profile.addPosition(slots.get(index));
            }
        }
        return profile;
    }

    private static FieldSectorProfile fromPlayers(List<Player> players) {
        FieldSectorProfile profile = new FieldSectorProfile();
        if (players != null) {
            for (Player player : players) {
                if (player != null && player.canPlay() && player.getMatchRedCards() == 0) {
                    profile.addPosition(player.getPosition());
                }
            }
        }
        return profile;
    }

    public float get(FieldSector.Line line, FieldSector.Lane lane) {
        return occupancy.get(FieldSector.of(line, lane));
    }

    public float attackingSupport(FieldSector.Lane lane) {
        return get(FieldSector.Line.ATTACK, lane)
            + get(FieldSector.Line.MIDFIELD, lane) * .72f
            + get(FieldSector.Line.DEFENSE, lane) * .20f;
    }

    public float defensiveCoverage(FieldSector.Lane lane) {
        return get(FieldSector.Line.DEFENSE, lane)
            + get(FieldSector.Line.MIDFIELD, lane) * .48f;
    }

    /** Probabilidades LEFT/CENTER/RIGHT, já combinando amplitude e ocupação natural. */
    public float[] laneProbabilities(float width) {
        width = Math.max(0f, Math.min(100f, width));
        float center = width <= 50f
            ? .75f - width * .007f
            : .40f - (width - 50f) * .004f;
        center = Math.max(.20f, Math.min(.75f, center));
        float side = (1f - center) * .5f;
        float[] weights = {side, center, side};

        float leftSupport = attackingSupport(FieldSector.Lane.LEFT);
        float centerSupport = attackingSupport(FieldSector.Lane.CENTER);
        float rightSupport = attackingSupport(FieldSector.Lane.RIGHT);
        float average = Math.max(.25f, (leftSupport + centerSupport + rightSupport) / 3f);
        float[] support = {leftSupport, centerSupport, rightSupport};
        float total = 0f;
        for (int index = 0; index < weights.length; index++) {
            float shapeFactor = Math.max(.62f, Math.min(1.32f, .66f + .34f * support[index] / average));
            weights[index] *= shapeFactor;
            total += weights[index];
        }
        for (int index = 0; index < weights.length; index++) weights[index] /= total;
        return weights;
    }

    public FieldSector.Lane chooseLane(float width, Random random) {
        float[] probabilities = laneProbabilities(width);
        float roll = random.nextFloat();
        if (roll < probabilities[0]) return FieldSector.Lane.LEFT;
        if (roll < probabilities[0] + probabilities[1]) return FieldSector.Lane.CENTER;
        return FieldSector.Lane.RIGHT;
    }

    private void add(FieldSector sector, float value) {
        occupancy.put(sector, occupancy.get(sector) + value);
    }

    private void addPosition(String rawPosition) {
        String position = rawPosition == null ? "CM" : rawPosition.toUpperCase();
        switch (position) {
            case "GK": add(FieldSector.DEF_CENTER, .55f); break;
            case "CB": case "SW": add(FieldSector.DEF_CENTER, 1f); break;
            case "LB": add(FieldSector.DEF_LEFT, 1f); add(FieldSector.MID_LEFT, .20f); break;
            case "RB": add(FieldSector.DEF_RIGHT, 1f); add(FieldSector.MID_RIGHT, .20f); break;
            case "LWB": add(FieldSector.DEF_LEFT, .55f); add(FieldSector.MID_LEFT, .75f); break;
            case "RWB": add(FieldSector.DEF_RIGHT, .55f); add(FieldSector.MID_RIGHT, .75f); break;
            case "CDM": add(FieldSector.DEF_CENTER, .35f); add(FieldSector.MID_CENTER, .80f); break;
            case "CM": add(FieldSector.MID_CENTER, 1f); break;
            case "CAM": add(FieldSector.MID_CENTER, .42f); add(FieldSector.ATT_CENTER, .68f); break;
            case "LM": add(FieldSector.MID_LEFT, 1f); break;
            case "RM": add(FieldSector.MID_RIGHT, 1f); break;
            case "LW": add(FieldSector.MID_LEFT, .22f); add(FieldSector.ATT_LEFT, 1f); break;
            case "RW": add(FieldSector.MID_RIGHT, .22f); add(FieldSector.ATT_RIGHT, 1f); break;
            case "CF": add(FieldSector.MID_CENTER, .20f); add(FieldSector.ATT_CENTER, .88f); break;
            case "ST": add(FieldSector.ATT_CENTER, 1f); break;
            default: add(FieldSector.MID_CENTER, 1f); break;
        }
    }
}
