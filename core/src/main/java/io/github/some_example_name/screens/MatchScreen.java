package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.MatchNarrator;

import java.util.ArrayList;
import java.util.List;

public class MatchScreen implements Screen {

    private final Main game;
    private final Match match;
    private final Club playerClub;
    private Stage stage;

    // Persistência de substituições na partida
    private int substitutionsUsed = 0;
    private final List<Player> substitutedPlayers = new ArrayList<>();

    // Controladores do Cronômetro
    private int currentMinute = 0;
    private boolean isPaused = false;
    private boolean isGoalFrozen = false;
    private boolean matchFinished = false;
    private float timeScale = 1.0f;
    private float baseInterval = 0.5f;
    private float currentInterval = 0.1f;
    private Timer.Task matchTimerTask;

    // UI
    private Label scoreLabel, minuteLabel;
    private Label posLabel, shotsLabel, targetShotsLabel, cornersLabel, foulsLabel, xGLabel;
    private MiniTacticalField tacticalField;
    private Table eventTable;
    private ScrollPane eventScroll;
    private Table goalOverlayTable;

    // Logos e Nomes
    private Image homeLogoImage;
    private Image awayLogoImage;
    private Label homeNameLabel;
    private Label awayNameLabel;

    // Botões
    private TextButton pauseBtn, speed1xBtn, speed2xBtn;

    public MatchScreen(Main game, Match match, Club playerClub) {
        this.game = game;
        this.match = match;
        this.playerClub = playerClub;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        if (game.skin.has("font-title", com.badlogic.gdx.graphics.g2d.BitmapFont.class)) {
            game.skin.getFont("font-title").getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear, Texture.TextureFilter.Linear
            );
        }

        buildLayout();
        buildGoalOverlay();
        startTimer();
    }

    private void updateTeamLogos() {
        if (match == null) return;

        if (match.getHomeTeam() != null && match.getHomeTeam().getLogoPath() != null) {
            String homePath = match.getHomeTeam().getLogoPath();
            if (Gdx.files.internal(homePath).exists()) {
                Texture t = new Texture(Gdx.files.internal(homePath));
                t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                homeLogoImage.setDrawable(new TextureRegionDrawable(t));
            }
        }

        if (match.getAwayTeam() != null && match.getAwayTeam().getLogoPath() != null) {
            String awayPath = match.getAwayTeam().getLogoPath();
            if (Gdx.files.internal(awayPath).exists()) {
                Texture t = new Texture(Gdx.files.internal(awayPath));
                t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                awayLogoImage.setDrawable(new TextureRegionDrawable(t));
            }
        }
    }

    private void buildLayout() {
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        root.background(game.background);
        stage.addActor(root);

        // 1. TOPO: PLACAR
        Table topBar = new Table();
        topBar.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        topBar.pad(8, 16, 8, 16);

        homeLogoImage = new Image();
        homeLogoImage.setScaling(Scaling.fit);

        awayLogoImage = new Image();
        awayLogoImage.setScaling(Scaling.fit);

        updateTeamLogos();

        Label.LabelStyle nameStyle = new Label.LabelStyle(game.skin.getFont("font-bold"), Color.WHITE);
        homeNameLabel = new Label(match.getHomeTeam().getName().toUpperCase(), nameStyle);
        awayNameLabel = new Label(match.getAwayTeam().getName().toUpperCase(), nameStyle);

        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.skin.getFont("font-title"), StyleFactory.GOLD);
        scoreLabel = new Label("0 x 0", scoreStyle);
        scoreLabel.setAlignment(Align.center);

        Label.LabelStyle timeStyle = new Label.LabelStyle(game.skin.getFont("font-bold"), Color.WHITE);
        minuteLabel = new Label("00' ▶", timeStyle);

        topBar.add(homeLogoImage).size(72, 40).padRight(10);
        topBar.add(homeNameLabel).padRight(16);
        topBar.add(scoreLabel).padLeft(12).padRight(12);
        topBar.add(awayNameLabel).padLeft(16).padRight(10);
        topBar.add(awayLogoImage).size(72, 40);

        topBar.add().expandX();
        topBar.add(minuteLabel).right();

        root.add(topBar).growX().pad(10, 12, 6, 12).row();

        // 2. CORPO PRINCIPAL
        Table contentTable = new Table();

        Table fieldCard = new Table();
        fieldCard.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        fieldCard.pad(10);

        Label fieldTitle = new Label("CAMPO TÁTICO", game.skin, "font-bold", StyleFactory.GOLD);
        fieldTitle.setFontScale(0.9f);
        fieldCard.add(fieldTitle).center().padBottom(8).row();

        tacticalField = new MiniTacticalField();
        fieldCard.add(tacticalField).grow().minWidth(320).minHeight(400);

        Table rightColumn = new Table();

        Table statsCard = new Table();
        statsCard.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        statsCard.pad(10);

        Label statsTitle = new Label("Estatísticas", game.skin, "font-bold", StyleFactory.GOLD);
        statsCard.add(statsTitle).colspan(2).left().padBottom(6).row();

        posLabel = new Label("50% x 50%", game.skin, "font-label", Color.WHITE);
        shotsLabel = new Label("0 x 0", game.skin, "font-label", Color.WHITE);
        targetShotsLabel = new Label("0 x 0", game.skin, "font-label", Color.WHITE);
        cornersLabel = new Label("0 x 0", game.skin, "font-label", Color.WHITE);
        foulsLabel = new Label("0 x 0", game.skin, "font-label", Color.WHITE);
        xGLabel = new Label("0.00 x 0.00", game.skin, "font-label", StyleFactory.SOFT_YELLOW);

        addStatRow(statsCard, "Posse", posLabel);
        addStatRow(statsCard, "Finaliz.", shotsLabel);
        addStatRow(statsCard, "No gol", targetShotsLabel);
        addStatRow(statsCard, "Escant.", cornersLabel);
        addStatRow(statsCard, "Faltas", foulsLabel);
        addStatRow(statsCard, "xG", xGLabel);

        Table eventsCard = new Table();
        eventsCard.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        eventsCard.pad(10);

        Label eventsTitle = new Label("Eventos", game.skin, "font-bold", StyleFactory.GOLD);
        eventsCard.add(eventsTitle).left().padBottom(6).row();

        eventTable = new Table();
        eventTable.top().left();
        eventScroll = new ScrollPane(eventTable, game.skin);
        eventScroll.setFadeScrollBars(false);
        eventsCard.add(eventScroll).grow();

        rightColumn.add(statsCard).growX().padBottom(6).row();
        rightColumn.add(eventsCard).grow();

        contentTable.add(fieldCard).grow().padRight(8);
        contentTable.add(rightColumn).width(280).growY();

        root.add(contentTable).grow().pad(0, 12, 6, 12).row();

        // 3. BARRA INFERIOR
        Table bottomBar = new Table();
        bottomBar.background(StyleFactory.createRoundedPanel(StyleFactory.WINE_RED, StyleFactory.GOLD));
        bottomBar.pad(6, 15, 6, 15);

        TextButton btnLineup = new TextButton("Escalação", game.skin);
        btnLineup.getLabel().setFontScale(0.85f);
        btnLineup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (isGoalFrozen) return;
                openTacticsDialog();
            }
        });

        pauseBtn = new TextButton("Pausar", game.skin);
        pauseBtn.getLabel().setFontScale(0.85f);
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (isGoalFrozen) return;
                isPaused = !isPaused;
                pauseBtn.setText(isPaused ? "Continuar" : "Pausar");
            }
        });

        speed1xBtn = new TextButton("1x", game.skin);
        speed1xBtn.getLabel().setFontScale(0.85f);
        speed1xBtn.setColor(Color.GREEN);
        speed1xBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                timeScale = 1.0f;
                updateSpeedButtonColors(speed1xBtn);
            }
        });

        speed2xBtn = new TextButton("2x", game.skin);
        speed2xBtn.getLabel().setFontScale(0.85f);
        speed2xBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                timeScale = 2.5f;
                updateSpeedButtonColors(speed2xBtn);
            }
        });

        TextButton btnSimulate = new TextButton("Simular", game.skin);
        btnSimulate.getLabel().setFontScale(0.85f);
        btnSimulate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                simulateRemainingMatch();
            }
        });

        bottomBar.add(btnLineup).height(32).padRight(12);
        bottomBar.add(pauseBtn).height(32).padRight(8);
        bottomBar.add(speed1xBtn).width(40).height(32).padRight(4);
        bottomBar.add(speed2xBtn).width(40).height(32).padRight(15);
        bottomBar.add().expandX();
        bottomBar.add(btnSimulate).height(32);

        root.add(bottomBar).growX().pad(0, 12, 10, 12);
    }

    private void openTacticsDialog() {
        isPaused = true;
        if (pauseBtn != null) {
            pauseBtn.setText("Continuar");
        }

        TacticsDialog d = new TacticsDialog(
            game,
            playerClub,
            substitutionsUsed,
            substitutedPlayers,
            () -> {
                applyTacticalChanges();
                // Despausa o jogo ao fechar a modal
                isPaused = false;
                if (pauseBtn != null) {
                    pauseBtn.setText("Pausar");
                }
            },
            (outPlayer, inPlayer) -> {
                substitutionsUsed++;
                String desc = "Sai: " + outPlayer.getName() + " ➔ Entra: " + inPlayer.getName() + " (" + playerClub.getName() + ")";
                MatchEvent event = new MatchEvent(currentMinute, desc, "SUBSTITUICAO", true);
                updateEvents(event);
                applyTacticalChanges();
            },
            () -> {
                applyTacticalChanges();
            }
        );
        d.show(stage);
    }

    private void applyTacticalChanges() {
        if (playerClub == null) return;

        List<Player> currentStarters = new ArrayList<>(playerClub.getTacticsMap().values());
        playerClub.getStartingXI().clear();
        playerClub.getStartingXI().addAll(currentStarters);
    }

    private void simulateRemainingMatch() {
        if (matchFinished) return;

        if (matchTimerTask != null) {
            matchTimerTask.cancel();
        }

        while (currentMinute < 90) {
            currentMinute++;
            MatchEvent event = game.matchEngine.simulateMinute(match, currentMinute);
            if (event != null) {
                updateEvents(event);
            }
        }

        scoreLabel.setText(match.getHomeGoals() + " x " + match.getAwayGoals());
        minuteLabel.setText("90' ▶");

        finishMatch();
    }

    private void addStatRow(Table table, String name, Label valueLabel) {
        table.add(new Label(name, game.skin, "font-bold", StyleFactory.GOLD)).left().expandX();
        table.add(valueLabel).right().row();
    }

    private void updateSpeedButtonColors(TextButton activeBtn) {
        speed1xBtn.setColor(Color.WHITE);
        speed2xBtn.setColor(Color.WHITE);
        activeBtn.setColor(Color.GREEN);
    }

    private void triggerGoalFreeze(MatchEvent event) {
        this.isGoalFrozen = true;

        scoreLabel.setText(match.getHomeGoals() + " x " + match.getAwayGoals());

        scoreLabel.clearActions();
        scoreLabel.setColor(Color.YELLOW);
        scoreLabel.addAction(Actions.sequence(
            Actions.scaleTo(1.3f, 1.3f, 0.2f, Interpolation.bounceOut),
            Actions.scaleTo(1.0f, 1.0f, 0.2f),
            Actions.color(Color.WHITE, 0.5f)
        ));

        if (tacticalField != null) {
            tacticalField.triggerGoalCelebration();
        }

        showGoalCelebrationBanner(event);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (tacticalField != null) {
                    tacticalField.resumeFromGoal();
                }
                isGoalFrozen = false;
                scheduleNextMinute(baseInterval);
            }
        }, 4.5f);
    }

    private void buildGoalOverlay() {
        goalOverlayTable = new Table();
        goalOverlayTable.setFillParent(true);
        goalOverlayTable.setVisible(false);
        goalOverlayTable.setTransform(true);
        stage.addActor(goalOverlayTable);
    }

    private void showGoalCelebrationBanner(MatchEvent event) {
        goalOverlayTable.clear();
        goalOverlayTable.setVisible(true);
        goalOverlayTable.getColor().a = 0f;
        goalOverlayTable.setScale(0.5f);
        goalOverlayTable.setOrigin(Align.center);

        Table banner = new Table();
        banner.background(StyleFactory.createRoundedPanel(StyleFactory.WINE_RED, StyleFactory.GOLD));
        banner.pad(20, 40, 20, 40);

        Label lblGoal = new Label("⚽ GOOOOOOOOL! ⚽", game.skin, "font-title", StyleFactory.GOLD);
        lblGoal.setFontScale(1.8f);

        Label lblDesc = new Label(event.description, game.skin, "font-bold", Color.WHITE);
        lblDesc.setFontScale(1.1f);

        banner.add(lblGoal).center().padBottom(10).row();
        banner.add(lblDesc).center();

        goalOverlayTable.add(banner).center();

        goalOverlayTable.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(0.4f, Interpolation.pow2Out),
                Actions.scaleTo(1.0f, 1.0f, 0.4f, Interpolation.swingOut)
            ),
            Actions.delay(2.7f),
            Actions.parallel(
                Actions.fadeOut(0.4f, Interpolation.pow2In),
                Actions.scaleTo(0.7f, 0.7f, 0.4f)
            ),
            Actions.run(() -> goalOverlayTable.setVisible(false))
        ));
    }

    private void startTimer() { scheduleNextMinute(baseInterval); }

    private void scheduleNextMinute(float delayInSeconds) {
        if (matchTimerTask != null) matchTimerTask.cancel();

        matchTimerTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (matchFinished) return;

                if (!isPaused && !isGoalFrozen) {
                    currentMinute++;
                    minuteLabel.setText(currentMinute + "' ▶");

                    MatchEvent event = game.matchEngine.simulateMinute(match, currentMinute);

                    if (tacticalField != null) {
                        tacticalField.setPossessionPercent(match.getHomePossession());
                    }

                    posLabel.setText(String.format("%.0f%% x %.0f%%", (float) match.getHomePossession(), 100f - (float) match.getHomePossession()));
                    shotsLabel.setText(match.getHomeShots() + " x " + match.getAwayShots());
                    targetShotsLabel.setText(match.getHomeShotsOnTarget() + " x " + match.getAwayShotsOnTarget());
                    cornersLabel.setText(match.getHomeCorners() + " x " + match.getAwayCorners());
                    foulsLabel.setText(match.getHomeFouls() + " x " + match.getAwayFouls());
                    xGLabel.setText(String.format("%.2f x %.2f", (float) match.getHomeXG(), match.getAwayXG()));

                    if (event != null) {
                        updateEvents(event);

                        // Intercepta lesão do time do jogador para pausar e abrir substituição
                        if ("LESIONADO".equals(event.type)) {
                            openTacticsDialog();
                            return;
                        }

                        if ("GOL".equals(event.type)) {
                            if (tacticalField != null) {
                                tacticalField.setAnimationSpeed(0.8f);
                                tacticalField.onMatchEvent(event, () -> triggerGoalFreeze(event));
                            }
                            return;
                        } else if ("CHUTE".equals(event.type)) {
                            currentInterval = 1.4f;
                            if (tacticalField != null) {
                                tacticalField.setAnimationSpeed(0.6f * timeScale);
                                tacticalField.onMatchEvent(event, null);
                            }
                        } else {
                            currentInterval = 0.3f;
                            if (tacticalField != null) {
                                tacticalField.setAnimationSpeed(1.8f * timeScale);
                                tacticalField.onMatchEvent(event, null);
                            }
                        }
                    } else {
                        currentInterval = baseInterval;
                        if (tacticalField != null) {
                            tacticalField.setAnimationSpeed(3.0f * timeScale);
                        }
                    }
                }

                if (currentMinute >= 90) {
                    finishMatch();
                } else {
                    float nextDelay = (currentInterval / timeScale);
                    scheduleNextMinute(nextDelay);
                }
            }
        }, delayInSeconds);
    }

    private void updateEvents(MatchEvent e) {
        if (e == null) return;

        String commentary = MatchNarrator.generateCommentary(e, match);

        Table commentBlock = new Table();
        commentBlock.left().padBottom(10);

        Label minuteLbl = new Label(e.minute + "'", game.skin, "font-bold", StyleFactory.GOLD);
        minuteLbl.setFontScale(0.9f);

        Label.LabelStyle textStyle = new Label.LabelStyle(game.skin.getFont("font-label"), Color.WHITE);
        Label textLbl = new Label(commentary, textStyle);
        textLbl.setWrap(true);

        if ("GOL".equals(e.type)) {
            textLbl.setColor(StyleFactory.SOFT_YELLOW);
        } else if ("CARTAO".equals(e.type)) {
            textLbl.setColor(Color.YELLOW);
        } else if ("SUBSTITUICAO".equals(e.type)) {
            textLbl.setColor(Color.CYAN);
        } else if ("LESIONADO".equals(e.type)) {
            textLbl.setColor(Color.valueOf("FF4D4D"));
        }

        commentBlock.add(minuteLbl).left().row();
        commentBlock.add(textLbl).width(240).left().padTop(2);

        eventTable.add(commentBlock).growX().left().row();

        eventScroll.layout();
        eventScroll.setScrollPercentY(100);
    }

    private void finishMatch() {
        if (matchFinished) return;
        matchFinished = true;
        if (matchTimerTask != null) matchTimerTask.cancel();

        game.matchEngine.finalizeMatch(match);
        game.league.advanceMatch();

        MatchResultDialog d = new MatchResultDialog(game, match) {
            @Override
            protected void result(Object obj) {
                game.setScreen(new ClubManagementScreen(game, playerClub));
            }
        };
        d.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { isPaused = true; }
    @Override public void resume() { isPaused = false; }
    @Override public void hide() { if (matchTimerTask != null) matchTimerTask.cancel(); }
    @Override
    public void dispose() {
        if (matchTimerTask != null) matchTimerTask.cancel();
        if (stage != null) stage.dispose();
    }
}
