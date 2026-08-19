package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.screens.MenuScreen;
import io.github.some_example_name.screens.NavigationDrawer;
import io.github.some_example_name.screens.CareerOverlay;
import io.github.some_example_name.simulation.PlayoffSimulator;
import io.github.some_example_name.simulation.SeasonSimulator;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.IconTextButton;

public class Main extends Game {
    public Skin skin;
    public GameDatabase database;
    public League league;
    public MatchEngine matchEngine;
    public SeasonSimulator seasonSimulator;
    public PlayoffSimulator playoffSimulator;
    public Club playerClub;
    public Drawable background;

    @Override
    public void create() {
        background = StyleFactory.createCheckerboard();
        setupSkin();
        database = new GameDatabase();
        league = new League("Liga Mundial", 1969);
        database.getClubs().forEach(league::addClub);
        matchEngine = new MatchEngine();
        seasonSimulator = new SeasonSimulator();
        playoffSimulator = new PlayoffSimulator(matchEngine, league);
        seasonSimulator.createSchedule(league);
        if (!league.getSchedule().isEmpty()) {
            league.setLastProcessedDate(league.getSchedule().get(0).getDate());
        }
        playerClub = database.getClubs().get(0);
        this.setScreen(new MenuScreen(this));
    }

    private void setupSkin() {
        skin = new Skin();

        // 1. CARREGAR FONTES TTF
        BitmapFont fontRegular = generateFont("Roboto/static/Roboto-Regular.ttf", 22);
        BitmapFont fontBold = generateFont("Roboto/static/Roboto-Bold.ttf", 24);
        BitmapFont fontBlack = generateFont("Roboto/static/Roboto-Black.ttf", 48);

        skin.add("default", fontRegular);
        skin.add("font-bold", fontBold);
        skin.add("font-title", fontBlack);
        skin.add("font-label", fontRegular);

        // Cores nomeadas usadas pelos construtores da skin antiga.
        skin.add("white", Color.WHITE, Color.class);
        skin.add("black", Color.BLACK, Color.class);
        skin.add("red", Color.RED, Color.class);
        skin.add("cyan", Color.CYAN, Color.class);

        // 2. ESTILOS DE LABEL
        skin.add("default", new Label.LabelStyle(fontRegular, Color.WHITE));
        skin.add("font-bold", new Label.LabelStyle(fontBold, Color.WHITE));
        skin.add("font-title", new Label.LabelStyle(fontBlack, StyleFactory.GOLD));
        // Aliases usados por telas legadas, mantendo um único padrão visual.
        skin.add("title", new Label.LabelStyle(fontBlack, StyleFactory.GOLD));
        skin.add("font-label", new Label.LabelStyle(fontRegular, Color.WHITE));

        // 3. ESTILOS DE BOTÃO (Usando BOLD para melhor leitura)
        Drawable btnNormal = StyleFactory.createModernButton(200, 50, StyleFactory.METAL_DARK, StyleFactory.GOLD);
        Drawable btnHover = StyleFactory.createModernButton(200, 50, Color.valueOf("444444"), StyleFactory.YELLOW_TITLE);
        Drawable btnPressed = StyleFactory.createModernButton(200, 50, StyleFactory.DARK_GOLD, Color.WHITE);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = btnNormal; textButtonStyle.over = btnHover; textButtonStyle.down = btnPressed;
        textButtonStyle.font = fontBold;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = StyleFactory.YELLOW_TITLE;
        skin.add("default", textButtonStyle);
        skin.add("toggle", textButtonStyle);
        skin.add("button", btnNormal, Drawable.class);

        // 4. JANELAS E OUTROS
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = StyleFactory.createMetallicBoard(800, 600, StyleFactory.PRUSSIAN_GREEN);
        windowStyle.titleFont = fontBold; windowStyle.titleFontColor = StyleFactory.GOLD;
        skin.add("default", windowStyle);
        skin.add("dialog", windowStyle);

        skin.add("default", new ScrollPane.ScrollPaneStyle());

        ProgressBar.ProgressBarStyle progressStyle = new ProgressBar.ProgressBarStyle();
        progressStyle.background = StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD);
        progressStyle.knob = StyleFactory.createRoundedPanel(StyleFactory.GOLD, StyleFactory.SOFT_YELLOW);
        progressStyle.knobBefore = progressStyle.knob;
        progressStyle.knobAfter = StyleFactory.createRoundedPanel(Color.valueOf("3B4A42"), StyleFactory.METAL_DARK);
        skin.add("default-horizontal", progressStyle);

        // SelectBox usado em Táticas. Sem este estilo a troca para a tela
        // lançava uma exceção ao procurar o estilo "default" no Skin.
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = fontRegular;
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.background = btnNormal;
        selectBoxStyle.backgroundOver = btnHover;
        selectBoxStyle.backgroundOpen = btnHover;
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = fontRegular;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = btnPressed;
        listStyle.background = btnNormal;
        selectBoxStyle.listStyle = listStyle;
        skin.add("default", selectBoxStyle);

        CheckBox.CheckBoxStyle checkStyle = new CheckBox.CheckBoxStyle();
        checkStyle.font = fontRegular; checkStyle.checkboxOff = StyleFactory.createSolid(Color.GRAY); checkStyle.checkboxOn = StyleFactory.createSolid(StyleFactory.GOLD);
        skin.add("default", checkStyle);
    }

    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    public void startPlayoffs() { playoffSimulator.startPlayoffs(); }
    @Override
    public void setScreen(Screen screen) {
        Screen previous = getScreen();
        super.setScreen(screen);
        if (previous != null && previous != screen) previous.dispose();
    }

    @Override public void dispose() {
        super.dispose();
        NavigationDrawer.disposeIcons();
        CareerOverlay.disposeAssets();
        IconTextButton.dispose();
        skin.dispose();
        StyleFactory.disposeGenerated();
    }
}
