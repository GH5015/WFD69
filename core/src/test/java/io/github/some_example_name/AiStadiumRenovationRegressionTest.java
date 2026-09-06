package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class AiStadiumRenovationRegressionTest {
    public static void main(String[] args) {
        Club tokyo = club("Tokyo Rising Sun", 85, 28_000, 41_000_000L);
        Club visitor = club("Visitante", 75, 40_000, 20_000_000L);
        League league = leagueWithHomeCrowds(tokyo, visitor, 26_880, 4);

        require(Math.abs(AiStadiumRenovationService.calculateOccupancy(league, tokyo) - .96d) < .001d,
            "Ocupação da IA deveria ser 96%");
        require(AiStadiumRenovationService.choosePlan(league, tokyo) == StadiumRenovationPlan.STRUCTURE,
            "Tokyo deveria aprovar modernização estrutural");

        long previousBalance = tokyo.getFinance().getBalance();
        AiStadiumRenovationService.processMonthly(league);
        require(tokyo.isStadiumRenovationInProgress(), "IA não iniciou a reforma provável");
        require(tokyo.getStadiumRenovationTargetCapacity() == 40_000, "Capacidade-alvo incorreta");
        require(tokyo.getFinance().getBalance() == previousBalance - StadiumRenovationPlan.STRUCTURE.getCost(),
            "Custo da reforma não foi debitado");
        require(AiStadiumRenovationService.choosePlan(league, tokyo) == null,
            "IA tentou aprovar outra reforma na mesma temporada");

        Club empty = club("Arquibancadas Vazias", 90, 28_000, 60_000_000L);
        League lowOccupancy = leagueWithHomeCrowds(empty, visitor, 16_000, 4);
        require(AiStadiumRenovationService.choosePlan(lowOccupancy, empty) == null,
            "Baixa ocupação não deveria gerar reforma");

        Club user = club("Clube do Usuário", 90, 28_000, 60_000_000L);
        user.setUserControlled(true);
        League userLeague = leagueWithHomeCrowds(user, visitor, 28_000, 4);
        require(AiStadiumRenovationService.choosePlan(userLeague, user) == null,
            "Serviço da IA não pode reformar o estádio do usuário");

        System.out.println("AI stadium renovation: occupancy, cash, reputation, capacity and phase decision OK.");
    }

    private static League leagueWithHomeCrowds(Club home, Club away, int attendance, int count) {
        League league = new League("WFL", 1974);
        league.addClub(home);
        league.addClub(away);
        List<Match> schedule = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            Match match = new Match(home, away);
            Calendar date = Calendar.getInstance();
            date.clear();
            date.set(1974, Calendar.MARCH, 2 + index * 7, 12, 0, 0);
            match.setDate(date.getTime());
            match.setAttendance(attendance, attendance);
            match.setPlayed(true);
            schedule.add(match);
        }
        league.setSchedule(schedule);
        return league;
    }

    private static Club club(String name, int reputation, int capacity, long balance) {
        Club club = new Club(name, "País", "Oriental", reputation, balance, "Arena", "logo.png");
        club.setStadiumCapacity(capacity);
        club.getFinance().setBalance(balance);
        return club;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
