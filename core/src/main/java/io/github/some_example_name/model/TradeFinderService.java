package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Procura propostas prontas envolvendo um ativo escolhido pelo usuário.
 * Todas as sugestões passam pelas mesmas regras, cap e avaliação da Central.
 */
public final class TradeFinderService {
    private static final int DEFAULT_LIMIT = 10;

    private TradeFinderService() {
    }

    public static List<Result> findForPlayer(
        League league,
        Club userClub,
        Player player
    ) {
        if (player == null) return new ArrayList<>();
        return find(league, userClub, player, null, DEFAULT_LIMIT);
    }

    public static List<Result> findForPick(
        League league,
        Club userClub,
        DraftPick pick
    ) {
        if (pick == null) return new ArrayList<>();
        return find(league, userClub, null, pick, DEFAULT_LIMIT);
    }

    private static List<Result> find(
        League league,
        Club userClub,
        Player selectedPlayer,
        DraftPick selectedPick,
        int limit
    ) {
        List<Result> matches = new ArrayList<>();
        if (league == null || userClub == null) return matches;
        if (selectedPick != null && !selectedPick.isAvailableForTrade(league)) return matches;

        Club assetOwner = selectedPlayer != null
            ? resolvePlayerOwner(league, selectedPlayer)
            : selectedPick != null ? selectedPick.getCurrentOwner() : null;
        if (assetOwner == null) return matches;

        boolean offeringUserAsset = assetOwner == userClub;
        List<Club> partners = new ArrayList<>();
        if (offeringUserAsset) {
            for (Club club : league.getClubs()) {
                if (club != null && club != userClub) partners.add(club);
            }
        } else {
            partners.add(assetOwner);
        }

        int season = league.getCurrentSeason();
        for (Club partner : partners) {
            if (!SeasonCalendar.isTradeWindowOpen(league, userClub)
                || !SeasonCalendar.isTradeWindowOpen(league, partner)) {
                continue;
            }

            Club packageOwner = offeringUserAsset ? partner : userClub;
            List<Asset> pool = eligibleAssets(packageOwner, league);
            for (int first = 0; first < pool.size(); first++) {
                evaluatePackage(
                    matches, league, userClub, partner, offeringUserAsset,
                    selectedPlayer, selectedPick, pool.get(first), null, null
                );
                for (int second = first + 1; second < pool.size(); second++) {
                    evaluatePackage(
                        matches, league, userClub, partner, offeringUserAsset,
                        selectedPlayer, selectedPick, pool.get(first), pool.get(second), null
                    );
                    // Ao tentar adquirir um ativo da IA, pacotes com três peças
                    // são comuns. Para ativos do usuário mantemos dois por busca
                    // para não multiplicar o custo por todas as 19 franquias.
                    if (!offeringUserAsset) {
                        for (int third = second + 1; third < pool.size(); third++) {
                            evaluatePackage(
                                matches, league, userClub, partner, false,
                                selectedPlayer, selectedPick,
                                pool.get(first), pool.get(second), pool.get(third)
                            );
                        }
                    }
                }
            }
        }

        matches.sort(Comparator
            .comparingDouble(Result::getScore).reversed()
            .thenComparing(Comparator.comparingLong(Result::getUserValueReceived).reversed()));

        List<Result> limited = new ArrayList<>();
        Map<Club, Integer> perClub = new HashMap<>();
        Set<String> signatures = new HashSet<>();
        for (Result result : matches) {
            if (!signatures.add(signature(result.offer))) continue;
            int amount = perClub.containsKey(result.partner) ? perClub.get(result.partner) : 0;
            if (offeringUserAsset && amount >= 2) continue;
            perClub.put(result.partner, amount + 1);
            limited.add(result);
            if (limited.size() >= limit) break;
        }
        return limited;
    }

    private static void evaluatePackage(
        List<Result> matches,
        League league,
        Club userClub,
        Club partner,
        boolean offeringUserAsset,
        Player selectedPlayer,
        DraftPick selectedPick,
        Asset first,
        Asset second,
        Asset third
    ) {
        TradeOffer offer = new TradeOffer(userClub, partner);
        addSelectedAsset(offer, offeringUserAsset, selectedPlayer, selectedPick);
        first.addTo(offer, !offeringUserAsset);
        if (second != null) second.addTo(offer, !offeringUserAsset);
        if (third != null) third.addTo(offer, !offeringUserAsset);

        TradeRulesValidator.ValidationResult rules =
            TradeRulesValidator.validateRules(offer, league);
        if (!rules.isValid) return;

        TradeDecision decision = TradeNegotiator.analyzeProposal(
            offer,
            league.getCurrentSeason()
        );
        if (decision.getStatus() == TradeDecision.Status.CONSIDERED && decision.hasCounterOffer()) {
            TradeOffer counter = decision.getCounterOffer();
            TradeRulesValidator.ValidationResult counterRules =
                TradeRulesValidator.validateRules(counter, league);
            if (!counterRules.isValid) return;
            TradeDecision confirmation = TradeNegotiator.analyzeProposal(
                counter,
                league.getCurrentSeason()
            );
            if (confirmation.getStatus() != TradeDecision.Status.ACCEPTED) return;
            offer = counter;
        } else if (decision.getStatus() != TradeDecision.Status.ACCEPTED) {
            return;
        }

        int season = league.getCurrentSeason();
        long userSends = SmartTradeEvaluator.calculateTotalPerceivedValue(
            userClub, offer.getUserPlayers(), offer.getUserPicks(), season
        );
        long userReceives = SmartTradeEvaluator.calculateTotalPerceivedValue(
            userClub, offer.getTargetPlayers(), offer.getTargetPicks(), season
        );
        double minimumUserReturn = offeringUserAsset ? .82d : .62d;
        if (userSends <= 0L || userReceives < Math.ceil(userSends * minimumUserReturn)) return;

        long partnerReceives = SmartTradeEvaluator.calculateTotalPerceivedValue(
            partner, offer.getUserPlayers(), offer.getUserPicks(), season
        );
        long partnerSends = SmartTradeEvaluator.calculateTotalPerceivedValue(
            partner, offer.getTargetPlayers(), offer.getTargetPicks(), season
        );
        if (partnerSends <= 0L) return;

        double userRatio = (double) userReceives / userSends;
        double partnerRatio = (double) partnerReceives / partnerSends;
        double score = 100d
            - Math.abs(1d - Math.min(1.6d, userRatio)) * 42d
            - Math.abs(1d - Math.min(1.6d, partnerRatio)) * 34d;
        score += Math.min(8d, Math.max(0d, (userRatio - 1d) * 20d));

        matches.add(new Result(
            offer,
            partner,
            Math.max(0d, Math.min(100d, score)),
            userSends,
            userReceives,
            partnerReceives,
            partnerSends
        ));
    }

    private static void addSelectedAsset(
        TradeOffer offer,
        boolean userSide,
        Player player,
        DraftPick pick
    ) {
        if (player != null) {
            if (userSide) offer.addPlayerToGive(player);
            else offer.addPlayerToReceive(player);
        } else if (pick != null) {
            if (userSide) offer.addPickToGive(pick);
            else offer.addPickToReceive(pick);
        }
    }

    private static List<Asset> eligibleAssets(Club owner, League league) {
        int season = league.getCurrentSeason();
        List<Asset> assets = new ArrayList<>();
        if (owner == null) return assets;
        for (Player player : owner.getSquad()) {
            if (player == null
                || player.getCurrentClub() != owner
                || player.isFreeAgent(season)
                || player.getTradeBlockedDays() > 0) {
                continue;
            }
            assets.add(new Asset(player, null));
        }
        for (DraftPick pick : owner.getDraftPicks()) {
            if (pick != null && pick.getCurrentOwner() == owner && pick.isAvailableForTrade(league)) {
                assets.add(new Asset(null, pick));
            }
        }
        return assets;
    }

    /**
     * Saves antigos podem conter o atleta no elenco correto, mas sem o vínculo
     * currentClub restaurado. O elenco é a fonte de verdade nesse caso; sem
     * esta reconciliação o Finder encerra a busca antes de avaliar propostas.
     */
    private static Club resolvePlayerOwner(League league, Player player) {
        if (player == null) return null;
        Club linked = player.getCurrentClub();
        if (linked != null && linked.getSquad().contains(player)) return linked;
        for (Club club : league.getClubs()) {
            if (club != null && club.getSquad().contains(player)) {
                player.setCurrentClub(club);
                return club;
            }
        }
        return null;
    }

    private static String signature(TradeOffer offer) {
        List<String> parts = new ArrayList<>();
        for (Player player : offer.getUserPlayers()) parts.add("UP:" + player.getId());
        for (DraftPick pick : offer.getUserPicks()) parts.add("UD:" + pickKey(pick));
        for (Player player : offer.getTargetPlayers()) parts.add("TP:" + player.getId());
        for (DraftPick pick : offer.getTargetPicks()) parts.add("TD:" + pickKey(pick));
        parts.sort(String::compareTo);
        return String.join("|", parts);
    }

    private static String pickKey(DraftPick pick) {
        String original = pick.getOriginalOwner() != null
            ? pick.getOriginalOwner().getName()
            : "?";
        return pick.getYear() + ":" + pick.getRound() + ":" + original;
    }

    private static final class Asset {
        private final Player player;
        private final DraftPick pick;

        private Asset(Player player, DraftPick pick) {
            this.player = player;
            this.pick = pick;
        }

        private void addTo(TradeOffer offer, boolean userSide) {
            if (player != null) {
                if (userSide) offer.addPlayerToGive(player);
                else offer.addPlayerToReceive(player);
            } else if (pick != null) {
                if (userSide) offer.addPickToGive(pick);
                else offer.addPickToReceive(pick);
            }
        }
    }

    public static final class Result {
        private final TradeOffer offer;
        private final Club partner;
        private final double score;
        private final long userValueSent;
        private final long userValueReceived;
        private final long partnerValueReceived;
        private final long partnerValueSent;

        private Result(
            TradeOffer offer,
            Club partner,
            double score,
            long userValueSent,
            long userValueReceived,
            long partnerValueReceived,
            long partnerValueSent
        ) {
            this.offer = offer;
            this.partner = partner;
            this.score = score;
            this.userValueSent = userValueSent;
            this.userValueReceived = userValueReceived;
            this.partnerValueReceived = partnerValueReceived;
            this.partnerValueSent = partnerValueSent;
        }

        public TradeOffer getOffer() { return offer; }
        public Club getPartner() { return partner; }
        public double getScore() { return score; }
        public long getUserValueSent() { return userValueSent; }
        public long getUserValueReceived() { return userValueReceived; }
        public long getPartnerValueReceived() { return partnerValueReceived; }
        public long getPartnerValueSent() { return partnerValueSent; }

        public String getBalanceLabel() {
            double ratio = userValueSent <= 0L ? 0d : (double) userValueReceived / userValueSent;
            if (ratio >= 1.08d) return "VANTAJOSA";
            if (ratio >= 0.94d) return "EQUILIBRADA";
            return "CUSTOSA";
        }
    }
}
