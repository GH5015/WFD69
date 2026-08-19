package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.model.TradeDecision;
import io.github.some_example_name.model.TradeOffer;
import io.github.some_example_name.utils.StyleFactory;

public class TradeFeedbackDialog extends Dialog {

    public interface TradeDialogListener {
        void onAcceptCounterOffer(TradeOffer counterOffer);
        void onModifyOffer();
        void onCancel();
    }

    public TradeFeedbackDialog(Skin skin, String targetClubName, TradeDecision decision, TradeDialogListener listener) {
        super("", skin);

        Table content = getContentTable();
        content.pad(20);

        // Cabeçalho: NOME DO CLUBE
        Label clubLabel = new Label(targetClubName.toUpperCase(), skin, "font-bold");
        clubLabel.setFontScale(0.8f);
        clubLabel.setColor(StyleFactory.SOFT_YELLOW);
        content.add(clubLabel).left().row();

        // Status Tag ex: 🟡 PROPOSTA CONSIDERADA
        Label statusLabel = new Label("● " + decision.getStatus().getLabel(), skin, "font-bold");
        statusLabel.setColor(Color.valueOf(decision.getStatus().getHexColor()));
        statusLabel.setFontScale(0.75f);
        content.add(statusLabel).left().padBottom(12).row();

        // Mensagem de Feedback em Aspas
        Label quoteLabel = new Label("\"" + decision.getFeedbackMessage() + "\"", skin);
        quoteLabel.setWrap(true);
        quoteLabel.setColor(Color.LIGHT_GRAY);
        content.add(quoteLabel).width(420).left().padBottom(16).row();

        // Barra 1: Valor da Proposta
        content.add(createValueBar("Valor da proposta:", decision.getOfferedValue(), Color.valueOf("F1C40F"), skin)).growX().row();

        // Barra 2: Valor Esperado
        content.add(createValueBar("Valor esperado: ", decision.getExpectedValue(), Color.valueOf("2ECC71"), skin)).growX().padBottom(20).row();

        // Botões de Ação
        Table btnTable = getButtonTable();
        btnTable.padBottom(10);

        if (decision.getStatus() == TradeDecision.Status.ACCEPTED) {
            TextButton confirmBtn = new TextButton("EFETIVAR TROCA", skin);
            confirmBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    hide();
                    listener.onAcceptCounterOffer(null); // Executa troca
                }
            });
            btnTable.add(confirmBtn).width(180).height(38);
        } else {
            if (decision.hasCounterOffer()) {
                TextButton acceptCounterBtn = new TextButton("ACEITAR CONTRAOFERTA", skin);
                acceptCounterBtn.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event, float x, float y) {
                        hide();
                        listener.onAcceptCounterOffer(decision.getCounterOffer());
                    }
                });
                btnTable.add(acceptCounterBtn).width(180).height(38).padRight(8);
            }

            TextButton modifyBtn = new TextButton("MODIFICAR", skin);
            modifyBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    hide();
                    listener.onModifyOffer();
                }
            });
            btnTable.add(modifyBtn).width(120).height(38).padRight(8);

            TextButton cancelBtn = new TextButton("CANCELAR", skin);
            cancelBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    hide();
                    listener.onCancel();
                }
            });
            btnTable.add(cancelBtn).width(100).height(38);
        }
    }

    private Table createValueBar(String title, int value, Color barColor, Skin skin) {
        Table row = new Table();

        Label label = new Label(title, skin, "font-bold");
        label.setFontScale(0.55f);
        label.setColor(Color.WHITE);
        row.add(label).width(130).left();

        int totalBlocks = 18;
        int filled = Math.round((value / 100.0f) * totalBlocks);
        StringBuilder barStr = new StringBuilder();
        for (int i = 0; i < totalBlocks; i++) {
            barStr.append(i < filled ? "█" : "░");
        }

        Label barLabel = new Label(barStr.toString(), skin, "font-bold");
        barLabel.setFontScale(0.60f);
        barLabel.setColor(barColor);
        row.add(barLabel).left().expandX();

        Label numLabel = new Label(String.valueOf(value), skin, "font-bold");
        numLabel.setFontScale(0.60f);
        numLabel.setColor(barColor);
        row.add(numLabel).right();

        return row;
    }
}
