package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.*;
import io.github.some_example_name.simulation.SeasonSimulator;
import java.util.*;

/** Regressão sem interface gráfica; executada por :core:expansionRegression. */
public final class ExpansionRegressionTest {
    public static void main(String[] args) {
        int[] years = {1969, 1974, 1978, 1982, 1986, 1990};
        int[] west = {15, 16, 16, 17, 17, 18};
        int[] east = {5, 6, 8, 9, 11, 12};
        int[] qualifiers = {8, 8, 10, 10, 12, 12};
        for (int i = 0; i < years.length; i++) {
            League league = fixture(years[i]);
            prepareThrough(league, years[i]);
            int count = west[i] + east[i];
            require(league.getClubs().size() == count, "Total " + years[i]);
            require(league.getFullStandings("Ocidental").size() == west[i], "Ocidente " + years[i]);
            require(league.getFullStandings("Oriental").size() == east[i], "Oriente " + years[i]);
            require(league.getPlayoffQualifierCount() == qualifiers[i], "Vagas " + years[i]);
            testPicks(league, years[i], count);
            require(DraftClassRepository.getClassForYear(years[i]).size() >= count * 2, "Classe insuficiente");
            testSeason(league, count, qualifiers[i]);
            System.out.println(years[i] + ": " + count + " clubes, " + count * 2 + " picks, " + qualifiers[i] + " classificados — OK");
        }
        for (int year : new int[]{1974, 1978, 1982, 1986, 1990}) testExpansionDraft(year);
        testAnnouncement();
        testOffseasonHook();
        System.out.println("Todas as regressões da expansão passaram.");
    }

    private static League fixture(int year) {
        League league = new League("WFL", year);
        for (int i = 0; i < 20; i++) {
            league.addClub(new Club(String.format("Fundador %02d", i), "Brasil", i < 15 ? "Ocidental" : "Oriental",
                80, 40_000_000, "Arena " + i, "santos.png"));
        }
        return league;
    }

    private static void prepareThrough(League league, int year) {
        for (int expansion : new int[]{1974, 1978, 1982, 1986, 1990}) {
            if (expansion <= year) LeagueExpansionService.prepare(league, expansion);
        }
    }

    private static void testPicks(League league, int year, int count) {
        DraftOrderService.initializeDraftPicks(league, year);
        List<DraftPick> picks = DraftOrderService.getCurrentDraftOrder(league, year);
        require(picks.size() == count * 2, "Quantidade de picks");
        for (int i = 0; i < picks.size(); i++) require(picks.get(i).getProjectedOverallPosition() == i + 1, "Numeração " + i);
        DraftPick traded = picks.get(0);
        Club original = traded.getCurrentOwner();
        Club receiver = league.getClubs().stream().filter(c -> c != original).findFirst().get();
        original.getDraftPicks().remove(traded);
        receiver.getDraftPicks().add(traded);
        traded.setCurrentOwner(receiver);
        DraftOrderService.initializeDraftPicks(league, year);
        require(DraftOrderService.getCurrentDraftOrder(league, year).size() == count * 2, "Pick negociada duplicou");
        require(traded.getCurrentOwner() == receiver, "Dono da pick alterado");
    }

    private static void testSeason(League league, int count, int qualifiers) {
        new SeasonSimulator().createSchedule(league);
        require(league.getSchedule().size() == count * (count - 1), "Total de partidas");
        Set<String> pairs = new HashSet<>();
        Map<Date, Set<Club>> daily = new HashMap<>();
        for (Match match : league.getSchedule()) {
            require(pairs.add(match.getHomeTeam().getName() + "/" + match.getAwayTeam().getName()), "Confronto repetido");
            Set<Club> day = daily.computeIfAbsent(match.getDate(), d -> new HashSet<>());
            require(day.add(match.getHomeTeam()) && day.add(match.getAwayTeam()), "Clube joga duas vezes no mesmo dia");
            Calendar date = Calendar.getInstance(); date.setTime(match.getDate());
            require(date.get(Calendar.MONTH) < Calendar.SEPTEMBER
                || date.get(Calendar.MONTH) == Calendar.SEPTEMBER && date.get(Calendar.DAY_OF_MONTH) <= 15, "Regular invadiu playoffs");
        }
        require(daily.size() == (count - 1) * 2, "Rodadas");
        while ("REGULAR".equals(league.getCurrentStage()) && league.getNextMatch() != null) {
            league.getNextMatch().setResult(1, 0);
            league.advanceMatch();
        }
        if (qualifiers > 8) {
            require("PLAY_IN".equals(league.getNextMatch().getStage()), "Play-In ausente");
            Club bye = league.getFullStandings("Ocidental").get(0).club;
            require(league.isClubStillInPlayoffs(bye), "Clube com bye tratado como eliminado");
        }
        int guard = 0;
        while (league.getNextMatch() != null && guard++ < 40) {
            league.getNextMatch().setResult(1, 0);
            league.advanceMatch();
        }
        require(guard < 40, "Chave não termina");
        Set<Club> participants = new HashSet<>();
        PlayoffSeries finalSeries = null;
        for (PlayoffSeries series : league.getPlayoffSeries()) {
            participants.add(series.getFirstSeed()); participants.add(series.getSecondSeed());
            require(series.isComplete(), "Série incompleta");
            if ("F".equals(series.getId())) finalSeries = series;
        }
        require(participants.size() == qualifiers, "Participantes dos playoffs");
        require(finalSeries != null && finalSeries.getWinner() != null, "Final sem vencedor");
        if (qualifiers > 8) require(!finalSeries.getFirstSeed().getConference().equals(finalSeries.getSecondSeed().getConference()), "Final deve ser Ocidente x Oriente");
        Calendar finalDate = Calendar.getInstance(); finalDate.setTime(league.getCurrentDate());
        require(finalDate.get(Calendar.MONTH) == Calendar.OCTOBER, "Final fora de outubro");
    }

    private static void testExpansionDraft(int year) {
        League league = fixture(year - 1);
        prepareThrough(league, year - 1);
        List<Club> incumbents = new ArrayList<>(league.getClubs());
        Map<Club, List<Player>> protections = new HashMap<>();
        for (Club club : incumbents) {
            for (int i = 0; i < 25; i++) {
                Player p = new Player(club.getName() + " atleta " + i, "Brasil", i % 8 == 0 ? Position.GK : Position.values()[i % Position.values().length],
                    null, 24, new TechnicalAttributes(60 + i, 65, 65, 70, 66, 68), 88, 10_000);
                p.renewContract(240_000, 4, year);
                p.transferTo(club);
            }
            protections.put(club, LeagueExpansionService.suggestedProtection(club));
        }
        Club user = incumbents.get(0);
        List<Player> manual = new ArrayList<>(user.getSquad().subList(0, 15));
        protections.put(user, manual);
        league.setCurrentStage("OFFSEASON");
        league.setCurrentDate(new GregorianCalendar(year - 1, Calendar.NOVEMBER, 1).getTime());
        Date before = league.getCurrentDate(); league.advanceDateOneDay();
        require(league.getCurrentDate().equals(before), "Expansão obrigatória ignorada");
        List<Club> newcomers = LeagueExpansionService.prepare(league, year);
        try {
            LeagueExpansionService.runDraft(league, user, Collections.emptyList());
            throw new AssertionError("Proteção inválida aceita");
        } catch (IllegalArgumentException expected) { }
        List<String> log = LeagueExpansionService.runDraft(league, user, manual);
        for (Club club : incumbents) {
            require(club.getSquad().size() >= 25 - LeagueExpansionService.MAX_LOSSES_PER_CLUB, "Limite de perdas");
            require(club.getSquad().containsAll(protections.get(club)), "Jogador protegido transferido");
        }
        for (Club club : newcomers) {
            require(club.getSquad().size() == 20, "Expansion roster " + club.getName());
            require(ClubProfile.forClub(club).founded == year, "Fundação incorreta");
            require(club.getFinance().getAnnualPayroll() <= club.getFinance().getHardCap(), "Hard cap");
        }
        require(!LeagueExpansionService.isPending(league), "Expansão não concluída");
        int size = league.getClubs().size();
        LeagueExpansionService.prepare(league, year);
        require(league.getClubs().size() == size, "Clubes duplicados");
        require(log.equals(LeagueExpansionService.runDraft(league, user, manual)), "Reexecutou draft");
        testPicks(league, year, size);
        require(league.runDraftLottery().size() == size, "Loteria não incluiu os novos clubes");
        List<DraftPick> regularPicks = DraftOrderService.getCurrentDraftOrder(league, year);
        for (int i = 0; i < regularPicks.size(); i++) require(regularPicks.get(i).getProjectedOverallPosition() == i + 1, "Ordem após loteria");
        List<Player> prospects = DraftClassRepository.getClassForYear(year);
        for (int index = 0; index < regularPicks.size(); index++) {
            league.recordDraftSelection(regularPicks.get(index), prospects.get(index));
        }
        require(league.getDraftSelections().size() == size * 2, "Draft regular não consumiu todas as picks");
        FreeAgencyService market = new FreeAgencyService(league);
        market.enforceRosterLimitsForNewSeason();
        for (Club c : league.getClubs()) require(c.getSquad().size() >= 23 && c.getSquad().size() <= 26, "Elenco não completado via mercado");
        league.advanceDateOneDay(); require(league.getCurrentDate().after(before), "Calendário continuou bloqueado");
        System.out.println("Expansion Draft " + year + ": proteção manual, limite de perdas e idempotência — OK");
    }

    private static void testAnnouncement() {
        League league = fixture(1973);
        league.setCurrentDate(new GregorianCalendar(1973, Calendar.JANUARY, 5).getTime());
        require(NewsGenerator.generateWeekly(league).stream().anyMatch(n -> n.getHeadline().contains("EXPANSÃO PARA 1974")), "Anúncio ausente");
        require(NewsGenerator.generateWeekly(league).stream().noneMatch(n -> n.getHeadline().contains("EXPANSÃO PARA 1974")), "Anúncio repetido");
    }

    private static void testOffseasonHook() {
        League league = fixture(1973);
        league.beginOffseason();
        require(league.getClubs().size() == 22 && LeagueExpansionService.isPending(league), "Integração offseason");
        league.beginOffseason();
        require(league.getClubs().size() == 22, "Reabertura da offseason duplicou clubes");
        League later = fixture(1977);
        prepareThrough(later, 1974);
        later.beginOffseason();
        require(later.getClubs().size() == 24 && later.getPlayoffQualifierCount() == 8, "Expansão alterou playoffs encerrados");
        require(later.getFullStandings(null).size() == 22, "Franquias futuras na tabela encerrada");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
