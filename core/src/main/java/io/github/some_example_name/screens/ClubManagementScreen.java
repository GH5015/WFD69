package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.utils.ResponsiveViewport;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.BoardObjective;
import io.github.some_example_name.model.BoardObjectiveService;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.PlayerDevelopmentDialog;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClubManagementScreen implements Screen {

    private final Main game;
    private final Club club;

    private final Stage stage;

    private final Texture pranchetaTexture;
    private final Texture fieldTexture;

    private final Texture boardStarTexture;

    private Texture clubLogoTexture;

    private String sortType =
        "OVR";

    private boolean sortAscending =
        false;

    private String positionFilter =
        "TODOS";

    public ClubManagementScreen(
        Main game,
        Club club
    ) {

        this.game =
            game;

        this.club =
            club;

        stage =
            new Stage(
                new ResponsiveViewport()
            );

        pranchetaTexture =
            new Texture(
                Gdx.files.internal(
                    "prancheta.png"
                )
            );

        fieldTexture =
            new Texture(
                Gdx.files.internal(
                    "campo.png"
                )
            );

        boardStarTexture =
            new Texture(
                Gdx.files.internal(
                    "Icons8/icons8-estrela-48.png"
                )
            );

        clubLogoTexture =
            loadLogo(
                club
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

        /*
         * Lesões e expulsões do clube podem ter ocorrido durante a partida.
         * O alerta é mostrado somente depois de retornar ao elenco.
         */
        showNextPostMatchSquadAlert();
    }

    // =========================================================
    // ALERTAS PÓS-PARTIDA
    // =========================================================

    private void showNextPostMatchSquadAlert() {
        final MatchEvent alert = game.consumeSquadAlert();

        if (alert == null) {
            BoardReviewDialog.showDue(stage, game, club);
            return;
        }

        final boolean injury =
            "LESIONADO".equals(alert.type);

        Dialog dialog = new Dialog(
            "",
            game.skin
        ) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    game.setScreen(
                        new TacticsScreen(
                            game,
                            club
                        )
                    );
                    return;
                }

                /* Se houver mais de uma ocorrência, elas aparecem em ordem. */
                showNextPostMatchSquadAlert();
            }
        };

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        Color accent = injury
            ? ScreenUI.WARNING
            : ScreenUI.DANGER;

        Table content = dialog.getContentTable();
        content.clear();
        content.background(
            StyleFactory.createMetallicBoard(
                730,
                350,
                Color.valueOf("151A17")
            )
        );
        content.pad(21f, 30f, 16f, 30f);

        Label title = new Label(
            injury
                ? "LESÃO NO ELENCO"
                : "EXPULSÃO CONFIRMADA",
            game.skin,
            "font-title"
        );
        title.setFontScale(0.84f);
        title.setColor(accent);
        title.setAlignment(Align.center);

        Label minute = new Label(
            "OCORRÊNCIA DA PARTIDA • " + alert.minute + "'",
            game.skin,
            "font-bold"
        );
        minute.setFontScale(0.45f);
        minute.setColor(StyleFactory.SOFT_YELLOW);
        minute.setAlignment(Align.center);

        Label description = new Label(
            alert.description != null
                ? alert.description
                : "Um jogador do seu clube sofreu uma ocorrência grave.",
            game.skin
        );
        description.setFontScale(0.55f);
        description.setColor(Color.WHITE);
        description.setAlignment(Align.center);
        description.setWrap(true);

        Label nextStep = new Label(
            injury
                ? "O jogador ficará indisponível; ajuste escalação e elenco se necessário."
                : "O jogador cumprirá suspensão; revise sua escalação antes da próxima partida.",
            game.skin
        );
        nextStep.setFontScale(0.48f);
        nextStep.setColor(ScreenUI.MUTED_TEXT);
        nextStep.setAlignment(Align.center);
        nextStep.setWrap(true);

        content.add(title)
            .width(650f)
            .center()
            .padBottom(9f)
            .row();

        content.add(minute)
            .width(650f)
            .center()
            .padBottom(13f)
            .row();

        content.add(description)
            .width(620f)
            .center()
            .padBottom(13f)
            .row();

        content.add(nextStep)
            .width(620f)
            .center();

        TextButton continueButton = ScreenUI.createInteractiveButton(
            "CONTINUAR NO ELENCO",
            game.skin
        );
        continueButton.getLabel().setFontScale(0.54f);

        TextButton lineupButton = ScreenUI.createPrimaryButton(
            game.skin,
            "AJUSTAR ESCALAÇÃO"
        );
        lineupButton.getLabel().setFontScale(0.55f);

        dialog.button(continueButton, false);
        dialog.button(lineupButton, true);
        dialog.show(stage);
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
                    pranchetaTexture
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

        page
            .add(
                createHeader()
            )
            .growX()
            .height(78f)
            .padBottom(10f)
            .row();

        // =====================================================
        // STATUS RÁPIDO
        // =====================================================

        page
            .add(
                createStatusBar()
            )
            .growX()
            .height(60f)
            .padBottom(10f)
            .row();

        // =====================================================
        // DIRETORIA E OBJETIVOS DA TEMPORADA
        // =====================================================

        page
            .add(
                createBoardPanel()
            )
            .growX()
            .height(158f)
            .padBottom(10f)
            .row();

        // =====================================================
        // CORPO
        // =====================================================

        Table body =
            new Table();

        float usableWidth =
            Math.max(
                850f,
                stage.getWidth() -
                    ScreenUI.PAGE_LEFT_OPEN -
                    ScreenUI.PAGE_RIGHT
            );

        body
            .add(
                createRosterPanel()
            )
            .width(
                usableWidth *
                    0.62f
            )
            .growY()
            .padRight(10f);

        body
            .add(
                createFieldPreview()
            )
            .width(
                usableWidth *
                    0.35f
            )
            .growY();

        page
            .add(body)
            .grow()
            .row();

        root.add(
            page
        );

        NavigationDrawer.attach(
            stage,
            game,
            club,
            "ELENCO",
            true
        );

        CareerOverlay.attach(
            stage,
            game,
            club
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader() {

        Table header =
            ScreenUI.createPanel();

        if (
            clubLogoTexture != null
        ) {

            Image logo =
                new Image(
                    new TextureRegionDrawable(
                        clubLogoTexture
                    )
                );

            logo.setScaling(
                Scaling.fit
            );

            header
                .add(logo)
                .width(92f)
                .height(54f)
                .padRight(14f);
        }

        Table titleArea =
            new Table();

        Label title =
            new Label(
                "ELENCO",
                game.skin,
                "font-title"
            );

        title.setFontScale(
            0.84f
        );

        title.setColor(
            StyleFactory.GOLD
        );

        titleArea
            .add(title)
            .left()
            .row();

        Label subtitle =
            new Label(
                club.getName().toUpperCase(),
                game.skin,
                "font-bold"
            );

        subtitle.setFontScale(
            0.58f
        );

        subtitle.setColor(
            ScreenUI.MUTED_TEXT
        );

        titleArea
            .add(subtitle)
            .left();

        header
            .add(titleArea)
            .left()
            .expandX();

        ImageTextButton playoffTestButton =
            IconTextButton.create(
                "TESTAR PLAYOFFS",
                game.skin,
                "Icons8/icons8-relógio-50.png"
            );

        playoffTestButton
            .getLabel()
            .setFontScale(
                0.52f
            );

        playoffTestButton.setDisabled(
            !"REGULAR".equals(
                game.league.getCurrentStage()
            )
        );

        playoffTestButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    simulateUntilPlayoffs();
                }
            }
        );

        header
            .add(playoffTestButton)
            .width(190f)
            .height(42f)
            .padRight(8f);

        ImageTextButton tacticsButton =
            IconTextButton.create(
                "EDITAR TÁTICA",
                game.skin,
                "Icons8/icons8-estrutura-em-árvore-50.png"
            );

        tacticsButton
            .getLabel()
            .setFontScale(
                0.62f
            );

        tacticsButton.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    game.setScreen(
                        new TacticsScreen(
                            game,
                            club
                        )
                    );
                }
            }
        );

        header
            .add(tacticsButton)
            .width(190f)
            .height(42f);

        return header;
    }

    /**
     * Atalho de desenvolvimento para avançar toda a fase regular sem abrir
     * cada partida. A simulação para assim que a chave dos playoffs é criada.
     */
    private void simulateUntilPlayoffs() {

        if (
            !"REGULAR".equals(
                game.league.getCurrentStage()
            )
        ) {

            showPlayoffTestFeedback(
                "Os playoffs já estão em andamento."
            );

            return;
        }

        int simulatedMatches =
            0;

        while (
            "REGULAR".equals(
                game.league.getCurrentStage()
            ) &&
                game.league.getNextMatch() != null
        ) {

            game.matchEngine.simulate(
                game.league.getNextMatch()
            );

            game.league.advanceMatch();

            simulatedMatches++;
        }

        refreshUI();

        showPlayoffTestFeedback(
            simulatedMatches +
                " partidas da fase regular foram simuladas.\n" +
                "A chave está disponível em TABELA > PLAYOFFS."
        );
    }

    private void showPlayoffTestFeedback(
        String message
    ) {

        Dialog dialog =
            new Dialog(
                "TESTE DE PLAYOFFS",
                game.skin
            );

        Label text =
            new Label(
                message,
                game.skin
            );

        text.setAlignment(
            Align.center
        );

        text.setWrap(
            true
        );

        dialog
            .getContentTable()
            .add(text)
            .width(430f)
            .pad(18f);

        dialog.button(
            "OK",
            true
        );

        dialog.show(stage);
    }

    // =========================================================
    // STATUS BAR
    // =========================================================

    private Table createStatusBar() {

        Table bar =
            new Table();

        bar.add(
            ScreenUI.createStatusBox(
                game.skin,
                "JOGADORES",
                club.getSquad().size() +
                    "/26",
                club.getSquad().size() >= 23
                    ? ScreenUI.SUCCESS
                    : ScreenUI.WARNING
            )
        ).growX().uniformX().padRight(8f);

        bar.add(
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
        ).growX().uniformX().padRight(8f);

        int unavailable =
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
        }

        bar.add(
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
        ).growX().uniformX().padRight(8f);

        bar.add(
            ScreenUI.createStatusBox(
                game.skin,
                "FORMAÇÃO",
                club.getFormation() != null
                    ? club.getFormation().getName()
                    : "N/D",
                StyleFactory.SOFT_YELLOW
            )
        ).growX().uniformX();

        return bar;
    }

    // =========================================================
    // DIRETORIA
    // =========================================================

    private Table createBoardPanel() {
        BoardObjectiveService.Evaluation evaluation =
            BoardObjectiveService.evaluate(game.league, club);

        Table panel = new Table();
        panel.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("0B1711"),
                StyleFactory.DARK_GOLD
            )
        );
        panel.pad(10f);

        panel
            .add(createBoardConfidence(
                evaluation,
                game.managerCareer.calculateHistoryConfidenceBonus(
                    club,
                    game.league.getCurrentSeason()
                )
            ))
            .width(300f)
            .growY()
            .padRight(10f);

        Table objectives = new Table();
        List<BoardObjectiveService.ObjectiveProgress> progress =
            evaluation.getObjectives();

        for (int i = 0; i < progress.size(); i++) {
            objectives
                .add(createObjectiveCard(progress.get(i)))
                .grow()
                .uniformX()
                .padRight(i < progress.size() - 1 ? 7f : 0f);
        }

        panel.add(objectives).grow();
        return panel;
    }

    private Table createBoardConfidence(
        BoardObjectiveService.Evaluation evaluation,
        int historyBonus
    ) {
        int effectiveConfidence = Math.min(100, evaluation.getConfidence() + historyBonus);
        Color statusColor = boardStatusColor(effectiveConfidence);

        Table box = new Table();
        box.top().left();
        box.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("111F17"),
                statusColor
            )
        );
        box.pad(10f, 13f, 9f, 13f);

        Label title = new Label(
            "CONFIANÇA DA DIRETORIA",
            game.skin,
            "font-bold"
        );
        title.setFontScale(0.54f);
        title.setColor(StyleFactory.SOFT_YELLOW);

        Label status = new Label(
            evaluation.getStatus(),
            game.skin,
            "font-title"
        );
        status.setFontScale(0.72f);
        status.setColor(statusColor);

        Label description = new Label(
            boardStatusDescription(evaluation.getConfidence()),
            game.skin
        );
        description.setFontScale(0.47f);
        description.setColor(ScreenUI.MUTED_TEXT);
        description.setWrap(true);

        Table heading = new Table();
        heading.add(title).growX().left();
        for (int index = 0; index < 5; index++) {
            Image star = new Image(new TextureRegionDrawable(boardStarTexture));
            star.setScaling(Scaling.fit);
            star.setColor(index < game.managerCareer.getReputationStars()
                ? StyleFactory.GOLD : Color.valueOf("414A43"));
            heading.add(star).size(12f).padLeft(1f);
        }

        status.setText(boardStatusLabel(effectiveConfidence));
        description.setText(
            "Histórico: +" + historyBonus + " • Prestígio "
                + game.managerCareer.getReputationStars() + "/5\n"
                + boardStatusDescription(effectiveConfidence)
        );

        box.add(heading).growX().left().row();
        box.add(status).left().padTop(2f).row();
        box.add(createSegmentedProgress(effectiveConfidence, statusColor, 14))
            .growX().height(8f).padTop(5f).padBottom(5f).row();
        box.add(description).growX().left();
        return box;
    }

    private Table createObjectiveCard(
        BoardObjectiveService.ObjectiveProgress progress
    ) {
        BoardObjective objective = progress.getObjective();
        Color priorityColor = objective.getPriority() == BoardObjective.Priority.CRITICAL
            ? StyleFactory.GOLD
            : objective.getPriority() == BoardObjective.Priority.IMPORTANT
                ? StyleFactory.SOFT_YELLOW
                : ScreenUI.MUTED_TEXT;

        Table card = new Table();
        card.top().left();
        card.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("15221A"),
                Color.valueOf("3B4B40")
            )
        );
        card.pad(8f, 9f, 7f, 9f);

        Table priority = new Table();
        for (int i = 0; i < objective.getPriority().getStars(); i++) {
            Image star = new Image(
                new TextureRegionDrawable(boardStarTexture)
            );
            star.setColor(priorityColor);
            star.setScaling(Scaling.fit);
            priority.add(star).size(13f).padRight(1f);
        }

        Label priorityName = new Label(
            objective.getPriority().getLabel(),
            game.skin,
            "font-bold"
        );
        priorityName.setFontScale(0.42f);
        priorityName.setColor(priorityColor);
        priority.add(priorityName).padLeft(4f).left();

        Label category = new Label(
            objective.getCategory().getLabel(),
            game.skin,
            "font-bold"
        );
        category.setFontScale(0.39f);
        category.setColor(ScreenUI.MUTED_TEXT);

        Table top = new Table();
        top.add(priority).left();
        top.add().growX();
        top.add(category).right();

        Label objectiveTitle = new Label(
            objective.getTitle(),
            game.skin,
            "font-bold"
        );
        objectiveTitle.setFontScale(0.46f);
        objectiveTitle.setColor(StyleFactory.TEXT_PRIMARY);
        objectiveTitle.setWrap(true);
        objectiveTitle.setAlignment(Align.left);

        Label detail = new Label(
            progress.getDetail() + "  •  " + progress.getState(),
            game.skin
        );
        detail.setFontScale(0.42f);
        detail.setColor(objectiveProgressColor((int) Math.round(progress.getPercentage())));
        detail.setEllipsis(true);

        int percentage = (int) Math.round(progress.getPercentage());
        Label percentageLabel = new Label(
            percentage + "%",
            game.skin,
            "font-bold"
        );
        percentageLabel.setFontScale(0.42f);
        percentageLabel.setColor(objectiveProgressColor(percentage));

        Table progressLine = new Table();
        progressLine.add(
            createSegmentedProgress(
                percentage,
                objectiveProgressColor(percentage),
                10
            )
        ).growX().height(7f).padRight(5f);
        progressLine.add(percentageLabel).right();

        card.add(top).growX().row();
        card.add(objectiveTitle).growX().left().height(39f).padTop(4f).row();
        card.add(detail).growX().left().padTop(1f).row();
        card.add(progressLine).growX().padTop(5f);
        return card;
    }

    private Table createSegmentedProgress(
        int percentage,
        Color activeColor,
        int segments
    ) {
        Table bar = new Table();
        int active = Math.round(segments * Math.max(0, Math.min(100, percentage)) / 100f);

        for (int i = 0; i < segments; i++) {
            Table segment = new Table();
            segment.background(
                StyleFactory.createRoundedPanel(
                    i < active ? activeColor : Color.valueOf("344039"),
                    i < active ? activeColor : Color.valueOf("344039")
                )
            );
            bar.add(segment).growX().uniformX().padRight(i < segments - 1 ? 2f : 0f);
        }
        return bar;
    }

    private Color boardStatusColor(int confidence) {
        if (confidence >= 80) return Color.valueOf("62DB8A");
        if (confidence >= 65) return Color.valueOf("9ED36A");
        if (confidence >= 45) return StyleFactory.SOFT_YELLOW;
        if (confidence >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private Color objectiveProgressColor(int percentage) {
        if (percentage >= 80) return ScreenUI.SUCCESS;
        if (percentage >= 45) return StyleFactory.SOFT_YELLOW;
        if (percentage >= 25) return ScreenUI.WARNING;
        return ScreenUI.DANGER;
    }

    private String boardStatusDescription(int confidence) {
        if (confidence >= 80) return "A franquia aprova plenamente a direção do projeto.";
        if (confidence >= 65) return "A diretoria está satisfeita com o trabalho atual.";
        if (confidence >= 45) return "O projeto está dentro do esperado, mas exige atenção.";
        if (confidence >= 25) return "Resultados e decisões recentes aumentaram a cobrança.";
        return "O cargo está ameaçado e uma reação é necessária.";
    }

    private String boardStatusLabel(int confidence) {
        if (confidence >= 80) return "EXCELENTE";
        if (confidence >= 65) return "ALTA";
        if (confidence >= 45) return "ESTÁVEL";
        if (confidence >= 25) return "SOB PRESSÃO";
        return "CRÍTICA";
    }

    // =========================================================
    // ROSTER
    // =========================================================

    private Table createRosterPanel() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        // =====================================================
        // HEADER DO PAINEL
        // =====================================================

        Table panelHeader =
            new Table();

        Label title =
            ScreenUI.createSectionTitle(
                game.skin,
                "ELENCO • ESTATÍSTICAS"
            );

        panelHeader
            .add(title)
            .left()
            .expandX();

        SelectBox<String> filter =
            ScreenUI.createSelectBox(
                game.skin
            );

        filter.setItems(
            "TODOS",
            "GOLEIROS",
            "DEFESA",
            "MEIO-CAMPO",
            "ATAQUE"
        );

        filter.setSelected(
            positionFilter
        );

        filter.addListener(
            new ChangeListener() {

                @Override
                public void changed(
                    ChangeEvent event,
                    com.badlogic.gdx.scenes.scene2d.Actor actor
                ) {

                    positionFilter =
                        filter.getSelected();

                    refreshUI();
                }
            }
        );

        panelHeader
            .add(filter)
            .width(235f)
            .height(52f)
            .right();

        panel
            .add(panelHeader)
            .growX()
            .padBottom(10f)
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

        scroll.setScrollingDisabled(
            false,
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
        // CABEÇALHO
        // =====================================================

        Table header =
            ScreenUI.createTableHeaderRow();

        addSortHeader(
            header,
            "POS",
            "POS",
            65f
        );

        addSortHeader(
            header,
            "JOGADOR",
            "NOME",
            230f
        );

        addSortHeader(
            header,
            "OVR",
            "OVR",
            62f
        );

        addSortHeader(
            header,
            "EFF",
            "EFF",
            62f
        );

        addSortHeader(
            header,
            "SALÁRIO",
            "SALARY",
            105f
        );

        addSortHeader(
            header,
            "MOR",
            "MORALE",
            62f
        );

        addSortHeader(
            header,
            "G",
            "GOALS",
            45f
        );

        addSortHeader(
            header,
            "A",
            "ASSISTS",
            45f
        );

        addSortHeader(
            header,
            "CA",
            "YELLOW",
            45f
        );

        addSortHeader(
            header,
            "CV",
            "RED",
            45f
        );

        addSortHeader(
            header,
            "FADIGA",
            "FATIGUE",
            80f
        );

        table
            .add(header)
            .growX()
            .height(48f)
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

        int visibleIndex =
            0;

        for (
            Player player :
            players
        ) {

            if (
                !matchesPositionFilter(
                    player
                )
            ) {

                continue;
            }

            Table row =
                createPlayerRow(
                    player,
                    visibleIndex++
                );

            table
                .add(row)
                .growX()
                .height(54f)
                .row();
        }

        return table;
    }

    private Table createPlayerRow(
        Player player,
        int rowIndex
    ) {

        Table row =
            ScreenUI.createRow(
                rowIndex
            );

        boolean available =
            player.canPlay();

        // =====================================================
        // POS
        // =====================================================

        Table badge =
            ScreenUI.createBadge(
                game.skin,
                player.getPosition(),
                StyleFactory.getPositionColor(
                    player.getPosition()
                )
            );

        row
            .add(badge)
            .width(65f)
            .height(30f);

        // =====================================================
        // NAME
        // =====================================================

        String name =
            player.getName();

        if (
            player.isInjured()
        ) {

            name +=
                " • LES " +
                    player.getInjuryDaysRemaining() +
                    "D";

        } else if (
            player.isSuspended()
        ) {

            name +=
                " • SUS " +
                    player.getSuspendedMatches() +
                    "J";
        }

        Label nameLabel =
            ScreenUI.createBoldValue(
                game.skin,
                name,
                available
                    ? Color.WHITE
                    : ScreenUI.DANGER,
                Align.left
            );

        nameLabel.setEllipsis(
            true
        );

        row
            .add(nameLabel)
            .width(230f)
            .padLeft(8f);

        // =====================================================
        // OVR
        // =====================================================

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        player.getOverall()
                    ),
                    StyleFactory.SOFT_YELLOW,
                    Align.center
                )
            )
            .width(62f);

        int effective =
            player.getEffectiveOverallForPosition(
                player.getPosition()
            );

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        effective
                    ),
                    effective <
                        player.getOverall()
                        ? ScreenUI.WARNING
                        : Color.WHITE,
                    Align.center
                )
            )
            .width(62f);

        // =====================================================
        // SALARY
        // =====================================================

        row
            .add(
                ScreenUI.createValueLabel(
                    game.skin,
                    formatSalary(
                        player.getMonthlySalary()
                    ),
                    StyleFactory.CREME_AGED,
                    Align.center
                )
            )
            .width(105f);

        // =====================================================
        // MORALE
        // =====================================================

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
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        morale
                    ),
                    moraleColor,
                    Align.center
                )
            )
            .width(62f);

        // =====================================================
        // STATS
        // =====================================================

        row
            .add(
                value(
                    player.getSeasonGoals()
                )
            )
            .width(45f);

        row
            .add(
                value(
                    player.getSeasonAssists()
                )
            )
            .width(45f);

        row
            .add(
                value(
                    player.getSeasonYellowCards()
                )
            )
            .width(45f);

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    String.valueOf(
                        player.getSeasonRedCards()
                    ),
                    player.getSeasonRedCards() > 0
                        ? ScreenUI.DANGER
                        : Color.WHITE,
                    Align.center
                )
            )
            .width(45f);

        // =====================================================
        // FATIGUE
        // =====================================================

        int fatigue =
            player.getFatigue();

        Color fatigueColor =
            fatigue >= 80
                ? ScreenUI.SUCCESS
                : fatigue >= 50
                ? ScreenUI.WARNING
                : ScreenUI.DANGER;

        row
            .add(
                ScreenUI.createBoldValue(
                    game.skin,
                    fatigue + "%",
                    fatigueColor,
                    Align.center
                )
            )
            .width(80f);

        row.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    showPlayerProfileDialog(player);
                }
            }
        );

        return row;
    }

    private void showPlayerProfileDialog(Player player) {
        int currentYear = game.league.getCurrentSeason();
        Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.getContentTable().background(
            StyleFactory.createMetallicBoard(1040, 760, Color.valueOf("141A16"))
        );
        dialog.getContentTable().pad(18f, 24f, 14f, 24f);

        Table content = dialog.getContentTable();
        content.add(ScreenUI.createSectionTitle(game.skin, "PERFIL DO JOGADOR"))
            .center().padBottom(5f).row();
        content.add(ScreenUI.createBoldValue(game.skin, player.getName(), Color.WHITE, Align.center))
            .center().padBottom(2f).row();
        content.add(ScreenUI.createSubtitle(game.skin,
            player.getNationality() + " • " + player.getAge() + " anos • " + String.format(Locale.US, "%.2fm", player.getHeight())))
            .center().padBottom(12f).row();

        Table overview = new Table();
        overview.add(ScreenUI.createStatusBox(game.skin, "POSIÇÃO", player.getPosition(), StyleFactory.getPositionColor(player.getPosition())))
            .width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(game.skin, "OVERALL", String.valueOf(player.getOverall()), StyleFactory.SOFT_YELLOW))
            .width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(game.skin, "POTENCIAL", getLetterGrade(player.getPotential()), getGradeColor(player.getPotential())))
            .width(220f).height(62f).padRight(7f);
        overview.add(ScreenUI.createStatusBox(game.skin, "NOTA MÉDIA", formatAverageRating(player), getAverageRatingColor(player.getSeasonAverageRating())))
            .width(220f).height(62f);
        content.add(overview).width(910f).padBottom(10f).row();

        Table columns = new Table();
        columns.add(createSeasonStatsPanel(player)).width(440f).height(265f).padRight(10f).top();
        columns.add(createProfileRightColumn(player, currentYear)).width(460f).height(265f).top();
        content.add(columns).width(910f).padBottom(10f).row();

        Table attributes = ScreenUI.createSubtlePanel();
        attributes.add(ScreenUI.createSectionTitle(game.skin, "ATRIBUTOS")).colspan(3).left().padBottom(5f).row();
        TechnicalAttributes values = player.getTechnicalAttributes();
        addAttributeRow(attributes, "ATAQUE", values.getAtaque(), "PASSE", values.getPasse(), "DRIBLE", values.getDrible());
        addAttributeRow(attributes, "FÍSICO", values.getFisico(), "DEFESA", values.getDefesa(), "GOLEIRO", values.getGoleiro());
        content.add(attributes).width(910f).height(112f).padBottom(6f).row();

        TextButton development = ScreenUI.createSecondaryButton(game.skin, "DESENVOLVIMENTO");
        development.getLabel().setFontScale(0.48f);
        development.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                new PlayerDevelopmentDialog(game.skin, player, club).show(stage);
            }
        });
        dialog.button(development, false);

        TextButton close = ScreenUI.createPrimaryButton(game.skin, "FECHAR");
        close.getLabel().setFontScale(0.55f);
        dialog.button(close, true);
        dialog.show(stage);
    }

    private Table createSeasonStatsPanel(Player player) {
        Table panel = ScreenUI.createSubtlePanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "ESTATÍSTICAS DA TEMPORADA")).colspan(2).left().padBottom(8f).row();
        addProfileDataRow(panel, "JOGOS", String.valueOf(player.getSeasonAppearances()), Color.WHITE);
        addProfileDataRow(panel, "MINUTOS", String.valueOf(player.getSeasonMinutes()), StyleFactory.SOFT_YELLOW);
        int growth = player.getSeasonOverallGrowth();
        addProfileDataRow(panel, "EVOLUÇÃO OVR", (growth > 0 ? "+" : "") + growth,
            growth >= 2 ? ScreenUI.SUCCESS : growth > 0 ? StyleFactory.SOFT_YELLOW : ScreenUI.MUTED_TEXT);
        addProfileDataRow(panel, "GOLS", String.valueOf(player.getSeasonGoals()), StyleFactory.SOFT_YELLOW);
        addProfileDataRow(panel, "ASSISTÊNCIAS", String.valueOf(player.getSeasonAssists()), ScreenUI.SUCCESS);
        addProfileDataRow(panel, "CLEAN SHEETS", String.valueOf(player.getSeasonCleanSheets()), ScreenUI.SUCCESS);
        addProfileDataRow(panel, "CARTÕES", player.getSeasonYellowCards() + " A  •  " + player.getSeasonRedCards() + " V", player.getSeasonRedCards() > 0 ? ScreenUI.DANGER : StyleFactory.SOFT_YELLOW);
        addProfileDataRow(panel, "NOTA MÉDIA", formatAverageRating(player), getAverageRatingColor(player.getSeasonAverageRating()));
        return panel;
    }

    private Table createProfileRightColumn(Player player, int currentYear) {
        Table column = new Table();
        Table contract = ScreenUI.createSubtlePanel();
        contract.add(ScreenUI.createSectionTitle(game.skin, "CONTRATO")).colspan(2).left().padBottom(7f).row();
        addProfileDataRow(contract, "SALÁRIO ANUAL", formatAnnualSalary(player.getAnnualSalary()), StyleFactory.SOFT_YELLOW);
        addProfileDataRow(contract, "TÉRMINO", String.valueOf(player.getContractEndYear()), Color.WHITE);
        addProfileDataRow(contract, "RESTANTE", formatContractRemaining(player.getRemainingContractYears(currentYear)), getContractColor(player.getRemainingContractYears(currentYear)));
        addProfileDataRow(contract, "NEGOCIAÇÃO", player.canNegotiateContract(currentYear) ? "DISPONÍVEL" : "BLOQUEADA", player.canNegotiateContract(currentYear) ? ScreenUI.SUCCESS : ScreenUI.WARNING);
        column.add(contract).growX().height(145f).padBottom(8f).row();

        Table condition = ScreenUI.createSubtlePanel();
        condition.add(ScreenUI.createSectionTitle(game.skin, "CONDIÇÃO ATUAL")).colspan(2).left().padBottom(7f).row();
        addProfileDataRow(condition, "MORAL", player.getMorale() + "/100", player.getMorale() >= 75 ? ScreenUI.SUCCESS : player.getMorale() >= 45 ? ScreenUI.WARNING : ScreenUI.DANGER);
        addProfileDataRow(condition, "FADIGA", player.getFatigue() + "%", player.getFatigue() >= 80 ? ScreenUI.SUCCESS : player.getFatigue() >= 50 ? ScreenUI.WARNING : ScreenUI.DANGER);
        addProfileDataRow(condition, "STATUS", getPlayerStatus(player), player.canPlay() ? ScreenUI.SUCCESS : ScreenUI.DANGER);
        column.add(condition).growX().height(112f);
        return column;
    }

    private void addAttributeRow(Table table, String firstName, int firstValue, String secondName, int secondValue, String thirdName, int thirdValue) {
        table.add(createAttributeValue(firstName, firstValue)).growX().uniformX().padRight(7f);
        table.add(createAttributeValue(secondName, secondValue)).growX().uniformX().padRight(7f);
        table.add(createAttributeValue(thirdName, thirdValue)).growX().uniformX().row();
    }

    private Table createAttributeValue(String title, int value) {
        Table item = new Table();
        item.add(ScreenUI.createSubtitle(game.skin, title)).left().expandX();
        item.add(ScreenUI.createBoldValue(game.skin, value + " • " + getLetterGrade(value), getGradeColor(value), Align.right));
        return item;
    }

    private void addProfileDataRow(Table table, String label, String value, Color color) {
        table.add(ScreenUI.createSubtitle(game.skin, label)).left().expandX().padBottom(5f);
        table.add(ScreenUI.createBoldValue(game.skin, value, color, Align.right)).right().padBottom(5f).row();
    }

    private String formatAverageRating(Player player) {
        return player.getSeasonRatingMatches() == 0 ? "—" : String.format(Locale.US, "%.1f", player.getSeasonAverageRating());
    }

    private String formatContractRemaining(int years) {
        if (years == 0) return "EXPIRADO";
        return years + " ano" + (years > 1 ? "s" : "");
    }

    private String formatAnnualSalary(long salary) {
        return String.format(Locale.US, "WFL$ %.2fM", salary / 1_000_000.0);
    }

    private String getPlayerStatus(Player player) {
        if (player.isInjured()) return "LESIONADO • " + player.getInjuryDaysRemaining() + " dia" + (player.getInjuryDaysRemaining() == 1 ? "" : "s");
        if (player.isSuspended()) return "SUSPENSO • " + player.getSuspendedMatches() + "J";
        return "DISPONÍVEL";
    }

    private Color getContractColor(int years) {
        return years == 0 ? ScreenUI.DANGER : years == 1 ? ScreenUI.WARNING : ScreenUI.SUCCESS;
    }

    private Color getAverageRatingColor(double rating) {
        return rating >= 7.0 ? ScreenUI.SUCCESS : rating >= 6.0 ? StyleFactory.SOFT_YELLOW : ScreenUI.DANGER;
    }

    private Color getGradeColor(int value) {
        return value >= 85 ? ScreenUI.SUCCESS : value >= 70 ? StyleFactory.SOFT_YELLOW : value >= 60 ? ScreenUI.WARNING : ScreenUI.DANGER;
    }

    private String getLetterGrade(int value) {
        if (value >= 90) return "A+";
        if (value >= 85) return "A";
        if (value >= 80) return "A-";
        if (value >= 77) return "B+";
        if (value >= 73) return "B";
        if (value >= 70) return "B-";
        if (value >= 67) return "C+";
        if (value >= 63) return "C";
        if (value >= 60) return "C-";
        if (value >= 57) return "D+";
        if (value >= 53) return "D";
        if (value >= 50) return "D-";
        return "F";
    }

    // =========================================================
    // FIELD
    // =========================================================

    private Table createFieldPreview() {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        Formation formation =
            club.getFormation();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    game.skin,
                    formation != null
                        ? "ONZE INICIAL • " +
                        formation.getName()
                        : "ONZE INICIAL"
                )
            )
            .center()
            .padBottom(10f)
            .row();

        int fieldWidth =
            Math.min(
                410,
                Math.max(
                    300,
                    (int) (
                        Gdx.graphics
                            .getWidth() *
                            0.27f
                    )
                )
            );

        int fieldHeight =
            Math.min(
                580,
                Math.max(
                    420,
                    (int) (
                        fieldWidth *
                            1.42f
                    )
                )
            );

        Stack field =
            new Stack();

        field.setSize(
            fieldWidth,
            fieldHeight
        );

        Image fieldImage =
            new Image(
                fieldTexture
            );

        fieldImage.setFillParent(
            true
        );

        field.add(
            fieldImage
        );

        if (
            formation == null
        ) {

            Table warning =
                new Table();

            Label label =
                new Label(
                    "Nenhuma formação selecionada.\nAbra a tela Táticas.",
                    game.skin,
                    "font-bold"
                );

            label.setAlignment(
                Align.center
            );

            label.setColor(
                StyleFactory.SOFT_YELLOW
            );

            warning
                .add(label)
                .center();

            field.add(
                warning
            );

        } else {

            Group players =
                new Group();

            players.setSize(
                fieldWidth,
                fieldHeight
            );

            List<String> slots =
                formation
                    .getPositionSlots();

            for (
                int i = 0;
                i <
                    Math.min(
                        11,
                        slots.size()
                    );
                i++
            ) {

                Player player =
                    club.getTacticsMap()
                        .get(i);

                players.addActor(
                    createFieldPlayerCard(
                        player,
                        slots.get(i),
                        i,
                        slots,
                        fieldWidth,
                        fieldHeight
                    )
                );
            }

            field.add(
                players
            );
        }

        panel
            .add(field)
            .width(fieldWidth)
            .height(fieldHeight)
            .center();

        return panel;
    }

    private Table createFieldPlayerCard(
        Player player,
        String position,
        int slot,
        List<String> slots,
        int width,
        int height
    ) {

        Table card =
            new Table();

        card.background(
            StyleFactory.createRoundedPanel(
                new Color(
                    0.025f,
                    0.055f,
                    0.045f,
                    0.95f
                ),
                StyleFactory.GOLD
            )
        );

        card.setSize(
            Math.min(
                108f,
                width *
                    0.26f
            ),
            48f
        );

        Label name =
            new Label(
                player != null
                    ? ScreenUI.shorten(
                    player.getName(),
                    12
                )
                    : "VAZIO",
                game.skin,
                "font-bold"
            );

        name.setFontScale(
            0.50f
        );

        name.setAlignment(
            Align.center
        );

        card
            .add(name)
            .width(
                card.getWidth() -
                    8f
            )
            .center()
            .row();

        Label pos =
            new Label(
                position +
                    (
                        player != null
                            ? "  " +
                            player.getEffectiveOverallForPosition(
                                position
                            )
                            : ""
                    ),
                game.skin,
                "font-bold"
            );

        pos.setFontScale(
            0.48f
        );

        pos.setColor(
            StyleFactory.SOFT_YELLOW
        );

        card
            .add(pos)
            .center();

        int line =
            positionWeight(
                position
            );

        int indexOnLine =
            0;

        for (
            int i = 0;
            i < slot;
            i++
        ) {

            if (
                positionWeight(
                    slots.get(i)
                ) ==
                    line
            ) {

                indexOnLine++;
            }
        }

        int countOnLine =
            0;

        for (
            String slotPosition :
            slots
        ) {

            if (
                positionWeight(
                    slotPosition
                ) ==
                    line
            ) {

                countOnLine++;
            }
        }

        float[] y = {
            0.08f,
            0.30f,
            0.55f,
            0.80f
        };

        float x =
            (
                indexOnLine +
                    1f
            ) /
                (
                    countOnLine +
                        1f
                );

        int yIndex =
            Math.max(
                0,
                Math.min(
                    y.length - 1,
                    line - 1
                )
            );

        card.setPosition(
            x *
                width -
                card.getWidth() /
                    2f,
            y[yIndex] *
                height -
                card.getHeight() /
                    2f
        );

        return card;
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
                label + arrow,
                game.skin,
                "toggle"
            );

        button
            .getLabel()
            .setFontScale(
                0.52f
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
            .height(38f);
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

            case "FATIGUE":

                comparator =
                    Comparator.comparingInt(
                        Player::getFatigue
                    );

                break;

            case "MORALE":

                comparator =
                    Comparator.comparingInt(
                        Player::getMorale
                    );

                break;

            case "GOALS":

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

            case "YELLOW":

                comparator =
                    Comparator.comparingInt(
                        Player::getSeasonYellowCards
                    );

                break;

            case "RED":

                comparator =
                    Comparator.comparingInt(
                        Player::getSeasonRedCards
                    );

                break;

            case "SALARY":

                comparator =
                    Comparator.comparingLong(
                        Player::getMonthlySalary
                    );

                break;

            case "EFF":

                comparator =
                    Comparator.comparingInt(
                        Player::getEffectiveOverall
                    );

                break;

            default:

                comparator =
                    Comparator.comparingInt(
                        Player::getOverall
                    );
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
    // FILTER
    // =========================================================

    private boolean matchesPositionFilter(
        Player player
    ) {

        if (
            "TODOS".equals(
                positionFilter
            )
        ) {

            return true;
        }

        int weight =
            player.getPositionWeight();

        switch (
            positionFilter
        ) {

            case "GOLEIROS":
                return weight == 1;

            case "DEFESA":
                return weight == 2;

            case "MEIO-CAMPO":
                return weight == 3;

            case "ATAQUE":
                return weight == 4;

            default:
                return true;
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private int positionWeight(
        String position
    ) {

        if (
            position == null
        ) {
            return 4;
        }

        if (
            position.equals(
                "GK"
            )
        ) {
            return 1;
        }

        if (
            position.matches(
                "CB|RB|LB|RWB|LWB"
            )
        ) {
            return 2;
        }

        if (
            position.matches(
                "CDM|CM|CAM|RM|LM"
            )
        ) {
            return 3;
        }

        return 4;
    }

    private Label value(
        int value
    ) {

        return ScreenUI.createValueLabel(
            game.skin,
            String.valueOf(
                value
            ),
            Color.WHITE,
            Align.center
        );
    }

    private String formatSalary(
        long salary
    ) {

        if (
            salary >=
                1_000_000
        ) {

            return String.format(
                "WFL$ %.1fM",
                salary /
                    1_000_000f
            );
        }

        if (
            salary >=
                1_000
        ) {

            return String.format(
                "WFL$ %.0fK",
                salary /
                    1_000f
            );
        }

        return "WFL$ " +
            salary;
    }

    private Texture loadLogo(
        Club target
    ) {

        try {

            if (
                target.getLogoPath() != null &&
                    Gdx.files
                        .internal(
                            target.getLogoPath()
                        )
                        .exists()
            ) {

                return new Texture(
                    Gdx.files.internal(
                        target.getLogoPath()
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

        pranchetaTexture.dispose();
        fieldTexture.dispose();
        boardStarTexture.dispose();

        if (
            clubLogoTexture != null
        ) {

            clubLogoTexture.dispose();
        }
    }
}
