package io.github.some_example_name.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/** Mantém a rolagem de listas quando uma tela recria seus componentes. */
public final class ScrollPositionMemory {
    private static final Map<String, Position> POSITIONS = new HashMap<>();

    private ScrollPositionMemory() { }

    public static void capture(Stage stage, String scope) {
        if (stage == null || scope == null || stage.getRoot().getChildren().size == 0) return;
        visit(stage.getRoot(), new Visitor() {
            @Override public void accept(ScrollPane pane, String key) {
                POSITIONS.put(scope + ":" + key, new Position(pane.getScrollX(), pane.getScrollY()));
            }
        });
    }

    public static void restore(Stage stage, String scope) {
        if (stage == null || scope == null) return;
        visit(stage.getRoot(), new Visitor() {
            @Override public void accept(final ScrollPane pane, String key) {
                final Position saved = POSITIONS.get(scope + ":" + key);
                if (saved == null) return;

                /* A tabela recebe seu tamanho definitivo no primeiro draw. A ação
                   aguarda esse layout para não limitar a posição restaurada a zero. */
                pane.addAction(new Action() {
                    private int attempts;

                    @Override public boolean act(float delta) {
                        pane.validate();
                        boolean verticalReady = saved.y <= 0f || pane.getMaxY() > 0f;
                        boolean horizontalReady = saved.x <= 0f || pane.getMaxX() > 0f;
                        if ((!verticalReady || !horizontalReady) && attempts++ < 4) return false;
                        pane.setScrollX(saved.x);
                        pane.setScrollY(saved.y);
                        pane.updateVisualScroll();
                        return true;
                    }
                });
            }
        });
    }

    private static void visit(Group root, Visitor visitor) {
        int[] unnamedIndex = {0};
        visitChildren(root, visitor, unnamedIndex);
    }

    private static void visitChildren(Group group, Visitor visitor, int[] unnamedIndex) {
        Array<Actor> children = group.getChildren();
        for (Actor actor : children) {
            if (actor instanceof ScrollPane) {
                ScrollPane pane = (ScrollPane) actor;
                String name = pane.getName();
                String key = name != null && !name.trim().isEmpty()
                    ? "name=" + name
                    : "index=" + unnamedIndex[0];
                unnamedIndex[0]++;
                visitor.accept(pane, key);
            }
            if (actor instanceof Group) visitChildren((Group) actor, visitor, unnamedIndex);
        }
    }

    private interface Visitor {
        void accept(ScrollPane pane, String key);
    }

    private static final class Position {
        final float x;
        final float y;

        Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
