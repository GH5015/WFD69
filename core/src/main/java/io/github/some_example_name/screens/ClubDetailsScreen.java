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
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClubDetailsScreen implements Screen {

    private final Main game;

    /**
     * Clube sendo visualizado.
     */
    private final Club club;

    /**
     * Clube controlado pelo usuário.
     */
    private final Club playerClub;

    private final Stage stage;

    private Texture logoTexture;

    private String sortType =
        "OVR";

    private boolean sortAscending =
        false;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public ClubDetailsScreen(
        Main game,
        Club club,
        Club playerClub
    ) {

        this.game =
            game;

        this.club =
            club;

        this.playerClub =
            playerClub;

        this.stage =
            new Stage(
                new ScreenViewport()
            );

        this.logoTexture =
            loadLogo();
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

        page
            .add(
                ScreenUI.createHeader(
                    game.skin,
                    "VISÃO GERAL DO CLUBE",
                    club.getConference() !=
                        null
                        ? club.getConference()
                        .toUpperCase()
                        : ""
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        // =====================================================
        // CLUB INFO
        // =====================================================

        page
            .add(
                createClubHeader()
            )
            .growX()
            .height(114f)
            .padBottom(10f)
            .row();

        // =====================================================
        // BODY
        // =====================================================

        Table body =
            new Table();

        float usableWidth =
            Math.max(
                900f,
                Gdx.graphics
                    .getWidth()
                    -
                    ScreenUI.PAGE_LEFT_OPEN
                    -
                    ScreenUI.PAGE_RIGHT
            );

        body
            .add(
                createSquadPanel()
            )
            .width(
                usableWidth *
                    0.69f
            )
            .growY()
            .padRight(10f);

        body
            .add(
                createSidePanel()
            )
            .width(
                usableWidth *
                    0.28f
            )
            .growY();

        page
            .add(body)
            .grow()
            .row();

        root.add(
            page
        );

        /*
         * Como essa tela normalmente nasceu da classificação,
         * mantemos TABELA como contexto ativo.
         */
        NavigationDrawer.attach(
            stage,
            game,
            playerClub,
            "TABELA",
            true
        );

    }

    // =========================================================
    // CLUB HEADER
    // =========================================================

    private Table createClubHeader() {

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
                .width(145f)
                .height(82f)
                .padRight(18f);
        }

        Table identity =
            new Table();

        identity.left();

        Label clubName =
            new Label(
                club.getName()
                    .toUpperCase(),
                game.skin,
                "font-title"
            );

        clubName.setFontScale(
            0.84f
        );

        clubName.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(clubName)
            .left()
            .row();

        String country =
            club.getCountry() !=
                null
                ? club.getCountry()
                : "N/D";

        Label countryLabel =
            new Label(
                country,
                game.skin,
                "font-bold"
            );

        countryLabel.setFontScale(
            0.55f
        );

        countryLabel.setColor(
            ScreenUI.MUTED_TEXT
        );

        identity
            .add(countryLabel)
            .left()
            .padTop(3f)
            .row();

        String stadium =
            club.getStadium() !=
                null
                ? club.getStadium()
                : "Estádio não informado";

        Label stadiumLabel =
            new Label(
                stadium +
                    " • " +
                    club.getStadiumCapacity() +
                    " lugares",
                game.skin
            );

        stadiumLabel.setFontScale(
            0.52f
        );

        stadiumLabel.setColor(
            StyleFactory.CREME_AGED
        );

        identity
            .add(stadiumLabel)
            .left()
            .padTop(5f);

        panel
            .add(identity)
            .left()
            .expandX();

        // =====================================================
        // OVERALL
        // =====================================================

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "OVERALL",
                    String.valueOf(
                        (int) Math.round(
                            club.getOverall()
                        )
                    ),
                    StyleFactory.GOLD
                )
            )
            .width(170f)
            .height(48f)
            .padRight(7f);

        // =====================================================
        // REPUTATION
        // =====================================================

        panel
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "REPUTAÇÃO",
                    String.valueOf(
                        club.getReputation()
                    ),
                    StyleFactory.SOFT_YELLOW
                )
            )
            .width(175f)
            .height(48f);

        return panel;
    }

    // =========================================================
    // SQUAD
    // =========================================================

    private Table createSquadPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        // =====================================================
        // TITLE
        // =====================================================

        Table title =
            new Table();

        title
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "ELENCO • " +
                        club.getSquad()
                            .size() +
                        " JOGADORES"
                )
            )
            .left()
            .expandX();

        Label hint =
            ScreenUI.createSubtitle(
                game.skin,
                "Clique nos cabeçalhos para ordenar"
            );

        title
            .add(hint)
            .right();

        panel
            .add(title)
            .growX()
            .padBottom(9f)
            .row();

        // =====================================================
        // TABLE
        // =====================================================

        ScrollPane scroll =
            new ScrollPane(
                createSquadTable(),
                game.skin
            );

        scroll.setFadeScrollBars(
            false
        );

        panel
            .add(scroll)
            .grow();

        return panel;
    }

    private Table createSquadTable() {

        Table table =
            new Table();

        table.top();

        // =====================================================
        // HEADER
        // =====================================================

        Table header =
            ScreenUI.createTableHeaderRow();

        addSortHeader(
            header,
            "POS",
            "POS",
            72f
        );

        addSortHeader(
            header,
            "JOGADOR",
            "NOME",
            300f
        );

        addSortHeader(
            header,
            "IDADE",
            "IDADE",
            70f
        );

        addSortHeader(
            header,
            "OVR",
            "OVR",
            72f
        );

        addSortHeader(
            header,
            "GOLS",
            "GOLS",
            65f
        );

        addSortHeader(
            header,
            "ASSIST.",
            "ASSISTS",
            72f
        );

        addSortHeader(
            header,
            "MORAL",
            "MORAL",
            76f
        );

        table
            .add(header)
            .growX()
            .height(44f)
            .row();

        // =====================================================
        // PLAYERS
        // =====================================================

        List<Player> players =
            new ArrayList<>(
                club.getSquad()
            );

        sortPlayers(
            players
        );

        int index =
            0;

        for (
            Player player :
            players
        ) {

            Table row =
                ScreenUI.createRow(
                    index++
                );

            // =================================================
            // POS
            // =================================================

            row
                .add(
                    ScreenUI.createBadge(
                        game.skin,
                        player.getPosition(),
                        StyleFactory
                            .getPositionColor(
                                player.getPosition()
                            )
                    )
                )
                .width(72f)
                .height(27f);

            // =================================================
            // NAME
            // =================================================

            row
                .add(
                    ScreenUI.createBoldValue(
                        game.skin,
                        player.getName(),
                        Color.WHITE,
                        Align.left
                    )
                )
                .width(300f)
                .padLeft(9f);

            // =================================================
            // AGE
            // =================================================

            row
                .add(
                    value(
                        String.valueOf(
                            player.getAge()
                        ),
                        ScreenUI.MUTED_TEXT
                    )
                )
                .width(70f);

            // =================================================
            // OVR
            // =================================================

            row
                .add(
                    value(
                        String.valueOf(
                            player.getOverall()
                        ),
                        StyleFactory.SOFT_YELLOW
                    )
                )
                .width(72f);

            // =================================================
            // G
            // =================================================

            row
                .add(
                    value(
                        String.valueOf(
                            player.getSeasonGoals()
                        ),
                        Color.WHITE
                    )
                )
                .width(65f);

            // =================================================
            // A
            // =================================================

            row
                .add(
                    value(
                        String.valueOf(
                            player.getSeasonAssists()
                        ),
                        Color.WHITE
                    )
                )
                .width(72f);

            // =================================================
            // MORALE
            // =================================================

            int morale =
                player.getMorale();

            Color moraleColor =
                morale >= 75
                    ? ScreenUI.SUCCESS
                    : morale >= 45
                    ? ScreenUI.WARNING
                    : ScreenUI.DANGER;

            row
                .add(
                    value(
                        String.valueOf(
                            morale
                        ),
                        moraleColor
                    )
                )
                .width(76f);

            table
                .add(row)
                .growX()
                .height(48f)
                .row();
        }

        return table;
    }

    // =========================================================
    // SIDE PANEL
    // =========================================================

    private Table createSidePanel() {

        Table root =
            new Table();

        root.top();

        // =====================================================
        // PERFIL
        // =====================================================

        Table profile =
            ScreenUI.createPanel();

        profile.top();

        profile
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "IDENTIDADE"
                )
            )
            .left()
            .colspan(2)
            .padBottom(12f)
            .row();

        addInfoLine(
            profile,
            "CONFERÊNCIA",
            club.getConference()
        );

        addInfoLine(
            profile,
            "FILOSOFIA",
            club.getPhilosophy()
        );

        addInfoLine(
            profile,
            "MENTALIDADE",
            club.getMentality()
        );

        addInfoLine(
            profile,
            "FORMAÇÃO",
            club.getFormation() != null
                ? club.getFormation()
                .getName()
                : "N/D"
        );

        root
            .add(profile)
            .growX()
            .padBottom(10f)
            .row();

        // =====================================================
        // STATUS
        // =====================================================

        Table status =
            ScreenUI.createPanel();

        status.top();

        status
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    "STATUS DO ELENCO"
                )
            )
            .left()
            .padBottom(10f)
            .row();

        int unavailable =
            0;

        int totalMorale =
            0;

        int totalFatigue =
            0;

        for (
            Player player :
            club.getSquad()
        ) {

            if (
                !player.canPlay()
            ) {

                unavailable++;
            }

            totalMorale +=
                player.getMorale();

            totalFatigue +=
                player.getFatigue();
        }

        int size =
            Math.max(
                1,
                club.getSquad()
                    .size()
            );

        int avgMorale =
            totalMorale /
                size;

        int avgFatigue =
            totalFatigue /
                size;

        status
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "INDISPONÍVEIS",
                    String.valueOf(
                        unavailable
                    ),
                    unavailable == 0
                        ? ScreenUI.SUCCESS
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(42f)
            .padBottom(7f)
            .row();

        status
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "MORAL MÉDIA",
                    String.valueOf(
                        avgMorale
                    ),
                    avgMorale >= 75
                        ? ScreenUI.SUCCESS
                        : avgMorale >= 45
                        ? ScreenUI.WARNING
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(42f)
            .padBottom(7f)
            .row();

        status
            .add(
                ScreenUI.createStatusBox(
                    game.skin,
                    "CONDIÇÃO MÉDIA",
                    avgFatigue +
                        "%",
                    avgFatigue >= 75
                        ? ScreenUI.SUCCESS
                        : avgFatigue >= 50
                        ? ScreenUI.WARNING
                        : ScreenUI.DANGER
                )
            )
            .growX()
            .height(42f);

        root
            .add(status)
            .growX()
            .padBottom(10f)
            .row();

        // =====================================================
        // ACTIONS
        // =====================================================

        Table actions =
            ScreenUI.createPanel();

        ImageTextButton standings =
            IconTextButton.create(
                "VOLTAR À TABELA",
                game.skin,
                "Icons8/icons8-lista-50.png"
            );

        standings
            .getLabel()
            .setFontScale(
                0.55f
            );

        standings.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    game.setScreen(
                        new StandingsScreen(
                            game,
                            playerClub
                        )
                    );
                }
            }
        );

        actions
            .add(standings)
            .growX()
            .height(42f)
            .padBottom(7f)
            .row();

        if (
            club !=
                playerClub
        ) {

            ImageTextButton trade =
                IconTextButton.create(
                    "NEGOCIAR",
                    game.skin,
                    "Icons8/icons8-partilhar-2-50.png"
                );

            trade
                .getLabel()
                .setFontScale(
                    0.56f
                );

            trade.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                        InputEvent event,
                        float x,
                        float y
                    ) {

                        game.setScreen(
                            new TradeScreen(
                                game,
                                playerClub,
                                club
                            )
                        );
                    }
                }
            );

            actions
                .add(trade)
                .growX()
                .height(42f);
        }

        root
            .add(actions)
            .growX()
            .row();

        return root;
    }

    // =========================================================
    // SORT
    // =========================================================

    private void addSortHeader(
        Table row,
        String label,
        String type,
        float width
    ) {

        String arrow =
            "";

        if (
            sortType.equals(
                type
            )
        ) {

            arrow =
                sortAscending
                    ? " ↑"
                    : " ↓";
        }

        TextButton button =
            ScreenUI.createInteractiveButton(
                label +
                    arrow,
                game.skin,
                "toggle"
            );

        button
            .getLabel()
            .setFontScale(
                0.50f
            );

        button.setChecked(
            sortType.equals(
                type
            )
        );

        button.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        sortType.equals(
                            type
                        )
                    ) {

                        sortAscending =
                            !sortAscending;

                    } else {

                        sortType =
                            type;

                        sortAscending =
                            false;
                    }

                    refreshUI();
                }
            }
        );

        row
            .add(button)
            .width(width)
            .height(36f);
    }

    private void sortPlayers(
        List<Player> players
    ) {

        Comparator<Player> comparator;

        switch (
            sortType
        ) {

            case "NOME":

                comparator =
                    Comparator.comparing(
                        Player::getName
                    );

                break;

            case "POS":

                comparator =
                    Comparator.comparingInt(
                        Player::getPositionWeight
                    );

                break;

            case "IDADE":

                comparator =
                    Comparator.comparingInt(
                        Player::getAge
                    );

                break;

            case "GOLS":

                comparator =
                    Comparator.comparingInt(
                        Player::getSeasonGoals
                    );

                break;

            case "ASSISTS":

                comparator =
                    Comparator.comparingInt(
                        Player::getSeasonAssists
                    );

                break;

            case "MORAL":

                comparator =
                    Comparator.comparingInt(
                        Player::getMorale
                    );

                break;

            case "OVR":
            default:

                comparator =
                    Comparator.comparingInt(
                        Player::getOverall
                    );

                break;
        }

        if (
            !sortAscending
        ) {

            comparator =
                comparator.reversed();
        }

        players.sort(
            comparator
        );
    }

    // =========================================================
    // INFO
    // =========================================================

    private void addInfoLine(
        Table table,
        String key,
        String value
    ) {

        Label keyLabel =
            ScreenUI.createSubtitle(
                game.skin,
                key
            );

        table
            .add(keyLabel)
            .left()
            .expandX()
            .padBottom(9f);

        Label valueLabel =
            ScreenUI.createBoldValue(
                game.skin,
                value != null
                    ? value
                    : "N/D",
                StyleFactory.CREME_AGED,
                Align.right
            );

        table
            .add(valueLabel)
            .right()
            .padBottom(9f)
            .row();
    }

    private Label value(
        String text,
        Color color
    ) {

        return ScreenUI.createBoldValue(
            game.skin,
            text,
            color,
            Align.center
        );
    }

    // =========================================================
    // LOGO
    // =========================================================

    private Texture loadLogo() {

        if (
            club == null ||
                club.getLogoPath() ==
                    null
        ) {

            return null;
        }

        try {

            if (
                Gdx.files
                    .internal(
                        club.getLogoPath()
                    )
                    .exists()
            ) {

                return new Texture(
                    Gdx.files.internal(
                        club.getLogoPath()
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
    }
}
