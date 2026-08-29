package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SmartTradeEvaluator {

    /**
     * Calcula o valor real percebido por um clube específico ao receber um jogador.
     */
    public static long getPerceivedPlayerValue(Club receivingClub, Player player) {
        long baseTradeValue = TradeValueCalculator.calculateMarketPoints(player);
        return getPerceivedPlayerValue(receivingClub, player, baseTradeValue, -1);
    }

    public static long getPerceivedPlayerValue(Club receivingClub, Player player, int currentSeasonYear) {
        long baseTradeValue = TradeValueCalculator.calculateMarketPoints(player, currentSeasonYear);
        return getPerceivedPlayerValue(receivingClub, player, baseTradeValue, currentSeasonYear);
    }

    private static long getPerceivedPlayerValue(Club receivingClub, Player player, long baseTradeValue, int currentSeasonYear) {
        Map<String, Integer> positionNeeds = ClubNeedEvaluator.calculatePositionNeeds(receivingClub);
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(receivingClub);

        boolean currentClubIsEvaluatingItsOwnPlayer = player.getCurrentClub() == receivingClub;
        int needStars = currentClubIsEvaluatingItsOwnPlayer
            ? ClubNeedEvaluator.calculateNeedAfterRemoving(receivingClub, player)
            : positionNeeds.getOrDefault(player.getPosition(), 3);

        // 1. Multiplicador de Carência de Posição
        double needMultiplier = 1.0;
        switch (needStars) {
            case 5: needMultiplier = currentClubIsEvaluatingItsOwnPlayer ? 1.28 : 1.30; break;
            case 4: needMultiplier = currentClubIsEvaluatingItsOwnPlayer ? 1.16 : 1.15; break;
            case 3: needMultiplier = currentClubIsEvaluatingItsOwnPlayer ? 1.05 : 1.00; break;
            case 2: needMultiplier = currentClubIsEvaluatingItsOwnPlayer ? 1.00 : 0.82; break;
            case 1: needMultiplier = currentClubIsEvaluatingItsOwnPlayer ? 0.95 : 0.65; break;
        }

        // 2. Multiplicador do Momento da Franquia
        double phaseMultiplier = 1.0;
        switch (phase) {
            case CONTENDER:
                if (player.getOverall() >= 88 && player.getAge() <= 32) phaseMultiplier = 1.30;
                else if (player.getOverall() >= 84) phaseMultiplier = 1.18;
                else if (player.getAge() <= 21 && player.getOverall() < 76) phaseMultiplier = 0.72;
                break;

            case BUYER:
                if (player.getOverall() >= 84 && player.getAge() <= 29) phaseMultiplier = 1.20;
                else if (player.getOverall() >= 80 && player.getAge() <= 27) phaseMultiplier = 1.12;
                else if (player.getAge() >= 32) phaseMultiplier = 0.80;
                break;

            case SELLER:
                if (player.getAge() <= 23 && player.getTruePotential() - player.getOverall() >= 6) phaseMultiplier = 1.28;
                else if (player.getAge() >= 31) phaseMultiplier = 0.86;
                break;

            case REBUILDING:
                if (player.getAge() <= 21 && player.getTruePotential() >= 86) phaseMultiplier = 1.55;
                else if (player.getAge() <= 23 || player.getTruePotential() >= 88) phaseMultiplier = 1.34;
                else if (player.getAge() >= 31) phaseMultiplier = 0.68;
                else if (player.getAge() >= 29) phaseMultiplier = 0.80;
                break;
        }

        // 3. Fator Reputação do Clube vs Atração de Superstars
        if (receivingClub.getReputation() < 60 && player.getOverall() >= 85) {
            phaseMultiplier *= 1.10;
        }

        // 4. Fator Custo Salarial (Calculado diretamente a partir do elenco)
        if (receivingClub.getSquad() != null && !receivingClub.getSquad().isEmpty()) {
            double avgSalary = receivingClub.getSquad().stream()
                .mapToLong(Player::getAnnualSalary)
                .average()
                .orElse(0.0);

            if (avgSalary > 0 && player.getAnnualSalary() > (avgSalary * 1.8)) {
                phaseMultiplier *= 0.90;
            }
        }

        if (currentSeasonYear >= 0) {
            int remaining = player.getRemainingContractYears(currentSeasonYear);
            if (remaining == 0) phaseMultiplier *= 0.85;
            else if (remaining == 1) phaseMultiplier *= 0.92;
            else if (remaining >= 4) phaseMultiplier *= 1.04;
        }

        if (currentClubIsEvaluatingItsOwnPlayer) {
            phaseMultiplier *= TradeRosterImpactEvaluator.getRetentionPremium(receivingClub, player);
        }

        if (player.getOverall() >= 92) phaseMultiplier *= 1.12d;

        return Math.round(baseTradeValue * needMultiplier * phaseMultiplier);
    }

    /**
     * Calcula o valor percebido total de um pacote completo (Jogadores + Picks).
     */
    public static long calculateTotalPerceivedValue(Club evaluatingClub, List<Player> players, List<DraftPick> picks, int currentSeasonYear) {
        List<Long> playerValues = new ArrayList<>();
        for (Player player : players) {
            playerValues.add(getPerceivedPlayerValue(evaluatingClub, player, currentSeasonYear));
        }
        playerValues.sort(Comparator.reverseOrder());

        /* Vários reservas não substituem uma estrela e ainda consomem vagas
         * de elenco. Cada jogador adicional sofre retorno decrescente. */
        double[] playerWeights = {1d, 0.68d, 0.50d, 0.38d, 0.30d};
        long playerVal = 0L;
        for (int i = 0; i < playerValues.size(); i++) {
            playerVal += Math.round(playerValues.get(i) * playerWeights[Math.min(i, playerWeights.length - 1)]);
        }

        List<Long> pickValues = new ArrayList<>();
        for (DraftPick pick : picks) {
            pickValues.add(DraftPickEvaluator.getPerceivedPickValue(evaluatingClub, pick, currentSeasonYear));
        }
        pickValues.sort(Comparator.reverseOrder());
        double[] pickWeights = {1d, 0.88d, 0.78d, 0.70d, 0.64d};
        long pickVal = 0L;
        for (int i = 0; i < pickValues.size(); i++) {
            pickVal += Math.round(pickValues.get(i) * pickWeights[Math.min(i, pickWeights.length - 1)]);
        }
        return playerVal + pickVal;
    }

    /**
     * Decisão Final da IA sobre aceitar ou recusar a proposta
     */
    public static TradeResult evaluateContextualOffer(TradeOffer offer) {
        TradeRulesValidator.ValidationResult ruleCheck = TradeRulesValidator.validateRules(offer);
        if (!ruleCheck.isValid) {
            return new TradeResult(false, "A proposta não atende ao regulamento: " + ruleCheck.reason);
        }

        Club targetClub = offer.getTargetClub();

        long valueTargetReceives = calculateTotalPerceivedValue(
            targetClub, offer.getUserPlayers(), offer.getUserPicks(), -1
        );

        long valueTargetGivesUp = calculateTotalPerceivedValue(
            targetClub, offer.getTargetPlayers(), offer.getTargetPicks(), -1
        );

        long newPayroll = targetClub.getFinance().getAnnualPayroll()
            - offer.getTargetPlayers().stream().mapToLong(Player::getAnnualSalary).sum()
            + offer.getUserPlayers().stream().mapToLong(Player::getAnnualSalary).sum();

        if (newPayroll > targetClub.getFinance().getSalaryCap()) {
            return new TradeResult(false, "A IA recusou: O negócio violaria o Salary Cap do " + targetClub.getName() + ".");
        }

        if (valueTargetReceives >= valueTargetGivesUp) {
            return new TradeResult(true, "A proposta foi aceita! O " + targetClub.getName() + " considerou o negócio estratégico para o elenco.");
        } else {
            return new TradeResult(false, "Proposta recusada. O " + targetClub.getName() + " não vê sentido em acumular esses ativos na situação atual do elenco.");
        }
    }

    /**
     * Calcula a pontuação de interesse do clube alvo na proposta (0 a 100).
     */
    public static int calculateInterestScore(TradeOffer offer) {
        return calculateInterestScore(offer, -1);
    }

    public static int calculateInterestScore(TradeOffer offer, int currentSeasonYear) {
        Club targetClub = offer.getTargetClub();

        long valueReceived = calculateTotalPerceivedValue(
            targetClub, offer.getUserPlayers(), offer.getUserPicks(), currentSeasonYear
        );

        long valueGivenUp = calculateTotalPerceivedValue(
            targetClub, offer.getTargetPlayers(), offer.getTargetPicks(), currentSeasonYear
        );

        if (valueGivenUp == 0) {
            // Até que os dois lados tenham ativos, não existe uma proposta
            // completa para a IA demonstrar interesse real.
            return 0;
        }

        double ratio = (double) valueReceived / (double) valueGivenUp;
        int score = (int) Math.round(ratio * 70.0);
        return Math.min(100, Math.max(0, score));
    }

    public static class TradeResult {
        public final boolean accepted;
        public final String message;

        public TradeResult(boolean accepted, String message) {
            this.accepted = accepted;
            this.message = message;
        }
    }
}
