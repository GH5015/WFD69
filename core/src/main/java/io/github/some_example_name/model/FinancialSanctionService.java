package io.github.some_example_name.model;

import java.util.*;

/** Sanção esportiva por três fechamentos consecutivos com caixa negativo. */
public final class FinancialSanctionService {
    private FinancialSanctionService() { }

    public static List<Player> closeSeason(Club club, int year) {
        List<Player> released = new ArrayList<>();
        if (club == null || !club.getFinance().closeSeasonSolvency(year)) return released;
        List<Player> ranked = new ArrayList<>(club.getSquad());
        ranked.sort(Comparator.comparingInt(Player::getOverall).reversed()
            .thenComparing(Player::getName));
        for (Player player : ranked.subList(0, Math.min(5, ranked.size()))) {
            club.getStartingXI().remove(player);
            club.getTacticsMap().values().removeIf(p -> p == player);
            player.setContractEndYear(year);
            player.setContractYears(0);
            player.setTradeBlockedDays(0);
            player.transferTo(null);
            released.add(player);
        }
        club.getBenchPlayers(); // Remove do banco os atletas que saíram.
        return released;
    }
}
