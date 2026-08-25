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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.model.TradeRecord;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Entrada do mercado: concentra o histórico antes da central de negociações. */
public class TradeHubScreen implements Screen {
    private static final int HISTORY_LIMIT = 8;

    private final Main game;
    private final Club userClub;
    private final Stage stage;
    private final Texture backgroundTexture;

    public TradeHubScreen(Main game, Club userClub) {
        this.game = game;
        this.userClub = userClub;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("prancheta.png"));
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

        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);
        root.add(background);

        Table page = ScreenUI.createPage(true);
        page.add(ScreenUI.createHeader(
                game.skin,
                "MERCADO DE TROCAS",
                "TEMPORADA " + game.league.getCurrentSeason() + " • "
                    + SeasonCalendar.getTradeStatus(game.league, userClub)
            ))
            .growX().height(ScreenUI.HEADER_HEIGHT).padBottom(10f).row();

        List<TradeRecord> leagueTrades = recentTrades(false);
        List<TradeRecord> clubTrades = recentTrades(true);

        Table overview = new Table();
        overview.add(ScreenUI.createStatusBox(
                game.skin, "ÚLTIMAS TROCAS DA LIGA", leagueTrades.size() + " recentes", StyleFactory.SOFT_YELLOW
            )).growX().uniformX().height(62f).padRight(10f);
        overview.add(ScreenUI.createStatusBox(
                game.skin, "NEGOCIAÇÕES DO " + ScreenUI.shorten(userClub.getName().toUpperCase(), 18),
                clubTrades.size() + " recentes", ScreenUI.SUCCESS
            )).growX().uniformX().height(62f);
        page.add(overview).growX().padBottom(10f).row();

        Table histories = new Table();
        histories.add(createHistoryPanel("MOVIMENTAÇÕES RECENTES DA LIGA", leagueTrades, false))
            .grow().uniformX().padRight(10f);
        histories.add(createHistoryPanel("ÚLTIMAS NEGOCIAÇÕES DO SEU CLUBE", clubTrades, true))
            .grow().uniformX();
        page.add(histories).grow().padBottom(10f).row();

        TextButton openTradeCenter = ScreenUI.createPrimaryButton(game.skin, "ABRIR CENTRAL DE TROCAS");
        openTradeCenter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TradeScreen(game, userClub));
            }
        });
        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
            Table actions = new Table();
            TextButton back = ScreenUI.createInteractiveButton("← VOLTAR À OFF SEASON", game.skin);
            back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ game.setScreen(new OffSeasonScreen(game, userClub)); }});
            actions.add(back).width(240f).height(48f).padRight(8f);
            actions.add(openTradeCenter).width(310f).height(52f);
            page.add(actions).center();
        } else page.add(openTradeCenter).width(310f).height(52f).center();

        root.add(page);
        if (!"OFFSEASON".equals(game.league.getCurrentStage())) NavigationDrawer.attach(stage, game, userClub, "TROCAS", true);
    }

    private Table createHistoryPanel(String title, List<TradeRecord> records, boolean clubOnly) {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, title)).left().padBottom(9f).row();

        Table entries = new Table();
        entries.top();
        if (records.isEmpty()) {
            Label empty = ScreenUI.createSubtitle(
                game.skin,
                clubOnly
                    ? "Seu clube ainda não realizou trocas nesta temporada."
                    : "Nenhuma troca foi registrada nesta temporada."
            );
            empty.setWrap(true);
            empty.setAlignment(Align.center);
            entries.add(empty).growX().pad(32f);
        } else {
            for (TradeRecord record : records) {
                entries.add(createTradeCard(record)).growX().padBottom(7f).row();
            }
        }

        ScrollPane scroll = new ScrollPane(entries, game.skin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        panel.add(scroll).grow().minHeight(400f);
        return panel;
    }

    private Table createTradeCard(TradeRecord record) {
        Table card = ScreenUI.createSubtlePanel();
        card.pad(10f, 12f, 9f, 12f);

        String firstName = record.getFirstClub() != null ? record.getFirstClub().getName() : "Clube";
        String secondName = record.getSecondClub() != null ? record.getSecondClub().getName() : "Clube";
        card.add(ScreenUI.createBoldValue(
                game.skin, firstName + "  ⇄  " + secondName, StyleFactory.CREME_AGED, Align.left
            )).left().expandX();
        card.add(ScreenUI.createSubtitle(game.skin, formatDate(record.getDate()) + " • " + record.getSeason()))
            .right().row();

        card.add(createAssetText(firstName + " enviou: ", record.getFirstClubAssets()))
            .colspan(2).growX().left().padTop(5f).row();
        card.add(createAssetText(secondName + " enviou: ", record.getSecondClubAssets()))
            .colspan(2).growX().left().padTop(2f);
        return card;
    }

    private Label createAssetText(String prefix, List<String> assets) {
        Label label = ScreenUI.createSubtitle(game.skin, prefix + joinAssets(assets));
        label.setWrap(true);
        label.setColor(Color.valueOf("B8C0B6"));
        return label;
    }

    private List<TradeRecord> recentTrades(boolean onlyUserClub) {
        List<TradeRecord> records = new ArrayList<>();
        for (TradeRecord record : game.league.getTradeHistory()) {
            if (!onlyUserClub || record.involves(userClub)) records.add(record);
            if (records.size() == HISTORY_LIMIT) break;
        }
        return records;
    }

    private String joinAssets(List<String> assets) {
        if (assets == null || assets.isEmpty()) return "nenhum ativo";
        StringBuilder text = new StringBuilder();
        for (int index = 0; index < assets.size(); index++) {
            if (index > 0) text.append(" • ");
            text.append(assets.get(index));
        }
        return text.toString();
    }

    private String formatDate(Date date) {
        return date == null ? "Data não registrada" : new SimpleDateFormat("dd/MM", Locale.US).format(date);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
