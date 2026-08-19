package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClubManagementScreen implements Screen {
    private final Main game;
    private final Club club;
    private Stage stage;
    private Texture pranchetaTexture;
    private Texture fieldTexture;
    private Texture clubLogoTexture;
    private Texture opponentLogoTexture;
    private String sortType = "OVR";
    private boolean sortAscending = false;
    private String positionFilter = "TODOS";
    private final List<Texture> navigationTextures = new ArrayList<>();

    public ClubManagementScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ScreenViewport());
        this.pranchetaTexture = new Texture(Gdx.files.internal("prancheta.png"));
        this.fieldTexture = new Texture(Gdx.files.internal("campo.png"));
        this.clubLogoTexture = loadLogo(club);
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

        root.add(new Image(new TextureRegionDrawable(pranchetaTexture)));

        Table mainContent = new Table();
        mainContent.pad(48, 238, 42, 74);

        mainContent.add(createHeader()).growX().height(78).row();

        Table body = new Table();
        float usableWidth = Math.max(520, Gdx.graphics.getWidth() - 238 - 74 - 24);
        body.add(createRosterPanel()).width(usableWidth * 0.56f).growY().padRight(12);
        body.add(createFieldPreview()).width(usableWidth * 0.40f).growY();
        mainContent.add(body).grow();

        root.add(mainContent);

        NavigationDrawer.attach(stage, game, club, "ELENCO", true);
        CareerOverlay.attach(stage, game, club);
    }

    private Table createHeader() {
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        header.pad(6, 18, 6, 18);

        Image clubLogo = new Image(clubLogoTexture);
        clubLogo.setScaling(Scaling.fit);
        header.add(clubLogo).size(78, 43).padRight(12);
        Label title = new Label(club.getName().toUpperCase(), game.skin, "font-title");
        title.setFontScale(0.95f);
        header.add(title).left().expandX().padRight(14);

        Table season = new Table();
        header.add(season).center().expandX();

        return header;
    }

    private Table createRosterPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createSolid(new Color(0.03f, 0.04f, 0.04f, 0.30f)));
        panel.top().pad(12);
        Label title = new Label("ELENCO • ESTATÍSTICAS", game.skin, "font-bold");
        title.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(title).left().expandX();
        SelectBox<String> positionBox = new SelectBox<>(game.skin);
        positionBox.setItems("TODOS", "GOLEIROS", "DEFESA", "MEIO-CAMPO", "ATAQUE");
        positionBox.setSelected(positionFilter);
        positionBox.addListener(new ChangeListener() {
            @Override public void changed(ChangeListener.ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                positionFilter = positionBox.getSelected();
                refreshUI();
            }
        });
        panel.add(positionBox).width(155).right().row();
        panel.add(new ScrollPane(createSquadTable(), game.skin)).grow().fill();
        return panel;
    }

    private Table createFieldPreview() {
        Table panel = new Table();
        panel.background(StyleFactory.createSolid(new Color(0.03f, 0.10f, 0.05f, 0.22f)));
        panel.top().pad(8);

        Formation formation = club.getFormation();
        String formationName = (formation != null) ? formation.getName() : "NENHUMA";

        panel.add(new Label("TÁTICA ATUAL • " + formationName, game.skin, "font-bold")).padBottom(8).row();

        int fieldWidth = Math.min(390, Math.max(260, (int)(Gdx.graphics.getWidth() * 0.28f)));
        int fieldHeight = Math.min(560, Math.max(390, (int)(fieldWidth * 1.78f)));
        Stack field = new Stack();
        field.setSize(fieldWidth, fieldHeight);
        Image fieldImage = new Image(fieldTexture);
        fieldImage.setFillParent(true);
        field.add(fieldImage);

        if (formation == null) {
            Table warningTable = new Table();
            Label warningLabel = new Label("Nenhuma tática selecionada.\nVisite a aba Táticas.", game.skin, "font-bold");
            warningLabel.setAlignment(Align.center);
            warningLabel.setColor(StyleFactory.SOFT_YELLOW);
            warningTable.add(warningLabel).center();
            field.add(warningTable);
        } else {
            Group players = new Group();
            players.setSize(fieldWidth, fieldHeight);
            List<String> slots = formation.getPositionSlots();
            for (int i = 0; i < Math.min(11, slots.size()); i++) {
                Player player = club.getTacticsMap().get(i);
                players.addActor(createFieldPlayerCard(player, slots.get(i), i, slots, fieldWidth, fieldHeight));
            }
            field.add(players);
        }

        panel.add(field).width(fieldWidth).height(fieldHeight);
        return panel;
    }

    private Table createFieldPlayerCard(Player player, String position, int slot, List<String> slots,
                                        int width, int height) {
        Table card = new Table();
        card.background(StyleFactory.createRoundedPanel(new Color(0.08f, 0.10f, 0.08f, 0.90f), StyleFactory.SOFT_YELLOW));
        card.setSize(Math.min(120, width * 0.28f), 48);
        String name = player == null ? "VAZIO" : player.getName();
        Label label = new Label(name + "\n" + position, game.skin, "font-bold");
        label.setAlignment(Align.center);
        label.setFontScale(0.55f);
        card.add(label).width(card.getWidth() - 8).center();

        int line = positionWeight(position);
        int sameLineIndex = 0;
        for (int i = 0; i < slot; i++) {
            if (positionWeight(slots.get(i)) == line) sameLineIndex++;
        }
        int lineCount = 0;
        for (String slotPosition : slots) if (positionWeight(slotPosition) == line) lineCount++;
        float[] y = {0.08f, 0.29f, 0.53f, 0.79f};
        float x = (sameLineIndex + 1f) / (lineCount + 1f);
        card.setPosition(x * width - card.getWidth() / 2f, y[line - 1] * height - card.getHeight() / 2f);
        return card;
    }

    private Table createSquadTable() {
        Table table = new Table();
        table.add(createSortButton("POS", "POS")).width(54);
        table.add(createSortButton("JOGADOR", "NOME")).width(200).left();
        table.add(createSortButton("OVR", "OVR")).width(48);
        table.add(createSortButton("EFF", "EFF")).width(48);
        table.add(createSortButton("SALÁRIO", "SALARY")).width(90); // Coluna de Salários
        table.add(createSortButton("MOR", "MORALE")).width(48);
        table.add(createSortButton("G", "GOALS")).width(40);
        table.add(createSortButton("A", "ASSISTS")).width(40);
        table.add(createSortButton("CA", "YELLOW")).width(40);
        table.add(createSortButton("CV", "RED")).width(40);
        table.add(createSortButton("FADIGA", "FATIGUE")).width(70).row();

        List<Player> players = new ArrayList<>(club.getSquad());
        sortPlayers(players);

        for (Player p : players) {
            if (!matchesPositionFilter(p)) continue;

            boolean available = p.canPlay();
            Color defaultColor = available ? Color.WHITE : Color.GRAY;
            Label.LabelStyle textStyle = new Label.LabelStyle(game.skin.getFont("font-bold"), defaultColor);

            Table posBadge = new Table();
            posBadge.background(StyleFactory.createBadge(StyleFactory.getPositionColor(p.getPosition())));
            Label posLabel = new Label(p.getPosition(), textStyle);
            posLabel.setFontScale(0.55f);
            posBadge.add(posLabel).center();
            table.add(posBadge).width(48).height(24).pad(4);

            String displayName = p.getName();
            if (p.isInjured()) displayName += " [LESIONADO: " + p.getInjuryDuration() + "J]";
            else if (p.isSuspended()) displayName += " [SUSPENSO: " + p.getSuspendedMatches() + "J]";

            Label nameLabel = new Label(displayName, textStyle);
            nameLabel.setFontScale(0.65f);
            if (!available) nameLabel.setColor(Color.valueOf("FF4D4D"));
            table.add(nameLabel).left().padLeft(4);

            Label ovrLabel = new Label(String.valueOf(p.getOverall()), textStyle);
            ovrLabel.setFontScale(0.65f);
            table.add(ovrLabel);

            int effVal = p.getEffectiveOverallForPosition(p.getPosition());
            Label effLabel = new Label(String.valueOf(effVal), textStyle);
            effLabel.setFontScale(0.65f);
            if (effVal < p.getOverall()) effLabel.setColor(Color.valueOf("E67E22"));
            table.add(effLabel);

            // Exibição do Salário Anual formatado em Milhões/Milhares
            long monthlySalary = p.getMonthlySalary();
            String formattedSalary = "WFL$ " + String.format("%,d", monthlySalary).replace(',', '.');
            Label salaryLabel = new Label(formattedSalary, textStyle);
            salaryLabel.setFontScale(0.58f);
            salaryLabel.setColor(StyleFactory.SOFT_YELLOW);
            table.add(salaryLabel);

            int moraleVal = p.getMorale();
            Label moraleLabel = new Label(String.valueOf(moraleVal), textStyle);
            moraleLabel.setFontScale(0.65f);
            if (moraleVal >= 75) moraleLabel.setColor(Color.GREEN);
            else if (moraleVal >= 45) moraleLabel.setColor(StyleFactory.SOFT_YELLOW);
            else moraleLabel.setColor(Color.valueOf("FF4D4D"));
            table.add(moraleLabel);

            Label goalsLabel = new Label(String.valueOf(p.getSeasonGoals()), textStyle);
            goalsLabel.setFontScale(0.65f);
            table.add(goalsLabel);

            Label assistsLabel = new Label(String.valueOf(p.getSeasonAssists()), textStyle);
            assistsLabel.setFontScale(0.65f);
            table.add(assistsLabel);

            Label ycLabel = new Label(String.valueOf(p.getSeasonYellowCards()), textStyle);
            ycLabel.setFontScale(0.65f);
            table.add(ycLabel);

            Label rcLabel = new Label(String.valueOf(p.getSeasonRedCards()), textStyle);
            rcLabel.setFontScale(0.65f);
            if (p.getSeasonRedCards() > 0) rcLabel.setColor(Color.valueOf("FF4D4D"));
            table.add(rcLabel);

            Label fadLabel = new Label(p.getFatigue() + "%", textStyle);
            fadLabel.setFontScale(0.62f);
            if (p.getFatigue() >= 80) fadLabel.setColor(Color.GREEN);
            else if (p.getFatigue() >= 50) fadLabel.setColor(StyleFactory.SOFT_YELLOW);
            else fadLabel.setColor(Color.valueOf("FF4D4D"));
            table.add(fadLabel);

            table.row().padBottom(4);
        }
        return table;
    }

    private boolean matchesPositionFilter(Player player) {
        if (positionFilter.equals("TODOS")) return true;
        int weight = player.getPositionWeight();
        if (positionFilter.equals("GOLEIROS")) return weight == 1;
        if (positionFilter.equals("DEFESA")) return weight == 2;
        if (positionFilter.equals("MEIO-CAMPO")) return weight == 3;
        return weight == 4;
    }

    private TextButton createSortButton(String label, final String type) {
        TextButton btn = new TextButton(label, game.skin, "toggle");
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
            case "FATIGUE": comp = Comparator.comparingInt(Player::getFatigue); break;
            case "MORALE": comp = Comparator.comparingInt(Player::getMorale); break;
            case "GOALS": comp = Comparator.comparingInt(Player::getSeasonGoals); break;
            case "ASSISTS": comp = Comparator.comparingInt(Player::getSeasonAssists); break;
            case "YELLOW": comp = Comparator.comparingInt(Player::getSeasonYellowCards); break;
            case "RED": comp = Comparator.comparingInt(Player::getSeasonRedCards); break;
            default: comp = Comparator.comparingInt(Player::getOverall); break;
        }
        if (!sortAscending) comp = comp.reversed();
        players.sort(comp);
    }

    private int positionWeight(String position) {
        if (position.equals("GK")) return 1;
        if (position.matches("CB|RB|LB|RWB|LWB")) return 2;
        if (position.matches("CDM|CM|CAM|RM|LM")) return 3;
        return 4;
    }

    private Texture loadLogo(Club target) {
        try {
            return new Texture(Gdx.files.internal(target.getLogoPath()));
        } catch (Exception e) {
            return new Texture(Gdx.files.internal("libgdx.png"));
        }
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
        pranchetaTexture.dispose();
        fieldTexture.dispose();
        if (clubLogoTexture != null) clubLogoTexture.dispose();
        if (opponentLogoTexture != null) opponentLogoTexture.dispose();
        for (Texture texture : navigationTextures) texture.dispose();
        navigationTextures.clear();
    }
}
