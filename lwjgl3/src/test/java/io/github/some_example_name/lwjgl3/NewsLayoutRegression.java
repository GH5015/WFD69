package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.WflNewsDialog;
import java.lang.reflect.Method;
import java.util.*;

/** Hidden-window smoke test with long club names and screenshots of both scroll positions. */
public final class NewsLayoutRegression extends ApplicationAdapter {
    private Stage stage;
    private Main game;
    private int frames;
    private static String output;

    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);
        config.disableAudio(true);
        config.setWindowedMode(1600, 1000);
        new Lwjgl3Application(new NewsLayoutRegression(), config);
    }

    @Override public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin");
            setup.setAccessible(true);
            setup.invoke(game);
            game.league = new League("WFL", 1969);
            String[] names = {"Barcelona Mediterrâneo", "Baghdad Mesopotamia", "Santos Atlântico", "Amsterdã Total",
                "Manchester Albion", "Tel Aviv Stars", "Bavaria München", "Buenos Aires Plata"};
            Date date = new GregorianCalendar(1969, Calendar.JANUARY, 5).getTime();
            for (int i = 0; i < names.length; i += 2) {
                Club home = new Club(names[i]), away = new Club(names[i + 1]);
                game.league.addClub(home);
                game.league.addClub(away);
                Match match = new Match(home, away);
                match.setDate(date);
                match.setResult(4 - i / 2, 0);
                game.league.getSchedule().add(match);
            }
            List<NewsEvent> edition = Arrays.asList(
                new NewsEvent(date, NewsEvent.Category.RESULTADO, "BARCELONA MEDITERRÂNEO VENCE SEM SOFRER GOLS",
                    "Barcelona Mediterrâneo 4 x 0 Baghdad Mesopotamia."),
                new NewsEvent(date, NewsEvent.Category.DESTAQUE, "JOHAN CRUYFF É O NOME DA SEMANA",
                    "Johan Cruyff marcou 4 gols nos últimos jogos pelo Amsterdã Total."),
                new NewsEvent(date, NewsEvent.Category.HISTORIA, "BARCELONA MEDITERRÂNEO TEM O MELHOR ATAQUE",
                    "4 gols marcados fazem do clube a principal força ofensiva da WFL."));
            stage = new Stage(new FitViewport(1600, 1000));
            Method show = WflNewsDialog.class.getDeclaredMethod("show", Stage.class, Main.class, List.class);
            show.setAccessible(true);
            show.invoke(null, stage, game, edition);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override public void render() {
        ScreenUtils.clear(.025f, .04f, .03f, 1);
        stage.act(1f / 30f);
        stage.draw();
        if (++frames == 10) {
            verify(stage.getRoot());
            capture("top");
            ScrollPane scroll = stage.getRoot().findActor("news-scroll");
            if (scroll.getMaxY() <= 0) throw new AssertionError("Expected vertical scrolling");
            scroll.setScrollY(scroll.getMaxY());
            scroll.updateVisualScroll();
        }
        if (frames == 20) {
            capture("bottom");
            System.out.println("News layout: labels fit their bounds; top and bottom rendered successfully.");
            Gdx.app.exit();
        }
    }

    private void verify(Actor actor) {
        if (actor instanceof Label && actor.getName() != null) {
            Label label = (Label) actor;
            label.validate();
            if (label.getGlyphLayout().height > label.getHeight() + 1
                || label.getGlyphLayout().width > label.getWidth() + 1)
                throw new AssertionError("Text overflow: " + actor.getName());
        }
        if (actor instanceof Group) for (Actor child : ((Group) actor).getChildren()) verify(child);
    }

    private void capture(String name) {
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(Gdx.files.absolute(output + "/news-" + name + ".png"), pixels, -1, true);
        pixels.dispose();
    }

    @Override public void dispose() { stage.dispose(); game.skin.dispose(); }
}
