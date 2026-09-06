package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.DraftScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.*;

public final class DraftLayoutPreview extends ApplicationAdapter {
    private static String output;
    private Main game; private DraftScreen screen; private Stage stage; private int frames;
    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new DraftLayoutPreview(), config);
    }
    @Override public void create() {
        try {
            game = new Main(); Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard(); game.database = new GameDatabase();
            game.league = new League("WFL", 1969); game.league.setCurrentStage("OFFSEASON");
            game.database.getClubs().forEach(game.league::addClub); game.playerClub = game.league.getClubs().get(0);
            game.freeAgencyService = new FreeAgencyService(game.league); game.loadDraftClassForYear(1970);
            DraftOrderService.initializeDraftPicks(game.league, 1970);
            for (Player player : game.draftClass) game.draftScoutManager.addTarget(player);
            for (int i = 0; i < 60; i++) game.draftScoutManager.advanceDay();
            screen = new DraftScreen(game, game.playerClub, game.draftScoutManager); screen.show(); screen.resize(1600, 900);
            Field field = DraftScreen.class.getDeclaredField("stage"); field.setAccessible(true); stage = (Stage) field.get(screen);
        } catch (Exception failure) { throw new RuntimeException(failure); }
    }
    @Override public void render() {
        screen.render(.1f);
        if (++frames == 10) {
            if (stage.getRoot().findActor("draft-prospects") == null) throw new AssertionError("Prospect list missing");
            Pixmap image = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(output), image, -1, true); image.dispose(); Gdx.app.exit();
        }
    }
    @Override public void dispose() { screen.dispose(); game.skin.dispose(); }
}
