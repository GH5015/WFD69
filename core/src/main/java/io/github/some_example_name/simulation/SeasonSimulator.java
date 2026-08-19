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

        Calendar cal = Calendar.getInstance();
        cal.set(league.getCurrentSeason(), Calendar.MARCH, 1);
        // Ajustar para o primeiro domingo
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) cal.add(Calendar.DATE, 1);

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

            // Avança para o próximo domingo ou quarta (distribuindo 38 rodadas em 36 semanas)
            if (round % 4 == 0) cal.add(Calendar.DATE, 3); // Quarta
            else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) cal.add(Calendar.DATE, 4); // Domingo
            else cal.add(Calendar.DATE, 7); // Próximo Domingo
        }

        fullSchedule.addAll(turn1);

        // Intervalo de meio de ano
        cal.add(Calendar.DATE, 14);

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
            if (round % 4 == 0) cal.add(Calendar.DATE, 3);
            else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) cal.add(Calendar.DATE, 4);
            else cal.add(Calendar.DATE, 7);
        }

        league.setSchedule(fullSchedule);
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
