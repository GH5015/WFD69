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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ContractRenewalService;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Central de renovação de contratos do clube controlado pelo usuário. */
public class ContractRenewalScreen implements Screen {

    private final Main game;
    private final Club club;
    private final Stage stage;
    private final Texture backgroundTexture;

    private Texture sliderBackgroundTexture;
    private Texture sliderKnobTexture;

    public ContractRenewalScreen(
        Main game,
        Club club
    ) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(
            Gdx.files.internal("prancheta.png")
        );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        ensureSliderTextures();
        refreshUI();
    }

    private void refreshUI() {
        stage.clear();

        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);

        Image background = new Image(
            new TextureRegionDrawable(backgroundTexture)
        );
        background.setFillParent(true);
        root.add(background);

        Table page = ScreenUI.createPage(true);
        int currentYear = game.league.getCurrentSeason();

        page.add(
            ScreenUI.createHeader(
                game.skin,
                "RENOVAÇÃO DE CONTRATOS",
                club.getName().toUpperCase() + " • " + currentYear + " • " +
                    (SeasonCalendar.isRenewalWindowOpen(game.league)
                        ? "RENOVAÇÕES ABERTAS"
                        : "PAUSA DOS PLAYOFFS")
            )
        ).growX().height(ScreenUI.HEADER_HEIGHT).padBottom(10f).row();

        page.add(createFinanceHeader()).growX().height(98f).padBottom(10f).row();
        page.add(createContractListPanel(currentYear)).grow().row();

        root.add(page);

        NavigationDrawer.attach(
            stage,
            game,
            club,
            "CONTRATOS",
            true
        );
    }

    private Table createFinanceHeader() {
        Table panel = ScreenUI.createPanel();
        long cap = club.getFinance().getSalaryCap();
        long payroll = club.getFinance().getAnnualPayroll();
        long space = cap - payroll;

        panel.add(ScreenUI.createStatusBox(
            game.skin,
            "SALARY CAP",
            formatMoney(cap),
            StyleFactory.SOFT_YELLOW
        )).growX().uniformX().padRight(8f);

        panel.add(ScreenUI.createStatusBox(
            game.skin,
            "FOLHA SALARIAL",
            formatMoney(payroll),
            payroll <= cap ? ScreenUI.SUCCESS : ScreenUI.DANGER
        )).growX().uniformX().padRight(8f);

        panel.add(ScreenUI.createStatusBox(
            game.skin,
            "ESPAÇO DISPONÍVEL",
            formatMoney(space),
            space >= 0 ? ScreenUI.SUCCESS : ScreenUI.DANGER
        )).growX().uniformX();

        return panel;
    }

    private Table createContractListPanel(int currentYear) {
        Table panel = ScreenUI.createPanel();
        panel.top();

        Table heading = new Table();
        heading.add(ScreenUI.createSectionTitle(
            game.skin,
            "CONTRATOS DO ELENCO"
        )).left().expandX();
        heading.add(ScreenUI.createSubtitle(
            game.skin,
            "Renove somente a partir de 2 anos restantes"
        )).right();
        panel.add(heading).growX().padBottom(9f).row();

        Table list = new Table();
        list.top();
        list.add(createListHeader()).growX().height(34f).padBottom(4f).row();

        List<Player> players = new ArrayList<>(club.getSquad());
        players.sort(Comparator
            .comparingInt((Player player) -> player.getRemainingContractYears(currentYear))
            .thenComparing(Comparator.comparingInt(Player::getOverall).reversed()));

        int index = 0;
        for (Player player : players) {
            list.add(createPlayerContractRow(player, currentYear, index++))
                .growX().height(54f).padBottom(3f).row();
        }

        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();

        return panel;
    }

    private Table createListHeader() {
        Table header = ScreenUI.createTableHeaderRow();
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "JOGADOR", Align.left))
            .width(310f).padLeft(10f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "ID.", Align.center)).width(48f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "OVR", Align.center)).width(55f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "SALÁRIO/ANO", Align.center)).width(140f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "CONTRATO", Align.center)).width(105f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "INTERESSE", Align.center)).width(115f);
        header.add(ScreenUI.createTableHeaderLabel(game.skin, "SITUAÇÃO", Align.center)).width(150f);
        header.add().expandX();
        return header;
    }

    private Table createPlayerContractRow(
        Player player,
        int currentYear,
        int index
    ) {
        Table row = ScreenUI.createRow(index);
        int remainingYears = player.getRemainingContractYears(currentYear);
        ContractRenewalService.Demand demand = ContractRenewalService.calculateDemand(player, club, currentYear);
        boolean canNegotiate = canNegotiate(player, currentYear);

        Table playerInfo = new Table();
        playerInfo.add(ScreenUI.createBadge(
            game.skin,
            player.getPosition(),
            StyleFactory.getPositionColor(player.getPosition())
        )).width(52f).height(24f).padRight(6f);
        playerInfo.add(ScreenUI.createBoldValue(
            game.skin,
            ScreenUI.shorten(player.getName(), 23),
            Color.WHITE,
            Align.left
        )).left().expandX();
        playerInfo.row();
        playerInfo.add().width(58f);
        playerInfo.add(ScreenUI.createSubtitle(
            game.skin,
            ContractRenewalService.getPerformanceSummary(player)
        )).left();

        row.add(playerInfo).width(310f).left().padLeft(7f);
        row.add(ScreenUI.createBoldValue(game.skin, String.valueOf(player.getAge()), StyleFactory.CREME_AGED, Align.center)).width(48f);
        row.add(ScreenUI.createBoldValue(game.skin, String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW, Align.center)).width(55f);
        row.add(ScreenUI.createBoldValue(game.skin, formatMoney(player.getAnnualSalary()), Color.WHITE, Align.center)).width(140f);
        row.add(ScreenUI.createBoldValue(game.skin, formatRemainingYears(remainingYears), getRemainingColor(remainingYears), Align.center)).width(105f);
        row.add(ScreenUI.createBoldValue(game.skin, formatStars(demand.interestStars), getInterestColor(demand.interestStars), Align.center)).width(115f);
        row.add(createSituationBadge(player, currentYear, canNegotiate)).width(150f).height(26f);

        TextButton negotiate = ScreenUI.createInteractiveButton(
            canNegotiate ? "NEGOCIAR" : "INDISPONÍVEL",
            game.skin
        );
        negotiate.getLabel().setFontScale(0.48f);
        negotiate.setDisabled(!canNegotiate);
        negotiate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (canNegotiate) {
                    showNegotiationDialog(player, demand.preferredYears, demand.desiredAnnualSalary);
                }
            }
        });
        row.add(negotiate).width(128f).height(34f).padRight(7f);
        return row;
    }

    private Table createSituationBadge(
        Player player,
        int currentYear,
        boolean canNegotiate
    ) {
        int remaining = player.getRemainingContractYears(currentYear);
        if (!SeasonCalendar.isRenewalWindowOpen(game.league)) {
            return ScreenUI.createBadge(game.skin, "PAUSA DOS PLAYOFFS", ScreenUI.DANGER);
        }
        if (remaining == 0) {
            return ScreenUI.createBadge(game.skin, "FREE AGENT", ScreenUI.DANGER);
        }
        if (!player.canNegotiateContract(currentYear)) {
            return ScreenUI.createBadge(game.skin, "RECÉM-RENOVADO", Color.valueOf("3A4140"));
        }
        if (remaining == 1) {
            return ScreenUI.createBadge(game.skin, "PRIORIDADE", ScreenUI.WARNING);
        }
        if (canNegotiate) {
            return ScreenUI.createBadge(game.skin, "DISPONÍVEL", ScreenUI.SUCCESS);
        }
        return ScreenUI.createBadge(game.skin, "CONTRATO SEGURO", Color.valueOf("3A4140"));
    }

    private boolean canNegotiate(Player player, int currentYear) {
        return SeasonCalendar.isRenewalWindowOpen(game.league)
            && player.getRemainingContractYears(currentYear) <= 2
            && player.canNegotiateContract(currentYear);
    }

    private void showNegotiationDialog(
        Player player,
        int defaultYears,
        long defaultAnnualSalary
    ) {
        int currentYear = game.league.getCurrentSeason();
        ContractRenewalService.Demand demand = ContractRenewalService.calculateDemand(player, club, currentYear);
        Dialog dialog = createDialog();
        Table content = dialog.getContentTable();

        content.add(createNegotiationHeader(player, demand, currentYear)).width(800f).height(190f).padBottom(10f).row();

        final int[] years = {Math.max(1, Math.min(5, defaultYears))};
        final TextButton[] yearButtons = new TextButton[5];
        final Runnable[] proposalUpdater = new Runnable[1];
        Table yearSelection = new Table();
        yearSelection.add(ScreenUI.createSubtitle(game.skin, "DURAÇÃO DO NOVO CONTRATO")).colspan(5).center().padBottom(5f).row();
        for (int year = 1; year <= 5; year++) {
            final int selectedYear = year;
            TextButton button = ScreenUI.createInteractiveButton(year + " ANO" + (year > 1 ? "S" : ""), game.skin);
            button.getLabel().setFontScale(0.50f);
            button.setColor(year == years[0] ? StyleFactory.GOLD : Color.WHITE);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    years[0] = selectedYear;
                    for (int index = 0; index < yearButtons.length; index++) {
                        yearButtons[index].setColor(index + 1 == years[0] ? StyleFactory.GOLD : Color.WHITE);
                    }
                    if (proposalUpdater[0] != null) {
                        proposalUpdater[0].run();
                    }
                }
            });
            yearButtons[year - 1] = button;
            yearSelection.add(button).width(118f).height(36f).padRight(year < 5 ? 4f : 0f);
        }
        content.add(yearSelection).width(800f).padBottom(11f).row();

        long sliderMin = Math.max(12_000L, demand.minimumAnnualSalary * 80 / 100);
        long sliderMax = Math.max(demand.maximumComfortableAnnualSalary * 125 / 100, sliderMin + 100_000L);
        Slider salarySlider = new Slider(sliderMin / 1_000f, sliderMax / 1_000f, 1f, false, createSliderStyle());
        salarySlider.setValue(Math.max(sliderMin / 1_000f, Math.min(sliderMax / 1_000f, defaultAnnualSalary / 1_000f)));

        Label offeredSalary = ScreenUI.createBoldValue(game.skin, "", StyleFactory.PLAYOFF_GOLD, Align.center);
        offeredSalary.setFontScale(0.76f);
        Label capImpact = ScreenUI.createSubtitle(game.skin, "");
        capImpact.setAlignment(Align.center);
        Label chance = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.center);
        Label chanceBar = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.center);
        chanceBar.setFontScale(0.46f);
        Label chanceDescription = ScreenUI.createSubtitle(game.skin, "");
        chanceDescription.setAlignment(Align.center);

        Runnable updateProposal = () -> {
            long offered = Math.round(salarySlider.getValue()) * 1_000L;
            long projected = club.getFinance().getAnnualPayroll() - player.getAnnualSalary() + offered;
            long capSpace = club.getFinance().getSalaryCap() - projected;
            int probability = ContractRenewalService.estimateAcceptanceChance(player, club, currentYear, offered, years[0]);
            offeredSalary.setText(formatMoney(offered) + " / ano");
            capImpact.setText("Salary Cap: " + formatMoney(club.getFinance().getAnnualPayroll()) + " → " + formatMoney(projected) + "  •  espaço: " + formatMoney(capSpace));
            capImpact.setColor(capSpace >= 0 ? ScreenUI.SUCCESS : ScreenUI.DANGER);
            chance.setText("PROBABILIDADE DE ACEITAÇÃO  " + probability + "%");
            chance.setColor(getProbabilityColor(probability));
            chanceBar.setText(createProbabilityBar(probability));
            chanceBar.setColor(getProbabilityColor(probability));
            chanceDescription.setText(getProbabilityDescription(probability));
        };

        salarySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateProposal.run();
            }
        });

        Table salaryPanel = ScreenUI.createSubtlePanel();
        salaryPanel.add(ScreenUI.createSubtitle(game.skin, "SALÁRIO ANUAL OFERECIDO")).center().padBottom(3f).row();
        salaryPanel.add(offeredSalary).center().padBottom(7f).row();
        salaryPanel.add(salarySlider).width(620f).height(26f).center().padBottom(5f).row();
        salaryPanel.add(capImpact).center().padBottom(8f).row();
        salaryPanel.add(chance).center().row();
        salaryPanel.add(chanceBar).center().padBottom(2f).row();
        salaryPanel.add(chanceDescription).center();
        content.add(salaryPanel).width(800f).height(172f).padBottom(8f).row();
        proposalUpdater[0] = updateProposal;
        updateProposal.run();

        TextButton send = ScreenUI.createPrimaryButton(game.skin, "ENVIAR PROPOSTA");
        send.getLabel().setFontScale(0.57f);
        send.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                long offered = Math.round(salarySlider.getValue()) * 1_000L;
                ContractRenewalService.Decision decision = ContractRenewalService.evaluateProposal(player, club, currentYear, offered, years[0]);
                dialog.hide();
                showDecisionDialog(player, decision, offered, years[0]);
            }
        });
        dialog.button(send, true);
        dialog.show(stage);
    }

    private Table createNegotiationHeader(
        Player player,
        ContractRenewalService.Demand demand,
        int currentYear
    ) {
        Table panel = ScreenUI.createSubtlePanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "RENOVAR CONTRATO")).colspan(3).center().padBottom(8f).row();
        panel.add(ScreenUI.createBoldValue(game.skin, player.getName(), Color.WHITE, Align.center)).colspan(3).center().padBottom(5f).row();
        panel.add(ScreenUI.createSubtitle(game.skin, player.getPosition() + " • " + player.getAge() + " anos • OVR " + player.getOverall())).colspan(3).center().padBottom(10f).row();
        panel.add(createDemandValue("ATUAL", formatMoney(player.getAnnualSalary()))).growX().uniformX().padRight(5f);
        panel.add(createDemandValue("MÍNIMO", formatMoney(demand.minimumAnnualSalary))).growX().uniformX().padRight(5f);
        panel.add(createDemandValue("DESEJADO", formatMoney(demand.desiredAnnualSalary))).growX().uniformX().row();
        panel.add(ScreenUI.createSubtitle(game.skin, "Interesse " + formatStars(demand.interestStars) + " • " + demand.performanceSummary + " • " + player.getRemainingContractYears(currentYear) + " ano(s) restante(s)")).colspan(3).center().padTop(8f);
        return panel;
    }

    private Table createDemandValue(String title, String value) {
        Table valueBox = new Table();
        valueBox.add(ScreenUI.createSubtitle(game.skin, title)).center().row();
        valueBox.add(ScreenUI.createBoldValue(game.skin, value, StyleFactory.SOFT_YELLOW, Align.center)).center();
        return valueBox;
    }

    private void showDecisionDialog(
        Player player,
        ContractRenewalService.Decision decision,
        long offeredAnnualSalary,
        int offeredYears
    ) {
        Dialog dialog = createDialog();
        Table content = dialog.getContentTable();
        Color color = getOutcomeColor(decision.outcome);
        String title = getOutcomeTitle(decision.outcome);

        content.add(ScreenUI.createSectionTitle(game.skin, title)).width(620f).center().padBottom(10f).row();
        Label message = ScreenUI.createValueLabel(game.skin, decision.message, Color.WHITE, Align.center);
        message.setWrap(true);
        content.add(message).width(590f).padBottom(12f).row();

        if (decision.outcome == ContractRenewalService.Outcome.COUNTER_OFFER) {
            Table counter = ScreenUI.createSubtlePanel();
            counter.add(ScreenUI.createSubtitle(game.skin, player.getName() + " deseja:")).colspan(2).center().padBottom(6f).row();
            counter.add(ScreenUI.createBoldValue(game.skin, decision.counterYears + " anos", Color.WHITE, Align.center)).width(230f);
            counter.add(ScreenUI.createBoldValue(game.skin, formatMoney(decision.counterAnnualSalary) + " / ano", StyleFactory.PLAYOFF_GOLD, Align.center)).width(230f).row();
            content.add(counter).width(520f).height(88f).padBottom(9f).row();

            TextButton accept = ScreenUI.createPrimaryButton(game.skin, "ACEITAR");
            accept.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    renewPlayer(player, decision.counterAnnualSalary, decision.counterYears);
                    dialog.hide();
                }
            });
            TextButton negotiate = ScreenUI.createInteractiveButton("NEGOCIAR", game.skin);
            negotiate.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialog.hide();
                    showNegotiationDialog(player, decision.counterYears, decision.counterAnnualSalary);
                }
            });
            TextButton decline = ScreenUI.createInteractiveButton("RECUSAR", game.skin);
            decline.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialog.hide();
                    refreshUI();
                }
            });
            content.add(accept).width(155f).height(40f).padRight(6f);
            content.add(negotiate).width(155f).height(40f).padRight(6f);
            content.add(decline).width(155f).height(40f).row();
        } else if (decision.outcome == ContractRenewalService.Outcome.ACCEPTED) {
            TextButton confirm = ScreenUI.createPrimaryButton(game.skin, "CONFIRMAR RENOVAÇÃO");
            confirm.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    renewPlayer(player, offeredAnnualSalary, offeredYears);
                    dialog.hide();
                }
            });
            content.add(confirm).width(280f).height(42f).row();
        } else {
            TextButton close = ScreenUI.createInteractiveButton("VOLTAR", game.skin);
            close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialog.hide();
                    refreshUI();
                }
            });
            content.add(close).width(150f).height(40f).row();
        }

        content.getColor().set(color);
        dialog.show(stage);
    }

    private void renewPlayer(Player player, long annualSalary, int years) {
        if (!SeasonCalendar.isRenewalWindowOpen(game.league)) {
            refreshUI();
            return;
        }
        player.renewContract(annualSalary, years, game.league.getCurrentSeason());
        refreshUI();
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.getContentTable().background(StyleFactory.createMetallicBoard(860, 690, Color.valueOf("141A16")));
        dialog.getContentTable().pad(18f, 24f, 14f, 24f);
        return dialog;
    }

    private void ensureSliderTextures() {
        if (sliderBackgroundTexture == null) {
            Pixmap background = new Pixmap(120, 6, Pixmap.Format.RGBA8888);
            background.setColor(Color.valueOf("4B504C"));
            background.fill();
            sliderBackgroundTexture = new Texture(background);
            background.dispose();
        }
        if (sliderKnobTexture == null) {
            Pixmap knob = new Pixmap(18, 18, Pixmap.Format.RGBA8888);
            knob.setColor(StyleFactory.GOLD);
            knob.fillCircle(9, 9, 9);
            sliderKnobTexture = new Texture(knob);
            knob.dispose();
        }
    }

    private Slider.SliderStyle createSliderStyle() {
        return new Slider.SliderStyle(
            new TextureRegionDrawable(new TextureRegion(sliderBackgroundTexture)),
            new TextureRegionDrawable(new TextureRegion(sliderKnobTexture))
        );
    }

    private String formatRemainingYears(int years) {
        if (years == 0) return "0 • EXPIRADO";
        return years + " ano" + (years > 1 ? "s" : "");
    }

    private Color getRemainingColor(int years) {
        if (years == 0) return ScreenUI.DANGER;
        if (years == 1) return ScreenUI.WARNING;
        return ScreenUI.SUCCESS;
    }

    private String formatStars(int stars) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 5; index++) builder.append(index < stars ? "★" : "☆");
        return builder.toString();
    }

    private Color getInterestColor(int stars) {
        return stars >= 4 ? ScreenUI.SUCCESS : stars >= 3 ? StyleFactory.SOFT_YELLOW : ScreenUI.DANGER;
    }

    private Color getProbabilityColor(int probability) {
        return probability >= 70 ? ScreenUI.SUCCESS : probability >= 45 ? ScreenUI.WARNING : ScreenUI.DANGER;
    }

    private String getProbabilityDescription(int probability) {
        if (probability >= 75) return "Muito provável • estimativa do staff";
        if (probability >= 50) return "Negociação equilibrada • estimativa do staff";
        return "Baixa probabilidade • estimativa do staff";
    }

    private String createProbabilityBar(int probability) {
        int filled = Math.max(0, Math.min(20, Math.round(probability / 5f)));
        StringBuilder bar = new StringBuilder();
        for (int index = 0; index < 20; index++) {
            bar.append(index < filled ? "█" : "░");
        }
        return bar.toString();
    }

    private Color getOutcomeColor(ContractRenewalService.Outcome outcome) {
        if (outcome == ContractRenewalService.Outcome.ACCEPTED) return ScreenUI.SUCCESS;
        if (outcome == ContractRenewalService.Outcome.COUNTER_OFFER) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private String getOutcomeTitle(ContractRenewalService.Outcome outcome) {
        if (outcome == ContractRenewalService.Outcome.ACCEPTED) return "RENOVAÇÃO ACEITA";
        if (outcome == ContractRenewalService.Outcome.COUNTER_OFFER) return "CONTRAOFERTA";
        if (outcome == ContractRenewalService.Outcome.FREE_AGENCY) return "PREFERÊNCIA PELA FREE AGENCY";
        return "PROPOSTA RECUSADA";
    }

    private String formatMoney(long amount) {
        String sign = amount < 0 ? "-" : "";
        long absolute = Math.abs(amount);
        if (absolute >= 1_000_000L) {
            return sign + String.format(Locale.US, "WFL$ %.2fM", absolute / 1_000_000.0);
        }
        return sign + String.format(Locale.US, "WFL$ %,d", absolute);
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        if (sliderBackgroundTexture != null) sliderBackgroundTexture.dispose();
        if (sliderKnobTexture != null) sliderKnobTexture.dispose();
    }
}
