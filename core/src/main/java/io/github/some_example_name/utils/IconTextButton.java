package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

/** Botão de ação com ícone, mantendo o estilo visual do Skin. */
public final class IconTextButton {
    private static final Map<String, Texture> ICONS = new HashMap<>();

    private IconTextButton() {}

    public static ImageTextButton create(String text, Skin skin, String iconPath) {
        TextButton.TextButtonStyle base = skin.get("default", TextButton.TextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle(base);
        Texture texture = ICONS.get(iconPath);
        if (texture == null) {
            try {
                texture = new Texture(Gdx.files.internal(iconPath));
            } catch (Exception e) {
                texture = new Texture(Gdx.files.internal("libgdx.png"));
            }
            ICONS.put(iconPath, texture);
        }
        style.imageUp = new TextureRegionDrawable(texture);
        style.imageOver = style.imageUp;
        style.imageDown = style.imageUp;
        ImageTextButton button = new ImageTextButton(text, style);
        button.getLabel().setFontScale(0.82f);
        return button;
    }

    public static void dispose() {
        for (Texture texture : ICONS.values()) texture.dispose();
        ICONS.clear();
    }
}
