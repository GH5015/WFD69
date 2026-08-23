package io.github.some_example_name.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.screens.RemoveScoutTargetDialog;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

public class PlayerReportDialog extends Dialog {

    private final Skin skin;

    private final ScoutTarget target;

    private final DraftScoutManager scoutManager;

    private final Runnable onRemoveCallback;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PlayerReportDialog(
        Skin skin,
        ScoutTarget target,
        DraftScoutManager scoutManager,
        Runnable onRemoveCallback
    ) {

        super(
            "",
            skin
        );

        this.skin =
            skin;

        this.target =
            target;

        this.scoutManager =
            scoutManager;

        this.onRemoveCallback =
            onRemoveCallback;

        setModal(
            true
        );

        setMovable(
            false
        );

        buildUI();
    }

    // =========================================================
    // UI
    // =========================================================

    private void buildUI() {

        getContentTable()
            .clear();

        getButtonTable()
            .clear();

        Player player =
            target.getPlayer();

        Table root =
            getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                720,
                650,
                Color.valueOf(
                    "151A17"
                )
            )
        );

        root.pad(
            12f
        );

        // =====================================================
        // HEADER
        // =====================================================

        root
            .add(
                createHeader(
                    player
                )
            )
            .growX()
            .height(95f)
            .padBottom(8f)
            .row();

        // =====================================================
        // KNOWLEDGE
        // =====================================================

        root
            .add(
                createKnowledgePanel()
            )
            .growX()
            .height(78f)
            .padBottom(8f)
            .row();

        // =====================================================
        // RATINGS
        // =====================================================

        Table body =
            new Table();

        body
            .add(
                createRatingsPanel(
                    player
                )
            )
            .grow()
            .uniformX()
            .padRight(8f);

        body
            .add(
                createProjectionPanel(
                    player
                )
            )
            .grow()
            .uniformX();

        root
            .add(body)
            .width(670f)
            .height(335f)
            .row();

        // =====================================================
        // ACTIONS
        // =====================================================

        createButtons();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private Table createHeader(
        Player player
    ) {

        Table panel =
            ScreenUI.createPanel();

        Table identity =
            new Table();

        Label name =
            new Label(
                player.getName()
                    .toUpperCase(),
                skin,
                "font-title"
            );

        name.setFontScale(
            0.76f
        );

        name.setColor(
            StyleFactory.GOLD
        );

        identity
            .add(name)
            .left()
            .row();

        String infoText =
            (
                player.getNationality() != null
                    ? player.getNationality()
                    : "N/D"
            )
                +
                "  •  " +
                player.getAge() +
                " ANOS  •  " +
                player
                    .getPrimaryPosition()
                    .name()
                +
                "  •  " +
                String.format(
                    "%.2fm",
                    player.getHeight()
                );

        Label info =
            new Label(
                infoText,
                skin,
                "font-bold"
            );

        info.setFontScale(
            0.50f
        );

        info.setColor(
            ScreenUI.MUTED_TEXT
        );

        identity
            .add(info)
            .left()
            .padTop(4f);

        panel
            .add(identity)
            .left()
            .expandX();

        panel
            .add(
                ScreenUI.createBadge(
                    skin,
                    player
                        .getPrimaryPosition()
                        .name(),
                    StyleFactory
                        .getPositionColor(
                            player
                                .getPrimaryPosition()
                                .name()
                        )
                )
            )
            .height(30f);

        return panel;
    }

    // =========================================================
    // KNOWLEDGE
    // =========================================================

    private Table createKnowledgePanel() {

        Table panel =
            ScreenUI.createPanel();

        double knowledge =
            target.getKnowledgePercentage();

        Color knowledgeColor =
            target.isFullyScouted()
                ? ScreenUI.SUCCESS
                : StyleFactory.GOLD;

        Table title =
            new Table();

        title
            .add(
                ScreenUI.createSectionTitle(
                    skin,
                    "CONHECIMENTO DO SCOUT"
                )
            )
            .left()
            .expandX();

        Label percentage =
            new Label(
                String.format(
                    "%.1f%%",
                    knowledge
                ),
                skin,
                "font-bold"
            );

        percentage.setFontScale(
            0.64f
        );

        percentage.setColor(
            knowledgeColor
        );

        title
            .add(percentage)
            .right();

        panel
            .add(title)
            .growX()
            .padBottom(7f)
            .row();

        Table progress =
            new Table();

        progress
            .add(
                ScreenUI.createBlockProgress(
                    skin,
                    knowledge,
                    20,
                    knowledgeColor
                )
            )
            .center();

        panel
            .add(progress)
            .growX();

        return panel;
    }

    // =========================================================
    // RATINGS
    // =========================================================

    private Table createRatingsPanel(
        Player player
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    skin,
                    "RELATÓRIO TÉCNICO"
                )
            )
            .left()
            .colspan(2)
            .padBottom(10f)
            .row();

        // =====================================================
        // OVR/POT
        // =====================================================

        addRatingRow(
            panel,
            "OVERALL ESTIMADO",
            target.getDisplayOverall(),
            true
        );

        addRatingRow(
            panel,
            "POTENCIAL ESTIMADO",
            target.getDisplayPotential(),
            true
        );

        panel
            .add(
                ScreenUI.createDivider()
            )
            .growX()
            .height(1f)
            .colspan(2)
            .padTop(7f)
            .padBottom(9f)
            .row();

        // =====================================================
        // ATTRIBUTES
        // =====================================================

        addRatingRow(
            panel,
            "ATAQUE",
            target.getAttributeDisplay(
                player.getAttackStat()
            ),
            false
        );

        addRatingRow(
            panel,
            "PASSE",
            target.getAttributeDisplay(
                player.getPassStat()
            ),
            false
        );

        addRatingRow(
            panel,
            "DRIBLE",
            target.getAttributeDisplay(
                player.getDribbleStat()
            ),
            false
        );

        addRatingRow(
            panel,
            "FÍSICO",
            target.getAttributeDisplay(
                player.getPhysicalStat()
            ),
            false
        );

        addRatingRow(
            panel,
            "DEFESA",
            target.getAttributeDisplay(
                player.getDefenseStat()
            ),
            false
        );

        return panel;
    }

    private void addRatingRow(
        Table table,
        String title,
        String value,
        boolean important
    ) {

        Label titleLabel =
            important
                ? ScreenUI.createBoldValue(
                skin,
                title,
                Color.WHITE,
                Align.left
            )
                : ScreenUI.createSubtitle(
                skin,
                title
            );

        table
            .add(titleLabel)
            .left()
            .expandX()
            .padBottom(8f);

        Label valueLabel =
            new Label(
                value != null
                    ? value
                    : "?",
                skin,
                "font-bold"
            );

        valueLabel.setFontScale(
            important
                ? 0.68f
                : 0.61f
        );

        valueLabel.setColor(
            getGradeColor(
                value
            )
        );

        valueLabel.setAlignment(
            Align.right
        );

        table
            .add(valueLabel)
            .right()
            .width(90f)
            .padBottom(8f)
            .row();
    }

    // =========================================================
    // PROJECTION
    // =========================================================

    private Table createProjectionPanel(
        Player player
    ) {

        Table panel =
            ScreenUI.createPanel();

        panel.top();

        panel
            .add(
                ScreenUI.createSectionTitle(
                    skin,
                    "PROJEÇÃO DO SCOUT"
                )
            )
            .left()
            .padBottom(12f)
            .row();

        Label projection =
            new Label(
                getScoutProjection(
                    player
                ),
                skin
            );

        projection.setWrap(
            true
        );

        projection.setAlignment(
            Align.center
        );

        projection.setFontScale(
            0.61f
        );

        projection.setColor(
            StyleFactory.CREME_AGED
        );

        panel
            .add(projection)
            .width(285f)
            .center()
            .padBottom(16f)
            .row();

        panel
            .add(
                ScreenUI.createDivider()
            )
            .growX()
            .height(1f)
            .padBottom(12f)
            .row();

        // =====================================================
        // STATUS
        // =====================================================

        String status;

        Color statusColor;

        if (
            target.isFullyScouted()
        ) {

            status =
                "RELATÓRIO COMPLETO";

            statusColor =
                ScreenUI.SUCCESS;

        } else if (
            target.getKnowledgePercentage() >=
                75
        ) {

            status =
                "ALTA CONFIANÇA";

            statusColor =
                StyleFactory.SOFT_YELLOW;

        } else if (
            target.getKnowledgePercentage() >=
                40
        ) {

            status =
                "EM AVALIAÇÃO";

            statusColor =
                ScreenUI.WARNING;

        } else {

            status =
                "DADOS LIMITADOS";

            statusColor =
                ScreenUI.MUTED_TEXT;
        }

        panel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "STATUS",
                    status,
                    statusColor
                )
            )
            .growX()
            .height(44f)
            .padBottom(8f)
            .row();

        String nextUpdate =
            target.isFullyScouted()
                ? "Concluído"
                : "Próximo avanço ao passar o dia";

        panel
            .add(
                ScreenUI.createStatusBox(
                    skin,
                    "PRÓXIMA ATUALIZAÇÃO",
                    nextUpdate,
                    target.isFullyScouted()
                        ? ScreenUI.SUCCESS
                        : StyleFactory.CREME_AGED
                )
            )
            .growX()
            .height(44f);

        return panel;
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void createButtons() {

        Table buttons =
            getButtonTable();

        buttons.pad(
            8f,
            8f,
            10f,
            8f
        );

        boolean isActiveTarget = scoutManager.isActiveTarget(target);

        ImageTextButton remove =
            IconTextButton.create(
                isActiveTarget ? "REMOVER DO SCOUTING" : "RELATÓRIO CONCLUÍDO",
                skin,
                "Icons8/icons8-remover-50.png"
            );

        remove
            .getLabel()
            .setFontScale(
                0.52f
            );

        remove
            .getLabel()
            .setColor(
                isActiveTarget
                    ? ScreenUI.DANGER
                    : ScreenUI.SUCCESS
            );

        remove.setDisabled(!isActiveTarget);

        remove.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    Stage stage =
                        getStage();

                    hide();

                    if (
                        stage != null
                    ) {

                        RemoveScoutTargetDialog confirm =
                            new RemoveScoutTargetDialog(
                                skin,
                                target,
                                scoutManager,
                                onRemoveCallback
                            );

                        confirm.show(
                            stage
                        );
                    }
                }
            }
        );

        buttons
            .add(remove)
            .width(220f)
            .height(43f)
            .padRight(8f);

        ImageTextButton close =
            IconTextButton.create(
                "FECHAR",
                skin,
                "Icons8/icons8-fechar-janela-50.png"
            );

        close
            .getLabel()
            .setFontScale(
                0.54f
            );

        close.addListener(
            new ClickListener() {

                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {

                    hide();
                }
            }
        );

        buttons
            .add(close)
            .width(135f)
            .height(43f);
    }

    // =========================================================
    // PROJECTION LOGIC
    // =========================================================

    private String getScoutProjection(
        Player player
    ) {

        double knowledge =
            target
                .getKnowledgePercentage();

        if (
            knowledge <
                30.0
        ) {

            return "Ainda há poucos dados para estimar com segurança o teto deste jogador.";
        }

        /*
         * Mantém a lógica já existente.
         *
         * A projeção textual só aparece depois que
         * existe conhecimento mínimo suficiente.
         */

        int potential =
            player.getPotential();

        if (
            potential >=
                88
        ) {

            return "Projeção de astro de classe mundial. Pode se tornar uma peça central da franquia.";

        } else if (
            potential >=
                82
        ) {

            return "Projeção de titular de alto nível, com capacidade para ter papel importante em uma equipe competitiva.";

        } else if (
            potential >=
                75
        ) {

            return "Projeção de jogador sólido de rotação, com margem para contribuir regularmente.";

        } else {

            return "Projeção mais limitada. O valor tende a estar ligado a profundidade de elenco e funções específicas.";
        }
    }

    // =========================================================
    // GRADE COLOR
    // =========================================================

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

        char first =
            grade.charAt(
                0
            );

        switch (
            first
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
                /*
                 * Quando o scouting revela um
                 * intervalo ou valor numérico.
                 */
                return StyleFactory.CREME_AGED;
        }
    }
}
