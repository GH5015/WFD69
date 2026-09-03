package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.StaffScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.*;

public final class StaffMarketLayoutRegression extends ApplicationAdapter {
    private Main game;
    private StaffScreen screen;
    private Stage stage;
    private Club club;
    private StaffMember previous;
    private int frames;
    private static String output;
    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new StaffMarketLayoutRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard();
            game.league = new League("WFL", 1990); game.league.setCurrentStage("OFFSEASON");
            club = new Club("Santos Atlântico", "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
            game.league.addClub(club);
            previous = club.getStaffMember(StaffRole.COACH);
            screen = new StaffScreen(game, club); screen.show(); screen.resize(1600, 900);
            Field field = StaffScreen.class.getDeclaredField("stage"); field.setAccessible(true); stage = (Stage) field.get(screen);
        } catch (Exception failure) { throw new RuntimeException(failure); }
    }
    @Override public void render() {
        screen.render(1f / 30f);
        if (++frames == 10) {
            ScrollPane scroll = stage.getRoot().findActor("staff-market-scroll");
            Table rows = (Table) scroll.getActor();
            if (rows.getChildren().size != 15 || scroll.getMaxY() <= 0) throw new AssertionError("Market list not scrollable/complete");
            snapshot("-market");
            Table row = (Table) rows.getChildren().first();
            Actor negotiate = row.getChildren().peek();
            for (EventListener listener : negotiate.getListeners()) if (listener instanceof ClickListener)
                ((ClickListener) listener).clicked(new InputEvent(), 1, 1);
        }
        if (frames == 20) {
            Dialog dialog = stage.getRoot().findActor("staff-contract-dialog");
            if (dialog == null || dialog.getX() < 0 || dialog.getY() < 0 || dialog.getTop() > stage.getHeight()) throw new AssertionError("Offer bounds");
            SelectBox<?> salary = dialog.findActor("staff-offer-salary");
            salary.setSelectedIndex(0);
            fire(dialog.findActor("staff-send-offer"));
            if (club.getStaffMember(StaffRole.COACH) != previous) throw new AssertionError("Low offer hired staff");
            if (!dialog.findActor("staff-use-counteroffer").isVisible()) throw new AssertionError("No counteroffer button");
        }
        if (frames == 25) snapshot("-offer");
        if (frames == 30) {
            Dialog dialog = stage.getRoot().findActor("staff-contract-dialog");
            fire(dialog.findActor("staff-use-counteroffer"));
            fire(dialog.findActor("staff-send-offer"));
            if (club.getStaffMember(StaffRole.COACH) == previous) throw new AssertionError("Counteroffer not signed");
            System.out.println("Staff UI: 15 scrollable candidates, editable offer, counteroffer and signing OK.");
            Gdx.app.exit();
        }
    }
    private void fire(Actor actor) { actor.fire(new ChangeListener.ChangeEvent()); }
    private void snapshot(String suffix) {
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
        PixmapIO.writePNG(Gdx.files.absolute(output + suffix + ".png"), pixmap, -1, true); pixmap.dispose();
    }
    @Override public void dispose() { screen.dispose(); game.skin.dispose(); }
}
