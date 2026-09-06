package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubNeedEvaluator;
import io.github.some_example_name.model.DraftPick;
import io.github.some_example_name.model.DraftPickEvaluator;
import io.github.some_example_name.model.DraftOrderService;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.model.SmartTradeEvaluator;
import io.github.some_example_name.model.TradeFinderService;
import io.github.some_example_name.model.TradeMarketSearchEvaluator;
import io.github.some_example_name.model.TradeOffer;
import io.github.some_example_name.model.TradeRosterImpactEvaluator;
import io.github.some_example_name.utils.PlayerDetailsDialog;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Catálogo global de jogadores contratados disponível no Mercado de Trocas. */
public class TradePlayerSearchScreen implements Screen {
    private final Main game;
    private final Club userClub;
    private final Stage stage;
    private final Texture backgroundTexture;

    private String query = "";
    private String positionFilter = "TODOS";
    private String sortMode = "ENCAIXE";
    private String ownerScope = "LIGA";
    private String assetType = "JOGADORES";
    private String marketProfile = "TODOS";
    private Player selected;
    private DraftPick selectedPick;
    private TextField searchField;

    public TradePlayerSearchScreen(Main game, Club userClub) {
        this.game = game;
        this.userClub = userClub;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("prancheta.png"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        refreshUI();
    }

    private void refreshUI() {
        io.github.some_example_name.utils.ScrollPositionMemory.capture(stage, getClass().getName());
        DraftOrderService.refreshAllPickProjections(game.league);
        List<Player> players = filteredPlayers();
        List<DraftPick> picks = filteredPicks();
        if ("JOGADORES".equals(assetType)) {
            if (selected == null || !players.contains(selected)) {
                selected = players.isEmpty() ? null : players.get(0);
            }
            selectedPick = null;
        } else {
            if (selectedPick == null || !picks.contains(selectedPick)) {
                selectedPick = picks.isEmpty() ? null : picks.get(0);
            }
            selected = null;
        }

        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);

        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);
        root.add(background);

        Table page = new Table();
        page.top();
        page.pad(6f, 24f, 10f, 24f);
        page.background(StyleFactory.createMetallicBoard(
            64,
            64,
            Color.valueOf("07110D")
        ));

        page.add(createBrandStrip()).growX().height(46f).padBottom(5f).row();
        page.add(ScreenUI.createHeader(
            game.skin,
            "TRADE FINDER",
            "BUSCA DE JOGADORES E PICKS • " + resultCount(players, picks) + " RESULTADO" + (resultCount(players, picks) == 1 ? "" : "S")
        )).growX().height(70f).padBottom(7f).row();

        page.add(createControls()).growX().height("JOGADORES".equals(assetType) ? 205f : 158f).padBottom(7f).row();

        Table body = new Table();
        body.add("JOGADORES".equals(assetType) ? createResultsPanel(players) : createPickResultsPanel(picks))
            .grow().minHeight(0f).padRight(8f);
        body.add(createSelectedPanel()).width(500f).growY().minHeight(0f);
        page.add(body).grow().minHeight(0f).padBottom(7f).row();

        Table footer = new Table();
        footer.background(StyleFactory.createRoundedPanel(
            Color.valueOf("161B19"),
            StyleFactory.BORDER_SOFT
        ));
        footer.pad(4f, 12f, 4f, 12f);
        TextButton back = ScreenUI.createInteractiveButton("← VOLTAR AO MERCADO", game.skin);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TradeHubScreen(game, userClub));
            }
        });
        TextButton central = ScreenUI.createInteractiveButton("ABRIR CENTRAL SEM FILTRO", game.skin);
        central.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TradeScreen(game, userClub));
            }
        });
        footer.add(back).width(275f).height(42f).left();
        footer.add().expandX();
        footer.add(ScreenUI.createSubtitle(game.skin, "WFL  •  WORLD FOOTBALL LEAGUE")).center();
        footer.add().expandX();
        footer.add(central).width(310f).height(42f).right();
        page.add(footer).growX().height(52f);

        root.add(page);
        io.github.some_example_name.utils.ScrollPositionMemory.restore(stage, getClass().getName());
    }

    private Table createBrandStrip() {
        Table strip = new Table();
        strip.background(StyleFactory.createRoundedPanel(
            StyleFactory.METAL_DARK,
            Color.valueOf("55605B")
        ));
        strip.pad(2f, 28f, 2f, 28f);

        Label brand = ScreenUI.createBoldValue(game.skin, "WFL", StyleFactory.YELLOW_TITLE, Align.left);
        brand.setFontScale(.72f);
        strip.add(brand).left();
        Label fullName = ScreenUI.createSubtitle(game.skin, "WORLD\nFOOTBALL\nLEAGUE");
        fullName.setFontScale(.35f);
        strip.add(fullName).width(125f).left().padLeft(9f);
        strip.add().expandX();
        Label motto = ScreenUI.createSubtitle(game.skin, "MAIS QUE UM JOGO.\nUMA LIGA.");
        motto.setAlignment(Align.center);
        motto.setColor(StyleFactory.CREME_AGED);
        strip.add(motto).center();
        strip.add().expandX();
        Label values = ScreenUI.createSubtitle(game.skin, "GERIR\nCONQUISTAR\nFAZER HISTÓRIA");
        values.setFontScale(.36f);
        values.setAlignment(Align.right);
        strip.add(values).right();
        return strip;
    }

    private Table createControls() {
        Table panel = ScreenUI.createPanel();
        panel.pad(9f, 12f, 9f, 12f);

        Table left = new Table();
        left.top().left();
        Table filterMark = ScreenUI.createSubtlePanel();
        Label filterTitle = ScreenUI.createBoldValue(game.skin, "▼\nFILTROS\nDE BUSCA", StyleFactory.CREME_AGED, Align.center);
        filterTitle.setFontScale(.45f);
        filterMark.add(filterTitle).grow();

        Table filterFields = new Table();
        filterFields.top().left();
        Table scopeTabs = new Table();
        scopeTabs.add(modeButton("MERCADO DA LIGA", "LIGA", true)).width(210f).height(34f).padRight(7f);
        scopeTabs.add(modeButton("MEUS ATIVOS", "MEUS", true)).width(185f).height(34f);
        filterFields.add(scopeTabs).growX().left().padBottom(7f).row();

        Table searchRow = new Table();
        searchField = new TextField(query, game.skin);
        searchField.setMessageText(
            "JOGADORES".equals(assetType)
                ? "Ex.: Silva, Brasil, ST ou nome do clube..."
                : "Digite o clube de origem ou o ano da pick..."
        );
        searchField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override public void keyTyped(TextField textField, char character) {
                if (character == '\r' || character == '\n') {
                    query = textField.getText().trim();
                    refreshUI();
                }
            }
        });
        searchRow.add(searchField).growX().height(36f).padRight(8f);

        TextButton search = ScreenUI.createPrimaryButton(game.skin, "BUSCAR");
        search.getLabel().setFontScale(.48f);
        search.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                query = searchField.getText().trim();
                refreshUI();
            }
        });
        searchRow.add(search).width(145f).height(36f).padRight(7f);

        TextButton clear = ScreenUI.createInteractiveButton("LIMPAR", game.skin, "toggle");
        clear.getLabel().setFontScale(.44f);
        clear.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                query = "";
                refreshUI();
            }
        });
        searchRow.add(clear).width(135f).height(36f);
        filterFields.add(searchRow).growX().padBottom(7f).row();

        if ("JOGADORES".equals(assetType)) {
            Table positionRow = new Table();
            positionRow.add(compactControlLabel("POSIÇÃO")).width(95f).left();
            Table filters = new Table();
            for (String filter : new String[]{"TODOS", "GK", "DEF", "MEI", "ATA"}) {
                filters.add(positionButton(filter)).width(108f).height(32f).padRight(7f);
            }
            positionRow.add(filters).growX().left();
            filterFields.add(positionRow).growX().padBottom(6f).row();

            Table profileRow = new Table();
            profileRow.add(compactControlLabel("PERFIL")).width(95f).left();
            Table profiles = new Table();
            for (String profile : new String[]{"TODOS", "ENCAIXE", "NEGOCIÁVEIS", "OPORTUNIDADES", "JOVENS", "EXPIRANDO"}) {
                profiles.add(profileButton(profile)).width(profile.length() > 11 ? 150f : 112f).height(32f).padRight(7f);
            }
            profileRow.add(profiles).growX().left();
            filterFields.add(profileRow).growX();
        } else {
            filterFields.add(ScreenUI.createSubtitle(
                game.skin,
                "1ª e 2ª rodadas pertencentes ao dono atual"
            )).left().padTop(8f);
        }

        left.add(filterMark).width(145f).growY().padRight(14f);
        left.add(filterFields).grow().minWidth(0f);

        Table right = new Table();
        right.top().left();
        Table typeRow = new Table();
        typeRow.add(compactControlLabel("TIPO")).width(115f).left();
        typeRow.add(modeButton("JOGADORES", "JOGADORES", false)).width(150f).height(34f).padRight(7f);
        typeRow.add(modeButton("PICKS DO DRAFT", "PICKS", false)).width(165f).height(34f);
        right.add(typeRow).growX().left().padBottom(12f).row();

        Table sortRow = new Table();
        sortRow.add(compactControlLabel("ORDENAR POR")).width(115f).left();
        String[] sortOptions = "JOGADORES".equals(assetType)
            ? new String[]{"ENCAIXE", "DISPON.", "VALOR", "OVR", "IDADE"}
            : new String[]{"VALOR", "ANO", "RODADA"};
        for (String mode : sortOptions) {
            sortRow.add(sortButton(mode)).width(84f).height(34f).padRight(6f);
        }
        right.add(sortRow).growX().left().padBottom(18f).row();

        Table hint = new Table();
        Label infoIcon = ScreenUI.createBoldValue(game.skin, "ⓘ", StyleFactory.YELLOW_TITLE, Align.center);
        infoIcon.setFontScale(.58f);
        hint.add(infoIcon).width(42f).padRight(8f);
        hint.add(ScreenUI.createSubtitle(
            game.skin,
            "Use encaixe e disponibilidade para priorizar alvos realistas."
        )).left().growX();
        right.add(hint).growX().left();

        panel.add(left).grow().minWidth(0f).padRight(18f);
        panel.add(right).width(650f).growY().minWidth(0f);
        return panel;
    }

    private Label compactControlLabel(String text) {
        Label label = ScreenUI.createSubtitle(game.skin, text);
        label.setFontScale(.48f);
        label.setColor(StyleFactory.CREME_AGED);
        return label;
    }

    private TextButton profileButton(final String profile) {
        TextButton button = ScreenUI.createInteractiveButton(profile, game.skin, "toggle");
        button.getLabel().setFontScale(profile.length() > 11 ? .38f : .43f);
        button.setChecked(profile.equals(marketProfile));
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                marketProfile = profile;
                refreshUI();
            }
        });
        return button;
    }

    private TextButton modeButton(final String label, final String value, final boolean scope) {
        TextButton button = ScreenUI.createInteractiveButton(label, game.skin, "toggle");
        button.getLabel().setFontScale(.44f);
        button.setChecked(scope ? value.equals(ownerScope) : value.equals(assetType));
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (scope) ownerScope = value;
                else {
                    assetType = value;
                    sortMode = "JOGADORES".equals(value) ? "ENCAIXE" : "VALOR";
                }
                query = "";
                refreshUI();
            }
        });
        return button;
    }

    private TextButton positionButton(final String filter) {
        TextButton button = ScreenUI.createInteractiveButton(filter, game.skin, "toggle");
        button.getLabel().setFontScale(.46f);
        button.setChecked(filter.equals(positionFilter));
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                positionFilter = filter;
                refreshUI();
            }
        });
        return button;
    }

    private TextButton sortButton(final String mode) {
        TextButton button = ScreenUI.createInteractiveButton(mode, game.skin, "toggle");
        button.getLabel().setFontScale(.42f);
        button.setChecked(mode.equals(sortMode));
        button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                sortMode = mode;
                refreshUI();
            }
        });
        return button;
    }

    private Table createResultsPanel(List<Player> players) {
        Table panel = ScreenUI.createPanel();
        panel.top().pad(8f, 12f, 8f, 12f);
        Table heading = new Table();
        Label icon = ScreenUI.createBoldValue(game.skin, "●", StyleFactory.YELLOW_TITLE, Align.center);
        icon.setFontScale(.56f);
        heading.add(icon).width(30f).padRight(5f);
        heading.add(ScreenUI.createSectionTitle(game.skin, "JOGADORES ENCONTRADOS")).left();
        heading.add().expandX();
        heading.add(ScreenUI.createSubtitle(game.skin, players.size() + " RESULTADOS")).right();
        panel.add(heading).growX().padBottom(7f).row();

        Table list = new Table();
        list.top();
        Table header = ScreenUI.createTableHeaderRow();
        addHeader(header, "POS", 75f, Align.center);
        addHeader(header, "JOGADOR", 210f, Align.left);
        addHeader(header, "CLUBE", 200f, Align.left);
        addHeader(header, "ID", 50f, Align.center);
        addHeader(header, "OVR", 55f, Align.center);
        addHeader(header, "POT", 55f, Align.center);
        addHeader(header, "SALÁRIO", 115f, Align.center);
        addHeader(header, "CONTR.", 90f, Align.center);
        addHeader(header, "FIT", 60f, Align.center);
        addHeader(header, "MERCADO", 140f, Align.center);
        addHeader(header, "VALOR", 70f, Align.center);
        addHeader(header, "", 48f, Align.center);
        list.add(header).growX().height(38f).row();

        if (players.isEmpty()) {
            Label empty = ScreenUI.createSubtitle(
                game.skin,
                "Nenhum jogador corresponde aos filtros atuais."
            );
            empty.setAlignment(Align.center);
            list.add(empty).growX().pad(42f).row();
        } else {
            int index = 0;
            for (final Player player : players) {
                list.add(createPlayerRow(player, index++)).growX().height(45f).padBottom(2f).row();
            }
        }

        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setName("trade-finder-player-results");
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        panel.add(scroll).grow().minHeight(0f);
        return panel;
    }

    private Table createPlayerRow(final Player player, int index) {
        Table row = ScreenUI.createRow(index);
        if (player == selected) {
            row.background(StyleFactory.createRoundedPanel(
                new Color(.18f, .15f, .035f, .97f),
                StyleFactory.GOLD
            ));
        }

        row.add(ScreenUI.createBadge(
            game.skin,
            player.getPosition(),
            StyleFactory.getPositionColor(player.getPosition())
        )).width(75f).height(26f);
        row.add(createPlayerIdentity(player)).width(210f).padLeft(7f);
        row.add(value(ScreenUI.shorten(player.getCurrentClub().getName(), 22), ScreenUI.MUTED_TEXT, Align.left)).width(200f).padLeft(5f);
        row.add(value(String.valueOf(player.getAge()), ScreenUI.MUTED_TEXT, Align.center)).width(50f);
        row.add(value(String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW, Align.center)).width(55f);
        row.add(value(io.github.some_example_name.model.PlayerPotentialDisplay.forViewer(player, userClub), ScreenUI.SUCCESS, Align.center)).width(55f);
        row.add(value(formatMoney(player.getAnnualSalary()), Color.WHITE, Align.center)).width(115f);
        row.add(value(contractText(player), contractColor(player), Align.center)).width(90f);
        int fit = fitScore(player);
        row.add(value(String.valueOf(fit), scoreColor(fit), Align.center)).width(60f);
        String market = marketLabel(player);
        row.add(value(market, marketColor(player), Align.center)).width(140f);
        row.add(value(String.valueOf(perceivedValue(player)), StyleFactory.SOFT_YELLOW, Align.center)).width(70f);

        TextButton details = ScreenUI.createInteractiveButton("i", game.skin);
        details.getLabel().setFontScale(.48f);
        details.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                event.stop();
                showPlayerDetails(player);
            }
        });
        row.add(details).width(42f).height(30f).padRight(3f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selected = player;
                refreshUI();
            }
        });
        return row;
    }

    private Table createPlayerIdentity(Player player) {
        Table identity = new Table();
        Table country = ScreenUI.createBadge(game.skin, countryCode(player.getNationality()), Color.valueOf("2A4D3D"));
        identity.add(country).width(42f).height(22f).padRight(7f);
        identity.add(value(ScreenUI.shorten(player.getName(), 20), Color.WHITE, Align.left)).growX().left();
        return identity;
    }

    private Table createPickResultsPanel(List<DraftPick> picks) {
        Table panel = ScreenUI.createPanel();
        panel.top().pad(8f, 12f, 8f, 12f);
        Table heading = new Table();
        heading.add(ScreenUI.createSectionTitle(game.skin, "PICKS ENCONTRADAS")).left();
        heading.add().expandX();
        heading.add(ScreenUI.createSubtitle(game.skin, picks.size() + " RESULTADOS")).right();
        panel.add(heading).growX().padBottom(7f).row();

        Table list = new Table();
        list.top();
        Table header = ScreenUI.createTableHeaderRow();
        addHeader(header, "RODADA", 90f, Align.center);
        addHeader(header, "ANO", 80f, Align.center);
        addHeader(header, "DONO ATUAL", 230f, Align.left);
        addHeader(header, "ORIGEM", 220f, Align.left);
        addHeader(header, "PROJEÇÃO", 105f, Align.center);
        addHeader(header, "CERTEZA", 95f, Align.center);
        addHeader(header, "VALOR", 95f, Align.center);
        list.add(header).growX().height(38f).row();

        if (picks.isEmpty()) {
            Label empty = ScreenUI.createSubtitle(game.skin, "Nenhuma pick corresponde aos filtros atuais.");
            empty.setAlignment(Align.center);
            list.add(empty).growX().pad(42f).row();
        } else {
            int index = 0;
            for (final DraftPick pick : picks) {
                list.add(createPickRow(pick, index++)).growX().height(48f).row();
            }
        }

        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setName("trade-finder-pick-results");
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        panel.add(scroll).grow().minHeight(0f);
        return panel;
    }

    private Table createPickRow(final DraftPick pick, int index) {
        Table row = ScreenUI.createRow(index);
        if (pick == selectedPick) {
            row.background(StyleFactory.createRoundedPanel(
                new Color(.18f, .15f, .035f, .97f),
                StyleFactory.GOLD
            ));
        }
        Club owner = pick.getCurrentOwner();
        Club original = pick.getOriginalOwner();
        row.add(value(pick.getRound() + "ª", StyleFactory.SOFT_YELLOW, Align.center)).width(90f);
        row.add(value(String.valueOf(pick.getYear()), Color.WHITE, Align.center)).width(80f);
        row.add(value(owner != null ? ScreenUI.shorten(owner.getName(), 24) : "—", Color.WHITE, Align.left))
            .width(230f).padLeft(7f);
        row.add(value(original != null ? ScreenUI.shorten(original.getName(), 23) : "—", ScreenUI.MUTED_TEXT, Align.left))
            .width(220f).padLeft(5f);
        row.add(value("#" + pick.getProjectedOverallPosition(), ScreenUI.SUCCESS, Align.center)).width(105f);
        row.add(value(Math.round(pick.getProjectedPositionConfidence() * 100d) + "%", ScreenUI.MUTED_TEXT, Align.center)).width(95f);
        row.add(value(String.valueOf(perceivedPickValue(pick)), StyleFactory.SOFT_YELLOW, Align.center)).width(95f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                selectedPick = pick;
                refreshUI();
            }
        });
        return row;
    }

    private Table createSelectedPanel() {
        Table panel = ScreenUI.createPanel();
        panel.top().pad(8f, 11f, 8f, 11f);
        Table heading = new Table();
        Label icon = ScreenUI.createBoldValue(game.skin, "●", StyleFactory.YELLOW_TITLE, Align.center);
        icon.setFontScale(.54f);
        heading.add(icon).width(28f).padRight(4f);
        heading.add(ScreenUI.createSectionTitle(
            game.skin,
            "JOGADORES".equals(assetType) ? "JOGADOR SELECIONADO" : "PICK SELECIONADA"
        )).left();
        heading.add().expandX();
        panel.add(heading).growX().padBottom(8f).row();

        if ("PICKS".equals(assetType)) {
            return populateSelectedPickPanel(panel);
        }

        if (selected == null) {
            Label empty = ScreenUI.createSubtitle(game.skin, "Selecione um jogador na lista.");
            empty.setAlignment(Align.center);
            panel.add(empty).grow().center();
            return panel;
        }

        Table playerOverview = new Table();
        Table identity = new Table();
        identity.top().left();
        Label playerName = ScreenUI.createBoldValue(
            game.skin,
            selected.getName().toUpperCase(),
            StyleFactory.YELLOW_TITLE,
            Align.left
        );
        playerName.setFontScale(.64f);
        playerName.setEllipsis(true);
        identity.add(playerName).growX().left().row();

        Table nationality = new Table();
        nationality.add(ScreenUI.createBadge(
            game.skin,
            countryCode(selected.getNationality()),
            Color.valueOf("2A4D3D")
        )).width(48f).height(23f).padRight(7f);
        nationality.add(ScreenUI.createSubtitle(
            game.skin,
            selected.getPosition() + " • " + selected.getAge() + " anos • " + selected.getNationality()
        )).left().growX();
        identity.add(nationality).growX().left().padTop(4f).padBottom(10f).row();

        Table ratings = new Table();
        ratings.add(ScreenUI.createStatusBox(game.skin, "OVR", String.valueOf(selected.getOverall()), StyleFactory.SOFT_YELLOW))
            .growX().uniformX().height(68f).padRight(7f);
        ratings.add(ScreenUI.createStatusBox(game.skin, "POTENCIAL", io.github.some_example_name.model.PlayerPotentialDisplay.forViewer(selected, userClub), ScreenUI.SUCCESS))
            .growX().uniformX().height(68f);
        identity.add(ratings).growX();
        playerOverview.add(identity).growX().minWidth(0f);
        panel.add(playerOverview).growX().padBottom(8f).row();

        Table information = ScreenUI.createSubtlePanel();
        information.pad(8f, 10f, 8f, 10f);
        addInfo(information, "CLUBE", selected.getCurrentClub().getName(), Color.WHITE);
        addInfo(information, "FASE DO CLUBE", phaseLabel(selected.getCurrentClub()), StyleFactory.SOFT_YELLOW);
        addInfo(information, "SALÁRIO", formatMoney(selected.getAnnualSalary()) + "/ano", Color.WHITE);
        addInfo(information, "CONTRATO", contractText(selected), contractColor(selected));
        addInfo(information, "NECESSIDADE NO SEU CLUBE", needStars(selected), ScreenUI.SUCCESS);
        addInfo(information, "ENCAIXE ESPORTIVO", fitScore(selected) + "/100", scoreColor(fitScore(selected)));
        addInfo(information, "ABERTURA DO VENDEDOR", availabilityScore(selected) + "/100", marketColor(selected));
        addInfo(information, "VALOR PARA SEU CLUBE", String.valueOf(perceivedValue(selected)), StyleFactory.SOFT_YELLOW);
        panel.add(information).growX().padBottom(7f).row();

        boolean untouchable = TradeRosterImpactEvaluator.isUntouchable(
            selected.getCurrentClub(), selected
        );
        String status = availabilityText(selected, untouchable);
        Color statusColor = canNegotiate(selected) ? (untouchable ? StyleFactory.GOLD : ScreenUI.SUCCESS) : ScreenUI.DANGER;
        Table market = ScreenUI.createSubtlePanel();
        market.add(ScreenUI.createSubtitle(game.skin, "STATUS DE MERCADO")).left().expandX();
        market.add(ScreenUI.createBoldValue(game.skin, status, statusColor, Align.right)).right();
        market.pad(8f, 10f, 8f, 10f);
        panel.add(market).growX().padBottom(7f).row();

        TextButton profile = ScreenUI.createPrimaryButton(game.skin, "▤  VER PERFIL COMPLETO");
        profile.getLabel().setFontScale(.45f);
        profile.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showPlayerDetails(selected);
            }
        });
        panel.add(profile).growX().height(39f).padBottom(5f).row();

        TextButton finder = ScreenUI.createInteractiveButton(
            canSearchTrade(selected) ? "↔  BUSCAR TROCAS POSSÍVEIS" : "TRADE FINDER INDISPONÍVEL",
            game.skin
        );
        finder.getLabel().setFontScale(.47f);
        finder.setDisabled(!canSearchTrade(selected));
        finder.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (canSearchTrade(selected)) showTradeFinderResults();
            }
        });
        panel.add(finder).growX().height(39f).padBottom(5f).row();

        if (selected.getCurrentClub() != userClub) {
            TextButton negotiate = ScreenUI.createInteractiveButton("✎  MONTAR PROPOSTA MANUAL", game.skin);
            negotiate.getLabel().setFontScale(.45f);
            negotiate.setDisabled(!canNegotiate(selected));
            negotiate.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (!canNegotiate(selected)) return;
                    game.setScreen(new TradeScreen(
                        game,
                        userClub,
                        selected.getCurrentClub(),
                        selected
                    ));
                }
            });
            panel.add(negotiate).growX().height(39f);
        }
        return panel;
    }

    private Table populateSelectedPickPanel(Table panel) {
        if (selectedPick == null) {
            Label empty = ScreenUI.createSubtitle(game.skin, "Selecione uma pick na lista.");
            empty.setAlignment(Align.center);
            panel.add(empty).grow().center();
            return panel;
        }

        Club owner = selectedPick.getCurrentOwner();
        Club original = selectedPick.getOriginalOwner();
        panel.add(ScreenUI.createBoldValue(
            game.skin,
            selectedPick.getRound() + "ª RODADA • " + selectedPick.getYear(),
            StyleFactory.SOFT_YELLOW,
            Align.left
        )).growX().left().row();
        panel.add(ScreenUI.createSubtitle(
            game.skin,
            owner != null ? owner.getName() : "Dono não definido"
        )).left().padTop(3f).padBottom(12f).row();

        Table overview = new Table();
        overview.add(ScreenUI.createStatusBox(
            game.skin,
            "PROJEÇÃO",
            "#" + selectedPick.getProjectedOverallPosition(),
            ScreenUI.SUCCESS
        )).growX().uniformX().height(60f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(
            game.skin,
            "TRADE VALUE",
            String.valueOf(perceivedPickValue(selectedPick)),
            StyleFactory.SOFT_YELLOW
        )).growX().uniformX().height(60f);
        panel.add(overview).growX().padBottom(11f).row();

        Table information = ScreenUI.createSubtlePanel();
        addInfo(information, "DONO ATUAL", owner != null ? owner.getName() : "—", Color.WHITE);
        addInfo(information, "ORIGEM", original != null ? original.getName() : "—", ScreenUI.MUTED_TEXT);
        addInfo(information, "RODADA", selectedPick.getRound() + "ª", Color.WHITE);
        addInfo(information, "ANO", String.valueOf(selectedPick.getYear()), Color.WHITE);
        addInfo(
            information,
            "CERTEZA DA PROJEÇÃO",
            Math.round(selectedPick.getProjectedPositionConfidence() * 100d) + "%",
            selectedPick.getProjectedPositionConfidence() >= .75d ? ScreenUI.SUCCESS : ScreenUI.WARNING
        );
        addInfo(
            information,
            "SITUAÇÃO",
            owner == userClub ? "SUA ESCOLHA" : "PERTENCE A OUTRO CLUBE",
            owner == userClub ? ScreenUI.SUCCESS : StyleFactory.SOFT_YELLOW
        );
        panel.add(information).growX().padBottom(10f).row();

        Label hint = ScreenUI.createSubtitle(
            game.skin,
            owner == userClub
                ? "O Trade Finder buscará o melhor retorno disponível por esta escolha."
                : "O Trade Finder calculará pacotes para adquirir esta escolha."
        );
        hint.setWrap(true);
        hint.setAlignment(Align.center);
        panel.add(hint).growX().pad(6f, 4f, 12f, 4f).row();

        TextButton finder = ScreenUI.createPrimaryButton(
            game.skin,
            canSearchTrade(selectedPick) ? "BUSCAR TROCAS POSSÍVEIS" : "TRADE FINDER INDISPONÍVEL"
        );
        finder.getLabel().setFontScale(.47f);
        finder.setDisabled(!canSearchTrade(selectedPick));
        finder.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (canSearchTrade(selectedPick)) showTradeFinderResults();
            }
        });
        panel.add(finder).growX().height(48f);
        return panel;
    }

    private void showTradeFinderResults() {
        final List<TradeFinderService.Result> results = selected != null
            ? TradeFinderService.findForPlayer(game.league, userClub, selected)
            : TradeFinderService.findForPick(game.league, userClub, selectedPick);

        final Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.getContentTable().background(
            StyleFactory.createMetallicBoard(1120, 760, Color.valueOf("091813"))
        );
        dialog.getContentTable().pad(18f, 22f, 14f, 22f);
        Table heading = new Table();
        Table headingCopy = new Table();
        headingCopy.left();
        Label headingTitle = ScreenUI.createSectionTitle(game.skin, "TRADE FINDER • TROCAS POSSÍVEIS");
        headingTitle.setFontScale(.70f);
        headingCopy.add(headingTitle).left().row();
        Label headingSubtitle = ScreenUI.createSubtitle(game.skin, "Pacotes calculados com valor, necessidade e impacto no elenco");
        headingSubtitle.setColor(StyleFactory.GOLD);
        headingCopy.add(headingSubtitle).left().padTop(2f);
        heading.add(headingCopy).growX().left();
        heading.add(ScreenUI.createBadge(game.skin, "MERCADO DE TROCAS", StyleFactory.GOLD)).height(30f).right();
        dialog.getContentTable().add(heading).growX().height(58f).padBottom(8f).row();
        Table content = new Table();
        content.top().pad(10f);
        String assetName = selected != null
            ? selected.getName()
            : pickLabel(selectedPick);
        Label subtitle = ScreenUI.createSubtitle(
            game.skin,
            results.isEmpty()
                ? "Nenhuma proposta aceitável foi encontrada para " + assetName + "."
                : results.size() + " proposta" + (results.size() == 1 ? " encontrada" : "s encontradas") +
                    " envolvendo " + assetName + "."
        );
        subtitle.setWrap(true);
        subtitle.setAlignment(Align.center);
        content.add(subtitle).width(980f).center().padBottom(10f).row();

        if (results.isEmpty()) {
            Label explanation = ScreenUI.createSubtitle(
                game.skin,
                "Isso pode ocorrer por diferença de valor, proteção de estrelas, tamanho mínimo do elenco, " +
                    "Salary Cap ou falta de interesse das outras franquias. Você ainda pode montar uma proposta manual."
            );
            explanation.setWrap(true);
            explanation.setAlignment(Align.center);
            content.add(explanation).width(820f).pad(35f).row();
        } else {
            int index = 0;
            for (final TradeFinderService.Result result : results) {
                content.add(createTradeResultCard(dialog, result, index++))
                    .width(1000f).height(112f).padBottom(7f).row();
            }
        }

        ScrollPane scroll = new ScrollPane(content, game.skin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        dialog.getContentTable().add(scroll).width(1040f).height(560f).pad(4f);
        dialog.button("FECHAR");
        dialog.show(stage);
        dialog.setSize(1120f, 760f);
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) * .5f, (stage.getHeight() - dialog.getHeight()) * .5f);
    }

    private Table createTradeResultCard(
        final Dialog dialog,
        final TradeFinderService.Result result,
        int index
    ) {
        TradeOffer offer = result.getOffer();
        Table card = ScreenUI.createRow(index);
        card.pad(9f, 12f, 9f, 12f);

        Table club = new Table();
        club.add(ScreenUI.createBoldValue(
            game.skin,
            result.getPartner().getName().toUpperCase(),
            StyleFactory.SOFT_YELLOW,
            Align.left
        )).left().row();
        club.add(ScreenUI.createSubtitle(
            game.skin,
            phaseLabel(result.getPartner()) + " • COMPAT. " + Math.round(result.getScore()) + "%" +
                " • FIT " + Math.round(result.getStrategicFit())
        )).left().padTop(3f);
        card.add(club).width(210f).left().padRight(10f);

        Table sends = createOfferAssets(
            "VOCÊ ENVIA",
            offer.getUserPlayers(),
            offer.getUserPicks(),
            result.getUserValueSent()
        );
        card.add(sends).width(285f).growY().padRight(8f);

        Table receives = createOfferAssets(
            "VOCÊ RECEBE",
            offer.getTargetPlayers(),
            offer.getTargetPicks(),
            result.getUserValueReceived()
        );
        card.add(receives).width(285f).growY().padRight(8f);

        Color balanceColor = "VANTAJOSA".equals(result.getBalanceLabel())
            ? ScreenUI.SUCCESS
            : "EQUILIBRADA".equals(result.getBalanceLabel())
                ? StyleFactory.SOFT_YELLOW
                : ScreenUI.WARNING;
        Table action = new Table();
        action.add(ScreenUI.createBoldValue(
            game.skin,
            result.getBalanceLabel(),
            balanceColor,
            Align.center
        )).center().padBottom(6f).row();
        TextButton open = ScreenUI.createPrimaryButton(game.skin, "ABRIR PROPOSTA");
        open.getLabel().setFontScale(.43f);
        open.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                game.setScreen(new TradeScreen(game, userClub, result.getOffer()));
            }
        });
        action.add(open).width(160f).height(38f);
        card.add(action).width(175f);
        return card;
    }

    private Table createOfferAssets(
        String title,
        List<Player> players,
        List<DraftPick> picks,
        long value
    ) {
        Table box = ScreenUI.createSubtlePanel();
        box.pad(6f, 9f, 6f, 9f);
        box.add(ScreenUI.createSubtitle(game.skin, title)).left().expandX();
        box.add(ScreenUI.createBoldValue(
            game.skin,
            "TV " + value,
            StyleFactory.SOFT_YELLOW,
            Align.right
        )).right().row();
        Label assets = ScreenUI.createBoldValue(
            game.skin,
            describeAssets(players, picks),
            Color.WHITE,
            Align.left
        );
        assets.setWrap(true);
        box.add(assets).colspan(2).width(255f).left().padTop(5f);
        return box;
    }

    private String describeAssets(List<Player> players, List<DraftPick> picks) {
        List<String> descriptions = new ArrayList<>();
        for (Player player : players) {
            descriptions.add(ScreenUI.shorten(player.getName(), 18) + " • OVR " + player.getOverall());
        }
        for (DraftPick pick : picks) descriptions.add(pickLabel(pick));
        return descriptions.isEmpty() ? "Nenhum ativo" : String.join("\n", descriptions);
    }

    private String pickLabel(DraftPick pick) {
        if (pick == null) return "Pick não definida";
        String via = pick.getOriginalOwner() != null
            ? " via " + ScreenUI.shorten(pick.getOriginalOwner().getName(), 13)
            : "";
        return pick.getRound() + "ª rodada " + pick.getYear() + " • geral #" +
            pick.getProjectedOverallPosition() + via;
    }

    private List<Player> filteredPlayers() {
        List<Player> players = new ArrayList<>();
        String normalizedQuery = normalize(query);
        int season = game.league.getCurrentSeason();

        for (Club club : game.league.getClubs()) {
            if (club == null) continue;
            if ("LIGA".equals(ownerScope) && club == userClub) continue;
            if ("MEUS".equals(ownerScope) && club != userClub) continue;
            for (Player player : club.getSquad()) {
                reconcileRosterOwner(player, club);
                if (
                    player == null ||
                        player.getCurrentClub() != club ||
                        player.isFreeAgent(season) ||
                        !matchesPosition(player)
                ) {
                    continue;
                }
                if (!normalizedQuery.isEmpty()) {
                    String secondary = player.getSecondaryPosition() != null
                        ? player.getSecondaryPosition().name()
                        : "";
                    String searchable = normalize(
                        player.getName() + " " + club.getName() + " " + player.getNationality() +
                            " " + player.getPosition() + " " + secondary + " " + player.getAge()
                    );
                    if (!searchable.contains(normalizedQuery)) continue;
                }
                if (!TradeMarketSearchEvaluator.matchesProfile(
                    marketProfile, userClub, club, player, season
                )) continue;
                players.add(player);
            }
        }

        Comparator<Player> comparator;
        if (!normalizedQuery.isEmpty()) {
            comparator = Comparator.comparingInt((Player player) -> queryRelevance(player, normalizedQuery)).reversed()
                .thenComparing(Comparator.comparingInt(this::fitScore).reversed())
                .thenComparing(Comparator.comparingInt(Player::getOverall).reversed());
        } else if ("ENCAIXE".equals(sortMode)) {
            comparator = Comparator.comparingInt(this::fitScore).reversed()
                .thenComparing(Comparator.comparingInt(this::availabilityScore).reversed())
                .thenComparing(Comparator.comparingInt(Player::getOverall).reversed());
        } else if ("DISPON.".equals(sortMode)) {
            comparator = Comparator.comparingInt(this::availabilityScore).reversed()
                .thenComparing(Comparator.comparingInt(this::fitScore).reversed());
        } else if ("IDADE".equals(sortMode)) {
            comparator = Comparator.comparingInt(Player::getAge)
                .thenComparing(Comparator.comparingInt(Player::getOverall).reversed());
        } else if ("OVR".equals(sortMode)) {
            comparator = Comparator.comparingInt(Player::getOverall).reversed()
                .thenComparing(Player::getName);
        } else {
            comparator = Comparator.comparingLong(this::perceivedValue).reversed()
                .thenComparing(Comparator.comparingInt(Player::getOverall).reversed());
        }
        players.sort(comparator);
        return players;
    }

    private void reconcileRosterOwner(Player player, Club rosterClub) {
        if (player == null || rosterClub == null || player.getCurrentClub() == rosterClub) return;
        Club linked = player.getCurrentClub();
        if (linked == null || !linked.getSquad().contains(player)) player.setCurrentClub(rosterClub);
    }

    private List<DraftPick> filteredPicks() {
        List<DraftPick> picks = new ArrayList<>();
        String normalizedQuery = normalize(query);
        for (Club club : game.league.getClubs()) {
            if (club == null) continue;
            if ("LIGA".equals(ownerScope) && club == userClub) continue;
            if ("MEUS".equals(ownerScope) && club != userClub) continue;
            for (DraftPick pick : club.getDraftPicks()) {
                if (pick == null || pick.getCurrentOwner() != club || !pick.isAvailableForTrade(game.league)) continue;
                String originalName = pick.getOriginalOwner() != null
                    ? pick.getOriginalOwner().getName()
                    : "";
                String searchable = normalize(
                    club.getName() + " " + originalName + " " + pick.getYear()
                );
                if (!normalizedQuery.isEmpty() && !searchable.contains(normalizedQuery)) continue;
                picks.add(pick);
            }
        }

        Comparator<DraftPick> comparator;
        if ("ANO".equals(sortMode)) {
            comparator = Comparator.comparingInt(DraftPick::getYear)
                .thenComparingInt(DraftPick::getRound)
                .thenComparingInt(DraftPick::getProjectedPosition);
        } else if ("RODADA".equals(sortMode)) {
            comparator = Comparator.comparingInt(DraftPick::getRound)
                .thenComparingInt(DraftPick::getProjectedPosition)
                .thenComparingInt(DraftPick::getYear);
        } else {
            comparator = Comparator.comparingLong(this::perceivedPickValue).reversed();
        }
        picks.sort(comparator);
        return picks;
    }

    private boolean matchesPosition(Player player) {
        String position = player.getPosition();
        if ("TODOS".equals(positionFilter)) return true;
        if ("GK".equals(positionFilter)) return "GK".equals(position);
        if ("DEF".equals(positionFilter)) return position.matches("CB|LB|RB|LWB|RWB");
        if ("MEI".equals(positionFilter)) return position.matches("CDM|CM|CAM|LM|RM");
        return position.matches("LW|RW|CF|ST");
    }

    private boolean canNegotiate(Player player) {
        return player != null &&
            player.getCurrentClub() != null &&
            player.getCurrentClub() != userClub &&
            player.getTradeBlockedDays() == 0 &&
            !player.isFreeAgent(game.league.getCurrentSeason()) &&
            SeasonCalendar.isTradeWindowOpen(game.league, userClub) &&
            SeasonCalendar.isTradeWindowOpen(game.league, player.getCurrentClub());
    }

    private boolean canSearchTrade(Player player) {
        if (player == null || player.getCurrentClub() == null) return false;
        if (player.getTradeBlockedDays() > 0 || player.isFreeAgent(game.league.getCurrentSeason())) return false;
        if (!SeasonCalendar.isTradeWindowOpen(game.league, userClub)) return false;
        return player.getCurrentClub() == userClub
            || SeasonCalendar.isTradeWindowOpen(game.league, player.getCurrentClub());
    }

    private boolean canSearchTrade(DraftPick pick) {
        if (pick == null || pick.getCurrentOwner() == null) return false;
        if (!SeasonCalendar.isTradeWindowOpen(game.league, userClub)) return false;
        return pick.getCurrentOwner() == userClub
            || SeasonCalendar.isTradeWindowOpen(game.league, pick.getCurrentOwner());
    }

    private String availabilityText(Player player, boolean untouchable) {
        if (!SeasonCalendar.isTradeWindowOpen(game.league, userClub)) return "JANELA FECHADA";
        if (player.getTradeBlockedDays() > 0) return "BLOQUEADO • " + player.getTradeBlockedDays() + " DIAS";
        if (player.getCurrentClub() == userClub) {
            return untouchable ? "PEÇA CENTRAL • SUA DECISÃO" : "SEU ATIVO • PRONTO PARA BUSCA";
        }
        if (!SeasonCalendar.isTradeWindowOpen(game.league, player.getCurrentClub())) return "CLUBE INDISPONÍVEL";
        return untouchable ? "INTOCÁVEL • PREÇO ELEVADO" : "DISPONÍVEL PARA PROPOSTAS";
    }

    private long perceivedValue(Player player) {
        return SmartTradeEvaluator.getPerceivedPlayerValue(
            userClub,
            player,
            game.league.getCurrentSeason()
        );
    }

    private int fitScore(Player player) {
        return TradeMarketSearchEvaluator.fitScore(userClub, player);
    }

    private int availabilityScore(Player player) {
        if (player == null || player.getCurrentClub() == null) return 0;
        if (player.getCurrentClub() == userClub) return 100;
        return TradeMarketSearchEvaluator.availabilityScore(
            player.getCurrentClub(), player, game.league.getCurrentSeason()
        );
    }

    private String marketLabel(Player player) {
        if (player.getCurrentClub() == userClub) return "SEU ATIVO";
        return TradeMarketSearchEvaluator.availabilityLabel(
            player.getCurrentClub(), player, game.league.getCurrentSeason()
        );
    }

    private Color marketColor(Player player) {
        int score = availabilityScore(player);
        if (score >= 76) return ScreenUI.SUCCESS;
        if (score >= 50) return StyleFactory.SOFT_YELLOW;
        return score > 0 ? ScreenUI.WARNING : ScreenUI.DANGER;
    }

    private Color scoreColor(int score) {
        if (score >= 72) return ScreenUI.SUCCESS;
        if (score >= 52) return StyleFactory.SOFT_YELLOW;
        return ScreenUI.MUTED_TEXT;
    }

    private int queryRelevance(Player player, String normalizedQuery) {
        String name = normalize(player.getName());
        String club = normalize(player.getCurrentClub().getName());
        if (name.equals(normalizedQuery)) return 5;
        if (name.startsWith(normalizedQuery)) return 4;
        for (String part : name.split("\\s+")) if (part.startsWith(normalizedQuery)) return 3;
        if (name.contains(normalizedQuery)) return 2;
        return club.startsWith(normalizedQuery) ? 1 : 0;
    }

    private long perceivedPickValue(DraftPick pick) {
        return DraftPickEvaluator.getPerceivedPickValue(
            userClub,
            pick,
            game.league.getCurrentSeason()
        );
    }

    private int resultCount(List<Player> players, List<DraftPick> picks) {
        return "JOGADORES".equals(assetType) ? players.size() : picks.size();
    }

    private String needStars(Player player) {
        Map<String, Integer> needs = ClubNeedEvaluator.calculatePositionNeeds(userClub);
        int need = needs.containsKey(player.getPosition()) ? needs.get(player.getPosition()) : 3;
        return ScreenUI.formatStars(need);
    }

    private String phaseLabel(Club club) {
        ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(club);
        switch (phase) {
            case CONTENDER: return "CANDIDATO AO TÍTULO";
            case BUYER: return "COMPRADOR";
            case SELLER: return "VENDEDOR";
            case REBUILDING:
            default: return "RECONSTRUÇÃO";
        }
    }

    private String contractText(Player player) {
        int years = Math.max(0, player.getRemainingContractYears(game.league.getCurrentSeason()));
        return years == 0 ? "EXPIRANDO" : years + " ANO" + (years == 1 ? "" : "S");
    }

    private Color contractColor(Player player) {
        int years = player.getRemainingContractYears(game.league.getCurrentSeason());
        return years <= 0 ? ScreenUI.DANGER : years == 1 ? ScreenUI.WARNING : ScreenUI.SUCCESS;
    }

    private String formatMoney(long amount) {
        if (amount >= 1_000_000L) {
            return String.format(Locale.US, "WFL$ %.1fM", amount / 1_000_000d);
        }
        return String.format(Locale.US, "WFL$ %.0fK", amount / 1_000d);
    }

    private void showPlayerDetails(Player player) {
        new PlayerDetailsDialog(
            game.skin,
            player,
            game.league.getCurrentSeason()
        ).show(stage);
    }

    private void addHeader(Table row, String text, float width, int align) {
        row.add(ScreenUI.createTableHeaderLabel(game.skin, text, align)).width(width);
    }

    private Label value(String text, Color color, int align) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, align);
        label.setEllipsis(true);
        return label;
    }

    private void addInfo(Table table, String label, String value, Color color) {
        table.add(ScreenUI.createSubtitle(game.skin, label)).left().expandX().padBottom(6f);
        table.add(ScreenUI.createBoldValue(game.skin, value, color, Align.right)).right().padBottom(6f).row();
    }

    private String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT)
            .trim();
    }

    private String countryCode(String nationality) {
        String country = normalize(nationality);
        switch (country) {
            case "argentina": return "ARG";
            case "alemanha": return "GER";
            case "brasil": return "BRA";
            case "espanha": return "ESP";
            case "italia": return "ITA";
            case "franca": return "FRA";
            case "inglaterra": return "ENG";
            case "pais de gales": return "WAL";
            case "escocia": return "SCO";
            case "irlanda": return "IRL";
            case "irlanda do norte": return "NIR";
            case "holanda": return "NED";
            case "portugal": return "POR";
            case "uruguai": return "URU";
            case "paraguai": return "PAR";
            case "chile": return "CHI";
            case "colombia": return "COL";
            case "mexico": return "MEX";
            case "estados unidos":
            case "eua": return "USA";
            case "canada": return "CAN";
            case "japao": return "JPN";
            case "coreia do sul": return "KOR";
            case "nigeria": return "NGA";
            case "camaroes": return "CMR";
            case "costa do marfim": return "CIV";
            case "australia": return "AUS";
            case "suecia": return "SWE";
            case "noruega": return "NOR";
            case "dinamarca": return "DEN";
            case "suica": return "SUI";
            case "turquia": return "TUR";
            case "russia": return "RUS";
            default:
                if (nationality == null || nationality.trim().isEmpty()) return "---";
                String clean = normalize(nationality).replaceAll("[^a-z]", "").toUpperCase(Locale.ROOT);
                return clean.length() <= 3 ? clean : clean.substring(0, 3);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { io.github.some_example_name.utils.ScrollPositionMemory.capture(stage, getClass().getName()); }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
