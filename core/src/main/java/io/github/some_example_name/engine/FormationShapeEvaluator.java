package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;

/** Lê a ocupação real das zonas a partir dos slots da formação selecionada. */
public final class FormationShapeEvaluator {
    private FormationShapeEvaluator() { }

    public static Shape evaluate(Club club) {
        return evaluate(club != null ? club.getFormation() : null);
    }

    public static Shape evaluate(Formation formation) {
        int defense = 0;
        int midfield = 0;
        int corridors = 0;
        int attack = 0;
        int centralMidfield = 0;
        if (formation != null && formation.getPositionSlots() != null) {
            for (String raw : formation.getPositionSlots()) {
                String position = raw == null ? "" : raw.toUpperCase();
                if (position.matches("CB|LB|RB|LWB|RWB|SW")) defense++;
                if (position.matches("CDM|CM|CAM|LM|RM")) midfield++;
                if (position.matches("CDM|CM|CAM")) centralMidfield++;
                if (position.matches("LW|RW|LM|RM|LWB|RWB")) corridors++;
                if (position.matches("ST|CF|LW|RW")) attack++;
            }
        }
        return new Shape(defense, midfield, corridors, attack, centralMidfield);
    }

    public static final class Shape {
        private final int defense;
        private final int midfield;
        private final int corridors;
        private final int attack;
        private final int centralMidfield;

        private Shape(int defense, int midfield, int corridors, int attack, int centralMidfield) {
            this.defense = defense;
            this.midfield = midfield;
            this.corridors = corridors;
            this.attack = attack;
            this.centralMidfield = centralMidfield;
        }

        public int getDefense() { return defense; }
        public int getMidfield() { return midfield; }
        public int getCorridors() { return corridors; }
        public int getAttack() { return attack; }
        public int getCentralMidfield() { return centralMidfield; }
        public String describe() {
            return "Defesa " + defense + " • Meio " + midfield
                + " • Corredores " + corridors + " • Ataque " + attack;
        }
    }
}
