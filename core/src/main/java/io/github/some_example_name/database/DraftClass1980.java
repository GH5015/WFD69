package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1980. */
public final class DraftClass1980 {

    private DraftClass1980() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Ruud Gullit", "Holanda", Position.CAM, Position.CF, 18, 70, 75, 57, 88, 82, 98));
        players.add(createPlayer("Franco Baresi", "Itália", Position.CB, Position.SW, 20, 37, 69, 82, 80, 62, 98));
        players.add(createPlayer("Frank Rijkaard", "Holanda", Position.CDM, Position.CB, 18, 51, 70, 74, 84, 66, 97));
        players.add(createPlayer("Oscar Ruggeri", "Argentina", Position.CB, null, 18, 37, 57, 75, 87, 51, 95));
        players.add(createPlayer("Jorge Burruchaga", "Argentina", Position.CAM, Position.CF, 18, 67, 73, 38, 77, 78, 95));
        players.add(createPlayer("Walter Zenga", "Itália", Position.GK, null, 20, 12, 38, 78, 84, 17, 95));
        players.add(createPlayer("Pietro Vierchowod", "Itália", Position.CB, null, 21, 34, 55, 77, 90, 48, 95));
        players.add(createPlayer("Luis Fernández", "França", Position.CM, Position.CDM, 21, 53, 70, 70, 85, 66, 94));
        players.add(createPlayer("Mauro Galvão", "Brasil", Position.CB, Position.SW, 19, 37, 63, 76, 82, 57, 94));
        players.add(createPlayer("Ricardo Rocha", "Brasil", Position.CB, null, 18, 35, 57, 73, 84, 51, 93));
        players.add(createPlayer("Sergio Batista", "Argentina", Position.CDM, Position.CM, 18, 47, 66, 69, 83, 61, 93));
        players.add(createPlayer("Mozer", "Brasil", Position.CB, null, 20, 34, 56, 73, 87, 49, 93));
        players.add(createPlayer("Leandro", "Brasil", Position.RB, Position.CB, 21, 53, 68, 72, 83, 69, 94));
        players.add(createPlayer("Luigi De Agostini", "Itália", Position.LB, Position.LM, 19, 52, 66, 68, 82, 67, 92));
        players.add(createPlayer("Aldo Serena", "Itália", Position.ST, null, 20, 72, 52, 30, 87, 63, 92));
        players.add(createPlayer("Alemão", "Brasil", Position.CM, Position.CDM, 19, 53, 70, 67, 83, 66, 92));
        players.add(createPlayer("Andrea Carnevale", "Itália", Position.ST, null, 19, 70, 53, 28, 83, 65, 91));
        players.add(createPlayer("José Touré", "França", Position.CF, Position.RW, 19, 67, 66, 32, 82, 78, 91));
        players.add(createPlayer("Juan Barbas", "Argentina", Position.CM, Position.CAM, 21, 59, 72, 52, 78, 72, 91));
        players.add(createPlayer("Jean-Louis Zanon", "França", Position.CM, null, 20, 52, 68, 61, 79, 65, 87));
        players.add(createPlayer("Philippe Anziani", "França", Position.ST, null, 19, 67, 54, 28, 80, 64, 86));

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
