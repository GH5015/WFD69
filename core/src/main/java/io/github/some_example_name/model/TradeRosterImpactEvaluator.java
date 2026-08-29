package io.github.some_example_name.model;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Avalia o custo esportivo e institucional de uma franquia abrir mão de um
 * atleta. Esses prêmios só existem para o clube atual do jogador; um possível
 * comprador continua avaliando o mesmo atleta pelo encaixe em seu próprio
 * projeto.
 */
public final class TradeRosterImpactEvaluator {

    public static final double UNTOUCHABLE_PREMIUM = 1.35d;

    private TradeRosterImpactEvaluator() {
    }

    public static boolean isUntouchable(Club club, Player player) {
        if (!ownsPlayer(club, player)) return false;

        List<Player> ranked = club.getSquad().stream()
            .sorted(Comparator.comparingInt(Player::getOverall).reversed())
            .collect(java.util.stream.Collectors.toList());
        int rank = ranked.indexOf(player);
        int bestOverall = ranked.isEmpty() ? player.getOverall() : ranked.get(0).getOverall();
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(club);

        if (player.getOverall() >= 92 && rank <= 1) return true;
        if (player.getAge() <= 23 && player.getTruePotential() >= 90 && rank <= 2) return true;

        switch (phase) {
            case CONTENDER:
                return player.getOverall() >= 88 && player.getOverall() >= bestOverall - 2;
            case BUYER:
                return player.getOverall() >= 87 && player.getAge() <= 29 && rank <= 1;
            case SELLER:
                return player.getAge() <= 23 && player.getTruePotential() >= 88 && rank <= 2;
            case REBUILDING:
            default:
                return player.getAge() <= 23 && player.getTruePotential() >= 86 && rank <= 2;
        }
    }

    public static double getRetentionPremium(Club club, Player player) {
        if (!ownsPlayer(club, player)) return 1d;

        double premium = isUntouchable(club, player) ? UNTOUCHABLE_PREMIUM : 1d;
        premium *= getLineupImpactPremium(club, player);
        premium *= getIdentityPremium(club, player);
        return premium;
    }

    public static double getLineupImpactPremium(Club club, Player player) {
        if (!ownsPlayer(club, player) || !isStarter(club, player)) return 1d;

        int replacementOverall = findReplacementOverall(club, player);
        if (replacementOverall <= 0) return 1.48d;

        int drop = Math.max(0, player.getOverall() - replacementOverall);
        if (drop <= 1) return 1.02d;
        if (drop <= 3) return 1.06d;
        if (drop <= 6) return 1.13d;
        if (drop <= 10) return 1.23d;
        if (drop <= 15) return 1.35d;
        return 1.48d;
    }

    public static int getExpectedReplacementOverall(Club club, Player player) {
        return findReplacementOverall(club, player);
    }

    public static String getLineupImpactLabel(Club club, Player player) {
        double premium = getLineupImpactPremium(club, player);
        if (premium >= 1.35d) return "MUITO ALTO";
        if (premium >= 1.20d) return "ALTO";
        if (premium >= 1.10d) return "MÉDIO";
        return "BAIXO";
    }

    private static boolean isStarter(Club club, Player player) {
        List<Player> lineup = club.getStartingXI();
        if (lineup != null && lineup.contains(player)) return true;

        long betterPlayers = club.getSquad().stream()
            .filter(candidate -> candidate != player)
            .filter(candidate -> candidate.getOverall() > player.getOverall())
            .count();
        return betterPlayers < 11;
    }

    private static int findReplacementOverall(Club club, Player outgoingPlayer) {
        int bestEffectiveOverall = 0;
        for (Player candidate : club.getSquad()) {
            if (candidate == outgoingPlayer) continue;

            int penalty;
            if (candidate.getPosition().equals(outgoingPlayer.getPosition())) {
                penalty = 0;
            } else if (positionGroup(candidate.getPosition()).equals(positionGroup(outgoingPlayer.getPosition()))) {
                penalty = 5;
            } else {
                continue;
            }

            bestEffectiveOverall = Math.max(bestEffectiveOverall, candidate.getOverall() - penalty);
        }
        return bestEffectiveOverall;
    }

    private static String positionGroup(String position) {
        if (position == null) return "";
        if (position.matches("GK")) return "GK";
        if (position.matches("CB|SW|LB|RB|LWB|RWB")) return "DEF";
        if (position.matches("CDM|CM|CAM|LM|RM")) return "MID";
        if (position.matches("LW|RW|CF|ST")) return "ATT";
        return position;
    }

    private static double getIdentityPremium(Club club, Player player) {
        List<Player> ranked = club.getSquad().stream()
            .sorted(Comparator.comparingInt(Player::getOverall).reversed())
            .limit(3)
            .collect(java.util.stream.Collectors.toList());
        if (!ranked.contains(player)) return 1d;

        String philosophy = club.getPhilosophy() == null
            ? ""
            : club.getPhilosophy().toLowerCase(Locale.ROOT);
        boolean homeIdentity = club.getCountry() != null
            && player.getNationality() != null
            && club.getCountry().equalsIgnoreCase(player.getNationality());
        boolean youthIdentity = player.getAge() <= 23
            && (philosophy.contains("jov") || philosophy.contains("formar") || philosophy.contains("talent"));
        boolean starIdentity = player.getOverall() >= 88
            && (philosophy.contains("estrela") || philosophy.contains("arte") || philosophy.contains("venced"));

        return homeIdentity || youthIdentity || starIdentity ? 1.08d : 1d;
    }

    private static boolean ownsPlayer(Club club, Player player) {
        return club != null && player != null && club.getSquad() != null
            && club.getSquad().contains(player) && player.getCurrentClub() == club;
    }
}
