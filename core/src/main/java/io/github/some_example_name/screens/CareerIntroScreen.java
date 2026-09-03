package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.function.Supplier;

/** Introdução cinematográfica reproduzida uma vez após a escolha da franquia. */
public class CareerIntroScreen implements Screen {
    private static final String DESKTOP_VIDEO = "0829_intro.webm";
    private static final String SOURCE_VIDEO = "0829.mp4";

    private final Main game;
    private final Supplier<Screen> nextScreen;
    private final Stage stage;
    private VideoPlayer videoPlayer;
    private VideoActor videoActor;
    private TextButton skipButton;
    private boolean finished;

    public CareerIntroScreen(Main game, Supplier<Screen> nextScreen) {
        this.game = game;
        this.nextScreen = nextScreen;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override public void show() {
        createInterface();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE && skipButton.isVisible()) finishIntro();
                else revealSkipButton();
                return true;
            }

            @Override public boolean mouseMoved(int screenX, int screenY) {
                revealSkipButton();
                return false;
            }

            @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                revealSkipButton();
                return false;
            }
        }));
        startVideo();
    }

    private void createInterface() {
        stage.clear();
        Stack background = new Stack();
        background.setFillParent(true);
        stage.addActor(background);

        Image black = new Image(StyleFactory.createSolid(Color.BLACK));
        black.setScaling(Scaling.stretch);
        background.add(black);

        Table controls = new Table();
        controls.setFillParent(true);
        controls.bottom().right().pad(0f, 38f, 32f, 0f);
        skipButton = ScreenUI.createPrimaryButton(game.skin, "PULAR INTRODUÇÃO  ›");
        skipButton.getLabel().setFontScale(.58f);
        skipButton.setVisible(false);
        skipButton.getColor().a = 0f;
        skipButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { finishIntro(); }
        });
        controls.add(skipButton).width(285f).height(52f);
        stage.addActor(controls);
    }

    private void startVideo() {
        try {
            FileHandle video = Gdx.files.internal(DESKTOP_VIDEO);
            if (!video.exists()) video = Gdx.files.internal(SOURCE_VIDEO);
            if (!video.exists()) {
                finishIntro();
                return;
            }

            videoPlayer = VideoPlayerCreator.createVideoPlayer();
            videoPlayer.setLooping(false);
            videoPlayer.setVolume(1f);
            videoPlayer.setOnCompletionListener(file -> Gdx.app.postRunnable(this::finishIntro));
            videoPlayer.setOnVideoSizeListener(this::layoutVideo);
            videoActor = new VideoActor(videoPlayer);
            // O vídeo fica entre o fundo preto e a camada de controles.
            stage.getRoot().addActorAt(1, videoActor);
            layoutVideo(ResponsiveViewport.DESIGN_WIDTH, ResponsiveViewport.DESIGN_HEIGHT);
            if (!videoPlayer.load(video)) {
                finishIntro();
                return;
            }
            videoPlayer.play();
        } catch (Throwable error) {
            Gdx.app.error("WFL-INTRO", "Não foi possível reproduzir a introdução.", error);
            finishIntro();
        }
    }

    private void layoutVideo(float videoWidth, float videoHeight) {
        if (videoActor == null || videoWidth <= 0f || videoHeight <= 0f) return;
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();
        float scale = Math.min(worldWidth / videoWidth, worldHeight / videoHeight);
        float width = videoWidth * scale;
        float height = videoHeight * scale;
        videoActor.setBounds((worldWidth - width) * .5f, (worldHeight - height) * .5f, width, height);
    }

    private void revealSkipButton() {
        if (skipButton == null || skipButton.isVisible() || finished) return;
        skipButton.setVisible(true);
        skipButton.clearActions();
        skipButton.addAction(Actions.fadeIn(.22f));
    }

    private void finishIntro() {
        if (finished) return;
        finished = true;
        game.setScreen(nextScreen.get());
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (videoPlayer != null && videoPlayer.isBuffered()) {
            layoutVideo(videoPlayer.getVideoWidth(), videoPlayer.getVideoHeight());
        }
    }

    @Override public void pause() { if (videoPlayer != null) videoPlayer.pause(); }
    @Override public void resume() { if (videoPlayer != null && !finished) videoPlayer.resume(); }
    @Override public void hide() { }

    @Override public void dispose() {
        if (videoActor != null) {
            videoActor.remove();
            videoActor = null;
        }
        if (videoPlayer != null) {
            try { videoPlayer.stop(); } catch (Throwable ignored) { }
            videoPlayer.dispose();
            videoPlayer = null;
        }
        stage.dispose();
    }
}
