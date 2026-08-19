package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.InterestLevel;

public class InterestBarWidget {

    /**
     * Cria a tabela com a barra de interesse do clube adversário.
     */
    public static Table createWidget(Club targetClub, int interestScore, Skin skin) {
        Table container = new Table();
        InterestLevel level = InterestLevel.fromScore(interestScore);

        // Título ex: "INTERESSE DO BAVARIA"
        Label titleLabel = new Label("INTERESSE DO " + targetClub.getName().toUpperCase(), skin, "font-bold");
        titleLabel.setFontScale(0.65f);
        titleLabel.setColor(Color.WHITE);
        container.add(titleLabel).left().expandX();

        // Status numérico ex: "78 / 100"
        Label scoreLabel = new Label(interestScore + " / 100", skin, "font-bold");
        scoreLabel.setFontScale(0.65f);
        scoreLabel.setColor(level.getColor());
        container.add(scoreLabel).right().row();

        // Barra gráfica com blocos (20 blocos no total)
        int totalBlocks = 20;
        int filledBlocks = Math.round((interestScore / 100.0f) * totalBlocks);

        StringBuilder barText = new StringBuilder();
        for (int i = 0; i < totalBlocks; i++) {
            barText.append(i < filledBlocks ? "█" : "░");
        }

        Label barLabel = new Label(barText.toString(), skin, "font-bold");
        barLabel.setFontScale(0.70f);
        barLabel.setColor(level.getColor());
        container.add(barLabel).colspan(2).left().padTop(2).padBottom(4).row();

        // Classificação ex: "🟢 Interessado"
        Label statusLabel = new Label("● " + level.getLabel(), skin, "font-bold");
        statusLabel.setFontScale(0.60f);
        statusLabel.setColor(level.getColor());
        container.add(statusLabel).colspan(2).left().row();

        return container;
    }
}
