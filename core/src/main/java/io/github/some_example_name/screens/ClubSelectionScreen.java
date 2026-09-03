package io.github.some_example_name.screens;

import io.github.some_example_name.utils.ClubLogoAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.BoardObjective;
import io.github.some_example_name.model.BoardObjectiveService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubProfile;
import io.github.some_example_name.model.LeagueExpansionService;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ManagerCareer;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ClubUniformAssets;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Painel de escolha e apresentação das vinte franquias da WFL. */
public class ClubSelectionScreen implements Screen {
    private static final Color PAGE_GREEN = Color.valueOf("06150E");
    private static final Color CARD = Color.valueOf("0D1913");
    private static final Color CARD_ALT = Color.valueOf("111F17");
    private static final Color CARD_SELECTED = Color.valueOf("3A3212");
    private static final Color DIVIDER = Color.valueOf("5A4A18");

    private final Main game;
    private final Stage stage;
    private final Texture starTexture;
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, Drawable> uniformDrawables = new HashMap<>();
    private Club selectedClub;
    private String regionFilter = "TODAS";
    private String sortMode = "REPUTAÇÃO";
    private boolean expansionCatalog;
    private final List<Club> expansionPreviews = new ArrayList<>();

    public ClubSelectionScreen(Main game) {
        this.game = game;
        stage = new Stage(new ResponsiveViewport());
        starTexture = ScreenUI.loadTintableIcon("Icons8/icons8-estrela-48.png");
        starTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        for (LeagueExpansionService.Franchise f : LeagueExpansionService.allFranchises())
            expansionPreviews.add(LeagueExpansionService.createClub(f));
        List<Club> clubs = game.league.getClubs();
        if (isJobSearch()) {
            List<ManagerCareer.JobOffer> offers = game.managerCareer.getJobOffers(game.league);
            selectedClub = offers.isEmpty() ? null : offers.get(0).getClub();
        } else {
            selectedClub = game.playerClub != null ? game.playerClub : (clubs.isEmpty() ? null : clubs.get(0));
        }
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); refreshUI(); }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);
        Image background = new Image(game.background);
        background.setColor(.32f, .42f, .35f, 1f);
        root.add(background);
        root.add(new Image(StyleFactory.createSolid(new Color(0f, .035f, .018f, .82f))));

        Table border = new Table();
        border.background(StyleFactory.createRoundedPanel(PAGE_GREEN, StyleFactory.GOLD));
        border.pad(2f);
        Table page = new Table();
        page.top().left().pad(15f, 18f, 15f, 18f);
        page.add(topHeader()).growX().height(100f).padBottom(12f).row();
        Table body = new Table();
        body.add(clubBrowser()).width(675f).growY().padRight(12f);
        body.add(clubDetails()).grow();
        page.add(body).grow().padBottom(12f).row();
        page.add(footer()).growX().height(70f);
        border.add(page).grow();
        root.add(border);
    }

    private Table topHeader() {
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(Color.valueOf("07170F"), DIVIDER));
        header.pad(11f, 22f, 11f, 22f);
        Label brand = ScreenUI.createBoldValue(game.skin, "WFL", StyleFactory.SOFT_YELLOW, Align.center);
        brand.setFontScale(1.35f);
        header.add(brand).width(138f).center();
        header.add(new Image(StyleFactory.createSolid(DIVIDER))).width(1f).height(64f).pad(0f, 20f, 0f, 20f);

        Table title = new Table();
        title.left();
        Label main = ScreenUI.createSectionTitle(game.skin, isJobSearch() ? "MERCADO DE TREINADORES" : "SELEÇÃO DE CLUBE");
        main.setFontScale(.94f);
        title.add(main).left().row();
        Label sub = ScreenUI.createSubtitle(game.skin, isJobSearch()
            ? "Escolha uma franquia disponível para continuar sua carreira"
            : expansionCatalog ? "Simule a história e assuma na off-season antes da estreia"
            : "Escolha a franquia para iniciar sua carreira");
        sub.setColor(StyleFactory.TEXT_SECONDARY);
        title.add(sub).left().padTop(3f);
        header.add(title).left().expandX();

        Table league = new Table();
        Label season = ScreenUI.createBoldValue(game.skin,
            expansionCatalog && selectedClub != null ? "EXPANSÃO WFL  •  ESTREIA " + selectedClub.getStartYear()
                : "WFL  •  TEMPORADA " + game.league.getCurrentSeason(), StyleFactory.SOFT_YELLOW, Align.right);
        season.setFontScale(.76f);
        league.add(season).right().colspan(3).padBottom(7f).row();
        league.add(topPill("LIGA MUNDIAL FECHADA")).width(205f).height(29f).padRight(6f);
        int clubCount = expansionCatalog && selectedClub != null
            ? LeagueExpansionService.projectedClubCount(selectedClub.getStartYear()) : game.league.getClubs().size();
        league.add(topPill(clubCount + " FRANQUIAS")).width(150f).height(29f).padRight(6f);
        league.add(topPill("SEM REBAIXAMENTO")).width(170f).height(29f);
        header.add(league).right();
        return header;
    }

    private Table topPill(String text) {
        Table pill = new Table();
        pill.background(StyleFactory.createRoundedPanel(Color.valueOf("142017"), DIVIDER));
        Label label = bold(text, StyleFactory.CREME_AGED, Align.center, .43f);
        pill.add(label).center();
        return pill;
    }

    private Table clubBrowser() {
        Table panel = new Table();
        panel.top().background(StyleFactory.createRoundedPanel(Color.valueOf("08150F"), DIVIDER)).pad(10f);
        Table catalog = new Table();
        catalog.background(StyleFactory.createRoundedPanel(Color.valueOf("322A0D"), StyleFactory.GOLD));
        catalog.pad(8f, 14f, 8f, 14f);
        Label catalogTitle = bold(
            isJobSearch() ? "FRANQUIAS COM VAGA" : expansionCatalog ? "FRANQUIAS DE EXPANSÃO" : "CLUBES DA WFL",
            StyleFactory.SOFT_YELLOW,
            Align.left,
            .55f
        );
        Label catalogHint = ScreenUI.createSubtitle(
            game.skin,
            isJobSearch() ? "ESCOLHA SEU PRÓXIMO PROJETO" : expansionCatalog ? "ESTREIAS DE 1974 A 1990" : "FRANQUIAS FUNDADORAS"
        );
        catalogHint.setFontScale(.45f);
        catalog.add(catalogTitle).left().expandX();
        catalog.add(catalogHint).right();
        panel.add(catalog).growX().height(52f).padBottom(10f).row();

        if (!isJobSearch() && game.league.getCurrentSeason() == 1969) {
            Table tabs = new Table();
            for (boolean expansion : new boolean[]{false, true}) {
                String caption = expansion ? "EXPANSÕES • 10 CLUBES" : "FUNDADORES • 1969";
                TextButton tab = expansion == expansionCatalog ? ScreenUI.createPrimaryButton(game.skin, caption)
                    : ScreenUI.createSecondaryButton(game.skin, caption);
                tab.getLabel().setFontScale(.48f);
                tab.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent e, float x, float y) {
                        expansionCatalog = expansion;
                        List<Club> visible = filteredClubs();
                        selectedClub = visible.isEmpty() ? null : visible.get(0);
                        refreshUI();
                    }
                });
                tabs.add(tab).growX().uniformX().height(43f).pad(2f);
            }
            panel.add(tabs).growX().padBottom(8f).row();
        }

        Table filters = new Table();
        final SelectBox<String> region = ScreenUI.createSelectBox(game.skin);
        region.setItems("TODAS", "OCIDENTAL", "ORIENTAL");
        region.setSelected(regionFilter);
        region.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                regionFilter = region.getSelected(); refreshUI();
            }
        });
        final SelectBox<String> sort = ScreenUI.createSelectBox(game.skin);
        sort.setItems("REPUTAÇÃO", "OVERALL", "NOME");
        sort.setSelected(sortMode);
        sort.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                sortMode = sort.getSelected(); refreshUI();
            }
        });
        filters.add(labeledFilter("REGIÃO", region)).growX().height(56f).padRight(10f);
        filters.add(labeledFilter("ORDENAR", sort)).growX().height(56f);
        panel.add(filters).growX().padBottom(10f).row();

        Table columns = new Table();
        columns.background(StyleFactory.createSolid(Color.valueOf("111B15"))).pad(6f, 10f, 6f, 10f);
        columns.add(smallHeader("CLUBE", Align.left)).width(338f).left();
        columns.add(smallHeader("REGIÃO", Align.center)).width(116f).center();
        columns.add(smallHeader(expansionCatalog ? "ESTREIA NA WFL" : "EXPECTATIVA DA DIRETORIA", Align.right)).growX().right();
        panel.add(columns).growX().height(34f).row();

        List<Club> visible = filteredClubs();
        Table list = new Table();
        list.top().left();
        int previousYear = -1;
        for (int i = 0; i < visible.size(); i++) {
            Club club = visible.get(i);
            if (expansionCatalog && club.getStartYear() != previousYear) {
                previousYear = club.getStartYear();
                list.add(bold("EXPANSÃO " + previousYear + " • ASSUMA EM NOV/" + (previousYear - 1),
                    StyleFactory.SOFT_YELLOW, Align.left, .45f)).growX().height(32f).padTop(5f).row();
            }
            list.add(clubRow(club, i)).growX().height(64f).row();
        }
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setOverscroll(false, false);
        panel.add(scroll).grow().row();
        Label count = ScreenUI.createSubtitle(game.skin,
            "MOSTRANDO " + visible.size() + " DE "
                + (isJobSearch() ? game.managerCareer.getJobOffers(game.league).size() : expansionCatalog ? expansionPreviews.size() : game.league.getClubs().size())
                + (isJobSearch() ? " VAGAS" : " FRANQUIAS"));
        count.setAlignment(Align.center);
        panel.add(count).growX().height(34f).center();
        return panel;
    }

    private Table labeledFilter(String title, SelectBox<String> select) {
        Table box = new Table();
        box.background(StyleFactory.createRoundedPanel(Color.valueOf("101A14"), DIVIDER)).pad(6f, 10f, 6f, 10f);
        Label label = ScreenUI.createSubtitle(game.skin, title);
        label.setFontScale(.43f);
        box.add(label).width(72f).left().padRight(6f);
        box.add(select).grow().height(40f);
        return box;
    }

    private Label smallHeader(String text, int align) {
        return bold(text, StyleFactory.TEXT_MUTED, align, .38f);
    }

    private List<Club> filteredClubs() {
        List<Club> result = new ArrayList<>();
        for (Club club : expansionCatalog ? expansionPreviews : game.league.getClubs())
            if (("TODAS".equals(regionFilter) || regionFilter.equalsIgnoreCase(club.getConference()))
                && (!isJobSearch() || game.managerCareer.hasOfferFrom(game.league, club))) result.add(club);
        Comparator<Club> comparator;
        if ("NOME".equals(sortMode)) comparator = Comparator.comparing(Club::getName, String.CASE_INSENSITIVE_ORDER);
        else if ("OVERALL".equals(sortMode)) comparator = Comparator.comparingDouble(Club::getOverall).reversed();
        else comparator = Comparator.comparingInt(Club::getReputation).reversed();
        if (expansionCatalog) comparator = Comparator.comparingInt(Club::getStartYear).thenComparing(comparator);
        Collections.sort(result, comparator);
        return result;
    }

    private Table clubRow(final Club club, final int index) {
        final Table row = new Table();
        final boolean selected = club == selectedClub;
        setRowBackground(row, selected, false, index);
        row.pad(6f, 10f, 6f, 10f).setTouchable(Touchable.enabled);
        row.add(bold(selected ? "●" : "○", selected ? StyleFactory.GOLD : StyleFactory.TEXT_MUTED,
            Align.center, .55f)).width(30f);
        row.add(logo(club)).width(46f).height(46f).padRight(12f);
        Label name = bold(club.getName(), selected ? StyleFactory.SOFT_YELLOW : StyleFactory.CREME_AGED,
            Align.left, .54f);
        name.setEllipsis(true);
        row.add(name).width(250f).left().padRight(8f);
        Label region = bold(countryCode(club.getCountry()) + "  " + club.getConference(),
            StyleFactory.TEXT_SECONDARY, Align.center, .40f);
        region.setEllipsis(true);
        row.add(region).width(116f).padRight(8f);
        ManagerCareer.JobOffer offer = isJobSearch()
            ? game.managerCareer.getOfferFrom(game.league, club)
            : null;
        String expectation = offer == null
            ? expansionCatalog ? String.valueOf(club.getStartYear()) : primaryBoardObjective(club)
            : offer.getContractYears() + " ANOS • " + primaryBoardObjective(club);
        Label objective = bold(expectation,
            selected ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_SECONDARY, Align.right, .39f);
        objective.setEllipsis(false);
        objective.setWrap(true);
        row.add(objective).growX().right().padLeft(6f);
        row.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                if (selectedClub != club) { selectedClub = club; refreshUI(); }
            }
            @Override public void enter(InputEvent e, float x, float y, int p, Actor from) {
                setRowBackground(row, club == selectedClub, true, index);
            }
            @Override public void exit(InputEvent e, float x, float y, int p, Actor to) {
                setRowBackground(row, club == selectedClub, false, index);
            }
        });
        return row;
    }

    private void setRowBackground(Table row, boolean selected, boolean hover, int index) {
        Color fill = selected ? CARD_SELECTED : hover ? StyleFactory.PANEL_HOVER : index % 2 == 0 ? CARD : CARD_ALT;
        Color edge = selected ? StyleFactory.GOLD : hover ? StyleFactory.DARK_GOLD : Color.valueOf("243229");
        row.background(StyleFactory.createRoundedPanel(fill, edge));
    }

    private Table clubDetails() {
        Table details = new Table();
        details.top().left();
        if (selectedClub == null) { details.add(contentPanel()).grow(); return details; }
        ClubProfile profile = ClubProfile.forClub(selectedClub);
        details.add(hero(profile)).growX().height(250f).padBottom(10f).row();
        Table middle = new Table();
        middle.add(identityPanel(profile)).grow().padRight(7f);
        middle.add(infrastructurePanel()).width(245f).growY().padRight(7f);
        middle.add(financePanel()).width(250f).growY().padRight(7f);
        middle.add(stylePanel()).width(225f).growY();
        details.add(middle).growX().height(206f).padBottom(10f).row();
        Table bottom = new Table();
        bottom.add(squadPanel()).width(360f).growY().padRight(7f);
        bottom.add(expectationsPanel()).width(360f).growY().padRight(7f);
        bottom.add(rivalriesPanel(profile)).grow();
        details.add(bottom).grow();
        return details;
    }

    private Table hero(ClubProfile profile) {
        Table hero = contentPanel();
        hero.pad(12f);
        Table crest = contentPanel();
        crest.add(logo(selectedClub)).width(184f).height(184f);
        hero.add(crest).width(210f).growY().padRight(14f);
        Table main = new Table();
        main.top().left();
        Label name = ScreenUI.createSectionTitle(game.skin, selectedClub.getName().toUpperCase());
        name.setFontScale(1.02f);
        main.add(name).growX().left().row();
        main.add(bold(profile.tagline, StyleFactory.GOLD, Align.left, .52f)).growX().left().padTop(5f).padBottom(10f).row();
        main.add(line()).growX().height(1f).padBottom(11f).row();
        Table facts = new Table();
        facts.add(heroFact("PAÍS / REGIÃO", countryCode(selectedClub.getCountry()) + "  " + selectedClub.getCountry()
            + " • " + selectedClub.getConference())).growX().left();
        facts.add(heroFact("CIDADE-SEDE", profile.city)).growX().left();
        facts.add(heroFact("FUNDAÇÃO", String.valueOf(profile.founded))).width(105f).left();
        main.add(facts).growX().padBottom(12f).row();
        Table status = new Table();
        Table rep = new Table();
        rep.left().add(smallHeader("REPUTAÇÃO MUNDIAL", Align.left)).left().row();
        rep.add(stars(reputationStars(selectedClub.getReputation()), 20f)).left().padTop(4f);
        status.add(rep).width(185f).left();
        status.add(heroFact("OBJETIVO CRÍTICO DA DIRETORIA", primaryBoardObjective(selectedClub))).growX().left();
        main.add(status).growX();
        hero.add(main).grow().padRight(13f);
        hero.add(uniformPanel()).width(220f).growY();
        return hero;
    }

    private Table heroFact(String title, String data) {
        Table fact = new Table();
        fact.left().add(smallHeader(title, Align.left)).growX().left().row();
        Label value = bold(data, StyleFactory.CREME_AGED, Align.left, .43f);
        value.setEllipsis(true);
        fact.add(value).growX().left().padTop(5f);
        return fact;
    }

    private Table uniformPanel() {
        Table panel = contentPanel();
        panel.pad(7f);
        String path = ClubUniformAssets.forClub(selectedClub);
        Drawable uniform = uniformDrawables.get(path);
        if (uniform == null) {
            textureImage(path); // O cache da tela continua responsável por descartar a textura.
            uniform = ClubUniformAssets.drawable(textures.get(path));
            uniformDrawables.put(path, uniform);
        }
        Image shirt = new Image(uniform);
        shirt.setScaling(Scaling.fit);
        panel.add(shirt).width(170f).height(174f).expand().center().row();
        Table badge = new Table();
        badge.background(StyleFactory.createRoundedPanel(Color.valueOf("302A10"), DIVIDER));
        badge.add(bold("UNIFORME", StyleFactory.CREME_AGED, Align.center, .38f));
        panel.add(badge).growX().height(29f).padTop(4f);
        return panel;
    }

    private Table identityPanel(ClubProfile profile) {
        Table panel = sectionPanel("IDENTIDADE DO CLUBE");
        Label text = ScreenUI.createSubtitle(game.skin, profile.identity);
        text.setColor(StyleFactory.TEXT_SECONDARY);
        text.setWrap(true);
        text.setAlignment(Align.topLeft);
        panel.add(text).grow().top().left().padTop(9f);
        return panel;
    }

    private Table infrastructurePanel() {
        Table panel = sectionPanel("EQUIPE TÉCNICA");
        StaffRole[] displayOrder = {
            StaffRole.COACH,
            StaffRole.SCOUT,
            StaffRole.DEVELOPMENT_DIRECTOR,
            StaffRole.FITNESS_COACH,
            StaffRole.DOCTOR
        };
        for (StaffRole role : displayOrder) {
            staffRow(panel, role, selectedClub.getStaffMember(role));
        }
        return panel;
    }

    private void staffRow(Table panel, StaffRole role, StaffMember member) {
        Table row = new Table();
        Label roleLabel = bold(shortRole(role), StyleFactory.TEXT_MUTED, Align.left, .34f);
        row.add(roleLabel).width(58f).left();

        String memberName = member == null ? "Cargo vago" : member.getName();
        Label name = bold(memberName, StyleFactory.CREME_AGED, Align.left, .36f);
        name.setEllipsis(true);
        row.add(name).growX().left().padRight(3f);
        row.add(stars(member == null ? 0f : member.getDisplayRating(), 10f)).width(60f).right();
        panel.add(row).colspan(2).growX().height(30f).row();
    }

    private String shortRole(StaffRole role) {
        switch (role) {
            case COACH: return "TÉCNICO";
            case SCOUT: return "SCOUT";
            case DEVELOPMENT_DIRECTOR: return "DESENV.";
            case FITNESS_COACH: return "FÍSICO";
            case DOCTOR: return "MÉDICO";
            default: return role.getLabel().toUpperCase();
        }
    }

    private Table financePanel() {
        Table panel = sectionPanel("FINANÇAS");
        valueRow(panel, "Caixa", money(selectedClub.getFinance().getBalance()), ScreenUI.SUCCESS);
        valueRow(panel, "Salary Cap", money(selectedClub.getFinance().getSalaryCap()), StyleFactory.SOFT_YELLOW);
        valueRow(panel, "Folha salarial", money(selectedClub.getFinance().getAnnualPayroll()), StyleFactory.CREME_AGED);
        long balance = selectedClub.getFinance().getBalance();
        valueRow(panel, "Poder econômico", balance >= 60_000_000L ? "Muito forte" : balance >= 40_000_000L ? "Forte" : "Estável", ScreenUI.SUCCESS);
        return panel;
    }

    private Table stylePanel() {
        Table panel = sectionPanel("ESTILO DA FRANQUIA");
        float attack = (selectedClub.getMentalityValue() + selectedClub.getTempo()) / 2f;
        float defense = (100f - selectedClub.getMentalityValue() + selectedClub.getPressure()) / 2f;
        float passing = Math.max(45f, (float) selectedClub.getOverall() - 10f);
        float intensity = (selectedClub.getTempo() + selectedClub.getPressure()) / 2f;
        valueRow(panel, "Ataque", level(attack), levelColor(attack));
        valueRow(panel, "Passe", level(passing), levelColor(passing));
        valueRow(panel, "Defesa", level(defense), levelColor(defense));
        valueRow(panel, "Intensidade", level(intensity), levelColor(intensity));
        return panel;
    }

    private Table squadPanel() {
        Table panel = sectionPanel("ELENCO");
        if (isExpansionPreview(selectedClub)) {
            valueRow(panel, "Estreia na WFL", String.valueOf(selectedClub.getStartYear()), StyleFactory.SOFT_YELLOW);
            Label details = value("Comece na off-season de " + (selectedClub.getStartYear() - 1)
                + ".\n\nO elenco será formado pelo Expansion Draft, Free Agency e Draft regular."
                + "\n\nUma escolha na 1ª rodada e uma na 2ª rodada do Draft inaugural.", StyleFactory.CREME_AGED, Align.left);
            details.setEllipsis(false);
            details.setWrap(true);
            panel.add(details).colspan(2).growX().padTop(12f);
            return panel;
        }
        valueRow(panel, "Tamanho do elenco", selectedClub.getSquad().size() + " jogadores", StyleFactory.CREME_AGED);
        valueRow(panel, "Idade média", String.format(Locale.US, "%.1f anos", averageAge()), StyleFactory.CREME_AGED);
        valueRow(panel, "Overall médio", String.valueOf(Math.round(selectedClub.getOverall())), StyleFactory.SOFT_YELLOW);
        panel.add(smallHeader("JOGADORES-CHAVE", Align.left)).colspan(2).growX().left().padTop(5f).padBottom(3f).row();
        List<Player> leaders = keyPlayers();
        for (int i = 0; i < leaders.size(); i++) {
            Player p = leaders.get(i);
            panel.add(value((i + 1) + "  " + p.getName(), StyleFactory.CREME_AGED, Align.left)).growX().height(26f);
            panel.add(value(p.getPrimaryPosition() + "  " + p.getOverall(), StyleFactory.SOFT_YELLOW, Align.right)).height(26f).row();
        }
        return panel;
    }

    private Table expectationsPanel() {
        Table panel = sectionPanel("OBJETIVOS DA DIRETORIA");
        List<BoardObjective> objectives = BoardObjectiveService.objectivesFor(selectedClub);
        for (BoardObjective objective : objectives) {
            Table row = new Table();
            Label priority = bold(
                ScreenUI.formatActiveStars(objective.getPriority().getStars()),
                priorityColor(objective.getPriority()),
                Align.center,
                .40f
            );
            row.add(priority).width(40f).left().padRight(5f);

            Label title = bold(
                objective.getTitle(),
                StyleFactory.CREME_AGED,
                Align.left,
                .39f
            );
            title.setEllipsis(false);
            title.setWrap(true);
            row.add(title).growX().left();
            panel.add(row).colspan(2).growX().height(44f).padBottom(2f).row();
        }
        panel.add(smallHeader("CONFIANÇA DA DIRETORIA", Align.left)).growX().left().padTop(8f).row();
        BoardObjectiveService.Evaluation evaluation =
            BoardObjectiveService.evaluate(game.league, selectedClub);
        panel.add(value(
            evaluation.getStatus(),
            confidenceColor(evaluation.getConfidence()),
            Align.left
        )).left();
        if (isJobSearch()) {
            ManagerCareer.JobOffer offer = game.managerCareer.getOfferFrom(game.league, selectedClub);
            if (offer != null) {
                panel.row();
                panel.add(smallHeader("OFERTA", Align.left)).growX().left().padTop(7f).row();
                panel.add(value(
                    offer.getContractYears() + " TEMPORADAS",
                    StyleFactory.SOFT_YELLOW,
                    Align.left
                )).left();
            }
        }
        return panel;
    }

    private String primaryBoardObjective(Club club) {
        List<BoardObjective> objectives = BoardObjectiveService.objectivesFor(club);
        if (objectives.isEmpty()) return "Construir uma temporada competitiva";
        for (BoardObjective objective : objectives) {
            if (objective.getPriority() == BoardObjective.Priority.CRITICAL) {
                return objective.getTitle();
            }
        }
        return objectives.get(0).getTitle();
    }

    private Color priorityColor(BoardObjective.Priority priority) {
        if (priority == BoardObjective.Priority.CRITICAL) return StyleFactory.SOFT_YELLOW;
        if (priority == BoardObjective.Priority.IMPORTANT) return StyleFactory.CREME_AGED;
        return StyleFactory.TEXT_MUTED;
    }

    private Color confidenceColor(int confidence) {
        if (confidence >= 65) return ScreenUI.SUCCESS;
        if (confidence >= 45) return StyleFactory.SOFT_YELLOW;
        if (confidence >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private Table rivalriesPanel(ClubProfile profile) {
        Table panel = sectionPanel("RIVALIDADES / DESTAQUES");
        for (int i = 0; i < profile.rivals.length; i++) {
            Club rival = findClub(profile.rivals[i]);
            Table row = new Table();
            if (rival != null) row.add(logo(rival)).width(44f).height(44f).padRight(9f);
            Table text = new Table();
            text.left().add(value(profile.rivals[i], StyleFactory.CREME_AGED, Align.left)).growX().left().row();
            int rivalryLevel = rival == null ? Math.max(1, 5 - i) : ClubProfile.rivalryLevel(selectedClub, rival);
            Label type = ScreenUI.createSubtitle(game.skin,
                rival == null ? "Rivalidade da WFL" : ClubProfile.rivalryLabel(selectedClub, rival));
            type.setFontScale(.42f);
            text.add(type).left();
            row.add(text).growX().left();
            row.add(intensityBlocks(rivalryLevel)).width(105f).right();
            panel.add(row).colspan(3).growX().height(54f).row();
        }
        panel.add(line()).colspan(3).growX().height(1f).padTop(5f).padBottom(6f).row();
        panel.add(trophyBox(profile.wflTitles + selectedClub.getTitlesCount(), "TÍTULOS WFL")).growX();
        panel.add(trophyBox(profile.worldCups, "COPAS MUNDIAIS")).growX();
        panel.add(trophyBox(profile.regionalTitles, "TÍTULOS REGIONAIS")).growX();
        return panel;
    }

    private Table intensityBlocks(int active) {
        Table table = new Table();
        for (int i = 0; i < 6; i++)
            table.add(new Image(StyleFactory.createSolid(i < active ? Color.valueOf("E64832") : Color.valueOf("4A3028"))))
                .width(13f).height(8f).padRight(2f);
        return table;
    }

    private Table trophyBox(int amount, String text) {
        Table box = new Table();
        box.add(bold(String.valueOf(amount), StyleFactory.SOFT_YELLOW, Align.center, .67f)).row();
        box.add(smallHeader(text, Align.center)).padTop(2f);
        return box;
    }

    private Table sectionPanel(String title) {
        Table panel = contentPanel();
        panel.top().left().pad(11f, 12f, 11f, 12f);
        panel.add(bold(title, StyleFactory.GOLD, Align.left, .49f)).colspan(2).growX().left().padBottom(7f).row();
        panel.add(line()).colspan(2).growX().height(1f).padBottom(7f).row();
        return panel;
    }

    private Table contentPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(Color.valueOf("0B1912"), DIVIDER));
        return panel;
    }

    private Image line() { return new Image(StyleFactory.createSolid(DIVIDER)); }

    private void valueRow(Table panel, String title, String data, Color color) {
        panel.add(value(title, StyleFactory.TEXT_SECONDARY, Align.left)).growX().left().height(34f).padRight(8f);
        panel.add(value(data, color, Align.right)).right().height(34f).row();
    }

    private Label value(String text, Color color, int align) { return bold(text, color, align, .42f); }

    private Label bold(String text, Color color, int align, float scale) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, align);
        label.setFontScale(scale);
        label.setEllipsis(true);
        return label;
    }

    private Table footer() {
        Table footer = new Table();
        footer.background(StyleFactory.createRoundedPanel(Color.valueOf("07150E"), DIVIDER)).pad(7f, 12f, 7f, 12f);
        TextButton back = ScreenUI.createInteractiveButton("<  VOLTAR", game.skin);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(isJobSearch() ? new UnemployedScreen(game) : new MenuScreen(game));
            }
        });
        footer.add(back).width(205f).height(49f).left().expandX();
        TextButton rules = ScreenUI.createInteractiveButton("REGRAS DA WFL", game.skin);
        rules.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { showLeagueRules(); }
        });
        footer.add(rules).width(230f).height(49f).padRight(12f);
        TextButton start = ScreenUI.createPrimaryButton(game.skin, isJobSearch() ? "ASSUMIR FRANQUIA  >" : "INICIAR CARREIRA  >");
        start.setDisabled(selectedClub == null);
        start.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { if (selectedClub != null) showConfirmation(); }
        });
        footer.add(start).width(350f).height(52f);
        return footer;
    }

    private void showLeagueRules() {
        LeagueRulesDialog.show(stage, game);
    }

    private void showConfirmation() {
        final Club choice = selectedClub;
        if (choice == null) return;

        ClubProfile profile = ClubProfile.forClub(choice);
        final Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setTouchable(Touchable.enabled);
        overlay.background(StyleFactory.createSolid(new Color(0f, 0.015f, 0.009f, 0.72f)));
        // Impede que um clique no fundo acione elementos da seleção de clubes.
        overlay.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { event.stop(); }
        });

        Table modal = new Table();
        modal.background(StyleFactory.createRoundedPanel(Color.valueOf("0B2017"), StyleFactory.GOLD));
        modal.pad(24f, 30f, 24f, 30f);

        Table heading = new Table();
        heading.add(line()).growX().height(1f).padRight(14f);
        Label headingLabel = ScreenUI.createSectionTitle(
            game.skin,
            isJobSearch() ? "CONFIRMAR NOVO CARGO" : "CONFIRMAR FRANQUIA"
        );
        headingLabel.setFontScale(0.68f);
        headingLabel.setAlignment(Align.center);
        heading.add(headingLabel).center();
        heading.add(line()).growX().height(1f).padLeft(14f);
        modal.add(heading).growX().height(38f).padBottom(18f).row();

        Table identity = new Table();
        Table crest = new Table();
        crest.background(StyleFactory.createRoundedPanel(Color.valueOf("10271C"), DIVIDER));
        crest.add(logo(choice)).size(128f);
        identity.add(crest).size(146f).padRight(24f);

        Table copy = new Table();
        copy.left();
        Label clubName = ScreenUI.createBoldValue(
            game.skin,
            choice.getName().toUpperCase(),
            StyleFactory.TEXT_PRIMARY,
            Align.left
        );
        clubName.setFontScale(0.88f);
        clubName.setEllipsis(true);
        copy.add(clubName).growX().left().row();
        Label tagline = ScreenUI.createSubtitle(game.skin, profile.tagline);
        tagline.setColor(StyleFactory.TEXT_SECONDARY);
        tagline.setFontScale(0.57f);
        tagline.setWrap(true);
        copy.add(tagline).width(405f).left().padTop(8f);
        identity.add(copy).growX().left();
        modal.add(identity).growX().height(150f).padBottom(17f).row();

        Table facts = new Table();
        facts.add(confirmationFact("REGIÃO", countryCode(choice.getCountry()) + " • " + choice.getConference()))
            .width(180f).height(72f).padRight(10f);
        facts.add(confirmationFact("REPUTAÇÃO", reputationLabel(choice.getReputation())))
            .width(180f).height(72f).padRight(10f);
        facts.add(confirmationFact("OBJETIVO INICIAL", confirmationObjective(choice)))
            .width(190f).height(72f);
        modal.add(facts).growX().padBottom(18f).row();

        modal.add(line()).growX().height(1f).padBottom(16f).row();
        Label question = ScreenUI.createValueLabel(
            game.skin,
            isJobSearch()
                ? "Deseja assumir o comando desta franquia?"
                : isExpansionPreview(choice) ? "Simular 1969–" + (choice.getStartYear() - 1)
                    + " e assumir na off-season?" : "Deseja iniciar sua carreira com esta franquia?",
            StyleFactory.TEXT_PRIMARY,
            Align.center
        );
        question.setFontScale(0.62f);
        modal.add(question).center().padBottom(18f).row();

        Table actions = new Table();
        TextButton back = ScreenUI.createSecondaryButton(game.skin, "VOLTAR");
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { overlay.remove(); }
        });
        actions.add(back).width(185f).height(54f).padRight(16f);

        TextButton confirm = ScreenUI.createPrimaryButton(
            game.skin,
            isJobSearch() ? "CONFIRMAR NOVO CARGO" : "CONFIRMAR E INICIAR"
        );
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                overlay.remove();
                if (isExpansionPreview(choice)) {
                    game.setScreen(new ExpansionCareerLoadingScreen(game, choice.getName()));
                    return;
                }
                game.selectPlayerClub(choice);
                game.setScreen(
                    "OFFSEASON".equals(game.league.getCurrentStage())
                        ? new OffSeasonScreen(game, choice)
                        : new ClubManagementScreen(game, choice)
                );
            }
        });
        actions.add(confirm).width(315f).height(54f);
        modal.add(actions).center();

        overlay.add(modal).width(650f).height(480f);
        stage.addActor(overlay);
    }

    private Table confirmationFact(String title, String data) {
        Table fact = new Table();
        fact.background(StyleFactory.createRoundedPanel(Color.valueOf("10251B"), DIVIDER));
        fact.pad(9f, 12f, 9f, 12f);
        Label titleLabel = smallHeader(title, Align.left);
        titleLabel.setColor(StyleFactory.GOLD);
        fact.add(titleLabel).growX().left().row();
        Label valueLabel = bold(data, StyleFactory.TEXT_PRIMARY, Align.left, 0.48f);
        valueLabel.setEllipsis(false);
        valueLabel.setWrap(true);
        fact.add(valueLabel).growX().left().padTop(5f);
        return fact;
    }

    private String reputationLabel(int reputation) {
        if (reputation >= 94) return "Mundial";
        if (reputation >= 88) return "Alta";
        if (reputation >= 80) return "Tradicional";
        return "Em crescimento";
    }

    private String confirmationObjective(Club club) {
        String objective = primaryBoardObjective(club);
        String normalized = objective.toLowerCase(Locale.ROOT);
        if (normalized.contains("campe")) return "Ser campeão";
        if (normalized.contains("final")) return "Disputar a final";
        if (normalized.contains("playoff")) return "Playoffs";
        return ScreenUI.shorten(objective, 28);
    }

    private boolean isExpansionPreview(Club club) {
        return club != null && expansionPreviews.contains(club);
    }

    private boolean isJobSearch() {
        return game.playerClub == null
            && game.managerCareer.isUnemployed()
            && !game.managerCareer.getHistory().isEmpty();
    }

    private Table stars(float rating, float size) {
        return ScreenUI.createStarRating(starTexture, rating, size);
    }

    private float reputationStars(int rep) {
        if (rep >= 97) return 5f;
        if (rep >= 94) return 4.5f;
        if (rep >= 90) return 4f;
        if (rep >= 88) return 3.5f;
        if (rep >= 85) return 3f;
        if (rep >= 82) return 2.5f;
        if (rep >= 80) return 2f;
        return 1.5f;
    }

    private List<Player> keyPlayers() {
        List<Player> result = new ArrayList<>(selectedClub.getSquad());
        Collections.sort(result, Comparator.comparingInt(Player::getOverall).reversed());
        return result.subList(0, Math.min(3, result.size()));
    }

    private double averageAge() {
        return selectedClub.getSquad().stream().mapToInt(Player::getAge).average().orElse(0d);
    }

    private Club findClub(String name) {
        for (Club club : game.league.getClubs()) if (club.getName().equals(name)) return club;
        return null;
    }

    private Image logo(Club club) {
        return club == null || club.getLogoPath() == null || !Gdx.files.internal(club.getLogoPath()).exists()
            ? new Image(StyleFactory.createSolid(Color.valueOf("253329"))) : textureImage(club.getLogoPath(), true);
    }

    private Image textureImage(String path) {
        return textureImage(path, false);
    }

    private Image textureImage(String path, boolean clubLogo) {
        String cacheKey = clubLogo ? "logo:" + path : path;
        Texture texture = textures.get(cacheKey);
        if (texture == null) {
            texture = clubLogo ? ClubLogoAssets.load(path) : new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.put(cacheKey, texture);
        }
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setScaling(Scaling.fit);
        return image;
    }

    private String countryCode(String country) {
        if (country == null) return "WFL";
        switch (country) {
            case "Brasil": return "BRA"; case "Itália": return "ITA"; case "Alemanha": return "ALE";
            case "Inglaterra": return "ING"; case "Holanda": return "HOL"; case "Espanha": return "ESP";
            case "Hungria": return "HUN"; case "Portugal": return "POR"; case "Argentina": return "ARG";
            case "Uruguai": return "URU"; case "França": return "FRA"; case "Irlanda do Norte": return "NIR";
            case "Japão": return "JPN"; case "Coreia do Sul": return "KOR"; case "Irã": return "IRN";
            case "Iraque": return "IRQ"; case "Israel": return "ISR";
            default: return country.substring(0, Math.min(3, country.length())).toUpperCase();
        }
    }

    private String money(long value) {
        return value >= 1_000_000L ? String.format(Locale.US, "WFL$ %.1fM", value / 1_000_000d)
            : String.format(Locale.US, "WFL$ %.0fK", value / 1_000d);
    }

    private String level(float v) { return v >= 70f ? "ALTO" : v >= 58f ? "MÉDIO-ALTO" : v >= 42f ? "MÉDIO" : "BAIXO"; }
    private Color levelColor(float v) { return v >= 70f ? ScreenUI.SUCCESS : v >= 58f ? StyleFactory.SOFT_YELLOW : v >= 42f ? ScreenUI.WARNING : ScreenUI.DANGER; }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        stage.dispose(); starTexture.dispose();
        for (Texture texture : textures.values()) texture.dispose();
        textures.clear();
    }
}
