package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class DraftPickAvailabilityRegressionTest {
    public static void main(String[] args) {
        League league = new League("WFL", 1975);
        Club home = team("Casa"), away = team("Outro");
        league.addClub(home); league.addClub(away);
        DraftPick old = pick(home, 1974), current = pick(home, 1975);
        DraftPick next = pick(home, 1976), future = pick(home, 1977);
        DraftPick other = pick(away, 1977);
        require(!old.isAvailableForTrade(league) && !current.isAvailableForTrade(league), "Past drafts are tradable");
        require(next.isAvailableForTrade(league) && future.isAvailableForTrade(league), "Upcoming picks incorrectly blocked");
        TradeOffer offer = new TradeOffer(home, away);
        offer.addPickToGive(old); offer.addPickToReceive(other);
        require(!TradeRulesValidator.validateRules(offer, 1975).isValid, "Year-only validation accepts old pick");
        require(!TradeExecutionService.execute(league, offer), "Old pick executed");
        require(old.getCurrentOwner() == home, "Rejected trade mutated owner");
        require(TradeFinderService.findForPick(league, home, old).isEmpty(), "Finder accepted obsolete pick");
        next.markUsed();
        require(!next.isAvailableForTrade(league) && !next.isAvailableForTrade(1975), "Used pick remains tradable");
        DraftPick unusedSecond = new DraftPick(1976, 2, home);
        home.getDraftPicks().add(unusedSecond);
        league.finalizeDraft();
        require(!unusedSecond.isAvailableForTrade(league) && !unusedSecond.isAvailableForTrade(1975), "Unused expired slot remains tradable");
        require(future.isAvailableForTrade(league), "Finalization blocked future years");
        DraftPick legacy = pick(home, 1976);
        require(!legacy.isAvailableForTrade(league), "Legacy finalized pick remains tradable");
        require(DraftOrderService.getCurrentDraftOrder(league, 1976).contains(next), "Historical order was lost");
        System.out.println("Draft picks: past years, used picks, finalized drafts, legacy data, future years, finder and execution OK.");
    }
    private static Club team(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }
    private static DraftPick pick(Club club, int year) {
        DraftPick pick = new DraftPick(year, 1, club); club.getDraftPicks().add(pick); return pick;
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
