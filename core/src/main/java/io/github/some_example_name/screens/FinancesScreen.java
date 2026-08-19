package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.EconomicPower;
import io.github.some_example_name.model.FinancialHealthState;
import io.github.some_example_name.utils.StyleFactory;

public class FinancesScreen implements Screen {
    private final Main game;
    private final Club club;
    private Stage stage;
    private Texture pranchetaTexture;
    private Texture starTexture;

    public FinancesScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ScreenViewport());
        this.pranchetaTexture = new Texture(Gdx.files.internal("prancheta.png"));
        this.starTexture = new Texture(Gdx.files.internal("Icons8/icons8-estrela-48.png"));    }

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
        mainContent.pad(42, 238, 36, 74);

        // 1. Saldo no Topo
        mainContent.add(createHeaderPanel()).growX().padBottom(12).row();

        // 2. Receitas vs Despesas
        mainContent.add(createFinancialStatementPanel()).growX().padBottom(12).row();

        // 3. Painéis Inferiores: Poder Econômico + Situação Financeira
        Table bottomContainer = new Table();
        bottomContainer.add(createEconomicOverviewPanel()).growX().uniformX().padRight(12);
        bottomContainer.add(createSituationPanel()).growX().uniformX();

        mainContent.add(bottomContainer).growX().row();

        root.add(mainContent);

        NavigationDrawer.attach(stage, game, club, "FINANÇAS", true);
        CareerOverlay.attach(stage, game, club);
    }

    private Table createHeaderPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(10, 16, 10, 16);

        ClubFinance fin = club.getFinance();

        Label title = new Label("SALDO ATUAL", game.skin, "font-bold");
        title.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(title).center().row();

        String formattedBalance = String.format("WFL$ %,d,00", fin.getBalance()).replace(',', '.');
        Label balanceLabel = new Label(formattedBalance, game.skin, "font-title");
        balanceLabel.setColor(StyleFactory.GOLD);
        panel.add(balanceLabel).center().padBottom(6).row();

        Table flowTable = new Table();
        flowTable.add(new Label("Receita mensal: ", game.skin, "font-bold")).left();
        Label revLabel = new Label("+" + formatWFL(fin.getTotalMonthlyRevenue()), game.skin, "font-bold");
        revLabel.setColor(Color.GREEN);
        flowTable.add(revLabel).left().padRight(24);

        flowTable.add(new Label("Despesas mensais: ", game.skin, "font-bold")).left();
        Label expLabel = new Label("-" + formatWFL(fin.getTotalMonthlyExpenses()), game.skin, "font-bold");
        expLabel.setColor(Color.RED);
        flowTable.add(expLabel).left().row();

        long net = fin.getMonthlyNetResult();
        Label netLabel = new Label("Resultado: " + (net >= 0 ? "+" : "-") + formatWFL(Math.abs(net)), game.skin, "font-bold");
        netLabel.setColor(net >= 0 ? Color.GREEN : Color.RED);
        flowTable.add(netLabel).colspan(4).center().padTop(4);

        panel.add(flowTable).center();
        return panel;
    }

    private Table createFinancialStatementPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(14, 20, 14, 20);

        ClubFinance fin = club.getFinance();

        // Coluna Receitas
        Table revCol = new Table();
        revCol.add(new Label("RECEITAS", game.skin, "font-bold")).left().padBottom(8).colspan(2).row();
        addFinanceRow(revCol, "Bilheteria", fin.getTicketRevenue(), Color.WHITE);
        addFinanceRow(revCol, "Televisão", fin.getTvRevenue(), Color.WHITE);
        addFinanceRow(revCol, "Camisas", fin.getShirtSalesRevenue(), Color.WHITE);
        addFinanceRow(revCol, "Premiações", fin.getPrizeMoneyRevenue(), Color.WHITE);
        addFinanceRow(revCol, "TOTAL", fin.getTotalMonthlyRevenue(), Color.GREEN);

        // Coluna Despesas
        Table expCol = new Table();
        expCol.add(new Label("DESPESAS", game.skin, "font-bold")).left().padBottom(8).colspan(2).row();
        addFinanceRow(expCol, "Salários", fin.getPlayerSalariesExpense(), Color.WHITE);
        addFinanceRow(expCol, "Infraestrutura", fin.getInfrastructureExpense(), Color.WHITE);
        addFinanceRow(expCol, "Dep. médico", fin.getMedicalExpense(), Color.WHITE);
        addFinanceRow(expCol, "Olheiros", fin.getScoutingExpense(), Color.WHITE);
        addFinanceRow(expCol, "TOTAL", fin.getTotalMonthlyExpenses(), Color.RED);

        panel.add(revCol).growX().top().padRight(28);
        panel.add(expCol).growX().top();

        return panel;
    }

    private Table createEconomicOverviewPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(14, 18, 14, 18);

        ClubFinance fin = club.getFinance();

        Label title = new Label("PODER ECONÔMICO", game.skin, "font-bold");
        title.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(title).colspan(2).left().padBottom(6).row();

        // Montagem gráfica com o PNG icons8-estrela-48.png
        Table starsWidget = createStarsWidget();
        panel.add(starsWidget).colspan(2).left().padBottom(10).row();

        String annualRev = String.format("WFL$ %,dM", fin.getTotalAnnualRevenue() / 1_000_000);
        String clubVal = String.format("WFL$ %,dM", fin.getClubValuation() / 1_000_000);

        panel.add(new Label("Receita anual:", game.skin, "font-bold")).left().expandX();
        Label revLabel = new Label(annualRev, game.skin, "font-bold");
        revLabel.setColor(Color.WHITE);
        panel.add(revLabel).right().row();

        panel.add(new Label("Valor do clube:", game.skin, "font-bold")).left().expandX().padTop(4);
        Label valLabel = new Label(clubVal, game.skin, "font-bold");
        valLabel.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(valLabel).right().padTop(4).row();

        return panel;
    }

    private Table createStarsWidget() {
        Table starsTable = new Table();
        int rating = EconomicPower.getStarRating(club);

        for (int i = 0; i < 5; i++) {
            Image starImage = new Image(new TextureRegionDrawable(starTexture));
            if (i < rating) {
                starImage.setColor(StyleFactory.GOLD);
            } else {
                starImage.setColor(new Color(1f, 1f, 1f, 0.22f)); // Opacidade reduzida para estrelas inativas
            }
            starsTable.add(starImage).size(22, 22).padRight(4);
        }
        return starsTable;
    }

    private Table createSituationPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.MUSGO_DEEP, StyleFactory.GOLD));
        panel.pad(14, 18, 14, 18);

        ClubFinance fin = club.getFinance();
        FinancialHealthState health = fin.getHealthState();

        Label title = new Label("SITUAÇÃO FINANCEIRA", game.skin, "font-bold");
        title.setColor(StyleFactory.SOFT_YELLOW);
        panel.add(title).colspan(2).left().padBottom(8).row();

        // Caixa
        panel.add(new Label("Caixa:", game.skin, "font-bold")).left().expandX();
        Label healthLabel = new Label(health.getFormattedStatus(), game.skin, "font-bold");
        healthLabel.setColor(health.getColor());
        panel.add(healthLabel).right().row();

        // Linhas do Salary Cap
        addFinanceRow(panel, "Salary Cap", fin.getSalaryCap(), Color.WHITE);
        addFinanceRow(panel, "Folha salarial", fin.getAnnualPayroll(), Color.WHITE);

        long space = fin.getAvailableCapSpace();
        addFinanceRow(panel, "Espaço disponível", space, space >= 0 ? Color.GREEN : Color.RED);

        return panel;
    }

    private void addFinanceRow(Table parent, String labelText, long amount, Color valueColor) {
        parent.add(new Label(labelText, game.skin, "font-bold")).left().expandX().padBottom(3);
        Label valLabel = new Label(formatWFL(amount), game.skin, "font-bold");
        valLabel.setColor(valueColor);
        parent.add(valLabel).right().padBottom(3).row();
    }

    private String formatWFL(long amount) {
        return "WFL$ " + String.format("%,d", amount).replace(',', '.');
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        if (pranchetaTexture != null) pranchetaTexture.dispose();
        if (starTexture != null) starTexture.dispose();
    }
}
