package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TechnicalAttributes;

import java.util.ArrayList;
import java.util.List;

/** Relaciona passe, amplitude e pressão com os atletas realmente escalados. */
public final class TacticalSuitabilityEvaluator {
    private TacticalSuitabilityEvaluator() { }

    public static Profile evaluate(Club club, List<Player> lineup) {
        List<Player> active = new ArrayList<>();
        if (lineup != null) {
            for (Player player : lineup) {
                if (player != null && player.getMatchRedCards() == 0 && !player.isInjured()) active.add(player);
            }
        }

        double averagePass = average(active, Attribute.PASS, "GK", false);
        double averageDribble = average(active, Attribute.DRIBBLE, "GK", false);
        double averagePhysical = average(active, Attribute.PHYSICAL, "GK", false);
        double averageDefense = average(active, Attribute.DEFENSE, "GK", false);
        double averageAttack = average(active, Attribute.ATTACK, "GK", false);
        double averageFatigue = active.stream().mapToInt(Player::getFatigue).average().orElse(100d);
        double attackerAttack = average(active, Attribute.ATTACK, "ST|CF|LW|RW|CAM", true);
        double attackerPhysical = average(active, Attribute.PHYSICAL, "ST|CF|LW|RW|CAM", true);

        List<Player> wingers = filterPositions(active, "LW|RW|LM|RM");
        List<Player> fullbacks = filterPositions(active, "LB|RB|LWB|RWB");
        double wingerQuality = roleQuality(wingers, .42d, .38d, .20d);
        double fullbackQuality = roleQuality(fullbacks, .32d, .28d, .40d);
        int naturalWidePlayers = wingers.size() + fullbacks.size();
        double wideQuality = (wingers.isEmpty() ? 50d : wingerQuality) * .62d
            + (fullbacks.isEmpty() ? 52d : fullbackQuality) * .38d;

        double shortPassingQuality = averagePass * .64d + averageDribble * .36d;
        double longPassingQuality = averagePass * .45d + attackerAttack * .35d
            + ((averagePhysical + attackerPhysical) / 2d) * .20d;
        double pressQuality = averageDefense * .45d + averagePhysical * .35d + averagePass * .20d;

        return new Profile(
            club != null ? club.getPassing() : 50f,
            club != null ? club.getWidth() : 50f,
            averagePass,
            averageDribble,
            shortPassingQuality,
            longPassingQuality,
            wideQuality,
            naturalWidePlayers,
            pressQuality,
            averagePhysical,
            averageDefense,
            averageAttack,
            averageFatigue
        );
    }

    public static Profile apply(Club club, List<Player> lineup, TacticalModifiers modifiers) {
        Profile profile = evaluate(club, lineup);
        if (club == null || modifiers == null) return profile;

        float passing = club.getPassing();
        double passStrength = .50d + Math.abs(passing - 50d) / 100d;
        if (passing <= 40f) {
            if (profile.shortPassingQuality >= 80d) {
                modifiers.passRetentionMultiplier *= 1d + .08d * passStrength;
                modifiers.centralCreationMultiplier *= 1d + .04d * passStrength;
            } else if (profile.shortPassingQuality >= 72d) {
                modifiers.passRetentionMultiplier *= 1d + .04d * passStrength;
                modifiers.centralCreationMultiplier *= 1d + .02d * passStrength;
            } else if (profile.shortPassingQuality < 68d) {
                double deficit = Math.min(12d, 68d - profile.shortPassingQuality);
                modifiers.turnoverRiskMultiplier *= 1d + deficit * .012d * passStrength;
                modifiers.passRetentionMultiplier *= Math.max(.84d, 1d - deficit * .008d * passStrength);
            }
        } else if (passing >= 60f) {
            if (profile.longPassingQuality >= 79d) {
                modifiers.transitionSpeedMultiplier *= 1d + .10d * passStrength;
                modifiers.aerialThreatMultiplier *= 1d + .10d * passStrength;
            } else if (profile.longPassingQuality >= 71d) {
                modifiers.transitionSpeedMultiplier *= 1d + .05d * passStrength;
                modifiers.aerialThreatMultiplier *= 1d + .05d * passStrength;
            } else if (profile.longPassingQuality < 67d) {
                double deficit = Math.min(12d, 67d - profile.longPassingQuality);
                modifiers.turnoverRiskMultiplier *= 1d + deficit * .011d * passStrength;
            }
        }

        double widthStrength = Math.max(0d, club.getWidth() - 50d) / 50d;
        if (widthStrength > 0d) {
            if (profile.wideQuality >= 78d && profile.naturalWidePlayers >= 3) {
                modifiers.flankThreatMultiplier *= 1d + .18d * widthStrength;
                modifiers.crossingMultiplier *= 1d + .14d * widthStrength;
            } else if (profile.wideQuality >= 70d && profile.naturalWidePlayers >= 2) {
                modifiers.flankThreatMultiplier *= 1d + .08d * widthStrength;
                modifiers.crossingMultiplier *= 1d + .06d * widthStrength;
            } else {
                modifiers.spacingPenaltyMultiplier *= 1d + .20d * widthStrength;
                modifiers.turnoverRiskMultiplier *= 1d + .10d * widthStrength;
                modifiers.defensiveCoverageMultiplier *= Math.max(.82d, 1d - .12d * widthStrength);
            }
        }

        // Uma equipe sem físico, defesa e passe adequados tenta pressionar,
        // mas tem mais dificuldade para converter a tentativa em recuperação.
        modifiers.pressingEfficiency *= clamp(.82d + profile.pressQuality / 360d, .88d, 1.12d);
        return profile;
    }

    /**
     * Faz uma equipe intensa perder eficiência ao longo da partida quando o XI
     * não possui Físico suficiente. É recalculado a cada minuto, portanto uma
     * substituição por um atleta descansado melhora a sustentação imediatamente.
     */
    public static double applyMinuteSustainability(
        Profile profile,
        TacticalModifiers modifiers,
        int minute
    ) {
        if (profile == null || modifiers == null) return 1d;
        double sustainability = calculateSustainability(
            profile.getEffectivePhysical(),
            modifiers.tempoSetting,
            modifiers.pressureSetting,
            minute
        );
        double deterioration = 1d - sustainability;
        modifiers.pressingEfficiency *= sustainability;
        modifiers.transitionSpeedMultiplier *= 1d - deterioration * .34d;
        modifiers.eventFrequencyMultiplier *= 1d - deterioration * .24d;
        modifiers.turnoverRiskMultiplier *= 1d + deterioration * .72d;
        modifiers.defensiveCoverageMultiplier *= 1d - deterioration * .28d;
        return sustainability;
    }

    public static double calculateSustainability(
        double effectivePhysical,
        double tempo,
        double pressure,
        int minute
    ) {
        double intensityDemand = tempo * .42d + pressure * .58d;
        double excessDemand = Math.max(0d, intensityDemand - effectivePhysical);
        double startMinute = clamp(66d - excessDemand * .55d, 48d, 70d);
        if (minute <= startMinute || intensityDemand < 58d) return 1d;

        double progress = clamp((minute - startMinute) / Math.max(1d, 90d - startMinute), 0d, 1d);
        double maximumDrop = clamp(.06d + excessDemand * .014d, .06d, .36d);
        return clamp(1d - maximumDrop * Math.pow(progress, .82d), .64d, 1d);
    }

    private static List<Player> filterPositions(List<Player> players, String expression) {
        List<Player> filtered = new ArrayList<>();
        for (Player player : players) {
            String position = player.getPosition();
            if (position != null && position.matches("(?i)" + expression)) filtered.add(player);
        }
        return filtered;
    }

    private static double average(
        List<Player> players,
        Attribute attribute,
        String positions,
        boolean includeOnlyMatching
    ) {
        double total = 0d;
        int count = 0;
        for (Player player : players) {
            String position = player.getPosition() == null ? "" : player.getPosition();
            boolean matches = position.matches("(?i)" + positions);
            if ((includeOnlyMatching && !matches) || (!includeOnlyMatching && matches)) continue;
            total += attribute.value(player.getTechnicalAttributes());
            count++;
        }
        return count == 0 ? 60d : total / count;
    }

    private static double roleQuality(List<Player> players, double attack, double dribble, double pass) {
        if (players.isEmpty()) return 50d;
        double total = 0d;
        for (Player player : players) {
            TechnicalAttributes attributes = player.getTechnicalAttributes();
            total += attributes.getAtaque() * attack
                + attributes.getDrible() * dribble
                + attributes.getPasse() * pass;
        }
        return total / players.size();
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private enum Attribute {
        ATTACK { int value(TechnicalAttributes a) { return a.getAtaque(); } },
        PASS { int value(TechnicalAttributes a) { return a.getPasse(); } },
        DEFENSE { int value(TechnicalAttributes a) { return a.getDefesa(); } },
        PHYSICAL { int value(TechnicalAttributes a) { return a.getFisico(); } },
        DRIBBLE { int value(TechnicalAttributes a) { return a.getDrible(); } };
        abstract int value(TechnicalAttributes attributes);
    }

    public static final class Profile {
        private final float passingSetting;
        private final float widthSetting;
        private final double averagePass;
        private final double averageDribble;
        private final double shortPassingQuality;
        private final double longPassingQuality;
        private final double wideQuality;
        private final int naturalWidePlayers;
        private final double pressQuality;
        private final double averagePhysical;
        private final double averageDefense;
        private final double averageAttack;
        private final double averageFatigue;

        private Profile(
            float passingSetting, float widthSetting, double averagePass, double averageDribble,
            double shortPassingQuality, double longPassingQuality, double wideQuality,
            int naturalWidePlayers, double pressQuality, double averagePhysical,
            double averageDefense, double averageAttack, double averageFatigue
        ) {
            this.passingSetting = passingSetting;
            this.widthSetting = widthSetting;
            this.averagePass = averagePass;
            this.averageDribble = averageDribble;
            this.shortPassingQuality = shortPassingQuality;
            this.longPassingQuality = longPassingQuality;
            this.wideQuality = wideQuality;
            this.naturalWidePlayers = naturalWidePlayers;
            this.pressQuality = pressQuality;
            this.averagePhysical = averagePhysical;
            this.averageDefense = averageDefense;
            this.averageAttack = averageAttack;
            this.averageFatigue = averageFatigue;
        }

        public int getAveragePass() { return (int) Math.round(averagePass); }
        public int getAverageDribble() { return (int) Math.round(averageDribble); }
        public int getPassingFitScore() {
            double score = passingSetting <= 40f ? shortPassingQuality
                : passingSetting >= 60f ? longPassingQuality
                : (shortPassingQuality + longPassingQuality) / 2d;
            return (int) Math.round(score);
        }
        public int getWideQuality() { return (int) Math.round(wideQuality); }
        public int getNaturalWidePlayers() { return naturalWidePlayers; }
        public int getPressQuality() { return (int) Math.round(pressQuality); }
        public int getAveragePhysical() { return (int) Math.round(averagePhysical); }
        public int getAverageFatigue() { return (int) Math.round(averageFatigue); }
        public double getEffectivePhysical() {
            return averagePhysical * (.78d + .22d * clamp(averageFatigue / 100d, 0d, 1d));
        }
        public int getOverallFitScore(Club club) {
            return calculateOverallFitScore(club, getEffectivePhysical());
        }
        public int getBaseOverallFitScore(Club club) {
            return calculateOverallFitScore(club, averagePhysical);
        }
        private int calculateOverallFitScore(Club club, double physicalCapacity) {
            if (club == null) return 50;
            double intensityDemand = club.getTempo() * .42d + club.getPressure() * .58d;
            double physicalFit = clamp(76d + (physicalCapacity - intensityDemand) * 1.2d, 42d, 94d);
            double defensiveStyle = club.getPressure() >= 60f
                ? pressQuality
                : averageDefense * .58d + averagePhysical * .42d;
            double transitionFit = averagePhysical * .45d + averageAttack * .35d + averagePass * .20d;
            double styleFit = getPassingFitScore();
            if (club.getTempo() >= 70f || club.getMentalityValue() >= 70f) {
                styleFit = styleFit * .55d + transitionFit * .45d;
            }
            double widthWeight = club.getWidth() >= 60f ? .15d : .06d;
            double pressureWeight = .18d + club.getPressure() / 500d;
            double physicalWeight = .18d + Math.max(club.getTempo(), club.getPressure()) / 500d;
            double passingWeight = 1d - widthWeight - pressureWeight - physicalWeight;
            double score = styleFit * passingWeight
                + wideQuality * widthWeight
                + defensiveStyle * pressureWeight
                + physicalFit * physicalWeight;
            return (int) Math.round(clamp(score, 35d, 95d));
        }
        public String getIntensityFitLabel(Club club) {
            if (club == null) return "SEM DADOS";
            double demand = club.getTempo() * .42d + club.getPressure() * .58d;
            double margin = getEffectivePhysical() - demand;
            if (margin >= 5d) return "EQUIPE ADEQUADA";
            if (margin >= -3d) return "INTENSIDADE SUSTENTÁVEL";
            if (margin >= -10d) return "QUEDA APÓS 70'";
            return "INTENSIDADE INSUSTENTÁVEL";
        }
        public String getPassingStyleLabel() {
            return passingSetting <= 40f ? "PASSE CURTO" : passingSetting >= 60f ? "PASSE LONGO" : "PASSE MISTO";
        }
        public String getPassingFitLabel() { return fitLabel(getPassingFitScore()); }
        public String getWidthFitLabel() {
            if (widthSetting < 60f) return "AMPLITUDE MODERADA";
            if (wideQuality >= 78d && naturalWidePlayers >= 3) return "EXCELENTE PELOS LADOS";
            if (wideQuality >= 70d && naturalWidePlayers >= 2) return "BOA OCUPAÇÃO LATERAL";
            return "POUCA QUALIDADE PELOS LADOS";
        }
        private static String fitLabel(int score) {
            if (score >= 80) return "EXCELENTE ADEQUAÇÃO";
            if (score >= 72) return "BOA ADEQUAÇÃO";
            if (score >= 68) return "ADEQUAÇÃO RAZOÁVEL";
            return "ELENCO POUCO ADEQUADO";
        }
    }
}
