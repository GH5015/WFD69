package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchResultDialog extends Dialog {
    private final Main game;
    private final Match match;
    private Table contentContainer;

    public MatchResultDialog(Main game, Match match) {
        super("", game.skin);
        this.game = game;
        this.match = match;

        getContentTable().clear();
        buildLayout();
    }

    private void buildLayout() {
        Table root = getContentTable();
        root.background(StyleFactory.createMetallicBoard(1200, 720, Color.valueOf("1E1E1E")));
        root.pad(12);

        root.add(createHeaderSection()).growX().padBottom(6).row();
        root.add(createTabBar()).growX().padBottom(6).row();

        contentContainer = new Table();
        contentContainer.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        contentContainer.pad(10);

        root.add(contentContainer).grow().padBottom(8).row();

        switchTab(0);

        ImageTextButton btn = IconTextButton.create("CONTINUAR", game.skin, "Icons8/icons8-ok-50.png");
        btn.getLabel().setFontScale(1.0f);
        button(btn, true);
    }

    private Table createHeaderSection() {
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        header.pad(8, 12, 8, 12);

        Label leagueLbl = new Label("LIGA MUNDIAL  •  RODADA DE CAMPEONATO", game.skin, "font-bold", StyleFactory.GOLD);
        leagueLbl.setFontScale(0.90f);
        header.add(leagueLbl).colspan(3).center().padBottom(6).row();

        Table scoreRow = new Table();

        // --- MANDANTE (Logo Ampliada + Nome Menor) ---
        Table homeBlock = new Table();
        if (match.getHomeTeam() != null && match.getHomeTeam().getLogoPath() != null) {
            String homePath = match.getHomeTeam().getLogoPath();
            if (Gdx.files.internal(homePath).exists()) {
                Image homeLogo = new Image(new Texture(Gdx.files.internal(homePath)));
                homeLogo.setScaling(Scaling.fit);
                homeBlock.add(homeLogo).size(200, 120).center().padBottom(2).row();
            }
        }
        Label hName = new Label(match.getHomeTeam().getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        hName.setFontScale(0.60f);
        hName.setAlignment(Align.center);
        homeBlock.add(hName).center();

        // --- PLACAR CENTRAL ---
        Label score = new Label(match.getHomeGoals() + "  x  " + match.getAwayGoals(), game.skin, "font-title", Color.WHITE);
        score.setFontScale(2.0f);
        score.setAlignment(Align.center);

        // --- VISITANTE (Logo Ampliada + Nome Menor) ---
        Table awayBlock = new Table();
        if (match.getAwayTeam() != null && match.getAwayTeam().getLogoPath() != null) {
            String awayPath = match.getAwayTeam().getLogoPath();
            if (Gdx.files.internal(awayPath).exists()) {
                Image awayLogo = new Image(new Texture(Gdx.files.internal(awayPath)));
                awayLogo.setScaling(Scaling.fit);
                awayBlock.add(awayLogo).size(200, 120).center().padBottom(2).row();
            }
        }
        Label aName = new Label(match.getAwayTeam().getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        aName.setFontScale(0.60f);
        aName.setAlignment(Align.center);
        awayBlock.add(aName).center();

        // Montagem com maior espaçamento (pad 45px nas laterais do placar)
        scoreRow.add(homeBlock).right().expandX().padRight(45);
        scoreRow.add(score).center().width(160);
        scoreRow.add(awayBlock).left().expandX().padLeft(45);

        header.add(scoreRow).growX().padBottom(6).row();

        // --- AUTORES DOS GOLS ---
        Table goalsRow = new Table();

        Table homeGoals = new Table().top().right();
        for (Player p : getScorersForTeam(match.getHomeTeam())) {
            Label gLbl = new Label("⚽ " + p.getName(), game.skin, "font-label", StyleFactory.CREME_AGED);
            gLbl.setFontScale(0.55f);
            homeGoals.add(gLbl).right().row();
        }

        Table awayGoals = new Table().top().left();
        for (Player p : getScorersForTeam(match.getAwayTeam())) {
            Label gLbl = new Label("⚽ " + p.getName(), game.skin, "font-label", StyleFactory.CREME_AGED);
            gLbl.setFontScale(0.55f);
            awayGoals.add(gLbl).left().row();
        }

        goalsRow.add(homeGoals).right().expandX().padRight(45);
        goalsRow.add(new Label("", game.skin)).width(160);
        goalsRow.add(awayGoals).left().expandX().padLeft(45);

        header.add(goalsRow).growX().padBottom(6).row();

        Label infoLbl = new Label("Estádio: " + match.getHomeTeam().getStadium() + "  |  Público: 58.320", game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        infoLbl.setFontScale(0.80f);
        header.add(infoLbl).colspan(3).center().row();

        return header;
    }

    private Table createTabBar() {
        Table tabBar = new Table();
        tabBar.defaults().padRight(8).height(38).growX();

        String[] tabs = {"Resumo", "Mini Campo da partida ⭐", "Jogadores"};
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            TextButton tabBtn = new TextButton(tabs[i], game.skin);
            tabBtn.getLabel().setFontScale(0.9f);

            tabBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    switchTab(index);
                }
            });

            tabBar.add(tabBtn);
        }

        return tabBar;
    }

    private void switchTab(int tabIndex) {
        contentContainer.clear();

        switch (tabIndex) {
            case 0:
                buildResumoTab();
                break;
            case 1:
                buildMiniCampoTab();
                break;
            case 2:
                buildJogadoresTab();
                break;
        }
    }

    private void buildResumoTab() {
        Table mainSplit = new Table();

        Table statsTable = new Table();
        statsTable.defaults().padBottom(8).growX();

        Label title = new Label("RESUMO DA PARTIDA", game.skin, "font-bold", StyleFactory.GOLD);
        title.setFontScale(1.05f);
        statsTable.add(title).center().padBottom(12).row();

        addPossessionRow(statsTable, match.getHomePossession(), match.getAwayPossession());
        addStatComparisonRow(statsTable, String.valueOf(match.getHomeShots()), "Finalizações", String.valueOf(match.getAwayShots()));
        addStatComparisonRow(statsTable, String.valueOf(match.getHomeShotsOnTarget()), "No alvo", String.valueOf(match.getAwayShotsOnTarget()));
        addStatComparisonRow(statsTable, String.format("%.2f", match.getHomeXG()), "xG", String.format("%.2f", match.getAwayXG()));

        addStatComparisonRow(statsTable, String.valueOf(match.getHomeCorners()), "Escanteios", String.valueOf(match.getAwayCorners()));
        addStatComparisonRow(statsTable, String.valueOf(match.getHomeFouls()), "Faltas", String.valueOf(match.getAwayFouls()));

        String homeCardsFormatted = getFormattedCardCount(true);
        String awayCardsFormatted = getFormattedCardCount(false);
        addStatComparisonRow(statsTable, homeCardsFormatted, "Cartões", awayCardsFormatted);

        mainSplit.add(statsTable).expand().top().padRight(20);

        mainSplit.add(new Label("│\n│\n│\n│\n│\n│\n│\n│\n│", game.skin, "font-label", StyleFactory.DARK_GOLD)).padRight(20);

        Table sideTable = new Table().top();

        Table highlightsBox = new Table();
        highlightsBox.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        highlightsBox.pad(10);

        Label hlTitle = new Label("⭐ DESTAQUES", game.skin, "font-bold", StyleFactory.GOLD);
        hlTitle.setFontScale(0.95f);
        highlightsBox.add(hlTitle).left().padBottom(6).row();

        Player bestPlayer = getBestPlayerByGrade();
        float bestGradeValue = bestPlayer != null ? calculatePlayerGrade(bestPlayer) : 6.0f;

        Label bestHeader = new Label("Melhor em Campo", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        bestHeader.setFontScale(0.82f);
        highlightsBox.add(bestHeader).left().row();

        Label bestStars = new Label("★★★★★", game.skin, "font-bold", StyleFactory.GOLD);
        bestStars.setFontScale(0.82f);
        highlightsBox.add(bestStars).left().row();

        Label bestName = new Label(bestPlayer != null ? bestPlayer.getName() : "N/A", game.skin, "font-bold", Color.WHITE);
        bestName.setFontScale(1.0f);
        highlightsBox.add(bestName).left().row();

        Label bestGrade = new Label(String.format("Nota: %.1f", bestGradeValue), game.skin, "font-label", StyleFactory.CREME_AGED);
        bestGrade.setFontScale(0.82f);
        highlightsBox.add(bestGrade).left().row();

        Label bestStats = new Label(getBestPlayerSummary(bestPlayer), game.skin, "font-label", Color.LIGHT_GRAY);
        bestStats.setFontScale(0.75f);
        highlightsBox.add(bestStats).left().padBottom(8).row();

        Player worstPlayer = getWorstPlayerByGrade();
        float worstGradeValue = worstPlayer != null ? calculatePlayerGrade(worstPlayer) : 5.0f;

        Label worstHeader = new Label("Pior em Campo", game.skin, "font-bold", Color.SALMON);
        worstHeader.setFontScale(0.82f);
        highlightsBox.add(worstHeader).left().row();

        Label worstStars = new Label("★★", game.skin, "font-bold", Color.GRAY);
        worstStars.setFontScale(0.82f);
        highlightsBox.add(worstStars).left().row();

        Label worstName = new Label(worstPlayer != null ? worstPlayer.getName() : "N/A", game.skin, "font-bold", Color.WHITE);
        worstName.setFontScale(1.0f);
        highlightsBox.add(worstName).left().row();

        Label worstGrade = new Label(String.format("Nota: %.1f", worstGradeValue), game.skin, "font-label", StyleFactory.CREME_AGED);
        worstGrade.setFontScale(0.82f);
        highlightsBox.add(worstGrade).left().row();

        Label worstStats = new Label(getWorstPlayerSummary(worstPlayer), game.skin, "font-label", Color.LIGHT_GRAY);
        worstStats.setFontScale(0.75f);
        highlightsBox.add(worstStats).left().row();

        sideTable.add(highlightsBox).growX().padBottom(10).row();

        Table others = new Table();
        others.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        others.pad(10);
        Label othersTitle = new Label("📋 OUTROS RESULTADOS", game.skin, "font-label", StyleFactory.GOLD);
        others.add(othersTitle).left().padBottom(6).row();

        List<Match> simulated = game.league.getSchedule().stream()
            .filter(m -> m.isPlayed() && m != match && m.getDate().equals(match.getDate()))
            .limit(3).collect(Collectors.toList());

        if (simulated.isEmpty()) {
            others.add(new Label("Nenhum outro jogo nesta data", game.skin, "font-label", Color.LIGHT_GRAY)).left().row();
        } else {
            for (Match om : simulated) {
                String rStr = om.getHomeTeam().getName() + " " + om.getHomeGoals() + "-" + om.getAwayGoals() + " " + om.getAwayTeam().getName();
                Label lbl = new Label(rStr, game.skin, "font-label", StyleFactory.SOFT_YELLOW);
                lbl.setFontScale(0.82f);
                others.add(lbl).left().padBottom(2).row();
            }
        }
        sideTable.add(others).growX().padBottom(10).row();

        Table top4 = new Table();
        top4.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        top4.pad(10);
        Label rankTitle = new Label("🏆 TOP 4 DA TABELA", game.skin, "font-label", StyleFactory.GOLD);
        top4.add(rankTitle).left().padBottom(6).row();

        List<StandingsRow> standings = game.league.getFullStandings(null).stream().limit(4).collect(Collectors.toList());
        int pos = 1;
        for (StandingsRow sr : standings) {
            String medal = pos == 1 ? "🥇" : (pos == 2 ? "🥈" : (pos == 3 ? "🥉" : "4."));
            Label rLbl = new Label(medal + " " + sr.club.getName() + " (" + sr.points + " pts)", game.skin, "font-label", StyleFactory.CREME_AGED);
            rLbl.setFontScale(0.82f);
            top4.add(rLbl).left().padBottom(2).row();
            pos++;
        }
        sideTable.add(top4).growX().row();

        mainSplit.add(sideTable).width(380).top();

        contentContainer.add(mainSplit).grow().row();
    }

    private void buildMiniCampoTab() {
        Table mainSplit = new Table();
        mainSplit.top();

        int homeShots = match.getHomeShots();
        int homePossession = match.getHomePossession();

        int leftPct = Math.min(60, Math.max(20, 35 + (homeShots % 7) * 4 - (homePossession % 5)));
        int rightPct = Math.min(60, Math.max(20, 30 + (homePossession % 8) * 4));
        int centerPct = Math.max(10, 100 - (leftPct + rightPct));

        Table fieldContainer = new Table();
        fieldContainer.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        fieldContainer.pad(12);

        Label fieldTitle = new Label("VETORES DE ATAQUE DA PARTIDA ⭐", game.skin, "font-bold", StyleFactory.GOLD);
        fieldTitle.setFontScale(0.95f);
        fieldContainer.add(fieldTitle).center().padBottom(10).row();

        Table pitch = new Table();
        pitch.background(StyleFactory.createRoundedPanel(Color.valueOf("1E4620"), StyleFactory.GOLD));
        pitch.pad(15);
        pitch.defaults().center().expandX();

        Label goalArea = new Label("[  GOL ADVERSÁRIO  ]", game.skin, "font-bold", StyleFactory.CREME_AGED);
        goalArea.setFontScale(0.85f);
        pitch.add(goalArea).padBottom(20).row();

        Table attackArrowsRow = new Table();
        attackArrowsRow.defaults().expandX().center();

        Label leftArrow = new Label("↖ ↖ ↖\n" + leftPct + "%", game.skin, "font-bold", getZoneColor(leftPct));
        leftArrow.setAlignment(Align.center);

        Label centerArrow = new Label("⬆ ⬆ ⬆\n" + centerPct + "%", game.skin, "font-bold", getZoneColor(centerPct));
        centerArrow.setAlignment(Align.center);

        Label rightArrow = new Label("↗ ↗ ↗\n" + rightPct + "%", game.skin, "font-bold", getZoneColor(rightPct));
        rightArrow.setAlignment(Align.center);

        attackArrowsRow.add(leftArrow);
        attackArrowsRow.add(centerArrow);
        attackArrowsRow.add(rightArrow);

        pitch.add(attackArrowsRow).growX().padBottom(25).row();

        Table sectorLabelsRow = new Table();
        sectorLabelsRow.defaults().expandX().center();

        Label leftName = new Label("Esquerda", game.skin, "font-label", Color.WHITE);
        Label centerName = new Label("Centro", game.skin, "font-label", Color.WHITE);
        Label rightName = new Label("Direita", game.skin, "font-label", Color.WHITE);

        sectorLabelsRow.add(leftName);
        sectorLabelsRow.add(centerName);
        sectorLabelsRow.add(rightName);

        pitch.add(sectorLabelsRow).growX().padBottom(15).row();

        Label buildUpLabel = new Label("─── LEITURA DE ZONAS OFENSIVAS ───", game.skin, "font-label", StyleFactory.DARK_GOLD);
        buildUpLabel.setFontScale(0.75f);
        pitch.add(buildUpLabel).padBottom(8).row();

        fieldContainer.add(pitch).width(440).height(380).center();
        mainSplit.add(fieldContainer).expand().fill().padRight(20);

        mainSplit.add(new Label("│\n│\n│\n│\n│\n│\n│\n│\n│", game.skin, "font-label", StyleFactory.DARK_GOLD)).padRight(20);

        Table mapTable = new Table();
        mapTable.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        mapTable.pad(15);

        Label mapTitle = new Label("ESTATÍSTICAS DE ATAQUE", game.skin, "font-bold", StyleFactory.GOLD);
        mapTitle.setFontScale(0.95f);
        mapTable.add(mapTitle).left().padBottom(15).row();

        addAttackZoneRow(mapTable, "Ataques pela Esquerda ↖", leftPct, getZoneColor(leftPct));
        addAttackZoneRow(mapTable, "Ataques pelo Centro ⬆", centerPct, getZoneColor(centerPct));
        addAttackZoneRow(mapTable, "Ataques pela Direita ↗", rightPct, getZoneColor(rightPct));

        mainSplit.add(mapTable).width(380).top();

        contentContainer.add(mainSplit).grow().row();
    }

    private void buildJogadoresTab() {
        Table playersTable = new Table();
        playersTable.top().pad(10);

        Table homeCol = new Table().top();
        Label hTitle = new Label(match.getHomeTeam().getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        homeCol.add(hTitle).padBottom(10).row();

        for (Player p : match.getHomeTeam().getStartingXI()) {
            float grade = calculatePlayerGrade(p);
            Color gradeColor = getGradeColor(grade);

            Table row = new Table();
            Label nameLbl = new Label(p.getName() + " (" + p.getPosition() + ")", game.skin, "font-label", Color.WHITE);
            nameLbl.setFontScale(0.85f);

            Label gradeLbl = new Label(String.format("%.1f", grade), game.skin, "font-bold", gradeColor);
            gradeLbl.setFontScale(0.9f);

            row.add(nameLbl).left().expandX();
            row.add(gradeLbl).right().padLeft(10);

            homeCol.add(row).growX().padBottom(5).row();
        }

        Table awayCol = new Table().top();
        Label aTitle = new Label(match.getAwayTeam().getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        awayCol.add(aTitle).padBottom(10).row();

        for (Player p : match.getAwayTeam().getStartingXI()) {
            float grade = calculatePlayerGrade(p);
            Color gradeColor = getGradeColor(grade);

            Table row = new Table();
            Label gradeLbl = new Label(String.format("%.1f", grade), game.skin, "font-bold", gradeColor);
            gradeLbl.setFontScale(0.9f);

            Label nameLbl = new Label("(" + p.getPosition() + ") " + p.getName(), game.skin, "font-label", Color.WHITE);
            nameLbl.setFontScale(0.85f);

            row.add(gradeLbl).left().padRight(10);
            row.add(nameLbl).right().expandX();

            awayCol.add(row).growX().padBottom(5).row();
        }

        playersTable.add(homeCol).expandX().top().padRight(20);
        playersTable.add(awayCol).expandX().top().padLeft(20);

        ScrollPane scroll = new ScrollPane(playersTable, game.skin);
        scroll.setFadeScrollBars(false);

        contentContainer.add(scroll).grow().row();
    }

    private float calculatePlayerGrade(Player p) {
        if (p == null) return 6.0f;

        float grade = 6.0f;

        boolean isHome = match.getHomeTeam().getStartingXI().contains(p);
        boolean isWin = isHome ? (match.getHomeGoals() > match.getAwayGoals()) : (match.getAwayGoals() > match.getHomeGoals());
        boolean isDraw = match.getHomeGoals() == match.getAwayGoals();

        if (isWin) grade += 0.5f;
        else if (!isDraw) grade -= 0.3f;

        long goals = match.getGoalScorers().stream().filter(g -> g.equals(p)).count();
        long assists = match.getAssisters().stream().filter(a -> a.equals(p)).count();

        grade += goals * 1.4f;
        grade += assists * 0.8f;

        String pos = p.getPosition() != null ? p.getPosition().toString() : "";

        if ("GK".equalsIgnoreCase(pos)) {
            int goalsConceded = isHome ? match.getAwayGoals() : match.getHomeGoals();
            if (goalsConceded == 0) grade += 1.2f;
            else grade -= goalsConceded * 0.4f;
        } else if ("CB".equalsIgnoreCase(pos) || "LB".equalsIgnoreCase(pos) || "RB".equalsIgnoreCase(pos)) {
            int goalsConceded = isHome ? match.getAwayGoals() : match.getHomeGoals();
            if (goalsConceded == 0) grade += 0.6f;
            else grade -= goalsConceded * 0.2f;
        }

        String card = match.getCards().get(p);
        if ("Amarelo".equalsIgnoreCase(card) || "YELLOW".equalsIgnoreCase(card)) {
            grade -= 0.6f;
        } else if ("Vermelho".equalsIgnoreCase(card) || "RED".equalsIgnoreCase(card)) {
            grade -= 2.2f;
        }

        return Math.max(1.0f, Math.min(10.0f, grade));
    }

    private Player getBestPlayerByGrade() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(match.getHomeTeam().getStartingXI());
        allPlayers.addAll(match.getAwayTeam().getStartingXI());

        return allPlayers.stream()
            .max(Comparator.comparingDouble(this::calculatePlayerGrade))
            .orElse(null);
    }

    private Player getWorstPlayerByGrade() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(match.getHomeTeam().getStartingXI());
        allPlayers.addAll(match.getAwayTeam().getStartingXI());

        return allPlayers.stream()
            .min(Comparator.comparingDouble(this::calculatePlayerGrade))
            .orElse(null);
    }

    private Color getGradeColor(float grade) {
        if (grade >= 8.0f) return StyleFactory.GOLD;
        if (grade >= 7.0f) return StyleFactory.SOFT_YELLOW;
        if (grade >= 6.0f) return Color.WHITE;
        return Color.SALMON;
    }

    private String getBestPlayerSummary(Player p) {
        if (p == null) return "Sem dados";
        long goals = match.getGoalScorers().stream().filter(g -> g.equals(p)).count();
        long assists = match.getAssisters().stream().filter(a -> a.equals(p)).count();
        if (goals > 0 || assists > 0) {
            return goals + " gol(s)  •  " + assists + " assist.  •  Atuação decisiva";
        }
        return "Dominou as ações de jogo  •  Excelente rendimento";
    }

    private String getWorstPlayerSummary(Player p) {
        if (p == null) return "Sem dados";
        String card = match.getCards().get(p);
        if (card != null) return "Recebeu cartão " + card + "  •  Faltas excessivas";
        return "Erros defensivos e baixo aproveitamento";
    }

    private Color getZoneColor(int percent) {
        if (percent >= 40) return StyleFactory.GOLD;
        if (percent >= 25) return StyleFactory.SOFT_YELLOW;
        return Color.LIGHT_GRAY;
    }

    private void addAttackZoneRow(Table table, String zoneName, int percent, Color barColor) {
        Table block = new Table();
        block.defaults().left();

        Label nameLbl = new Label(zoneName + "  •  " + percent + "%", game.skin, "font-label", Color.WHITE);
        nameLbl.setFontScale(0.85f);
        block.add(nameLbl).padBottom(4).row();

        int totalBlocks = 16;
        int activeBlocks = Math.round((percent / 100f) * totalBlocks);

        StringBuilder barStr = new StringBuilder();
        for (int i = 0; i < activeBlocks; i++) barStr.append("|");
        for (int i = activeBlocks; i < totalBlocks; i++) barStr.append(".");

        Label barLbl = new Label("[" + barStr.toString() + "]", game.skin, "font-bold", barColor);
        barLbl.setFontScale(1.0f);
        block.add(barLbl).row();

        table.add(block).growX().padBottom(14).row();
    }

    private void addPossessionRow(Table table, int home, int away) {
        Table row = new Table();

        Label homeLbl = new Label(home + "%", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        homeLbl.setFontScale(0.95f);

        int totalBlocks = 15;
        int homeBlocks = Math.round((home / 100f) * totalBlocks);
        int awayBlocks = totalBlocks - homeBlocks;

        StringBuilder barStr = new StringBuilder();
        for (int i = 0; i < homeBlocks; i++) barStr.append("|");
        for (int i = awayBlocks; i < totalBlocks; i++) barStr.append(".");

        Label barLbl = new Label("[" + barStr.toString() + "]", game.skin, "font-bold", StyleFactory.GOLD);
        Label titleLbl = new Label("Posse de bola", game.skin, "font-label", Color.WHITE);
        Label awayLbl = new Label(away + "%", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);

        Table centerGroup = new Table();
        centerGroup.add(titleLbl).center().row();
        centerGroup.add(barLbl).center();

        row.add(homeLbl).right().width(60).padRight(10);
        row.add(centerGroup).center().expandX();
        row.add(awayLbl).left().width(60).padLeft(10);

        table.add(row).growX().padBottom(10).row();
    }

    private void addStatComparisonRow(Table table, String left, String label, String right) {
        Table row = new Table();

        Label leftLbl = new Label(left, game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        leftLbl.setAlignment(Align.right);

        Label centerLbl = new Label(label, game.skin, "font-label", Color.WHITE);
        centerLbl.setAlignment(Align.center);

        Label rightLbl = new Label(right, game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        rightLbl.setAlignment(Align.left);

        row.add(leftLbl).right().width(80).padRight(15);
        row.add(centerLbl).center().expandX();
        row.add(rightLbl).left().width(80).padLeft(15);

        table.add(row).growX().padBottom(8).row();
    }

    private List<Player> getScorersForTeam(Club team) {
        return match.getGoalScorers().stream()
            .filter(p -> team.getSquad().contains(p))
            .collect(Collectors.toList());
    }

    private String getFormattedCardCount(boolean isHome) {
        Club team = isHome ? match.getHomeTeam() : match.getAwayTeam();

        long yellow = 0;
        long red = 0;

        for (Map.Entry<Player, String> entry : match.getCards().entrySet()) {
            if (team.getSquad().contains(entry.getKey())) {
                String cardType = entry.getValue();
                if ("Amarelo".equalsIgnoreCase(cardType) || "YELLOW".equalsIgnoreCase(cardType)) {
                    yellow++;
                } else if ("Vermelho".equalsIgnoreCase(cardType) || "RED".equalsIgnoreCase(cardType)) {
                    red++;
                }
            }
        }

        return "🟨 " + yellow + "  🔴 " + red;
    }
}
