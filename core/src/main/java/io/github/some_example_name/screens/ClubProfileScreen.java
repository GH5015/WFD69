package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.SeasonHistory;
import io.github.some_example_name.utils.StyleFactory;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClubProfileScreen implements Screen {
    private final Main game;
    private final Club club;
    private Stage stage;
    private Texture logoTexture;
    private Texture kitTexture;
    private Texture starTexture;

    private final Map<String, Drawable> solidDrawableCache = new HashMap<>();

    private String activeTab = "RESUMO";
    private boolean isSummaryExpanded = true;

    private final Color COLOR_BG_PANEL = new Color(0.06f, 0.06f, 0.07f, 0.98f);
    private final Color COLOR_CARD_BG = new Color(0.12f, 0.12f, 0.14f, 1f);
    private final Color COLOR_INNER_BG = new Color(0.08f, 0.08f, 0.09f, 1f);
    private final Color COLOR_DIVIDER  = new Color(0.22f, 0.22f, 0.25f, 1f);

    public ClubProfileScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ScreenViewport());

        try {
            logoTexture = new Texture(Gdx.files.internal(club.getLogoPath()));
        } catch (Exception e) {
            logoTexture = new Texture(Gdx.files.internal("libgdx.png"));
        }

        try {
            if (Gdx.files.internal("uniforme_santos.png").exists()) {
                kitTexture = new Texture(Gdx.files.internal("uniforme_santos.png"));
            } else {
                kitTexture = null;
            }
        } catch (Exception e) {
            kitTexture = null;
        }

        try {
            if (Gdx.files.internal("Icons8/icons8-estrela-48.png").exists()) {
                starTexture = new Texture(Gdx.files.internal("Icons8/icons8-estrela-48.png"));
            } else {
                starTexture = null;
            }
        } catch (Exception e) {
            starTexture = null;
        }
    }

    private Drawable getSolidDrawable(Color color) {
        String key = color.toString();
        if (solidDrawableCache.containsKey(key)) {
            return solidDrawableCache.get(key);
        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Drawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        solidDrawableCache.put(key, drawable);
        return drawable;
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
        NavigationDrawer.attach(stage, game, club, "PERFIL");

        Table outerTable = new Table();
        outerTable.top().padTop(20).padBottom(20);

        Table content = new Table();
        content.background(getSolidDrawable(COLOR_BG_PANEL));
        content.pad(25);

        content.add(createHeaderTable()).growX().padBottom(18).row();
        content.add(createTabsBar()).growX().padBottom(18).row();

        Table tabContent = new Table();
        if (activeTab.equals("RESUMO")) {
            tabContent.add(createResumoTab()).growX().row();
        } else if (activeTab.equals("HISTÓRIA")) {
            tabContent.add(createHistoriaTab()).growX().row();
        } else if (activeTab.equals("INFRAESTRUTURA")) {
            tabContent.add(createInfraestruturaTab()).growX().row();
        }

        content.add(tabContent).growX().row();
        outerTable.add(content).width(1080).center();

        ScrollPane scroll = new ScrollPane(outerTable, game.skin);
        scroll.setFadeScrollBars(false);
        root.add(scroll);
    }

    private Table createHeaderTable() {
        Table table = new Table();
        table.background(getSolidDrawable(COLOR_CARD_BG));
        table.pad(15);

        Label title = new Label(club.getName().toUpperCase(), game.skin, "font-title");
        title.setFontScale(1.6f);
        title.setColor(StyleFactory.GOLD);

        String countryStr = (club.getCountry() != null) ? club.getCountry() : "Brasil";
        Label subTitle = new Label("Fundado: 1969  •  País: " + countryStr + " (BR)", game.skin, "font-bold");
        subTitle.setFontScale(0.70f);
        subTitle.setColor(Color.WHITE);

        table.add(title).center().row();
        table.add(subTitle).center().padTop(4);

        return table;
    }

    private Table createTabsBar() {
        Table bar = new Table();
        String[] tabs = {"RESUMO", "HISTÓRIA", "INFRAESTRUTURA"};

        for (String tab : tabs) {
            TextButton btn = new TextButton(tab, game.skin);
            btn.getLabel().setFontScale(0.68f);

            if (tab.equals(activeTab)) {
                btn.setColor(StyleFactory.GOLD);
                btn.getLabel().setColor(Color.BLACK);
            } else {
                btn.setColor(COLOR_CARD_BG);
                btn.getLabel().setColor(Color.WHITE);
            }

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    activeTab = tab;
                    refreshUI();
                }
            });

            bar.add(btn).width(210).height(42).padRight(12);
        }
        return bar;
    }

    private Table createResumoTab() {
        Table resumoTable = new Table();
        Table mainGrid = new Table();
        mainGrid.defaults().top();

        Table leftCol = new Table();
        leftCol.background(getSolidDrawable(COLOR_CARD_BG));
        leftCol.pad(18);

        Image logoImg = new Image(new TextureRegionDrawable(logoTexture));
        logoImg.setScaling(Scaling.fit);
        leftCol.add(logoImg).width(220).height(170).center().padBottom(15).row();

        leftCol.add(createDivider()).growX().padBottom(15).row();

        Label kitTitle = new Label("UNIFORME CASA", game.skin, "font-bold");
        kitTitle.setFontScale(0.65f);
        kitTitle.setColor(StyleFactory.GOLD);
        leftCol.add(kitTitle).left().padBottom(8).row();

        Table kitBox = new Table();
        kitBox.background(getSolidDrawable(COLOR_INNER_BG));

        if (kitTexture != null) {
            Image kitImg = new Image(new TextureRegionDrawable(kitTexture));
            kitImg.setScaling(Scaling.fit);
            kitBox.add(kitImg).width(180).height(150).pad(10);
        } else {
            Label kitDesc = new Label("Titular 1969", game.skin);
            kitDesc.setFontScale(0.65f);
            kitBox.add(kitDesc).pad(20);
        }

        leftCol.add(kitBox).growX().padBottom(15).row();
        leftCol.add(createDivider()).growX().padBottom(15).row();

        Label presTitle = new Label("PRESIDENTE / MANAGER", game.skin, "font-bold");
        presTitle.setFontScale(0.60f);
        presTitle.setColor(StyleFactory.GOLD);
        leftCol.add(presTitle).left().row();

        Label presName = new Label("Você (Manager)", game.skin, "font-bold");
        presName.setFontScale(0.70f);
        presName.setColor(Color.WHITE);
        leftCol.add(presName).left().row();

        Label presSince = new Label("Desde: 1969", game.skin);
        presSince.setFontScale(0.58f);
        presSince.setColor(Color.LIGHT_GRAY);
        leftCol.add(presSince).left().padBottom(5);

        mainGrid.add(leftCol).width(300).padRight(18);

        Table rightCol = new Table();
        rightCol.background(getSolidDrawable(COLOR_CARD_BG));
        rightCol.pad(22).left();

        Label infoTitle = new Label("INFORMAÇÕES GERAIS", game.skin, "font-title");
        infoTitle.setFontScale(0.78f);
        infoTitle.setColor(StyleFactory.GOLD);
        rightCol.add(infoTitle).left().padBottom(12).row();

        int rep = Math.max(1, Math.min(5, club.getReputation()));
        int overall = (int) (club.getOverall() > 0 ? club.getOverall() : 70);
        int capacity = club.getStadiumCapacity() > 0 ? club.getStadiumCapacity() : 15000;

        long fanbase = (long) (Math.pow(rep, 2.2) * 95_000L) + ((long) capacity * 12L) + ((long) overall * 3_500L);
        double conversionRate = 0.035 + (rep * 0.005);
        long shirts = (long) (fanbase * conversionRate) + (long) (Math.pow(overall, 2) * 6.5);

        NumberFormat fmt = NumberFormat.getInstance(new Locale("pt", "BR"));

        Table infoGrid = new Table();
        infoGrid.defaults().left().padBottom(8);

        infoGrid.add(new Label("Reputação:", game.skin, "font-bold")).width(180);
        infoGrid.add(createStarsWidget(rep)).row();

        infoGrid.add(new Label("Torcida Estimada:", game.skin, "font-bold"));
        infoGrid.add(new Label(fmt.format(fanbase) + " torcedores", game.skin)).row();

        infoGrid.add(new Label("Camisas Vendidas:", game.skin, "font-bold"));
        infoGrid.add(new Label(fmt.format(shirts) + " / ano", game.skin)).row();

        rightCol.add(infoGrid).growX().padBottom(18).row();
        rightCol.add(createDivider()).growX().padBottom(18).row();

        Label stadiumTitle = new Label("ESTÁDIO", game.skin, "font-title");
        stadiumTitle.setFontScale(0.78f);
        stadiumTitle.setColor(StyleFactory.GOLD);
        rightCol.add(stadiumTitle).left().padBottom(12).row();

        Table stadiumGrid = new Table();
        stadiumGrid.defaults().left().padBottom(8);

        stadiumGrid.add(new Label("Nome:", game.skin, "font-bold")).width(180);
        stadiumGrid.add(new Label(club.getStadium(), game.skin)).row();

        stadiumGrid.add(new Label("Capacidade:", game.skin, "font-bold"));
        stadiumGrid.add(new Label(fmt.format(club.getStadiumCapacity()) + " pessoas", game.skin)).row();

        stadiumGrid.add(new Label("Gramado:", game.skin, "font-bold"));
        Label lawn = new Label("Excelente", game.skin);
        lawn.setColor(Color.GREEN);
        stadiumGrid.add(lawn).row();

        rightCol.add(stadiumGrid).growX().padBottom(18).row();
        rightCol.add(createDivider()).growX().padBottom(18).row();

        Label rivalsTitle = new Label("RIVAIS REGIONAIS", game.skin, "font-title");
        rivalsTitle.setFontScale(0.78f);
        rivalsTitle.setColor(StyleFactory.GOLD);
        rightCol.add(rivalsTitle).left().padBottom(12).row();

        Label rivalsList = new Label("• Rio Imperial FC     • Atlético Guanabara     • Serrano EC", game.skin);
        rivalsList.setFontScale(0.65f);
        rivalsList.setColor(Color.LIGHT_GRAY);
        rightCol.add(rivalsList).left().row();

        mainGrid.add(rightCol).growX();
        resumoTable.add(mainGrid).growX().row();

        Table accordion = new Table();
        accordion.background(getSolidDrawable(COLOR_CARD_BG));
        accordion.pad(15);

        Table accordionHeader = new Table();
        Label accTitle = new Label("Resumo e Filosofia do Clube", game.skin, "font-bold");
        accTitle.setFontScale(0.70f);
        accTitle.setColor(StyleFactory.GOLD);

        Label accIcon = new Label(isSummaryExpanded ? "▲" : "▼", game.skin, "font-bold");
        accIcon.setColor(Color.WHITE);

        accordionHeader.add(accTitle).left().expandX();
        accordionHeader.add(accIcon).right();

        accordionHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isSummaryExpanded = !isSummaryExpanded;
                refreshUI();
            }
        });

        accordion.add(accordionHeader).growX().row();

        if (isSummaryExpanded) {
            accordion.add(createDivider()).growX().padTop(8).padBottom(8).row();
            String descText = "O " + club.getName() + " é um clube de grande tradição no cenário nacional. " +
                "A equipe manda seus jogos no estádio " + club.getStadium() + " e prioriza como filosofia " +
                "\"" + club.getPhilosophy() + "\". Atualmente, adota uma postura tática " + club.getMentality() + ".";

            Label descLabel = new Label(descText, game.skin);
            descLabel.setWrap(true);
            descLabel.setFontScale(0.62f);
            descLabel.setColor(Color.WHITE);
            accordion.add(descLabel).growX().padTop(5);
        }

        resumoTable.add(accordion).growX().padTop(18).row();

        return resumoTable;
    }

    // ==========================================
    // ABA HISTÓRIA DINÂMICA
    // ==========================================
    private Table createHistoriaTab() {
        Table historiaTable = new Table();
        historiaTable.top().left();

        Table topStats = new Table();
        topStats.background(getSolidDrawable(COLOR_CARD_BG));
        topStats.pad(15);

        int seasonsCount = club.getSeasonHistories().size() + 1;
        String valFormatted = String.format(new Locale("pt", "BR"), "€ %.2f M", club.getBudget() > 0 ? club.getBudget() : 1.20);

        topStats.add(createMiniStatBox("TÍTULOS", String.valueOf(club.getTitlesCount()), StyleFactory.GOLD)).expandX().fillX();
        topStats.add(createMiniStatBox("TEMPORADAS", seasonsCount + "ª (" + club.getCurrentYear() + ")", Color.WHITE)).expandX().fillX();
        topStats.add(createMiniStatBox("VALOR DO CLUBE", valFormatted, Color.GREEN)).expandX().fillX();
        topStats.add(createMiniStatBox("MAIOR INVICTO", club.getMaxUnbeatenStreak() + " jogos", Color.LIGHT_GRAY)).expandX().fillX();
        topStats.add(createMiniStatBox("MAIOR VITÓRIA", club.getBiggestWin(), Color.WHITE)).expandX().fillX();

        historiaTable.add(topStats).growX().padBottom(18).row();

        Table grid = new Table();
        grid.defaults().top();

        Table leftCol = new Table();

        Table statsCard = new Table();
        statsCard.background(getSolidDrawable(COLOR_CARD_BG));
        statsCard.pad(18);

        Label statsTitle = new Label("ESTATÍSTICAS HISTÓRICAS", game.skin, "font-title");
        statsTitle.setFontScale(0.78f);
        statsTitle.setColor(StyleFactory.GOLD);
        statsCard.add(statsTitle).left().padBottom(12).row();

        Table statsGrid = new Table();
        statsGrid.defaults().pad(5).growX();

        int saldo = club.getGoalDifference();
        String saldoText = (saldo > 0 ? "+" : "") + saldo;

        statsGrid.add(createStatCell("Jogos Totais", String.valueOf(club.getTotalGames()), Color.WHITE));
        statsGrid.add(createStatCell("Aproveitamento", club.getWinPercentage() + "%", Color.WHITE)).row();

        statsGrid.add(createStatCell("Vitórias", String.valueOf(club.getTotalWins()), Color.GREEN));
        statsGrid.add(createStatCell("Empates", String.valueOf(club.getTotalDraws()), Color.YELLOW)).row();

        statsGrid.add(createStatCell("Derrotas", String.valueOf(club.getTotalLosses()), new Color(0.9f, 0.3f, 0.3f, 1f)));
        statsGrid.add(createStatCell("Saldo de Gols", saldoText, saldo >= 0 ? StyleFactory.GOLD : Color.RED)).row();

        statsGrid.add(createStatCell("Gols Marcados", String.valueOf(club.getGoalsFor()), Color.WHITE));
        statsGrid.add(createStatCell("Gols Sofridos", String.valueOf(club.getGoalsAgainst()), Color.LIGHT_GRAY)).row();

        statsCard.add(statsGrid).growX().row();
        leftCol.add(statsCard).growX().padBottom(18).row();

        Table recordsCard = new Table();
        recordsCard.background(getSolidDrawable(COLOR_CARD_BG));
        recordsCard.pad(18);

        Label recordsTitle = new Label("RECORDES DO CLUBE", game.skin, "font-title");
        recordsTitle.setFontScale(0.78f);
        recordsTitle.setColor(StyleFactory.GOLD);
        recordsCard.add(recordsTitle).left().padBottom(12).row();

        recordsCard.add(createRecordRow("Maior Artilheiro", club.getTopScorerName(), club.getTopScorerGoals() + " gols")).growX().padBottom(8).row();
        recordsCard.add(createDivider()).growX().padBottom(8).row();
        recordsCard.add(createRecordRow("Mais Jogos", club.getMostGamesPlayerName(), club.getMostGamesCount() + " jogos")).growX().padBottom(8).row();
        recordsCard.add(createDivider()).growX().padBottom(8).row();
        recordsCard.add(createRecordRow("Mais Assistências", club.getTopAssisterName(), club.getTopAssisterCount() + " assist.")).growX();

        leftCol.add(recordsCard).growX();
        grid.add(leftCol).width(500).padRight(18);

        Table seasonsCard = new Table();
        seasonsCard.background(getSolidDrawable(COLOR_CARD_BG));
        seasonsCard.pad(18);

        Label seasonsTitle = new Label("HISTÓRICO DE TEMPORADAS", game.skin, "font-title");
        seasonsTitle.setFontScale(0.78f);
        seasonsTitle.setColor(StyleFactory.GOLD);
        seasonsCard.add(seasonsTitle).left().padBottom(12).row();

        Table headerRow = new Table();
        headerRow.background(getSolidDrawable(COLOR_INNER_BG));
        headerRow.pad(8);
        headerRow.add(new Label("ANO", game.skin, "font-bold")).width(100).left();
        headerRow.add(new Label("LIGA", game.skin, "font-bold")).width(140).center();
        headerRow.add(new Label("COPA", game.skin, "font-bold")).width(140).center();
        seasonsCard.add(headerRow).growX().padBottom(6).row();

        Table seasonsTable = new Table();
        seasonsTable.top().defaults().pad(6);

        addSeasonRow(seasonsTable, String.valueOf(club.getCurrentYear()), "Em Andamento", "Em Andamento", true);

        for (SeasonHistory history : club.getSeasonHistories()) {
            addSeasonRow(seasonsTable, String.valueOf(history.getYear()), history.getLigaResult(), history.getCopaResult(), false);
        }

        ScrollPane seasonScroll = new ScrollPane(seasonsTable, game.skin);
        seasonScroll.setFadeScrollBars(false);
        seasonsCard.add(seasonScroll).growX().height(330);

        grid.add(seasonsCard).growX();
        historiaTable.add(grid).growX();

        return historiaTable;
    }

    private void addSeasonRow(Table table, String year, String liga, String copa, boolean isCurrent) {
        Table row = new Table();
        row.pad(8);
        row.background(getSolidDrawable(COLOR_INNER_BG));

        Label yearLbl = new Label(year, game.skin, "font-bold");
        yearLbl.setFontScale(0.65f);

        Label ligaLbl = new Label(liga, game.skin);
        ligaLbl.setFontScale(0.65f);
        if (isCurrent || liga.contains("🥇")) ligaLbl.setColor(StyleFactory.GOLD);

        Label copaLbl = new Label(copa, game.skin);
        copaLbl.setFontScale(0.65f);
        if (isCurrent || copa.contains("🥇")) copaLbl.setColor(StyleFactory.GOLD);

        row.add(yearLbl).width(100).left();
        row.add(ligaLbl).width(140).center();
        row.add(copaLbl).width(140).center();

        table.add(row).growX().row();
    }

    private Table createStarsWidget(int rating) {
        Table starsTable = new Table();
        for (int i = 0; i < 5; i++) {
            if (starTexture != null) {
                Image starImg = new Image(new TextureRegionDrawable(starTexture));
                starImg.setScaling(Scaling.fit);
                if (i < rating) {
                    starImg.setColor(StyleFactory.GOLD);
                } else {
                    starImg.setColor(new Color(0.25f, 0.25f, 0.25f, 0.8f));
                }
                starsTable.add(starImg).width(26).height(26).padRight(4);
            } else {
                Label star = new Label("*", game.skin, "font-title");
                star.setFontScale(1.1f);
                star.setColor(i < rating ? StyleFactory.GOLD : Color.DARK_GRAY);
                starsTable.add(star).padRight(4);
            }
        }
        return starsTable;
    }

    private Table createInfraestruturaTab() {
        Table t = new Table();
        t.background(getSolidDrawable(COLOR_CARD_BG));
        t.pad(25);
        Label title = new Label("CENTRO DE TREINAMENTO E INFRAESTRUTURA", game.skin, "font-title");
        title.setColor(StyleFactory.GOLD);
        t.add(title).padBottom(15).row();

        Table grid = new Table();
        grid.defaults().left().pad(6);
        grid.add(new Label("Nível do CT:", game.skin, "font-bold")).width(200);
        grid.add(createStarsWidget(3)).row();
        grid.add(new Label("Categorias de Base:", game.skin, "font-bold"));
        grid.add(createStarsWidget(2)).row();
        grid.add(new Label("Departamento Médico:", game.skin, "font-bold"));
        grid.add(createStarsWidget(4)).row();

        t.add(grid);
        return t;
    }

    private Table createMiniStatBox(String title, String value, Color valueColor) {
        Table box = new Table();

        Label titleLbl = new Label(title, game.skin, "font-bold");
        titleLbl.setFontScale(0.52f);
        titleLbl.setColor(Color.LIGHT_GRAY);

        Label valLbl = new Label(value, game.skin, "font-title");
        valLbl.setFontScale(0.85f);
        valLbl.setColor(valueColor);

        box.add(titleLbl).row();
        box.add(valLbl).padTop(2);
        return box;
    }

    private Table createStatCell(String labelText, String valueText, Color valueColor) {
        Table cell = new Table();
        cell.background(getSolidDrawable(COLOR_INNER_BG));
        cell.pad(8);

        Label lbl = new Label(labelText, game.skin);
        lbl.setFontScale(0.58f);
        lbl.setColor(Color.LIGHT_GRAY);

        Label val = new Label(valueText, game.skin, "font-bold");
        val.setFontScale(0.72f);
        val.setColor(valueColor);

        cell.add(lbl).left().expandX();
        cell.add(val).right();
        return cell;
    }

    private Table createRecordRow(String category, String name, String stat) {
        Table row = new Table();

        Label catLbl = new Label(category, game.skin, "font-bold");
        catLbl.setFontScale(0.58f);
        catLbl.setColor(StyleFactory.GOLD);

        Label nameLbl = new Label(name, game.skin, "font-bold");
        nameLbl.setFontScale(0.70f);
        nameLbl.setColor(Color.WHITE);

        Label statLbl = new Label(stat, game.skin);
        statLbl.setFontScale(0.65f);
        statLbl.setColor(Color.LIGHT_GRAY);

        row.add(catLbl).left().row();
        row.add(nameLbl).left().expandX();
        row.add(statLbl).right();
        return row;
    }

    private Table createDivider() {
        Table line = new Table();
        line.background(getSolidDrawable(COLOR_DIVIDER));
        return line;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (logoTexture != null) logoTexture.dispose();
        if (kitTexture != null) kitTexture.dispose();
        if (starTexture != null) starTexture.dispose();

        for (Drawable d : solidDrawableCache.values()) {
            if (d instanceof TextureRegionDrawable) {
                TextureRegionDrawable trd = (TextureRegionDrawable) d;
                trd.getRegion().getTexture().dispose();
            }
        }
        solidDrawableCache.clear();
    }
}
