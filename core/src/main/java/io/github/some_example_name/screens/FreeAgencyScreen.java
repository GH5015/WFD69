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
    }

    private void refreshUI() {
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
        float headerWidth = Math.max(820f, stage.getWidth() - ScreenUI.PAGE_LEFT_OPEN - ScreenUI.PAGE_RIGHT - 405f);
        headerLine.add(ScreenUI.createHeader(game.skin, "FREE AGENCY", "WFL • " + game.league.getCurrentSeason() + " • " + SeasonCalendar.getFreeAgencyStatus(game.league))).width(headerWidth).height(78f).left();
        headerLine.add().expandX();
        page.add(headerLine).growX().height(78f).padBottom(10f).row();
        page.add(createSalaryCapPanel()).growX().height(72f).padBottom(8f).row();
        if (marketOpen) {
            page.add(createToolbar()).growX().height(82f).padBottom(8f).row();
            page.add("JOGADORES".equals(tab) ? createMarketBody() : createOffersPanel()).grow().row();
        } else {
            page.add(createMarketClosedPanel()).grow();
        }
        root.add(page);

        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
            Table returnOverlay = new Table(); returnOverlay.setFillParent(true); returnOverlay.bottom().left().pad(18f);
            TextButton back = ScreenUI.createInteractiveButton("← VOLTAR À OFF SEASON", game.skin);
            back.getLabel().setFontScale(.45f);
            back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ game.setScreen(new OffSeasonScreen(game, club)); }});
            returnOverlay.add(back).width(235f).height(42f); root.add(returnOverlay);
        } else {
            NavigationDrawer.attach(stage, game, club, "AGENTES", true);
        }
    }

    private Table createSalaryCapPanel() {
        long cap = club.getFinance().getSalaryCap();
        long payroll = club.getFinance().getAnnualPayroll();
        long available = cap - payroll;
        Table panel = ScreenUI.createPanel();
        Table title = new Table();
        title.add(ScreenUI.createSectionTitle(game.skin, "SALARY CAP")).left().row();
        title.add(ScreenUI.createSubtitle(game.skin, "Folha anual e espaço disponível antes de enviar propostas.")).left();
        panel.add(title).left().width(245f).padRight(10f);
        panel.add(ScreenUI.createBlockProgress(game.skin, cap <= 0 ? 0 : payroll * 100.0 / cap, 18, available < 0 ? ScreenUI.DANGER : StyleFactory.GOLD)).left().width(185f).padRight(10f);
        panel.add(status("SALARY CAP", money(cap), StyleFactory.SOFT_YELLOW)).width(190f).padRight(6f);
        panel.add(status("FOLHA ATUAL", money(payroll), Color.WHITE)).width(190f).padRight(6f);
        panel.add(status("ESPAÇO NO CAP", money(Math.abs(available)), available >= 0 ? ScreenUI.SUCCESS : ScreenUI.DANGER)).width(190f).padRight(6f);
        panel.add(status("ELENCO", club.getSquad().size() + "/26", club.getSquad().size() < 26 ? ScreenUI.SUCCESS : ScreenUI.WARNING)).width(150f);
        return panel;
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
        tabs.add(tabButton("JOGADORES")).width(140f).height(32f).padRight(5f);
        tabs.add(tabButton("MINHAS OFERTAS")).width(155f).height(32f);
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
            int pending = 0;
            for (FreeAgencyService.Offer offer : game.freeAgencyService.getUserOffers()) {
                if (offer.getStatus() == FreeAgencyService.OfferStatus.PENDING) pending++;
            }
            panel.add(ScreenUI.createSubtitle(game.skin, pending == 0
                ? "Nenhuma proposta pendente. As decisões são processadas ao avançar o dia."
                : pending + " proposta(s) aguardando decisão ao avançar o dia.")).left().padTop(10f);
        }
        return panel;
    }

    private TextButton tabButton(final String value) {
        TextButton button = ScreenUI.createInteractiveButton(value, game.skin, "toggle");
        boolean active = value.equals(tab);
        button.setChecked(active);
        button.getLabel().setFontScale(0.48f);
        button.setColor(active ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        button.getLabel().setColor(active ? Color.BLACK : Color.WHITE);
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                tab = value;
                refreshUI();
            }
        });
        return button;
    }

    private TextButton filterButton(final String value) {
        TextButton button = ScreenUI.createInteractiveButton(value, game.skin, "toggle");
        boolean active = value.equals(filter);
        button.setChecked(active);
        button.getLabel().setFontScale(0.44f);
        button.setColor(active ? StyleFactory.GOLD : StyleFactory.METAL_DARK);
        button.getLabel().setColor(active ? Color.BLACK : Color.WHITE);
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
        float detailsWidth = Math.min(440f, usable * 0.31f);
        body.add(createMarketTable()).width(usable - detailsWidth - 10f).growY().padRight(10f);
        body.add(createPlayerDetails()).width(detailsWidth).growY();
        return body;
    }

    private Table createMarketTable() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        Table list = new Table();
        Table header = ScreenUI.createTableHeaderRow();
        header.add(head("★", Align.center)).width(42f);
        header.add(head("JOGADOR", Align.left)).width(200f);
        header.add(head("POS", Align.center)).width(55f);
        header.add(head("IDADE", Align.center)).width(64f);
        header.add(head("OVR", Align.center)).width(54f);
        header.add(head("POT", Align.center)).width(54f);
        header.add(head("PEDIDO", Align.center)).width(106f);
        header.add(head("INTERESSE", Align.center)).width(112f);
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
        TextButton favourite = ScreenUI.createInteractiveButton(game.freeAgencyService.isFavourite(player) ? "★" : "☆", game.skin, "toggle");
        favourite.getLabel().setFontScale(0.62f);
        favourite.getLabel().setColor(game.freeAgencyService.isFavourite(player) ? StyleFactory.SOFT_YELLOW : ScreenUI.MUTED_TEXT);
        favourite.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.freeAgencyService.toggleFavourite(player);
                refreshUI();
            }
        });
        row.add(favourite).width(42f).height(29f);
        row.add(value(ScreenUI.shorten(player.getName(), 22), Color.WHITE, Align.left)).width(200f).padLeft(5f);
        row.add(ScreenUI.createBadge(game.skin, player.getPosition(), StyleFactory.getPositionColor(player.getPosition()))).width(55f).height(25f);
        row.add(value(String.valueOf(player.getAge()), Color.WHITE, Align.center)).width(64f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW, Align.center)).width(54f);
        row.add(value(String.valueOf(player.getPotential()), ScreenUI.SUCCESS, Align.center)).width(54f);
        row.add(value(money(game.freeAgencyService.getRequestedAnnualSalary(player)), Color.WHITE, Align.center)).width(106f);
        row.add(value(stars(game.freeAgencyService.getInterestStars(player, club)), interestColor(player), Align.center)).width(112f);
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
        title.add(ScreenUI.createSectionTitle(game.skin, "JOGADOR SELECIONADO")).left().expandX();
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

        Table ratings = new Table();
        ratings.add(status("OVR", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW)).growX().padRight(6f);
        ratings.add(status("POTENCIAL", String.valueOf(player.getPotential()), ScreenUI.SUCCESS)).growX();
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
        contract.add(label("PEDIDO", money(demand) + " / ano", Color.WHITE)).left().expandX().row();
        contract.add(label("PREFERÊNCIA", getYearsText(game.freeAgencyService.getPreferredYears(player)), ScreenUI.MUTED_TEXT)).left().padTop(5f).row();
        contract.add(label("INTERESSE", stars(interest) + "  " + interestText(interest), interestColor(player))).left().padTop(5f).row();
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
        final SelectBox<String> years = ScreenUI.createSelectBox(game.skin);
        years.setItems("1", "2", "3", "4", "5");
        years.setSelected(String.valueOf(game.freeAgencyService.getPreferredYears(player)));

        final Label chance = ScreenUI.createBoldValue(game.skin, "", ScreenUI.SUCCESS, Align.left);
        final Label capPreview = ScreenUI.createBoldValue(game.skin, "", Color.WHITE, Align.left);
        final Label taxPreview = ScreenUI.createSubtitle(game.skin, "");
        final Runnable updatePreview = new Runnable() {
            @Override public void run() {
                long salaryValue = salary.getSelected().salary;
                int duration = Integer.parseInt(years.getSelected());
                int odds = game.freeAgencyService.estimateAcceptanceChance(player, club, salaryValue, duration);
                long payroll = game.freeAgencyService.getProjectedPayroll(club, salaryValue);
                long cap = club.getFinance().getSalaryCap();
                long tax = game.freeAgencyService.getLuxuryTax(club, payroll);
                chance.setText("Chance estimada: " + odds + "%");
                capPreview.setText("Após assinatura: " + money(payroll) + " / " + money(cap));
                capPreview.setColor(payroll > cap ? ScreenUI.DANGER : ScreenUI.SUCCESS);
                taxPreview.setText(tax > 0 ? "⚠ ACIMA DO SALARY CAP • Luxury Tax estimada: " + money(tax) : "Dentro do Salary Cap");
                taxPreview.setColor(tax > 0 ? ScreenUI.DANGER : ScreenUI.SUCCESS);
            }
        };
        ChangeListener listener = new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { updatePreview.run(); }
        };
        salary.addListener(listener);
        years.addListener(listener);
        updatePreview.run();

        Dialog dialog = new Dialog("OFERTA DE CONTRATO", game.skin) {
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
        dialog.getContentTable().pad(16f);
        Label title = ScreenUI.createBoldValue(game.skin, player.getName().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.center);
        title.setFontScale(0.70f);
        dialog.getContentTable().add(title).width(500f).center().row();
        dialog.getContentTable().add(ScreenUI.createSubtitle(game.skin, player.getPosition() + " • " + player.getAge() + " anos • OVR " + player.getOverall())).center().padTop(4f).row();
        dialog.getContentTable().add(label("PEDIDO", money(requested) + " / ano • " + getYearsText(game.freeAgencyService.getPreferredYears(player)), Color.WHITE)).left().padTop(18f).row();
        dialog.getContentTable().add(ScreenUI.createSubtitle(game.skin, "SALÁRIO ANUAL")).left().padTop(12f).row();
        dialog.getContentTable().add(salary).width(300f).height(42f).left().padTop(4f).row();
        dialog.getContentTable().add(ScreenUI.createSubtitle(game.skin, "DURAÇÃO (ANOS)")).left().padTop(12f).row();
        dialog.getContentTable().add(years).width(150f).height(42f).left().padTop(4f).row();
        dialog.getContentTable().add(chance).left().padTop(16f).row();
        dialog.getContentTable().add(capPreview).left().padTop(7f).row();
        dialog.getContentTable().add(taxPreview).left().padTop(5f).row();
        dialog.button("ENVIAR OFERTA", true);
        dialog.button("CANCELAR", false);
        dialog.show(stage);
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
        StringBuilder output = new StringBuilder();
        for (int index = 0; index < 5; index++) output.append(index < count ? "★" : "☆");
        return output.toString();
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
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); }
}
