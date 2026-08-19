package io.github.some_example_name.model;

import java.util.List;
import java.util.Map;

public class SmartTradeEvaluator {

    /**
     * Calcula o valor real percebido por um clube específico ao receber um jogador.
     */
    public static long getPerceivedPlayerValue(Club receivingClub, Player player) {
        long baseTradeValue = TradeValueCalculator.calculateTradeValue(player);
        Map<String, Integer> positionNeeds = ClubNeedEvaluator.calculatePositionNeeds(receivingClub);
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(receivingClub);

        int needStars = positionNeeds.getOrDefault(player.getPosition(), 3);

        // 1. Multiplicador de Carência de Posição
        double needMultiplier = 1.0;
        switch (needStars) {
            case 5: needMultiplier = 1.60; break;
            case 4: needMultiplier = 1.30; break;
            case 3: needMultiplier = 1.00; break;
            case 2: needMultiplier = 0.70; break;
            case 1: needMultiplier = 0.35; break;
        }

        // 2. Multiplicador do Momento da Franquia
        double phaseMultiplier = 1.0;
        switch (phase) {
            case CONTENDER:
                if (player.getOverall() >= 84) phaseMultiplier = 1.35;
                else if (player.getAge() <= 20) phaseMultiplier = 0.70;
                break;

            case BUYER:
                if (player.getOverall() >= 80 && player.getAge() <= 27) phaseMultiplier = 1.25;
                else if (player.getAge() >= 32) phaseMultiplier = 0.75;
                break;

            case SELLER:
                if (player.getPotential() - player.getOverall() >= 6) phaseMultiplier = 1.15;
                break;

            case REBUILDING:
                if (player.getAge() <= 21 || player.getPotential() >= 86) phaseMultiplier = 1.45;
                else if (player.getAge() >= 29) phaseMultiplier = 0.45;
                break;
        }

        // 3. Fator Reputação do Clube vs Atração de Superstars
        if (receivingClub.getReputation() < 60 && player.getOverall() >= 85) {
            phaseMultiplier *= 1.25;
        }

        // 4. Fator Custo Salarial (Calculado diretamente a partir do elenco)
        if (receivingClub.getSquad() != null && !receivingClub.getSquad().isEmpty()) {
            double avgSalary = receivingClub.getSquad().stream()
                .mapToLong(Player::getAnnualSalary)
                .average()
                .orElse(0.0);

            if (avgSalary > 0 && player.getAnnualSalary() > (avgSalary * 1.8)) {
                phaseMultiplier *= 0.75;
            }
        }

        return Math.round(baseTradeValue * needMultiplier * phaseMultiplier);
    }

    /**
     * Calcula o valor percebido total de um pacote completo (Jogadores + Picks).
     */
    public static long calculateTotalPerceivedValue(Club evaluatingClub, List<Player> players, List<DraftPick> picks, int currentSeasonYear) {
        long playerVal = players.stream().mapToLong(p -> getPerceivedPlayerValue(evaluatingClub, p)).sum();
        long pickVal = picks.stream().mapToLong(p -> DraftPickEvaluator.getPerceivedPickValue(evaluatingClub, p, currentSeasonYear)).sum();
        return playerVal + pickVal;
    }

    /**
     * Decisão Final da IA sobre aceitar ou recusar a proposta
     */
    public static TradeResult evaluateContextualOffer(TradeOffer offer) {
        Club targetClub = offer.getTargetClub();

        long valueTargetReceives = offer.getUserPlayers().stream()
            .mapToLong(p -> getPerceivedPlayerValue(targetClub, p)).sum();

        long valueTargetGivesUp = offer.getTargetPlayers().stream()
            .mapToLong(p -> getPerceivedPlayerValue(targetClub, p)).sum();

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
        Club targetClub = offer.getTargetClub();

        long valueReceived = offer.getUserPlayers().stream()
            .mapToLong(p -> getPerceivedPlayerValue(targetClub, p)).sum();

        long valueGivenUp = offer.getTargetPlayers().stream()
            .mapToLong(p -> getPerceivedPlayerValue(targetClub, p)).sum();

        if (valueGivenUp == 0) {
            return valueReceived > 0 ? 100 : 0;
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
