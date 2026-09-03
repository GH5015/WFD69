package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1972. */
public final class DraftClass1972 {

    private DraftClass1972() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Zico", "Brasil", Position.CAM, Position.CF, 19, 73, 82, 31, 68, 86, 98));
        players.add(createPlayer("Paulo Roberto Falcão", "Brasil", Position.CM, Position.CDM, 18, 66, 80, 70, 79, 74, 97));
        players.add(createPlayer("Gaetano Scirea", "Itália", Position.CB, Position.SW, 19, 37, 67, 76, 76, 60, 96));
        players.add(createPlayer("Ubaldo Fillol", "Argentina", Position.GK, null, 21, 12, 41, 84, 86, 17, 95));
        players.add(createPlayer("Roberto Dinamite", "Brasil", Position.ST, null, 18, 72, 55, 27, 78, 68, 95));
        players.add(createPlayer("José Reinaldo", "Brasil", Position.ST, null, 18, 67, 54, 25, 72, 73, 95));
        players.add(createPlayer("Władysław Żmuda", "Polônia", Position.CB, null, 18, 35, 59, 79, 84, 50, 94));
        players.add(createPlayer("Graeme Souness", "Escócia", Position.CM, Position.CDM, 19, 59, 72, 68, 86, 67, 94));
        players.add(createPlayer("Giancarlo Antognoni", "Itália", Position.CAM, null, 18, 65, 78, 37, 70, 79, 94));
        players.add(createPlayer("Júnior", "Brasil", Position.LB, Position.CM, 18, 52, 70, 66, 78, 73, 94));
        players.add(createPlayer("Sócrates", "Brasil", Position.CAM, Position.CM, 18, 58, 74, 42, 74, 72, 95));
        players.add(createPlayer("René Houseman", "Argentina", Position.RW, Position.LW, 18, 72, 67, 30, 79, 87, 94));
        players.add(createPlayer("Éder Aleixo", "Brasil", Position.LW, null, 18, 67, 62, 29, 80, 79, 93));
        players.add(createPlayer("Manfred Kaltz", "Alemanha Ocidental", Position.RB, null, 19, 49, 65, 69, 82, 64, 92));
        players.add(createPlayer("Dieter Müller", "Alemanha Ocidental", Position.ST, null, 18, 71, 52, 27, 81, 65, 92));
        players.add(createPlayer("Paulo Isidoro", "Brasil", Position.CM, Position.RW, 18, 60, 70, 48, 82, 77, 91));
        players.add(createPlayer("Wim Rijsbergen", "Holanda", Position.CB, Position.RB, 20, 39, 62, 78, 83, 55, 91));
        players.add(createPlayer("Antoni Szymanowski", "Polônia", Position.RB, null, 21, 47, 65, 75, 84, 63, 90));
        players.add(createPlayer("Ricardo Villa", "Argentina", Position.CM, Position.CAM, 19, 62, 74, 48, 75, 73, 90));
        players.add(createPlayer("Jorge Olguín", "Argentina", Position.RB, Position.CB, 20, 42, 61, 72, 80, 57, 90));
        players.add(createPlayer("César Cueto", "Peru", Position.CAM, Position.CM, 20, 66, 83, 37, 67, 82, 93));
        players.add(createPlayer("Guillermo La Rosa", "Peru", Position.ST, null, 20, 70, 56, 27, 82, 67, 89));
        players.add(createPlayer("Rubén Galván", "Argentina", Position.CM, Position.CDM, 20, 54, 69, 67, 81, 64, 89));
        players.add(createPlayer("Tarciso", "Brasil", Position.RW, Position.ST, 20, 72, 61, 35, 85, 75, 89));
        players.add(createPlayer("Jorge Mendonça", "Brasil", Position.CAM, Position.ST, 18, 67, 67, 29, 74, 75, 90));
        players.add(createPlayer("Romeu Cambalhota", "Brasil", Position.ST, Position.CF, 19, 68, 61, 28, 78, 72, 88));
        players.add(createPlayer("Hans Bongartz", "Alemanha Ocidental", Position.CAM, Position.CM, 20, 61, 72, 45, 73, 74, 88));
        players.add(createPlayer("Marek Kusto", "Polônia", Position.LW, Position.ST, 18, 64, 58, 31, 79, 73, 87));
        players.add(createPlayer("Ronald Worm", "Alemanha Ocidental", Position.ST, null, 18, 68, 54, 29, 81, 67, 87));
        players.add(createPlayer("Nílton Batata", "Brasil", Position.RW, Position.ST, 18, 67, 58, 29, 82, 78, 88));
        players.add(createPlayer("Miguel Oviedo", "Argentina", Position.CM, Position.CDM, 21, 51, 67, 64, 79, 63, 86));
        players.add(createPlayer("José Navarro", "Peru", Position.RB, null, 21, 44, 61, 71, 81, 59, 86));
        players.add(createPlayer("Mario Galindo", "Chile", Position.RB, null, 20, 45, 59, 71, 82, 59, 86));
        players.add(createPlayer("Juan Machuca", "Chile", Position.RB, null, 21, 42, 58, 72, 82, 57, 85));
        players.add(createPlayer("Christian Lopez", "França", Position.CB, null, 19, 36, 58, 72, 79, 52, 88));
        players.add(createPlayer("Franco Selvaggi", "Itália", Position.ST, null, 19, 66, 52, 27, 77, 65, 86));
        players.add(createPlayer("Jean-Louis Gasset", "França", Position.CM, null, 18, 51, 65, 52, 70, 66, 81));

        return players;
    }

    private static Player createPlayer(
        String name,
        String nationality,
        Position primaryPosition,
        Position secondaryPosition,
        int age,
        int attack,
        int passing,
        int defense,
        int physical,
        int dribbling,
        int potential
    ) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("ataque", attack);
        attributes.put("passe", passing);
        attributes.put("defesa", defense);
        attributes.put("fisico", physical);
        attributes.put("drible", dribbling);

        double salary = 8_000d + Math.max(0, potential - 78) * 800d;
        return new Player(
            name,
            nationality,
            primaryPosition,
            secondaryPosition,
            age,
            new TechnicalAttributes(attributes),
            potential,
            salary
        );
    }
}
