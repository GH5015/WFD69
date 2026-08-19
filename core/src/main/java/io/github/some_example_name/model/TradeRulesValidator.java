package io.github.some_example_name.model;

import java.util.List;

public class TradeRulesValidator {

    public static final int MIN_ROSTER_SIZE = 23;
    public static final int MAX_ROSTER_SIZE = 26;

    public static class ValidationResult {
        public final boolean isValid;
        public final String reason;

        public ValidationResult(boolean isValid, String reason) {
            this.isValid = isValid;
            this.reason = reason;
        }
    }

    /**
     * Valida se a troca cumpre o regulamento do campeonato para ambos os clubes.
     */
    public static ValidationResult validateRules(TradeOffer offer) {
        // --- 1. VALIDAÇÃO DO TAMANHO DO ELENCO (ROSTER LIMITS) ---
        Club userClub = offer.getUserClub();
        Club targetClub = offer.getTargetClub();

        int userPostTradeCount = userClub.getSquad().size()
            - offer.getUserPlayers().size()
            + offer.getTargetPlayers().size();

        int targetPostTradeCount = targetClub.getSquad().size()
            - offer.getTargetPlayers().size()
            + offer.getUserPlayers().size();

        if (userPostTradeCount > MAX_ROSTER_SIZE) {
            return new ValidationResult(false, "Sua equipe ultrapassará o limite máximo de " + MAX_ROSTER_SIZE + " jogadores (" + userPostTradeCount + ").");
        }
        if (userPostTradeCount < MIN_ROSTER_SIZE) {
            return new ValidationResult(false, "Sua equipe ficará abaixo do limite mínimo de " + MIN_ROSTER_SIZE + " jogadores (" + userPostTradeCount + ").");
        }

        if (targetPostTradeCount > MAX_ROSTER_SIZE) {
            return new ValidationResult(false, "O " + targetClub.getName() + " ultrapassaria o limite máximo de " + MAX_ROSTER_SIZE + " jogadores.");
        }
        if (targetPostTradeCount < MIN_ROSTER_SIZE) {
            return new ValidationResult(false, "O " + targetClub.getName() + " ficaria abaixo do limite mínimo de " + MIN_ROSTER_SIZE + " jogadores.");
        }

        // --- 2. VALIDAÇÃO DO TETO SALARIAL (SALARY CAP) ---
        long userPayrollOut = offer.getUserPlayers().stream().mapToLong(Player::getAnnualSalary).sum();
        long userPayrollIn = offer.getTargetPlayers().stream().mapToLong(Player::getAnnualSalary).sum();
        long userProjectedPayroll = userClub.getFinance().getAnnualPayroll() - userPayrollOut + userPayrollIn;

        if (userProjectedPayroll > userClub.getFinance().getSalaryCap()) {
            return new ValidationResult(false, "A troca ultrapassa o Salary Cap da sua franquia!");
        }

        long targetPayrollOut = offer.getTargetPlayers().stream().mapToLong(Player::getAnnualSalary).sum();
        long targetPayrollIn = offer.getUserPlayers().stream().mapToLong(Player::getAnnualSalary).sum();
        long targetProjectedPayroll = targetClub.getFinance().getAnnualPayroll() - targetPayrollOut + targetPayrollIn;

        if (targetProjectedPayroll > targetClub.getFinance().getSalaryCap()) {
            return new ValidationResult(false, "A troca estoura o Salary Cap do " + targetClub.getName() + ".");
        }

        return new ValidationResult(true, "Regras aprovadas.");
    }
}
