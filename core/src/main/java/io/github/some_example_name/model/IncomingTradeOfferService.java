package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

/** Gera propostas espontâneas da IA pelo avanço semanal da carreira. */
public final class IncomingTradeOfferService {
    private static final double WEEKLY_OFFER_CHANCE = .48d;
    private static final int PLAYER_ATTEMPTS = 3;
    private static final int PICK_ATTEMPTS = 2;

    private IncomingTradeOfferService() { }

    public static boolean processWeeklyOffer(League league, Club userClub) {
        if (league == null || userClub == null
            || league.getPendingIncomingTradeOffer() != null
            || !SeasonCalendar.isTradeWindowOpen(league, userClub)
            || !incomingOffersAreAvailable(league)) {
            return false;
        }

        Random random = new Random(buildWeeklySeed(league, userClub));
        if (random.nextDouble() > WEEKLY_OFFER_CHANCE) return false;

        int season = league.getCurrentSeason();
        List<Player> players = new ArrayList<>();
        for (Player player : userClub.getSquad()) {
            if (player == null || player.getCurrentClub() != userClub
                || player.isFreeAgent(season) || player.getTradeBlockedDays() > 0
                || player.getOverall() < 68) {
                continue;
            }
            players.add(player);
        }
        Collections.shuffle(players, random);

        List<TradeFinderService.Result> possibilities = new ArrayList<>();
        for (int index = 0; index < Math.min(PLAYER_ATTEMPTS, players.size()); index++) {
            addBestResults(possibilities, TradeFinderService.findForPlayer(
                league, userClub, players.get(index)
            ));
        }

        List<DraftPick> picks = new ArrayList<>(userClub.getDraftPicks());
        picks.removeIf(pick -> pick == null || !pick.isAvailableForTrade(league));
        Collections.shuffle(picks, random);
        for (int index = 0; index < Math.min(PICK_ATTEMPTS, picks.size()); index++) {
            DraftPick pick = picks.get(index);
            if (pick == null || pick.getCurrentOwner() != userClub) continue;
            addBestResults(possibilities, TradeFinderService.findForPick(league, userClub, pick));
        }

        if (possibilities.isEmpty()) return false;
        possibilities.sort(Comparator.comparingDouble(TradeFinderService.Result::getScore).reversed());
        TradeFinderService.Result selected = possibilities.get(
            random.nextInt(Math.min(3, possibilities.size()))
        );
        return league.queueIncomingTradeOffer(selected.getOffer());
    }

    /** Gera uma proposta específica pela escolha do usuário quando ela entra no relógio. */
    public static boolean processDraftPickOffer(League league, Club userClub, DraftPick currentPick) {
        if (league == null || userClub == null || currentPick == null
            || league.getPendingIncomingTradeOffer() != null
            || !"OFFSEASON".equals(league.getCurrentStage())
            || !SeasonCalendar.isDraftOpen(league)
            || league.isDraftFinalized()
            || league.hasGeneratedDraftTradeOffer(currentPick)
            || currentPick.getCurrentOwner() != userClub
            || !currentPick.isAvailableForTrade(league)) {
            return false;
        }

        List<TradeFinderService.Result> possibilities =
            TradeFinderService.findForPick(league, userClub, currentPick);
        if (possibilities.isEmpty()) {
            league.markDraftTradeOfferGenerated(currentPick);
            return false;
        }
        if (!league.markDraftTradeOfferGenerated(currentPick)) return false;

        Random random = new Random(buildWeeklySeed(league, userClub)
            + currentPick.getProjectedOverallPosition() * 997L);
        TradeFinderService.Result selected = possibilities.get(
            random.nextInt(Math.min(3, possibilities.size()))
        );
        return league.queueIncomingTradeOffer(selected.getOffer());
    }

    private static void addBestResults(
        List<TradeFinderService.Result> destination,
        List<TradeFinderService.Result> results
    ) {
        for (int index = 0; index < Math.min(2, results.size()); index++) {
            destination.add(results.get(index));
        }
    }

    private static long buildWeeklySeed(League league, Club userClub) {
        Date date = league.getCurrentDate();
        long day = date != null ? date.getTime() / 86_400_000L : 0L;
        return day * 131L + league.getCurrentSeason() * 10_009L + userClub.getName().hashCode();
    }

    private static boolean incomingOffersAreAvailable(League league) {
        if (!"OFFSEASON".equals(league.getCurrentStage())) return true;
        if (league.getCurrentDate() == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(league.getCurrentDate());
        return league.isDraftFinalized()
            && calendar.get(Calendar.MONTH) == Calendar.DECEMBER
            && calendar.get(Calendar.DAY_OF_MONTH) >= 26;
    }
}
