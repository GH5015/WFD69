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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarScreen implements Screen {

    private final Main game;
    private final Club playerClub;

    private final Stage stage;

    private Texture calendarBackground;

    private boolean filterOnlyMyClub =
        false;

    private final SimpleDateFormat dateFormat =
        new SimpleDateFormat(
            "EEE, dd/MM",
            new Locale(
                "pt",
                "BR"
            )
        );

    private final SimpleDateFormat monthFormat =
        new SimpleDateFormat(
            "MMMM yyyy",
            new Locale(
                "pt",
                "BR"
            )
        );

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public CalendarScreen(
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
                        "calendario.png"
                    )
                    .exists()
            ) {

                calendarBackground =
                    new Texture(
                        Gdx.files.internal(
                            "calendario.png"
                        )
                    );
            }

        } catch (
            Exception ignored
        ) {

            calendarBackground =
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
            calendarBackground != null
        ) {

            Image bg =
                new Image(
                    calendarBackground
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

            Image tint =
                new Image(
                    StyleFactory.createSolid(
                        new Color(
                            0f,
                            0.03f,
                            0.02f,
                            0.22f
                        )
                    )
                );

            tint.setFillParent(
                true
            );

            root.add(
                tint
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

        String rightInfo =
            "TEMPORADA " +
                game.league
                    .getCurrentSeason();

        page
            .add(
                ScreenUI.createHeader(
                    game.skin,
                    "CALENDÁRIO DA TEMPORADA",
                    rightInfo
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        page
            .add(
                createAnnualLeagueCalendar()
            )
            .growX()
            .height(320f)
            .padBottom(10f)
            .row();

        // =====================================================
        // CONTROLES / RESUMO
        // =====================================================

        page
            .add(
                createControlPanel()
            )
            .growX()
            .height(64f)
            .padBottom(10f)
            .row();

        // =====================================================
        // FIXTURES
        // =====================================================

        Table calendarPanel =
            ScreenUI.createPanel();

        calendarPanel.top();

        Table matches =
            createMatchTable();

        ScrollPane scroll =
            new ScrollPane(
                matches,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        calendarPanel
            .add(scroll)
            .grow();

        page
            .add(calendarPanel)
            .grow()
            .row();

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            playerClub,
            "CALENDÁRIO",
            true
        );

    }

    // =========================================================
    // CALENDÁRIO ANUAL DA WFL
    // =========================================================

    private Table createAnnualLeagueCalendar() {
        Table panel = ScreenUI.createPanel();
        panel.top();

        Table heading = new Table();
        heading.add(ScreenUI.createSectionTitle(game.skin, "CALENDÁRIO ANUAL DA WFL")).left().expandX();
        Label deadline = ScreenUI.createSubtitle(game.skin, "TRADE DEADLINE • 15 DE SETEMBRO");
        deadline.setColor(ScreenUI.WARNING);
        heading.add(deadline).right();
        panel.add(heading).growX().padBottom(5f).row();

        Table table = new Table();
        Table header = ScreenUI.createTableHeaderRow();
        addAnnualHeader(header, "PERÍODO", 145f);
        addAnnualHeader(header, "COMPETIÇÃO", 205f);
        addAnnualHeader(header, "TROCAS", 155f);
        addAnnualHeader(header, "RENOVAÇÃO", 195f);
        addAnnualHeader(header, "FREE AGENCY", 185f);
        addAnnualHeader(header, "DRAFT / SCOUTING", 205f);
        table.add(header).growX().height(29f).row();

        String[][] rows = {
            { "JANEIRO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "FEVEREIRO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "MARÇO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "ABRIL", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "MAIO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "JUNHO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "JULHO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "AGOSTO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "1–15 SETEMBRO", "REGULAR", "ABERTAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "16–30 SETEMBRO", "REGULAR", "ENCERRADAS", "ABERTAS", "FECHADA", "SCOUTING" },
            { "OUTUBRO", "PLAYOFFS", "ENCERRADAS", "ENCERRADA", "FECHADA", "SCOUTING" },
            { "NOVEMBRO", "OFFSEASON", "ABERTAS", "PRÓPRIOS FA", "ABERTA", "SCOUTING" },
            { "DEZEMBRO", "DRAFT / OFFSEASON", "ABERTAS", "ABERTAS", "ABERTA", "DRAFT + SCOUTING" }
        };

        for (int index = 0; index < rows.length; index++) {
            Table row = ScreenUI.createRow(index);
            addAnnualValue(row, rows[index][0], Color.WHITE, 145f);
            addAnnualValue(row, rows[index][1], competitionColor(rows[index][1]), 205f);
            addAnnualValue(row, rows[index][2], calendarStatusColor(rows[index][2]), 155f);
            addAnnualValue(row, rows[index][3], calendarStatusColor(rows[index][3]), 195f);
            addAnnualValue(row, rows[index][4], calendarStatusColor(rows[index][4]), 185f);
            addAnnualValue(row, rows[index][5], calendarStatusColor(rows[index][5]), 205f);
            table.add(row).growX().height(18f).row();
        }

        panel.add(table).growX();
        return panel;
    }

    private void addAnnualHeader(Table table, String text, float width) {
        Label label = ScreenUI.createTableHeaderLabel(game.skin, text, Align.center);
        label.setFontScale(0.40f);
        table.add(label).width(width).center();
    }

    private void addAnnualValue(Table table, String text, Color color, float width) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, Align.center);
        label.setFontScale(0.39f);
        table.add(label).width(width).center();
    }

    private Color calendarStatusColor(String status) {
        if (status.contains("ENCERR") || status.contains("FECHADA")) return ScreenUI.DANGER;
        if (status.contains("SCOUT") || status.contains("DRAFT")) return StyleFactory.SOFT_YELLOW;
        if (status.contains("PRÓPRIOS")) return ScreenUI.WARNING;
        return ScreenUI.SUCCESS;
    }

    private Color competitionColor(String competition) {
        if (competition.contains("PLAYOFF")) return StyleFactory.SOFT_YELLOW;
        if (competition.contains("OFFSEASON") || competition.contains("DRAFT")) return ScreenUI.WARNING;
        return ScreenUI.SUCCESS;
    }

    // =========================================================
    // CONTROLS
    // =========================================================

    private Table createControlPanel() {

        Table panel =
            ScreenUI.createPanel();

        // =====================================================
        // FILTER
        // =====================================================

        ImageTextButton filter =
            IconTextButton.create(
                filterOnlyMyClub
                    ? "MOSTRAR TODOS"
                    : "MEUS JOGOS",
                game.skin,
                "Icons8/icons8-binóculos-50.png"
            );

        filter
            .getLabel()
            .setFontScale(
                0.57f
            );

        filter.setChecked(
            filterOnlyMyClub
        );

        filter.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    filterOnlyMyClub =
                        !filterOnlyMyClub;

                    refreshUI();
                }
            }
        );

        panel
            .add(filter)
            .width(180f)
            .height(42f)
            .padRight(12f);

        // =====================================================
        // GAME COUNTS
        // =====================================================

        int total =
            0;

        int played =
            0;

        int clubMatches =
            0;

        for (
            Match match :
            game.league
                .getSchedule()
        ) {

            total++;

            if (
                match.isPlayed()
            ) {
                played++;
            }

            if (
                isMyMatch(
                    match
                )
            ) {
                clubMatches++;
            }
        }

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "JOGOS",
                    played +
                        "/" +
                        total,
                    StyleFactory.GOLD
                )
            )
            .width(175f)
            .height(42f)
            .padRight(8f);

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "SEU CLUBE",
                    clubMatches +
                        " jogos",
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(180f)
            .height(42f)
            .padRight(8f);

        // =====================================================
        // ROUND
        // =====================================================

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "RODADA",
                    String.valueOf(
                        game.league
                            .getCurrentRound()
                    ),
                    Color.WHITE
                )
            )
            .width(155f)
            .height(42f);

        panel
            .add()
            .expandX();

        Label legend =
            new Label(
                "DOURADO: PRÓXIMO  •  VERDE: SEU CLUBE",
                game.skin,
                "font-bold"
            );

        legend.setFontScale(
            0.48f
        );

        legend.setColor(
            ScreenUI.MUTED_TEXT
        );

        panel
            .add(legend)
            .right();

        return panel;
    }

    // =========================================================
    // MATCH TABLE
    // =========================================================

    private Table createMatchTable() {

        Table container =
            new Table();

        container.top();

        List<Match> matches =
            game.league
                .getSchedule();

        Match nextMatch =
            game.league
                .getNextMatch();

        String lastMonth =
            "";

        int lastWeek =
            -1;

        Calendar calendar =
            Calendar.getInstance();

        int visibleIndex =
            0;

        for (
            Match match :
            matches
        ) {

            boolean myMatch =
                isMyMatch(
                    match
                );

            if (
                filterOnlyMyClub &&
                    !myMatch
            ) {

                continue;
            }

            if (
                match.getDate() ==
                    null
            ) {

                continue;
            }

            calendar.setTime(
                match.getDate()
            );

            int currentWeek =
                calendar.get(
                    Calendar.WEEK_OF_YEAR
                );

            String currentMonth =
                monthFormat.format(
                    match.getDate()
                );

            // =================================================
            // MONTH
            // =================================================

            if (
                !currentMonth.equals(
                    lastMonth
                )
            ) {

                Table monthHeader =
                    ScreenUI.createPanel();

                Label month =
                    new Label(
                        currentMonth.toUpperCase(),
                        game.skin,
                        "font-title"
                    );

                month.setFontScale(
                    0.66f
                );

                month.setColor(
                    StyleFactory.GOLD
                );

                monthHeader
                    .add(month)
                    .left()
                    .expandX();

                Label phase =
                    new Label(
                        match.getStage() !=
                            null
                            ? match.getStage()
                            : "REGULAR",
                        game.skin,
                        "font-bold"
                    );

                phase.setFontScale(
                    0.50f
                );

                phase.setColor(
                    ScreenUI.MUTED_TEXT
                );

                monthHeader
                    .add(phase)
                    .right();

                container
                    .add(monthHeader)
                    .growX()
                    .height(48f)
                    .padTop(
                        visibleIndex == 0
                            ? 0f
                            : 14f
                    )
                    .padBottom(7f)
                    .row();

                lastMonth =
                    currentMonth;

                lastWeek =
                    -1;
            }

            // =================================================
            // WEEK
            // =================================================

            if (
                currentWeek !=
                    lastWeek
            ) {

                Label week =
                    new Label(
                        "SEMANA " +
                            currentWeek,
                        game.skin,
                        "font-bold"
                    );

                week.setFontScale(
                    0.48f
                );

                week.setColor(
                    StyleFactory.SOFT_YELLOW
                );

                container
                    .add(week)
                    .left()
                    .padLeft(6f)
                    .padTop(5f)
                    .padBottom(5f)
                    .row();

                lastWeek =
                    currentWeek;
            }

            // =================================================
            // MATCH
            // =================================================

            boolean next =
                match ==
                    nextMatch;

            Table row =
                createMatchRow(
                    match,
                    visibleIndex,
                    myMatch,
                    next
                );

            container
                .add(row)
                .growX()
                .height(54f)
                .padBottom(4f)
                .row();

            visibleIndex++;
        }

        if (
            visibleIndex ==
                0
        ) {

            Table empty =
                ScreenUI.createSubtlePanel();

            Label text =
                new Label(
                    "Nenhuma partida encontrada para o filtro atual.",
                    game.skin
                );

            text.setColor(
                ScreenUI.MUTED_TEXT
            );

            empty
                .add(text)
                .pad(30f);

            container
                .add(empty)
                .growX()
                .padTop(20f);
        }

        return container;
    }

    private Table createMatchRow(
        Match match,
        int index,
        boolean myMatch,
        boolean next
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        if (
            next
        ) {

            row.background(
                StyleFactory.createRoundedPanel(
                    new Color(
                        0.24f,
                        0.18f,
                        0.03f,
                        0.98f
                    ),
                    StyleFactory.PLAYOFF_GOLD
                )
            );

        } else if (
            myMatch
        ) {

            row.background(
                StyleFactory.createRoundedPanel(
                    new Color(
                        0.035f,
                        0.16f,
                        0.095f,
                        0.96f
                    ),
                    StyleFactory.DARK_GOLD
                )
            );
        }

        // =====================================================
        // DATE
        // =====================================================

        String date =
            dateFormat.format(
                match.getDate()
            );

        Label dateLabel =
            ScreenUI.createBoldValue(
                game.skin,
                date.toUpperCase(),
                match.isPlayed()
                    ? ScreenUI.MUTED_TEXT
                    : Color.WHITE,
                Align.left
            );

        row
            .add(dateLabel)
            .width(155f)
            .padLeft(14f);

        // =====================================================
        // HOME
        // =====================================================

        Label home =
            new Label(
                ScreenUI.shorten(
                    match.getHomeTeam()
                        .getName(),
                    24
                ),
                game.skin,
                "font-bold"
            );

        home.setFontScale(
            0.59f
        );

        home.setAlignment(
            Align.right
        );

        home.setColor(
            match.getHomeTeam() ==
                playerClub
                ? StyleFactory.GOLD
                : StyleFactory.CREME_AGED
        );

        row
            .add(home)
            .expandX()
            .right();

        // =====================================================
        // SCORE / VS
        // =====================================================

        String result;

        if (
            match.isPlayed()
        ) {

            result =
                match.getHomeGoals() +
                    "  -  " +
                    match.getAwayGoals();

        } else {

            result =
                "VS";
        }

        Label score =
            new Label(
                result,
                game.skin,
                "font-bold"
            );

        score.setFontScale(
            0.68f
        );

        score.setAlignment(
            Align.center
        );

        score.setColor(
            next
                ? StyleFactory.PLAYOFF_GOLD
                : Color.WHITE
        );

        row
            .add(score)
            .width(88f)
            .center();

        // =====================================================
        // AWAY
        // =====================================================

        Label away =
            new Label(
                ScreenUI.shorten(
                    match.getAwayTeam()
                        .getName(),
                    24
                ),
                game.skin,
                "font-bold"
            );

        away.setFontScale(
            0.59f
        );

        away.setAlignment(
            Align.left
        );

        away.setColor(
            match.getAwayTeam() ==
                playerClub
                ? StyleFactory.GOLD
                : StyleFactory.CREME_AGED
        );

        row
            .add(away)
            .expandX()
            .left();

        // =====================================================
        // STATUS
        // =====================================================

        String statusText;

        Color statusColor;

        if (
            next
        ) {

            statusText =
                "PRÓXIMO";

            statusColor =
                StyleFactory.PLAYOFF_GOLD;

        } else if (
            match.isPlayed()
        ) {

            statusText =
                "FINAL";

            statusColor =
                ScreenUI.MUTED_TEXT;

        } else {

            statusText =
                match.getStage() !=
                    null
                    ? match.getStage()
                    : "AGENDADO";

            statusColor =
                ScreenUI.MUTED_TEXT;
        }

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                statusText,
                next
                    ? StyleFactory.DARK_GOLD
                    : Color.valueOf(
                    "3C4540"
                )
            );

        row
            .add(badge)
            .width(115f)
            .height(26f)
            .padRight(12f);

        return row;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean isMyMatch(
        Match match
    ) {

        return
            match.getHomeTeam() ==
                playerClub ||
                match.getAwayTeam() ==
                    playerClub;
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
            calendarBackground != null
        ) {

            calendarBackground.dispose();
        }
    }
}
