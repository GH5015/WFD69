package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.utils.PlayerReportDialog;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

/** Biblioteca separada para relatórios de draft concluídos em 100%. */
public class CompletedScoutingScreen implements Screen {
    private final Main game;
    private final Club club;
    private final DraftScoutManager scoutManager;
    private final Stage stage;
    private final Texture backgroundTexture;

    public CompletedScoutingScreen(Main game, Club club, DraftScoutManager scoutManager) {
        this.game = game;
        this.club = club;
        this.scoutManager = scoutManager;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("prancheta.png"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        refreshUI();
    }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);
        root.add(background);

        Table page = ScreenUI.createPage(true);
        int completed = scoutManager.getCompletedTargets().size();
        page.add(ScreenUI.createHeader(game.skin, "JOGADORES OBSERVADOS", "RELATÓRIOS 100%  •  " + completed))
            .growX().height(ScreenUI.HEADER_HEIGHT).padBottom(10f).row();

        Table status = ScreenUI.createPanel();
        status.add(ScreenUI.createStatusBox(game.skin, "RELATÓRIOS CONCLUÍDOS", String.valueOf(completed), ScreenUI.SUCCESS))
            .growX().uniformX().padRight(8f);
        status.add(ScreenUI.createStatusBox(game.skin, "CONHECIMENTO", "100%", ScreenUI.SUCCESS))
            .growX().uniformX().padRight(8f);
        status.add(ScreenUI.createStatusBox(game.skin, "CLASSE", "1970", StyleFactory.SOFT_YELLOW))
            .growX().uniformX();
        page.add(status).growX().height(64f).padBottom(10f).row();
        page.add(createCompletedTable()).grow().padBottom(10f).row();

        TextButton back = ScreenUI.createPrimaryButton(game.skin, "VOLTAR AO SCOUTING");
        back.getLabel().setFontScale(0.55f);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DraftScoutingScreen(game, club, scoutManager));
            }
        });
        page.add(back).width(300f).height(50f).center().row();
        root.add(page);
        NavigationDrawer.attach(stage, game, club, "SCOUTING", true);
    }

    private Table createCompletedTable() {
        Table panel = ScreenUI.createTablePanel();
        Table content = new Table();
        content.top();
        content.add(createHeader()).growX().height(48f).row();
        int index = 0;
        for (ScoutTarget target : scoutManager.getCompletedTargets()) {
            content.add(createRow(target, index++)).growX().height(62f).row();
        }
        if (index == 0) {
            content.add(ScreenUI.createSubtitle(game.skin, "Nenhum relatório foi concluído ainda.")).height(100f).center().row();
        }
        ScrollPane scroll = new ScrollPane(content, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createHeader() {
        Table header = ScreenUI.createTableHeaderRow();
        addHeader(header, "NAC", 85f, Align.center);
        addHeader(header, "JOGADOR", 300f, Align.left);
        addHeader(header, "IDADE", 85f, Align.center);
        addHeader(header, "POS", 100f, Align.center);
        addHeader(header, "OVR", 120f, Align.center);
        addHeader(header, "POTENCIAL", 140f, Align.center);
        addHeader(header, "RELATÓRIO", 170f, Align.center);
        return header;
    }

    private Table createRow(ScoutTarget target, int index) {
        Player player = target.getPlayer();
        Table row = ScreenUI.createRow(index);
        String nationality = player.getNationality() == null ? "N/A" : player.getNationality();
        row.add(ScreenUI.createBoldValue(game.skin, nationality.substring(0, Math.min(3, nationality.length())).toUpperCase(), ScreenUI.MUTED_TEXT, Align.center)).width(85f);
        Label name = ScreenUI.createBoldValue(game.skin, player.getName(), Color.WHITE, Align.left);
        name.setEllipsis(true);
        row.add(name).width(300f).padLeft(8f);
        row.add(ScreenUI.createBoldValue(game.skin, String.valueOf(player.getAge()), Color.WHITE, Align.center)).width(85f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(100f).height(28f);
        row.add(ScreenUI.createBoldValue(game.skin, target.getDisplayOverall(), StyleFactory.SOFT_YELLOW, Align.center)).width(120f);
        row.add(ScreenUI.createBoldValue(game.skin, target.getDisplayPotential(), ScreenUI.SUCCESS, Align.center)).width(140f);
        TextButton view = ScreenUI.createInteractiveButton("VER RELATÓRIO", game.skin);
        view.getLabel().setFontScale(0.5f);
        view.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { openReport(target); }
        });
        row.add(view).width(150f).height(34f);
        return row;
    }

    private void openReport(ScoutTarget target) {
        new PlayerReportDialog(game.skin, target, scoutManager, this::refreshUI).show(stage);
    }

    private void addHeader(Table table, String title, float width, int alignment) {
        table.add(ScreenUI.createTableHeaderLabel(game.skin, title, alignment)).width(width);
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); backgroundTexture.dispose(); }
}
