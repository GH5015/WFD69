package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1977. */
public final class DraftClass1977 {

    private DraftClass1977() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Bernd Schuster", "Alemanha Ocidental", Position.CM, Position.CAM, 18, 62, 76, 60, 79, 77, 96));
        players.add(createPlayer("Anatoliy Demyanenko", "URSS", Position.LB, Position.LM, 18, 50, 65, 70, 84, 69, 94));
        players.add(createPlayer("Ramón Díaz", "Argentina", Position.ST, Position.CF, 18, 71, 58, 27, 80, 73, 94));
        players.add(createPlayer("Walter Schachner", "Áustria", Position.ST, null, 20, 72, 56, 28, 86, 70, 93));
        players.add(createPlayer("Vagiz Khidiyatullin", "URSS", Position.CB, Position.CDM, 18, 41, 62, 72, 84, 57, 93));
        players.add(createPlayer("Jean-François Domergue", "França", Position.LB, Position.CB, 20, 44, 61, 69, 80, 59, 88));
        players.add(createPlayer("José María Giménez", "Uruguai", Position.CM, null, 19, 56, 68, 57, 79, 68, 87));
        players.add(createPlayer("Rubén Paz", "Uruguai", Position.CAM, Position.CM, 18, 62, 74, 38, 72, 80, 93));
        players.add(createPlayer("Juan Ramón Carrasco", "Uruguai", Position.CAM, Position.CF, 21, 67, 73, 34, 74, 81, 91));
        players.add(createPlayer("Hugo de León", "Uruguai", Position.CB, null, 19, 39, 61, 75, 86, 54, 94));
        players.add(createPlayer("Alberto Bica", "Uruguai", Position.CM, Position.CAM, 19, 58, 70, 49, 76, 72, 88));
        players.add(createPlayer("Waldemar Victorino", "Uruguai", Position.ST, null, 21, 73, 54, 28, 84, 65, 91));
        players.add(createPlayer("Venancio Ramos", "Uruguai", Position.RW, Position.ST, 18, 67, 61, 31, 79, 77, 89));
        players.add(createPlayer("Zé Sérgio", "Brasil", Position.LW, null, 20, 67, 65, 32, 79, 82, 91));
        players.add(createPlayer("Jorginho Putinatti", "Brasil", Position.CAM, Position.LW, 18, 59, 69, 36, 73, 77, 89));
        players.add(createPlayer("Baltazar", "Brasil", Position.ST, null, 18, 69, 52, 27, 82, 65, 90));

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
