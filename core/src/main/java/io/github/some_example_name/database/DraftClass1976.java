package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1976. */
public final class DraftClass1976 {

    private DraftClass1976() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Glenn Hoddle", "Inglaterra", Position.CAM, Position.CM, 19, 65, 81, 40, 70, 82, 96));
        players.add(createPlayer("David O'Leary", "Irlanda", Position.CB, null, 18, 35, 62, 76, 82, 55, 94));
        players.add(createPlayer("Laurie Cunningham", "Inglaterra", Position.LW, Position.RW, 20, 71, 68, 31, 81, 87, 94));
        players.add(createPlayer("Ray Wilkins", "Inglaterra", Position.CM, Position.CDM, 20, 55, 75, 64, 79, 72, 94));
        players.add(createPlayer("Maxime Bossis", "França", Position.CB, Position.LB, 21, 42, 65, 75, 82, 61, 94));
        players.add(createPlayer("Karlheinz Förster", "Alemanha Ocidental", Position.CB, null, 18, 33, 56, 74, 83, 49, 94));
        players.add(createPlayer("Kenny Sansom", "Inglaterra", Position.LB, null, 18, 48, 64, 72, 82, 69, 93));
        players.add(createPlayer("Andy Gray", "Escócia", Position.ST, null, 21, 78, 55, 31, 89, 67, 93));
        players.add(createPlayer("Hans-Peter Briegel", "Alemanha Ocidental", Position.LB, Position.CM, 21, 55, 65, 73, 92, 63, 93));
        players.add(createPlayer("Klaus Allofs", "Alemanha Ocidental", Position.ST, Position.LW, 20, 72, 62, 29, 81, 74, 93));
        players.add(createPlayer("Terry Butcher", "Inglaterra", Position.CB, null, 18, 34, 57, 74, 86, 49, 93));
        players.add(createPlayer("Peter Barnes", "Inglaterra", Position.LW, Position.RW, 19, 70, 67, 33, 82, 82, 92));
        players.add(createPlayer("Cyrille Regis", "Inglaterra", Position.ST, null, 18, 68, 51, 29, 91, 66, 92));
        players.add(createPlayer("Willie Miller", "Escócia", Position.CB, null, 21, 36, 60, 77, 84, 53, 92));
        players.add(createPlayer("Bernd Förster", "Alemanha Ocidental", Position.CB, null, 20, 35, 58, 76, 85, 51, 92));
        players.add(createPlayer("Patrick Battiston", "França", Position.RB, Position.CB, 19, 45, 62, 71, 82, 59, 92));
        players.add(createPlayer("Viv Anderson", "Inglaterra", Position.RB, null, 20, 51, 64, 72, 86, 67, 92));
        players.add(createPlayer("Steve Archibald", "Escócia", Position.ST, null, 20, 72, 57, 29, 82, 70, 91));
        players.add(createPlayer("Tony Woodcock", "Inglaterra", Position.ST, Position.RW, 21, 73, 61, 31, 82, 72, 90));
        players.add(createPlayer("Graham Rix", "Inglaterra", Position.LM, Position.LW, 19, 64, 70, 40, 78, 79, 90));
        players.add(createPlayer("Gary Bailey", "Inglaterra", Position.GK, null, 18, 11, 36, 74, 82, 17, 90));
        players.add(createPlayer("Jean-François Larios", "França", Position.CM, Position.CAM, 20, 57, 72, 50, 75, 72, 90));
        players.add(createPlayer("Chris Hughton", "Irlanda", Position.LB, Position.RB, 18, 45, 61, 69, 82, 62, 89));
        players.add(createPlayer("Norbert Eder", "Alemanha Ocidental", Position.CDM, Position.CB, 21, 45, 62, 70, 84, 58, 89));
        players.add(createPlayer("Mal Donaghy", "Irlanda do Norte", Position.CB, Position.RB, 19, 39, 58, 69, 82, 56, 87));
        players.add(createPlayer("Hans-Peter Müller", "Alemanha Ocidental", Position.CM, null, 21, 54, 67, 59, 80, 65, 86));

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
