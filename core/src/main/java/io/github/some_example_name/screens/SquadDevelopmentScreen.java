package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.PlayerDevelopmentDialog;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Visão comparativa da evolução de todo o elenco. */
public class SquadDevelopmentScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;

    private String filter = "TODOS";
    private String positionFilter = "TODAS";
    private String sortField = "PROGRESSÃO";
    private boolean sortAscending;
    private boolean showingInjuredPlayers;

    public SquadDevelopmentScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        refreshUI();
    }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        Table page = ScreenUI.createPage(true);
        page.add(createHeader()).growX().height(78f).padBottom(10f).row();
        page.add(createViewTabs()).growX().height(44f).padBottom(10f).row();
        if (showingInjuredPlayers) {
            page.add(createInjurySummary()).growX().height(70f).padBottom(10f).row();
            page.add(createInjuredPlayersPanel()).grow().row();
        } else {
            page.add(createSummary()).growX().height(70f).padBottom(10f).row();
            page.add(createFilters()).growX().height(105f).padBottom(10f).row();
            page.add(createRosterPanel()).grow().row();
        }
        root.add(page);

        NavigationDrawer.attach(stage, game, club, "DESENV.", true);
        CareerOverlay.attach(stage, game, club);
    }

    private Table createHeader() {
        Table header = ScreenUI.createPanel();
        Table identity = new Table();
        Label title = new Label(showingInjuredPlayers ? "JOGADORES LESIONADOS" : "DESENVOLVIMENTO DO ELENCO", game.skin, "font-title");
        title.setFontScale(0.72f);
        title.setColor(StyleFactory.GOLD);
        identity.add(title).left().row();
        Label subtitle = ScreenUI.createSubtitle(game.skin, showingInjuredPlayers ? "CENTRO MÉDICO • " + club.getName().toUpperCase() : club.getName().toUpperCase());
        subtitle.setFontScale(0.52f);
        subtitle.setColor(ScreenUI.MUTED_TEXT);
        identity.add(subtitle).left();
        header.add(identity).left().expandX();
        header.add(ScreenUI.createStatusBox(game.skin, "TEMPORADA", String.valueOf(game.league.getCurrentSeason()), StyleFactory.SOFT_YELLOW))
            .width(175f).height(48f);
        return header;
    }

    private Table createViewTabs() {
        Table tabs = ScreenUI.createPanel();
        TextButton development = createViewTab("DESENVOLVIMENTO", false);
        TextButton injured = createViewTab("LESIONADOS (" + getInjuredPlayers().size() + ")", true);
        tabs.add(development).width(230f).height(34f).padRight(8f);
        tabs.add(injured).width(210f).height(34f);
        return tabs;
    }

    private TextButton createViewTab(String text, boolean injuredTab) {
        TextButton button = ScreenUI.createInteractiveButton(text, game.skin, "toggle");
        button.getLabel().setFontScale(0.47f);
        boolean selected = showingInjuredPlayers == injuredTab;
        button.setChecked(selected);
        button.setColor(selected ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        button.getLabel().setColor(selected ? Color.BLACK : Color.WHITE);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showingInjuredPlayers = injuredTab;
                refreshUI();
            }
        });
        return button;
    }

    private Table createInjurySummary() {
        List<Player> injured = getInjuredPlayers();
        int nextMatch = 0;
        int shortTerm = 0;
        int longTerm = 0;

        for (Player player : injured) {
            if (player.getInjuryDuration() <= 1) nextMatch++;
            else if (player.getInjuryDuration() <= 3) shortTerm++;
            else longTerm++;
        }

        Table summary = new Table();
        summary.add(status("NO DEPARTAMENTO MÉDICO", String.valueOf(injured.size()), injured.isEmpty() ? ScreenUI.SUCCESS : ScreenUI.DANGER)).growX().uniformX().padRight(8f);
        summary.add(status("VOLTA NO PRÓXIMO JOGO", String.valueOf(nextMatch), nextMatch > 0 ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT)).growX().uniformX().padRight(8f);
        summary.add(status("ATÉ 3 JOGOS", String.valueOf(shortTerm), shortTerm > 0 ? StyleFactory.SOFT_YELLOW : ScreenUI.MUTED_TEXT)).growX().uniformX().padRight(8f);
        summary.add(status("4+ JOGOS", String.valueOf(longTerm), longTerm > 0 ? ScreenUI.DANGER : ScreenUI.MUTED_TEXT)).growX().uniformX().padRight(8f);
        summary.add(status("DISPONIBILIDADE", injured.isEmpty() ? "ELENCO COMPLETO" : "ATENÇÃO MÉDICA", injured.isEmpty() ? ScreenUI.SUCCESS : StyleFactory.SOFT_YELLOW)).growX().uniformX();
        return summary;
    }

    private Table createInjuredPlayersPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        List<Player> injured = getInjuredPlayers();

        if (injured.isEmpty()) {
            Table empty = new Table();
            Label title = ScreenUI.createBoldValue(game.skin, "NENHUM JOGADOR LESIONADO", ScreenUI.SUCCESS, Align.center);
            title.setFontScale(0.68f);
            Label text = ScreenUI.createSubtitle(game.skin, "Elenco completo e disponível para a próxima partida.");
            text.setColor(ScreenUI.MUTED_TEXT);
            empty.add(title).padBottom(9f).row();
            empty.add(text);
            panel.add(empty).grow().center();
            return panel;
        }

        Table table = new Table();
        table.top();
        Table header = ScreenUI.createTableHeaderRow();
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "JOGADOR", Align.left)).width(310f).padLeft(10f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "IDADE", Align.center)).width(76f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "POS", Align.center)).width(78f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "OVR", Align.center)).width(75f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "LESÃO", Align.center)).width(205f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "RETORNO", Align.center)).width(160f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "SITUAÇÃO", Align.center)).width(160f);
        table.add(header).growX().height(42f).row();

        int index = 0;
        for (Player player : injured) {
            table.add(createInjuredPlayerRow(player, index++)).growX().height(54f).row();
        }

        ScrollPane scroll = new ScrollPane(table, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createInjuredPlayerRow(final Player player, int index) {
        Table row = ScreenUI.createRow(index);
        String injuryType = player.getInjuryType();
        if (injuryType == null || injuryType.trim().isEmpty()) injuryType = "LESÃO";
        row.add(ScreenUI.createBoldValue(game.skin, ScreenUI.shorten(player.getName(), 28), Color.WHITE, Align.left)).left().width(310f).padLeft(10f);
        row.add(value(String.valueOf(player.getAge()), Color.WHITE)).width(76f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(78f).height(27f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).width(75f);
        row.add(value(injuryType.toUpperCase(), ScreenUI.DANGER)).width(205f);
        row.add(value(returnEstimate(player.getInjuryDuration()), StyleFactory.SOFT_YELLOW)).width(160f);
        row.add(value(injuryStatus(player.getInjuryDuration()), injuryStatusColor(player.getInjuryDuration()))).width(160f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                new PlayerDevelopmentDialog(game.skin, player, club).show(stage);
            }
        });
        return row;
    }

    private List<Player> getInjuredPlayers() {
        List<Player> injured = new ArrayList<>();
        for (Player player : club.getSquad()) {
            if (player.isInjured()) injured.add(player);
        }
        injured.sort(Comparator.comparingInt(Player::getInjuryDuration).reversed().thenComparing(Player::getName));
        return injured;
    }

    private String returnEstimate(int matches) {
        return matches <= 1 ? "PRÓXIMO JOGO" : matches + " JOGOS";
    }

    private String injuryStatus(int matches) {
        return matches <= 1 ? "RETORNO IMINENTE" : matches <= 3 ? "EM RECUPERAÇÃO" : "EM TRATAMENTO";
    }

    private Color injuryStatusColor(int matches) {
        return matches <= 1 ? ScreenUI.SUCCESS : matches <= 3 ? StyleFactory.SOFT_YELLOW : ScreenUI.DANGER;
    }

    private Table createSummary() {
        List<Player> players = club.getSquad();
        int total = players.size();
        int young = 0;
        int improving = 0;
        int declining = 0;
        int totalOverall = 0;
        int totalPotential = 0;

        for (Player player : players) {
            totalOverall += player.getOverall();
            totalPotential += player.getPotential();
            if (player.getAge() <= 21) young++;
            if (recentChange(player) > 0) improving++;
            if (recentChange(player) < 0) declining++;
        }

        Table summary = new Table();
        summary.add(status("ELENCO", total + " JOGADORES", StyleFactory.CREME_AGED)).growX().uniformX().padRight(8f);
        summary.add(status("MÉDIA OVR", total == 0 ? "—" : String.valueOf(Math.round((float) totalOverall / total)), StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8f);
        summary.add(status("MÉDIA POT", total == 0 ? "—" : String.valueOf(Math.round((float) totalPotential / total)), ScreenUI.SUCCESS)).growX().uniformX().padRight(8f);
        summary.add(status("JOVENS ATÉ 21", String.valueOf(young), ScreenUI.SUCCESS)).growX().uniformX().padRight(8f);
        summary.add(status("EM EVOLUÇÃO", String.valueOf(improving), improving > 0 ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT)).growX().uniformX().padRight(8f);
        summary.add(status("EM DECLÍNIO", String.valueOf(declining), declining > 0 ? ScreenUI.DANGER : ScreenUI.MUTED_TEXT)).growX().uniformX();
        return summary;
    }

    private Table status(String label, String value, Color color) {
        return ScreenUI.createStatusBox(game.skin, label, value, color);
    }

    private Table createFilters() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table quick = new Table();
        String[] filters = { "TODOS", "JOVENS", "TITULARES", "RESERVAS", "EM EVOLUÇÃO", "ESTÁVEIS", "DECLÍNIO" };
        for (String option : filters) {
            quick.add(createFilterButton(option)).height(34f).padRight(5f);
        }
        panel.add(quick).left().row();

        Table selects = new Table();
        selects.add(ScreenUI.createSubtitle(game.skin, "POSIÇÃO:")).padRight(6f);
        SelectBox<String> positions = ScreenUI.createSelectBox(game.skin);
        positions.setItems("TODAS", "GK", "DEF", "MID", "ATA");
        positions.setSelected(positionFilter);
        positions.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                positionFilter = positions.getSelected();
                refreshUI();
            }
        });
        selects.add(positions).width(155f).height(42f).padRight(22f);
        selects.add(ScreenUI.createSubtitle(game.skin, "ORDENAR POR:")).padRight(6f);
        SelectBox<String> sort = ScreenUI.createSelectBox(game.skin);
        sort.setItems("PROGRESSÃO", "NOME", "IDADE", "OVR", "POTENCIAL", "POSIÇÃO");
        sort.setSelected(sortField);
        sort.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                sortField = sort.getSelected();
                sortAscending = false;
                refreshUI();
            }
        });
        selects.add(sort).width(190f).height(42f);
        panel.add(selects).left().padTop(7f);
        return panel;
    }

    private TextButton createFilterButton(String option) {
        TextButton button = ScreenUI.createInteractiveButton(option, game.skin, "toggle");
        button.getLabel().setFontScale(0.46f);
        boolean selected = option.equals(filter);
        button.setChecked(selected);
        button.setColor(selected ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        button.getLabel().setColor(selected ? Color.BLACK : Color.WHITE);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                filter = option;
                refreshUI();
            }
        });
        return button;
    }

    private Table createRosterPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table table = new Table();
        table.top();

        Table header = ScreenUI.createTableHeaderRow();
        addSortHeader(header, "JOGADOR", "NOME", 295f);
        addSortHeader(header, "IDADE", "IDADE", 76f);
        addSortHeader(header, "POS", "POSIÇÃO", 78f);
        addSortHeader(header, "OVR", "OVR", 75f);
        addSortHeader(header, "POT", "POTENCIAL", 75f);
        addSortHeader(header, "Δ OVR", "PROGRESSÃO", 95f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "TENDÊNCIA", Align.center)).width(155f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "STATUS", Align.center)).width(175f);
        table.add(header).growX().height(42f).row();

        List<Player> players = new ArrayList<>(club.getSquad());
        filterAndSort(players);
        int index = 0;
        for (Player player : players) {
            table.add(createPlayerRow(player, index++)).growX().height(52f).row();
        }

        ScrollPane scroll = new ScrollPane(table, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private void addSortHeader(Table header, String label, String field, float width) {
        String suffix = field.equals(sortField) ? (sortAscending ? " ↑" : " ↓") : "";
        TextButton button = ScreenUI.createInteractiveButton(label + suffix, game.skin, "toggle");
        button.getLabel().setFontScale(0.48f);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (field.equals(sortField)) sortAscending = !sortAscending;
                else {
                    sortField = field;
                    sortAscending = false;
                }
                refreshUI();
            }
        });
        header.add(button).width(width).height(38f);
    }

    private Table createPlayerRow(final Player player, int index) {
        Table row = ScreenUI.createRow(index);
        int change = recentChange(player);
        row.add(ScreenUI.createBoldValue(game.skin, ScreenUI.shorten(player.getName(), 27), Color.WHITE, Align.left)).left().width(295f).padLeft(10f);
        row.add(value(String.valueOf(player.getAge()), Color.WHITE)).width(76f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(78f).height(27f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).width(75f);
        row.add(value(String.valueOf(player.getPotential()), ScreenUI.SUCCESS)).width(75f);
        row.add(value(formatChange(change), change > 0 ? ScreenUI.SUCCESS : change < 0 ? ScreenUI.DANGER : ScreenUI.MUTED_TEXT)).width(95f);
        row.add(value(trend(change), trendColor(change))).width(155f);
        row.add(value(status(player, change), statusColor(player, change))).width(175f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                new PlayerDevelopmentDialog(game.skin, player, club).show(stage);
            }
        });
        return row;
    }

    private Label value(String text, Color color) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, Align.center);
        label.setFontScale(0.51f);
        return label;
    }

    private void filterAndSort(List<Player> players) {
        players.removeIf(player -> !matchesFilter(player) || !matchesPosition(player));
        Comparator<Player> comparator;
        switch (sortField) {
            case "NOME": comparator = Comparator.comparing(Player::getName); break;
            case "IDADE": comparator = Comparator.comparingInt(Player::getAge); break;
            case "OVR": comparator = Comparator.comparingInt(Player::getOverall); break;
            case "POTENCIAL": comparator = Comparator.comparingInt(Player::getPotential); break;
            case "POSIÇÃO": comparator = Comparator.comparing(Player::getPosition); break;
            default: comparator = Comparator.comparingInt(this::recentChange); break;
        }
        if (!sortAscending) comparator = comparator.reversed();
        players.sort(comparator.thenComparing(Player::getName));
    }

    private boolean matchesFilter(Player player) {
        int change = recentChange(player);
        if ("JOVENS".equals(filter)) return player.getAge() <= 21;
        if ("TITULARES".equals(filter)) return club.getTacticsMap().containsValue(player);
        if ("RESERVAS".equals(filter)) return !club.getTacticsMap().containsValue(player);
        if ("EM EVOLUÇÃO".equals(filter)) return change > 0;
        if ("ESTÁVEIS".equals(filter)) return change == 0;
        if ("DECLÍNIO".equals(filter)) return change < 0;
        return true;
    }

    private boolean matchesPosition(Player player) {
        String position = player.getPosition();
        if ("GK".equals(positionFilter)) return "GK".equals(position);
        if ("DEF".equals(positionFilter)) return position.matches("CB|LB|RB|LWB|RWB");
        if ("MID".equals(positionFilter)) return position.matches("CDM|CM|CAM|LM|RM");
        if ("ATA".equals(positionFilter)) return position.matches("ST|CF|LW|RW");
        return true;
    }

    private int recentChange(Player player) {
        List<Integer> history = player.getOverallDevelopmentHistory();
        if (history.size() < 2) return 0;
        return history.get(history.size() - 1) - history.get(0);
    }

    private String formatChange(int change) { return change > 0 ? "+" + change : String.valueOf(change); }
    private String trend(int change) { return change >= 3 ? "↗↗↗" : change == 2 ? "↗↗" : change == 1 ? "↗" : change == 0 ? "→" : change == -1 ? "↘" : change == -2 ? "↘↘" : "↘↘↘"; }
    private Color trendColor(int change) { return change > 0 ? ScreenUI.SUCCESS : change < 0 ? ScreenUI.DANGER : ScreenUI.MUTED_TEXT; }
    private String status(Player player, int change) {
        if (player.getTruePotential() >= 90 && player.getAge() <= 23) return "PROSPECTO";
        if (player.getOverall() >= 88) return "ESTRELA";
        if (change > 0) return "EM ALTA";
        if (change < 0) return "DECLÍNIO";
        return "ESTÁVEL";
    }
    private Color statusColor(Player player, int change) { return change < 0 ? ScreenUI.DANGER : player.getOverall() >= 88 ? StyleFactory.SOFT_YELLOW : change > 0 ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT; }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0.02f, 0.05f, 0.04f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); }
}