package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.LeagueHistory;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Cerimônia anual exibida entre o resumo da temporada e as aposentadorias. */
public class WflAwardsScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;

    public WflAwardsScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); refresh(); }

    private void refresh() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        int year = game.league.getCurrentSeason();
        LeagueHistory.SeasonRecord record = game.league.getHistory().findSeason(year);
        Table page = ScreenUI.createPage(true);
        page.add(ScreenUI.createHeader(game.skin, "PRÊMIOS ANUAIS DA WFL", "TEMPORADA " + year))
            .growX().height(ScreenUI.HEADER_HEIGHT).padBottom(9f).row();

        if (record == null) {
            Table empty = ScreenUI.createPanel();
            empty.add(ScreenUI.createSectionTitle(game.skin, "PRÊMIOS AINDA NÃO DEFINIDOS")).center().row();
            empty.add(ScreenUI.createSubtitle(game.skin, "Os vencedores são registrados após a final da WFL.")).center().padTop(10f);
            page.add(empty).grow().padBottom(9f).row();
        } else {
            page.add(createAwardGrid(record)).growX().height(258f).padBottom(9f).row();
            page.add(createTeamOfYear(record)).grow().padBottom(9f).row();
        }
        page.add(createActions()).growX().height(52f);
        root.add(page);
    }

    private Table createAwardGrid(LeagueHistory.SeasonRecord record) {
        Table grid = new Table();
        grid.add(awardCard("MVP DA TEMPORADA", record.getMvp(), "Jogador mais valioso", "MVP")).growX().uniformX().padRight(8f).padBottom(8f);
        grid.add(awardCard("ARTILHEIRO", record.getTopScorer(), record.getTopScorerGoals() + " gols", "GOL")).growX().uniformX().padRight(8f).padBottom(8f);
        grid.add(awardCard("LÍDER EM ASSISTÊNCIAS", record.getAssistLeader(), record.getAssistLeaderAssists() + " assistências", "AST")).growX().uniformX().padBottom(8f).row();
        grid.add(awardCard("ROOKIE DO ANO", record.getRookieOfYear(), "Melhor estreante", "ROK")).growX().uniformX().padRight(8f);
        grid.add(awardCard("MELHOR GOLEIRO", record.getBestGoalkeeper(), "Destaque entre os goleiros", "GK")).growX().uniformX().padRight(8f);
        grid.add(awardCard("MELHOR DEFENSOR", record.getBestDefender(), "Destaque defensivo", "DEF")).growX().uniformX();
        return grid;
    }

    private Table awardCard(String title, String player, String detail, String badge) {
        Table card = ScreenUI.createPanel();
        card.pad(10f);
        card.add(ScreenUI.createBadge(game.skin, badge, StyleFactory.SOFT_YELLOW)).width(58f).height(29f).padRight(10f);
        Table copy = new Table();
        copy.add(ScreenUI.createSubtitle(game.skin, title)).left().row();
        com.badlogic.gdx.scenes.scene2d.ui.Label name = ScreenUI.createBoldValue(game.skin, player, Color.WHITE, Align.left);
        name.setFontScale(.68f);
        copy.add(name).left().padTop(4f).row();
        copy.add(ScreenUI.createBoldValue(game.skin, detail, StyleFactory.SOFT_YELLOW, Align.left)).left().padTop(3f);
        card.add(copy).growX().left();
        return card;
    }

    private Table createTeamOfYear(LeagueHistory.SeasonRecord record) {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table heading = new Table();
        heading.add(ScreenUI.createSectionTitle(game.skin, "TIME DO ANO  •  4-3-3")).left().expandX();
        heading.add(ScreenUI.createSubtitle(game.skin, "Seleção formada por desempenho, impacto e posição")).right();
        panel.add(heading).growX().pad(9f, 12f, 8f, 12f).row();

        Table pitch = ScreenUI.createSubtlePanel();
        pitch.pad(8f, 28f, 8f, 28f);
        List<LeagueHistory.TeamOfYearMember> members = record.getTeamOfYear();
        addFormationLine(pitch, members, "LW", "ST", "RW");
        addFormationLine(pitch, members, "CM", "CAM", "CM");
        addFormationLine(pitch, members, "LB", "CB", "CB", "RB");
        addFormationLine(pitch, members, "GK");
        panel.add(pitch).grow().pad(0f, 10f, 10f, 10f);
        return panel;
    }

    private void addFormationLine(Table pitch, List<LeagueHistory.TeamOfYearMember> source, String... slots) {
        List<LeagueHistory.TeamOfYearMember> remaining = new ArrayList<>(source);
        for (String slot : slots) {
            LeagueHistory.TeamOfYearMember member = takeFirst(remaining, slot);
            pitch.add(playerCard(member, slot)).width(slots.length == 1 ? 265f : 235f).height(62f).pad(3f, 9f, 3f, 9f);
        }
        pitch.row();
    }

    private LeagueHistory.TeamOfYearMember takeFirst(List<LeagueHistory.TeamOfYearMember> members, String slot) {
        for (int index = 0; index < members.size(); index++) {
            LeagueHistory.TeamOfYearMember member = members.get(index);
            if (slot.equals(member.getSlot())) {
                members.remove(index);
                return member;
            }
        }
        return null;
    }

    private Table playerCard(LeagueHistory.TeamOfYearMember member, String slot) {
        Table card = ScreenUI.createRow(slot.hashCode());
        card.add(ScreenUI.createBadge(game.skin, slot, StyleFactory.getPositionColor(slot))).width(48f).height(25f).padLeft(6f).padRight(7f);
        Table copy = new Table();
        copy.add(ScreenUI.createBoldValue(game.skin, member != null ? member.getPlayerName() : "—", Color.WHITE, Align.left)).left().row();
        String detail = member == null ? "Sem registro" : member.getClubName() + "  •  OVR " + member.getOverall()
            + (member.getAverageRating() > 0 ? "  •  " + String.format(Locale.US, "%.1f", member.getAverageRating()) : "");
        com.badlogic.gdx.scenes.scene2d.ui.Label detailLabel = ScreenUI.createSubtitle(game.skin, detail);
        detailLabel.setEllipsis(true);
        copy.add(detailLabel).growX().left().padTop(2f);
        card.add(copy).growX().left().padRight(6f);
        return card;
    }

    private Table createActions() {
        Table actions = new Table();
        TextButton back = ScreenUI.createSecondaryButton(game.skin, "VOLTAR AO RESUMO");
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new SeasonSummaryScreen(game, club)); }
        });
        TextButton next = ScreenUI.createPrimaryButton(game.skin, "VER APOSENTADORIAS  ›");
        next.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new RetirementSummaryScreen(game, club)); }
        });
        actions.add(back).width(250f).height(44f).left().expandX();
        actions.add(next).width(390f).height(50f).right();
        return actions;
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); }
}
