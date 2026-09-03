package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public final class ScreenUI {

    /** Escala base legível, com ajuste automático caso o botão seja estreito. */
    private static final float DEFAULT_BUTTON_FONT_SCALE = 0.50f;

    private static final Color HOVER_TINT =
        Color.valueOf("FFF4C9");

    private ScreenUI() {
    }

    /**
     * Converte ícones monocromáticos pretos em uma máscara branca. Assim a
     * tonalização do Scene2D distingue estados dourados, parciais e vazios.
     */
    public static Texture loadTintableIcon(String internalPath) {
        Pixmap source = new Pixmap(Gdx.files.internal(internalPath));
        Pixmap mask = new Pixmap(source.getWidth(), source.getHeight(), Pixmap.Format.RGBA8888);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int alpha = source.getPixel(x, y) & 0xFF;
                mask.drawPixel(x, y, 0xFFFFFF00 | alpha);
            }
        }
        Texture texture = new Texture(mask);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        source.dispose();
        mask.dispose();
        return texture;
    }

    /** Componente único de avaliação, com suporte a passos de meia estrela. */
    public static Table createStarRating(Texture texture, float rating, float size) {
        Table stars = new Table();
        float safeRating = Math.max(0f, Math.min(5f, Math.round(rating * 2f) / 2f));
        if (texture == null) return stars;
        for (int index = 0; index < 5; index++) {
            Image star = new Image(new TextureRegionDrawable(texture));
            star.setScaling(Scaling.fit);
            if (index + 1 <= safeRating) {
                star.setColor(StyleFactory.GOLD);
            } else if (index < safeRating) {
                // A estrela intermediária recebe bronze para ser inequivocamente parcial.
                star.setColor(Color.valueOf("A9822E"));
            } else {
                star.setColor(Color.valueOf("4D5750"));
            }
            stars.add(star).size(size).padRight(Math.max(1f, size * .12f));
        }
        return stars;
    }

    /** Fallback textual compartilhado para tabelas compactas e relatórios. */
    public static String formatStars(float rating) {
        float safeRating = Math.max(0f, Math.min(5f, Math.round(rating * 2f) / 2f));
        StringBuilder output = new StringBuilder();
        int full = (int) safeRating;
        for (int index = 0; index < full; index++) output.append('★');
        if (safeRating - full >= .5f) output.append('½');
        for (int index = (int) Math.ceil(safeRating); index < 5; index++) output.append('☆');
        return output.toString();
    }

    /** Prioridades usam somente as estrelas ativas, sem completar uma escala de cinco. */
    public static String formatActiveStars(int count) {
        StringBuilder output = new StringBuilder();
        for (int index = 0; index < Math.max(0, count); index++) output.append('★');
        return output.toString();
    }

    // =========================================================
    // LAYOUT
    // =========================================================

    /*
     * Largura total do NavigationDrawer e parte que permanece
     * visível quando ele está recolhido.
     */
    public static final float NAV_WIDTH =
        258f;

    public static final float NAV_VISIBLE_CLOSED =
        80f;


    public static final float PAGE_LEFT_OPEN =
        NAV_WIDTH;

    public static final float PAGE_LEFT_CLOSED =
        NAV_VISIBLE_CLOSED;

    public static final float PAGE_RIGHT =
        46f;

    public static final float PAGE_TOP =
        36f;

    public static final float PAGE_BOTTOM =
        32f;

    public static final float HEADER_HEIGHT =
        72f;

    // =========================================================
    // PALETA
    // =========================================================

    public static final Color PANEL =
        Color.valueOf(
            "111A15"
        );

    public static final Color PANEL_ALT =
        Color.valueOf(
            "17211B"
        );

    public static final Color PANEL_SOFT =
        Color.valueOf(
            "1B2720"
        );

    public static final Color TABLE_HEADER =
        Color.valueOf(
            "0D1712"
        );

    public static final Color ROW_EVEN =
        Color.valueOf(
            "131C17"
        );

    public static final Color ROW_ODD =
        Color.valueOf(
            "18231C"
        );

    public static final Color MUTED_TEXT =
        StyleFactory.TEXT_MUTED;

    public static final Color SUCCESS =
        StyleFactory.SUCCESS;

    public static final Color WARNING =
        StyleFactory.WARNING;

    public static final Color DANGER =
        StyleFactory.DANGER;

    // =========================================================
    // GLOBAL THEME
    // =========================================================

    /**
     * Pode ser chamado manualmente no Main depois de carregar
     * o Skin. Os helpers abaixo também chamam automaticamente.
     */
    public static void applyGlobalTheme(
        Skin skin
    ) {

        StyleFactory.applyGlobalTextContrast(
            skin
        );
    }

    private static void prepare(
        Skin skin
    ) {

        applyGlobalTheme(
            skin
        );
    }

    // =========================================================
    // PAGE
    // =========================================================

    public static Table createPage(
        boolean drawerOpen
    ) {

        Table page =
            new Table();

        page.top();
        page.left();

        page.pad(
            PAGE_TOP,
            drawerOpen
                ? PAGE_LEFT_OPEN
                : PAGE_LEFT_CLOSED,
            PAGE_BOTTOM,
            PAGE_RIGHT
        );

        animateTabContent(page);

        return page;
    }

    /** Fade curto usado por páginas e conteúdos reconstruídos ao trocar abas. */
    public static void animateTabContent(Actor actor) {
        if (actor == null) {
            return;
        }

        actor.clearActions();
        actor.getColor().a = 0f;
        actor.addAction(
            Actions.fadeIn(
                0.20f,
                Interpolation.fade
            )
        );
    }

    // =========================================================
    // PANELS
    // =========================================================

    public static Table createPanel() {

        Table panel =
            new Table();

        panel.background(
            StyleFactory.createRoundedPanel(
                PANEL,
                StyleFactory.BORDER_SOFT
            )
        );

        panel.pad(
            14f
        );

        return panel;
    }

    public static Table createSubtlePanel() {

        Table panel =
            new Table();

        panel.background(
            StyleFactory.createRoundedPanel(
                PANEL_ALT,
                Color.valueOf(
                    "334139"
                )
            )
        );

        panel.pad(
            12f
        );

        return panel;
    }

    public static Table createTablePanel() {

        Table panel =
            new Table();

        panel.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "0C1410"
                ),
                StyleFactory.DARK_GOLD
            )
        );

        panel.pad(
            10f
        );

        return panel;
    }

    // =========================================================
    // HEADER
    // =========================================================

    public static Table createHeader(
        Skin skin,
        String title,
        String rightText
    ) {

        prepare(
            skin
        );

        Table header =
            new Table();

        header.background(
            StyleFactory.createRoundedPanel(
                StyleFactory.MUSGO_DEEP,
                StyleFactory.GOLD
            )
        );

        header.pad(
            12f,
            20f,
            12f,
            20f
        );

        Label titleLabel =
            createLabel(
                skin,
                title,
                "font-title"
            );

        titleLabel.setFontScale(
            0.86f
        );

        titleLabel.setColor(
            StyleFactory.YELLOW_TITLE
        );

        titleLabel.setAlignment(
            Align.left
        );

        header
            .add(titleLabel)
            .left()
            .expandX();

        if (
            rightText != null &&
            !rightText.trim().isEmpty()
        ) {

            Label right =
                createLabel(
                    skin,
                    rightText,
                    "font-bold"
                );

            right.setFontScale(
                0.62f
            );

            right.setColor(
                StyleFactory.SOFT_YELLOW
            );

            right.setAlignment(
                Align.right
            );

            header
                .add(right)
                .right();
        }

        return header;
    }

    // =========================================================
    // LABELS
    // =========================================================

    public static Label createSectionTitle(
        Skin skin,
        String text
    ) {

        prepare(
            skin
        );

        Label label =
            createLabel(
                skin,
                text,
                "font-bold"
            );

        label.setFontScale(
            0.70f
        );

        label.setColor(
            StyleFactory.YELLOW_TITLE
        );

        label.setAlignment(
            Align.left
        );

        return label;
    }

    public static Label createSubtitle(
        Skin skin,
        String text
    ) {

        prepare(
            skin
        );

        Label label =
            createLabel(
                skin,
                text,
                "default"
            );

        label.setFontScale(
            0.58f
        );

        label.setColor(
            MUTED_TEXT
        );

        return label;
    }

    public static Label createBoldValue(
        Skin skin,
        String text,
        Color color,
        int alignment
    ) {

        prepare(
            skin
        );

        Label label =
            createLabel(
                skin,
                text,
                "font-bold"
            );

        label.setFontScale(
            0.64f
        );

        label.setColor(
            color != null
                ? color
                : StyleFactory.TEXT_PRIMARY
        );

        label.setAlignment(
            alignment
        );

        return label;
    }

    public static Label createValueLabel(
        Skin skin,
        String text,
        Color color,
        int alignment
    ) {

        prepare(
            skin
        );

        Label label =
            createLabel(
                skin,
                text,
                "default"
            );

        label.setFontScale(
            0.62f
        );

        label.setColor(
            color != null
                ? color
                : StyleFactory.TEXT_PRIMARY
        );

        label.setAlignment(
            alignment
        );

        return label;
    }

    public static Label createTableHeaderLabel(
        Skin skin,
        String text,
        int alignment
    ) {

        prepare(
            skin
        );

        Label label =
            createLabel(
                skin,
                text,
                "font-bold"
            );

        label.setFontScale(
            0.56f
        );

        label.setColor(
            StyleFactory.SOFT_YELLOW
        );

        label.setAlignment(
            alignment
        );

        return label;
    }

    private static Label createLabel(
        Skin skin,
        String text,
        String preferredStyle
    ) {

        if (
            skin != null &&
            preferredStyle != null &&
            skin.has(
                preferredStyle,
                Label.LabelStyle.class
            )
        ) {

            return new Label(
                text != null
                    ? text
                    : "",
                skin,
                preferredStyle
            );
        }

        return new Label(
            text != null
                ? text
                : "",
            skin
        );
    }

    // =========================================================
    // TABLE ROWS
    // =========================================================

    public static Table createTableHeaderRow() {

        Table row =
            new Table();

        row.background(
            StyleFactory.createRoundedPanel(
                TABLE_HEADER,
                Color.valueOf(
                    "39453D"
                )
            )
        );

        row.pad(
            5f,
            8f,
            5f,
            8f
        );

        return row;
    }

    public static Table createRow(
        int index
    ) {

        Table row =
            new Table();

        Color background =
            index % 2 == 0
                ? ROW_EVEN
                : ROW_ODD;

        row.background(
            StyleFactory.createRoundedPanel(
                background,
                Color.valueOf(
                    "28352D"
                )
            )
        );

        row.pad(
            4f,
            7f,
            4f,
            7f
        );

        /*
         * A linha inteira é uma área de toque. Isso mantém a seleção de
         * jogadores acessível também ao clicar no escudo, atributos ou
         * espaços vazios — e não apenas no rótulo do nome.
         */
        row.setTouchable(Touchable.enabled);

        addHoverAnimation(row, 1.015f);

        return row;
    }

    /** Cria uma caixa de seleção com feedback visual de foco e clique. */
    public static <T> SelectBox<T> createSelectBox(
        Skin skin
    ) {

        prepare(skin);

        SelectBox.SelectBoxStyle style = createVisibleSelectBoxStyle(skin);
        SelectBox<T> selectBox = new VisibleSelectBox<>(style, style.font);

        selectBox.setSize(240f, 52f);
        selectBox.setMaxListCount(7);
        selectBox.setAlignment(Align.left);
        selectBox.setTouchable(Touchable.enabled);
        /* O LibGDX usa Touchable.disabled na lista como estado interno de
         * "popup fechado". Forçar enabled aqui fazia show() retornar antes
         * de adicionar a lista ao Stage. */
        selectBox.getList().setTouchable(Touchable.disabled);
        selectBox.getList().setAlignment(Align.left);
        selectBox.setScrollingDisabled(true);

        selectBox.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    actor.clearActions();
                    actor.setScale(1f);
                }
            }
        );

        return selectBox;
    }

    /** Cria um estilo próprio para cada filtro, sempre legível sobre o painel. */
    private static SelectBox.SelectBoxStyle createVisibleSelectBoxStyle(
        Skin skin
    ) {
        SelectBox.SelectBoxStyle base = skin.get(
            "default",
            SelectBox.SelectBoxStyle.class
        );

        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(base);
        BitmapFont font = base.font != null
            ? base.font
            : skin.get("default", BitmapFont.class);

        style.font = font;
        style.fontColor = new Color(StyleFactory.CREME_AGED);
        style.disabledFontColor = new Color(StyleFactory.TEXT_DISABLED);
        style.background = StyleFactory.createModernButton(
            240,
            52,
            Color.valueOf("17251D"),
            StyleFactory.GOLD
        );
        style.backgroundOver = StyleFactory.createModernButton(
            240,
            52,
            Color.valueOf("294230"),
            StyleFactory.PLAYOFF_GOLD
        );
        style.backgroundOpen = StyleFactory.createModernButton(
            240,
            52,
            Color.valueOf("382E12"),
            StyleFactory.SOFT_YELLOW
        );
        style.backgroundDisabled = StyleFactory.createModernButton(
            240,
            52,
            Color.valueOf("18211C"),
            Color.valueOf("3E4A43")
        );
        style.scrollStyle = new ScrollPane.ScrollPaneStyle();
        style.scrollStyle.background = StyleFactory.createRoundedPanel(
            Color.valueOf("09130E"),
            StyleFactory.GOLD
        );

        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle =
            new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
        listStyle.font = font;
        listStyle.fontColorUnselected = new Color(StyleFactory.CREME_AGED);
        listStyle.fontColorSelected = new Color(StyleFactory.SOFT_YELLOW);
        listStyle.selection = StyleFactory.createRoundedPanel(
            Color.valueOf("403414"),
            StyleFactory.GOLD
        );
        listStyle.background = StyleFactory.createRoundedPanel(
            Color.valueOf("0C1711"),
            StyleFactory.GOLD
        );
        style.listStyle = listStyle;

        return style;
    }

    // =========================================================
    // BADGES
    // =========================================================

    public static Table createBadge(
        Skin skin,
        String text,
        Color background
    ) {

        prepare(
            skin
        );

        Color safeBackground =
            background != null
                ? background
                : Color.valueOf(
                    "445047"
                );

        Table badge =
            new Table();

        badge.background(
            StyleFactory.createBadge(
                safeBackground
            )
        );

        badge.pad(
            3f,
            8f,
            3f,
            8f
        );

        Label label =
            createLabel(
                skin,
                text,
                "font-bold"
            );

        label.setFontScale(
            0.56f
        );

        /*
         * Esta é uma das partes mais importantes da correção:
         * o texto do badge escolhe automaticamente entre
         * tinta escura e clara conforme o fundo.
         */
        label.setColor(
            StyleFactory.getReadableTextColor(
                safeBackground
            )
        );

        label.setAlignment(
            Align.center
        );

        badge
            .add(label)
            .center();

        return badge;
    }

    // =========================================================
    // STATUS BOX
    // =========================================================

    public static Table createStatusBox(
        Skin skin,
        String title,
        String value,
        Color valueColor
    ) {

        prepare(
            skin
        );

        Table box =
            createSubtlePanel();

        box.pad(
            6f,
            11f,
            6f,
            11f
        );

        Label titleLabel =
            createLabel(
                skin,
                title,
                "font-bold"
            );

        titleLabel.setFontScale(
            0.50f
        );

        titleLabel.setColor(
            MUTED_TEXT
        );

        titleLabel.setAlignment(
            Align.center
        );

        box
            .add(titleLabel)
            .center()
            .row();

        Label valueLabel =
            createLabel(
                skin,
                value,
                "font-bold"
            );

        valueLabel.setFontScale(
            0.67f
        );

        valueLabel.setColor(
            valueColor != null
                ? valueColor
                : StyleFactory.TEXT_PRIMARY
        );

        valueLabel.setAlignment(
            Align.center
        );

        box
            .add(valueLabel)
            .center()
            .padTop(2f);

        return box;
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    public static TextButton createPrimaryButton(
        Skin skin,
        String text
    ) {

        prepare(
            skin
        );

        TextButton.TextButtonStyle base =
            getButtonStyle(
                skin
            );

        TextButton.TextButtonStyle style =
            new TextButton.TextButtonStyle(
                base
            );

        style.up =
            StyleFactory.createModernButton(
                178,
                48,
                Color.valueOf(
                    "6E5816"
                ),
                StyleFactory.GOLD
            );

        style.over =
            StyleFactory.createModernButton(
                178,
                48,
                Color.valueOf(
                    "846A1B"
                ),
                StyleFactory.PLAYOFF_GOLD
            );

        style.down =
            StyleFactory.createModernButton(
                178,
                48,
                Color.valueOf(
                    "4F4012"
                ),
                StyleFactory.DARK_GOLD
            );

        style.checked =
            style.down;

        style.fontColor =
            new Color(
                StyleFactory.CREME_AGED
            );

        style.overFontColor =
            new Color(
                Color.WHITE
            );

        style.downFontColor =
            new Color(
                StyleFactory.SOFT_YELLOW
            );

        style.checkedFontColor =
            new Color(
                StyleFactory.SOFT_YELLOW
            );

        style.disabledFontColor =
            new Color(
                StyleFactory.TEXT_DISABLED
            );

        TextButton button = new AdaptiveTextButton(
            text,
            style
        );

        button.getLabel().setFontScale(DEFAULT_BUTTON_FONT_SCALE);

        addHoverAnimation(button, 1.035f);

        return button;
    }

    public static TextButton createSecondaryButton(
        Skin skin,
        String text
    ) {

        prepare(
            skin
        );

        TextButton.TextButtonStyle base =
            getButtonStyle(
                skin
            );

        TextButton.TextButtonStyle style =
            new TextButton.TextButtonStyle(
                base
            );

        style.up =
            StyleFactory.createModernButton(
                168,
                46,
                Color.valueOf(
                    "1C2922"
                ),
                Color.valueOf(
                    "526159"
                )
            );

        style.over =
            StyleFactory.createModernButton(
                168,
                46,
                StyleFactory.PANEL_HOVER,
                StyleFactory.DARK_GOLD
            );

        style.down =
            StyleFactory.createModernButton(
                168,
                46,
                Color.valueOf(
                    "121B16"
                ),
                StyleFactory.DARK_GOLD
            );

        style.checked =
            style.down;

        style.fontColor =
            new Color(
                StyleFactory.TEXT_PRIMARY
            );

        style.overFontColor =
            new Color(
                StyleFactory.SOFT_YELLOW
            );

        style.downFontColor =
            new Color(
                StyleFactory.CREME_AGED
            );

        style.checkedFontColor =
            new Color(
                StyleFactory.SOFT_YELLOW
            );

        style.disabledFontColor =
            new Color(
                StyleFactory.TEXT_DISABLED
            );

        TextButton button = new AdaptiveTextButton(
            text,
            style
        );

        button.getLabel().setFontScale(DEFAULT_BUTTON_FONT_SCALE);

        addHoverAnimation(button, 1.03f);

        return button;
    }

    /** Aplica o mesmo hover animado aos botões usados por telas legadas. */
    public static TextButton createInteractiveButton(
        String text,
        Skin skin
    ) {

        prepare(skin);
        TextButton button = new AdaptiveTextButton(text, skin);
        button.getLabel().setFontScale(DEFAULT_BUTTON_FONT_SCALE);
        addHoverAnimation(button, 1.03f);
        return button;
    }

    /** Variante para botões que utilizam um estilo específico do Skin. */
    public static TextButton createInteractiveButton(
        String text,
        Skin skin,
        String styleName
    ) {

        prepare(skin);
        TextButton button = new AdaptiveTextButton(text, skin, styleName);
        button.getLabel().setFontScale(DEFAULT_BUTTON_FONT_SCALE);
        addHoverAnimation(button, 1.03f);
        return button;
    }

    private static void addHoverAnimation(
        final Actor actor,
        final float hoverScale
    ) {

        if (actor instanceof Group) {
            ((Group) actor).setTransform(true);
        }

        actor.setOrigin(Align.center);

        actor.addListener(
            new InputListener() {

                @Override
                public void enter(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor fromActor
                ) {

                    if (!actor.isTouchable()) {
                        return;
                    }

                    actor.clearActions();
                    actor.addAction(
                        Actions.parallel(
                            Actions.scaleTo(
                                hoverScale,
                                hoverScale,
                                0.14f,
                                Interpolation.sineOut
                            ),
                            Actions.color(
                                HOVER_TINT,
                                0.14f,
                                Interpolation.sineOut
                            )
                        )
                    );
                }

                @Override
                public void exit(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor toActor
                ) {

                    actor.clearActions();
                    actor.addAction(
                        Actions.parallel(
                            Actions.scaleTo(
                                1f,
                                1f,
                                0.18f,
                                Interpolation.sine
                            ),
                            Actions.color(
                                Color.WHITE,
                                0.18f,
                                Interpolation.sine
                            )
                        )
                    );
                }

                @Override
                public boolean touchDown(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    int button
                ) {

                    actor.clearActions();
                    actor.addAction(
                        Actions.sequence(
                            Actions.scaleTo(
                                0.98f,
                                0.98f,
                                0.05f,
                                Interpolation.sineOut
                            ),
                            Actions.scaleTo(
                                hoverScale,
                                hoverScale,
                                0.12f,
                                Interpolation.sineOut
                            )
                        )
                    );

                    return false;
                }
            }
        );
    }

    private static TextButton.TextButtonStyle getButtonStyle(
        Skin skin
    ) {

        if (
            skin != null &&
            skin.has(
                "default",
                TextButton.TextButtonStyle.class
            )
        ) {

            return skin.get(
                "default",
                TextButton.TextButtonStyle.class
            );
        }

        return skin.get(
            TextButton.TextButtonStyle.class
        );
    }

    // =========================================================
    // DIVIDER
    // =========================================================

    public static Image createDivider() {

        Image divider =
            new Image(
                StyleFactory.createSolid(
                    Color.valueOf(
                        "465248"
                    )
                )
            );

        return divider;
    }

    // =========================================================
    // PROGRESS
    // =========================================================

    public static Table createBlockProgress(
        Skin skin,
        double percentage,
        int totalBlocks,
        Color fillColor
    ) {

        prepare(
            skin
        );

        Table progress =
            new Table();

        int safeBlocks =
            Math.max(
                1,
                totalBlocks
            );

        double safePercentage =
            Math.max(
                0.0,
                Math.min(
                    100.0,
                    percentage
                )
            );

        int filled =
            (int) Math.round(
                safePercentage /
                    100.0 *
                    safeBlocks
            );

        Color active =
            fillColor != null
                ? fillColor
                : StyleFactory.GOLD;

        for (
            int i = 0;
            i < safeBlocks;
            i++
        ) {

            Color color =
                i < filled
                    ? active
                    : Color.valueOf(
                        "344038"
                    );

            Image block =
                new Image(
                    StyleFactory.createSolid(
                        color
                    )
                );

            progress
                .add(block)
                .width(8f)
                .height(10f)
                .padRight(
                    2f
                );
        }

        return progress;
    }

    // =========================================================
    // TEXT UTILS
    // =========================================================

    public static String shorten(
        String text,
        int maxLength
    ) {

        if (
            text == null
        ) {

            return "";
        }

        if (
            maxLength <= 3 ||
            text.length() <=
                maxLength
        ) {

            return text;
        }

        return text.substring(
            0,
            maxLength - 3
        ) + "...";
    }
}
