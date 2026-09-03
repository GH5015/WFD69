package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1974. */
public final class DraftClass1974 {

    private DraftClass1974() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Paolo Rossi", "Itália", Position.ST, null, 18, 72, 57, 27, 73, 72, 96));
        players.add(createPlayer("Liam Brady", "Irlanda", Position.CAM, Position.CM, 18, 61, 78, 38, 69, 82, 95));
        players.add(createPlayer("Toninho Cerezo", "Brasil", Position.CM, Position.CDM, 19, 60, 75, 68, 83, 70, 94));
        players.add(createPlayer("Edinho", "Brasil", Position.CB, Position.LB, 19, 39, 61, 76, 84, 57, 94));
        players.add(createPlayer("Oscar", "Brasil", Position.CB, null, 20, 37, 59, 77, 85, 53, 93));
        players.add(createPlayer("Amaral", "Brasil", Position.CB, null, 20, 36, 58, 78, 86, 51, 91));
        players.add(createPlayer("Hans van Breukelen", "Holanda", Position.GK, null, 18, 12, 39, 76, 80, 17, 94));
        players.add(createPlayer("Frank Stapleton", "Irlanda", Position.ST, null, 18, 69, 55, 32, 83, 66, 92));
        players.add(createPlayer("Peter Reid", "Inglaterra", Position.CM, Position.CDM, 18, 52, 65, 66, 84, 61, 91));
        players.add(createPlayer("Miroslav Votava", "Alemanha Ocidental", Position.CDM, Position.CM, 18, 47, 64, 68, 82, 58, 89));
        players.add(createPlayer("Tschen La Ling", "Holanda", Position.RW, null, 18, 67, 65, 32, 78, 82, 91));
        players.add(createPlayer("Martin Jol", "Holanda", Position.CM, null, 18, 53, 67, 61, 80, 64, 87));
        players.add(createPlayer("Alan Devonshire", "Inglaterra", Position.LM, Position.LW, 18, 63, 69, 39, 78, 79, 89));
        players.add(createPlayer("Gerry Peyton", "Irlanda", Position.GK, null, 18, 11, 35, 73, 78, 16, 85));
        players.add(createPlayer("Engin Verel", "Turquia", Position.LW, Position.RW, 18, 65, 64, 34, 77, 78, 87));
        players.add(createPlayer("Juanito", "Espanha", Position.RW, Position.CF, 19, 69, 67, 32, 81, 82, 93));
        players.add(createPlayer("Enzo Ferrero", "Argentina", Position.LW, null, 21, 71, 68, 31, 81, 84, 90));
        players.add(createPlayer("Perico Alonso", "Espanha", Position.CM, Position.CDM, 21, 53, 68, 65, 84, 63, 88));
        players.add(createPlayer("Daniel Bertoni", "Argentina", Position.RW, Position.ST, 19, 74, 66, 33, 84, 81, 93));
        players.add(createPlayer("Ricardo Bochini", "Argentina", Position.CAM, null, 20, 68, 82, 32, 68, 86, 95));
        players.add(createPlayer("Osvaldo Ardiles", "Argentina", Position.CM, null, 21, 58, 76, 55, 79, 80, 93));
        players.add(createPlayer("Daniel Passarella", "Argentina", Position.CB, null, 21, 50, 67, 82, 87, 58, 96));
        players.add(createPlayer("Batista", "Brasil", Position.CDM, Position.CM, 19, 48, 65, 70, 83, 60, 90));
        players.add(createPlayer("Rosemiro", "Brasil", Position.RB, null, 20, 47, 61, 70, 85, 64, 87));
        players.add(createPlayer("Carlos", "Brasil", Position.GK, null, 18, 12, 37, 74, 81, 17, 90));
        players.add(createPlayer("Joãozinho", "Brasil", Position.LW, null, 20, 69, 66, 32, 78, 83, 90));
        players.add(createPlayer("Rodolfo Rodríguez", "Uruguai", Position.GK, null, 18, 12, 38, 77, 84, 17, 93));
        players.add(createPlayer("Darío Pereyra", "Uruguai", Position.CB, null, 18, 38, 61, 75, 83, 55, 93));
        players.add(createPlayer("Roberto Mouzo", "Argentina", Position.CB, null, 21, 35, 58, 77, 84, 50, 89));
        players.add(createPlayer("Jorge Valdano", "Argentina", Position.ST, null, 18, 68, 58, 28, 79, 69, 94));

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
