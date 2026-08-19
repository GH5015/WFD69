package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

public class MenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture backgroundTexture;

    public MenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("background.png"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Image bgImage = new Image(backgroundTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);
        bgImage.toBack();

        Image overlay = new Image(StyleFactory.createSolid(new Color(0, 0, 0, 0.12f)));
        overlay.setFillParent(true);
        stage.addActor(overlay);

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.bottom().padBottom(60);
        stage.addActor(uiTable);

        ImageTextButton startButton = IconTextButton.create(
                "INICIAR CARREIRA", game.skin, "Icons8/icons8-ligar-50.png");
        startButton.getLabel().setFontScale(1.2f);
        startButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ClubManagementScreen(game, game.playerClub));
            }
        });

        ImageTextButton exitButton = IconTextButton.create("SAIR", game.skin, "Icons8/icons8-sair-50.png");
        exitButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table actions = new Table();
        actions.add(startButton).width(390).height(76).padRight(16);
        actions.add(exitButton).width(160).height(60);
        uiTable.add(actions);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
