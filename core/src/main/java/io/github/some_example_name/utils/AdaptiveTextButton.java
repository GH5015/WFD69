package io.github.some_example_name.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Botão que reduz somente a fonte necessária para manter o texto dentro da
 * área clicável. Isso evita que rótulos longos atravessem bordas quando uma
 * tela é usada em resoluções menores.
 */
public class AdaptiveTextButton extends TextButton {
    private static final float HORIZONTAL_PADDING = 20f;
    private static final float VERTICAL_PADDING = 10f;
    private static final float MIN_FONT_SCALE = 0.34f;
    private static final float MAX_FONT_SCALE = 0.72f;

    public AdaptiveTextButton(String text, TextButtonStyle style) {
        super(text, style);
        configureLabel();
    }

    public AdaptiveTextButton(String text, Skin skin) {
        super(text, skin);
        configureLabel();
    }

    public AdaptiveTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        configureLabel();
    }

    private void configureLabel() {
        getLabel().setWrap(false);
    }

    @Override
    public void layout() {
        super.layout();
        fitLabel(getLabel(), getWidth(), getHeight(), HORIZONTAL_PADDING, VERTICAL_PADDING);
    }

    static void fitLabel(
        Label label,
        float width,
        float height,
        float horizontalPadding,
        float verticalPadding
    ) {
        if (label == null || width <= 0f || height <= 0f) {
            return;
        }

        // Alguns botões legados aplicavam escalas muito altas diretamente na
        // Label. O teto mantém a hierarquia tipográfica e evita que uma ação
        // pareça maior do que o próprio container antes mesmo do ajuste fino.
        float cappedScaleX = Math.min(MAX_FONT_SCALE, label.getFontScaleX());
        float cappedScaleY = Math.min(MAX_FONT_SCALE, label.getFontScaleY());
        if (cappedScaleX != label.getFontScaleX() || cappedScaleY != label.getFontScaleY()) {
            label.setFontScale(cappedScaleX, cappedScaleY);
        }

        float availableWidth = Math.max(1f, width - horizontalPadding);
        float availableHeight = Math.max(1f, height - verticalPadding);
        float preferredWidth = label.getPrefWidth();
        float preferredHeight = label.getPrefHeight();

        if (preferredWidth <= availableWidth && preferredHeight <= availableHeight) {
            return;
        }

        float horizontalRatio = availableWidth / Math.max(1f, preferredWidth);
        float verticalRatio = availableHeight / Math.max(1f, preferredHeight);
        float ratio = Math.min(horizontalRatio, verticalRatio);

        if (ratio >= 1f) {
            return;
        }

        float newScaleX = Math.max(MIN_FONT_SCALE, label.getFontScaleX() * ratio);
        float newScaleY = Math.max(MIN_FONT_SCALE, label.getFontScaleY() * ratio);
        label.setFontScale(newScaleX, newScaleY);
    }
}
