package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.PlayerStats;
import io.github.some_example_name.model.PlayoffSeries;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandingsScreen implements Screen {

    private final Main game;
    private final Club playerClub;

    private final Stage stage;

    private Texture backgroundTexture;

    private String viewMode =
        "CLASSIFICACAO";

    private String tableScope =
        "CONFERENCIAS";

    private String statsType =
        "Gols";

    private final Map<String, Texture> clubLogos =
        new HashMap<>();

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public StandingsScreen(
        Main game,
        Club playerClub
    ) {

        this.game =
            game;

        this.playerClub =
            playerClub;

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        try {

            if (
                Gdx.files
                    .internal(
                        "tabela.png"
                    )
                    .exists()
            ) {

                backgroundTexture =
                    new Texture(
                        Gdx.files.internal(
                            "tabela.png"
                        )
                    );
            }

        } catch (
            Exception ignored
        ) {

            backgroundTexture =
                null;
        }
    }

    // =========================================================
    // SHOW
    // =========================================================

    @Override
    public void show() {

        Gdx.input.setInputProcessor(
            stage
        );

        refreshUI();
    }

    // =========================================================
    // UI
    // =========================================================

    private void refreshUI() {

        stage.clear();

        Stack root =
            new Stack();

        root.setFillParent(
            true
        );

        stage.addActor(
            root
        );

        // =====================================================
        // BACKGROUND
        // =====================================================

        if (
            backgroundTexture != null
        ) {

            Image bg =
                new Image(
                    backgroundTexture
                );

            bg.setFillParent(
                true
            );

            bg.setScaling(
                Scaling.fill
            );

            root.add(
                bg
            );

            Image dark =
                new Image(
                    StyleFactory.createSolid(
                        new Color(
                            0f,
                            0f,
                            0f,
                            0.28f
                        )
                    )
                );

            dark.setFillParent(
                true
            );

            root.add(
                dark
            );

        } else {

            root.add(
                new Image(
                    game.background
                )
            );
        }

        Table page =
            ScreenUI.createPage(
                true
            );

        // =====================================================
        // HEADER
        // =====================================================

        page
            .add(
                ScreenUI.createHeader(
                    game.skin,
                    viewMode.equals(
                        "CLASSIFICACAO"
                    )
                        ? "CLASSIFICAÇÃO DA WFL"
                        : "PLAYOFFS DA WFL",
                    "TEMPORADA " +
                        game.league
                            .getCurrentSeason()
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        // =====================================================
        // CONTROLES
        // =====================================================

        page
            .add(
                createControls()
            )
            .growX()
            .height(62f)
            .padBottom(10f)
            .row();

        // =====================================================
        // CONTEÚDO
        // =====================================================

        if (
            viewMode.equals(
                "CLASSIFICACAO"
            )
        ) {

            page
                .add(
                    createClassificationView()
                )
                .grow()
                .row();

        } else {

            page
                .add(
                    createPlayoffsView()
                )
                .grow()
                .row();
        }

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            playerClub,
            "TABELA",
            true
        );

    }

    // =========================================================
    // CONTROLES
    // =========================================================

    private Table createControls() {

        Table panel =
            ScreenUI.createPanel();

        // =====================================================
        // TABELA
        // =====================================================

        ImageTextButton tableButton =
            IconTextButton.create(
                "CLASSIFICAÇÃO",
                game.skin,
                "Icons8/icons8-lista-50.png"
            );

        tableButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        tableButton.setChecked(
            viewMode.equals(
                "CLASSIFICACAO"
            )
        );

        tableButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    viewMode =
                        "CLASSIFICACAO";

                    refreshUI();
                }
            }
        );

        panel
            .add(tableButton)
            .width(185f)
            .height(42f)
            .padRight(7f);

        // =====================================================
        // PLAYOFF
        // =====================================================

        ImageTextButton playoffsButton =
            IconTextButton.create(
                "PLAYOFFS",
                game.skin,
                "Icons8/icons8-estádio-50.png"
            );

        playoffsButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        playoffsButton.setChecked(
            viewMode.equals(
                "PLAYOFFS"
            )
        );

        playoffsButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    viewMode =
                        "PLAYOFFS";

                    refreshUI();
                }
            }
        );

        panel
            .add(playoffsButton)
            .width(155f)
            .height(42f);

        panel
            .add()
            .expandX();

        // =====================================================
        // ESCOPO DA CLASSIFICAÇÃO
        // =====================================================

        if (
            viewMode.equals(
                "CLASSIFICACAO"
            )
        ) {

            ImageTextButton conferenceButton =
                IconTextButton.create(
                    "CONFERÊNCIAS",
                    game.skin,
                    "Icons8/icons8-quatro-quadrados-50.png"
                );

            conferenceButton
                .getLabel()
                .setFontScale(
                    0.55f
                );

            conferenceButton.setChecked(
                tableScope.equals(
                    "CONFERENCIAS"
                )
            );

            conferenceButton.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        tableScope =
                            "CONFERENCIAS";

                        refreshUI();
                    }
                }
            );

            panel
                .add(conferenceButton)
                .width(190f)
                .height(42f)
                .padRight(7f);

            ImageTextButton generalButton =
                IconTextButton.create(
                    "GERAL",
                    game.skin,
                    "Icons8/icons8-estrutura-em-árvore-50.png"
                );

            generalButton
                .getLabel()
                .setFontScale(
                    0.55f
                );

            generalButton.setChecked(
                tableScope.equals(
                    "GERAL"
                )
            );

            generalButton.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        tableScope =
                            "GERAL";

                        refreshUI();
                    }
                }
            );

            panel
                .add(generalButton)
                .width(145f)
                .height(42f);
        }

        return panel;
    }

    // =========================================================
    // CLASSIFICAÇÃO
    // =========================================================

    private Table createClassificationView() {

        Table root =
            new Table();

        root.top();

        float usableWidth =
            Math.max(
                360f,
                Gdx.graphics
                    .getWidth() -
                    ScreenUI.PAGE_LEFT_OPEN -
                    ScreenUI.PAGE_RIGHT
            );

        // =====================================================
        // CLASSIFICAÇÃO
        // =====================================================

        Table standingsPanel =
            ScreenUI.createPanel();

        standingsPanel.top();

        if (
            tableScope.equals(
                "CONFERENCIAS"
            )
        ) {

            ScrollPane standingsScroll =
                new ScrollPane(
                    createConferenceTables(),
                    game.skin
                );

            standingsScroll.setFadeScrollBars(
                false
            );

            standingsScroll.setScrollingDisabled(
                false,
                false
            );

            standingsPanel
                .add(standingsScroll)
                .grow();

        } else {

            ScrollPane standingsScroll =
                new ScrollPane(
                    createSingleTable(
                        null
                    ),
                    game.skin
                );

            standingsScroll.setFadeScrollBars(
                false
            );

            standingsScroll.setScrollingDisabled(
                false,
                false
            );

            standingsPanel
                .add(standingsScroll)
                .grow();
        }

        // =====================================================
        // LÍDERES
        // =====================================================

        Table statsPanel =
            createPlayerStatsPanel();

        if (
            usableWidth >=
                1120f
        ) {

            root
                .add(standingsPanel)
                .width(
                    usableWidth *
                        0.64f
                )
                .growY()
                .padRight(10f);

            root
                .add(statsPanel)
                .width(
                    usableWidth *
                        0.33f
                )
                .growY();

        } else {

            float contentHeight =
                Math.max(
                    280f,
                    stage.getHeight() -
                        ScreenUI.PAGE_TOP -
                        ScreenUI.PAGE_BOTTOM -
                        ScreenUI.HEADER_HEIGHT -
                        104f
                );

            float standingsHeight =
                contentHeight * 0.60f;

            float statsHeight =
                contentHeight -
                    standingsHeight -
                    10f;

            root
                .add(standingsPanel)
                .growX()
                .height(standingsHeight)
                .padBottom(10f)
                .row();

            root
                .add(statsPanel)
                .growX()
                .height(statsHeight)
                .row();
        }

        return root;
    }

    private Table createConferenceTables() {

        Table container =
            new Table();

        container.top();

        container
            .add(
                createSingleTable(
                    "Ocidental"
                )
            )
            .growX()
            .padBottom(14f)
            .row();

        container
            .add(
                createSingleTable(
                    "Oriental"
                )
            )
            .growX()
            .row();

        return container;
    }

    // =========================================================
    // TABELA
    // =========================================================

    private Table createSingleTable(
        String conference
    ) {

        Table panel =
            new Table();

        panel.top();

        String titleText =
            conference == null
                ? "CLASSIFICAÇÃO GERAL"
                : "CONFERÊNCIA " +
                conference.toUpperCase();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                titleText
            );

        panel
            .add(title)
            .left()
            .padBottom(8f)
            .row();

        Table table =
            new Table();

        table.top();

        // =====================================================
        // HEADER
        // =====================================================

        Table header =
            ScreenUI.createTableHeaderRow();

        addHeader(
            header,
            "#",
            42f,
            Align.center
        );

        addHeader(
            header,
            "",
            55f,
            Align.center
        );

        addHeader(
            header,
            "CLUBE",
            250f,
            Align.left
        );

        addHeader(
            header,
            "PTS",
            56f,
            Align.center
        );

        addHeader(
            header,
            "J",
            46f,
            Align.center
        );

        addHeader(
            header,
            "V",
            46f,
            Align.center
        );

        addHeader(
            header,
            "E",
            46f,
            Align.center
        );

        addHeader(
            header,
            "D",
            46f,
            Align.center
        );

        addHeader(
            header,
            "SG",
            55f,
            Align.center
        );

        addHeader(
            header,
            "CS",
            50f,
            Align.center
        );

        table
            .add(header)
            .growX()
            .height(42f)
            .row();

        // =====================================================
        // ROWS
        // =====================================================

        List<StandingsRow> rows =
            game.league
                .getFullStandings(
                    conference
                );

        int playoffCutoff =
            conference == null
                ? 8
                : conference.equals(
                "Ocidental"
            )
                ? 6
                : 2;

        int position =
            1;

        for (
            StandingsRow standings :
            rows
        ) {

            boolean userClub =
                standings.club ==
                    playerClub;

            boolean playoffZone =
                position <=
                    playoffCutoff;

            Table row =
                ScreenUI.createRow(
                    position - 1
                );

            if (
                userClub
            ) {

                row.background(
                    StyleFactory.createRoundedPanel(
                        new Color(
                            0.16f,
                            0.13f,
                            0.03f,
                            0.96f
                        ),
                        StyleFactory.GOLD
                    )
                );
            }

            // =================================================
            // POS
            // =================================================

            Color positionColor =
                playoffZone
                    ? StyleFactory.PLAYOFF_GOLD
                    : Color.LIGHT_GRAY;

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        position + "º",
                        positionColor,
                        Align.center
                    )
                )
                .width(42f);

            // =================================================
            // LOGO
            // =================================================

            Texture logoTexture =
                loadClubLogo(
                    standings.club
                );

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

                row
                    .add(logo)
                    .width(55f)
                    .height(32f);

            } else {

                row
                    .add()
                    .width(55f);
            }

            // =================================================
            // CLUB
            // =================================================

            TextButton clubButton =
                ScreenUI.createInteractiveButton(
                    ScreenUI.shorten(
                        standings.club
                            .getName(),
                        28
                    ),
                    game.skin,
                    "toggle"
                );

            clubButton
                .getLabel()
                .setFontScale(
                    0.54f
                );

            clubButton
                .getLabel()
                .setAlignment(
                    Align.left
                );

            if (
                userClub
            ) {

                clubButton.setColor(
                    StyleFactory.DARK_GOLD
                );

                clubButton
                    .getLabel()
                    .setColor(
                        StyleFactory.SOFT_YELLOW
                    );
            }

            final Club selectedClub =
                standings.club;

            clubButton.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        game.setScreen(
                            new ClubDetailsScreen(
                                game,
                                selectedClub,
                                playerClub
                            )
                        );
                    }
                }
            );

            row
                .add(clubButton)
                .width(250f)
                .height(36f)
                .left();

            // =================================================
            // STATS
            // =================================================

            addValue(
                row,
                String.valueOf(
                    standings.points
                ),
                56f,
                userClub
                    ? StyleFactory.SOFT_YELLOW
                    : Color.WHITE
            );

            addValue(
                row,
                String.valueOf(
                    standings.matches
                ),
                46f,
                ScreenUI.MUTED_TEXT
            );

            addValue(
                row,
                String.valueOf(
                    standings.wins
                ),
                46f,
                ScreenUI.SUCCESS
            );

            addValue(
                row,
                String.valueOf(
                    standings.draws
                ),
                46f,
                ScreenUI.WARNING
            );

            addValue(
                row,
                String.valueOf(
                    standings.losses
                ),
                46f,
                Color.WHITE
            );

            Color gdColor =
                standings.goalDifference > 0
                    ? ScreenUI.SUCCESS
                    : standings.goalDifference < 0
                    ? ScreenUI.DANGER
                    : Color.WHITE;

            String gdText =
                standings.goalDifference > 0
                    ? "+" +
                    standings.goalDifference
                    : String.valueOf(
                    standings.goalDifference
                );

            addValue(
                row,
                gdText,
                55f,
                gdColor
            );

            addValue(
                row,
                String.valueOf(
                    standings.cleanSheets
                ),
                50f,
                Color.WHITE
            );

            table
                .add(row)
                .growX()
                .height(47f)
                .row();

            // =================================================
            // PLAYOFF CUTOFF
            // =================================================

            if (
                position ==
                    playoffCutoff &&
                    position <
                        rows.size()
            ) {

                Table cutoff =
                    new Table();

                cutoff.background(
                    StyleFactory.createSolid(
                        new Color(
                            0.83f,
                            0.69f,
                            0.22f,
                            0.25f
                        )
                    )
                );

                Label cutoffLabel =
                    new Label(
                        "ZONA DE CLASSIFICAÇÃO AOS PLAYOFFS",
                        game.skin,
                        "font-bold"
                    );

                cutoffLabel.setFontScale(
                    0.43f
                );

                cutoffLabel.setColor(
                    StyleFactory.SOFT_YELLOW
                );

                cutoff
                    .add(cutoffLabel)
                    .center();

                table
                    .add(cutoff)
                    .growX()
                    .height(19f)
                    .row();
            }

            position++;
        }

        panel
            .add(table)
            .growX()
            .row();

        return panel;
    }

    // =========================================================
    // PLAYER STATS
    // =========================================================

    private Table createPlayerStatsPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "LÍDERES DA LIGA"
                )
            )
            .left()
            .padBottom(4f)
            .row();

        Label sub =
            ScreenUI.createSubtitle(
                game.skin,
                "ESTATÍSTICAS INDIVIDUAIS"
            );

        panel
            .add(sub)
            .left()
            .padBottom(10f)
            .row();

        // =====================================================
        // SELECTOR
        // =====================================================

        Table selector =
            new Table();

        String[] types = {
            "Gols",
            "Assists",
            "Amarelos",
            "Vermelhos"
        };

        int typeIndex =
            0;

        for (
            String type :
            types
        ) {

            TextButton button =
                ScreenUI.createSecondaryButton(
                    game.skin,
                    type
                );

            button
                .getLabel()
                .setFontScale(
                    0.48f
                );

            boolean selected =
                type.equals(
                    statsType
                );

            button.setColor(
                selected
                    ? StyleFactory.GOLD
                    : StyleFactory.METAL_DARK
            );

            button
                .getLabel()
                .setColor(
                    selected
                        ? Color.BLACK
                        : Color.WHITE
                );

            button.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        statsType =
                            type;

                        refreshUI();
                    }
                }
            );

            selector
                .add(button)
                .growX()
                .height(38f)
                .padRight(
                    typeIndex % 2 == 0
                        ? 4f
                        : 0f
                )
                .padBottom(4f);

            if (
                typeIndex % 2 == 1
            ) {

                selector.row();
            }

            typeIndex++;
        }

        panel
            .add(selector)
            .growX()
            .padBottom(9f)
            .row();

        // =====================================================
        // TABLE HEADER
        // =====================================================

        Table statsTable =
            new Table();

        statsTable.top();

        Table statsHeader =
            ScreenUI.createTableHeaderRow();

        addHeader(
            statsHeader,
            "#",
            38f,
            Align.center
        );

        addHeader(
            statsHeader,
            "JOGADOR",
            190f,
            Align.left
        );

        addHeader(
            statsHeader,
            "CLUBE",
            125f,
            Align.left
        );

        addHeader(
            statsHeader,
            "VAL",
            52f,
            Align.center
        );

        statsTable
            .add(statsHeader)
            .growX()
            .height(40f)
            .row();

        List<PlayerStats> stats =
            game.league
                .getPlayerStats(
                    statsType
                );

        int rank =
            1;

        for (
            PlayerStats stat :
            stats
        ) {

            if (
                rank >
                    15
            ) {
                break;
            }

            Table row =
                ScreenUI.createRow(
                    rank - 1
                );

            Color rankColor =
                rank == 1
                    ? StyleFactory.PLAYOFF_GOLD
                    : rank == 2
                    ? Color.LIGHT_GRAY
                    : rank == 3
                    ? Color.valueOf(
                    "CD7F32"
                )
                    : Color.WHITE;

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        String.valueOf(
                            rank
                        ),
                        rankColor,
                        Align.center
                    )
                )
                .width(38f);

            String playerName =
                stat.player.getName() +
                    " (" +
                    stat.player.getPosition() +
                    ")";

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        ScreenUI.shorten(
                            playerName,
                            23
                        ),
                        Color.WHITE,
                        Align.left
                    )
                )
                .width(190f)
                .padLeft(6f);

            Club club =
                findClubByPlayer(
                    stat.player
                );

            row
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        club != null
                            ? ScreenUI.shorten(
                            club.getName(),
                            16
                        )
                            : "SEM CLUBE",
                        ScreenUI.MUTED_TEXT,
                        Align.left
                    )
                )
                .width(125f);

            int value =
                statsType.equals(
                    "Gols"
                )
                    ? stat.goals
                    : statsType.equals(
                    "Assists"
                )
                    ? stat.assists
                    : statsType.equals(
                    "Amarelos"
                )
                    ? stat.yellowCards
                    : stat.redCards;

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        String.valueOf(
                            value
                        ),
                        StyleFactory.SOFT_YELLOW,
                        Align.center
                    )
                )
                .width(52f);

            statsTable
                .add(row)
                .growX()
                .height(42f)
                .row();

            rank++;
        }

        ScrollPane scroll =
            new ScrollPane(
                statsTable,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        panel
            .add(scroll)
            .grow();

        return panel;
    }

    // =========================================================
    // PLAYOFFS
    // =========================================================

    private Table createPlayoffsView() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "CHAVE DOS PLAYOFFS"
            );

        panel
            .add(title)
            .center()
            .padBottom(18f)
            .row();

        List<PlayoffSeries> series =
            game.league
                .getPlayoffSeries();

        if (
            series.isEmpty()
        ) {

            Table empty =
                ScreenUI.createSubtlePanel();

            Label message =
                new Label(
                    "Os playoffs ainda não começaram.\n" +
                        "Classificam-se 6 clubes da Conferência Ocidental e 2 da Oriental.",
                    game.skin
                );

            message.setWrap(
                true
            );

            message.setAlignment(
                Align.center
            );

            message.setColor(
                ScreenUI.MUTED_TEXT
            );

            empty
                .add(message)
                .width(600f)
                .pad(30f);

            panel
                .add(empty)
                .center()
                .padTop(40f);

        } else {

            Table bracket =
                new Table();

            bracket.top();

            bracket
                .add(
                    createBracketRound(
                        "QUARTAS DE FINAL",
                        "MELHOR DE 3",
                        new String[] { "QF1", "QF2", "QF3", "QF4" },
                        series
                    )
                )
                .width(300f)
                .top()
                .padRight(18f);

            bracket
                .add(
                    createBracketRound(
                        "SEMIFINAIS",
                        "MELHOR DE 3",
                        new String[] { "SF1", "SF2" },
                        series
                    )
                )
                .width(300f)
                .top()
                .padRight(18f);

            bracket
                .add(
                    createBracketRound(
                        "FINAL",
                        "JOGO ÚNICO",
                        new String[] { "F" },
                        series
                    )
                )
                .width(300f)
                .top();

            panel
                .add(bracket)
                .center();
        }

        return panel;
    }

    private Table createBracketRound(
        String title,
        String format,
        String[] seriesIds,
        List<PlayoffSeries> allSeries
    ) {

        Table column =
            new Table();

        Label roundTitle =
            ScreenUI.createSectionTitle(
                game.skin,
                title
            );

        roundTitle.setFontScale(0.58f);
        column.add(roundTitle).center().padBottom(2f).row();

        Label formatLabel =
            ScreenUI.createSubtitle(game.skin, format);

        formatLabel.setFontScale(0.44f);
        formatLabel.setColor(ScreenUI.MUTED_TEXT);
        column.add(formatLabel).center().padBottom(12f).row();

        if (seriesIds.length == 2) {
            column.add().height(42f).row();
        } else if (seriesIds.length == 1) {
            column.add().height(132f).row();
        }

        for (int i = 0; i < seriesIds.length; i++) {
            column.add(createBracketSeriesCard(findSeries(seriesIds[i], allSeries)))
                .width(286f)
                .height(76f)
                .padBottom(seriesIds.length == 1 ? 0f : 18f)
                .row();

            if (seriesIds.length == 2 && i == 0) {
                column.add().height(76f).row();
            }
        }

        return column;
    }

    private PlayoffSeries findSeries(
        String id,
        List<PlayoffSeries> series
    ) {

        for (PlayoffSeries item : series) {
            if (id.equals(item.getId())) return item;
        }

        return null;
    }

    private Table createBracketSeriesCard(PlayoffSeries series) {

        Table card =
            ScreenUI.createSubtlePanel();

        card.pad(6f, 9f, 6f, 9f);

        if (series == null) {
            Label pending =
                ScreenUI.createSubtitle(game.skin, "A DEFINIR");
            pending.setColor(ScreenUI.MUTED_TEXT);
            card.add(pending).center().expand();
            return card;
        }

        boolean complete = series.isComplete();
        card.background(
            StyleFactory.createRoundedPanel(
                complete ? Color.valueOf("1A3522") : ScreenUI.PANEL_ALT,
                complete ? ScreenUI.SUCCESS : StyleFactory.DARK_GOLD
            )
        );

        addBracketTeam(card, series.getFirstSeed(), series.getFirstSeedWins(), series.getWinner());
        card.row();
        addBracketTeam(card, series.getSecondSeed(), series.getSecondSeedWins(), series.getWinner());
        card.row();

        Label status = ScreenUI.createSubtitle(
            game.skin,
            complete
                ? "CLASSIFICADO"
                : "JOGO " + (series.getGamesPlayed() + 1) + " DE " + series.getBestOf()
        );
        status.setFontScale(0.40f);
        status.setColor(complete ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT);
        card.add(status).right().padTop(2f);

        return card;
    }

    private void addBracketTeam(
        Table card,
        Club club,
        int wins,
        Club winner
    ) {

        Label name = ScreenUI.createBoldValue(
            game.skin,
            ScreenUI.shorten(club.getName(), 24),
            club == winner ? StyleFactory.SOFT_YELLOW : Color.WHITE,
            Align.left
        );
        name.setFontScale(0.48f);
        card.add(name).left().expandX();

        Label score = ScreenUI.createBoldValue(
            game.skin,
            String.valueOf(wins),
            club == winner ? StyleFactory.SOFT_YELLOW : ScreenUI.MUTED_TEXT,
            Align.right
        );
        score.setFontScale(0.54f);
        card.add(score).right().width(25f);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void addHeader(
        Table table,
        String text,
        float width,
        int alignment
    ) {

        table
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    text,
                    alignment
                )
            )
            .width(width);
    }

    private void addValue(
        Table row,
        String text,
        float width,
        Color color
    ) {

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    text,
                    color,
                    Align.center
                )
            )
            .width(width);
    }

    private Club findClubByPlayer(
        Player player
    ) {

        if (
            player == null ||
                game.league == null ||
                game.league.getClubs() ==
                    null
        ) {

            return null;
        }

        if (
            player.getCurrentClub() !=
                null
        ) {

            return player
                .getCurrentClub();
        }

        for (
            Club club :
            game.league
                .getClubs()
        ) {

            if (
                club.getSquad()
                    .contains(
                        player
                    )
            ) {

                return club;
            }
        }

        return null;
    }

    private Texture loadClubLogo(
        Club club
    ) {

        if (
            club == null ||
                club.getLogoPath() ==
                    null
        ) {

            return null;
        }

        String path =
            club.getLogoPath();

        if (
            clubLogos.containsKey(
                path
            )
        ) {

            return clubLogos.get(
                path
            );
        }

        try {

            if (
                Gdx.files
                    .internal(path)
                    .exists()
            ) {

                Texture texture =
                    new Texture(
                        Gdx.files.internal(
                            path
                        )
                    );

                clubLogos.put(
                    path,
                    texture
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {

        stage.dispose();

        if (
            backgroundTexture != null
        ) {

            backgroundTexture.dispose();
        }

        for (
            Texture texture :
            clubLogos.values()
        ) {

            texture.dispose();
        }

        clubLogos.clear();
    }
}
