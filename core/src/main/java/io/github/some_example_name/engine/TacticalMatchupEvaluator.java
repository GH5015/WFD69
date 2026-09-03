package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compara as duas propostas táticas. Os efeitos são contextuais e limitados:
 * a qualidade dos atletas continua sendo o fator dominante.
 */
public final class TacticalMatchupEvaluator {
    private TacticalMatchupEvaluator() { }

    public static Result evaluateAndApply(
        Club home,
        TacticalModifiers homeMods,
        Club away,
        TacticalModifiers awayMods
    ) {
        List<String> homeInsights = new ArrayList<>();
        List<String> awayInsights = new ArrayList<>();
        applyOneSide(home, homeMods, away, awayMods, homeInsights, awayInsights);
        applyOneSide(away, awayMods, home, homeMods, awayInsights, homeInsights);
        applyFormationMatchup(home, homeMods, away, awayMods, homeInsights, awayInsights);
        return new Result(homeInsights, awayInsights);
    }

    public static Result analyze(Club home, Club away) {
        TacticalModifiers homeMods = modifiers(home);
        TacticalModifiers awayMods = modifiers(away);
        return evaluateAndApply(home, homeMods, away, awayMods);
    }

    private static TacticalModifiers modifiers(Club club) {
        return TacticalEngine.calculateModifiers(
            club.getTempo(), club.getMentalityValue(), club.getPassing(), club.getWidth(), club.getPressure()
        );
    }

    private static void applyOneSide(
        Club team,
        TacticalModifiers own,
        Club opponent,
        TacticalModifiers rival,
        List<String> ownInsights,
        List<String> rivalInsights
    ) {
        boolean highPress = team.getPressure() >= 70f;
        boolean opponentShort = opponent.getPassing() <= 40f;
        boolean opponentLong = opponent.getPassing() >= 60f;
        if (highPress && opponentShort) {
            own.regainChance *= 1.06d;
            own.highRegainChance *= 1.08d;
            rival.turnoverRiskMultiplier *= 1.04d;
            add(ownInsights, "✓ pressão favorecida contra a saída curta");
            add(rivalInsights, "⚠ saída curta sob pressão rival");
        }
        if (highPress && opponentLong) {
            own.pressBreakRisk *= 1.08d;
            rival.transitionSpeedMultiplier *= 1.06d;
            add(ownInsights, "⚠ vulnerável a bolas longas às costas");
            add(rivalInsights, "✓ passe longo pode escapar da pressão");
        }

        boolean lowBlock = team.getPressure() <= 35f && team.getMentalityValue() <= 40f;
        if (lowBlock && opponent.getWidth() <= 40f) {
            own.defensiveCoverageMultiplier *= 1.06d;
            rival.centralCreationMultiplier *= .95d;
            add(ownInsights, "✓ bloco congestionado contra ataque central");
            add(rivalInsights, "⚠ pouco espaço pelo centro");
        }
        if (lowBlock && opponent.getWidth() >= 70f) {
            rival.flankThreatMultiplier *= 1.06d;
            rival.crossingMultiplier *= 1.05d;
            add(ownInsights, "⚠ bloco baixo ameaçado pelos corredores");
            add(rivalInsights, "✓ amplitude explora o bloco baixo");
        }
        if (team.getPassing() <= 40f && opponent.getPressure() <= 35f) {
            own.possessionMultiplier *= 1.05d;
            own.passRetentionMultiplier *= 1.04d;
            add(ownInsights, "✓ espaço para controlar a posse");
        }
        if (team.getWidth() >= 70f && opponent.getWidth() <= 40f) {
            own.flankThreatMultiplier *= 1.07d;
            rival.defensiveCoverageMultiplier *= .97d;
            add(ownInsights, "✓ superioridade pelos lados");
            add(rivalInsights, "⚠ inferioridade na cobertura lateral");
        }
        if (opponent.getMentalityValue() >= 75f && team.getTempo() >= 60f) {
            own.counterAttackMultiplier *= 1.08d;
            own.transitionSpeedMultiplier *= 1.05d;
            add(ownInsights, "✓ transição perigosa contra equipe ofensiva");
        }
    }

    private static void applyFormationMatchup(
        Club home,
        TacticalModifiers homeMods,
        Club away,
        TacticalModifiers awayMods,
        List<String> homeInsights,
        List<String> awayInsights
    ) {
        FormationShapeEvaluator.Shape h = FormationShapeEvaluator.evaluate(home);
        FormationShapeEvaluator.Shape a = FormationShapeEvaluator.evaluate(away);
        int centralDifference = h.getCentralMidfield() - a.getCentralMidfield();
        if (centralDifference != 0) {
            double effect = Math.min(.08d, Math.abs(centralDifference) * .04d);
            if (centralDifference > 0) {
                homeMods.possessionMultiplier *= 1d + effect;
                awayMods.possessionMultiplier *= 1d - effect * .55d;
                add(homeInsights, "✓ superioridade central " + h.getCentralMidfield() + " x " + a.getCentralMidfield());
                add(awayInsights, "⚠ inferioridade central " + a.getCentralMidfield() + " x " + h.getCentralMidfield());
            } else {
                awayMods.possessionMultiplier *= 1d + effect;
                homeMods.possessionMultiplier *= 1d - effect * .55d;
                add(awayInsights, "✓ superioridade central " + a.getCentralMidfield() + " x " + h.getCentralMidfield());
                add(homeInsights, "⚠ inferioridade central " + h.getCentralMidfield() + " x " + a.getCentralMidfield());
            }
        }

        int homeThreat = h.getAttack() - a.getDefense();
        int awayThreat = a.getAttack() - h.getDefense();
        if (homeThreat >= 0) {
            homeMods.boxPresenceMultiplier *= 1d + Math.min(.06d, .03d * (homeThreat + 1));
            add(homeInsights, "✓ presença contra a última linha rival");
        }
        if (awayThreat >= 0) {
            awayMods.boxPresenceMultiplier *= 1d + Math.min(.06d, .03d * (awayThreat + 1));
            add(awayInsights, "✓ presença contra a última linha rival");
        }

        int corridorDifference = h.getCorridors() - a.getCorridors();
        if (corridorDifference > 0 && home.getWidth() >= 60f) {
            homeMods.flankThreatMultiplier *= 1d + Math.min(.06d, corridorDifference * .03d);
            add(homeInsights, "✓ mais jogadores ocupando os corredores");
        } else if (corridorDifference < 0 && away.getWidth() >= 60f) {
            awayMods.flankThreatMultiplier *= 1d + Math.min(.06d, -corridorDifference * .03d);
            add(awayInsights, "✓ mais jogadores ocupando os corredores");
        }
    }

    private static void add(List<String> insights, String text) {
        if (!insights.contains(text) && insights.size() < 4) insights.add(text);
    }

    public static final class Result {
        private final List<String> homeInsights;
        private final List<String> awayInsights;

        private Result(List<String> homeInsights, List<String> awayInsights) {
            this.homeInsights = Collections.unmodifiableList(new ArrayList<>(homeInsights));
            this.awayInsights = Collections.unmodifiableList(new ArrayList<>(awayInsights));
        }

        public List<String> getHomeInsights() { return homeInsights; }
        public List<String> getAwayInsights() { return awayInsights; }
    }
}
