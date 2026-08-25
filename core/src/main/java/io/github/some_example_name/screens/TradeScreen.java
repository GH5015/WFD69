package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.InterestBarWidget;
import io.github.some_example_name.utils.PlayerDetailsDialog;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.TradeValueBar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TradeScreen implements Screen {

    private static final int MAX_TRADE_ASSETS = TradeOffer.MAX_ASSETS_PER_SIDE;

    private final Main game;
    private final Club userClub;

    private Club targetClub;

    private final Stage stage;
    private final Texture backgroundTexture;

    private TradeOffer currentOffer;

    // =========================================================
    // CONSTRUTORES
    // =========================================================

    public TradeScreen(
        Main game,
        Club userClub
    ) {

        this(
            game,
            userClub,
            findDefaultPartner(
                game,
                userClub
            )
        );
    }

    public TradeScreen(
        Main game,
        Club userClub,
        Club targetClub
    ) {

        this.game =
            game;

        this.userClub =
            userClub;

        this.targetClub =
            targetClub != null
                ? targetClub
                : findDefaultPartner(
                game,
                userClub
            );

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        this.backgroundTexture =
            new Texture(
                Gdx.files.internal(
                    "prancheta.png"
                )
            );

        if (
            this.targetClub != null
        ) {

            this.currentOffer =
                new TradeOffer(
                    userClub,
                    this.targetClub
                );
        }
    }

    // =========================================================
    // SHOW
    // =========================================================

    @Override
    public void show() {

        Gdx.input.setInputProcessor(
            stage
        );

        refreshUI();
    }

    // =========================================================
    // INTERFACE
    // =========================================================

    private void refreshUI() {

        DraftOrderService.getCurrentDraftOrder(
            game.league,
            game.league.getCurrentSeason() + 1
        );

        stage.clear();

        Stack root =
            new Stack();

        root.setFillParent(
            true
        );

        stage.addActor(
            root
        );

        Image background =
            new Image(
                new TextureRegionDrawable(
                    backgroundTexture
                )
            );

        background.setFillParent(
            true
        );

        root.add(
            background
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
                ScreenUI.createHeader(
                    game.skin,
                    "CENTRAL DE TROCAS",
                    "TEMPORADA " +
                        game.league
                            .getCurrentSeason() +
                        " • " +
                        SeasonCalendar.getTradeStatus(
                            game.league,
                            userClub
                        )
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        if (
            targetClub == null ||
                currentOffer == null
        ) {

            page
                .add(
                    createNoPartnerPanel()
                )
                .grow()
                .row();

            root.add(
                page
            );

            if (!"OFFSEASON".equals(game.league.getCurrentStage())) NavigationDrawer.attach(stage, game, userClub, "TROCAS", true);


            return;
        }

        // =====================================================
        // INTERESSE
        // =====================================================

        page
            .add(
                createNegotiationHeader()
            )
            .growX()
            .height(84f)
            .padBottom(10f)
            .row();

        // =====================================================
        // CLUBES
        // =====================================================

        Table tradeColumns =
            new Table();

        tradeColumns
            .add(
                createClubPanel(
                    userClub,
                    true
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        tradeColumns
            .add(
                createClubPanel(
                    targetClub,
                    false
                )
            )
            .grow()
            .uniformX();

        page
            .add(tradeColumns)
            .grow()
            .padBottom(10f)
            .row();

        // =====================================================
        // RESUMO DA PROPOSTA
        // =====================================================

        page
            .add(
                createOfferSummaryPanel()
            )
            .growX()
            .height(174f)
            .row();

        root.add(
            page
        );

        if (!"OFFSEASON".equals(game.league.getCurrentStage())) NavigationDrawer.attach(stage, game, userClub, "TROCAS", true);
        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
            Table returnOverlay = new Table(); returnOverlay.setFillParent(true); returnOverlay.bottom().left().pad(18f);
            TextButton back = ScreenUI.createInteractiveButton("← MERCADO DE TROCAS", game.skin);
            back.getLabel().setFontScale(.45f);
            back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ game.setScreen(new TradeHubScreen(game, userClub)); }});
            returnOverlay.add(back).width(220f).height(42f); root.add(returnOverlay);
        }

    }

    // =========================================================
    // SEM PARCEIRO
    // =========================================================

    private Table createNoPartnerPanel() {

        Table panel =
            ScreenUI.createPanel();

        Label title =
            new Label(
                "NENHUM CLUBE DISPONÍVEL",
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

        Label text =
            ScreenUI.createSubtitle(
                game.skin,
                "Não foi possível encontrar um parceiro para negociação."
            );

        panel
            .add(text)
            .center()
            .padTop(8f);

        return panel;
    }

    // =========================================================
    // CABEÇALHO DE NEGOCIAÇÃO
    // =========================================================

    private Table createNegotiationHeader() {

        Table panel =
            ScreenUI.createPanel();

        int interest =
            SmartTradeEvaluator
                .calculateInterestScore(
                    currentOffer,
                    game.league.getCurrentSeason()
                );

        // =====================================================
        // TEXTO
        // =====================================================

        Table info =
            new Table();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "INTERESSE NA NEGOCIAÇÃO"
            );

        info
            .add(title)
            .left()
            .row();

        String status;

        Color statusColor;

        if (
            interest >= 80
        ) {

            status =
                "MUITO INTERESSADO";

            statusColor =
                ScreenUI.SUCCESS;

        } else if (
            interest >= 60
        ) {

            status =
                "INTERESSADO";

            statusColor =
                StyleFactory.SOFT_YELLOW;

        } else if (
            interest >= 40
        ) {

            status =
                "AVALIANDO";

            statusColor =
                ScreenUI.WARNING;

        } else {

            status =
                "POUCO INTERESSE";

            statusColor =
                ScreenUI.DANGER;
        }

        Label statusLabel =
            new Label(
                targetClub.getName() +
                    " • " +
                    status,
                game.skin,
                "font-bold"
            );

        statusLabel.setFontScale(
            0.60f
        );

        statusLabel.setColor(
            statusColor
        );

        info
            .add(statusLabel)
            .left()
            .padTop(4f);

        panel
            .add(info)
            .left()
            .expandX();

        // =====================================================
        // BARRA JÁ EXISTENTE
        // =====================================================

        Table interestWidget =
            InterestBarWidget.createWidget(
                targetClub,
                interest,
                game.skin
            );

        panel
            .add(interestWidget)
            .width(430f)
            .right();

        return panel;
    }

    // =========================================================
    // PAINEL DE CADA CLUBE
    // =========================================================

    private Table createClubPanel(
        Club club,
        boolean isUser
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        ClubNeedEvaluator.TeamPhase phase =
            ClubNeedEvaluator
                .getTeamPhase(
                    club
                );

        int outgoing =
            isUser
                ? currentOffer
                .getUserPlayers()
                .size()
                : currentOffer
                .getTargetPlayers()
                .size();

        int incoming =
            isUser
                ? currentOffer
                .getTargetPlayers()
                .size()
                : currentOffer
                .getUserPlayers()
                .size();

        int projectedRoster =
            club.getSquad()
                .size()
                -
                outgoing
                +
                incoming;

        // =====================================================
        // HEADER DO CLUBE
        // =====================================================

        Table header =
            new Table();

        if (
            isUser
        ) {

            Table identity =
                new Table();

            Label yourClub =
                new Label(
                    "SEU CLUBE",
                    game.skin,
                    "font-bold"
                );

            yourClub.setFontScale(
                0.48f
            );

            yourClub.setColor(
                ScreenUI.MUTED_TEXT
            );

            identity
                .add(yourClub)
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
                0.70f
            );

            clubName.setColor(
                StyleFactory.GOLD
            );

            identity
                .add(clubName)
                .left();

            header
                .add(identity)
                .left()
                .expandX();

        } else {

            TextButton partnerSelector =
                ScreenUI.createInteractiveButton(
                    "NEGOCIAR COM: " +
                        ScreenUI.shorten(
                            targetClub.getName(),
                            22
                        ) +
                        "  ▾",
                    game.skin
                );

            partnerSelector
                .getLabel()
                .setFontScale(
                    0.54f
                );

            partnerSelector.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        showPartnerPicker();
                    }
                }
            );

            header
                .add(partnerSelector)
                .width(340f)
                .height(50f)
                .left()
                .expandX();
        }

        // =====================================================
        // FASE
        // =====================================================

        Color phaseColor =
            getPhaseColor(
                phase
            );

        Table phaseBadge =
            ScreenUI.createBadge(
                game.skin,
                phase.name(),
                phaseColor
            );

        header
            .add(phaseBadge)
            .height(28f)
            .right();

        panel
            .add(header)
            .growX()
            .padBottom(6f)
            .row();

        // =====================================================
        // INFO
        // =====================================================

        Table info =
            new Table();

        info
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "ELENCO",
                    projectedRoster +
                        "/26",
                    projectedRoster >= 23 &&
                        projectedRoster <= 26
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .uniformX()
            .padRight(6f);

        info
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "OVR",
                    String.valueOf(
                        (int) Math.round(
                            club.getOverall()
                        )
                    ),
                    StyleFactory.SOFT_YELLOW
                )
            )
            .growX()
            .uniformX();

        panel
            .add(info)
            .growX()
            .height(43f)
            .padBottom(9f)
            .row();

        // =====================================================
        // ASSETS
        // =====================================================

        panel
            .add(
                createTradeAssetSlots(
                    club,
                    isUser
                )
            )
            .grow();

        return panel;
    }

    private void showPartnerPicker() {

        Dialog dialog =
            new Dialog(
                "",
                game.skin
            );

        dialog.setModal(
            true
        );

        dialog.setMovable(
            false
        );

        Table content =
            dialog.getContentTable();

        content.background(
            StyleFactory.createMetallicBoard(
                660,
                610,
                Color.valueOf(
                    "141A16"
                )
            )
        );

        content.pad(
            18f
        );

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "ESCOLHER CLUBE PARA NEGOCIAR"
            );

        title.setAlignment(
            Align.center
        );

        content
            .add(title)
            .width(590f)
            .padBottom(6f)
            .row();

        Label hint =
            ScreenUI.createSubtitle(
                game.skin,
                "Ao trocar de parceiro, a proposta atual será reiniciada."
            );

        hint.setAlignment(
            Align.center
        );

        content
            .add(hint)
            .width(590f)
            .padBottom(12f)
            .row();

        Table list =
            new Table();

        list.top();

        int index =
            0;

        for (
            Club candidate :
            game.league.getClubs()
        ) {

            if (
                candidate == userClub
            ) {

                continue;
            }

            Table row =
                ScreenUI.createRow(
                    index++
                );

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        candidate.getName(),
                        candidate == targetClub
                            ? StyleFactory.GOLD
                            : Color.WHITE,
                        Align.left
                    )
                )
                .left()
                .expandX()
                .padLeft(12f);

            row
                .add(
                    ScreenUI.createSubtitle(
                        game.skin,
                        "OVR " +
                            Math.round(
                                candidate.getOverall()
                            )
                    )
                )
                .width(75f)
                .center();

            row
                .add(
                    ScreenUI.createBadge(
                        game.skin,
                        ClubNeedEvaluator
                            .getTeamPhase(
                                candidate
                            )
                            .name(),
                        getPhaseColor(
                            ClubNeedEvaluator
                                .getTeamPhase(
                                    candidate
                                )
                        )
                    )
                )
                .width(118f)
                .height(26f)
                .padRight(7f);

            row.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        targetClub =
                            candidate;

                        currentOffer =
                            new TradeOffer(
                                userClub,
                                targetClub
                            );

                        dialog.hide();

                        refreshUI();
                    }
                }
            );

            list
                .add(row)
                .growX()
                .height(48f)
                .padBottom(4f)
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

        content
            .add(scroll)
            .width(590f)
            .height(410f)
            .row();

        TextButton close =
            ScreenUI.createInteractiveButton(
                "CANCELAR",
                game.skin
            );

        close
            .getLabel()
            .setFontScale(
                0.56f
            );

        dialog.button(
            close,
            true
        );

        dialog.show(
            stage
        );
    }

    // =========================================================
    // LISTA DE ATIVOS
    // =========================================================

    private Table createTradeAssetSlots(
        Club club,
        boolean isUser
    ) {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel.top();

        List<Player> selectedPlayers =
            isUser
                ? currentOffer.getUserPlayers()
                : currentOffer.getTargetPlayers();

        List<DraftPick> selectedPicks =
            isUser
                ? currentOffer.getUserPicks()
                : currentOffer.getTargetPicks();

        int assetCount =
            selectedPlayers.size() +
                selectedPicks.size();

        Table heading =
            new Table();

        heading
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    isUser
                        ? "VOCÊ OFERECE"
                        : "VOCÊ RECEBE"
                )
            )
            .left()
            .expandX();

        heading
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    assetCount +
                        "/" +
                        MAX_TRADE_ASSETS +
                        " ATIVOS",
                    assetCount > 0
                        ? Color.valueOf(
                            "285B45"
                        )
                        : Color.valueOf(
                            "3A4140"
                        )
                )
            )
            .height(26f)
            .right();

        panel
            .add(heading)
            .growX()
            .padBottom(9f)
            .row();

        for (
            Player player :
            selectedPlayers
        ) {

            panel
                .add(
                    createSelectedPlayerSlot(
                        player,
                        isUser
                    )
                )
                .growX()
                .height(66f)
                .padBottom(5f)
                .row();
        }

        for (
            DraftPick pick :
            selectedPicks
        ) {

            panel
                .add(
                    createSelectedPickSlot(
                        pick,
                        isUser
                    )
                )
                .growX()
                .height(66f)
                .padBottom(5f)
                .row();
        }

        for (
            int slot = assetCount;
            slot < MAX_TRADE_ASSETS;
            slot++
        ) {

            panel
                .add(
                    createEmptyAssetSlot(
                        club,
                        isUser,
                        slot + 1
                    )
                )
                .growX()
                .height(66f)
                .padBottom(5f)
                .row();
        }

        Label hint =
            ScreenUI.createSubtitle(
                game.skin,
                "Clique em um ativo para removê-lo."
            );

        hint.setAlignment(
            Align.center
        );

        panel
            .add(hint)
            .growX()
            .padTop(3f);

        return panel;
    }

    private Table createSelectedPlayerSlot(
        Player player,
        boolean isUser
    ) {

        Table slot =
            new Table();

        slot.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "25220F"
                ),
                StyleFactory.GOLD
            )
        );

        slot
            .add(
                ScreenUI.createBadge(
                    game.skin,
                    player.getPosition(),
                    StyleFactory.getPositionColor(
                        player.getPosition()
                    )
                )
            )
            .width(55f)
            .height(25f)
            .padLeft(8f);

        Table info =
            new Table();

        info
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    ScreenUI.shorten(
                        player.getName(),
                        24
                    ),
                    Color.WHITE,
                    Align.left
                )
            )
            .left()
            .expandX();

        info
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "OVR " +
                        player.getOverall(),
                    StyleFactory.SOFT_YELLOW,
                    Align.right
                )
            )
            .right()
            .row();

        Club receivingClub =
            isUser
                ? targetClub
                : userClub;

        int positionNeed =
            ClubNeedEvaluator
                .calculatePositionNeeds(
                    receivingClub
                )
                .getOrDefault(
                    player.getPosition(),
                    3
                );

        long perceivedValue =
            SmartTradeEvaluator
                .getPerceivedPlayerValue(
                    receivingClub,
                    player,
                    game.league.getCurrentSeason()
                );

        info
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Sal. " +
                        formatMoney(
                            player.getAnnualSalary()
                        ) +
                        "/ano • Contr. " +
                        formatRemainingContract(
                            player
                        )
                )
            )
            .left();

        info
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "TV " +
                        perceivedValue +
                        " • Nec. " +
                        formatNeedStars(
                            positionNeed
                        )
                )
            )
            .right();

        TextButton details = ScreenUI.createInteractiveButton("i", game.skin);
        details.getLabel().setFontScale(0.5f);
        details.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                showPlayerDetails(player);
            }
        });

        slot
            .add(info)
            .growX()
            .padLeft(8f)
            .padRight(8f);

        slot.add(details).width(32f).height(30f).padRight(3f);

        Label remove =
            new Label(
                "×",
                game.skin,
                "font-title"
            );

        remove.setFontScale(
            0.75f
        );

        remove.setColor(
            ScreenUI.DANGER
        );

        slot
            .add(remove)
            .width(38f)
            .center();

        slot.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        isUser
                    ) {

                        currentOffer.removePlayerToGive(
                            player
                        );

                    } else {

                        currentOffer.removePlayerToReceive(
                            player
                        );
                    }

                    refreshUI();
                }
            }
        );

        return slot;
    }

    private Table createSelectedPickSlot(
        DraftPick pick,
        boolean isUser
    ) {

        Club receivingClub = isUser ? targetClub : userClub;

        Table slot =
            new Table();

        slot.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "1B2731"
                ),
                Color.valueOf(
                    "6E9CC5"
                )
            )
        );

        Label round =
            ScreenUI.createBoldValue(
                game.skin,
                pick.getRound() +
                    "ª",
                Color.valueOf(
                    "9DC8F0"
                ),
                Align.center
            );

        slot
            .add(round)
            .width(55f)
            .padLeft(8f);

        Table info =
            new Table();

        info
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    pick.getYear() +
                        " • DRAFT PICK",
                    Color.WHITE,
                    Align.left
                )
            )
            .left()
            .expandX()
            .row();

        info
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                        "Projeção #" +
                        pick.getProjectedPosition() +
                        "  •  TV " +
                        DraftPickEvaluator.getPerceivedPickValue(
                            receivingClub,
                            pick,
                            game.league.getCurrentSeason()
                        )
                )
            )
            .left();

        slot
            .add(info)
            .growX()
            .padLeft(8f)
            .padRight(8f);

        Label remove =
            new Label(
                "×",
                game.skin,
                "font-title"
            );

        remove.setFontScale(
            0.75f
        );

        remove.setColor(
            ScreenUI.DANGER
        );

        slot
            .add(remove)
            .width(38f)
            .center();

        slot.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        isUser
                    ) {

                        currentOffer.removePickToGive(
                            pick
                        );

                    } else {

                        currentOffer.removePickToReceive(
                            pick
                        );
                    }

                    refreshUI();
                }
            }
        );

        return slot;
    }

    private Table createEmptyAssetSlot(
        Club club,
        boolean isUser,
        int slotNumber
    ) {

        Table slot =
            new Table();

        slot.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "18201C"
                ),
                Color.valueOf(
                    "53665B"
                )
            )
        );

        Label add =
            new Label(
                "+",
                game.skin,
                "font-title"
            );

        add.setFontScale(
            0.84f
        );

        add.setColor(
            StyleFactory.GOLD
        );

        slot
            .add(add)
            .width(55f)
            .center();

        Table info =
            new Table();

        info
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "ADICIONAR ATIVO",
                    StyleFactory.CREME_AGED,
                    Align.left
                )
            )
            .left()
            .row();

        info
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Espaço " +
                        slotNumber +
                        " • jogador ou escolha de draft"
                )
            )
            .left();

        slot
            .add(info)
            .growX()
            .padLeft(8f);

        slot.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    showAssetPicker(
                        club,
                        isUser
                    );
                }
            }
        );

        return slot;
    }

    private void showAssetPicker(
        Club club,
        boolean isUser
    ) {

        Dialog dialog =
            new Dialog(
                "",
                game.skin
            );

        dialog.setModal(
            true
        );

        dialog.setMovable(
            false
        );

        Table content =
            dialog.getContentTable();

        content.background(
            StyleFactory.createMetallicBoard(
                780,
                670,
                Color.valueOf(
                    "141A16"
                )
            )
        );

        content.pad(
            18f
        );

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "SELECIONAR ATIVO • " +
                    club.getName()
                        .toUpperCase()
            );

        title.setAlignment(
            Align.center
        );

        content
            .add(title)
            .width(700f)
            .padBottom(5f)
            .row();

        Label subtitle =
            ScreenUI.createSubtitle(
                game.skin,
                "Escolha um jogador ou uma escolha de draft para esta proposta."
            );

        subtitle.setAlignment(
            Align.center
        );

        content
            .add(subtitle)
            .width(700f)
            .padBottom(12f)
            .row();

        Table list =
            new Table();

        list.top();

        List<Player> players =
            new ArrayList<>(
                club.getSquad()
            );

        players.sort(
            Comparator.comparingInt(
                Player::getOverall
            ).reversed()
        );

        int playerIndex =
            0;

        for (
            Player player :
            players
        ) {

            boolean selected =
                isUser
                    ? currentOffer.getUserPlayers()
                        .contains(player)
                    : currentOffer.getTargetPlayers()
                        .contains(player);

            if (!selected && !player.isFreeAgent(game.league.getCurrentSeason())) {

                list
                    .add(
                        createPlayerPickerRow(
                            player,
                            isUser,
                            dialog,
                            playerIndex++
                        )
                    )
                    .growX()
                    .height(52f)
                    .padBottom(4f)
                    .row();
            }
        }

        Table picksHeader =
            ScreenUI.createTableHeaderRow();

        picksHeader
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "ESCOLHAS DE DRAFT",
                    Align.left
                )
            )
            .left()
            .padLeft(10f);

        list
            .add(picksHeader)
            .growX()
            .height(34f)
            .padTop(9f)
            .padBottom(4f)
            .row();

        int pickIndex =
            0;

        for (
            DraftPick pick :
            club.getDraftPicks()
        ) {

            boolean selected =
                isUser
                    ? currentOffer.getUserPicks()
                        .contains(pick)
                    : currentOffer.getTargetPicks()
                        .contains(pick);

            if (!selected && pick.getCurrentOwner() == club) {

                list
                    .add(
                        createPickPickerRow(
                            pick,
                            isUser,
                            dialog,
                            pickIndex++
                        )
                    )
                    .growX()
                    .height(48f)
                    .padBottom(4f)
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

        content
            .add(scroll)
            .width(700f)
            .height(500f)
            .row();

        TextButton close =
            ScreenUI.createInteractiveButton(
                "CANCELAR",
                game.skin
            );

        close
            .getLabel()
            .setFontScale(
                0.56f
            );

        dialog.button(
            close,
            true
        );

        dialog.show(
            stage
        );
    }

    private Table createPlayerPickerRow(
        Player player,
        boolean isUser,
        Dialog dialog,
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
                    StyleFactory.getPositionColor(
                        player.getPosition()
                    )
                )
            )
            .width(58f)
            .height(25f)
            .padLeft(7f);

        Label playerName = ScreenUI.createBoldValue(game.skin, player.getName(), Color.WHITE, Align.left);
        playerName.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                showPlayerDetails(player);
            }
        });

        row
            .add(playerName)
            .left()
            .expandX()
            .padLeft(8f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "OVR " +
                        player.getOverall(),
                    StyleFactory.SOFT_YELLOW,
                    Align.center
                )
            )
            .width(90f);

        row
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Contr. " +
                        formatRemainingContract(
                            player
                        )
                )
            )
            .width(105f)
            .center();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "+",
                    ScreenUI.SUCCESS,
                    Align.center
                )
            )
            .width(42f);

        row.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        isUser
                    ) {

                        currentOffer.addPlayerToGive(
                            player
                        );

                    } else {

                        currentOffer.addPlayerToReceive(
                            player
                        );
                    }

                    dialog.hide();

                    refreshUI();
                }
            }
        );

        return row;
    }

    private void showPlayerDetails(Player player) {
        new PlayerDetailsDialog(
            game.skin,
            player,
            game.league.getCurrentSeason()
        ).show(stage);
    }

    private String formatRemainingContract(Player player) {
        int remaining = player.getRemainingContractYears(game.league.getCurrentSeason());
        if (remaining == 0) return "expirado";
        return remaining + " ano" + (remaining > 1 ? "s" : "");
    }

    private Table createPickPickerRow(
        DraftPick pick,
        boolean isUser,
        Dialog dialog,
        int index
    ) {

        Club receivingClub = isUser ? targetClub : userClub;

        Table row =
            ScreenUI.createRow(
                index
            );

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    pick.getYear() +
                        " • " +
                        pick.getRound() +
                        "ª RODADA",
                    Color.WHITE,
                    Align.left
                )
            )
            .left()
            .expandX()
            .padLeft(12f);

        row
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                        "Proj. #" +
                        pick.getProjectedPosition() +
                        "  •  TV " +
                        DraftPickEvaluator.getPerceivedPickValue(
                            receivingClub,
                            pick,
                            game.league.getCurrentSeason()
                        )
                )
            )
            .width(145f)
            .center();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "+",
                    ScreenUI.SUCCESS,
                    Align.center
                )
            )
            .width(42f);

        row.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        isUser
                    ) {

                        currentOffer.addPickToGive(
                            pick
                        );

                    } else {

                        currentOffer.addPickToReceive(
                            pick
                        );
                    }

                    dialog.hide();

                    refreshUI();
                }
            }
        );

        return row;
    }

    private Table createAssetsList(
        Club club,
        boolean isUser
    ) {

        Table items =
            new Table();

        items.top();

        Map<String, Integer> needs =
            ClubNeedEvaluator
                .calculatePositionNeeds(
                    club
                );

        // =====================================================
        // JOGADORES
        // =====================================================

        Table playersHeader =
            ScreenUI.createTableHeaderRow();

        playersHeader
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "JOGADORES",
                    Align.left
                )
            )
            .left()
            .expandX()
            .padLeft(10f);

        playersHeader
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    isUser
                        ? "OFERECER"
                        : "RECEBER",
                    Align.center
                )
            )
            .width(85f);

        items
            .add(playersHeader)
            .growX()
            .height(38f)
            .padBottom(4f)
            .row();

        int index =
            0;

        for (
            Player player :
            club.getSquad()
        ) {

            boolean selected =
                isUser
                    ? currentOffer
                    .getUserPlayers()
                    .contains(
                        player
                    )
                    : currentOffer
                    .getTargetPlayers()
                    .contains(
                        player
                    );

            Table row =
                ScreenUI.createRow(
                    index++
                );

            if (
                selected
            ) {

                row.background(
                    StyleFactory.createRoundedPanel(
                        new Color(
                            0.18f,
                            0.15f,
                            0.035f,
                            0.97f
                        ),
                        StyleFactory.GOLD
                    )
                );
            }

            // =================================================
            // POS
            // =================================================

            Table position =
                ScreenUI.createBadge(
                    game.skin,
                    player.getPosition(),
                    StyleFactory
                        .getPositionColor(
                            player.getPosition()
                        )
                );

            row
                .add(position)
                .width(54f)
                .height(26f)
                .padLeft(5f);

            // =================================================
            // JOGADOR
            // =================================================

            Table playerInfo =
                new Table();

            Label name =
                new Label(
                    ScreenUI.shorten(
                        player.getName(),
                        22
                    ),
                    game.skin,
                    "font-bold"
                );

            name.setFontScale(
                0.57f
            );

            name.setColor(
                Color.WHITE
            );

            playerInfo
                .add(name)
                .left()
                .expandX();

            Label ovr =
                new Label(
                    "OVR " +
                        player.getOverall(),
                    game.skin,
                    "font-bold"
                );

            ovr.setFontScale(
                0.53f
            );

            ovr.setColor(
                StyleFactory.SOFT_YELLOW
            );

            playerInfo
                .add(ovr)
                .right()
                .row();

            int urgency =
                needs.getOrDefault(
                    player.getPosition(),
                    3
                );

            StringBuilder urgencyText =
                new StringBuilder(
                    "Necessidade: "
                );

            for (
                int i = 0;
                i < 5;
                i++
            ) {

                urgencyText.append(
                    i < urgency
                        ? "★"
                        : "☆"
                );
            }

            Label need =
                new Label(
                    urgencyText.toString(),
                    game.skin
                );

            need.setFontScale(
                0.43f
            );

            need.setColor(
                urgency >= 4
                    ? ScreenUI.WARNING
                    : ScreenUI.MUTED_TEXT
            );

            playerInfo
                .add(need)
                .left()
                .colspan(2);

            row
                .add(playerInfo)
                .growX()
                .padLeft(6f)
                .padRight(8f);

            // =================================================
            // SELECT
            // =================================================

            Label selectedLabel =
                new Label(
                    selected
                        ? "✓"
                        : "+",
                    game.skin,
                    "font-bold"
                );

            selectedLabel.setFontScale(
                0.78f
            );

            selectedLabel.setColor(
                selected
                    ? ScreenUI.SUCCESS
                    : StyleFactory.GOLD
            );

            row
                .add(selectedLabel)
                .width(36f)
                .center();

            row.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        togglePlayerSelection(
                            player,
                            isUser
                        );

                        refreshUI();
                    }
                }
            );

            items
                .add(row)
                .growX()
                .height(54f)
                .padBottom(3f)
                .row();

            // =================================================
            // TRADE VALUE
            // =================================================

            Table valueBar =
                TradeValueBar
                    .createBarWidget(
                        player,
                        game.skin
                    );

            items
                .add(valueBar)
                .growX()
                .padLeft(58f)
                .padRight(12f)
                .padBottom(5f)
                .row();
        }

        // =====================================================
        // PICKS
        // =====================================================

        Table picksHeader =
            ScreenUI.createTableHeaderRow();

        picksHeader
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    "DRAFT PICKS",
                    Align.left
                )
            )
            .left()
            .expandX()
            .padLeft(10f);

        items
            .add(picksHeader)
            .growX()
            .height(38f)
            .padTop(10f)
            .padBottom(4f)
            .row();

        if (
            club.getDraftPicks()
                .isEmpty()
        ) {

            Label none =
                ScreenUI.createSubtitle(
                    game.skin,
                    "Nenhuma escolha de Draft disponível."
                );

            items
                .add(none)
                .left()
                .pad(12f)
                .row();

        } else {

            int pickIndex =
                0;

            for (
                DraftPick pick :
                club.getDraftPicks()
            ) {

                boolean selected =
                    isUser
                        ? currentOffer
                        .getUserPicks()
                        .contains(
                            pick
                        )
                        : currentOffer
                        .getTargetPicks()
                        .contains(
                            pick
                        );

                Table row =
                    ScreenUI.createRow(
                        pickIndex++
                    );

                if (
                    selected
                ) {

                    row.background(
                        StyleFactory.createRoundedPanel(
                            new Color(
                                0.18f,
                                0.15f,
                                0.035f,
                                0.97f
                            ),
                            StyleFactory.GOLD
                        )
                    );
                }

                Label pickLabel =
                    new Label(
                        pick.getYear() +
                            "  •  " +
                            pick.getRound() +
                            "ª RODADA",
                        game.skin,
                        "font-bold"
                    );

                pickLabel.setFontScale(
                    0.56f
                );

                pickLabel.setColor(
                    StyleFactory.CREME_AGED
                );

                row
                    .add(pickLabel)
                    .left()
                    .expandX()
                    .padLeft(12f);

                Label projection =
                    new Label(
                        "Proj. #" +
                            pick.getProjectedPosition(),
                        game.skin
                    );

                projection.setFontScale(
                    0.48f
                );

                projection.setColor(
                    ScreenUI.MUTED_TEXT
                );

                row
                    .add(projection)
                    .width(85f);

                Label marker =
                    new Label(
                        selected
                            ? "✓"
                            : "+",
                        game.skin,
                        "font-bold"
                    );

                marker.setColor(
                    selected
                        ? ScreenUI.SUCCESS
                        : StyleFactory.GOLD
                );

                row
                    .add(marker)
                    .width(40f)
                    .center();

                row.addListener(
                    new ClickListener() {

                        @Override
                        public void clicked(
                            InputEvent event,
                            float x,
                            float y
                        ) {

                            togglePickSelection(
                                pick,
                                isUser
                            );

                            refreshUI();
                        }
                    }
                );

                items
                    .add(row)
                    .growX()
                    .height(44f)
                    .padBottom(3f)
                    .row();
            }
        }

        return items;
    }

    // =========================================================
    // SELECTION
    // =========================================================

    private void togglePlayerSelection(
        Player player,
        boolean isUser
    ) {

        if (
            isUser
        ) {

            if (
                currentOffer
                    .getUserPlayers()
                    .contains(
                        player
                    )
            ) {

                currentOffer
                    .removePlayerToGive(
                        player
                    );

            } else {

                currentOffer
                    .addPlayerToGive(
                        player
                    );
            }

        } else {

            if (
                currentOffer
                    .getTargetPlayers()
                    .contains(
                        player
                    )
            ) {

                currentOffer
                    .removePlayerToReceive(
                        player
                    );

            } else {

                currentOffer
                    .addPlayerToReceive(
                        player
                    );
            }
        }
    }

    private void togglePickSelection(
        DraftPick pick,
        boolean isUser
    ) {

        if (
            isUser
        ) {

            if (
                currentOffer
                    .getUserPicks()
                    .contains(
                        pick
                    )
            ) {

                currentOffer
                    .removePickToGive(
                        pick
                    );

            } else {

                currentOffer
                    .addPickToGive(
                        pick
                    );
            }

        } else {

            if (
                currentOffer
                    .getTargetPicks()
                    .contains(
                        pick
                    )
            ) {

                currentOffer
                    .removePickToReceive(
                        pick
                    );

            } else {

                currentOffer
                    .addPickToReceive(
                        pick
                    );
            }
        }
    }

    // =========================================================
    // RESUMO
    // =========================================================

    private Table createOfferSummaryPanel() {

        Table panel =
            ScreenUI.createPanel();

        TradeRulesValidator.ValidationResult validation =
            TradeRulesValidator
                .validateRules(
                    currentOffer,
                    game.league
                );

        // =====================================================
        // USER OFFER
        // =====================================================

        int userAssets = currentOffer.getUserAssetCount();

        int targetAssets = currentOffer.getTargetAssetCount();

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    userClub
                        .getName()
                        .toUpperCase(),
                    userAssets +
                        " ativos",
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(260f)
            .height(44f)
            .padRight(8f);

        Label arrow =
            new Label(
                "⇄",
                game.skin,
                "font-title"
            );

        arrow.setFontScale(
            0.70f
        );

        arrow.setColor(
            StyleFactory.GOLD
        );

        panel
            .add(arrow)
            .width(52f)
            .center();

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    targetClub
                        .getName()
                        .toUpperCase(),
                    targetAssets +
                        " ativos",
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(260f)
            .height(44f)
            .padRight(20f);

        panel
            .add()
            .expandX();

        if (
            !validation.isValid
        ) {

            Label error =
                new Label(
                    validation.reason,
                    game.skin,
                    "font-bold"
                );

            error.setFontScale(
                0.48f
            );

            error.setColor(
                ScreenUI.DANGER
            );

            error.setWrap(
                true
            );

            error.setAlignment(
                Align.right
            );

            panel
                .add(error)
                .width(350f)
                .right()
                .padRight(12f);
        }

        TextButton send =
            ScreenUI.createPrimaryButton(
                game.skin,
                "ANALISAR PROPOSTA"
            );

        send.setDisabled(
            !validation.isValid
        );

        send.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        !validation.isValid
                    ) {
                        return;
                    }

                    TradeDecision decision =
                        TradeNegotiator
                            .analyzeProposal(
                                currentOffer,
                                game.league
                                    .getCurrentSeason()
                            );

                    showFeedbackDialog(
                        decision
                    );
                }
            }
        );

        panel
            .add(send)
            .width(225f)
            .height(46f);

        panel.row();

        panel
            .add(
                createSalaryCapImpactPanel()
            )
            .colspan(6)
            .growX()
            .height(74f)
            .padTop(9f);

        return panel;
    }

    private Table createSalaryCapImpactPanel() {

        Table panel =
            ScreenUI.createSubtlePanel();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "IMPACTO NO SALARY CAP E VALOR PERCEBIDO"
                )
            )
            .colspan(2)
            .center()
            .padBottom(5f)
            .row();

        panel
            .add(
                createCapImpactCard(
                    userClub,
                    currentOffer.getUserPlayers(),
                    currentOffer.getUserPicks(),
                    currentOffer.getTargetPlayers(),
                    currentOffer.getTargetPicks()
                )
            )
            .growX()
            .uniformX()
            .padRight(8f);

        panel
            .add(
                createCapImpactCard(
                    targetClub,
                    currentOffer.getTargetPlayers(),
                    currentOffer.getTargetPicks(),
                    currentOffer.getUserPlayers(),
                    currentOffer.getUserPicks()
                )
            )
            .growX()
            .uniformX();

        return panel;
    }

    private Table createCapImpactCard(
        Club club,
        List<Player> outgoing,
        List<DraftPick> outgoingPicks,
        List<Player> incoming,
        List<DraftPick> incomingPicks
    ) {

        long salaryOut =
            outgoing.stream()
                .mapToLong(
                    Player::getAnnualSalary
                )
                .sum();

        long salaryIn =
            incoming.stream()
                .mapToLong(
                    Player::getAnnualSalary
                )
                .sum();

        long currentPayroll =
            club.getFinance()
                .getAnnualPayroll();

        long salaryCap =
            club.getFinance()
                .getSalaryCap();

        long projectedPayroll =
            currentPayroll -
                salaryOut +
                salaryIn;

        long projectedSpace =
            salaryCap -
                projectedPayroll;

        long delta =
            salaryIn -
                salaryOut;

        long valueSent = SmartTradeEvaluator.calculateTotalPerceivedValue(
            club,
            outgoing,
            outgoingPicks,
            game.league.getCurrentSeason()
        );

        long valueReceived = SmartTradeEvaluator.calculateTotalPerceivedValue(
            club,
            incoming,
            incomingPicks,
            game.league.getCurrentSeason()
        );

        long valueDelta = valueReceived - valueSent;

        Color capColor =
            projectedSpace >= 0
                ? ScreenUI.SUCCESS
                : ScreenUI.DANGER;

        Table card =
            new Table();

        card.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf(
                    "18201C"
                ),
                capColor
            )
        );

        card.pad(
            7f,
            12f,
            7f,
            12f
        );

        card
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    ScreenUI.shorten(
                        club.getName(),
                        24
                    ),
                    StyleFactory.CREME_AGED,
                    Align.left
                )
            )
            .left()
            .expandX();

        card
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    (delta >= 0
                        ? "+"
                        : "") +
                        formatMoney(
                            delta
                        ),
                    delta <= 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.WARNING,
                    Align.right
                )
            )
            .right()
            .padRight(10f);

        card
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    "TV " + (valueDelta >= 0 ? "+" : "") + valueDelta,
                    valueDelta >= 0 ? ScreenUI.SUCCESS : ScreenUI.DANGER,
                    Align.right
                )
            )
            .right()
            .row();

        card
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Folha: " +
                        formatMoney(
                            currentPayroll
                        ) +
                        " → " +
                        formatMoney(
                            projectedPayroll
                        )
                )
            )
            .left()
            .colspan(3)
            .row();

        card
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "Cap " +
                        formatMoney(
                            salaryCap
                        ) +
                        " • espaço após:"
                )
            )
            .left()
            .expandX()
            .colspan(2);

        card
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    formatMoney(
                        projectedSpace
                    ),
                    capColor,
                    Align.right
                )
            )
            .right();

        return card;
    }

    private String formatNeedStars(
        int need
    ) {

        StringBuilder stars =
            new StringBuilder();

        for (
            int index = 0;
            index < 5;
            index++
        ) {

            stars.append(
                index < need
                    ? "★"
                    : "☆"
            );
        }

        return stars.toString();
    }

    private String formatMoney(
        long amount
    ) {

        long absolute =
            Math.abs(
                amount
            );

        String sign =
            amount < 0
                ? "-"
                : "";

        if (
            absolute >= 1_000_000L
        ) {

            return sign +
                String.format(
                    Locale.US,
                    "WFL$ %.2fM",
                    absolute / 1_000_000.0
                );
        }

        return sign +
            "WFL$ " +
            String.format(
                Locale.US,
                "%,d",
                absolute
            );
    }

    // =========================================================
    // FEEDBACK
    // =========================================================

    private void showFeedbackDialog(
        TradeDecision decision
    ) {

        TradeFeedbackDialog dialog =
            new TradeFeedbackDialog(
                game.skin,
                targetClub.getName(),
                decision,
                new TradeFeedbackDialog.TradeDialogListener() {

                    @Override
                    public void onAcceptCounterOffer(
                        TradeOffer counterOffer
                    ) {

                        if (
                            counterOffer != null
                        ) {

                            currentOffer =
                                counterOffer;
                        }

                        executeTrade();
                    }

                    @Override
                    public void onModifyOffer() {

                        refreshUI();
                    }

                    @Override
                    public void onCancel() {

                        currentOffer =
                            new TradeOffer(
                                userClub,
                                targetClub
                            );

                        refreshUI();
                    }
                }
            );

        dialog.show(
            stage
        );
    }

    // =========================================================
    // EXECUTA TROCA
    // =========================================================

    private void executeTrade() {

        TradeRulesValidator.ValidationResult validation =
            TradeRulesValidator.validateRules(
                currentOffer,
                game.league
            );

        if (!validation.isValid) {
            Dialog blocked = new Dialog("TROCA BLOQUEADA", game.skin);
            blocked.text(validation.reason);
            blocked.button("OK");
            blocked.show(stage);
            refreshUI();
            return;
        }

        game.league.recordTrade(
            TradeRecord.fromOffer(
                currentOffer,
                game.league
            )
        );

        /*
         * Copiamos as listas porque transferTo()
         * altera os elencos durante a operação.
         */

        java.util.List<Player> userPlayers =
            new ArrayList<>(
                currentOffer
                    .getUserPlayers()
            );

        java.util.List<Player> targetPlayers =
            new ArrayList<>(
                currentOffer
                    .getTargetPlayers()
            );

        java.util.List<DraftPick> userPicks =
            new ArrayList<>(
                currentOffer
                    .getUserPicks()
            );

        java.util.List<DraftPick> targetPicks =
            new ArrayList<>(
                currentOffer
                    .getTargetPicks()
            );

        // =====================================================
        // PLAYERS
        // =====================================================

        for (
            Player player :
            userPlayers
        ) {

            player.transferTo(
                targetClub
            );
        }

        for (
            Player player :
            targetPlayers
        ) {

            player.transferTo(
                userClub
            );
        }

        // =====================================================
        // PICKS
        // =====================================================

        for (
            DraftPick pick :
            userPicks
        ) {

            userClub
                .getDraftPicks()
                .remove(
                    pick
                );

            if (
                !targetClub
                    .getDraftPicks()
                    .contains(
                        pick
                    )
            ) {

                targetClub
                    .getDraftPicks()
                    .add(
                        pick
                    );
            }

            pick.setCurrentOwner(
                targetClub
            );
        }

        for (
            DraftPick pick :
            targetPicks
        ) {

            targetClub
                .getDraftPicks()
                .remove(
                    pick
                );

            if (
                !userClub
                    .getDraftPicks()
                    .contains(
                        pick
                    )
            ) {

                userClub
                    .getDraftPicks()
                    .add(
                        pick
                    );
            }

            pick.setCurrentOwner(
                userClub
            );
        }

        // =====================================================
        // XI
        // =====================================================

        updateBestStartingLineup(
            userClub
        );

        updateBestStartingLineup(
            targetClub
        );

        currentOffer =
            new TradeOffer(
                userClub,
                targetClub
            );

        refreshUI();

        Dialog success =
            new Dialog(
                "TROCA CONCLUÍDA",
                game.skin
            );

        success.text(
            "A transação foi concluída.\n" +
                "Elencos, contratos, picks e escalações foram atualizados."
        );

        success.button(
            "OK"
        );

        success.show(
            stage
        );
    }

    // =========================================================
    // REESCALAÇÃO
    // =========================================================

    private void updateBestStartingLineup(
        Club club
    ) {

        if (
            club == null ||
                club.getSquad() ==
                    null ||
                club.getSquad()
                    .isEmpty()
        ) {

            return;
        }

        if (
            club.getFormation() ==
                null
        ) {

            club.setFormation(
                Formation.F_433
            );
        }

        Formation formation =
            club.getFormation();

        if (
            formation
                .getPositionSlots() ==
                null
        ) {

            return;
        }

        club.getTacticsMap()
            .clear();

        club.getStartingXI()
            .clear();

        java.util.List<String> slots =
            formation
                .getPositionSlots();

        Set<Player> usedPlayers =
            new HashSet<>();

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
                slots.get(
                    i
                );

            Player best =
                null;

            int bestEffective =
                -1;

            for (
                Player player :
                club.getSquad()
            ) {

                if (
                    usedPlayers
                        .contains(
                            player
                        ) ||
                        !player.canPlay()
                ) {

                    continue;
                }

                int effective =
                    player
                        .getEffectiveOverallForPosition(
                            position
                        );

                if (
                    effective >
                        bestEffective
                ) {

                    bestEffective =
                        effective;

                    best =
                        player;
                }
            }

            if (
                best != null
            ) {

                club.assignPlayerToSlot(
                    i,
                    best
                );

                usedPlayers.add(
                    best
                );
            }
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Color getPhaseColor(
        ClubNeedEvaluator.TeamPhase phase
    ) {

        switch (
            phase
        ) {

            case CONTENDER:
                return StyleFactory.DARK_GOLD;

            case BUYER:
                return Color.valueOf(
                    "285B45"
                );

            case SELLER:
                return Color.valueOf(
                    "6A5324"
                );

            case REBUILDING:
            default:
                return Color.valueOf(
                    "623636"
                );
        }
    }

    private static Club findDefaultPartner(
        Main game,
        Club userClub
    ) {

        if (
            game == null ||
                game.league == null ||
                game.league.getClubs() ==
                    null
        ) {

            return null;
        }

        for (
            Club club :
            game.league
                .getClubs()
        ) {

            if (
                club !=
                    userClub
            ) {

                return club;
            }
        }

        return null;
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

        backgroundTexture.dispose();
    }
}
