package io.github.some_example_name;

import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.League;
import io.github.some_example_name.simulation.SeasonSimulator;
import io.github.some_example_name.simulation.PlayoffSimulator;
import io.github.some_example_name.engine.MatchEngine;

import java.util.List;
import java.util.stream.Collectors;

public class ConsoleSimulator {
    public static void main(String[] args) {
        System.out.println("Iniciando a LIGA MUNDIAL DE FUTEBOL - ERA 1969 (MODO CONSOLE)");

        GameDatabase database = new GameDatabase();
        League league = new League("Liga Mundial", 1969);

        for (Club club : database.getClubs()) {
            league.addClub(club);
        }

        MatchEngine engine = new MatchEngine(league);
        SeasonSimulator simulator = new SeasonSimulator();
        // Passando League no construtor
        PlayoffSimulator playoffSimulator = new PlayoffSimulator(engine, league);

        simulator.createSchedule(league);

        // Simular temporada regular
        while (league.getNextMatch() != null) {
            engine.simulate(league.getNextMatch());
            league.advanceMatch();
        }

        System.out.println("\nTemporada regular finalizada!");

        // Início dos Playoffs usando o novo método startPlayoffs
        playoffSimulator.startPlayoffs();

        // Simular jogos de playoff adicionados
        while (league.getNextMatch() != null) {
            engine.simulate(league.getNextMatch());
            league.advanceMatch();
        }

        System.out.println("\nFim da simulação console.");
    }
}
