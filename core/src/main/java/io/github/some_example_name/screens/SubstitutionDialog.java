package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubstitutionDialog extends Dialog {
    private final Main game;
    private final Club club;
    private final int maxSubstitutions = 5;
    private int substitutionCount = 0;
    private Player selectedPlayer = null;
    private Runnable onSubstitutionMade;

    public SubstitutionDialog(Main game, Club club, int currentSubstitutions, Runnable onSubstitutionMade) {
        super("", game.skin);
        this.game = game;
        this.club = club;
        this.substitutionCount = currentSubstitutions;
        this.onSubstitutionMade = onSubstitutionMade;
        getContentTable().clear();
        buildLayout();
    }

    private void buildLayout() {
        Table root = getContentTable();
        root.background(StyleFactory.createMetallicBoard(1000, 750, Color.valueOf("2B2B2B")));
        root.pad(30);

        // Header
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.WINE_RED, StyleFactory.GOLD));
        Label title = new Label("⚽ SUBSTITUIÇÕES", game.skin, "font-title", Color.WHITE);
        title.setFontScale(1.5f);
        header.add(title).pad(15);
        root.add(header).growX().padBottom(20).row();

        // Info de Substituições
        int remaining = maxSubstitutions - substitutionCount;
        Label infoLabel = new Label("DISPONÍVEIS: " + remaining + "/" + maxSubstitutions, game.skin, "font-bold",
            remaining > 0 ? StyleFactory.GOLD : Color.RED);
        infoLabel.setFontScale(1.2f);
        root.add(infoLabel).center().padBottom(20).row();

        if (remaining <= 0) {
            Label noMoreLabel = new Label("Limite de substituições atingido!", game.skin, "font-label", Color.RED);
            noMoreLabel.setFontScale(1.1f);
            root.add(noMoreLabel).center().padBottom(20).row();
        }

        // Conteúdo Principal (2 colunas)
        Table mainContent = new Table();

        // Coluna Esquerda: Titulares em campo
        Table titleColumnLeft = new Table();
        Label titleLeft = new Label("👕 TITULARES EM CAMPO", game.skin, "font-bold", StyleFactory.GOLD);
        titleLeft.setFontScale(1.1f);
        titleColumnLeft.add(titleLeft).padBottom(10).row();

        Table startersTable = createStartersTable();
        ScrollPane startersScroll = new ScrollPane(startersTable, game.skin);
        titleColumnLeft.add(startersScroll).growX().height(300);
        mainContent.add(titleColumnLeft).growX().padRight(15);

        // Coluna Direita: Bancos/Suplentes
        Table titleColumnRight = new Table();
        Label titleRight = new Label("🔄 SUPLENTES", game.skin, "font-bold", StyleFactory.GOLD);
        titleRight.setFontScale(1.1f);
        titleColumnRight.add(titleRight).padBottom(10).row();

        Table benchTable = createBenchTable();
        ScrollPane benchScroll = new ScrollPane(benchTable, game.skin);
        titleColumnRight.add(benchScroll).growX().height(300);
        mainContent.add(titleColumnRight).growX();

        root.add(mainContent).growX().padBottom(20).row();

        // Informação do jogador selecionado
        Table selectedInfoTable = new Table();
        selectedInfoTable.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        selectedInfoTable.pad(15);

        Label selectedTitle = new Label("ℹ️ INFORMAÇÕES DO JOGADOR", game.skin, "font-bold", StyleFactory.GOLD);
        selectedTitle.setFontScale(1.0f);
        selectedInfoTable.add(selectedTitle).left().row();

        Table playerInfoContent = new Table();
        playerInfoContent.add(new Label("Selecione um jogador para ver detalhes", game.skin, "font-label", StyleFactory.SOFT_YELLOW)).left().row();
        selectedInfoTable.add(playerInfoContent).growX();

        root.add(selectedInfoTable).growX().padBottom(20).row();

        // Botões de Ação
        Table buttonsTable = new Table();
        ImageTextButton btnContinue = IconTextButton.create("CONTINUAR", game.skin, "Icons8/icons8-ok-50.png");
        btnContinue.getLabel().setFontScale(1.1f);
        button(btnContinue, true);
    }

    private Table createStartersTable() {
        Table table = new Table();
        Map<Integer, Player> tactics = club.getTacticsMap();

        for (Map.Entry<Integer, Player> entry : tactics.entrySet()) {
            Player p = entry.getValue();

            TextButton btn = new TextButton("", game.skin);
            btn.add(createPlayerInfoWidget(p, true)).pad(8);
            btn.setDisabled(substitutionCount >= maxSubstitutions);

            btn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (substitutionCount < maxSubstitutions) {
                        selectedPlayer = p;
                        // Aqui você poderia atualizar a visualização de detalhes
                    }
                }
            });

            table.add(btn).growX().height(80).padBottom(5).row();
        }

        return table;
    }

    private Table createBenchTable() {
        Table table = new Table();
        List<Player> bench = getBenchPlayers();

        if (bench.isEmpty()) {
            Label noBench = new Label("Sem suplentes disponíveis", game.skin, "font-label", StyleFactory.SOFT_YELLOW);
            noBench.setFontScale(0.9f);
            table.add(noBench).center().padBottom(10).row();
        } else {
            for (Player p : bench) {
                TextButton btn = new TextButton("", game.skin);
                btn.add(createPlayerInfoWidget(p, false)).pad(8);

                btn.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event, float x, float y) {
                        if (selectedPlayer != null && substitutionCount < maxSubstitutions) {
                            performSubstitution(selectedPlayer, p);
                            if (onSubstitutionMade != null) onSubstitutionMade.run();
                            hide();
                        }
                    }
                });

                table.add(btn).growX().height(80).padBottom(5).row();
            }
        }

        return table;
    }

    private Table createPlayerInfoWidget(Player p, boolean isStarter) {
        Table widget = new Table();
        widget.background(StyleFactory.createRoundedPanel(
            isStarter ? StyleFactory.PRUSSIAN_GREEN : StyleFactory.METAL_DARK,
            StyleFactory.GOLD
        ));
        widget.pad(10);

        // Nome
        Label nameLabel = new Label(p.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLabel.setFontScale(0.95f);
        widget.add(nameLabel).left().padRight(10);

        // Posição
        Label posLabel = new Label(p.getPosition(), game.skin, "font-label", Color.WHITE);
        posLabel.setFontScale(0.85f);
        widget.add(posLabel).left().padRight(10);

        // Overall
        Label ovrLabel = new Label("OVR: " + p.getOverall(), game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        ovrLabel.setFontScale(0.85f);
        widget.add(ovrLabel).right().expandX();

        // Segunda linha: Fadiga e Status
        Table row2 = new Table();

        // Barra de fadiga
        ProgressBar fatigueBar = new ProgressBar(0, 100, 1, false, game.skin);
        fatigueBar.setValue(p.getFatigue());
        if (p.getFatigue() >= 80) fatigueBar.setColor(Color.GREEN);
        else if (p.getFatigue() >= 50) fatigueBar.setColor(Color.YELLOW);
        else fatigueBar.setColor(Color.RED);

        row2.add(fatigueBar).width(100).height(3).padRight(5);
        row2.add(new Label(p.getFatigue() + "%", game.skin, "font-label", Color.WHITE)).width(40);

        if (p.isInjured()) {
            row2.add(new Label("🏥", game.skin, "font-label", Color.RED)).padRight(5);
        }
        if (p.isSuspended()) {
            row2.add(new Label("🚫", game.skin, "font-label", Color.RED));
        }

        widget.add(row2).colspan(3).left().row();

        return widget;
    }

    private List<Player> getBenchPlayers() {
        List<Player> bench = new ArrayList<>();
        Map<Integer, Player> tactics = club.getTacticsMap();

        for (Player p : club.getSquad()) {
            if (!tactics.containsValue(p) && p.canPlay()) {
                bench.add(p);
            }
        }

        return bench;
    }

    private void performSubstitution(Player out, Player in) {
        // Encontrar slot do jogador que sai
        for (Map.Entry<Integer, Player> entry : club.getTacticsMap().entrySet()) {
            if (entry.getValue() == out) {
                club.assignPlayerToSlot(entry.getKey(), in);
                substitutionCount++;
                break;
            }
        }
    }

    public int getSubstitutionCount() {
        return substitutionCount;
    }
}

