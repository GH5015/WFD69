package io.github.some_example_name.utils;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

/** Variante com ícone para manter texto, ícone e borda dentro do botão. */
public class AdaptiveImageTextButton extends ImageTextButton {
    private static final float ICON_SIZE = 28f;
    private static final float ICON_GAP = 10f;
    private static final float HORIZONTAL_PADDING = 24f;
    private static final float VERTICAL_PADDING = 10f;

    public AdaptiveImageTextButton(String text, ImageTextButtonStyle style) {
        super(text, style);
        getLabel().setWrap(false);
        getImageCell().size(ICON_SIZE).padRight(ICON_GAP);
    }

    @Override
    public void layout() {
        super.layout();
        AdaptiveTextButton.fitLabel(
            getLabel(),
            Math.max(1f, getWidth() - ICON_SIZE - ICON_GAP),
            getHeight(),
            HORIZONTAL_PADDING,
            VERTICAL_PADDING
        );
    }
}
