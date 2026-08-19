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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClubDetailsScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Club playerClub;
    private Stage stage;
    private Texture logoTexture;
    private String sortType = "OVR";
    private boolean sortAscending = false;

    public ClubDetailsScreen(Main game, Club club, Club playerClub) {
        this.game = game;
        this.club = club;
        this.playerClub = playerClub;
        this.stage = new Stage(new ScreenViewport());
        try {
            logoTexture = new Texture(Gdx.files.internal(club.getLogoPath()));
        } catch (Exception e) {
            logoTexture = new Texture(Gdx.files.internal("libgdx.png"));
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
        root.add(new Image(game.background));

        Table content = new Table();
        content.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.07f, 0.88f), StyleFactory.GOLD));
        content.top().left().pad(40);

        Table header = new Table();
        float logoWidth = Math.min(320, Gdx.graphics.getWidth() * 0.25f);
        header.add(new Image(logoTexture)).width(logoWidth).height(logoWidth * 0.40f).padRight(24);
        Table identity = new Table();
        identity.add(new Label(club.getName().toUpperCase(), game.skin, "font-title", Color.GOLD)).left().row();
        identity.add(new Label(club.getCountry() + " - Reputação: " + club.getReputation(), game.skin)).left().row();
        header.add(identity).left().expandX();
        content.add(header).growX().row();

        Table squadHeader = new Table();
        squadHeader.padTop(30).padBottom(10);
        squadHeader.add(createSortButton("NOME", "NOME")).width(300).left();
        squadHeader.add(createSortButton("POS", "POS")).width(150).center();
        squadHeader.add(createSortButton("OVR", "OVR")).width(100).center();
        squadHeader.add(createSortButton("G", "GOLS")).width(60).center();
        squadHeader.add(createSortButton("A", "ASSISTS")).width(60).center();
        content.add(squadHeader).row();

        Table squadTable = new Table();
        List<Player> players = new ArrayList<>(club.getSquad());
        sortPlayers(players);
        for (Player p : players) {
            squadTable.add(new Label(p.getName(), game.skin)).width(300).left().padBottom(5);
            squadTable.add(new Label(p.getPosition(), game.skin)).width(150).center();
            squadTable.add(new Label(String.valueOf(p.getOverall()), game.skin)).width(100).center();
            squadTable.add(new Label(String.valueOf(p.getSeasonGoals()), game.skin)).width(60).center();
            squadTable.add(new Label(String.valueOf(p.getSeasonAssists()), game.skin)).width(60).center();
            squadTable.row();
        }
        ScrollPane scroll = new ScrollPane(squadTable, game.skin);
        content.add(scroll).expand().fill().padTop(10);
        root.add(content);
        NavigationDrawer.attach(stage, game, playerClub, "PERFIL");
        CareerOverlay.attach(stage, game, playerClub);
    }

    private TextButton createSortButton(String label, String type) {
        String arrow = sortType.equals(type) ? (sortAscending ? " ^" : " v") : "";
        TextButton btn = new TextButton(label + arrow, game.skin, "toggle");
        if (sortType.equals(type)) btn.setChecked(true);
        btn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                if (sortType.equals(type)) sortAscending = !sortAscending;
                else { sortType = type; sortAscending = false; }
                refreshUI();
            }
        });
        return btn;
    }

    private void sortPlayers(List<Player> players) {
        Comparator<Player> comp;
        switch (sortType) {
            case "NOME": comp = Comparator.comparing(Player::getName); break;
            case "POS": comp = Comparator.comparingInt(Player::getPositionWeight); break;
            case "GOLS": comp = Comparator.comparingInt(Player::getSeasonGoals); break;
            case "ASSISTS": comp = Comparator.comparingInt(Player::getSeasonAssists); break;
            default: comp = Comparator.comparingInt(Player::getOverall); break;
        }
        if (!sortAscending) comp = comp.reversed();
        players.sort(comp);
    }

    private Table createSidebar() {
        Table sidebar = new Table();
        String[] btns = {"VOLTAR PARA TABELA", "MEU CLUBE", "CALENDARIO"};
        for (String b : btns) {
            TextButton tb = new TextButton(b, game.skin);
            tb.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (b.equals("VOLTAR PARA TABELA")) game.setScreen(new StandingsScreen(game, playerClub));
                    if (b.equals("MEU CLUBE")) game.setScreen(new ClubManagementScreen(game, playerClub));
                    if (b.equals("CALENDARIO")) game.setScreen(new CalendarScreen(game, playerClub));
                }
            });
            sidebar.add(tb).width(300).height(80).pad(10).row();
        }
        return sidebar;
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); if(logoTexture!=null) logoTexture.dispose(); }
}
