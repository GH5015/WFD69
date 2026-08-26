package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.some_example_name.database.DraftClass1970;
import io.github.some_example_name.database.DraftClass1971;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.engine.DevelopmentEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.DraftOrderService;
import io.github.some_example_name.model.FreeAgencyService;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.screens.CareerOverlay;
import io.github.some_example_name.screens.MenuScreen;
import io.github.some_example_name.screens.NavigationDrawer;
import io.github.some_example_name.simulation.PlayoffSimulator;
import io.github.some_example_name.simulation.SeasonSimulator;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;

public class Main extends Game {

    public Skin skin;

    public GameDatabase database;
    public League league;

    public MatchEngine matchEngine;
    public DevelopmentEngine developmentEngine;
    public SeasonSimulator seasonSimulator;
    public PlayoffSimulator playoffSimulator;
    public FreeAgencyService freeAgencyService;

    public Club playerClub;
    public Drawable background;

    // ==============================
    // DRAFT / SCOUTING
    // ==============================

    /**
     * Manager persistente do scouting.
     * Não deve ser recriado ao abrir a tela.
     */
    public DraftScoutManager draftScoutManager;

    /**
     * Classe do Draft persistente da temporada.
     *
     * Isso evita gerar novos Players e novos UUIDs
     * toda vez que DraftScoutingScreen é aberta.
     */
    public List<Player> draftClass;
    public int draftClassYear;

    /** Seleciona uma única classe persistente para cada ano de Draft. */
    public void loadDraftClassForYear(int year) {
        if (draftClass != null && draftClassYear == year) return;

        draftClass = year == 1971
            ? DraftClass1971.getPlayers()
            : DraftClass1970.getPlayers();
        draftClassYear = year;
        draftScoutManager = new DraftScoutManager(3);
    }

    /*
     * Alertas do clube ocorridos durante a partida. Eles sobrevivem à troca
     * da MatchScreen para a tela de elenco e são consumidos uma vez lá.
     */
    private final List<MatchEvent> pendingSquadAlerts =
        new ArrayList<>();

    public void queueSquadAlert(
        MatchEvent event
    ) {
        if (event != null) {
            pendingSquadAlerts.add(event);
        }
    }

    public MatchEvent consumeSquadAlert() {
        return pendingSquadAlerts.isEmpty()
            ? null
            : pendingSquadAlerts.remove(0);
    }

    @Override
    public void create() {

        // ==============================
        // VISUAL
        // ==============================

        background = StyleFactory.createCheckerboard();
        setupSkin();

        // ==============================
        // DATABASE
        // ==============================

        database = new GameDatabase();

        // ==============================
        // LIGA
        // ==============================

        league = new League("Liga Mundial", 1969);

        database.getClubs().forEach(league::addClub);
        database.applyInitialContractsAndBindClubs(
            league.getCurrentSeason()
        );
        freeAgencyService = new FreeAgencyService(league);
        // A temporada inaugural também respeita o limite obrigatório de elenco.
        freeAgencyService.enforceRosterLimitsForNewSeason();
        DraftOrderService.initializeDraftPicks(
            league,
            league.getCurrentSeason() + 1
        );

        // ==============================
        // MOTORES
        // ==============================

        matchEngine = new MatchEngine();

        developmentEngine = new DevelopmentEngine();

        seasonSimulator = new SeasonSimulator();

        playoffSimulator = new PlayoffSimulator(
            matchEngine,
            league
        );

        // ==============================
        // TEMPORADA 1969
        // ==============================

        seasonSimulator.createSchedule(league);

        if (!league.getSchedule().isEmpty()) {
            league.setLastProcessedDate(
                league.getSchedule().get(0).getDate()
            );
        }

        // ==============================
        // CLUBE DO USUÁRIO
        // ==============================

        if (!database.getClubs().isEmpty()) {
            playerClub = database.getClubs().get(0);
            playerClub.setUserControlled(true);

            for (Club club : database.getClubs()) {
                if (club != playerClub) {
                    club.autoSelectBestFormationAndXI();
                }
            }
        }

        // ==============================
        // DRAFT / SCOUTING
        // ==============================

        // A classe é gerada uma única vez e permanece estável durante
        // todo o ciclo de scouting e Draft daquela temporada.
        loadDraftClassForYear(league.getCurrentSeason() + 1);

        // ==============================
        // TELA INICIAL
        // ==============================

        setScreen(new MenuScreen(this));
    }

    private void setupSkin() {

        skin = new Skin();

        // ==============================
        // FONTES
        // ==============================

        BitmapFont fontRegular =
            generateFont(
                "Roboto/static/Roboto-Regular.ttf",
                26
            );

        BitmapFont fontBold =
            generateFont(
                "Roboto/static/Roboto-Bold.ttf",
                29
            );

        BitmapFont fontBlack =
            generateFont(
                "Roboto/static/Roboto-Black.ttf",
                56
            );

        skin.add(
            "default",
            fontRegular
        );

        skin.add(
            "font-bold",
            fontBold
        );

        skin.add(
            "font-title",
            fontBlack
        );

        skin.add(
            "font-label",
            fontRegular
        );

        // ==============================
        // CORES
        // ==============================

        skin.add(
            "white",
            Color.WHITE,
            Color.class
        );

        skin.add(
            "black",
            Color.BLACK,
            Color.class
        );

        skin.add(
            "red",
            Color.RED,
            Color.class
        );

        skin.add(
            "cyan",
            Color.CYAN,
            Color.class
        );

        // ==============================
        // LABELS
        // ==============================

        skin.add(
            "default",
            new Label.LabelStyle(
                fontRegular,
                Color.WHITE
            )
        );

        skin.add(
            "font-bold",
            new Label.LabelStyle(
                fontBold,
                Color.WHITE
            )
        );

        skin.add(
            "font-title",
            new Label.LabelStyle(
                fontBlack,
                StyleFactory.GOLD
            )
        );

        // Compatibilidade com telas antigas.
        skin.add(
            "title",
            new Label.LabelStyle(
                fontBlack,
                StyleFactory.GOLD
            )
        );

        skin.add(
            "font-label",
            new Label.LabelStyle(
                fontRegular,
                Color.WHITE
            )
        );

        // ==============================
        // BOTÕES
        // ==============================

        Drawable btnNormal =
            StyleFactory.createModernButton(
                220,
                58,
                StyleFactory.METAL_DARK,
                StyleFactory.GOLD
            );

        Drawable btnHover =
            StyleFactory.createModernButton(
                220,
                58,
                Color.valueOf("444444"),
                StyleFactory.YELLOW_TITLE
            );

        Drawable btnPressed =
            StyleFactory.createModernButton(
                220,
                58,
                StyleFactory.DARK_GOLD,
                Color.WHITE
            );

        TextButton.TextButtonStyle textButtonStyle =
            new TextButton.TextButtonStyle();

        textButtonStyle.up = btnNormal;
        textButtonStyle.over = btnHover;
        textButtonStyle.down = btnPressed;

        textButtonStyle.font = fontBold;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor =
            StyleFactory.YELLOW_TITLE;

        skin.add(
            "default",
            textButtonStyle
        );

        skin.add(
            "toggle",
            textButtonStyle
        );

        skin.add(
            "button",
            btnNormal,
            Drawable.class
        );

        // ==============================
        // WINDOW / DIALOG
        // ==============================

        Window.WindowStyle windowStyle =
            new Window.WindowStyle();

        windowStyle.background =
            StyleFactory.createMetallicBoard(
                800,
                600,
                StyleFactory.PRUSSIAN_GREEN
            );

        windowStyle.titleFont = fontBold;
        windowStyle.titleFontColor =
            StyleFactory.GOLD;

        skin.add(
            "default",
            windowStyle
        );

        skin.add(
            "dialog",
            windowStyle
        );

        // ==============================
        // SCROLLPANE
        // ==============================

        skin.add(
            "default",
            new ScrollPane.ScrollPaneStyle()
        );

        // ==============================
        // PROGRESS BAR
        // ==============================

        ProgressBar.ProgressBarStyle progressStyle =
            new ProgressBar.ProgressBarStyle();

        progressStyle.background =
            StyleFactory.createRoundedPanel(
                StyleFactory.METAL_DARK,
                StyleFactory.GOLD
            );

        progressStyle.knob =
            StyleFactory.createRoundedPanel(
                StyleFactory.GOLD,
                StyleFactory.SOFT_YELLOW
            );

        progressStyle.knobBefore =
            progressStyle.knob;

        progressStyle.knobAfter =
            StyleFactory.createRoundedPanel(
                Color.valueOf("3B4A42"),
                StyleFactory.METAL_DARK
            );

        skin.add(
            "default-horizontal",
            progressStyle
        );

        // ==============================
        // SELECT BOX
        // ==============================

        SelectBox.SelectBoxStyle selectBoxStyle =
            new SelectBox.SelectBoxStyle();

        selectBoxStyle.font =
            fontRegular;

        selectBoxStyle.fontColor =
            Color.WHITE;

        selectBoxStyle.background =
            StyleFactory.createModernButton(
                220,
                58,
                Color.valueOf("17251D"),
                StyleFactory.DARK_GOLD
            );

        selectBoxStyle.backgroundOver =
            StyleFactory.createModernButton(
                220,
                58,
                Color.valueOf("2C452F"),
                StyleFactory.PLAYOFF_GOLD
            );

        selectBoxStyle.backgroundOpen =
            StyleFactory.createModernButton(
                220,
                58,
                Color.valueOf("3A3113"),
                StyleFactory.SOFT_YELLOW
            );

        selectBoxStyle.scrollStyle =
            new ScrollPane.ScrollPaneStyle();

        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle =
            new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();

        listStyle.font =
            fontRegular;

        listStyle.fontColorSelected =
            Color.WHITE;

        listStyle.fontColorUnselected =
            Color.WHITE;

        listStyle.selection =
            StyleFactory.createRoundedPanel(
                Color.valueOf("493A12"),
                StyleFactory.PLAYOFF_GOLD
            );

        listStyle.background =
            StyleFactory.createRoundedPanel(
                Color.valueOf("132019"),
                StyleFactory.BORDER_SOFT
            );

        selectBoxStyle.listStyle =
            listStyle;

        skin.add(
            "default",
            selectBoxStyle
        );

        // ==============================
        // CAMPO DE TEXTO
        // ==============================

        TextField.TextFieldStyle textFieldStyle =
            new TextField.TextFieldStyle();

        textFieldStyle.font =
            fontRegular;

        textFieldStyle.fontColor =
            Color.WHITE;

        textFieldStyle.messageFontColor =
            StyleFactory.TEXT_MUTED;

        textFieldStyle.background =
            StyleFactory.createModernButton(
                220,
                50,
                Color.valueOf("17251D"),
                StyleFactory.DARK_GOLD
            );

        textFieldStyle.cursor =
            StyleFactory.createSolid(
                StyleFactory.SOFT_YELLOW
            );

        textFieldStyle.selection =
            StyleFactory.createSolid(
                Color.valueOf("5A4A1A")
            );

        skin.add(
            "default",
            textFieldStyle
        );

        // ==============================
        // CHECKBOX
        // ==============================

        CheckBox.CheckBoxStyle checkStyle =
            new CheckBox.CheckBoxStyle();

        checkStyle.font =
            fontRegular;

        checkStyle.checkboxOff =
            StyleFactory.createSolid(
                Color.GRAY
            );

        checkStyle.checkboxOn =
            StyleFactory.createSolid(
                StyleFactory.GOLD
            );

        skin.add(
            "default",
            checkStyle
        );
    }

    private BitmapFont generateFont(
        String path,
        int size
    ) {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal(path)
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;

        parameter.minFilter =
            Texture.TextureFilter.Linear;

        parameter.magFilter =
            Texture.TextureFilter.Linear;

        BitmapFont font =
            generator.generateFont(parameter);

        generator.dispose();

        return font;
    }

    public void startPlayoffs() {
        playoffSimulator.startPlayoffs();
    }

    @Override
    public void setScreen(Screen screen) {

        Screen previous =
            getScreen();

        super.setScreen(screen);

        if (
            previous != null &&
                previous != screen
        ) {
            previous.dispose();
        }
    }

    @Override
    public void dispose() {

        super.dispose();

        NavigationDrawer.disposeIcons();
        CareerOverlay.disposeAssets();
        IconTextButton.dispose();

        if (skin != null) {
            skin.dispose();
        }

        StyleFactory.disposeGenerated();
    }
}