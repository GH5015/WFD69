package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubProfile;

/** Uniforme principal compartilhado pela seleção de clubes e pelo campo tático. */
public final class ClubUniformAssets {
    private ClubUniformAssets() { }

    public static String forClub(Club club) {
        String key = ClubProfile.forClub(club).uniformKey;
        // Preserva a grafia dos arquivos fornecidos pelo usuário.
        String assetKey = "amsterdam".equals(key) ? "amsterdan" : "tehran".equals(key) ? "theran" : key;
        String[] paths = {"uniforme_" + key + ".png", "uniforme_" + assetKey + ".png",
            "uniforms/" + key + "_home.png", key + "_home.png"};
        for (String path : paths) if (Gdx.files.internal(path).exists()) return path;
        return "Icons8/icons8-camisa-de-jogador-50.png";
    }

    /** Enquadramento virtual de 500x500, independente da resolução e das margens do PNG. */
    public static Drawable drawable(Texture texture) {
        TextureData data = texture.getTextureData();
        if (!data.isPrepared()) data.prepare();
        Pixmap pixels = data.consumePixmap();
        try {
            int left = pixels.getWidth(), top = pixels.getHeight(), right = -1, bottom = -1;
            for (int y = 0; y < pixels.getHeight(); y++) {
                for (int x = 0; x < pixels.getWidth(); x++) {
                    // Desconsidera resíduos quase invisíveis na margem transparente.
                    if ((pixels.getPixel(x, y) & 0xff) <= 16) continue;
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                }
            }
            TextureRegion region = right < left ? new TextureRegion(texture)
                : new TextureRegion(texture, left, top, right - left + 1, bottom - top + 1);
            return new UniformDrawable(region);
        } finally {
            if (data.disposePixmap()) pixels.dispose();
        }
    }

    private static final class UniformDrawable extends BaseDrawable {
        // A camisa do Santos em 500x500 ocupa 429px de altura, com 43px abaixo.
        private static final float REFERENCE_SIZE = 500f;
        private static final float SHIRT_HEIGHT = 429f / REFERENCE_SIZE;
        private static final float BOTTOM_MARGIN = 43f / REFERENCE_SIZE;
        private final TextureRegion region;

        UniformDrawable(TextureRegion region) {
            this.region = region;
            setMinWidth(REFERENCE_SIZE);
            setMinHeight(REFERENCE_SIZE);
        }

        @Override public void draw(Batch batch, float x, float y, float width, float height) {
            float scale = Math.min(height * SHIRT_HEIGHT / region.getRegionHeight(), width / region.getRegionWidth());
            float shirtWidth = region.getRegionWidth() * scale;
            float shirtHeight = region.getRegionHeight() * scale;
            batch.draw(region, x + (width - shirtWidth) / 2f,
                y + height * BOTTOM_MARGIN, shirtWidth, shirtHeight);
        }
    }
}
