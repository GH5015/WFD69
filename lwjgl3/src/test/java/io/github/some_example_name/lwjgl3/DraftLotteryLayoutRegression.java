package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.DraftLotteryScreen;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/** Hidden native rendering plus real button events through the complete ceremony. */
public final class DraftLotteryLayoutRegression extends ApplicationAdapter {
    private static String output;
    private Main game;
    private DraftLotteryScreen screen;
    private Stage stage;
    private int frames, total;
    private List<Club> official;
    private Set<Integer> screenshots = new HashSet<>();
    private int pauseChecks;
    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        config.setForegroundFPS(120);
        new Lwjgl3Application(new DraftLotteryLayoutRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main(); Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.database = new GameDatabase(); game.league = new League("WFL", 1970); game.league.setCurrentStage("OFFSEASON");
            game.database.getClubs().forEach(game.league::addClub);
            game.playerClub = game.league.getClubs().get(0);
            DraftOrderService.initializeDraftPicks(game.league, 1971);
            // Give the user two lottery origins, to cover traded picks and multi-pick status.
            List<Club> participants = game.league.getDraftLotteryParticipants(); total = participants.size();
            for (Club origin : participants.subList(0, 2)) for (DraftPick pick : origin.getDraftPicks())
                if (pick.getYear() == 1971 && pick.getRound() == 1) pick.setCurrentOwner(game.playerClub);
            open();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private void open() {
        screen = new DraftLotteryScreen(game, game.playerClub); screen.show(); screen.resize(1600, 900); stage = (Stage) field("stage");
    }
    @Override public void render() {
        screen.render(.1f); frames++;
        if (frames == 5) {
            snapshot("ready"); bounds();
            if (game.league.isDraftLotteryCompleted()) throw new AssertionError("Opening screen drew lottery");
            fire("lottery-auto"); official = new ArrayList<>(game.league.getDraftLotteryOrder());
            fire("lottery-next"); if ((Integer) field("revealed") != 0) throw new AssertionError("Double click revealed early");
        }
        if (frames < 6) return;
        String phase = field("phase").toString(); int revealed = (Integer) field("revealed"); float time = (Float) field("elapsed");
        if (phase.equals("ANTICIPATION") && revealed == 0 && time > .5f && pauseChecks == 0) {
            snapshot("suspense"); fire("lottery-auto"); pauseChecks = 1;
        }
        if (phase.equals("READY") && pauseChecks == 1) {
            if (revealed != 1) throw new AssertionError("Auto pause did not stop after current reveal");
            fire("lottery-auto"); pauseChecks = 2;
        }
        if (phase.equals("CELEBRATION") && time > .45f) {
            int pick = total - revealed + 1;
            Label title = stage.getRoot().findActor("lottery-pick-title");
            if (!title.getText().toString().equals("PICK #" + pick)) throw new AssertionError("Reveal rank off by one");
            if (screenshots.add(pick) && (pick <= 3 || pick == total)) { snapshot("pick-" + pick); bounds(); }
        }
        if (!game.league.getDraftLotteryOrder().equals(official)) throw new AssertionError("Animation changed actual draw");
        if (phase.equals("COMPLETE")) {
            if (screenshots.size() != total || pauseChecks != 2) throw new AssertionError("Some reveals bypassed animation");
            snapshot("complete"); screen.dispose(); open();
            if (!field("phase").toString().equals("COMPLETE") || !game.league.getDraftLotteryOrder().equals(official)) throw new AssertionError("Reopening rerolled lottery");
            screen.dispose();
            game.league = new League("WFL", 1971); game.league.setCurrentStage("OFFSEASON"); game.database.getClubs().forEach(game.league::addClub);
            open(); fire("lottery-next"); List<Club> skipped = game.league.getDraftLotteryOrder(); fire("lottery-skip");
            if (!field("phase").toString().equals("COMPLETE") || !skipped.equals(game.league.getDraftLotteryOrder())) throw new AssertionError("Skipping rerolled lottery");
            System.out.println("Lottery UI: suspense/reveal/top-three animations, pause, no double click, skip, reentry, traded picks and layout OK.");
            Gdx.app.exit();
        }
        if (frames > 1500) throw new AssertionError("Ceremony stuck");
    }
    private Object field(String name) {
        try { Field field = DraftLotteryScreen.class.getDeclaredField(name); field.setAccessible(true); return field.get(screen); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    private void fire(String name) {
        Actor actor = stage.getRoot().findActor(name); if (actor == null) throw new AssertionError("Missing button " + name);
        actor.fire(new ChangeListener.ChangeEvent());
    }
    private void bounds() {
        for (String name : new String[]{"lottery-ceremony", "lottery-next", "lottery-auto", "lottery-skip"}) {
            Actor actor = stage.getRoot().findActor(name); Vector2 p = actor.localToStageCoordinates(new Vector2());
            if (p.x < -1 || p.y < -1 || p.x + actor.getWidth() > stage.getWidth() + 1 || p.y + actor.getHeight() > stage.getHeight() + 1)
                throw new AssertionError("Layout overflow: " + name);
        }
    }
    private void snapshot(String name) {
        Pixmap image = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
        PixmapIO.writePNG(Gdx.files.absolute(output + "/" + name + ".png"), image, -1, true); image.dispose();
    }
    @Override public void dispose() { screen.dispose(); game.skin.dispose(); }
}
