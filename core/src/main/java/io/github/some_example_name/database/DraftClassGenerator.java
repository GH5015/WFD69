package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.NameGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Complementa uma classe do Draft sem alterar os prospectos já definidos. */
public final class DraftClassGenerator {
    public static final int MINIMUM_PROSPECTS = 40;
    private static final int MAX_FILLER_OVERALL = 69;
    private static final int MAX_FILLER_POTENTIAL = 80;

    private DraftClassGenerator() {
    }

    /**
     * Acrescenta apenas os jogadores necessários para completar as duas
     * rodadas da WFL. Os complementos são apostas de baixo teto, para não
     * substituir o valor dos prospectos históricos da classe.
     */
    public static List<Player> ensureMinimumProspects(List<Player> source) {
        List<Player> prospects = source != null
            ? new ArrayList<>(source)
            : new ArrayList<Player>();

        Set<String> usedNames = new HashSet<>();
        for (Player player : prospects) {
            if (player != null && player.getName() != null) {
                usedNames.add(player.getName().toLowerCase());
            }
        }

        Random random = new Random();
        int fillerNumber = 1;
        while (prospects.size() < MINIMUM_PROSPECTS) {
            String name = nextUniqueName(usedNames, fillerNumber++);
            Player filler = createFiller(name, random);

            // A proteção mantém o limite mesmo se a fórmula de overall mudar.
            if (filler.getOverall() > MAX_FILLER_OVERALL) {
                continue;
            }

            prospects.add(filler);
        }

        return prospects;
    }

    private static String nextUniqueName(Set<String> usedNames, int suffix) {
        String name = NameGenerator.generateName();
        if (usedNames.contains(name.toLowerCase())) {
            name += " " + suffix;
        }
        usedNames.add(name.toLowerCase());
        return name;
    }

    private static Player createFiller(String name, Random random) {
        Position[] positions = Position.values();
        Position primary = positions[random.nextInt(positions.length)];
        int base = 48 + random.nextInt(19); // 48–66 em cada atributo.
        int attack = vary(base, random);
        int passing = vary(base, random);
        int defense = vary(base, random);
        int physical = vary(base, random);
        int dribbling = vary(base, random);
        int goalkeeping = primary == Position.GK
            ? 62 + random.nextInt(6)
            : 18 + random.nextInt(18);

        TechnicalAttributes attributes = new TechnicalAttributes(
            attack,
            passing,
            defense,
            physical,
            dribbling,
            goalkeeping
        );

        int potential = 65 + random.nextInt(MAX_FILLER_POTENTIAL - 64);
        return new Player(
            name,
            NameGenerator.generateNationality(),
            primary,
            secondaryPositionFor(primary, random),
            17 + random.nextInt(5),
            attributes,
            potential,
            6_000d + random.nextInt(5_000)
        );
    }

    private static int vary(int base, Random random) {
        return Math.max(42, Math.min(68, base - 4 + random.nextInt(9)));
    }

    private static Position secondaryPositionFor(Position primary, Random random) {
        if (primary == Position.GK || random.nextBoolean()) {
            return null;
        }

        switch (primary) {
            case CB: return random.nextBoolean() ? Position.SW : Position.CDM;
            case LB: return Position.LWB;
            case RB: return Position.RWB;
            case LWB: return Position.LB;
            case RWB: return Position.RB;
            case CDM: return Position.CM;
            case CM: return random.nextBoolean() ? Position.CDM : Position.CAM;
            case CAM: return Position.CM;
            case LM: return Position.LW;
            case RM: return Position.RW;
            case LW: return Position.CF;
            case RW: return Position.CF;
            case CF: return Position.ST;
            case ST: return Position.CF;
            default: return null;
        }
    }
}
