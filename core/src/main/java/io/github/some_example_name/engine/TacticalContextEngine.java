package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;

import java.util.List;

/** Compõe os modificadores-base com formação, elenco, fadiga, rival e estado do jogo. */
public final class TacticalContextEngine {
    private TacticalContextEngine() { }

    public static ContextPair apply(
        Club home,
        List<Player> homeLineup,
        Club away,
        List<Player> awayLineup,
        Match match,
        int minute
    ) {
        TacticalModifiers homeModifiers = base(home);
        TacticalModifiers awayModifiers = base(away);

        FormationShapeEvaluator.Shape homeFormation = FormationShapeEvaluator.evaluate(home);
        FormationShapeEvaluator.Shape awayFormation = FormationShapeEvaluator.evaluate(away);
        TacticalSuitabilityEvaluator.Profile homeFit =
            TacticalSuitabilityEvaluator.apply(home, homeLineup, homeModifiers);
        TacticalSuitabilityEvaluator.Profile awayFit =
            TacticalSuitabilityEvaluator.apply(away, awayLineup, awayModifiers);

        double homeSustainability = TacticalSuitabilityEvaluator.applyMinuteSustainability(
            homeFit, homeModifiers, minute
        );
        double awaySustainability = TacticalSuitabilityEvaluator.applyMinuteSustainability(
            awayFit, awayModifiers, minute
        );
        TacticalMatchupEvaluator.Result matchup = TacticalMatchupEvaluator.evaluateAndApply(
            home, homeModifiers, away, awayModifiers
        );

        if (match != null) {
            applyMatchState(homeModifiers, match.getHomeGoals() - match.getAwayGoals(), minute);
            applyMatchState(awayModifiers, match.getAwayGoals() - match.getHomeGoals(), minute);
        }

        return new ContextPair(
            homeModifiers, awayModifiers, homeFormation, awayFormation,
            homeFit, awayFit, matchup, homeSustainability, awaySustainability
        );
    }

    private static TacticalModifiers base(Club club) {
        return TacticalEngine.calculateModifiers(
            club.getTempo(), club.getMentalityValue(), club.getPassing(), club.getWidth(), club.getPressure()
        );
    }

    private static void applyMatchState(TacticalModifiers modifiers, int goalDifference, int minute) {
        if (minute < 65 || goalDifference == 0) return;
        double urgency = Math.min(.04d, (minute - 64d) / 26d * .04d);
        if (goalDifference < 0) {
            modifiers.attackMultiplier *= 1d + urgency;
            modifiers.boxPresenceMultiplier *= 1d + urgency;
            modifiers.defensiveCoverageMultiplier *= 1d - urgency * .55d;
        } else {
            modifiers.defenseMultiplier *= 1d + urgency * .65d;
            modifiers.turnoverRiskMultiplier *= 1d - urgency * .45d;
        }
    }

    public static final class ContextPair {
        private final TacticalModifiers homeModifiers;
        private final TacticalModifiers awayModifiers;
        private final FormationShapeEvaluator.Shape homeFormation;
        private final FormationShapeEvaluator.Shape awayFormation;
        private final TacticalSuitabilityEvaluator.Profile homeFit;
        private final TacticalSuitabilityEvaluator.Profile awayFit;
        private final TacticalMatchupEvaluator.Result matchup;
        private final double homeSustainability;
        private final double awaySustainability;

        private ContextPair(
            TacticalModifiers homeModifiers,
            TacticalModifiers awayModifiers,
            FormationShapeEvaluator.Shape homeFormation,
            FormationShapeEvaluator.Shape awayFormation,
            TacticalSuitabilityEvaluator.Profile homeFit,
            TacticalSuitabilityEvaluator.Profile awayFit,
            TacticalMatchupEvaluator.Result matchup,
            double homeSustainability,
            double awaySustainability
        ) {
            this.homeModifiers = homeModifiers;
            this.awayModifiers = awayModifiers;
            this.homeFormation = homeFormation;
            this.awayFormation = awayFormation;
            this.homeFit = homeFit;
            this.awayFit = awayFit;
            this.matchup = matchup;
            this.homeSustainability = homeSustainability;
            this.awaySustainability = awaySustainability;
        }

        public TacticalModifiers getHomeModifiers() { return homeModifiers; }
        public TacticalModifiers getAwayModifiers() { return awayModifiers; }
        public FormationShapeEvaluator.Shape getHomeFormation() { return homeFormation; }
        public FormationShapeEvaluator.Shape getAwayFormation() { return awayFormation; }
        public TacticalSuitabilityEvaluator.Profile getHomeFit() { return homeFit; }
        public TacticalSuitabilityEvaluator.Profile getAwayFit() { return awayFit; }
        public TacticalMatchupEvaluator.Result getMatchup() { return matchup; }
        public double getHomeSustainability() { return homeSustainability; }
        public double getAwaySustainability() { return awaySustainability; }
    }
}
