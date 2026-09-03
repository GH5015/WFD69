package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.NameGenerator;

import java.util.ArrayList;
import java.util.Collections;
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
        return ensureMinimumProspects(source, new Random());
    }

    /** Complemento determinístico usado pelas classes vinculadas a um ano. */
    public static List<Player> ensureMinimumProspects(List<Player> source, int year) {
        return ensureMinimumProspects(source, new Random(seedForYear(year) ^ 0x4D494E494D554DL));
    }

    private static List<Player> ensureMinimumProspects(List<Player> source, Random random) {
        return ensureMinimumProspects(source, random, MINIMUM_PROSPECTS);
    }

    public static List<Player> ensureMinimumProspects(List<Player> source, int year, int minimum) {
        return ensureMinimumProspects(source, new Random(seedForYear(year) ^ 0x4D494E494D554DL), minimum);
    }

    private static List<Player> ensureMinimumProspects(List<Player> source, Random random, int minimum) {
        List<Player> prospects = source != null
            ? new ArrayList<>(source)
            : new ArrayList<Player>();

        Set<String> usedNames = new HashSet<>();
        for (Player player : prospects) {
            if (player != null && player.getName() != null) {
                usedNames.add(player.getName().toLowerCase());
            }
        }

        int fillerNumber = 1;
        while (prospects.size() < minimum) {
            String name = nextUniqueName(usedNames, fillerNumber++, random);
            Player filler = createFiller(name, random);

            // A proteção mantém o limite mesmo se a fórmula de overall mudar.
            if (filler.getOverall() > MAX_FILLER_OVERALL) {
                continue;
            }

            prospects.add(filler);
        }

        return prospects;
    }

    /** Cria uma classe futura completa quando não existe uma classe histórica. */
    public static List<Player> generateProceduralClass(int year) {
        Random random = new Random(seedForYear(year));
        List<Position> positions = proceduralPositions();
        Collections.shuffle(positions, random);
        List<Player> prospects = new ArrayList<>(MINIMUM_PROSPECTS);
        Set<String> usedNames = new HashSet<>();

        for (int index = 0; index < MINIMUM_PROSPECTS; index++) {
            String name = nextUniqueName(usedNames, index + 1, random);
            prospects.add(createProceduralProspect(
                name,
                NameGenerator.generateNationality(random),
                positions.get(index),
                index,
                random
            ));
        }

        return prospects;
    }

    private static long seedForYear(int year) {
        return 0x57464C4452414654L ^ ((long) year * 0x9E3779B97F4A7C15L);
    }

    private static List<Position> proceduralPositions() {
        List<Position> positions = new ArrayList<>(MINIMUM_PROSPECTS);
        add(positions, Position.GK, 3);
        add(positions, Position.CB, 6);
        add(positions, Position.LB, 2);
        add(positions, Position.RB, 2);
        add(positions, Position.LWB, 1);
        add(positions, Position.RWB, 1);
        add(positions, Position.CDM, 3);
        add(positions, Position.CM, 4);
        add(positions, Position.CAM, 3);
        add(positions, Position.LM, 2);
        add(positions, Position.RM, 2);
        add(positions, Position.LW, 2);
        add(positions, Position.RW, 2);
        add(positions, Position.CF, 2);
        add(positions, Position.ST, 5);
        return positions;
    }

    private static void add(List<Position> positions, Position position, int amount) {
        for (int index = 0; index < amount; index++) positions.add(position);
    }

    private static Player createProceduralProspect(
        String name,
        String nationality,
        Position primary,
        int rank,
        Random random
    ) {
        int base;
        int potentialFloor;
        int potentialCeiling;
        if (rank < 5) {
            base = 77 + random.nextInt(6);
            potentialFloor = 90;
            potentialCeiling = 96;
        } else if (rank < 20) {
            base = 70 + random.nextInt(8);
            potentialFloor = 84;
            potentialCeiling = 93;
        } else {
            base = 62 + random.nextInt(10);
            potentialFloor = 75;
            potentialCeiling = 88;
        }

        TechnicalAttributes attributes = attributesFor(primary, base, random);
        return new Player(
            name,
            nationality,
            primary,
            secondaryPositionFor(primary, random),
            17 + random.nextInt(5),
            attributes,
            potentialFloor + random.nextInt(potentialCeiling - potentialFloor + 1),
            9_000d + Math.max(0, 40 - rank) * 450d
        );
    }

    private static TechnicalAttributes attributesFor(Position position, int base, Random random) {
        int atk = varyAround(base, random);
        int pas = varyAround(base, random);
        int def = varyAround(base, random);
        int fis = varyAround(base, random);
        int dri = varyAround(base, random);
        int gk = 18 + random.nextInt(18);

        switch (position) {
            case GK:
                atk = 15 + random.nextInt(16); dri = 22 + random.nextInt(18);
                def = Math.max(45, base - 12); pas = base - 5; fis = base; gk = base + 4;
                break;
            case CB:
                atk = base - 18; pas = base - 3; def = base + 5; fis = base + 3; dri = base - 10;
                break;
            case LB: case RB:
                atk = base - 7; pas = base + 1; def = base + 3; fis = base + 3; dri = base;
                break;
            case LWB: case RWB:
                atk = base; pas = base + 2; def = base; fis = base + 3; dri = base + 2;
                break;
            case CDM:
                atk = base - 8; pas = base + 3; def = base + 4; fis = base + 3; dri = base - 1;
                break;
            case CM:
                atk = base; pas = base + 5; def = base - 1; fis = base; dri = base + 2;
                break;
            case CAM:
                atk = base + 3; pas = base + 4; def = base - 13; fis = base - 2; dri = base + 5;
                break;
            case LM: case RM:
                atk = base + 1; pas = base + 3; def = base - 9; fis = base + 2; dri = base + 4;
                break;
            case LW: case RW:
                atk = base + 4; pas = base; def = base - 17; fis = base + 2; dri = base + 5;
                break;
            case CF:
                atk = base + 5; pas = base + 2; def = base - 18; fis = base + 1; dri = base + 4;
                break;
            case ST:
                atk = base + 6; pas = base - 4; def = base - 22; fis = base + 4; dri = base;
                break;
            default:
                break;
        }

        return new TechnicalAttributes(
            clamp(atk), clamp(pas), clamp(def), clamp(fis), clamp(dri), clamp(gk)
        );
    }

    private static int varyAround(int base, Random random) {
        return base - 3 + random.nextInt(7);
    }

    private static int clamp(int value) {
        return Math.max(15, Math.min(95, value));
    }

    private static String nextUniqueName(Set<String> usedNames, int suffix, Random random) {
        String name = NameGenerator.generateName(random);
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
            NameGenerator.generateNationality(random),
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
