package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import java.util.*;
import java.util.List;

/** Cerimônia de expansão em turnos, com escolhas manuais da franquia do usuário. */
final class ExpansionRosterDialog {
    private final Main game;
    private final Club user;
    private final Runnable completed;
    private final LeagueExpansionService.DraftSession session;
    private final Dialog dialog;
    private final Table sources = new Table(), rows = new Table(), details = new Table(), roster = new Table();
    private final Label status, error;
    private final SelectBox<String> filter, sort;
    private final TextField search;
    private final TextButton choose, next, simulate;
    private Player selected;
    private final Map<String, com.badlogic.gdx.graphics.Texture> logos = new HashMap<>();

    static void show(Stage stage, Main game, Club club, Runnable completed) {
        show(stage, game, club, Collections.emptyList(), completed);
    }
    static void show(Stage stage, Main game, Club club, Collection<Player> protectedPlayers, Runnable completed) {
        new ExpansionRosterDialog(stage, game, club, protectedPlayers, completed);
    }
    private ExpansionRosterDialog(Stage stage, Main game, Club club, Collection<Player> protectedPlayers, Runnable completed) {
        this.game = game; user = club; this.completed = completed;
        session = LeagueExpansionService.beginSession(game.league, club, protectedPlayers);
        dialog = new Dialog("", game.skin) {
            @Override public boolean remove() {
                boolean removed = super.remove();
                if (removed) { for (com.badlogic.gdx.graphics.Texture t : logos.values()) t.dispose(); logos.clear(); }
                return removed;
            }
        };
        dialog.setName("expansion-roster-dialog"); dialog.setMovable(false); dialog.setModal(true);
        dialog.setBackground(StyleFactory.createRoundedPanel(Color.valueOf("071610"), StyleFactory.GOLD));
        Table content = dialog.getContentTable(); content.pad(18);
        Table heading = new Table();
        Label title = ScreenUI.createSectionTitle(game.skin, "WFL EXPANSION DRAFT"); title.setFontScale(1.3f);
        heading.add(title).expandX().left();
        heading.add(text(club.getName().toUpperCase(Locale.ROOT), .8f, StyleFactory.SOFT_YELLOW)).right();
        heading.add(crest(club)).size(70).padLeft(14);
        content.add(heading).growX().height(76).row();
        status = text("", .65f, StyleFactory.SOFT_YELLOW);
        content.add(status).growX().height(38).padBottom(10).row();

        Table body = new Table();
        Table left = panel("FRANQUIAS DE ORIGEM");
        left.add(scroll(sources)).grow().row();
        Label rules = text("REGRAS RÁPIDAS\nAté 15 protegidos por clube\nMáximo de 3 saídas por clube\n20 jogadores por nova franquia\nContratos e salários mantidos", .55f, StyleFactory.SOFT_YELLOW);
        rules.setWrap(true); left.add(rules).growX().height(155).padTop(10);

        Table center = panel("JOGADORES DESPROTEGIDOS");
        Table controls = new Table();
        search = new TextField("", game.skin); search.setMessageText("Buscar jogador / clube");
        filter = ScreenUI.createSelectBox(game.skin); filter.setItems("TODOS", "GK", "DEF", "MEI", "ATA");
        sort = ScreenUI.createSelectBox(game.skin); sort.setItems("OVR", "IDADE", "SALÁRIO");
        controls.add(search).growX().minWidth(0).height(38).padRight(6);
        controls.add(filter).width(105).height(38).padRight(6);
        controls.add(sort).width(120).height(38);
        center.add(controls).growX().padBottom(8).row();
        Table columns = new Table();
        addCell(columns, "JOGADOR / CLUBE", 330, StyleFactory.SOFT_YELLOW);
        addCell(columns, "POS", 55, StyleFactory.SOFT_YELLOW);
        addCell(columns, "IDADE", 55, StyleFactory.SOFT_YELLOW);
        addCell(columns, "OVR", 48, StyleFactory.SOFT_YELLOW);
        addCell(columns, "POT", 48, StyleFactory.SOFT_YELLOW);
        addCell(columns, "SALÁRIO", 108, StyleFactory.SOFT_YELLOW);
        center.add(columns).growX().height(28).row();
        ScrollPane available = scroll(rows); available.setName("expansion-roster-scroll");
        center.add(available).grow();

        Table right = new Table();
        Table detailPanel = panel("JOGADOR SELECIONADO"); detailPanel.add(scroll(details)).grow();
        Table rosterPanel = panel("ELENCO DA EXPANSÃO"); rosterPanel.add(scroll(roster)).grow();
        right.add(detailPanel).grow().padBottom(10).row(); right.add(rosterPanel).grow();
        body.add(left).width(325).growY().padRight(12);
        body.add(center).grow().minWidth(700).padRight(12);
        body.add(right).width(450).growY();
        content.add(body).grow().row();
        error = text("", .55f, ScreenUI.WARNING); error.setWrap(true);
        content.add(error).growX().height(36).row();
        Table actions = dialog.getButtonTable(); actions.pad(0, 18, 16, 18);
        TextButton back = button("VOLTAR À OFF SEASON", () -> dialog.hide());
        next = button("REVELAR PRÓXIMA ESCOLHA", () -> act(false)); next.setName("expansion-next-pick");
        simulate = button("SIMULAR ATÉ MINHA VEZ", () -> act(true));
        choose = button("SELECIONAR JOGADOR", () -> {
            try {
                if (session.currentClub() == null) { session.finish(game.league); dialog.hide(); completed.run(); return; }
                session.choose(user, selected); selected = null; error.setText(""); refresh();
            } catch (IllegalArgumentException failure) { error.setText(failure.getMessage()); }
        });
        choose.setName("confirm-expansion-roster");
        actions.add(back).width(260).height(50).padRight(10);
        actions.add(next).width(320).height(50).padRight(10);
        actions.add(simulate).width(285).height(50).padRight(10);
        actions.add(choose).width(310).height(50);
        ChangeListener change = new ChangeListener() { public void changed(ChangeEvent e, Actor a) { refresh(); } };
        search.addListener(change); filter.addListener(change); sort.addListener(change);
        refresh(); dialog.show(stage);
        dialog.setSize(stage.getWidth() - 32, stage.getHeight() - 32);
        dialog.setPosition(16, 16);
    }
    private void act(boolean untilUser) {
        try {
            do {
                if (session.currentClub() == null || session.currentClub() == user) break;
                session.chooseAi();
            } while (untilUser);
            selected = null; error.setText(""); refresh();
        } catch (IllegalArgumentException failure) { error.setText(failure.getMessage()); }
    }
    private void refresh() {
        Club current = session.currentClub();
        Club viewed = session.getClubs().contains(user) ? user : current == null ? session.getClubs().get(0) : current;
        status.setText("OFF SEASON " + game.league.getCurrentSeason() + "  •  " + (current == null ? "TODAS AS ESCOLHAS CONCLUÍDAS"
            : "ESCOLHA #" + (session.getLog().size() + 1) + "  •  VEZ DE " + current.getName().toUpperCase(Locale.ROOT)));
        choose.setText(current == null ? "CONCLUIR EXPANSÃO" : "SELECIONAR JOGADOR");
        choose.setDisabled(current != null && (current != user || selected == null));
        next.setDisabled(current == null || current == user);
        simulate.setDisabled(current == null || current == user);
        sources.clear(); rows.clear(); details.clear(); roster.clear();
        sources.top(); rows.top(); details.top().left(); roster.top().left();
        List<Player> pool = session.available();
        for (Club source : game.league.getClubs()) {
            if (session.getClubs().contains(source)) continue;
            long count = pool.stream().filter(p -> session.source(p) == source).count();
            Table row = new Table();
            Label name = text(source.getName(), .56f, Color.WHITE); name.setEllipsis(true);
            row.add(name).width(280).left().row();
            row.add(text(count + " expostos  •  " + session.losses(source) + "/3 escolhidos", .48f,
                session.losses(source) >= 3 ? ScreenUI.WARNING : ScreenUI.MUTED_TEXT)).left();
            sources.add(row).growX().height(62).row();
        }
        Comparator<Player> order = "IDADE".equals(sort.getSelected()) ? Comparator.comparingInt(Player::getAge)
            : "SALÁRIO".equals(sort.getSelected()) ? Comparator.comparingLong(Player::getAnnualSalary)
            : Comparator.comparingInt(Player::getOverall).reversed();
        pool.sort(order.thenComparing(Player::getName));
        String query = search.getText().toLowerCase(Locale.ROOT).trim();
        for (Player p : pool) {
            Club source = session.source(p);
            if (session.losses(source) >= LeagueExpansionService.MAX_LOSSES_PER_CLUB) continue;
            if (!(p.getName() + " " + source.getName()).toLowerCase(Locale.ROOT).contains(query)) continue;
            if (!"TODOS".equals(filter.getSelected()) && !group(p).equals(filter.getSelected())) continue;
            Table row = ScreenUI.createRow(rows.getRows());
            row.setName("expansion-player-" + p.getId()); row.setTouchable(Touchable.enabled);
            if (p == selected) row.background(StyleFactory.createRoundedPanel(Color.valueOf("302B13"), StyleFactory.GOLD));
            Table identity = new Table();
            Label name = text(p.getName(), .56f, Color.WHITE); name.setEllipsis(true);
            identity.add(name).width(325).left().row();
            Label origin = text(source.getName(), .44f, ScreenUI.MUTED_TEXT); origin.setEllipsis(true);
            identity.add(origin).width(325).left(); row.add(identity).width(330);
            addCell(row, p.getPosition(), 55, Color.WHITE); addCell(row, "" + p.getAge(), 55, Color.WHITE);
            addCell(row, "" + p.getOverall(), 48, StyleFactory.SOFT_YELLOW);
            addCell(row, PlayerPotentialDisplay.forViewer(p, user), 48, ScreenUI.SUCCESS);
            addCell(row, money(p.getAnnualSalary()), 108, ScreenUI.SUCCESS);
            row.addListener(new ClickListener() { public void clicked(InputEvent e, float x, float y) { selected = p; refresh(); } });
            rows.add(row).growX().height(58).padBottom(3).row();
        }
        if (selected == null) details.add(text("Selecione um jogador na lista.", .6f, ScreenUI.MUTED_TEXT)).left();
        else {
            details.add(crest(session.source(selected))).size(90).left().padBottom(10).row();
            Label name = text(selected.getName().toUpperCase(Locale.ROOT), .8f, StyleFactory.SOFT_YELLOW); name.setWrap(true);
            details.add(name).growX().padBottom(14).row();
            detail(session.source(selected).getName());
            detail(selected.getNationality() + "  •  " + selected.getPosition() + "  •  " + selected.getAge() + " anos");
            detail("OVR " + selected.getOverall() + "    POT " + PlayerPotentialDisplay.forViewer(selected, user));
            TechnicalAttributes a = selected.getTechnicalAttributes();
            detail("ATA " + a.getAtaque() + "   PAS " + a.getPasse() + "   DEF " + a.getDefesa());
            detail("FIS " + a.getFisico() + "   DRI " + a.getDrible());
            detail("Salário: " + money(selected.getAnnualSalary()) + " / ano");
            detail("Contrato até " + selected.getContractEndYear());
        }
        roster.add(text(viewed.getName(), .65f, StyleFactory.SOFT_YELLOW)).left().row();
        roster.add(text(session.chosen(viewed).size() + "/" + LeagueExpansionService.requiredSelections(viewed)
            + " escolhidos  •  Folha " + money(session.payroll(viewed)), .53f, ScreenUI.SUCCESS)).left().padBottom(8).row();
        roster.add(text("Hard Cap disponível: " + money(viewed.getFinance().getHardCap() - session.payroll(viewed)), .5f, ScreenUI.MUTED_TEXT)).left().padBottom(8).row();
        for (Player p : session.chosen(viewed)) {
            Label item = text(p.getPosition() + "  " + p.getName(), .52f, Color.WHITE); item.setEllipsis(true);
            roster.add(item).width(410).left().height(28).row();
        }
        if (!session.getLog().isEmpty()) {
            Label last = text("ÚLTIMA ESCOLHA\n" + session.getLog().get(session.getLog().size() - 1), .53f, StyleFactory.SOFT_YELLOW);
            last.setWrap(true); roster.add(last).growX().padTop(12).row();
        }
    }
    private void detail(String value) { Label label = text(value, .6f, Color.WHITE); label.setWrap(true); details.add(label).growX().left().padBottom(13).row(); }
    private Actor crest(Club club) {
        String path = club.getLogoPath();
        if (path == null || !com.badlogic.gdx.Gdx.files.internal(path).exists()) return new Actor();
        com.badlogic.gdx.graphics.Texture texture = logos.get(path);
        if (texture == null) { texture = io.github.some_example_name.utils.ClubLogoAssets.load(path); logos.put(path, texture); }
        Image image = new Image(texture); image.setScaling(com.badlogic.gdx.utils.Scaling.fit); return image;
    }
    private Table panel(String title) { Table p = ScreenUI.createPanel(); p.background(StyleFactory.createRoundedPanel(Color.valueOf("0A1712"), StyleFactory.DARK_GOLD)); p.top().left(); p.add(ScreenUI.createSectionTitle(game.skin, title)).growX().left().padBottom(12).row(); return p; }
    private ScrollPane scroll(Table table) { ScrollPane s = new ScrollPane(table, game.skin); s.setScrollingDisabled(true, false); s.setFadeScrollBars(false); return s; }
    private Label text(String value, float scale, Color color) { Label l = new Label(value, game.skin); l.setFontScale(scale); l.setColor(color); return l; }
    private void addCell(Table row, String value, float width, Color color) { Label l = text(value, .5f, color); l.setEllipsis(true); row.add(l).width(width).left(); }
    private TextButton button(String title, Runnable action) { TextButton b = ScreenUI.createSecondaryButton(game.skin, title); b.getLabel().setFontScale(.52f); b.addListener(new ClickListener() { public void clicked(InputEvent e, float x, float y) { if (!b.isDisabled()) action.run(); } }); return b; }
    private String money(long amount) { return String.format(Locale.US, "WFL$ %.2fM", amount / 1_000_000d); }
    private String group(Player p) { String pos = p.getPrimaryPosition().name(); return "GK".equals(pos) ? "GK" : pos.endsWith("B") || "SW".equals(pos) ? "DEF" : pos.contains("M") ? "MEI" : "ATA"; }
}
