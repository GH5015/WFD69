package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1975. */
public final class DraftClass1975 {

    private DraftClass1975() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Zbigniew Boniek", "Polônia", Position.CAM, Position.RW, 19, 70, 76, 42, 82, 82, 96));
        players.add(createPlayer("Karl-Heinz Rummenigge", "Alemanha Ocidental", Position.ST, Position.RW, 19, 74, 62, 31, 86, 77, 96));
        players.add(createPlayer("Bryan Robson", "Inglaterra", Position.CM, Position.CDM, 18, 58, 69, 68, 84, 66, 95));
        players.add(createPlayer("Rinat Dasayev", "URSS", Position.GK, null, 18, 11, 39, 77, 82, 17, 95));
        players.add(createPlayer("Antonio Cabrini", "Itália", Position.LB, Position.LWB, 18, 51, 67, 72, 82, 70, 95));
        players.add(createPlayer("Fulvio Collovati", "Itália", Position.CB, null, 18, 34, 57, 74, 81, 49, 93));
        players.add(createPlayer("Gordon Strachan", "Escócia", Position.CM, Position.RM, 18, 59, 72, 52, 78, 75, 92));
        players.add(createPlayer("Jean-Marie Pfaff", "Bélgica", Position.GK, null, 21, 12, 39, 80, 84, 18, 94));
        players.add(createPlayer("Jan Ceulemans", "Bélgica", Position.CAM, Position.CF, 18, 65, 69, 44, 85, 73, 94));
        players.add(createPlayer("Preben Elkjær", "Dinamarca", Position.ST, null, 18, 70, 55, 29, 88, 74, 94));
        players.add(createPlayer("John Wark", "Escócia", Position.CM, Position.CDM, 18, 58, 67, 67, 85, 63, 92));
        players.add(createPlayer("Mark Lawrenson", "Irlanda", Position.CB, Position.RB, 18, 38, 61, 73, 82, 57, 93));
        players.add(createPlayer("Bruce Grobbelaar", "Rodésia", Position.GK, null, 18, 12, 41, 74, 85, 20, 92));
        players.add(createPlayer("Stefano Tacconi", "Itália", Position.GK, null, 18, 11, 36, 73, 81, 17, 92));
        players.add(createPlayer("Giancarlo Galdiolo", "Itália", Position.CB, null, 21, 36, 57, 76, 84, 49, 87));
        players.add(createPlayer("Salvatore Bagni", "Itália", Position.CM, Position.CDM, 18, 54, 65, 65, 84, 63, 90));
        players.add(createPlayer("Evaristo Beccalossi", "Itália", Position.CAM, null, 19, 64, 75, 32, 67, 82, 92));
        players.add(createPlayer("Bruno Giordano", "Itália", Position.ST, null, 18, 70, 57, 27, 79, 73, 93));
        players.add(createPlayer("Sergio Brio", "Itália", Position.CB, null, 19, 34, 55, 73, 86, 47, 89));
        players.add(createPlayer("Davie Cooper", "Escócia", Position.LW, null, 19, 66, 68, 33, 75, 82, 91));
        players.add(createPlayer("Rüdiger Abramczik", "Alemanha Ocidental", Position.RW, Position.ST, 19, 69, 61, 31, 83, 77, 91));
        players.add(createPlayer("Stefan Majewski", "Polônia", Position.CB, Position.CDM, 19, 42, 62, 70, 83, 57, 90));
        players.add(createPlayer("László Kiss", "Hungria", Position.ST, null, 19, 69, 55, 28, 79, 67, 88));
        players.add(createPlayer("Antonio Alzamendi", "Uruguai", Position.ST, Position.RW, 19, 68, 59, 29, 84, 73, 91));
        players.add(createPlayer("Jimmy Nicholl", "Irlanda do Norte", Position.RB, null, 19, 45, 60, 69, 82, 60, 88));
        players.add(createPlayer("Reinhold Hintermaier", "Áustria", Position.CM, null, 19, 54, 68, 55, 76, 68, 87));
        players.add(createPlayer("Faouzi Mansouri", "Argélia", Position.LB, Position.CB, 19, 42, 59, 68, 80, 57, 87));
        players.add(createPlayer("Kiyoshi Okuma", "Japão", Position.CB, null, 21, 34, 55, 67, 79, 48, 82));

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
