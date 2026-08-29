package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.BoardObjective;
import io.github.some_example_name.model.BoardObjectiveService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.Calendar;
import java.util.Date;

/** Relatórios persistentes de início, meio de temporada e Trade Deadline. */
public final class BoardReviewDialog {
    private BoardReviewDialog() { }

    public static boolean showDue(Stage stage, Main game, Club club) {
        if (stage == null || game == null || game.league == null || club == null
            || !"REGULAR".equals(game.league.getCurrentStage())) return false;

        int season = game.league.getCurrentSeason();
        club.beginBoardSeason(season);
        int played = regularMatches(club, game, true);
        int total = regularMatches(club, game, false);

        String checkpoint = null;
        if (!club.hasShownBoardReview(season, "INICIO") && played == 0) {
            checkpoint = "INICIO";
        } else if (!club.hasShownBoardReview(season, "MEIO")
            && total > 0 && played >= Math.max(1, (int) Math.ceil(total / 2d))) {
            checkpoint = "MEIO";
        } else if (!club.hasShownBoardReview(season, "DEADLINE")
            && reachedTradeDeadline(game.league.getCurrentDate())) {
            checkpoint = "DEADLINE";
        }

        if (checkpoint == null) return false;
        BoardObjectiveService.Evaluation evaluation = BoardObjectiveService.evaluate(game.league, club);
        club.markBoardReviewShown(season, checkpoint);
        if (!"INICIO".equals(checkpoint)) club.updateBoardConfidence(evaluation.getConfidence());
        show(stage, game, club, checkpoint, evaluation);
        return true;
    }

    private static void show(
        Stage stage,
        Main game,
        Club club,
        String checkpoint,
        BoardObjectiveService.Evaluation evaluation
    ) {
        final Texture stars = new Texture(Gdx.files.internal("Icons8/icons8-estrela-48.png"));
        Dialog dialog = new Dialog("", game.skin) {
            private boolean textureDisposed;
            @Override public void hide() {
                super.hide();
                if (!textureDisposed) {
                    textureDisposed = true;
                    stars.dispose();
                }
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        Table content = dialog.getContentTable();
        content.clear();
        content.background(StyleFactory.createMetallicBoard(960, 650, Color.valueOf("0B1711")));
        content.pad(20f, 25f, 15f, 25f);

        Label title = new Label(title(checkpoint, game.league.getCurrentSeason()), game.skin, "font-title");
        title.setFontScale(0.78f);
        title.setColor(StyleFactory.GOLD);
        title.setAlignment(Align.center);
        content.add(title).width(880f).center().row();

        Label clubName = new Label(club.getName().toUpperCase(), game.skin, "font-bold");
        clubName.setFontScale(0.52f);
        clubName.setColor(StyleFactory.CREME_AGED);
        clubName.setAlignment(Align.center);
        content.add(clubName).width(880f).center().padTop(3f).padBottom(10f).row();

        if (!"INICIO".equals(checkpoint)) {
            Color confidenceColor = statusColor(evaluation.getConfidence());
            Table status = ScreenUI.createSubtlePanel();
            status.add(ScreenUI.createSubtitle(game.skin, "CONFIANÇA DA DIRETORIA")).left().expandX();
            Label value = ScreenUI.createBoldValue(game.skin, evaluation.getStatus(), confidenceColor, Align.right);
            value.setFontScale(0.60f);
            status.add(value).right();
            content.add(status).growX().height(48f).padBottom(8f).row();
        } else {
            Label introduction = ScreenUI.createSubtitle(
                game.skin,
                "A diretoria definiu a direção esperada para esta temporada. Prioridades críticas têm maior peso."
            );
            introduction.setAlignment(Align.center);
            content.add(introduction).growX().padBottom(10f).row();
        }

        for (BoardObjectiveService.ObjectiveProgress progress : evaluation.getObjectives()) {
            content.add(objectiveRow(game, progress, stars, "INICIO".equals(checkpoint)))
                .growX().height(73f).padBottom(5f).row();
        }

        Label message = ScreenUI.createSubtitle(game.skin, message(checkpoint, evaluation.getConfidence()));
        message.setWrap(true);
        message.setAlignment(Align.center);
        message.setColor(StyleFactory.SOFT_YELLOW);
        content.add(message).width(820f).center().padTop(7f);

        dialog.button("CONTINUAR", true);
        dialog.show(stage);
    }

    private static Table objectiveRow(
        Main game,
        BoardObjectiveService.ObjectiveProgress progress,
        Texture starTexture,
        boolean introduction
    ) {
        BoardObjective objective = progress.getObjective();
        Table row = ScreenUI.createSubtlePanel();
        row.pad(7f, 10f, 7f, 10f);

        Table priority = new Table();
        for (int i = 0; i < objective.getPriority().getStars(); i++) {
            Image star = new Image(new TextureRegionDrawable(starTexture));
            star.setScaling(Scaling.fit);
            star.setColor(StyleFactory.GOLD);
            priority.add(star).size(15f).padRight(2f);
        }
        Label priorityName = new Label(objective.getPriority().getLabel(), game.skin, "font-bold");
        priorityName.setFontScale(0.42f);
        priorityName.setColor(StyleFactory.SOFT_YELLOW);
        priority.add(priorityName).padLeft(5f);
        row.add(priority).width(155f).left().padRight(10f);

        Table copy = new Table();
        Label objectiveTitle = new Label(objective.getTitle(), game.skin, "font-bold");
        objectiveTitle.setFontScale(0.48f);
        objectiveTitle.setColor(Color.WHITE);
        objectiveTitle.setWrap(true);
        copy.add(objectiveTitle).growX().left().row();
        Label detail = new Label(introduction ? objective.getCategory().getLabel() : progress.getDetail(), game.skin);
        detail.setFontScale(0.42f);
        detail.setColor(ScreenUI.MUTED_TEXT);
        copy.add(detail).growX().left().padTop(3f);
        row.add(copy).growX().left().padRight(12f);

        if (!introduction) {
            int pct = (int) Math.round(progress.getPercentage());
            Table result = new Table();
            Label state = new Label(progress.getState(), game.skin, "font-bold");
            state.setFontScale(0.39f);
            state.setColor(progressColor(pct));
            result.add(state).right().row();
            Label percentage = new Label(pct + "%", game.skin, "font-bold");
            percentage.setFontScale(0.55f);
            percentage.setColor(progressColor(pct));
            result.add(percentage).right().padTop(3f);
            row.add(result).width(155f).right();
        }
        return row;
    }

    private static int regularMatches(Club club, Main game, boolean onlyPlayed) {
        int count = 0;
        for (Match match : game.league.getSchedule()) {
            if (!"REGULAR".equals(match.getStage())) continue;
            if (match.getHomeTeam() != club && match.getAwayTeam() != club) continue;
            if (!onlyPlayed || match.isPlayed()) count++;
        }
        return count;
    }

    private static boolean reachedTradeDeadline(Date date) {
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return month > Calendar.SEPTEMBER || (month == Calendar.SEPTEMBER && day >= 15);
    }

    private static String title(String checkpoint, int season) {
        if ("INICIO".equals(checkpoint)) return "OBJETIVOS " + season;
        if ("MEIO".equals(checkpoint)) return "AVALIAÇÃO DA DIRETORIA • MEIO DA TEMPORADA";
        return "AVALIAÇÃO DA DIRETORIA • TRADE DEADLINE";
    }

    private static String message(String checkpoint, int confidence) {
        if ("INICIO".equals(checkpoint)) return "A diretoria avalia a condução do projeto, não apenas vitórias e derrotas.";
        if ("DEADLINE".equals(checkpoint) && confidence < 45)
            return "A equipe precisa reagir na reta final. Esta é a última grande janela para ajustar o elenco.";
        if (confidence >= 65) return "O trabalho está alinhado ao projeto da franquia.";
        if (confidence >= 45) return "O desempenho é estável, mas alguns objetivos ainda exigem atenção.";
        return "A cobrança aumentou. A diretoria espera uma reação até o fim da temporada.";
    }

    private static Color statusColor(int confidence) {
        if (confidence >= 80) return Color.valueOf("62DB8A");
        if (confidence >= 65) return Color.valueOf("9ED36A");
        if (confidence >= 45) return StyleFactory.SOFT_YELLOW;
        if (confidence >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private static Color progressColor(int percentage) {
        if (percentage >= 80) return ScreenUI.SUCCESS;
        if (percentage >= 45) return StyleFactory.SOFT_YELLOW;
        if (percentage >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }
}
