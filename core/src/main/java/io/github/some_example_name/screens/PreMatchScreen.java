package io.github.some_example_name.screens;

import io.github.some_example_name.utils.ClubLogoAssets;

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
import io.github.some_example_name.engine.TacticalEngine;
import io.github.some_example_name.engine.TacticalModifiers;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubProfile;
import io.github.some_example_name.model.AttendanceService;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PreMatchScreen implements Screen {

    private final Main game;
    private final Match match;
    private final Club playerClub;

    private final Stage stage;

    private Texture backgroundTexture;
    private Texture homeLogoTexture;
    private Texture awayLogoTexture;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PreMatchScreen(
        Main game,
        Match match,
        Club playerClub
    ) {

        this.game = game;
        this.match = match;
        this.playerClub = playerClub;

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        // =====================================================
        // BACKGROUND
        // =====================================================

        try {

            if (
                Gdx.files
                    .internal(
                        "fundo_pre.png"
                    )
                    .exists()
            ) {

                backgroundTexture =
                    new Texture(
                        Gdx.files.internal(
                            "fundo_pre.png"
                        )
                    );
            }

        } catch (
            Exception ignored
        ) {

            backgroundTexture =
                null;
        }

        // =====================================================
        // CLUB LOGOS
        // =====================================================

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

        /* A prévia precisa refletir a escalação que realmente entrará em
         * campo. Antes, a recomposição da IA ocorria apenas na MatchScreen e
         * esta tela ainda mostrava os buracos deixados por indisponíveis. */
        game.matchEngine.prepareLineupsForPreview(match);
        AttendanceService.ensureAttendance(game.league, match);

        buildUI();
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

        // =====================================================
        // BACKGROUND
        // =====================================================

        if (
            backgroundTexture != null
        ) {

            Image background =
                new Image(
                    new TextureRegionDrawable(
                        backgroundTexture
                    )
                );

            background.setFillParent(
                true
            );

            background.setScaling(
                Scaling.fill
            );

            root.add(
                background
            );

        } else {

            root.add(
                new Image(
                    game.background
                )
            );
        }

        // =====================================================
        // DARK OVERLAY
        // =====================================================

        Image overlay =
            new Image(
                StyleFactory.createSolid(
                    new Color(
                        0f,
                        0.025f,
                        0.018f,
                        0.36f
                    )
                )
            );

        overlay.setFillParent(
            true
        );

        root.add(
            overlay
        );

        // =====================================================
        // MAIN PAGE
        // =====================================================

        Table page =
            new Table();

        page.top();

        /*
         * O rodapé agora é fixo.
         * Reservamos 88px na parte inferior para ele.
         */
        page.pad(
            18f,
            42f,
            88f,
            42f
        );

        // =====================================================
        // HEADER
        // =====================================================

        page
            .add(
                createHeader()
            )
            .growX()
            .height(66f)
            .padBottom(10f)
            .row();

        // =====================================================
        // MATCH HERO
        // =====================================================

        page
            .add(
                createMatchHero()
            )
            .growX()
            .height(260f)
            .padBottom(10f)
            .row();

        // =====================================================
        // LINEUPS
        // =====================================================

        Table middle =
            new Table();

        middle
            .add(
                createLineupPanel(
                    match.getHomeTeam()
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        middle
            .add(
                createLineupPanel(
                    match.getAwayTeam()
                )
            )
            .grow()
            .uniformX();

        page
            .add(middle)
            .grow()
            .padBottom(10f)
            .row();

        // =====================================================
        // ABSENCES + ROUND
        // =====================================================

        Table lower =
            new Table();

        lower
            .add(
                createAbsencesPanel()
            )
            .grow()
            .uniformX()
            .padRight(10f);

        lower
            .add(
                createRoundMatchesPanel()
            )
            .grow()
            .uniformX();

        page
            .add(lower)
            .growX()
            .height(150f)
            .row();

        root.add(
            page
        );

        // =====================================================
        // FIXED BOTTOM BAR
        // =====================================================

        Table actionLayer =
            new Table();

        actionLayer.setFillParent(
            true
        );

        actionLayer.bottom();

        actionLayer.pad(
            0f,
            42f,
            14f,
            42f
        );

        actionLayer
            .add(
                createActions()
            )
            .growX()
            .height(62f);

        root.add(
            actionLayer
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        String date =
            "DATA NÃO DEFINIDA";

        if (
            match != null &&
                match.getDate() != null
        ) {

            date =
                new SimpleDateFormat(
                    "dd 'DE' MMMM 'DE' yyyy",
                    new Locale(
                        "pt",
                        "BR"
                    )
                )
                    .format(
                        match.getDate()
                    )
                    .toUpperCase();
        }

        int rivalry = getRivalryLevel();
        return ScreenUI.createHeader(
            game.skin,
            "WFL • RODADA " +
                (
                    game.league != null
                        ? game.league
                        .getCurrentRound()
                        : 1
                ) + (rivalry > 0 ? " • CLÁSSICO" : ""),
            date
        );
    }

    // =========================================================
    // MATCH HERO
    // =========================================================

    private Table createMatchHero() {

        Table panel =
            ScreenUI.createPanel();

        Club home =
            match.getHomeTeam();

        Club away =
            match.getAwayTeam();

        int[] odds =
            calculateAdvancedOdds(
                home,
                away
            );

        panel.add().expandX();

        panel
            .add(
                createTeamHero(
                    home,
                    homeLogoTexture,
                    true
                )
            )
            .width(350f)
            .growY();

        panel
            .add(
                createProbabilityCenter(
                    odds
                )
            )
            .width(350f)
            .padLeft(45f)
            .padRight(45f)
            .center();

        panel
            .add(
                createTeamHero(
                    away,
                    awayLogoTexture,
                    false
                )
            )
            .width(350f)
            .growY();

        panel.add().expandX();

        return panel;
    }

    // =========================================================
    // TEAM HERO
    // =========================================================

    private Table createTeamHero(
        Club team,
        Texture logo,
        boolean home
    ) {

        Table box =
            new Table();

        box.center();

        // =====================================================
        // LOGO
        // =====================================================

        if (
            logo != null
        ) {

            Image image =
                new Image(
                    new TextureRegionDrawable(
                        logo
                    )
                );

            image.setScaling(
                Scaling.fit
            );

            box
                .add(image)
                .width(155f)
                .height(92f)
                .center()
                .padBottom(4f)
                .row();
        }

        // =====================================================
        // CLUB NAME
        // =====================================================

        Label name =
            new Label(
                team.getName()
                    .toUpperCase(),
                game.skin,
                "font-title"
            );

        name.setFontScale(
            0.70f
        );

        name.setAlignment(
            Align.center
        );

        name.setColor(
            StyleFactory.GOLD
        );

        box
            .add(name)
            .width(300f)
            .center()
            .row();

        // =====================================================
        // POSITION
        // =====================================================

        int position =
            getLeaguePosition(
                team
            );

        int points =
            getLeaguePoints(
                team
            );

        Label info =
            new Label(
                position +
                    "º • " +
                    points +
                    " PTS • " +
                    (
                        home
                            ? "MANDANTE"
                            : "VISITANTE"
                    ),
                game.skin,
                "font-bold"
            );

        info.setFontScale(
            0.49f
        );

        info.setColor(
            ScreenUI.MUTED_TEXT
        );

        box
            .add(info)
            .center()
            .padTop(3f)
            .row();

        // =====================================================
        // FORM
        // =====================================================

        box
            .add(
                createRecentForm(
                    team
                )
            )
            .center()
            .padTop(6f);

        return box;
    }

    // =========================================================
    // PROBABILITY CENTER
    // =========================================================

    private Table createProbabilityCenter(
        int[] odds
    ) {

        Table center =
            new Table();

        int rivalryLevel = getRivalryLevel();
        if (rivalryLevel > 0) {
            center
                .add(createRivalryPresentation(rivalryLevel))
                .width(315f)
                .height(52f)
                .center()
                .padBottom(5f)
                .row();
        }

        Label vs =
            new Label(
                "VS",
                game.skin,
                "font-title"
            );

        vs.setFontScale(
            0.85f
        );

        vs.setColor(
            Color.WHITE
        );

        center
            .add(vs)
            .center()
            .padBottom(6f)
            .row();

        Label title =
            ScreenUI.createSubtitle(
                game.skin,
                "PROBABILIDADES"
            );

        center
            .add(title)
            .center()
            .padBottom(6f)
            .row();

        Table values =
            new Table();

        values
            .add(
                createOddsValue(
                    getShortName(
                        match.getHomeTeam()
                    ),
                    odds[0],
                    StyleFactory.GOLD
                )
            )
            .width(105f);

        values
            .add(
                createOddsValue(
                    "EMPATE",
                    odds[1],
                    Color.LIGHT_GRAY
                )
            )
            .width(105f);

        values
            .add(
                createOddsValue(
                    getShortName(
                        match.getAwayTeam()
                    ),
                    odds[2],
                    StyleFactory.GOLD
                )
            )
            .width(105f);

        center
            .add(values)
            .center()
            .row();

        center
            .add(createAttendancePreview())
            .width(315f)
            .height(44f)
            .center()
            .padTop(7f);

        return center;
    }

    private Table createAttendancePreview() {
        Table box = ScreenUI.createSubtlePanel();
        box.pad(5f, 10f, 5f, 10f);

        int capacity = match.getHomeTeam().getOperationalStadiumCapacity();
        int attendance = match.getAttendance();
        boolean soldOut = capacity > 0 && attendance >= capacity;

        Label title = ScreenUI.createSubtitle(game.skin, "PÚBLICO PREVISTO");
        title.setAlignment(Align.center);
        box.add(title).growX().center().row();

        String valueText = formatAttendance(attendance) + " / " + formatAttendance(capacity)
            + (soldOut ? "  •  LOTADO" : "");
        Label value = ScreenUI.createBoldValue(
            game.skin,
            valueText,
            soldOut ? ScreenUI.SUCCESS : StyleFactory.SOFT_YELLOW,
            Align.center
        );
        value.setFontScale(.48f);
        box.add(value).growX().center().row();

        return box;
    }

    private String formatAttendance(int value) {
        return String.format(Locale.ROOT, "%,d", Math.max(0, value)).replace(',', '.');
    }

    private Table createRivalryPresentation(int level) {
        Table banner = new Table();
        banner.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("351B17"),
                level >= 5 ? Color.valueOf("EF5B43") : StyleFactory.GOLD
            )
        );
        banner.pad(5f, 10f, 5f, 10f);

        Label title = new Label(
            ClubProfile.rivalryLabel(playerClub, getPlayerOpponent()),
            game.skin,
            "font-bold"
        );
        title.setFontScale(.48f);
        title.setColor(level >= 5 ? Color.valueOf("FF806A") : StyleFactory.SOFT_YELLOW);
        title.setAlignment(Align.center);
        banner.add(title).colspan(2).growX().center().row();

        Club opponent = getPlayerOpponent();
        Label pairing = new Label(
            getShortName(playerClub) + "  ×  " + getShortName(opponent),
            game.skin,
            "font-bold"
        );
        pairing.setFontScale(.34f);
        pairing.setColor(StyleFactory.CREME_AGED);
        pairing.setAlignment(Align.left);
        banner.add(pairing).growX().left().padTop(3f);
        banner.add(createRivalryMeter(level)).right().padTop(3f);
        return banner;
    }

    private Table createRivalryMeter(int level) {
        Table meter = new Table();
        for (int index = 0; index < 5; index++) {
            Color color = index < level
                ? (level >= 5 ? Color.valueOf("EF5B43") : StyleFactory.GOLD)
                : Color.valueOf("503A32");
            meter.add(new Image(StyleFactory.createSolid(color)))
                .width(13f).height(7f).padLeft(2f);
        }
        return meter;
    }

    private int getRivalryLevel() {
        return ClubProfile.rivalryLevel(playerClub, getPlayerOpponent());
    }

    private Club getPlayerOpponent() {
        if (match == null || playerClub == null) return null;
        return match.getHomeTeam() == playerClub ? match.getAwayTeam() : match.getHomeTeam();
    }

    // =========================================================
    // ODDS BOX
    // =========================================================

    private Table createOddsValue(
        String label,
        int percentage,
        Color color
    ) {

        Table box =
            ScreenUI.createSubtlePanel();

        Label value =
            new Label(
                percentage +
                    "%",
                game.skin,
                "font-title"
            );

        value.setFontScale(
            0.62f
        );

        value.setColor(
            color
        );

        box
            .add(value)
            .center()
            .row();

        Label name =
            new Label(
                label,
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.43f
        );

        name.setColor(
            ScreenUI.MUTED_TEXT
        );

        box
            .add(name)
            .center();

        return box;
    }

    // =========================================================
    // LINEUP PANEL
    // =========================================================

    private Table createLineupPanel(
        Club team
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        // =====================================================
        // HEADING
        // =====================================================

        Table heading =
            new Table();

        heading
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    team.getName()
                        .toUpperCase()
                )
            )
            .left()
            .expandX();

        String formation =
            team.getFormation() != null
                ? team
                .getFormation()
                .getName()
                : "N/D";

        heading
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    formation,
                    StyleFactory.DARK_GOLD
                )
            )
            .height(27f);

        panel
            .add(heading)
            .growX()
            .padBottom(6f)
            .row();

        // =====================================================
        // TABLE HEADER
        // =====================================================

        Table header =
            ScreenUI.createTableHeaderRow();

        header
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "POS",
                    Align.center
                )
            )
            .width(60f);

        header
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "TITULAR",
                    Align.left
                )
            )
            .expandX();

        header
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "OVR",
                    Align.center
                )
            )
            .width(60f);

        header
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "COND.",
                    Align.center
                )
            )
            .width(70f);

        panel
            .add(header)
            .growX()
            .height(34f)
            .row();

        // =====================================================
        // FORMATION SLOTS
        // =====================================================

        Formation formationObject =
            team.getFormation();

        List<String> slots =
            formationObject != null &&
                formationObject
                    .getPositionSlots() != null
                ? formationObject
                .getPositionSlots()
                : createDefaultSlots();

        // =====================================================
        // PLAYERS
        // =====================================================

        for (
            int i = 0;
            i < 11;
            i++
        ) {

            String slot =
                i < slots.size()
                    ? slots.get(i)
                    : "CM";

            Player player =
                team.getTacticsMap()
                    .get(i);

            Table row =
                ScreenUI.createRow(
                    i
                );

            // =================================================
            // POSITION
            // =================================================

            row
                .add(
                    ScreenUI.createBadge(
                        game.skin,
                        slot,
                        StyleFactory
                            .getPositionColor(
                                slot
                            )
                    )
                )
                .width(60f)
                .height(24f);

            // =================================================
            // NAME
            // =================================================

            String name =
                player != null
                    ? player.getName()
                    : "VAGA NÃO PREENCHIDA";

            Label nameLabel =
                ScreenUI.createBoldValue(
                    game.skin,
                    ScreenUI.shorten(
                        name,
                        22
                    ),
                    player != null
                        ? Color.WHITE
                        : ScreenUI.DANGER,
                    Align.left
                );

            row
                .add(nameLabel)
                .expandX()
                .left()
                .padLeft(7f);

            // =================================================
            // EFFECTIVE OVR
            // =================================================

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        player != null
                            ? String.valueOf(
                            player
                                .getEffectiveOverallForPosition(
                                    slot
                                )
                        )
                            : "-",
                        player != null
                            ? StyleFactory.SOFT_YELLOW
                            : ScreenUI.DANGER,
                        Align.center
                    )
                )
                .width(60f);

            // =================================================
            // CONDITION
            // =================================================

            Color conditionColor =
                player == null
                    ? ScreenUI.DANGER
                    : player.getFatigue() >= 75
                    ? ScreenUI.SUCCESS
                    : player.getFatigue() >= 50
                    ? ScreenUI.WARNING
                    : ScreenUI.DANGER;

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        player != null
                            ? player.getFatigue() +
                            "%"
                            : "-",
                        conditionColor,
                        Align.center
                    )
                )
                .width(70f);

            // =================================================
            // PLAYER DETAILS
            // =================================================

            if (
                player != null
            ) {

                final Player selected =
                    player;

                row.addListener(
                    new ClickListener() {

                        @Override
                        public void clicked(
                            InputEvent event,
                            float x,
                            float y
                        ) {

                            showPlayerModal(
                                selected
                            );
                        }
                    }
                );
            }

            panel
                .add(row)
                .growX()
                .height(36f)
                .row();
        }

        return panel;
    }

    // =========================================================
    // ABSENCES
    // =========================================================

    private Table createAbsencesPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "DESFALQUES"
                )
            )
            .left()
            .colspan(2)
            .padBottom(6f)
            .row();

        panel
            .add(
                createAbsenceColumn(
                    match.getHomeTeam()
                )
            )
            .grow()
            .uniformX()
            .padRight(7f);

        panel
            .add(
                createAbsenceColumn(
                    match.getAwayTeam()
                )
            )
            .grow()
            .uniformX();

        return panel;
    }

    // =========================================================
    // ABSENCE COLUMN
    // =========================================================

    private Table createAbsenceColumn(
        Club team
    ) {

        Table column =
            ScreenUI.createSubtlePanel();

        column.top();

        Label title =
            new Label(
                team.getName()
                    .toUpperCase(),
                game.skin,
                "font-bold"
            );

        title.setFontScale(
            0.48f
        );

        title.setColor(
            StyleFactory.SOFT_YELLOW
        );

        column
            .add(title)
            .left()
            .padBottom(5f)
            .row();

        boolean hasAbsence =
            false;

        int shown =
            0;

        for (
            Player player :
            team.getSquad()
        ) {

            if (
                player.canPlay()
            ) {

                continue;
            }

            hasAbsence =
                true;

            String reason =
                player.isInjured()
                    ? "LES • " +
                    player.getInjuryDaysRemaining() +
                    "D"
                    : "SUS • " +
                    player.getSuspendedMatches() +
                    "J";

            Table row =
                new Table();

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        ScreenUI.shorten(
                            player.getName(),
                            15
                        ),
                        Color.WHITE,
                        Align.left
                    )
                )
                .left()
                .expandX();

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        reason,
                        ScreenUI.DANGER,
                        Align.right
                    )
                )
                .right();

            column
                .add(row)
                .growX()
                .padBottom(3f)
                .row();

            shown++;

            /*
             * Evita que um número grande de lesões
             * expanda o painel e empurre a interface.
             */
            if (
                shown >= 3
            ) {

                break;
            }
        }

        if (
            !hasAbsence
        ) {

            column
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        "Nenhum desfalque",
                        ScreenUI.SUCCESS,
                        Align.left
                    )
                )
                .left();
        }

        return column;
    }

    // =========================================================
    // ROUND MATCHES
    // =========================================================

    private Table createRoundMatchesPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "JOGOS DA RODADA"
                )
            )
            .left()
            .padBottom(6f)
            .row();

        Table list =
            new Table();

        List<Match> matches =
            game.league != null
                ? game.league
                .getCurrentRoundMatches()
                : new ArrayList<Match>();

        if (
            matches == null ||
                matches.isEmpty()
        ) {

            matches =
                new ArrayList<>();

            matches.add(
                match
            );
        }

        int index =
            0;

        for (
            Match roundMatch :
            matches
        ) {

            boolean current =
                roundMatch ==
                    match;

            Table row =
                ScreenUI.createRow(
                    index++
                );

            if (
                current
            ) {

                row.background(
                    StyleFactory.createRoundedPanel(
                        new Color(
                            0.18f,
                            0.14f,
                            0.025f,
                            0.97f
                        ),
                        StyleFactory.GOLD
                    )
                );
            }

            row
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        ScreenUI.shorten(
                            roundMatch
                                .getHomeTeam()
                                .getName(),
                            14
                        ),
                        Color.WHITE,
                        Align.right
                    )
                )
                .expandX()
                .right();

            int[] odds =
                calculateAdvancedOdds(
                    roundMatch
                        .getHomeTeam(),
                    roundMatch
                        .getAwayTeam()
                );

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        odds[0] +
                            "%",
                        StyleFactory.SOFT_YELLOW,
                        Align.center
                    )
                )
                .width(48f);

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        "VS",
                        ScreenUI.MUTED_TEXT,
                        Align.center
                    )
                )
                .width(32f);

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        odds[2] +
                            "%",
                        StyleFactory.SOFT_YELLOW,
                        Align.center
                    )
                )
                .width(48f);

            row
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        ScreenUI.shorten(
                            roundMatch
                                .getAwayTeam()
                                .getName(),
                            14
                        ),
                        Color.WHITE,
                        Align.left
                    )
                )
                .expandX()
                .left();

            list
                .add(row)
                .growX()
                .height(30f)
                .padBottom(2f)
                .row();

            /*
             * Painel pequeno no pré-jogo.
             * Exibe no máximo 4 jogos e mantém
             * os botões sempre visíveis.
             */
            if (
                index >= 4
            ) {

                break;
            }
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
    // FIXED ACTION BAR
    // =========================================================

    private Table createActions() {

        Table bar =
            ScreenUI.createPanel();

        bar.pad(
            7f,
            14f,
            7f,
            14f
        );

        // =====================================================
        // BACK
        // =====================================================

        ImageTextButton back =
            IconTextButton.create(
                "VOLTAR",
                game.skin,
                "Icons8/icons8-logout-arredondado-à-esquerda-50.png"
            );

        back
            .getLabel()
            .setFontScale(
                0.60f
            );

        back.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    game.setScreen(
                        new ClubManagementScreen(
                            game,
                            playerClub
                        )
                    );
                }
            }
        );

        bar
            .add(back)
            .width(155f)
            .height(42f);

        // =====================================================
        // CENTER SPACE
        // =====================================================

        bar
            .add()
            .expandX();

        // =====================================================
        // TACTICS
        // =====================================================

        ImageTextButton tactics =
            IconTextButton.create(
                "AJUSTAR TÁTICA",
                game.skin,
                "Icons8/icons8-estrutura-em-árvore-50.png"
            );

        tactics
            .getLabel()
            .setFontScale(
                0.60f
            );

        tactics.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    game.setScreen(
                        new TacticsScreen(
                            game,
                            playerClub
                        )
                    );
                }
            }
        );

        bar
            .add(tactics)
            .width(210f)
            .height(42f)
            .padRight(10f);

        // =====================================================
        // PLAY MATCH
        // =====================================================

        ImageTextButton play =
            IconTextButton.create(
                "JOGAR PARTIDA",
                game.skin,
                "Icons8/icons8-ligar-50.png"
            );

        play
            .getLabel()
            .setFontScale(
                0.64f
            );

        play.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    String error =
                        validatePlayerClubStatus();

                    if (
                        error != null
                    ) {

                        showValidationModal(
                            error
                        );

                        return;
                    }

                    game.setScreen(
                        new MatchScreen(
                            game,
                            match,
                            playerClub
                        )
                    );
                }
            }
        );

        bar
            .add(play)
            .width(235f)
            .height(42f);

        return bar;
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private String validatePlayerClubStatus() {

        if (
            playerClub == null
        ) {

            return null;
        }

        // =====================================================
        // FORMATION
        // =====================================================

        if (
            playerClub.getFormation() ==
                null
        ) {

            return "Escolha uma formação antes de iniciar a partida.";
        }

        // =====================================================
        // 11 PLAYERS
        // =====================================================

        if (
            playerClub
                .getTacticsMap()
                .size() <
                11
        ) {

            return "A escalação titular precisa possuir 11 jogadores.";
        }

        // =====================================================
        // VALIDATE PLAYERS
        // =====================================================

        for (
            Player starter :
            playerClub
                .getTacticsMap()
                .values()
        ) {

            if (
                starter == null
            ) {

                return "Existem posições vazias na escalação titular.";
            }

            if (
                !starter.canPlay()
            ) {

                if (
                    starter.isInjured()
                ) {

                    return starter.getName() +
                        " está lesionado. Substitua-o antes da partida.";
                }

                if (
                    starter.isSuspended()
                ) {

                    return starter.getName() +
                        " está suspenso. Substitua-o antes da partida.";
                }
            }
        }

        return null;
    }

    // =========================================================
    // VALIDATION DIALOG
    // =========================================================

    private void showValidationModal(
        String message
    ) {

        Dialog dialog =
            new Dialog(
                "",
                game.skin
            );

        Table content =
            dialog.getContentTable();

        content.background(
            StyleFactory.createRoundedPanel(
                StyleFactory.PRUSSIAN_GREEN,
                StyleFactory.GOLD
            )
        );

        content.pad(
            24f
        );

        Label title =
            new Label(
                "ATENÇÃO",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.66f
        );

        title.setColor(
            ScreenUI.WARNING
        );

        content
            .add(title)
            .center()
            .padBottom(12f)
            .row();

        Label text =
            new Label(
                message,
                game.skin
            );

        text.setWrap(
            true
        );

        text.setAlignment(
            Align.center
        );

        text.setColor(
            StyleFactory.CREME_AGED
        );

        content
            .add(text)
            .width(420f)
            .center();

        dialog.button(
            "OK"
        );

        dialog.show(
            stage
        );
    }

    // =========================================================
    // PLAYER DETAILS
    // =========================================================

    private void showPlayerModal(
        Player player
    ) {

        Dialog dialog =
            new Dialog(
                "",
                game.skin
            );

        Table content =
            dialog.getContentTable();

        content.background(
            StyleFactory.createRoundedPanel(
                ScreenUI.PANEL,
                StyleFactory.GOLD
            )
        );

        content.pad(
            22f
        );

        // =====================================================
        // NAME
        // =====================================================

        Label name =
            new Label(
                player.getName()
                    .toUpperCase(),
                game.skin,
                "font-title"
            );

        name.setFontScale(
            0.68f
        );

        name.setColor(
            StyleFactory.GOLD
        );

        content
            .add(name)
            .colspan(2)
            .center()
            .padBottom(12f)
            .row();

        // =====================================================
        // INFO
        // =====================================================

        addPlayerDetail(
            content,
            "POSIÇÃO",
            player.getPosition()
        );

        addPlayerDetail(
            content,
            "OVERALL",
            String.valueOf(
                player.getOverall()
            )
        );

        addPlayerDetail(
            content,
            "IDADE",
            String.valueOf(
                player.getAge()
            )
        );

        addPlayerDetail(
            content,
            "MORAL",
            String.valueOf(
                player.getMorale()
            )
        );

        addPlayerDetail(
            content,
            "CONDIÇÃO",
            player.getFatigue() +
                "%"
        );

        addPlayerDetail(
            content,
            "ATAQUE",
            String.valueOf(
                player
                    .getTechnicalAttributes()
                    .getAtaque()
            )
        );

        addPlayerDetail(
            content,
            "PASSE",
            String.valueOf(
                player
                    .getTechnicalAttributes()
                    .getPasse()
            )
        );

        addPlayerDetail(
            content,
            "DEFESA",
            String.valueOf(
                player
                    .getTechnicalAttributes()
                    .getDefesa()
            )
        );

        addPlayerDetail(
            content,
            "FÍSICO",
            String.valueOf(
                player
                    .getTechnicalAttributes()
                    .getFisico()
            )
        );

        addPlayerDetail(
            content,
            "DRIBLE",
            String.valueOf(
                player
                    .getTechnicalAttributes()
                    .getDrible()
            )
        );

        dialog.button(
            "FECHAR"
        );

        dialog.show(
            stage
        );
    }

    // =========================================================
    // PLAYER DETAIL ROW
    // =========================================================

    private void addPlayerDetail(
        Table table,
        String label,
        String value
    ) {

        table
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    label
                )
            )
            .left()
            .width(150f)
            .padBottom(6f);

        table
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    value,
                    StyleFactory.CREME_AGED,
                    Align.right
                )
            )
            .right()
            .width(120f)
            .padBottom(6f)
            .row();
    }

    // =========================================================
    // ODDS
    // =========================================================

    private int[] calculateAdvancedOdds(
        Club home,
        Club away
    ) {

        double homePower =
            calculateComprehensiveTeamPower(
                home,
                true
            );

        double awayPower =
            calculateComprehensiveTeamPower(
                away,
                false
            );

        double diff =
            homePower -
                awayPower;

        // =====================================================
        // RAW ODDS
        // =====================================================

        double rawHomeOdds =
            Math.max(
                1.15,
                2.20 -
                    diff *
                        0.045
            );

        double rawAwayOdds =
            Math.max(
                1.15,
                2.20 +
                    diff *
                        0.045
            );

        double rawDrawOdds =
            Math.max(
                2.80,
                3.40 -
                    Math.abs(
                        diff
                    ) *
                        0.015
            );

        // =====================================================
        // PROBABILITIES
        // =====================================================

        double pHome =
            1.0 /
                rawHomeOdds;

        double pAway =
            1.0 /
                rawAwayOdds;

        double pDraw =
            1.0 /
                rawDrawOdds;

        double total =
            pHome +
                pAway +
                pDraw;

        int homePercent =
            (int) Math.round(
                pHome /
                    total *
                    100
            );

        int drawPercent =
            (int) Math.round(
                pDraw /
                    total *
                    100
            );

        int awayPercent =
            100 -
                homePercent -
                drawPercent;

        return new int[]{
            homePercent,
            drawPercent,
            awayPercent
        };
    }

    // =========================================================
    // TEAM POWER
    // =========================================================

    private double calculateComprehensiveTeamPower(
        Club club,
        boolean home
    ) {

        if (
            club == null ||
                club.getSquad()
                    .isEmpty()
        ) {

            return 70.0;
        }

        // =====================================================
        // SQUAD
        // =====================================================

        double squadPower =
            club.getSquad()
                .stream()
                .mapToInt(
                    Player::getOverall
                )
                .average()
                .orElse(
                    70.0
                );

        // =====================================================
        // STARTERS
        // =====================================================

        List<Player> starters =
            new ArrayList<>(
                club.getTacticsMap()
                    .values()
            );

        double startersPower =
            starters
                .stream()
                .filter(
                    p ->
                        p != null &&
                            p.canPlay()
                )
                .mapToInt(
                    Player::getOverall
                )
                .average()
                .orElse(
                    squadPower
                );

        // =====================================================
        // MORALE
        // =====================================================

        double morale =
            starters
                .stream()
                .filter(
                    p ->
                        p != null &&
                            p.canPlay()
                )
                .mapToInt(
                    Player::getMorale
                )
                .average()
                .orElse(
                    75.0
                );

        // =====================================================
        // FATIGUE
        // =====================================================

        double fatigue =
            starters
                .stream()
                .filter(
                    p ->
                        p != null
                )
                .mapToInt(
                    Player::getFatigue
                )
                .average()
                .orElse(
                    100.0
                );

        // =====================================================
        // ABSENCES
        // =====================================================

        int absences =
            0;

        for (
            Player player :
            club.getSquad()
        ) {

            if (
                !player.canPlay()
            ) {

                absences++;
            }
        }

        double moraleImpact =
            0.85 +
                0.30 *
                    morale /
                    100.0;

        double fatigueImpact =
            0.75 +
                0.25 *
                    fatigue /
                    100.0;

        double absenceImpact =
            Math.max(
                0.85,
                1.0 -
                    absences *
                        0.025
            );

        // =====================================================
        // GK
        // =====================================================

        double gkPower =
            65.0;

        for (
            Player player :
            starters
        ) {

            if (
                player != null &&
                    "GK".equalsIgnoreCase(
                        player.getPosition()
                    ) &&
                    player.canPlay()
            ) {

                gkPower =
                    player.getOverall();

                break;
            }
        }

        // =====================================================
        // TACTICS
        // =====================================================

        TacticalModifiers modifiers =
            TacticalEngine.calculateModifiers(
                club.getTempo(),
                club.getMentalityValue(),
                club.getPassing(),
                club.getWidth(),
                club.getPressure()
            );

        double tacticalBonus =
            modifiers.attackMultiplier *
                0.05;

        // =====================================================
        // HOME ADVANTAGE
        // =====================================================

        double homeBonus =
            home
                ? 1.08
                : 1.0;

        return (
            startersPower *
                0.50
                +
                squadPower *
                    0.25
                +
                gkPower *
                    0.25
        )
            *
            fatigueImpact
            *
            moraleImpact
            *
            absenceImpact
            *
            homeBonus
            *
            (
                1.0 +
                    tacticalBonus
            );
    }

    // =========================================================
    // LEAGUE POSITION
    // =========================================================

    private int getLeaguePosition(
        Club team
    ) {

        if (
            game.league == null
        ) {

            return 1;
        }

        List<StandingsRow> standings =
            game.league
                .getFullStandings(
                    null
                );

        for (
            int i = 0;
            i < standings.size();
            i++
        ) {

            if (
                standings
                    .get(i)
                    .club ==
                    team
            ) {

                return i + 1;
            }
        }

        return 1;
    }

    // =========================================================
    // LEAGUE POINTS
    // =========================================================

    private int getLeaguePoints(
        Club team
    ) {

        if (
            game.league == null
        ) {

            return 0;
        }

        for (
            StandingsRow row :
            game.league
                .getFullStandings(
                    null
                )
        ) {

            if (
                row.club ==
                    team
            ) {

                return row.points;
            }
        }

        return 0;
    }

    // =========================================================
    // RECENT FORM
    // =========================================================

    private Table createRecentForm(
        Club team
    ) {

        Table form =
            new Table();

        for (
            char result :
            getRecentForm(
                team
            )
        ) {

            Color color;

            if (
                result ==
                    'V'
            ) {

                color =
                    ScreenUI.SUCCESS;

            } else if (
                result ==
                    'D'
            ) {

                color =
                    ScreenUI.DANGER;

            } else if (
                result ==
                    'E'
            ) {

                color =
                    ScreenUI.WARNING;

            } else {

                color =
                    Color.GRAY;
            }

            Table badge =
                ScreenUI.createBadge(
                    game.skin,
                    String.valueOf(
                        result
                    ),
                    color
                );

            form
                .add(badge)
                .width(27f)
                .height(23f)
                .padRight(3f);
        }

        return form;
    }

    // =========================================================
    // GET FORM
    // =========================================================

    private List<Character> getRecentForm(
        Club team
    ) {

        List<Character> form =
            new ArrayList<>();

        if (
            game.league == null ||
                game.league.getSchedule() ==
                    null
        ) {

            fillEmptyForm(
                form
            );

            return form;
        }

        List<Match> matches =
            new ArrayList<>();

        for (
            Match previous :
            game.league
                .getSchedule()
        ) {

            if (
                previous.isPlayed() &&
                    (
                        previous.getHomeTeam() ==
                            team ||
                            previous.getAwayTeam() ==
                                team
                    )
            ) {

                matches.add(
                    previous
                );
            }
        }

        int start =
            Math.max(
                0,
                matches.size() -
                    5
            );

        for (
            int i = start;
            i < matches.size();
            i++
        ) {

            Match previous =
                matches.get(i);

            int teamGoals =
                previous.getHomeTeam() ==
                    team
                    ? previous.getHomeGoals()
                    : previous.getAwayGoals();

            int opponentGoals =
                previous.getHomeTeam() ==
                    team
                    ? previous.getAwayGoals()
                    : previous.getHomeGoals();

            if (
                teamGoals >
                    opponentGoals
            ) {

                form.add(
                    'V'
                );

            } else if (
                teamGoals ==
                    opponentGoals
            ) {

                form.add(
                    'E'
                );

            } else {

                form.add(
                    'D'
                );
            }
        }

        fillEmptyForm(
            form
        );

        return form;
    }

    // =========================================================
    // FILL EMPTY FORM
    // =========================================================

    private void fillEmptyForm(
        List<Character> form
    ) {

        while (
            form.size() <
                5
        ) {

            form.add(
                0,
                '-'
            );
        }
    }

    // =========================================================
    // DEFAULT FORMATION
    // =========================================================

    private List<String> createDefaultSlots() {

        List<String> slots =
            new ArrayList<>();

        slots.add("GK");

        slots.add("LB");
        slots.add("CB");
        slots.add("CB");
        slots.add("RB");

        slots.add("CM");
        slots.add("CM");
        slots.add("CM");

        slots.add("LW");
        slots.add("ST");
        slots.add("RW");

        return slots;
    }

    // =========================================================
    // SHORT CLUB NAME
    // =========================================================

    private String getShortName(
        Club club
    ) {

        if (
            club == null ||
                club.getName() ==
                    null
        ) {

            return "TIME";
        }

        String[] parts =
            club.getName()
                .trim()
                .split(" ");

        return parts[0]
            .toUpperCase();
    }

    // =========================================================
    // LOAD LOGO
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

                Texture texture =
                    ClubLogoAssets.load(club.getLogoPath());

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
    // RENDER
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

    // =========================================================
    // RESIZE
    // =========================================================

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

    // =========================================================
    // SCREEN METHODS
    // =========================================================

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    @Override
    public void dispose() {

        stage.dispose();

        if (
            backgroundTexture != null
        ) {

            backgroundTexture.dispose();
        }

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
    }
}
