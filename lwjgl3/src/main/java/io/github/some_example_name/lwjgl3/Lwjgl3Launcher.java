package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Graphics.DisplayMode;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.ResponsiveViewport;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("WorldFD69 - Liga Mundial de Futebol");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        configuration.setResizable(true);

        // Abre com o maior tamanho 16:9 que cabe confortavelmente no monitor.
        // Depois disso a janela pode ser redimensionada livremente; o viewport
        // mantém o layout proporcional em qualquer resolução e aspect ratio.
        DisplayMode display =
            Lwjgl3ApplicationConfiguration.getDisplayMode();

        float availableWidth = display.width * 0.90f;
        float availableHeight = display.height * 0.90f;
        float initialScale = Math.min(
            1f,
            Math.min(
                availableWidth / ResponsiveViewport.DESIGN_WIDTH,
                availableHeight / ResponsiveViewport.DESIGN_HEIGHT
            )
        );

        int windowWidth = Math.max(
            1,
            Math.round(ResponsiveViewport.DESIGN_WIDTH * initialScale)
        );
        int windowHeight = Math.max(
            1,
            Math.round(ResponsiveViewport.DESIGN_HEIGHT * initialScale)
        );

        configuration.setWindowedMode(windowWidth, windowHeight);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
