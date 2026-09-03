package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class StyleFactory {

    private static final List<Texture> GENERATED_TEXTURES =
        new ArrayList<>();

    /*
     * Guarda cada Skin que já recebeu a correção global.
     * Isso evita reprocessar os estilos toda vez que uma tela
     * chama algum helper do ScreenUI.
     */
    private static final Set<Skin> THEMED_SKINS =
        Collections.newSetFromMap(
            new IdentityHashMap<Skin, Boolean>()
        );

    // =========================================================
    // PALETA PRINCIPAL
    // =========================================================

    public static final Color PRUSSIAN_GREEN =
        Color.valueOf("0F281B");

    public static final Color MUSGO_DEEP =
        Color.valueOf("091A11");

    public static final Color MUSGO_LIGHT =
        Color.valueOf("1E3F20");

    public static final Color GOLD =
        Color.valueOf("D4AF37");

    public static final Color DARK_GOLD =
        Color.valueOf("997A15");

    public static final Color PLAYOFF_GOLD =
        Color.valueOf("FFD700");

    public static final Color SOFT_YELLOW =
        Color.valueOf("F7E5A9");

    public static final Color YELLOW_TITLE =
        Color.valueOf("FFE8A3");

    public static final Color WINE_RED =
        Color.valueOf("7A121C");

    public static final Color METAL_DARK =
        Color.valueOf("1C2127");

    public static final Color CARD_BG =
        Color.valueOf("12161B");

    public static final Color CREME_AGED =
        Color.valueOf("F4F0EA");

    public static final Color STADIUM_GREEN =
        Color.valueOf("163E2B");

    // =========================================================
    // CORES DE TEXTO GLOBAIS
    // =========================================================

    /*
     * A regra é simples:
     * nenhuma interface escura deve depender de Color.BLACK
     * herdado do Skin.
     */
    public static final Color TEXT_PRIMARY =
        Color.valueOf("FFFDF7");

    public static final Color TEXT_SECONDARY =
        Color.valueOf("E7EBE6");

    public static final Color TEXT_MUTED =
        Color.valueOf("BBC5BD");

    public static final Color TEXT_DISABLED =
        Color.valueOf("8B958D");

    public static final Color TEXT_ON_GOLD =
        Color.valueOf("102018");

    public static final Color BORDER_SOFT =
        Color.valueOf("45534A");

    public static final Color PANEL_HOVER =
        Color.valueOf("24362B");

    // =========================================================
    // STATUS
    // =========================================================

    public static final Color SUCCESS =
        Color.valueOf("65D38A");

    public static final Color WARNING =
        Color.valueOf("F0B44D");

    public static final Color DANGER =
        Color.valueOf("FF6B6B");

    // =========================================================
    // POSIÇÕES
    // =========================================================

    public static final Color POS_GK =
        Color.valueOf("2980B9");

    public static final Color POS_DEF =
        Color.valueOf("27AE60");

    public static final Color POS_MID =
        Color.valueOf("F39C12");

    public static final Color POS_ATT =
        Color.valueOf("E74C3C");

    // =========================================================
    // CORREÇÃO GLOBAL DE CONTRASTE
    // =========================================================

    /**
     * Corrige as cores de fonte compartilhadas pelo Skin.
     *
     * Como o Skin é compartilhado por todas as telas, basta um
     * ScreenUI usar o Skin uma vez para a correção atingir
     * Labels, TextButtons, ImageTextButtons, SelectBoxes,
     * Lists, CheckBoxes, TextFields e títulos de Window/Dialog.
     *
     * Isso elimina o caso clássico de fonte preta herdada em
     * painel preto/musgo.
     */
    public static void applyGlobalTextContrast(
        Skin skin
    ) {

        if (
            skin == null ||
            THEMED_SKINS.contains(
                skin
            )
        ) {
            return;
        }

        THEMED_SKINS.add(
            skin
        );

        applyLabelStyles(
            skin
        );

        applyTextButtonStyles(
            skin
        );

        applyImageTextButtonStyles(
            skin
        );

        applyCheckBoxStyles(
            skin
        );

        applySelectBoxStyles(
            skin
        );

        applyListStyles(
            skin
        );

        applyTextFieldStyles(
            skin
        );

        applyWindowStyles(
            skin
        );
    }

    private static void applyLabelStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, Label.LabelStyle> styles =
                skin.getAll(
                    Label.LabelStyle.class
                );

            for (
                ObjectMap.Entry<String, Label.LabelStyle> entry :
                styles.entries()
            ) {

                if (
                    entry.value == null
                ) {
                    continue;
                }

                String name =
                    entry.key != null
                        ? entry.key.toLowerCase()
                        : "";

                if (
                    name.contains(
                        "title"
                    )
                ) {

                    entry.value.fontColor =
                        new Color(
                            YELLOW_TITLE
                        );

                } else if (
                    name.contains(
                        "disabled"
                    )
                ) {

                    entry.value.fontColor =
                        new Color(
                            TEXT_DISABLED
                        );

                } else {

                    entry.value.fontColor =
                        new Color(
                            TEXT_PRIMARY
                        );
                }
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyTextButtonStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, TextButton.TextButtonStyle> styles =
                skin.getAll(
                    TextButton.TextButtonStyle.class
                );

            for (
                ObjectMap.Entry<String, TextButton.TextButtonStyle> entry :
                styles.entries()
            ) {

                fixTextButtonStyle(
                    entry.value
                );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyImageTextButtonStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, ImageTextButton.ImageTextButtonStyle> styles =
                skin.getAll(
                    ImageTextButton.ImageTextButtonStyle.class
                );

            for (
                ObjectMap.Entry<String, ImageTextButton.ImageTextButtonStyle> entry :
                styles.entries()
            ) {

                fixTextButtonStyle(
                    entry.value
                );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyCheckBoxStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, CheckBox.CheckBoxStyle> styles =
                skin.getAll(
                    CheckBox.CheckBoxStyle.class
                );

            for (
                ObjectMap.Entry<String, CheckBox.CheckBoxStyle> entry :
                styles.entries()
            ) {

                fixTextButtonStyle(
                    entry.value
                );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void fixTextButtonStyle(
        TextButton.TextButtonStyle style
    ) {

        if (
            style == null
        ) {
            return;
        }

        style.fontColor =
            new Color(
                TEXT_PRIMARY
            );

        style.overFontColor =
            new Color(
                YELLOW_TITLE
            );

        style.downFontColor =
            new Color(
                SOFT_YELLOW
            );

        style.checkedFontColor =
            new Color(
                SOFT_YELLOW
            );

        style.checkedOverFontColor =
            new Color(
                YELLOW_TITLE
            );

        style.disabledFontColor =
            new Color(
                TEXT_DISABLED
            );

        style.focusedFontColor =
            new Color(
                TEXT_PRIMARY
            );
    }

    private static void applySelectBoxStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, SelectBox.SelectBoxStyle> styles =
                skin.getAll(
                    SelectBox.SelectBoxStyle.class
                );

            for (
                ObjectMap.Entry<String, SelectBox.SelectBoxStyle> entry :
                styles.entries()
            ) {

                SelectBox.SelectBoxStyle style =
                    entry.value;

                if (
                    style == null
                ) {
                    continue;
                }

                style.fontColor =
                    new Color(
                        TEXT_PRIMARY
                    );

                style.disabledFontColor =
                    new Color(
                        TEXT_DISABLED
                    );

                if (
                    style.listStyle != null
                ) {

                    style.listStyle.fontColorUnselected =
                        new Color(
                            TEXT_PRIMARY
                        );

                    style.listStyle.fontColorSelected =
                        new Color(
                            SOFT_YELLOW
                        );

                    /*
                     * A seleção recebe fundo escuro dourado,
                     * nunca preto puro.
                     */
                    style.listStyle.selection =
                        createRoundedPanel(
                            Color.valueOf(
                                "352B10"
                            ),
                            DARK_GOLD
                        );
                }
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyListStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle> styles =
                skin.getAll(
                    com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle.class
                );

            for (
                ObjectMap.Entry<String, com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle> entry :
                styles.entries()
            ) {

                com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle style =
                    entry.value;

                if (
                    style == null
                ) {
                    continue;
                }

                style.fontColorUnselected =
                    new Color(
                        TEXT_PRIMARY
                    );

                style.fontColorSelected =
                    new Color(
                        SOFT_YELLOW
                    );

                style.selection =
                    createRoundedPanel(
                        Color.valueOf(
                            "352B10"
                        ),
                        DARK_GOLD
                    );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyTextFieldStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, TextField.TextFieldStyle> styles =
                skin.getAll(
                    TextField.TextFieldStyle.class
                );

            for (
                ObjectMap.Entry<String, TextField.TextFieldStyle> entry :
                styles.entries()
            ) {

                TextField.TextFieldStyle style =
                    entry.value;

                if (
                    style == null
                ) {
                    continue;
                }

                style.fontColor =
                    new Color(
                        TEXT_PRIMARY
                    );

                style.disabledFontColor =
                    new Color(
                        TEXT_DISABLED
                    );

                style.messageFontColor =
                    new Color(
                        TEXT_MUTED
                    );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    private static void applyWindowStyles(
        Skin skin
    ) {

        try {

            ObjectMap<String, Window.WindowStyle> styles =
                skin.getAll(
                    Window.WindowStyle.class
                );

            for (
                ObjectMap.Entry<String, Window.WindowStyle> entry :
                styles.entries()
            ) {

                Window.WindowStyle style =
                    entry.value;

                if (
                    style == null
                ) {
                    continue;
                }

                style.titleFontColor =
                    new Color(
                        YELLOW_TITLE
                    );
            }

        } catch (
            Exception ignored
        ) {
        }
    }

    // =========================================================
    // CONTRASTE DINÂMICO
    // =========================================================

    /**
     * Retorna uma cor de texto legível sobre a cor recebida.
     * Útil para badges, chips e indicadores coloridos.
     */
    public static Color getReadableTextColor(
        Color background
    ) {

        if (
            background == null
        ) {

            return new Color(
                TEXT_PRIMARY
            );
        }

        float luminance =
            background.r * 0.299f +
            background.g * 0.587f +
            background.b * 0.114f;

        /*
         * Fundos claros recebem tinta escura.
         * Fundos escuros recebem creme.
         */
        if (
            luminance >= 0.58f
        ) {

            return new Color(
                TEXT_ON_GOLD
            );
        }

        return new Color(
            TEXT_PRIMARY
        );
    }

    // =========================================================
    // DRAWABLES
    // =========================================================

    public static Drawable createSolid(
        Color color
    ) {

        Pixmap pixmap =
            new Pixmap(
                1,
                1,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            color
        );

        pixmap.fill();

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new TextureRegionDrawable(
            texture
        );
    }

    public static Drawable createModernButton(
        int width,
        int height,
        Color baseColor,
        Color borderColor
    ) {

        Pixmap pixmap =
            new Pixmap(
                width,
                height,
                Pixmap.Format.RGBA8888
            );

        int radius =
            Math.max(
                8,
                Math.min(
                    16,
                    Math.min(
                        width,
                        height
                    ) /
                        3
                )
            );

        int inset =
            Math.max(
                2,
                Math.min(3, height / 16)
            );

        fillRounded(
            pixmap,
            0,
            0,
            width,
            height,
            radius,
            borderColor
        );

        fillRounded(
            pixmap,
            inset,
            inset,
            width - inset * 2,
            height - inset * 2,
            Math.max(
                4,
                radius - inset
            ),
            baseColor
        );

        pixmap.setColor(
            new Color(
                1f,
                1f,
                1f,
                0.10f
            )
        );

        pixmap.fillRectangle(
            inset + radius / 2,
            height / 2,
            Math.max(1, width - (inset + radius / 2) * 2),
            Math.max(
                1,
                height / 2 -
                    inset
            )
        );

        pixmap.setColor(
            new Color(
                0f,
                0f,
                0f,
                0.18f
            )
        );

        pixmap.fillRectangle(
            inset + radius / 2,
            inset,
            Math.max(1, width - (inset + radius / 2) * 2),
            Math.max(2, height / 8)
        );

        pixmap.setColor(
            new Color(
                borderColor.r,
                borderColor.g,
                borderColor.b,
                0.42f
            )
        );

        pixmap.fillRectangle(
            inset + radius,
            height - inset - 2,
            Math.max(1, width - (inset + radius) * 2),
            1
        );

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        int patch =
            Math.max(
                6,
                Math.min(
                    radius + 2,
                    Math.min(width, height) / 2 - 1
                )
            );

        return new NinePatchDrawable(
            new NinePatch(
                texture,
                patch,
                patch,
                patch,
                patch
            )
        );
    }

    private static void fillRounded(
        Pixmap pixmap,
        int x,
        int y,
        int width,
        int height,
        int radius,
        Color color
    ) {

        pixmap.setColor(
            color
        );

        pixmap.fillRectangle(
            x + radius,
            y,
            width -
                radius * 2,
            height
        );

        pixmap.fillRectangle(
            x,
            y + radius,
            width,
            height -
                radius * 2
        );

        pixmap.fillCircle(
            x + radius,
            y + radius,
            radius
        );

        pixmap.fillCircle(
            x + width -
                radius -
                1,
            y + radius,
            radius
        );

        pixmap.fillCircle(
            x + radius,
            y + height -
                radius -
                1,
            radius
        );

        pixmap.fillCircle(
            x + width -
                radius -
                1,
            y + height -
                radius -
                1,
            radius
        );
    }

    public static Drawable createRoundedPanel(
        Color background,
        Color border
    ) {

        int size =
            64;

        Pixmap pixmap =
            new Pixmap(
                size,
                size,
                Pixmap.Format.RGBA8888
            );

        fillRounded(
            pixmap,
            0,
            0,
            size,
            size,
            12,
            border
        );

        fillRounded(
            pixmap,
            2,
            2,
            size - 4,
            size - 4,
            10,
            background
        );

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new NinePatchDrawable(
            new NinePatch(
                texture,
                12,
                12,
                12,
                12
            )
        );
    }

    public static Drawable createBadge(
        Color color
    ) {

        int width =
            48;

        int height =
            24;

        Pixmap pixmap =
            new Pixmap(
                width,
                height,
                Pixmap.Format.RGBA8888
            );

        fillRounded(
            pixmap,
            0,
            0,
            width,
            height,
            6,
            color
        );

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new NinePatchDrawable(
            new NinePatch(
                texture,
                6,
                6,
                6,
                6
            )
        );
    }

    // =========================================================
    // POSITION COLORS
    // =========================================================

    public static Color getPositionColor(
        String position
    ) {

        if (
            position == null
        ) {

            return Color.valueOf(
                "59615C"
            );
        }

        String pos =
            position
                .toUpperCase();

        if (
            pos.equals(
                "GK"
            )
        ) {

            return POS_GK;
        }

        if (
            pos.matches(
                "CB|RB|LB|RWB|LWB|SW"
            )
        ) {

            return POS_DEF;
        }

        if (
            pos.matches(
                "CDM|CM|CAM|RM|LM|RAM|LAM|AM"
            )
        ) {

            return POS_MID;
        }

        if (
            pos.matches(
                "RW|LW|CF|ST|SS|RF|LF"
            )
        ) {

            return POS_ATT;
        }

        return Color.valueOf(
            "59615C"
        );
    }

    // =========================================================
    // LEGACY DRAWABLES
    // =========================================================

    public static Drawable createCheckerboard() {

        int size =
            64;

        Pixmap pixmap =
            new Pixmap(
                size,
                size,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            Color.valueOf(
                "121519"
            )
        );

        pixmap.fill();

        pixmap.setColor(
            Color.valueOf(
                "191D24"
            )
        );

        pixmap.fillRectangle(
            0,
            0,
            size / 2,
            size / 2
        );

        pixmap.fillRectangle(
            size / 2,
            size / 2,
            size / 2,
            size / 2
        );

        Texture texture =
            new Texture(
                pixmap
            );

        texture.setWrap(
            Texture.TextureWrap.Repeat,
            Texture.TextureWrap.Repeat
        );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new TextureRegionDrawable(
            texture
        );
    }

    public static Drawable createParchment() {

        Pixmap pixmap =
            new Pixmap(
                64,
                64,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            CREME_AGED
        );

        pixmap.fill();

        pixmap.setColor(
            new Color(
                0f,
                0f,
                0f,
                0.2f
            )
        );

        pixmap.drawRectangle(
            0,
            0,
            64,
            64
        );

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new NinePatchDrawable(
            new NinePatch(
                texture,
                8,
                8,
                8,
                8
            )
        );
    }

    public static Drawable createMetallicBoard(
        int width,
        int height,
        Color bgColor
    ) {

        Pixmap pixmap =
            new Pixmap(
                width,
                height,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            bgColor
        );

        pixmap.fillRectangle(
            0,
            0,
            width,
            height
        );

        pixmap.setColor(
            GOLD
        );

        for (
            int i = 0;
            i < 3;
            i++
        ) {

            pixmap.drawRectangle(
                i,
                i,
                width -
                    i * 2,
                height -
                    i * 2
            );
        }

        pixmap.setColor(
            Color.LIGHT_GRAY
        );

        pixmap.fillCircle(
            8,
            8,
            3
        );

        pixmap.fillCircle(
            width - 8,
            8,
            3
        );

        pixmap.fillCircle(
            8,
            height - 8,
            3
        );

        pixmap.fillCircle(
            width - 8,
            height - 8,
            3
        );

        Texture texture =
            new Texture(
                pixmap
            );

        pixmap.dispose();

        GENERATED_TEXTURES.add(
            texture
        );

        return new NinePatchDrawable(
            new NinePatch(
                texture,
                10,
                10,
                10,
                10
            )
        );
    }

    public static Drawable createBrushedMetal() {

        return createMetallicBoard(
            32,
            32,
            METAL_DARK
        );
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    public static void disposeGenerated() {

        for (
            Texture texture :
            GENERATED_TEXTURES
        ) {

            if (
                texture != null
            ) {

                texture.dispose();
            }
        }

        GENERATED_TEXTURES.clear();

        THEMED_SKINS.clear();
    }
}
