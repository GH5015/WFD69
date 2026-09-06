package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Escolhe e coloca um reserva no mesmo slot quando há lesão durante simulação rápida. */
public final class AutomaticInjurySubstitutionService {
    private AutomaticInjurySubstitutionService() { }

    public static Result replace(
        Match match,
        Club club,
        Map<Integer, Player> lineupBeforeMinute,
        List<Player> bench,
        List<Player> alreadySubstituted,
        int minute
    ) {
        if (match == null || club == null || lineupBeforeMinute == null || bench == null) return null;

        Map.Entry<Integer, Player> injuredSlot = null;
        for (Map.Entry<Integer, Player> entry : lineupBeforeMinute.entrySet()) {
            Player player = entry.getValue();
            if (player != null && player.isInjured()) {
                injuredSlot = entry;
                break;
            }
        }
        if (injuredSlot == null) return null;

        Player injured = injuredSlot.getValue();
        int slot = injuredSlot.getKey();
        String targetPosition = targetPosition(club, slot, injured);
        List<Player> candidates = new ArrayList<>();
        for (Player player : bench) {
            if (player == null || !player.canPlay() || player == injured
                || club.getStartingXI().contains(player)
                || (alreadySubstituted != null && alreadySubstituted.contains(player))) continue;
            candidates.add(player);
        }
        if (candidates.isEmpty()) return null;

        candidates.sort(Comparator
            .comparingInt((Player player) -> replacementScore(player, targetPosition)).reversed()
            .thenComparing(Player::getName));
        Player replacement = candidates.get(0);
        club.assignPlayerToSlot(slot, replacement);
        match.registerSubstitution(injured, replacement, minute, club);
        return new Result(injured, replacement, slot);
    }

    private static String targetPosition(Club club, int slot, Player injured) {
        if (club.getFormation() != null && slot >= 0
            && slot < club.getFormation().getPositionSlots().size()) {
            return club.getFormation().getPositionSlots().get(slot);
        }
        return injured.getPrimaryPosition() == null ? "CM" : injured.getPrimaryPosition().name();
    }

    private static int replacementScore(Player player, String targetPosition) {
        int positional = player.getEffectiveOverallForPosition(targetPosition);
        int natural = player.getPrimaryPosition() != null
            && player.getPrimaryPosition().name().equalsIgnoreCase(targetPosition) ? 2
            : player.getSecondaryPosition() != null
                && player.getSecondaryPosition().name().equalsIgnoreCase(targetPosition) ? 1 : 0;
        return natural * 10_000 + positional * 100 + player.getFatigue();
    }

    public static final class Result {
        public final Player injured;
        public final Player replacement;
        public final int slot;
        private Result(Player injured, Player replacement, int slot) {
            this.injured = injured; this.replacement = replacement; this.slot = slot;
        }
    }
}
