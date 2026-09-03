package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1981. */
public final class DraftClass1981 {

    private DraftClass1981() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Ronald Koeman", "Holanda", Position.CB, Position.CDM, 18, 54, 75, 76, 82, 67, 97));
        players.add(createPlayer("Enzo Francescoli", "Uruguai", Position.CAM, Position.CF, 20, 76, 82, 33, 76, 87, 96));
        players.add(createPlayer("Ian Rush", "País de Gales", Position.ST, null, 20, 80, 59, 29, 86, 72, 96));
        players.add(createPlayer("Carlos Valderrama", "Colômbia", Position.CAM, Position.CM, 20, 62, 85, 38, 68, 84, 96));
        players.add(createPlayer("John Barnes", "Inglaterra", Position.LW, Position.LM, 18, 69, 70, 34, 84, 86, 95));
        players.add(createPlayer("Jean-Pierre Papin", "França", Position.ST, Position.CF, 18, 71, 56, 27, 80, 70, 95));
        players.add(createPlayer("Giuseppe Bergomi", "Itália", Position.CB, Position.RB, 18, 39, 61, 78, 86, 56, 95));
        players.add(createPlayer("Emilio Butragueño", "Espanha", Position.ST, Position.CF, 18, 69, 67, 28, 74, 83, 95));
        players.add(createPlayer("Dunga", "Brasil", Position.CDM, Position.CM, 18, 49, 69, 70, 85, 62, 94));
        players.add(createPlayer("Roberto Donadoni", "Itália", Position.RW, Position.CM, 18, 67, 74, 42, 78, 83, 94));
        players.add(createPlayer("Peter Schmeichel", "Dinamarca", Position.GK, null, 18, 12, 40, 77, 88, 18, 94));
        players.add(createPlayer("Andoni Zubizarreta", "Espanha", Position.GK, null, 20, 12, 40, 81, 84, 17, 94));
        players.add(createPlayer("Júlio César", "Brasil", Position.CB, Position.SW, 18, 38, 63, 76, 86, 59, 94));
        players.add(createPlayer("Míchel", "Espanha", Position.RM, Position.RW, 18, 64, 74, 42, 78, 77, 94));
        players.add(createPlayer("Aleksandr Zavarov", "URSS", Position.CAM, Position.CM, 20, 65, 77, 42, 75, 80, 94));
        players.add(createPlayer("Peter Beardsley", "Inglaterra", Position.CF, Position.CAM, 20, 72, 73, 32, 74, 84, 93));
        players.add(createPlayer("Manuel Amoros", "França", Position.RB, Position.LB, 19, 50, 67, 72, 84, 70, 94));
        players.add(createPlayer("Mark Hughes", "País de Gales", Position.ST, Position.CF, 18, 71, 57, 31, 88, 67, 93));
        players.add(createPlayer("Renato Gaúcho", "Brasil", Position.RW, Position.CF, 19, 71, 68, 35, 84, 81, 93));
        players.add(createPlayer("Guido Buchwald", "Alemanha Ocidental", Position.CB, Position.CDM, 20, 40, 64, 75, 87, 56, 93));
        players.add(createPlayer("Ally McCoist", "Escócia", Position.ST, null, 19, 73, 53, 27, 82, 67, 92));
        players.add(createPlayer("Bruno Bellone", "França", Position.LW, Position.ST, 19, 70, 64, 31, 84, 79, 92));
        players.add(createPlayer("Erwin Koeman", "Holanda", Position.CM, Position.LM, 20, 58, 74, 60, 79, 72, 91));
        players.add(createPlayer("Steve Hodge", "Inglaterra", Position.CM, Position.LM, 19, 58, 70, 56, 82, 69, 90));
        players.add(createPlayer("Uwe Rahn", "Alemanha Ocidental", Position.CAM, Position.CF, 19, 66, 70, 39, 78, 75, 91));
        players.add(createPlayer("Charlie Nicholas", "Escócia", Position.ST, Position.CF, 20, 75, 61, 29, 78, 74, 91));
        players.add(createPlayer("Jorge da Silva", "Uruguai", Position.ST, null, 20, 73, 55, 28, 82, 68, 90));
        players.add(createPlayer("Héctor Enrique", "Argentina", Position.CM, Position.CDM, 19, 54, 70, 62, 82, 67, 90));
        players.add(createPlayer("Carlos Tapia", "Argentina", Position.CAM, Position.CM, 19, 62, 73, 40, 72, 77, 90));
        players.add(createPlayer("Romerito", "Paraguai", Position.CAM, Position.CF, 21, 72, 75, 37, 80, 81, 92));
        players.add(createPlayer("Gary Stevens", "Inglaterra", Position.RB, Position.RM, 18, 48, 63, 70, 84, 65, 90));
        players.add(createPlayer("Jan Mølby", "Dinamarca", Position.CM, Position.CDM, 18, 54, 72, 63, 82, 67, 91));
        players.add(createPlayer("Ian Wright", "Inglaterra", Position.ST, null, 18, 68, 51, 27, 84, 70, 91));
        players.add(createPlayer("David Seaman", "Inglaterra", Position.GK, null, 18, 11, 38, 73, 83, 17, 91));
        players.add(createPlayer("Bernard Lama", "França", Position.GK, null, 18, 12, 40, 74, 85, 19, 91));
        players.add(createPlayer("Ivan Hašek", "Tchecoslováquia", Position.CM, Position.CDM, 18, 52, 67, 63, 81, 66, 89));
        players.add(createPlayer("Kalusha Bwalya", "Zâmbia", Position.RW, Position.CF, 18, 68, 64, 30, 81, 79, 91));
        players.add(createPlayer("Stanislav Cherchesov", "URSS", Position.GK, null, 18, 11, 36, 71, 82, 16, 87));

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
