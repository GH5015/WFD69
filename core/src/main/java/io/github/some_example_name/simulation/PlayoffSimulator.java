package io.github.some_example_name.simulation;

import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.Match;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PlayoffSimulator {
    private MatchEngine matchEngine;
    private League league; // Referência à liga para adicionar jogos
    private Calendar playoffDate;

    public PlayoffSimulator(MatchEngine engine, League league) {
        this.matchEngine = engine;
        this.league = league;
    }

    public void startPlayoffs() {
        List<Club> westTop6 = league.getFullStandings("Ocidental").stream()
                .map(r -> r.club).limit(6).collect(Collectors.toList());
        List<Club> eastTop2 = league.getFullStandings("Oriental").stream()
                .map(r -> r.club).limit(2).collect(Collectors.toList());

        // Resetar o calendário da liga para adicionar apenas os jogos dos playoffs
        league.setSchedule(new ArrayList<>());

        playoffDate = Calendar.getInstance();
        playoffDate.set(league.getCurrentSeason(), Calendar.DECEMBER, 1, 16, 0); // Começa em Dezembro

        // Chaveamento
        List<Club> seeds = new ArrayList<>();
        seeds.add(westTop6.get(0)); // 1
        seeds.add(eastTop2.get(0)); // 2 (Oriental)
        seeds.add(westTop6.get(1)); // 3
        seeds.add(westTop6.get(2)); // 4
        seeds.add(westTop6.get(3)); // 5
        seeds.add(westTop6.get(4)); // 6
        seeds.add(eastTop2.get(1)); // 7 (Oriental)
        seeds.add(westTop6.get(5)); // 8

        // Quartas de Final
        addSeriesToSchedule("QUARTAS", seeds.get(0), seeds.get(7));
        addSeriesToSchedule("QUARTAS", seeds.get(3), seeds.get(4));
        addSeriesToSchedule("QUARTAS", seeds.get(1), seeds.get(6));
        addSeriesToSchedule("QUARTAS", seeds.get(2), seeds.get(5));
    }

    private void addSeriesToSchedule(String stageName, Club club1, Club club2) {
        // Jogo 1: Mando do pior classificado
        Match m1 = new Match(club2, club1);
        m1.setStage(stageName);
        m1.setDate(playoffDate.getTime());
        league.getSchedule().add(m1);
        playoffDate.add(Calendar.DATE, 7); // Próximo jogo uma semana depois

        // Jogo 2: Mando do melhor classificado
        Match m2 = new Match(club1, club2);
        m2.setStage(stageName);
        m2.setDate(playoffDate.getTime());
        league.getSchedule().add(m2);
        playoffDate.add(Calendar.DATE, 7);

        // Jogo 3: Mando do melhor (se necessário)
        Match m3 = new Match(club1, club2);
        m3.setStage(stageName);
        m3.setDate(playoffDate.getTime());
        league.getSchedule().add(m3);
        playoffDate.add(Calendar.DATE, 7);
    }
}
