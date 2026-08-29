package io.github.some_example_name.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubProfile;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ManagerCareer;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.utils.ResponsiveViewport;
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
    private Club selectedClub;
    private String regionFilter = "TODAS";
    private String sortMode = "REPUTAÇÃO";

    public ClubSelectionScreen(Main game) {
        this.game = game;
        stage = new Stage(new ResponsiveViewport());
        starTexture = new Texture(Gdx.files.internal("Icons8/icons8-estrela-48.png"));
        starTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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
        page.top().left().pad(13f, 16f, 13f, 16f);
        page.add(topHeader()).growX().height(94f).padBottom(10f).row();
        Table body = new Table();
        body.add(clubBrowser()).width(635f).growY().padRight(10f);
        body.add(clubDetails()).grow();
        page.add(body).grow().padBottom(10f).row();
        page.add(footer()).growX().height(66f);
        border.add(page).grow();
        root.add(border);
    }

    private Table topHeader() {
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(Color.valueOf("07170F"), DIVIDER));
        header.pad(9f, 20f, 9f, 20f);
        Label brand = ScreenUI.createBoldValue(game.skin, "WFL", StyleFactory.SOFT_YELLOW, Align.center);
        brand.setFontScale(1.25f);
        header.add(brand).width(145f).center();
        header.add(new Image(StyleFactory.createSolid(DIVIDER))).width(1f).height(61f).pad(0f, 18f, 0f, 18f);

        Table title = new Table();
        title.left();
        Label main = ScreenUI.createSectionTitle(game.skin, isJobSearch() ? "MERCADO DE TREINADORES" : "SELEÇÃO DE CLUBE");
        main.setFontScale(.88f);
        title.add(main).left().row();
        Label sub = ScreenUI.createSubtitle(game.skin, isJobSearch()
            ? "Escolha uma franquia disponível para continuar sua carreira"
            : "Escolha a franquia para iniciar sua carreira");
        sub.setColor(StyleFactory.TEXT_SECONDARY);
        title.add(sub).left().padTop(3f);
        header.add(title).left().expandX();

        Table league = new Table();
        Label season = ScreenUI.createBoldValue(game.skin,
            "WFL  •  TEMPORADA " + game.league.getCurrentSeason(), StyleFactory.SOFT_YELLOW, Align.right);
        season.setFontScale(.72f);
        league.add(season).right().colspan(3).padBottom(7f).row();
        league.add(topPill("LIGA MUNDIAL FECHADA")).width(205f).height(29f).padRight(6f);
        league.add(topPill(game.league.getClubs().size() + " FRANQUIAS")).width(150f).height(29f).padRight(6f);
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
        Table tabs = new Table();
        TextButton clubs = ScreenUI.createPrimaryButton(game.skin, "CLUBES DA WFL");
        TextButton unemployed = ScreenUI.createInteractiveButton("DESEMPREGADO", game.skin);
        unemployed.setDisabled(!isJobSearch());
        unemployed.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (isJobSearch()) game.setScreen(new UnemployedScreen(game));
            }
        });
        tabs.add(clubs).growX().height(42f).padRight(7f);
        tabs.add(unemployed).growX().height(42f);
        panel.add(tabs).growX().height(44f).padBottom(9f).row();

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
        filters.add(labeledFilter("REGIÃO", region)).growX().height(49f).padRight(8f);
        filters.add(labeledFilter("ORDENAR", sort)).growX().height(49f);
        panel.add(filters).growX().padBottom(8f).row();

        Table columns = new Table();
        columns.background(StyleFactory.createSolid(Color.valueOf("111B15"))).pad(4f, 8f, 4f, 8f);
        columns.add(smallHeader("CLUBE", Align.left)).width(300f).left();
        columns.add(smallHeader("REGIÃO", Align.center)).width(105f).center();
        columns.add(smallHeader("EXPECTATIVA DA DIRETORIA", Align.right)).growX().right();
        panel.add(columns).growX().height(29f).row();

        List<Club> visible = filteredClubs();
        Table list = new Table();
        list.top().left();
        for (int i = 0; i < visible.size(); i++) list.add(clubRow(visible.get(i), i)).growX().height(52f).row();
        ScrollPane scroll = new ScrollPane(list, game.skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setOverscroll(false, false);
        panel.add(scroll).grow().row();
        Label count = ScreenUI.createSubtitle(game.skin,
            "MOSTRANDO " + visible.size() + " DE "
                + (isJobSearch() ? game.managerCareer.getJobOffers(game.league).size() : game.league.getClubs().size())
                + (isJobSearch() ? " VAGAS" : " FRANQUIAS"));
        count.setAlignment(Align.center);
        panel.add(count).growX().height(30f).center();
        return panel;
    }

    private Table labeledFilter(String title, SelectBox<String> select) {
        Table box = new Table();
        box.background(StyleFactory.createRoundedPanel(Color.valueOf("101A14"), DIVIDER)).pad(4f, 8f, 4f, 8f);
        Label label = ScreenUI.createSubtitle(game.skin, title);
        label.setFontScale(.43f);
        box.add(label).width(68f).left();
        box.add(select).grow().height(38f);
        return box;
    }

    private Label smallHeader(String text, int align) {
        return bold(text, StyleFactory.TEXT_MUTED, align, .38f);
    }

    private List<Club> filteredClubs() {
        List<Club> result = new ArrayList<>();
        for (Club club : game.league.getClubs())
            if (("TODAS".equals(regionFilter) || regionFilter.equalsIgnoreCase(club.getConference()))
                && (!isJobSearch() || game.managerCareer.hasOfferFrom(game.league, club))) result.add(club);
        Comparator<Club> comparator;
        if ("NOME".equals(sortMode)) comparator = Comparator.comparing(Club::getName, String.CASE_INSENSITIVE_ORDER);
        else if ("OVERALL".equals(sortMode)) comparator = Comparator.comparingDouble(Club::getOverall).reversed();
        else comparator = Comparator.comparingInt(Club::getReputation).reversed();
        Collections.sort(result, comparator);
        return result;
    }

    private Table clubRow(final Club club, final int index) {
        final Table row = new Table();
        final boolean selected = club == selectedClub;
        setRowBackground(row, selected, false, index);
        row.pad(4f, 8f, 4f, 8f).setTouchable(Touchable.enabled);
        row.add(bold(selected ? "●" : "○", selected ? StyleFactory.GOLD : StyleFactory.TEXT_MUTED,
            Align.center, .55f)).width(28f);
        row.add(logo(club)).width(38f).height(38f).padRight(8f);
        Label name = bold(club.getName(), selected ? StyleFactory.SOFT_YELLOW : StyleFactory.CREME_AGED,
            Align.left, .50f);
        name.setEllipsis(true);
        row.add(name).width(226f).left();
        Label region = bold(countryCode(club.getCountry()) + "  " + club.getConference(),
            StyleFactory.TEXT_SECONDARY, Align.center, .40f);
        region.setEllipsis(true);
        row.add(region).width(112f);
        ManagerCareer.JobOffer offer = isJobSearch()
            ? game.managerCareer.getOfferFrom(game.league, club)
            : null;
        String expectation = offer == null
            ? ClubProfile.forClub(club).boardObjective
            : offer.getContractYears() + " ANOS • " + ClubProfile.forClub(club).boardObjective;
        Label objective = bold(expectation,
            selected ? StyleFactory.SOFT_YELLOW : StyleFactory.TEXT_SECONDARY, Align.right, .39f);
        objective.setEllipsis(true);
        row.add(objective).growX().right();
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
        details.add(hero(profile)).growX().height(244f).padBottom(9f).row();
        Table middle = new Table();
        middle.add(identityPanel(profile)).grow().padRight(7f);
        middle.add(infrastructurePanel()).width(245f).growY().padRight(7f);
        middle.add(financePanel()).width(250f).growY().padRight(7f);
        middle.add(stylePanel()).width(225f).growY();
        details.add(middle).growX().height(194f).padBottom(9f).row();
        Table bottom = new Table();
        bottom.add(squadPanel()).width(360f).growY().padRight(7f);
        bottom.add(expectationsPanel(profile)).width(300f).growY().padRight(7f);
        bottom.add(rivalriesPanel(profile)).grow();
        details.add(bottom).grow();
        return details;
    }

    private Table hero(ClubProfile profile) {
        Table hero = contentPanel();
        hero.pad(12f);
        Table crest = contentPanel();
        crest.add(logo(selectedClub)).width(164f).height(164f);
        hero.add(crest).width(190f).growY().padRight(14f);
        Table main = new Table();
        main.top().left();
        Label name = ScreenUI.createSectionTitle(game.skin, selectedClub.getName().toUpperCase());
        name.setFontScale(.95f);
        main.add(name).growX().left().row();
        main.add(bold(profile.tagline, StyleFactory.GOLD, Align.left, .52f)).growX().left().padTop(3f).padBottom(8f).row();
        main.add(line()).growX().height(1f).padBottom(9f).row();
        Table facts = new Table();
        facts.add(heroFact("PAÍS / REGIÃO", countryCode(selectedClub.getCountry()) + "  " + selectedClub.getCountry()
            + " • " + selectedClub.getConference())).growX().left();
        facts.add(heroFact("CIDADE-SEDE", profile.city)).growX().left();
        facts.add(heroFact("FUNDAÇÃO", String.valueOf(profile.founded))).width(105f).left();
        main.add(facts).growX().padBottom(10f).row();
        Table status = new Table();
        Table rep = new Table();
        rep.left().add(smallHeader("REPUTAÇÃO MUNDIAL", Align.left)).left().row();
        rep.add(stars(reputationStars(selectedClub.getReputation()), 20f)).left().padTop(4f);
        status.add(rep).width(185f).left();
        status.add(heroFact("OBJETIVO DA DIRETORIA", profile.boardObjective)).growX().left();
        main.add(status).growX();
        hero.add(main).grow().padRight(13f);
        hero.add(uniformPanel(profile)).width(365f).growY();
        return hero;
    }

    private Table heroFact(String title, String data) {
        Table fact = new Table();
        fact.left().add(smallHeader(title, Align.left)).growX().left().row();
        Label value = bold(data, StyleFactory.CREME_AGED, Align.left, .43f);
        value.setEllipsis(true);
        fact.add(value).growX().left().padTop(3f);
        return fact;
    }

    private Table uniformPanel(ClubProfile profile) {
        Table panel = contentPanel();
        panel.pad(7f);
        panel.add(uniformSlot(profile, "home", "CASA", Color.WHITE)).grow().padRight(5f);
        panel.add(uniformSlot(profile, "away", "FORA", StyleFactory.GOLD)).grow().padRight(5f);
        panel.add(uniformSlot(profile, "third", "ALTERNATIVO", Color.valueOf("D7DDD8"))).grow();
        return panel;
    }

    private Table uniformSlot(ClubProfile profile, String type, String label, Color tint) {
        Table slot = new Table();
        slot.bottom();
        String path = findUniform(profile.uniformKey, type);
        Image shirt = textureImage(path == null ? "Icons8/icons8-camisa-de-jogador-50.png" : path);
        if (path == null) shirt.setColor(tint);
        slot.add(shirt).width(103f).height(133f).row();
        Table badge = new Table();
        badge.background(StyleFactory.createRoundedPanel(Color.valueOf("302A10"), DIVIDER));
        badge.add(bold(label, StyleFactory.CREME_AGED, Align.center, .38f));
        slot.add(badge).growX().height(29f).padTop(4f);
        return slot;
    }

    private String findUniform(String key, String type) {
        String[] paths = {"uniforms/" + key + "_" + type + ".png",
            "uniforme_" + key + ("home".equals(type) ? "" : "_" + type) + ".png", key + "_" + type + ".png"};
        for (String path : paths) if (Gdx.files.internal(path).exists()) return path;
        return null;
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
        row.add(stars(member == null ? 0 : member.getEffectLevel(), 10f)).width(60f).right();
        panel.add(row).colspan(2).growX().height(27f).row();
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

    private Table expectationsPanel(ClubProfile profile) {
        Table panel = sectionPanel("EXPECTATIVAS");
        for (String item : profile.expectations) {
            Label bullet = bold("•  " + item, StyleFactory.CREME_AGED, Align.left, .42f);
            bullet.setWrap(true);
            panel.add(bullet).growX().left().height(34f).row();
        }
        panel.add(smallHeader("CONFIANÇA DA DIRETORIA", Align.left)).growX().left().padTop(8f).row();
        panel.add(value(selectedClub.getReputation() >= 90 ? "CONFIANTE" : "CAUTELOSA", ScreenUI.SUCCESS, Align.left)).left();
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

    private Table rivalriesPanel(ClubProfile profile) {
        Table panel = sectionPanel("RIVALIDADES / DESTAQUES");
        for (int i = 0; i < profile.rivals.length; i++) {
            Club rival = findClub(profile.rivals[i]);
            Table row = new Table();
            if (rival != null) row.add(logo(rival)).width(37f).height(37f).padRight(8f);
            Table text = new Table();
            text.left().add(value(profile.rivals[i], StyleFactory.CREME_AGED, Align.left)).growX().left().row();
            Label type = ScreenUI.createSubtitle(game.skin, i == 0 ? "Rivalidade histórica" : "Clássico internacional");
            type.setFontScale(.42f);
            text.add(type).left();
            row.add(text).growX().left();
            row.add(intensityBlocks(5 - i)).width(105f).right();
            panel.add(row).colspan(3).growX().height(48f).row();
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
        panel.top().left().pad(9f, 10f, 9f, 10f);
        panel.add(bold(title, StyleFactory.GOLD, Align.left, .49f)).colspan(2).growX().left().padBottom(5f).row();
        panel.add(line()).colspan(2).growX().height(1f).padBottom(4f).row();
        return panel;
    }

    private Table contentPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(Color.valueOf("0B1912"), DIVIDER));
        return panel;
    }

    private Image line() { return new Image(StyleFactory.createSolid(DIVIDER)); }

    private void valueRow(Table panel, String title, String data, Color color) {
        panel.add(value(title, StyleFactory.TEXT_SECONDARY, Align.left)).growX().left().height(31f);
        panel.add(value(data, color, Align.right)).right().height(31f).row();
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
        TextButton rules = ScreenUI.createInteractiveButton("REGRAS DA CARREIRA", game.skin);
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
        Dialog dialog = new Dialog("LIGA MUNDIAL DE FUTEBOL", game.skin);
        dialog.text("20 franquias • Liga fechada • Sem rebaixamento\n\nTemporada regular, playoffs e Off Season completa.\nA escolha define a franquia controlada pelo usuário.");
        dialog.button("FECHAR").show(stage);
    }

    private void showConfirmation() {
        final Club choice = selectedClub;
        final Dialog dialog = new Dialog(isJobSearch() ? "CONFIRMAR NOVO CARGO" : "CONFIRMAR FRANQUIA", game.skin);
        dialog.text(choice.getName().toUpperCase() + "\n\n" + ClubProfile.forClub(choice).tagline
            + (isJobSearch()
                ? "\n\nContrato oferecido: " + game.managerCareer.getOfferFrom(game.league, choice).getContractYears()
                    + " temporadas.\nDeseja assumir esta franquia?"
                : "\n\nDeseja iniciar sua carreira com esta franquia?"));
        TextButton confirm = ScreenUI.createPrimaryButton(game.skin, isJobSearch() ? "CONFIRMAR NOVO CARGO" : "CONFIRMAR E INICIAR");
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                dialog.hide();
                game.selectPlayerClub(choice);
                game.setScreen(
                    "OFFSEASON".equals(game.league.getCurrentStage())
                        ? new OffSeasonScreen(game, choice)
                        : new ClubManagementScreen(game, choice)
                );
            }
        });
        dialog.button("VOLTAR");
        dialog.button(confirm);
        dialog.show(stage);
    }

    private boolean isJobSearch() {
        return game.playerClub == null
            && game.managerCareer.isUnemployed()
            && !game.managerCareer.getHistory().isEmpty();
    }

    private Table stars(int rating, float size) {
        Table table = new Table();
        for (int i = 0; i < 5; i++) {
            Image star = new Image(new TextureRegionDrawable(new TextureRegion(starTexture)));
            star.setScaling(Scaling.fit);
            star.setColor(i < rating ? StyleFactory.GOLD : Color.valueOf("424A43"));
            table.add(star).width(size).height(size).padRight(2f);
        }
        return table;
    }

    private int reputationStars(int rep) { return rep >= 94 ? 5 : rep >= 90 ? 4 : rep >= 85 ? 3 : rep >= 80 ? 2 : 1; }

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
            ? new Image(StyleFactory.createSolid(Color.valueOf("253329"))) : textureImage(club.getLogoPath());
    }

    private Image textureImage(String path) {
        Texture texture = textures.get(path);
        if (texture == null) {
            texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.put(path, texture);
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
