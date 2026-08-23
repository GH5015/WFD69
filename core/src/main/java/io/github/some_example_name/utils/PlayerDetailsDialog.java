package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TechnicalAttributes;

import java.util.Locale;

/** Perfil completo de atleta reutilizável em telas de elenco e negociações. */
public class PlayerDetailsDialog extends Dialog {
    private final Skin skin;
    private final Player player;
    private final int currentYear;

    public PlayerDetailsDialog(Skin skin, Player player, int currentYear) {
        super("", skin);
        this.skin = skin;
        this.player = player;
        this.currentYear = currentYear;
        setModal(true);
        setMovable(false);
        buildUI();
    }

    private void buildUI() {
        Table content = getContentTable();
        content.background(StyleFactory.createMetallicBoard(1040, 760, Color.valueOf("141A16")));
        content.pad(18f, 24f, 14f, 24f);
        content.add(ScreenUI.createSectionTitle(skin, "PERFIL DO JOGADOR")).center().padBottom(5f).row();
        content.add(ScreenUI.createBoldValue(skin, player.getName(), Color.WHITE, Align.center)).center().padBottom(2f).row();
        content.add(ScreenUI.createSubtitle(skin, player.getNationality() + " • " + player.getAge() + " anos • " + String.format(Locale.US, "%.2fm", player.getHeight())))
            .center().padBottom(12f).row();

        Table overview = new Table();
        overview.add(ScreenUI.createStatusBox(skin, "POSIÇÃO", player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(skin, "OVERALL", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(skin, "POTENCIAL", letterGrade(player.getPotential()), gradeColor(player.getPotential()))).width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(skin, "NOTA MÉDIA", averageRating(), averageColor())).width(220f).height(62f);
        content.add(overview).width(910f).padBottom(10f).row();

        Table columns = new Table();
        columns.add(createStatsPanel()).width(440f).height(265f).padRight(10f).top();
        columns.add(createContractAndConditionPanel()).width(460f).height(265f).top();
        content.add(columns).width(910f).padBottom(10f).row();

        Table attributes = ScreenUI.createSubtlePanel();
        attributes.add(ScreenUI.createSectionTitle(skin, "ATRIBUTOS")).colspan(3).left().padBottom(5f).row();
        TechnicalAttributes values = player.getTechnicalAttributes();
        addAttributeRow(attributes, "ATAQUE", values.getAtaque(), "PASSE", values.getPasse(), "DRIBLE", values.getDrible());
        addAttributeRow(attributes, "FÍSICO", values.getFisico(), "DEFESA", values.getDefesa(), "GOLEIRO", values.getGoleiro());
        content.add(attributes).width(910f).height(112f).padBottom(6f).row();

        TextButton close = ScreenUI.createPrimaryButton(skin, "FECHAR");
        close.getLabel().setFontScale(0.55f);
        button(close, true);
    }

    private Table createStatsPanel() {
        Table panel = ScreenUI.createSubtlePanel();
        panel.add(ScreenUI.createSectionTitle(skin, "ESTATÍSTICAS DA TEMPORADA")).colspan(2).left().padBottom(8f).row();
        addData(panel, "JOGOS", String.valueOf(player.getSeasonAppearances()), Color.WHITE);
        addData(panel, "GOLS", String.valueOf(player.getSeasonGoals()), StyleFactory.SOFT_YELLOW);
        addData(panel, "ASSISTÊNCIAS", String.valueOf(player.getSeasonAssists()), ScreenUI.SUCCESS);
        addData(panel, "CLEAN SHEETS", String.valueOf(player.getSeasonCleanSheets()), ScreenUI.SUCCESS);
        addData(panel, "CARTÕES", player.getSeasonYellowCards() + " A  •  " + player.getSeasonRedCards() + " V", player.getSeasonRedCards() > 0 ? ScreenUI.DANGER : StyleFactory.SOFT_YELLOW);
        addData(panel, "NOTA MÉDIA", averageRating(), averageColor());
        return panel;
    }

    private Table createContractAndConditionPanel() {
        Table column = new Table();
        Table contract = ScreenUI.createSubtlePanel();
        contract.add(ScreenUI.createSectionTitle(skin, "CONTRATO")).colspan(2).left().padBottom(7f).row();
        addData(contract, "SALÁRIO ANUAL", String.format(Locale.US, "WFL$ %.2fM", player.getAnnualSalary() / 1_000_000.0), StyleFactory.SOFT_YELLOW);
        addData(contract, "TÉRMINO", String.valueOf(player.getContractEndYear()), Color.WHITE);
        int remaining = player.getRemainingContractYears(currentYear);
        addData(contract, "RESTANTE", remaining == 0 ? "EXPIRADO" : remaining + " ano" + (remaining > 1 ? "s" : ""), remaining == 0 ? ScreenUI.DANGER : remaining == 1 ? ScreenUI.WARNING : ScreenUI.SUCCESS);
        addData(contract, "NEGOCIAÇÃO", player.canNegotiateContract(currentYear) ? "DISPONÍVEL" : "BLOQUEADA", player.canNegotiateContract(currentYear) ? ScreenUI.SUCCESS : ScreenUI.WARNING);
        column.add(contract).growX().height(145f).padBottom(8f).row();

        Table condition = ScreenUI.createSubtlePanel();
        condition.add(ScreenUI.createSectionTitle(skin, "CONDIÇÃO ATUAL")).colspan(2).left().padBottom(7f).row();
        addData(condition, "MORAL", player.getMorale() + "/100", player.getMorale() >= 75 ? ScreenUI.SUCCESS : player.getMorale() >= 45 ? ScreenUI.WARNING : ScreenUI.DANGER);
        addData(condition, "FADIGA", player.getFatigue() + "%", player.getFatigue() >= 80 ? ScreenUI.SUCCESS : player.getFatigue() >= 50 ? ScreenUI.WARNING : ScreenUI.DANGER);
        String status = player.isInjured() ? "LESIONADO • " + player.getInjuryDuration() + "J" : player.isSuspended() ? "SUSPENSO • " + player.getSuspendedMatches() + "J" : "DISPONÍVEL";
        addData(condition, "STATUS", status, player.canPlay() ? ScreenUI.SUCCESS : ScreenUI.DANGER);
        column.add(condition).growX().height(112f);
        return column;
    }

    private void addAttributeRow(Table table, String firstName, int firstValue, String secondName, int secondValue, String thirdName, int thirdValue) {
        table.add(attribute(firstName, firstValue)).growX().uniformX().padRight(7f);
        table.add(attribute(secondName, secondValue)).growX().uniformX().padRight(7f);
        table.add(attribute(thirdName, thirdValue)).growX().uniformX().row();
    }

    private Table attribute(String title, int value) {
        Table item = new Table();
        item.add(ScreenUI.createSubtitle(skin, title)).left().expandX();
        item.add(ScreenUI.createBoldValue(skin, value + " • " + letterGrade(value), gradeColor(value), Align.right));
        return item;
    }

    private void addData(Table table, String label, String value, Color color) {
        table.add(ScreenUI.createSubtitle(skin, label)).left().expandX().padBottom(5f);
        table.add(ScreenUI.createBoldValue(skin, value, color, Align.right)).right().padBottom(5f).row();
    }

    private String averageRating() {
        return player.getSeasonRatingMatches() == 0 ? "—" : String.format(Locale.US, "%.1f", player.getSeasonAverageRating());
    }

    private Color averageColor() {
        double rating = player.getSeasonAverageRating();
        return rating >= 7.0 ? ScreenUI.SUCCESS : rating >= 6.0 ? StyleFactory.SOFT_YELLOW : ScreenUI.DANGER;
    }

    private Color gradeColor(int value) {
        return value >= 85 ? ScreenUI.SUCCESS : value >= 70 ? StyleFactory.SOFT_YELLOW : value >= 60 ? ScreenUI.WARNING : ScreenUI.DANGER;
    }

    private String letterGrade(int value) {
        if (value >= 90) return "A+";
        if (value >= 85) return "A";
        if (value >= 80) return "A-";
        if (value >= 77) return "B+";
        if (value >= 73) return "B";
        if (value >= 70) return "B-";
        if (value >= 67) return "C+";
        if (value >= 63) return "C";
        if (value >= 60) return "C-";
        if (value >= 57) return "D+";
        if (value >= 53) return "D";
        if (value >= 50) return "D-";
        return "F";
    }
}
