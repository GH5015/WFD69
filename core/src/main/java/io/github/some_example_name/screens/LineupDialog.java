package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LineupDialog extends Dialog {

    private final Main game;
    private final Club club;

    public LineupDialog(
        Main game,
        Club club
    ) {

        super(
            "",
            game.skin
        );

        this.game =
            game;

        this.club =
            club;

        setModal(
            true
        );

        setMovable(
            false
        );

        buildLayout();
    }

    // =========================================================
    // LAYOUT
    // =========================================================

    private void buildLayout() {

        Table root =
            getContentTable();

        root.clear();

        root.background(
            StyleFactory.createMetallicBoard(
                1050,
                720,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        // =====================================================
        // HEADER
        // =====================================================

        root
            .add(
                createHeader()
            )
            .growX()
            .height(72f)
            .padBottom(8f)
            .row();

        // =====================================================
        // BODY
        // =====================================================

        Table body =
            new Table();

        body
            .add(
                createPitchPanel()
            )
            .grow()
            .padRight(8f);

        body
            .add(
                createPlayerListPanel()
            )
            .width(410f)
            .growY();

        root
            .add(body)
            .width(990f)
            .height(540f)
            .row();

        // =====================================================
        // ACTION
        // =====================================================

        ImageTextButton continueButton =
            IconTextButton.create(
                "CONTINUAR",
                game.skin,
                "Icons8/icons8-ok-50.png"
            );

        continueButton
            .getLabel()
            .setFontScale(
                0.60f
            );

        button(
            continueButton,
            true
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table header =
            ScreenUI.createPanel();

        Table identity =
            new Table();

        Label title =
            new Label(
                "ESCALAÇÃO",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.74f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(title)
            .left()
            .row();

        Label clubName =
            ScreenUI.createSubtitle(
                game.skin,
                club.getName()
                    .toUpperCase()
            );

        identity
            .add(clubName)
            .left();

        header
            .add(identity)
            .left()
            .expandX();

        String formation =
            club.getFormation() != null
                ? club
                .getFormation()
                .getName()
                : "N/D";

        header
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "FORMAÇÃO",
                    formation,
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(185f)
            .height(43f)
            .padRight(7f);

        header
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "TITULARES",
                    club.getTacticsMap()
                        .size() +
                        "/11",
                    club.getTacticsMap()
                        .size() >=
                        11
                        ? ScreenUI.SUCCESS
                        : ScreenUI.WARNING
                )
            )
            .width(175f)
            .height(43f);

        return header;
    }

    // =========================================================
    // PITCH
    // =========================================================

    private Table createPitchPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "CAMPO TÁTICO"
                )
            )
            .center()
            .padBottom(8f)
            .row();

        Table pitch =
            new Table();

        pitch.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "173C20"
                ),
                StyleFactory.DARK_GOLD
            )
        );

        pitch.pad(
            15f
        );

        Formation formation =
            club.getFormation();

        if (
            formation == null ||
                formation.getPositionSlots() ==
                    null
        ) {

            Label warning =
                new Label(
                    "Nenhuma formação definida.",
                    game.skin,
                    "font-bold"
                );

            warning.setColor(
                ScreenUI.WARNING
            );

            pitch
                .add(warning)
                .center();

            panel
                .add(pitch)
                .grow();

            return panel;
        }

        List<String> slots =
            formation
                .getPositionSlots();

        Map<Integer, Player> tactics =
            club.getTacticsMap();

        Table attackers =
            createLine();

        Table midfield =
            createLine();

        Table defense =
            createLine();

        Table goalkeeper =
            createLine();

        for (
            int i = 0;
            i < Math.min(
                11,
                slots.size()
            );
            i++
        ) {

            String position =
                slots.get(i);

            Player player =
                tactics.get(i);

            Table card =
                createPlayerCard(
                    player,
                    position
                );

            int weight =
                getPositionWeight(
                    position
                );

            if (
                weight ==
                    4
            ) {

                attackers
                    .add(card)
                    .pad(5f);

            } else if (
                weight ==
                    3
            ) {

                midfield
                    .add(card)
                    .pad(5f);

            } else if (
                weight ==
                    2
            ) {

                defense
                    .add(card)
                    .pad(5f);

            } else {

                goalkeeper
                    .add(card)
                    .pad(5f);
            }
        }

        pitch
            .add(attackers)
            .expand()
            .fillX()
            .row();

        pitch
            .add(midfield)
            .expand()
            .fillX()
            .row();

        pitch
            .add(defense)
            .expand()
            .fillX()
            .row();

        pitch
            .add(goalkeeper)
            .expand()
            .fillX()
            .row();

        panel
            .add(pitch)
            .grow();

        return panel;
    }

    private Table createPlayerCard(
        Player player,
        String targetPosition
    ) {

        Table card =
            new Table();

        boolean unavailable =
            player != null &&
                !player.canPlay();

        card.background(
            StyleFactory.createRoundedPanel(
                unavailable
                    ? Color.valueOf(
                    "281919"
                )
                    : ScreenUI.PANEL,
                StyleFactory.DARK_GOLD
            )
        );

        card.pad(
            5f
        );

        if (
            player == null
        ) {

            Label pos =
                new Label(
                    targetPosition,
                    game.skin,
                    "font-bold"
                );

            pos.setColor(
                StyleFactory.GOLD
            );

            pos.setFontScale(
                0.57f
            );

            card
                .add(pos)
                .center()
                .row();

            Label empty =
                new Label(
                    "VAZIO",
                    game.skin
                );

            empty.setFontScale(
                0.45f
            );

            empty.setColor(
                Color.GRAY
            );

            card
                .add(empty)
                .center();

            return card;
        }

        Label name =
            new Label(
                ScreenUI.shorten(
                    player.getName(),
                    12
                ),
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.49f
        );

        name.setColor(
            unavailable
                ? ScreenUI.DANGER
                : Color.WHITE
        );

        name.setAlignment(
            Align.center
        );

        card
            .add(name)
            .width(95f)
            .center()
            .row();

        int effective =
            player
                .getEffectiveOverallForPosition(
                    targetPosition
                );

        Table info =
            new Table();

        info
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    targetPosition,
                    StyleFactory
                        .getPositionColor(
                            targetPosition
                        )
                )
            )
            .height(22f)
            .padRight(4f);

        Label ovr =
            new Label(
                String.valueOf(
                    effective
                ),
                game.skin,
                "font-bold"
            );

        ovr.setFontScale(
            0.55f
        );

        ovr.setColor(
            effective <
                player.getOverall()
                ? ScreenUI.WARNING
                : StyleFactory.SOFT_YELLOW
        );

        info.add(
            ovr
        );

        card
            .add(info)
            .center()
            .padTop(2f)
            .padBottom(3f)
            .row();

        card
            .add(
                ScreenUI.createBlockProgress(
                    game.skin,
                    player.getFatigue(),
                    7,
                    getConditionColor(
                        player.getFatigue()
                    )
                )
            )
            .center();

        return card;
    }

    // =========================================================
    // LIST
    // =========================================================

    private Table createPlayerListPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "TITULARES"
                )
            )
            .left()
            .padBottom(8f)
            .row();

        Table list =
            new Table();

        List<Integer> slots =
            new ArrayList<>(
                club.getTacticsMap()
                    .keySet()
            );

        slots.sort(
            Integer::compareTo
        );

        int index =
            0;

        for (
            Integer slot :
            slots
        ) {

            Player player =
                club.getTacticsMap()
                    .get(slot);

            if (
                player == null
            ) {

                continue;
            }

            list
                .add(
                    createDetailsRow(
                        player,
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

    private Table createDetailsRow(
        Player player,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

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
            .width(58f)
            .height(25f)
            .padLeft(4f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    ScreenUI.shorten(
                        player.getName(),
                        17
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
            .width(45f);

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
            .width(55f)
            .padRight(4f);

        return row;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Table createLine() {

        Table line =
            new Table();

        line.center();

        return line;
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

    private int getPositionWeight(
        String position
    ) {

        if (
            position == null
        ) {

            return 4;
        }

        if (
            position.equalsIgnoreCase(
                "GK"
            )
        ) {

            return 1;
        }

        if (
            position.matches(
                "CB|RB|LB|RWB|LWB|SW"
            )
        ) {

            return 2;
        }

        if (
            position.matches(
                "CDM|CM|CAM|RM|LM|RAM|LAM|AM"
            )
        ) {

            return 3;
        }

        return 4;
    }
}
