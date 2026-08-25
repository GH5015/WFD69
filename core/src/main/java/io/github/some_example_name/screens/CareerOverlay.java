package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * HUD persistente da carreira.
 *
 * Exibe:
 *
 * - Temporada
 * - Data
 * - Próxima partida
 * - Adversário
 * - Botão de avançar/jogar
 *
 * Também processa:
 *
 * - avanço diário
 * - scouting
 * - recuperação física
 * - partidas da IA
 * - fechamento financeiro mensal
 */
public final class CareerOverlay
    extends WidgetGroup {

    private static final Map<String, Texture> LOGOS =
        new HashMap<>();

    private final Main game;
    private final Club club;

    private final Table matchCard;
    private final Table dateCard;

    private final ImageTextButton advanceButton;

    // =========================================================
    // DIMENSÕES
    // =========================================================

    private static final float MATCH_WIDTH =
        390f;

    private static final float MATCH_HEIGHT =
        96f;

    private static final float DATE_WIDTH =
        205f;

    private static final float DATE_HEIGHT =
        58f;

    private static final float BUTTON_WIDTH =
        245f;

    private static final float BUTTON_HEIGHT =
        58f;

    private static final float SCREEN_MARGIN =
        18f;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public CareerOverlay(
        Main game,
        Club club
    ) {

        this.game =
            game;

        this.club =
            club;

        setTouchable(
            Touchable.childrenOnly
        );

        setSize(
            ResponsiveViewport.DESIGN_WIDTH,
            ResponsiveViewport.DESIGN_HEIGHT
        );

        // =====================================================
        // CARD DA PARTIDA
        // =====================================================

        matchCard =
            new Table();

        matchCard.setSize(
            MATCH_WIDTH,
            MATCH_HEIGHT
        );

        addActor(
            matchCard
        );

        // =====================================================
        // CARD DE DATA
        // =====================================================

        dateCard =
            new Table();

        dateCard.setSize(
            DATE_WIDTH,
            DATE_HEIGHT
        );

        addActor(
            dateCard
        );

        // =====================================================
        // BOTÃO PRINCIPAL
        // =====================================================

        advanceButton =
            IconTextButton.create(
                "AVANÇAR DIA",
                game.skin,
                "Icons8/icons8-relógio-50.png"
            );

        advanceButton.setSize(
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        advanceButton
            .getLabel()
            .setFontScale(
                0.72f
            );

        advanceButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    /*
                     * Guardamos a tela anterior.
                     *
                     * Se advanceOneDay abrir a PreMatchScreen,
                     * não chamamos show() de novo na nova tela.
                     */
                    Screen screenBefore =
                        game.getScreen();

                    advanceOneDay(
                        game,
                        club
                    );

                    Screen screenAfter =
                        game.getScreen();

                    if (
                        screenBefore ==
                            screenAfter
                    ) {

                        /*
                         * Algumas telas constroem os valores
                         * dinâmicos no show().
                         */
                        if (
                            screenAfter != null
                        ) {

                            screenAfter.show();

                        } else {

                            refresh();
                        }
                    }
                }
            }
        );

        addActor(
            advanceButton
        );

        updatePositions();

        refresh();
    }

    // =========================================================
    // ATTACH
    // =========================================================

    public static CareerOverlay attach(
        Stage stage,
        Main game,
        Club club
    ) {

        CareerOverlay overlay =
            new CareerOverlay(
                game,
                club
            );

        stage.addActor(
            overlay
        );

        return overlay;
    }

    // =========================================================
    // RESPONSIVIDADE
    // =========================================================

    @Override
    public void act(
        float delta
    ) {

        if (getStage() != null) {
            setSize(
                getStage().getWidth(),
                getStage().getHeight()
            );
        }

        updatePositions();

        super.act(
            delta
        );
    }

    private void updatePositions() {

        float width =
            getWidth();

        float height =
            getHeight();

        // =====================================================
        // PRÓXIMA PARTIDA NO TOPO DIREITO
        // =====================================================

        matchCard.setPosition(
            Math.max(
                12f,
                width -
                    MATCH_WIDTH -
                    SCREEN_MARGIN
            ),
            Math.max(
                12f,
                height -
                    MATCH_HEIGHT -
                    10f
            )
        );

        // =====================================================
        // TEMPORADA / DATA NO CANTO INFERIOR ESQUERDO
        // =====================================================

        dateCard.setPosition(
            ScreenUI.PAGE_LEFT_OPEN + 8f,
            SCREEN_MARGIN
        );

        // =====================================================
        // AVANÇAR NO CANTO INFERIOR DIREITO
        // =====================================================

        advanceButton.setPosition(
            Math.max(
                12f,
                width -
                    BUTTON_WIDTH -
                    SCREEN_MARGIN
            ),
            SCREEN_MARGIN
        );
    }

    // =========================================================
    // REFRESH
    // =========================================================

    private void refresh() {

        refreshDateCard();

        refreshMatchCard();
    }

    // =========================================================
    // CARD TEMPORADA / DATA
    // =========================================================

    private void refreshDateCard() {

        dateCard.clear();

        dateCard.background(
            StyleFactory.createRoundedPanel(
                new Color(
                    0.025f,
                    0.070f,
                    0.050f,
                    0.98f
                ),
                StyleFactory.GOLD
            )
        );

        dateCard.pad(
            7f,
            14f,
            7f,
            14f
        );

        Label seasonLabel =
            new Label(
                "TEMPORADA " +
                    game.league
                        .getCurrentSeason(),
                game.skin,
                "font-bold"
            );

        seasonLabel.setFontScale(
            0.66f
        );

        seasonLabel.setColor(
            StyleFactory.GOLD
        );

        seasonLabel.setAlignment(
            Align.left
        );

        dateCard
            .add(seasonLabel)
            .left()
            .row();

        Date currentDate =
            game.league
                .getCurrentDate();

        String dateString =
            currentDate != null
                ? new SimpleDateFormat(
                "dd/MM/yyyy",
                new Locale(
                    "pt",
                    "BR"
                )
            ).format(
                currentDate
            )
                : "DATA N/A";

        Label dateLabel =
            new Label(
                dateString,
                game.skin
            );

        dateLabel.setFontScale(
            0.65f
        );

        dateLabel.setColor(
            Color.WHITE
        );

        dateCard
            .add(dateLabel)
            .left()
            .padTop(2f);
    }

    // =========================================================
    // CARD DA PARTIDA
    // =========================================================

    private void refreshMatchCard() {

        matchCard.clear();

        matchCard.background(
            StyleFactory.createRoundedPanel(
                new Color(
                    0.020f,
                    0.060f,
                    0.045f,
                    0.985f
                ),
                StyleFactory.GOLD
            )
        );

        matchCard.pad(
            8f,
            12f,
            8f,
            12f
        );

        Match next =
            game.league
                .getNextMatchForClub(
                    club
                );

        // =====================================================
        // SEM PRÓXIMA PARTIDA
        // =====================================================

        if (
            next == null
        ) {

            boolean playoffs =
                "PLAYOFFS".equals(
                    game.league
                        .getCurrentStage()
                );

            boolean offseason =
                "OFFSEASON".equals(
                    game.league
                        .getCurrentStage()
                );

            boolean leagueStillPlaying =
                game.league
                    .getNextMatch() != null;

            Label status =
                new Label(
                    offseason
                        ? "OFFSEASON • MERCADO E DRAFT"
                        : playoffs
                        ? leagueStillPlaying
                            ? "PLAYOFFS EM ANDAMENTO"
                            : "PLAYOFFS FINALIZADOS"
                        : "FASE REGULAR CONCLUÍDA",
                    game.skin,
                    "font-bold"
                );

            status.setFontScale(
                0.72f
            );

            status.setColor(
                StyleFactory.SOFT_YELLOW
            );

            status.setAlignment(
                Align.center
            );

            matchCard
                .add(status)
                .expand()
                .center();

            advanceButton.setText(
                offseason
                    ? "AVANÇAR DIA"
                    : playoffs
                    ? leagueStillPlaying
                        ? "AVANÇAR DIA"
                        : "INICIAR OFFSEASON"
                    : "INICIAR PLAYOFFS"
            );

            advanceButton.setDisabled(
                false
            );

            return;
        }

        // =====================================================
        // CLUBES
        // =====================================================

        Club opponent =
            next.getHomeTeam() == club
                ? next.getAwayTeam()
                : next.getHomeTeam();

        Table userCell =
            createTeamCell(
                club
            );

        Table opponentCell =
            createTeamCell(
                opponent
            );

        matchCard
            .add(userCell)
            .width(150f)
            .center();

        // =====================================================
        // VS
        // =====================================================

        Table middle =
            new Table();

        Label vs =
            new Label(
                "VS",
                game.skin,
                "font-bold"
            );

        vs.setFontScale(
            0.78f
        );

        vs.setColor(
            StyleFactory.GOLD
        );

        middle
            .add(vs)
            .center()
            .row();

        String matchDate =
            next.getDate() != null
                ? new SimpleDateFormat(
                "dd/MM",
                new Locale(
                    "pt",
                    "BR"
                )
            ).format(
                next.getDate()
            )
                : "";

        Label date =
            new Label(
                matchDate,
                game.skin
            );

        date.setFontScale(
            0.48f
        );

        date.setColor(
            ScreenUI.MUTED_TEXT
        );

        middle
            .add(date)
            .center()
            .padTop(2f);

        matchCard
            .add(middle)
            .width(54f)
            .center();

        matchCard
            .add(opponentCell)
            .width(150f)
            .center();

        // =====================================================
        // BOTÃO
        // =====================================================

        boolean canPlay =
            isMatchDay(
                next
            );

        advanceButton.setText(
            canPlay
                ? "JOGAR PARTIDA"
                : "AVANÇAR DIA"
        );

        advanceButton.setDisabled(
            false
        );
    }

    // =========================================================
    // TIME NO CARD
    // =========================================================

    private Table createTeamCell(
        Club team
    ) {

        Table cell =
            new Table();

        Image logo =
            new Image(
                new TextureRegionDrawable(
                    logo(
                        team
                    )
                )
            );

        logo.setScaling(
            Scaling.fit
        );

        cell
            .add(logo)
            .size(
                58f,
                42f
            )
            .center()
            .row();

        Label name =
            new Label(
                ScreenUI.shorten(
                    team.getName(),
                    20
                ),
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.54f
        );

        name.setAlignment(
            Align.center
        );

        name.setColor(
            StyleFactory.CREME_AGED
        );

        cell
            .add(name)
            .width(145f)
            .center()
            .padTop(3f);

        return cell;
    }

    // =========================================================
    // MATCH DAY
    // =========================================================

    private boolean isMatchDay(
        Match ignored
    ) {

        return isMatchDay(
            game,
            club
        );
    }

    public static boolean isMatchDay(
        Main game,
        Club club
    ) {

        Match userMatch =
            game.league
                .getNextMatchForClub(
                    club
                );

        if (
            userMatch == null ||
                game.league
                    .getCurrentDate() ==
                    null
        ) {

            return false;
        }

        Match nextGlobal =
            game.league
                .getNextMatch();

        boolean dateReached =
            !game.league
                .getCurrentDate()
                .before(
                    userMatch.getDate()
                );

        return
            nextGlobal ==
                userMatch &&
                dateReached;
    }

    // =========================================================
    // AVANÇO DE DATA
    // =========================================================

    public static void advanceOneDay(
        Main game,
        Club club
    ) {

        Match userMatch =
            game.league
                .getNextMatchForClub(
                    club
                );

        // =====================================================
        // FIM DA FASE
        // =====================================================

        if (
            userMatch == null &&
                game.league.getNextMatch() == null
        ) {

            if (
                "OFFSEASON".equals(
                    game.league.getCurrentStage()
                )
            ) {

                advanceOffseasonDay(
                    game,
                    club
                );

                return;
            }

            game.league
                .checkAndAdvanceStage();

            return;
        }

        // =====================================================
        // DIA DA PARTIDA
        // =====================================================

        if (
            isMatchDay(
                game,
                club
            )
        ) {

            game.setScreen(
                new PreMatchScreen(
                    game,
                    userMatch,
                    club
                )
            );

            return;
        }

        // =====================================================
        // DATA ANTERIOR
        // =====================================================

        Date previousDate =
            game.league
                .getCurrentDate();

        // =====================================================
        // AVANÇA UM DIA
        // =====================================================

        game.league
            .advanceDateOneDay();

        Date newDate =
            game.league
                .getCurrentDate();

        processDailyActivities(
            game,
            club,
            previousDate,
            newDate
        );

        // =====================================================
        // JOGOS DA IA
        // =====================================================

        processDueMatches(
            game,
            club
        );
    }

    /** A offseason também avança dia a dia para movimentações e scouting. */
    private static void advanceOffseasonDay(
        Main game,
        Club club
    ) {
        Date previousDate = game.league.getCurrentDate();
        game.league.advanceDateOneDay();
        Date newDate = game.league.getCurrentDate();
        processDailyActivities(game, club, previousDate, newDate);

        if (isFirstDayOfNewSeason(game, newDate)) {
            game.league.startNewSeason();
            game.seasonSimulator.createSchedule(game.league);
            if (!game.league.getSchedule().isEmpty()) {
                game.league.setLastProcessedDate(game.league.getSchedule().get(0).getDate());
            }
        }
    }

    private static void processDailyActivities(
        Main game,
        Club club,
        Date previousDate,
        Date newDate
    ) {
        if (changedMonth(previousDate, newDate)) {
            processMonthlyFinances(game);
        }
        if (game.draftScoutManager != null) {
            game.draftScoutManager.advanceDay();
        }
        recoverAllPlayers(game, 1);
        if (changedWeek(previousDate, newDate) && game.developmentEngine != null) {
            game.developmentEngine.updateWeekly(game.league);
        }
        if (game.freeAgencyService != null) {
            game.freeAgencyService.processPendingOffers(club, game.league.getCurrentSeason());
        }
    }

    private static boolean isFirstDayOfNewSeason(Main game, Date date) {
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == Calendar.JANUARY
            && calendar.get(Calendar.DAY_OF_MONTH) == 1
            && calendar.get(Calendar.YEAR) > game.league.getCurrentSeason();
    }

    // =========================================================
    // MUDANÇA DE MÊS
    // =========================================================

    private static boolean changedMonth(
        Date previousDate,
        Date newDate
    ) {

        if (
            previousDate == null ||
                newDate == null
        ) {

            return false;
        }

        Calendar previous =
            Calendar.getInstance();

        previous.setTime(
            previousDate
        );

        Calendar current =
            Calendar.getInstance();

        current.setTime(
            newDate
        );

        return
            previous.get(
                Calendar.MONTH
            )
                !=
                current.get(
                    Calendar.MONTH
                )
                ||
                previous.get(
                    Calendar.YEAR
                )
                    !=
                    current.get(
                        Calendar.YEAR
                    );
    }

    private static boolean changedWeek(
        Date previousDate,
        Date newDate
    ) {

        if (
            previousDate == null ||
                newDate == null
        ) {

            return false;
        }

        Calendar previous = Calendar.getInstance();
        previous.setTime(previousDate);
        Calendar current = Calendar.getInstance();
        current.setTime(newDate);

        return previous.get(Calendar.WEEK_OF_YEAR) != current.get(Calendar.WEEK_OF_YEAR) ||
            previous.get(Calendar.YEAR) != current.get(Calendar.YEAR);
    }

    // =========================================================
    // FINANÇAS MENSAIS
    // =========================================================

    private static void processMonthlyFinances(
        Main game
    ) {

        if (
            game == null ||
                game.league == null
        ) {

            return;
        }

        for (
            Club team :
            game.league
                .getClubs()
        ) {

            if (
                team != null &&
                    team.getFinance() != null
            ) {

                team.getFinance()
                    .applyMonthlyBalance();
            }
        }
    }

    // =========================================================
    // SIMULA PARTIDAS DA IA
    // =========================================================

    private static void processDueMatches(
        Main game,
        Club playerClub
    ) {

        Match due =
            game.league
                .getNextMatch();

        Match userMatch =
            game.league
                .getNextMatchForClub(
                    playerClub
                );

        while (
            due != null &&
                due != userMatch &&
                game.league
                    .getCurrentDate() != null &&
                !due.getDate()
                    .after(
                        game.league
                            .getCurrentDate()
                    )
        ) {

            game.matchEngine
                .simulate(
                    due
                );

            game.league
                .advanceMatch();

            due =
                game.league
                    .getNextMatch();

            userMatch =
                game.league
                    .getNextMatchForClub(
                        playerClub
                    );
        }
    }

    // =========================================================
    // RECUPERAÇÃO DOS JOGADORES
    // =========================================================

    private static void recoverAllPlayers(
        Main game,
        int days
    ) {

        for (
            Club team :
            game.league
                .getClubs()
        ) {

            for (
                Player player :
                team.getSquad()
            ) {

                player.recover(
                    days
                );
            }
        }
    }

    // =========================================================
    // LOGOS
    // =========================================================

    private static Texture logo(
        Club team
    ) {

        String path =
            team != null
                ? team.getLogoPath()
                : null;

        String key =
            path != null
                ? path
                : "__fallback__";

        Texture texture =
            LOGOS.get(
                key
            );

        if (
            texture != null
        ) {

            return texture;
        }

        try {

            if (
                path != null &&
                    Gdx.files
                        .internal(path)
                        .exists()
            ) {

                texture =
                    new Texture(
                        Gdx.files.internal(
                            path
                        )
                    );

            } else {

                texture =
                    new Texture(
                        Gdx.files.internal(
                            "libgdx.png"
                        )
                    );
            }

        } catch (
            Exception e
        ) {

            texture =
                new Texture(
                    Gdx.files.internal(
                        "libgdx.png"
                    )
                );
        }

        LOGOS.put(
            key,
            texture
        );

        return texture;
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    public static void disposeAssets() {

        for (
            Texture texture :
            LOGOS.values()
        ) {

            texture.dispose();
        }

        LOGOS.clear();
    }
}
