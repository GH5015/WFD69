package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;
import java.util.Map;

public class LineupDialog extends Dialog {
    private final Main game;
    private final Club club;

    public LineupDialog(Main game, Club club) {
        super("", game.skin);
        this.game = game;
        this.club = club;
        getContentTable().clear();
        buildLayout();
    }

    private void buildLayout() {
        Table root = getContentTable();
        root.background(StyleFactory.createMetallicBoard(1100, 850, Color.valueOf("2B2B2B")));
        root.pad(30);

        // Header
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.WINE_RED, StyleFactory.GOLD));
        Label title = new Label("⚽ ESCALAÇÃO - " + club.getName().toUpperCase(), game.skin, "font-title", Color.WHITE);
        title.setFontScale(1.6f);
        header.add(title).pad(20);
        root.add(header).growX().padBottom(25).row();

        // Formação Info
        Label formationLabel = new Label("FORMAÇÃO: " + club.getFormation(), game.skin, "font-bold", StyleFactory.GOLD);
        formationLabel.setFontScale(1.2f);
        root.add(formationLabel).center().padBottom(20).row();

        // Campo Tático
        Table pitchTable = new Table();
        pitchTable.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        pitchTable.pad(25);

        Formation f = club.getFormation();
        List<String> formationSlots = f.getPositionSlots();
        Map<Integer, Player> tacticsMap = club.getTacticsMap();

        // Criar linhas para cada posição
        Table fwRow = createLineRow(tacticsMap, formationSlots, 4);  // Atacantes
        Table mfRow = createLineRow(tacticsMap, formationSlots, 3);  // Meio-campo
        Table dfRow = createLineRow(tacticsMap, formationSlots, 2);  // Defesa
        Table gkRow = createLineRow(tacticsMap, formationSlots, 1);  // Goleiro

        pitchTable.add(fwRow).expandX().fillX().padBottom(15).row();
        pitchTable.add(mfRow).expandX().fillX().padBottom(15).row();
        pitchTable.add(dfRow).expandX().fillX().padBottom(15).row();
        pitchTable.add(gkRow).expandX().fillX().row();

        root.add(pitchTable).growX().padBottom(20).row();

        // Seção de Legenda (Status dos jogadores)
        Table legendTable = new Table();
        legendTable.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        legendTable.pad(15);

        Label legendTitle = new Label("📋 STATUS DOS JOGADORES", game.skin, "font-label", StyleFactory.GOLD);
        legendTitle.setFontScale(1.1f);
        legendTable.add(legendTitle).left().padBottom(12).row();

        Table playerDetailsTable = new Table();
        int count = 0;
        for (Map.Entry<Integer, Player> entry : tacticsMap.entrySet()) {
            Player p = entry.getValue();
            if (count >= 11) break;  // Máximo 11 jogadores

            Table playerRow = createPlayerDetailsRow(p);
            playerDetailsTable.add(playerRow).growX().padBottom(8).row();
            count++;
        }

        ScrollPane scroll = new ScrollPane(playerDetailsTable, game.skin);
        scroll.setFadeScrollBars(false);
        legendTable.add(scroll).growX().height(120);
        root.add(legendTable).growX().padBottom(20).row();

        // Botão de Ação
        ImageTextButton btn = IconTextButton.create("CONTINUAR", game.skin, "Icons8/icons8-ok-50.png");
        btn.getLabel().setFontScale(1.3f);
        button(btn, true);
    }

    private Table createLineRow(Map<Integer, Player> tacticsMap, List<String> formationSlots, int posWeight) {
        Table row = new Table();
        row.align(Align.center);

        for (int i = 0; i < 11; i++) {
            String targetPos = formationSlots.get(i);
            int weight = getPositionWeight(targetPos);

            if (weight == posWeight) {
                Player p = tacticsMap.get(i);
                Table playerCard = createPlayerCard(p, targetPos);
                row.add(playerCard).width(120).height(90).padRight(5);
            }
        }

        return row;
    }

    private Table createPlayerCard(Player p, String targetPos) {
        Table card = new Table();
        card.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        card.pad(8);

        if (p == null) {
            Label empty = new Label("[ VAZIO ]", game.skin, "font-label", StyleFactory.SOFT_YELLOW);
            empty.setFontScale(0.8f);
            card.add(empty).center().row();
            return card;
        }

        // Nome do jogador
        Label nameLabel = new Label(p.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLabel.setFontScale(0.85f);
        card.add(nameLabel).center().padBottom(3).row();

        // Overall
        Label ovrLabel = new Label("OVR: " + p.getOverall(), game.skin, "font-label", Color.WHITE);
        ovrLabel.setFontScale(0.75f);
        card.add(ovrLabel).center().padBottom(3).row();

        // Efetivo (com penalty de fadiga)
        int effective = p.getEffectiveOverallForPosition(targetPos);
        Label effLabel = new Label("EFT: " + effective, game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        effLabel.setFontScale(0.75f);
        if (effective < p.getOverall()) effLabel.setColor(Color.RED);
        card.add(effLabel).center().padBottom(5).row();

        // Barra de Fadiga
        ProgressBar fatigueBar = new ProgressBar(0, 100, 1, false, game.skin);
        fatigueBar.setValue(p.getFatigue());

        // Cor da barra baseada na fadiga
        if (p.getFatigue() >= 80) {
            fatigueBar.setColor(Color.GREEN);
        } else if (p.getFatigue() >= 50) {
            fatigueBar.setColor(Color.YELLOW);
        } else {
            fatigueBar.setColor(Color.RED);
        }

        card.add(fatigueBar).width(100).height(4).padBottom(2).row();

        // Fadiga percentual
        Label fatigueLabel = new Label(p.getFatigue() + "%", game.skin, "font-label", Color.WHITE);
        fatigueLabel.setFontScale(0.65f);
        card.add(fatigueLabel).center().row();

        // Status (Lesão/Suspensão)
        if (p.isSuspended()) {
            Label suspLabel = new Label("🚫 SUSPENSO", game.skin, "font-label", Color.RED);
            suspLabel.setFontScale(0.7f);
            card.add(suspLabel).center().row();
        } else if (p.isInjured()) {
            Label injLabel = new Label("🏥 LESIONADO", game.skin, "font-label", Color.RED);
            injLabel.setFontScale(0.7f);
            card.add(injLabel).center().row();
        }

        return card;
    }

    private Table createPlayerDetailsRow(Player p) {
        Table row = new Table();

        // Nome
        Label nameLabel = new Label(p.getName(), game.skin, "font-label", StyleFactory.GOLD);
        nameLabel.setFontScale(0.95f);
        row.add(nameLabel).width(150).left();

        // Overall
        Label ovrLabel = new Label("OVR: " + p.getOverall(), game.skin, "font-label", Color.WHITE);
        ovrLabel.setFontScale(0.85f);
        row.add(ovrLabel).width(80).center();

        // Efetivo
        Label effLabel = new Label("EFT: " + p.getEffectiveOverall(), game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        effLabel.setFontScale(0.85f);
        row.add(effLabel).width(80).center();

        // Fadiga (com barra visual)
        Table fatigueCell = new Table();
        ProgressBar fatBar = new ProgressBar(0, 100, 1, false, game.skin);
        fatBar.setValue(p.getFatigue());
        if (p.getFatigue() >= 80) fatBar.setColor(Color.GREEN);
        else if (p.getFatigue() >= 50) fatBar.setColor(Color.YELLOW);
        else fatBar.setColor(Color.RED);

        fatigueCell.add(fatBar).width(80).height(3).padRight(5);
        fatigueCell.add(new Label(p.getFatigue() + "%", game.skin, "font-label", Color.WHITE)).width(40);
        row.add(fatigueCell).width(130).center();

        // Status
        String status = "✓ OK";
        Color statusColor = Color.GREEN;
        if (p.isSuspended()) {
            status = "🚫 SUSPENSO (" + p.getSuspendedMatches() + ")";
            statusColor = Color.RED;
        } else if (p.isInjured()) {
            status = "🏥 LESIONADO (" + p.getInjuredMatches() + ")";
            statusColor = Color.RED;
        }

        Label statusLabel = new Label(status, game.skin, "font-label", statusColor);
        statusLabel.setFontScale(0.85f);
        row.add(statusLabel).width(180).left();

        return row;
    }

    private int getPositionWeight(String pos) {
        if (pos.equalsIgnoreCase("GK")) return 1;
        if (pos.matches("CB|RB|LB|RWB|LWB")) return 2;
        if (pos.matches("CDM|CM|CAM|RM|LM")) return 3;
        if (pos.matches("RW|LW|CF|ST")) return 4;
        return 5;
    }
}

