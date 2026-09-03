package io.github.some_example_name;

import io.github.some_example_name.model.*;
import io.github.some_example_name.simulation.ExpansionCareerSimulator;
import java.util.Calendar;
import java.util.concurrent.CancellationException;

public final class ExpansionCareerRegressionTest {
    public static void main(String[] args) {
        String name = args.length == 0 ? "Cairo Pharaohs" : args[0];
        LeagueExpansionService.Franchise franchise = LeagueExpansionService.forClub(name);
        ExpansionCareerSimulator.Result result = new ExpansionCareerSimulator().simulate(name,
            (year, phase, fraction) -> { if (!phase.startsWith("SIMULANDO")) System.out.println(year + " " + phase); }, () -> false);
        League league = result.league;
        require(league.getCurrentSeason() == franchise.year - 1, "Ano da off-season");
        require("OFFSEASON".equals(league.getCurrentStage()), "Etapa da entrada");
        Calendar date = Calendar.getInstance(); date.setTime(league.getCurrentDate());
        require(date.get(Calendar.MONTH) == Calendar.NOVEMBER && date.get(Calendar.DAY_OF_MONTH) == 1, "Início da off-season");
        require(league.getClubs().size() == LeagueExpansionService.projectedClubCount(franchise.year), "Quantidade de clubes");
        require(result.club.getSquad().isEmpty(), "Elenco antes do Expansion Draft");
        require(result.club.getFinance().getBalance() == franchise.balance, "Caixa inaugural intacto");
        require(LeagueExpansionService.isPending(league), "Expansion Draft pendente");
        require(!league.isDraftLotteryCompleted() && !league.isDraftFinalized(), "Lottery e Draft pendentes");
        require(result.club.getDraftPicks().stream().filter(p -> p.getYear() == franchise.year).count() == 2, "Duas picks inaugurais");
        require(result.draftClass.size() >= league.getClubs().size() * 2, "Classe inaugural suficiente");
        require(league.getNextMatch() == null, "Nenhuma partida pendente");
        require(league.getSchedule().stream().allMatch(Match::isPlayed), "Temporada anterior simulada");
        require(league.getSchedule().stream().noneMatch(m -> m.getHomeTeam() == result.club || m.getAwayTeam() == result.club), "Clube não jogou antes da estreia");
        require(!league.isWeeklyNewsPending() && !league.hasPendingRoundSummary(), "Sem popups históricos pendentes");
        require(league.getClubs().stream().noneMatch(Club::isUserControlled), "Liga simulada apenas por IA");
        require(league.getHistory().getSeasons().size() == franchise.year - 1969, "Histórico de todas as temporadas");
        for (int year = 1969; year < franchise.year; year++) {
            require(league.getHistory().findSeason(year) != null, "Memória da temporada " + year);
            if (year > 1969) require(league.getHistory().getFirstOverallPick(year) != null, "Pick #1 histórica " + year);
        }
        for (LeagueExpansionService.Franchise f : LeagueExpansionService.allFranchises()) {
            if (f.year < franchise.year) require(league.isExpansionDraftCompleted(f.year), "Expansão anterior " + f.year);
        }
        try {
            new ExpansionCareerSimulator().simulate(name, (y, p, f) -> {}, () -> true);
            throw new AssertionError("Cancelamento não respeitado");
        } catch (CancellationException expected) { }
        result.club.setUserControlled(true);
        LeagueExpansionService.runDraft(league, result.club, java.util.Collections.emptyList(),
            LeagueExpansionService.suggestedSelections(league, result.club));
        require(result.club.getSquad().size() == 20, "20 jogadores recebidos no Expansion Draft");
        require(!LeagueExpansionService.isPending(league), "Expansão concluída pelo usuário");
        result.freeAgency.enforceRosterLimitsForNewSeason();
        league.startNewSeason();
        new io.github.some_example_name.simulation.SeasonSimulator().createSchedule(league);
        require(league.getCurrentSeason() == franchise.year, "Estreia no ano escolhido");
        require(league.getNextMatchForClub(result.club) != null, "Clube incluído no calendário inaugural");
        require(result.club.getSquad().size() >= 23, "Elenco jogável na estreia");
        System.out.println("Carreira " + name + " pronta para " + franchise.year + ": OK");
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
