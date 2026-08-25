package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.EconomicPower;
import io.github.some_example_name.model.FinancialHealthState;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

public class FinancesScreen implements Screen {

    private final Main game;
    private final Club club;

    private final Stage stage;

    private final Texture backgroundTexture;

    private Texture starTexture;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public FinancesScreen(
        Main game,
        Club club
    ) {

        this.game =
            game;

        this.club =
            club;

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

        try {

            if (
                Gdx.files
                    .internal(
                        "Icons8/icons8-estrela-48.png"
                    )
                    .exists()
            ) {

                starTexture =
                    new Texture(
                        Gdx.files.internal(
                            "Icons8/icons8-estrela-48.png"
                        )
                    );
            }

        } catch (
            Exception ignored
        ) {

            starTexture =
                null;
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
    // UI
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

        Image bg =
            new Image(
                new TextureRegionDrawable(
                    backgroundTexture
                )
            );

        bg.setFillParent(
            true
        );

        root.add(
            bg
        );

        Table page =
            ScreenUI.createPage(
                true
            );

        ClubFinance finance =
            club.getFinance();

        // =====================================================
        // HEADER
        // =====================================================

        page
            .add(
                ScreenUI.createHeader(
                    game.skin,
                    "FINANÇAS",
                    club.getName()
                        .toUpperCase()
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        // =====================================================
        // TOP CARDS
        // =====================================================

        Table topCards =
            createTopCards(
                finance
            );

        page
            .add(topCards)
            .growX()
            .height(112f)
            .padBottom(10f)
            .row();

        // =====================================================
        // RECEITAS / DESPESAS
        // =====================================================

        Table middle =
            new Table();

        middle
            .add(
                createRevenuePanel(
                    finance
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        middle
            .add(
                createExpensePanel(
                    finance
                )
            )
            .grow()
            .uniformX();

        page
            .add(middle)
            .grow()
            .padBottom(10f)
            .row();

        // =====================================================
        // BOTTOM
        // =====================================================

        Table bottom =
            new Table();

        bottom
            .add(
                createSalaryCapPanel(
                    finance
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        bottom
            .add(
                createEconomicPowerPanel(
                    finance
                )
            )
            .grow()
            .uniformX()
            .padRight(10f);

        bottom
            .add(
                createHealthPanel(
                    finance
                )
            )
            .grow()
            .uniformX();

        page
            .add(bottom)
            .growX()
            .height(180f)
            .row();

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            club,
            "FINANÇAS",
            true
        );

    }

    // =========================================================
    // TOP CARDS
    // =========================================================

    private Table createTopCards(
        ClubFinance finance
    ) {

        Table cards =
            new Table();

        long balance =
            finance.getBalance();

        long revenue =
            finance.getTotalMonthlyRevenue();

        long expenses =
            finance.getTotalMonthlyExpenses();

        long net =
            finance.getMonthlyNetResult();

        cards
            .add(
                createLargeFinanceCard(
                    "SALDO ATUAL",
                    formatWFL(
                        balance
                    ),
                    balance >= 0
                        ? StyleFactory.GOLD
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .uniformX()
            .padRight(8f);

        cards
            .add(
                createLargeFinanceCard(
                    "RECEITA MENSAL",
                    "+" +
                        formatWFL(
                            revenue
                        ),
                    ScreenUI.SUCCESS
                )
            )
            .growX()
            .uniformX()
            .padRight(8f);

        cards
            .add(
                createLargeFinanceCard(
                    "DESPESAS MENSAIS",
                    "-" +
                        formatWFL(
                            expenses
                        ),
                    ScreenUI.DANGER
                )
            )
            .growX()
            .uniformX()
            .padRight(8f);

        cards
            .add(
                createLargeFinanceCard(
                    "RESULTADO MENSAL",
                    (
                        net >= 0
                            ? "+"
                            : "-"
                    ) +
                        formatWFL(
                            Math.abs(
                                net
                            )
                        ),
                    net >= 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .uniformX();

        return cards;
    }

    private Table createLargeFinanceCard(
        String title,
        String value,
        Color valueColor
    ) {

        Table panel =
            ScreenUI.createPanel();

        Label titleLabel =
            new Label(
                title,
                game.skin,
                "font-bold"
            );

        titleLabel.setFontScale(
            0.50f
        );

        titleLabel.setColor(
            ScreenUI.MUTED_TEXT
        );

        titleLabel.setAlignment(
            Align.center
        );

        panel
            .add(titleLabel)
            .center()
            .row();

        Label valueLabel =
            new Label(
                value,
                game.skin,
                "font-title"
            );

        valueLabel.setFontScale(
            0.64f
        );

        valueLabel.setColor(
            valueColor
        );

        valueLabel.setAlignment(
            Align.center
        );

        panel
            .add(valueLabel)
            .center()
            .padTop(8f);

        return panel;
    }

    // =========================================================
    // RECEITAS
    // =========================================================

    private Table createRevenuePanel(
        ClubFinance finance
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Table header =
            new Table();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "RECEITAS MENSAIS"
            );

        header
            .add(title)
            .left()
            .expandX();

        Label total =
            ScreenUI.createBoldValue(
                game.skin,
                formatWFL(
                    finance.getTotalMonthlyRevenue()
                ),
                ScreenUI.SUCCESS,
                Align.right
            );

        header
            .add(total)
            .right();

        panel
            .add(header)
            .growX()
            .padBottom(12f)
            .row();

        addFinanceLine(
            panel,
            "Bilheteria",
            finance.getTicketRevenue(),
            ScreenUI.SUCCESS
        );

        addFinanceLine(
            panel,
            "Direitos de TV",
            finance.getTvRevenue(),
            ScreenUI.SUCCESS
        );

        addFinanceLine(
            panel,
            "Venda de camisas",
            finance.getShirtSalesRevenue(),
            ScreenUI.SUCCESS
        );

        addFinanceLine(
            panel,
            "Premiações",
            finance.getPrizeMoneyRevenue(),
            ScreenUI.SUCCESS
        );

        panel
            .add(
                ScreenUI.createDivider()
            )
            .growX()
            .height(1f)
            .colspan(2)
            .padTop(9f)
            .padBottom(9f)
            .row();

        addFinanceLine(
            panel,
            "TOTAL",
            finance.getTotalMonthlyRevenue(),
            ScreenUI.SUCCESS,
            true
        );

        return panel;
    }

    // =========================================================
    // DESPESAS
    // =========================================================

    private Table createExpensePanel(
        ClubFinance finance
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Table header =
            new Table();

        header
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "DESPESAS MENSAIS"
                )
            )
            .left()
            .expandX();

        header
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    formatWFL(
                        finance.getTotalMonthlyExpenses()
                    ),
                    ScreenUI.DANGER,
                    Align.right
                )
            )
            .right();

        panel
            .add(header)
            .growX()
            .padBottom(12f)
            .row();

        addFinanceLine(
            panel,
            "Salários",
            finance.getPlayerSalariesExpense(),
            ScreenUI.DANGER
        );

        addFinanceLine(
            panel,
            "Infraestrutura",
            finance.getInfrastructureExpense(),
            ScreenUI.DANGER
        );

        addFinanceLine(
            panel,
            "Departamento médico",
            finance.getMedicalExpense(),
            ScreenUI.DANGER
        );

        addFinanceLine(
            panel,
            "Scouting",
            finance.getScoutingExpense(),
            ScreenUI.DANGER
        );

        panel
            .add(
                ScreenUI.createDivider()
            )
            .growX()
            .height(1f)
            .colspan(2)
            .padTop(9f)
            .padBottom(9f)
            .row();

        addFinanceLine(
            panel,
            "TOTAL",
            finance.getTotalMonthlyExpenses(),
            ScreenUI.DANGER,
            true
        );

        return panel;
    }

    // =========================================================
    // SALARY CAP
    // =========================================================

    private Table createSalaryCapPanel(
        ClubFinance finance
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "SALARY CAP"
                )
            )
            .left()
            .padBottom(12f)
            .row();

        long cap =
            finance.getSalaryCap();

        long payroll =
            finance.getAnnualPayroll();

        long available =
            finance.getAvailableCapSpace();

        double percentage =
            cap > 0
                ? (
                payroll *
                    100.0 /
                    cap
            )
                : 0.0;

        Table capNumbers =
            new Table();

        capNumbers
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "CAP"
                )
            )
            .left()
            .expandX();

        capNumbers
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    formatWFL(
                        cap
                    ),
                    StyleFactory.SOFT_YELLOW,
                    Align.right
                )
            )
            .right()
            .row();

        capNumbers
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "FOLHA"
                )
            )
            .left()
            .expandX()
            .padTop(5f);

        capNumbers
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    formatWFL(
                        payroll
                    ),
                    Color.WHITE,
                    Align.right
                )
            )
            .right()
            .padTop(5f)
            .row();

        panel
            .add(capNumbers)
            .growX()
            .padBottom(10f)
            .row();

        Table percentageRow =
            new Table();

        Label used =
            new Label(
                String.format(
                    "%.1f%% utilizado",
                    percentage
                ),
                game.skin,
                "font-bold"
            );

        used.setFontScale(
            0.60f
        );

        Color usageColor =
            percentage >=
                100
                ? ScreenUI.DANGER
                : percentage >=
                90
                ? ScreenUI.WARNING
                : ScreenUI.SUCCESS;

        used.setColor(
            usageColor
        );

        percentageRow
            .add(used)
            .left()
            .expandX();

        percentageRow
            .add(
                ScreenUI.createBlockProgress(
                    game.skin,
                    percentage,
                    12,
                    usageColor
                )
            )
            .right();

        panel
            .add(percentageRow)
            .growX()
            .padBottom(8f)
            .row();

        Table space =
            ScreenUI.createStatusBox(
                game.skin,
                "ESPAÇO DISPONÍVEL",
                formatWFL(
                    available
                ),
                available >= 0
                    ? ScreenUI.SUCCESS
                    : ScreenUI.DANGER
            );

        panel
            .add(space)
            .growX()
            .height(42f);

        return panel;
    }

    // =========================================================
    // ECONOMIC POWER
    // =========================================================

    private Table createEconomicPowerPanel(
        ClubFinance finance
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "PODER ECONÔMICO"
                )
            )
            .left()
            .padBottom(10f)
            .row();

        panel
            .add(
                createStarsWidget()
            )
            .left()
            .padBottom(12f)
            .row();

        Table values =
            new Table();

        values
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "RECEITA ANUAL"
                )
            )
            .left()
            .expandX();

        values
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    compactWFL(
                        finance.getTotalAnnualRevenue()
                    ),
                    Color.WHITE,
                    Align.right
                )
            )
            .right()
            .row();

        values
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    "VALOR DO CLUBE"
                )
            )
            .left()
            .expandX()
            .padTop(7f);

        values
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    compactWFL(
                        finance.getClubValuation()
                    ),
                    StyleFactory.GOLD,
                    Align.right
                )
            )
            .right()
            .padTop(7f);

        panel
            .add(values)
            .growX();

        return panel;
    }

    // =========================================================
    // HEALTH
    // =========================================================

    private Table createHealthPanel(
        ClubFinance finance
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "SAÚDE FINANCEIRA"
                )
            )
            .left()
            .padBottom(12f)
            .row();

        FinancialHealthState health =
            finance.getHealthState();

        Table stateBox =
            ScreenUI.createStatusBox(
                game.skin,
                "SITUAÇÃO",
                health.getFormattedStatus(),
                health.getColor()
            );

        panel
            .add(stateBox)
            .growX()
            .height(44f)
            .padBottom(9f)
            .row();

        long monthlyNet =
            finance.getMonthlyNetResult();

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "FLUXO MENSAL",
                    (
                        monthlyNet >= 0
                            ? "+"
                            : "-"
                    ) +
                        formatWFL(
                            Math.abs(
                                monthlyNet
                            )
                        ),
                    monthlyNet >= 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(44f)
            .padBottom(9f)
            .row();

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "CAIXA",
                    compactWFL(
                        finance.getBalance()
                    ),
                    finance.getBalance() >=
                        0
                        ? StyleFactory.SOFT_YELLOW
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(44f);

        return panel;
    }

    // =========================================================
    // FINANCE ROW
    // =========================================================

    private void addFinanceLine(
        Table panel,
        String label,
        long amount,
        Color valueColor
    ) {

        addFinanceLine(
            panel,
            label,
            amount,
            valueColor,
            false
        );
    }

    private void addFinanceLine(
        Table panel,
        String label,
        long amount,
        Color valueColor,
        boolean bold
    ) {

        Label labelActor;

        if (
            bold
        ) {

            labelActor =
                new Label(
                    label,
                    game.skin,
                    "font-bold"
                );

        } else {

            labelActor =
                new Label(
                    label,
                    game.skin
                );
        }

        labelActor.setFontScale(
            bold
                ? 0.64f
                : 0.60f
        );

        labelActor.setColor(
            bold
                ? Color.WHITE
                : ScreenUI.MUTED_TEXT
        );

        panel
            .add(labelActor)
            .left()
            .expandX()
            .padBottom(8f);

        Label amountActor =
            new Label(
                formatWFL(
                    amount
                ),
                game.skin,
                "font-bold"
            );

        amountActor.setFontScale(
            bold
                ? 0.67f
                : 0.61f
        );

        amountActor.setColor(
            valueColor
        );

        panel
            .add(amountActor)
            .right()
            .padBottom(8f)
            .row();
    }

    // =========================================================
    // STARS
    // =========================================================

    private Table createStarsWidget() {

        Table stars =
            new Table();

        int rating =
            EconomicPower
                .getStarRating(
                    club
                );

        for (
            int i = 0;
            i < 5;
            i++
        ) {

            if (
                starTexture != null
            ) {

                Image star =
                    new Image(
                        new TextureRegionDrawable(
                            starTexture
                        )
                    );

                star.setScaling(
                    Scaling.fit
                );

                star.setColor(
                    i < rating
                        ? StyleFactory.GOLD
                        : new Color(
                        1f,
                        1f,
                        1f,
                        0.18f
                    )
                );

                stars
                    .add(star)
                    .size(24f)
                    .padRight(4f);

            } else {

                Label star =
                    new Label(
                        i < rating
                            ? "★"
                            : "☆",
                        game.skin,
                        "font-bold"
                    );

                star.setColor(
                    i < rating
                        ? StyleFactory.GOLD
                        : Color.DARK_GRAY
                );

                stars
                    .add(star)
                    .padRight(3f);
            }
        }

        return stars;
    }

    // =========================================================
    // FORMAT
    // =========================================================

    private String formatWFL(
        long amount
    ) {

        return "WFL$ " +
            String.format(
                "%,d",
                amount
            ).replace(
                ',',
                '.'
            );
    }

    private String compactWFL(
        long amount
    ) {

        long absolute =
            Math.abs(
                amount
            );

        String prefix =
            amount <
                0
                ? "-WFL$ "
                : "WFL$ ";

        if (
            absolute >=
                1_000_000_000L
        ) {

            return prefix +
                String.format(
                    "%.2fB",
                    absolute /
                        1_000_000_000f
                );
        }

        if (
            absolute >=
                1_000_000L
        ) {

            return prefix +
                String.format(
                    "%.2fM",
                    absolute /
                        1_000_000f
                );
        }

        if (
            absolute >=
                1_000L
        ) {

            return prefix +
                String.format(
                    "%.0fK",
                    absolute /
                        1_000f
                );
        }

        return prefix +
            absolute;
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

        if (
            starTexture != null
        ) {

            starTexture.dispose();
        }
    }
}
