package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1983. */
public final class DraftClass1983 {

    private DraftClass1983() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Gheorghe Hagi", "Romênia", Position.CAM, Position.RW, 18, 72, 82, 34, 74, 87, 97));
        players.add(createPlayer("Dragan Stojković", "Iugoslávia", Position.CAM, Position.CM, 18, 68, 80, 36, 72, 85, 96));
        players.add(createPlayer("Laurent Blanc", "França", Position.CB, Position.SW, 18, 42, 67, 75, 81, 60, 95));
        players.add(createPlayer("Jürgen Kohler", "Alemanha Ocidental", Position.CB, null, 18, 34, 56, 78, 88, 48, 95));
        players.add(createPlayer("José Luis Chilavert", "Paraguai", Position.GK, null, 18, 20, 55, 76, 84, 24, 94));
        players.add(createPlayer("Darko Pančev", "Iugoslávia", Position.ST, null, 18, 76, 53, 27, 82, 68, 94));
        players.add(createPlayer("Norman Whiteside", "Irlanda do Norte", Position.CAM, Position.CF, 18, 70, 71, 49, 89, 72, 93));
        players.add(createPlayer("Karl-Heinz Riedle", "Alemanha Ocidental", Position.ST, null, 18, 72, 54, 29, 86, 65, 93));
        players.add(createPlayer("Des Walker", "Inglaterra", Position.CB, null, 18, 33, 55, 76, 91, 51, 93));
        players.add(createPlayer("Denis Irwin", "Irlanda", Position.LB, Position.RB, 18, 48, 65, 72, 83, 66, 92));
        players.add(createPlayer("Trifon Ivanov", "Bulgária", Position.CB, null, 18, 37, 57, 75, 89, 51, 92));
        players.add(createPlayer("John Jensen", "Dinamarca", Position.CM, Position.CDM, 18, 55, 69, 65, 83, 65, 91));
        players.add(createPlayer("Anders Limpar", "Suécia", Position.LW, Position.CAM, 18, 65, 70, 35, 77, 82, 91));
        players.add(createPlayer("Tony Cottee", "Inglaterra", Position.ST, null, 18, 73, 54, 27, 80, 68, 91));
        players.add(createPlayer("Gary Pallister", "Inglaterra", Position.CB, null, 18, 33, 57, 73, 88, 50, 91));
        players.add(createPlayer("Leonel Álvarez", "Colômbia", Position.CDM, Position.CM, 18, 47, 66, 70, 85, 61, 91));
        players.add(createPlayer("Tony Dorigo", "Austrália", Position.LB, Position.LM, 18, 50, 64, 70, 83, 67, 90));
        players.add(createPlayer("Aykut Kocaman", "Turquia", Position.ST, Position.CF, 18, 70, 58, 28, 81, 71, 91));
        players.add(createPlayer("Colin Hendry", "Escócia", Position.CB, null, 18, 34, 55, 72, 89, 47, 90));
        players.add(createPlayer("Quique Sánchez Flores", "Espanha", Position.RB, Position.CB, 18, 45, 61, 70, 82, 61, 89));
        players.add(createPlayer("Gustavo Quinteros", "Bolívia", Position.CB, null, 18, 35, 57, 69, 84, 50, 88));
        players.add(createPlayer("John van Loen", "Holanda", Position.ST, null, 18, 71, 52, 29, 88, 63, 89));
        players.add(createPlayer("Carlos Luis Morales", "Equador", Position.GK, null, 18, 11, 37, 73, 82, 17, 88));
        players.add(createPlayer("Anthony Baffoe", "Gana", Position.CB, Position.RB, 18, 38, 59, 70, 85, 58, 89));
        players.add(createPlayer("Hansi Flick", "Alemanha Ocidental", Position.CM, Position.CDM, 18, 53, 67, 62, 78, 64, 86));
        players.add(createPlayer("Carlton Palmer", "Inglaterra", Position.CM, Position.CDM, 18, 48, 62, 66, 88, 58, 87));
        players.add(createPlayer("Iain Dowie", "Irlanda do Norte", Position.ST, null, 18, 68, 50, 30, 89, 61, 87));
        players.add(createPlayer("Robbie Earle", "Jamaica", Position.CM, Position.CAM, 18, 60, 68, 51, 82, 69, 88));
        players.add(createPlayer("Neil Redfearn", "Inglaterra", Position.CM, Position.CAM, 18, 58, 69, 53, 81, 68, 87));
        players.add(createPlayer("Gary Ablett", "Inglaterra", Position.LB, Position.CB, 18, 40, 59, 69, 82, 57, 87));
        players.add(createPlayer("Colin Calderwood", "Escócia", Position.CB, null, 18, 32, 55, 70, 85, 47, 87));
        players.add(createPlayer("Peter Beagrie", "Inglaterra", Position.LW, Position.LM, 18, 63, 67, 35, 79, 79, 88));
        players.add(createPlayer("Neil McDonald", "Inglaterra", Position.RB, Position.CM, 18, 47, 62, 66, 82, 61, 86));
        players.add(createPlayer("John Moshoeu", "África do Sul", Position.CAM, Position.CM, 18, 61, 71, 39, 75, 76, 89));
        players.add(createPlayer("Mika Aaltonen", "Finlândia", Position.CM, Position.CAM, 18, 55, 68, 50, 76, 69, 85));

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
