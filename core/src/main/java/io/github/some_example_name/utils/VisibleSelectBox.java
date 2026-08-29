package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * SelectBox com indicador visual permanente. O componente padrão não exibe
 * uma seta, o que fazia filtros parecerem rótulos estáticos em painéis escuros.
 */
public class VisibleSelectBox<T> extends SelectBox<T> {
    private final BitmapFont indicatorFont;

    public VisibleSelectBox(SelectBoxStyle style, BitmapFont indicatorFont) {
        super(style);
        this.indicatorFont = indicatorFont;

        /* O listener padrão deixou de abrir a lista dentro de algumas
         * hierarquias com Stack/ScrollPane. Um toggle explícito evita que o
         * evento seja apenas consumido pelo campo fechado. */
        removeListener(getClickListener());
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isDisabled() || getItems().size == 0) return;
                if (getScrollPane().hasParent()) hideScrollPane();
                else showScrollPane();
                event.stop();
            }
        });
    }

    /**
     * O popup do SelectBox é um ator separado adicionado diretamente ao Stage.
     * Algumas telas usam Stacks, ScrollPanes e painéis de tela cheia; por isso
     * reforçamos explicitamente sua visibilidade, foco e ordem de desenho.
     */
    @Override
    public void showScrollPane() {
        if (getItems().size == 0 || getStage() == null) return;

        /* Escalas animadas no campo fechado deslocavam o cálculo de posição
         * localToStageCoordinates usado internamente pelo LibGDX. */
        clearActions();
        setScale(1f);
        super.showScrollPane();

        ScrollPane popup = getScrollPane();
        popup.clearActions();
        popup.setColor(Color.WHITE);
        popup.setVisible(true);
        popup.setTouchable(Touchable.enabled);
        popup.toFront();
        popup.invalidateHierarchy();

        getList().setVisible(true);
        getList().setTouchable(Touchable.enabled);
        getList().invalidateHierarchy();
        getStage().setScrollFocus(popup);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (indicatorFont == null) {
            return;
        }

        Color previousColor = new Color(indicatorFont.getColor());
        indicatorFont.setColor(
            isDisabled() ? StyleFactory.TEXT_DISABLED : StyleFactory.SOFT_YELLOW
        );
        indicatorFont.draw(
            batch,
            "v",
            getX() + getWidth() - 20f,
            getY() + getHeight() * 0.63f
        );
        indicatorFont.setColor(previousColor);
    }
}
