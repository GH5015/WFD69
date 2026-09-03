package io.github.some_example_name.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import java.util.*;
import java.util.List;

/** Montagem manual e reversível do elenco, antes de confirmar qualquer transferência. */
final class ExpansionRosterDialog {
    private final Stage stage;
    private final Main game;
    private final Club club;
    private final Runnable completed;
    private final Set<Player> selected = new LinkedHashSet<>();
    private final List<Player> candidates;
    private final int required;
    private final Table rows = new Table();
    private final Dialog dialog;
    private final Label summary, error;
    private final TextField search;
    private final SelectBox<String> filter;
    private final TextButton confirm;

    static void show(Stage stage, Main game, Club club, Runnable completed) {
        Dialog dialog = new ExpansionRosterDialog(stage, game, club, completed).dialog;
        dialog.show(stage);
        stage.setScrollFocus(dialog.findActor("expansion-roster-scroll"));
    }

    private ExpansionRosterDialog(Stage stage, Main game, Club club, Runnable completed) {
        this.stage = stage; this.game = game; this.club = club; this.completed = completed;
        candidates = LeagueExpansionService.availablePlayers(game.league);
        required = LeagueExpansionService.requiredSelections(club);
        dialog = new Dialog("", game.skin);
        dialog.getTitleTable().clear();
        dialog.setBackground(StyleFactory.createRoundedPanel(Color.valueOf("091D14"), StyleFactory.GOLD));
        dialog.setName("expansion-roster-dialog");
        Table content = dialog.getContentTable(); content.pad(18);
        content.add(ScreenUI.createSectionTitle(game.skin, "WFL EXPANSION • ESCOLHA SEU ELENCO"))
            .left().padBottom(14).row();
        Label intro = new Label(club.getName() + " • Escolha " + required + " jogadores desprotegidos.\n"
            + "Máximo de " + LeagueExpansionService.MAX_LOSSES_PER_CLUB + " atletas por clube de origem; contratos e salários são mantidos.\n"
            + "Suas escolhas são reservadas primeiro. Depois, a IA monta a outra franquia.", game.skin);
        intro.setFontScale(.68f); intro.setWrap(true);
        content.add(intro).width(1150).padBottom(12).row();
        Table controls = new Table();
        search = new TextField("", game.skin); search.setMessageText("Buscar jogador ou clube");
        filter = ScreenUI.createSelectBox(game.skin);
        filter.setItems("TODOS", "SELECIONADOS", "GK", "DEF", "MEI", "ATA");
        controls.add(search).width(590).height(42).padRight(10);
        controls.add(filter).width(250).height(42).padRight(10);
        TextButton suggest = ScreenUI.createSecondaryButton(game.skin, "SUGERIR ELENCO");
        suggest.getLabel().setFontScale(.55f);
        controls.add(suggest).width(290).height(42);
        content.add(controls).padBottom(12).row();
        summary = new Label("", game.skin); summary.setFontScale(.7f);
        content.add(summary).left().padBottom(8).row();
        ScrollPane scroll = new ScrollPane(rows, game.skin);
        scroll.setName("expansion-roster-scroll");
        scroll.setScrollingDisabled(true, false); scroll.setFadeScrollBars(false);
        content.add(scroll).width(1150).height(Math.min(480, stage.getHeight() - 400)).row();
        error = new Label("", game.skin); error.setFontScale(.62f); error.setWrap(true); error.setColor(ScreenUI.WARNING);
        content.add(error).width(1150).height(60).row();
        confirm = ScreenUI.createPrimaryButton(game.skin, "CONFIRMAR ELENCO E CONCLUIR EXPANSÃO");
        confirm.setName("confirm-expansion-roster"); confirm.getLabel().setFontScale(.6f);
        dialog.getButtonTable().add(confirm).width(640).height(50).padRight(15);
        dialog.button("VOLTAR");
        ChangeListener changed = new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { refresh(); }
        };
        search.addListener(changed); filter.addListener(changed);
        suggest.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                try {
                    List<Player> suggestions = LeagueExpansionService.suggestedSelections(game.league, club);
                    selected.clear(); selected.addAll(suggestions); error.setText(""); refresh();
                } catch (IllegalArgumentException failure) { error.setText(failure.getMessage()); }
            }
        });
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (confirm.isDisabled()) return;
                try {
                    List<String> log = LeagueExpansionService.runDraft(game.league, club, Collections.emptyList(), selected);
                    dialog.hide(); completed.run();
                    Dialog report = new Dialog("WFL EXPANSION CONCLUÍDO", game.skin);
                    Label text = new Label(String.join("\n", log), game.skin); text.setFontScale(.65f); text.setWrap(true);
                    Table body = new Table(); body.add(text).width(1000).pad(14);
                    ScrollPane reportScroll = new ScrollPane(body, game.skin); reportScroll.setScrollingDisabled(true, false);
                    report.getContentTable().add(reportScroll).width(1040).height(460);
                    report.button("CONTINUAR OFF SEASON"); report.show(stage);
                } catch (IllegalArgumentException failure) { error.setText(failure.getMessage()); }
            }
        });
        refresh();
    }

    private void refresh() {
        rows.clear(); rows.top();
        summary.setText("SELECIONADOS: " + selected.size() + "/" + required + "     FOLHA PROJETADA: "
            + money(payroll()) + "     HARD CAP: " + money(club.getFinance().getHardCap()));
        confirm.setDisabled(selected.size() != required);
        String query = search.getText().trim().toLowerCase(Locale.ROOT);
        for (Player p : candidates) {
            Club source = p.getCurrentClub();
            if (source == null || !(p.getName() + " " + source.getName()).toLowerCase(Locale.ROOT).contains(query)) continue;
            String f = filter.getSelected();
            if ("SELECIONADOS".equals(f) && !selected.contains(p)) continue;
            if (!"TODOS".equals(f) && !"SELECIONADOS".equals(f) && !f.equals(group(p))) continue;
            Table row = new Table();
            TextButton toggle = ScreenUI.createSecondaryButton(game.skin, selected.contains(p) ? "REMOVER" : "ESCOLHER");
            toggle.getLabel().setFontScale(.52f);
            toggle.setName("expansion-player-" + p.getId());
            toggle.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { toggle(p); }
            });
            row.add(toggle).width(150).height(50).padRight(10);
            Label name = new Label(p.getName() + " • " + source.getName(), game.skin, "font-bold");
            name.setFontScale(.58f); name.setEllipsis(true);
            name.setColor(selected.contains(p) ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_PRIMARY);
            row.add(name).width(440).left().padRight(8);
            Label details = new Label(p.getPrimaryPosition() + "  |  " + p.getAge() + " anos  |  OVR " + p.getOverall()
                + "  |  " + money(p.getAnnualSalary()) + "/ano  |  até " + p.getContractEndYear(), game.skin);
            details.setFontScale(.53f);
            row.add(details).width(510).left();
            rows.add(row).growX().padBottom(4).row();
        }
    }

    private void toggle(Player player) {
        error.setText("");
        if (selected.remove(player)) { refresh(); return; }
        if (selected.size() >= required) { error.setText("Remova um atleta antes de escolher outro."); return; }
        long fromClub = selected.stream().filter(p -> p.getCurrentClub() == player.getCurrentClub()).count();
        if (fromClub >= LeagueExpansionService.MAX_LOSSES_PER_CLUB) {
            error.setText("Máximo de " + LeagueExpansionService.MAX_LOSSES_PER_CLUB + " atletas do " + player.getCurrentClub().getName() + "."); return;
        }
        if (payroll() + player.getAnnualSalary() > club.getFinance().getHardCap()) {
            error.setText("Esta escolha ultrapassaria o Hard Cap. Revise os salários selecionados."); return;
        }
        selected.add(player); refresh();
    }
    private long payroll() { return club.getFinance().getAnnualPayroll() + selected.stream().mapToLong(Player::getAnnualSalary).sum(); }
    private String money(long value) { return String.format(Locale.US, "W$ %.2fM", value / 1_000_000d); }
    private String group(Player p) {
        String position = p.getPrimaryPosition().name();
        return "GK".equals(position) ? "GK" : position.endsWith("B") || "SW".equals(position) ? "DEF"
            : position.contains("M") ? "MEI" : "ATA";
    }
}
