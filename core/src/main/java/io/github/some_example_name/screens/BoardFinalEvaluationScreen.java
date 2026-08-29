package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.BoardObjective;
import io.github.some_example_name.model.BoardObjectiveService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

/** Avaliação definitiva da diretoria exibida antes do resumo da temporada. */
public class BoardFinalEvaluationScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;
    private final Texture starTexture;
    private final BoardObjectiveService.Evaluation evaluation;
    private final int season;
    private final int historyBonus;

    public BoardFinalEvaluationScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
        this.starTexture = new Texture(Gdx.files.internal("Icons8/icons8-estrela-48.png"));
        this.season = game.league.getCurrentSeason();
        this.evaluation = BoardObjectiveService.evaluate(game.league, club);
        this.historyBonus = game.managerCareer.calculateHistoryConfidenceBonus(club, season);

        if (club.getFinalBoardEvaluationSeason() != season) {
            int score = Math.min(100, evaluation.getConfidence() + historyBonus);
            club.recordFinalBoardEvaluation(season, score, shouldDismiss(score));
        }
        game.managerCareer.recordSeason(
            game.league,
            club,
            evaluation,
            club.getFinalBoardScore(),
            club.isFinalBoardDismissed()
        );
    }

    private boolean shouldDismiss(int score) {
        if (score >= 35) return false;
        if (score < 20) return true;
        int roll = Math.floorMod((club.getName() + season + "DIRETORIA").hashCode(), 100);
        return roll < 65;
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        refresh();
    }

    private void refresh() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        Image shade = new Image(StyleFactory.createSolid(new Color(0f, 0.02f, 0.015f, 0.62f)));
        shade.setFillParent(true);
        root.add(shade);

        Table page = new Table();
        page.setFillParent(true);
        page.top();
        page.pad(28f, 190f, 24f, 190f);
        page.add(ScreenUI.createHeader(game.skin, "AVALIAÇÃO DA DIRETORIA • " + season, club.getName().toUpperCase()))
            .growX().height(74f).padBottom(10f).row();
        page.add(createResult()).growX().height(174f).padBottom(10f).row();
        page.add(createObjectives()).grow().padBottom(12f).row();
        page.add(createDecision()).growX().height(92f).padBottom(11f).row();
        page.add(createContinue()).width(390f).height(56f).center();
        root.add(page);
    }

    private Table createResult() {
        int score = club.getFinalBoardScore();
        Color color = finalColor(score);
        Table panel = ScreenUI.createPanel();
        panel.pad(15f, 24f, 15f, 24f);

        Table scoreBox = new Table();
        scoreBox.background(StyleFactory.createRoundedPanel(Color.valueOf("122019"), color));
        scoreBox.pad(10f, 28f, 10f, 28f);
        Label scoreLabel = new Label(score + " / 100", game.skin, "font-title");
        scoreLabel.setFontScale(0.88f);
        scoreLabel.setColor(color);
        scoreBox.add(scoreLabel).center().row();
        scoreBox.add(finalStars(score)).center().padTop(4f).row();
        Label grade = new Label(finalGrade(score), game.skin, "font-bold");
        grade.setFontScale(0.48f);
        grade.setColor(color);
        scoreBox.add(grade).center().padTop(4f);
        panel.add(scoreBox).width(280f).growY().padRight(26f);

        Table overview = new Table();
        overview.left();
        Label heading = ScreenUI.createSectionTitle(game.skin, "RESULTADO GERAL");
        overview.add(heading).left().padBottom(8f).row();
        int previous = club.getFinalBoardPreviousConfidence();
        int movement = score - previous;
        overview.add(ScreenUI.createSubtitle(game.skin, "Confiança anterior: " + previous)).left().row();
        overview.add(ScreenUI.createSubtitle(
            game.skin,
            "Avaliação dos objetivos: " + evaluation.getConfidence()
                + "  •  Histórico: +" + historyBonus
        )).left().padTop(3f).row();
        Label current = ScreenUI.createBoldValue(
            game.skin,
            "Confiança atual: " + score + "  " + (movement > 0 ? "▲ +" : movement < 0 ? "▼ " : "— ") + (movement == 0 ? "" : movement),
            color,
            Align.left
        );
        current.setFontScale(0.59f);
        overview.add(current).left().padTop(5f).row();
        Label explanation = ScreenUI.createSubtitle(game.skin, finalExplanation(score));
        explanation.setWrap(true);
        overview.add(explanation).growX().left().padTop(9f);
        panel.add(overview).grow();
        return panel;
    }

    private Table createObjectives() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "OBJETIVOS DA TEMPORADA"))
            .growX().left().padBottom(9f).row();
        for (BoardObjectiveService.ObjectiveProgress progress : evaluation.getObjectives()) {
            panel.add(objectiveRow(progress)).growX().height(67f).padBottom(5f).row();
        }
        return panel;
    }

    private Table objectiveRow(BoardObjectiveService.ObjectiveProgress progress) {
        BoardObjective objective = progress.getObjective();
        int pct = (int) Math.round(progress.getPercentage());
        Color color = progressColor(pct);
        Table row = ScreenUI.createSubtlePanel();
        row.pad(6f, 12f, 6f, 12f);

        Table priority = new Table();
        for (int i = 0; i < objective.getPriority().getStars(); i++) {
            Image star = new Image(new TextureRegionDrawable(starTexture));
            star.setScaling(Scaling.fit);
            star.setColor(StyleFactory.GOLD);
            priority.add(star).size(15f).padRight(2f);
        }
        row.add(priority).width(70f).left().padRight(9f);

        Table copy = new Table();
        Label title = new Label(objective.getTitle(), game.skin, "font-bold");
        title.setFontScale(0.49f);
        title.setColor(Color.WHITE);
        title.setWrap(true);
        copy.add(title).growX().left().row();
        Label detail = new Label(progress.getDetail(), game.skin);
        detail.setFontScale(0.42f);
        detail.setColor(ScreenUI.MUTED_TEXT);
        copy.add(detail).growX().left().padTop(2f);
        row.add(copy).growX().padRight(12f);

        Label state = new Label(progress.getState(), game.skin, "font-bold");
        state.setFontScale(0.40f);
        state.setColor(color);
        row.add(state).width(185f).right();
        Label percent = new Label(pct + "%", game.skin, "font-bold");
        percent.setFontScale(0.59f);
        percent.setColor(color);
        row.add(percent).width(72f).right();
        return row;
    }

    private Table createDecision() {
        int score = club.getFinalBoardScore();
        boolean dismissed = club.isFinalBoardDismissed();
        Color color = dismissed ? ScreenUI.DANGER : score < 50 ? ScreenUI.WARNING : ScreenUI.SUCCESS;
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(Color.valueOf("111D17"), color));
        panel.pad(12f, 22f, 12f, 22f);
        Table copy = new Table();
        copy.add(ScreenUI.createSubtitle(game.skin, "DECISÃO DA DIRETORIA")).left().row();
        Label decision = new Label(
            dismissed ? "ENCERRAMENTO DO CICLO" : score < 50 ? "PERMANÊNCIA • CARGO EM RISCO" : "PERMANÊNCIA CONFIRMADA",
            game.skin,
            "font-bold"
        );
        decision.setFontScale(0.63f);
        decision.setColor(color);
        copy.add(decision).left().padTop(3f);
        panel.add(copy).left().expandX();
        Label quote = new Label(decisionMessage(score, dismissed), game.skin);
        quote.setFontScale(0.48f);
        quote.setColor(StyleFactory.CREME_AGED);
        quote.setWrap(true);
        quote.setAlignment(Align.right);
        panel.add(quote).width(550f).right();
        return panel;
    }

    private TextButton createContinue() {
        String text = club.isFinalBoardDismissed() ? "PROCURAR NOVA FRANQUIA  ›" : "CONTINUAR  ›";
        TextButton button = ScreenUI.createPrimaryButton(game.skin, text);
        button.getLabel().setFontScale(0.58f);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                completeSeasonTransition();
            }
        });
        return button;
    }

    private void completeSeasonTransition() {
        if ("PLAYOFFS".equals(game.league.getCurrentStage())) game.league.checkAndAdvanceStage();
        if (game.freeAgencyService != null) game.freeAgencyService.releaseExpiredContractsAtOffseasonStart();
        if (club.isFinalBoardDismissed()) {
            club.setUserControlled(false);
            game.playerClub = null;
            game.setScreen(new UnemployedScreen(game));
        } else {
            game.setScreen(new SeasonSummaryScreen(game, club));
        }
    }

    private String finalGrade(int score) {
        if (score >= 80) return "EXCELENTE";
        if (score >= 65) return "BOM";
        if (score >= 50) return "ACEITÁVEL";
        if (score >= 35) return "INSATISFATÓRIO";
        return "FRACASSO";
    }

    private Table finalStars(int score) {
        int active = score >= 80 ? 5 : score >= 65 ? 4 : score >= 50 ? 3 : score >= 35 ? 2 : 1;
        Table stars = new Table();
        for (int i = 0; i < 5; i++) {
            Image star = new Image(new TextureRegionDrawable(starTexture));
            star.setScaling(Scaling.fit);
            star.setColor(i < active ? StyleFactory.GOLD : Color.valueOf("414A43"));
            stars.add(star).size(16f).padRight(2f);
        }
        return stars;
    }

    private String finalExplanation(int score) {
        if (score >= 80) return "Contrato totalmente seguro e projeto acima das expectativas.";
        if (score >= 65) return "A diretoria considera a temporada positiva.";
        if (score >= 50) return "O desempenho foi suficiente para manter a estabilidade.";
        if (score >= 35) return "A permanência foi mantida, mas a próxima temporada começa sob cobrança.";
        return "O resultado ficou muito abaixo da direção esperada para a franquia.";
    }

    private String decisionMessage(int score, boolean dismissed) {
        if (dismissed) return "A diretoria decidiu buscar uma nova liderança para o próximo ciclo.";
        if (score < 50) return "A continuidade foi aprovada, mas uma reação será necessária desde o início da próxima temporada.";
        return "A diretoria está satisfeita com o progresso apresentado.";
    }

    private Color finalColor(int score) {
        if (score >= 65) return ScreenUI.SUCCESS;
        if (score >= 50) return StyleFactory.SOFT_YELLOW;
        if (score >= 35) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private Color progressColor(int pct) {
        if (pct >= 80) return ScreenUI.SUCCESS;
        if (pct >= 45) return StyleFactory.SOFT_YELLOW;
        if (pct >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0.02f, 0.015f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); starTexture.dispose(); }
}
