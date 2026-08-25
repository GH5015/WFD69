package io.github.some_example_name.simulation;

import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
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

        // 38 rodadas entre janeiro e 15 de setembro. Cinco intervalos de
        // seis dias evitam que a fase regular invada a janela dos playoffs.
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(league.getCurrentSeason(), Calendar.JANUARY, 2, 12, 0, 0);

        List<Match> fullSchedule = new ArrayList<>();
        List<Match> turn1 = new ArrayList<>();

        // Turno 1
        for (int round = 0; round < roundsPerTurn; round++) {
            Date roundDate = cal.getTime();

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

            advanceToNextRegularRound(cal, round, (roundsPerTurn * 2) - 1);
        }

        fullSchedule.addAll(turn1);

        // Turno 2 (Invertido)
        for (int round = 0; round < roundsPerTurn; round++) {
            Date roundDate = cal.getTime();
            int startIndex = round * (totalTeams / 2);
            for (int j = 0; j < totalTeams / 2; j++) {
                Match m1 = turn1.get(startIndex + j);
                Match m2 = new Match(m1.getAwayTeam(), m1.getHomeTeam());
                m2.setDate(roundDate);
                fullSchedule.add(m2);
            }
            advanceToNextRegularRound(cal, roundsPerTurn + round, (roundsPerTurn * 2) - 1);
        }

        league.setSchedule(fullSchedule);
    }

    private void advanceToNextRegularRound(Calendar calendar, int completedRound, int lastRound) {
        if (completedRound >= lastRound) return;
        boolean compressedWeek = completedRound == 6 || completedRound == 13
            || completedRound == 20 || completedRound == 27 || completedRound == 34;
        calendar.add(Calendar.DATE, compressedWeek ? 6 : 7);
    }

    public void processEndSeason(League league) {
        for (Club club : league.getClubs()) {
            for (Player p : club.getSquad()) {
                p.recover(30);
                p.setAge(p.getAge() + 1);
            }
        }
        league.nextSeason();
    }
}
