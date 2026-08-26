package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Painel apresentado uma única vez depois que todos os jogos de uma rodada
 * regular forem concluídos. A League deixa a rodada pendente e este diálogo
 * só a consome quando o usuário usa novamente o botão "Avançar dia".
 */
public final class RoundSummaryDialog extends Dialog {

    private final Main game;
    private final Club playerClub;
    private final List<Match> roundMatches;
    private final int roundNumber;

    private RoundSummaryDialog(
        Main game,
        Club playerClub,
        java.util.Date roundDate
    ) {
        super("", game.skin);

        this.game = game;
        this.playerClub = playerClub;
        this.roundMatches = new ArrayList<>(
            game.league.getRegularMatchesOnDate(roundDate)
        );
        this.roundNumber = game.league.getRoundNumberForDate(roundDate);

        setModal(true);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(true);

        getContentTable().clear();
        buildLayout();
    }

    /**
     * Exibe e consome a rodada pendente. Retorna false se não houver nada para
     * mostrar; assim o fluxo normal de avanço continua sem efeitos colaterais.
     */
    public static boolean showPending(
        Stage stage,
        Main game,
        Club playerClub
    ) {
        if (
            stage == null ||
            game == null ||
            game.league == null ||
            !game.league.hasPendingRoundSummary()
        ) {
            return false;
        }

        java.util.Date roundDate =
            game.league.consumePendingRoundSummaryDate();

        if (roundDate == null) {
            return false;
        }

        new RoundSummaryDialog(
            game,
            playerClub,
            roundDate
        ).show(stage);

        return true;
    }

    private void buildLayout() {
        Table root = getContentTable();

        root.background(
            StyleFactory.createMetallicBoard(
                1280,
                790,
                Color.valueOf("101713")
            )
        );

        root.pad(16f, 20f, 16f, 20f);

        root.add(createHeader())
            .growX()
            .height(86f)
            .padBottom(9f)
            .row();

        root.add(createClubResultBanner())
            .growX()
            .height(68f)
            .padBottom(10f)
            .row();

        Table body = new Table();

        Table leftColumn = new Table();
        leftColumn.top();
        leftColumn.add(createResultsPanel())
            .growX()
            .height(315f)
            .padBottom(10f)
            .row();
        leftColumn.add(createStandingsPanel())
            .growX()
            .height(175f);

        Table rightColumn = new Table();
        rightColumn.top();
        rightColumn.add(createHighlightsPanel())
            .growX()
            .height(325f)
            .padBottom(10f)
            .row();
        rightColumn.add(createNextEventPanel())
            .growX()
            .height(165f);

        body.add(leftColumn)
            .width(710f)
            .growY()
            .padRight(10f);

        body.add(rightColumn)
            .width(490f)
            .growY();

        root.add(body)
            .grow()
            .height(500f)
            .padBottom(11f)
            .row();

        addActions();
    }

    private Table createHeader() {
        Table header = ScreenUI.createPanel();
        header.pad(9f, 16f, 9f, 16f);

        Table titleBlock = new Table();

        Label title = new Label(
            "★  RESUMO DA RODADA " + roundNumber + "  ★",
            game.skin,
            "font-title"
        );
        title.setFontScale(0.92f);
        title.setColor(StyleFactory.PLAYOFF_GOLD);
        title.setAlignment(Align.left);

        Label subtitle = new Label(
            "Todos os jogos foram concluídos",
            game.skin
        );
        subtitle.setFontScale(0.52f);
        subtitle.setColor(ScreenUI.MUTED_TEXT);

        titleBlock.add(title)
            .left()
            .row();
        titleBlock.add(subtitle)
            .left()
            .padTop(2f);

        header.add(titleBlock)
            .expandX()
            .left();

        Label season = new Label(
            "WFL  •  TEMPORADA " + game.league.getCurrentSeason(),
            game.skin,
            "font-bold"
        );
        season.setFontScale(0.49f);
        season.setColor(StyleFactory.SOFT_YELLOW);
        season.setAlignment(Align.right);

        header.add(season)
            .right()
            .padRight(10f);

        TextButton close = ScreenUI.createInteractiveButton(
            "X",
            game.skin
        );
        close.getLabel().setFontScale(0.56f);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {
                hide();
            }
        });

        header.add(close)
            .size(42f, 42f)
            .right();

        return header;
    }

    private Table createClubResultBanner() {
        Match clubMatch = findPlayerClubMatch();

        String message = "Rodada concluída.";
        Color accent = StyleFactory.SOFT_YELLOW;

        if (clubMatch != null) {
            boolean home = clubMatch.getHomeTeam() == playerClub;
            int scored = home
                ? clubMatch.getHomeGoals()
                : clubMatch.getAwayGoals();
            int conceded = home
                ? clubMatch.getAwayGoals()
                : clubMatch.getHomeGoals();

            if (scored > conceded) {
                message = playerClub.getName() + " venceu sua partida.";
                accent = ScreenUI.SUCCESS;
            } else if (scored < conceded) {
                message = playerClub.getName() + " foi superado nesta rodada.";
                accent = ScreenUI.DANGER;
            } else {
                message = playerClub.getName() + " empatou sua partida.";
                accent = ScreenUI.WARNING;
            }
        }

        Table banner = new Table();
        banner.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("0F2B1C"),
                accent
            )
        );
        banner.pad(8f, 18f, 8f, 18f);

        Label marker = new Label(
            "RODADA CONCLUÍDA",
            game.skin,
            "font-bold"
        );
        marker.setFontScale(0.45f);
        marker.setColor(StyleFactory.SOFT_YELLOW);

        Label text = new Label(
            message,
            game.skin,
            "font-bold"
        );
        text.setFontScale(0.69f);
        text.setColor(Color.WHITE);

        banner.add(marker)
            .width(165f)
            .left()
            .padRight(10f);

        banner.add(text)
            .expandX()
            .left();

        return banner;
    }

    private Table createResultsPanel() {
        Table panel = ScreenUI.createTablePanel();
        panel.top();
        panel.pad(10f, 13f, 8f, 13f);

        panel.add(sectionTitle("RESULTADOS DA RODADA"))
            .growX()
            .left()
            .padBottom(7f)
            .row();

        if (roundMatches.isEmpty()) {
            panel.add(ScreenUI.createSubtitle(
                game.skin,
                "Nenhuma partida encontrada para esta rodada."
            )).left();
            return panel;
        }

        for (Match match : roundMatches) {
            boolean playerMatch = involvesPlayerClub(match);
            Table line = new Table();

            if (playerMatch) {
                line.background(
                    StyleFactory.createRoundedPanel(
                        Color.valueOf("183A25"),
                        StyleFactory.DARK_GOLD
                    )
                );
            }

            Label home = valueLabel(
                ScreenUI.shorten(match.getHomeTeam().getName(), 25),
                playerMatch
                    ? StyleFactory.SOFT_YELLOW
                    : StyleFactory.CREME_AGED,
                Align.right,
                0.47f
            );

            Label score = valueLabel(
                match.getHomeGoals() + "  x  " + match.getAwayGoals(),
                playerMatch
                    ? StyleFactory.PLAYOFF_GOLD
                    : Color.WHITE,
                Align.center,
                0.52f
            );

            Label away = valueLabel(
                ScreenUI.shorten(match.getAwayTeam().getName(), 25),
                playerMatch
                    ? StyleFactory.SOFT_YELLOW
                    : StyleFactory.CREME_AGED,
                Align.left,
                0.47f
            );

            line.add(home)
                .width(245f)
                .right()
                .pad(3f, 6f, 3f, 2f);

            line.add(score)
                .width(94f)
                .center();

            line.add(away)
                .width(245f)
                .left()
                .pad(3f, 2f, 3f, 6f);

            panel.add(line)
                .growX()
                .height(25f)
                .padBottom(2f)
                .row();
        }

        return panel;
    }

    private Table createStandingsPanel() {
        Table panel = ScreenUI.createTablePanel();
        panel.top();
        panel.pad(10f, 13f, 8f, 13f);

        Table heading = new Table();
        heading.add(sectionTitle("CLASSIFICAÇÃO PARCIAL"))
            .expandX()
            .left();

        Label points = valueLabel(
            "PTS",
            ScreenUI.MUTED_TEXT,
            Align.right,
            0.43f
        );
        heading.add(points).right();

        panel.add(heading)
            .growX()
            .padBottom(6f)
            .row();

        List<StandingsRow> standings =
            game.league.getFullStandings(null);

        int limit = Math.min(5, standings.size());
        for (int index = 0; index < limit; index++) {
            StandingsRow row = standings.get(index);
            boolean playerRow = row.club == playerClub;

            Table line = new Table();
            if (playerRow) {
                line.background(
                    StyleFactory.createRoundedPanel(
                        Color.valueOf("183A25"),
                        StyleFactory.DARK_GOLD
                    )
                );
            }

            line.add(valueLabel(
                String.valueOf(index + 1),
                playerRow
                    ? StyleFactory.PLAYOFF_GOLD
                    : ScreenUI.MUTED_TEXT,
                Align.center,
                0.45f
            )).width(32f);

            line.add(valueLabel(
                ScreenUI.shorten(row.club.getName(), 31),
                playerRow
                    ? StyleFactory.SOFT_YELLOW
                    : StyleFactory.CREME_AGED,
                Align.left,
                0.46f
            )).expandX().left();

            line.add(valueLabel(
                String.valueOf(row.points),
                playerRow
                    ? StyleFactory.PLAYOFF_GOLD
                    : Color.WHITE,
                Align.right,
                0.48f
            )).width(44f).right();

            panel.add(line)
                .growX()
                .height(23f)
                .padBottom(1f)
                .row();
        }

        return panel;
    }

    private Table createHighlightsPanel() {
        Table panel = ScreenUI.createTablePanel();
        panel.top();
        panel.pad(11f, 13f, 10f, 13f);

        panel.add(sectionTitle("DESTAQUES DA RODADA"))
            .left()
            .padBottom(8f)
            .row();

        Match bestMatch = getBestMatch();
        Player topScorer = getTopScorer();
        Match surprise = getBiggestSurprise();

        panel.add(createHighlightCard(
            "MELHOR JOGO",
            bestMatch != null
                ? getMatchResult(bestMatch)
                : "Sem partidas concluídas",
            bestMatch != null
                ? (bestMatch.getHomeGoals() + bestMatch.getAwayGoals())
                    + " gols no total"
                : "A rodada ainda não tem dados."
        )).growX().height(82f).padBottom(7f).row();

        String scorerText = topScorer != null
            ? topScorer.getName()
            : "Nenhum artilheiro";
        String scorerDetail = topScorer != null
            ? getRoundGoalCount(topScorer) + " gol(s) na rodada"
            : "Nenhuma bola na rede nesta rodada.";

        panel.add(createHighlightCard(
            "CRAQUE DA RODADA",
            scorerText,
            scorerDetail
        )).growX().height(82f).padBottom(7f).row();

        String surpriseText = surprise != null
            ? getMatchResult(surprise)
            : "Sem zebra nesta rodada";
        String surpriseDetail = surprise != null
            ? getSurpriseDescription(surprise)
            : "Os favoritos confirmaram seus resultados.";

        panel.add(createHighlightCard(
            "MAIOR SURPRESA",
            surpriseText,
            surpriseDetail
        )).growX().height(82f);

        return panel;
    }

    private Table createHighlightCard(
        String title,
        String mainText,
        String detail
    ) {
        Table card = ScreenUI.createSubtlePanel();
        card.pad(8f, 12f, 8f, 12f);

        Label titleLabel = valueLabel(
            title,
            StyleFactory.GOLD,
            Align.left,
            0.45f
        );

        Label mainLabel = valueLabel(
            mainText,
            StyleFactory.CREME_AGED,
            Align.left,
            0.57f
        );

        Label detailLabel = valueLabel(
            detail,
            ScreenUI.MUTED_TEXT,
            Align.left,
            0.44f
        );

        card.add(titleLabel).left().row();
        card.add(mainLabel).left().padTop(2f).row();
        card.add(detailLabel).left().padTop(2f);

        return card;
    }

    private Table createNextEventPanel() {
        Table panel = ScreenUI.createPanel();
        panel.pad(13f, 16f, 13f, 16f);

        panel.add(sectionTitle("PRÓXIMO EVENTO"))
            .left()
            .padBottom(7f)
            .row();

        Match next = game.league.getNextMatchForClub(playerClub);

        String headline;
        String detail;

        if (next != null && "REGULAR".equals(next.getStage())) {
            int nextRound =
                game.league.getRoundNumberForDate(next.getDate());

            headline = "Rodada " + nextRound;
            detail = ScreenUI.shorten(
                next.getHomeTeam().getName(),
                20
            ) + " x " + ScreenUI.shorten(
                next.getAwayTeam().getName(),
                20
            );
        } else if ("PLAYOFFS".equals(game.league.getCurrentStage())) {
            headline = "Playoffs";
            detail = "A fase eliminatória está pronta para começar.";
        } else {
            headline = "Próxima etapa";
            detail = "Acompanhe o calendário da liga.";
        }

        Label headlineLabel = valueLabel(
            headline,
            StyleFactory.PLAYOFF_GOLD,
            Align.left,
            0.78f
        );

        Label detailLabel = valueLabel(
            detail,
            StyleFactory.CREME_AGED,
            Align.left,
            0.50f
        );

        detailLabel.setWrap(true);

        panel.add(headlineLabel).left().row();
        panel.add(detailLabel).width(420f).left().padTop(4f);

        return panel;
    }

    private void addActions() {
        TextButton standings = ScreenUI.createInteractiveButton(
            "VER TABELA COMPLETA",
            game.skin
        );
        standings.getLabel().setFontScale(0.56f);
        standings.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {
                hide();
                game.setScreen(
                    new StandingsScreen(
                        game,
                        playerClub
                    )
                );
            }
        });

        TextButton continueButton = ScreenUI.createPrimaryButton(
            game.skin,
            "CONTINUAR  >"
        );
        continueButton.getLabel().setFontScale(0.62f);

        button(standings, false);
        button(continueButton, true);
    }

    private Match findPlayerClubMatch() {
        for (Match match : roundMatches) {
            if (involvesPlayerClub(match)) {
                return match;
            }
        }
        return null;
    }

    private boolean involvesPlayerClub(Match match) {
        return match != null &&
            (match.getHomeTeam() == playerClub ||
                match.getAwayTeam() == playerClub);
    }

    private Match getBestMatch() {
        Match best = null;
        int bestGoals = -1;

        for (Match match : roundMatches) {
            int goals =
                match.getHomeGoals() +
                match.getAwayGoals();

            if (goals > bestGoals) {
                bestGoals = goals;
                best = match;
            }
        }

        return best;
    }

    private Player getTopScorer() {
        Map<Player, Integer> goalCounts =
            new HashMap<Player, Integer>();

        for (Match match : roundMatches) {
            for (Player scorer : match.getGoalScorers()) {
                Integer goals = goalCounts.get(scorer);
                goalCounts.put(
                    scorer,
                    goals == null
                        ? 1
                        : goals + 1
                );
            }
        }

        Player best = null;
        int bestGoals = 0;

        for (Map.Entry<Player, Integer> entry :
            goalCounts.entrySet()) {
            Player player = entry.getKey();
            int goals = entry.getValue();

            if (
                best == null ||
                goals > bestGoals ||
                (goals == bestGoals &&
                    player.getName().compareTo(best.getName()) < 0)
            ) {
                best = player;
                bestGoals = goals;
            }
        }

        return best;
    }

    private int getRoundGoalCount(Player player) {
        int goals = 0;

        for (Match match : roundMatches) {
            for (Player scorer : match.getGoalScorers()) {
                if (scorer == player) {
                    goals++;
                }
            }
        }

        return goals;
    }

    private Match getBiggestSurprise() {
        Match surprise = null;
        double biggestGap = 0d;

        for (Match match : roundMatches) {
            Club winner = getWinner(match);

            if (winner == null) {
                continue;
            }

            Club loser = winner == match.getHomeTeam()
                ? match.getAwayTeam()
                : match.getHomeTeam();

            double gap = loser.getOverall() - winner.getOverall();

            if (gap > biggestGap) {
                biggestGap = gap;
                surprise = match;
            }
        }

        return surprise;
    }

    private Club getWinner(Match match) {
        if (match.getHomeGoals() > match.getAwayGoals()) {
            return match.getHomeTeam();
        }

        if (match.getAwayGoals() > match.getHomeGoals()) {
            return match.getAwayTeam();
        }

        return null;
    }

    private String getSurpriseDescription(Match match) {
        Club winner = getWinner(match);
        Club loser = winner == match.getHomeTeam()
            ? match.getAwayTeam()
            : match.getHomeTeam();

        int difference = Math.max(
            1,
            (int) Math.round(
                loser.getOverall() - winner.getOverall()
            )
        );

        return winner.getName() +
            " venceu um rival " +
            difference +
            " OVR acima.";
    }

    private String getMatchResult(Match match) {
        return ScreenUI.shorten(
            match.getHomeTeam().getName(),
            18
        ) + " " + match.getHomeGoals() +
            " x " + match.getAwayGoals() + " " +
            ScreenUI.shorten(
                match.getAwayTeam().getName(),
                18
            );
    }

    private Label sectionTitle(String text) {
        return valueLabel(
            text,
            StyleFactory.GOLD,
            Align.left,
            0.53f
        );
    }

    private Label valueLabel(
        String text,
        Color color,
        int alignment,
        float scale
    ) {
        Label label = new Label(
            text,
            game.skin,
            "font-bold"
        );
        label.setFontScale(scale);
        label.setColor(color);
        label.setAlignment(alignment);
        return label;
    }
}
