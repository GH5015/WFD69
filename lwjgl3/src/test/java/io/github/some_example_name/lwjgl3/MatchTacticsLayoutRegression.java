package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.*;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.TacticsDialog;
import java.lang.reflect.Method;

public class MatchTacticsLayoutRegression extends ApplicationAdapter {
    private Stage stage;
    private Main game;
    private int frames;
    private TacticsDialog dialog;
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setInitialVisible(false); cfg.disableAudio(true); cfg.setWindowedMode(1600, 900);
        new Lwjgl3Application(new MatchTacticsLayoutRegression(), cfg);
    }
    public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            Club club = new GameDatabase().getClubs().get(0);
            club.autoSelectBestFormationAndXI();
            stage = new Stage();
            dialog = new TacticsDialog(game, club, 0, new java.util.ArrayList<>(), club.getBenchPlayers(), false, () -> {}, (a,b) -> {}, () -> {});
            dialog.show(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(.1f); stage.draw();
        if (++frames == 8) {
            Actor presets = stage.getRoot().findActor("match-tactics-presets");
            Actor content = stage.getRoot().findActor("match-tactics-content");
            Actor scroll = content.getParent();
            Vector2 presetBottom = presets.localToStageCoordinates(new Vector2());
            Vector2 scrollTop = scroll.localToStageCoordinates(new Vector2(0, scroll.getHeight()));
            if (scrollTop.y > presetBottom.y + 1) throw new AssertionError("Body overlaps presets");
            int buttons = 0;
            for (Actor child : ((Group) presets).getChildren()) if (child instanceof TextButton) {
                buttons++;
                Vector2 point = child.localToStageCoordinates(new Vector2(child.getWidth()/2, child.getHeight()/2));
                Actor hit = stage.hit(point.x, point.y, true);
                if (hit == null || !(hit == child || hit.isDescendantOf(child))) throw new AssertionError("Preset obscured");
            }
            if (buttons != 6) throw new AssertionError("Missing presets");
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(System.getProperty("user.dir") + "/../build/reports/match-tactics.png"), pixmap);
            pixmap.dispose();
            System.out.println("Match tactics layout: all six presets visible and hittable; body separate OK.");
            Gdx.app.exit();
        }
    }
    public void dispose() { stage.dispose(); game.skin.dispose(); }
}
