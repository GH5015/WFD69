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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.InterestBarWidget;
import io.github.some_example_name.utils.StyleFactory;
import io.github.some_example_name.utils.TradeValueBar;
import java.util.HashSet;
import java.util.Set;

import java.util.Map;

public class TradeScreen implements Screen {
    private final Main game;
    private final Club userClub;
    private Club targetClub;
    private Stage stage;
    private Texture pranchetaTexture;
    private TradeOffer currentOffer;

    public TradeScreen(Main game, Club userClub) {
        this(game, userClub, game.league.getClubs().stream()
            .filter(c -> !c.equals(userClub))
            .findFirst()
            .orElse(null));
    }

    public TradeScreen(Main game, Club userClub, Club targetClub) {
        this.game = game;
        this.userClub = userClub;
        this.targetClub = targetClub;
        this.stage = new Stage(new ScreenViewport());
        this.pranchetaTexture = new Texture(Gdx.files.internal("prancheta.png"));
        this.currentOffer = new TradeOffer(userClub, targetClub);
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
        mainContent.pad(36, 220, 24, 60);

        // Cabeçalho Principal
        Label title = new Label("CENTRAL DE TROCAS (TRADES)", game.skin, "font-title");
        title.setColor(StyleFactory.GOLD);
        mainContent.add(title).colspan(2).center().padBottom(6).row();

        // Widget de Interesse em Tempo Real
        int interestScore = SmartTradeEvaluator.calculateInterestScore(currentOffer);
        Table interestWidget = InterestBarWidget.createWidget(targetClub, interestScore, game.skin);
        mainContent.add(interestWidget).colspan(2).growX().padBottom(10).row();

        // Colunas de Negociação
        Table columns = new Table();
        columns.add(createClubTradePanel(userClub, true)).grow().uniformX().padRight(10);
        columns.add(createClubTradePanel(targetClub, false)).grow().uniformX();
        mainContent.add(columns).grow().row();

        // Painel de Resumo Financeiro, Roster e Ação
        mainContent.add(createOfferSummaryPanel()).colspan(2).growX().padTop(8).row();

        root.add(mainContent);
        NavigationDrawer.attach(stage, game, userClub, "TROCAS", false);
        CareerOverlay.attach(stage, game, userClub);
    }

    private Table createClubTradePanel(Club club, boolean isUser) {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(10);

        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(club);
        int postTradeSize = club.getSquad().size()
            - (isUser ? currentOffer.getUserPlayers().size() : currentOffer.getTargetPlayers().size())
            + (isUser ? currentOffer.getTargetPlayers().size() : currentOffer.getUserPlayers().size());

        Table headerTable = new Table();

        if (isUser) {
            Label clubTitle = new Label(club.getName().toUpperCase(), game.skin, "font-bold");
            clubTitle.setColor(StyleFactory.SOFT_YELLOW);
            headerTable.add(clubTitle).left().expandX();
        } else {
            Array<Club> availableClubs = new Array<>();
            for (Club c : game.league.getClubs()) {
                if (!c.equals(userClub)) {
                    availableClubs.add(c);
                }
            }

            SelectBox<Club> clubSelector = new SelectBox<>(game.skin);
            clubSelector.setItems(availableClubs);
            clubSelector.setSelected(targetClub);
            clubSelector.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Club selected = clubSelector.getSelected();
                    if (selected != null && !selected.equals(targetClub)) {
                        targetClub = selected;
                        currentOffer = new TradeOffer(userClub, targetClub);
                        refreshUI();
                    }
                }
            });
            headerTable.add(clubSelector).left().expandX().height(30).padRight(5);
        }

        Label phaseTag = new Label("[" + phase.name() + "]", game.skin, "font-bold");
        phaseTag.setFontScale(0.55f);
        phaseTag.setColor(phase == ClubNeedEvaluator.TeamPhase.CONTENDER ? StyleFactory.GOLD : Color.CYAN);
        headerTable.add(phaseTag).right().row();

        Label rosterInfo = new Label("Elenco Projetado: " + postTradeSize + "/26", game.skin);
        rosterInfo.setFontScale(0.55f);
        rosterInfo.setColor(postTradeSize < 23 || postTradeSize > 26 ? Color.RED : Color.LIGHT_GRAY);
        headerTable.add(rosterInfo).colspan(2).left().padBottom(6).row();

        panel.add(headerTable).growX().row();

        // Lista de Ativos
        Table itemsTable = new Table();
        Map<String, Integer> positionNeeds = ClubNeedEvaluator.calculatePositionNeeds(club);

        Label playersHeader = new Label("--- ELENCO ---", game.skin, "font-bold");
        playersHeader.setFontScale(0.6f);
        playersHeader.setColor(Color.GRAY);
        itemsTable.add(playersHeader).left().padTop(2).padBottom(4).row();

        ScrollPane scroll = new ScrollPane(itemsTable, game.skin);

        for (Player p : club.getSquad()) {
            boolean isSelected = isUser ? currentOffer.getUserPlayers().contains(p) : currentOffer.getTargetPlayers().contains(p);

            Table playerRow = new Table();
            playerRow.background(StyleFactory.createRoundedPanel(isSelected ? StyleFactory.MUSGO_LIGHT : StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
            playerRow.pad(4, 6, 4, 6);

            int needStars = positionNeeds.getOrDefault(p.getPosition(), 3);
            String needIcon = "★".repeat(needStars);

            Label nameLabel = new Label(p.getPosition() + " " + p.getName() + " (OVR " + p.getOverall() + ")", game.skin, "font-bold");
            nameLabel.setFontScale(0.65f);
            playerRow.add(nameLabel).left().expandX();

            Label needLabel = new Label("Urg: " + needIcon, game.skin);
            needLabel.setFontScale(0.50f);
            needLabel.setColor(needStars >= 4 ? Color.ORANGE : Color.GRAY);
            playerRow.add(needLabel).right().row();

            Table tvWidget = TradeValueBar.createBarWidget(p, game.skin);
            playerRow.add(tvWidget).colspan(2).left().padTop(2);

            playerRow.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) playerRow.setColor(StyleFactory.SOFT_YELLOW);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer == -1) playerRow.setColor(Color.WHITE);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    float scrollY = scroll.getScrollY();
                    togglePlayerSelection(p, isUser);
                    refreshUI();
                    Gdx.app.postRunnable(() -> scroll.setScrollY(scrollY));
                }
            });

            itemsTable.add(playerRow).growX().padBottom(4).row();
        }

        Label picksHeader = new Label("--- PICKS DE DRAFT ---", game.skin, "font-bold");
        picksHeader.setFontScale(0.6f);
        picksHeader.setColor(Color.GRAY);
        itemsTable.add(picksHeader).left().padTop(6).padBottom(4).row();

        for (DraftPick pick : club.getDraftPicks()) {
            boolean isSelected = isUser ? currentOffer.getUserPicks().contains(pick) : currentOffer.getTargetPicks().contains(pick);

            TextButton pickBtn = new TextButton(pick.getYear() + " - " + pick.getRound() + "ª Rodada (Proj. #" + pick.getProjectedPosition() + ")", game.skin, "toggle");
            pickBtn.getLabel().setFontScale(0.65f);
            pickBtn.setChecked(isSelected);
            pickBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    float scrollY = scroll.getScrollY();
                    if (isUser) {
                        if (currentOffer.getUserPicks().contains(pick)) currentOffer.removePickToGive(pick);
                        else currentOffer.addPickToGive(pick);
                    } else {
                        if (currentOffer.getTargetPicks().contains(pick)) currentOffer.removePickToReceive(pick);
                        else currentOffer.addPickToReceive(pick);
                    }
                    refreshUI();
                    Gdx.app.postRunnable(() -> scroll.setScrollY(scrollY));
                }
            });
            itemsTable.add(pickBtn).growX().padBottom(3).row();
        }

        panel.add(scroll).grow().row();
        return panel;
    }

    private void togglePlayerSelection(Player player, boolean isUser) {
        if (isUser) {
            if (currentOffer.getUserPlayers().contains(player)) {
                currentOffer.removePlayerToGive(player);
            } else {
                currentOffer.addPlayerToGive(player);
            }
        } else {
            if (currentOffer.getTargetPlayers().contains(player)) {
                currentOffer.removePlayerToReceive(player);
            } else {
                currentOffer.addPlayerToReceive(player);
            }
        }
    }

    private Table createOfferSummaryPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(6, 12, 6, 12);

        TradeRulesValidator.ValidationResult ruleCheck = TradeRulesValidator.validateRules(currentOffer);

        if (!ruleCheck.isValid) {
            Label errorLabel = new Label("⚠️ " + ruleCheck.reason, game.skin, "font-bold");
            errorLabel.setColor(Color.valueOf("E74C3C"));
            errorLabel.setFontScale(0.60f);
            panel.add(errorLabel).center().padBottom(2).row();
        }

        TextButton sendBtn = new TextButton("ANALISAR PROPOSTA", game.skin);
        sendBtn.setDisabled(!ruleCheck.isValid);
        sendBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (!ruleCheck.isValid) return;
                TradeDecision decision = TradeNegotiator.analyzeProposal(currentOffer);
                showFeedbackDialog(decision);
            }
        });

        panel.add(sendBtn).height(34).width(210).center();
        return panel;
    }

    private void showFeedbackDialog(TradeDecision decision) {
        TradeFeedbackDialog dialog = new TradeFeedbackDialog(
            game.skin,
            targetClub.getName(),
            decision,
            new TradeFeedbackDialog.TradeDialogListener() {
                @Override
                public void onAcceptCounterOffer(TradeOffer counterOffer) {
                    if (counterOffer != null) {
                        currentOffer = counterOffer;
                    }
                    executeTrade();
                }

                @Override
                public void onModifyOffer() {
                    refreshUI();
                }

                @Override
                public void onCancel() {
                    currentOffer = new TradeOffer(userClub, targetClub);
                    refreshUI();
                }
            }
        );
        dialog.show(stage);
    }

    private void executeTrade() {
        // Transferência de Jogadores
        for (Player p : currentOffer.getUserPlayers()) {
            userClub.getSquad().remove(p);
            targetClub.getSquad().add(p);
        }
        for (Player p : currentOffer.getTargetPlayers()) {
            targetClub.getSquad().remove(p);
            userClub.getSquad().add(p);
        }

        // Transferência de Draft Picks
        for (DraftPick pick : currentOffer.getUserPicks()) {
            userClub.getDraftPicks().remove(pick);
            targetClub.getDraftPicks().add(pick);
        }
        for (DraftPick pick : currentOffer.getTargetPicks()) {
            targetClub.getDraftPicks().remove(pick);
            userClub.getDraftPicks().add(pick);
        }

        // --- ATUALIZAÇÃO DOS TITULARES ---
        updateBestStartingLineup(userClub);
        updateBestStartingLineup(targetClub);

        currentOffer = new TradeOffer(userClub, targetClub);
        refreshUI();

        Dialog successDialog = new Dialog("Troca Concluída", game.skin);
        successDialog.text("A transação foi aprovada! As escalações titulares de ambos os times foram atualizadas.");
        successDialog.button("OK");
        successDialog.show(stage);
    }

    /**
     * Atualiza os 11 titulares do clube preenchendo cada posição da formação
     * com o jogador que possui o melhor 'Effective Overall' disponível para aquela função.
     */
    private void updateBestStartingLineup(Club club) {
        if (club == null || club.getSquad() == null || club.getSquad().isEmpty()) return;

        if (club.getFormation() == null) {
            club.setFormation(Formation.F_433);
        }

        Formation formation = club.getFormation();
        if (formation.getPositionSlots() == null) return;

        club.getTacticsMap().clear();
        club.getStartingXI().clear();

        // Especifica java.util.List para evitar conflito com com.badlogic.gdx.scenes.scene2d.ui.List
        java.util.List<String> slots = formation.getPositionSlots();
        Set<Player> usedPlayers = new HashSet<>();

        for (int i = 0; i < Math.min(11, slots.size()); i++) {
            String targetPosition = slots.get(i);
            Player bestPlayer = null;
            int maxEffOverall = -1;

            for (Player p : club.getSquad()) {
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        if (pranchetaTexture != null) pranchetaTexture.dispose();
    }
}
