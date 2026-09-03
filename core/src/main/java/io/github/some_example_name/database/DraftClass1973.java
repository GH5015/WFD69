package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1973. */
public final class DraftClass1973 {

    private DraftClass1973() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Michel Platini", "França", Position.CAM, Position.CM, 18, 65, 79, 36, 67, 80, 97));
        players.add(createPlayer("Mario Kempes", "Argentina", Position.ST, Position.CF, 19, 76, 63, 30, 84, 78, 96));
        players.add(createPlayer("Marco Tardelli", "Itália", Position.CM, Position.CDM, 19, 57, 68, 72, 86, 66, 94));
        players.add(createPlayer("Hans Krankl", "Áustria", Position.ST, null, 20, 77, 54, 27, 84, 68, 94));
        players.add(createPlayer("Felix Magath", "Alemanha Ocidental", Position.CAM, Position.CM, 20, 64, 77, 44, 74, 78, 94));
        players.add(createPlayer("Paul Mariner", "Inglaterra", Position.ST, null, 20, 71, 53, 31, 86, 63, 92));
        players.add(createPlayer("Alan Hansen", "Escócia", Position.CB, null, 18, 37, 65, 73, 78, 61, 95));
        players.add(createPlayer("Steve Coppell", "Inglaterra", Position.RW, Position.RM, 18, 67, 67, 39, 83, 79, 92));
        players.add(createPlayer("Vladimir Petrović", "Iugoslávia", Position.CAM, Position.RW, 18, 67, 76, 36, 71, 82, 93));
        players.add(createPlayer("Safet Sušić", "Iugoslávia", Position.CAM, Position.CF, 18, 66, 75, 31, 72, 84, 95));
        players.add(createPlayer("Ivica Šurjak", "Iugoslávia", Position.LW, Position.LM, 20, 72, 68, 37, 82, 80, 91));
        players.add(createPlayer("Dražen Mužinić", "Iugoslávia", Position.CM, Position.CDM, 20, 52, 68, 68, 82, 64, 88));
        players.add(createPlayer("Aleksandar Trifunović", "Iugoslávia", Position.CM, null, 19, 55, 69, 59, 76, 67, 86));
        players.add(createPlayer("Jan Peters", "Holanda", Position.CM, Position.CAM, 19, 59, 73, 50, 75, 72, 89));
        players.add(createPlayer("Bruno Pezzey", "Áustria", Position.CB, Position.CDM, 18, 40, 61, 73, 84, 55, 93));
        players.add(createPlayer("Herbert Prohaska", "Áustria", Position.CM, Position.CAM, 18, 56, 74, 54, 73, 73, 93));
        players.add(createPlayer("Uli Stielike", "Alemanha Ocidental", Position.CDM, Position.CB, 18, 49, 68, 73, 84, 62, 95));
        players.add(createPlayer("Wolfgang Dremmler", "Alemanha Ocidental", Position.CDM, Position.CM, 19, 48, 65, 67, 83, 59, 88));
        players.add(createPlayer("Gabriele Oriali", "Itália", Position.CDM, Position.CM, 20, 48, 65, 72, 84, 59, 91));
        players.add(createPlayer("Francesco Graziani", "Itália", Position.ST, Position.CF, 20, 71, 57, 31, 85, 66, 92));
        players.add(createPlayer("Roberto Pruzzo", "Itália", Position.ST, null, 18, 69, 52, 27, 81, 64, 93));
        players.add(createPlayer("Patrizio Sala", "Itália", Position.CM, Position.CDM, 18, 51, 65, 62, 78, 62, 87));
        players.add(createPlayer("Christian Sarramagna", "França", Position.LW, null, 21, 67, 64, 37, 78, 76, 87));
        players.add(createPlayer("Jean-Paul Bertrand-Demanes", "França", Position.GK, null, 21, 13, 39, 80, 81, 18, 88));
        players.add(createPlayer("João Alves", "Portugal", Position.CAM, Position.CM, 20, 61, 75, 42, 70, 78, 91));
        players.add(createPlayer("Shéu Han", "Portugal", Position.CM, Position.CDM, 20, 53, 68, 62, 77, 65, 88));

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
