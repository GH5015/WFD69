package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/** Enquadramento comum dos escudos, sem modificar os arquivos originais. */
public final class ClubLogoAssets {
    public static final int CANVAS_SIZE = 500;
    // Os escudos fornecidos em 500x500 ocupam aproximadamente 360px do quadro.
    public static final int CREST_SIZE = 360;

    private ClubLogoAssets() { }

    /** A tela que carrega o escudo continua responsável por descartar a textura. */
    public static Texture load(String path) {
        Pixmap source = new Pixmap(Gdx.files.internal(path));
        Pixmap framed = null;
        try {
            framed = frame(source);
            Texture texture = new Texture(framed);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return texture;
        } finally {
            source.dispose();
            if (framed != null) framed.dispose();
        }
    }

    /** Recorta só as margens transparentes e centraliza sem esticar o desenho. */
    public static Pixmap frame(Pixmap source) {
        int left = source.getWidth(), top = source.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getPixel(x, y) & 0xff) <= 16) continue;
                left = Math.min(left, x);
                right = Math.max(right, x);
                top = Math.min(top, y);
                bottom = Math.max(bottom, y);
            }
        }
        Pixmap result = new Pixmap(CANVAS_SIZE, CANVAS_SIZE, Pixmap.Format.RGBA8888);
        if (right < left) return result;
        int sourceWidth = right - left + 1, sourceHeight = bottom - top + 1;
        float scale = CREST_SIZE / (float) Math.max(sourceWidth, sourceHeight);
        int width = Math.max(1, Math.round(sourceWidth * scale));
        int height = Math.max(1, Math.round(sourceHeight * scale));
        result.setBlending(Pixmap.Blending.None);
        result.setFilter(Pixmap.Filter.BiLinear);
        result.drawPixmap(source, left, top, sourceWidth, sourceHeight,
            (CANVAS_SIZE - width) / 2, (CANVAS_SIZE - height) / 2, width, height);
        return result;
    }
}
