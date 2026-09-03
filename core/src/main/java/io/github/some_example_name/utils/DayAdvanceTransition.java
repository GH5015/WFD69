package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Transição não bloqueante exibida antes do processamento de um novo dia. */
public final class DayAdvanceTransition {
    private static final String ACTOR_NAME = "day-advance-transition";
    private static final Locale PT_BR = new Locale("pt", "BR");
    private static Drawable overlayBackground;
    private static Drawable cardBackground;
    private static Drawable datesBackground;
    private static Drawable progressTrack;
    private static Drawable progressFill;

    private DayAdvanceTransition() {
    }

    public static void play(
        Stage stage,
        Main game,
        int days,
        Runnable onComplete
    ) {
        if (stage == null || game == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        Actor running = stage.getRoot().findActor(ACTOR_NAME);
        if (running != null) return;

        int safeDays = Math.max(1, days);
        Date currentDate = game.league == null ? null : game.league.getCurrentDate();
        Date targetDate = plusDays(currentDate, safeDays);
        ensureDrawables();

        final Table overlay = new Table();
        overlay.setName(ACTOR_NAME);
        overlay.setFillParent(true);
        overlay.setTouchable(Touchable.enabled);
        overlay.setColor(1f, 1f, 1f, 0f);
        overlay.background(overlayBackground);

        Table card = new Table();
        card.setTransform(true);
        card.background(cardBackground);
        card.pad(24f, 34f, 24f, 34f);

        Label eyebrow = ScreenUI.createBoldValue(
            game.skin,
            safeDays == 1 ? "CALENDÁRIO DA WFL" : "PASSAGEM DE TEMPO",
            StyleFactory.GOLD,
            Align.center
        );
        eyebrow.setFontScale(0.52f);
        card.add(eyebrow).growX().center().padBottom(5f).row();

        Label title = new Label(
            safeDays == 1 ? "AVANÇANDO O DIA" : "AVANÇANDO " + safeDays + " DIAS",
            game.skin,
            "font-title"
        );
        title.setFontScale(0.84f);
        title.setColor(StyleFactory.SOFT_YELLOW);
        title.setAlignment(Align.center);
        card.add(title).growX().center().padBottom(18f).row();

        Table dates = new Table();
        dates.background(datesBackground);
        dates.pad(10f, 18f, 10f, 18f);
        dates.add(dateBlock(game, "DATA ATUAL", currentDate, StyleFactory.CREME_AGED))
            .width(220f).growY();

        Label arrow = ScreenUI.createBoldValue(
            game.skin,
            "→",
            StyleFactory.GOLD,
            Align.center
        );
        arrow.setFontScale(1.05f);
        arrow.setOrigin(Align.center);
        dates.add(arrow).width(76f).center();

        dates.add(dateBlock(game, "PRÓXIMA DATA", targetDate, ScreenUI.SUCCESS))
            .width(220f).growY();
        card.add(dates).width(570f).height(82f).center().padBottom(18f).row();

        Stack progressStack = new Stack();
        Image track = new Image(progressTrack);
        final Image progress = new Image(progressFill);
        progress.setOrigin(Align.left);
        progress.setScaleX(0f);
        progressStack.add(track);
        progressStack.add(progress);
        card.add(progressStack).width(570f).height(9f).center().padBottom(10f).row();

        Label detail = ScreenUI.createSubtitle(
            game.skin,
            "ATUALIZANDO SCOUTING  •  FINANÇAS  •  MERCADO  •  ELENCOS"
        );
        detail.setFontScale(0.48f);
        detail.setAlignment(Align.center);
        card.add(detail).growX().center();

        overlay.add(card).width(650f).height(310f).center();
        stage.addActor(overlay);

        arrow.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.12f, 1.12f, 0.32f, Interpolation.sineOut),
                    Actions.scaleTo(1f, 1f, 0.32f, Interpolation.sine)
                )
            )
        );
        progress.addAction(
            Actions.scaleTo(1f, 1f, 0.82f, Interpolation.smoother)
        );
        card.setOrigin(Align.center);
        card.setScale(0.94f);
        card.addAction(
            Actions.scaleTo(1f, 1f, 0.24f, Interpolation.swingOut)
        );

        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.18f, Interpolation.fade),
                Actions.delay(0.82f),
                Actions.fadeOut(0.18f, Interpolation.fade),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        overlay.remove();
                        if (onComplete != null) onComplete.run();
                    }
                })
            )
        );
    }

    private static Table dateBlock(
        Main game,
        String caption,
        Date date,
        Color color
    ) {
        Table block = new Table();
        Label top = ScreenUI.createSubtitle(game.skin, caption);
        top.setFontScale(0.43f);
        top.setAlignment(Align.center);
        Label value = ScreenUI.createBoldValue(
            game.skin,
            formatDate(date),
            color,
            Align.center
        );
        value.setFontScale(0.66f);
        block.add(top).growX().center().row();
        block.add(value).growX().center().padTop(4f);
        return block;
    }

    private static void ensureDrawables() {
        if (overlayBackground != null) return;
        overlayBackground =
            StyleFactory.createSolid(new Color(0.004f, 0.018f, 0.011f, 0.94f));
        cardBackground =
            StyleFactory.createRoundedPanel(
                Color.valueOf("0B1B12"),
                StyleFactory.GOLD
            );
        datesBackground =
            StyleFactory.createRoundedPanel(
                Color.valueOf("111F17"),
                Color.valueOf("46584D")
            );
        progressTrack = StyleFactory.createSolid(Color.valueOf("2D3A32"));
        progressFill = StyleFactory.createSolid(StyleFactory.GOLD);
    }

    private static Date plusDays(Date date, int days) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    private static String formatDate(Date date) {
        if (date == null) return "DATA INDISPONÍVEL";
        return new SimpleDateFormat("dd 'DE' MMMM", PT_BR)
            .format(date)
            .toUpperCase(PT_BR);
    }
}
