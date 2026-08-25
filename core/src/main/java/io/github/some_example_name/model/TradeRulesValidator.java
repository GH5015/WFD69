package io.github.some_example_name.model;

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
        return validateRules(offer, -1);
    }

    /** Aplica também as janelas anuais de negociação da WFL. */
    public static ValidationResult validateRules(TradeOffer offer, League league) {
        if (offer != null && league != null && offer.getUserClub() != null && offer.getTargetClub() != null) {
            if (!SeasonCalendar.isTradeWindowOpen(league, offer.getUserClub())) {
                return new ValidationResult(false, SeasonCalendar.getTradeStatus(league, offer.getUserClub()) + ".");
            }
            if (!SeasonCalendar.isTradeWindowOpen(league, offer.getTargetClub())) {
                return new ValidationResult(false, offer.getTargetClub().getName() + " não pode negociar neste momento.");
            }
        }
        return validateRules(offer, league != null ? league.getCurrentSeason() : -1);
    }

    /**
     * @param currentSeasonYear use a season year to also prevent players whose
     *                          contracts have expired from being traded.
     */
    public static ValidationResult validateRules(TradeOffer offer, int currentSeasonYear) {
        if (offer == null || offer.getUserClub() == null || offer.getTargetClub() == null) {
            return new ValidationResult(false, "A proposta precisa ter dois clubes válidos.");
        }
        if (offer.getUserClub() == offer.getTargetClub()) {
            return new ValidationResult(false, "Um clube não pode negociar consigo mesmo.");
        }

        if (offer.getUserAssetCount() == 0 || offer.getTargetAssetCount() == 0) {
            return new ValidationResult(false, "Cada clube deve enviar ao menos um ativo na troca.");
        }
        if (offer.getUserAssetCount() > TradeOffer.MAX_ASSETS_PER_SIDE
            || offer.getTargetAssetCount() > TradeOffer.MAX_ASSETS_PER_SIDE) {
            return new ValidationResult(false, "Cada clube pode incluir no máximo "
                + TradeOffer.MAX_ASSETS_PER_SIDE + " ativos na proposta.");
        }

        // --- 1. VALIDAÇÃO DO TAMANHO DO ELENCO (ROSTER LIMITS) ---
        Club userClub = offer.getUserClub();
        Club targetClub = offer.getTargetClub();

        ValidationResult userAssets = validateOwnership(
            userClub, offer.getUserPlayers(), offer.getUserPicks(), currentSeasonYear
        );
        if (!userAssets.isValid) return userAssets;

        ValidationResult targetAssets = validateOwnership(
            targetClub, offer.getTargetPlayers(), offer.getTargetPicks(), currentSeasonYear
        );
        if (!targetAssets.isValid) return targetAssets;

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

    private static ValidationResult validateOwnership(
        Club club,
        Iterable<Player> players,
        Iterable<DraftPick> picks,
        int currentSeasonYear
    ) {
        for (Player player : players) {
            if (player == null || player.getCurrentClub() != club || !club.getSquad().contains(player)) {
                return new ValidationResult(false, "Há um jogador que não pertence mais ao " + club.getName() + ".");
            }
            if (currentSeasonYear >= 0 && player.isFreeAgent(currentSeasonYear)) {
                return new ValidationResult(false, player.getName() + " está com o contrato expirado e não pode ser negociado.");
            }
            if (player.getTradeBlockedDays() > 0) {
                return new ValidationResult(false, player.getName() + " não pode ser trocado por mais " + player.getTradeBlockedDays() + " dia(s).");
            }
        }

        for (DraftPick pick : picks) {
            if (pick == null || pick.getCurrentOwner() != club || !club.getDraftPicks().contains(pick)) {
                return new ValidationResult(false, "Há uma escolha de draft que não pertence mais ao " + club.getName() + ".");
            }
        }

        return new ValidationResult(true, "Ativos válidos.");
    }
}
