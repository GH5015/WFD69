package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonHistory;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClubProfileScreen implements Screen {

    private final Main game;
    private final Club club;

    private final Stage stage;

    private Texture logoTexture;
    private Texture starTexture;

    private String activeTab = "RESUMO";

    public ClubProfileScreen(
        Main game,
        Club club
    ) {

        this.game = game;
        this.club = club;

        this.stage =
            new Stage(
                new ScreenViewport()
            );

        logoTexture =
            loadTextureOrNull(
                club.getLogoPath()
            );

        starTexture =
            loadTextureOrNull(
                "Icons8/icons8-estrela-48.png"
            );
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

        root.add(
            new Image(
                game.background
            )
        );

        Table page =
            ScreenUI.createPage(
                true
            );

        // =====================================================
        // HEADER
        // =====================================================

        Table header =
            ScreenUI.createHeader(
                game.skin,
                "PERFIL DO CLUBE",
                club.getConference() != null
                    ? club.getConference().toUpperCase()
                    : ""
            );

        page
            .add(header)
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        // =====================================================
        // IDENTIDADE DO CLUBE
        // =====================================================

        page
            .add(
                createIdentityPanel()
            )
            .growX()
            .height(105f)
            .padBottom(10f)
            .row();

        // =====================================================
        // ABAS
        // =====================================================

        page
            .add(
                createTabs()
            )
            .growX()
            .height(48f)
            .padBottom(10f)
            .row();

        // =====================================================
        // CONTEÚDO
        // =====================================================

        Table tabContent =
            new Table();

        if (
            "RESUMO".equals(
                activeTab
            )
        ) {

            tabContent.add(
                createSummaryTab()
            ).grow();

        } else if (
            "HISTÓRIA".equals(
                activeTab
            )
        ) {

            tabContent.add(
                createHistoryTab()
            ).grow();

        } else {

            tabContent.add(
                createInfrastructureTab()
            ).grow();
        }

        page
            .add(tabContent)
            .grow()
            .row();

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            club,
            "PERFIL",
            true
        );

    }

    // =========================================================
    // IDENTIDADE
    // =========================================================

    private Table createIdentityPanel() {

        Table panel =
            ScreenUI.createPanel();

        if (
            logoTexture != null
        ) {

            Image logo =
                new Image(
                    new TextureRegionDrawable(
                        logoTexture
                    )
                );

            logo.setScaling(
                Scaling.fit
            );

            panel
                .add(logo)
                .width(130f)
                .height(78f)
                .padRight(18f);
        }

        Table identity =
            new Table();

        identity.left();

        Label clubName =
            new Label(
                club.getName().toUpperCase(),
                game.skin,
                "font-title"
            );

        clubName.setFontScale(
            0.90f
        );

        clubName.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(clubName)
            .left()
            .row();

        String country =
            club.getCountry() != null
                ? club.getCountry()
                : "N/D";

        Label sub =
            new Label(
                country +
                    "  •  " +
                    "WFL  •  " +
                    "Desde 1969",
                game.skin
            );

        sub.setFontScale(
            0.62f
        );

        sub.setColor(
            ScreenUI.MUTED_TEXT
        );

        identity
            .add(sub)
            .left()
            .padTop(3f)
            .row();

        Label stadium =
            new Label(
                club.getStadium() +
                    "  •  " +
                    formatNumber(
                        club.getStadiumCapacity()
                    ) +
                    " lugares",
                game.skin
            );

        stadium.setFontScale(
            0.60f
        );

        stadium.setColor(
            StyleFactory.CREME_AGED
        );

        identity
            .add(stadium)
            .left()
            .padTop(5f);

        panel
            .add(identity)
            .left()
            .expandX();

        // =====================================================
        // OVERALL
        // =====================================================

        Table overallBox =
            ScreenUI.createStatusBox(
                game.skin,
                "OVERALL",
                String.valueOf(
                    (int) Math.round(
                        club.getOverall()
                    )
                ),
                StyleFactory.GOLD
            );

        panel
            .add(overallBox)
            .width(180f)
            .height(52f)
            .padRight(8f);

        // =====================================================
        // ELENCO
        // =====================================================

        Table squadBox =
            ScreenUI.createStatusBox(
                game.skin,
                "ELENCO",
                club.getSquad().size() + "/26",
                club.getSquad().size() >= 23
                    ? ScreenUI.SUCCESS
                    : ScreenUI.WARNING
            );

        panel
            .add(squadBox)
            .width(180f)
            .height(52f);

        return panel;
    }

    // =========================================================
    // TABS
    // =========================================================

    private Table createTabs() {

        Table tabs =
            new Table();

        String[] names = {
            "RESUMO",
            "HISTÓRIA",
            "INFRAESTRUTURA"
        };

        for (
            String tab :
            names
        ) {

            TextButton button =
                ScreenUI.createInteractiveButton(
                    tab,
                    game.skin
                );

            boolean selected =
                tab.equals(
                    activeTab
                );

            button.getLabel()
                .setFontScale(
                    0.62f
                );

            button.setColor(
                selected
                    ? StyleFactory.GOLD
                    : StyleFactory.METAL_DARK
            );

            button.getLabel()
                .setColor(
                    selected
                        ? Color.BLACK
                        : Color.WHITE
                );

            button.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        activeTab =
                            tab;

                        refreshUI();
                    }
                }
            );

            tabs
                .add(button)
                .width(220f)
                .height(42f)
                .padRight(8f);
        }

        tabs
            .add()
            .expandX();

        return tabs;
    }

    // =========================================================
    // RESUMO
    // =========================================================

    private Table createSummaryTab() {

        Table root =
            new Table();

        root.top();

        // =====================================================
        // COLUNA ESQUERDA
        // =====================================================

        Table left =
            ScreenUI.createPanel();

        left.top();

        Label overviewTitle =
            ScreenUI.createSectionTitle(
                game.skin,
                "IDENTIDADE"
            );

        left
            .add(overviewTitle)
            .left()
            .padBottom(12f)
            .row();

        if (
            logoTexture != null
        ) {

            Image logo =
                new Image(
                    new TextureRegionDrawable(
                        logoTexture
                    )
                );

            logo.setScaling(
                Scaling.fit
            );

            left
                .add(logo)
                .width(240f)
                .height(150f)
                .center()
                .padBottom(14f)
                .row();
        }

        left
            .add(
                ScreenUI.createDivider()
            )
            .growX()
            .height(1f)
            .padBottom(14f)
            .row();

        left
            .add(
                createInfoRow(
                    "REPUTAÇÃO",
                    createStarsWidget(
                        normalizeReputation()
                    )
                )
            )
            .growX()
            .padBottom(10f)
            .row();

        left
            .add(
                createTextInfoRow(
                    "FILOSOFIA",
                    club.getPhilosophy()
                )
            )
            .growX()
            .padBottom(10f)
            .row();

        left
            .add(
                createTextInfoRow(
                    "MENTALIDADE",
                    club.getMentality()
                )
            )
            .growX()
            .padBottom(10f)
            .row();

        left
            .add(
                createTextInfoRow(
                    "FORMAÇÃO",
                    club.getFormation() != null
                        ? club.getFormation().getName()
                        : "Não definida"
                )
            )
            .growX()
            .row();

        root
            .add(left)
            .width(340f)
            .growY()
            .padRight(12f);

        // =====================================================
        // COLUNA DIREITA
        // =====================================================

        Table right =
            new Table();

        right.top();

        // =====================================================
        // CARDS
        // =====================================================

        Table stats =
            new Table();

        stats.add(
            createBigStat(
                "OVERALL",
                String.valueOf(
                    (int) Math.round(
                        club.getOverall()
                    )
                ),
                StyleFactory.GOLD
            )
        ).growX().uniformX().padRight(8f);

        stats.add(
            createBigStat(
                "JOGADORES",
                String.valueOf(
                    club.getSquad().size()
                ),
                Color.WHITE
            )
        ).growX().uniformX().padRight(8f);

        stats.add(
            createBigStat(
                "ESTÁDIO",
                compactNumber(
                    club.getStadiumCapacity()
                ),
                StyleFactory.SOFT_YELLOW
            )
        ).growX().uniformX();

        right
            .add(stats)
            .growX()
            .padBottom(12f)
            .row();

        // =====================================================
        // INFORMAÇÕES GERAIS
        // =====================================================

        Table info =
            ScreenUI.createPanel();

        info.top();

        info
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "INFORMAÇÕES GERAIS"
                )
            )
            .left()
            .colspan(2)
            .padBottom(12f)
            .row();

        addInfoLine(
            info,
            "País",
            club.getCountry()
        );

        addInfoLine(
            info,
            "Conferência",
            club.getConference()
        );

        addInfoLine(
            info,
            "Estádio",
            club.getStadium()
        );

        addInfoLine(
            info,
            "Capacidade",
            formatNumber(
                club.getStadiumCapacity()
            )
        );

        addInfoLine(
            info,
            "Filosofia",
            club.getPhilosophy()
        );

        addInfoLine(
            info,
            "Mentalidade",
            club.getMentality()
        );

        right
            .add(info)
            .growX()
            .padBottom(12f)
            .row();

        // =====================================================
        // DESTAQUES DO ELENCO
        // =====================================================

        right
            .add(
                createSquadHighlights()
            )
            .grow()
            .row();

        root
            .add(right)
            .grow();

        return root;
    }

    private Table createSquadHighlights() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "DESTAQUES DO ELENCO"
                )
            )
            .left()
            .colspan(4)
            .padBottom(10f)
            .row();

        Table header =
            ScreenUI.createTableHeaderRow();

        addTableHeader(
            header,
            "POS",
            70f,
            Align.center
        );

        addTableHeader(
            header,
            "JOGADOR",
            250f,
            Align.left
        );

        addTableHeader(
            header,
            "OVR",
            70f,
            Align.center
        );

        addTableHeader(
            header,
            "IDADE",
            70f,
            Align.center
        );

        panel
            .add(header)
            .growX()
            .colspan(4)
            .height(44f)
            .row();

        List<Player> players =
            new ArrayList<>(
                club.getSquad()
            );

        players.sort(
            Comparator
                .comparingInt(
                    Player::getOverall
                )
                .reversed()
        );

        int limit =
            Math.min(
                6,
                players.size()
            );

        for (
            int i = 0;
            i < limit;
            i++
        ) {

            Player p =
                players.get(i);

            Table row =
                ScreenUI.createRow(
                    i
                );

            Table badge =
                ScreenUI.createBadge(
                    game.skin,
                    p.getPosition(),
                    StyleFactory.getPositionColor(
                        p.getPosition()
                    )
                );

            row
                .add(badge)
                .width(70f)
                .height(30f);

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        p.getName(),
                        Color.WHITE,
                        Align.left
                    )
                )
                .width(250f)
                .padLeft(8f);

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        String.valueOf(
                            p.getOverall()
                        ),
                        StyleFactory.SOFT_YELLOW,
                        Align.center
                    )
                )
                .width(70f);

            row
                .add(
                    ScreenUI.createValueLabel(
                        game.skin,
                        String.valueOf(
                            p.getAge()
                        ),
                        ScreenUI.MUTED_TEXT,
                        Align.center
                    )
                )
                .width(70f);

            panel
                .add(row)
                .growX()
                .colspan(4)
                .height(48f)
                .row();
        }

        return panel;
    }

    // =========================================================
    // HISTÓRIA
    // =========================================================

    private Table createHistoryTab() {

        Table root =
            new Table();

        root.top();

        Table quickStats =
            new Table();

        quickStats.add(
            createBigStat(
                "JOGOS",
                String.valueOf(
                    club.getTotalGames()
                ),
                Color.WHITE
            )
        ).growX().uniformX().padRight(8f);

        quickStats.add(
            createBigStat(
                "VITÓRIAS",
                String.valueOf(
                    club.getTotalWins()
                ),
                ScreenUI.SUCCESS
            )
        ).growX().uniformX().padRight(8f);

        quickStats.add(
            createBigStat(
                "EMPATES",
                String.valueOf(
                    club.getTotalDraws()
                ),
                ScreenUI.WARNING
            )
        ).growX().uniformX().padRight(8f);

        quickStats.add(
            createBigStat(
                "DERROTAS",
                String.valueOf(
                    club.getTotalLosses()
                ),
                ScreenUI.DANGER
            )
        ).growX().uniformX();

        root
            .add(quickStats)
            .growX()
            .padBottom(12f)
            .row();

        Table lower =
            new Table();

        // =====================================================
        // RECORDES
        // =====================================================

        Table records =
            ScreenUI.createPanel();

        records.top();

        records
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "RECORDES"
                )
            )
            .left()
            .colspan(2)
            .padBottom(12f)
            .row();

        addRecord(
            records,
            "Maior invencibilidade",
            club.getMaxUnbeatenStreak() +
                " jogos"
        );

        addRecord(
            records,
            "Maior vitória",
            club.getBiggestWin()
        );

        addRecord(
            records,
            "Maior artilheiro",
            club.getTopScorerName() +
                " • " +
                club.getTopScorerGoals() +
                " gols"
        );

        addRecord(
            records,
            "Mais jogos",
            club.getMostGamesPlayerName() +
                " • " +
                club.getMostGamesCount()
        );

        addRecord(
            records,
            "Mais assistências",
            club.getTopAssisterName() +
                " • " +
                club.getTopAssisterCount()
        );

        int saldo =
            club.getGoalDifference();

        addRecord(
            records,
            "Saldo histórico",
            (saldo > 0 ? "+" : "") +
                saldo
        );

        lower
            .add(records)
            .width(430f)
            .growY()
            .padRight(12f);

        // =====================================================
        // TEMPORADAS
        // =====================================================

        Table seasons =
            ScreenUI.createPanel();

        seasons.top();

        seasons
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "HISTÓRICO DE TEMPORADAS"
                )
            )
            .left()
            .padBottom(12f)
            .row();

        Table table =
            new Table();

        Table tableHeader =
            ScreenUI.createTableHeaderRow();

        addTableHeader(
            tableHeader,
            "ANO",
            90f,
            Align.center
        );

        addTableHeader(
            tableHeader,
            "LIGA",
            200f,
            Align.center
        );

        addTableHeader(
            tableHeader,
            "COPA",
            200f,
            Align.center
        );

        table
            .add(tableHeader)
            .growX()
            .height(44f)
            .row();

        addSeasonRow(
            table,
            club.getCurrentYear(),
            "Em andamento",
            "Em andamento",
            0
        );

        int index =
            1;

        for (
            SeasonHistory history :
            club.getSeasonHistories()
        ) {

            addSeasonRow(
                table,
                history.getYear(),
                history.getLigaResult(),
                history.getCopaResult(),
                index++
            );
        }

        ScrollPane scroll =
            new ScrollPane(
                table,
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        seasons
            .add(scroll)
            .grow();

        lower
            .add(seasons)
            .grow();

        root
            .add(lower)
            .grow()
            .row();

        return root;
    }

    // =========================================================
    // INFRA
    // =========================================================

    private Table createInfrastructureTab() {

        Table root =
            new Table();

        root.top();

        Table cards =
            new Table();

        cards.add(
            createInfrastructureCard(
                "CENTRO DE TREINAMENTO",
                3,
                "Estrutura responsável pelo desenvolvimento técnico e físico."
            )
        ).grow().uniformX().padRight(10f);

        cards.add(
            createInfrastructureCard(
                "CATEGORIAS DE BASE",
                2,
                "Formação e descoberta de novos jogadores."
            )
        ).grow().uniformX().padRight(10f);

        cards.add(
            createInfrastructureCard(
                "DEPARTAMENTO MÉDICO",
                4,
                "Recuperação física e tratamento de lesões."
            )
        ).grow().uniformX();

        root
            .add(cards)
            .growX()
            .height(235f)
            .padBottom(12f)
            .row();

        Table stadium =
            ScreenUI.createPanel();

        stadium.top();

        stadium
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "ESTÁDIO"
                )
            )
            .left()
            .colspan(2)
            .padBottom(12f)
            .row();

        addInfoLine(
            stadium,
            "Nome",
            club.getStadium()
        );

        addInfoLine(
            stadium,
            "Capacidade",
            formatNumber(
                club.getStadiumCapacity()
            ) +
                " pessoas"
        );

        addInfoLine(
            stadium,
            "Condição do gramado",
            "Excelente"
        );

        addInfoLine(
            stadium,
            "Clube",
            club.getName()
        );

        root
            .add(stadium)
            .growX()
            .row();

        return root;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Table createBigStat(
        String title,
        String value,
        Color valueColor
    ) {

        Table card =
            ScreenUI.createPanel();

        Label titleLabel =
            new Label(
                title,
                game.skin,
                "font-bold"
            );

        titleLabel.setFontScale(
            0.52f
        );

        titleLabel.setColor(
            ScreenUI.MUTED_TEXT
        );

        Label valueLabel =
            new Label(
                value,
                game.skin,
                "font-title"
            );

        valueLabel.setFontScale(
            0.78f
        );

        valueLabel.setColor(
            valueColor
        );

        card
            .add(titleLabel)
            .center()
            .row();

        card
            .add(valueLabel)
            .center()
            .padTop(4f);

        return card;
    }

    private Table createInfrastructureCard(
        String title,
        int rating,
        String description
    ) {

        Table card =
            ScreenUI.createPanel();

        card.top();

        Label titleLabel =
            ScreenUI.createSectionTitle(
                game.skin,
                title
            );

        titleLabel.setAlignment(
            Align.center
        );

        card
            .add(titleLabel)
            .center()
            .padBottom(14f)
            .row();

        card
            .add(
                createStarsWidget(
                    rating
                )
            )
            .center()
            .padBottom(14f)
            .row();

        Label desc =
            new Label(
                description,
                game.skin
            );

        desc.setWrap(
            true
        );

        desc.setAlignment(
            Align.center
        );

        desc.setFontScale(
            0.58f
        );

        desc.setColor(
            ScreenUI.MUTED_TEXT
        );

        card
            .add(desc)
            .width(280f)
            .center();

        return card;
    }

    private Table createStarsWidget(
        int rating
    ) {

        Table stars =
            new Table();

        rating =
            Math.max(
                0,
                Math.min(
                    5,
                    rating
                )
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
                        : Color.valueOf(
                        "4D514D"
                    )
                );

                stars
                    .add(star)
                    .size(22f)
                    .padRight(3f);

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
                        : Color.GRAY
                );

                stars
                    .add(star)
                    .padRight(3f);
            }
        }

        return stars;
    }

    private Table createInfoRow(
        String title,
        Table value
    ) {

        Table row =
            new Table();

        Label label =
            ScreenUI.createSubtitle(
                game.skin,
                title
            );

        row
            .add(label)
            .left()
            .expandX();

        row
            .add(value)
            .right();

        return row;
    }

    private Table createTextInfoRow(
        String title,
        String value
    ) {

        Table row =
            new Table();

        row
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    title
                )
            )
            .left()
            .expandX();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    value != null
                        ? value
                        : "N/D",
                    Color.WHITE,
                    Align.right
                )
            )
            .right();

        return row;
    }

    private void addInfoLine(
        Table table,
        String key,
        String value
    ) {

        table
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    key.toUpperCase()
                )
            )
            .width(180f)
            .left()
            .padBottom(9f);

        table
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    value != null
                        ? value
                        : "N/D",
                    Color.WHITE,
                    Align.left
                )
            )
            .left()
            .expandX()
            .padBottom(9f)
            .row();
    }

    private void addRecord(
        Table table,
        String key,
        String value
    ) {

        table
            .add(
                ScreenUI.createSubtitle(
                    game.skin,
                    key.toUpperCase()
                )
            )
            .left()
            .expandX()
            .padBottom(12f);

        table
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    value != null
                        ? value
                        : "N/D",
                    StyleFactory.CREME_AGED,
                    Align.right
                )
            )
            .right()
            .padBottom(12f)
            .row();
    }

    private void addSeasonRow(
        Table table,
        int year,
        String league,
        String cup,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        year
                    ),
                    StyleFactory.GOLD,
                    Align.center
                )
            )
            .width(90f);

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    league,
                    Color.WHITE,
                    Align.center
                )
            )
            .width(200f);

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    cup,
                    Color.WHITE,
                    Align.center
                )
            )
            .width(200f);

        table
            .add(row)
            .growX()
            .height(46f)
            .row();
    }

    private void addTableHeader(
        Table table,
        String text,
        float width,
        int align
    ) {

        table
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    text,
                    align
                )
            )
            .width(width);
    }

    private int normalizeReputation() {

        int reputation =
            club.getReputation();

        if (
            reputation > 5
        ) {

            return Math.max(
                1,
                Math.min(
                    5,
                    Math.round(
                        reputation /
                            20f
                    )
                )
            );
        }

        return Math.max(
            1,
            Math.min(
                5,
                reputation
            )
        );
    }

    private String formatNumber(
        long value
    ) {

        return NumberFormat
            .getInstance(
                new Locale(
                    "pt",
                    "BR"
                )
            )
            .format(
                value
            );
    }

    private String compactNumber(
        long value
    ) {

        if (
            value >= 1_000_000
        ) {

            return String.format(
                Locale.US,
                "%.1fM",
                value /
                    1_000_000f
            );
        }

        if (
            value >= 1_000
        ) {

            return String.format(
                Locale.US,
                "%.0fK",
                value /
                    1_000f
            );
        }

        return String.valueOf(
            value
        );
    }

    private Texture loadTextureOrNull(
        String path
    ) {

        if (
            path == null ||
                path.trim().isEmpty()
        ) {

            return null;
        }

        try {

            if (
                Gdx.files
                    .internal(path)
                    .exists()
            ) {

                return new Texture(
                    Gdx.files.internal(
                        path
                    )
                );
            }

        } catch (
            Exception ignored
        ) {
        }

        return null;
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

        if (
            logoTexture != null
        ) {
            logoTexture.dispose();
        }

        if (
            starTexture != null
        ) {
            starTexture.dispose();
        }
    }
}
