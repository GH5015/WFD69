package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TacticsScreen implements Screen {
    private final Main game;
    private final Club club;
    private Stage stage;
    private Player selectedPlayer = null;
    private Texture pitchTexture;
    private Texture kitTexture;

    // Estado da Aba Direita (false = Plantel, true = Táticas)
    private boolean showTacticsTab = false;

    public TacticsScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        if (Gdx.files.internal("uniforme_santos.png").exists()) {
            kitTexture = new Texture(Gdx.files.internal("uniforme_santos.png"));
        }

        generate2DPitchTexture();
        refreshUI();
    }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(new Image(game.background));

        Table mainLayout = new Table();
        mainLayout.top().pad(10, 220, 10, 10);

        // ====================================================
        // 1. CABEÇALHO E CONTROLES DE FORMAÇÃO
        // ====================================================
        Table headerTable = new Table();
        headerTable.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        headerTable.pad(8, 12, 8, 12);

        Label titleLbl = new Label("TÁTICAS E ESCALAÇÃO", game.skin, "font-bold", Color.WHITE);
        titleLbl.setFontScale(1.0f);
        headerTable.add(titleLbl).left().padRight(15);

        headerTable.add(new Label("FORMAÇÃO:", game.skin, "font-bold", StyleFactory.GOLD)).padRight(6);

        final SelectBox<Formation> selectBox = new SelectBox<>(game.skin);
        selectBox.setItems(Formation.values());
        if (club.getFormation() != null) {
            selectBox.setSelected(club.getFormation());
        }
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setFormation(selectBox.getSelected());
                refreshUI();
            }
        });
        headerTable.add(selectBox).width(140).padRight(15);

        headerTable.add().expandX();

        ImageTextButton btnAuto = IconTextButton.create("AUTO-ESCALAR", game.skin, "Icons8/icons8-ativa-modo-rápido-50.png");
        btnAuto.getLabel().setFontScale(0.85f);
        btnAuto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (club.getFormation() == null && selectBox.getSelected() != null) {
                    club.setFormation(selectBox.getSelected());
                }
                autoSelectByEffectiveOverall();
                refreshUI();
            }
        });
        headerTable.add(btnAuto).height(36).padRight(8);

        ImageTextButton btnClear = IconTextButton.create("LIMPAR", game.skin, "Icons8/icons8-remover-50.png");
        btnClear.getLabel().setFontScale(0.85f);
        btnClear.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                club.getTacticsMap().clear();
                club.getStartingXI().clear();
                selectedPlayer = null;
                refreshUI();
            }
        });
        headerTable.add(btnClear).height(36);

        mainLayout.add(headerTable).growX().padBottom(10).row();

        // ====================================================
        // 2. CORPO PRINCIPAL
        // ====================================================
        Table bodySplit = new Table();

        Table leftArea = new Table();

        if (club.getFormation() == null) {
            Table warningBox = new Table();
            warningBox.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
            warningBox.pad(20);

            Label warnLbl = new Label("Nenhuma formação selecionada.\nEscolha um esquema tático acima para montar a equipe.", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
            warnLbl.setAlignment(Align.center);
            warningBox.add(warnLbl).center().row();

            TextButton btnChooseDefault = new TextButton("Definir 1ª Formação", game.skin);
            btnChooseDefault.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Formation[] forms = Formation.values();
                    if (forms.length > 0) {
                        club.setFormation(forms[0]);
                        selectBox.setSelected(forms[0]);
                        autoSelectByEffectiveOverall();
                        refreshUI();
                    }
                }
            });
            warningBox.add(btnChooseDefault).padTop(15).width(200).height(40);

            leftArea.add(warningBox).grow().padBottom(10).row();
        } else {
            leftArea.add(createPitchLayout2D()).grow().padBottom(10).row();
        }

        leftArea.add(createSelectedPlayerPanel()).growX().height(100);

        bodySplit.add(leftArea).expand().fill().padRight(10);
        bodySplit.add(createRightSidebarPanel()).width(360).growY();

        mainLayout.add(bodySplit).grow().row();
        root.add(mainLayout);

        NavigationDrawer.attach(stage, game, club, "TÁTICAS", true);
    }

    private void autoSelectByEffectiveOverall() {
        Formation formation = club.getFormation();
        if (formation == null || formation.getPositionSlots() == null) return;

        club.getTacticsMap().clear();
        List<String> slots = formation.getPositionSlots();
        Set<Player> usedPlayers = new HashSet<>();

        for (int i = 0; i < Math.min(11, slots.size()); i++) {
            String targetPosition = slots.get(i);
            Player bestPlayer = null;
            int maxEffOverall = -1;

            for (Player p : club.getSquad()) {
                // Filtra jogadores suspensos ou lesionados durante a auto-escalação
                if (usedPlayers.contains(p) || !p.canPlay()) continue;

                int effOvr = p.getEffectiveOverallForPosition(targetPosition);
                if (effOvr > maxEffOverall) {
                    maxEffOverall = effOvr;
                    bestPlayer = p;
                }
            }

            if (bestPlayer != null) {
                club.assignPlayerToSlot(i, bestPlayer);
                usedPlayers.add(bestPlayer);
            }
        }
    }

    public List<Player> getAvailableLineup(Club club) {
        List<Player> available = new ArrayList<>();
        for (Player p : club.getSquad()) {
            if (p.canPlay()) {
                available.add(p);
            }
        }
        return available;
    }

    private void generate2DPitchTexture() {
        if (pitchTexture != null) pitchTexture.dispose();

        int w = 600;
        int h = 750;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.valueOf("1E4620"));
        pixmap.fill();

        pixmap.setColor(Color.valueOf("1B3E1C"));
        int stripeHeight = h / 10;
        for (int i = 0; i < 10; i += 2) {
            pixmap.fillRectangle(0, i * stripeHeight, w, stripeHeight);
        }

        pixmap.setColor(new Color(1f, 1f, 1f, 0.4f));
        pixmap.drawRectangle(15, 15, w - 30, h - 30);

        int midY = h / 2;
        pixmap.drawLine(15, midY, w - 15, midY);
        pixmap.drawCircle(w / 2, midY, 65);

        pixmap.drawRectangle(w / 2 - 140, 15, 280, 110);
        pixmap.drawRectangle(w / 2 - 60, 15, 120, 45);

        pixmap.drawRectangle(w / 2 - 140, h - 125, 280, 110);
        pixmap.drawRectangle(w / 2 - 60, h - 60, 120, 45);

        pitchTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private Table createPitchLayout2D() {
        Table pitch = new Table();
        pitch.background(new TextureRegionDrawable(new TextureRegion(pitchTexture)));
        pitch.pad(8);

        Formation f = club.getFormation();
        if (f == null || f.getPositionSlots() == null) return pitch;

        List<String> slots = f.getPositionSlots();

        Table stRow = createRowTable();
        Table wingRow = createRowTable();
        Table camRow = createRowTable();
        Table midRow = createRowTable();
        Table cdmRow = createRowTable();
        Table wingbackRow = createRowTable();
        Table defRow = createRowTable();
        Table gkRow = createRowTable();

        for (int i = 0; i < Math.min(11, slots.size()); i++) {
            final int slotIndex = i;
            String targetPos = slots.get(slotIndex);
            Player p = club.getTacticsMap().get(slotIndex);

            Table card = createMiniPlayerCard(p, targetPos, slotIndex);
            int layer = getPositionDepthLayer(targetPos);

            switch (layer) {
                case 7: stRow.add(card).pad(2, 6, 2, 6); break;
                case 6: wingRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 5: camRow.add(card).pad(2, 6, 2, 6); break;
                case 4: midRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 3: cdmRow.add(card).pad(2, 6, 2, 6); break;
                case 2: wingbackRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 1: defRow.add(card).expandX().pad(2, 4, 2, 4); break;
                default: gkRow.add(card).pad(2); break;
            }
        }

        pitch.add(stRow).expand().fillX().center().row();
        pitch.add(wingRow).expand().fillX().center().row();
        pitch.add(camRow).expand().center().row();
        pitch.add(midRow).expand().fillX().center().row();
        pitch.add(cdmRow).expand().center().row();
        pitch.add(wingbackRow).expand().fillX().center().row();
        pitch.add(defRow).expand().fillX().center().row();
        pitch.add(gkRow).expand().center().row();

        return pitch;
    }

    private Table createRowTable() {
        Table row = new Table();
        row.center();
        return row;
    }

    private Table createMiniPlayerCard(Player p, String targetPos, final int slotIndex) {
        Table outerCard = new Table();
        outerCard.setTransform(true);
        outerCard.setOrigin(Align.center);

        boolean isSelected = (p != null && p == selectedPlayer);
        Color borderColor = isSelected ? StyleFactory.GOLD : StyleFactory.DARK_GOLD;

        Stack stack = new Stack();

        Table kitLayer = new Table();
        kitLayer.top();
        if (p != null && kitTexture != null) {
            Image kitImg = new Image(kitTexture);
            kitLayer.add(kitImg).size(100, 100).padTop(-55).center();
        }

        Table cardFront = new Table();
        cardFront.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, borderColor));
        cardFront.pad(4);

        if (p != null) {
            int effOvr = p.getEffectiveOverallForPosition(targetPos);
            boolean isOutOfPosition = !p.getPosition().equalsIgnoreCase(targetPos) || effOvr < p.getOverall();

            String displayName = p.getName() + (isOutOfPosition ? " ⚠" : "");
            Label nameLbl = new Label(displayName, game.skin, "font-bold", isOutOfPosition ? Color.valueOf("FF9800") : Color.WHITE);
            nameLbl.setFontScale(0.70f);
            nameLbl.setEllipsis(true);
            cardFront.add(nameLbl).width(85).center().padTop(2).padBottom(4).row();

            Label posOvrLbl = new Label(targetPos.toUpperCase() + " " + effOvr, game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
            posOvrLbl.setFontScale(0.7f);
            cardFront.add(posOvrLbl).padBottom(2).row();

            float fatigue = p.getFatigue();
            cardFront.add(createFatigueBar(fatigue)).width(75).height(5).row();
        } else {
            Label posLbl = new Label(targetPos.toUpperCase(), game.skin, "font-bold", Color.GRAY);
            posLbl.setFontScale(0.8f);
            cardFront.add(posLbl).padBottom(2).row();

            Label emptyLbl = new Label("[ VAZIO ]", game.skin, "font-label", Color.LIGHT_GRAY);
            emptyLbl.setFontScale(0.65f);
            cardFront.add(emptyLbl).row();
        }

        stack.add(kitLayer);
        stack.add(cardFront);

        outerCard.add(stack).grow();

        if (isSelected) {
            outerCard.addAction(Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.08f, 1.08f, 0.35f),
                    Actions.scaleTo(0.96f, 0.96f, 0.35f)
                )
            ));
        }

        outerCard.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isSelected) {
                    outerCard.addAction(Actions.scaleTo(1.06f, 1.06f, 0.1f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!isSelected) {
                    outerCard.addAction(Actions.scaleTo(1.0f, 1.0f, 0.1f));
                }
            }
        });

        outerCard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedPlayer != null) {
                    if (selectedPlayer.canPlay()) {
                        club.assignPlayerToSlot(slotIndex, selectedPlayer);
                        selectedPlayer = null;
                    }
                } else if (p != null) {
                    selectedPlayer = p;
                }
                refreshUI();
            }
        });

        return outerCard;
    }

    private Table createSelectedPlayerPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        panel.pad(8);

        if (selectedPlayer == null) {
            Label noSel = new Label("Clique em um jogador do campo ou do plantel para selecionar/substituir", game.skin, "font-label", Color.LIGHT_GRAY);
            panel.add(noSel).center();
            return panel;
        }

        Table infoTable = new Table();
        infoTable.defaults().left().padRight(15);

        Label nameLbl = new Label(selectedPlayer.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLbl.setFontScale(0.95f);

        Label posLbl = new Label("Posição Origem: " + selectedPlayer.getPosition(), game.skin, "font-label", Color.WHITE);
        Label ovrLbl = new Label("Overall Base: " + selectedPlayer.getOverall(), game.skin, "font-bold", StyleFactory.SOFT_YELLOW);

        Label formLbl = new Label("Forma: ★★★★★", game.skin, "font-label", StyleFactory.GOLD);
        Label fatigueLbl = new Label("Fadiga: " + (int) selectedPlayer.getFatigue() + "%", game.skin, "font-label", Color.WHITE);

        String statusStr = "Moral: 😀 Excelente";
        if (selectedPlayer.isSuspended()) statusStr = "Status: ⛔ SUSPENSO";
        else if (selectedPlayer.isInjured()) statusStr = "Status: 🚑 LESIONADO";

        Label moralLbl = new Label(statusStr, game.skin, "font-label", selectedPlayer.canPlay() ? Color.WHITE : Color.valueOf("FF4D4D"));

        infoTable.add(nameLbl).colspan(2).padBottom(2).row();
        infoTable.add(posLbl);
        infoTable.add(formLbl).row();
        infoTable.add(ovrLbl);
        infoTable.add(fatigueLbl).row();
        infoTable.add(new Label("", game.skin));
        infoTable.add(moralLbl).row();

        panel.add(infoTable).expand().fill();
        return panel;
    }

    private Table createRightSidebarPanel() {
        Table container = new Table();
        container.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        container.pad(8);

        Table tabBar = new Table();

        TextButton btnSquad = new TextButton("PLANTEL", game.skin);
        TextButton btnTactics = new TextButton("TÁTICAS", game.skin);

        if (!showTacticsTab) {
            btnSquad.setColor(StyleFactory.GOLD);
            btnTactics.setColor(Color.GRAY);
        } else {
            btnSquad.setColor(Color.GRAY);
            btnTactics.setColor(StyleFactory.GOLD);
        }

        btnSquad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTacticsTab = false;
                refreshUI();
            }
        });

        btnTactics.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTacticsTab = true;
                refreshUI();
            }
        });

        tabBar.add(btnSquad).expandX().fillX().height(30).padRight(4);
        tabBar.add(btnTactics).expandX().fillX().height(30);

        container.add(tabBar).growX().padBottom(8).row();

        if (!showTacticsTab) {
            container.add(createSquadPanelContent()).grow();
        } else {
            container.add(createTacticsPanelContent()).grow();
        }

        return container;
    }

    private Table createSquadPanelContent() {
        Table content = new Table();

        List<Player> starters = new ArrayList<>(club.getTacticsMap().values());
        List<Player> nonStarters = new ArrayList<>();

        // Filtra apenas jogadores disponíveis (que NÃO estão lesionados nem suspensos/expulsos)
        for (Player p : club.getSquad()) {
            if (!starters.contains(p) && p.canPlay()) {
                nonStarters.add(p);
            }
        }

        Table listContent = new Table();
        listContent.top().defaults().growX().padBottom(4);

        Label benchTitle = new Label("BANCO DE RESERVAS (MÁX. 7)", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        benchTitle.setFontScale(0.85f);
        listContent.add(benchTitle).left().padTop(5).padBottom(5).row();

        int benchCount = Math.min(7, nonStarters.size());
        if (benchCount == 0) {
            Label emptyLbl = new Label("Nenhum reserva disponível", game.skin, "font-label", Color.GRAY);
            emptyLbl.setFontScale(0.75f);
            listContent.add(emptyLbl).left().padBottom(8).row();
        } else {
            for (int i = 0; i < benchCount; i++) {
                Player p = nonStarters.get(i);
                listContent.add(createSquadRow(p, nonStarters, i, true)).row();
            }
        }

        Label unassignedTitle = new Label("NÃO RELACIONADOS", game.skin, "font-bold", Color.LIGHT_GRAY);
        unassignedTitle.setFontScale(0.85f);
        listContent.add(unassignedTitle).left().padTop(15).padBottom(5).row();

        if (nonStarters.size() <= benchCount) {
            Label emptyUnassignedLbl = new Label("Nenhum jogador na reserva estendida", game.skin, "font-label", Color.GRAY);
            emptyUnassignedLbl.setFontScale(0.75f);
            listContent.add(emptyUnassignedLbl).left().row();
        } else {
            for (int i = benchCount; i < nonStarters.size(); i++) {
                Player p = nonStarters.get(i);
                listContent.add(createSquadRow(p, nonStarters, i, false)).row();
            }
        }

        ScrollPane scroll = new ScrollPane(listContent, game.skin);
        scroll.setFadeScrollBars(false);
        content.add(scroll).grow();

        return content;
    }

    private Table createSquadRow(final Player p, final List<Player> nonStarters, final int index, boolean isBench) {
        Table row = new Table();
        row.setTransform(true);
        row.setOrigin(Align.center);

        boolean isSelected = (p == selectedPlayer);
        Color bg = isSelected ? StyleFactory.WINE_RED : (isBench ? StyleFactory.METAL_DARK : Color.valueOf("18221D"));
        row.background(StyleFactory.createRoundedPanel(bg, isSelected ? StyleFactory.GOLD : Color.CLEAR));
        row.pad(5);

        Label posLbl = new Label(p.getPosition(), game.skin, "font-bold", StyleFactory.GOLD);
        posLbl.setFontScale(0.8f);

        String displayName = p.getName();
        if (!p.canPlay()) displayName += " ⛔";

        Label nameLbl = new Label(displayName, game.skin, "font-label", p.canPlay() ? Color.WHITE : Color.GRAY);
        nameLbl.setFontScale(0.85f);
        nameLbl.setEllipsis(true);

        Label ovrLbl = new Label(String.valueOf(p.getOverall()), game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        ovrLbl.setFontScale(0.85f);

        row.add(posLbl).width(35).left();
        row.add(nameLbl).expandX().left().padLeft(5);
        row.add(ovrLbl).right().width(30);

        row.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isSelected) row.addAction(Actions.scaleTo(1.02f, 1.02f, 0.08f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!isSelected) row.addAction(Actions.scaleTo(1.0f, 1.0f, 0.08f));
            }
        });

        row.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedPlayer != null && selectedPlayer != p) {
                    if (nonStarters.contains(selectedPlayer)) {
                        int prevIndex = club.getSquad().indexOf(selectedPlayer);
                        int currIndex = club.getSquad().indexOf(p);

                        if (prevIndex != -1 && currIndex != -1) {
                            club.getSquad().set(prevIndex, p);
                            club.getSquad().set(currIndex, selectedPlayer);
                        }
                        selectedPlayer = null;
                    } else {
                        if (p.canPlay()) {
                            selectedPlayer = p;
                        }
                    }
                } else {
                    selectedPlayer = (selectedPlayer == p) ? null : p;
                }
                refreshUI();
            }
        });

        return row;
    }

    private Table createTacticsPanelContent() {
        Table panel = new Table();
        Table list = new Table();
        list.top().defaults().growX().padBottom(10);

        Slider.SliderStyle sliderStyle = getCustomSliderStyle();

        Table tempoBox = createTacticCard("RITMO", "Lento", "Rápido");
        final Slider sTempo = new Slider(0, 100, 5, false, sliderStyle);
        sTempo.setValue(club.getTempo());

        final Label attacksLbl = new Label("Ataques previstos: " + Math.round(10 + (sTempo.getValue() / 10f) * 2f), game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        attacksLbl.setFontScale(0.8f);

        sTempo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setTempo(sTempo.getValue());
                attacksLbl.setText("Ataques previstos: " + Math.round(10 + (sTempo.getValue() / 10f) * 2f));
            }
        });
        tempoBox.add(sTempo).growX().pad(4, 0, 4, 0).row();
        tempoBox.add(attacksLbl).left().padTop(2);
        list.add(tempoBox).row();

        Table mentalityBox = createTacticCard("MENTALIDADE", "Defensiva", "Ofensiva");
        final Slider sMentality = new Slider(10, 90, 20, false, sliderStyle);
        sMentality.setValue(club.getMentalityValue());

        final Table mentalityMetrics = new Table();
        updateMentalityMetrics(mentalityMetrics, club.getMentalityValue() / 10f);

        sMentality.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = sMentality.getValue();
                if (val <= 20) club.setMentality("Defensiva");
                else if (val <= 40) club.setMentality("Equilibrada");
                else if (val <= 60) club.setMentality("Ofensiva");
                else club.setMentality("Ultra Ofensiva");

                updateMentalityMetrics(mentalityMetrics, val / 10f);
            }
        });
        mentalityBox.add(sMentality).growX().pad(4, 0, 4, 0).row();
        mentalityBox.add(mentalityMetrics).growX();
        list.add(mentalityBox).row();

        Table passingBox = createTacticCard("ESTILO DE PASSE", "Curto", "Longo");
        final Slider sPassing = new Slider(0, 100, 5, false, sliderStyle);
        sPassing.setValue(club.getPassing());

        final Table passingMetrics = new Table();
        updatePassingMetrics(passingMetrics, club.getPassing() / 10f);

        sPassing.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setPassing(sPassing.getValue());
                updatePassingMetrics(passingMetrics, sPassing.getValue() / 10f);
            }
        });
        passingBox.add(sPassing).growX().pad(4, 0, 4, 0).row();
        passingBox.add(passingMetrics).growX();
        list.add(passingBox).row();

        Table widthBox = createTacticCard("AMPLITUDE", "Estreita", "Aberta");
        final Slider sWidth = new Slider(0, 100, 5, false, sliderStyle);
        sWidth.setValue(club.getWidth());

        final Label widthDescLbl = new Label(sWidth.getValue() > 50 ? "Foco: Jogo pelas pontas e cruzamentos" : "Foco: Infiltração pelo centro", game.skin, "font-label", Color.WHITE);
        widthDescLbl.setFontScale(0.75f);

        sWidth.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setWidth(sWidth.getValue());
                widthDescLbl.setText(sWidth.getValue() > 50 ? "Foco: Jogo pelas pontas e cruzamentos" : "Foco: Infiltração pelo centro");
            }
        });
        widthBox.add(sWidth).growX().pad(4, 0, 4, 0).row();
        widthBox.add(widthDescLbl).left();
        list.add(widthBox).row();

        Table pressureBox = createTacticCard("PRESSÃO", "Baixa", "Alta");
        final Slider sPressure = new Slider(0, 100, 5, false, sliderStyle);
        sPressure.setValue(club.getPressure());

        final Table pressureMetrics = new Table();
        updatePressureMetrics(pressureMetrics, club.getPressure() / 10f);

        sPressure.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setPressure(sPressure.getValue());
                updatePressureMetrics(pressureMetrics, sPressure.getValue() / 10f);
            }
        });
        pressureBox.add(sPressure).growX().pad(4, 0, 4, 0).row();
        pressureBox.add(pressureMetrics).growX();
        list.add(pressureBox).row();

        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();

        return panel;
    }

    private Table createTacticCard(String title, String leftLabel, String rightLabel) {
        Table card = new Table();
        card.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        card.pad(8);

        Label titleLbl = new Label(title, game.skin, "font-bold", StyleFactory.GOLD);
        titleLbl.setFontScale(0.85f);
        card.add(titleLbl).left().colspan(2).padBottom(4).row();

        Label leftLbl = new Label(leftLabel, game.skin, "font-label", Color.LIGHT_GRAY);
        leftLbl.setFontScale(0.75f);
        Label rightLbl = new Label(rightLabel, game.skin, "font-label", Color.LIGHT_GRAY);
        rightLbl.setFontScale(0.75f);

        card.add(leftLbl).left();
        card.add(rightLbl).right().row();

        return card;
    }

    private void updateMentalityMetrics(Table container, float val) {
        container.clear();
        float chancePercent = val * 10f;
        float defensePercent = (11 - val) * 10f;

        container.add(createMetricRow("Criação de chances:", chancePercent, StyleFactory.GOLD)).growX().row();
        container.add(createMetricRow("Consistência defensiva:", defensePercent, Color.valueOf("4CAF50"))).growX();
    }

    private void updatePassingMetrics(Table container, float val) {
        container.clear();
        float possessionPercent = (11 - val) * 10f;
        float counterPercent = val * 10f;

        container.add(createMetricRow("Posse de bola:", possessionPercent, Color.valueOf("2196F3"))).growX().row();
        container.add(createMetricRow("Contra-ataques:", counterPercent, Color.valueOf("FF9800"))).growX();
    }

    private void updatePressureMetrics(Table container, float val) {
        container.clear();
        float recoveryPercent = val * 10f;
        float fatiguePercent = val * 10f;

        container.add(createMetricRow("Recuperação de bola:", recoveryPercent, Color.valueOf("4CAF50"))).growX().row();
        container.add(createMetricRow("Desgaste / Fadiga:", fatiguePercent, Color.valueOf("F44336"))).growX();
    }

    private Table createMetricRow(String labelText, float percent, Color barColor) {
        Table row = new Table();
        Label lbl = new Label(labelText, game.skin, "font-label", Color.WHITE);
        lbl.setFontScale(0.7f);
        row.add(lbl).left().expandX();

        Table bar = new Table();
        bar.background(getSolidDrawable(Color.DARK_GRAY));

        Table fill = new Table();
        fill.background(getSolidDrawable(barColor));

        float normalized = Math.max(0, Math.min(100, percent)) / 100f;
        bar.add(fill).width(100 * normalized).height(6).left().expandX();

        row.add(bar).width(100).height(6).right();
        return row;
    }

    private Slider.SliderStyle getCustomSliderStyle() {
        Pixmap bgPixmap = new Pixmap(100, 4, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.DARK_GRAY);
        bgPixmap.fill();
        Texture bgTex = new Texture(bgPixmap);
        bgPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(14, 14, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(StyleFactory.GOLD);
        knobPixmap.fillCircle(7, 7, 7);
        Texture knobTex = new Texture(knobPixmap);
        knobPixmap.dispose();

        return new Slider.SliderStyle(new TextureRegionDrawable(new TextureRegion(bgTex)), new TextureRegionDrawable(new TextureRegion(knobTex)));
    }

    private Table createFatigueBar(float fatiguePercent) {
        Table outerBar = new Table();
        outerBar.background(getSolidDrawable(Color.DARK_GRAY));

        Color barColor;
        if (fatiguePercent >= 70) barColor = Color.valueOf("4CAF50");
        else if (fatiguePercent >= 40) barColor = Color.valueOf("FFC107");
        else barColor = Color.valueOf("F44336");

        Table fillBar = new Table();
        fillBar.background(getSolidDrawable(barColor));

        float normalized = Math.max(0, Math.min(100, fatiguePercent)) / 100f;
        outerBar.add(fillBar).width(75 * normalized).height(5).left().expandX();

        return outerBar;
    }

    private TextureRegionDrawable getSolidDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private int getPositionDepthLayer(String pos) {
        if (pos == null) return 0;
        String p = pos.trim().toUpperCase();

        if (p.equals("ST") || p.equals("CF") || p.equals("SS") || p.equals("RF") || p.equals("LF")) {
            return 7;
        }
        if (p.equals("LW") || p.equals("RW")) {
            return 6;
        }
        if (p.contains("CAM") || p.equals("RAM") || p.equals("LAM") || p.equals("AM")) {
            return 5;
        }
        if (p.contains("CM") || p.equals("LM") || p.equals("RM")) {
            return 4;
        }
        if (p.contains("DM")) {
            return 3;
        }
        if (p.contains("WB")) {
            return 2;
        }
        if (p.contains("CB") || p.equals("LB") || p.equals("RB") || p.equals("SW")) {
            return 1;
        }

        return 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.1f, 0.09f, 1);
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
        if (pitchTexture != null) pitchTexture.dispose();
        if (kitTexture != null) kitTexture.dispose();
        stage.dispose();
    }
}
