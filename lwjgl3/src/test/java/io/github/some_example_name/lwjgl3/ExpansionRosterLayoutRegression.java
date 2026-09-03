package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.screens.ExpansionDraftDialog;
import io.github.some_example_name.utils.ResponsiveViewport;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public final class ExpansionRosterLayoutRegression extends ApplicationAdapter {
    private Main game;
    private Club club;
    private Stage stage;
    private List<Player> chosen;
    private int frames;
    private boolean completed;
    private static String output;
    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false); config.disableAudio(true); config.setWindowedMode(1600, 900);
        new Lwjgl3Application(new ExpansionRosterLayoutRegression(), config);
    }
    @Override public void create() {
        try {
            game = new Main();
            Method setup = Main.class.getDeclaredMethod("setupSkin"); setup.setAccessible(true); setup.invoke(game);
            game.league = new League("WFL", 1973); game.league.setCurrentStage("OFFSEASON");
            for (int i = 0; i < 20; i++) {
                Club team = new Club("Clube Original " + i, "Brasil", i < 15 ? "Ocidental" : "Oriental", 80, 40_000_000, "Arena", "santos.png");
                game.league.addClub(team);
                for (int j = 0; j < 25; j++) {
                    Player player = new Player("Jogador de nome comprido " + i + "/" + j, "Brasil", j % 7 == 0 ? Position.GK : Position.CM,
                        null, 24, new TechnicalAttributes(55 + j, 55 + j, 55 + j, 70, 65, 60), 90, 10_000);
                    player.renewContract(240_000, 3, 1973); player.transferTo(team);
                }
            }
            club = LeagueExpansionService.prepare(game.league, 1974).get(1); club.setUserControlled(true);
            chosen = LeagueExpansionService.suggestedSelections(game.league, club);
            stage = new Stage(new ResponsiveViewport()); stage.getViewport().update(1600, 900, true);
            ExpansionDraftDialog.show(stage, game, club, () -> completed = true);
        } catch (Exception failure) { throw new RuntimeException(failure); }
    }
    @Override public void render() {
        ScreenUtils.clear(.025f, .07f, .045f, 1f); stage.act(1f / 30f); stage.draw();
        if (++frames == 10) {
            Dialog dialog = stage.getRoot().findActor("expansion-roster-dialog");
            TextButton confirm = dialog.findActor("confirm-expansion-roster");
            if (!confirm.isDisabled()) throw new AssertionError("Confirmação vazia habilitada");
            for (Player p : chosen) click(dialog.findActor("expansion-player-" + p.getId()));
            if (confirm.isDisabled() || !club.getSquad().isEmpty()) throw new AssertionError("Seleção não é reversível");
        }
        if (frames == 20) {
            Pixmap image = Pixmap.createFromFrameBuffer(0, 0, 1600, 900);
            PixmapIO.writePNG(Gdx.files.absolute(output), image, -1, true); image.dispose();
            Dialog dialog = stage.getRoot().findActor("expansion-roster-dialog");
            if (dialog.getX() < 0 || dialog.getY() < 0 || dialog.getTop() > stage.getHeight()) throw new AssertionError("Modal fora da tela");
            click(dialog.findActor("confirm-expansion-roster"));
            if (!completed || !new HashSet<>(club.getSquad()).equals(new HashSet<>(chosen))) throw new AssertionError("Escolhas não aplicadas");
            if (LeagueExpansionService.isPending(game.league)) throw new AssertionError("Expansão não concluiu");
            System.out.println("Manual expansion UI: 20 button choices, confirmation, exact transfers and modal bounds OK.");
            Gdx.app.exit();
        }
    }
    private void click(Actor actor) {
        if (actor == null) throw new AssertionError("Botão de escolha ausente");
        for (EventListener listener : actor.getListeners()) if (listener instanceof ClickListener)
            ((ClickListener) listener).clicked(new InputEvent(), 1, 1);
    }
    @Override public void dispose() { stage.dispose(); game.skin.dispose(); }
}
