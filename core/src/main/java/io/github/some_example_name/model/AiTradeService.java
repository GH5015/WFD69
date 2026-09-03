package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Mercado automático entre os clubes controlados pela IA.
 *
 * A rotina é chamada semanalmente e executa no máximo uma troca. Ela usa as
 * mesmas regras, teto salarial e avaliação contextual da central de trocas.
 */
public final class AiTradeService {

    private static final double WEEKLY_TRADE_CHANCE = 0.28d;

    private AiTradeService() {
    }

    public static boolean processWeeklyTrade(League league, Club userClub) {
        if (league == null || league.getClubs() == null || league.getClubs().size() < 3) {
            return false;
        }

        List<Club> aiClubs = new ArrayList<>();
        for (Club club : league.getClubs()) {
            if (club != null && club != userClub && SeasonCalendar.isTradeWindowOpen(league, club)) {
                aiClubs.add(club);
            }
        }
        if (aiClubs.size() < 2) return false;

        Random random = new Random(buildWeeklySeed(league));
        if (random.nextDouble() > WEEKLY_TRADE_CHANCE) return false;

        Collections.shuffle(aiClubs, random);
        for (Club buyer : aiClubs) {
            for (Club seller : aiClubs) {
                if (buyer == seller) continue;
                TradeOffer offer = buildOffer(buyer, seller, league, random);
                if (offer == null) continue;

                TradeRulesValidator.ValidationResult rules = TradeRulesValidator.validateRules(offer, league);
                if (!rules.isValid) continue;

                TradeDecision decision = TradeNegotiator.analyzeProposal(offer, league.getCurrentSeason());
                if (decision.getStatus() != TradeDecision.Status.ACCEPTED) continue;

                league.recordTrade(TradeRecord.fromOffer(offer, league));
                executeTrade(offer);
                refreshBestLineup(buyer);
                refreshBestLineup(seller);
                return true;
            }
        }
        return false;
    }

    private static TradeOffer buildOffer(Club buyer, Club seller, League league, Random random) {
        int season = league.getCurrentSeason();
        String neededPosition = getHighestNeedPosition(buyer, seller);
        Player player = findTradeablePlayer(seller, neededPosition, season, random);
        if (player == null) return null;

        long sellerPlayerValue = SmartTradeEvaluator.getPerceivedPlayerValue(seller, player, season);
        List<DraftPick> picks = new ArrayList<>(buyer.getDraftPicks());
        picks.removeIf(pick -> pick == null || !pick.isAvailableForTrade(league));
        picks.sort(Comparator.<DraftPick>comparingLong(
            pick -> DraftPickEvaluator.getPerceivedPickValue(seller, pick, season)
        ).reversed());

        // Uma pick por jogador funciona quando ambos os elencos têm espaço.
        if (buyer.getSquad().size() < TradeRulesValidator.MAX_ROSTER_SIZE
            && seller.getSquad().size() > TradeRulesValidator.MIN_ROSTER_SIZE) {
            for (DraftPick pick : picks) {
                if (pick.getCurrentOwner() != buyer) continue;

                long sellerPickValue = DraftPickEvaluator.getPerceivedPickValue(seller, pick, season);
                long buyerPickValue = DraftPickEvaluator.getPerceivedPickValue(buyer, pick, season);
                long buyerPlayerValue = SmartTradeEvaluator.getPerceivedPlayerValue(buyer, player, season);

                if (sellerPickValue < Math.ceil(sellerPlayerValue * 0.98d)
                    || buyerPlayerValue < Math.ceil(buyerPickValue * 0.82d)) {
                    continue;
                }

                TradeOffer offer = new TradeOffer(buyer, seller);
                offer.addPickToGive(pick);
                offer.addPlayerToReceive(player);
                return offer;
            }
        }

        // Elencos no mínimo/máximo recebem um jogador de volta. Isso mantém
        // as 23–26 vagas legais e permite que a IA negocie mesmo em ligas com
        // todos os clubes no tamanho mínimo de elenco.
        Player outgoingPlayer = findTradeablePlayer(
            buyer, getHighestNeedPosition(seller, buyer), season, random
        );
        if (outgoingPlayer == null) return null;

        TradeOffer playerSwap = new TradeOffer(buyer, seller);
        playerSwap.addPlayerToGive(outgoingPlayer);
        playerSwap.addPlayerToReceive(player);
        if (isFairForBothClubs(playerSwap, buyer, seller, season)) return playerSwap;

        for (DraftPick pick : picks) {
            if (pick.getCurrentOwner() != buyer) continue;
            playerSwap.addPickToGive(pick);
            if (isFairForBothClubs(playerSwap, buyer, seller, season)) return playerSwap;
            playerSwap.removePickToGive(pick);
        }
        return null;
    }

    private static boolean isFairForBothClubs(TradeOffer offer, Club buyer, Club seller, int season) {
        long sellerReceives = SmartTradeEvaluator.calculateTotalPerceivedValue(
            seller, offer.getUserPlayers(), offer.getUserPicks(), season
        );
        long sellerSends = SmartTradeEvaluator.calculateTotalPerceivedValue(
            seller, offer.getTargetPlayers(), offer.getTargetPicks(), season
        );
        long buyerReceives = SmartTradeEvaluator.calculateTotalPerceivedValue(
            buyer, offer.getTargetPlayers(), offer.getTargetPicks(), season
        );
        long buyerSends = SmartTradeEvaluator.calculateTotalPerceivedValue(
            buyer, offer.getUserPlayers(), offer.getUserPicks(), season
        );

        return sellerReceives >= Math.ceil(sellerSends * 0.98d)
            && buyerReceives >= Math.ceil(buyerSends * 0.82d);
    }

    private static String getHighestNeedPosition(Club clubWithNeed, Club supplyingClub) {
        Map<String, Integer> needs = ClubNeedEvaluator.calculatePositionNeeds(clubWithNeed);
        String bestPosition = "CM";
        int bestNeed = Integer.MIN_VALUE;
        for (Player player : supplyingClub.getSquad()) {
            int positionNeed = needs.getOrDefault(player.getPosition(), 3);
            if (positionNeed > bestNeed) {
                bestNeed = positionNeed;
                bestPosition = player.getPosition();
            }
        }
        return bestPosition;
    }

    private static Player findTradeablePlayer(Club seller, String neededPosition, int season, Random random) {
        Map<String, Integer> sellerNeeds = ClubNeedEvaluator.calculatePositionNeeds(seller);
        List<Player> candidates = new ArrayList<>();
        for (Player player : seller.getSquad()) {
            if (player.isFreeAgent(season) || !player.canPlay() || player.getOverall() < 72) continue;
            if (neededPosition.equals(player.getPosition())) candidates.add(player);
        }
        if (candidates.isEmpty()) return null;

        candidates.sort(Comparator
            .comparingInt((Player player) -> sellerNeeds.getOrDefault(player.getPosition(), 3))
            .thenComparing(Comparator.comparingInt(Player::getOverall).reversed()));

        // Evita que a IA venda sempre sua estrela: considera os três melhores
        // ativos negociáveis da posição e escolhe um deles de modo determinístico.
        return candidates.get(random.nextInt(Math.min(3, candidates.size())));
    }

    private static void executeTrade(TradeOffer offer) {
        Club buyer = offer.getUserClub();
        Club seller = offer.getTargetClub();

        for (Player player : new ArrayList<>(offer.getTargetPlayers())) {
            player.transferTo(buyer);
        }
        for (DraftPick pick : new ArrayList<>(offer.getUserPicks())) {
            buyer.getDraftPicks().remove(pick);
            if (!seller.getDraftPicks().contains(pick)) seller.getDraftPicks().add(pick);
            pick.setCurrentOwner(seller);
        }
    }

    private static void refreshBestLineup(Club club) {
        if (club == null || club.getSquad().isEmpty()) return;
        if (club.getFormation() == null) club.setFormation(Formation.F_433);

        club.getTacticsMap().clear();
        club.getStartingXI().clear();
        Set<Player> used = new HashSet<>();
        List<String> slots = club.getFormation().getPositionSlots();

        for (int index = 0; index < Math.min(11, slots.size()); index++) {
            String position = slots.get(index);
            Player best = null;
            int bestEffectiveOverall = -1;
            for (Player player : club.getSquad()) {
                if (used.contains(player) || !player.canPlay()) continue;
                int effectiveOverall = player.getEffectiveOverallForPosition(position);
                if (effectiveOverall > bestEffectiveOverall) {
                    bestEffectiveOverall = effectiveOverall;
                    best = player;
                }
            }
            if (best != null) {
                club.assignPlayerToSlot(index, best);
                used.add(best);
            }
        }
    }

    private static long buildWeeklySeed(League league) {
        Date date = league.getCurrentDate();
        long dateValue = date != null ? date.getTime() / 86_400_000L : 0L;
        return (league.getCurrentSeason() * 10_007L) ^ (dateValue * 31L) ^ league.getCurrentRound();
    }
}
