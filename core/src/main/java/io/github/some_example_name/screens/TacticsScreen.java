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
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;

public class TacticsScreen implements Screen {

    private final Main game;
    private final Club club;

    private final Stage stage;

    private Player selectedPlayer;

    private Texture pitchTexture;

    private Texture sliderBackgroundTexture;
    private Texture sliderKnobTexture;

    private boolean showTacticsTab =
        false;

    public TacticsScreen(
        Main game,
        Club club
    ) {

        this.game =
            game;

        this.club =
            club;

        this.stage =
            new Stage(
                new ScreenViewport()
            );
    }

    // =========================================================
    // SHOW
    // =========================================================

    @Override
    public void show() {

        Gdx.input.setInputProcessor(
            stage
        );

        ensurePitchTexture();

        ensureSliderTextures();

        refreshUI();
    }

    // =========================================================
    // MAIN UI
    // =========================================================

    private void refreshUI() {

        stage.clear();

        Stack root =
            new Stack();

        root.setFillParent(
            true
        );

        stage.addActor(
            root
        );

        root.add(
            new Image(
                game.background
            )
        );

        Table page =
            ScreenUI.createPage(
                true
            );

        // =====================================================
        // HEADER
        // =====================================================

        page
            .add(
                createHeader()
            )
            .growX()
            .height(78f)
            .padBottom(10f)
            .row();

        // =====================================================
        // BODY
        // =====================================================

        Table body =
            new Table();

        Table left =
            new Table();

        left.top();

        if (
            club.getFormation() ==
                null
        ) {

            left
                .add(
                    createNoFormationPanel()
                )
                .grow()
                .padBottom(10f)
                .row();

        } else {

            left
                .add(
                    createPitchPanel()
                )
                .grow()
                .padBottom(10f)
                .row();
        }

        left
            .add(
                createSelectedPlayerPanel()
            )
            .growX()
            .height(105f)
            .row();

        body
            .add(left)
            .grow()
            .padRight(10f);

        body
            .add(
                createRightPanel()
            )
            .width(390f)
            .growY();

        page
            .add(body)
            .grow()
            .row();

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            club,
            "TÁTICAS",
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
                "TÁTICAS E ESCALAÇÃO",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.78f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        titleArea
            .add(title)
            .left()
            .row();

        Label clubLabel =
            new Label(
                club.getName().toUpperCase(),
                game.skin,
                "font-bold"
            );

        clubLabel.setFontScale(
            0.52f
        );

        clubLabel.setColor(
            ScreenUI.MUTED_TEXT
        );

        titleArea
            .add(clubLabel)
            .left();

        header
            .add(titleArea)
            .left()
            .padRight(28f);

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
            .padRight(8f);

        final SelectBox<Formation> formationBox =
            
            ScreenUI.createSelectBox(
                game.skin
            );

        formationBox.setItems(
            Formation.values()
        );

        if (
            club.getFormation() !=
                null
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

                    Formation selected =
                        formationBox
                            .getSelected();

                    if (
                        selected != null &&
                            selected !=
                                club.getFormation()
                    ) {

                        club.setFormation(
                            selected
                        );

                        selectedPlayer =
                            null;

                        refreshUI();
                    }
                }
            }
        );
        
        

        header
            .add(formationBox)
            .width(210f)
            .height(50f)
            .padRight(12f);

        header
            .add()
            .expandX();

        // =====================================================
        // AUTO
        // =====================================================

        ImageTextButton autoButton =
            IconTextButton.create(
                "SELECIONAR 11",
                game.skin,
                "Icons8/icons8-ativa-modo-rápido-50.png"
            );

        autoButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        autoButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        club.getFormation() ==
                            null
                    ) {

                        club.setFormation(
                            formationBox
                                .getSelected()
                        );
                    }

                    club.autoSelectXI();

                    selectedPlayer =
                        null;

                    refreshUI();
                }
            }
        );

        header
            .add(autoButton)
            .width(195f)
            .height(42f)
            .padRight(8f);

        // =====================================================
        // CLEAR
        // =====================================================

        ImageTextButton clearButton =
            IconTextButton.create(
                "LIMPAR",
                game.skin,
                "Icons8/icons8-remover-50.png"
            );

        clearButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        clearButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    club
                        .getTacticsMap()
                        .clear();

                    club
                        .getStartingXI()
                        .clear();

                    selectedPlayer =
                        null;

                    refreshUI();
                }
            }
        );

        header
            .add(clearButton)
            .width(135f)
            .height(42f);

        return header;
    }

    // =========================================================
    // NO FORMATION
    // =========================================================

    private Table createNoFormationPanel() {

        Table panel =
            ScreenUI.createPanel();

        Label title =
            new Label(
                "NENHUMA FORMAÇÃO SELECIONADA",
                game.skin,
                "font-bold"
            );

        title.setColor(
            ScreenUI.WARNING
        );

        panel
            .add(title)
            .center()
            .row();

        Label desc =
            new Label(
                "Escolha uma formação no topo para montar o time.",
                game.skin
            );

        desc.setColor(
            ScreenUI.MUTED_TEXT
        );

        desc.setFontScale(
            0.62f
        );

        panel
            .add(desc)
            .center()
            .padTop(8f)
            .padBottom(16f)
            .row();

        TextButton button =
            ScreenUI.createPrimaryButton(
                game.skin,
                "USAR FORMAÇÃO PADRÃO"
            );

        button.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    Formation[] formations =
                        Formation.values();

                    if (
                        formations.length >
                            0
                    ) {

                        club.setFormation(
                            formations[0]
                        );

                        club.autoSelectXI();

                        refreshUI();
                    }
                }
            }
        );

        panel
            .add(button)
            .width(270f)
            .height(44f);

        return panel;
    }

    // =========================================================
    // PITCH
    // =========================================================

    private Table createPitchPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "CAMPO TÁTICO • " +
                    club.getFormation()
                        .getName()
            );

        panel
            .add(title)
            .center()
            .padBottom(8f)
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

        pitch.pad(8f);

        Formation formation =
            club.getFormation();

        if (
            formation == null ||
                formation.getPositionSlots() ==
                    null
        ) {

            return pitch;
        }

        List<String> slots =
            formation.getPositionSlots();

        Table attackers =
            line();

        Table wings =
            line();

        Table attackingMid =
            line();

        Table midfield =
            line();

        Table defensiveMid =
            line();

        Table wingbacks =
            line();

        Table defense =
            line();

        Table goalkeeper =
            line();

        for (
            int i = 0;
            i <
                Math.min(
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
                    attackers
                        .add(card)
                        .pad(2f, 7f, 2f, 7f);
                    break;

                case 6:
                    wings
                        .add(card)
                        .expandX()
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 5:
                    attackingMid
                        .add(card)
                        .pad(2f, 7f, 2f, 7f);
                    break;

                case 4:
                    midfield
                        .add(card)
                        .expandX()
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 3:
                    defensiveMid
                        .add(card)
                        .pad(2f, 7f, 2f, 7f);
                    break;

                case 2:
                    wingbacks
                        .add(card)
                        .expandX()
                        .pad(2f, 5f, 2f, 5f);
                    break;

                case 1:
                    defense
                        .add(card)
                        .expandX()
                        .pad(2f, 5f, 2f, 5f);
                    break;

                default:
                    goalkeeper
                        .add(card)
                        .pad(2f);
            }
        }

        pitch
            .add(attackers)
            .expand()
            .fillX()
            .row();

        pitch
            .add(wings)
            .expand()
            .fillX()
            .row();

        pitch
            .add(attackingMid)
            .expand()
            .fillX()
            .row();

        pitch
            .add(midfield)
            .expand()
            .fillX()
            .row();

        pitch
            .add(defensiveMid)
            .expand()
            .fillX()
            .row();

        pitch
            .add(wingbacks)
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
        final int slotIndex
    ) {

        Table outer =
            new Table();

        outer.setTransform(
            true
        );

        outer.setOrigin(
            Align.center
        );

        boolean selected =
            player != null &&
                player ==
                    selectedPlayer;

        Table card =
            new Table();

        card.background(
            StyleFactory.createRoundedPanel(
                selected
                    ? new Color(
                    0.23f,
                    0.17f,
                    0.035f,
                    0.98f
                )
                    : ScreenUI.PANEL,
                selected
                    ? StyleFactory.PLAYOFF_GOLD
                    : StyleFactory.DARK_GOLD
            )
        );

        card.pad(
            5f
        );

        if (
            player != null
        ) {

            int effective =
                player.getEffectiveOverallForPosition(
                    targetPosition
                );

            boolean outOfPosition =
                !player
                    .getPosition()
                    .equalsIgnoreCase(
                        targetPosition
                    );

            Label name =
                new Label(
                    ScreenUI.shorten(
                        player.getName(),
                        13
                    ),
                    game.skin,
                    "font-bold"
                );

            name.setFontScale(
                0.54f
            );

            name.setColor(
                outOfPosition
                    ? ScreenUI.WARNING
                    : Color.WHITE
            );

            name.setAlignment(
                Align.center
            );

            card
                .add(name)
                .width(105f)
                .center()
                .row();

            Table positionLine =
                new Table();

            Table badge =
                ScreenUI.createBadge(
                    game.skin,
                    targetPosition,
                    StyleFactory.getPositionColor(
                        targetPosition
                    )
                );

            positionLine
                .add(badge)
                .height(24f)
                .padRight(5f);

            Label overall =
                new Label(
                    String.valueOf(
                        effective
                    ),
                    game.skin,
                    "font-bold"
                );

            overall.setFontScale(
                0.62f
            );

            overall.setColor(
                StyleFactory.SOFT_YELLOW
            );

            positionLine.add(
                overall
            );

            card
                .add(positionLine)
                .center()
                .padTop(3f)
                .padBottom(4f)
                .row();

            card
                .add(
                    createFatigueBar(
                        player.getFatigue()
                    )
                )
                .width(86f)
                .height(5f)
                .center();

        } else {

            Label position =
                new Label(
                    targetPosition,
                    game.skin,
                    "font-bold"
                );

            position.setColor(
                StyleFactory.GOLD
            );

            position.setAlignment(
                Align.center
            );

            card
                .add(position)
                .center()
                .row();

            Label empty =
                new Label(
                    "VAGA",
                    game.skin
                );

            empty.setFontScale(
                0.52f
            );

            empty.setColor(
                Color.GRAY
            );

            card
                .add(empty)
                .center()
                .padTop(4f);
        }

        outer
            .add(card)
            .width(122f)
            .height(64f);

        if (
            selected
        ) {

            outer.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(
                            1.04f,
                            1.04f,
                            0.30f
                        ),
                        Actions.scaleTo(
                            0.98f,
                            0.98f,
                            0.30f
                        )
                    )
                )
            );
        }

        outer.addListener(
            new InputListener() {

                @Override
                public void enter(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor fromActor
                ) {

                    if (
                        !selected
                    ) {

                        outer.addAction(
                            Actions.scaleTo(
                                1.04f,
                                1.04f,
                                0.08f
                            )
                        );
                    }
                }

                @Override
                public void exit(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor toActor
                ) {

                    if (
                        !selected
                    ) {

                        outer.addAction(
                            Actions.scaleTo(
                                1f,
                                1f,
                                0.08f
                            )
                        );
                    }
                }
            }
        );

        outer.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        selectedPlayer !=
                            null
                    ) {

                        if (
                            selectedPlayer
                                .canPlay()
                        ) {

                            club.assignPlayerToSlot(
                                slotIndex,
                                selectedPlayer
                            );

                            selectedPlayer =
                                null;
                        }

                    } else if (
                        player != null
                    ) {

                        selectedPlayer =
                            player;
                    }

                    refreshUI();
                }
            }
        );

        return outer;
    }

    // =========================================================
    // SELECTED PLAYER
    // =========================================================

    private Table createSelectedPlayerPanel() {

        Table panel =
            ScreenUI.createPanel();

        if (
            selectedPlayer ==
                null
        ) {

            Label hint =
                new Label(
                    "Selecione um jogador no plantel e depois clique em uma posição do campo.",
                    game.skin
                );

            hint.setFontScale(
                0.62f
            );

            hint.setColor(
                ScreenUI.MUTED_TEXT
            );

            panel
                .add(hint)
                .center();

            return panel;
        }

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                selectedPlayer
                    .getPosition(),
                StyleFactory.getPositionColor(
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

        name.setColor(
            StyleFactory.GOLD
        );

        name.setFontScale(
            0.72f
        );

        info
            .add(name)
            .left()
            .row();

        Label details =
            new Label(
                "OVR " +
                    selectedPlayer.getOverall() +
                    "  •  " +
                    "Fadiga " +
                    selectedPlayer.getFatigue() +
                    "%  •  " +
                    "Moral " +
                    selectedPlayer.getMorale(),
                game.skin
            );

        details.setFontScale(
            0.56f
        );

        details.setColor(
            selectedPlayer.canPlay()
                ? StyleFactory.CREME_AGED
                : ScreenUI.DANGER
        );

        info
            .add(details)
            .left()
            .padTop(4f);

        panel
            .add(info)
            .left()
            .expandX();

        TextButton deselect =
            ScreenUI.createSecondaryButton(
                game.skin,
                "CANCELAR"
            );

        deselect.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    selectedPlayer =
                        null;

                    refreshUI();
                }
            }
        );

        panel
            .add(deselect)
            .width(115f)
            .height(36f);

        return panel;
    }

    // =========================================================
    // RIGHT PANEL
    // =========================================================

    private Table createRightPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        // =====================================================
        // TABS
        // =====================================================

        Table tabs =
            new Table();

        TextButton squadButton =
            ScreenUI.createInteractiveButton(
                "PLANTEL",
                game.skin
            );

        TextButton tacticsButton =
            ScreenUI.createInteractiveButton(
                "INSTRUÇÕES",
                game.skin
            );

        squadButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        tacticsButton
            .getLabel()
            .setFontScale(
                0.58f
            );

        squadButton.setColor(
            !showTacticsTab
                ? StyleFactory.GOLD
                : StyleFactory.METAL_DARK
        );

        tacticsButton.setColor(
            showTacticsTab
                ? StyleFactory.GOLD
                : StyleFactory.METAL_DARK
        );

        squadButton
            .getLabel()
            .setColor(
                !showTacticsTab
                    ? Color.BLACK
                    : Color.WHITE
            );

        tacticsButton
            .getLabel()
            .setColor(
                showTacticsTab
                    ? Color.BLACK
                    : Color.WHITE
            );

        squadButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    showTacticsTab =
                        false;

                    refreshUI();
                }
            }
        );

        tacticsButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    showTacticsTab =
                        true;

                    refreshUI();
                }
            }
        );

        tabs
            .add(squadButton)
            .growX()
            .height(38f)
            .padRight(5f);

        tabs
            .add(tacticsButton)
            .growX()
            .height(38f);

        panel
            .add(tabs)
            .growX()
            .padBottom(10f)
            .row();

        if (
            showTacticsTab
        ) {

            panel
                .add(
                    createTacticsControls()
                )
                .grow();

        } else {

            panel
                .add(
                    createSquadList()
                )
                .grow();
        }

        return panel;
    }

    // =========================================================
    // SQUAD LIST
    // =========================================================

    private Table createSquadList() {

        Table root =
            new Table();

        root.top();

        List<Player> starters =
            new ArrayList<>(
                club.getTacticsMap()
                    .values()
            );

        List<Player> reserves =
            new ArrayList<>();

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

                reserves.add(
                    player
                );
            }
        }

        reserves.sort(
            (
                a,
                b
            ) ->
                Integer.compare(
                    b.getOverall(),
                    a.getOverall()
                )
        );

        Table list =
            new Table();

        list.top();

        list
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "BANCO DE RESERVAS"
                )
            )
            .left()
            .growX()
            .padBottom(7f)
            .row();

        int bench =
            Math.min(
                7,
                reserves.size()
            );

        for (
            int i = 0;
            i < bench;
            i++
        ) {

            list
                .add(
                    createSquadRow(
                        reserves.get(i),
                        true,
                        i
                    )
                )
                .growX()
                .height(46f)
                .padBottom(3f)
                .row();
        }

        list
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "NÃO RELACIONADOS"
                )
            )
            .left()
            .padTop(12f)
            .padBottom(6f)
            .row();

        for (
            int i = bench;
            i <
                reserves.size();
            i++
        ) {

            list
                .add(
                    createSquadRow(
                        reserves.get(i),
                        false,
                        i
                    )
                )
                .growX()
                .height(44f)
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

        root
            .add(scroll)
            .grow();

        return root;
    }

    private Table createSquadRow(
        Player player,
        boolean bench,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        boolean selected =
            player ==
                selectedPlayer;

        if (
            selected
        ) {

            row.background(
                StyleFactory.createRoundedPanel(
                    new Color(
                        0.24f,
                        0.17f,
                        0.035f,
                        0.96f
                    ),
                    StyleFactory.GOLD
                )
            );
        }

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                player.getPosition(),
                StyleFactory.getPositionColor(
                    player.getPosition()
                )
            );

        row
            .add(badge)
            .width(52f)
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
            .padLeft(8f);

        Label ovr =
            ScreenUI.createBoldValue(
                game.skin,
                String.valueOf(
                    player.getOverall()
                ),
                StyleFactory.SOFT_YELLOW,
                Align.center
            );

        row
            .add(ovr)
            .width(42f)
            .padRight(6f);

        row.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (!player.canPlay()) {
                        return;
                    }

                    selectedPlayer =
                        selectedPlayer ==
                            player
                            ? null
                            : player;

                    refreshUI();
                }
            }
        );

        return row;
    }

    // =========================================================
    // TACTIC CONTROLS
    // =========================================================

    private Table createTacticsControls() {

        Table root =
            new Table();

        Table cards =
            new Table();

        cards.top();

        Slider.SliderStyle sliderStyle =
            createSliderStyle();

        // =====================================================
        // RITMO
        // =====================================================

        Slider tempo =
            new Slider(
                0f,
                100f,
                5f,
                false,
                sliderStyle
            );

        tempo.setValue(
            club.getTempo()
        );

        Label tempoValue =
            valueLabel(
                Math.round(
                    club.getTempo()
                ) +
                    "%"
            );

        tempo.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    club.setTempo(
                        tempo.getValue()
                    );

                    tempoValue.setText(
                        Math.round(
                            tempo.getValue()
                        ) +
                            "%"
                    );
                }
            }
        );

        cards
            .add(
                tacticCard(
                    "RITMO",
                    "Lento",
                    "Rápido",
                    tempo,
                    tempoValue
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        // =====================================================
        // MENTALIDADE
        // =====================================================

        Slider mentality =
            new Slider(
                10f,
                90f,
                20f,
                false,
                sliderStyle
            );

        mentality.setValue(
            club.getMentalityValue()
        );

        Label mentalityValue =
            valueLabel(
                club.getMentality()
            );

        mentality.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    float value =
                        mentality.getValue();

                    if (
                        value <=
                            20
                    ) {

                        club.setMentality(
                            "Defensiva"
                        );

                    } else if (
                        value <=
                            40
                    ) {

                        club.setMentality(
                            "Equilibrada"
                        );

                    } else if (
                        value <=
                            60
                    ) {

                        club.setMentality(
                            "Ofensiva"
                        );

                    } else {

                        club.setMentality(
                            "Ultra Ofensiva"
                        );
                    }

                    mentalityValue.setText(
                        club.getMentality()
                    );
                }
            }
        );

        cards
            .add(
                tacticCard(
                    "MENTALIDADE",
                    "Defensiva",
                    "Ofensiva",
                    mentality,
                    mentalityValue
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        // =====================================================
        // PASSE
        // =====================================================

        Slider passing =
            new Slider(
                0f,
                100f,
                5f,
                false,
                sliderStyle
            );

        passing.setValue(
            club.getPassing()
        );

        Label passingValue =
            valueLabel(
                Math.round(
                    club.getPassing()
                ) +
                    "%"
            );

        passing.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    club.setPassing(
                        passing.getValue()
                    );

                    passingValue.setText(
                        Math.round(
                            passing.getValue()
                        ) +
                            "%"
                    );
                }
            }
        );

        cards
            .add(
                tacticCard(
                    "ESTILO DE PASSE",
                    "Curto",
                    "Longo",
                    passing,
                    passingValue
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        // =====================================================
        // WIDTH
        // =====================================================

        Slider width =
            new Slider(
                0f,
                100f,
                5f,
                false,
                sliderStyle
            );

        width.setValue(
            club.getWidth()
        );

        Label widthValue =
            valueLabel(
                Math.round(
                    club.getWidth()
                ) +
                    "%"
            );

        width.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    club.setWidth(
                        width.getValue()
                    );

                    widthValue.setText(
                        Math.round(
                            width.getValue()
                        ) +
                            "%"
                    );
                }
            }
        );

        cards
            .add(
                tacticCard(
                    "AMPLITUDE",
                    "Estreita",
                    "Aberta",
                    width,
                    widthValue
                )
            )
            .growX()
            .padBottom(8f)
            .row();

        // =====================================================
        // PRESS
        // =====================================================

        Slider pressure =
            new Slider(
                0f,
                100f,
                5f,
                false,
                sliderStyle
            );

        pressure.setValue(
            club.getPressure()
        );

        Label pressureValue =
            valueLabel(
                Math.round(
                    club.getPressure()
                ) +
                    "%"
            );

        pressure.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {

                    club.setPressure(
                        pressure.getValue()
                    );

                    pressureValue.setText(
                        Math.round(
                            pressure.getValue()
                        ) +
                            "%"
                    );
                }
            }
        );

        cards
            .add(
                tacticCard(
                    "PRESSÃO",
                    "Baixa",
                    "Alta",
                    pressure,
                    pressureValue
                )
            )
            .growX()
            .row();

        ScrollPane scroll =
            new ScrollPane(
                cards,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        root
            .add(scroll)
            .grow();

        return root;
    }

    private Table tacticCard(
        String title,
        String left,
        String right,
        Slider slider,
        Label value
    ) {

        Table card =
            ScreenUI.createSubtlePanel();

        Table titleRow =
            new Table();

        titleRow
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    title
                )
            )
            .left()
            .expandX();

        titleRow
            .add(value)
            .right();

        card
            .add(titleRow)
            .growX()
            .colspan(2)
            .padBottom(8f)
            .row();

        Label leftLabel =
            ScreenUI.createSubtitle(
                game.skin,
                left
            );

        Label rightLabel =
            ScreenUI.createSubtitle(
                game.skin,
                right
            );

        card
            .add(leftLabel)
            .left();

        card
            .add(rightLabel)
            .right()
            .row();

        card
            .add(slider)
            .growX()
            .colspan(2)
            .height(24f)
            .padTop(4f);

        return card;
    }

    private Label valueLabel(
        String value
    ) {

        return ScreenUI.createBoldValue(
            game.skin,
            value,
            StyleFactory.SOFT_YELLOW,
            Align.right
        );
    }

    // =========================================================
    // AUTO LINEUP
    // =========================================================
    //
    // A lógica de seleção automática agora fica centralizada
    // em Club.autoSelectXI(). Dessa forma, TacticsScreen,
    // outras telas e a IA usam exatamente a mesma regra.
    //
    // =========================================================

    // =========================================================
    // PITCH TEXTURE
    // =========================================================

    private void ensurePitchTexture() {

        if (
            pitchTexture != null
        ) {

            return;
        }

        int width =
            600;

        int height =
            750;

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
                0.42f
            )
        );

        pixmap.drawRectangle(
            15,
            15,
            width - 30,
            height - 30
        );

        int middle =
            height /
                2;

        pixmap.drawLine(
            15,
            middle,
            width - 15,
            middle
        );

        pixmap.drawCircle(
            width /
                2,
            middle,
            65
        );

        pixmap.drawRectangle(
            width /
                2 -
                140,
            15,
            280,
            110
        );

        pixmap.drawRectangle(
            width /
                2 -
                60,
            15,
            120,
            45
        );

        pixmap.drawRectangle(
            width /
                2 -
                140,
            height -
                125,
            280,
            110
        );

        pixmap.drawRectangle(
            width /
                2 -
                60,
            height -
                60,
            120,
            45
        );

        pitchTexture =
            new Texture(
                pixmap
            );

        pixmap.dispose();
    }

    // =========================================================
    // SLIDER
    // =========================================================

    private void ensureSliderTextures() {

        if (
            sliderBackgroundTexture ==
                null
        ) {

            Pixmap background =
                new Pixmap(
                    100,
                    5,
                    Pixmap.Format.RGBA8888
                );

            background.setColor(
                Color.valueOf(
                    "4B504C"
                )
            );

            background.fill();

            sliderBackgroundTexture =
                new Texture(
                    background
                );

            background.dispose();
        }

        if (
            sliderKnobTexture ==
                null
        ) {

            Pixmap knob =
                new Pixmap(
                    16,
                    16,
                    Pixmap.Format.RGBA8888
                );

            knob.setColor(
                StyleFactory.GOLD
            );

            knob.fillCircle(
                8,
                8,
                8
            );

            sliderKnobTexture =
                new Texture(
                    knob
                );

            knob.dispose();
        }
    }

    private Slider.SliderStyle createSliderStyle() {

        return new Slider.SliderStyle(
            new TextureRegionDrawable(
                new TextureRegion(
                    sliderBackgroundTexture
                )
            ),
            new TextureRegionDrawable(
                new TextureRegion(
                    sliderKnobTexture
                )
            )
        );
    }

    // =========================================================
    // FATIGUE
    // =========================================================

    private Table createFatigueBar(
        float fatigue
    ) {

        Table container =
            new Table();

        container.background(
            StyleFactory.createSolid(
                Color.valueOf(
                    "303532"
                )
            )
        );

        Color color;

        if (
            fatigue >=
                70f
        ) {

            color =
                ScreenUI.SUCCESS;

        } else if (
            fatigue >=
                40f
        ) {

            color =
                ScreenUI.WARNING;

        } else {

            color =
                ScreenUI.DANGER;
        }

        Table fill =
            new Table();

        fill.background(
            StyleFactory.createSolid(
                color
            )
        );

        float normalized =
            Math.max(
                0f,
                Math.min(
                    100f,
                    fatigue
                )
            ) /
                100f;

        container
            .add(fill)
            .width(
                86f *
                    normalized
            )
            .height(5f)
            .left()
            .expandX();

        return container;
    }

    // =========================================================
    // POSITION DEPTH
    // =========================================================

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

    private Table line() {

        Table row =
            new Table();

        row.center();

        return row;
    }

    // =========================================================
    // SCREEN
    // =========================================================

    @Override
    public void render(
        float delta
    ) {

        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        stage.act(
            delta
        );

        stage.draw();
    }

    @Override
    public void resize(
        int width,
        int height
    ) {

        stage
            .getViewport()
            .update(
                width,
                height,
                true
            );
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {

        stage.dispose();

        if (
            pitchTexture != null
        ) {

            pitchTexture.dispose();
        }

        if (
            sliderBackgroundTexture !=
                null
        ) {

            sliderBackgroundTexture.dispose();
        }

        if (
            sliderKnobTexture !=
                null
        ) {

            sliderKnobTexture.dispose();
        }
    }
}
