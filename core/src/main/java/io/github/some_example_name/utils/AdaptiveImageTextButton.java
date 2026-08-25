package io.github.some_example_name.utils;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

/** Variante com ícone para manter texto, ícone e borda dentro do botão. */
public class AdaptiveImageTextButton extends ImageTextButton {
    private static final float HORIZONTAL_PADDING = 44f;
    private static final float VERTICAL_PADDING = 10f;

    public AdaptiveImageTextButton(String text, ImageTextButtonStyle style) {
        super(text, style);
        getLabel().setWrap(false);
    }

    @Override
    public void layout() {
        super.layout();
        AdaptiveTextButton.fitLabel(
            getLabel(),
            getWidth(),
            getHeight(),
            HORIZONTAL_PADDING,
            VERTICAL_PADDING
        );
    }
}
