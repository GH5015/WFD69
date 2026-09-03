package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class PlayerNegotiationRegressionTest {
    public static void main(String[] args) {
        int reduced = 0, firm = 0, acceptedDiscount = 0;
        for (int i = 0; i < 100; i++) {
            PlayerNegotiation.Session session = new PlayerNegotiation.Session();
            PlayerNegotiation.Response first = PlayerNegotiation.respond(session, 1_000_000, 950_000, 4, 4, 4, "player" + i);
            if (first.salary < 1_000_000) reduced++; else firm++;
            require(PlayerNegotiation.respond(session, 1_000_000, 950_000, 4, 4, 4, "player" + i) == first, "Identical offer rerolled");
            require(session.rounds == 1, "Identical offer used another round");
            for (int round = 0; round < 12; round++)
                PlayerNegotiation.respond(session, 1_000_000, 800_000 + round * 1000, 4, 4, 5, "player" + i);
            require(session.askingRatio >= .88, "Unlimited discount");
            PlayerNegotiation.Response accept = PlayerNegotiation.respond(session, 1_000_000,
                PlayerNegotiation.askingSalary(session, 1_000_000), 4, 4, 4, "player" + i);
            require(accept.accepted, "Player refused own latest terms");
            PlayerNegotiation.Response discount = PlayerNegotiation.respond(new PlayerNegotiation.Session(),
                1_000_000, 990_000, 4, 4, 5, "discount" + i);
            if (discount.accepted) acceptedDiscount++;
        }
        require(reduced > 0 && firm > 0 && acceptedDiscount > 0, "Missing varied responses");
        PlayerNegotiation.Response duration = PlayerNegotiation.respond(new PlayerNegotiation.Session(),
            1_000_000, 950_000, 1, 5, 5, "duration");
        require(!duration.accepted && duration.years == 5 && duration.salary == 1_000_000, "Duration ignored");

        League league = new League("WFL", 1969);
        Club club = new Club("Casa", "Brasil", "Ocidental", 85, 40_000_000, "Arena", "santos.png");
        league.addClub(club); club.setUserControlled(true);
        FreeAgencyService market = new FreeAgencyService(league);
        Player player = new Player("Livre", "Brasil", Position.ST, null, 24,
            new TechnicalAttributes(80,80,80,80,80,60), 90, 10_000);
        market.addUndraftedFreeAgents(Collections.singletonList(player));
        long ask = market.getRequestedAnnualSalary(player);
        require(market.submitOffer(club, player, ask * 85 / 100, 4).isAccepted(), "Initial submission failed");
        market.processPendingOffers(club, 1969);
        FreeAgencyService.Offer counter = market.findOffer(player);
        require(counter.getStatus() == FreeAgencyService.OfferStatus.COUNTER_OFFER && player.getCurrentClub() == null,
            "Counteroffer should leave player free");
        require(market.submitOffer(club, player, counter.getCounterAnnualSalary(), counter.getCounterYears()).isAccepted(), "Counter response failed");
        require(market.findOffer(player).getStatus() == FreeAgencyService.OfferStatus.PENDING, "Latest offer not found");
        require(market.findOffer(player).getAnnualSalary() == counter.getCounterAnnualSalary(), "Counter salary rounded away");
        require(!market.submitOffer(club, player, ask, 4).isAccepted(), "Duplicate pending offer allowed");
        market.processPendingOffers(club, 1969);
        require(market.findOffer(player).getStatus() != FreeAgencyService.OfferStatus.PENDING, "Response never processed");
        System.out.println("Player negotiation: concessions, firm demands, discounted acceptance, duration, limited rounds, repeated offers and FA counter workflow OK.");
    }
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
}
