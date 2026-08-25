package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftOrderService;
import io.github.some_example_name.model.DraftPick;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

/** Painel de projeção das 40 escolhas, atualizado pela classificação vigente. */
public class DraftBoardScreen implements Screen {
    private final Main game;
    private final Club club;
    private final DraftScoutManager scoutManager;
    private final Stage stage;
    private final Texture backgroundTexture;

    public DraftBoardScreen(Main game, Club club, DraftScoutManager scoutManager) {
        this.game = game;
        this.club = club;
        this.scoutManager = scoutManager;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("prancheta.png"));
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); refreshUI(); }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);
        root.add(background);

        int draftYear = game.league.getCurrentSeason() + 1;
        List<DraftPick> picks = DraftOrderService.getCurrentDraftOrder(game.league, draftYear);
        Table page = ScreenUI.createPage(true);
        page.add(ScreenUI.createHeader(game.skin, "PROJEÇÃO DO DRAFT", "DRAFT " + draftYear + "  •  40 ESCOLHAS"))
            .growX().height(ScreenUI.HEADER_HEIGHT).padBottom(10f).row();

        Table summary = ScreenUI.createPanel();
        summary.add(ScreenUI.createStatusBox(game.skin, "ESCOLHAS", picks.size() + "/40", picks.size() == 40 ? ScreenUI.SUCCESS : ScreenUI.WARNING)).growX().uniformX().padRight(8f);
        summary.add(ScreenUI.createStatusBox(game.skin, "ORDEM", "TABELA ATUAL", StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8f);
        summary.add(ScreenUI.createStatusBox(game.skin, "SUA FRANQUIA", club.getName(), StyleFactory.getPositionColor("CM"))).growX().uniformX();
        page.add(summary).growX().height(64f).padBottom(10f).row();
        page.add(createDraftTable(picks)).grow().padBottom(10f).row();

        TextButton back = ScreenUI.createPrimaryButton(game.skin, "VOLTAR AO SCOUTING");
        back.getLabel().setFontScale(0.55f);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new DraftScoutingScreen(game, club, scoutManager)); }
        });
        page.add(back).width(300f).height(50f).center().row();
        root.add(page);
        NavigationDrawer.attach(stage, game, club, "SCOUTING", true);
    }

    private Table createDraftTable(List<DraftPick> picks) {
        Table panel = ScreenUI.createTablePanel();
        Table content = new Table();
        content.top();
        int previousRound = 0;
        int overallPick = 0;
        int cardInRound = 0;
        for (DraftPick pick : picks) {
            if (pick.getRound() != previousRound) {
                if (cardInRound % 2 != 0) content.add().width(620f).height(76f).row();
                previousRound = pick.getRound();
                cardInRound = 0;
                content.add(createRoundDivider(previousRound)).colspan(2).growX().height(36f).padTop(5f).padBottom(5f).row();
            }
            content.add(createPickCard(pick, ++overallPick)).width(620f).height(76f).padRight(cardInRound % 2 == 0 ? 8f : 0f).padBottom(6f);
            cardInRound++;
            if (cardInRound % 2 == 0) content.row();
        }
        if (cardInRound % 2 != 0) content.add().width(620f).height(76f).row();
        ScrollPane scroll = new ScrollPane(content, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createRoundDivider(int round) {
        Table divider = ScreenUI.createSubtlePanel();
        divider.add(ScreenUI.createSectionTitle(game.skin, round + "ª RODADA  •  ESCOLHAS " + ((round - 1) * 20 + 1) + "–" + (round * 20))).left().padLeft(12f);
        divider.add(ScreenUI.createSubtitle(game.skin, "A ordem muda conforme a classificação da liga")).right().expandX().padRight(12f);
        return divider;
    }

    private Table createPickCard(DraftPick pick, int overallPick) {
        boolean userOwnsPick = pick.getCurrentOwner() == club;
        int leaguePosition = DraftOrderService.getLeagueStandingPosition(game.league, pick.getOriginalOwner());
        Table card = ScreenUI.createRow(overallPick - 1);
        Color mainColor = userOwnsPick ? StyleFactory.SOFT_YELLOW : Color.WHITE;
        card.add(ScreenUI.createStatusBox(game.skin, "PICK " + overallPick, "#" + overallPick, StyleFactory.SOFT_YELLOW)).width(70f).height(58f).padLeft(7f).padRight(6f);

        Table team = new Table();
        team.add(ScreenUI.createBoldValue(game.skin, pick.getCurrentOwner().getName(), mainColor, Align.left)).left().expandX().row();
        String origin = pick.getOriginalOwner() == pick.getCurrentOwner()
            ? "Escolha própria"
            : "via " + pick.getOriginalOwner().getName();
        team.add(ScreenUI.createSubtitle(game.skin, origin)).left();
        card.add(team).width(255f).left().padRight(6f);

        Table projection = new Table();
        projection.add(ScreenUI.createSubtitle(game.skin, "PROJEÇÃO")).left().expandX();
        projection.add(ScreenUI.createBoldValue(game.skin, "#" + pick.getProjectedPosition(), StyleFactory.SOFT_YELLOW, Align.right)).right().row();
        projection.add(ScreenUI.createSubtitle(game.skin, "POS. LIGA (ORIGEM)")).left().expandX();
        projection.add(ScreenUI.createBoldValue(game.skin, leaguePosition > 0 ? leaguePosition + "º" : "—", Color.WHITE, Align.right)).right();
        card.add(projection).width(175f).padRight(6f);

        card.add(ScreenUI.createBadge(game.skin, pick.getRound() + "ª ROD.", StyleFactory.getPositionColor("CM"))).width(75f).height(28f).padRight(7f);
        return card;
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); backgroundTexture.dispose(); }
}
