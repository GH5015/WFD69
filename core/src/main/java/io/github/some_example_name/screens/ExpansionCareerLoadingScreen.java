package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.Main;
import io.github.some_example_name.engine.DevelopmentEngine;
import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.LeagueExpansionService;
import io.github.some_example_name.simulation.ExpansionCareerSimulator;
import io.github.some_example_name.simulation.PlayoffSimulator;
import io.github.some_example_name.simulation.SeasonSimulator;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import java.util.concurrent.CancellationException;

/** Mantém a interface responsiva enquanto uma nova liga é simulada fora da tela. */
public final class ExpansionCareerLoadingScreen extends ScreenAdapter {
    private final Main game;
    private final String clubName;
    private final Stage stage = new Stage(new ResponsiveViewport());
    private Label progressLabel;
    private volatile String progressText = "PREPARANDO A LIGA DE 1969…";
    private volatile float progress;
    private volatile boolean cancelled;
    private Thread worker;

    public ExpansionCareerLoadingScreen(Main game, String clubName) { this.game = game; this.clubName = clubName; }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        LeagueExpansionService.Franchise f = LeagueExpansionService.forClub(clubName);
        Table root = new Table(); root.setFillParent(true); stage.addActor(root);
        Table panel = ScreenUI.createPanel(); panel.pad(40f);
        Label title = ScreenUI.createSectionTitle(game.skin, "CONSTRUINDO A HISTÓRIA DA WFL");
        panel.add(title).padBottom(26f).row();
        Label text = new Label(clubName + " • ESTREIA " + f.year + "\n\n"
            + "Simulando temporadas desde 1969 com partidas, playoffs, evolução, drafts e mercado.\n"
            + "Você assumirá em novembro de " + (f.year - 1) + ", antes do Expansion Draft e da montagem do elenco."
            + "\n\nQuanto mais distante a estreia, maior o tempo de preparação.", game.skin);
        text.setWrap(true); text.setAlignment(Align.center); text.setFontScale(.8f);
        panel.add(text).width(1030f).padBottom(32f).row();
        progressLabel = new Label(progressText, game.skin, "font-bold");
        progressLabel.setWrap(true); progressLabel.setAlignment(Align.center);
        progressLabel.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(progressLabel).width(1030f).height(110f).padBottom(22f).row();
        TextButton cancel = ScreenUI.createSecondaryButton(game.skin, "CANCELAR / VOLTAR À SELEÇÃO");
        cancel.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                cancelled = true;
                if (worker != null) worker.interrupt();
                game.setScreen(new ClubSelectionScreen(game));
            }
        });
        panel.add(cancel).width(570f).height(58f);
        root.add(panel).width(1150f);
        worker = new Thread(() -> {
            try {
                ExpansionCareerSimulator.Result result = new ExpansionCareerSimulator().simulate(clubName,
                    (year, phase, value) -> { progress = value; progressText = "WFL " + year + " • " + phase; },
                    () -> cancelled);
                Gdx.app.postRunnable(() -> {
                    if (cancelled || game.getScreen() != this) return;
                    game.database = result.database;
                    game.league = result.league;
                    game.freeAgencyService = result.freeAgency;
                    game.matchEngine = new MatchEngine(result.league);
                    game.developmentEngine = new DevelopmentEngine();
                    game.seasonSimulator = new SeasonSimulator();
                    game.playoffSimulator = new PlayoffSimulator(game.matchEngine, result.league);
                    game.draftClass = result.draftClass;
                    game.draftClassYear = f.year;
                    game.draftScoutManager = new DraftScoutManager(result.club.getStaffLevel(io.github.some_example_name.model.StaffRole.SCOUT));
                    game.selectPlayerClub(result.club);
                    game.setScreen(new OffSeasonScreen(game, result.club));
                });
            } catch (CancellationException ignored) {
                // O estado anterior permanece intacto até a publicação do resultado.
            } catch (Exception failure) {
                Gdx.app.error("ExpansionCareer", "Falha ao preparar " + clubName, failure);
                progressText = "Não foi possível concluir a simulação.\nVolte à seleção para tentar novamente.";
            }
        }, "wfl-expansion-career");
        worker.setDaemon(true);
        worker.start();
    }

    @Override public void render(float delta) {
        ScreenUtils.clear(.025f, .07f, .045f, 1f);
        progressLabel.setText(Math.round(progress * 100) + "%\n" + progressText);
        stage.act(delta); stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void hide() { cancelled = true; if (worker != null) worker.interrupt(); }
    @Override public void dispose() { hide(); stage.dispose(); }
}
