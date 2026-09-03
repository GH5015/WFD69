package io.github.some_example_name.screens;

import io.github.some_example_name.utils.ClubLogoAssets;

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
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.AttendanceService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonHistory;
import io.github.some_example_name.model.StadiumRenovationPlan;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
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
    private Texture stadiumIconTexture;

    private String activeTab = "RESUMO";

    public ClubProfileScreen(
        Main game,
        Club club
    ) {

        this.game = game;
        this.club = club;

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        logoTexture =
            loadTextureOrNull(
                club.getLogoPath()
            );

        starTexture = ScreenUI.loadTintableIcon("Icons8/icons8-estrela-48.png");
        stadiumIconTexture = ScreenUI.loadTintableIcon("Icons8/icons8-estádio-50.png");
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
            .height(126f)
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
                .width(150f)
                .height(94f)
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
            0.96f
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
                .width(270f)
                .height(170f)
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

        StaffRole[] roles = {
            StaffRole.COACH,
            StaffRole.SCOUT,
            StaffRole.DEVELOPMENT_DIRECTOR,
            StaffRole.FITNESS_COACH,
            StaffRole.DOCTOR
        };

        for (int i = 0; i < roles.length; i++) {
            cards.add(
                createStaffInfrastructureCard(roles[i])
            ).grow().uniformX().padRight(i < roles.length - 1 ? 8f : 0f);
        }

        root
            .add(cards)
            .growX()
            .height(220f)
            .padBottom(12f)
            .row();

        Table stadium =
            ScreenUI.createPanel();

        stadium.top();

        Table stadiumHeader = new Table();
        stadiumHeader.add(ScreenUI.createSectionTitle(game.skin, "ESTÁDIO | REFORMAS E EXPANSÃO"))
            .left().expandX();
        Label stadiumName = ScreenUI.createBoldValue(
            game.skin,
            club.getStadium().toUpperCase(),
            StyleFactory.SOFT_YELLOW,
            Align.right
        );
        stadiumName.setFontScale(.52f);
        stadiumHeader.add(stadiumName).right();
        stadium.add(stadiumHeader).growX().colspan(2).padBottom(10f).row();

        stadium.add(createStadiumOverview()).growX().growY().padRight(12f);
        stadium.add(createRenovationStatus()).width(570f).growY().row();
        stadium.add(createPitchMaintenancePanel()).colspan(2).growX().height(190f).padTop(12f);

        root
            .add(stadium)
            .growX()
            .row();

        return root;
    }

    private Table createStadiumOverview() {
        Table overview = ScreenUI.createSubtlePanel();
        overview.top().pad(12f);

        if (stadiumIconTexture != null) {
            Image icon = new Image(new TextureRegionDrawable(stadiumIconTexture));
            icon.setScaling(Scaling.fit);
            icon.setColor(StyleFactory.GOLD);
            overview.add(icon).size(76f).padRight(14f).top();
        }

        Table identity = new Table();
        identity.top().left();
        Label name = ScreenUI.createSectionTitle(game.skin, club.getStadium().toUpperCase());
        name.setFontScale(.64f);
        identity.add(name).growX().left().row();
        Label capacity = ScreenUI.createBoldValue(
            game.skin,
            formatNumber(club.getStadiumCapacity()) + " LUGARES",
            StyleFactory.CREME_AGED,
            Align.left
        );
        capacity.setFontScale(.58f);
        identity.add(capacity).left().padTop(4f).row();
        if (club.isStadiumRenovationInProgress()) {
            Label temporary = ScreenUI.createBoldValue(
                game.skin,
                "EM OBRA • " + formatNumber(club.getOperationalStadiumCapacity()) + " LIBERADOS",
                ScreenUI.WARNING,
                Align.left
            );
            temporary.setFontScale(.40f);
            identity.add(temporary).left().padTop(4f);
        } else {
            identity.add(ScreenUI.createSubtitle(
                game.skin,
                "Limite WFL: " + formatNumber(StadiumRenovationPlan.MAX_CAPACITY)
            )).left().padTop(4f);
        }
        overview.add(identity).growX().top().row();

        Table metrics = new Table();
        metrics.add(createStadiumMetric(
            "MÉDIA DE PÚBLICO",
            averageHomeAttendance() > 0 ? formatNumber(averageHomeAttendance()) : "SEM JOGOS",
            Color.WHITE
        )).growX().uniformX().padRight(6f);
        metrics.add(createStadiumMetric(
            "RECEITA / JOGO",
            averageGateRevenue() > 0 ? money(averageGateRevenue()) : "SEM RECEITA",
            ScreenUI.SUCCESS
        )).growX().uniformX().padRight(6f);
        metrics.add(createStadiumMetric(
            "CONDIÇÃO",
            club.getStadiumCondition() + "% • " + stadiumConditionLabel(),
            stadiumConditionColor()
        )).growX().uniformX();
        overview.add(metrics).colspan(2).growX().padTop(12f);
        return overview;
    }

    private Table createStadiumMetric(String title, String value, Color color) {
        Table metric = new Table();
        metric.background(StyleFactory.createRoundedPanel(
            Color.valueOf("0D1813"),
            Color.valueOf("35483D")
        ));
        metric.pad(7f, 9f, 7f, 9f);
        Label titleLabel = ScreenUI.createSubtitle(game.skin, title);
        titleLabel.setFontScale(.40f);
        metric.add(titleLabel).left().row();
        Label valueLabel = ScreenUI.createBoldValue(game.skin, value, color, Align.left);
        valueLabel.setFontScale(.48f);
        metric.add(valueLabel).left().padTop(3f);
        return metric;
    }

    private Table createPitchMaintenancePanel() {
        Table panel = ScreenUI.createSubtlePanel();
        panel.top().pad(10f, 12f, 10f, 12f);
        panel.add(ScreenUI.createSectionTitle(game.skin, "CONDIÇÃO DO ESTÁDIO"))
            .colspan(4).growX().left().padBottom(7f).row();

        Table condition = new Table();
        condition.left();
        Label value = ScreenUI.createBoldValue(
            game.skin,
            club.getStadiumCondition() + "% • " + stadiumConditionLabel(),
            stadiumConditionColor(),
            Align.left
        );
        value.setFontScale(.62f);
        condition.add(value).growX().left().row();
        condition.add(ScreenUI.createBlockProgress(
            game.skin,
            club.getStadiumCondition(),
            20,
            stadiumConditionColor()
        )).growX().height(17f).padTop(5f).row();
        Label upkeep = ScreenUI.createSubtitle(
            game.skin,
            "Desgaste: -2% por jogo em casa • Manutenção: "
                + money(club.getFinance().getStadiumMaintenanceExpense()) + " / mês"
        );
        upkeep.setWrap(true);
        condition.add(upkeep).growX().height(30f).left().padTop(5f).row();

        Match nextHomeMatch = AttendanceService.findNextHomeMatch(game.league, club);
        if (nextHomeMatch != null) {
            int demand = AttendanceService.estimateDemand(game.league, nextHomeMatch);
            int unmet = Math.max(0, demand - club.getOperationalStadiumCapacity());
            String demandText = unmet > 0
                ? "DEMANDA NÃO ATENDIDA • " + formatNumber(unmet) + " INGRESSOS"
                : "PRÓXIMO JOGO • DEMANDA DE " + formatNumber(demand) + " TORCEDORES";
            Label demandAlert = ScreenUI.createBoldValue(
                game.skin,
                demandText,
                unmet > 0 ? ScreenUI.WARNING : ScreenUI.SUCCESS,
                Align.left
            );
            demandAlert.setFontScale(.42f);
            condition.add(demandAlert).growX().height(25f).left().padTop(3f);
        }
        panel.add(condition).width(390f).growY().padRight(12f);

        panel.add(createPitchAction(
            "TRATAMENTO DO GRAMADO",
            "Recupera +15% da condição",
            club.getPitchTreatmentCost(),
            false
        )).grow().uniformX().padRight(9f);
        panel.add(createPitchAction(
            "TROCA COMPLETA",
            "Restaura a condição para 100%",
            club.getPitchReplacementCost(),
            true
        )).grow().uniformX().padRight(12f);
        panel.add(createTicketPricingCard()).width(285f).growY();
        return panel;
    }

    private Table createTicketPricingCard() {
        Table card = new Table();
        card.top().left();

        Label title = ScreenUI.createBoldValue(game.skin, "PREÇO MÉDIO DO INGRESSO", StyleFactory.SOFT_YELLOW, Align.left);
        title.setFontScale(.45f);
        card.add(title).colspan(3).growX().left().row();

        Label price = ScreenUI.createBoldValue(
            game.skin,
            "WFL$ " + club.getAverageTicketPrice(),
            StyleFactory.CREME_AGED,
            Align.center
        );
        price.setFontScale(.62f);

        TextButton decrease = ScreenUI.createInteractiveButton("−", game.skin);
        TextButton increase = ScreenUI.createInteractiveButton("+", game.skin);
        decrease.getLabel().setFontScale(.62f);
        increase.getLabel().setFontScale(.62f);
        decrease.setDisabled(!club.isUserControlled() || club.getAverageTicketPrice() <= 10);
        increase.setDisabled(!club.isUserControlled() || club.getAverageTicketPrice() >= 100);
        decrease.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                updateTicketPrice(club.getAverageTicketPrice() - 5);
            }
        });
        increase.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                updateTicketPrice(club.getAverageTicketPrice() + 5);
            }
        });

        card.add(decrease).width(48f).height(35f).padTop(8f);
        card.add(price).width(145f).height(35f).padTop(8f);
        card.add(increase).width(48f).height(35f).padTop(8f).row();

        int suggested = club.getSuggestedTicketPrice();
        float demandImpact = AttendanceService.ticketDemandMultiplier(club);
        int impactPercent = Math.round((demandImpact - 1f) * 100f);
        Color impactColor = impactPercent < 0 ? ScreenUI.WARNING : impactPercent > 0 ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT;
        String impactText = impactPercent == 0
            ? "Demanda sem alteração"
            : "Impacto estimado no público: " + (impactPercent > 0 ? "+" : "") + impactPercent + "%";
        Label impact = ScreenUI.createBoldValue(game.skin, impactText, impactColor, Align.center);
        impact.setFontScale(.36f);
        card.add(impact).colspan(3).growX().center().padTop(6f).row();

        Label reference = ScreenUI.createSubtitle(
            game.skin,
            "Preço recomendado: WFL$ " + suggested
                + "\nReceita estimada por jogo"
        );
        reference.setAlignment(Align.center);
        reference.setWrap(true);
        card.add(reference).colspan(3).growX().height(40f).center().padTop(3f);
        return card;
    }

    private void updateTicketPrice(int price) {
        club.setAverageTicketPrice(price);
        Match nextHomeMatch = AttendanceService.findNextHomeMatch(game.league, club);
        if (nextHomeMatch != null) nextHomeMatch.resetAttendanceProjection();
        refreshUI();
    }

    private Table createPitchAction(String titleText, String description, long cost, final boolean replacement) {
        Table card = new Table();
        card.left();
        Label title = ScreenUI.createBoldValue(game.skin, titleText, StyleFactory.SOFT_YELLOW, Align.left);
        title.setFontScale(.48f);
        card.add(title).growX().left().row();
        card.add(ScreenUI.createSubtitle(game.skin, description)).growX().left().padTop(2f).row();
        card.add(ScreenUI.createBoldValue(game.skin, money(cost), StyleFactory.CREME_AGED, Align.left))
            .growX().left().padTop(4f).row();

        boolean perfect = club.getStadiumCondition() >= 100;
        boolean affordable = club.getFinance().getBalance() >= cost;
        boolean allowed = club.isUserControlled() && !perfect && affordable;
        TextButton action = ScreenUI.createInteractiveButton(
            perfect ? "CONDIÇÃO MÁXIMA" : !affordable ? "SEM CAIXA" : replacement ? "TROCAR GRAMADO" : "REALIZAR TRATAMENTO",
            game.skin
        );
        action.getLabel().setFontScale(.42f);
        action.setDisabled(!allowed);
        action.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (allowed) showPitchMaintenanceConfirmation(replacement);
            }
        });
        card.add(action).width(225f).height(36f).left().padTop(6f);
        return card;
    }

    private void showPitchMaintenanceConfirmation(final boolean replacement) {
        final long cost = replacement ? club.getPitchReplacementCost() : club.getPitchTreatmentCost();
        final int target = replacement ? 100 : Math.min(100, club.getStadiumCondition() + 15);
        final Dialog dialog = new Dialog(replacement ? "TROCAR GRAMADO" : "TRATAR GRAMADO", game.skin);
        dialog.text(
            (replacement
                ? "A troca completa instalará um novo gramado."
                : "O tratamento recuperará parcialmente as áreas desgastadas.")
                + "\n\nCondição: " + club.getStadiumCondition() + "% → " + target + "%"
                + "\nCusto: " + money(cost)
                + "\nNova manutenção mensal: " + projectedStadiumMaintenance(target)
        );
        TextButton confirm = ScreenUI.createPrimaryButton(
            game.skin,
            replacement ? "CONFIRMAR TROCA" : "CONFIRMAR TRATAMENTO"
        );
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                boolean completed = replacement ? club.replaceStadiumPitch() : club.treatStadiumPitch();
                if (completed) {
                    dialog.hide();
                    refreshUI();
                }
            }
        });
        dialog.button("CANCELAR");
        dialog.button(confirm);
        dialog.show(stage);
    }

    private String projectedStadiumMaintenance(int condition) {
        return projectedStadiumMaintenance(club.getStadiumCapacity(), condition);
    }

    private String projectedStadiumMaintenance(int capacity, int condition) {
        long base = capacity * 6L;
        double multiplier = 1d + (100 - Math.max(0, Math.min(100, condition))) / 50d;
        return money(Math.round(base * multiplier));
    }

    private String stadiumConditionLabel() {
        int condition = club.getStadiumCondition();
        if (condition >= 90) return "EXCELENTE";
        if (condition >= 75) return "BOA";
        if (condition >= 55) return "DESGASTADA";
        return "CRÍTICA";
    }

    private Color stadiumConditionColor() {
        int condition = club.getStadiumCondition();
        if (condition >= 75) return ScreenUI.SUCCESS;
        if (condition >= 55) return StyleFactory.SOFT_YELLOW;
        return ScreenUI.WARNING;
    }

    private Table createRenovationStatus() {
        Table box = ScreenUI.createSubtlePanel();
        box.top();

        if (club.isStadiumRenovationInProgress()) {
            box.add(ScreenUI.createSectionTitle(game.skin, "OBRA EM ANDAMENTO"))
                .left().colspan(2).padBottom(7f).row();
            addInfoLine(box, "Projeto", club.getStadiumRenovationName());
            addInfoLine(box, "Nova capacidade", formatNumber(club.getStadiumRenovationTargetCapacity()));
            addInfoLine(box, "Durante a obra", formatNumber(club.getOperationalStadiumCapacity()) + " lugares");
            addInfoLine(box, "Prazo restante", club.getStadiumRenovationDaysRemaining() + " dias");
            addInfoLine(box, "Investimento", money(club.getStadiumRenovationCost()));
            box.add(ScreenUI.createBlockProgress(
                game.skin,
                club.getStadiumRenovationProgress(),
                18,
                StyleFactory.GOLD
            )).colspan(2).growX().height(18f).padTop(5f);
            return box;
        }

        box.add(ScreenUI.createSectionTitle(game.skin, "PRÓXIMA EXPANSÃO"))
            .left().padBottom(7f).row();
        if (club.getStadiumCapacity() < StadiumRenovationPlan.MAX_CAPACITY) {
            StadiumRenovationPlan preview = firstAvailableRenovationPlan();
            if (preview != null) {
                Table previewRow = new Table();
                previewRow.add(createStadiumMetric(
                    "CAPACIDADE",
                    formatNumber(club.getStadiumCapacity()) + "  >  " +
                        formatNumber(club.getStadiumCapacity() + preview.getAdditionalCapacity()),
                    ScreenUI.SUCCESS
                )).growX().uniformX().padRight(6f);
                previewRow.add(createStadiumMetric(
                    "CUSTO",
                    money(preview.getCost()),
                    StyleFactory.SOFT_YELLOW
                )).growX().uniformX().padRight(6f);
                previewRow.add(createStadiumMetric(
                    "PRAZO",
                    preview.getDurationDays() + " DIAS",
                    Color.WHITE
                )).growX().uniformX();
                box.add(previewRow).growX().height(62f).padBottom(7f).row();
            }
        } else {
            Label hint = ScreenUI.createSubtitle(
                game.skin,
                "O estádio já atingiu a capacidade máxima permitida pela WFL."
            );
            hint.setWrap(true);
            box.add(hint).growX().height(44f).left().row();
        }

        TextButton renovate = ScreenUI.createPrimaryButton(
            game.skin,
            club.getStadiumCapacity() >= StadiumRenovationPlan.MAX_CAPACITY
                ? "CAPACIDADE MÁXIMA"
                : "PLANEJAR REFORMA"
        );
        boolean allowed = club.isUserControlled()
            && club.getStadiumCapacity() < StadiumRenovationPlan.MAX_CAPACITY;
        renovate.setDisabled(!allowed);
        renovate.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (allowed) showRenovationOptions();
            }
        });
        box.add(renovate).width(250f).height(45f).center().padTop(8f);
        return box;
    }

    private StadiumRenovationPlan firstAvailableRenovationPlan() {
        for (StadiumRenovationPlan plan : StadiumRenovationPlan.values()) {
            if (club.getStadiumCapacity() + plan.getAdditionalCapacity()
                <= StadiumRenovationPlan.MAX_CAPACITY) return plan;
        }
        return null;
    }

    private void showRenovationOptions() {
        final Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        Table content = dialog.getContentTable();
        content.background(StyleFactory.createMetallicBoard(1220, 710, Color.valueOf("071A12")));
        content.pad(22f, 28f, 18f, 28f);

        Table heading = new Table();
        if (stadiumIconTexture != null) {
            Image icon = new Image(new TextureRegionDrawable(stadiumIconTexture));
            icon.setScaling(Scaling.fit);
            icon.setColor(StyleFactory.GOLD);
            heading.add(icon).size(54f).padRight(12f);
        }
        Table copy = new Table();
        copy.left();
        Label title = ScreenUI.createSectionTitle(game.skin, "REFORMAS E EXPANSÃO");
        title.setFontScale(.84f);
        copy.add(title).left().row();
        copy.add(ScreenUI.createSubtitle(
            game.skin,
            club.getStadium().toUpperCase() + " • escolha um projeto para análise"
        )).left().padTop(2f);
        heading.add(copy).left().expandX();
        Label cash = ScreenUI.createBoldValue(
            game.skin,
            "CAIXA  " + money(club.getFinance().getBalance()),
            club.getFinance().getBalance() >= StadiumRenovationPlan.STANDS.getCost()
                ? ScreenUI.SUCCESS : ScreenUI.WARNING,
            Align.right
        );
        cash.setFontScale(.58f);
        heading.add(cash).right();
        content.add(heading).growX().padBottom(16f).row();

        Table projects = new Table();
        for (final StadiumRenovationPlan plan : StadiumRenovationPlan.values()) {
            projects.add(createRenovationOptionCard(dialog, plan))
                .width(360f).height(390f).padRight(12f);
        }
        content.add(projects).growX().center().padBottom(14f).row();

        Table footer = new Table();
        Label warning = ScreenUI.createSubtitle(
            game.skin,
            "Durante a obra, setores serão fechados e a capacidade de público ficará temporariamente reduzida."
        );
        warning.setColor(ScreenUI.WARNING);
        footer.add(warning).left().expandX();
        TextButton close = ScreenUI.createSecondaryButton(game.skin, "VOLTAR");
        close.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        footer.add(close).width(180f).height(44f).right();
        content.add(footer).growX();
        dialog.show(stage);
        dialog.setSize(1220f, 710f);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) * .5f,
            (stage.getHeight() - dialog.getHeight()) * .5f
        );
    }

    private Table createRenovationOptionCard(final Dialog parent, final StadiumRenovationPlan plan) {
        final boolean capacityAllowed = club.getStadiumCapacity() + plan.getAdditionalCapacity()
            <= StadiumRenovationPlan.MAX_CAPACITY;
        final boolean affordable = club.getFinance().getBalance() >= plan.getCost();
        Table card = ScreenUI.createSubtlePanel();
        card.top().pad(15f);

        Label title = ScreenUI.createBoldValue(
            game.skin, plan.getDisplayName(), StyleFactory.SOFT_YELLOW, Align.center);
        title.setFontScale(.60f);
        title.setWrap(true);
        card.add(title).growX().height(48f).center().row();
        Label detail = ScreenUI.createSubtitle(game.skin, plan.getDescription());
        detail.setAlignment(Align.center);
        detail.setWrap(true);
        card.add(detail).width(315f).height(50f).center().padBottom(10f).row();

        Table facts = new Table();
        addRenovationFact(facts, "NOVA CAPACIDADE",
            formatNumber(club.getStadiumCapacity() + plan.getAdditionalCapacity()),
            capacityAllowed ? ScreenUI.SUCCESS : ScreenUI.DANGER);
        addRenovationFact(facts, "CAPACIDADE DURANTE A OBRA",
            formatNumber(club.previewTemporaryStadiumCapacity(plan)), ScreenUI.WARNING);
        addRenovationFact(facts, "PRAZO", plan.getDurationDays() + " dias", Color.WHITE);
        addRenovationFact(facts, "INVESTIMENTO", money(plan.getCost()),
            affordable ? StyleFactory.SOFT_YELLOW : ScreenUI.DANGER);
        addRenovationFact(facts, "RECEITA POTENCIAL / JOGO",
            "+" + money(estimateAdditionalGateRevenue(plan)), ScreenUI.SUCCESS);
        card.add(facts).growX().padBottom(12f).row();

        TextButton choose = ScreenUI.createPrimaryButton(
            game.skin,
            !capacityAllowed ? "EXCEDE O LIMITE" : !affordable ? "CAIXA INSUFICIENTE" : "ANALISAR PROJETO"
        );
        choose.getLabel().setFontScale(.47f);
        choose.setDisabled(!capacityAllowed || !affordable);
        choose.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (!capacityAllowed || !affordable) return;
                parent.hide();
                showRenovationConfirmation(plan);
            }
        });
        card.add(choose).growX().height(46f);
        return card;
    }

    private void addRenovationFact(Table table, String title, String value, Color color) {
        Label name = ScreenUI.createSubtitle(game.skin, title);
        name.setFontScale(.40f);
        table.add(name).growX().left().pad(4f, 2f, 4f, 2f);
        Label amount = ScreenUI.createBoldValue(game.skin, value, color, Align.right);
        amount.setFontScale(.45f);
        table.add(amount).right().pad(4f, 2f, 4f, 2f).row();
    }

    private void showRenovationConfirmation(final StadiumRenovationPlan plan) {
        final Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        Table content = dialog.getContentTable();
        content.background(StyleFactory.createMetallicBoard(820, 700, Color.valueOf("082017")));
        content.pad(26f, 50f, 24f, 50f);

        Label heading = ScreenUI.createSectionTitle(game.skin, "CONFIRMAR REFORMA");
        heading.setFontScale(.88f);
        content.add(heading).center().padBottom(14f).row();

        Table project = new Table();
        if (stadiumIconTexture != null) {
            Image icon = new Image(new TextureRegionDrawable(stadiumIconTexture));
            icon.setScaling(Scaling.fit);
            icon.setColor(StyleFactory.GOLD);
            project.add(icon).size(76f).padRight(18f);
        }
        Label projectName = ScreenUI.createBoldValue(
            game.skin, plan.getDisplayName(), StyleFactory.SOFT_YELLOW, Align.left);
        projectName.setFontScale(.68f);
        project.add(projectName).left();
        content.add(project).center().padBottom(14f).row();

        Table facts = ScreenUI.createSubtlePanel();
        facts.pad(10f, 18f, 10f, 18f);
        addConfirmationRow(facts, "Capacidade",
            formatNumber(club.getStadiumCapacity()) + "  >  " +
                formatNumber(club.getStadiumCapacity() + plan.getAdditionalCapacity()), Color.WHITE);
        addConfirmationRow(facts, "Custo", money(plan.getCost()), StyleFactory.SOFT_YELLOW);
        addConfirmationRow(facts, "Prazo", plan.getDurationDays() + " dias", Color.WHITE);
        addConfirmationRow(facts, "Durante a obra",
            "Capacidade temporária: " + formatNumber(club.previewTemporaryStadiumCapacity(plan)),
            ScreenUI.WARNING);
        addConfirmationRow(facts, "Caixa atual", money(club.getFinance().getBalance()), ScreenUI.SUCCESS);
        addConfirmationRow(facts, "Caixa após aprovação",
            money(club.getFinance().getBalance() - plan.getCost()), StyleFactory.CREME_AGED);
        addConfirmationRow(facts, "Receita potencial / jogo",
            "+" + money(estimateAdditionalGateRevenue(plan)), ScreenUI.SUCCESS);
        addConfirmationRow(facts, "Manutenção após entrega",
            projectedStadiumMaintenance(
                club.getStadiumCapacity() + plan.getAdditionalCapacity(), club.getStadiumCondition()),
            ScreenUI.MUTED_TEXT);
        content.add(facts).growX().padBottom(12f).row();

        Label note = ScreenUI.createSubtitle(
            game.skin,
            "O investimento será descontado imediatamente. A nova capacidade será liberada somente quando o prazo chegar a zero."
        );
        note.setWrap(true);
        note.setAlignment(Align.center);
        content.add(note).width(680f).height(46f).center().padBottom(14f).row();

        Table actions = new Table();
        TextButton cancel = ScreenUI.createSecondaryButton(game.skin, "CANCELAR");
        cancel.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                showRenovationOptions();
            }
        });
        TextButton confirm = ScreenUI.createPrimaryButton(game.skin, "INICIAR REFORMA");
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (club.startStadiumRenovation(plan)) {
                    resetFutureHomeAttendanceProjections();
                    dialog.hide();
                    refreshUI();
                }
            }
        });
        actions.add(cancel).width(270f).height(54f).padRight(28f);
        actions.add(confirm).width(300f).height(54f);
        content.add(actions).center();
        dialog.show(stage);
        dialog.setSize(820f, 700f);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) * .5f,
            (stage.getHeight() - dialog.getHeight()) * .5f
        );
    }

    private void addConfirmationRow(Table table, String title, String value, Color color) {
        Label key = ScreenUI.createBoldValue(game.skin, title, StyleFactory.SOFT_YELLOW, Align.left);
        key.setFontScale(.50f);
        table.add(key).width(245f).left().pad(7f, 4f, 7f, 4f);
        Label amount = ScreenUI.createBoldValue(game.skin, value, color, Align.left);
        amount.setFontScale(.50f);
        table.add(amount).growX().left().pad(7f, 4f, 7f, 4f).row();
    }

    private int averageHomeAttendance() {
        long total = 0L;
        int matches = 0;
        for (Match match : game.league.getSchedule()) {
            if (match != null && match.isPlayed() && match.getHomeTeam() == club
                && match.getAttendance() > 0) {
                total += match.getAttendance();
                matches++;
            }
        }
        return matches == 0 ? 0 : (int) Math.round(total / (double) matches);
    }

    private long averageGateRevenue() {
        long total = 0L;
        int matches = 0;
        for (Match match : game.league.getSchedule()) {
            if (match != null && match.isPlayed() && match.getHomeTeam() == club
                && match.getGateRevenue() > 0L) {
                total += match.getGateRevenue();
                matches++;
            }
        }
        return matches == 0 ? 0L : Math.round(total / (double) matches);
    }

    private long estimateAdditionalGateRevenue(StadiumRenovationPlan plan) {
        if (plan == null) return 0L;
        int currentCapacity = club.getStadiumCapacity();
        int targetCapacity = Math.min(
            StadiumRenovationPlan.MAX_CAPACITY,
            currentCapacity + plan.getAdditionalCapacity()
        );
        Match nextHome = AttendanceService.findNextHomeMatch(game.league, club);
        int demand = nextHome != null
            ? AttendanceService.estimateDemand(game.league, nextHome)
            : Math.round(targetCapacity * .78f);
        int extraAttendance = Math.max(
            0,
            Math.min(targetCapacity, demand) - Math.min(currentCapacity, demand)
        );
        return Math.round(
            extraAttendance * club.getAverageTicketPrice()
                * io.github.some_example_name.model.ClubFinance.CLUB_GATE_REVENUE_SHARE
        );
    }

    private void resetFutureHomeAttendanceProjections() {
        for (Match match : game.league.getSchedule()) {
            if (match != null && !match.isPlayed() && match.getHomeTeam() == club) {
                match.resetAttendanceProjection();
            }
        }
    }

    private Table createStaffInfrastructureCard(StaffRole role) {
        Table card = ScreenUI.createPanel();
        card.top();
        StaffMember member = club.getStaffMember(role);

        Label roleLabel = ScreenUI.createSectionTitle(game.skin, role.getLabel().toUpperCase());
        roleLabel.setAlignment(Align.center);
        roleLabel.setWrap(true);
        card.add(roleLabel).width(250f).height(42f).center().row();

        card.add(ScreenUI.createBoldValue(
            game.skin,
            member != null ? member.getName() : "CARGO VAGO",
            StyleFactory.CREME_AGED,
            Align.center
        )).width(250f).center().padTop(7f).row();

        card.add(createStarsWidget(member != null ? member.getDisplayRating() : 0f))
            .center().padTop(8f).row();

        if (member != null) {
            Label contract = ScreenUI.createSubtitle(
                game.skin,
                "Contrato até " + member.getContractEndYear() + "\n" + money(member.getAnnualSalary()) + " / ano"
            );
            contract.setAlignment(Align.center);
            card.add(contract).center().padTop(8f);
        }
        return card;
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

    private Table createStarsWidget(
        float rating
    ) {
        return ScreenUI.createStarRating(starTexture, rating, 22f);
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

    private float normalizeReputation() {

        int reputation =
            club.getReputation();

        if (
            reputation > 5
        ) {

            if (reputation >= 97) return 5f;
            if (reputation >= 94) return 4.5f;
            if (reputation >= 90) return 4f;
            if (reputation >= 88) return 3.5f;
            if (reputation >= 85) return 3f;
            if (reputation >= 82) return 2.5f;
            if (reputation >= 80) return 2f;
            return 1.5f;
        }

        return Math.max(1f, Math.min(5f, reputation));
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

    private String money(long value) {
        if (value >= 1_000_000L) {
            return String.format(Locale.US, "WFL$ %.1fM", value / 1_000_000d);
        }
        return String.format(Locale.US, "WFL$ %.0fK", value / 1_000d);
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

                return ClubLogoAssets.load(path);
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

        if (stadiumIconTexture != null) {
            stadiumIconTexture.dispose();
        }
    }
}
