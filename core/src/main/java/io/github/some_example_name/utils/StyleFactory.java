package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.ArrayList;
import java.util.List;

public class StyleFactory {
    private static final List<Texture> GENERATED_TEXTURES = new ArrayList<>();

    public static final Color PRUSSIAN_GREEN = Color.valueOf("0F281B");
    public static final Color MUSGO_DEEP = Color.valueOf("091A11");
    public static final Color GOLD = Color.valueOf("D4AF37");
    public static final Color DARK_GOLD = Color.valueOf("997A15");
    public static final Color SOFT_YELLOW = Color.valueOf("F7E5A9");
    public static final Color YELLOW_TITLE = Color.valueOf("FFE8A3");
    public static final Color WINE_RED = Color.valueOf("7A121C");
    public static final Color METAL_DARK = Color.valueOf("1C2127");
    public static final Color CARD_BG = Color.valueOf("12161B");
    public static final Color CREME_AGED = Color.valueOf("F4F0EA");
    public static final Color STADIUM_GREEN = Color.valueOf("163E2B");
    public static final Color PLAYOFF_GOLD = Color.valueOf("FFD700");
    public static final Color MUSGO_LIGHT = Color.valueOf("1E3F20"); // <--- Adicione esta constante

    public static final Color POS_GK = Color.valueOf("2980B9");
    public static final Color POS_DEF = Color.valueOf("27AE60");
    public static final Color POS_MID = Color.valueOf("F39C12");
    public static final Color POS_ATT = Color.valueOf("E74C3C");

    public static Drawable createSolid(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new TextureRegionDrawable(texture);
    }

    public static Drawable createModernButton(int width, int height, Color baseColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int radius = Math.max(6, Math.min(12, Math.min(width, height) / 4));
        fillRounded(pixmap, 0, 0, width, height, radius, borderColor);
        fillRounded(pixmap, 2, 2, width - 4, height - 4, Math.max(3, radius - 2), baseColor);
        pixmap.setColor(new Color(1f, 1f, 1f, 0.07f));
        pixmap.fillRectangle(3, height / 2, width - 6, Math.max(1, height / 2 - 3));
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 6, 6, 6, 6));
    }

    private static void fillRounded(Pixmap pixmap, int x, int y, int width, int height, int radius, Color color) {
        pixmap.setColor(color);
        pixmap.fillRectangle(x + radius, y, width - radius * 2, height);
        pixmap.fillRectangle(x, y + radius, width, height - radius * 2);
        pixmap.fillCircle(x + radius, y + radius, radius);
        pixmap.fillCircle(x + width - radius - 1, y + radius, radius);
        pixmap.fillCircle(x + radius, y + height - radius - 1, radius);
        pixmap.fillCircle(x + width - radius - 1, y + height - radius - 1, radius);
    }

    public static Drawable createRoundedPanel(Color background, Color border) {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        fillRounded(pixmap, 0, 0, size, size, 12, border);
        fillRounded(pixmap, 2, 2, size - 4, size - 4, 10, background);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 12, 12, 12, 12));
    }

    public static Drawable createBadge(Color color) {
        int w = 48, h = 24;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        fillRounded(pixmap, 0, 0, w, h, 6, color);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 6, 6, 6, 6));
    }

    public static Color getPositionColor(String position) {
        if (position == null) return Color.WHITE;
        String pos = position.toUpperCase();
        if (pos.equals("GK")) return POS_GK;
        if (pos.matches("CB|RB|LB|RWB|LWB")) return POS_DEF;
        if (pos.matches("CDM|CM|CAM|RM|LM")) return POS_MID;
        if (pos.matches("RW|LW|CF|ST")) return POS_ATT;
        return Color.LIGHT_GRAY;
    }

    public static Drawable createCheckerboard() {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("121519"));
        pixmap.fill();
        pixmap.setColor(Color.valueOf("191D24"));
        pixmap.fillRectangle(0, 0, size/2, size/2);
        pixmap.fillRectangle(size/2, size/2, size/2, size/2);
        Texture texture = new Texture(pixmap);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new TextureRegionDrawable(texture);
    }

    public static Drawable createParchment() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(CREME_AGED);
        pixmap.fill();
        pixmap.setColor(new Color(0,0,0,0.2f));
        pixmap.drawRectangle(0,0,64,64);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 8, 8, 8, 8));
    }

    public static Drawable createMetallicBoard(int width, int height, Color bgColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(bgColor);
        pixmap.fillRectangle(0, 0, width, height);
        pixmap.setColor(GOLD);
        for(int i=0; i<3; i++) pixmap.drawRectangle(i, i, width-(i*2), height-(i*2));
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fillCircle(8, 8, 3);
        pixmap.fillCircle(width-8, 8, 3);
        pixmap.fillCircle(8, height-8, 3);
        pixmap.fillCircle(width-8, height-8, 3);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        GENERATED_TEXTURES.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 10, 10, 10, 10));
    }

    public static Drawable createBrushedMetal() { return createMetallicBoard(32, 32, METAL_DARK); }

    public static void disposeGenerated() {
        for (Texture texture : GENERATED_TEXTURES) texture.dispose();
        GENERATED_TEXTURES.clear();
    }
}
