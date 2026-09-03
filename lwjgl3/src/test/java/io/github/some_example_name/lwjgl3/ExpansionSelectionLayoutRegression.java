package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.screens.ClubSelectionScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class ExpansionSelectionLayoutRegression extends ApplicationAdapter {
    private static String output;
    private Main game;
    private ClubSelectionScreen screen;
    private int frame;

    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new ExpansionSelectionLayoutRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard();
            game.database = new GameDatabase(); game.league = new League("WFL", 1969);
            game.database.getClubs().forEach(game.league::addClub);
            game.playerClub = game.league.getClubs().get(0);
            screen = new ClubSelectionScreen(game);
            Field catalog = ClubSelectionScreen.class.getDeclaredField("expansionCatalog"); catalog.setAccessible(true); catalog.set(screen, true);
            Field previews = ClubSelectionScreen.class.getDeclaredField("expansionPreviews"); previews.setAccessible(true);
            List<Club> clubs = (List<Club>) previews.get(screen);
            if (clubs.size() != 10 || game.league.getClubs().size() != 20) throw new AssertionError("Preview mutated league");
            Field selected = ClubSelectionScreen.class.getDeclaredField("selectedClub"); selected.setAccessible(true); selected.set(screen, clubs.get(1));
            screen.show(); screen.resize(1600, 900);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    @Override public void render() {
        screen.render(1f / 30f);
        if (++frame == 10) {
            Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(output), pixels, -1, true); pixels.dispose();
            if (game.league.getClubs().size() != 20 || game.league.getCurrentSeason() != 1969) throw new AssertionError("Preview state changed");
            System.out.println("Expansion selection rendered; 10 independent previews, founding league unchanged.");
            Gdx.app.exit();
        }
    }
    @Override public void dispose() { screen.dispose(); game.skin.dispose(); }
}
