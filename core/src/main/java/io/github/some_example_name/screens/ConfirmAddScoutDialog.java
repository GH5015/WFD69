package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

public class ConfirmAddScoutDialog extends Dialog {

    private final Skin skin;

    private final Player player;

    private final DraftScoutManager scoutManager;

    private final Runnable onAddedCallback;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ConfirmAddScoutDialog(
        Skin skin,
        Player player,
        DraftScoutManager scoutManager,
        Runnable onAddedCallback
    ) {

        super(
            "",
            skin
        );

        this.skin =
            skin;

        this.player =
            player;

        this.scoutManager =
            scoutManager;

        this.onAddedCallback =
            onAddedCallback;

        setModal(
            true
        );

        setMovable(
            false
        );

        buildUI();
    }

    // =========================================================
    // UI
    // =========================================================

    private void buildUI() {

        getContentTable()
            .clear();

        getButtonTable()
            .clear();

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                520,
                370,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        // =====================================================
        // PLAYER
        // =====================================================

        Table playerPanel =
            ScreenUI.createPanel();

        Label name =
            new Label(
                player.getName()
                    .toUpperCase(),
                skin,
                "font-title"
            );

        name.setFontScale(
            0.70f
        );

        name.setColor(
            StyleFactory.GOLD
        );

        name.setAlignment(
            Align.center
        );

        playerPanel
            .add(name)
            .colspan(2)
            .center()
            .padBottom(8f)
            .row();

        playerPanel
            .add(
                ScreenUI.createBadge(
                    skin,
                    player
                        .getPrimaryPosition()
                        .name(),
                    StyleFactory
                        .getPositionColor(
                            player
                                .getPrimaryPosition()
                                .name()
                        )
                )
            )
            .height(28f)
            .padRight(8f);

        String info =
            (
                player.getNationality() != null
                    ? player.getNationality()
                    : "N/D"
            )
                +
                "  •  " +
                player.getAge() +
                " anos";

        playerPanel
            .add(
                ScreenUI.createValueLabel(
                    skin,
                    info,
                    ScreenUI.MUTED_TEXT,
                    Align.left
                )
            )
            .left();

        root
            .add(playerPanel)
            .growX()
            .height(105f)
            .padBottom(8f)
            .row();

        // =====================================================
        // INFORMATION
        // =====================================================

        Table infoPanel =
            ScreenUI.createSubtlePanel();

        infoPanel
            .add(
                ScreenUI.createSectionTitle(
                    skin,
                    "INICIAR OBSERVAÇÃO?"
                )
            )
            .center()
            .padBottom(10f)
            .row();

        Label description =
            new Label(
                "O jogador ocupará uma das cinco vagas do seu scout.\n" +
                    "O conhecimento começará em 0% e aumentará a cada dia.",
                skin
            );

        description.setWrap(
            true
        );

        description.setAlignment(
            Align.center
        );

        description.setFontScale(
            0.58f
        );

        description.setColor(
            StyleFactory.CREME_AGED
        );

        infoPanel
            .add(description)
            .width(420f)
            .center()
            .padBottom(10f)
            .row();

        int occupied =
            scoutManager != null
                ? scoutManager
                .getActiveTargets()
                .size()
                : 0;

        infoPanel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "VAGAS APÓS ADICIONAR",
                    Math.min(
                        5,
                        occupied + 1
                    ) +
                        "/5",
                    occupied < 5
                        ? StyleFactory.SOFT_YELLOW
                        : ScreenUI.DANGER
                )
            )
            .width(300f)
            .height(42f);

        root
            .add(infoPanel)
            .growX()
            .row();

        // =====================================================
        // BUTTONS
        // =====================================================

        Table buttons =
            getButtonTable();

        buttons.pad(
            8f
        );

        ImageTextButton confirm =
            IconTextButton.create(
                "INICIAR OBSERVAÇÃO",
                skin,
                "Icons8/icons8-binóculos-50.png"
            );

        confirm
            .getLabel()
            .setFontScale(
                0.54f
            );

        confirm.setDisabled(
            scoutManager == null ||
                scoutManager.isFull()
        );

        confirm.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        scoutManager == null ||
                            scoutManager.isFull()
                    ) {

                        return;
                    }

                    boolean added =
                        scoutManager.addTarget(
                            player
                        );

                    hide();

                    if (
                        added &&
                            onAddedCallback != null
                    ) {

                        onAddedCallback.run();
                    }
                }
            }
        );

        buttons
            .add(confirm)
            .width(220f)
            .height(43f)
            .padRight(7f);

        ImageTextButton cancel =
            IconTextButton.create(
                "CANCELAR",
                skin,
                "Icons8/icons8-cancelar-50.png"
            );

        cancel
            .getLabel()
            .setFontScale(
                0.54f
            );

        cancel.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    hide();
                }
            }
        );

        buttons
            .add(cancel)
            .width(135f)
            .height(43f);
    }
}
