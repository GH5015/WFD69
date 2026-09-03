package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubNeedEvaluator;
import io.github.some_example_name.model.DraftPick;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TradeExecutionService;
import io.github.some_example_name.model.TradeOffer;
import io.github.some_example_name.model.TradeRulesValidator;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

/** Apresenta ao usuário propostas espontâneas enviadas pelas franquias da IA. */
public final class IncomingTradeOfferDialog {
    private IncomingTradeOfferDialog() { }

    public static boolean showPending(Stage stage, Main game, Club userClub) {
        if (stage == null || game == null || game.league == null || userClub == null) return false;
        final TradeOffer offer = game.league.getPendingIncomingTradeOffer();
        if (offer == null) return false;

        TradeRulesValidator.ValidationResult validation =
            TradeRulesValidator.validateRules(offer, game.league);
        if (!validation.isValid) {
            game.league.clearPendingIncomingTradeOffer(offer);
            return false;
        }

        final Club partner = offer.getTargetClub();
        final Dialog dialog = new Dialog("OFERTA DE TROCA RECEBIDA", game.skin);
        Table content = new Table();
        content.top().pad(14f);

        Label heading = ScreenUI.createBoldValue(
            game.skin,
            partner.getName().toUpperCase(),
            StyleFactory.SOFT_YELLOW,
            Align.center
        );
        heading.setFontScale(.72f);
        content.add(heading).colspan(2).growX().center().row();

        String phase = phaseLabel(ClubNeedEvaluator.getTeamPhase(partner));
        Label subtitle = ScreenUI.createSubtitle(
            game.skin,
            "A franquia apresentou uma proposta oficial • " + phase
        );
        subtitle.setAlignment(Align.center);
        content.add(subtitle).colspan(2).growX().center().padTop(4f).padBottom(14f).row();

        content.add(createAssetPanel(
            game,
            partner.getName() + " OFERECE",
            offer.getTargetPlayers(),
            offer.getTargetPicks(),
            ScreenUI.SUCCESS
        )).width(390f).height(260f).growY().padRight(10f);

        content.add(createAssetPanel(
            game,
            "SUA FRANQUIA ENVIA",
            offer.getUserPlayers(),
            offer.getUserPicks(),
            ScreenUI.WARNING
        )).width(390f).height(260f).growY().row();

        Label note = ScreenUI.createSubtitle(
            game.skin,
            "A proposta respeita as regras de elenco, contratos e Hard Cap da WFL."
        );
        note.setAlignment(Align.center);
        note.setColor(Color.WHITE);
        content.add(note).colspan(2).growX().center().padTop(12f);

        dialog.getContentTable().add(content).width(830f).height(355f).pad(8f);

        TextButton reject = ScreenUI.createInteractiveButton("RECUSAR", game.skin);
        TextButton negotiate = ScreenUI.createInteractiveButton("NEGOCIAR", game.skin);
        TextButton accept = ScreenUI.createPrimaryButton(game.skin, "ACEITAR TROCA");

        reject.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.league.clearPendingIncomingTradeOffer(offer);
                dialog.hide();
            }
        });
        negotiate.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.league.clearPendingIncomingTradeOffer(offer);
                dialog.hide();
                game.setScreen(new TradeScreen(game, userClub, offer));
            }
        });
        accept.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                TradeRulesValidator.ValidationResult currentValidation =
                    TradeRulesValidator.validateRules(offer, game.league);
                if (!currentValidation.isValid) {
                    game.league.clearPendingIncomingTradeOffer(offer);
                    dialog.hide();
                    Dialog invalid = new Dialog("OFERTA INDISPONÍVEL", game.skin);
                    invalid.text(currentValidation.reason);
                    invalid.button("OK");
                    invalid.show(stage);
                    return;
                }

                boolean completed = TradeExecutionService.execute(game.league, offer);
                game.league.clearPendingIncomingTradeOffer(offer);
                dialog.hide();
                Dialog result = new Dialog(completed ? "TROCA CONCLUÍDA" : "TROCA NÃO CONCLUÍDA", game.skin);
                result.text(completed
                    ? "A proposta foi aceita. Elencos, picks e escalações foram atualizados."
                    : "A proposta deixou de atender às regras da WFL.");
                result.button("CONTINUAR");
                result.show(stage);
            }
        });

        dialog.getButtonTable().add(reject).width(190f).height(46f).pad(8f);
        dialog.getButtonTable().add(negotiate).width(190f).height(46f).pad(8f);
        dialog.getButtonTable().add(accept).width(230f).height(46f).pad(8f);
        dialog.show(stage);
        return true;
    }

    private static Table createAssetPanel(
        Main game,
        String titleText,
        List<Player> players,
        List<DraftPick> picks,
        Color accent
    ) {
        Table panel = ScreenUI.createPanel();
        panel.top().pad(10f);
        Label title = ScreenUI.createBoldValue(game.skin, titleText.toUpperCase(), accent, Align.left);
        title.setFontScale(.52f);
        panel.add(title).growX().left().padBottom(8f).row();

        int index = 0;
        for (Player player : players) {
            Table row = ScreenUI.createRow(index++);
            Table identity = new Table();
            identity.left();
            identity.add(ScreenUI.createBoldValue(
                game.skin, player.getName(), StyleFactory.CREME_AGED, Align.left
            )).growX().left().row();
            identity.add(ScreenUI.createSubtitle(
                game.skin,
                player.getPosition() + " • " + player.getAge() + " anos"
            )).growX().left();
            row.add(identity).growX().left().padLeft(8f);
            row.add(ScreenUI.createBoldValue(
                game.skin, "OVR " + player.getOverall(), accent, Align.right
            )).width(80f).right().padRight(8f);
            panel.add(row).growX().height(54f).padBottom(5f).row();
        }

        for (DraftPick pick : picks) {
            Table row = ScreenUI.createRow(index++);
            String original = pick.getOriginalOwner() != null ? pick.getOriginalOwner().getName() : "WFL";
            Table identity = new Table();
            identity.left();
            identity.add(ScreenUI.createBoldValue(
                game.skin,
                pick.getYear() + " • " + pick.getRound() + "ª RODADA",
                StyleFactory.CREME_AGED,
                Align.left
            )).growX().left().row();
            identity.add(ScreenUI.createSubtitle(
                game.skin,
                "Pick de " + original + " • geral #" + pick.getProjectedOverallPosition()
            )).growX().left();
            row.add(identity).growX().left().padLeft(8f);
            panel.add(row).growX().height(54f).padBottom(5f).row();
        }
        return panel;
    }

    private static String phaseLabel(ClubNeedEvaluator.TeamPhase phase) {
        if (phase == null) return "MERCADO ATIVO";
        switch (phase) {
            case CONTENDER: return "CANDIDATO AO TÍTULO";
            case BUYER: return "COMPRADOR";
            case SELLER: return "VENDEDOR";
            case REBUILDING:
            default: return "RECONSTRUÇÃO";
        }
    }
}
