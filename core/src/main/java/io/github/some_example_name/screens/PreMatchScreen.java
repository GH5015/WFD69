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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.engine.TacticalEngine;
import io.github.some_example_name.engine.TacticalModifiers;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PreMatchScreen implements Screen {

    private final Main game;
    private final Match match;
    private final Club playerClub;
    private Stage stage;

    private Texture bgTexture;
    private Texture homeLogoTexture;
    private Texture awayLogoTexture;

    public PreMatchScreen(Main game, Match match, Club playerClub) {
        this.game = game;
        this.match = match;
        this.playerClub = playerClub;
        this.stage = new Stage(new ScreenViewport());

        if (Gdx.files.internal("fundo_pre.png").exists()) {
            this.bgTexture = new Texture(Gdx.files.internal("fundo_pre.png"));
        } else {
            this.bgTexture = null;
        }

        this.homeLogoTexture = loadLogo(match.getHomeTeam());
        this.awayLogoTexture = loadLogo(match.getAwayTeam());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        stage.clear();

        Stack rootStack = new Stack();
        rootStack.setFillParent(true);
        stage.addActor(rootStack);

        if (bgTexture != null) {
            rootStack.add(new Image(bgTexture));
        } else if (game.background != null) {
            rootStack.add(new Image(game.background));
        }

        Table mainContainer = new Table();
        mainContainer.top().pad(16, 24, 16, 24);

        Table contentBox = new Table();
        contentBox.background(StyleFactory.createSolid(new Color(0.05f, 0.07f, 0.08f, 0.88f)));
        contentBox.pad(12);

        // 1. Cabeçalho
        contentBox.add(buildHeader()).growX().padBottom(10).row();

        // 2. Banner de Probabilidades + Resumo dos Times
        contentBox.add(buildMatchBanner()).growX().padBottom(12).row();

        // 3. Escalações Titulares
        contentBox.add(buildLineupsTable()).growX().padBottom(10).row();

        // 4. Desfalques Detalhados
        contentBox.add(buildAbsencesTable()).growX().padBottom(10).row();

        // 5. Jogos da Rodada com Destaque
        contentBox.add(buildRoundMatchesTable()).growX().padBottom(12).row();

        // 6. Botões de Ação (Voltar e Iniciar Partida)
        Table buttonsTable = new Table();

        TextButton btnBack = new TextButton("⬅ VOLTAR", game.skin);
        btnBack.getLabel().setFontScale(0.95f);
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TacticsScreen(game, playerClub));
            }
        });

        TextButton btnStart = new TextButton("⚽ INICIAR PARTIDA", game.skin);
        btnStart.getLabel().setFontScale(0.95f);
        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String validationError = validatePlayerClubStatus();
                if (validationError != null) {
                    showValidationModal(validationError);
                } else {
                    game.setScreen(new MatchScreen(game, match, playerClub));
                }
            }
        });

        buttonsTable.add(btnBack).width(160).height(42).padRight(12);
        buttonsTable.add(btnStart).width(280).height(42);

        contentBox.add(buttonsTable).center().padTop(6);

        ScrollPane scrollPane = new ScrollPane(contentBox, game.skin);
        scrollPane.setFadeScrollBars(false);
        mainContainer.add(scrollPane).grow();

        rootStack.add(mainContainer);
    }

    /**
     * Valida se a formação foi escolhida e se há jogadores lesionados ou suspensos no time titular do jogador.
     * @return Mensagem de erro caso haja impedimento, ou null se estiver válido.
     */
    private String validatePlayerClubStatus() {
        if (playerClub == null) return null;

        // 1. Exige que uma formação seja escolhida
        if (playerClub.getFormation() == null) {
            return "Escolha uma formação para o seu time antes de iniciar a partida!";
        }

        // 2. Não permite avançar se houver jogador lesionado ou suspenso/expulso no time titular
        for (Player starter : playerClub.getTacticsMap().values()) {
            if (starter != null && !starter.canPlay()) {
                if (starter.isInjured()) {
                    return "O jogador titular " + starter.getName() + " está lesionado. Substitua-o antes de jogar.";
                } else if (starter.isSuspended()) {
                    return "O jogador titular " + starter.getName() + " está suspenso/expulso. Substitua-o antes de jogar.";
                }
            }
        }

        return null;
    }

    /**
     * Exibe um modal de aviso informando o erro de validação ao usuário.
     */
    private void showValidationModal(String message) {
        Dialog dialog = new Dialog("", game.skin);
        dialog.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        dialog.pad(16);

        Table content = dialog.getContentTable();
        content.defaults().pad(8);

        Label titleLbl = new Label("⚠️ ATENÇÃO", game.skin, "font-bold", StyleFactory.GOLD);
        titleLbl.setFontScale(1.1f);
        titleLbl.setAlignment(Align.center);
        content.add(titleLbl).center().row();

        Label msgLbl = new Label(message, game.skin, "font-label", Color.WHITE);
        msgLbl.setFontScale(0.85f);
        msgLbl.setWrap(true);
        msgLbl.setAlignment(Align.center);
        content.add(msgLbl).width(320).center().padTop(8).row();

        TextButton btnOk = new TextButton("OK", game.skin);
        btnOk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.getButtonTable().padTop(12);
        dialog.button(btnOk);
        dialog.show(stage);
    }

    private Table buildHeader() {
        Table header = new Table();
        header.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        header.pad(8, 16, 8, 16);

        int currentRound = (game.league != null) ? game.league.getCurrentRound() : 1;
        Label lblTitle = new Label("WFL • RODADA " + currentRound, game.skin, "font-bold", StyleFactory.GOLD);
        lblTitle.setFontScale(1.0f);
        lblTitle.setAlignment(Align.center);

        String matchDateStr = "15 DE JUNHO DE 1969";
        if (match != null && match.getDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'DE' MMMM 'DE' yyyy", new Locale("pt", "BR"));
            matchDateStr = dateFormat.format(match.getDate()).toUpperCase();
        }

        Label lblDate = new Label(matchDateStr, game.skin, "font-label", Color.WHITE);
        lblDate.setFontScale(0.80f);
        lblDate.setAlignment(Align.center);

        header.add(lblTitle).center().row();
        header.add(lblDate).center().padTop(2);

        return header;
    }

    private Table buildMatchBanner() {
        Table banner = new Table();
        banner.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        banner.pad(12);

        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        int[] odds = calculateAdvancedOdds(home, away);
        int homePct = odds[0];
        int drawPct = odds[1];
        int awayPct = odds[2];

        // Lado Esquerdo - Mandante
        Table homeBox = buildTeamSummaryBox(home, homeLogoTexture);

        // Centro - Probabilidades + Barras
        Table probBox = new Table();

        Label lblProbTitle = new Label("─────── PROBABILIDADES DE RESULTADO ───────", game.skin, "font-bold", StyleFactory.DARK_GOLD);
        lblProbTitle.setFontScale(0.75f);
        probBox.add(lblProbTitle).colspan(3).center().padBottom(8).row();

        // Percentuais no topo
        Label lblHomePct = new Label(homePct + "%", game.skin, "font-bold", StyleFactory.GOLD);
        lblHomePct.setFontScale(1.1f);
        Label lblDrawPct = new Label(drawPct + "%", game.skin, "font-bold", Color.LIGHT_GRAY);
        lblDrawPct.setFontScale(1.1f);
        Label lblAwayPct = new Label(awayPct + "%", game.skin, "font-bold", StyleFactory.GOLD);
        lblAwayPct.setFontScale(1.1f);

        probBox.add(lblHomePct).width(75).center();
        probBox.add(lblDrawPct).width(75).center();
        probBox.add(lblAwayPct).width(75).center().row();

        // Nomes curtos abaixo dos percentuais
        Label lblHomeSub = new Label(getShortName(home), game.skin, "font-label", Color.WHITE);
        lblHomeSub.setFontScale(0.68f);
        Label lblDrawSub = new Label("EMPATE", game.skin, "font-label", Color.LIGHT_GRAY);
        lblDrawSub.setFontScale(0.68f);
        Label lblAwaySub = new Label(getShortName(away), game.skin, "font-label", Color.WHITE);
        lblAwaySub.setFontScale(0.68f);

        probBox.add(lblHomeSub).width(75).center();
        probBox.add(lblDrawSub).width(75).center();
        probBox.add(lblAwaySub).width(75).center().row();
        probBox.getCell(lblAwaySub).padBottom(10);

        // Barras de Progresso Organizadas
        Table barsTable = new Table();
        barsTable.defaults().padRight(12).center();

        barsTable.add(createVerticalBar(homePct, StyleFactory.GOLD)).width(45).height(45);
        barsTable.add(createVerticalBar(drawPct, Color.GRAY)).width(45).height(45);
        barsTable.add(createVerticalBar(awayPct, StyleFactory.GOLD)).width(45).height(45);

        probBox.add(barsTable).colspan(3).center();

        // Lado Direito - Visitante
        Table awayBox = buildTeamSummaryBox(away, awayLogoTexture);

        banner.add(homeBox).width(210).center();
        banner.add(probBox).expandX().center();
        banner.add(awayBox).width(210).center();

        return banner;
    }

    private Table buildTeamSummaryBox(Club team, Texture logo) {
        Table box = new Table();
        box.center();

        if (logo != null) {
            Image img = new Image(logo);
            img.setScaling(Scaling.fit);
            box.add(img).size(95, 65).padBottom(4).row();
        }

        Label name = new Label(team.getName().toUpperCase(), game.skin, "font-bold", Color.WHITE);
        name.setFontScale(0.80f);
        name.setAlignment(Align.center);
        box.add(name).padBottom(4).row();

        int pos = 1;
        int pts = 0;
        if (game.league != null) {
            List<StandingsRow> standings = game.league.getFullStandings(null);
            for (int i = 0; i < standings.size(); i++) {
                if (standings.get(i).club == team) {
                    pos = i + 1;
                    pts = standings.get(i).points;
                    break;
                }
            }
        }

        Label infoLbl = new Label("Posição: " + pos + "º  |  Pontos: " + pts, game.skin, "font-label", StyleFactory.SOFT_YELLOW);
        infoLbl.setFontScale(0.68f);
        box.add(infoLbl).padBottom(4).row();

        // Linha da Forma Recente Dinâmica
        Table formTable = new Table();
        formTable.add(new Label("Forma: ", game.skin, "font-label", Color.LIGHT_GRAY)).padRight(2);

        List<Character> recentForm = getRecentForm(team);
        for (char result : recentForm) {
            Color color;
            if (result == 'V') {
                color = Color.GREEN;
            } else if (result == 'E') {
                color = Color.LIGHT_GRAY;
            } else if (result == 'D') {
                color = Color.RED;
            } else {
                color = Color.DARK_GRAY;
            }

            Label letter = new Label(String.valueOf(result), game.skin, "font-bold", color);
            letter.setFontScale(0.72f);
            formTable.add(letter).padRight(3);
        }

        box.add(formTable);
        return box;
    }

    private Table createVerticalBar(int percentage, Color barColor) {
        Table barContainer = new Table();
        barContainer.background(getSolidDrawable(Color.valueOf("1F1F1F")));
        barContainer.top();

        Table fill = new Table();
        fill.background(getSolidDrawable(barColor));

        float normalized = Math.max(0f, Math.min(100f, percentage)) / 100f;
        barContainer.add(fill).growX().height(45 * normalized).bottom();
        return barContainer;
    }

    private Table buildLineupsTable() {
        Table root = new Table();

        Table homeCol = buildTeamLineupColumn(match.getHomeTeam());
        Table awayCol = buildTeamLineupColumn(match.getAwayTeam());

        root.add(homeCol).expandX().fillX().padRight(6);
        root.add(awayCol).expandX().fillX().padLeft(6);

        return root;
    }

    private Table buildTeamLineupColumn(Club team) {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        panel.pad(8);

        Label nameLbl = new Label(team.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLbl.setFontScale(0.85f);
        panel.add(nameLbl).left().padBottom(2).row();

        Formation form = team.getFormation();
        String formName = form != null ? form.getName() : "4-3-3";
        Label formLbl = new Label("Formação: " + formName, game.skin, "font-label", Color.LIGHT_GRAY);
        formLbl.setFontScale(0.75f);
        panel.add(formLbl).left().padBottom(8).row();

        Table squadList = new Table();
        squadList.top().left();

        List<String> defaultSlots = List.of("GK", "LB", "CB", "CB", "RB", "CM", "CM", "CM", "LW", "ST", "RW");
        List<String> slots = (form != null && form.getPositionSlots() != null && !form.getPositionSlots().isEmpty())
            ? form.getPositionSlots() : defaultSlots;

        List<Player> starters = new ArrayList<>(team.getTacticsMap().values());

        for (int i = 0; i < 11; i++) {
            String slotPos = (i < slots.size()) ? slots.get(i) : "CM";
            final Player p = (i < starters.size()) ? starters.get(i) : null;

            Table row = new Table();
            row.left().padBottom(3);

            Label posBadge = new Label(slotPos, game.skin, "font-bold", StyleFactory.getPositionColor(slotPos));
            posBadge.setFontScale(0.72f);
            row.add(posBadge).width(40).left();

            String pName = p != null ? p.getName() : "...";
            Label namePlayer = new Label(pName, game.skin, "font-label", Color.WHITE);
            namePlayer.setFontScale(0.75f);
            namePlayer.setEllipsis(true);
            row.add(namePlayer).expandX().left();

            if (p != null) {
                row.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                row.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showPlayerCardModal(p);
                    }
                });
            }

            squadList.add(row).growX().row();
        }

        panel.add(squadList).growX().left();
        return panel;
    }

    private void showPlayerCardModal(Player player) {
        Dialog dialog = new Dialog("", game.skin);
        dialog.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        dialog.pad(16);

        Table content = dialog.getContentTable();
        content.defaults().pad(4);

        Label nameLbl = new Label(player.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLbl.setFontScale(1.1f);
        nameLbl.setAlignment(Align.center);
        content.add(nameLbl).colspan(2).center().padBottom(2).row();

        Label posLbl = new Label("POSIÇÃO: " + player.getPosition(), game.skin, "font-label", Color.LIGHT_GRAY);
        posLbl.setFontScale(0.78f);
        posLbl.setAlignment(Align.center);
        content.add(posLbl).colspan(2).center().padBottom(12).row();

        TechnicalAttributes attrs = player.getTechnicalAttributes();
        int atk = attrs != null ? attrs.getAtaque() : 70;
        int pass = attrs != null ? attrs.getPasse() : 70;
        int drb = attrs != null ? attrs.getDrible() : 70;
        int phy = attrs != null ? attrs.getFisico() : 70;

        content.add(new Label("ATAQUE", game.skin, "font-bold", Color.WHITE)).left();
        content.add(new Label(String.valueOf(atk), game.skin, "font-bold", StyleFactory.SOFT_YELLOW)).right().row();

        content.add(new Label("PASSE", game.skin, "font-bold", Color.WHITE)).left();
        content.add(new Label(String.valueOf(pass), game.skin, "font-bold", StyleFactory.SOFT_YELLOW)).right().row();

        content.add(new Label("DRIBLE", game.skin, "font-bold", Color.WHITE)).left();
        content.add(new Label(String.valueOf(drb), game.skin, "font-bold", StyleFactory.SOFT_YELLOW)).right().row();

        content.add(new Label("FÍSICO", game.skin, "font-bold", Color.WHITE)).left();
        content.add(new Label(String.valueOf(phy), game.skin, "font-bold", StyleFactory.SOFT_YELLOW)).right().padBottom(10).row();

        Table divider = new Table();
        divider.background(getSolidDrawable(StyleFactory.GOLD));
        content.add(divider).colspan(2).growX().height(1).padBottom(8).row();

        content.add(new Label("OVERALL", game.skin, "font-bold", StyleFactory.GOLD)).left();
        Label ovrVal = new Label(String.valueOf(player.getOverall()), game.skin, "font-bold", Color.WHITE);
        ovrVal.setFontScale(1.15f);
        content.add(ovrVal).right().row();

        TextButton btnClose = new TextButton("FECHAR", game.skin);
        btnClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.getButtonTable().padTop(12);
        dialog.button(btnClose);
        dialog.show(stage);
    }

    private Table buildAbsencesTable() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        panel.pad(10);

        Label title = new Label("DESFALQUES", game.skin, "font-bold", Color.WHITE);
        title.setFontScale(0.85f);
        panel.add(title).colspan(2).center().padBottom(8).row();

        Table homeAbs = buildAbsenceCounts(match.getHomeTeam());
        Table awayAbs = buildAbsenceCounts(match.getAwayTeam());

        panel.add(homeAbs).expandX().fillX().top().padRight(8);
        panel.add(awayAbs).expandX().fillX().top().padLeft(8);

        return panel;
    }

    private Table buildAbsenceCounts(Club team) {
        Table box = new Table();
        box.top().left();

        Label nameLbl = new Label(team.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLbl.setFontScale(0.80f);
        box.add(nameLbl).left().padBottom(6).row();

        List<Player> absentees = new ArrayList<>();
        for (Player p : team.getSquad()) {
            if (!p.canPlay()) {
                absentees.add(p);
            }
        }

        if (absentees.isEmpty()) {
            Label noAbsence = new Label("Nenhum desfalque", game.skin, "font-label", Color.LIGHT_GRAY);
            noAbsence.setFontScale(0.72f);
            box.add(noAbsence).left();
        } else {
            for (Player p : absentees) {
                Table row = new Table();
                row.left().padBottom(4);

                String icon = p.isInjured() ? "🩹 " : "🔴 ";
                String reason = p.isInjured() ? "Lesionado (" + p.getInjuryDuration() + "J)" : "Suspenso (" + p.getSuspendedMatches() + "J)";

                Label playerLbl = new Label(icon + p.getName(), game.skin, "font-bold", Color.WHITE);
                playerLbl.setFontScale(0.75f);

                Label reasonLbl = new Label(reason, game.skin, "font-label", Color.LIGHT_GRAY);
                reasonLbl.setFontScale(0.68f);

                row.add(playerLbl).left().row();
                row.add(reasonLbl).left().padLeft(16);

                row.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                row.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showPlayerCardModal(p);
                    }
                });

                box.add(row).growX().left().row();
            }
        }

        return box;
    }

    private Table buildRoundMatchesTable() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.DARK_GOLD));
        panel.pad(10);

        int roundNum = (game.league != null) ? game.league.getCurrentRound() : 12;
        Label title = new Label("JOGOS DA RODADA " + roundNum, game.skin, "font-bold", StyleFactory.GOLD);
        title.setFontScale(0.85f);
        panel.add(title).center().padBottom(10).row();

        Table list = new Table();
        list.top().defaults().growX().padBottom(6);

        List<Match> roundMatches = new ArrayList<>();
        if (game.league != null) {
            roundMatches = game.league.getCurrentRoundMatches();
        }

        if (roundMatches.isEmpty()) {
            roundMatches.add(match);
        }

        for (Match m : roundMatches) {
            boolean isUserMatch = (m == match);
            int[] odds = calculateAdvancedOdds(m.getHomeTeam(), m.getAwayTeam());

            Table outerRow = new Table();
            Color bgRow = isUserMatch ? StyleFactory.WINE_RED : StyleFactory.METAL_DARK;
            Color borderRow = isUserMatch ? StyleFactory.GOLD : Color.CLEAR;
            outerRow.background(StyleFactory.createRoundedPanel(bgRow, borderRow));
            outerRow.pad(6);

            Label timeLbl = new Label("15:00", game.skin, "font-bold", Color.GRAY);
            timeLbl.setFontScale(0.65f);
            outerRow.add(timeLbl).colspan(5).center().padBottom(2).row();

            Label homeName = new Label(m.getHomeTeam().getName(), game.skin, "font-label", Color.WHITE);
            homeName.setFontScale(0.72f);
            homeName.setEllipsis(true);

            Label homeOdds = new Label(odds[0] + "%", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
            homeOdds.setFontScale(0.72f);

            Label xLbl = new Label("───", game.skin, "font-bold", Color.GRAY);
            xLbl.setFontScale(0.72f);

            Label awayOdds = new Label(odds[2] + "%", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
            awayOdds.setFontScale(0.72f);

            Label awayName = new Label(m.getAwayTeam().getName(), game.skin, "font-label", Color.WHITE);
            awayName.setFontScale(0.72f);
            awayName.setEllipsis(true);

            outerRow.add(homeName).width(160).left();
            outerRow.add(homeOdds).width(45).right();
            outerRow.add(xLbl).width(40).center();
            outerRow.add(awayOdds).width(45).left();
            outerRow.add(awayName).width(160).right();

            if (isUserMatch) {
                outerRow.row().padTop(4);
                Label badge = new Label("▶ VOCÊ ESTÁ AQUI", game.skin, "font-bold", StyleFactory.GOLD);
                badge.setFontScale(0.68f);
                outerRow.add(badge).colspan(5).center();
            }

            list.add(outerRow).row();
        }

        panel.add(list).growX();
        return panel;
    }

    private int[] calculateAdvancedOdds(Club home, Club away) {
        double homePower = calculateComprehensiveTeamPower(home, true);
        double awayPower = calculateComprehensiveTeamPower(away, false);

        double diff = homePower - awayPower;

        double rawHomeOdds = Math.max(1.15, 2.20 - (diff * 0.045));
        double rawAwayOdds = Math.max(1.15, 2.20 + (diff * 0.045));
        double rawDrawOdds = Math.max(2.80, 3.40 - (Math.abs(diff) * 0.015));

        double pHomeImp = (1.0 / rawHomeOdds) * 100.0;
        double pAwayImp = (1.0 / rawAwayOdds) * 100.0;
        double pDrawImp = (1.0 / rawDrawOdds) * 100.0;

        double totalMarket = pHomeImp + pAwayImp + pDrawImp;

        int homePct = (int) Math.round((pHomeImp / totalMarket) * 100.0);
        int drawPct = (int) Math.round((pDrawImp / totalMarket) * 100.0);
        int awayPct = 100 - homePct - drawPct;

        return new int[]{homePct, drawPct, awayPct};
    }

    private double calculateComprehensiveTeamPower(Club club, boolean isHome) {
        if (club == null || club.getSquad().isEmpty()) return 70.0;

        double squadPower = club.getSquad().stream()
            .mapToInt(Player::getOverall)
            .average().orElse(70.0);

        List<Player> starters = new ArrayList<>(club.getTacticsMap().values());
        double startersPower = starters.stream()
            .filter(p -> p != null && p.canPlay())
            .mapToInt(Player::getOverall)
            .average().orElse(squadPower);

        // --- CÁLCULO DE IMPACTO DA MORAL ---
        double moraleAvg = starters.stream()
            .filter(p -> p != null && p.canPlay())
            .mapToInt(Player::getMorale)
            .average().orElse(
                club.getSquad().stream().mapToInt(Player::getMorale).average().orElse(75.0)
            );
        // Transforma a escala 0-100 em multiplicador entre 0.85 (moral zero) e 1.15 (moral 100)
        double moraleImpact = 0.85 + (0.30 * (moraleAvg / 100.0));

        double fatigueAvg = starters.stream()
            .filter(p -> p != null)
            .mapToInt(Player::getFatigue)
            .average().orElse(100.0);
        double fatigueImpact = 0.75 + (0.25 * (fatigueAvg / 100.0));

        int absences = 0;
        for (Player p : club.getSquad()) {
            if (!p.canPlay()) absences++;
        }
        double absencePenalty = Math.max(0.85, 1.0 - (absences * 0.025));

        Player gk = starters.stream()
            .filter(p -> p != null && "GK".equalsIgnoreCase(p.getPosition()) && p.canPlay())
            .findFirst().orElse(null);
        double gkPower = gk != null ? gk.getOverall() : 65.0;

        TacticalModifiers mods = TacticalEngine.calculateModifiers(
            club.getTempo(), club.getMentalityValue(), club.getPassing(), club.getWidth(), club.getPressure()
        );
        double tacticPowerBonus = mods.attackMultiplier * 0.05;

        double homeBonus = isHome ? 1.08 : 1.00;

        // A força total passa a considerar a variável moraleImpact
        return ((startersPower * 0.50) + (squadPower * 0.25) + (gkPower * 0.25))
            * fatigueImpact * moraleImpact * absencePenalty * homeBonus * (1.0 + tacticPowerBonus);
    }

    private com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable getSolidDrawable(Color color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(texture));
    }

    private String getShortName(Club club) {
        if (club == null || club.getName() == null) return "TIME";
        String name = club.getName().trim();
        if (name.contains(" ")) {
            return name.split(" ")[0].toUpperCase();
        }
        return name.toUpperCase();
    }

    private Texture loadLogo(Club club) {
        if (club != null && club.getLogoPath() != null && Gdx.files.internal(club.getLogoPath()).exists()) {
            try {
                return new Texture(Gdx.files.internal(club.getLogoPath()));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private List<Character> getRecentForm(Club team) {
        List<Character> form = new ArrayList<>();
        if (game.league == null) {
            return List.of('V', 'V', 'E', 'V', 'D');
        }

        List<Match> teamMatches = new ArrayList<>();
        if (game.league != null && game.league.getSchedule() != null) {
            for (Match m : game.league.getSchedule()) {
                if (m.isPlayed() && (m.getHomeTeam() == team || m.getAwayTeam() == team)) {
                    teamMatches.add(m);
                }
            }
        }

        int start = Math.max(0, teamMatches.size() - 5);
        for (int i = start; i < teamMatches.size(); i++) {
            Match m = teamMatches.get(i);
            int homeGoals = m.getHomeGoals();
            int awayGoals = m.getAwayGoals();

            if (m.getHomeTeam().equals(team)) {
                if (homeGoals > awayGoals) form.add('V');
                else if (homeGoals == awayGoals) form.add('E');
                else form.add('D');
            } else {
                if (awayGoals > homeGoals) form.add('V');
                else if (awayGoals == homeGoals) form.add('E');
                else form.add('D');
            }
        }

        while (form.size() < 5) {
            form.add(0, '-');
        }

        return form;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (homeLogoTexture != null) homeLogoTexture.dispose();
        if (awayLogoTexture != null) awayLogoTexture.dispose();
    }
}
