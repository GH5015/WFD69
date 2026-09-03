package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.utils.ClubLogoAssets;
import java.util.ArrayList;
import java.util.List;

/** Checks all club crests and renders a comparison sheet without opening a visible window. */
public final class LogoLayoutRegression extends ApplicationAdapter {
    private static final String[] FILES = {"santos", "rio", "milano", "bavaria", "manchester", "london",
        "amsterdam_total", "madrid", "barcelona", "budapest", "lisboa", "buenosaires", "montevideo",
        "paris", "belfast", "tokyo", "seoul", "tehran", "baghdad", "telaviv", "mexico", "cairo",
        "shanghai", "sidney", "newyork", "riyadh", "bangkok", "bombay", "marseille", "jakarta"};
    private final List<Texture> textures = new ArrayList<>();
    private SpriteBatch batch;
    private BitmapFont font;
    private static String output;

    public static void main(String[] args) {
        output = args[0];
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);
        config.disableAudio(true);
        config.setWindowedMode(1200, 1000);
        new Lwjgl3Application(new LogoLayoutRegression(), config);
    }

    @Override public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        for (String name : FILES) {
            Pixmap original = new Pixmap(Gdx.files.internal(name + ".png"));
            Pixmap framed = ClubLogoAssets.frame(original);
            verify(framed, name, true);
            framed.dispose();
            original.dispose();
            textures.add(ClubLogoAssets.load(name + ".png"));
        }
        for (int[] size : new int[][] {{100, 300}, {300, 100}, {1, 1}}) {
            Pixmap source = new Pixmap(size[0], size[1], Pixmap.Format.RGBA8888);
            source.setColor(1, 1, 1, 1);
            source.fill();
            Pixmap framed = ClubLogoAssets.frame(source);
            verify(framed, "synthetic", true);
            framed.dispose();
            source.dispose();
        }
        Pixmap empty = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        Pixmap framed = ClubLogoAssets.frame(empty);
        verify(framed, "transparent", false);
        framed.dispose();
        empty.dispose();
    }

    private void verify(Pixmap image, String name, boolean visible) {
        if (image.getWidth() != 500 || image.getHeight() != 500) throw new AssertionError(name + " canvas");
        int left = 500, top = 500, right = -1, bottom = -1;
        for (int y = 0; y < 500; y++) for (int x = 0; x < 500; x++) {
            if ((image.getPixel(x, y) & 255) <= 16) continue;
            left = Math.min(left, x); right = Math.max(right, x);
            top = Math.min(top, y); bottom = Math.max(bottom, y);
        }
        if (!visible) {
            if (right != -1) throw new AssertionError("Transparent input acquired content");
            return;
        }
        int longest = Math.max(right - left + 1, bottom - top + 1);
        if (longest < 357 || longest > 360 || Math.abs(left + right - 499) > 3
            || Math.abs(top + bottom - 499) > 3) throw new AssertionError(name + " framing: " + longest);
    }

    @Override public void render() {
        ScreenUtils.clear(.025f, .07f, .05f, 1);
        batch.begin();
        for (int i = 0; i < textures.size(); i++) {
            float x = (i % 6) * 200, y = 800 - (i / 6) * 200;
            batch.draw(textures.get(i), x, y + 12, 190, 190);
            font.draw(batch, FILES[i], x + 15, y + 22);
        }
        batch.end();
        Pixmap screenshot = Pixmap.createFromFrameBuffer(0, 0, 1200, 1000);
        PixmapIO.writePNG(Gdx.files.absolute(output), screenshot, -1, true);
        screenshot.dispose();
        System.out.println("All 30 crests: centered 500x500 framing, preserved proportions. Synthetic cases passed.");
        Gdx.app.exit();
    }

    @Override public void dispose() {
        for (Texture texture : textures) texture.dispose();
        batch.dispose();
        font.dispose();
    }
}
