package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu lateral global da carreira.
 *
 * Identidade visual:
 * verde escuro + dourado + creme.
 */
public final class NavigationDrawer
    extends Group {

    private static final Map<String, Texture> ICONS =
        new HashMap<>();

    private static Texture fallbackIcon;

    private static final float WIDTH =
        ScreenUI.NAV_WIDTH;

    /*
     * Quando fechado, cerca de 62px
     * permanecem visíveis.
     */
    private static final float CLOSED_X =
        -(WIDTH - ScreenUI.NAV_VISIBLE_CLOSED);

    private static final float ITEM_WIDTH =
        104f;

    private static final float ICON_SIZE =
        62f;

    private boolean open;

    private final Table panel;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public NavigationDrawer(
        Main game,
        Club club,
        String active,
        boolean initiallyOpen
    ) {

        setSize(
            WIDTH,
            ResponsiveViewport.DESIGN_HEIGHT
        );

        open =
            initiallyOpen;

        setX(
            open
                ? 0f
                : CLOSED_X
        );

        setY(
            0f
        );

        setTouchable(
            Touchable.childrenOnly
        );

        // =====================================================
        // PAINEL PRINCIPAL
        // =====================================================

        panel =
            new Table();

        panel.setSize(
            WIDTH,
            getHeight()
        );

        panel.background(
            StyleFactory.createRoundedPanel(
                new Color(
                    0.018f,
                    0.060f,
                    0.043f,
                    0.985f
                ),
                StyleFactory.GOLD
            )
        );

        panel.top();

        panel.padTop(
            74f
        );

        panel.padLeft(
            10f
        );

        panel.padRight(
            10f
        );

        panel.setTouchable(
            open
                ? Touchable.enabled
                : Touchable.childrenOnly
        );

        addActor(
            panel
        );

        // =====================================================
        // NAVEGAÇÃO
        // =====================================================

        addItem(
            game,
            "PERFIL",
            "Icons8/icons8-menu-de-usuário-masculino-50.png",
            active,
            () ->
                game.setScreen(
                    new ClubProfileScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "ELENCO",
            "Icons8/icons8-camisa-de-jogador-50.png",
            active,
            () ->
                game.setScreen(
                    new ClubManagementScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "TÁTICAS",
            "Icons8/icons8-estrutura-em-árvore-50.png",
            active,
            () ->
                game.setScreen(
                    new TacticsScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "DESENV.",
            "Icons8/icons8-em-alta-50.png",
            active,
            () ->
                game.setScreen(
                    new SquadDevelopmentScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "SCOUTING",
            "Icons8/icons8-binóculos-50.png",
            active,
            () ->
                game.setScreen(
                    new DraftScoutingScreen(
                        game,
                        club,
                        game.draftScoutManager
                    )
                )
        );

        addItem(
            game,
            "TROCAS",
            "Icons8/icons8-partilhar-2-50.png",
            active,
            () -> {
                game.setScreen(
                    new TradeHubScreen(
                        game,
                        club
                    )
                );
            }
        );

        addItem(
            game,
            "CONTRATOS",
            "Icons8/icons8-caixa-de-selecção-seleccionada-2-50.png",
            active,
            () ->
                game.setScreen(
                    new ContractRenewalScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "AGENTES",
            "Icons8/icons8-contatos-50.png",
            active,
            () ->
                game.setScreen(
                    new FreeAgencyScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "TABELA",
            "Icons8/icons8-lista-50.png",
            active,
            () ->
                game.setScreen(
                    new StandingsScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "CALENDÁRIO",
            "Icons8/icons8-lembrete-de-compromissos-50.png",
            active,
            () ->
                game.setScreen(
                    new CalendarScreen(
                        game,
                        club
                    )
                )
        );

        addItem(
            game,
            "FINANÇAS",
            "Icons8/icons8-caixa-50.png",
            active,
            () ->
                game.setScreen(
                    new FinancesScreen(
                        game,
                        club
                    )
                )
        );

        // =====================================================
        // BOTÃO DE ABRIR / FECHAR
        // =====================================================

        ImageButton menuButton =
            iconButton(
                "Icons8/icons8-menu-2-50.png",
                false
            );

        menuButton.setSize(
            60f,
            60f
        );

        /*
         * O botão fica na parte que continua
         * visível mesmo com o menu fechado.
         */
        menuButton.setPosition(
            WIDTH - 64f,
            getHeight() - 72f
        );

        menuButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    toggleDrawer();
                }
            }
        );

        addActor(
            menuButton
        );
    }

    // =========================================================
    // ITEM
    // =========================================================

    private void addItem(
        Main game,
        String label,
        String iconPath,
        String active,
        Runnable action
    ) {

        boolean isActive =
            label.equalsIgnoreCase(
                active
            );

        Table item =
            new Table();

        /*
         * O ativo ganha um bloco dourado/musgo
         * semelhante ao mockup aprovado.
         */
        if (isActive) {

            item.background(
                StyleFactory.createRoundedPanel(
                    new Color(
                        0.30f,
                        0.22f,
                        0.035f,
                        0.98f
                    ),
                    StyleFactory.GOLD
                )
            );
        }

        ImageButton button =
            iconButton(
                iconPath,
                isActive
            );

        button.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    action.run();
                }
            }
        );

        item
            .add(button)
            .size(
                ICON_SIZE
            )
            .padTop(3f)
            .row();

        Label name =
            new Label(
                label,
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.52f
        );

        name.setColor(
            isActive
                ? StyleFactory.SOFT_YELLOW
                : StyleFactory.CREME_AGED
        );

        item
            .add(name)
            .center()
            .padTop(3f)
            .padBottom(4f);

        panel
            .add(item)
            .width(
                ITEM_WIDTH
            )
            .height(
                84f
            )
            .center()
            .padBottom(4f)
            .row();
    }

    // =========================================================
    // TOGGLE
    // =========================================================

    private void toggleDrawer() {

        open =
            !open;

        float targetX =
            open
                ? 0f
                : CLOSED_X;

        panel.setTouchable(
            open
                ? Touchable.enabled
                : Touchable.childrenOnly
        );

        clearActions();

        addAction(
            Actions.moveTo(
                targetX,
                getY(),
                0.22f,
                Interpolation.pow2Out
            )
        );
    }

    // =========================================================
    // RESPONSIVIDADE
    // =========================================================

    @Override
    public void act(
        float delta
    ) {

        float newHeight = getStage() != null
            ? getStage().getHeight()
            : getHeight();

        if (
            getHeight() !=
                newHeight
        ) {

            setHeight(
                newHeight
            );

            panel.setHeight(
                newHeight
            );
        }

        super.act(
            delta
        );
    }

    // =========================================================
    // ATTACH
    // =========================================================

    public static NavigationDrawer attach(
        Stage stage,
        Main game,
        Club club,
        String active
    ) {

        return attach(
            stage,
            game,
            club,
            active,
            false
        );
    }

    public static NavigationDrawer attach(
        Stage stage,
        Main game,
        Club club,
        String active,
        boolean initiallyOpen
    ) {

        NavigationDrawer drawer =
            new NavigationDrawer(
                game,
                club,
                active,
                initiallyOpen
            );

        stage.addActor(
            drawer
        );

        return drawer;
    }

    // =========================================================
    // ÍCONES
    // =========================================================

    private ImageButton iconButton(
        String path,
        boolean active
    ) {

        Texture texture =
            loadIcon(
                path
            );

        ImageButton.ImageButtonStyle style =
            new ImageButton.ImageButtonStyle();

        TextureRegionDrawable icon = new TextureRegionDrawable(texture);

        /*
         * Os PNGs originais são pretos. loadIcon os converte para uma
         * máscara branca, permitindo aplicar cores claras sem perder os
         * detalhes sobre o fundo verde do menu.
         */
        style.imageUp = icon.tint(
            active ? Color.WHITE : StyleFactory.SOFT_YELLOW
        );
        style.imageOver = icon.tint(Color.WHITE);
        style.imageDown = icon.tint(StyleFactory.YELLOW_TITLE);

        Color base =
            active
                ? Color.valueOf("5A4510")
                : Color.valueOf("18251F");

        style.up =
            StyleFactory.createModernButton(
                64,
                64,
                base,
                StyleFactory.GOLD
            );

        style.over =
            StyleFactory.createModernButton(
                64,
                64,
                StyleFactory.MUSGO_LIGHT,
                StyleFactory.YELLOW_TITLE
            );

        style.down =
            StyleFactory.createModernButton(
                64,
                64,
                StyleFactory.DARK_GOLD,
                Color.WHITE
            );

        return new ImageButton(
            style
        );
    }

    private Texture loadIcon(
        String path
    ) {

        Texture texture =
            ICONS.get(
                path
            );

        if (
            texture != null
        ) {
            return texture;
        }

        try {

            if (
                path != null &&
                    Gdx.files
                        .internal(path)
                        .exists()
            ) {

                texture = ScreenUI.loadTintableIcon(path);

            } else {

                texture =
                    getFallbackIcon();
            }

        } catch (
            Exception ignored
        ) {

            texture =
                getFallbackIcon();
        }

        ICONS.put(
            path,
            texture
        );

        return texture;
    }

    // =========================================================
    // FALLBACK SEM LIBGDX.PNG
    // =========================================================

    /**
     * Quando um asset não existir, não mostramos
     * mais o logo vermelho do LibGDX.
     *
     * Em vez disso aparece um pequeno símbolo
     * abstrato dourado.
     */
    private static Texture getFallbackIcon() {

        if (
            fallbackIcon != null
        ) {
            return fallbackIcon;
        }

        int size =
            48;

        Pixmap pixmap =
            new Pixmap(
                size,
                size,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            new Color(
                0f,
                0f,
                0f,
                0f
            )
        );

        pixmap.fill();

        pixmap.setColor(Color.WHITE);

        pixmap.fillCircle(
            size / 2,
            11,
            4
        );

        pixmap.fillCircle(
            size / 2,
            24,
            4
        );

        pixmap.fillCircle(
            size / 2,
            37,
            4
        );

        fallbackIcon =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        return fallbackIcon;
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    public static void disposeIcons() {

        /*
         * fallbackIcon também pode estar no map.
         * Portanto evitamos dar dispose nele duas vezes.
         */

        for (
            Texture texture :
            ICONS.values()
        ) {

            if (
                texture != null &&
                    texture != fallbackIcon
            ) {

                texture.dispose();
            }
        }

        ICONS.clear();

        if (
            fallbackIcon != null
        ) {

            fallbackIcon.dispose();

            fallbackIcon =
                null;
        }
    }
}
