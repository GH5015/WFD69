package io.github.some_example_name.model;

import java.util.ArrayList;

/** Execução compartilhada por propostas enviadas e recebidas. */
public final class TradeExecutionService {
    private TradeExecutionService() { }

    public static boolean execute(League league, TradeOffer offer) {
        TradeRulesValidator.ValidationResult validation =
            TradeRulesValidator.validateRules(offer, league);
        if (!validation.isValid) return false;

        Club userClub = offer.getUserClub();
        Club targetClub = offer.getTargetClub();
        league.recordTrade(TradeRecord.fromOffer(offer, league));

        for (Player player : new ArrayList<>(offer.getUserPlayers())) {
            player.transferTo(targetClub);
        }
        for (Player player : new ArrayList<>(offer.getTargetPlayers())) {
            player.transferTo(userClub);
        }
        for (DraftPick pick : new ArrayList<>(offer.getUserPicks())) {
            transferPick(pick, userClub, targetClub);
        }
        for (DraftPick pick : new ArrayList<>(offer.getTargetPicks())) {
            transferPick(pick, targetClub, userClub);
        }

        userClub.autoSelectBestFormationAndXI();
        targetClub.autoSelectBestFormationAndXI();
        return true;
    }

    private static void transferPick(DraftPick pick, Club from, Club to) {
        from.getDraftPicks().remove(pick);
        if (!to.getDraftPicks().contains(pick)) to.getDraftPicks().add(pick);
        pick.setCurrentOwner(to);
    }
}
