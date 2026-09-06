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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.SaveGameService;
import io.github.some_example_name.utils.StyleFactory;

public class MenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture backgroundTexture;

    public MenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ResponsiveViewport());
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
        startButton.setName("menu-start-button");
        startButton.getLabel().setFontScale(0.68f);
        startButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CareerIntroScreen(game, () -> new ClubSelectionScreen(game)));
            }
        });

        ImageTextButton loadButton = IconTextButton.create(
                "CARREGAR PARTIDA", game.skin, "Icons8/icons8-abrir-pasta-50.png");
        loadButton.setName("menu-load-button");
        loadButton.setDisabled(!SaveGameService.hasSave());
        if (loadButton.isDisabled()) loadButton.getLabel().setColor(Color.GRAY);
        loadButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (loadButton.isDisabled()) return;
                try {
                    SaveGameService.load(game);
                    if (game.managerCareer.isUnemployed()) {
                        game.setScreen(new UnemployedScreen(game));
                    } else if ("OFFSEASON".equals(game.league.getCurrentStage())) {
                        game.setScreen(new OffSeasonScreen(game, game.playerClub));
                    } else {
                        game.setScreen(new ClubManagementScreen(game, game.playerClub));
                    }
                } catch (Exception failure) {
                    Gdx.app.error("WFL-SAVE", "Não foi possível carregar a partida.", failure);
                    Dialog dialog = new Dialog("ERRO AO CARREGAR", game.skin);
                    dialog.text(failure.getMessage() == null ? "O save não pôde ser carregado." : failure.getMessage());
                    dialog.button("FECHAR");
                    dialog.show(stage);
                }
            }
        });

        ImageTextButton settingsButton = IconTextButton.create(
                "CONFIGURAÇÕES", game.skin, "Icons8/icons8-configurações-50.png");
        settingsButton.setName("menu-settings-button");
        settingsButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                SettingsDialog.show(game, stage);
            }
        });

        ImageTextButton exitButton = IconTextButton.create("SAIR", game.skin, "Icons8/icons8-sair-50.png");
        exitButton.setName("menu-exit-button");
        exitButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table actions = new Table();
        actions.add(startButton).width(330).height(68).padRight(12);
        actions.add(loadButton).width(275).height(58).padRight(12);
        actions.add(settingsButton).width(245).height(58).padRight(12);
        actions.add(exitButton).width(135).height(54);
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
