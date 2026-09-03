package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class AiRenewalRegressionTest {
    public static void main(String[] args) {
        League league = new League("WFL", 1970);
        Club ai = club("IA"), user = club("Usuário"), capped = club("Sem espaço");
        user.setUserControlled(true);
        league.addClub(ai); league.addClub(user); league.addClub(capped);
        for (int i = 0; i < 23; i++) add(ai, "Atleta " + i, i == 22 ? 36 : 25, 90 - i, 300_000, 1971);
        Player star = ai.getSquad().get(0), surplus = ai.getSquad().get(22);
        ai.getStartingXI().add(star);
        Player own = add(user, "Decisão manual", 25, 85, 400_000, 1971);
        Player longTerm = add(ai, "Contrato longo", 22, 82, 300_000, 1975);
        Player blocked = add(capped, "Renovação inviável", 24, 85, 100_000, 1971);
        add(capped, "Folha comprometida", 28, 88, capped.getFinance().getHardCap() - 100_000, 1975);
        require(AiContractRenewalService.renewExpiringContracts(league) == 0, "In-season renewal");
        league.setCurrentStage("OFFSEASON");
        long desired = ContractRenewalService.calculateDemand(star, ai, 1970).desiredAnnualSalary;
        int renewed = AiContractRenewalService.renewExpiringContracts(league);
        require(renewed > 0, "No AI renewals");
        require(star.getCurrentClub() == ai && star.getContractEndYear() > 1971, "Star not retained");
        require(Math.abs(star.getAnnualSalary() - desired) <= 6 && star.getTradeBlockedDays() == 30,
            "Negotiation rules bypassed (allow monthly salary rounding)");
        require(longTerm.getContractEndYear() == 1975, "Long-term contract changed");
        require(own.getContractEndYear() == 1971, "AI renewed user's player");
        require(blocked.getContractEndYear() == 1971, "Hard cap exceeded");
        require(surplus.getContractEndYear() == 1971, "Surplus veteran should enter market");
        require(AiContractRenewalService.renewExpiringContracts(league) == 0, "Renewed twice in same offseason");
        FreeAgencyService market = new FreeAgencyService(league);
        require(!market.getFreeAgents().contains(star) && star.getCurrentClub() == ai, "Retained player released");
        require(market.getFreeAgents().contains(surplus) && market.getFreeAgents().contains(blocked), "Released players missing from market");
        require(ai.getFinance().getAnnualPayroll() <= ai.getFinance().getHardCap(), "Renewals exceeded cap");
        League automatic = new League("WFL", 1970); Club auto = club("Renovação na transição"); automatic.addClub(auto);
        Player veteran = add(auto, "Veterano", 36, 85, 300_000, 1971);
        automatic.setCurrentStage("OFFSEASON"); new FreeAgencyService(automatic);
        require(veteran.getCurrentClub() == auto && veteran.getContractEndYear() == 1972, "Automatic transition/short veteran contract failed");
        System.out.println("AI renewals: retention before release, negotiated terms, user control, hard cap, surplus, veterans and idempotence OK.");
    }
    private static Club club(String name) { return new Club(name, "Brasil", "Ocidental", 80, 50_000_000, "Arena", "santos.png"); }
    private static Player add(Club club, String name, int age, int quality, long salary, int end) {
        Player player = new Player(name, "Brasil", Position.CM, null, age,
            new TechnicalAttributes(quality, quality, quality, quality, quality, 60), quality, 10_000);
        player.transferTo(club); player.renewContract(salary, 1, 1970); player.setContractEndYear(end); return player;
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
