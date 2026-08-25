package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

public class PlayoffScreen implements Screen {
    private final Main game;
    private final List<String> logs;
    private Stage stage;

    public PlayoffScreen(Main game, List<String> logs) {
        this.game = game;
        this.logs = logs;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.background(game.background);
        stage.addActor(root);

        Table content = new Table();
        content.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.07f, 0.90f), StyleFactory.GOLD));
        content.center().pad(50);

        Label.LabelStyle titleStyle = game.skin.get("title", Label.LabelStyle.class);
        content.add(new Label("RESULTADOS DOS PLAYOFFS", titleStyle)).padBottom(40).row();

        for (String line : logs) {
            Label l = new Label(line, game.skin);
            l.setFontScale(1.2f);
            if (line.contains("🏆")) l.setFontScale(1.8f);
            content.add(l).padBottom(10).row();
        }

        ImageTextButton btnBack = IconTextButton.create("VOLTAR AO MENU", game.skin,
                "Icons8/icons8-página-inicial-50.png");
        btnBack.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        content.add(btnBack).width(400).height(80).padTop(50);

        ScrollPane scroll = new ScrollPane(content, game.skin);
        root.add(scroll).expand().fill();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
