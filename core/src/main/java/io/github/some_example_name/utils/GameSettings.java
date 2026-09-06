package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;

/** Preferências visuais e de áudio persistidas entre execuções do jogo. */
public final class GameSettings {
    public static final int[][] RESOLUTIONS = {
        {1280, 720},
        {1366, 768},
        {1600, 900},
        {1920, 1080},
        {2560, 1440}
    };
    public static final int[] FPS_LIMITS = {60, 120, 144, 240};
    public static final int[] VOLUME_LEVELS = {0, 25, 50, 75, 100};

    private static final String PREFERENCES = "worldfd69-settings";
    private static final String INITIALIZED = "initialized";
    private static final String FULLSCREEN = "fullscreen";
    private static final String VSYNC = "vsync";
    private static final String WINDOW_WIDTH = "window-width";
    private static final String WINDOW_HEIGHT = "window-height";
    private static final String FPS_LIMIT = "fps-limit";
    private static final String MASTER_VOLUME = "master-volume";

    private GameSettings() { }

    /** Aplica as opções gravadas sem alterar a configuração inicial na primeira execução. */
    public static void applySaved() {
        Preferences preferences = preferences();
        if (!preferences.getBoolean(INITIALIZED, false)) return;
        apply(
            preferences.getBoolean(FULLSCREEN, false),
            preferences.getBoolean(VSYNC, true),
            preferences.getInteger(WINDOW_WIDTH, 1280),
            preferences.getInteger(WINDOW_HEIGHT, 720),
            preferences.getInteger(FPS_LIMIT, 120)
        );
    }

    public static void saveAndApply(
        boolean fullscreen,
        boolean vsync,
        int windowWidth,
        int windowHeight,
        int fpsLimit,
        int volumePercent
    ) {
        Preferences preferences = preferences();
        preferences.putBoolean(INITIALIZED, true);
        preferences.putBoolean(FULLSCREEN, fullscreen);
        preferences.putBoolean(VSYNC, vsync);
        preferences.putInteger(WINDOW_WIDTH, Math.max(960, windowWidth));
        preferences.putInteger(WINDOW_HEIGHT, Math.max(540, windowHeight));
        preferences.putInteger(FPS_LIMIT, closest(FPS_LIMITS, fpsLimit));
        preferences.putInteger(MASTER_VOLUME, clamp(volumePercent, 0, 100));
        preferences.flush();
        apply(fullscreen, vsync, windowWidth, windowHeight, fpsLimit);
    }

    private static void apply(
        boolean fullscreen,
        boolean vsync,
        int windowWidth,
        int windowHeight,
        int fpsLimit
    ) {
        try {
            Gdx.graphics.setVSync(vsync);
            Gdx.graphics.setForegroundFPS(closest(FPS_LIMITS, fpsLimit));
            if (fullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Graphics.DisplayMode display = Gdx.graphics.getDisplayMode();
                int width = Math.min(Math.max(960, windowWidth), display.width);
                int height = Math.min(Math.max(540, windowHeight), display.height);
                Gdx.graphics.setWindowedMode(width, height);
            }
        } catch (Throwable error) {
            Gdx.app.error("WFL-SETTINGS", "Não foi possível aplicar as configurações de vídeo.", error);
        }
    }

    public static boolean isFullscreen() {
        return preferences().getBoolean(FULLSCREEN, Gdx.graphics.isFullscreen());
    }

    public static boolean isVsyncEnabled() {
        return preferences().getBoolean(VSYNC, true);
    }

    public static int getWindowWidth() {
        return preferences().getInteger(WINDOW_WIDTH, closestResolution()[0]);
    }

    public static int getWindowHeight() {
        return preferences().getInteger(WINDOW_HEIGHT, closestResolution()[1]);
    }

    public static int getFpsLimit() {
        return preferences().getInteger(FPS_LIMIT, 120);
    }

    public static int getVolumePercent() {
        return preferences().getInteger(MASTER_VOLUME, 100);
    }

    public static float getMasterVolume() {
        return getVolumePercent() / 100f;
    }

    private static int[] closestResolution() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int[] best = RESOLUTIONS[0];
        long bestDistance = Long.MAX_VALUE;
        for (int[] resolution : RESOLUTIONS) {
            long dx = resolution[0] - width;
            long dy = resolution[1] - height;
            long distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                best = resolution;
                bestDistance = distance;
            }
        }
        return best;
    }

    private static int closest(int[] values, int requested) {
        int best = values[0];
        int distance = Math.abs(best - requested);
        for (int value : values) {
            int candidateDistance = Math.abs(value - requested);
            if (candidateDistance < distance) {
                best = value;
                distance = candidateDistance;
            }
        }
        return best;
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static Preferences preferences() {
        return Gdx.app.getPreferences(PREFERENCES);
    }
}
