package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.LeagueExpansionService;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.PlayoffSeries;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.SeasonCalendar;
import io.github.some_example_name.utils.DayAdvanceTransition;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Painel exclusivo da intertemporada, sem HUD de partidas ou menu regular. */
public class OffSeasonScreen implements Screen {
    private enum OffseasonPhase {
        EXPANSION("1 NOV", "WFL EXPANSION", "Proteja seu elenco antes da entrada das novas franquias."),
        STAFF("1–5 NOV", "STAFF", "Contrate, renove ou substitua membros da comissão técnica."),
        SCOUTING("6–30 NOV", "SCOUTING", "Acompanhe a classe e construa seu Big Board."),
        LOTTERY("1 DEZ", "LOTERIA DO DRAFT", "Acompanhe a revelação da ordem oficial das escolhas."),
        WORKOUTS("2–19 DEZ", "WORKOUTS", "Finalize relatórios e organize seu Big Board."),
        DRAFT("20 DEZ", "WFL DRAFT", "Selecione os futuros jogadores da franquia."),
        FREE_AGENCY("21–25 DEZ", "FREE AGENCY", "Contrate jogadores livres e acompanhe propostas."),
        TRADES("26–31 DEZ", "TROCAS", "Negocie jogadores e escolhas após o Draft."),
        NEW_SEASON("1 JAN", "NOVA TEMPORADA", "A nova temporada está pronta para começar.");
        final String date, title, description;
        OffseasonPhase(String date, String title, String description) { this.date=date; this.title=title; this.description=description; }
    }
    private final Main game;
    private final Club club;
    private final Stage stage;
    private final Texture backgroundTexture;

    public OffSeasonScreen(Main game, Club club) {
        this.game = game;
        this.club = club;
        this.stage = new Stage(new ResponsiveViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("prancheta.png"));
    }

    @Override
    public void show() {
        if (LeagueExpansionService.isPending(game.league)) {
            LeagueExpansionService.prepare(game.league, game.league.getCurrentSeason() + 1);
        }
        Gdx.input.setInputProcessor(stage);
        refreshUI();
        IncomingTradeOfferDialog.showPending(stage, game, club);
    }

    private void refreshUI() {
        stage.clear();
        Stack root = new Stack();
        root.setFillParent(true);
        stage.addActor(root);

        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setFillParent(true);
        root.add(background);

        Table page = ScreenUI.createPage(true);
        page.add(ScreenUI.createHeader(
                game.skin,
                "OFF SEASON — " + (game.league.getCurrentSeason() + 1),
                club.getName().toUpperCase() + " • " + currentMonthLabel()
            )).growX().height(70f).padBottom(7f).row();
        OffseasonPhase phase = currentPhase();
        page.add(createPhaseTimeline(phase)).growX().height(104f).padBottom(8f).row();
        Table body = new Table();
        body.add(createCurrentPhasePanel(phase)).width(470f).growY().padRight(9f);
        body.add(createNextPhasePanel(phase)).width(365f).growY().padRight(9f);
        body.add(createFranchiseSituation()).width(330f).growY();
        page.add(body).growX().height(350f).padBottom(8f).row();
        page.add(createPhaseAgenda(phase)).growX().height(178f).padBottom(8f).row();
        page.add(createAdvancePhasePanel(phase)).growX().height(68f).row();

        root.add(page);
    }

    private Table createPhaseTimeline(OffseasonPhase active) {
        Table panel = ScreenUI.createPanel(); panel.pad(8f, 12f, 8f, 12f);
        for (OffseasonPhase phase : OffseasonPhase.values()) {
            if (phase == OffseasonPhase.EXPANSION && !LeagueExpansionService.isExpansionYear(game.league.getCurrentSeason() + 1)) continue;
            boolean current = phase == active;
            boolean complete = phase.ordinal() < active.ordinal();
            Table item = new Table();
            item.background(StyleFactory.createRoundedPanel(Color.valueOf("18201C"), current ? StyleFactory.GOLD : Color.valueOf("3C4A43")));
            item.pad(5f, 7f, 5f, 7f);
            item.add(ScreenUI.createSubtitle(game.skin, current ? "ATIVO" : complete ? "CONCLUÍDO" : "PRÓXIMO")).center().row();
            item.add(ScreenUI.createBoldValue(game.skin, phase.date, current ? StyleFactory.SOFT_YELLOW : Color.WHITE, Align.center)).center().padTop(2f).row();
            Label label = ScreenUI.createSubtitle(game.skin, phase.title); label.setAlignment(Align.center); label.setWrap(true); label.setFontScale(.52f);
            item.add(label).width(118f).center().padTop(2f);
            panel.add(item).growX().uniformX().height(80f).padRight(3f);
        }
        return panel;
    }

    private Table createCurrentPhasePanel(final OffseasonPhase phase) {
        Table panel = ScreenUI.createPanel(); panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "FASE ATUAL")).left().row();
        panel.add(ScreenUI.createBoldValue(game.skin, phase.title + " (" + phase.date + ")", StyleFactory.CREME_AGED, Align.left)).left().padTop(7f).row();
        Label description = ScreenUI.createSubtitle(game.skin,
            phase == OffseasonPhase.EXPANSION && club.getStartYear() == game.league.getCurrentSeason() + 1
                ? "Monte o primeiro elenco da sua franquia antes da estreia na WFL." : phase.description);
        description.setWrap(true);
        panel.add(description).width(410f).left().padTop(8f).row();
        Label rules = ScreenUI.createSubtitle(game.skin, phaseRules(phase)); rules.setWrap(true);
        panel.add(rules).width(410f).left().expandY().top().padTop(12f).row();
        TextButton action = ScreenUI.createPrimaryButton(game.skin, actionLabel(phase));
        boolean enabled = phase != OffseasonPhase.NEW_SEASON && phaseAvailable(phase);
        action.setDisabled(!enabled); action.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ if (phaseAvailable(phase)) openPhase(phase); }});
        panel.add(action).width(300f).height(45f).left().padTop(8f);
        return panel;
    }

    private Table createNextPhasePanel(OffseasonPhase current) {
        OffseasonPhase next = nextPhase(current); Table panel = ScreenUI.createPanel(); panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "PRÓXIMO EVENTO")).left().row();
        panel.add(ScreenUI.createBoldValue(game.skin, next.title, StyleFactory.SOFT_YELLOW, Align.left)).left().padTop(14f).row();
        panel.add(ScreenUI.createBoldValue(game.skin, next.date, StyleFactory.SOFT_YELLOW, Align.left)).left().padTop(4f).row();
        Label info = ScreenUI.createSubtitle(game.skin, next.description); info.setWrap(true);
        panel.add(info).width(315f).left().expandY().top().padTop(12f).row();
        panel.add(ScreenUI.createSubtitle(game.skin, current == OffseasonPhase.NEW_SEASON ? "Temporada pronta." : "Desbloqueia ao avançar para o próximo marco.")).left();
        return panel;
    }

    private Table createPhaseAgenda(OffseasonPhase active) {
        Table panel = ScreenUI.createPanel(); panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "AGENDA DA OFF SEASON")).colspan(LeagueExpansionService.isExpansionYear(game.league.getCurrentSeason() + 1) ? 9 : 8).left().padBottom(6f).row();
        for (OffseasonPhase phase : OffseasonPhase.values()) {
            if (phase == OffseasonPhase.EXPANSION && !LeagueExpansionService.isExpansionYear(game.league.getCurrentSeason() + 1)) continue;
            Table card = new Table(); boolean current=phase==active, complete=phase.ordinal()<active.ordinal();
            card.background(StyleFactory.createRoundedPanel(Color.valueOf("18201C"), current?ScreenUI.SUCCESS:Color.valueOf("3C4A43"))); card.pad(7f);
            card.add(ScreenUI.createSubtitle(game.skin, phase.date)).center().row();
            Label title=ScreenUI.createBoldValue(game.skin, phase.title, current?StyleFactory.SOFT_YELLOW:StyleFactory.CREME_AGED,Align.center); title.setWrap(true); title.setFontScale(.56f);
            card.add(title).width(125f).height(38f).center().row();
            card.add(ScreenUI.createSubtitle(game.skin,current?"ATIVO":complete?"CONCLUÍDO":"BLOQUEADO")).center().padTop(3f);
            panel.add(card).growX().uniformX().height(126f).padRight(4f);
        }
        return panel;
    }

    private Table createAdvancePhasePanel(final OffseasonPhase phase) {
        OffseasonPhase next=nextPhase(phase); Table panel=ScreenUI.createPanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "PRÓXIMO MARCO: " + next.date + " • " + next.title)).left().expandX();
        TextButton advance=ScreenUI.createPrimaryButton(game.skin, phase==OffseasonPhase.NEW_SEASON?"INICIAR NOVA TEMPORADA":"AVANÇAR PARA O PRÓXIMO EVENTO");
        advance.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ advanceToNextPhase(phase); }});
        panel.add(advance).width(390f).height(46f).right(); return panel;
    }

    private Table createOffSeasonFlow() {
        Table panel = ScreenUI.createPanel();
        panel.add(createFlowStep("PLAYOFFS", false)).growX().uniformX();
        panel.add(createFlowArrow()).width(42f);
        panel.add(createFlowStep("FINAL", false)).growX().uniformX();
        panel.add(createFlowArrow()).width(42f);
        panel.add(createFlowStep("OFF SEASON", true)).growX().uniformX();
        panel.add(createFlowArrow()).width(42f);
        panel.add(createFlowStep("NOVA TEMPORADA", false)).growX().uniformX();
        return panel;
    }

    private Label createFlowStep(String label, boolean current) {
        Label step = ScreenUI.createBoldValue(
            game.skin, label, current ? StyleFactory.SOFT_YELLOW : Color.valueOf("9BA39A"), Align.center
        );
        step.setAlignment(Align.center);
        return step;
    }

    private Label createFlowArrow() {
        Label arrow = new Label("→", game.skin, "font-title");
        arrow.setColor(StyleFactory.GOLD);
        arrow.setFontScale(0.55f);
        return arrow;
    }

    private Table createSeasonRecap() {
        Table panel = ScreenUI.createPanel();
        String champion = getChampionName();
        panel.add(ScreenUI.createSectionTitle(game.skin, "TEMPORADA ENCERRADA")).left().expandX();
        panel.add(ScreenUI.createBoldValue(game.skin, "CAMPEÃO: " + champion, StyleFactory.SOFT_YELLOW, Align.right)).right().row();
        panel.add(ScreenUI.createSubtitle(
                game.skin,
                "A temporada acabou. Agora é hora de definir o próximo ciclo da franquia."
            ))
            .colspan(2).left().padTop(5f);
        return panel;
    }

    private Table createEventTimeline() {
        Table panel = ScreenUI.createPanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "PRÓXIMOS EVENTOS")).colspan(3).left().padBottom(7f).row();
        panel.add(createEventCard("NOVEMBRO", "FREE AGENCY • STAFF • TROCAS", isAtOrAfter(Calendar.NOVEMBER)))
            .growX().uniformX().padRight(8f);
        panel.add(createEventCard("DEZEMBRO", "LOTERIA • SCOUTING • DRAFT", isAtOrAfter(Calendar.DECEMBER)))
            .growX().uniformX().padRight(8f);
        panel.add(createEventCard("JANEIRO", "NOVA TEMPORADA", isAtOrAfter(Calendar.JANUARY)))
            .growX().uniformX();
        return panel;
    }

    private Table createEventCard(String month, String detail, boolean unlocked) {
        Color accent = unlocked ? StyleFactory.GOLD : Color.valueOf("53665B");
        Table card = new Table();
        card.background(StyleFactory.createRoundedPanel(Color.valueOf("18201C"), accent));
        card.pad(4f, 10f, 4f, 10f);
        card.add(ScreenUI.createBoldValue(game.skin, month, unlocked ? StyleFactory.SOFT_YELLOW : Color.valueOf("AAB0A8"), Align.left)).left();
        card.add(ScreenUI.createSubtitle(game.skin, detail)).left().padLeft(14f).expandX();
        card.add(ScreenUI.createBoldValue(
            game.skin, unlocked ? "DESBLOQUEADO" : "BLOQUEADO", unlocked ? ScreenUI.SUCCESS : Color.valueOf("89918A"), Align.left
        )).right();
        return card;
    }

    private Table createFreeAgencyCallToAction() {
        boolean open = SeasonCalendar.isFreeAgencyOpen(game.league);
        Table panel = ScreenUI.createPanel();
        panel.add(ScreenUI.createSectionTitle(game.skin, "FREE AGENCY")).left().expandX();
        panel.add(ScreenUI.createSubtitle(
            game.skin,
            open ? "MERCADO ABERTO" : "DISPONÍVEL EM NOVEMBRO"
        )).right().padRight(12f);

        TextButton access = ScreenUI.createPrimaryButton(game.skin, open ? "ACESSAR FREE AGENCY" : "BLOQUEADO");
        access.setDisabled(!open);
        access.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (open) game.setScreen(new FreeAgencyScreen(game, club));
            }
        });
        panel.add(access).width(230f).height(42f).right();
        return panel;
    }

    private Table createOperations() {
        Table panel = ScreenUI.createPanel();
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "OPERAÇÕES DA FRANQUIA")).colspan(3).left().padBottom(9f).row();

        boolean november = isMonth(Calendar.NOVEMBER);
        boolean draftPhase = SeasonCalendar.isDraftOpen(game.league);
        boolean lotteryPhase = SeasonCalendar.isDraftLotteryOpen(game.league);

        panel.add(createOperationCard("STAFF", "Altere treinador, scout, preparador físico, médico e desenvolvimento.", "ENTRAR", november, () ->
            game.setScreen(new StaffScreen(game, club))
        )).grow().uniformX().padRight(8f).padBottom(8f);
        panel.add(createOperationCard("TROCAS", "Veja o mercado e negocie com outros clubes.", "ENTRAR", november, () ->
            game.setScreen(new TradeHubScreen(game, club))
        )).grow().uniformX().padRight(8f).padBottom(8f);
        panel.add(createOperationCard("FREE AGENCY", "Contrate jogadores e acompanhe propostas pendentes.", "ENTRAR", SeasonCalendar.isFreeAgencyOpen(game.league), () ->
            game.setScreen(new FreeAgencyScreen(game, club))
        )).grow().uniformX().padRight(8f).padBottom(8f);
        panel.row();

        boolean lotteryDone = game.league.isDraftLotteryCompleted();
        boolean draftReady = draftPhase && lotteryDone && !game.league.isDraftFinalized();
        panel.add(createOperationCard(game.league.isDraftFinalized() ? "DRAFT FINALIZADO" : draftReady ? "WFL DRAFT" : "DRAFT E SCOUTING", game.league.isDraftFinalized() ? "Rookies integrados e prospectos não escolhidos enviados à Free Agency." : draftReady ? "Escolha seus prospectos pela ordem oficial da lottery." : "Observe atletas e organize sua estratégia de draft.", game.league.isDraftFinalized() ? "CONCLUÍDO" : draftReady ? "ENTRAR NO DRAFT" : "ENTRAR", draftPhase && !game.league.isDraftFinalized(), () ->
            game.setScreen(draftReady ? new DraftScreen(game, club, game.draftScoutManager) : new DraftScoutingScreen(game, club, game.draftScoutManager))
        )).grow().uniformX().padRight(8f);
        panel.add(createOperationCard(lotteryDone ? "LOTERIA CONCLUÍDA" : "LOTERIA DO DRAFT", lotteryDone ? "A ordem oficial está definida; o Draft foi desbloqueado." : "Acompanhe a revelação da ordem oficial das escolhas.", lotteryDone ? "CONCLUÍDA" : "INICIAR", lotteryPhase && !lotteryDone, () ->
            game.setScreen(new DraftLotteryScreen(game, club))
        )).grow().uniformX().padRight(8f);
        panel.add(createOperationCard("SORTEIO DA SEDE", "Defina a cidade que receberá a próxima final da WFL.", "SORTEAR", draftPhase, () ->
            game.setScreen(new FinalHostDrawScreen(game, club))
        )).grow().uniformX();
        return panel;
    }

    private Table createOperationCard(String title, String description, String action, boolean unlocked, Runnable callback) {
        Table card = new Table();
        card.background(StyleFactory.createRoundedPanel(Color.valueOf("18201C"), unlocked ? Color.valueOf("53665B") : Color.valueOf("344139")));
        card.pad(10f, 12f, 9f, 12f);
        card.add(ScreenUI.createSectionTitle(game.skin, title)).left().padBottom(5f).row();
        Label text = ScreenUI.createSubtitle(game.skin, description);
        text.setWrap(true);
        card.add(text).growX().expandY().top().left().padBottom(6f).row();
        TextButton button = ScreenUI.createInteractiveButton(unlocked ? action : "BLOQUEADO", game.skin);
        button.getLabel().setFontScale(0.55f);
        button.setDisabled(!unlocked);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (unlocked) callback.run();
            }
        });
        card.add(button).width(130f).height(34f).left();
        return card;
    }

    private Table createFranchiseSummary() {
        ClubFinance finance = club.getFinance();
        Table panel = ScreenUI.createPanel();
        panel.add(ScreenUI.createStatusBox(game.skin, "SALDO", formatMoney(finance.getBalance()), ScreenUI.SUCCESS)).growX().uniformX().padRight(7f);
        panel.add(ScreenUI.createStatusBox(game.skin, "SALARY CAP", formatMoney(finance.getAnnualPayroll()) + " / " + formatMoney(finance.getSalaryCap()), StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(7f);
        panel.add(ScreenUI.createStatusBox(game.skin, "ELENCO", club.getSquad().size() + "/" + 26, StyleFactory.CREME_AGED)).growX().uniformX().padRight(7f);
        panel.add(ScreenUI.createStatusBox(game.skin, "PICKS", String.valueOf(club.getDraftPicks().size()), Color.valueOf("9DC8F0"))).growX().uniformX().padRight(7f);
        panel.add(ScreenUI.createStatusBox(game.skin, "REPUTAÇÃO", String.valueOf(club.getReputation()), StyleFactory.SOFT_YELLOW)).growX().uniformX();
        return panel;
    }

    private Table createFranchiseSituation() {
        ClubFinance finance = club.getFinance();
        int expiring = 0;
        java.util.List<io.github.some_example_name.model.StaffMember> expiredStaff = new java.util.ArrayList<>();
        for (Player player : club.getSquad()) {
            if (player.getRemainingContractYears(game.league.getCurrentSeason()) <= 1) expiring++;
        }
        for (io.github.some_example_name.model.StaffRole role : io.github.some_example_name.model.StaffRole.values()) {
            io.github.some_example_name.model.StaffMember member = club.getStaffMember(role);
            // Contratos que terminam nesta temporada já exigem decisão nesta
            // Off Season, antes do início do próximo calendário.
            if (member != null && member.getContractEndYear() <= game.league.getCurrentSeason()) expiredStaff.add(member);
        }

        String picks = "Sem picks";
        if (!club.getDraftPicks().isEmpty()) {
            StringBuilder list = new StringBuilder();
            int shown = 0;
            for (io.github.some_example_name.model.DraftPick pick : club.getDraftPicks()) {
                if (pick.getYear() != game.league.getCurrentSeason() + 1 || pick.getRound() != 1) continue;
                if (shown++ > 0) list.append(" • ");
                list.append("#").append(pick.getProjectedPosition());
                if (shown == 2) break;
            }
            if (list.length() > 0) picks = list.toString();
        }

        Table panel = ScreenUI.createPanel();
        panel.pad(10f, 11f, 10f, 11f);
        panel.top();
        panel.add(ScreenUI.createSectionTitle(game.skin, "SUA FRANQUIA")).left().padBottom(5f).row();
        addSituationRow(panel, "CAIXA", compactMoney(finance.getBalance()), ScreenUI.SUCCESS);
        addSituationRow(panel, "CAP", compactMoney(finance.getAnnualPayroll()) + " / " + compactMoney(finance.getSalaryCap()), StyleFactory.SOFT_YELLOW);
        addSituationRow(panel, "ELENCO", club.getSquad().size() + " / 26", StyleFactory.CREME_AGED);
        addSituationRow(panel, "CONTRATOS", String.valueOf(expiring), expiring > 0 ? ScreenUI.WARNING : ScreenUI.SUCCESS);
        addSituationRow(panel, "STAFF", String.valueOf(expiredStaff.size()), expiredStaff.isEmpty() ? ScreenUI.SUCCESS : ScreenUI.WARNING);
        if (club.getStartYear() == game.league.getCurrentSeason() + 1 && !game.league.isDraftLotteryCompleted()) {
            long firstRound = club.getDraftPicks().stream().filter(p -> p.getYear() == club.getStartYear() && p.getRound() == 1).count();
            long secondRound = club.getDraftPicks().stream().filter(p -> p.getYear() == club.getStartYear() && p.getRound() == 2).count();
            picks = "R1: " + firstRound + " / R2: " + secondRound;
        }
        addSituationRow(panel, "PICKS", picks, Color.valueOf("9DC8F0"));
        addSituationRow(panel, "SCOUTING", game.draftScoutManager.getActiveTargets().size() + " / " + game.draftScoutManager.getMaxScoutedPlayers(), StyleFactory.SOFT_YELLOW);
        addSituationRow(panel, "REPUTAÇÃO", String.valueOf(club.getReputation()), StyleFactory.SOFT_YELLOW);

        panel.add(ScreenUI.createSectionTitle(game.skin, "PENDÊNCIAS")).left().padTop(7f).padBottom(3f).row();
        if (expiring > 0) addPending(panel, expiring + " contratos expiram em breve", true);
        for (io.github.some_example_name.model.StaffMember member : expiredStaff) {
            addPending(panel, member.getName() + " • " + member.getRole().getLabel(), true);
        }
        if (club.getSquad().size() < 26) addPending(panel, (26 - club.getSquad().size()) + " vaga(s) disponível(is) no elenco", true);
        if (!game.draftScoutManager.isFull()) addPending(panel, "Scout com capacidade disponível", true);
        if (!expiredStaff.isEmpty()) {
            TextButton manage = ScreenUI.createInteractiveButton("GERENCIAR STAFF", game.skin);
            manage.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ game.setScreen(new StaffScreen(game, club)); }});
            panel.add(manage).colspan(2).width(170f).height(30f).center().padTop(3f).row();
        }
        return panel;
    }

    private void addSituationRow(Table panel, String label, String value, Color color) {
        Label name = ScreenUI.createSubtitle(game.skin, label);
        name.setFontScale(0.54f);
        Label amount = ScreenUI.createBoldValue(game.skin, value, color, Align.right);
        amount.setFontScale(0.58f);
        amount.setAlignment(Align.right);
        panel.add(name).width(102f).left().height(21f);
        panel.add(amount).width(168f).right().height(21f).row();
    }

    private void addPending(Table panel, String message, boolean active) {
        Label item = ScreenUI.createSubtitle(game.skin, (active ? "⚠ " : "✓ ") + message);
        item.setFontScale(0.53f);
        item.setColor(active ? ScreenUI.WARNING : Color.valueOf("879188"));
        item.setWrap(true);
        panel.add(item).colspan(2).growX().left().padBottom(2f).row();
    }

    private Table createNextEventPanel() {
        Table panel = ScreenUI.createPanel();
        String event;
        String time;
        if (SeasonCalendar.isFreeAgencyOpen(game.league)) {
            event = "FREE AGENCY DISPONÍVEL";
            time = "EVENTO DISPONÍVEL";
        } else if (SeasonCalendar.isDraftLotteryOpen(game.league)) {
            event = SeasonCalendar.isDraftOpen(game.league) ? "DRAFT DISPONÍVEL" : "DRAFT LOTTERY DISPONÍVEL";
            time = "EVENTO DISPONÍVEL";
        } else {
            event = "FREE AGENCY";
            time = daysUntil(Calendar.NOVEMBER, 1) + " dias";
        }
        panel.add(ScreenUI.createSectionTitle(game.skin, "PRÓXIMO EVENTO")).left().expandX();
        panel.add(ScreenUI.createBoldValue(game.skin, event, StyleFactory.SOFT_YELLOW, Align.center)).center().padRight(18f);
        panel.add(ScreenUI.createSubtitle(game.skin, time)).right();
        return panel;
    }

    private TextButton createAdvanceButton() {
        TextButton button = ScreenUI.createPrimaryButton(game.skin, "AVANÇAR DIA • " + currentMonthLabel());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DayAdvanceTransition.play(stage, game, 1, new Runnable() {
                    @Override public void run() {
                        CareerOverlay.advanceOneDay(game, club);
                        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
                            refreshUI();
                            if (!IncomingTradeOfferDialog.showPending(stage, game, club)
                                && !FreeAgencyDecisionDialog.showPending(stage, game)) {
                                WflNewsDialog.showPending(stage, game);
                            }
                        } else {
                            game.setScreen(new ClubManagementScreen(game, club));
                        }
                    }
                });
            }
        });
        return button;
    }

    private OffseasonPhase currentPhase() {
        if (LeagueExpansionService.isPending(game.league)) return OffseasonPhase.EXPANSION;
        Date date = game.league.getCurrentDate();
        if (date == null) return OffseasonPhase.STAFF;
        Calendar c = Calendar.getInstance(); c.setTime(date); int month=c.get(Calendar.MONTH), day=c.get(Calendar.DAY_OF_MONTH);
        if (month == Calendar.NOVEMBER) return day <= 5 ? OffseasonPhase.STAFF : OffseasonPhase.SCOUTING;
        if (month == Calendar.DECEMBER) return day == 1 ? OffseasonPhase.LOTTERY : day <= 19 ? OffseasonPhase.WORKOUTS : day == 20 ? OffseasonPhase.DRAFT : day <= 25 ? OffseasonPhase.FREE_AGENCY : OffseasonPhase.TRADES;
        return OffseasonPhase.NEW_SEASON;
    }

    private OffseasonPhase nextPhase(OffseasonPhase phase) {
        int next = Math.min(OffseasonPhase.values().length - 1, phase.ordinal() + 1);
        return OffseasonPhase.values()[next];
    }

    private String actionLabel(OffseasonPhase phase) {
        switch (phase) {
            case EXPANSION: return club.getStartYear() == game.league.getCurrentSeason() + 1
                ? "FORMAR ELENCO / EXPANSION DRAFT" : "PROTEGER ELENCO / EXPANSION DRAFT";
            case STAFF: return "IR PARA STAFF";
            case TRADES: return "IR PARA TROCAS";
            case FREE_AGENCY: return "IR PARA FREE AGENCY";
            case LOTTERY: return "INICIAR LOTERIA";
            case SCOUTING: return "IR PARA SCOUTING";
            case WORKOUTS: return "IR PARA WORKOUTS";
            case DRAFT: return "IR PARA O DRAFT";
            default: return "CONCLUÍDO";
        }
    }

    private String phaseRules(OffseasonPhase phase) {
        switch (phase) {
            case EXPANSION: return (club.getStartYear() == game.league.getCurrentSeason() + 1
                ? "• Escolha os 20 jogadores desprotegidos da sua franquia."
                : "• Até 15 proteções, deixando 3 desprotegidos; no máximo 3 saídas por clube.")
                + "\n• As novas franquias formam elencos de 20 veteranos.\n• Cada uma recebe uma pick por rodada do Draft regular.\n• As vagas restantes são preenchidas na Free Agency e no Draft.";
            case STAFF: return "• Alterações de staff são exclusivas desta fase.\n• Revise contratos vencidos antes de avançar.";
            case TRADES: return "• Trocas pós-Draft de jogadores e picks estão abertas.\n• Use a Central para avaliar o interesse dos clubes.";
            case FREE_AGENCY: return "• Mercado geral de agentes livres aberto após o Draft.\n• Propostas são avaliadas diariamente.";
            case LOTTERY: return "• A ordem oficial do Draft será definida uma única vez.";
            case SCOUTING: return "• Finalize observações e prepare sua lista de favoritos.";
            case WORKOUTS: return "• Compare os prospectos e defina sua estratégia para o Draft.";
            case DRAFT: return "• Contratos rookie são automáticos após cada escolha.";
            default: return "• A agenda da Off Season foi concluída.";
        }
    }

    private boolean phaseAvailable(OffseasonPhase phase) {
        if (phase == OffseasonPhase.LOTTERY) return !game.league.isDraftLotteryCompleted();
        if (phase == OffseasonPhase.DRAFT) return game.league.isDraftLotteryCompleted() && !game.league.isDraftFinalized();
        return true;
    }

    private void openPhase(OffseasonPhase phase) {
        switch (phase) {
            case EXPANSION: ExpansionDraftDialog.show(stage, game, club, this::refreshUI); break;
            case STAFF: game.setScreen(new StaffScreen(game, club)); break;
            case TRADES: game.setScreen(new TradeHubScreen(game, club)); break;
            case FREE_AGENCY: game.setScreen(new FreeAgencyScreen(game, club)); break;
            case LOTTERY: game.setScreen(new DraftLotteryScreen(game, club)); break;
            case SCOUTING: game.setScreen(new DraftScoutingScreen(game, club, game.draftScoutManager)); break;
            case WORKOUTS: game.setScreen(new DraftScoutingScreen(game, club, game.draftScoutManager)); break;
            case DRAFT: game.setScreen(new DraftScreen(game, club, game.draftScoutManager)); break;
            default: break;
        }
    }

    /** Avanço por marco: aplica todas as atividades diárias até o início da próxima fase. */
    private void advanceToNextPhase(OffseasonPhase phase) {
        if (LeagueExpansionService.isPending(game.league)) {
            openPhase(OffseasonPhase.EXPANSION);
            return;
        }
        if (phase == OffseasonPhase.LOTTERY && !game.league.isDraftLotteryCompleted()) {
            Dialog dialog = new Dialog("LOTERIA OBRIGATÓRIA", game.skin) {
                @Override protected void result(Object object) {
                    if (Boolean.TRUE.equals(object)) game.setScreen(new DraftLotteryScreen(game, club));
                }
            };
            dialog.text("A ordem oficial do Draft precisa ser sorteada antes de avançar para os workouts.");
            dialog.button("IR PARA LOTERIA", true); dialog.button("VOLTAR", false);
            dialog.show(stage);
            return;
        }
        Calendar target = Calendar.getInstance(); target.clear();
        int year = game.league.getCurrentSeason();
        switch (phase) {
            case STAFF: target.set(year, Calendar.NOVEMBER, 6, 12, 0, 0); break;
            case SCOUTING: target.set(year, Calendar.DECEMBER, 1, 12, 0, 0); break;
            case LOTTERY: target.set(year, Calendar.DECEMBER, 2, 12, 0, 0); break;
            case WORKOUTS: target.set(year, Calendar.DECEMBER, 20, 12, 0, 0); break;
            case DRAFT: target.set(year, Calendar.DECEMBER, 21, 12, 0, 0); break;
            case FREE_AGENCY: target.set(year, Calendar.DECEMBER, 26, 12, 0, 0); break;
            case TRADES: target.set(year + 1, Calendar.JANUARY, 1, 12, 0, 0); break;
            default: target.set(year + 1, Calendar.JANUARY, 1, 12, 0, 0); break;
        }
        while ("OFFSEASON".equals(game.league.getCurrentStage()) && game.league.getCurrentDate() != null && game.league.getCurrentDate().before(target.getTime())) {
            CareerOverlay.advanceOneDay(game, club);
        }
        if ("OFFSEASON".equals(game.league.getCurrentStage())) {
            refreshUI();
            if (!IncomingTradeOfferDialog.showPending(stage, game, club)
                && !FreeAgencyDecisionDialog.showPending(stage, game)) {
                WflNewsDialog.showPending(stage, game);
            }
        } else {
            game.setScreen(new ClubManagementScreen(game, club));
        }
    }

    private void showFinalSummary() {
        Dialog dialog = new Dialog("FINAL DA WFL", game.skin);
        dialog.text("Campeão da temporada " + game.league.getCurrentSeason() + ": " + getChampionName());
        dialog.button("OK");
        dialog.show(stage);
    }

    private String getChampionName() {
        for (PlayoffSeries series : game.league.getPlayoffSeries()) {
            if ("F".equals(series.getId()) && series.getWinner() != null) return series.getWinner().getName();
        }
        return "A definir";
    }

    private boolean isMonth(int month) {
        Date date = game.league.getCurrentDate();
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == month;
    }

    private boolean isAtOrAfter(int month) {
        Date date = game.league.getCurrentDate();
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int current = calendar.get(Calendar.MONTH);
        if (month == Calendar.NOVEMBER) return current == Calendar.NOVEMBER || current == Calendar.DECEMBER || current == Calendar.JANUARY;
        if (month == Calendar.DECEMBER) return current == Calendar.DECEMBER || current == Calendar.JANUARY;
        return current == Calendar.JANUARY;
    }

    private String currentMonthLabel() {
        Date date = game.league.getCurrentDate();
        return date == null ? "OFF SEASON" : new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(date).toUpperCase();
    }

    private int daysUntil(int month, int day) {
        Date date = game.league.getCurrentDate();
        if (date == null) return 0;
        Calendar now = Calendar.getInstance(); now.setTime(date);
        Calendar target = Calendar.getInstance(); target.clear(); target.set(now.get(Calendar.YEAR), month, day, 12, 0, 0);
        if (target.before(now)) target.add(Calendar.YEAR, 1);
        return Math.max(0, (int) ((target.getTimeInMillis() - now.getTimeInMillis()) / 86_400_000L));
    }

    private String formatMoney(long amount) {
        if (Math.abs(amount) >= 1_000_000L) return String.format(Locale.US, "WFL$ %.1fM", amount / 1_000_000d);
        return "WFL$ " + String.format(Locale.US, "%,d", amount);
    }

    private String compactMoney(long amount) {
        if (Math.abs(amount) >= 1_000_000L) return String.format(Locale.US, "W$%.1fM", amount / 1_000_000d);
        return String.format(Locale.US, "W$%.0fK", amount / 1_000d);
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0f, 0f, 0f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { stage.dispose(); backgroundTexture.dispose(); }
}
