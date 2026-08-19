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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.screens.NavigationDrawer;
import io.github.some_example_name.screens.CareerOverlay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarScreen implements Screen {
    private final Main game;
    private final Club playerClub;
    private Stage stage;
    private Table matchContainer;
    private Texture calendarBgTexture;
    private boolean filterOnlyMyClub = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM", new Locale("pt", "BR"));
    private SimpleDateFormat monthSdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));

    public CalendarScreen(Main game, Club playerClub) {
        this.game = game;
        this.playerClub = playerClub;
        this.stage = new Stage(new ScreenViewport());
        try {
            this.calendarBgTexture = new Texture(Gdx.files.internal("calendario.png"));
        } catch (Exception e) {
            this.calendarBgTexture = null;
        }
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

        // Plano de fundo calendario.png
        if (calendarBgTexture != null) {
            Image bg = new Image(calendarBgTexture);
            bg.setFillParent(true);
            bg.setScaling(Scaling.fill);
            root.add(bg);
        } else {
            root.add(new Image(game.background));
        }

        // Overlay sutil para sintonizar a imagem de fundo
        Image overlay = new Image(StyleFactory.createSolid(new Color(0, 0, 0, 0.08f)));
        overlay.setFillParent(true);
        root.add(overlay);

        // Conteúdo Principal Centralizado na Tela
        Table mainFrame = new Table();
        mainFrame.setFillParent(true);
        mainFrame.center().pad(20);

        Table content = new Table();
        // Opacidade reduzida (0.65f) para deixar o fundo calendario.png visível de forma sutil e elegante
        content.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.06f, 0.65f), StyleFactory.GOLD));
        content.top().pad(24);

        Label.LabelStyle titleStyle = game.skin.get("title", Label.LabelStyle.class);
        Label headerTitle = new Label("CALENDÁRIO DA TEMPORADA WFL 1969", titleStyle);
        headerTitle.setFontScale(0.95f);
        content.add(headerTitle).padBottom(16).row();

        ImageTextButton filterBtn = IconTextButton.create(filterOnlyMyClub ? "MOSTRAR TODOS OS JOGOS" : "FILTRAR JOGOS DO MEU CLUBE",
            game.skin, "Icons8/icons8-binóculos-50.png");
        filterBtn.setChecked(filterOnlyMyClub);
        filterBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                filterOnlyMyClub = !filterOnlyMyClub;
                refreshUI();
            }
        });
        content.add(filterBtn).width(Math.min(450, Gdx.graphics.getWidth() * 0.35f)).height(54).padBottom(20).row();

        matchContainer = new Table();
        updateMatchTable();

        ScrollPane scroll = new ScrollPane(matchContainer, game.skin);
        content.add(scroll).grow().fill();

        float usableWidth = Math.min(1300f, Gdx.graphics.getWidth() - 80f);
        mainFrame.add(content).width(usableWidth).growY();

        root.add(mainFrame);
        NavigationDrawer.attach(stage, game, playerClub, "CALENDÁRIO");
    }

    private void updateMatchTable() {
        matchContainer.clear();
        List<Match> matches = game.league.getSchedule();
        Match nextMatch = game.league.getNextMatch();

        String lastMonth = "";
        int lastWeek = -1;
        Calendar cal = Calendar.getInstance();

        for (Match m : matches) {
            boolean isMyMatch = (m.getHomeTeam() == playerClub || m.getAwayTeam() == playerClub);
            if (filterOnlyMyClub && !isMyMatch) continue;

            cal.setTime(m.getDate());
            int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
            String currentMonth = monthSdf.format(m.getDate());

            // Cabeçalho de Mês
            if (!currentMonth.equals(lastMonth)) {
                Label monthLabel = new Label(currentMonth.toUpperCase(), game.skin, "font-title");
                monthLabel.setColor(StyleFactory.GOLD);
                monthLabel.setFontScale(0.85f);
                matchContainer.add(monthLabel).colspan(3).padTop(28).padBottom(10).left().growX().row();
                lastMonth = currentMonth;
                lastWeek = -1;
            }

            // Cabeçalho de Semana
            if (currentWeek != lastWeek) {
                Label weekLabel = new Label("SEMANA " + currentWeek, game.skin, "font-bold");
                weekLabel.setColor(StyleFactory.SOFT_YELLOW);
                weekLabel.setFontScale(0.72f);
                matchContainer.add(weekLabel).colspan(3).padTop(14).padBottom(8).left().growX().row();
                lastWeek = currentWeek;
            }

            Table matchRow = new Table();
            boolean isNext = m == nextMatch;
            Color rowColor = isNext ? new Color(0.42f, 0.30f, 0.05f, 0.92f)
                : (isMyMatch ? new Color(0.06f, 0.24f, 0.15f, 0.90f)
                : new Color(0.08f, 0.11f, 0.14f, 0.75f));
            matchRow.background(StyleFactory.createRoundedPanel(rowColor, isNext ? StyleFactory.PLAYOFF_GOLD : StyleFactory.METAL_DARK));

            // Data
            Label dateLabel = new Label(sdf.format(m.getDate()), game.skin, "font-bold");
            dateLabel.setFontScale(0.68f);
            dateLabel.setColor(m.isPlayed() ? Color.GRAY : Color.WHITE);
            matchRow.add(dateLabel).width(190).left().padLeft(16);

            // Confronto (expande horizontalmente ocupando toda a largura da tela)
            Label vsLabel = new Label(m.getHomeTeam().getName() + "   VS   " + m.getAwayTeam().getName(), game.skin, "font-bold");
            vsLabel.setFontScale(0.72f);
            if (isNext) vsLabel.setColor(StyleFactory.SOFT_YELLOW);
            else if (isMyMatch) vsLabel.setColor(StyleFactory.GOLD);
            else vsLabel.setColor(StyleFactory.CREME_AGED);
            matchRow.add(vsLabel).growX().left();

            // Resultado
            String res = isNext ? "PRÓXIMO ⚽" : (m.isPlayed() ? m.getHomeGoals() + " - " + m.getAwayGoals() : "--");
            Label resultLabel = new Label(res, game.skin, "font-bold");
            resultLabel.setFontScale(0.72f);
            if (isNext) resultLabel.setColor(StyleFactory.PLAYOFF_GOLD);
            else if (m.isPlayed()) resultLabel.setColor(Color.WHITE);
            else resultLabel.setColor(Color.GRAY);
            matchRow.add(resultLabel).width(140).center().padRight(16);

            matchContainer.add(matchRow).colspan(3).growX().height(48).padBottom(6).row();
        }
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1);
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
        if (calendarBgTexture != null) calendarBgTexture.dispose();
    }
}
