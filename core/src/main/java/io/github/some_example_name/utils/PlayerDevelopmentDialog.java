package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DevelopmentFocus;
import io.github.some_example_name.model.Player;

import java.util.List;
import java.util.Map;

/** Tela de acompanhamento e escolha de foco individual do atleta. */
public class PlayerDevelopmentDialog extends Dialog {
    private final Skin skin;
    private final Player player;
    private final Club club;
    private final Table body = new Table();
    private boolean showHistory;

    public PlayerDevelopmentDialog(Skin skin, Player player, Club club) {
        super("", skin);
        this.skin = skin;
        this.player = player;
        this.club = club;
        setModal(true);
        setMovable(false);
        buildUI();
    }

    private void buildUI() {
        Table content = getContentTable();
        content.clear();
        content.background(StyleFactory.createMetallicBoard(1240, 825, Color.valueOf("141A16")));
        content.pad(16f, 22f, 12f, 22f);

        content.add(ScreenUI.createSectionTitle(skin, "DESENVOLVIMENTO DO ATLETA"))
            .center().padBottom(2f).row();
        Label clubName = ScreenUI.createSubtitle(skin, club.getName().toUpperCase());
        clubName.setColor(StyleFactory.SOFT_YELLOW);
        content.add(clubName).center().padBottom(10f).row();

        Table tabs = new Table();
        tabs.add(createTab("DESENVOLVIMENTO", false)).width(220f).height(38f).padRight(5f);
        tabs.add(createTab("HISTÓRICO", true)).width(170f).height(38f);
        content.add(tabs).center().padBottom(10f).row();

        content.add(body).width(1160f).height(640f).top().row();

        TextButton close = ScreenUI.createPrimaryButton(skin, "VOLTAR");
        close.getLabel().setFontScale(0.55f);
        button(close, true);
        refreshBody();
    }

    private TextButton createTab(String label, boolean historyTab) {
        TextButton tab = ScreenUI.createInteractiveButton(label, skin, "toggle");
        tab.getLabel().setFontScale(0.52f);
        boolean active = showHistory == historyTab;
        tab.setChecked(active);
        tab.setColor(active ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        tab.getLabel().setColor(active ? Color.BLACK : Color.WHITE);
        tab.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showHistory = historyTab;
                buildUI();
            }
        });
        return tab;
    }

    private void refreshBody() {
        body.clear();
        if (showHistory) {
            body.add(createHistoryPanel()).grow();
        } else {
            body.add(createDevelopmentPanel()).grow();
        }
    }

    private Table createDevelopmentPanel() {
        Table root = new Table();
        root.top();

        Table identity = ScreenUI.createPanel();
        Table playerInfo = new Table();
        Label name = new Label(player.getName().toUpperCase(), skin, "font-bold");
        name.setFontScale(0.82f);
        name.setColor(StyleFactory.GOLD);
        playerInfo.add(name).left().row();
        Label details = ScreenUI.createSubtitle(skin, player.getPosition() + "  •  " + player.getAge() + " ANOS  •  " + player.getDevelopmentCurve().getLabel().toUpperCase());
        playerInfo.add(details).left().padTop(3f);
        identity.add(playerInfo).left().expandX();
        identity.add(ScreenUI.createStatusBox(skin, "OVR ATUAL", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).width(175f).height(54f).padRight(8f);
        identity.add(ScreenUI.createStatusBox(skin, "POTENCIAL", String.valueOf(player.getPotential()), ScreenUI.SUCCESS)).width(175f).height(54f).padRight(8f);
        identity.add(ScreenUI.createStatusBox(skin, "DESENVOLVIMENTO", player.getDevelopmentPercent() + "%", progressColor())).width(190f).height(54f);
        root.add(identity).growX().height(78f).padBottom(9f).row();

        Table progress = ScreenUI.createSubtlePanel();
        progress.add(ScreenUI.createSubtitle(skin, "CAMINHO ATÉ O POTENCIAL")).left().padRight(12f);
        progress.add(ScreenUI.createBlockProgress(skin, player.getDevelopmentPercent(), 18, progressColor())).growX().height(16f);
        root.add(progress).growX().height(42f).padBottom(9f).row();

        Table columns = new Table();
        columns.add(createAttributesPanel()).width(570f).height(240f).top().padRight(9f);
        columns.add(createEvolutionPanel()).width(580f).height(240f).top();
        root.add(columns).growX().height(240f).padBottom(9f).row();

        root.add(createFocusPanel()).growX().height(145f).padBottom(9f).row();
        root.add(createProjectionPanel()).growX().height(125f);
        return root;
    }

    private Table createAttributesPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(skin, "ATRIBUTOS")).left().padBottom(10f).row();
        addAttribute(panel, "ATAQUE", player.getAttackStat(), player.getLastAttackDevelopment());
        addAttribute(panel, "PASSE", player.getPassStat(), player.getLastPassingDevelopment());
        addAttribute(panel, "DEFESA", player.getDefenseStat(), player.getLastDefenseDevelopment());
        addAttribute(panel, "FÍSICO", player.getPhysicalStat(), player.getLastPhysicalDevelopment());
        addAttribute(panel, "DRIBLE", player.getDribbleStat(), player.getLastDribblingDevelopment());
        return panel;
    }

    private void addAttribute(Table panel, String label, int value, int change) {
        panel.add(ScreenUI.createSubtitle(skin, label)).left().expandX().padBottom(6f);
        panel.add(ScreenUI.createBoldValue(skin, String.valueOf(value), StyleFactory.CREME_AGED, Align.right)).width(50f).padBottom(6f);
        String delta = change > 0 ? "▲ +" + change : "—";
        Color color = change > 0 ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT;
        panel.add(ScreenUI.createBoldValue(skin, delta, color, Align.right)).width(70f).padLeft(8f).padBottom(6f).row();
    }

    private Table createEvolutionPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(skin, "EVOLUÇÃO")).left().padBottom(10f).row();
        panel.add(ScreenUI.createSubtitle(skin, "ÚLTIMAS 5 SEMANAS")).left().padBottom(10f).row();
        panel.add(ScreenUI.createSubtitle(skin, "OVR")).left().row();

        List<Integer> history = player.getOverallDevelopmentHistory();
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) values.append("  →  ");
            values.append(history.get(i));
        }
        if (values.length() == 0) values.append(player.getOverall());
        Label sequence = new Label(values.toString(), skin, "font-bold");
        sequence.setFontScale(0.66f);
        sequence.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(sequence).left().padTop(4f).row();

        panel.add(ScreenUI.createSubtitle(skin, "RITMO DE EVOLUÇÃO: " + developmentPace())).left().padTop(22f).row();
        panel.add(ScreenUI.createSubtitle(skin, "FOCO: " + player.getDevelopmentFocus().getLabel().toUpperCase())).left().padTop(5f);
        return panel;
    }

    private Table createFocusPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(skin, "FOCO DE DESENVOLVIMENTO")).left().expandX();
        Label active = ScreenUI.createBoldValue(skin, "FOCO ATUAL: " + player.getDevelopmentFocus().getLabel().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.right);
        active.setFontScale(0.50f);
        panel.add(active).right().row();

        Table options = new Table();
        for (DevelopmentFocus focus : DevelopmentFocus.values()) {
            options.add(createFocusButton(focus)).width(170f).height(38f).pad(9f, 6f, 0f, 0f);
        }
        panel.add(options).colspan(2).left().padTop(4f);
        return panel;
    }

    private TextButton createFocusButton(final DevelopmentFocus focus) {
        TextButton button = ScreenUI.createInteractiveButton((player.getDevelopmentFocus() == focus ? "● " : "○ ") + focus.getLabel(), skin, "toggle");
        button.getLabel().setFontScale(0.50f);
        boolean selected = player.getDevelopmentFocus() == focus;
        button.setChecked(selected);
        button.setColor(selected ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        button.getLabel().setColor(selected ? Color.BLACK : Color.WHITE);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                player.setDevelopmentFocus(focus);
                refreshBody();
            }
        });
        return button;
    }

    private Table createProjectionPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(skin, "PROJEÇÃO")).left().padBottom(7f).row();
        int potential = player.getPotential();
        addProjection(panel, "POTENCIAL ESTIMADO", String.valueOf(potential));
        addProjection(panel, "FAIXA PROVÁVEL DE PICO", Math.max(player.getOverall(), potential - 3) + "–" + potential);
        addProjection(panel, "IDADE PROVÁVEL DO AUGE", peakAgeRange());
        addProjection(panel, "SITUAÇÃO", projectionStatus());
        return panel;
    }

    private Table createHistoryPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(skin, "HISTÓRICO DE DESENVOLVIMENTO")).left().padBottom(12f).row();
        panel.add(ScreenUI.createSubtitle(skin, "EVOLUÇÃO ANUAL DO OVERALL")).left().padBottom(8f).row();

        Map<Integer, Integer> history = player.getYearlyDevelopmentHistory();
        if (history.isEmpty()) {
            panel.add(ScreenUI.createSubtitle(skin, "O primeiro registro será criado na próxima atualização semanal.")).left().pad(20f);
        } else {
            for (Map.Entry<Integer, Integer> entry : history.entrySet()) {
                Table row = ScreenUI.createRow(0);
                row.add(ScreenUI.createBoldValue(skin, String.valueOf(entry.getKey()), StyleFactory.CREME_AGED, Align.left)).left().expandX().padLeft(12f);
                row.add(ScreenUI.createBoldValue(skin, "OVR " + entry.getValue(), StyleFactory.SOFT_YELLOW, Align.right)).right().padRight(12f);
                panel.add(row).width(700f).height(44f).padBottom(4f).row();
            }
        }
        return panel;
    }

    private void addProjection(Table table, String label, String value) {
        table.add(ScreenUI.createSubtitle(skin, label)).left().expandX().padBottom(4f);
        table.add(ScreenUI.createBoldValue(skin, value, StyleFactory.CREME_AGED, Align.right)).right().padBottom(4f).row();
    }

    private Color progressColor() {
        int percent = player.getDevelopmentPercent();
        return percent >= 80 ? ScreenUI.SUCCESS : percent >= 55 ? StyleFactory.SOFT_YELLOW : ScreenUI.WARNING;
    }

    private String developmentPace() {
        int gap = player.getTruePotential() - player.getOverall();
        if (player.getAge() <= 23 && gap >= 8) return "ALTO";
        if (player.getAge() <= 27 && gap >= 4) return "MODERADO";
        if (player.getAge() >= 31) return "BAIXO";
        return "ESTÁVEL";
    }

    private String peakAgeRange() {
        switch (player.getDevelopmentCurve()) {
            case EARLY: return "21–25";
            case LATE: return "25–29";
            default: return "23–28";
        }
    }

    private String projectionStatus() {
        if (player.getTruePotential() >= 90 && player.getAge() <= 24) return "FUTURA ESTRELA";
        if (player.getTruePotential() >= 84) return "ALTO POTENCIAL";
        if (player.getAge() >= 31) return "VETERANO EXPERIENTE";
        return "EM DESENVOLVIMENTO";
    }
}
