package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.utils.StyleFactory;

import java.util.HashMap;
import java.util.Map;

/** Navegação retrô consistente para todas as telas da carreira. */
public final class NavigationDrawer extends Group {
    private static final Map<String, Texture> ICONS = new HashMap<>();
    private static final float WIDTH = 240f;
    private static final float CLOSED_X = -176f;
    private boolean open;
    private final Table panel;

    public NavigationDrawer(Main game, Club club, String active, boolean initiallyOpen) {
        setSize(WIDTH, Gdx.graphics.getHeight());
        open = initiallyOpen;
        setX(open ? 0 : CLOSED_X);
        setTouchable(Touchable.childrenOnly);

        panel = new Table();
        panel.setSize(WIDTH, getHeight());
        panel.background(StyleFactory.createRoundedPanel(new Color(0.05f, 0.09f, 0.07f, 0.96f), StyleFactory.GOLD));
        panel.top().padTop(94).padLeft(12).padRight(12);

        // Garante que o painel só capture cliques quando estiver aberto
        panel.setTouchable(open ? Touchable.enabled : Touchable.childrenOnly);
        addActor(panel);

        // --- ITENS DO MENU ---
        addItem(panel, game, "PERFIL", "Icons8/icons8-menu-de-usu\u00e1rio-masculino-50.png", active, () ->
            game.setScreen(new ClubProfileScreen(game, club)));

        addItem(panel, game, "ELENCO", "Icons8/icons8-camisa-de-jogador-50.png", active, () ->
            game.setScreen(new ClubManagementScreen(game, club)));

        addItem(panel, game, "T\u00c1TICAS", "Icons8/icons8-estrutura-em-\u00e1rvore-50.png", active, () ->
            game.setScreen(new TacticsScreen(game, club)));

        // Item de Trocas / Mercado (Resolve parceiro padrão dinamicamente)
        addItem(panel, game, "TROCAS", "Icons8/icons8-menu-2-50.png", active, () -> {
            Club partner = game.league.getClubs().stream()
                .filter(c -> !c.equals(club))
                .findFirst()
                .orElse(null);
            game.setScreen(new TradeScreen(game, club, partner));
        });

        addItem(panel, game, "TABELA", "Icons8/icons8-est\u00e1dio-50.png", active, () ->
            game.setScreen(new StandingsScreen(game, club)));

        addItem(panel, game, "CALEND\u00c1RIO", "Icons8/icons8-rel\u00f3gio-50.png", active, () ->
            game.setScreen(new CalendarScreen(game, club)));

        addItem(panel, game, "FINAN\u00c7AS", "Icons8/icons8-menu-2-50.png", active, () ->
            game.setScreen(new FinancesScreen(game, club)));

        // Botão de alternância (Hambúrguer)
        ImageButton menuButton = iconButton("Icons8/icons8-menu-2-50.png", false);
        menuButton.setSize(64, 64);
        menuButton.setPosition(WIDTH - 52, getHeight() - 76);
        menuButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                toggleDrawer();
            }
        });
        addActor(menuButton);
    }

    private void toggleDrawer() {
        open = !open;
        float targetX = open ? 0 : CLOSED_X;

        // Atualiza a permissão de toque no painel para não barrar eventos da tela ao lado quando fechado
        panel.setTouchable(open ? Touchable.enabled : Touchable.childrenOnly);

        // Animação suave de deslizar em 0.2 segundos
        clearActions();
        addAction(Actions.moveTo(targetX, getY(), 0.2f, Interpolation.pow2Out));
    }

    public static NavigationDrawer attach(Stage stage, Main game, Club club, String active) {
        NavigationDrawer drawer = new NavigationDrawer(game, club, active, false);
        stage.addActor(drawer);
        return drawer;
    }

    public static NavigationDrawer attach(Stage stage, Main game, Club club, String active, boolean initiallyOpen) {
        NavigationDrawer drawer = new NavigationDrawer(game, club, active, initiallyOpen);
        stage.addActor(drawer);
        return drawer;
    }

    private void addItem(Table panel, Main game, String label, String path, String active, final Runnable action) {
        Table item = new Table();
        boolean isActive = label.equals(active);
        ImageButton button = iconButton(path, isActive);

        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        item.add(button).size(68).row();

        Label name = new Label(label, game.skin, "font-bold");
        name.setFontScale(0.55f);
        name.setColor(isActive ? StyleFactory.SOFT_YELLOW : StyleFactory.CREME_AGED);
        item.add(name).padTop(4).padBottom(10).row();

        panel.add(item).width(88).center().row();
    }

    private ImageButton iconButton(String path, boolean active) {
        Texture texture = ICONS.get(path);
        if (texture == null) {
            try {
                texture = new Texture(Gdx.files.internal(path));
            } catch (Exception e) {
                texture = new Texture(Gdx.files.internal("libgdx.png"));
            }
            ICONS.put(path, texture);
        }
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(texture);
        Color base = active ? StyleFactory.DARK_GOLD : StyleFactory.METAL_DARK;
        style.up = StyleFactory.createModernButton(68, 68, base, StyleFactory.GOLD);
        style.over = StyleFactory.createModernButton(68, 68, Color.valueOf("38424B"), StyleFactory.YELLOW_TITLE);
        style.down = StyleFactory.createModernButton(68, 68, StyleFactory.DARK_GOLD, Color.WHITE);
        return new ImageButton(style);
    }

    public static void disposeIcons() {
        for (Texture texture : ICONS.values()) texture.dispose();
        ICONS.clear();
    }
}
