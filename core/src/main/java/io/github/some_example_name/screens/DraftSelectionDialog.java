package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.screens.ConfirmAddScoutDialog;

import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;

public class DraftSelectionDialog extends Dialog {

    private final Skin skin;

    private final DraftScoutManager scoutManager;

    private final List<Player> availableDraftPlayers;

    private final Runnable onPlayerAddedCallback;

    private final Stage parentStage;

    private String positionFilter =
        "TODOS";

    private Table listContainer;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DraftSelectionDialog(
        Skin skin,
        Stage stage,
        DraftScoutManager scoutManager,
        List<Player> draftPlayers,
        Runnable onPlayerAddedCallback
    ) {

        super(
            "",
            skin
        );

        this.skin =
            skin;

        this.parentStage =
            stage;

        this.scoutManager =
            scoutManager;

        this.availableDraftPlayers =
            draftPlayers != null
                ? draftPlayers
                : new ArrayList<>();

        this.onPlayerAddedCallback =
            onPlayerAddedCallback;

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

        getButtonTable()
            .clear();

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                780,
                650,
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
            .height(82f)
            .padBottom(9f)
            .row();

        // =====================================================
        // FILTER
        // =====================================================

        root
            .add(
                createFilterBar()
            )
            .growX()
            .height(50f)
            .padBottom(8f)
            .row();

        // =====================================================
        // LIST
        // =====================================================

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        listContainer =
            new Table();

        listContainer.top();

        ScrollPane scroll =
            new ScrollPane(
                listContainer,
                skin
            );

        scroll.setFadeScrollBars(
            false
        );

        panel
            .add(scroll)
            .grow();

        root
            .add(panel)
            .width(730f)
            .height(430f)
            .row();

        refreshList();

        // =====================================================
        // CLOSE
        // =====================================================

        ImageTextButton close =
            IconTextButton.create(
                "FECHAR",
                skin,
                "Icons8/icons8-fechar-janela-50.png"
            );

        close
            .getLabel()
            .setFontScale(
                0.56f
            );

        button(
            close,
            false
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table panel =
            ScreenUI.createPanel();

        Table identity =
            new Table();

        Label title =
            new Label(
                "BANCO DE CANDIDATOS",
                skin,
                "font-title"
            );

        title.setFontScale(
            0.72f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(title)
            .left()
            .row();

        Label subtitle =
            ScreenUI.createSubtitle(
                skin,
                "CLASSE DO DRAFT DE 1970"
            );

        identity
            .add(subtitle)
            .left()
            .padTop(2f);

        panel
            .add(identity)
            .left()
            .expandX();

        int occupied =
            scoutManager != null
                ? scoutManager
                .getActiveTargets()
                .size()
                : 0;

        panel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "VAGAS",
                    occupied +
                        "/5",
                    occupied >= 5
                        ? ScreenUI.DANGER
                        : ScreenUI.SUCCESS
                )
            )
            .width(175f)
            .height(42f);

        return panel;
    }

    // =========================================================
    // FILTER BAR
    // =========================================================

    private Table createFilterBar() {

        Table panel =
            ScreenUI.createSubtlePanel();

        Label label =
            ScreenUI.createSubtitle(
                skin,
                "FILTRAR POR POSIÇÃO"
            );

        panel
            .add(label)
            .left()
            .padRight(10f);

        final SelectBox<String> filter =
            ScreenUI.createSelectBox(
                skin
            );

        filter.setItems(
            "TODOS",
            "GK",
            "DEF",
            "MID",
            "ATA"
        );

        filter.setSelected(
            positionFilter
        );

        filter.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    com.badlogic.gdx.scenes.scene2d.Actor actor
                ) {

                    positionFilter =
                        filter.getSelected();

                    refreshList();
                }
            }
        );

        panel
            .add(filter)
            .width(180f)
            .height(48f);

        panel
            .add()
            .expandX();

        Label info =
            ScreenUI.createSubtitle(
                skin,
                "OVR e potencial são revelados gradualmente pelo scout."
            );

        panel
            .add(info)
            .right();

        return panel;
    }

    // =========================================================
    // LIST
    // =========================================================

    private void refreshList() {

        if (
            listContainer == null
        ) {

            return;
        }

        listContainer.clear();

        // =====================================================
        // HEADER
        // =====================================================

        Table header =
            ScreenUI.createTableHeaderRow();

        addHeader(
            header,
            "NAC",
            75f,
            Align.center
        );

        addHeader(
            header,
            "JOGADOR",
            260f,
            Align.left
        );

        addHeader(
            header,
            "IDADE",
            70f,
            Align.center
        );

        addHeader(
            header,
            "POS",
            80f,
            Align.center
        );

        addHeader(
            header,
            "AÇÃO",
            120f,
            Align.center
        );

        listContainer
            .add(header)
            .growX()
            .height(42f)
            .row();

        int rowIndex =
            0;

        for (
            Player player :
            availableDraftPlayers
        ) {

            if (
                !matchesFilter(
                    player
                )
            ) {

                continue;
            }

            if (
                isAlreadyScouted(
                    player
                )
            ) {

                continue;
            }

            Table row =
                createPlayerRow(
                    player,
                    rowIndex++
                );

            listContainer
                .add(row)
                .growX()
                .height(51f)
                .row();
        }

        if (
            rowIndex ==
                0
        ) {

            Table empty =
                ScreenUI.createSubtlePanel();

            Label text =
                ScreenUI.createValueLabel(
                    skin,
                    "Nenhum candidato disponível para este filtro.",
                    ScreenUI.MUTED_TEXT,
                    Align.center
                );

            empty
                .add(text)
                .pad(25f);

            listContainer
                .add(empty)
                .growX()
                .padTop(10f)
                .row();
        }
    }

    private Table createPlayerRow(
        Player player,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        // =====================================================
        // NATIONALITY
        // =====================================================

        String nationality =
            player.getNationality() != null
                ? player.getNationality()
                : "N/A";

        String shortNationality =
            nationality.substring(
                0,
                Math.min(
                    3,
                    nationality.length()
                )
            ).toUpperCase();

        row
            .add(
                ScreenUI.createBoldValue(
                    skin,
                    shortNationality,
                    ScreenUI.MUTED_TEXT,
                    Align.center
                )
            )
            .width(75f);

        // =====================================================
        // NAME
        // =====================================================

        Label name =
            ScreenUI.createBoldValue(
                skin,
                player.getName(),
                Color.WHITE,
                Align.left
            );

        row
            .add(name)
            .width(260f)
            .left()
            .padLeft(8f);

        // =====================================================
        // AGE
        // =====================================================

        row
            .add(
                ScreenUI.createValueLabel(
                    skin,
                    String.valueOf(
                        player.getAge()
                    ),
                    Color.WHITE,
                    Align.center
                )
            )
            .width(70f);

        // =====================================================
        // POSITION
        // =====================================================

        row
            .add(
                ScreenUI.createBadge(
                    skin,
                    player
                        .getPrimaryPosition()
                        .name(),
                    StyleFactory
                        .getPositionColor(
                            player
                                .getPrimaryPosition()
                                .name()
                        )
                )
            )
            .width(80f)
            .height(26f);

        // =====================================================
        // ACTION
        // =====================================================

        ImageTextButton observe =
            IconTextButton.create(
                "OBSERVAR",
                skin,
                "Icons8/icons8-binóculos-50.png"
            );

        observe
            .getLabel()
            .setFontScale(
                0.49f
            );

        observe.setDisabled(
            scoutManager == null ||
                scoutManager.isFull()
        );

        observe.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        scoutManager == null ||
                            scoutManager.isFull()
                    ) {

                        return;
                    }

                    hide();

                    ConfirmAddScoutDialog confirm =
                        new ConfirmAddScoutDialog(
                            skin,
                            player,
                            scoutManager,
                            onPlayerAddedCallback
                        );

                    confirm.show(
                        parentStage
                    );
                }
            }
        );

        row
            .add(observe)
            .width(120f)
            .height(34f)
            .padRight(4f);

        return row;
    }

    // =========================================================
    // FILTER
    // =========================================================

    private boolean matchesFilter(
        Player player
    ) {

        if (
            "TODOS".equals(
                positionFilter
            )
        ) {

            return true;
        }

        String pos =
            player
                .getPrimaryPosition()
                .name();

        if (
            "GK".equals(
                positionFilter
            )
        ) {

            return "GK".equals(
                pos
            );
        }

        if (
            "DEF".equals(
                positionFilter
            )
        ) {

            return pos.matches(
                "CB|LB|RB|LWB|RWB"
            );
        }

        if (
            "MID".equals(
                positionFilter
            )
        ) {

            return pos.matches(
                "CDM|CM|CAM|LM|RM"
            );
        }

        if (
            "ATA".equals(
                positionFilter
            )
        ) {

            return pos.matches(
                "LW|RW|CF|ST"
            );
        }

        return true;
    }

    private boolean isAlreadyScouted(
        Player player
    ) {

        if (
            scoutManager == null ||
                player == null
        ) {

            return false;
        }

        return scoutManager.containsPlayer(player);
    }

    // =========================================================
    // HEADER CELL
    // =========================================================

    private void addHeader(
        Table table,
        String text,
        float width,
        int alignment
    ) {

        table
            .add(
                ScreenUI.createTableHeaderLabel(
                    skin,
                    text,
                    alignment
                )
            )
            .width(width);
    }
}
