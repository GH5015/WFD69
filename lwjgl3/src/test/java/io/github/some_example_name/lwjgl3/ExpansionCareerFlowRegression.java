package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.LeagueExpansionService;
import io.github.some_example_name.screens.ExpansionCareerLoadingScreen;
import io.github.some_example_name.screens.OffSeasonScreen;
import io.github.some_example_name.screens.ClubSelectionScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.Method;

/** Real background preparation, publication to Main and off-season rendering. */
public final class ExpansionCareerFlowRegression extends ApplicationAdapter {
    private static String output;
    private Main game;
    private League original;
    private int phase, frames;
    private long start;
    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new ExpansionCareerFlowRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard();
            game.database = new GameDatabase(); game.league = new League("WFL", 1969);
            game.database.getClubs().forEach(game.league::addClub);
            game.playerClub = game.league.getClubs().get(0);
            original = game.league;
            game.setScreen(new ExpansionCareerLoadingScreen(game, "Cairo Pharaohs"));
            start = System.nanoTime();
        } catch (Exception failure) { throw new RuntimeException(failure); }
    }
    @Override public void render() {
        game.render();
        if (System.nanoTime() - start > 180_000_000_000L) throw new AssertionError("Preparation timeout");
        if (phase == 0 && game.getScreen() instanceof OffSeasonScreen) {
            if (game.league == original || original.getCurrentSeason() != 1969 || original.getClubs().size() != 20)
                throw new AssertionError("Preparation changed original league");
            if (!game.playerClub.getName().equals("Cairo Pharaohs") || !game.playerClub.isUserControlled()
                || game.league.getClubs().stream().filter(c -> c.isUserControlled()).count() != 1)
                throw new AssertionError("Club control not transferred");
            if (!LeagueExpansionService.isPending(game.league) || game.draftClassYear != 1974)
                throw new AssertionError("Wrong entry phase/draft");
            if (!game.managerCareer.getHistory().isEmpty()) throw new AssertionError("Manager inherited simulated seasons");
            phase = 1;
        }
        if (phase == 1 && ++frames == 10) {
            Pixmap image = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(output), image, -1, true); image.dispose();
            original = game.league;
            game.setScreen(new ExpansionCareerLoadingScreen(game, "Jakarta Garudas"));
            game.setScreen(new ClubSelectionScreen(game)); // Cancela antes de publicar qualquer resultado.
            phase = 2; frames = 0;
        }
        if (phase == 2 && ++frames == 15) {
            if (game.league != original || !(game.getScreen() instanceof ClubSelectionScreen))
                throw new AssertionError("Cancelled worker replaced the career");
            System.out.println("Background handoff, off-season rendering and cancellation: OK");
            Gdx.app.exit();
        }
    }
    @Override public void dispose() { game.getScreen().dispose(); game.skin.dispose(); }
}
