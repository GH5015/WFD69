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
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.model.StaffImpact;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.screens.DraftSelectionDialog;
import io.github.some_example_name.utils.PlayerReportDialog;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

public class DraftScoutingScreen implements Screen {

    private final Main game;
    private final Club club;

    private final Stage stage;

    private final Texture backgroundTexture;

    private final DraftScoutManager scoutManager;

    private final List<Player> draftClass1970;

    // =========================================================
    // COLUNAS
    // =========================================================

    private static final float COL_NAC =
        80f;

    private static final float COL_PLAYER =
        290f;

    private static final float COL_AGE =
        80f;

    private static final float COL_POS =
        90f;

    private static final float COL_OVR =
        115f;

    private static final float COL_POT =
        115f;

    private static final float COL_KNOWLEDGE =
        185f;

    private static final float COL_ACTION =
        95f;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public DraftScoutingScreen(
        Main game,
        Club club,
        DraftScoutManager scoutManager
    ) {

        this.game =
            game;

        this.club =
            club;

        this.scoutManager =
            scoutManager;

        /*
         * Usa a classe persistente criada no Main.
         * Não gera novos Players ao reabrir a tela.
         */
        this.draftClass1970 =
            game.draftClass;

        this.stage =
            new Stage(
                new ResponsiveViewport()
            );

        this.backgroundTexture =
            new Texture(
                Gdx.files.internal(
                    "prancheta.png"
                )
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

        Image background =
            new Image(
                new TextureRegionDrawable(
                    backgroundTexture
                )
            );

        background.setFillParent(
            true
        );

        root.add(
            background
        );

        Table page =
            ScreenUI.createPage(
                true
            );

        // =====================================================
        // HEADER
        // =====================================================

        int overallKnowledge =
            scoutManager != null
                ? scoutManager
                .getOverallClassKnowledge(
                    draftClass1970 != null
                        ? draftClass1970.size()
                        : 0
                )
                : 0;

        page
            .add(
                ScreenUI.createHeader(
                    game.skin,
                    "DRAFT SCOUTING • CLASSE DE " + game.draftClassYear,
                    (SeasonCalendar.isDraftOpen(game.league)
                        ? "DRAFT ABERTO • "
                        : "SCOUTING ABERTO • DRAFT EM DEZEMBRO • ") +
                    "CONHECIMENTO GERAL  " +
                        overallKnowledge +
                        "%"
                )
            )
            .growX()
            .height(
                ScreenUI.HEADER_HEIGHT
            )
            .padBottom(10f)
            .row();

        // =====================================================
        // SCOUT
        // =====================================================

        page
            .add(
                createScoutPanel()
            )
            .growX()
            .height(64f)
            .padBottom(10f)
            .row();

        // =====================================================
        // TABLE
        // =====================================================

        page
            .add(
                createScoutingPanel()
            )
            .grow()
            .padBottom(10f)
            .row();

        // =====================================================
        // ADD
        // =====================================================

        Table actions =
            new Table();

        ImageTextButton add =
            IconTextButton.create(
                "OBSERVAR JOGADOR",
                game.skin,
                "Icons8/icons8-binóculos-50.png"
            );

        add.getLabel()
            .setFontScale(
                0.66f
            );

        boolean full =
            scoutManager == null ||
                scoutManager.isFull();

        add.setDisabled(
            full
        );

        add.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    if (
                        scoutManager == null ||
                            scoutManager.isFull() ||
                            draftClass1970 ==
                                null
                    ) {

                        return;
                    }

                    DraftSelectionDialog dialog =
                        new DraftSelectionDialog(
                            game.skin,
                            stage,
                            scoutManager,
                            draftClass1970,
                            () ->
                                refreshUI()
                        );

                    dialog.show(
                        stage
                    );
                }
            }
        );

        actions
            .add(add)
            .width(300f)
            .height(52f)
            .center()
            .padRight(8f);

        TextButton completed =
            ScreenUI.createInteractiveButton(
                "OBSERVADOS 100% (" +
                    (scoutManager != null ? scoutManager.getCompletedTargets().size() : 0) +
                    ")",
                game.skin
            );

        completed.getLabel().setFontScale(0.54f);
        completed.setDisabled(scoutManager == null || scoutManager.getCompletedTargets().isEmpty());
        completed.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (scoutManager != null && !scoutManager.getCompletedTargets().isEmpty()) {
                        game.setScreen(new CompletedScoutingScreen(game, club, scoutManager));
                    }
                }
            }
        );

        actions
            .add(completed)
            .width(270f)
            .height(52f)
            .center()
            .padRight(8f);

        TextButton draftBoard = ScreenUI.createInteractiveButton("PROJEÇÃO DO DRAFT", game.skin);
        draftBoard.getLabel().setFontScale(0.54f);
        draftBoard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DraftBoardScreen(game, club, scoutManager));
            }
        });
        actions.add(draftBoard).width(240f).height(52f).center();

        page
            .add(actions)
            .growX()
            .height(60f)
            .row();

        root.add(
            page
        );

        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
            Table returnOverlay = new Table(); returnOverlay.setFillParent(true); returnOverlay.bottom().left().pad(18f);
            TextButton back = ScreenUI.createInteractiveButton("← VOLTAR À OFF SEASON", game.skin);
            back.getLabel().setFontScale(0.45f);
            back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ game.setScreen(new OffSeasonScreen(game, club)); }});
            returnOverlay.add(back).width(235f).height(42f); root.add(returnOverlay);
        } else NavigationDrawer.attach(stage, game, club, "SCOUTING", true);

    }

    // =========================================================
    // SCOUT PANEL
    // =========================================================

    private Table createScoutPanel() {

        Table panel =
            ScreenUI.createPanel();

        int stars =
            scoutManager != null
                ? scoutManager
                .getScoutStars()
                : 0;

        int occupied =
            scoutManager != null
                ? scoutManager
                .getActiveTargets()
                .size()
                : 0;

        // =====================================================
        // SCOUT
        // =====================================================

        Label prefix =
            ScreenUI.createSubtitle(
                game.skin,
                "SCOUT PRINCIPAL"
            );

        panel
            .add(prefix)
            .left()
            .padRight(10f);

        StaffMember scoutMember = club.getStaffMember(StaffRole.SCOUT);
        Label name =
            new Label(
                scoutMember != null ? scoutMember.getName() : "Scout interino",
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.68f
        );

        name.setColor(
            Color.WHITE
        );

        panel
            .add(name)
            .left()
            .padRight(12f);

        Label starLabel =
            new Label(
                buildStars(
                    stars
                ),
                game.skin,
                "font-bold"
            );

        starLabel.setFontScale(
            0.61f
        );

        starLabel.setColor(
            StyleFactory.GOLD
        );

        panel
            .add(starLabel)
            .left();

        panel
            .add()
            .expandX();

        // =====================================================
        // SLOTS
        // =====================================================

        int maxSlots = scoutManager != null ? scoutManager.getMaxScoutedPlayers() : 0;
        Table rate = ScreenUI.createStatusBox(
            game.skin,
            "RITMO DIÁRIO",
            String.format(java.util.Locale.US, "%.1f%%", StaffImpact.scoutingDailyProgress(stars)),
            StyleFactory.SOFT_YELLOW
        );
        panel.add(rate).width(170f).height(42f).padRight(6f);

        Table precision = ScreenUI.createStatusBox(
            game.skin,
            "PRECISÃO",
            stars >= 5 ? "EXCELENTE" : stars >= 4 ? "ALTA" : stars >= 3 ? "MÉDIA" : "BAIXA",
            stars >= 4 ? ScreenUI.SUCCESS : stars >= 3 ? StyleFactory.SOFT_YELLOW : ScreenUI.WARNING
        );
        panel.add(precision).width(170f).height(42f).padRight(6f);

        Table slots =
            ScreenUI.createStatusBox(
                game.skin,
                "VAGAS OCUPADAS",
                occupied +
                    "/" +
                    maxSlots,
                occupied >=
                    maxSlots
                    ? ScreenUI.DANGER
                    : ScreenUI.SUCCESS
            );

        panel
            .add(slots)
            .width(230f)
            .height(42f);

        return panel;
    }

    // =========================================================
    // SCOUTING TABLE
    // =========================================================

    private Table createScoutingPanel() {

        Table panel =
            ScreenUI.createTablePanel();

        Table content =
            new Table();

        content.top();

        // =====================================================
        // HEADER
        // =====================================================

        content
            .add(
                createTableHeader()
            )
            .growX()
            .height(52f)
            .row();

        int rowIndex =
            0;

        if (
            scoutManager != null
        ) {

            for (
                ScoutTarget target :
                scoutManager
                    .getActiveTargets()
            ) {

                content
                    .add(
                        createTargetRow(
                            target,
                            rowIndex++
                        )
                    )
                    .growX()
                    .height(62f)
                    .row();
            }
        }

        // =====================================================
        // EMPTY SLOTS
        // =====================================================

        while (
            rowIndex <
                (scoutManager != null ? scoutManager.getMaxScoutedPlayers() : 0)
        ) {

            content
                .add(
                    createEmptyRow(
                        rowIndex
                    )
                )
                .growX()
                .height(62f)
                .row();

            rowIndex++;
        }

        ScrollPane scroll =
            new ScrollPane(
                content,
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

    // =========================================================
    // HEADER
    // =========================================================

    private Table createTableHeader() {

        Table row =
            ScreenUI.createTableHeaderRow();

        addHeader(
            row,
            "NAC",
            COL_NAC,
            Align.center
        );

        addHeader(
            row,
            "JOGADOR",
            COL_PLAYER,
            Align.left
        );

        addHeader(
            row,
            "IDADE",
            COL_AGE,
            Align.center
        );

        addHeader(
            row,
            "POS",
            COL_POS,
            Align.center
        );

        addHeader(
            row,
            "OVR EST.",
            COL_OVR,
            Align.center
        );

        addHeader(
            row,
            "POT EST.",
            COL_POT,
            Align.center
        );

        addHeader(
            row,
            "CONHECIMENTO",
            COL_KNOWLEDGE,
            Align.center
        );

        addHeader(
            row,
            "AÇÃO",
            COL_ACTION,
            Align.center
        );

        return row;
    }

    // =========================================================
    // TARGET ROW
    // =========================================================

    private Table createTargetRow(
        ScoutTarget target,
        int index
    ) {

        Table row =
            ScreenUI.createRow(
                index
            );

        Player player =
            target.getPlayer();

        // =====================================================
        // NATIONALITY
        // =====================================================

        String nationality =
            player.getNationality() !=
                null
                ? player.getNationality()
                : "N/A";

        String shortNationality =
            nationality.substring(
                0,
                Math.min(
                    3,
                    nationality.length()
                )
            ).toUpperCase();

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    shortNationality,
                    ScreenUI.MUTED_TEXT,
                    Align.center
                )
            )
            .width(
                COL_NAC
            );

        // =====================================================
        // NAME
        // =====================================================

        Label name =
            new Label(
                player.getName(),
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.63f
        );

        name.setColor(
            StyleFactory.CREME_AGED
        );

        name.setAlignment(
            Align.left
        );

        name.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    openReport(
                        target
                    );
                }
            }
        );

        row
            .add(name)
            .width(
                COL_PLAYER
            )
            .left()
            .padLeft(10f);

        // =====================================================
        // AGE
        // =====================================================

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    String.valueOf(
                        player.getAge()
                    ),
                    Color.WHITE,
                    Align.center
                )
            )
            .width(
                COL_AGE
            );

        // =====================================================
        // POSITION
        // =====================================================

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                player
                    .getPrimaryPosition()
                    .name(),
                StyleFactory
                    .getPositionColor(
                        player
                            .getPrimaryPosition()
                            .name()
                    )
            );

        row
            .add(badge)
            .width(
                COL_POS
            )
            .height(27f);

        // =====================================================
        // OVR
        // =====================================================

        String overall =
            target
                .getDisplayOverall();

        row
            .add(
                createGradeLabel(
                    overall
                )
            )
            .width(
                COL_OVR
            );

        // =====================================================
        // POT
        // =====================================================

        String potential =
            target
                .getDisplayPotential();

        row
            .add(
                createGradeLabel(
                    potential
                )
            )
            .width(
                COL_POT
            );

        // =====================================================
        // KNOWLEDGE
        // =====================================================

        row
            .add(
                createKnowledge(
                    target
                )
            )
            .width(
                COL_KNOWLEDGE
            );

        // =====================================================
        // ACTION
        // =====================================================

        TextButton view =
            ScreenUI.createInteractiveButton(
                "VER",
                game.skin
            );

        view.getLabel()
            .setFontScale(
                0.56f
            );

        view.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    openReport(
                        target
                    );
                }
            }
        );

        row
            .add(view)
            .width(70f)
            .height(34f);

        return row;
    }

    // =========================================================
    // EMPTY
    // =========================================================

    private Table createEmptyRow(
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
                        index + 1
                    ),
                    Color.valueOf(
                        "666A66"
                    ),
                    Align.center
                )
            )
            .width(
                COL_NAC
            );

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    "—  Vaga disponível",
                    Color.valueOf(
                        "777B77"
                    ),
                    Align.left
                )
            )
            .width(
                COL_PLAYER
            )
            .padLeft(10f);

        addEmpty(
            row,
            COL_AGE
        );

        addEmpty(
            row,
            COL_POS
        );

        addEmpty(
            row,
            COL_OVR
        );

        addEmpty(
            row,
            COL_POT
        );

        addEmpty(
            row,
            COL_KNOWLEDGE
        );

        addEmpty(
            row,
            COL_ACTION
        );

        return row;
    }

    // =========================================================
    // KNOWLEDGE
    // =========================================================

    private Table createKnowledge(
        ScoutTarget target
    ) {

        Table container =
            new Table();

        double knowledge =
            target
                .getKnowledgePercentage();

        Color color =
            target.isFullyScouted()
                ? ScreenUI.SUCCESS
                : StyleFactory.GOLD;

        Label percentage =
            ScreenUI.createBoldValue(
                game.skin,
                String.format(
                    "%.1f%%",
                    knowledge
                ),
                target.isFullyScouted()
                    ? ScreenUI.SUCCESS
                    : Color.WHITE,
                Align.center
            );

        container
            .add(percentage)
            .width(68f)
            .padRight(5f);

        container
            .add(
                ScreenUI.createBlockProgress(
                    game.skin,
                    knowledge,
                    8,
                    color
                )
            )
            .center();

        return container;
    }

    // =========================================================
    // REPORT
    // =========================================================

    private void openReport(
        ScoutTarget target
    ) {

        PlayerReportDialog dialog =
            new PlayerReportDialog(
                game.skin,
                target,
                scoutManager,
                () ->
                    refreshUI()
            );

        dialog.show(
            stage
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Label createGradeLabel(
        String grade
    ) {

        Label label =
            new Label(
                grade != null
                    ? grade
                    : "?",
                game.skin,
                "font-bold"
            );

        label.setFontScale(
            0.68f
        );

        label.setAlignment(
            Align.center
        );

        label.setColor(
            getGradeColor(
                grade
            )
        );

        return label;
    }

    private Color getGradeColor(
        String grade
    ) {

        if (
            grade == null ||
                grade.isEmpty() ||
                "?".equals(
                    grade
                )
        ) {

            return Color.GRAY;
        }

        switch (
            grade.charAt(
                0
            )
        ) {

            case 'A':
                return ScreenUI.SUCCESS;

            case 'B':
                return StyleFactory.SOFT_YELLOW;

            case 'C':
                return Color.ORANGE;

            case 'D':
            case 'F':
                return ScreenUI.DANGER;

            default:
                return StyleFactory.CREME_AGED;
        }
    }

    private String buildStars(
        int stars
    ) {
        return ScreenUI.formatStars(stars);
    }

    private void addHeader(
        Table table,
        String text,
        float width,
        int alignment
    ) {

        table
            .add(
                ScreenUI.createTableHeaderLabel(
                    game.skin,
                    text,
                    alignment
                )
            )
            .width(
                width
            );
    }

    private void addEmpty(
        Table row,
        float width
    ) {

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    "—",
                    Color.valueOf(
                        "666A66"
                    ),
                    Align.center
                )
            )
            .width(
                width
            );
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

        backgroundTexture.dispose();
    }
}
