package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class PlayerSalaryGrowthRegressionTest {
    public static void main(String[] args) {
        League league = new League("WFL", 1975);
        Club club = new Club("Casa", "Brasil", "Ocidental", 85, 40_000_000, "Arena", "santos.png");
        league.addClub(club);
        FreeAgencyService agency = new FreeAgencyService(league);
        for (Position position : new Position[]{Position.ST, Position.CB, Position.GK}) {
            Player player = new Player("Prospecto " + position, "Brasil", position, null, 22,
                attributes(65), 95, 10_000);
            player.renewContract(240_000, 4, 1975);
            long signed = player.getAnnualSalary();
            long lowMarket = player.getMarketAnnualSalary();
            long lowRenewal = ContractRenewalService.calculateDemand(player, club, 1975).desiredAnnualSalary;
            long lowAgency = agency.getRequestedAnnualSalary(player);
            int lowOverall = player.getOverall();
            player.setTechnicalAttributes(attributes(88));
            require(player.getOverall() > lowOverall, "Fixture did not evolve");
            require(player.getMarketAnnualSalary() > lowMarket * 1.3, "Market value did not follow current OVR");
            long demand = ContractRenewalService.calculateDemand(player, club, 1975).desiredAnnualSalary;
            require(demand > lowRenewal * 1.3, "Renewal stuck at old salary");
            require(agency.getRequestedAnnualSalary(player) > lowAgency * 1.3, "Free agency stuck at old salary");
            require(player.getAnnualSalary() == signed, "Growth changed the signed contract");
            require(ContractRenewalService.evaluateProposal(player, club, 1975, lowRenewal, 4).outcome
                != ContractRenewalService.Outcome.ACCEPTED, "Outdated salary accepted after growth");
            player.renewContract(demand, 4, 1975);
            require(Math.abs(player.getAnnualSalary() - demand) <= 6, "Negotiated salary not applied");
            long updatedSigned = player.getAnnualSalary();
            player.setTechnicalAttributes(attributes(93));
            require(player.getAnnualSalary() == updatedSigned, "New contract not fixed");
        }
        System.out.println("Player salaries: current OVR, striker/defender/keeper, renewal, free agency and fixed signed contracts OK.");
    }
    private static TechnicalAttributes attributes(int value) {
        return new TechnicalAttributes(value, value, value, value, value, value);
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
