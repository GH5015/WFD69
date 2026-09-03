package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.TradeDecision;
import io.github.some_example_name.model.TradeOffer;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

public class TradeFeedbackDialog extends Dialog {

    // =========================================================
    // LISTENER
    // =========================================================

    public interface TradeDialogListener {

        void onAcceptCounterOffer(
            TradeOffer counterOffer
        );

        void onModifyOffer();

        void onCancel();
    }

    // =========================================================
    // FIELDS
    // =========================================================

    private final Skin skin;

    private final String targetClubName;

    private final TradeDecision decision;

    private final TradeDialogListener listener;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TradeFeedbackDialog(
        Skin skin,
        String targetClubName,
        TradeDecision decision,
        TradeDialogListener listener
    ) {

        super(
            "",
            skin
        );

        this.skin =
            skin;

        this.targetClubName =
            targetClubName;

        this.decision =
            decision;

        this.listener =
            listener;

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
                900,
                620,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            18f
        );

        // =====================================================
        // HEADER
        // =====================================================

        root
            .add(
                createHeader()
            )
            .growX()
            .height(82f)
            .padBottom(10f)
            .row();

        // =====================================================
        // FEEDBACK
        // =====================================================

        root
            .add(
                createFeedbackPanel()
            )
            .growX()
            .padBottom(10f)
            .row();

        // =====================================================
        // VALUES
        // =====================================================

        root
            .add(
                createValuesPanel()
            )
            .growX()
            .padBottom(8f)
            .row();

        // =====================================================
        // ACTIONS
        // =====================================================

        createActions();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table panel =
            ScreenUI.createPanel();

        Table identity =
            new Table();

        Label title =
            new Label(
                targetClubName
                    .toUpperCase(),
                skin,
                "font-title"
            );

        title.setFontScale(
            0.72f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(title)
            .left()
            .row();

        Label sub =
            ScreenUI.createSubtitle(
                skin,
                "NEGOCIAÇÃO DE TROCA • RESPOSTA À PROPOSTA"
            );

        identity
            .add(sub)
            .left()
            .padTop(2f);

        panel
            .add(identity)
            .left()
            .expandX();

        Color statusColor =
            getStatusColor();

        Table status =
            ScreenUI.createBadge(
                skin,
                decision
                    .getStatus()
                    .getLabel()
                    .toUpperCase(),
                statusColor
            );

        panel
            .add(status)
            .height(30f)
            .right();

        return panel;
    }

    // =========================================================
    // FEEDBACK
    // =========================================================

    private Table createFeedbackPanel() {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        Label heading =
            ScreenUI.createSectionTitle(
                skin,
                "AVALIAÇÃO DA DIRETORIA"
            );

        panel
            .add(heading)
            .left()
            .padBottom(10f)
            .row();

        Label quote =
            new Label(
                "\"" +
                    decision.getFeedbackMessage() +
                    "\"",
                skin
            );

        quote.setWrap(
            true
        );

        quote.setAlignment(
            Align.center
        );

        quote.setColor(
            StyleFactory.CREME_AGED
        );

        quote.setFontScale(
            0.62f
        );

        panel
            .add(quote)
            .width(760f)
            .center()
            .pad(
                8f,
                14f,
                10f,
                14f
            );

        return panel;
    }

    // =========================================================
    // VALUES
    // =========================================================

    private Table createValuesPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    skin,
                    "VALOR DA NEGOCIAÇÃO"
                )
            )
            .left()
            .padBottom(10f)
            .row();

        long offered =
            decision.getOfferedValue();

        long expected =
            decision.getExpectedValue();

        long comparisonScale = Math.max(1L, Math.max(offered, expected));

        panel
            .add(
                createValueBar(
                    "SUA PROPOSTA",
                    offered,
                    comparisonScale,
                    StyleFactory.GOLD
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        panel
            .add(
                createValueBar(
                    "VALOR ESPERADO",
                    expected,
                    comparisonScale,
                    ScreenUI.SUCCESS
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        long difference =
            offered -
                expected;

        String differenceText =
            "TV " +
                (difference >= 0
                    ? "+" + difference
                    : String.valueOf(
                        difference
                    ));

        panel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "DIFERENÇA",
                    differenceText,
                    difference >= 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(42f);

        return panel;
    }

    private Table createValueBar(
        String title,
        long value,
        long comparisonScale,
        Color color
    ) {

        Table row =
            new Table();

        Label name =
            ScreenUI.createSubtitle(
                skin,
                title
            );

        row
            .add(name)
            .width(175f)
            .left();

        row
            .add(
                ScreenUI.createBlockProgress(
                    skin,
                    (int) Math.round(
                        Math.max(0d, Math.min(1d, (double) value / Math.max(1L, comparisonScale))) * 100d
                    ),
                    15,
                    color
                )
            )
            .expandX()
            .left();

        Label valueLabel =
            new Label(
                "TV " + value,
                skin,
                "font-bold"
            );

        valueLabel.setFontScale(
            0.65f
        );

        valueLabel.setColor(
            color
        );

        row
            .add(valueLabel)
            .width(95f)
            .right();

        return row;
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    private void createActions() {

        Table buttons =
            getButtonTable();

        buttons.pad(
            8f,
            8f,
            10f,
            8f
        );

        // =====================================================
        // ACCEPTED
        // =====================================================

        if (
            decision.getStatus() ==
                TradeDecision.Status.ACCEPTED
        ) {

            ImageTextButton confirm =
                IconTextButton.create(
                    "EFETIVAR TROCA",
                    skin,
                    "Icons8/icons8-aprovação-50.png"
                );

            confirm
                .getLabel()
                .setFontScale(
                    0.58f
                );

            confirm.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        hide();

                        if (
                            listener != null
                        ) {

                            listener
                                .onAcceptCounterOffer(
                                    null
                                );
                        }
                    }
                }
            );

            buttons
                .add(confirm)
                .width(205f)
                .height(44f)
                .padRight(8f);

            ImageTextButton cancel =
                createCancelButton();

            buttons
                .add(cancel)
                .width(140f)
                .height(44f);

            return;
        }

        // =====================================================
        // COUNTER OFFER
        // =====================================================

        if (
            decision.hasCounterOffer()
        ) {

            ImageTextButton counter =
                IconTextButton.create(
                    "ACEITAR CONTRAOFERTA",
                    skin,
                    "Icons8/icons8-aprovação-50.png"
                );

            counter
                .getLabel()
                .setFontScale(
                    0.53f
                );

            counter.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        hide();

                        if (
                            listener != null
                        ) {

                            listener
                                .onAcceptCounterOffer(
                                    decision
                                        .getCounterOffer()
                                );
                        }
                    }
                }
            );

            buttons
                .add(counter)
                .width(225f)
                .height(44f)
                .padRight(7f);
        }

        // =====================================================
        // MODIFY
        // =====================================================

        ImageTextButton modify =
            IconTextButton.create(
                "MODIFICAR",
                skin,
                "Icons8/icons8-editar-50.png"
            );

        modify
            .getLabel()
            .setFontScale(
                0.56f
            );

        modify.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    hide();

                    if (
                        listener != null
                    ) {

                        listener
                            .onModifyOffer();
                    }
                }
            }
        );

        buttons
            .add(modify)
            .width(145f)
            .height(44f)
            .padRight(7f);

        buttons
            .add(
                createCancelButton()
            )
            .width(135f)
            .height(44f);
    }

    private ImageTextButton createCancelButton() {

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

                    if (
                        listener != null
                    ) {

                        listener.onCancel();
                    }
                }
            }
        );

        return cancel;
    }

    // =========================================================
    // STATUS
    // =========================================================

    private Color getStatusColor() {

        try {

            return Color.valueOf(
                decision
                    .getStatus()
                    .getHexColor()
            );

        } catch (
            Exception ignored
        ) {

            return StyleFactory.GOLD;
        }
    }
}
