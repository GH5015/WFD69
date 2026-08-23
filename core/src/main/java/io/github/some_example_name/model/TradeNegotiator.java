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

        // Checagem de Salary Cap
        long newPayroll = targetClub.getFinance().getAnnualPayroll()
            - offer.getTargetPlayers().stream().mapToLong(Player::getAnnualSalary).sum()
            + offer.getUserPlayers().stream().mapToLong(Player::getAnnualSalary).sum();

        if (newPayroll > targetClub.getFinance().getSalaryCap()) {
            return new TradeDecision(
                TradeDecision.Status.REJECTED,
                "Não podemos aceitar este negócio. A transação ultrapassaria o nosso teto salarial (Salary Cap).",
                valueTargetReceives, valueTargetGivesUp, null
            );
        }

        // 2. Aceite Direto
        if (valueTargetReceives >= (valueTargetGivesUp * 0.98)) {
            return new TradeDecision(
                TradeDecision.Status.ACCEPTED,
                "A proposta atende perfeitamente aos nossos objetivos estratégicos. Negócio fechado!",
                valueTargetReceives, valueTargetGivesUp, null
            );
        }

        // 3. Contraoferta
        if (valueTargetReceives >= (valueTargetGivesUp * 0.70)) {
            long minimumValueForAcceptance = (long) Math.ceil(valueTargetGivesUp * 0.98d);
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
            if (!counter.getUserPicks().contains(pick)) {
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
