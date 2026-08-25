package io.github.some_example_name.utils;

import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Viewport compartilhado pelas telas do jogo.
 *
 * O layout foi criado em 1920x1080. O ExtendViewport mantém essa área lógica
 * mínima, reduzindo-a ou ampliando-a para caber na janela sem deformação. Em
 * proporções diferentes (4:3, 16:10 e ultrawide), o espaço excedente é
 * acrescentado ao mundo em vez de cortar a interface ou criar barras pretas.
 */
public final class ResponsiveViewport extends ExtendViewport {
    public static final float DESIGN_WIDTH = 1920f;
    public static final float DESIGN_HEIGHT = 1080f;

    public ResponsiveViewport() {
        super(DESIGN_WIDTH, DESIGN_HEIGHT);
    }
}
