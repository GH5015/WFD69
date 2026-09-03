package io.github.some_example_name.simulation;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.engine.DevelopmentEngine;
import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;

/** Simula uma liga independente; só publica o resultado ao concluir a preparação. */
public final class ExpansionCareerSimulator {
    public interface Progress { void update(int season, String phase, float fraction); }

    public static final class Result {
        public final GameDatabase database;
        public final League league;
        public final FreeAgencyService freeAgency;
        public final Club club;
        public final List<Player> draftClass;
        Result(GameDatabase database, League league, FreeAgencyService freeAgency, Club club, List<Player> draftClass) {
            this.database = database; this.league = league; this.freeAgency = freeAgency;
            this.club = club; this.draftClass = draftClass;
        }
    }

    public Result simulate(String clubName, Progress progress, BooleanSupplier cancelled) {
        LeagueExpansionService.Franchise franchise = LeagueExpansionService.forClub(clubName);
        if (franchise == null) throw new IllegalArgumentException("Franquia de expansão desconhecida: " + clubName);
        checkCancelled(cancelled);
        GameDatabase database = new GameDatabase();
        League league = new League("Liga Mundial", 1969);
        database.getClubs().forEach(league::addClub);
        database.applyInitialContractsAndBindClubs(1969);
        for (Club club : league.getClubs()) { club.setUserControlled(false); club.beginBoardSeason(1969); }
        FreeAgencyService freeAgency = new FreeAgencyService(league);
        freeAgency.enforceRosterLimitsForNewSeason();
        DraftOrderService.initializeDraftPicks(league, 1970);
        SeasonSimulator seasons = new SeasonSimulator();
        seasons.createSchedule(league);
        MatchEngine matches = new MatchEngine(league);
        DevelopmentEngine development = new DevelopmentEngine();

        while (league.getCurrentSeason() < franchise.year) {
            int year = league.getCurrentSeason();
            report(progress, franchise, year, "TEMPORADA REGULAR E PLAYOFFS", 0);
            Match match;
            int played = 0;
            while ((match = league.getNextMatch()) != null) {
                checkCancelled(cancelled);
                advanceUntil(league, freeAgency, development, match.getDate(), cancelled);
                matches.simulate(match);
                league.advanceMatch(); // Também cria e avança a chave dos playoffs.
                if (++played % 20 == 0) report(progress, franchise, year, "SIMULANDO PARTIDAS • " + played,
                    Math.min(.85f, played / (float) Math.max(1, league.getSchedule().size()) * .85f));
            }
            if (!"PLAYOFFS".equals(league.getCurrentStage()))
                throw new IllegalStateException("A temporada não concluiu os playoffs: " + year);
            league.beginOffseason(); // Histórico, prêmios, envelhecimento, aposentadorias e expansão.
            freeAgency.releaseExpiredContractsAtOffseasonStart();
            if (year + 1 == franchise.year) {
                checkCancelled(cancelled);
                Club chosen = league.getClubs().stream().filter(c -> clubName.equals(c.getName())).findFirst().get();
                league.markWeeklyNewsDisplayed();
                while (league.hasPendingRoundSummary()) league.consumePendingRoundSummaryDate();
                report(progress, franchise, year, "OFF-SEASON PRONTA", 1);
                return new Result(database, league, freeAgency, chosen, DraftClassRepository.getClassForYear(franchise.year));
            }

            report(progress, franchise, year, "OFF-SEASON • EXPANSÃO, DRAFT E MERCADO", .88f);
            if (LeagueExpansionService.isPending(league)) LeagueExpansionService.runDraft(league, null, Collections.emptyList());
            advanceUntil(league, freeAgency, development, date(year, Calendar.DECEMBER, 1), cancelled);
            league.runDraftLottery();
            advanceUntil(league, freeAgency, development, date(year, Calendar.DECEMBER, 20), cancelled);
            runAiDraft(league, freeAgency, cancelled);
            advanceUntil(league, freeAgency, development, date(year + 1, Calendar.JANUARY, 1), cancelled);
            freeAgency.enforceRosterLimitsForNewSeason();
            league.startNewSeason();
            seasons.createSchedule(league);
        }
        throw new IllegalStateException("Não foi possível preparar a expansão.");
    }

    private void runAiDraft(League league, FreeAgencyService freeAgency, BooleanSupplier cancelled) {
        List<Player> available = new ArrayList<>(DraftClassRepository.getClassForYear(league.getCurrentSeason() + 1));
        for (DraftPick pick : DraftOrderService.getCurrentDraftOrder(league, league.getCurrentSeason() + 1)) {
            checkCancelled(cancelled);
            if (available.isEmpty()) break;
            Player chosen = available.stream().max(Comparator.comparingInt(p -> draftScore(pick.getCurrentOwner(), p))).get();
            league.recordDraftSelection(pick, chosen);
            available.remove(chosen);
        }
        freeAgency.addUndraftedFreeAgents(available);
        league.finalizeDraft();
    }

    private int draftScore(Club club, Player player) {
        long samePosition = club.getSquad().stream().filter(p -> p.getPrimaryPosition() == player.getPrimaryPosition()).count();
        return player.getOverall() * 3 + player.getPotential() + Math.max(1, 5 - (int) samePosition) * 5;
    }

    private void advanceUntil(League league, FreeAgencyService market, DevelopmentEngine development,
                              Date target, BooleanSupplier cancelled) {
        while (league.getCurrentDate().before(target)) {
            checkCancelled(cancelled);
            Calendar previous = Calendar.getInstance(); previous.setTime(league.getCurrentDate());
            league.advanceDateOneDay();
            Calendar current = Calendar.getInstance(); current.setTime(league.getCurrentDate());
            boolean newWeek = previous.get(Calendar.WEEK_OF_YEAR) != current.get(Calendar.WEEK_OF_YEAR);
            for (Club club : league.getClubs()) {
                if (previous.get(Calendar.MONTH) != current.get(Calendar.MONTH)) club.getFinance().applyMonthlyBalance();
                for (Player player : club.getSquad()) {
                    player.recover(1, StaffImpact.fitnessRecoveryMultiplier(club.getStaffLevel(StaffRole.FITNESS_COACH)));
                    player.recoverFromInjury(1 + (newWeek ? StaffImpact.medicalRecoveryBonus(club.getStaffLevel(StaffRole.DOCTOR)) : 0));
                    player.advanceTradeEligibilityDay();
                }
                club.advanceStadiumRenovationDay();
            }
            if (newWeek) {
                development.updateWeekly(league);
                AiTradeService.processWeeklyTrade(league, null);
                league.generateWeeklyNewsIfNeeded();
                league.markWeeklyNewsDisplayed();
            }
            market.processAiFreeAgentSignings(null, league.getCurrentSeason());
        }
    }

    private static Date date(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(); calendar.clear(); calendar.set(year, month, day, 12, 0, 0);
        return calendar.getTime();
    }
    private static void checkCancelled(BooleanSupplier cancelled) {
        if (Thread.currentThread().isInterrupted() || cancelled.getAsBoolean()) throw new CancellationException();
    }
    private static void report(Progress progress, LeagueExpansionService.Franchise f, int year, String phase, float part) {
        progress.update(year, phase, Math.min(1, (year - 1969 + part) / (f.year - 1969f)));
    }
}
