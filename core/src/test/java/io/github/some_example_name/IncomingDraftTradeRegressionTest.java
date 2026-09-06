package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.Calendar;

public final class IncomingDraftTradeRegressionTest {
    public static void main(String[] args) {
        League league = new League("WFL", 1969);
        league.setCurrentStage("OFFSEASON");
        Calendar draftDay = Calendar.getInstance();
        draftDay.clear(); draftDay.set(1969, Calendar.DECEMBER, 20);
        league.setCurrentDate(draftDay.getTime());

        Club user = team("Usuário"), rival = team("Rival");
        league.addClub(user); league.addClub(rival);
        fillRoster(user); fillRoster(rival);

        DraftPick current = pick(user, 1, 5);
        pick(rival, 1, 7);
        pick(rival, 2, 20);

        require(IncomingTradeOfferService.processDraftPickOffer(league, user, current),
            "No proposal was generated while the user's pick was on the clock");
        TradeOffer offer = league.getPendingIncomingTradeOffer();
        require(offer != null && offer.getUserPicks().contains(current),
            "The incoming proposal does not target the current pick");
        require(TradeRulesValidator.validateRules(offer, league).isValid,
            "Generated draft proposal violates trade rules");

        league.clearPendingIncomingTradeOffer(offer);
        require(!IncomingTradeOfferService.processDraftPickOffer(league, user, current),
            "The same pick generated a repeated proposal after resolution");
        System.out.println("Incoming Draft trade: current pick targeted, valid offer queued and no repeat loop OK.");
    }

    private static Club team(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }
    private static void fillRoster(Club club) {
        for (int i = 0; i < 23; i++) {
            Player player = new Player(club.getName() + " " + i, "Brasil", Position.CM, null, 25,
                new TechnicalAttributes(65, 68, 65, 70, 66, 60), 78, 30_000);
            player.setContractEndYear(1972); player.transferTo(club);
        }
    }
    private static DraftPick pick(Club club, int round, int projected) {
        DraftPick pick = new DraftPick(1970, round, club, projected);
        pick.setProjectedPositionConfidence(1d); pick.setPicksPerRound(20);
        club.getDraftPicks().add(pick); return pick;
    }
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
