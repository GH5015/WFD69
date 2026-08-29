package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.RetirementRecord;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Continuação do resumo da temporada com as despedidas dos atletas. */
public class RetirementSummaryScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;

    public RetirementSummaryScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        Viewport viewport = new ResponsiveViewport();
        this.stage = new Stage(viewport);
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

        Image shade = new Image(StyleFactory.createSolid(new Color(0f, 0.02f, 0.015f, 0.52f)));
        shade.setFillParent(true);
        root.add(shade);

        Table page = ScreenUI.createPage(false);
        page.add(createHeader()).growX().height(76f).padBottom(10f).row();
        page.add(createTitle()).growX().height(48f).padBottom(10f).row();
        page.add(createSummary()).growX().height(70f).padBottom(10f).row();
        page.add(createRetirementsPanel()).grow().padBottom(12f).row();
        page.add(createActions()).growX().height(58f);
        root.add(page);
    }

    private Table createHeader() {
        Table header = ScreenUI.createPanel();

        Table identity = new Table();
        Label title = new Label("DESPEDIDAS DA TEMPORADA", game.skin, "font-title");
        title.setFontScale(0.76f);
        title.setColor(StyleFactory.GOLD);
        identity.add(title).left().row();

        Label subtitle = ScreenUI.createSubtitle(game.skin, "WFL • TEMPORADA " + game.league.getCurrentSeason());
        subtitle.setColor(ScreenUI.MUTED_TEXT);
        subtitle.setFontScale(0.50f);
        identity.add(subtitle).left();

        header.add(identity).left().expandX();
        header.add(ScreenUI.createStatusBox(
            game.skin,
            "PRÓXIMA ETAPA",
            "OFF SEASON",
            StyleFactory.SOFT_YELLOW
        )).width(190f).height(48f);
        return header;
    }

    private Table createTitle() {
        Table title = new Table();

        Label heading = new Label("—  APOSENTADORIAS  —", game.skin, "font-bold");
        heading.setColor(Color.WHITE);
        heading.setFontScale(0.71f);

        Label copy = ScreenUI.createSubtitle(
            game.skin,
            getRetirements().isEmpty()
                ? "Nenhum atleta encerrou a carreira nesta temporada."
                : "Uma homenagem aos atletas que encerraram suas carreiras na WFL."
        );
        copy.setColor(ScreenUI.MUTED_TEXT);

        title.add(heading).center().row();
        title.add(copy).center().padTop(4f);
        return title;
    }

    private Table createSummary() {
        List<RetirementRecord> records = getRetirements();
        int totalAge = 0;
        int bestOverall = 0;
        int userClubRetirements = 0;

        for (RetirementRecord record : records) {
            Player player = record.getPlayer();
            if (player == null) continue;
            totalAge += player.getAge();
            bestOverall = Math.max(bestOverall, player.getOverall());
            if (club != null && club.getName().equals(record.getLastClubName())) userClubRetirements++;
        }

        int averageAge = records.isEmpty() ? 0 : Math.round((float) totalAge / records.size());
        Table summary = new Table();
        summary.add(status("APOSENTADOS", String.valueOf(records.size()), records.isEmpty() ? ScreenUI.SUCCESS : StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8f);
        summary.add(status("IDADE MÉDIA", records.isEmpty() ? "—" : averageAge + " ANOS", Color.WHITE)).growX().uniformX().padRight(8f);
        summary.add(status("MAIOR OVR", records.isEmpty() ? "—" : String.valueOf(bestOverall), records.isEmpty() ? ScreenUI.MUTED_TEXT : StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8f);
        summary.add(status("DO SEU CLUBE", String.valueOf(userClubRetirements), userClubRetirements > 0 ? ScreenUI.WARNING : ScreenUI.MUTED_TEXT)).growX().uniformX();
        return summary;
    }

    private Table status(String label, String value, Color color) {
        return ScreenUI.createStatusBox(game.skin, label, value, color);
    }

    private Table createRetirementsPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();

        List<RetirementRecord> records = getRetirements();
        panel.add(ScreenUI.createSectionTitle(game.skin, "JOGADORES APOSENTADOS")).left().padBottom(8f).row();

        if (records.isEmpty()) {
            Table empty = new Table();
            Label title = ScreenUI.createBoldValue(game.skin, "SEM DESPEDIDAS NESTA TEMPORADA", ScreenUI.SUCCESS, Align.center);
            title.setFontScale(0.62f);
            Label text = ScreenUI.createSubtitle(game.skin, "Os veteranos seguirão disponíveis para o próximo ano.");
            text.setColor(ScreenUI.MUTED_TEXT);
            empty.add(title).padBottom(8f).row();
            empty.add(text);
            panel.add(empty).grow().center();
            return panel;
        }

        Table table = new Table();
        table.top();

        Table header = ScreenUI.createTableHeaderRow();
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "JOGADOR", Align.left)).width(300f).padLeft(10f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "ÚLTIMO CLUBE", Align.left)).width(250f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "IDADE", Align.center)).width(80f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "POS", Align.center)).width(78f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "OVR", Align.center)).width(82f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "TEMPORADA", Align.center)).width(220f);
        table.add(header).growX().height(42f).row();

        int index = 0;
        for (RetirementRecord record : records) {
            table.add(createRetirementRow(record, index++)).growX().height(54f).row();
        }

        ScrollPane scroll = new ScrollPane(table, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createRetirementRow(RetirementRecord record, int index) {
        Player player = record.getPlayer();
        Table row = ScreenUI.createRow(index);

        if (player == null) {
            row.add(value("Jogador indisponível", ScreenUI.MUTED_TEXT, Align.left)).colspan(6).growX();
            return row;
        }

        row.add(value(ScreenUI.shorten(player.getName(), 28), Color.WHITE, Align.left)).left().width(300f).padLeft(10f);
        row.add(value(ScreenUI.shorten(record.getLastClubName(), 24), StyleFactory.CREME_AGED, Align.left)).left().width(250f);
        row.add(value(String.valueOf(player.getAge()), Color.WHITE, Align.center)).width(80f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(78f).height(27f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW, Align.center)).width(82f);
        row.add(value(
            player.getSeasonAppearances() + " J  •  " + player.getSeasonGoals() + " G  •  " + player.getSeasonAssists() + " A",
            ScreenUI.MUTED_TEXT,
            Align.center
        )).width(220f);
        return row;
    }

    private Label value(String text, Color color, int align) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, align);
        label.setFontScale(0.51f);
        return label;
    }

    private Table createActions() {
        Table actions = new Table();

        Label note = ScreenUI.createSubtitle(game.skin, "Aposentados não entram na Free Agency.");
        note.setColor(ScreenUI.MUTED_TEXT);
        note.setFontScale(0.48f);

        TextButton continueButton = ScreenUI.createPrimaryButton(game.skin, "CONTINUAR PARA OFF SEASON  ›");
        continueButton.getLabel().setFontScale(0.58f);
        continueButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new OffSeasonScreen(game, club));
            }
        });

        actions.add(note).left().expandX();
        actions.add(continueButton).width(390f).height(54f).right();
        return actions;
    }

    private List<RetirementRecord> getRetirements() {
        List<RetirementRecord> records = new ArrayList<>(game.league.getSeasonRetirements());
        records.sort(new Comparator<RetirementRecord>() {
            @Override public int compare(RetirementRecord first, RetirementRecord second) {
                Player firstPlayer = first.getPlayer();
                Player secondPlayer = second.getPlayer();
                int firstAge = firstPlayer != null ? firstPlayer.getAge() : 0;
                int secondAge = secondPlayer != null ? secondPlayer.getAge() : 0;
                if (firstAge != secondAge) return secondAge - firstAge;
                String firstName = firstPlayer != null ? firstPlayer.getName() : "";
                String secondName = secondPlayer != null ? secondPlayer.getName() : "";
                return firstName.compareTo(secondName);
            }
        });
        return records;
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0.02f, 0.015f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); }
}
