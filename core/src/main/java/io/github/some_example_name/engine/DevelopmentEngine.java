package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DevelopmentCurve;
import io.github.some_example_name.model.DevelopmentFocus;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StaffRole;

import java.util.Random;

/** Aplica a evolução semanal dos atletas a partir de idade, uso e desempenho. */
public class DevelopmentEngine {
    private final Random random = new Random();

    public void updateWeekly(League league) {
        if (league == null) return;

        for (Club club : league.getClubs()) {
            for (Player player : club.getSquad()) {
                updateWeekly(player, league.getCurrentSeason(), club.getStaffLevel(StaffRole.COACH), club.getStaffLevel(StaffRole.DEVELOPMENT_DIRECTOR));
            }
        }
    }

    public void updateWeekly(Player player, int season) {
        updateWeekly(player, season, 3, 3);
    }

    private void updateWeekly(Player player, int season, int coachStars, int developmentDirectorStars) {
        if (player == null) return;

        int gap = Math.max(0, player.getTruePotential() - player.getOverall());
        boolean declining = player.getAge() >= 32 && random.nextDouble() < (0.20d + ((player.getAge() - 32) * 0.12d));
        double growth;

        if (declining) {
            growth = -0.035d * (1d + ((player.getAge() - 32) * 0.25d));
            if (player.getFatigue() < 55) growth *= 1.35d;
        } else {
            growth = 0.055d * (gap / 10d) * ageFactor(player.getAge());
            growth *= minutesFactor(player);
            growth *= performanceFactor(player);
            growth *= fatigueFactor(player);
            growth *= curveFactor(player.getDevelopmentCurve(), player.getAge());
            growth *= 0.90d + ((coachStars + developmentDirectorStars) * 0.025d);
            growth *= 0.88d + (random.nextDouble() * 0.24d);
            if (player.isInjured()) growth *= 0.35d;
            if (gap == 0 || player.getAge() > 33) growth = 0d;
        }

        double[] weights = focusWeights(player.getDevelopmentFocus());
        player.applyDevelopmentGrowth(
            growth * weights[0],
            growth * weights[1],
            growth * weights[2],
            growth * weights[3],
            growth * weights[4],
            growth * (player.getPrimaryPosition().isGoalkeeper() ? 1.8d : 0.30d)
        );
        player.recordDevelopmentYear(season);
    }

    private double ageFactor(int age) {
        if (age <= 19) return 1.28d;
        if (age <= 23) return 1.06d;
        if (age <= 27) return 0.68d;
        if (age <= 30) return 0.32d;
        return 0.10d;
    }

    private double minutesFactor(Player player) {
        int minutes = player.getSeasonMinutes();
        if (minutes >= 1_000) return 1.15d;
        if (minutes >= 450) return 1.00d;
        if (minutes >= 90) return 0.85d;
        return 0.75d;
    }

    private double performanceFactor(Player player) {
        if (player.getSeasonRatingMatches() == 0) return 1.0d;
        double rating = player.getSeasonAverageRating();
        if (rating >= 7.5d) return 1.12d;
        if (rating >= 7.0d) return 1.06d;
        if (rating < 6.0d) return 0.92d;
        return 1.0d;
    }

    private double fatigueFactor(Player player) {
        return 0.62d + (0.42d * player.getFatigue() / 100d);
    }

    private double curveFactor(DevelopmentCurve curve, int age) {
        switch (curve) {
            case EARLY:
                return age <= 22 ? 1.16d : 0.88d;
            case LATE:
                return age >= 23 && age <= 29 ? 1.15d : 0.85d;
            case VOLATILE:
                return 0.80d + (random.nextDouble() * 0.42d);
            default:
                return 1.0d;
        }
    }

    private double[] focusWeights(DevelopmentFocus focus) {
        switch (focus) {
            case ATTACK:
                return new double[] { 1.85d, 1.12d, 0.72d, 0.92d, 1.15d };
            case PASSING:
                return new double[] { 1.00d, 1.85d, 0.78d, 0.88d, 1.20d };
            case DEFENSE:
                return new double[] { 0.72d, 0.85d, 1.85d, 1.15d, 0.72d };
            case PHYSICAL:
                return new double[] { 1.12d, 0.78d, 1.10d, 1.85d, 0.78d };
            case DRIBBLING:
                return new double[] { 1.16d, 1.10d, 0.70d, 0.86d, 1.85d };
            default:
                return new double[] { 1d, 1d, 1d, 1d, 1d };
        }
    }
}
