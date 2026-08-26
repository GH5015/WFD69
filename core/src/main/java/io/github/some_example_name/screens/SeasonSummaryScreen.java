package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.PlayoffSeries;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;
import java.util.Locale;

/** Painel de fechamento da temporada antes da entrada na Off Season. */
public class SeasonSummaryScreen implements Screen {

    private final Main game;
    private final Club club;
    private final Stage stage;
    private Texture clubLogo;

    public SeasonSummaryScreen(
        Main game,
        Club club
    ) {

        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
        this.clubLogo = loadLogo();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);
        refresh();
    }

    private void refresh() {

        stage.clear();

        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        Image shade = new Image(
            StyleFactory.createSolid(new Color(0f, 0.02f, 0.015f, 0.46f))
        );
        shade.setFillParent(true);
        root.add(shade);

        Table page = new Table();
        page.setFillParent(true);
        page.top();
        page.pad(20f, 32f, 18f, 32f);

        page.add(createTopHeader()).growX().height(57f).padBottom(8f).row();
        page.add(createSeasonTitle()).growX().height(44f).padBottom(8f).row();
        page.add(createClubRecap()).growX().height(150f).padBottom(10f).row();
        page.add(createMetrics()).growX().height(101f).padBottom(10f).row();
        page.add(createInformationGrid()).growX().height(246f).padBottom(10f).row();
        page.add(createProgression()).growX().height(62f).padBottom(12f).row();
        page.add(createActions()).growX().height(58f);

        root.add(page);
    }

    private Table createTopHeader() {

        Table header = ScreenUI.createPanel();

        Label season = new Label(
            "TEMPORADA " + game.league.getCurrentSeason(),
            game.skin,
            "font-title"
        );
        season.setFontScale(0.82f);
        season.setColor(StyleFactory.GOLD);

        Label clubName = ScreenUI.createBoldValue(
            game.skin,
            club.getName().toUpperCase(),
            StyleFactory.SOFT_YELLOW,
            Align.right
        );
        clubName.setFontScale(0.62f);

        header.add(season).left().expandX();
        header.add(clubName).right().padRight(10f);

        if (clubLogo != null) {
            Image logo = new Image(new TextureRegionDrawable(clubLogo));
            logo.setScaling(Scaling.fit);
            header.add(logo).size(44f);
        }

        return header;
    }

    private Table createSeasonTitle() {

        Table title = new Table();

        Label summary = new Label(
            "—  RESUMO DA TEMPORADA  —",
            game.skin,
            "font-bold"
        );
        summary.setFontScale(0.70f);
        summary.setColor(Color.WHITE);

        Label completed = ScreenUI.createBoldValue(
            game.skin,
            "TEMPORADA CONCLUÍDA",
            ScreenUI.SUCCESS,
            Align.center
        );
        completed.setFontScale(0.43f);

        title.add(summary).center().row();
        title.add(completed).center().padTop(3f);

        return title;
    }

    private Table createClubRecap() {

        Table panel = ScreenUI.createPanel();
        panel.pad(12f, 18f, 12f, 18f);

        if (clubLogo != null) {
            Image logo = new Image(new TextureRegionDrawable(clubLogo));
            logo.setScaling(Scaling.fit);
            panel.add(logo).size(105f).padRight(18f);
        }

        Table identity = new Table();
        identity.add(ScreenUI.createSectionTitle(game.skin, club.getName().toUpperCase()))
            .left().padBottom(5f).row();
        identity.add(ScreenUI.createSubtitle(game.skin, "WFL • " + club.getCountry()))
            .left().padBottom(12f).row();
        identity.add(ScreenUI.createBoldValue(
            game.skin,
            "CAMPANHA: " + record(),
            StyleFactory.SOFT_YELLOW,
            Align.left
        )).left();

        panel.add(identity).width(365f).left().padRight(16f);
        panel.add(createHeroStat("POSIÇÃO FINAL", getFinalPosition() + "º", "de " + game.league.getClubs().size()))
            .growX().padRight(8f);
        panel.add(createHeroStat("GOLS MARCADOS", String.valueOf(club.getGoalsFor()), "na temporada"))
            .growX().padRight(8f);
        panel.add(createHeroStat("GOLS SOFRIDOS", String.valueOf(club.getGoalsAgainst()), "na temporada"))
            .growX().padRight(8f);
        panel.add(createHeroStat("PLAYOFFS", playoffDisplay(), getChampion() == club ? "campeão da WFL" : "pós-temporada"))
            .growX();

        return panel;
    }

    private Table createHeroStat(
        String title,
        String value,
        String hint
    ) {

        Table block = new Table();
        block.add(ScreenUI.createSubtitle(game.skin, title)).center().padBottom(6f).row();

        Label main = ScreenUI.createBoldValue(game.skin, value, Color.WHITE, Align.center);
        main.setFontScale(value.length() > 8 ? 0.55f : 0.90f);
        block.add(main).center().row();

        Label note = ScreenUI.createSubtitle(game.skin, hint);
        note.setFontScale(0.43f);
        block.add(note).center().padTop(3f);
        return block;
    }

    private Table createMetrics() {

        Table metrics = new Table();

        int points = club.getTotalWins() * 3 + club.getTotalDraws();
        String goalDifference = club.getGoalDifference() > 0 ? "+" + club.getGoalDifference() : String.valueOf(club.getGoalDifference());

        metrics.add(metricCard("VITÓRIAS", String.valueOf(club.getTotalWins()), ScreenUI.SUCCESS)).growX().padRight(8f);
        metrics.add(metricCard("EMPATES", String.valueOf(club.getTotalDraws()), StyleFactory.SOFT_YELLOW)).growX().padRight(8f);
        metrics.add(metricCard("DERROTAS", String.valueOf(club.getTotalLosses()), ScreenUI.DANGER)).growX().padRight(8f);
        metrics.add(metricCard("PONTOS", String.valueOf(points), Color.WHITE)).growX().padRight(8f);
        metrics.add(metricCard("SALDO DE GOLS", goalDifference, club.getGoalDifference() >= 0 ? ScreenUI.SUCCESS : ScreenUI.DANGER)).growX().padRight(8f);
        metrics.add(metricCard("APROVEITAMENTO", club.getWinPercentage() + "%", StyleFactory.SOFT_YELLOW)).growX();

        return metrics;
    }

    private Table metricCard(
        String title,
        String value,
        Color accent
    ) {

        Table card = ScreenUI.createSubtlePanel();
        card.add(ScreenUI.createSubtitle(game.skin, title)).center().padTop(8f).row();

        Label number = ScreenUI.createBoldValue(game.skin, value, Color.WHITE, Align.center);
        number.setFontScale(0.82f);
        card.add(number).center().padTop(2f).row();

        Table underline = new Table();
        underline.background(StyleFactory.createSolid(accent));
        card.add(underline).width(34f).height(3f).center().padTop(6f).padBottom(8f);
        return card;
    }

    private Table createInformationGrid() {

        Table grid = new Table();
        grid.add(createHighlightsPanel()).width(500f).growY().padRight(10f);
        grid.add(createFinancialPanel()).width(610f).growY().padRight(10f);
        grid.add(createReviewPanel()).growX().growY();
        return grid;
    }

    private Table createHighlightsPanel() {

        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "DESTAQUES DO CLUBE")).left().padBottom(8f).row();
        addHighlight(panel, "ARTILHEIRO", club.getTopScorerName(), club.getTopScorerGoals() + " gols");
        addHighlight(panel, "LÍDER EM ASSISTÊNCIAS", club.getTopAssisterName(), club.getTopAssisterCount() + " ass.");
        addHighlight(panel, "MAIS PARTIDAS", club.getMostGamesPlayerName(), club.getMostGamesCount() + " jogos");
        addHighlight(panel, "MAIOR INVENCIBILIDADE", "Sequência da temporada", club.getMaxUnbeatenStreak() + " jogos");
        return panel;
    }

    private void addHighlight(
        Table panel,
        String category,
        String name,
        String value
    ) {

        Table row = ScreenUI.createSubtlePanel();
        row.add(ScreenUI.createSubtitle(game.skin, category)).left().expandX().row();
        row.add(ScreenUI.createBoldValue(game.skin, name, Color.WHITE, Align.left)).left().expandX();
        row.add(ScreenUI.createBoldValue(game.skin, value, StyleFactory.SOFT_YELLOW, Align.right)).right();
        panel.add(row).growX().height(43f).padBottom(4f).row();
    }

    private Table createFinancialPanel() {

        ClubFinance finance = club.getFinance();
        long ticket = finance.getTicketRevenue() * 12L;
        long tv = finance.getTvRevenue() * 12L;
        long shirts = finance.getShirtSalesRevenue() * 12L;
        long prizes = finance.getSeasonPrizeEarnings();
        long revenue = finance.getTotalAnnualRevenue();
        long expenses = finance.getTotalMonthlyExpenses() * 12L;

        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "RESUMO FINANCEIRO")).left().padBottom(8f).row();

        Table content = new Table();
        Table lines = new Table();
        addFinanceLine(lines, "Receita estimada", money(revenue), Color.WHITE);
        addFinanceLine(lines, "Premiações", money(prizes), StyleFactory.SOFT_YELLOW);
        addFinanceLine(lines, "Bilheteria", money(ticket), Color.WHITE);
        addFinanceLine(lines, "TV", money(tv), Color.WHITE);
        addFinanceLine(lines, "Camisas", money(shirts), Color.WHITE);
        addFinanceLine(lines, "Folha e despesas", "-" + money(expenses), ScreenUI.DANGER);
        addFinanceLine(lines, "RESULTADO", signedMoney(revenue - expenses), revenue >= expenses ? ScreenUI.SUCCESS : ScreenUI.DANGER);

        content.add(lines).growX().padRight(18f);
        content.add(createRevenueBars(ticket, tv, shirts, prizes)).width(185f).growY();
        panel.add(content).grow();
        return panel;
    }

    private void addFinanceLine(
        Table table,
        String title,
        String value,
        Color color
    ) {

        table.add(ScreenUI.createSubtitle(game.skin, title)).left().expandX().padBottom(5f);
        table.add(ScreenUI.createBoldValue(game.skin, value, color, Align.right)).right().padBottom(5f).row();
    }

    private Table createRevenueBars(
        long ticket,
        long tv,
        long shirts,
        long prizes
    ) {

        long max = Math.max(1L, Math.max(Math.max(ticket, tv), Math.max(shirts, prizes)));
        Table chart = ScreenUI.createSubtlePanel();
        chart.add(ScreenUI.createSubtitle(game.skin, "RECEITAS (ANUAL)")).center().padBottom(8f).row();
        addRevenueBar(chart, "BIL", ticket, max);
        addRevenueBar(chart, "TV", tv, max);
        addRevenueBar(chart, "CAM", shirts, max);
        addRevenueBar(chart, "PRÊ", prizes, max);
        return chart;
    }

    private void addRevenueBar(
        Table chart,
        String name,
        long value,
        long max
    ) {

        Table row = new Table();
        Table fill = new Table();
        fill.background(StyleFactory.createSolid(ScreenUI.SUCCESS));
        row.add(ScreenUI.createSubtitle(game.skin, name)).width(30f).left();
        row.add(fill).width(Math.max(4f, 115f * value / max)).height(10f).left();
        chart.add(row).growX().height(29f).row();
    }

    private Table createReviewPanel() {

        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "BALANÇO DA TEMPORADA")).left().padBottom(8f).row();
        addBullet(panel, getFinalPosition() <= 8 ? "Classificação competitiva na temporada" : "Necessidade de reforços no elenco");
        addBullet(panel, countExpiringContracts() + " contrato(s) precisam de atenção");
        addBullet(panel, Math.max(0, 26 - club.getSquad().size()) + " vaga(s) disponível(is) no elenco");
        addBullet(panel, "Foco: Off Season, Free Agency e Draft");

        Table divider = new Table();
        divider.background(StyleFactory.createSolid(StyleFactory.DARK_GOLD));
        panel.add(divider).growX().height(1f).pad(7f, 0f, 7f, 0f).row();

        panel.add(ScreenUI.createSectionTitle(game.skin, "OBJETIVOS " + (game.league.getCurrentSeason() + 1))).left().padBottom(6f).row();
        addObjective(panel, "Melhorar o aproveitamento acima de " + Math.min(70, club.getWinPercentage() + 12) + "%");
        addObjective(panel, "Fortalecer o elenco com pelo menos 2 titulares");
        addObjective(panel, "Desenvolver jovens talentos via Draft");
        return panel;
    }

    private void addBullet(
        Table panel,
        String text
    ) {

        Label bullet = ScreenUI.createSubtitle(game.skin, "•  " + text);
        bullet.setColor(StyleFactory.CREME_AGED);
        panel.add(bullet).left().padBottom(5f).row();
    }

    private void addObjective(
        Table panel,
        String text
    ) {

        Table row = ScreenUI.createSubtlePanel();
        row.add(ScreenUI.createBoldValue(game.skin, "○", StyleFactory.SOFT_YELLOW, Align.center)).width(20f);
        row.add(ScreenUI.createSubtitle(game.skin, text)).left().expandX().padLeft(4f);
        panel.add(row).growX().height(30f).padBottom(3f).row();
    }

    private Table createProgression() {

        Table flow = ScreenUI.createPanel();
        flow.add(progressStep("✓", "TEMPORADA REGULAR", "Concluída", ScreenUI.SUCCESS)).growX().padRight(12f);
        flow.add(ScreenUI.createBoldValue(game.skin, "›", ScreenUI.MUTED_TEXT, Align.center)).width(30f);
        flow.add(progressStep(playoffResult().equals("CAMPEÃO") ? "✓" : "○", "PLAYOFFS", playoffResult(), StyleFactory.SOFT_YELLOW)).growX().padRight(12f);
        flow.add(ScreenUI.createBoldValue(game.skin, "›", ScreenUI.MUTED_TEXT, Align.center)).width(30f);
        flow.add(progressStep("◷", "OFF SEASON", "Próxima etapa", StyleFactory.GOLD)).growX();
        return flow;
    }

    private Table progressStep(
        String marker,
        String title,
        String state,
        Color color
    ) {

        Table step = new Table();
        Label icon = ScreenUI.createBoldValue(game.skin, marker, color, Align.center);
        icon.setFontScale(0.86f);
        step.add(icon).width(38f).padRight(6f);
        Table copy = new Table();
        copy.add(ScreenUI.createSubtitle(game.skin, title)).left().row();
        copy.add(ScreenUI.createBoldValue(game.skin, state, Color.WHITE, Align.left)).left().padTop(2f);
        step.add(copy).left();
        return step;
    }

    private Table createActions() {

        Table actions = new Table();

        TextButton standings = ScreenUI.createSecondaryButton(game.skin, "VER TABELA FINAL");
        standings.getLabel().setFontScale(0.48f);
        standings.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StandingsScreen(game, club));
            }
        });

        TextButton enter = ScreenUI.createPrimaryButton(game.skin, "VER APOSENTADORIAS  ›");
        enter.getLabel().setFontScale(0.64f);
        enter.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RetirementSummaryScreen(game, club));
            }
        });

        actions.add(standings).width(230f).height(44f).left().expandX();
        actions.add(enter).width(430f).height(54f).center();
        actions.add().width(230f).expandX();
        return actions;
    }

    private int getFinalPosition() {

        List<StandingsRow> rows = game.league.getFullStandings(null);
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).club == club) return index + 1;
        }
        return game.league.getClubs().size();
    }

    private String playoffResult() {

        if (getChampion() == club) return "CAMPEÃO";
        for (PlayoffSeries series : game.league.getPlayoffSeries()) {
            if (series.getFirstSeed() == club || series.getSecondSeed() == club) return "DISPUTOU";
        }
        return "NÃO SE CLASSIFICOU";
    }

    private String playoffDisplay() {

        return playoffResult().equals("NÃO SE CLASSIFICOU")
            ? "NÃO SE\nCLASSIFICOU"
            : playoffResult();
    }

    private int countExpiringContracts() {

        int count = 0;
        int year = game.league.getCurrentSeason();
        for (Player player : club.getSquad()) {
            if (player.getRemainingContractYears(year) <= 1) count++;
        }
        return count;
    }

    private String record() {

        return club.getTotalWins() + "V  " + club.getTotalDraws() + "E  " + club.getTotalLosses() + "D";
    }

    private Club getChampion() {

        for (PlayoffSeries series : game.league.getPlayoffSeries()) {
            if ("F".equals(series.getId())) return series.getWinner();
        }
        return null;
    }

    private String money(long value) {

        return String.format(Locale.US, "WFL$ %.1fM", value / 1_000_000d);
    }

    private String signedMoney(long value) {

        return (value >= 0 ? "+" : "-") + money(Math.abs(value));
    }

    private Texture loadLogo() {

        if (club == null || club.getLogoPath() == null) return null;
        try {
            if (Gdx.files.internal(club.getLogoPath()).exists()) {
                return new Texture(Gdx.files.internal(club.getLogoPath()));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        stage.dispose();
        if (clubLogo != null) clubLogo.dispose();
    }
}