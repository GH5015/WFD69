package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.some_example_name.engine.TacticalPreset;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.TacticsScreen;
import io.github.some_example_name.utils.StyleFactory;
import java.lang.reflect.*;
import java.util.List;

public final class BenchSelectionLayoutRegression extends ApplicationAdapter {
    private Main game;
    private TacticsScreen screen;
    private Stage stage;
    private Club club;
    private Player reserve, outside;
    private int frames;
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new BenchSelectionLayoutRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main(); Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.background = StyleFactory.createCheckerboard(); game.database = new GameDatabase();
            game.league = new League("WFL", 1969); game.database.getClubs().forEach(game.league::addClub);
            club = game.league.getClubs().get(0); game.playerClub = club;
            open();
            List<Player> bench = club.getBenchPlayers(); reserve = bench.get(0);
            for (Player p : club.getSquad()) if (p.canPlay() && !bench.contains(p) && !club.getTacticsMap().containsValue(p)) { outside = p; break; }
            if (outside == null) throw new AssertionError("No unselected fixture player");
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private void open() throws Exception {
        screen = new TacticsScreen(game, club); screen.show(); screen.resize(1600, 900);
        Field field = TacticsScreen.class.getDeclaredField("stage"); field.setAccessible(true); stage = (Stage) field.get(screen);
    }
    @Override public void render() {
        screen.render(1f / 30);
        if (++frames == 5) {
            click(outside); click(reserve);
            if (!club.getBenchPlayers().contains(outside) || club.getBenchPlayers().contains(reserve)) throw new AssertionError("UI swap failed");
        }
        if (frames == 10) {
            screen.dispose(); try { open(); } catch (Exception e) { throw new RuntimeException(e); }
            if (!club.getBenchPlayers().contains(outside)) throw new AssertionError("Reopening reset choice");
            click(outside); click(reserve);
            if (!club.getBenchPlayers().contains(reserve) || club.getBenchPlayers().contains(outside)) throw new AssertionError("Reverse UI swap failed");
            testInstructions();
            System.out.println("Bench UI and tactics instructions: swaps, reopening and all six presets OK."); Gdx.app.exit();
        }
    }
    private void click(Player player) {
        Actor row = stage.getRoot().findActor("tactics-squad-" + player.getId());
        if (row == null) throw new AssertionError("Player row missing");
        for (EventListener listener : row.getListeners()) if (listener instanceof ClickListener) ((ClickListener) listener).clicked(new InputEvent(), 1, 1);
    }
    private void testInstructions() {
        try {
            Field tab = TacticsScreen.class.getDeclaredField("showTacticsTab"); tab.setAccessible(true); tab.setBoolean(screen, true);
            Method refresh = TacticsScreen.class.getDeclaredMethod("refreshUI"); refresh.setAccessible(true); refresh.invoke(screen);
            screen.render(1f / 30);
            for (TacticalPreset preset : TacticalPreset.values()) {
                TextButton button = findButton(stage.getRoot(), preset.getLabel());
                if (button == null) throw new AssertionError("Preset missing: " + preset.getLabel());
                for (EventListener listener : button.getListeners()) if (listener instanceof ClickListener)
                    ((ClickListener) listener).clicked(new InputEvent(), 1, 1);
                screen.render(1f / 30);
                if (!preset.matches(club)) throw new AssertionError("Preset did not apply: " + preset.getLabel());
            }
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
    private TextButton findButton(Group group, String text) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextButton && ((TextButton) actor).getText().toString().startsWith(text)) return (TextButton) actor;
            if (actor instanceof Group) { TextButton found = findButton((Group) actor, text); if (found != null) return found; }
        }
        return null;
    }
    @Override public void dispose() { screen.dispose(); game.skin.dispose(); }
}
