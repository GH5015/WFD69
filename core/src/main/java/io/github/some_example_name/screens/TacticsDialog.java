package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
import java.util.function.BiConsumer;

public class TacticsDialog extends Dialog {

    private static final int MAX_SUBSTITUTIONS = 5;
    private static final int MAX_BENCH_PLAYERS = 7;

    private final Main game;
    private final Club club;

    private final Runnable onCloseCallback;
    private final BiConsumer<Player, Player> onSubstitutionListener;
    private final Runnable onTacticsChangedListener;

    private Player selectedPlayer;
    private Integer selectedSlot;

    private Texture pitchTexture;

    private Table contentTable;

    private Label substitutionCounterLabel;
    private Label selectedInfoLabel;

    private final List<Player> substitutedPlayers;
    private final List<Player> matchBenchPlayers;
    private boolean injuryReplacementPending;

    private int substitutionsUsed;

    public TacticsDialog(
        Main game,
        Club club,
        int initialSubstitutionsUsed,
        List<Player> substitutedPlayers,
        List<Player> matchBenchPlayers,
        boolean injuryReplacementPending,
        Runnable onCloseCallback,
        BiConsumer<Player, Player> onSubstitutionListener,
        Runnable onTacticsChangedListener
    ) {

        super(
            "",
            game.skin
        );

        this.game =
            game;

        this.club =
            club;

        this.substitutionsUsed =
            initialSubstitutionsUsed;

        this.substitutedPlayers =
            substitutedPlayers != null
                ? substitutedPlayers
                : new ArrayList<>();

        this.matchBenchPlayers =
            matchBenchPlayers != null
                ? new ArrayList<>(
                    matchBenchPlayers
                )
                : new ArrayList<>();

        this.injuryReplacementPending =
            injuryReplacementPending;

        this.onCloseCallback =
            onCloseCallback;

        this.onSubstitutionListener =
            onSubstitutionListener;

        this.onTacticsChangedListener =
            onTacticsChangedListener;

        ensureStartersPopulated();

        if (
            matchBenchPlayers == null
        ) {

            initializeMatchBench();
        }

        generatePitchTexture();

        setModal(
            true
        );

        setMovable(
            false
        );

        buildUI();
    }

    // =========================================================
    // INITIALIZATION
    // =========================================================

    private void ensureStartersPopulated() {

        Formation formation =
            club.getFormation();

        if (
            formation == null &&
                Formation.values().length > 0
        ) {

            formation =
                Formation.values()[0];

            club.setFormation(
                formation
            );
        }

        Map<Integer, Player> map =
            club.getTacticsMap();

        if (
            !map.isEmpty()
        ) {

            return;
        }

        List<Player> startingXI =
            club.getStartingXI();

        for (
            int i = 0;
            i < startingXI.size() &&
                i < 11;
            i++
        ) {

            map.put(
                i,
                startingXI.get(i)
            );
        }
    }

    // =========================================================
    // MAIN UI
    // =========================================================

    private void buildUI() {

        getContentTable()
            .clear();

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                1220,
                720,
                Color.valueOf(
                    "141A16"
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
            .height(64f)
            .padBottom(8f)
            .row();

        // =====================================================
        // CONTENT
        // =====================================================

        contentTable =
            new Table();

        root
            .add(contentTable)
            .width(1160f)
            .height(565f)
            .row();

        refreshContent();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table header =
            ScreenUI.createPanel();

        // =====================================================
        // TITLE
        // =====================================================

        Table titleArea =
            new Table();

        Label title =
            new Label(
                "TÁTICAS DURANTE A PARTIDA",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.70f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        titleArea
            .add(title)
            .left()
            .row();

        Label clubName =
            new Label(
                club.getName()
                    .toUpperCase(),
                game.skin,
                "font-bold"
            );

        clubName.setFontScale(
            0.47f
        );

        clubName.setColor(
            ScreenUI.MUTED_TEXT
        );

        titleArea
            .add(clubName)
            .left();

        header
            .add(titleArea)
            .left()
            .padRight(20f);

        // =====================================================
        // FORMATION
        // =====================================================

        Label formationLabel =
            ScreenUI.createSubtitle(
                game.skin,
                "FORMAÇÃO"
            );

        header
            .add(formationLabel)
            .padRight(7f);

        final SelectBox<Formation> formationBox =
            ScreenUI.createSelectBox(
                game.skin
            );

        formationBox.setItems(
            Formation.values()
        );

        if (
            club.getFormation() != null
        ) {

            formationBox.setSelected(
                club.getFormation()
            );
        }

        formationBox.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    Formation formation =
                        formationBox
                            .getSelected();

                    if (
                        formation == null
                    ) {

                        return;
                    }

                    club.setFormation(
                        formation
                    );

                    selectedPlayer =
                        null;

                    selectedSlot =
                        null;

                    if (
                        onTacticsChangedListener != null
                    ) {

                        onTacticsChangedListener.run();
                    }

                    refreshContent();
                }
            }
        );

        header
            .add(formationBox)
            .width(200f)
            .height(50f)
            .padRight(16f);

        // =====================================================
        // SUBS
        // =====================================================

        substitutionCounterLabel =
            new Label(
                "",
                game.skin,
                "font-bold"
            );

        substitutionCounterLabel.setFontScale(
            0.58f
        );

        updateSubstitutionCounter();

        header
            .add(substitutionCounterLabel)
            .left()
            .padRight(12f);

        header
            .add()
            .expandX();

        // =====================================================
        // CLOSE
        // =====================================================

        ImageTextButton close =
            IconTextButton.create(
                "FECHAR",
                game.skin,
                "Icons8/icons8-fechar-janela-50.png"
            );

        close
            .getLabel()
            .setFontScale(
                0.55f
            );

        close.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    closeDialog();
                }
            }
        );

        header
            .add(close)
            .width(125f)
            .height(40f);

        return header;
    }

    // =========================================================
    // CONTENT
    // =========================================================

    private void refreshContent() {

        if (
            contentTable == null
        ) {

            return;
        }

        updateSubstitutionCounter();

        contentTable.clear();

        Table left =
            new Table();

        left.top();

        // =====================================================
        // PITCH
        // =====================================================

        left
            .add(
                createPitchPanel()
            )
            .grow()
            .padBottom(8f)
            .row();

        // =====================================================
        // SELECTED PLAYER
        // =====================================================

        left
            .add(
                createSelectedPlayerPanel()
            )
            .growX()
            .height(88f);

        contentTable
            .add(left)
            .grow()
            .padRight(10f);

        // =====================================================
        // RIGHT PANEL
        // =====================================================

        contentTable
            .add(
                createSquadPanel()
            )
            .width(390f)
            .growY();
    }

    // =========================================================
    // PITCH
    // =========================================================

    private Table createPitchPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Table heading =
            new Table();

        heading
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "ESCALAÇÃO"
                )
            )
            .left()
            .expandX();

        Label hint =
            ScreenUI.createSubtitle(
                game.skin,
                "Clique em dois titulares para trocar posições"
            );

        heading
            .add(hint)
            .right();

        panel
            .add(heading)
            .growX()
            .padBottom(7f)
            .row();

        Table pitch =
            createPitchLayout();

        panel
            .add(pitch)
            .grow();

        return panel;
    }

    private Table createPitchLayout() {

        Table pitch =
            new Table();

        pitch.background(
            new TextureRegionDrawable(
                new TextureRegion(
                    pitchTexture
                )
            )
        );

        pitch.pad(
            7f
        );

        Formation formation =
            club.getFormation();

        if (
            formation == null ||
                formation.getPositionSlots() == null
        ) {

            return pitch;
        }

        List<String> slots =
            formation.getPositionSlots();

        Table st =
            createLine();

        Table wing =
            createLine();

        Table cam =
            createLine();

        Table mid =
            createLine();

        Table cdm =
            createLine();

        Table wingback =
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
                club.getTacticsMap()
                    .get(i);

            Table card =
                createPlayerCard(
                    player,
                    position,
                    i
                );

            switch (
                getPositionDepthLayer(
                    position
                )
            ) {

                case 7:
                    st.add(card)
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 6:
                    wing.add(card)
                        .expandX()
                        .pad(2f, 4f, 2f, 4f);
                    break;

                case 5:
                    cam.add(card)
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 4:
                    mid.add(card)
                        .expandX()
                        .pad(2f, 4f, 2f, 4f);
                    break;

                case 3:
                    cdm.add(card)
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 2:
                    wingback.add(card)
                        .expandX()
                        .pad(2f, 4f, 2f, 4f);
                    break;

                case 1:
                    defense.add(card)
                        .expandX()
                        .pad(2f, 4f, 2f, 4f);
                    break;

                default:
                    goalkeeper.add(card)
                        .pad(2f);
                    break;
            }
        }

        pitch
            .add(st)
            .expand()
            .fillX()
            .row();

        pitch
            .add(wing)
            .expand()
            .fillX()
            .row();

        pitch
            .add(cam)
            .expand()
            .fillX()
            .row();

        pitch
            .add(mid)
            .expand()
            .fillX()
            .row();

        pitch
            .add(cdm)
            .expand()
            .fillX()
            .row();

        pitch
            .add(wingback)
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

        return pitch;
    }

    private Table createPlayerCard(
        Player player,
        String targetPosition,
        int slotIndex
    ) {

        Table card =
            new Table();

        boolean selected =
            player != null &&
                player == selectedPlayer;

        boolean unavailable =
            player != null &&
                (
                    player.isInjured() ||
                        player.isSuspended() ||
                        substitutedPlayers.contains(
                            player
                        )
                );

        Color background =
            unavailable
                ? Color.valueOf(
                "281919"
            )
                : selected
                ? Color.valueOf(
                "302604"
            )
                : ScreenUI.PANEL;

        card.background(
            StyleFactory.createRoundedPanel(
                background,
                selected
                    ? StyleFactory.PLAYOFF_GOLD
                    : StyleFactory.DARK_GOLD
            )
        );

        card.pad(
            4f
        );

        if (
            player == null
        ) {

            Label position =
                new Label(
                    targetPosition,
                    game.skin,
                    "font-bold"
                );

            position.setFontScale(
                0.60f
            );

            position.setColor(
                StyleFactory.GOLD
            );

            card
                .add(position)
                .center()
                .row();

            Label empty =
                new Label(
                    "VAZIO",
                    game.skin
                );

            empty.setFontScale(
                0.47f
            );

            empty.setColor(
                Color.GRAY
            );

            card
                .add(empty)
                .center();

        } else {

            String name =
                ScreenUI.shorten(
                    player.getName(),
                    12
                );

            Label nameLabel =
                new Label(
                    name,
                    game.skin,
                    "font-bold"
                );

            nameLabel.setFontScale(
                0.51f
            );

            nameLabel.setColor(
                unavailable
                    ? ScreenUI.DANGER
                    : Color.WHITE
            );

            nameLabel.setAlignment(
                Align.center
            );

            card
                .add(nameLabel)
                .width(92f)
                .center()
                .row();

            int effective =
                player
                    .getEffectiveOverallForPosition(
                        targetPosition
                    );

            Table details =
                new Table();

            details
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

            Label overall =
                new Label(
                    String.valueOf(
                        effective
                    ),
                    game.skin,
                    "font-bold"
                );

            overall.setFontScale(
                0.57f
            );

            overall.setColor(
                effective < player.getOverall()
                    ? ScreenUI.WARNING
                    : StyleFactory.SOFT_YELLOW
            );

            details
                .add(overall);

            card
                .add(details)
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
                        getFatigueColor(
                            player.getFatigue()
                        )
                    )
                )
                .center();
        }

        final Player clickedPlayer =
            player;

        final int clickedSlot =
            slotIndex;

        card.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    handlePitchClick(
                        clickedPlayer,
                        clickedSlot
                    );
                }
            }
        );

        return card;
    }

    // =========================================================
    // PITCH CLICK
    // =========================================================

    private void handlePitchClick(
        Player player,
        int slotIndex
    ) {

        if (
            player != null &&
                substitutedPlayers
                    .contains(
                        player
                    )
        ) {

            return;
        }

        /*
         * Reserva selecionado.
         * Clicar num slot realiza a substituição.
         */
        if (
            selectedPlayer != null &&
                selectedSlot == null
        ) {

            if (
                substitutionsUsed >= MAX_SUBSTITUTIONS ||
                    !selectedPlayer.canPlay()
            ) {

                return;
            }

            Player oldStarter =
                club.getTacticsMap()
                    .get(slotIndex);

            if (
                !registerSubstitution(
                    oldStarter,
                    selectedPlayer
                )
            ) {

                return;
            }

            club.assignPlayerToSlot(
                slotIndex,
                selectedPlayer
            );

            notifyTacticsChanged();

            selectedPlayer =
                null;

            selectedSlot =
                null;

            refreshContent();

            return;
        }

        /*
         * Um titular já foi selecionado.
         * Segundo titular troca de posição.
         */
        if (
            selectedSlot != null &&
                selectedSlot != slotIndex
        ) {

            Player first =
                club.getTacticsMap()
                    .get(
                        selectedSlot
                    );

            Player second =
                club.getTacticsMap()
                    .get(
                        slotIndex
                    );

            boolean firstLocked =
                first != null &&
                    substitutedPlayers
                        .contains(
                            first
                        );

            boolean secondLocked =
                second != null &&
                    substitutedPlayers
                        .contains(
                            second
                        );

            if (
                !firstLocked &&
                    !secondLocked
            ) {

                club.assignPlayerToSlot(
                    selectedSlot,
                    second
                );

                club.assignPlayerToSlot(
                    slotIndex,
                    first
                );

                notifyTacticsChanged();
            }

            selectedPlayer =
                null;

            selectedSlot =
                null;

            refreshContent();

            return;
        }

        /*
         * Seleciona titular.
         */
        if (
            player != null
        ) {

            selectedPlayer =
                player;

            selectedSlot =
                slotIndex;

        } else {

            selectedPlayer =
                null;

            selectedSlot =
                null;
        }

        refreshContent();
    }

    // =========================================================
    // SELECTED PLAYER
    // =========================================================

    private Table createSelectedPlayerPanel() {

        Table panel =
            ScreenUI.createPanel();

        if (
            selectedPlayer == null
        ) {

            String text =
                substitutionsUsed >= MAX_SUBSTITUTIONS
                    ? "Limite de 5 substituições atingido. Ainda é possível reorganizar os titulares."
                    : "Selecione um titular para trocar posição ou escolha um reserva para fazer uma substituição.";

            Label hint =
                ScreenUI.createValueLabel(
                    game.skin,
                    text,
                    ScreenUI.MUTED_TEXT,
                    Align.center
                );

            hint.setWrap(
                true
            );

            panel
                .add(hint)
                .width(640f)
                .center();

            return panel;
        }

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                selectedPlayer
                    .getPosition(),
                StyleFactory
                    .getPositionColor(
                        selectedPlayer
                            .getPosition()
                    )
            );

        panel
            .add(badge)
            .height(28f)
            .padRight(12f);

        Table info =
            new Table();

        Label name =
            new Label(
                selectedPlayer
                    .getName()
                    .toUpperCase(),
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.64f
        );

        name.setColor(
            StyleFactory.GOLD
        );

        info
            .add(name)
            .left()
            .row();

        String actionText =
            selectedSlot != null
                ? "Titular selecionado • clique em outro titular para inverter posição."
                : "Reserva selecionado • clique no titular que deverá sair.";

        selectedInfoLabel =
            new Label(
                actionText,
                game.skin
            );

        selectedInfoLabel.setFontScale(
            0.49f
        );

        selectedInfoLabel.setColor(
            ScreenUI.MUTED_TEXT
        );

        info
            .add(selectedInfoLabel)
            .left()
            .padTop(3f);

        panel
            .add(info)
            .left()
            .expandX();

        Table stats =
            ScreenUI.createStatusBox(
                game.skin,
                "OVR",
                String.valueOf(
                    selectedPlayer.getOverall()
                ),
                StyleFactory.SOFT_YELLOW
            );

        panel
            .add(stats)
            .width(125f)
            .height(40f)
            .padRight(7f);

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

                    selectedPlayer =
                        null;

                    selectedSlot =
                        null;

                    refreshContent();
                }
            }
        );

        panel
            .add(cancel)
            .width(105f)
            .height(36f);

        return panel;
    }

    // =========================================================
    // SQUAD PANEL
    // =========================================================

    private Table createSquadPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Table heading =
            new Table();

        heading
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "BANCO DE RESERVAS"
                )
            )
            .left()
            .expandX();

        int remaining =
            Math.max(
                0,
                MAX_SUBSTITUTIONS -
                    substitutionsUsed
            );

        heading
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    remaining +
                        " RESTANTES",
                    remaining > 0
                        ? Color.valueOf(
                        "285B45"
                    )
                        : Color.valueOf(
                        "623636"
                    )
                )
            )
            .height(26f);

        panel
            .add(heading)
            .growX()
            .padBottom(8f)
            .row();

        Table list =
            new Table();

        list.top();

        List<Player> bench =
            getBenchPlayers();

        if (
            bench.isEmpty()
        ) {

            list
                .add(
                    ScreenUI.createSubtitle(
                        game.skin,
                        "Nenhum jogador disponível."
                    )
                )
                .left()
                .pad(12f)
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
                        createBenchRow(
                            player,
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

    private Table createBenchRow(
        Player player,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        boolean selected =
            player == selectedPlayer;

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
            .width(58f)
            .height(25f)
            .padLeft(5f);

        Label name =
            ScreenUI.createBoldValue(
                game.skin,
                ScreenUI.shorten(
                    player.getName(),
                    18
                ),
                player.canPlay()
                    ? Color.WHITE
                    : ScreenUI.DANGER,
                Align.left
            );

        row
            .add(name)
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
            .width(42f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    player.getFatigue() +
                        "%",
                    getFatigueColor(
                        player.getFatigue()
                    ),
                    Align.center
                )
            )
            .width(55f)
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
                        substitutionsUsed >= MAX_SUBSTITUTIONS ||
                            !player.canPlay()
                    ) {

                        return;
                    }

                    /*
                     * Um titular foi selecionado.
                     * Clicar no reserva faz a substituição.
                     */
                    if (
                        selectedSlot != null
                    ) {

                        Player oldStarter =
                            club.getTacticsMap()
                                .get(
                                    selectedSlot
                                );

                        if (
                            !registerSubstitution(
                                oldStarter,
                                player
                            )
                        ) {

                            return;
                        }

                        club.assignPlayerToSlot(
                            selectedSlot,
                            player
                        );

                        notifyTacticsChanged();

                        selectedPlayer =
                            null;

                        selectedSlot =
                            null;

                        refreshContent();

                        return;
                    }

                    /*
                     * Seleciona reserva primeiro.
                     */
                    selectedPlayer =
                        selectedPlayer == player
                            ? null
                            : player;

                    selectedSlot =
                        null;

                    refreshContent();
                }
            }
        );

        return row;
    }

    private boolean registerSubstitution(
        Player outPlayer,
        Player inPlayer
    ) {

        if (
            outPlayer == null &&
                !injuryReplacementPending
        ) {

            return false;
        }

        if (
            outPlayer != null &&
                !substitutedPlayers.contains(
                    outPlayer
                )
        ) {

            substitutedPlayers.add(
                outPlayer
            );
        }

        if (
            outPlayer == null
        ) {

            injuryReplacementPending =
                false;
        }

        substitutionsUsed++;

        if (
            onSubstitutionListener != null
        ) {

            onSubstitutionListener.accept(
                outPlayer,
                inPlayer
            );
        }

        return true;
    }

    // =========================================================
    // BENCH
    // =========================================================

    private List<Player> getBenchPlayers() {

        List<Player> starters =
            new ArrayList<>(
                club.getTacticsMap()
                    .values()
            );

        List<Player> bench =
            new ArrayList<>();

        for (
            Player player :
            matchBenchPlayers
        ) {

            if (
                !starters.contains(
                    player
                ) &&
                    !substitutedPlayers
                        .contains(
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

    private void initializeMatchBench() {

        List<Player> starters =
            new ArrayList<>(
                club.getTacticsMap()
                    .values()
            );

        for (
            Player player :
            club.getSquad()
        ) {

            if (
                !starters.contains(
                    player
                ) &&
                    player.canPlay()
            ) {

                matchBenchPlayers.add(
                    player
                );

                if (
                    matchBenchPlayers.size() ==
                        MAX_BENCH_PLAYERS
                ) {

                    return;
                }
            }
        }
    }

    // =========================================================
    // COUNTER
    // =========================================================

    private void updateSubstitutionCounter() {

        if (
            substitutionCounterLabel ==
                null
        ) {

            return;
        }

        substitutionCounterLabel.setText(
            "SUBSTITUIÇÕES  " +
                substitutionsUsed +
                "/" +
                MAX_SUBSTITUTIONS
        );

        substitutionCounterLabel.setColor(
            substitutionsUsed >=
                MAX_SUBSTITUTIONS
                ? ScreenUI.DANGER
                : StyleFactory.SOFT_YELLOW
        );
    }

    // =========================================================
    // PITCH TEXTURE
    // =========================================================

    private void generatePitchTexture() {

        if (
            pitchTexture != null
        ) {

            pitchTexture.dispose();
        }

        int width =
            600;

        int height =
            650;

        Pixmap pixmap =
            new Pixmap(
                width,
                height,
                Pixmap.Format.RGBA8888
            );

        pixmap.setColor(
            Color.valueOf(
                "173C20"
            )
        );

        pixmap.fill();

        pixmap.setColor(
            Color.valueOf(
                "1D4926"
            )
        );

        int stripe =
            height /
                10;

        for (
            int i = 0;
            i < 10;
            i += 2
        ) {

            pixmap.fillRectangle(
                0,
                i *
                    stripe,
                width,
                stripe
            );
        }

        pixmap.setColor(
            new Color(
                1f,
                1f,
                1f,
                0.40f
            )
        );

        pixmap.drawRectangle(
            12,
            12,
            width - 24,
            height - 24
        );

        int middle =
            height /
                2;

        pixmap.drawLine(
            12,
            middle,
            width - 12,
            middle
        );

        pixmap.drawCircle(
            width /
                2,
            middle,
            58
        );

        pixmap.drawRectangle(
            width /
                2 -
                130,
            12,
            260,
            95
        );

        pixmap.drawRectangle(
            width /
                2 -
                130,
            height - 107,
            260,
            95
        );

        pitchTexture =
            new Texture(
                pixmap
            );

        pixmap.dispose();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Table createLine() {

        Table row =
            new Table();

        row.center();

        return row;
    }

    private Color getFatigueColor(
        float fatigue
    ) {

        if (
            fatigue >=
                70
        ) {

            return ScreenUI.SUCCESS;
        }

        if (
            fatigue >=
                40
        ) {

            return ScreenUI.WARNING;
        }

        return ScreenUI.DANGER;
    }

    private void notifyTacticsChanged() {

        if (
            onTacticsChangedListener !=
                null
        ) {

            onTacticsChangedListener.run();
        }
    }

    private void closeDialog() {

        hide();

        if (
            onCloseCallback != null
        ) {

            onCloseCallback.run();
        }
    }

    private int getPositionDepthLayer(
        String position
    ) {

        if (
            position == null
        ) {

            return 0;
        }

        String p =
            position
                .trim()
                .toUpperCase();

        if (
            p.equals("ST") ||
                p.equals("CF") ||
                p.equals("SS") ||
                p.equals("RF") ||
                p.equals("LF")
        ) {
            return 7;
        }

        if (
            p.equals("LW") ||
                p.equals("RW")
        ) {
            return 6;
        }

        if (
            p.contains("CAM") ||
                p.equals("RAM") ||
                p.equals("LAM") ||
                p.equals("AM")
        ) {
            return 5;
        }

        if (
            p.equals("CM") ||
                p.equals("LM") ||
                p.equals("RM")
        ) {
            return 4;
        }

        if (
            p.equals("CDM") ||
                p.contains("DM")
        ) {
            return 3;
        }

        if (
            p.contains("WB")
        ) {
            return 2;
        }

        if (
            p.equals("CB") ||
                p.equals("LB") ||
                p.equals("RB") ||
                p.equals("SW")
        ) {
            return 1;
        }

        return 0;
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    @Override
    public boolean remove() {

        if (
            pitchTexture != null
        ) {

            pitchTexture.dispose();

            pitchTexture =
                null;
        }

        return super.remove();
    }
}
