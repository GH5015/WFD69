package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.LeagueHistory;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;
import java.util.Locale;

/** Arquivo histórico da liga: temporadas, carreiras, Hall da Fama e recordes. */
public class LeagueHistoryScreen implements Screen {
    private enum Tab { SEASONS, CAREERS, HALL, RECORDS }

    private final Main game;
    private final Club playerClub;
    private final Stage stage;
    private Tab activeTab = Tab.SEASONS;
    private String selectedPlayerId;

    public LeagueHistoryScreen(Main game, Club playerClub) {
        this.game = game;
        this.playerClub = playerClub;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); refreshUI(); }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        LeagueHistory history = game.league.getHistory();
        Table page = ScreenUI.createPage(true);
        page.add(ScreenUI.createHeader(game.skin, "MEMÓRIA HISTÓRICA DA WFL",
            "ARQUIVO DA LIGA  •  DESDE 1969")).growX().height(ScreenUI.HEADER_HEIGHT).padBottom(9f).row();
        page.add(createTabs()).growX().height(52f).padBottom(9f).row();

        Table content;
        if (activeTab == Tab.SEASONS) content = createSeasons(history);
        else if (activeTab == Tab.CAREERS) content = createCareers(history);
        else if (activeTab == Tab.HALL) content = createHall(history);
        else content = createRecords(history);
        page.add(content).grow().padBottom(9f).row();

        TextButton back = ScreenUI.createSecondaryButton(game.skin, "VOLTAR À TABELA");
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StandingsScreen(game, playerClub));
            }
        });
        page.add(back).width(260f).height(46f).center();
        root.add(page);
        ScreenUI.animateTabContent(content);
        NavigationDrawer.attach(stage, game, playerClub, "TABELA", true);
    }

    private Table createTabs() {
        Table panel = ScreenUI.createPanel();
        addTab(panel, "TEMPORADAS", Tab.SEASONS);
        addTab(panel, "CARREIRAS", Tab.CAREERS);
        addTab(panel, "HALL DA FAMA", Tab.HALL);
        addTab(panel, "LIVRO DE RECORDES", Tab.RECORDS);
        return panel;
    }

    private void addTab(Table panel, String text, Tab tab) {
        TextButton button = activeTab == tab
            ? ScreenUI.createPrimaryButton(game.skin, text)
            : ScreenUI.createSecondaryButton(game.skin, text);
        button.getLabel().setFontScale(.52f);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                activeTab = tab;
                refreshUI();
            }
        });
        panel.add(button).growX().uniformX().height(38f).pad(4f);
    }

    private Table createSeasons(LeagueHistory history) {
        Table panel = ScreenUI.createTablePanel();
        Table list = new Table();
        list.top();
        List<LeagueHistory.SeasonRecord> records = history.getSeasons();
        if (records.isEmpty()) {
            list.add(emptyState("A história começa ao fim da temporada atual.",
                "Campeão, vice, MVP, artilheiro, melhor jovem e Pick #1 serão arquivados automaticamente.")).grow().center();
        } else {
            int index = 0;
            for (LeagueHistory.SeasonRecord record : records) {
                list.add(createSeasonCard(record, index++)).growX().height(112f).padBottom(7f).row();
            }
        }
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow().pad(7f);
        return panel;
    }

    private Table createSeasonCard(LeagueHistory.SeasonRecord record, int index) {
        Table row = ScreenUI.createRow(index);
        Table season = new Table();
        season.add(ScreenUI.createBoldValue(game.skin, String.valueOf(record.getYear()), StyleFactory.SOFT_YELLOW, Align.center)).row();
        season.add(ScreenUI.createSubtitle(game.skin, "TEMPORADA WFL"));
        row.add(season).width(135f).padLeft(10f).padRight(12f);
        row.add(metric("CAMPEÃO", record.getChampion(), ScreenUI.SUCCESS)).growX().uniformX().padRight(7f);
        row.add(metric("VICE", record.getRunnerUp(), Color.WHITE)).growX().uniformX().padRight(7f);
        row.add(metric("MVP", record.getMvp(), StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(7f);
        row.add(metric("ARTILHEIRO", record.getTopScorer() + "  •  " + record.getTopScorerGoals() + " G", Color.WHITE)).growX().uniformX().padRight(7f);
        row.add(metric("MELHOR JOVEM", record.getBestYoung(), ScreenUI.SUCCESS)).growX().uniformX().padRight(7f);
        LeagueHistory.DraftRecord pick = record.getFirstOverallPick();
        String pickText = pick == null ? "Ainda não registrado" : pick.getPlayerName() + " → " + pick.getOwnerClub()
            + (pick.isViaTrade() ? " (via " + pick.getOriginalClub() + ")" : "");
        row.add(metric("PICK #1", pickText, StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8f);
        return row;
    }

    private Table createCareers(LeagueHistory history) {
        List<LeagueHistory.PlayerCareer> careers = history.getPlayerCareers();
        if (selectedPlayerId == null && !careers.isEmpty()) selectedPlayerId = careers.get(0).getPlayerId();
        Table columns = new Table();
        columns.add(createCareerList(careers)).width(350f).growY().padRight(9f);
        columns.add(createCareerProfile(history.getPlayerCareer(selectedPlayerId))).grow();
        return columns;
    }

    private Table createCareerList(List<LeagueHistory.PlayerCareer> careers) {
        Table panel = ScreenUI.createTablePanel();
        Table list = new Table();
        list.top();
        list.add(ScreenUI.createSectionTitle(game.skin, "JOGADORES")).growX().left().pad(10f).row();
        int index = 0;
        for (LeagueHistory.PlayerCareer career : careers) {
            final String playerId = career.getPlayerId();
            Table row = ScreenUI.createRow(index++);
            Color color = playerId.equals(selectedPlayerId) ? StyleFactory.SOFT_YELLOW : Color.WHITE;
            row.add(ScreenUI.createBoldValue(game.skin, career.getPlayerName(), color, Align.left)).growX().left();
            row.add(ScreenUI.createBoldValue(game.skin, String.valueOf(career.getLegacyPoints()), ScreenUI.SUCCESS, Align.right)).width(60f).right();
            row.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { selectedPlayerId = playerId; refreshUI(); }
            });
            list.add(row).growX().height(42f).padLeft(6f).padRight(6f).padBottom(3f).row();
        }
        if (careers.isEmpty()) list.add(emptyState("Nenhuma carreira arquivada.", "Conclua uma temporada para iniciar os registros.")).grow();
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createCareerProfile(LeagueHistory.PlayerCareer career) {
        Table panel = ScreenUI.createTablePanel();
        if (career == null) {
            panel.add(emptyState("Selecione um jogador.", "O histórico anual aparecerá aqui.")).grow();
            return panel;
        }
        Table content = new Table();
        content.top();
        Table hero = ScreenUI.createPanel();
        Table identity = new Table();
        identity.add(ScreenUI.createSectionTitle(game.skin, career.getPlayerName().toUpperCase())).left().row();
        identity.add(ScreenUI.createSubtitle(game.skin, career.getNationality() + "  •  CARREIRA WFL")).left();
        hero.add(identity).growX().left().pad(10f);
        hero.add(metric("JOGOS", String.valueOf(career.getAppearances()), Color.WHITE)).width(105f);
        hero.add(metric("GOLS", String.valueOf(career.getGoals()), ScreenUI.SUCCESS)).width(105f);
        hero.add(metric("ASSIST.", String.valueOf(career.getAssists()), StyleFactory.SOFT_YELLOW)).width(105f);
        hero.add(metric("LEGADO", String.valueOf(career.getLegacyPoints()), StyleFactory.SOFT_YELLOW)).width(105f).padRight(8f);
        content.add(hero).growX().height(82f).padBottom(7f).row();

        Table honors = ScreenUI.createPanel();
        honors.add(ScreenUI.createBoldValue(game.skin, career.getTitles() + "x CAMPEÃO WFL", StyleFactory.SOFT_YELLOW, Align.center)).growX();
        honors.add(ScreenUI.createBoldValue(game.skin, career.getMvpAwards() + "x MVP", StyleFactory.SOFT_YELLOW, Align.center)).growX();
        honors.add(ScreenUI.createBoldValue(game.skin, career.getTopScorerAwards() + "x ARTILHEIRO", StyleFactory.SOFT_YELLOW, Align.center)).growX();
        content.add(honors).growX().height(48f).padBottom(7f).row();

        Table table = new Table();
        table.add(header("ANO", Align.center)).width(70f);
        table.add(header("CLUBE", Align.left)).growX();
        table.add(header("J", Align.center)).width(62f);
        table.add(header("G", Align.center)).width(62f);
        table.add(header("A", Align.center)).width(62f);
        table.add(header("OVR", Align.center)).width(70f);
        table.add(header("NOTA", Align.center)).width(80f).row();
        int index = 0;
        for (LeagueHistory.PlayerSeason season : career.getSeasons()) {
            Table row = ScreenUI.createRow(index++);
            row.add(value(String.valueOf(season.getYear()), StyleFactory.SOFT_YELLOW, Align.center)).width(70f);
            row.add(value(season.getClubName(), Color.WHITE, Align.left)).growX();
            row.add(value(String.valueOf(season.getAppearances()), Color.WHITE, Align.center)).width(62f);
            row.add(value(String.valueOf(season.getGoals()), ScreenUI.SUCCESS, Align.center)).width(62f);
            row.add(value(String.valueOf(season.getAssists()), StyleFactory.SOFT_YELLOW, Align.center)).width(62f);
            row.add(value(String.valueOf(season.getOverall()), Color.WHITE, Align.center)).width(70f);
            row.add(value(season.getAverageRating() > 0 ? String.format(Locale.US, "%.1f", season.getAverageRating()) : "—", Color.WHITE, Align.center)).width(80f);
            table.add(row).growX().height(42f).padBottom(3f).row();
        }
        content.add(table).growX().top().row();
        ScrollPane scroll = new ScrollPane(content, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow().pad(7f);
        return panel;
    }

    private Table createHall(LeagueHistory history) {
        Table panel = ScreenUI.createTablePanel();
        Table list = new Table();
        list.top();
        List<LeagueHistory.HallOfFameEntry> entries = history.getHallOfFame();
        if (entries.isEmpty()) {
            list.add(emptyState("O Hall da Fama aguarda sua primeira lenda.",
                "Aposentados são avaliados por títulos, MVPs, gols, assistências, jogos, longevidade e recordes.")).grow().center();
        } else {
            int index = 0;
            for (LeagueHistory.HallOfFameEntry entry : entries) {
                LeagueHistory.PlayerCareer career = history.getPlayerCareer(entry.getPlayerId());
                Table card = ScreenUI.createRow(index++);
                card.add(ScreenUI.createBoldValue(game.skin, entry.getPlayerName().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.left)).growX().padLeft(18f);
                card.add(metric("ENTRADA", String.valueOf(entry.getInductionYear()), Color.WHITE)).width(150f);
                card.add(metric("TÍTULOS", career != null ? String.valueOf(career.getTitles()) : "—", Color.WHITE)).width(150f);
                card.add(metric("GOLS", career != null ? String.valueOf(career.getGoals()) : "—", ScreenUI.SUCCESS)).width(150f);
                card.add(metric("PONTOS DE LEGADO", String.valueOf(entry.getLegacyPoints()), StyleFactory.SOFT_YELLOW)).width(210f).padRight(12f);
                list.add(card).growX().height(78f).padBottom(7f).row();
            }
        }
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow().pad(7f);
        return panel;
    }

    private Table createRecords(LeagueHistory history) {
        Table panel = ScreenUI.createTablePanel();
        Table grid = new Table();
        grid.top();
        LeagueHistory.PlayerCareer goals = history.leaderByGoals();
        LeagueHistory.PlayerCareer assists = history.leaderByAssists();
        LeagueHistory.PlayerCareer titles = history.leaderByTitles();
        LeagueHistory.PlayerCareer games = history.leaderByAppearances();
        Club unbeaten = null;
        for (Club club : game.league.getClubs()) if (unbeaten == null || club.getMaxUnbeatenStreak() > unbeaten.getMaxUnbeatenStreak()) unbeaten = club;
        addRecord(grid, "MAIS GOLS NA CARREIRA", goals, goals != null ? goals.getGoals() : 0, "gols", 0);
        addRecord(grid, "MAIS ASSISTÊNCIAS", assists, assists != null ? assists.getAssists() : 0, "assistências", 1);
        addRecord(grid, "MAIS TÍTULOS", titles, titles != null ? titles.getTitles() : 0, "títulos WFL", 2);
        addRecord(grid, "MAIS JOGOS", games, games != null ? games.getAppearances() : 0, "partidas", 3);
        Table streak = ScreenUI.createPanel();
        streak.add(ScreenUI.createSectionTitle(game.skin, "MAIOR SEQUÊNCIA INVICTA")).left().pad(12f).row();
        streak.add(ScreenUI.createBoldValue(game.skin, unbeaten != null ? unbeaten.getName() : "—", Color.WHITE, Align.left)).left().padLeft(12f).padTop(6f).row();
        streak.add(ScreenUI.createBoldValue(game.skin, unbeaten != null ? unbeaten.getMaxUnbeatenStreak() + " jogos" : "Sem registro", StyleFactory.SOFT_YELLOW, Align.left)).left().padLeft(12f).padTop(5f);
        grid.add(streak).growX().height(130f).pad(6f).colspan(2).row();
        panel.add(grid).grow().pad(12f);
        return panel;
    }

    private void addRecord(Table grid, String title, LeagueHistory.PlayerCareer career, int amount, String unit, int index) {
        Table card = ScreenUI.createRow(index);
        card.add(ScreenUI.createSectionTitle(game.skin, title)).left().pad(12f).row();
        card.add(ScreenUI.createBoldValue(game.skin, career != null ? career.getPlayerName() : "—", Color.WHITE, Align.left)).left().padLeft(12f).padTop(7f).row();
        card.add(ScreenUI.createBoldValue(game.skin, career != null ? amount + " " + unit : "Nenhuma temporada concluída", StyleFactory.SOFT_YELLOW, Align.left)).left().padLeft(12f).padTop(5f);
        grid.add(card).growX().uniformX().height(130f).pad(6f);
        if (index % 2 == 1) grid.row();
    }

    private Table metric(String label, String value, Color color) {
        Table box = new Table();
        box.add(ScreenUI.createSubtitle(game.skin, label)).left().row();
        com.badlogic.gdx.scenes.scene2d.ui.Label valueLabel = ScreenUI.createBoldValue(game.skin, value, color, Align.left);
        valueLabel.setEllipsis(true);
        box.add(valueLabel).growX().left().padTop(4f);
        return box;
    }

    private Table emptyState(String title, String subtitle) {
        Table box = ScreenUI.createPanel();
        box.add(ScreenUI.createSectionTitle(game.skin, title)).center().padTop(35f).row();
        box.add(ScreenUI.createSubtitle(game.skin, subtitle)).center().padTop(12f);
        return box;
    }

    private com.badlogic.gdx.scenes.scene2d.ui.Label header(String text, int alignment) {
        return ScreenUI.createTableHeaderLabel(game.skin, text, alignment);
    }

    private com.badlogic.gdx.scenes.scene2d.ui.Label value(String text, Color color, int alignment) {
        return ScreenUI.createValueLabel(game.skin, text, color, alignment);
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); }
}
