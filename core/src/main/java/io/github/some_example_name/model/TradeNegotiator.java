package io.github.some_example_name.model;

import java.util.List;

public class TradeNegotiator {

    public static TradeDecision analyzeProposal(TradeOffer offer) {
        return analyzeProposal(offer, 2026);
    }

    public static TradeDecision analyzeProposal(TradeOffer offer, int currentSeasonYear) {
        // Valida a proposta antes de avaliá-la: uma oferta vazia, com ativo
        // duplicado/transferido ou contrato expirado nunca deve chegar à IA.
        TradeRulesValidator.ValidationResult ruleCheck = TradeRulesValidator.validateRules(offer, currentSeasonYear);
        if (!ruleCheck.isValid) {
            return new TradeDecision(
                TradeDecision.Status.REJECTED,
                "VIOLAÇÃO DO REGULAMENTO: " + ruleCheck.reason,
                0, 0, null
            );
        }

        Club targetClub = offer.getTargetClub();
        Club userClub = offer.getUserClub();

        // 1. Reutiliza o método utilitário centralizado
        long valueTargetReceives = SmartTradeEvaluator.calculateTotalPerceivedValue(
            targetClub, offer.getUserPlayers(), offer.getUserPicks(), currentSeasonYear
        );

        long valueTargetGivesUp = SmartTradeEvaluator.calculateTotalPerceivedValue(
            targetClub, offer.getTargetPlayers(), offer.getTargetPicks(), currentSeasonYear
        );

        Player outgoingCornerstone = offer.getTargetPlayers().stream()
            .max((first, second) -> Integer.compare(first.getOverall(), second.getOverall()))
            .orElse(null);

        if (violatesCornerstoneProtection(offer, outgoingCornerstone)) {
            return new TradeDecision(
                TradeDecision.Status.REJECTED,
                "Não trocaremos um jogador geracional por um pacote de atletas de rotação. "
                    + "A proposta precisa incluir outra estrela ou uma escolha premium de 1ª rodada.",
                valueTargetReceives, valueTargetGivesUp, null
            );
        }

        double requiredRatio = requiredAcceptanceRatio(targetClub, outgoingCornerstone);

        // O soft cap pode ser ultrapassado. O Hard Cap é absoluto e a
        // Luxury Tax aumenta o preço exigido pela IA.
        long newPayroll = targetClub.getFinance().getAnnualPayroll()
            - offer.getTargetPlayers().stream().mapToLong(Player::getAnnualSalary).sum()
            + offer.getUserPlayers().stream().mapToLong(Player::getAnnualSalary).sum();

        if (!targetClub.getFinance().isWithinHardCap(newPayroll)) {
            return new TradeDecision(
                TradeDecision.Status.REJECTED,
                "Não podemos aceitar este negócio. A transação ultrapassaria o nosso Hard Cap.",
                valueTargetReceives, valueTargetGivesUp, null
            );
        }

        if (newPayroll > targetClub.getFinance().getLuxuryTaxThreshold()) {
            ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(targetClub);
            requiredRatio += phase == ClubNeedEvaluator.TeamPhase.CONTENDER ? 0.04d
                : phase == ClubNeedEvaluator.TeamPhase.BUYER ? 0.08d : 0.14d;
        }

        // 2. Aceite Direto
        if (valueTargetReceives >= (valueTargetGivesUp * requiredRatio)) {
            return new TradeDecision(
                TradeDecision.Status.ACCEPTED,
                "A proposta atende perfeitamente aos nossos objetivos estratégicos. Negócio fechado!",
                valueTargetReceives, valueTargetGivesUp, null
            );
        }

        // 3. Contraoferta
        if (valueTargetReceives >= (valueTargetGivesUp * 0.65)) {
            long minimumValueForAcceptance = (long) Math.ceil(valueTargetGivesUp * requiredRatio);
            long marginNeeded = Math.max(1L, minimumValueForAcceptance - valueTargetReceives);
            TradeOffer counterOffer = buildCounterOffer(offer, userClub, marginNeeded, currentSeasonYear);

            if (counterOffer == null) {
                return new TradeDecision(
                    TradeDecision.Status.REJECTED,
                    "A proposta tem algum interesse, mas não há um ativo elegível que complete o valor sem exceder o limite da negociação.",
                    valueTargetReceives, valueTargetGivesUp, null
                );
            }

            String feedback = "Estamos interessados nos ativos oferecidos, mas consideramos a proposta insuficiente " +
                "para compensar a perda dos nossos atletas. Podemos fechar se você incluir mais peças.";

            return new TradeDecision(
                TradeDecision.Status.CONSIDERED,
                feedback, valueTargetReceives, valueTargetGivesUp, counterOffer
            );
        }

        // 4. Rejeição
        return new TradeDecision(
            TradeDecision.Status.REJECTED,
            "A proposta está muito distante do valor de mercado dos nossos jogadores. Não temos interesse na negociação nesses moldes.",
            valueTargetReceives, valueTargetGivesUp, null
        );
    }

    private static boolean violatesCornerstoneProtection(TradeOffer offer, Player cornerstone) {
        if (cornerstone == null || (cornerstone.getOverall() < 90
            && !TradeRosterImpactEvaluator.isUntouchable(offer.getTargetClub(), cornerstone))) return false;

        int bestIncomingOverall = offer.getUserPlayers().stream()
            .mapToInt(Player::getOverall)
            .max()
            .orElse(0);
        boolean includesPremiumFirstRoundPick = offer.getUserPicks().stream()
            .anyMatch(pick -> pick.getRound() == 1 && pick.getProjectedPosition() <= 8);

        return bestIncomingOverall <= cornerstone.getOverall() - 7
            && !includesPremiumFirstRoundPick;
    }

    private static double requiredAcceptanceRatio(Club targetClub, Player cornerstone) {
        if (cornerstone == null) return 1.00d;
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(targetClub);
        if (cornerstone.getOverall() >= 92) {
            return phase == ClubNeedEvaluator.TeamPhase.REBUILDING && cornerstone.getAge() >= 31
                ? 1.05d
                : 1.12d;
        }
        if (cornerstone.getOverall() >= 89) return 1.06d;
        return 1.00d;
    }

    private static TradeOffer buildCounterOffer(TradeOffer originalOffer, Club userClub, long marginNeeded, int currentSeasonYear) {
        TradeOffer counter = new TradeOffer(userClub, originalOffer.getTargetClub());

        originalOffer.getUserPlayers().forEach(counter::addPlayerToGive);
        originalOffer.getUserPicks().forEach(counter::addPickToGive);
        originalOffer.getTargetPlayers().forEach(counter::addPlayerToReceive);
        originalOffer.getTargetPicks().forEach(counter::addPickToReceive);

        if (counter.getUserAssetCount() >= TradeOffer.MAX_ASSETS_PER_SIDE) {
            return null;
        }

        // Busca Pick do usuário
        List<DraftPick> availablePicks = userClub.getDraftPicks();
        for (DraftPick pick : availablePicks) {
            if (pick != null && pick.isAvailableForTrade(currentSeasonYear) && !counter.getUserPicks().contains(pick)) {
                long pickVal = DraftPickEvaluator.getPerceivedPickValue(originalOffer.getTargetClub(), pick, currentSeasonYear);
                if (pickVal >= marginNeeded) {
                    counter.addPickToGive(pick);
                    if (TradeRulesValidator.validateRules(counter, currentSeasonYear).isValid) {
                        return counter;
                    }
                    counter.removePickToGive(pick);
                }
            }
        }

        // Se não encontrar Pick, busca jogador
        for (Player p : userClub.getSquad()) {
            if (!counter.getUserPlayers().contains(p)) {
                long pVal = SmartTradeEvaluator.getPerceivedPlayerValue(originalOffer.getTargetClub(), p, currentSeasonYear);
                if (!p.isFreeAgent(currentSeasonYear)
                    && pVal >= marginNeeded && pVal <= (marginNeeded * 1.4)) {
                    counter.addPlayerToGive(p);
                    if (TradeRulesValidator.validateRules(counter, currentSeasonYear).isValid) {
                        return counter;
                    }
                    counter.removePlayerToGive(p);
                }
            }
        }

        return null;
    }
}
