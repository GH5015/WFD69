package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.GregorianCalendar;
import java.util.Calendar;

public final class AiFreeAgencyRegressionTest {
    public static void main(String[] args) {
        League league = new League("WFL", 1969);
        Club ai = club("IA"), user = club("Usuário"), capped = club("Sem espaço");
        user.setUserControlled(true);
        league.addClub(ai); league.addClub(user); league.addClub(capped);
        Player expensive = new Player("Contrato", "Brasil", Position.CM, null, 25,
            new TechnicalAttributes(80, 80, 80, 80, 80, 60), 85, 10_000);
        expensive.transferTo(capped);
        expensive.renewContract(capped.getFinance().getHardCap(), 5, 1969);
        league.setCurrentStage("OFFSEASON");
        league.setCurrentDate(new GregorianCalendar(1969, Calendar.NOVEMBER, 6).getTime());
        FreeAgencyService market = new FreeAgencyService(league);
        require(!league.isDraftFinalized(), "Fixture must precede draft");
        int total = 0;
        for (int day = 6; day < 13; day++) {
            league.setCurrentDate(new GregorianCalendar(1969, Calendar.NOVEMBER, day).getTime());
            total += market.processAiFreeAgentSignings(user, 1969);
        }
        require(total > 0 && !ai.getSquad().isEmpty(), "AI did not sign independently before draft");
        require(league.getFreeAgencyHistory(1969).size() == total, "Missing or duplicate signing history");
        require(league.getFreeAgencyHistory(1970).isEmpty(), "History leaked into another season");
        require(new FreeAgencyService(league).getUserOffers().isEmpty()
            && league.getFreeAgencyHistory(1969).size() == total, "History lost when market recreated");
        require(user.getSquad().isEmpty(), "User squad changed automatically");
        require(capped.getSquad().size() == 1, "Capped club signed a player");
        require(ai.getFinance().getAnnualPayroll() <= ai.getFinance().getHardCap(), "Hard cap exceeded");
        for (Player p : ai.getSquad()) {
            require(p.getContractEndYear() > 1969 && p.getTradeBlockedDays() == 60, "Invalid signing contract");
            require(!market.getFreeAgents().contains(p), "Signed player still listed");
        }
        league.setCurrentStage("PLAYOFFS");
        require(market.processAiFreeAgentSignings(user, 1969) == 0, "Signed during playoffs");
        System.out.println("AI Free Agency: independent pre-draft signings, user control, contracts, cap and closed window OK.");
    }
    private static Club club(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 50_000_000, "Arena", "santos.png");
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
