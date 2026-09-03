package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1982. */
public final class DraftClass1982 {

    private DraftClass1982() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Marco van Basten", "Holanda", Position.ST, Position.CF, 18, 79, 64, 29, 82, 77, 99));
        players.add(createPlayer("Michael Laudrup", "Dinamarca", Position.CAM, Position.CF, 18, 70, 80, 34, 75, 86, 98));
        players.add(createPlayer("Jürgen Klinsmann", "Alemanha Ocidental", Position.ST, Position.CF, 18, 74, 57, 31, 86, 72, 96));
        players.add(createPlayer("Gianluca Vialli", "Itália", Position.ST, Position.CF, 18, 73, 59, 32, 87, 71, 96));
        players.add(createPlayer("Roberto Mancini", "Itália", Position.CF, Position.CAM, 18, 71, 73, 32, 77, 79, 96));
        players.add(createPlayer("Bebeto", "Brasil", Position.ST, Position.CF, 18, 73, 64, 28, 76, 81, 96));
        players.add(createPlayer("Abedi Pelé", "Gana", Position.CAM, Position.LW, 18, 66, 74, 35, 81, 84, 95));
        players.add(createPlayer("Oleg Protasov", "URSS", Position.ST, null, 18, 74, 55, 27, 84, 69, 94));
        players.add(createPlayer("Gerald Vanenburg", "Holanda", Position.RW, Position.CAM, 18, 66, 73, 38, 75, 85, 94));
        players.add(createPlayer("Thomas Berthold", "Alemanha Ocidental", Position.RB, Position.CB, 18, 47, 62, 72, 87, 63, 93));
        players.add(createPlayer("Giuseppe Giannini", "Itália", Position.CAM, Position.CM, 18, 61, 76, 44, 71, 78, 94));
        players.add(createPlayer("Gary McAllister", "Escócia", Position.CM, Position.CAM, 18, 57, 73, 58, 79, 70, 92));
        players.add(createPlayer("Marius Lăcătuș", "Romênia", Position.RW, Position.ST, 18, 70, 64, 36, 86, 78, 93));
        players.add(createPlayer("Luboš Kubík", "Tchecoslováquia", Position.CB, Position.CDM, 18, 42, 67, 72, 84, 60, 92));
        players.add(createPlayer("Salvatore Schillaci", "Itália", Position.ST, null, 18, 70, 52, 26, 80, 67, 92));
        players.add(createPlayer("Fernando De Napoli", "Itália", Position.CM, Position.CDM, 18, 52, 67, 67, 84, 62, 91));
        players.add(createPlayer("Valdo", "Brasil", Position.CM, Position.CAM, 18, 58, 74, 51, 75, 76, 92));
        players.add(createPlayer("Claudio Borghi", "Argentina", Position.CAM, Position.CF, 18, 66, 74, 30, 69, 84, 93));
        players.add(createPlayer("Eli Ohana", "Israel", Position.CF, Position.ST, 18, 69, 62, 29, 79, 76, 91));
        players.add(createPlayer("Lee Dixon", "Inglaterra", Position.RB, null, 18, 44, 62, 69, 84, 61, 90));
        players.add(createPlayer("Barry Venison", "Inglaterra", Position.RB, Position.CB, 18, 43, 61, 70, 84, 60, 89));
        players.add(createPlayer("Jan Heintze", "Dinamarca", Position.LB, Position.LM, 19, 50, 65, 69, 82, 68, 91));
        players.add(createPlayer("Riccardo Ferri", "Itália", Position.CB, Position.RB, 19, 37, 58, 73, 86, 51, 92));
        players.add(createPlayer("John van 't Schip", "Holanda", Position.RW, Position.RM, 19, 64, 69, 37, 79, 80, 91));
        players.add(createPlayer("Peter Bosz", "Holanda", Position.CDM, Position.CM, 19, 48, 67, 67, 83, 61, 89));
        players.add(createPlayer("Rashidi Yekini", "Nigéria", Position.ST, null, 19, 72, 50, 26, 88, 67, 92));
        players.add(createPlayer("Julio Salinas", "Espanha", Position.ST, null, 20, 72, 54, 28, 86, 64, 91));
        players.add(createPlayer("Stephen Keshi", "Nigéria", Position.CB, Position.CDM, 20, 40, 63, 72, 86, 57, 91));
        players.add(createPlayer("Ray Houghton", "Irlanda", Position.RM, Position.CM, 20, 60, 69, 51, 82, 71, 90));
        players.add(createPlayer("Wim Kieft", "Holanda", Position.ST, null, 20, 75, 52, 27, 85, 65, 93));
        players.add(createPlayer("Fandi Ahmad", "Singapura", Position.CF, Position.CAM, 20, 68, 68, 34, 80, 76, 89));
        players.add(createPlayer("Steve Bould", "Inglaterra", Position.CB, null, 20, 33, 56, 71, 86, 47, 90));
        players.add(createPlayer("Mark Bright", "Inglaterra", Position.ST, null, 20, 70, 53, 29, 86, 65, 89));
        players.add(createPlayer("Paul Walsh", "Inglaterra", Position.ST, Position.CF, 20, 70, 61, 29, 78, 74, 89));
        players.add(createPlayer("Bruno Martini", "França", Position.GK, null, 20, 11, 38, 76, 83, 17, 90));

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
