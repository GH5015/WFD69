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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.DraftClass1970;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.ui.DraftSelectionDialog;
import io.github.some_example_name.ui.PlayerReportDialog;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

public class DraftScoutingScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;
    private final Texture pranchetaTexture;
    private final DraftScoutManager scoutManager;
    private final List<Player> draftClass1970;

    public DraftScoutingScreen(Main game, Club club, DraftScoutManager scoutManager) {
        this.game = game;
        this.club = club;
        this.scoutManager = scoutManager;
        this.draftClass1970 = DraftClass1970.getPlayers();
        this.stage = new Stage(new ScreenViewport());
        this.pranchetaTexture = new Texture(Gdx.files.internal("prancheta.png"));
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

        // --- CABEÇALHO ---
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        header.pad(10, 18, 10, 18);

        Label title = new Label("DRAFT SCOUTING • CLASSE DE 1970", game.skin, "font-title");
        title.setFontScale(0.85f);
        header.add(title).left().expandX();

        Label classKnowledge = new Label("Conhecimento Geral: " + scoutManager.getOverallClassKnowledge(draftClass1970.size()) + "%", game.skin, "font-bold");
        classKnowledge.setColor(StyleFactory.SOFT_YELLOW);
        header.add(classKnowledge).right();

        mainContent.add(header).growX().height(64).padBottom(12).row();

        // --- PAINEL DO SCOUT ---
        Table scoutPanel = new Table();
        scoutPanel.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.06f, 0.90f), StyleFactory.GOLD));
        scoutPanel.pad(10);

        Label scoutLabel = new Label("SCOUT PRINCIPAL: Carlos Mendes (" + "★".repeat(scoutManager.getScoutStars()) + "☆".repeat(5 - scoutManager.getScoutStars()) + ")", game.skin, "font-bold");
        scoutLabel.setColor(StyleFactory.GOLD);
        scoutPanel.add(scoutLabel).left().expandX();

        // Atualizado para limite de 5 vagas
        Label slotsLabel = new Label("Vagas Ocupadas: " + scoutManager.getActiveTargets().size() + "/5", game.skin, "font-bold");
        slotsLabel.setColor(scoutManager.isFull() ? Color.valueOf("FF4D4D") : Color.GREEN);
        scoutPanel.add(slotsLabel).right();

        mainContent.add(scoutPanel).growX().padBottom(12).row();

        // --- TABELA DE TARGETS (MÁXIMO 5) ---
        Table tableContainer = new Table();
        tableContainer.background(StyleFactory.createSolid(new Color(0.03f, 0.04f, 0.04f, 0.40f)));
        tableContainer.top().pad(12);

        Table targetTable = new Table();
        targetTable.add(new Label("NAC", game.skin, "font-bold")).width(48);
        targetTable.add(new Label("JOGADOR", game.skin, "font-bold")).width(180).left();
        targetTable.add(new Label("IDADE", game.skin, "font-bold")).width(48);
        targetTable.add(new Label("POS", game.skin, "font-bold")).width(54);
        targetTable.add(new Label("OVR EST.", game.skin, "font-bold")).width(80);
        targetTable.add(new Label("POT EST.", game.skin, "font-bold")).width(80);
        targetTable.add(new Label("CONHEC.", game.skin, "font-bold")).width(90);
        targetTable.add(new Label("AÇÃO", game.skin, "font-bold")).width(60).row();

        for (ScoutTarget target : scoutManager.getActiveTargets()) {
            Player p = target.getPlayer();

            targetTable.add(new Label(p.getNationality().substring(0, Math.min(3, p.getNationality().length())).toUpperCase(), game.skin)).width(48);
            
            // Clique no Nome para Abrir a Ficha do Relatório
            TextButton nameBtn = new TextButton(p.getName(), game.skin, "transparent");
            nameBtn.getLabel().setAlignment(Align.left);
            nameBtn.getLabel().setColor(StyleFactory.CREME_AGED);
            nameBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    PlayerReportDialog dialog = new PlayerReportDialog(game.skin, target, scoutManager, () -> refreshUI());
                    dialog.show(stage);
                }
            });
            targetTable.add(nameBtn).left().width(180);

            targetTable.add(new Label(String.valueOf(p.getAge()), game.skin)).width(48);

            Table posBadge = new Table();
            posBadge.background(StyleFactory.createBadge(StyleFactory.getPositionColor(p.getPrimaryPosition().name())));
            Label posLabel = new Label(p.getPrimaryPosition().name(), game.skin, "font-bold");
            posLabel.setFontScale(0.55f);
            posBadge.add(posLabel).center();
            targetTable.add(posBadge).width(48).height(22).pad(4);

            // OVR Estimado pelas camadas de conhecimento
            String ovrDisplay = target.getDisplayOverall();
            Label ovrGrade = new Label(ovrDisplay, game.skin, "font-bold");
            ovrGrade.setColor(getGradeColor(ovrDisplay));
            targetTable.add(ovrGrade).width(80);

            // Potencial Estimado
            String potDisplay = target.getDisplayPotential();
            Label potGrade = new Label(potDisplay, game.skin, "font-bold");
            potGrade.setColor(getGradeColor(potDisplay));
            targetTable.add(potGrade).width(80);

            // Progresso em Porcentagem Exata
            Label knowLabel = new Label(String.format("%.1f%%", target.getKnowledgePercentage()), game.skin);
            knowLabel.setColor(target.isFullyScouted() ? Color.GREEN : Color.WHITE);
            targetTable.add(knowLabel).width(90);

            // Botão de Detalhes/Remoção rápida
            TextButton inspectBtn = new TextButton("VER", game.skin);
            inspectBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    PlayerReportDialog dialog = new PlayerReportDialog(game.skin, target, scoutManager, () -> refreshUI());
                    dialog.show(stage);
                }
            });
            targetTable.add(inspectBtn).width(50).pad(2);
            targetTable.row().padBottom(6);
        }

        tableContainer.add(new ScrollPane(targetTable, game.skin)).grow().row();

        // Botão para Adicionar Novo Alvo ao Scouting
        TextButton addBtn = new TextButton("+ OBSERVAR JOGADOR", game.skin);
        addBtn.setDisabled(scoutManager.isFull());
        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!scoutManager.isFull()) {
                    DraftSelectionDialog dialog = new DraftSelectionDialog(game.skin, stage, scoutManager, draftClass1970, () -> refreshUI());
                    dialog.show(stage);
                }
            }
        });
        tableContainer.add(addBtn).padTop(10).height(40).width(240);

        mainContent.add(tableContainer).grow();

        root.add(mainContent);

        NavigationDrawer.attach(stage, game, club, "SCOUTING", true);
        CareerOverlay.attach(stage, game, club);
    }

    /**
     * Mapeia as grades com prefixos/sufixos (+/-) e valores para suas respectivas cores
     */
    private Color getGradeColor(String gradeDisplay) {
        if (gradeDisplay.equals("?")) return Color.GRAY;
        
        // Trata a primeira letra da nota
        char baseGrade = gradeDisplay.charAt(0);
        switch (baseGrade) {
            case 'A': return Color.GREEN;
            case 'B': return StyleFactory.SOFT_YELLOW;
            case 'C': return Color.ORANGE;
            case 'D': case 'F': return Color.valueOf("FF4D4D");
            default:
                // Se for intervalo numérico (ex: 78-82) ou valor final exato (ex: 81)
                return StyleFactory.CREME_AGED;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        pranchetaTexture.dispose();
    }
}
