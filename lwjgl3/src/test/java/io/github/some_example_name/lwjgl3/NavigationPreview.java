package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.FreeAgencyScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.*;

/** Captura da navegação na central de mercado, com uma janela oculta. */
public class NavigationPreview extends ApplicationAdapter {
    private Main game;
    private Screen screen;
    private Stage stage;
    private int frames;
    private static boolean negotiation;
    private static boolean history;
    private static boolean squad;
    private static boolean offseason;
    public static void main(String[] args) {
        negotiation = args.length > 0 && "negotiation".equals(args[0]);
        history = args.length > 0 && "history".equals(args[0]);
        squad = args.length > 0 && "squad".equals(args[0]);
        offseason = args.length > 0 && "offseason".equals(args[0]);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new NavigationPreview(), config);
    }
    public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard();
            game.database = new GameDatabase(); game.league = new League("WFL", 1969);
            game.database.getClubs().forEach(game.league::addClub);
            game.playerClub = game.league.getClubs().get(0);
            if (squad) {
                game.playerClub.setFormation(Formation.values()[0]);
                for (int i = 0; i < Math.min(11, game.playerClub.getSquad().size()); i++)
                    game.playerClub.getTacticsMap().put(i, game.playerClub.getSquad().get(i));
            }
            game.freeAgencyService = new FreeAgencyService(game.league);
            if (offseason) {
                game.league.setCurrentStage("OFFSEASON");
                game.league.setCurrentDate(new java.util.GregorianCalendar(1969, 10, 2).getTime());
            }
            screen = offseason ? new io.github.some_example_name.screens.OffSeasonScreen(game, game.playerClub) : squad ? new io.github.some_example_name.screens.ClubManagementScreen(game, game.playerClub) : new FreeAgencyScreen(game, game.playerClub);
            if (squad) {
                Method refresh = screen.getClass().getDeclaredMethod("refreshUI"); refresh.setAccessible(true); refresh.invoke(screen);
            } else screen.show();
            screen.resize(1600, 900);
            Field field = screen.getClass().getDeclaredField("stage"); field.setAccessible(true); stage = (Stage) field.get(screen);
            if (history) {
                game.freeAgencyService.processAiFreeAgentSignings(game.playerClub, 1969);
                Field tab = FreeAgencyScreen.class.getDeclaredField("tab"); tab.setAccessible(true); tab.set(screen, "HISTÓRICO");
                Method refresh = FreeAgencyScreen.class.getDeclaredMethod("refreshUI"); refresh.setAccessible(true); refresh.invoke(screen);
            }
            if (negotiation) {
                Field selected = FreeAgencyScreen.class.getDeclaredField("selected"); selected.setAccessible(true);
                Method offer = FreeAgencyScreen.class.getDeclaredMethod("showOfferDialog", Player.class); offer.setAccessible(true);
                offer.invoke(screen, selected.get(screen));
            }
        } catch (Exception failure) { throw new RuntimeException(failure); }
    }
    public void render() {
        screen.render(.1f);
        if (++frames == 10) {
            Actor back = stage.getRoot().findActor("navigation-main-menu");
            Vector2 point = back.localToStageCoordinates(new Vector2(back.getWidth()/2, back.getHeight()/2));
            Actor hit = stage.hit(point.x, point.y, true);
            if (!negotiation && (hit == null || !(hit == back || hit.isDescendantOf(back)))) throw new AssertionError("Menu button is covered");
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(System.getProperty("user.dir") + "/../build/reports/" + (offseason ? "offseason-preview.png" : squad ? "squad-preview.png" : history ? "history-preview.png" : negotiation ? "negotiation-preview.png" : "navigation-preview.png")), pixmap, -1, true);
            pixmap.dispose();
            Gdx.app.exit();
        }
    }
    public void dispose() { screen.dispose(); game.skin.dispose(); }
}
