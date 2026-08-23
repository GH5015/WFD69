package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchResultDialog extends Dialog {

    private final Main game;
    private final Match match;

    private Table contentContainer;

    private int currentTab =
        0;

    private Texture homeLogoTexture;
    private Texture awayLogoTexture;

    public MatchResultDialog(
        Main game,
        Match match
    ) {

        super(
            "",
            game.skin
        );

        this.game =
            game;

        this.match =
            match;

        homeLogoTexture =
            loadLogo(
                match.getHomeTeam()
            );

        awayLogoTexture =
            loadLogo(
                match.getAwayTeam()
            );

        getContentTable()
            .clear();

        buildLayout();
    }

    // =========================================================
    // LAYOUT
    // =========================================================

    private void buildLayout() {

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                1220,
                760,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        root
            .add(
                createHeader()
            )
            .growX()
            .height(205f)
            .padBottom(8f)
            .row();

        root
            .add(
                createTabs()
            )
            .growX()
            .height(45f)
            .padBottom(8f)
            .row();

        contentContainer =
            ScreenUI.createPanel();

        contentContainer.top();

        root
            .add(
                contentContainer
            )
            .grow()
            .height(390f)
            .padBottom(8f)
            .row();

        switchTab(
            0
        );

        ImageTextButton continueButton =
            IconTextButton.create(
                "CONTINUAR",
                game.skin,
                "Icons8/icons8-ok-50.png"
            );

        continueButton
            .getLabel()
            .setFontScale(
                0.65f
            );

        button(
            continueButton,
            true
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table header =
            ScreenUI.createPanel();

        header.pad(
            8f,
            22f,
            8f,
            22f
        );

        Label competition =
            new Label(
                "WFL • FIM DE JOGO",
                game.skin,
                "font-bold"
            );

        competition.setFontScale(
            0.56f
        );

        competition.setColor(
            StyleFactory.GOLD
        );

        header
            .add(competition)
            .colspan(3)
            .center()
            .padBottom(5f)
            .row();

        // =====================================================
        // HOME
        // =====================================================

        Table home =
            createTeamHeader(
                match.getHomeTeam(),
                homeLogoTexture
            );

        // =====================================================
        // SCORE
        // =====================================================

        Table center =
            new Table();

        Label score =
            new Label(
                match.getHomeGoals() +
                    "   -   " +
                    match.getAwayGoals(),
                game.skin,
                "font-title"
            );

        score.setFontScale(
            1.45f
        );

        score.setColor(
            Color.WHITE
        );

        center
            .add(score)
            .center()
            .row();

        Label result =
            new Label(
                getResultDescription(),
                game.skin,
                "font-bold"
            );

        result.setFontScale(
            0.50f
        );

        result.setColor(
            StyleFactory.SOFT_YELLOW
        );

        center
            .add(result)
            .center()
            .padTop(4f);

        // =====================================================
        // AWAY
        // =====================================================

        Table away =
            createTeamHeader(
                match.getAwayTeam(),
                awayLogoTexture
            );

        header
            .add(home)
            .expandX()
            .right();

        header
            .add(center)
            .width(240f)
            .center();

        header
            .add(away)
            .expandX()
            .left()
            .row();

        // =====================================================
        // SCORERS
        // =====================================================

        Table scorers =
            new Table();

        scorers
            .add(
                createScorers(
                    match.getHomeTeam(),
                    true
                )
            )
            .expandX()
            .right()
            .padRight(80f);

        scorers
            .add()
            .width(240f);

        scorers
            .add(
                createScorers(
                    match.getAwayTeam(),
                    false
                )
            )
            .expandX()
            .left()
            .padLeft(80f);

        header
            .add(scorers)
            .colspan(3)
            .growX()
            .padTop(5f)
            .row();

        Label venue =
            new Label(
                match.getHomeTeam()
                    .getStadium(),
                game.skin
            );

        venue.setFontScale(
            0.46f
        );

        venue.setColor(
            ScreenUI.MUTED_TEXT
        );

        header
            .add(venue)
            .colspan(3)
            .center()
            .padTop(5f);

        return header;
    }

    private Table createTeamHeader(
        Club club,
        Texture logoTexture
    ) {

        Table block =
            new Table();

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

            block
                .add(logo)
                .width(155f)
                .height(82f)
                .center()
                .row();
        }

        Label name =
            new Label(
                club.getName()
                    .toUpperCase(),
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.57f
        );

        name.setAlignment(
            Align.center
        );

        name.setColor(
            StyleFactory.GOLD
        );

        block
            .add(name)
            .width(280f)
            .center();

        return block;
    }

    private Table createScorers(
        Club club,
        boolean right
    ) {

        Table table =
            new Table();

        List<Player> scorers =
            getScorersForTeam(
                club
            );

        if (
            scorers.isEmpty()
        ) {

            table.add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Sem gols"
                )
            );

            return table;
        }

        for (
            Player scorer :
            scorers
        ) {

            Label label =
                new Label(
                    scorer.getName(),
                    game.skin
                );

            label.setFontScale(
                0.47f
            );

            label.setColor(
                StyleFactory.CREME_AGED
            );

            label.setAlignment(
                right
                    ? Align.right
                    : Align.left
            );

            table
                .add(label)
                .right()
                .row();
        }

        return table;
    }

    // =========================================================
    // TABS
    // =========================================================

    private Table createTabs() {

        Table bar =
            new Table();

        String[] tabs = {
            "RESUMO",
            "DESTAQUES",
            "JOGADORES"
        };

        for (
            int i = 0;
            i < tabs.length;
            i++
        ) {

            final int tabIndex =
                i;

            TextButton button =
                ScreenUI.createInteractiveButton(
                    tabs[i],
                    game.skin
                );

            button
                .getLabel()
                .setFontScale(
                    0.56f
                );

            button.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        switchTab(
                            tabIndex
                        );
                    }
                }
            );

            bar
                .add(button)
                .growX()
                .height(40f)
                .padRight(
                    i <
                        tabs.length -
                            1
                        ? 6f
                        : 0f
                );
        }

        return bar;
    }

    private void switchTab(
        int tab
    ) {

        currentTab =
            tab;

        contentContainer.clear();

        switch (
            tab
        ) {

            case 1:

                buildHighlightsTab();

                break;

            case 2:

                buildPlayersTab();

                break;

            case 0:
            default:

                buildSummaryTab();

                break;
        }
    }

    // =========================================================
    // SUMMARY
    // =========================================================

    private void buildSummaryTab() {

        Table layout =
            new Table();

        // =====================================================
        // STATS
        // =====================================================

        Table stats =
            ScreenUI.createSubtlePanel();

        stats.top();
        stats.pad(
            18f,
            30f,
            18f,
            30f
        );

        stats
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "ESTATÍSTICAS DA PARTIDA"
                )
            )
            .growX()
            .center()
            .padBottom(16f)
            .row();

        addComparison(
            stats,
            match.getHomePossession() +
                "%",
            "POSSE",
            match.getAwayPossession() +
                "%"
        );

        addComparison(
            stats,
            String.valueOf(
                match.getHomeShots()
            ),
            "FINALIZAÇÕES",
            String.valueOf(
                match.getAwayShots()
            )
        );

        addComparison(
            stats,
            String.valueOf(
                match.getHomeShotsOnTarget()
            ),
            "NO ALVO",
            String.valueOf(
                match.getAwayShotsOnTarget()
            )
        );

        addComparison(
            stats,
            String.format(
                "%.2f",
                match.getHomeXG()
            ),
            "xG",
            String.format(
                "%.2f",
                match.getAwayXG()
            )
        );

        addComparison(
            stats,
            String.valueOf(
                match.getHomeCorners()
            ),
            "ESCANTEIOS",
            String.valueOf(
                match.getAwayCorners()
            )
        );

        addComparison(
            stats,
            String.valueOf(
                match.getHomeFouls()
            ),
            "FALTAS",
            String.valueOf(
                match.getAwayFouls()
            )
        );

        addComparison(
            stats,
            getFormattedCards(
                true
            ),
            "CARTÕES",
            getFormattedCards(
                false
            )
        );

        layout
            .add(stats)
            .grow()
            .padRight(10f);

        // =====================================================
        // SIDE
        // =====================================================

        Table side =
            new Table();

        side.top();

        side
            .add(
                createBestPlayerCard()
            )
            .growX()
            .padBottom(9f)
            .row();

        side
            .add(
                createOtherResults()
            )
            .growX()
            .padBottom(9f)
            .row();

        side
            .add(
                createTopFour()
            )
            .growX();

        layout
            .add(side)
            .width(390f)
            .growY();

        contentContainer
            .add(layout)
            .grow();
    }

    private void addComparison(
        Table table,
        String home,
        String label,
        String away
    ) {

        Table row =
            new Table();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    home,
                    StyleFactory.SOFT_YELLOW,
                    Align.right
                )
            )
            .width(150f)
            .right()
            .padRight(28f);

        row
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    label
                )
            )
            .width(210f)
            .center();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    away,
                    StyleFactory.SOFT_YELLOW,
                    Align.left
                )
            )
            .width(150f)
            .left()
            .padLeft(28f);

        table
            .add(row)
            .growX()
            .height(34f)
            .padBottom(8f)
            .row();
    }

    // =========================================================
    // BEST PLAYER
    // =========================================================

    private Table createBestPlayerCard() {

        Table panel =
            ScreenUI.createSubtlePanel();

        Player best =
            getBestPlayer();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "MELHOR EM CAMPO"
                )
            )
            .left()
            .padBottom(7f)
            .row();

        if (
            best ==
                null
        ) {

            panel
                .add(
                    ScreenUI.createSubtitle(
                        game.skin,
                        "Sem dados"
                    )
                )
                .left();

            return panel;
        }

        Label name =
            new Label(
                best.getName(),
                game.skin,
                "font-title"
            );

        name.setFontScale(
            0.61f
        );

        name.setColor(
            Color.WHITE
        );

        panel
            .add(name)
            .left()
            .row();

        float gradeValue =
            calculatePlayerGrade(
                best
            );

        Label gradeLabel =
            new Label(
                String.format(
                    "%.1f",
                    gradeValue
                ),
                game.skin,
                "font-title"
            );

        gradeLabel.setFontScale(
            1.15f
        );

        gradeLabel.setColor(
            getGradeColor(
                gradeValue
            )
        );

        panel
            .add(gradeLabel)
            .center()
            .padBottom(10f)
            .row();

        long goals =
            countGoals(
                best
            );

        long assists =
            countAssists(
                best
            );

        Label stats =
            ScreenUI.createSubtitle(
                game.skin,
                goals +
                    " gols • " +
                    assists +
                    " assistências"
            );

        panel
            .add(stats)
            .left()
            .padTop(4f);

        return panel;
    }

    // =========================================================
    // OTHER RESULTS
    // =========================================================

    private Table createOtherResults() {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "OUTROS RESULTADOS"
                )
            )
            .left()
            .padBottom(6f)
            .row();

        int count =
            0;

        for (
            Match other :
            game.league
                .getSchedule()
        ) {

            if (
                other ==
                    match ||
                    !other.isPlayed() ||
                    other.getDate() ==
                        null ||
                    match.getDate() ==
                        null ||
                    !other.getDate()
                        .equals(
                            match.getDate()
                        )
            ) {

                continue;
            }

            String result =
                ScreenUI.shorten(
                    other.getHomeTeam()
                        .getName(),
                    14
                )
                    +
                    " " +
                    other.getHomeGoals() +
                    "-" +
                    other.getAwayGoals() +
                    " " +
                    ScreenUI.shorten(
                        other.getAwayTeam()
                            .getName(),
                        14
                    );

            panel
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        result,
                        StyleFactory.CREME_AGED,
                        Align.left
                    )
                )
                .left()
                .padBottom(3f)
                .row();

            count++;

            if (
                count >=
                    4
            ) {

                break;
            }
        }

        if (
            count ==
                0
        ) {

            panel
                .add(
                    ScreenUI.createSubtitle(
                        game.skin,
                        "Nenhum outro jogo concluído."
                    )
                )
                .left();
        }

        return panel;
    }

    // =========================================================
    // TOP 4
    // =========================================================

    private Table createTopFour() {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "TOP 4 DA WFL"
                )
            )
            .left()
            .padBottom(6f)
            .row();

        List<StandingsRow> standings =
            game.league
                .getFullStandings(
                    null
                );

        int limit =
            Math.min(
                4,
                standings.size()
            );

        for (
            int i = 0;
            i < limit;
            i++
        ) {

            StandingsRow row =
                standings.get(i);

            Table line =
                new Table();

            line
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        String.valueOf(
                            i + 1
                        ),
                        i == 0
                            ? StyleFactory.PLAYOFF_GOLD
                            : Color.WHITE,
                        Align.center
                    )
                )
                .width(28f);

            line
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        ScreenUI.shorten(
                            row.club.getName(),
                            20
                        ),
                        Color.WHITE,
                        Align.left
                    )
                )
                .left()
                .expandX();

            line
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        row.points +
                            " pts",
                        StyleFactory.SOFT_YELLOW,
                        Align.right
                    )
                )
                .width(65f);

            panel
                .add(line)
                .growX()
                .padBottom(3f)
                .row();
        }

        return panel;
    }

    // =========================================================
    // HIGHLIGHTS TAB
    // =========================================================

    private void buildHighlightsTab() {

        Table layout =
            new Table();

        Player best =
            getBestPlayer();

        Player worst =
            getWorstPlayer();

        layout
            .add(
                createPerformanceCard(
                    "MELHOR EM CAMPO",
                    best,
                    ScreenUI.SUCCESS
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        layout
            .add(
                createPerformanceCard(
                    "MENOR NOTA",
                    worst,
                    ScreenUI.DANGER
                )
            )
            .grow()
            .uniformX();

        contentContainer
            .add(layout)
            .grow();
    }

    private Table createPerformanceCard(
        String title,
        Player player,
        Color accent
    ) {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        Label titleLabel =
            ScreenUI.createSectionTitle(
                game.skin,
                title
            );

        titleLabel.setColor(
            accent
        );

        panel
            .add(titleLabel)
            .center()
            .padBottom(15f)
            .row();

        if (
            player == null
        ) {

            panel.add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Sem dados"
                )
            );

            return panel;
        }

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                player.getPosition(),
                StyleFactory.getPositionColor(
                    player.getPosition()
                )
            );

        panel
            .add(badge)
            .height(28f)
            .center()
            .padBottom(8f)
            .row();

        Label name =
            new Label(
                player.getName(),
                game.skin,
                "font-title"
            );

        name.setFontScale(
            0.75f
        );

        name.setColor(
            Color.WHITE
        );

        panel
            .add(name)
            .center()
            .padBottom(10f)
            .row();

        // =============================================
        // NOTA
        // =============================================

        float gradeValue =
            calculatePlayerGrade(
                player
            );

        Label gradeLabel =
            new Label(
                String.format(
                    "%.1f",
                    gradeValue
                ),
                game.skin,
                "font-title"
            );

        gradeLabel.setFontScale(
            1.15f
        );

        gradeLabel.setColor(
            getGradeColor(
                gradeValue
            )
        );

        panel
            .add(gradeLabel)
            .center()
            .padBottom(10f)
            .row();

        // =============================================
        // GOLS
        // =============================================

        long goals =
            countGoals(
                player
            );

        // =============================================
        // ASSISTÊNCIAS
        // =============================================

        long assists =
            countAssists(
                player
            );

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "GOLS",
                    String.valueOf(
                        goals
                    ),
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(220f)
            .height(42f)
            .padBottom(6f)
            .row();

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "ASSISTÊNCIAS",
                    String.valueOf(
                        assists
                    ),
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(220f)
            .height(42f)
            .padBottom(6f)
            .row();

        // =============================================
        // CARTÕES
        // =============================================

        String card =
            match.getCards()
                .get(
                    player
                );

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "CARTÕES",
                    card != null
                        ? card
                        : "Nenhum",
                    card != null
                        ? ScreenUI.WARNING
                        : ScreenUI.SUCCESS
                )
            )
            .width(220f)
            .height(42f);

        return panel;
    }

    // =========================================================
    // PLAYERS TAB
    // =========================================================

    private void buildPlayersTab() {

        Table body =
            new Table();

        body
            .add(
                createPlayersColumn(
                    match.getHomeTeam()
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        body
            .add(
                createPlayersColumn(
                    match.getAwayTeam()
                )
            )
            .grow()
            .uniformX();

        contentContainer
            .add(body)
            .grow();
    }

    private Table createPlayersColumn(
        Club club
    ) {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    club.getName()
                        .toUpperCase()
                )
            )
            .left()
            .padBottom(8f)
            .row();

        Table list =
            new Table();

        List<Player> players =
            getUniqueStartingPlayers(
                club
            );

        players.sort(
            Comparator.comparingDouble(
                this::calculatePlayerGrade
            ).reversed()
        );

        int index =
            0;

        for (
            Player player :
            players
        ) {

            Table row =
                ScreenUI.createRow(
                    index++
                );

            row
                .add(
                    ScreenUI.createBadge(
                        game.skin,
                        player.getPosition(),
                        StyleFactory
                            .getPositionColor(
                                player.getPosition()
                            )
                    )
                )
                .width(62f)
                .height(25f);

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        player.getName(),
                        Color.WHITE,
                        Align.left
                    )
                )
                .expandX()
                .left()
                .padLeft(7f);

            float grade =
                calculatePlayerGrade(
                    player
                );

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        String.format(
                            "%.1f",
                            grade
                        ),
                        getGradeColor(
                            grade
                        ),
                        Align.center
                    )
                )
                .width(55f);

            list
                .add(row)
                .growX()
                .height(39f)
                .row();
        }

        ScrollPane scroll =
            new ScrollPane(
                list,
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
    // PLAYER GRADES
    // =========================================================

    private float calculatePlayerGrade(
        Player player
    ) {

        if (
            player ==
                null
        ) {

            return 6f;
        }

        float grade =
            6f;

        boolean home =
            match.getHomeTeam()
                .getStartingXI()
                .contains(
                    player
                );

        int teamGoals =
            home
                ? match.getHomeGoals()
                : match.getAwayGoals();

        int opponentGoals =
            home
                ? match.getAwayGoals()
                : match.getHomeGoals();

        if (
            teamGoals >
                opponentGoals
        ) {

            grade +=
                0.5f;

        } else if (
            teamGoals <
                opponentGoals
        ) {

            grade -=
                0.3f;
        }

        long goals =
            countGoals(
                player
            );

        long assists =
            countAssists(
                player
            );

        grade +=
            goals *
                1.4f;

        grade +=
            assists *
                0.8f;

        String position =
            player.getPosition();

        if (
            "GK".equalsIgnoreCase(
                position
            )
        ) {

            if (
                opponentGoals ==
                    0
            ) {

                grade +=
                    1.2f;

            } else {

                grade -=
                    opponentGoals *
                        0.4f;
            }

        } else if (
            "CB".equalsIgnoreCase(
                position
            ) ||
                "LB".equalsIgnoreCase(
                    position
                ) ||
                "RB".equalsIgnoreCase(
                    position
                )
        ) {

            if (
                opponentGoals ==
                    0
            ) {

                grade +=
                    0.6f;

            } else {

                grade -=
                    opponentGoals *
                        0.2f;
            }
        }

        String card =
            match.getCards()
                .get(
                    player
                );

        if (
            "Amarelo".equalsIgnoreCase(
                card
            ) ||
                "YELLOW".equalsIgnoreCase(
                    card
                )
        ) {

            grade -=
                0.6f;

        } else if (
            "Vermelho".equalsIgnoreCase(
                card
            ) ||
                "RED".equalsIgnoreCase(
                    card
                )
        ) {

            grade -=
                2.2f;
        }

        return Math.max(
            1f,
            Math.min(
                10f,
                grade
            )
        );
    }

    private Player getBestPlayer() {

        List<Player> players =
            getAllStarters();

        return players
            .stream()
            .max(
                Comparator.comparingDouble(
                    this::calculatePlayerGrade
                )
            )
            .orElse(
                null
            );
    }

    private Player getWorstPlayer() {

        List<Player> players =
            getAllStarters();

        return players
            .stream()
            .min(
                Comparator.comparingDouble(
                    this::calculatePlayerGrade
                )
            )
            .orElse(
                null
            );
    }

    private List<Player> getAllStarters() {

        List<Player> players =
            new ArrayList<>();

        players.addAll(
            getUniqueStartingPlayers(
                match.getHomeTeam()
            )
        );

        players.addAll(
            getUniqueStartingPlayers(
                match.getAwayTeam()
            )
        );

        return players;
    }

    private List<Player> getUniqueStartingPlayers(
        Club club
    ) {

        Set<Player> uniquePlayers =
            new LinkedHashSet<>();

        for (
            Player player :
            club.getStartingXI()
        ) {

            if (
                player != null
            ) {

                uniquePlayers.add(
                    player
                );
            }
        }

        return new ArrayList<>(
            uniquePlayers
        );
    }

    private Color getGradeColor(
        float grade
    ) {

        if (
            grade >=
                8f
        ) {

            return StyleFactory.PLAYOFF_GOLD;
        }

        if (
            grade >=
                7f
        ) {

            return ScreenUI.SUCCESS;
        }

        if (
            grade >=
                6f
        ) {

            return Color.WHITE;
        }

        return ScreenUI.DANGER;
    }

    // =========================================================
    // GOALS / CARDS
    // =========================================================

    private long countGoals(
        Player player
    ) {

        return match
            .getGoalScorers()
            .stream()
            .filter(
                scorer ->
                    scorer ==
                        player
            )
            .count();
    }

    private long countAssists(
        Player player
    ) {

        return match
            .getAssisters()
            .stream()
            .filter(
                assister ->
                    assister ==
                        player
            )
            .count();
    }

    private List<Player> getScorersForTeam(
        Club club
    ) {

        List<Player> result =
            new ArrayList<>();

        for (
            Player scorer :
            match.getGoalScorers()
        ) {

            if (
                club.getSquad()
                    .contains(
                        scorer
                    )
            ) {

                result.add(
                    scorer
                );
            }
        }

        return result;
    }

    private String getFormattedCards(
        boolean home
    ) {

        Club team =
            home
                ? match.getHomeTeam()
                : match.getAwayTeam();

        int yellow =
            0;

        int red =
            0;

        for (
            Map.Entry<Player, String> entry :
            match.getCards()
                .entrySet()
        ) {

            if (
                !team.getSquad()
                    .contains(
                        entry.getKey()
                    )
            ) {

                continue;
            }

            String type =
                entry.getValue();

            if (
                "Amarelo".equalsIgnoreCase(
                    type
                ) ||
                    "YELLOW".equalsIgnoreCase(
                        type
                    )
            ) {

                yellow++;

            } else if (
                "Vermelho".equalsIgnoreCase(
                    type
                ) ||
                    "RED".equalsIgnoreCase(
                        type
                    )
            ) {

                red++;
            }
        }

        return yellow +
            "A  " +
            red +
            "V";
    }

    // =========================================================
    // RESULT DESCRIPTION
    // =========================================================

    private String getResultDescription() {

        int home =
            match.getHomeGoals();

        int away =
            match.getAwayGoals();

        if (
            home ==
                away
        ) {

            return "EMPATE";
        }

        Club winner =
            home >
                away
                ? match.getHomeTeam()
                : match.getAwayTeam();

        return "VITÓRIA • " +
            winner.getName()
                .toUpperCase();
    }

    // =========================================================
    // LOGOS
    // =========================================================

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

                return new Texture(
                    Gdx.files.internal(
                        club.getLogoPath()
                    )
                );
            }

        } catch (
            Exception ignored
        ) {
        }

        return null;
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    private void disposeTextures() {

        if (
            homeLogoTexture != null
        ) {

            homeLogoTexture.dispose();

            homeLogoTexture =
                null;
        }

        if (
            awayLogoTexture != null
        ) {

            awayLogoTexture.dispose();

            awayLogoTexture =
                null;
        }
    }

    @Override
    public void hide() {

        super.hide();

        disposeTextures();
    }
}
