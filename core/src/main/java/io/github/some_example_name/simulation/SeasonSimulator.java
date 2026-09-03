package io.github.some_example_name.simulation;

import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.model.StaffImpact;
import io.github.some_example_name.utils.NameGenerator;

import java.util.*;

public class SeasonSimulator {
    private MatchEngine matchEngine;
    private Random random = new Random();

    public SeasonSimulator() {
        this.matchEngine = new MatchEngine();
    }

    public void createSchedule(League league) {
        List<Club> clubs = new ArrayList<>(league.getClubs());
        int totalTeams = clubs.size();
        int roundsPerTurn = totalTeams - 1;

        List<Club> rotatingList = new ArrayList<>(clubs);
        Club fixedTeam = rotatingList.remove(totalTeams - 1);

        // De 38 a 58 rodadas dentro da mesma janela, sem invadir os playoffs.

        List<Match> fullSchedule = new ArrayList<>();
        List<Match> turn1 = new ArrayList<>();

        // Turno 1
        for (int round = 0; round < roundsPerTurn; round++) {
            Date roundDate = regularRoundDate(league.getCurrentSeason(), round, roundsPerTurn * 2);

            Match mFixo = (round % 2 == 0) ? new Match(fixedTeam, rotatingList.get(0)) : new Match(rotatingList.get(0), fixedTeam);
            mFixo.setDate(roundDate);
            turn1.add(mFixo);

            for (int i = 1; i < totalTeams / 2; i++) {
                Match m = new Match(rotatingList.get(i), rotatingList.get(totalTeams - 1 - i));
                m.setDate(roundDate);
                turn1.add(m);
            }

            Club last = rotatingList.remove(rotatingList.size() - 1);
            rotatingList.add(0, last);

        }

        fullSchedule.addAll(turn1);

        // Turno 2 (Invertido)
        for (int round = 0; round < roundsPerTurn; round++) {
            Date roundDate = regularRoundDate(league.getCurrentSeason(), roundsPerTurn + round, roundsPerTurn * 2);
            int startIndex = round * (totalTeams / 2);
            for (int j = 0; j < totalTeams / 2; j++) {
                Match m1 = turn1.get(startIndex + j);
                Match m2 = new Match(m1.getAwayTeam(), m1.getHomeTeam());
                m2.setDate(roundDate);
                fullSchedule.add(m2);
            }
        }

        league.setSchedule(fullSchedule);
    }

    private Date regularRoundDate(int year, int round, int totalRounds) {
        java.time.LocalDate start = java.time.LocalDate.of(year, 1, 2);
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, java.time.LocalDate.of(year, 9, 15));
        java.time.LocalDate day = start.plusDays(Math.round(round * days / (double) Math.max(1, totalRounds - 1)));
        return Date.from(day.atTime(12, 0).atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public void processEndSeason(League league) {
        for (Club club : league.getClubs()) {
            for (Player p : club.getSquad()) {
                p.recover(30, StaffImpact.fitnessRecoveryMultiplier(club.getStaffLevel(StaffRole.FITNESS_COACH)));
                p.recoverFromInjury(30 + StaffImpact.medicalRecoveryBonus(club.getStaffLevel(StaffRole.DOCTOR)) * 3);
                p.setAge(p.getAge() + 1);
            }
        }
        league.nextSeason();
    }
}
