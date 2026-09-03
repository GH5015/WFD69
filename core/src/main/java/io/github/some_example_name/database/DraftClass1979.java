package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1979. */
public final class DraftClass1979 {

    private DraftClass1979() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Lothar Matthäus", "Alemanha Ocidental", Position.CM, Position.CDM, 18, 61, 73, 69, 86, 72, 98));
        players.add(createPlayer("Rudi Völler", "Alemanha Ocidental", Position.ST, null, 19, 74, 56, 27, 84, 71, 95));
        players.add(createPlayer("Pierre Littbarski", "Alemanha Ocidental", Position.RW, Position.CAM, 19, 67, 73, 34, 76, 85, 95));

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
