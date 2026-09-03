package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** AI retention decisions made before expiring contracts are released to free agency. */
public final class AiContractRenewalService {
    private AiContractRenewalService() { }

    public static int renewExpiringContracts(League league) {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage())) return 0;
        int renewed = 0, year = league.getCurrentSeason();
        for (Club club : league.getClubs()) {
            if (club.isUserControlled()) continue;
            List<Player> priorities = new ArrayList<>(club.getSquad());
            priorities.sort(Comparator.comparingInt((Player player) -> priority(club, player)).reversed()
                .thenComparing(Player::getName));
            for (int rank = 0; rank < priorities.size(); rank++) {
                Player player = priorities.get(rank);
                if (!player.isContractExpiringAtSeasonEnd(year)) continue;
                // Keep the core, prospects and positional cover; leave surplus veterans to the market.
                boolean prospect = player.getAge() <= 23 && player.getPotential() >= 80;
                long positionCount = club.getSquad().stream().filter(p -> p.getPrimaryPosition() == player.getPrimaryPosition()).count();
                if (rank >= 20 && !prospect && positionCount > 2) continue;
                ContractRenewalService.Demand demand = ContractRenewalService.calculateDemand(player, club, year);
                long ceiling = club.getFinance().getBalance() < 0 ? club.getFinance().getSalaryCap()
                    : club.getFinance().getBalance() < 20_000_000L ? club.getFinance().getLuxuryTaxThreshold()
                    : club.getFinance().getHardCap();
                long available = ceiling - club.getFinance().getAnnualPayroll() + player.getAnnualSalary();
                if (available < demand.desiredAnnualSalary) continue;
                ContractRenewalService.Decision decision = ContractRenewalService.evaluateProposal(player, club, year,
                    demand.desiredAnnualSalary, demand.preferredYears);
                if (decision.outcome != ContractRenewalService.Outcome.ACCEPTED) continue;
                player.renewContract(demand.desiredAnnualSalary, demand.preferredYears, year);
                renewed++;
            }
        }
        return renewed;
    }

    private static int priority(Club club, Player player) {
        int starterBonus = club.getTacticsMap().containsValue(player) || club.getStartingXI().contains(player) ? 25 : 0;
        int development = player.getAge() <= 23 ? Math.max(0, player.getPotential() - player.getOverall()) : 0;
        return player.getOverall() * 3 + starterBonus + development - Math.max(0, player.getAge() - 32) * 3;
    }
}
