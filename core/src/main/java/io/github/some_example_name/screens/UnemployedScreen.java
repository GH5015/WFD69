package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.BoardObjective;
import io.github.some_example_name.model.BoardObjectiveService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubNeedEvaluator;
import io.github.some_example_name.model.ManagerCareer;
import io.github.some_example_name.model.ManagerSeasonRecord;
import io.github.some_example_name.utils.DayAdvanceTransition;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/** Central da carreira enquanto o treinador está sem clube. */
public class UnemployedScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Texture starTexture;

    public UnemployedScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ResponsiveViewport());
        this.starTexture = ScreenUI.loadTintableIcon("Icons8/icons8-estrela-48.png");
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        refresh();
    }

    private void refresh() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));
        root.add(new Image(StyleFactory.createSolid(new Color(0f, .025f, .014f, .80f))));

        Table page = new Table();
        page.setFillParent(true);
        page.top().pad(28f, 145f, 24f, 145f);
        page.add(ScreenUI.createHeader(
            game.skin,
            "MERCADO DE TREINADORES",
            "CARREIRA WFL • " + dateText()
        )).growX().height(78f).padBottom(11f).row();

        Table body = new Table();
        body.add(profilePanel()).width(410f).growY().padRight(12f);
        body.add(offersPanel()).grow();
        page.add(body).grow().padBottom(12f).row();
        page.add(footer()).growX().height(68f);
        root.add(page);
    }

    private Table profilePanel() {
        ManagerCareer career = game.managerCareer;
        Table panel = ScreenUI.createPanel();
        panel.top().pad(18f);
        Label status = ScreenUI.createSectionTitle(game.skin, "VOCÊ ESTÁ DESEMPREGADO");
        status.setColor(ScreenUI.WARNING);
        panel.add(status).growX().left().padBottom(7f).row();
        panel.add(ScreenUI.createSubtitle(
            game.skin,
            "Sua carreira continua. Analise as vagas ou avance o tempo para receber novas propostas."
        )).growX().left().padBottom(18f).row();

        Table reputation = ScreenUI.createSubtlePanel();
        reputation.pad(14f);
        reputation.add(ScreenUI.createSubtitle(game.skin, "REPUTAÇÃO DO TREINADOR"))
            .growX().left().row();
        reputation.add(stars(career.getReputationDisplayRating(), 25f)).left().padTop(9f).row();
        Label rep = ScreenUI.createBoldValue(
            game.skin,
            career.getReputationLabel() + " • " + career.getReputation() + "/100",
            StyleFactory.SOFT_YELLOW,
            Align.left
        );
        rep.setFontScale(.58f);
        reputation.add(rep).left().padTop(7f).row();
        reputation.add(ScreenUI.createSubtitle(
            game.skin,
            "Sem clube há " + career.getUnemployedDays() + " dia(s)"
        )).left().padTop(4f);
        reputation.row();
        reputation.add(ScreenUI.createSubtitle(
            game.skin,
            "Títulos: " + career.getTitlesWon() + "  •  Prêmios: " + career.getAwardsWon()
        )).left().padTop(3f);
        panel.add(reputation).growX().padBottom(18f).row();

        panel.add(ScreenUI.createSectionTitle(game.skin, "HISTÓRICO RECENTE"))
            .growX().left().padBottom(8f).row();
        List<ManagerSeasonRecord> history = career.getHistory();
        if (history.isEmpty()) {
            panel.add(ScreenUI.createSubtitle(game.skin, "Nenhuma temporada concluída."))
                .growX().left();
        } else {
            int amount = Math.min(5, history.size());
            for (int index = 0; index < amount; index++) {
                ManagerSeasonRecord record = history.get(index);
                String result = record.isChampion() ? "CAMPEÃO"
                    : record.hasReachedPlayoffs() ? "PLAYOFFS" : "TEMPORADA REGULAR";
                Table row = ScreenUI.createRow(index);
                row.add(ScreenUI.createBoldValue(
                    game.skin, String.valueOf(record.getSeason()), StyleFactory.SOFT_YELLOW, Align.left
                )).width(62f).left();
                row.add(ScreenUI.createSubtitle(game.skin, record.getClubName())).growX().left();
                row.add(ScreenUI.createBoldValue(
                    game.skin, result, record.isChampion() ? StyleFactory.GOLD : ScreenUI.MUTED_TEXT, Align.right
                )).right();
                panel.add(row).growX().height(42f).padBottom(4f).row();
            }
        }
        return panel;
    }

    private Table offersPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top().pad(16f);
        Table heading = new Table();
        heading.add(ScreenUI.createSectionTitle(game.skin, "OFERTAS DE EMPREGO"))
            .growX().left();
        heading.add(ScreenUI.createSubtitle(
            game.skin,
            game.managerCareer.getJobOffers(game.league).size() + " oportunidade(s)"
        )).right();
        panel.add(heading).growX().padBottom(10f).row();

        Table list = new Table();
        list.top();
        List<ManagerCareer.JobOffer> offers = game.managerCareer.getJobOffers(game.league);
        if (offers.isEmpty()) {
            Table empty = ScreenUI.createSubtlePanel();
            empty.add(ScreenUI.createSubtitle(
                game.skin,
                "Nenhuma proposta disponível. Avance uma semana para movimentar o mercado."
            )).pad(25f);
            list.add(empty).growX().row();
        } else {
            for (int index = 0; index < offers.size(); index++) {
                list.add(offerCard(offers.get(index), index)).growX().height(154f).padBottom(7f).row();
            }
        }
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table offerCard(final ManagerCareer.JobOffer offer, int index) {
        final Club club = offer.getClub();
        Table card = ScreenUI.createRow(index);
        card.pad(11f, 14f, 11f, 14f);

        Table identity = new Table();
        identity.left();
        Label name = ScreenUI.createSectionTitle(game.skin, club.getName().toUpperCase());
        name.setFontScale(.62f);
        identity.add(name).growX().left().row();
        identity.add(ScreenUI.createSubtitle(
            game.skin,
            "Situação: " + phaseLabel(ClubNeedEvaluator.getTeamPhase(club))
                + "  •  OVR " + Math.round(club.getOverall())
        )).growX().left().padTop(3f).row();
        Label contract = ScreenUI.createBoldValue(
            game.skin,
            "CONTRATO: " + offer.getContractYears() + " TEMPORADAS",
            StyleFactory.SOFT_YELLOW,
            Align.left
        );
        contract.setFontScale(.48f);
        identity.add(contract).left().padTop(7f);
        card.add(identity).width(365f).growY().left().padRight(16f);

        Table objectives = new Table();
        objectives.left();
        objectives.add(ScreenUI.createSubtitle(game.skin, "PRINCIPAIS OBJETIVOS")).left().row();
        List<BoardObjective> goals = BoardObjectiveService.objectivesFor(club);
        for (int goalIndex = 0; goalIndex < Math.min(3, goals.size()); goalIndex++) {
            BoardObjective goal = goals.get(goalIndex);
            objectives.add(ScreenUI.createSubtitle(
                game.skin,
                ScreenUI.formatActiveStars(goal.getPriority().getStars()) + "  " + goal.getTitle()
            )).growX().left().padTop(4f).row();
        }
        card.add(objectives).grow().left().padRight(15f);

        Table actions = new Table();
        TextButton accept = ScreenUI.createPrimaryButton(game.skin, "ACEITAR");
        accept.getLabel().setFontScale(.48f);
        accept.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.selectPlayerClub(club);
                game.setScreen("OFFSEASON".equals(game.league.getCurrentStage())
                    ? new OffSeasonScreen(game, club)
                    : new ClubManagementScreen(game, club));
            }
        });
        TextButton reject = ScreenUI.createInteractiveButton("RECUSAR", game.skin);
        reject.getLabel().setFontScale(.45f);
        reject.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.managerCareer.rejectOffer(club);
                refresh();
            }
        });
        actions.add(accept).width(135f).height(42f).row();
        actions.add(reject).width(135f).height(38f).padTop(7f);
        card.add(actions).right();
        return card;
    }

    private Table footer() {
        Table footer = ScreenUI.createPanel();
        footer.pad(7f, 12f, 7f, 12f);
        TextButton vacancies = ScreenUI.createInteractiveButton("VER VAGAS", game.skin);
        vacancies.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ClubSelectionScreen(game));
            }
        });
        footer.add(vacancies).width(230f).height(48f).left().expandX();
        TextButton advance = ScreenUI.createPrimaryButton(game.skin, "AVANÇAR 7 DIAS  ›");
        advance.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                DayAdvanceTransition.play(stage, game, 7, new Runnable() {
                    @Override public void run() {
                        game.managerCareer.advanceUnemployedTime(game.league, 7);
                        game.league.generateWeeklyNewsIfNeeded();
                        refresh();
                        WflNewsDialog.showPending(stage, game);
                    }
                });
            }
        });
        footer.add(advance).width(330f).height(50f).right();
        return footer;
    }

    private Table stars(float active, float size) {
        return ScreenUI.createStarRating(starTexture, active, size);
    }

    private String phaseLabel(ClubNeedEvaluator.TeamPhase phase) {
        switch (phase) {
            case CONTENDER: return "CANDIDATO AO TÍTULO";
            case BUYER: return "COMPRADOR";
            case SELLER: return "VENDEDOR";
            case REBUILDING:
            default: return "RECONSTRUÇÃO";
        }
    }

    private String dateText() {
        return game.league.getCurrentDate() == null
            ? "TEMPORADA " + game.league.getCurrentSeason()
            : new SimpleDateFormat("dd MMM yyyy", new Locale("pt", "BR"))
                .format(game.league.getCurrentDate()).toUpperCase();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, .02f, .01f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); starTexture.dispose(); }
}
