package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class FinancialSanctionRegressionTest {
    public static void main(String[] args) {
        for (boolean user : new boolean[]{false, true}) {
            Club club = team(user ? "Usuário" : "IA", 12);
            club.setUserControlled(user);
            club.getFinance().setBalance(-1_000_000_000L);
            require(FinancialSanctionService.closeSeason(club, 1969).isEmpty(), "Early penalty");
            require(FinancialSanctionService.closeSeason(club, 1970).isEmpty(), "Early penalty");
            List<Player> best = new ArrayList<>(club.getSquad());
            best.sort(Comparator.comparingInt(Player::getOverall).reversed().thenComparing(Player::getName));
            best = new ArrayList<>(best.subList(0, 5));
            League league = new League("WFL", 1971); league.addClub(club);
            FreeAgencyService existingMarket = new FreeAgencyService(league);
            league.beginOffseason();
            require(club.getSquad().size() == 7, "Must lose exactly five");
            for (Player p : best) {
                require(p.getCurrentClub() == null && p.getContractEndYear() == 1971, "Player still contracted");
                require(!club.getTacticsMap().containsValue(p) && !club.getBenchPlayers().contains(p), "Stale lineup");
                require(existingMarket.getFreeAgents().contains(p), "Existing market missed sanction release");
            }
            FreeAgencyService rebuiltMarket = new FreeAgencyService(league);
            require(rebuiltMarket.getFreeAgents().containsAll(best), "Rebuilt market missed releases");
            require(FinancialSanctionService.closeSeason(club, 1971).isEmpty(), "Duplicate penalty");
            require(FinancialSanctionService.closeSeason(club, 1972).isEmpty(), "Cycle not reset");
            club.getFinance().setBalance(0);
            FinancialSanctionService.closeSeason(club, 1973);
            require(club.getFinance().getConsecutiveNegativeSeasons() == 0, "Zero should interrupt streak");
            club.getFinance().setBalance(-1);
            FinancialSanctionService.closeSeason(club, 1974);
            FinancialSanctionService.closeSeason(club, 1976);
            require(club.getFinance().getConsecutiveNegativeSeasons() == 1, "Skipped years counted as consecutive");
        }
        Club small = team("Pequeno", 3);
        small.getFinance().setBalance(-1);
        FinancialSanctionService.closeSeason(small, 1969);
        FinancialSanctionService.closeSeason(small, 1970);
        require(FinancialSanctionService.closeSeason(small, 1971).size() == 3, "Small roster failure");
        require(small.getFinance().getBalance() == -1, "Penalty paid compensation");
        System.out.println("Financial sanction: three seasons, top five, user/AI, reset, repeated calls, market integration and small squads OK.");
    }
    private static Club team(String name, int count) {
        Club club = new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
        for (int i = 0; i < count; i++) {
            int value = 65 + i;
            Player p = new Player(name + i, "Brasil", Position.CM, null, 22,
                new TechnicalAttributes(value, value, value, value, value, value), 95, 10_000);
            p.renewContract(120_000, 10, 1969); p.transferTo(club);
            if (i < 11) club.getTacticsMap().put(i, p);
        }
        return club;
    }
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
}
