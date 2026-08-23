package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubstitutionDialog extends Dialog {

    private static final int MAX_SUBSTITUTIONS =
        5;

    private final Main game;
    private final Club club;

    private int substitutionCount;

    private Player selectedStarter;

    private final Runnable onSubstitutionMade;

    private Table contentArea;

    public SubstitutionDialog(
        Main game,
        Club club,
        int currentSubstitutions,
        Runnable onSubstitutionMade
    ) {

        super(
            "",
            game.skin
        );

        this.game =
            game;

        this.club =
            club;

        this.substitutionCount =
            currentSubstitutions;

        this.onSubstitutionMade =
            onSubstitutionMade;

        setModal(
            true
        );

        setMovable(
            false
        );

        buildUI();
    }

    // =========================================================
    // UI
    // =========================================================

    private void buildUI() {

        getContentTable()
            .clear();

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                1000,
                690,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        root
            .add(
                createHeader()
            )
            .growX()
            .height(66f)
            .padBottom(8f)
            .row();

        contentArea =
            new Table();

        root
            .add(contentArea)
            .width(940f)
            .height(520f)
            .row();

        refreshContent();

        ImageTextButton close =
            IconTextButton.create(
                "CONTINUAR",
                game.skin,
                "Icons8/icons8-ok-50.png"
            );

        close
            .getLabel()
            .setFontScale(
                0.60f
            );

        button(
            close,
            true
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table header =
            ScreenUI.createPanel();

        Table titleArea =
            new Table();

        Label title =
            new Label(
                "SUBSTITUIÇÕES",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.72f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        titleArea
            .add(title)
            .left()
            .row();

        Label clubLabel =
            ScreenUI.createSubtitle(
                game.skin,
                club.getName()
                    .toUpperCase()
            );

        titleArea
            .add(clubLabel)
            .left();

        header
            .add(titleArea)
            .left()
            .expandX();

        int remaining =
            Math.max(
                0,
                MAX_SUBSTITUTIONS -
                    substitutionCount
            );

        header
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "DISPONÍVEIS",
                    remaining +
                        "/" +
                        MAX_SUBSTITUTIONS,
                    remaining > 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .width(200f)
            .height(42f);

        return header;
    }

    // =========================================================
    // CONTENT
    // =========================================================

    private void refreshContent() {

        contentArea.clear();

        int remaining =
            MAX_SUBSTITUTIONS -
                substitutionCount;

        if (
            remaining <= 0
        ) {

            Table warning =
                ScreenUI.createPanel();

            Label text =
                new Label(
                    "LIMITE DE SUBSTITUIÇÕES ATINGIDO",
                    game.skin,
                    "font-bold"
                );

            text.setColor(
                ScreenUI.DANGER
            );

            warning
                .add(text)
                .center();

            contentArea
                .add(warning)
                .growX()
                .height(65f)
                .colspan(2)
                .padBottom(8f)
                .row();
        }

        // =====================================================
        // COLUMNS
        // =====================================================

        contentArea
            .add(
                createStartersPanel()
            )
            .grow()
            .uniformX()
            .padRight(8f);

        contentArea
            .add(
                createBenchPanel()
            )
            .grow()
            .uniformX()
            .row();

        contentArea
            .add(
                createSelectionPanel()
            )
            .growX()
            .height(90f)
            .colspan(2)
            .padTop(8f);
    }

    // =========================================================
    // STARTERS
    // =========================================================

    private Table createStartersPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "TITULARES EM CAMPO"
                )
            )
            .left()
            .padBottom(8f)
            .row();

        Table list =
            new Table();

        int index =
            0;

        for (
            Map.Entry<Integer, Player> entry :
            club.getTacticsMap()
                .entrySet()
        ) {

            Player player =
                entry.getValue();

            if (
                player == null
            ) {
                continue;
            }

            list
                .add(
                    createPlayerRow(
                        player,
                        true,
                        index++
                    )
                )
                .growX()
                .height(48f)
                .padBottom(3f)
                .row();
        }

        ScrollPane scroll =
            new ScrollPane(
                list,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        panel
            .add(scroll)
            .grow();

        return panel;
    }

    // =========================================================
    // BENCH
    // =========================================================

    private Table createBenchPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "SUPLENTES"
                )
            )
            .left()
            .padBottom(8f)
            .row();

        Table list =
            new Table();

        List<Player> bench =
            getBenchPlayers();

        if (
            bench.isEmpty()
        ) {

            list
                .add(
                    ScreenUI.createSubtitle(
                        game.skin,
                        "Sem suplentes disponíveis"
                    )
                )
                .left()
                .pad(10f)
                .row();

        } else {

            int index =
                0;

            for (
                Player player :
                bench
            ) {

                list
                    .add(
                        createPlayerRow(
                            player,
                            false,
                            index++
                        )
                    )
                    .growX()
                    .height(48f)
                    .padBottom(3f)
                    .row();
            }
        }

        ScrollPane scroll =
            new ScrollPane(
                list,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        panel
            .add(scroll)
            .grow();

        return panel;
    }

    // =========================================================
    // PLAYER ROW
    // =========================================================

    private Table createPlayerRow(
        Player player,
        boolean starter,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        boolean selected =
            starter &&
                player == selectedStarter;

        if (
            selected
        ) {

            row.background(
                StyleFactory.createRoundedPanel(
                    Color.valueOf(
                        "302604"
                    ),
                    StyleFactory.GOLD
                )
            );
        }

        row
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    player.getPosition(),
                    StyleFactory
                        .getPositionColor(
                            player.getPosition()
                        )
                )
            )
            .width(60f)
            .height(25f)
            .padLeft(5f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    ScreenUI.shorten(
                        player.getName(),
                        20
                    ),
                    player.canPlay()
                        ? Color.WHITE
                        : ScreenUI.DANGER,
                    Align.left
                )
            )
            .left()
            .expandX()
            .padLeft(7f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        player.getOverall()
                    ),
                    StyleFactory.SOFT_YELLOW,
                    Align.center
                )
            )
            .width(48f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    player.getFatigue() +
                        "%",
                    getConditionColor(
                        player.getFatigue()
                    ),
                    Align.center
                )
            )
            .width(60f)
            .padRight(5f);

        row.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        substitutionCount >=
                            MAX_SUBSTITUTIONS
                    ) {

                        return;
                    }

                    if (
                        starter
                    ) {

                        selectedStarter =
                            selectedStarter ==
                                player
                                ? null
                                : player;

                        refreshContent();

                    } else if (
                        selectedStarter != null &&
                            player.canPlay()
                    ) {

                        performSubstitution(
                            selectedStarter,
                            player
                        );
                    }
                }
            }
        );

        return row;
    }

    // =========================================================
    // SELECTED
    // =========================================================

    private Table createSelectionPanel() {

        Table panel =
            ScreenUI.createPanel();

        if (
            selectedStarter == null
        ) {

            Label message =
                ScreenUI.createValueLabel(
                    game.skin,
                    substitutionCount >=
                        MAX_SUBSTITUTIONS
                        ? "Nenhuma substituição restante."
                        : "Selecione primeiro o jogador que deverá sair.",
                    ScreenUI.MUTED_TEXT,
                    Align.center
                );

            panel
                .add(message)
                .center();

            return panel;
        }

        panel
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "SAI"
                )
            )
            .padRight(10f);

        panel
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    selectedStarter
                        .getName()
                        .toUpperCase(),
                    StyleFactory.GOLD,
                    Align.left
                )
            )
            .left();

        panel
            .add()
            .expandX();

        Label instruction =
            ScreenUI.createValueLabel(
                game.skin,
                "Agora escolha o jogador que entrará.",
                StyleFactory.CREME_AGED,
                Align.right
            );

        panel
            .add(instruction)
            .right()
            .padRight(15f);

        TextButton cancel =
            ScreenUI.createSecondaryButton(
                game.skin,
                "CANCELAR"
            );

        cancel.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    selectedStarter =
                        null;

                    refreshContent();
                }
            }
        );

        panel
            .add(cancel)
            .width(110f)
            .height(36f);

        return panel;
    }

    // =========================================================
    // SUBSTITUTION
    // =========================================================

    private void performSubstitution(
        Player out,
        Player in
    ) {

        if (
            out == null ||
                in == null ||
                substitutionCount >=
                    MAX_SUBSTITUTIONS
        ) {

            return;
        }

        for (
            Map.Entry<Integer, Player> entry :
            club.getTacticsMap()
                .entrySet()
        ) {

            if (
                entry.getValue() ==
                    out
            ) {

                club.assignPlayerToSlot(
                    entry.getKey(),
                    in
                );

                substitutionCount++;

                selectedStarter =
                    null;

                if (
                    onSubstitutionMade !=
                        null
                ) {

                    onSubstitutionMade.run();
                }

                refreshContent();

                return;
            }
        }
    }

    // =========================================================
    // BENCH
    // =========================================================

    private List<Player> getBenchPlayers() {

        List<Player> bench =
            new ArrayList<>();

        Map<Integer, Player> tactics =
            club.getTacticsMap();

        for (
            Player player :
            club.getSquad()
        ) {

            if (
                !tactics.containsValue(
                    player
                ) &&
                    player.canPlay()
            ) {

                bench.add(
                    player
                );
            }
        }

        return bench;
    }

    private Color getConditionColor(
        int fatigue
    ) {

        if (
            fatigue >=
                75
        ) {

            return ScreenUI.SUCCESS;
        }

        if (
            fatigue >=
                50
        ) {

            return ScreenUI.WARNING;
        }

        return ScreenUI.DANGER;
    }

    // =========================================================
    // GETTER
    // =========================================================

    public int getSubstitutionCount() {

        return substitutionCount;
    }
}
