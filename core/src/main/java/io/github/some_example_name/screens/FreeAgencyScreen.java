package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.FreeAgencyService;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.DayAdvanceTransition;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Mercado de agentes livres, ofertas pendentes e impacto no teto salarial. */
public class FreeAgencyScreen implements Screen {
    private final Main game;
    private final Club club;
    private final Stage stage;
    private String tab = "JOGADORES";
    private String filter = "TODOS";
    private String sort = "OVR";
    private boolean sortAscending;
    private String search = "";
    private Player selected;

    public FreeAgencyScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        refreshUI();
        if (!IncomingTradeOfferDialog.showPending(stage, game, club)) {
            FreeAgencyDecisionDialog.showPending(stage, game);
        }
    }

    private void refreshUI() {
        io.github.some_example_name.utils.ScrollPositionMemory.capture(stage, getClass().getName());
        if (selected == null || !game.freeAgencyService.getFreeAgents().contains(selected)) {
            selected = game.freeAgencyService.getFreeAgents().isEmpty() ? null : game.freeAgencyService.getFreeAgents().get(0);
        }

        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        root.add(new Image(game.background));

        Table page = ScreenUI.createPage(true);
        boolean marketOpen = SeasonCalendar.isFreeAgentSigningOpen(game.league);
        Table headerLine = new Table();
        headerLine.add(ScreenUI.createHeader(game.skin, "FREE AGENCY", "AGENTES LIVRES DISPONÍVEIS • WFL " + game.league.getCurrentSeason())).left().expandX();
        page.add(headerLine).growX().height(70f).padBottom(6f).row();
        page.add(createCompactFinanceStrip()).growX().height(58f).padBottom(8f).row();
        if (marketOpen || "HISTÓRICO".equals(tab)) {
            page.add(createToolbar()).growX().height(82f).padBottom(8f).row();
            page.add("HISTÓRICO".equals(tab) ? createSigningHistory() : createMarketBody()).grow().row();
        } else {
            page.add(createToolbar()).growX().height(82f).padBottom(8f).row();
            page.add(createMarketClosedPanel()).grow();
        }
        root.add(page);

        Table actionOverlay = new Table();
        actionOverlay.setFillParent(true);
        actionOverlay.bottom().pad(18f);
        actionOverlay.add().expandX();
        TextButton advance = ScreenUI.createPrimaryButton(game.skin, "AVANÇAR DIA");
        advance.getLabel().setFontScale(.52f);
        advance.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                DayAdvanceTransition.play(stage, game, 1, new Runnable() {
                    @Override public void run() {
                        Screen screenBefore = game.getScreen();
                        CareerOverlay.advanceOneDay(game, club);
                        if (screenBefore != game.getScreen()) return;

                        tab = "JOGADORES";
                        refreshUI();
                        if (!IncomingTradeOfferDialog.showPending(stage, game, club)
                            && !FreeAgencyDecisionDialog.showPending(stage, game)) {
                            WflNewsDialog.showPending(stage, game);
                        }
                    }
                });
            }
        });
        actionOverlay.add(advance).width(220f).height(44f).right();
        root.add(actionOverlay);

        NavigationDrawer.attach(stage, game, club, "AGENTES", true);
        io.github.some_example_name.utils.ScrollPositionMemory.restore(stage, getClass().getName());
    }

    private Table createSalaryCapPanel() {
        long cap = club.getFinance().getSalaryCap();
        long taxLine = club.getFinance().getLuxuryTaxThreshold();
        long hardCap = club.getFinance().getHardCap();
        long payroll = club.getFinance().getAnnualPayroll();
        String payrollStatus = club.getFinance().getPayrollStatus(payroll);
        Color payrollColor = payroll > hardCap ? ScreenUI.DANGER
            : payroll > taxLine ? ScreenUI.WARNING
            : payroll > cap ? StyleFactory.SOFT_YELLOW : ScreenUI.SUCCESS;
        Table panel = ScreenUI.createPanel();
        Table title = new Table();
        title.add(ScreenUI.createSectionTitle(game.skin, "SALARY CAP")).left().row();
        title.add(ScreenUI.createSubtitle(game.skin, "Soft Cap flexível • Tax pesada • Hard Cap absoluto")).left();
        panel.add(title).left().width(225f).padRight(8f);
        panel.add(ScreenUI.createBlockProgress(game.skin, hardCap <= 0 ? 0 : payroll * 100.0 / hardCap, 16, payrollColor)).left().width(145f).padRight(8f);
        panel.add(status("SALARY CAP", money(cap), StyleFactory.SOFT_YELLOW)).width(165f).padRight(5f);
        panel.add(status("LUXURY TAX", money(taxLine), ScreenUI.WARNING)).width(165f).padRight(5f);
        panel.add(status("HARD CAP", money(hardCap), ScreenUI.DANGER)).width(165f).padRight(5f);
        panel.add(status("FOLHA ATUAL", money(payroll), payrollColor)).width(165f).padRight(5f);
        panel.add(status("SITUAÇÃO", payrollStatus, payrollColor)).width(205f);
        return panel;
    }

    /** Faixa curta para a central de mercado; detalhes completos continuam em Finanças. */
    private Table createCompactFinanceStrip() {
        long payroll = club.getFinance().getAnnualPayroll();
        long cap = club.getFinance().getSalaryCap();
        long hardCap = club.getFinance().getHardCap();
        long room = Math.max(0L, hardCap - payroll);
        Color color = payroll > hardCap ? ScreenUI.DANGER : payroll > cap ? ScreenUI.WARNING : ScreenUI.SUCCESS;
        Table strip = ScreenUI.createSubtlePanel();
        strip.add(status("SALARY CAP", money(cap), StyleFactory.SOFT_YELLOW)).width(180f).padRight(8f);
        strip.add(status("FOLHA ATUAL", money(payroll), color)).width(180f).padRight(8f);
        strip.add(status("ESPAÇO NO HARD CAP", money(room), ScreenUI.SUCCESS)).width(195f).padRight(10f);
        strip.add(ScreenUI.createBlockProgress(game.skin, hardCap == 0 ? 0 : payroll * 100d / hardCap, 14, color)).growX().height(12f);
        return strip;
    }

    private int pendingOfferCount() {
        int count = 0;
        for (FreeAgencyService.Offer offer : game.freeAgencyService.getUserOffers())
            if (offer.getStatus() == FreeAgencyService.OfferStatus.PENDING) count++;
        return count;
    }

    private Table createMarketClosedPanel() {
        Table panel = ScreenUI.createPanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "MERCADO FECHADO")).center().padBottom(10f).row();
        Label message = ScreenUI.createValueLabel(
            game.skin,
            "O mercado principal abre em 6 de novembro e fecha em 31 de dezembro.\nApós isso, agentes livres remanescentes podem ser contratados durante a temporada, até os playoffs.",
            Color.WHITE,
            Align.center
        );
        message.setWrap(true);
        panel.add(message).width(620f).center().row();
        panel.add(ScreenUI.createSubtitle(game.skin, "Status atual: " + SeasonCalendar.getFreeAgencyStatus(game.league))).center().padTop(14f);
        return panel;
    }

    private Table status(String title, String value, Color color) {
        return ScreenUI.createStatusBox(game.skin, title, value, color);
    }

    private Table createToolbar() {
        Table panel = ScreenUI.createPanel();
        Table tabs = new Table();
        tabs.add(tabButton("JOGADORES", "AGENTES LIVRES")).width(145f).height(34f).padRight(5f);
        tabs.add(targetsButton()).width(130f).height(34f).padRight(5f);
        tabs.add(tabButton("HISTÓRICO", "HISTÓRICO")).width(130f).height(34f);
        panel.add(tabs).left().padBottom(5f).row();

        if ("JOGADORES".equals(tab)) {
            Table controls = new Table();
            String[] filters = { "TODOS", "GK", "DEF", "MEI", "ATA", "★" };
            for (String option : filters) {
                controls.add(filterButton(option)).width("TODOS".equals(option) ? 94f : 72f).height(30f).padRight(4f);
            }
            controls.add(ScreenUI.createSubtitle(game.skin, "BUSCAR")).padLeft(8f).padRight(4f);
            final TextField field = new TextField(search, game.skin);
            field.setMessageText("Nome do jogador");
            field.setTextFieldFilter(new TextField.TextFieldFilter() {
                @Override public boolean acceptChar(TextField textField, char c) { return !Character.isISOControl(c); }
            });
            controls.add(field).width(165f).height(32f).padRight(4f);
            TextButton searchButton = ScreenUI.createInteractiveButton("BUSCAR", game.skin, "toggle");
            searchButton.getLabel().setFontScale(0.42f);
            searchButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    search = field.getText();
                    refreshUI();
                }
            });
            controls.add(searchButton).width(65f).height(30f).padRight(10f);
            controls.add(ScreenUI.createSubtitle(game.skin, "ORDENAR")).padRight(4f);
            SelectBox<String> sortBox = ScreenUI.createSelectBox(game.skin);
            sortBox.setItems("OVR", "POTENCIAL", "IDADE", "SALÁRIO", "INTERESSE");
            sortBox.setSelected(sort);
            sortBox.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    sort = sortBox.getSelected();
                    sortAscending = "IDADE".equals(sort);
                    refreshUI();
                }
            });
            controls.add(sortBox).width(135f).height(32f);
            panel.add(controls).left().padTop(8f);
        } else {
            panel.add(ScreenUI.createSubtitle(game.skin, "Contratações concluídas por todos os clubes • Temporada "
                + game.league.getCurrentSeason())).left().padTop(10f);
        }
        return panel;
    }

    private Table createSigningHistory() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        List<io.github.some_example_name.model.FreeAgencySigning> history =
            game.league.getFreeAgencyHistory(game.league.getCurrentSeason());
        panel.add(ScreenUI.createSectionTitle(game.skin, "CONTRATAÇÕES DA TEMPORADA (" + history.size() + ")"))
            .growX().left().padBottom(12f).row();
        Table list = new Table();
        list.top();
        Table header = new Table();
        String[] headings = {"DATA", "JOGADOR", "CLUBE", "POS", "OVR", "SALÁRIO ANUAL", "DURAÇÃO"};
        float[] widths = {105f, 290f, 320f, 65f, 65f, 170f, 110f};
        for (int i = 0; i < headings.length; i++)
            header.add(value(headings[i], StyleFactory.SOFT_YELLOW, Align.left)).width(widths[i]);
        list.add(header).growX().height(38f).row();
        java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < history.size(); i++) {
            io.github.some_example_name.model.FreeAgencySigning signing = history.get(i);
            Table row = ScreenUI.createRow(i);
            String[] values = {signing.date == 0 ? "—" : date.format(new java.util.Date(signing.date)),
                signing.playerName, signing.clubName, signing.position, String.valueOf(signing.overall),
                money(signing.annualSalary), getYearsText(signing.years)};
            for (int j = 0; j < values.length; j++) {
                Label cell = value(values[j], j == 5 ? ScreenUI.SUCCESS : Color.WHITE, Align.left);
                cell.setEllipsis(true);
                row.add(cell).width(widths[j]);
            }
            list.add(row).growX().height(46f).row();
        }
        if (history.isEmpty()) list.add(ScreenUI.createSubtitle(game.skin,
            "Nenhuma contratação registrada nesta temporada.")).pad(24f).row();
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private TextButton tabButton(final String value, String label) {
        TextButton button = ScreenUI.createInteractiveButton(label, game.skin, "toggle");
        boolean active = value.equals(tab);
        button.setChecked(active);
        button.getLabel().setFontScale(0.48f);
        button.setColor(Color.WHITE);
        button.getLabel().setColor(active ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_PRIMARY);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                tab = value;
                refreshUI();
            }
        });
        return button;
    }

    private TextButton targetsButton() {
        TextButton button = ScreenUI.createInteractiveButton("MEUS ALVOS", game.skin, "toggle");
        boolean active = "JOGADORES".equals(tab) && "★".equals(filter);
        button.setChecked(active); button.getLabel().setFontScale(.44f);
        button.getLabel().setColor(active ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_PRIMARY);
        button.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) {
            tab = "JOGADORES"; filter = "★"; refreshUI();
        }});
        return button;
    }

    private TextButton filterButton(final String value) {
        TextButton button = ScreenUI.createInteractiveButton(value, game.skin, "toggle");
        boolean active = value.equals(filter);
        button.setChecked(active);
        button.getLabel().setFontScale(0.44f);
        button.setColor(Color.WHITE);
        button.getLabel().setColor(active ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_PRIMARY);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                filter = value;
                refreshUI();
            }
        });
        return button;
    }

    private Table createMarketBody() {
        Table body = new Table();
        float usable = Math.max(900f, stage.getWidth() - ScreenUI.PAGE_LEFT_OPEN - ScreenUI.PAGE_RIGHT);
        float detailsWidth = Math.min(410f, usable * .30f);
        body.add(createMarketTable()).width(usable - detailsWidth - 10f).growY().padRight(10f);
        body.add(createPlayerDetails()).width(detailsWidth).growY();
        return body;
    }

    private Table createMarketTable() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table list = new Table();
        Table header = ScreenUI.createTableHeaderRow();
        header.add(head("#", Align.center)).width(34f);
        header.add(head("JOGADOR", Align.left)).width(180f);
        header.add(head("POS", Align.center)).width(52f);
        header.add(head("IDADE", Align.center)).width(48f);
        header.add(head("OVR", Align.center)).width(48f);
        header.add(head("POT", Align.center)).width(48f);
        header.add(head("NACIONALIDADE", Align.left)).width(105f);
        header.add(head("PEDIDO SALARIAL", Align.center)).width(125f);
        header.add(head("INTERESSE", Align.center)).width(98f);
        list.add(header).growX().height(38f).row();

        List<Player> players = filteredPlayers();
        int index = 0;
        for (Player player : players) {
            list.add(createPlayerRow(player, index++)).growX().height(49f).row();
        }
        if (players.isEmpty()) {
            Label empty = ScreenUI.createSubtitle(game.skin, "Nenhum agente livre corresponde aos filtros atuais.");
            empty.setAlignment(Align.center);
            list.add(empty).growX().height(70f);
        }
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private Table createPlayerRow(final Player player, int index) {
        Table row = ScreenUI.createRow(index);
        TextButton favourite = ScreenUI.createInteractiveButton(game.freeAgencyService.isFavourite(player) ? "★" : String.valueOf(index + 1), game.skin, "toggle");
        favourite.getLabel().setFontScale(0.62f);
        favourite.getLabel().setColor(game.freeAgencyService.isFavourite(player) ? StyleFactory.SOFT_YELLOW : ScreenUI.MUTED_TEXT);
        favourite.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.freeAgencyService.toggleFavourite(player);
                refreshUI();
            }
        });
        row.add(favourite).width(34f).height(29f);
        row.add(value(ScreenUI.shorten(player.getName(), 20), Color.WHITE, Align.left)).width(180f).padLeft(5f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(52f).height(25f);
        row.add(value(String.valueOf(player.getAge()), Color.WHITE, Align.center)).width(48f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW, Align.center)).width(48f);
        row.add(value(io.github.some_example_name.model.PlayerPotentialDisplay.forViewer(player, club), ScreenUI.SUCCESS, Align.center)).width(48f);
        row.add(value(ScreenUI.shorten(player.getNationality(), 13), ScreenUI.MUTED_TEXT, Align.left)).width(105f);
        row.add(value(money(game.freeAgencyService.getRequestedAnnualSalary(player)) + "/ano", Color.WHITE, Align.center)).width(125f);
        row.add(createInterestMeter(player)).width(98f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selected = player;
                refreshUI();
            }
        });
        return row;
    }

    private Table createPlayerDetails() {
        Table panel = ScreenUI.createPanel();
        panel.top().left();
        if (selected == null) {
            panel.add(ScreenUI.createSubtitle(game.skin, "Selecione um jogador para ver a proposta.")).expand().center();
            return panel;
        }
        Player player = selected;
        Table title = new Table();
        title.add(ScreenUI.createSectionTitle(game.skin, "ALVO PRINCIPAL")).left().expandX();
        TextButton favourite = ScreenUI.createInteractiveButton(game.freeAgencyService.isFavourite(player) ? "★ FAVORITO" : "☆ FAVORITAR", game.skin, "toggle");
        favourite.getLabel().setFontScale(0.43f);
        favourite.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.freeAgencyService.toggleFavourite(player);
                refreshUI();
            }
        });
        title.add(favourite).width(120f).height(32f);
        panel.add(title).growX().row();

        Label name = ScreenUI.createBoldValue(game.skin, player.getName().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.left);
        name.setFontScale(0.74f);
        panel.add(name).left().padTop(10f).row();
        panel.add(ScreenUI.createSubtitle(game.skin, player.getPosition() + " • " + player.getAge() + " anos • " + player.getNationality())).left().padTop(2f).row();
        panel.add(ScreenUI.createSubtitle(game.skin, "Agente livre • negociações podem alterar a pedida")).left().padTop(3f).row();

        Table ratings = new Table();
        ratings.add(status("OVR", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).growX().padRight(6f);
        ratings.add(status("POTENCIAL", io.github.some_example_name.model.PlayerPotentialDisplay.forViewer(player, club), ScreenUI.SUCCESS)).growX();
        panel.add(ratings).growX().padTop(12f).row();

        TechnicalAttributes attrs = player.getTechnicalAttributes();
        Table attributes = new Table();
        attributes.add(attribute("ATA", attrs.getAtaque())).growX();
        attributes.add(attribute("PAS", attrs.getPasse())).growX();
        attributes.add(attribute("DEF", attrs.getDefesa())).growX();
        attributes.add(attribute("FIS", attrs.getFisico())).growX();
        attributes.add(attribute("DRI", attrs.getDrible())).growX();
        panel.add(attributes).growX().padTop(12f).row();

        long demand = game.freeAgencyService.getRequestedAnnualSalary(player);
        int interest = game.freeAgencyService.getInterestStars(player, club);
        Table contract = ScreenUI.createSubtlePanel();
        contract.add(label("PEDIDO SALARIAL", money(demand) + " / ano", Color.WHITE)).left().expandX().row();
        contract.add(label("PREFERÊNCIA", getYearsText(game.freeAgencyService.getPreferredYears(player)), ScreenUI.MUTED_TEXT)).left().padTop(5f).row();
        contract.add(label("INTERESSE", interestText(interest), interestColor(player))).left().padTop(5f).row();
        contract.add(createInterestMeter(player)).left().padTop(5f).row();
        contract.add(label("CHANCE DE TITULARIDADE", game.freeAgencyService.getStarterChance(player, club) + "%", ScreenUI.SUCCESS)).left().padTop(5f);
        panel.add(contract).growX().padTop(12f).row();

        FreeAgencyService.Offer pending = pendingOffer(player);
        if (pending != null) {
            Label status = label("PROPOSTA " + pending.getStatus().getLabel(), pending.getDecisionMessage(), statusColor(pending.getStatus()));
            status.setWrap(true);
            panel.add(status).growX().padTop(14f).row();
        } else {
            TextButton makeOffer = ScreenUI.createInteractiveButton("FAZER PROPOSTA", game.skin);
            makeOffer.getLabel().setFontScale(0.55f);
            makeOffer.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    showOfferDialog(player);
                }
            });
            panel.add(makeOffer).growX().height(44f).padTop(16f);
        }
        return panel;
    }

    private Table createOffersPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table table = new Table();
        Table header = ScreenUI.createTableHeaderRow();
        header.add(head("JOGADOR", Align.left)).width(290f);
        header.add(head("OFERTA", Align.center)).width(160f);
        header.add(head("DURAÇÃO", Align.center)).width(130f);
        header.add(head("INTERESSE", Align.center)).width(160f);
        header.add(head("CONCORRÊNCIA", Align.center)).width(170f);
        header.add(head("SITUAÇÃO", Align.center)).width(180f);
        table.add(header).growX().height(40f).row();
        int index = 0;
        for (FreeAgencyService.Offer offer : game.freeAgencyService.getUserOffers()) {
            Table row = ScreenUI.createRow(index++);
            row.add(value(offer.getPlayer().getName(), Color.WHITE, Align.left)).width(290f).padLeft(10f);
            row.add(value(money(offer.getAnnualSalary()), Color.WHITE, Align.center)).width(160f);
            row.add(value(getYearsText(offer.getYears()), Color.WHITE, Align.center)).width(130f);
            row.add(value(stars(offer.getInterestStars()), interestColor(offer.getPlayer()), Align.center)).width(160f);
            row.add(value(offer.getCompetingOffers() + " clubes", ScreenUI.MUTED_TEXT, Align.center)).width(170f);
            row.add(value(offer.getStatus().getLabel(), statusColor(offer.getStatus()), Align.center)).width(180f);
            final String message = offer.getDecisionMessage();
            row.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    showMessage("PROPOSTA", message);
                }
            });
            table.add(row).growX().height(52f).row();
        }
        if (index == 0) {
            Label empty = ScreenUI.createSubtitle(game.skin, "Você ainda não enviou propostas. Escolha um agente livre na aba JOGADORES.");
            empty.setAlignment(Align.center);
            table.add(empty).growX().height(90f);
        }
        ScrollPane scroll = new ScrollPane(table, game.skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }

    private void showOfferDialog(final Player player) {
        final long requested = game.freeAgencyService.getRequestedAnnualSalary(player);
        final SelectBox<SalaryOption> salary = ScreenUI.createSelectBox(game.skin);
        SalaryOption[] options = salaryOptions(requested);
        salary.setItems(options);
        salary.setSelected(options[4]);
        FreeAgencyService.Offer previous = game.freeAgencyService.findOffer(player);
        if (previous != null && previous.getStatus() == FreeAgencyService.OfferStatus.COUNTER_OFFER) {
            SalaryOption counter = new SalaryOption(previous.getCounterAnnualSalary());
            salary.getItems().add(counter);
            salary.setSelected(counter);
        }
        final SelectBox<String> years = ScreenUI.createSelectBox(game.skin);
        years.setItems("1", "2", "3", "4", "5");
        years.setSelected(String.valueOf(game.freeAgencyService.getPreferredYears(player)));
        if (previous != null && previous.getStatus() == FreeAgencyService.OfferStatus.COUNTER_OFFER)
            years.setSelected(String.valueOf(previous.getCounterYears()));

        final Label chance = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.left);
        final Label capPreview = ScreenUI.createBoldValue(game.skin, "", Color.WHITE, Align.left);
        final Label taxPreview = ScreenUI.createSubtitle(game.skin, "");
        final Label totalPreview = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.right);
        final Label remainingPreview = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.right);
        final Label annualPreview = ScreenUI.createBoldValue(game.skin, "", Color.WHITE, Align.right);
        final Label durationPreview = ScreenUI.createBoldValue(game.skin, "", Color.WHITE, Align.right);
        final Table chanceMeter = new Table();
        final Runnable updatePreview = new Runnable() {
            @Override public void run() {
                long salaryValue = salary.getSelected().salary;
                int duration = Integer.parseInt(years.getSelected());
                int odds = game.freeAgencyService.estimateAcceptanceChance(player, club, salaryValue, duration);
                long payroll = game.freeAgencyService.getProjectedPayroll(club, salaryValue);
                long cap = club.getFinance().getSalaryCap();
                long taxLine = club.getFinance().getLuxuryTaxThreshold();
                long hardCap = club.getFinance().getHardCap();
                long tax = game.freeAgencyService.getLuxuryTax(club, payroll);
                chance.setText("Chance estimada: " + odds + "%");
                Color chanceColor = odds >= 70 ? ScreenUI.SUCCESS : odds >= 45 ? StyleFactory.SOFT_YELLOW : ScreenUI.WARNING;
                chance.setColor(chanceColor);
                chanceMeter.clearChildren();
                chanceMeter.add(ScreenUI.createBlockProgress(game.skin, odds, 18, chanceColor)).growX().height(12f);
                capPreview.setText("Após assinatura: " + money(payroll) + " • Hard Cap " + money(hardCap));
                capPreview.setColor(payroll > hardCap ? ScreenUI.DANGER : payroll > taxLine ? ScreenUI.WARNING : ScreenUI.SUCCESS);
                String status = club.getFinance().getPayrollStatus(payroll);
                taxPreview.setText(tax > 0 ? status + " • Tax anual estimada: " + money(tax) : status);
                taxPreview.setColor(payroll > hardCap ? ScreenUI.DANGER : tax > 0 ? ScreenUI.WARNING : payroll > cap ? StyleFactory.SOFT_YELLOW : ScreenUI.SUCCESS);
                totalPreview.setText(money(salaryValue * duration));
                remainingPreview.setText(money(hardCap - payroll));
                remainingPreview.setColor(payroll > hardCap ? ScreenUI.DANGER : ScreenUI.SUCCESS);
                annualPreview.setText(money(salaryValue));
                durationPreview.setText(duration + (duration == 1 ? " ano" : " anos"));
            }
        };
        ChangeListener listener = new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { updatePreview.run(); }
        };
        salary.addListener(listener);
        years.addListener(listener);
        updatePreview.run();

        Dialog dialog = new Dialog("", game.skin) {
            @Override protected void result(Object object) {
                if (!Boolean.TRUE.equals(object)) return;
                FreeAgencyService.Submission submission = game.freeAgencyService.submitOffer(
                    club,
                    player,
                    salary.getSelected().salary,
                    Integer.parseInt(years.getSelected())
                );
                refreshUI();
                showMessage(submission.isAccepted() ? "OFERTA ENVIADA" : "OFERTA NÃO ENVIADA", submission.getMessage());
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setName("contract-negotiation");
        dialog.setBackground(StyleFactory.createRoundedPanel(Color.valueOf("091411"), StyleFactory.GOLD));
        Table content = dialog.getContentTable();
        content.pad(24f);

        Table heading = new Table();
        Table headingCopy = new Table();
        headingCopy.left();
        Label offerTitle = ScreenUI.createSectionTitle(game.skin, "NEGOCIAÇÃO DE CONTRATO");
        offerTitle.setFontScale(.95f);
        headingCopy.add(offerTitle).left().row();
        Label subtitle = ScreenUI.createSubtitle(game.skin, "Negocie os termos com " + player.getName().toUpperCase());
        subtitle.setColor(StyleFactory.GOLD);
        headingCopy.add(subtitle).left().padTop(2f);
        heading.add(headingCopy).growX().left();
        heading.add(ScreenUI.createBadge(game.skin, "FREE AGENT", StyleFactory.GOLD)).height(30f).right();
        content.add(heading).growX().height(58f).padBottom(8f).row();

        Table identity = ScreenUI.createSubtlePanel();
        Table playerCopy = new Table();
        playerCopy.left();
        Label playerName = ScreenUI.createBoldValue(game.skin, player.getName().toUpperCase(), Color.WHITE, Align.left);
        playerName.setFontScale(.68f);
        playerCopy.add(playerName).left().row();
        playerCopy.add(ScreenUI.createSubtitle(game.skin,
            player.getAge() + " anos • " + player.getNationality() + " • " + player.getPosition())).left().padTop(3f);
        identity.add(playerCopy).growX().left().padRight(12f);
        identity.add(ScreenUI.createStatusBox(game.skin, "OVR", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).width(105f).height(62f).padRight(7f);
        identity.add(ScreenUI.createStatusBox(game.skin, "POTENCIAL", io.github.some_example_name.model.PlayerPotentialDisplay.forViewer(player, club), ScreenUI.SUCCESS)).width(118f).height(62f).padRight(7f);
        int interest = game.freeAgencyService.getInterestStars(player, club);
        Table interestPanel = new Table();
        interestPanel.add(ScreenUI.createSubtitle(game.skin, "INTERESSE DO JOGADOR")).row();
        interestPanel.add(createInterestMeter(player)).padTop(8f);
        identity.add(interestPanel).width(195f).height(62f);
        content.add(identity).growX().height(82f).padBottom(9f).row();

        Table body = new Table();
        Table terms = ScreenUI.createPanel();
        terms.top().left();
        terms.add(ScreenUI.createSectionTitle(game.skin, "SUA PROPOSTA")).colspan(2).growX().left().padBottom(18f).row();
        terms.add(ScreenUI.createSubtitle(game.skin, "PEDIDO DO JOGADOR")).width(220f).left();
        terms.add(ScreenUI.createBoldValue(game.skin, money(requested) + " / ano", StyleFactory.SOFT_YELLOW, Align.left)).growX().left().row();
        terms.add(ScreenUI.createSubtitle(game.skin, "DURAÇÃO DESEJADA")).left().padTop(8f);
        terms.add(ScreenUI.createSubtitle(game.skin, getYearsText(game.freeAgencyService.getPreferredYears(player)))).left().padTop(8f).row();
        terms.add(ScreenUI.createDivider()).colspan(2).growX().height(1f).padTop(18f).padBottom(18f).row();
        terms.add(ScreenUI.createSubtitle(game.skin, "SALÁRIO ANUAL")).left();
        terms.add(salary).growX().height(48f).row();
        terms.add(ScreenUI.createSubtitle(game.skin, "DURAÇÃO (ANOS)")).left().padTop(14f);
        terms.add(years).growX().height(48f).padTop(14f).row();
        terms.add(chance).colspan(2).left().padTop(22f).row();
        terms.add(chanceMeter).colspan(2).left().height(16f).padTop(8f).row();
        Label estimateHint = ScreenUI.createSubtitle(game.skin, "Estimativa, não garantia: o jogador pode aceitar, recusar ou fazer uma contraproposta.");
        estimateHint.setWrap(true);
        terms.add(estimateHint).colspan(2).growX().padTop(10f).row();
        Table capBox = ScreenUI.createSubtlePanel();
        capBox.add(capPreview).growX().left().row();
        capBox.add(taxPreview).growX().left().padTop(5f);
        capPreview.setWrap(true);
        taxPreview.setWrap(true);
        terms.add(capBox).colspan(2).growX().height(82f).padTop(18f);

        Table information = ScreenUI.createPanel();
        information.top().left();
        information.add(ScreenUI.createSectionTitle(game.skin, "IMPACTO FINANCEIRO")).colspan(2).growX().left().padBottom(12f).row();
        addOfferInfo(information, "Salário anual", annualPreview, Color.WHITE);
        addOfferInfo(information, "Duração", durationPreview, Color.WHITE);
        addOfferInfo(information, "Custo total", totalPreview, ScreenUI.SUCCESS);
        addOfferInfo(information, "Espaço no Hard Cap", remainingPreview, ScreenUI.SUCCESS);
        information.add(ScreenUI.createDivider()).colspan(2).growX().height(1f).padTop(11f).padBottom(11f).row();
        information.add(ScreenUI.createSubtitle(game.skin, "INFORMAÇÕES DO JOGADOR")).colspan(2).left().padBottom(7f).row();
        addOfferInfo(information, "Posição", player.getPosition(), Color.WHITE);
        addOfferInfo(information, "Idade", player.getAge() + " anos", Color.WHITE);
        addOfferInfo(information, "Nacionalidade", player.getNationality(), Color.WHITE);
        addOfferInfo(information, "Situação", "Sem contrato", StyleFactory.SOFT_YELLOW);
        information.add(ScreenUI.createDivider()).colspan(2).growX().height(1f).padTop(14f).padBottom(12f).row();
        information.add(ScreenUI.createSubtitle(game.skin, "ATRIBUTOS PRINCIPAIS")).colspan(2).left().padBottom(10f).row();
        TechnicalAttributes attributes = player.getTechnicalAttributes();
        Table stats = new Table();
        stats.add(attribute("ATA", attributes.getAtaque())).expandX();
        stats.add(attribute("PAS", attributes.getPasse())).expandX();
        stats.add(attribute("DEF", attributes.getDefesa())).expandX();
        stats.add(attribute("FIS", attributes.getFisico())).expandX();
        stats.add(attribute("DRI", attributes.getDrible())).expandX();
        information.add(stats).colspan(2).growX();

        body.add(terms).width(720f).growY().padRight(14f);
        body.add(information).grow().minWidth(510f);
        content.add(body).grow().padTop(6f).row();

        updatePreview.run();
        dialog.button("CANCELAR", false);
        dialog.button("ENVIAR OFERTA", true);
        dialog.getButtonTable().pad(12f, 24f, 22f, 24f);
        for (com.badlogic.gdx.scenes.scene2d.ui.Cell<?> cell : dialog.getButtonTable().getCells())
            cell.width(245f).height(48f).padLeft(8f).padRight(8f);
        dialog.show(stage);
        dialog.setSize(Math.min(1360f, stage.getWidth() - 40f), Math.min(820f, stage.getHeight() - 40f));
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) * .5f, (stage.getHeight() - dialog.getHeight()) * .5f);
    }

    private void addOfferInfo(Table table, String title, String value, Color color) {
        table.add(ScreenUI.createSubtitle(game.skin, title)).growX().left().height(28f);
        table.add(ScreenUI.createBoldValue(game.skin, value, color, Align.right)).right().height(28f).row();
    }

    private void addOfferInfo(Table table, String title, Label value, Color color) {
        value.setColor(color);
        table.add(ScreenUI.createSubtitle(game.skin, title)).growX().left().height(28f);
        table.add(value).right().height(28f).row();
    }

    private void showMessage(String title, String message) {
        Dialog dialog = new Dialog(title, game.skin);
        Label text = new Label(message, game.skin);
        text.setWrap(true);
        text.setAlignment(Align.center);
        dialog.getContentTable().add(text).width(430f).pad(18f);
        dialog.button("OK", true);
        dialog.show(stage);
    }

    private List<Player> filteredPlayers() {
        List<Player> result = new ArrayList<>(game.freeAgencyService.getFreeAgents());
        result.removeIf(player -> !matchesFilter(player) || !player.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)));
        Comparator<Player> comparator;
        if ("POTENCIAL".equals(sort)) comparator = Comparator.comparingInt(Player::getPotential);
        else if ("IDADE".equals(sort)) comparator = Comparator.comparingInt(Player::getAge);
        else if ("SALÁRIO".equals(sort)) comparator = Comparator.comparingLong(game.freeAgencyService::getRequestedAnnualSalary);
        else if ("INTERESSE".equals(sort)) comparator = Comparator.comparingInt(player -> game.freeAgencyService.getInterestStars(player, club));
        else comparator = Comparator.comparingInt(Player::getOverall);
        if (!sortAscending) comparator = comparator.reversed();
        result.sort(comparator.thenComparing(Player::getName));
        return result;
    }

    private boolean matchesFilter(Player player) {
        if ("★".equals(filter)) return game.freeAgencyService.isFavourite(player);
        String position = player.getPosition();
        if ("GK".equals(filter)) return "GK".equals(position);
        if ("DEF".equals(filter)) return position.matches("CB|LB|RB|LWB|RWB");
        if ("MEI".equals(filter)) return position.matches("CDM|CM|CAM|LM|RM");
        if ("ATA".equals(filter)) return position.matches("ST|CF|LW|RW");
        return true;
    }

    private FreeAgencyService.Offer pendingOffer(Player player) {
        FreeAgencyService.Offer offer = game.freeAgencyService.findOffer(player);
        return offer != null && offer.getStatus() == FreeAgencyService.OfferStatus.PENDING ? offer : null;
    }

    private Label head(String value, int align) {
        Label label = ScreenUI.createTableHeaderLabel(game.skin, value, align);
        label.setFontScale(0.47f);
        return label;
    }

    private Label value(String value, Color color, int align) {
        Label label = ScreenUI.createBoldValue(game.skin, value, color, align);
        label.setFontScale(0.48f);
        return label;
    }

    private Label attribute(String shortName, int value) {
        return label(shortName, String.valueOf(value), value >= 85 ? ScreenUI.SUCCESS : Color.WHITE);
    }

    private Label label(String key, String value, Color color) {
        Label label = ScreenUI.createSubtitle(game.skin, key + ": " + value);
        label.setColor(color);
        return label;
    }

    private String money(long amount) {
        return String.format(Locale.US, "WFL$ %.1fM", amount / 1_000_000.0);
    }

    private String stars(int count) {
        return ScreenUI.formatStars(count);
    }

    private Table createInterestMeter(Player player) {
        int interest = Math.max(0, Math.min(5, game.freeAgencyService.getInterestStars(player, club)));
        Table meter = ScreenUI.createBlockProgress(game.skin, interest * 20.0, 5, interestColor(player));
        meter.setName("interest-meter");
        meter.add(value(interest + "/5", interestColor(player), Align.center)).padLeft(5f);
        return meter;
    }

    private String getYearsText(int years) {
        return years + (years == 1 ? " ano" : " anos");
    }

    private String interestText(int stars) {
        return stars >= 5 ? "QUER MUITO VIR" : stars == 4 ? "ALTO" : stars == 3 ? "NEUTRO" : stars == 2 ? "BAIXO" : "MUITO BAIXO";
    }

    private Color interestColor(Player player) {
        int stars = game.freeAgencyService.getInterestStars(player, club);
        return stars >= 4 ? ScreenUI.SUCCESS : stars <= 2 ? ScreenUI.DANGER : StyleFactory.SOFT_YELLOW;
    }

    private Color statusColor(FreeAgencyService.OfferStatus status) {
        return status == FreeAgencyService.OfferStatus.ACCEPTED ? ScreenUI.SUCCESS
            : status == FreeAgencyService.OfferStatus.REJECTED ? ScreenUI.DANGER : StyleFactory.SOFT_YELLOW;
    }

    private SalaryOption[] salaryOptions(long requested) {
        double[] factors = { 0.70, 0.80, 0.90, 1.00, 1.08, 1.16, 1.28, 1.40, 1.55 };
        SalaryOption[] options = new SalaryOption[factors.length];
        for (int index = 0; index < factors.length; index++) {
            long value = Math.max(120_000L, Math.round(requested * factors[index] / 10_000.0) * 10_000L);
            options[index] = new SalaryOption(value);
        }
        return options;
    }

    private final class SalaryOption {
        private final long salary;
        private SalaryOption(long salary) { this.salary = salary; }
        @Override public String toString() { return money(salary) + " / ano"; }
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0.02f, 0.05f, 0.04f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { io.github.some_example_name.utils.ScrollPositionMemory.capture(stage, getClass().getName()); }
    @Override public void dispose() { stage.dispose(); }
}
