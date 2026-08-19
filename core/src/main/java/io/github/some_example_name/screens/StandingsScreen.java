package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.PlayerStats;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.IconTextButton;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StandingsScreen implements Screen {
    private final Main game;
    private final Club playerClub;
    private Stage stage;
    private Texture tableTexture;
    private String viewMode = "CLASSIFICACAO";
    private String tableScope = "CONFERENCIAS";
    private String statsType = "Gols";
    private final Map<String, Texture> clubLogos = new HashMap<>();

    public StandingsScreen(Main game, Club playerClub) {
        this.game = game;
        this.playerClub = playerClub;
        this.stage = new Stage(new ScreenViewport());
        this.tableTexture = new Texture(Gdx.files.internal("tabela.png"));
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
        Image tableBackground = new Image(tableTexture);
        tableBackground.setFillParent(true);
        tableBackground.setScaling(Scaling.fill);
        root.add(tableBackground);
        stage.addActor(root);
        NavigationDrawer.attach(stage, game, playerClub, "TABELA");

        Table mainContent = new Table();
        mainContent.top().pad(10, 18, 18, 18);

        Table heading = new Table();
        heading.background(StyleFactory.createRoundedPanel(
            new Color(0.20f, 0.105f, 0.045f, 0.72f), StyleFactory.GOLD));
        heading.pad(8, 20, 8, 20);
        Label headingTitle = new Label("CLASSIFICACAO DA LIGA", game.skin, "font-title");
        headingTitle.setFontScale(0.82f);
        heading.add(headingTitle).left().expandX();
        Label headingSeason = new Label("TEMPORADA " + game.league.getCurrentSeason(), game.skin, "font-bold");
        headingSeason.setColor(StyleFactory.SOFT_YELLOW);
        heading.add(headingSeason).right();
        mainContent.add(heading).colspan(2).growX().height(58).padBottom(8).row();

        Table controls = new Table();
        controls.background(StyleFactory.createRoundedPanel(new Color(0.055f, 0.18f, 0.12f, 0.72f), StyleFactory.GOLD));
        controls.pad(8, 14, 8, 14);
        ImageTextButton btnTable = IconTextButton.create("TABELA LIGA", game.skin, "Icons8/icons8-lista-50.png");
        btnTable.setChecked(viewMode.equals("CLASSIFICACAO"));
        btnTable.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { viewMode = "CLASSIFICACAO"; refreshUI(); }
        });
        ImageTextButton btnPlayoffs = IconTextButton.create("PLAYOFFS", game.skin, "Icons8/icons8-estádio-50.png");
        btnPlayoffs.setChecked(viewMode.equals("PLAYOFFS"));
        btnPlayoffs.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { viewMode = "PLAYOFFS"; refreshUI(); }
        });
        controls.add(btnTable).width(220).height(48).padRight(8);
        controls.add(btnPlayoffs).width(190).height(48).padRight(18);

        if (viewMode.equals("CLASSIFICACAO")) {
            ImageTextButton btnConf = IconTextButton.create("POR CONFERÊNCIAS", game.skin, "Icons8/icons8-quatro-quadrados-50.png");
            btnConf.setChecked(tableScope.equals("CONFERENCIAS"));
            btnConf.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) { tableScope = "CONFERENCIAS"; refreshUI(); }
            });
            ImageTextButton btnGeral = IconTextButton.create("CLASSIFICAÇÃO GERAL", game.skin, "Icons8/icons8-estrutura-em-árvore-50.png");
            btnGeral.setChecked(tableScope.equals("GERAL"));
            btnGeral.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) { tableScope = "GERAL"; refreshUI(); }
            });
            controls.add(btnConf).width(230).height(48).padRight(8);
            controls.add(btnGeral).width(250).height(48);
            mainContent.add(controls).colspan(2).center().padBottom(10).row();
            renderClassificationView(mainContent);
        } else {
            mainContent.add(controls).colspan(2).center().padBottom(10).row();
            renderPlayoffsView(mainContent);
        }

        Table contentFrame = new Table();
        contentFrame.setFillParent(true);
        contentFrame.center();
        contentFrame.pad(38, 56, 26, 56);
        float frameWidth = Math.min(1580f, Math.max(900f, Gdx.graphics.getWidth() - 130f));
        contentFrame.add(mainContent).width(frameWidth).top();
        root.add(contentFrame);
    }

    private void renderClassificationView(Table container) {
        Table leftSide = new Table();
        leftSide.background(StyleFactory.createRoundedPanel(new Color(0.055f, 0.18f, 0.12f, 0.80f), StyleFactory.GOLD));
        leftSide.pad(10);
        if (tableScope.equals("CONFERENCIAS")) {
            addConferenceTable(leftSide, "Ocidental");
            leftSide.row().padTop(10);
            addConferenceTable(leftSide, "Oriental");
        } else {
            addConferenceTable(leftSide, null);
        }
        Table rightSide = new Table();
        rightSide.background(StyleFactory.createRoundedPanel(new Color(0.055f, 0.18f, 0.12f, 0.80f), StyleFactory.GOLD));
        rightSide.pad(12);
        addPlayerStatsTable(rightSide);
        if (Gdx.graphics.getWidth() < 1120) {
            container.add(leftSide).top().growX().pad(10).row();
            container.add(rightSide).top().growX().pad(10).row();
        } else {
            container.add(leftSide).top().pad(5).padRight(20);
            container.add(rightSide).top().pad(5);
        }
    }

    private void renderPlayoffsView(Table container) {
        container.add(new Label("SÉRIES DE PLAYOFF", game.skin, "font-title")).colspan(2).padBottom(30).row();
        List<String> summaries = game.league.getPlayoffSeriesSummaries();
        if (summaries.isEmpty()) {
            container.add(new Label("OS PLAYOFFS COMEÇAM EM DEZEMBRO.", game.skin)).colspan(2).padTop(50);
        } else {
            for (String s : summaries) {
                container.add(new Label(s, game.skin)).pad(10).row();
            }
        }
    }

    private void addConferenceTable(Table container, String confName) {
        String titleText = (confName == null) ? "CLASSIFICAÇÃO GERAL DA WFL 1969" : "CONFERÊNCIA " + confName.toUpperCase();
        Label title = new Label(titleText, game.skin, "font-title");
        title.setFontScale(0.74f);
        title.setColor(StyleFactory.GOLD);
        container.add(title).colspan(10).padBottom(8).left().row();

        Table table = new Table();
        String[] headers = {"#", "", "CLUBE", "PTS", "J", "V", "E", "D", "SG", "CS"};
        float[] widths = {38, 54, 280, 58, 44, 44, 44, 44, 48, 48};
        for (int i = 0; i < headers.length; i++) {
            Label header = new Label(headers[i], game.skin, "font-bold");
            header.setFontScale(0.60f);
            header.setColor(StyleFactory.SOFT_YELLOW);
            table.add(header).width(widths[i]).height(28).center();
        }
        table.row().padBottom(4);

        List<StandingsRow> rows = game.league.getFullStandings(confName);
        int pos = 1;
        int playoffCutoff = (confName == null) ? 8 : (confName.equals("Ocidental") ? 6 : 2);

        for (StandingsRow r : rows) {
            boolean isUserClub = (r.club == playerClub);
            boolean inPlayoffs = (pos <= playoffCutoff);

            Label posLabel = new Label(pos + "º", game.skin, "font-bold");
            posLabel.setFontScale(0.60f);
            posLabel.setColor(inPlayoffs ? StyleFactory.PLAYOFF_GOLD : Color.LIGHT_GRAY);
            table.add(posLabel).center();

            Image clubLogo = new Image(new TextureRegionDrawable(loadClubLogo(r.club)));
            clubLogo.setScaling(Scaling.fit);
            table.add(clubLogo).width(50).height(30).center();

            TextButton clubBtn = new TextButton(r.club.getName(), game.skin, "toggle");
            clubBtn.getLabel().setFontScale(0.65f);
            if (isUserClub) {
                clubBtn.setColor(StyleFactory.GOLD);
                clubBtn.getLabel().setColor(Color.WHITE);
            }
            clubBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new ClubDetailsScreen(game, r.club, playerClub));
                }
            });
            table.add(clubBtn).width(widths[2]).height(32).left();

            addTableValue(table, String.valueOf(r.points), widths[3], isUserClub ? StyleFactory.SOFT_YELLOW : Color.WHITE);
            addTableValue(table, String.valueOf(r.matches), widths[4], Color.LIGHT_GRAY);
            addTableValue(table, String.valueOf(r.wins), widths[5], Color.WHITE);
            addTableValue(table, String.valueOf(r.draws), widths[6], Color.LIGHT_GRAY);
            addTableValue(table, String.valueOf(r.losses), widths[7], Color.WHITE);
            addTableValue(table, String.valueOf(r.goalDifference), widths[8], r.goalDifference > 0 ? Color.GREEN : (r.goalDifference < 0 ? Color.SALMON : Color.WHITE));
            addTableValue(table, String.valueOf(r.cleanSheets), widths[9], Color.WHITE);
            table.row().padBottom(3);

            if (pos == playoffCutoff) {
                Table playoffDivider = new Table();
                playoffDivider.background(StyleFactory.createSolid(StyleFactory.GOLD));
                Label cutoffTag = new Label("--- ZONA DE CLASSIFICAÇÃO AOS PLAYOFFS ---", game.skin, "font-bold");
                cutoffTag.setFontScale(0.48f);
                cutoffTag.setColor(StyleFactory.SOFT_YELLOW);
                playoffDivider.add(cutoffTag).center();
                table.add(playoffDivider).colspan(headers.length).growX().height(18).padTop(3).padBottom(4).row();
            }

            pos++;
        }
        container.add(table).row();
    }

    private void addTableValue(Table table, String value, float width, Color color) {
        Label label = new Label(value, game.skin, "font-bold");
        label.setFontScale(0.60f);
        label.setColor(color);
        table.add(label).width(width).height(32).center();
    }

    private void addPlayerStatsTable(Table container) {
        Label title = new Label("LÍDERES DA LIGA 1969", game.skin, "font-title");
        title.setFontScale(0.70f);
        title.setColor(StyleFactory.GOLD);
        container.add(title).colspan(4).padBottom(2).row();

        Label subtitle = new Label("ESTATÍSTICAS INDIVIDUAIS", game.skin, "font-bold");
        subtitle.setColor(StyleFactory.SOFT_YELLOW);
        subtitle.setFontScale(0.54f);
        container.add(subtitle).colspan(4).padBottom(10).row();

        Table selector = new Table();
        String[] types = {"Gols", "Assists", "Amarelos", "Vermelhos"};
        for (String type : types) {
            TextButton tb = new TextButton(type, game.skin);
            tb.getLabel().setFontScale(0.58f);
            if (type.equals(statsType)) tb.setColor(StyleFactory.GOLD);
            tb.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { statsType = type; refreshUI(); }
            });
            selector.add(tb).width(110).height(38).pad(3);
        }
        container.add(selector).colspan(4).padBottom(10).row();

        List<PlayerStats> stats = game.league.getPlayerStats(statsType);
        int pPos = 1;
        for (PlayerStats ps : stats) {
            if (pPos > 15) break;

            // 1. Posição no Ranking (Estilizada por Pódio)
            Label rank = new Label(pPos + ".", game.skin, "font-bold");
            rank.setFontScale(0.64f);
            if (pPos == 1) rank.setColor(StyleFactory.PLAYOFF_GOLD);
            else if (pPos == 2) rank.setColor(Color.LIGHT_GRAY);
            else if (pPos == 3) rank.setColor(Color.valueOf("CD7F32")); // Bronze
            else rank.setColor(Color.WHITE);

            container.add(rank).width(34).height(30).left();

            // 2. Nome do Clube (Estilizado com fonte em negrito e tom Azul Pastel / Cyan)
            Club club = findClubByPlayer(ps.player);
            String clubName = (club != null) ? club.getName().toUpperCase() : "SEM CLUBE";

            Label clubLabel = new Label(clubName, game.skin, "font-bold");
            clubLabel.setFontScale(0.52f);
            clubLabel.setColor(Color.valueOf("A0C4FF")); // Azul suave moderno
            container.add(clubLabel).width(120).height(30).left().padRight(50);

            // 3. Nome do Jogador e Posição (Estilizados com destaque limpo em branco)
            Label player = new Label(ps.player.getName() + " (" + ps.player.getPosition() + ")", game.skin, "font-bold");
            player.setFontScale(0.62f);
            player.setColor(Color.WHITE);
            container.add(player).width(184).height(32).left();

            // 4. Valor da Estatística
            int val = (statsType.equals("Gols") ? ps.goals : statsType.equals("Assists") ? ps.assists : statsType.equals("Amarelos") ? ps.yellowCards : ps.redCards);
            Label value = new Label(String.valueOf(val), game.skin, "font-bold");
            value.setFontScale(0.72f);
            value.setColor(StyleFactory.SOFT_YELLOW);
            container.add(value).width(54).height(32).center().row();

            pPos++;
        }
    }

    // Método auxiliar para localizar o clube do jogador
    private Club findClubByPlayer(io.github.some_example_name.model.Player player) {
        if (player == null || game.league == null || game.league.getClubs() == null) return null;
        for (Club c : game.league.getClubs()) {
            if (c.getSquad() != null && c.getSquad().contains(player)) {
                return c;
            }
        }
        return null;
    }

    private Texture loadClubLogo(io.github.some_example_name.model.Club club) {
        if (club == null) return null;
        Texture texture = clubLogos.get(club.getLogoPath());
        if (texture == null) {
            try { texture = new Texture(Gdx.files.internal(club.getLogoPath())); }
            catch (Exception e) { texture = new Texture(Gdx.files.internal("libgdx.png")); }
            clubLogos.put(club.getLogoPath(), texture);
        }
        return texture;
    }

    private Table createSidebar() {
        Table sidebar = new Table();
        sidebar.background(StyleFactory.createSolid(Color.valueOf("111111")));
        String[] btns = {"PERFIL", "ELENCO", "TATICAS", "TABELA", "CALENDARIO"};
        for (String b : btns) {
            TextButton tb = new TextButton(b, game.skin);
            if (b.equals("TABELA")) tb.setDisabled(true);
            tb.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (b.equals("PERFIL")) game.setScreen(new ClubProfileScreen(game, playerClub));
                    if (b.equals("ELENCO")) game.setScreen(new ClubManagementScreen(game, playerClub));
                    if (b.equals("TATICAS")) game.setScreen(new TacticsScreen(game, playerClub));
                    if (b.equals("CALENDARIO")) game.setScreen(new CalendarScreen(game, playerClub));
                }
            });
            sidebar.add(tb).width(300).height(80).pad(10).row();
        }
        return sidebar;
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        if (tableTexture != null) tableTexture.dispose();
        for (Texture texture : clubLogos.values()) texture.dispose();
        clubLogos.clear();
    }
}
