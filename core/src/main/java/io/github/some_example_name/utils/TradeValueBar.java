package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TradeValueCalculator;

public class TradeValueBar {

    /**
     * Retorna um widget (Table) contendo a representação visual em barra do Trade Value.
     */
    public static Table createBarWidget(Player player, Skin skin) {
        int tradeValue = TradeValueCalculator.calculateTradeValue(player);
        Table container = new Table();

        // Rótulo numérico ex: "TRADE VALUE  74"
        Label titleLabel = new Label("TRADE VALUE ", skin, "font-bold");
        titleLabel.setFontScale(0.60f);
        titleLabel.setColor(Color.WHITE);

        Label valueLabel = new Label(String.valueOf(tradeValue), skin, "font-bold");
        valueLabel.setFontScale(0.65f);
        valueLabel.setColor(getTradeValueColor(tradeValue));

        container.add(titleLabel).left();
        container.add(valueLabel).left().padRight(8);

        // Barra gráfica com caracteres ASCII/Unicode blocados
        int totalBlocks = 15;
        int filledBlocks = Math.round((tradeValue / 100.0f) * totalBlocks);

        StringBuilder barText = new StringBuilder();
        for (int i = 0; i < totalBlocks; i++) {
            barText.append(i < filledBlocks ? "█" : "░");
        }

        Label barLabel = new Label(barText.toString(), skin, "font-bold");
        barLabel.setFontScale(0.55f);
        barLabel.setColor(getTradeValueColor(tradeValue));

        container.add(barLabel).left();

        return container;
    }

    private static Color getTradeValueColor(int value) {
        if (value >= 85) return StyleFactory.GOLD;           // Elite / Innegociável
        if (value >= 70) return Color.valueOf("2ECC71");      // Alto Valor (Verde)
        if (value >= 50) return StyleFactory.SOFT_YELLOW;     // Média Mercado (Amarelo)
        return Color.valueOf("E74C3C");                       // Baixo Valor (Vermelho)
    }
}
