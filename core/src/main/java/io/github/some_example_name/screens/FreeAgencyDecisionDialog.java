package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.FreeAgencyService;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

/** Resultado das propostas resolvidas no último avanço de calendário. */
public final class FreeAgencyDecisionDialog {
    private FreeAgencyDecisionDialog() {
    }

    public static boolean showPending(Stage stage, Main game) {
        if (stage == null || game == null || game.freeAgencyService == null) {
            return false;
        }

        List<FreeAgencyService.Offer> decisions =
            game.freeAgencyService.consumeUserDecisions();
        if (decisions.isEmpty()) return false;

        Dialog dialog = new Dialog("RESULTADO DAS NEGOCIAÇÕES", game.skin);
        Table content = new Table();
        content.top().pad(12f);

        Label subtitle = ScreenUI.createSubtitle(
            game.skin,
            decisions.size() == 1
                ? "Uma proposta recebeu resposta após o avanço do dia."
                : decisions.size() + " propostas receberam resposta após o avanço do dia."
        );
        subtitle.setAlignment(Align.center);
        content.add(subtitle).width(650f).padBottom(12f).row();

        int index = 0;
        for (FreeAgencyService.Offer offer : decisions) {
            boolean accepted =
                offer.getStatus() == FreeAgencyService.OfferStatus.ACCEPTED;
            boolean counter = offer.getStatus() == FreeAgencyService.OfferStatus.COUNTER_OFFER;
            Color statusColor = accepted ? ScreenUI.SUCCESS : counter ? ScreenUI.WARNING : ScreenUI.DANGER;

            Table row = ScreenUI.createRow(index++);
            Table identity = new Table();
            identity.left();
            identity.add(ScreenUI.createBoldValue(
                game.skin,
                offer.getPlayer().getName(),
                StyleFactory.CREME_AGED,
                Align.left
            )).left().row();
            identity.add(ScreenUI.createSubtitle(
                game.skin,
                offer.getPlayer().getPosition() + " • OVR " +
                    offer.getPlayer().getOverall()
            )).left().padTop(2f);
            row.add(identity).width(205f).left().padLeft(9f);

            row.add(ScreenUI.createBoldValue(
                game.skin,
                offer.getStatus().getLabel(),
                statusColor,
                Align.center
            )).width(105f);

            Label message = ScreenUI.createSubtitle(
                game.skin,
                offer.getDecisionMessage()
            );
            message.setWrap(true);
            message.setColor(Color.WHITE);
            row.add(message).width(340f).left().pad(7f);
            content.add(row).width(670f).height(72f).padBottom(5f).row();
            if (counter) {
                com.badlogic.gdx.scenes.scene2d.ui.TextButton respond = ScreenUI.createInteractiveButton("ENVIAR NOS TERMOS PEDIDOS", game.skin);
                respond.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    @Override public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                        FreeAgencyService.Submission submission = game.freeAgencyService.submitOffer(
                            game.playerClub, offer.getPlayer(), offer.getCounterAnnualSalary(), offer.getCounterYears());
                        new Dialog("NEGOCIAÇÃO", game.skin).text(submission.getMessage()).button("OK").show(stage);
                        if (submission.isAccepted()) respond.setDisabled(true);
                    }
                });
                content.add(respond).width(360f).height(38f).padBottom(8f).row();
                Label hint = ScreenUI.createSubtitle(game.skin, "Para barganhar novamente, abra o jogador na Free Agency. Outros clubes ainda podem contratá-lo.");
                hint.setWrap(true);
                content.add(hint).width(650f).padBottom(8f).row();
            }
        }

        ScrollPane scroll = new ScrollPane(content, game.skin);
        scroll.setFadeScrollBars(false);
        dialog.getContentTable().add(scroll).width(710f).height(
            Math.min(480f, 90f + decisions.size() * 170f)
        ).pad(10f);
        dialog.button("CONTINUAR", true);
        dialog.show(stage);
        return true;
    }
}
