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
import io.github.some_example_name.utils.ResponsiveViewport;
import java.util.Locale;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.MatchNarrator;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchScreen implements Screen {

    private final Main game;
    private final Match match;
    private final Club playerClub;

    private final Stage stage;

    // =========================================================
    // SUBSTITUIÇÕES
    // =========================================================

    private int substitutionsUsed =
        0;

    private final List<Player> substitutedPlayers =
        new ArrayList<>();

    private final List<Player> matchBenchPlayers =
        new ArrayList<>();

    // =========================================================
    // CRONÔMETRO
    // =========================================================

    private int currentMinute =
        0;

    private boolean paused =
        false;

    private boolean goalFrozen =
        false;

    private boolean matchFinished =
        false;

    private boolean halftimeHandled =
        false;

    private boolean awaitingHalftimeLineup =
        false;

    private boolean awaitingInjurySubstitution =
        false;

    private boolean awaitingRedCardTactics =
        false;

    private boolean simulateAfterHalftime =
        false;

    private float timeScale =
        1.0f;

    private final float baseInterval =
        0.5f;

    private float currentInterval =
        0.5f;

    private Timer.Task matchTimerTask;

    // =========================================================
    // UI
    // =========================================================

    private Label scoreLabel;
    private Label minuteLabel;

    private Label homePossessionLabel;
    private Label awayPossessionLabel;
    private Label homeShotsLabel;
    private Label awayShotsLabel;
    private Label homeTargetShotsLabel;
    private Label awayTargetShotsLabel;
    private Label homeCornersLabel;
    private Label awayCornersLabel;
    private Label homeFoulsLabel;
    private Label awayFoulsLabel;
    private Label homeXgLabel;
    private Label awayXgLabel;

    private Label matchStateLabel;

    private MiniTacticalField tacticalField;

    private Table eventTable;
    private ScrollPane eventScroll;

    private Table momentumBars;
    private final List<Float> momentumHistory =
        new ArrayList<>();

    private Table goalOverlay;

    private TextButton pauseButton;
    private TextButton speed1xButton;
    private TextButton speed2xButton;

    private Texture homeLogoTexture;
    private Texture awayLogoTexture;

    public MatchScreen(
        Main game,
        Match match,
        Club playerClub
    ) {

        this.game =
            game;

        this.match =
            match;

        this.playerClub =
            playerClub;

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        homeLogoTexture =
            loadLogo(
                match.getHomeTeam()
            );

        awayLogoTexture =
            loadLogo(
                match.getAwayTeam()
            );
    }

    // =========================================================
    // SHOW
    // =========================================================

    @Override
    public void show() {

        Gdx.input.setInputProcessor(
            stage
        );

        game.matchEngine.prepareMatchLineups(match);

        initializeMatchBench();

        buildUI();

        buildGoalOverlay();

        startTimer();
    }

    // =========================================================
    // UI
    // =========================================================

    private void buildUI() {

        stage.clear();

        Stack root =
            new Stack();

        root.setFillParent(
            true
        );

        stage.addActor(
            root
        );

        root.add(
            new Image(
                game.background
            )
        );

        Image darkOverlay =
            new Image(
                StyleFactory.createSolid(
                    new Color(
                        0f,
                        0.02f,
                        0.015f,
                        0.38f
                    )
                )
            );

        darkOverlay.setFillParent(
            true
        );

        root.add(
            darkOverlay
        );

        Table main =
            new Table();

        main.setFillParent(
            true
        );

        main.pad(
            14f,
            22f,
            14f,
            22f
        );

        // =====================================================
        // SCOREBOARD
        // =====================================================

        main
            .add(
                createScoreboard()
            )
            .growX()
            .height(108f)
            .padBottom(10f)
            .row();

        // =====================================================
        // MATCH BODY
        // =====================================================

        Table body =
            new Table();

        body
            .add(
                createFieldPanel()
            )
            .grow()
            .padRight(12f);

        body
            .add(
                createRightColumn()
            )
            .width(440f)
            .growY();

        main
            .add(body)
            .grow()
            .padBottom(12f)
            .row();

        // =====================================================
        // CONTROLS
        // =====================================================

        main
            .add(
                createControlBar()
            )
            .growX()
            .height(68f);

        root.add(
            main
        );
    }

    // =========================================================
    // SCOREBOARD
    // =========================================================

    private Table createScoreboard() {

        Table bar =
            ScreenUI.createPanel();

        bar.pad(
            7f,
            18f,
            7f,
            18f
        );

        matchStateLabel =
            new Label(
                "●  EM JOGO",
                game.skin,
                "font-bold"
            );

        matchStateLabel.setFontScale(0.48f);
        matchStateLabel.setColor(ScreenUI.SUCCESS);

        bar
            .add(matchStateLabel)
            .width(132f)
            .left()
            .padRight(10f);

        // =====================================================
        // HOME
        // =====================================================

        Table home =
            createScoreTeam(
                match.getHomeTeam(),
                homeLogoTexture,
                true
            );

        bar
            .add(home)
            .expandX()
            .right();

        // =====================================================
        // SCORE
        // =====================================================

        Table center =
            new Table();

        scoreLabel =
            new Label(
                match.getHomeGoals() +
                    "   -   " +
                    match.getAwayGoals(),
                game.skin,
                "font-title"
            );

        scoreLabel.setFontScale(1.18f);

        scoreLabel.setColor(
            Color.WHITE
        );

        scoreLabel.setAlignment(
            Align.center
        );

        center
            .add(scoreLabel)
            .width(200f)
            .center()
            .row();

        minuteLabel =
            new Label(
                "00'",
                game.skin,
                "font-bold"
            );

        minuteLabel.setFontScale(
            0.58f
        );

        minuteLabel.setColor(
            StyleFactory.GOLD
        );

        Table minuteCard =
            ScreenUI.createSubtlePanel();

        minuteCard.add(minuteLabel).pad(3f, 16f, 3f, 16f);

        center
            .add(minuteCard)
            .center()
            .padTop(1f);

        bar
            .add(center)
            .width(230f)
            .center();

        // =====================================================
        // AWAY
        // =====================================================

        bar
            .add(
                createScoreTeam(
                    match.getAwayTeam(),
                    awayLogoTexture,
                    false
                )
            )
            .expandX()
            .left();

        return bar;
    }

    private Table createScoreTeam(
        Club club,
        Texture logoTexture,
        boolean home
    ) {

        Table team =
            new Table();

        Table identity =
            new Table();

        Label name =
            createScoreName(
                club
            );

        Label formation =
            ScreenUI.createBoldValue(
                game.skin,
                getFormationName(club),
                StyleFactory.SOFT_YELLOW,
                home ? Align.right : Align.left
            );

        formation.setFontScale(0.46f);

        identity.add(name).expandX().right().row();
        identity.add(formation).expandX().right().padTop(2f);

        if (
            home
        ) {

            team
                .add(identity)
                .right()
                .padRight(10f);

            if (
                logoTexture != null
            ) {

                Image logo =
                    new Image(
                        new TextureRegionDrawable(
                            logoTexture
                        )
                    );

                logo.setScaling(
                    Scaling.fit
                );

                team
                    .add(logo)
                    .width(78f)
                    .height(70f);
            }

        } else {

            if (
                logoTexture != null
            ) {

                Image logo =
                    new Image(
                        new TextureRegionDrawable(
                            logoTexture
                        )
                    );

                logo.setScaling(
                    Scaling.fit
                );

                team
                    .add(logo)
                    .width(78f)
                    .height(70f);
            }

            identity.getCell(formation).align(Align.left);
            identity.getCell(name).align(Align.left);
            team
                .add(identity)
                .left()
                .padLeft(10f);
        }

        return team;
    }

    private Label createScoreName(
        Club club
    ) {

        Label label =
            new Label(
                ScreenUI.shorten(
                    club.getName()
                        .toUpperCase(),
                    25
                ),
                game.skin,
                "font-bold"
            );

        label.setFontScale(
            0.66f
        );

        label.setColor(
            StyleFactory.CREME_AGED
        );

        return label;
    }

    // =========================================================
    // FIELD
    // =========================================================

    private Table createFieldPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Table title =
            new Table();

        title
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "CAMPO TÁTICO"
                )
            )
            .left()
            .expandX();

        Label formations =
            ScreenUI.createSubtitle(
                game.skin,
                getFormationName(
                    match.getHomeTeam()
                ) +
                    "  •  " +
                    getFormationName(
                        match.getAwayTeam()
                    )
            );

        title
            .add(formations)
            .right();

        panel
            .add(title)
            .growX()
            .padBottom(8f)
            .row();

        tacticalField =
            new MiniTacticalField();

        tacticalField.setTeams(
            match.getHomeTeam(),
            match.getAwayTeam()
        );

        tacticalField.setAnimationSpeed(
            1.1f
        );

        panel
            .add(tacticalField)
            .grow()
            .padBottom(8f)
            .row();

        panel
            .add(
                createMatchMomentumPanel()
            )
            .growX()
            .height(74f);

        return panel;
    }

    private Table createMatchMomentumPanel() {

        Table panel =
            ScreenUI.createSubtlePanel();

        Table heading =
            new Table();

        Label title =
            ScreenUI.createSubtitle(
                game.skin,
                "IMPACTO DA PARTIDA"
            );

        title.setFontScale(0.48f);
        title.setColor(StyleFactory.CREME_AGED);

        heading.add(title).left().expandX();
        heading.add(ScreenUI.createSubtitle(game.skin, "DOMÍNIO AO VIVO")).right();

        panel.add(heading).growX().padBottom(3f).row();

        Table graph =
            new Table();

        if (homeLogoTexture != null) {
            Image homeLogo = new Image(new TextureRegionDrawable(homeLogoTexture));
            homeLogo.setScaling(Scaling.fit);
            graph.add(homeLogo).size(36f).padRight(8f);
        }

        momentumBars = new Table();
        momentumBars.bottom();
        graph.add(momentumBars).growX().height(35f);

        if (awayLogoTexture != null) {
            Image awayLogo = new Image(new TextureRegionDrawable(awayLogoTexture));
            awayLogo.setScaling(Scaling.fit);
            graph.add(awayLogo).size(36f).padLeft(8f);
        }

        panel.add(graph).growX().height(40f);
        renderMomentumBars();

        return panel;
    }

    private void addMomentumSample() {

        float homeControl =
            match.getHomePossession()
                + (float) (match.getHomeXG() * 15d)
                + match.getHomeShots() * 2f
                + match.getHomeShotsOnTarget() * 3f;

        float awayControl =
            match.getAwayPossession()
                + (float) (match.getAwayXG() * 15d)
                + match.getAwayShots() * 2f
                + match.getAwayShotsOnTarget() * 3f;

        float balance = Math.max(
            -1f,
            Math.min(
                1f,
                (homeControl - awayControl) / 35f
            )
        );

        momentumHistory.add(balance);

        if (momentumHistory.size() > 52) {
            momentumHistory.remove(0);
        }

        renderMomentumBars();
    }

    private void renderMomentumBars() {

        if (momentumBars == null) {
            return;
        }

        momentumBars.clearChildren();

        if (momentumHistory.isEmpty()) {
            for (int index = 0; index < 26; index++) {
                momentumHistory.add(0f);
            }
        }

        for (Float value : momentumHistory) {
            float magnitude = Math.abs(value);
            float height = 4f + magnitude * 28f;
            Color color = value > 0.08f
                ? homeStatColor()
                : value < -0.08f
                    ? awayStatColor()
                    : Color.valueOf("7A807A");

            Table column = new Table();
            column.bottom();

            Table bar = new Table();
            bar.background(StyleFactory.createSolid(color));

            column.add().expandY().row();
            column.add(bar).width(7f).height(height);

            momentumBars.add(column).width(9f).height(35f);
        }
    }

    // =========================================================
    // RIGHT COLUMN
    // =========================================================

    private Table createRightColumn() {

        Table column =
            new Table();

        column.top();

        column
            .add(
                createStatsPanel()
            )
            .growX()
            .height(276f)
            .padBottom(10f)
            .row();

        column
            .add(
                createEventsPanel()
            )
            .grow();

        return column;
    }

    // =========================================================
    // STATS
    // =========================================================

    private Table createStatsPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "ESTATÍSTICAS"
                )
            )
            .left()
            .colspan(3)
            .padBottom(8f)
            .row();

        homePossessionLabel = createStatValue("50%", homeStatColor());
        awayPossessionLabel = createStatValue("50%", awayStatColor());
        homeShotsLabel = createStatValue("0", homeStatColor());
        awayShotsLabel = createStatValue("0", awayStatColor());
        homeTargetShotsLabel = createStatValue("0", homeStatColor());
        awayTargetShotsLabel = createStatValue("0", awayStatColor());
        homeCornersLabel = createStatValue("0", homeStatColor());
        awayCornersLabel = createStatValue("0", awayStatColor());
        homeFoulsLabel = createStatValue("0", homeStatColor());
        awayFoulsLabel = createStatValue("0", awayStatColor());
        homeXgLabel = createStatValue("0.00", homeStatColor());
        awayXgLabel = createStatValue("0.00", awayStatColor());

        addStatPair(panel, "POSSE", homePossessionLabel, awayPossessionLabel);
        addStatPair(panel, "FINALIZAÇÕES", homeShotsLabel, awayShotsLabel);
        addStatPair(panel, "NO ALVO", homeTargetShotsLabel, awayTargetShotsLabel);
        addStatPair(panel, "ESCANTEIOS", homeCornersLabel, awayCornersLabel);
        addStatPair(panel, "FALTAS", homeFoulsLabel, awayFoulsLabel);
        addStatPair(panel, "xG", homeXgLabel, awayXgLabel);

        return panel;
    }

    private Label createStatValue(
        String text,
        Color color
    ) {

        Label label =
            new Label(
                text,
                game.skin,
                "font-bold"
            );

        label.setFontScale(0.58f);
        label.setColor(color);
        label.setAlignment(Align.center);

        return label;
    }

    private void addStatPair(
        Table table,
        String title,
        Label homeValue,
        Label awayValue
    ) {

        Table row =
            ScreenUI.createSubtlePanel();

        Label category =
            ScreenUI.createSubtitle(
                game.skin,
                title
            );

        category.setFontScale(0.48f);
        category.setColor(StyleFactory.CREME_AGED);
        category.setAlignment(Align.center);

        row.add(homeValue).width(72f).left();
        row.add(category).expandX().center();
        row.add(awayValue).width(72f).right();

        table
            .add(row)
            .growX()
            .height(29f)
            .padBottom(3f)
            .row();
    }

    private Color homeStatColor() {

        return Color.valueOf("3A9BFF");
    }

    private Color awayStatColor() {

        return Color.valueOf("FF5959");
    }

    // =========================================================
    // EVENTS
    // =========================================================

    private Table createEventsPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "NARRAÇÃO DA PARTIDA"
                )
            )
            .left()
            .padBottom(8f)
            .row();

        eventTable =
            new Table();

        eventTable.top();
        eventTable.left();

        Label kickoff =
            ScreenUI.createValueLabel(
                game.skin,
                "0'   A bola está rolando.",
                ScreenUI.MUTED_TEXT,
                Align.left
            );

        kickoff.setFontScale(0.50f);

        eventTable
            .add(kickoff)
            .growX()
            .left()
            .padBottom(5f)
            .row();

        eventScroll =
            new ScrollPane(
                eventTable,
                game.skin
            );

        eventScroll.setFadeScrollBars(
            false
        );

        panel
            .add(eventScroll)
            .grow();

        return panel;
    }

    // =========================================================
    // CONTROLS
    // =========================================================

    private Table createControlBar() {

        Table bar =
            ScreenUI.createPanel();

        ImageTextButton tactics =
            IconTextButton.create(
                "TÁTICAS",
                game.skin,
                "Icons8/icons8-estrutura-em-árvore-50.png"
            );

        tactics
            .getLabel()
            .setFontScale(
                0.58f
            );

        tactics.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        !goalFrozen &&
                            !matchFinished
                    ) {

                        openTacticsDialog();
                    }
                }
            }
        );

        bar
            .add(tactics)
            .width(155f)
            .height(42f)
            .padRight(8f);

        pauseButton =
            ScreenUI.createInteractiveButton(
                "PAUSAR",
                game.skin
            );

        pauseButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        pauseButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        goalFrozen ||
                            matchFinished
                    ) {

                        return;
                    }

                    paused =
                        !paused;

                    pauseButton.setText(
                        paused
                            ? "CONTINUAR"
                            : "PAUSAR"
                    );

                    matchStateLabel.setText(
                        paused
                            ? "PAUSADO"
                            : "EM JOGO"
                    );

                    matchStateLabel.setColor(
                        paused
                            ? ScreenUI.WARNING
                            : ScreenUI.SUCCESS
                    );
                }
            }
        );

        bar
            .add(pauseButton)
            .width(135f)
            .height(42f)
            .padRight(8f);

        speed1xButton =
            ScreenUI.createInteractiveButton(
                "1x",
                game.skin
            );

        speed2xButton =
            ScreenUI.createInteractiveButton(
                "2.5x",
                game.skin
            );

        speed1xButton
            .getLabel()
            .setFontScale(
                0.54f
            );

        speed2xButton
            .getLabel()
            .setFontScale(
                0.54f
            );

        speed1xButton.setColor(
            StyleFactory.GOLD
        );

        speed1xButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    timeScale =
                        1f;

                    updateSpeedButtons(
                        speed1xButton
                    );
                }
            }
        );

        speed2xButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    timeScale =
                        2.5f;

                    updateSpeedButtons(
                        speed2xButton
                    );
                }
            }
        );

        bar
            .add(speed1xButton)
            .width(58f)
            .height(42f)
            .padRight(4f);

        bar
            .add(speed2xButton)
            .width(68f)
            .height(42f);

        bar
            .add()
            .expandX();

        Label substitutions =
            ScreenUI.createSubtitle(
                game.skin,
                "SUBSTITUIÇÕES: " +
                    substitutionsUsed +
                    "/5"
            );

        bar
            .add(substitutions)
            .padRight(15f);

        ImageTextButton simulate =
            IconTextButton.create(
                "SIMULAR RESTANTE",
                game.skin,
                "Icons8/icons8-ativa-modo-rápido-50.png"
            );

        simulate
            .getLabel()
            .setFontScale(
                0.58f
            );

        simulate.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    simulateRemainingMatch();
                }
            }
        );

        bar
            .add(simulate)
            .width(220f)
            .height(42f);

        return bar;
    }

    // =========================================================
    // TACTICS
    // =========================================================

    private void openTacticsDialog() {

        paused =
            true;

        if (
            pauseButton != null
        ) {

            pauseButton.setText(
                "CONTINUAR"
            );
        }

        if (
            matchStateLabel != null
        ) {

            matchStateLabel.setText(
                "TÁTICAS"
            );

            matchStateLabel.setColor(
                StyleFactory.GOLD
            );
        }

        TacticsDialog dialog =
            new TacticsDialog(
                game,
                playerClub,
                substitutionsUsed,
                substitutedPlayers,
                matchBenchPlayers,
                awaitingInjurySubstitution,

                () -> {

                    applyTacticalChanges();

                    paused =
                        false;

                    pauseButton.setText(
                        "PAUSAR"
                    );

                    matchStateLabel.setText(
                        "EM JOGO"
                    );

                    matchStateLabel.setColor(
                        ScreenUI.SUCCESS
                    );

                    if (
                        awaitingHalftimeLineup
                    ) {

                        awaitingHalftimeLineup =
                            false;

                        if (
                            simulateAfterHalftime
                        ) {

                            simulateAfterHalftime =
                                false;

                            simulateRemainingMatch();

                        } else {

                            scheduleNextMinute(
                                baseInterval /
                                    timeScale
                            );
                        }

                    } else if (
                        awaitingInjurySubstitution
                    ) {

                        awaitingInjurySubstitution =
                            false;

                        scheduleNextMinute(
                            baseInterval /
                                timeScale
                        );

                    } else if (
                        awaitingRedCardTactics
                    ) {

                        awaitingRedCardTactics =
                            false;

                        scheduleNextMinute(
                            baseInterval /
                                timeScale
                        );
                    }
                },

                (
                    outPlayer,
                    inPlayer
                ) -> {

                    match.registerSubstitution(
                        outPlayer,
                        inPlayer,
                        currentMinute,
                        playerClub
                    );

                    substitutionsUsed++;

                    if (
                        outPlayer != null &&
                            !substitutedPlayers
                                .contains(
                                    outPlayer
                                )
                    ) {

                        substitutedPlayers.add(
                            outPlayer
                        );
                    }

                    String description =
                        outPlayer != null
                            ? "Sai " +
                                outPlayer.getName() +
                                ", entra " +
                                inPlayer.getName() +
                                " (" +
                                playerClub.getName() +
                                ")."
                            : "Substituição por lesão: entra " +
                                inPlayer.getName() +
                                " (" +
                                playerClub.getName() +
                                ").";

                    MatchEvent substitution =
                        new MatchEvent(
                            currentMinute,
                            description,
                            "SUBSTITUICAO",
                            playerClub ==
                                match.getHomeTeam()
                        );

                    updateEvents(
                        substitution
                    );

                    applyTacticalChanges();
                },

                () ->
                    applyTacticalChanges()
            );

        dialog.show(
            stage
        );
    }

    private void initializeMatchBench() {

        matchBenchPlayers.clear();

        if (
            playerClub == null
        ) {

            return;
        }

        List<Player> starters =
            new ArrayList<>(
                playerClub.getTacticsMap()
                    .values()
            );

        for (
            Player player :
            playerClub.getSquad()
        ) {

            if (
                !starters.contains(
                    player
                ) &&
                    player.canPlay()
            ) {

                matchBenchPlayers.add(
                    player
                );

                if (
                    matchBenchPlayers.size() ==
                        7
                ) {

                    return;
                }
            }
        }
    }

    private void showHalftimeSequence() {

        halftimeHandled =
            true;

        awaitingHalftimeLineup =
            true;

        paused =
            true;

        refreshMatchStats();

        if (
            pauseButton != null
        ) {

            pauseButton.setText(
                "INTERVALO"
            );
        }

        if (
            matchStateLabel != null
        ) {

            matchStateLabel.setText(
                "INTERVALO"
            );

            matchStateLabel.setColor(
                StyleFactory.SOFT_YELLOW
            );
        }

        Dialog halftimeDialog =
            new Dialog(
                "",
                game.skin
            ) {

                @Override
                protected void result(
                    Object object
                ) {

                    if (
                        Boolean.TRUE.equals(
                            object
                        )
                    ) {

                        openTacticsDialog();
                    }
                }
            };

        halftimeDialog.setModal(
            true
        );

        Table content =
            halftimeDialog.getContentTable();

        content.pad(
            18f,
            24f,
            12f,
            24f
        );

        Label title =
            new Label(
                "INTERVALO",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.88f
        );

        title.setColor(
            StyleFactory.PLAYOFF_GOLD
        );

        title.setAlignment(
            Align.center
        );

        content
            .add(title)
            .width(640f)
            .padBottom(10f)
            .row();

        String scoreText =
            ScreenUI.shorten(
                match.getHomeTeam()
                    .getName(),
                20
            ) +
                "   " +
                match.getHomeGoals() +
                "  -  " +
                match.getAwayGoals() +
                "   " +
                ScreenUI.shorten(
                    match.getAwayTeam()
                        .getName(),
                    20
                );

        Label score =
            new Label(
                scoreText,
                game.skin,
                "font-bold"
            );

        score.setFontScale(
            0.72f
        );

        score.setColor(
            StyleFactory.TEXT_PRIMARY
        );

        score.setAlignment(
            Align.center
        );

        content
            .add(score)
            .width(640f)
            .padBottom(14f)
            .row();

        Table summary =
            ScreenUI.createSubtlePanel();

        addHalftimeStat(
            summary,
            "POSSE",
            match.getHomePossession() +
                "%   -   " +
                match.getAwayPossession() +
                "%"
        );

        addHalftimeStat(
            summary,
            "FINALIZAÇÕES",
            match.getHomeShots() +
                "   -   " +
                match.getAwayShots()
        );

        addHalftimeStat(
            summary,
            "NO ALVO",
            match.getHomeShotsOnTarget() +
                "   -   " +
                match.getAwayShotsOnTarget()
        );

        addHalftimeStat(
            summary,
            "xG",
            String.format(
                Locale.US,
                "%.2f   -   %.2f",
                match.getHomeXG(),
                match.getAwayXG()
            )
        );

        content
            .add(summary)
            .width(500f)
            .padBottom(13f)
            .row();

        Label instruction =
            ScreenUI.createSubtitle(
                game.skin,
                "Confira a escalação e ajuste sua equipe antes do segundo tempo."
            );

        instruction.setAlignment(
            Align.center
        );

        content
            .add(instruction)
            .width(640f)
            .padBottom(4f)
            .row();

        TextButton lineupButton =
            ScreenUI.createPrimaryButton(
                game.skin,
                "VER ESCALAÇÃO"
            );

        lineupButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        halftimeDialog.button(
            lineupButton,
            true
        );

        halftimeDialog.show(
            stage
        );
    }

    private void addHalftimeStat(
        Table table,
        String title,
        String value
    ) {

        table
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    title
                )
            )
            .left()
            .expandX()
            .padBottom(6f);

        table
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    value,
                    StyleFactory.SOFT_YELLOW,
                    Align.right
                )
            )
            .right()
            .padBottom(6f)
            .row();
    }

    private void applyTacticalChanges() {

        if (
            playerClub == null
        ) {

            return;
        }

        List<Player> starters =
            new ArrayList<>(
                playerClub
                    .getTacticsMap()
                    .values()
            );

        playerClub
            .getStartingXI()
            .clear();

        playerClub
            .getStartingXI()
            .addAll(
                starters
            );

        if (
            tacticalField != null
        ) {

            tacticalField.setTeams(
                match.getHomeTeam(),
                match.getAwayTeam()
            );
        }
    }

    // =========================================================
    // TIMER
    // =========================================================

    private void startTimer() {

        scheduleNextMinute(
            baseInterval
        );
    }

    private void scheduleNextMinute(
        float delay
    ) {

        if (
            matchTimerTask != null
        ) {

            matchTimerTask.cancel();
        }

        matchTimerTask =
            Timer.schedule(
                new Timer.Task() {

                    @Override
                    public void run() {

                        if (
                            matchFinished
                        ) {

                            return;
                        }

                        if (
                            currentMinute ==
                                45 &&
                                !halftimeHandled
                        ) {

                            showHalftimeSequence();

                            return;
                        }

                        if (
                            !paused &&
                                !goalFrozen
                        ) {

                            currentMinute++;

                            minuteLabel.setText(
                                currentMinute +
                                    "'"
                            );

                            MatchEvent event =
                                game.matchEngine
                                    .simulateMinute(
                                        match,
                                        currentMinute
                                    );

                            refreshMatchStats();

                            if (
                                event != null
                            ) {

                                updateEvents(
                                    event
                                );

                                boolean injuryNeedsTactics =
                                    shouldOpenInjuryTactics(
                                        event
                                    );

                                boolean redCardNeedsTactics =
                                    shouldOpenRedCardTactics(
                                        event
                                    );

                                if (injuryNeedsTactics) {
                                    awaitingInjurySubstitution =
                                        true;
                                }

                                if (redCardNeedsTactics) {
                                    awaitingRedCardTactics =
                                        true;
                                }

                                if (
                                    isCriticalMatchEvent(
                                        event
                                    )
                                ) {

                                    if (
                                        isPlayerClubEvent(
                                            event
                                        )
                                    ) {
                                        game.queueSquadAlert(
                                            event
                                        );
                                    }

                                    showCriticalEventPopup(
                                        event,
                                        injuryNeedsTactics ||
                                            redCardNeedsTactics
                                    );

                                    return;
                                }

                                processVisualEvent(
                                    event
                                );

                                if (
                                    "GOL".equals(
                                        event.type
                                    )
                                ) {

                                    return;
                                }
                            }
                        }

                        if (
                            currentMinute >=
                                90
                        ) {

                            finishMatch();

                        } else {

                            scheduleNextMinute(
                                currentInterval /
                                    timeScale
                            );
                        }
                    }
                },
                delay
            );
    }

    // =========================================================
    // ALERTAS CRÍTICOS: LESÃO E EXPULSÃO
    // =========================================================

    private boolean isCriticalMatchEvent(
        MatchEvent event
    ) {
        return event != null &&
            (
                "LESIONADO".equals(
                    event.type
                ) ||
                isRedCardEvent(
                    event
                )
            );
    }

    private boolean isPlayerClubEvent(
        MatchEvent event
    ) {
        return event != null &&
            playerClub != null &&
            event.isHomeTeam ==
                (playerClub == match.getHomeTeam());
    }

    /** Mantém alertas do elenco também quando o restante é simulado. */
    private void queueCriticalEventForSquad(
        MatchEvent event
    ) {
        if (
            isCriticalMatchEvent(
                event
            ) &&
            isPlayerClubEvent(
                event
            )
        ) {
            game.queueSquadAlert(
                event
            );
        }
    }

    private boolean isRedCardEvent(
        MatchEvent event
    ) {
        if (
            event == null ||
                !"CARTAO".equals(
                    event.type
                )
        ) {
            return false;
        }

        String description =
            event.description != null
                ? event.description.toUpperCase()
                : "";

        return description.contains(
            "VERMELHO"
        ) || description.contains(
            "EXPULSO"
        );
    }

    /**
     * Pausa a simulação e informa qualquer evento grave. Para o clube do
     * usuário, o alerta oferece uma entrada direta para a tela de táticas.
     */
    private void showCriticalEventPopup(
        final MatchEvent event,
        final boolean needsTactics
    ) {
        paused = true;
        goalFrozen = false;

        if (pauseButton != null) {
            pauseButton.setText(
                "INTERRUPÇÃO"
            );
        }

        if (matchStateLabel != null) {
            matchStateLabel.setText(
                "OCORRÊNCIA"
            );
            matchStateLabel.setColor(
                "LESIONADO".equals(event.type)
                    ? ScreenUI.WARNING
                    : ScreenUI.DANGER
            );
        }

        Dialog dialog = new Dialog(
            "",
            game.skin
        ) {
            @Override
            protected void result(
                Object object
            ) {
                if (
                    Boolean.TRUE.equals(
                        object
                    ) && needsTactics
                ) {
                    openTacticsDialog();
                    return;
                }

                resumeAfterCriticalAlert();
            }
        };

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        boolean injury = "LESIONADO".equals(event.type);
        Color accent = injury
            ? ScreenUI.WARNING
            : ScreenUI.DANGER;

        Table content = dialog.getContentTable();
        content.clear();
        content.background(
            StyleFactory.createMetallicBoard(
                720,
                360,
                Color.valueOf("151A17")
            )
        );
        content.pad(20f, 30f, 16f, 30f);

        Label title = new Label(
            injury
                ? "LESÃO!"
                : "CARTÃO VERMELHO!",
            game.skin,
            "font-title"
        );
        title.setFontScale(0.96f);
        title.setColor(accent);
        title.setAlignment(Align.center);

        Label minute = new Label(
            "MINUTO " + event.minute + "'",
            game.skin,
            "font-bold"
        );
        minute.setFontScale(0.47f);
        minute.setColor(StyleFactory.SOFT_YELLOW);
        minute.setAlignment(Align.center);

        Club affectedClub = event.isHomeTeam
            ? match.getHomeTeam()
            : match.getAwayTeam();

        Label club = new Label(
            affectedClub != null
                ? affectedClub.getName().toUpperCase()
                : "PARTIDA",
            game.skin,
            "font-bold"
        );
        club.setFontScale(0.52f);
        club.setColor(StyleFactory.CREME_AGED);
        club.setAlignment(Align.center);

        Label description = new Label(
            event.description != null
                ? event.description
                : "Ocorrência grave durante a partida.",
            game.skin
        );
        description.setFontScale(0.55f);
        description.setColor(Color.WHITE);
        description.setWrap(true);
        description.setAlignment(Align.center);

        content.add(title)
            .width(640f)
            .center()
            .padBottom(5f)
            .row();

        content.add(minute)
            .width(640f)
            .center()
            .padBottom(9f)
            .row();

        content.add(club)
            .width(640f)
            .center()
            .padBottom(12f)
            .row();

        content.add(description)
            .width(610f)
            .center()
            .padBottom(needsTactics ? 13f : 6f)
            .row();

        if (needsTactics) {
            Label hint = new Label(
                "Seu time pode precisar de uma alteração na escalação.",
                game.skin
            );
            hint.setFontScale(0.48f);
            hint.setColor(ScreenUI.MUTED_TEXT);
            hint.setAlignment(Align.center);

            content.add(hint)
                .width(610f)
                .center()
                .padBottom(4f);
        }

        TextButton continueButton =
            ScreenUI.createInteractiveButton(
                "CONTINUAR",
                game.skin
            );
        continueButton.getLabel().setFontScale(0.56f);
        dialog.button(continueButton, false);

        if (needsTactics) {
            TextButton tacticsButton =
                ScreenUI.createPrimaryButton(
                    game.skin,
                    "AJUSTAR TÁTICAS"
                );
            tacticsButton.getLabel().setFontScale(0.55f);
            dialog.button(tacticsButton, true);
        }

        dialog.show(stage);
    }

    private void resumeAfterCriticalAlert() {
        awaitingInjurySubstitution = false;
        awaitingRedCardTactics = false;
        paused = false;

        if (pauseButton != null) {
            pauseButton.setText(
                "PAUSAR"
            );
        }

        if (matchStateLabel != null) {
            matchStateLabel.setText(
                "EM JOGO"
            );
            matchStateLabel.setColor(
                ScreenUI.SUCCESS
            );
        }

        scheduleNextMinute(
            baseInterval /
                timeScale
        );
    }

    // =========================================================
    // EVENT VISUALS
    // =========================================================

    private void processVisualEvent(
        MatchEvent event
    ) {

        if (
            "GOL".equals(
                event.type
            )
        ) {

            if (
                tacticalField != null
            ) {

                /*
                 * Bloqueia os controles durante a jogada, mas deixa o campo
                 * concluir os passes e a finalização antes do banner de gol.
                 */
                goalFrozen =
                    true;

                tacticalField
                    .setAnimationSpeed(
                        0.78f *
                            timeScale
                    );

                tacticalField
                    .onMatchEvent(
                        event,
                        () ->
                            triggerGoalFreeze(
                                event
                            )
                    );

            } else {

                triggerGoalFreeze(
                    event
                );
            }

            return;
        }

        if (
            tacticalField ==
                null
        ) {

            return;
        }

        if (
            "CHUTE".equals(
                event.type
            )
        ) {

            currentInterval =
                1.4f;

            tacticalField
                    .setAnimationSpeed(
                    0.85f *
                        timeScale
                );

            tacticalField
                .onMatchEvent(
                    event,
                    null
                );

        } else {

            currentInterval =
                0.3f;

            tacticalField
                    .setAnimationSpeed(
                    1.0f *
                        timeScale
                );

            tacticalField
                .onMatchEvent(
                    event,
                    null
                );
        }
    }

    private boolean shouldOpenInjuryTactics(
        MatchEvent event
    ) {

        return event != null &&
            "LESIONADO".equals(
                event.type
            ) &&
            substitutionsUsed < 5 &&
            playerClub != null &&
            event.isHomeTeam ==
                (playerClub ==
                    match.getHomeTeam());
    }

    private boolean shouldOpenRedCardTactics(
        MatchEvent event
    ) {

        if (
            event == null ||
                !"CARTAO".equals(
                    event.type
                ) ||
                playerClub == null ||
                event.isHomeTeam !=
                    (playerClub ==
                        match.getHomeTeam())
        ) {

            return false;
        }

        return isRedCardEvent(
            event
        );
    }

    // =========================================================
    // STATS REFRESH
    // =========================================================

    private void refreshMatchStats() {

        scoreLabel.setText(
            match.getHomeGoals() +
                "   -   " +
                match.getAwayGoals()
        );

        homePossessionLabel.setText(match.getHomePossession() + "%");
        awayPossessionLabel.setText(match.getAwayPossession() + "%");
        homeShotsLabel.setText(String.valueOf(match.getHomeShots()));
        awayShotsLabel.setText(String.valueOf(match.getAwayShots()));
        homeTargetShotsLabel.setText(String.valueOf(match.getHomeShotsOnTarget()));
        awayTargetShotsLabel.setText(String.valueOf(match.getAwayShotsOnTarget()));
        homeCornersLabel.setText(String.valueOf(match.getHomeCorners()));
        awayCornersLabel.setText(String.valueOf(match.getAwayCorners()));
        homeFoulsLabel.setText(String.valueOf(match.getHomeFouls()));
        awayFoulsLabel.setText(String.valueOf(match.getAwayFouls()));
        homeXgLabel.setText(String.format(Locale.US, "%.2f", match.getHomeXG()));
        awayXgLabel.setText(String.format(Locale.US, "%.2f", match.getAwayXG()));

        addMomentumSample();

        if (
            tacticalField != null
        ) {

            tacticalField
                .setPossessionPercent(
                    match.getHomePossession()
                );
        }
    }

    // =========================================================
    // EVENTS
    // =========================================================

    private void updateEvents(
        MatchEvent event
    ) {

        if (
            event == null ||
                eventTable == null
        ) {

            return;
        }

        String commentary =
            MatchNarrator
                .generateCommentary(
                    event,
                    match
                );

        Table block =
            ScreenUI.createSubtlePanel();

        block.pad(6f, 8f, 6f, 8f);

        Color eventColor =
            getEventColor(
                event.type
            );

        Label minute =
            new Label(
                event.minute +
                    "'",
                game.skin,
                "font-bold"
            );

        minute.setFontScale(
            0.53f
        );

        minute.setColor(
            eventColor
        );

        Table minuteBadge =
            new Table();

        minuteBadge.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("18231C"),
                eventColor
            )
        );

        minuteBadge.add(minute).pad(4f, 5f, 4f, 5f);

        block
            .add(minuteBadge)
            .width(40f)
            .height(29f)
            .top()
            .left()
            .padRight(7f);

        Label text =
            new Label(
                commentary,
                game.skin
            );

        text.setFontScale(0.48f);

        text.setWrap(
            true
        );

        text.setColor(Color.WHITE);

        block
            .add(text)
            .growX()
            .left();

        eventTable
            .add(block)
            .growX()
            .padBottom(4f)
            .row();

        eventScroll.layout();

        eventScroll.setScrollPercentY(
            1f
        );
    }

    private Color getEventColor(
        String type
    ) {

        if (
            "GOL".equals(
                type
            )
        ) {

            return StyleFactory.SOFT_YELLOW;
        }

        if (
            "CARTAO".equals(
                type
            )
        ) {

            return Color.YELLOW;
        }

        if (
            "SUBSTITUICAO".equals(
                type
            )
        ) {

            return Color.CYAN;
        }

        if (
            "LESIONADO".equals(
                type
            )
        ) {

            return ScreenUI.DANGER;
        }

        if (
            "CHUTE".equals(
                type
            )
        ) {

            return StyleFactory.CREME_AGED;
        }

        return Color.WHITE;
    }

    // =========================================================
    // GOAL
    // =========================================================

    private void buildGoalOverlay() {

        goalOverlay =
            new Table();

        goalOverlay.setFillParent(
            true
        );

        goalOverlay.setVisible(
            false
        );

        goalOverlay.setTransform(
            true
        );

        stage.addActor(
            goalOverlay
        );
    }

    private void triggerGoalFreeze(
        MatchEvent event
    ) {

        goalFrozen =
            true;

        refreshMatchStats();

        scoreLabel.clearActions();

        scoreLabel.setColor(
            StyleFactory.PLAYOFF_GOLD
        );

        scoreLabel.addAction(
            Actions.sequence(

                Actions.scaleTo(
                    1.28f,
                    1.28f,
                    0.18f,
                    Interpolation.bounceOut
                ),

                Actions.scaleTo(
                    1f,
                    1f,
                    0.18f
                ),

                Actions.color(
                    Color.WHITE,
                    0.45f
                )
            )
        );

        if (
            tacticalField != null
        ) {

            tacticalField
                .triggerGoalCelebration();
        }

        showGoalBanner(
            event
        );

        Timer.schedule(
            new Timer.Task() {

                @Override
                public void run() {

                    if (
                        tacticalField !=
                            null
                    ) {

                        tacticalField
                            .resumeFromGoal();
                    }

                    goalFrozen =
                        false;

                    currentInterval =
                        baseInterval;

                    scheduleNextMinute(
                        baseInterval /
                            timeScale
                    );
                }
            },
            4.5f
        );
    }

    private void showGoalBanner(
        MatchEvent event
    ) {

        goalOverlay.clear();

        goalOverlay.setVisible(
            true
        );

        goalOverlay
            .getColor()
            .a =
            0f;

        goalOverlay.setScale(
            0.55f
        );

        goalOverlay.setOrigin(
            Align.center
        );

        Table banner =
            ScreenUI.createPanel();

        banner.pad(
            22f,
            52f,
            22f,
            52f
        );

        Label goal =
            new Label(
                "GOL!",
                game.skin,
                "font-title"
            );

        goal.setFontScale(
            1.55f
        );

        goal.setColor(
            StyleFactory.PLAYOFF_GOLD
        );

        banner
            .add(goal)
            .center()
            .padBottom(8f)
            .row();

        Label description =
            new Label(
                event.description,
                game.skin,
                "font-bold"
            );

        description.setFontScale(
            0.70f
        );

        description.setColor(
            Color.WHITE
        );

        description.setWrap(
            true
        );

        description.setAlignment(
            Align.center
        );

        banner
            .add(description)
            .width(600f)
            .center();

        goalOverlay
            .add(banner)
            .center();

        goalOverlay.addAction(
            Actions.sequence(

                Actions.parallel(
                    Actions.fadeIn(
                        0.35f
                    ),
                    Actions.scaleTo(
                        1f,
                        1f,
                        0.35f,
                        Interpolation.swingOut
                    )
                ),

                Actions.delay(
                    2.8f
                ),

                Actions.parallel(
                    Actions.fadeOut(
                        0.35f
                    ),
                    Actions.scaleTo(
                        0.75f,
                        0.75f,
                        0.35f
                    )
                ),

                Actions.run(
                    () ->
                        goalOverlay
                            .setVisible(
                                false
                            )
                )
            )
        );
    }

    // =========================================================
    // SIMULATE
    // =========================================================

    private void simulateRemainingMatch() {

        if (
            matchFinished
        ) {

            return;
        }

        if (
            matchTimerTask != null
        ) {

            matchTimerTask.cancel();
        }

        paused =
            false;

        if (
            currentMinute <
                45 &&
                !halftimeHandled
        ) {

            while (
                currentMinute <
                    45
            ) {

                currentMinute++;

                MatchEvent event =
                    game.matchEngine
                        .simulateMinute(
                            match,
                            currentMinute
                        );

                if (
                    event != null
                ) {

                    updateEvents(
                        event
                    );

                    queueCriticalEventForSquad(
                        event
                    );
                }
            }

            minuteLabel.setText(
                "45'"
            );

            refreshMatchStats();

            simulateAfterHalftime =
                true;

            showHalftimeSequence();

            return;
        }

        while (
            currentMinute <
                90
        ) {

            currentMinute++;

            MatchEvent event =
                game.matchEngine
                    .simulateMinute(
                        match,
                        currentMinute
                    );

            if (
                event != null
            ) {

                updateEvents(
                    event
                );

                queueCriticalEventForSquad(
                    event
                );
            }
        }

        minuteLabel.setText(
            "90'"
        );

        refreshMatchStats();

        finishMatch();
    }

    // =========================================================
    // FINISH
    // =========================================================

    private void finishMatch() {

        if (
            matchFinished
        ) {

            return;
        }

        matchFinished =
            true;

        paused =
            true;

        if (
            matchTimerTask != null
        ) {

            matchTimerTask.cancel();
        }

        minuteLabel.setText(
            "90'"
        );

        matchStateLabel.setText(
            "ENCERRADO"
        );

        matchStateLabel.setColor(
            StyleFactory.GOLD
        );

        refreshMatchStats();

        game.matchEngine
            .finalizeMatch(
                match
            );

        game.league
            .advanceMatch();

        showMatchReportDialog();
    }

    private void showMatchReportDialog() {
        MatchResultDialog resultDialog = new MatchResultDialog(
            game,
            match
        ) {
            @Override
            protected void result(
                Object object
            ) {
                returnToSquadScreen();
            }
        };

        resultDialog.show(stage);
    }

    private void returnToSquadScreen() {
        game.setScreen(
            new ClubManagementScreen(
                game,
                playerClub
            )
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void updateSpeedButtons(
        TextButton active
    ) {

        speed1xButton.setColor(
            Color.WHITE
        );

        speed2xButton.setColor(
            Color.WHITE
        );

        active.setColor(
            StyleFactory.GOLD
        );
    }

    private String getFormationName(
        Club club
    ) {

        return club.getFormation() != null
            ? club
            .getFormation()
            .getName()
            : "N/D";
    }

    private Texture loadLogo(
        Club club
    ) {

        if (
            club == null ||
                club.getLogoPath() ==
                    null
        ) {

            return null;
        }

        try {

            if (
                Gdx.files
                    .internal(
                        club.getLogoPath()
                    )
                    .exists()
            ) {

                Texture texture =
                    new Texture(
                        Gdx.files.internal(
                            club.getLogoPath()
                        )
                    );

                texture.setFilter(
                    Texture.TextureFilter.Linear,
                    Texture.TextureFilter.Linear
                );

                return texture;
            }

        } catch (
            Exception ignored
        ) {
        }

        return null;
    }

    // =========================================================
    // SCREEN
    // =========================================================

    @Override
    public void render(
        float delta
    ) {

        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        stage.act(
            delta
        );

        stage.draw();
    }

    @Override
    public void resize(
        int width,
        int height
    ) {

        stage
            .getViewport()
            .update(
                width,
                height,
                true
            );
    }

    @Override
    public void pause() {

        paused =
            true;
    }

    @Override
    public void resume() {

        paused =
            false;
    }

    @Override
    public void hide() {

        if (
            matchTimerTask != null
        ) {

            matchTimerTask.cancel();
        }
    }

    @Override
    public void dispose() {

        if (
            matchTimerTask != null
        ) {

            matchTimerTask.cancel();
        }

        stage.dispose();

        if (
            homeLogoTexture != null
        ) {

            homeLogoTexture.dispose();
        }

        if (
            awayLogoTexture != null
        ) {

            awayLogoTexture.dispose();
        }

        if (
            tacticalField != null
        ) {

            tacticalField.dispose();
        }
    }
}
