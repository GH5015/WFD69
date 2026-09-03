package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1978. */
public final class DraftClass1978 {

    private DraftClass1978() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Diego Maradona", "Argentina", Position.CAM, Position.CF, 18, 79, 83, 27, 75, 93, 99));
        players.add(createPlayer("Gary Lineker", "Inglaterra", Position.ST, null, 18, 72, 54, 25, 78, 68, 96));
        players.add(createPlayer("Careca", "Brasil", Position.ST, null, 18, 73, 58, 27, 82, 73, 96));
        players.add(createPlayer("Andreas Brehme", "Alemanha Ocidental", Position.LB, Position.LM, 18, 52, 67, 70, 81, 68, 95));
        players.add(createPlayer("Igor Belanov", "URSS", Position.ST, Position.RW, 18, 70, 60, 29, 88, 77, 95));
        players.add(createPlayer("Carlo Ancelotti", "Itália", Position.CM, Position.CDM, 19, 55, 72, 67, 80, 66, 93));
        players.add(createPlayer("Paul McGrath", "Irlanda", Position.CB, Position.CDM, 19, 37, 59, 73, 86, 54, 94));
        players.add(createPlayer("Chris Waddle", "Inglaterra", Position.RW, Position.LW, 18, 65, 68, 31, 77, 83, 94));
        players.add(createPlayer("Carlos Manuel", "Portugal", Position.CM, Position.CAM, 20, 60, 72, 57, 81, 71, 92));
        players.add(createPlayer("Joachim Löw", "Alemanha Ocidental", Position.CAM, Position.ST, 18, 65, 68, 32, 73, 74, 85));
        players.add(createPlayer("Kevin Ratcliffe", "País de Gales", Position.CB, null, 18, 34, 58, 73, 84, 50, 92));
        players.add(createPlayer("Steve Bruce", "Inglaterra", Position.CB, null, 18, 35, 55, 71, 85, 48, 90));
        players.add(createPlayer("Gary Gillespie", "Escócia", Position.CB, null, 18, 34, 60, 72, 82, 52, 90));
        players.add(createPlayer("John Lukic", "Inglaterra", Position.GK, null, 18, 11, 37, 75, 82, 17, 91));
        players.add(createPlayer("Craig Johnston", "Austrália", Position.RM, Position.CAM, 18, 62, 69, 42, 80, 77, 90));
        players.add(createPlayer("Uwe Bein", "Alemanha Ocidental", Position.CAM, Position.CM, 18, 59, 75, 38, 70, 77, 92));
        players.add(createPlayer("Alan Brazil", "Escócia", Position.ST, null, 19, 72, 55, 28, 83, 68, 88));
        players.add(createPlayer("Alex McLeish", "Escócia", Position.CB, null, 19, 34, 59, 74, 86, 50, 92));
        players.add(createPlayer("Mick McCarthy", "Irlanda", Position.CB, null, 19, 32, 56, 72, 86, 47, 89));
        players.add(createPlayer("Sammy Lee", "Inglaterra", Position.CM, Position.RM, 19, 57, 69, 55, 79, 68, 88));
        players.add(createPlayer("Nery Pumpido", "Argentina", Position.GK, null, 21, 12, 39, 78, 83, 18, 93));
        players.add(createPlayer("Hallvar Thoresen", "Noruega", Position.ST, Position.CF, 21, 70, 61, 31, 84, 70, 90));
        players.add(createPlayer("Neville Southall", "País de Gales", Position.GK, null, 20, 11, 37, 76, 86, 17, 95));
        players.add(createPlayer("Hugo Sánchez", "México", Position.ST, Position.CF, 20, 75, 58, 28, 81, 75, 96));
        players.add(createPlayer("Michael Robinson", "Irlanda", Position.ST, null, 20, 69, 56, 31, 86, 64, 87));
        players.add(createPlayer("Quique Setién", "Espanha", Position.CM, Position.CAM, 20, 57, 71, 50, 75, 70, 89));
        players.add(createPlayer("Garth Crooks", "Inglaterra", Position.ST, null, 20, 70, 53, 28, 83, 65, 87));
        players.add(createPlayer("Luther Blissett", "Inglaterra", Position.ST, null, 20, 71, 52, 27, 87, 65, 89));
        players.add(createPlayer("Victor Diogo", "Uruguai", Position.RB, null, 20, 47, 62, 70, 82, 63, 89));

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
