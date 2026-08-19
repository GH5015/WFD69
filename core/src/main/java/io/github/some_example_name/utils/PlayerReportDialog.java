package io.github.some_example_name.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.utils.StyleFactory;

public class PlayerReportDialog extends Dialog {
    private final ScoutTarget target;
    private final DraftScoutManager scoutManager;
    private final Runnable onRemoveCallback;

    public PlayerReportDialog(Skin skin, ScoutTarget target, DraftScoutManager scoutManager, Runnable onRemoveCallback) {
        super("", skin);
        this.target = target;
        this.scoutManager = scoutManager;
        this.onRemoveCallback = onRemoveCallback;

        buildUI(skin);
    }

    private void buildUI(Skin skin) {
        Table content = getContentTable();
        content.pad(16, 24, 16, 24);

        Player p = target.getPlayer();

        // --- SEÇÃO 1: CABEÇALHO DO JOGADOR ---
        Label nameLabel = new Label(p.getName().toUpperCase(), skin, "font-title");
        nameLabel.setFontScale(0.9f);
        nameLabel.setColor(StyleFactory.GOLD);
        content.add(nameLabel).colspan(2).center().padBottom(6).row();

        // Subtítulo: Nacionalidade, Idade, Posição e Altura
        String subInfo = String.format("%s  •  %d anos  •  %s  •  %.2fm",
                p.getNationality(), p.getAge(), p.getPrimaryPosition().name(), p.getHeight());
        Label infoLabel = new Label(subInfo, skin);
        infoLabel.setColor(StyleFactory.CREME_AGED);
        content.add(infoLabel).colspan(2).center().padBottom(14).row();

        // Barra de Progresso do Scout
        Table knowTable = new Table();
        knowTable.add(new Label("CONHECIMENTO DO SCOUT", skin, "font-bold")).left().padBottom(4).row();

        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue((float) target.getKnowledgePercentage());

        Table progressContainer = new Table();
        progressContainer.add(progressBar).width(200).height(16).padRight(10);
        
        Label pctLabel = new Label(String.format("%.0f%%", target.getKnowledgePercentage()), skin, "font-bold");
        pctLabel.setColor(target.isFullyScouted() ? Color.GREEN : StyleFactory.SOFT_YELLOW);
        progressContainer.add(pctLabel);

        knowTable.add(progressContainer).row();
        content.add(knowTable).colspan(2).center().padBottom(14).row();

        content.add(createDivider()).colspan(2).growX().padBottom(12).row();

        // --- SEÇÃO 2: ATRIBUTOS E NOTAS (OVR, POT, STATS) ---
        Table statsTable = new Table();
        statsTable.defaults().left().padVertical(3);

        addStatRow(statsTable, "OVR", target.getDisplayOverall(), skin, true);
        addStatRow(statsTable, "Potencial", target.getDisplayPotential(), skin, true);
        
        // Espaçador interno
        statsTable.add().height(8).row();

        addStatRow(statsTable, "Ataque", target.getAttributeDisplay(p.getAttackStat()), skin, false);
        addStatRow(statsTable, "Passe", target.getAttributeDisplay(p.getPassStat()), skin, false);
        addStatRow(statsTable, "Drible", target.getAttributeDisplay(p.getDribbleStat()), skin, false);
        addStatRow(statsTable, "Físico", target.getAttributeDisplay(p.getPhysicalStat()), skin, false);
        addStatRow(statsTable, "Defesa", target.getAttributeDisplay(p.getDefenseStat()), skin, false);

        content.add(statsTable).colspan(2).growX().padBottom(12).row();

        content.add(createDivider()).colspan(2).growX().padBottom(12).row();

        // --- SEÇÃO 3: PROJEÇÃO DO ATLETA ---
        Table projectionTable = new Table();
        projectionTable.add(new Label("PROJEÇÃO", skin, "font-bold")).left().padBottom(4).row();

        Label quoteLabel = new Label("\"" + getScoutProjection(p) + "\"", skin);
        quoteLabel.setWrap(true);
        quoteLabel.setAlignment(Align.center);
        quoteLabel.setColor(StyleFactory.CREME_AGED);
        projectionTable.add(quoteLabel).width(320).center().row();

        content.add(projectionTable).colspan(2).center().padBottom(12).row();

        content.add(createDivider()).colspan(2).growX().padBottom(12).row();

        // --- SEÇÃO 4: ÚLTIMA OBSERVAÇÃO ---
        Table statusTable = new Table();
        statusTable.add(new Label("ÚLTIMA OBSERVAÇÃO", skin, "font-bold")).left().padBottom(2).row();
        
        Label lastSeenLabel = new Label("Há 2 dias", skin);
        lastSeenLabel.setColor(Color.LIGHT_GRAY);
        statusTable.add(lastSeenLabel).left().padBottom(4).row();

        Label nextUpdateLabel = new Label("Próxima atualização: amanhã", skin);
        nextUpdateLabel.setColor(StyleFactory.SOFT_YELLOW);
        statusTable.add(nextUpdateLabel).left().row();

        content.add(statusTable).colspan(2).left().padBottom(16).row();

        // --- BOTÕES DE AÇÃO ---
        TextButton removeBtn = new TextButton("REMOVER DO SCOUTING", skin);
        removeBtn.getLabel().setColor(Color.valueOf("FF4D4D"));
        button(removeBtn, true);
        button("FECHAR", false);
        padBottom(12);
    }

    private void addStatRow(Table table, String labelName, String valueDisplay, Skin skin, boolean isBold) {
        Label nameLabel = new Label(labelName, skin, isBold ? "font-bold" : "default");
        table.add(nameLabel).expandX();

        Label valLabel = new Label(valueDisplay, skin, "font-bold");
        valLabel.setColor(getGradeColor(valueDisplay));
        table.add(valLabel).right().row();
    }

    private Table createDivider() {
        Table divider = new Table();
        divider.background(StyleFactory.createSolid(new Color(1f, 1f, 1f, 0.15f)));
        divider.setHeight(1);
        return divider;
    }

    private String getScoutProjection(Player player) {
        if (target.getKnowledgePercentage() < 30.0) {
            return "Dados insuficientes para determinar o teto do jogador.";
        }
        int pot = player.getPotential();
        if (pot >= 88) {
            return "Potencial para se tornar um astro de classe mundial e pilar da franquia.";
        } else if (pot >= 82) {
            return "Potencial para se tornar titular em uma franquia de alto nível.";
        } else if (pot >= 75) {
            return "Promessa sólida para rotação de elenco e composição técnica.";
        } else {
            return "Teto limitado; utilidade restrita a composições emergenciais.";
        }
    }

    private Color getGradeColor(String gradeDisplay) {
        if (gradeDisplay.equals("?")) return Color.GRAY;
        char baseGrade = gradeDisplay.charAt(0);
        switch (baseGrade) {
            case 'A': return Color.GREEN;
            case 'B': return StyleFactory.SOFT_YELLOW;
            case 'C': return Color.ORANGE;
            case 'D': case 'F': return Color.valueOf("FF4D4D");
            default: return StyleFactory.CREME_AGED;
        }
    }

    @Override
    protected void result(Object object) {
        if (Boolean.TRUE.equals(object)) {
            scoutManager.removeTarget(target);
            onRemoveCallback.run();
        }
    }
}
