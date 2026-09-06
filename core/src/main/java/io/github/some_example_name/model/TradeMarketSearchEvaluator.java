package io.github.some_example_name.model;

import java.util.Map;

/**
 * Pontua jogadores para a busca de trocas. A pontuação não substitui a
 * negociação: ela apenas coloca no topo alvos que fazem sentido esportivo e
 * que o clube atual tem maior chance de aceitar negociar.
 */
public final class TradeMarketSearchEvaluator {

    private TradeMarketSearchEvaluator() {
    }

    public static int fitScore(Club userClub, Player player) {
        if (userClub == null || player == null) return 0;

        int need = needFor(userClub, player);
        int bestAtPosition = bestOverallAtPosition(userClub, player);
        int upgrade = player.getOverall() - bestAtPosition;
        int upside = Math.max(0, player.getTruePotential() - player.getOverall());

        double score = 18d + need * 10d;
        score += Math.max(-10d, Math.min(22d, upgrade * 2.4d));
        score += Math.min(12d, upside * .8d);

        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(userClub);
        if ((phase == ClubNeedEvaluator.TeamPhase.REBUILDING || phase == ClubNeedEvaluator.TeamPhase.BUYER)
            && player.getAge() <= 23) score += 8d;
        if (phase == ClubNeedEvaluator.TeamPhase.CONTENDER && player.getOverall() >= 82) score += 8d;
        if (player.getAge() >= 32) score -= phase == ClubNeedEvaluator.TeamPhase.REBUILDING ? 12d : 5d;

        return clamp(score);
    }

    public static int availabilityScore(Club owner, Player player, int season) {
        if (owner == null || player == null || player.getCurrentClub() != owner) return 0;
        if (player.getTradeBlockedDays() > 0 || player.isFreeAgent(season)) return 0;
        if (TradeRosterImpactEvaluator.isUntouchable(owner, player)) return 12;

        double retention = TradeRosterImpactEvaluator.getRetentionPremium(owner, player);
        int score = 72 - (int) Math.round(Math.max(0d, retention - 1d) * 90d);

        int remaining = player.getRemainingContractYears(season);
        if (remaining <= 1) score += 13;

        int needAfterExit = ClubNeedEvaluator.calculateNeedAfterRemoving(owner, player);
        if (needAfterExit <= 2) score += 12;
        else if (needAfterExit == 3) score += 5;
        else if (needAfterExit >= 5) score -= 13;

        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(owner);
        if (phase == ClubNeedEvaluator.TeamPhase.SELLER) score += 9;
        if (phase == ClubNeedEvaluator.TeamPhase.REBUILDING) {
            score += 7;
            if (player.getAge() >= 28) score += 9;
            if (player.getAge() <= 23 && player.getTruePotential() >= 86) score -= 12;
        }
        if (phase == ClubNeedEvaluator.TeamPhase.CONTENDER && player.getOverall() >= 82) score -= 9;

        return clamp(score);
    }

    public static boolean matchesProfile(
        String profile,
        Club userClub,
        Club owner,
        Player player,
        int season
    ) {
        if (profile == null || "TODOS".equals(profile)) return true;
        if ("ENCAIXE".equals(profile)) return fitScore(userClub, player) >= 68;
        if ("NEGOCIÁVEIS".equals(profile)) return availabilityScore(owner, player, season) >= 55;
        if ("OPORTUNIDADES".equals(profile)) {
            return availabilityScore(owner, player, season) >= 76
                && fitScore(userClub, player) >= 48;
        }
        if ("JOVENS".equals(profile)) {
            return player.getAge() <= 23 && player.getTruePotential() > player.getOverall();
        }
        if ("EXPIRANDO".equals(profile)) return player.getRemainingContractYears(season) <= 1;
        return true;
    }

    public static String availabilityLabel(Club owner, Player player, int season) {
        if (player == null || owner == null || player.getTradeBlockedDays() > 0) return "BLOQUEADO";
        int score = availabilityScore(owner, player, season);
        if (score < 25) return "INTOCÁVEL";
        if (score < 50) return "DIFÍCIL";
        if (score < 70) return "POSSÍVEL";
        if (score < 84) return "NEGOCIÁVEL";
        return "OPORTUNIDADE";
    }

    private static int needFor(Club club, Player player) {
        Map<String, Integer> needs = ClubNeedEvaluator.calculatePositionNeeds(club);
        int need = needs.containsKey(player.getPosition()) ? needs.get(player.getPosition()) : 3;
        if (player.getSecondaryPosition() != null) {
            Integer secondaryNeed = needs.get(player.getSecondaryPosition().name());
            if (secondaryNeed != null) need = Math.max(need, secondaryNeed);
        }
        return need;
    }

    private static int bestOverallAtPosition(Club club, Player target) {
        int best = 55;
        String targetGroup = positionGroup(target.getPosition());
        for (Player current : club.getSquad()) {
            if (current == null) continue;
            if (current.getPosition().equals(target.getPosition())) {
                best = Math.max(best, current.getOverall());
            } else if (positionGroup(current.getPosition()).equals(targetGroup)) {
                best = Math.max(best, current.getOverall() - 5);
            }
        }
        return best;
    }

    private static String positionGroup(String position) {
        if (position == null) return "";
        if (position.matches("GK")) return "GK";
        if (position.matches("CB|SW|LB|RB|LWB|RWB")) return "DEF";
        if (position.matches("CDM|CM|CAM|LM|RM")) return "MEI";
        if (position.matches("LW|RW|CF|ST")) return "ATA";
        return position;
    }

    private static int clamp(double value) {
        return (int) Math.round(Math.max(0d, Math.min(100d, value)));
    }
}
