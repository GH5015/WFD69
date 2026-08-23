package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

public class RemoveScoutTargetDialog extends Dialog {

    private final Skin skin;

    private final ScoutTarget target;

    private final DraftScoutManager scoutManager;

    private final Runnable onRemovedCallback;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RemoveScoutTargetDialog(
        Skin skin,
        ScoutTarget target,
        DraftScoutManager scoutManager,
        Runnable onRemovedCallback
    ) {

        super(
            "",
            skin
        );

        this.skin =
            skin;

        this.target =
            target;

        this.scoutManager =
            scoutManager;

        this.onRemovedCallback =
            onRemovedCallback;

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
                380,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        // =====================================================
        // HEADER
        // =====================================================

        Table header =
            ScreenUI.createPanel();

        Label title =
            new Label(
                "REMOVER OBSERVAÇÃO",
                skin,
                "font-title"
            );

        title.setFontScale(
            0.67f
        );

        title.setColor(
            ScreenUI.DANGER
        );

        header
            .add(title)
            .center();

        root
            .add(header)
            .growX()
            .height(65f)
            .padBottom(8f)
            .row();

        // =====================================================
        // PLAYER
        // =====================================================

        Table playerPanel =
            ScreenUI.createSubtlePanel();

        Label name =
            new Label(
                target
                    .getPlayer()
                    .getName()
                    .toUpperCase(),
                skin,
                "font-title"
            );

        name.setFontScale(
            0.67f
        );

        name.setColor(
            StyleFactory.GOLD
        );

        playerPanel
            .add(name)
            .colspan(2)
            .center()
            .padBottom(10f)
            .row();

        playerPanel
            .add(
                ScreenUI.createBadge(
                    skin,
                    target
                        .getPlayer()
                        .getPrimaryPosition()
                        .name(),
                    StyleFactory
                        .getPositionColor(
                            target
                                .getPlayer()
                                .getPrimaryPosition()
                                .name()
                        )
                )
            )
            .height(27f)
            .padRight(10f);

        playerPanel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "CONHECIMENTO",
                    String.format(
                        "%.1f%%",
                        target
                            .getKnowledgePercentage()
                    ),
                    target.isFullyScouted()
                        ? ScreenUI.SUCCESS
                        : StyleFactory.SOFT_YELLOW
                )
            )
            .width(210f)
            .height(40f);

        root
            .add(playerPanel)
            .growX()
            .height(105f)
            .padBottom(8f)
            .row();

        // =====================================================
        // WARNING
        // =====================================================

        Table warning =
            ScreenUI.createPanel();

        Label warningTitle =
            new Label(
                "O PROGRESSO SERÁ PERDIDO",
                skin,
                "font-bold"
            );

        warningTitle.setFontScale(
            0.57f
        );

        warningTitle.setColor(
            ScreenUI.DANGER
        );

        warning
            .add(warningTitle)
            .center()
            .padBottom(6f)
            .row();

        Label message =
            new Label(
                "Ao remover este jogador, a vaga do scout será liberada.\n" +
                    "Se ele for observado novamente, um novo relatório poderá começar do zero.",
                skin
            );

        message.setWrap(
            true
        );

        message.setAlignment(
            Align.center
        );

        message.setFontScale(
            0.55f
        );

        message.setColor(
            StyleFactory.CREME_AGED
        );

        warning
            .add(message)
            .width(420f)
            .center();

        root
            .add(warning)
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

        ImageTextButton remove =
            IconTextButton.create(
                "REMOVER",
                skin,
                "Icons8/icons8-remover-50.png"
            );

        remove
            .getLabel()
            .setFontScale(
                0.55f
            );

        remove
            .getLabel()
            .setColor(
                ScreenUI.DANGER
            );

        remove.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        scoutManager != null &&
                            target != null
                    ) {

                        scoutManager
                            .removeTarget(
                                target
                            );
                    }

                    hide();

                    if (
                        onRemovedCallback != null
                    ) {

                        onRemovedCallback.run();
                    }
                }
            }
        );

        buttons
            .add(remove)
            .width(150f)
            .height(43f)
            .padRight(7f);

        ImageTextButton cancel =
            IconTextButton.create(
                "MANTER JOGADOR",
                skin,
                "Icons8/icons8-cancelar-50.png"
            );

        cancel
            .getLabel()
            .setFontScale(
                0.50f
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
            .width(180f)
            .height(43f);
    }
}
