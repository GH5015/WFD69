package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1986. */
public final class DraftClass1986 {

    private DraftClass1986() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Paolo Maldini", "Itália", Position.LB, Position.CB, 18, 51, 67, 79, 86, 68, 98));
        players.add(createPlayer("Davor Šuker", "Iugoslávia", Position.ST, Position.CF, 18, 77, 61, 27, 81, 76, 96));
        players.add(createPlayer("Marcel Desailly", "França", Position.CB, Position.CDM, 18, 42, 64, 76, 89, 58, 96));
        players.add(createPlayer("Fernando Hierro", "Espanha", Position.CB, Position.CDM, 18, 49, 68, 75, 86, 61, 96));
        players.add(createPlayer("Zvonimir Boban", "Iugoslávia", Position.CAM, Position.CM, 18, 66, 78, 43, 76, 82, 95));
        players.add(createPlayer("Didier Deschamps", "França", Position.CDM, Position.CM, 18, 47, 69, 72, 85, 62, 95));
        players.add(createPlayer("Stefan Effenberg", "Alemanha Ocidental", Position.CM, Position.CAM, 18, 61, 77, 62, 84, 74, 95));
        players.add(createPlayer("Youri Djorkaeff", "França", Position.CAM, Position.CF, 18, 68, 75, 36, 78, 81, 95));
        players.add(createPlayer("Giuseppe Signori", "Itália", Position.ST, Position.LW, 18, 73, 62, 27, 81, 77, 94));
        players.add(createPlayer("Andreas Herzog", "Áustria", Position.CAM, Position.CM, 18, 61, 77, 43, 73, 79, 94));
        players.add(createPlayer("Cláudio Taffarel", "Brasil", Position.GK, null, 20, 12, 41, 80, 86, 18, 95));
        players.add(createPlayer("Raí", "Brasil", Position.CAM, Position.CM, 21, 69, 78, 43, 82, 79, 95));
        players.add(createPlayer("Aldair", "Brasil", Position.CB, Position.SW, 21, 40, 65, 76, 85, 58, 94));
        players.add(createPlayer("René Higuita", "Colômbia", Position.GK, null, 20, 26, 61, 77, 86, 44, 94));
        players.add(createPlayer("Rúben Sosa", "Uruguai", Position.ST, Position.LW, 20, 74, 63, 29, 85, 77, 94));
        players.add(createPlayer("Olaf Thon", "Alemanha Ocidental", Position.CM, Position.CAM, 20, 62, 75, 55, 80, 76, 94));
        players.add(createPlayer("Alessandro Costacurta", "Itália", Position.CB, null, 20, 34, 60, 76, 84, 52, 94));
        players.add(createPlayer("Gianluca Pagliuca", "Itália", Position.GK, null, 20, 11, 39, 77, 86, 17, 94));
        players.add(createPlayer("Néstor Sensini", "Argentina", Position.CB, Position.CDM, 20, 40, 65, 73, 85, 59, 93));
        players.add(createPlayer("Mazinho", "Brasil", Position.CM, Position.LB, 20, 54, 72, 67, 82, 71, 93));
        players.add(createPlayer("Müller", "Brasil", Position.ST, Position.RW, 20, 72, 61, 29, 87, 77, 93));
        players.add(createPlayer("Mario Basler", "Alemanha Ocidental", Position.RM, Position.CAM, 18, 64, 74, 42, 80, 78, 93));
        players.add(createPlayer("Oliver Bierhoff", "Alemanha Ocidental", Position.ST, null, 18, 71, 52, 30, 88, 63, 93));
        players.add(createPlayer("Fabrizio Ravanelli", "Itália", Position.ST, Position.CF, 18, 70, 55, 32, 87, 67, 92));
        players.add(createPlayer("Slaven Bilić", "Iugoslávia", Position.CB, null, 18, 37, 61, 73, 86, 53, 92));
        players.add(createPlayer("Dorinel Munteanu", "Romênia", Position.CM, Position.LM, 18, 58, 72, 59, 84, 70, 92));
        players.add(createPlayer("Klas Ingesson", "Suécia", Position.CM, Position.CDM, 18, 53, 67, 65, 88, 63, 91));
        players.add(createPlayer("Martin Dahlin", "Suécia", Position.ST, Position.CF, 18, 70, 57, 30, 87, 69, 92));
        players.add(createPlayer("Francesco Moriero", "Itália", Position.RW, Position.RM, 18, 65, 68, 37, 81, 80, 91));
        players.add(createPlayer("Thomas Strunz", "Alemanha Ocidental", Position.CDM, Position.RB, 18, 47, 65, 68, 84, 61, 90));
        players.add(createPlayer("Paolo Di Canio", "Itália", Position.CF, Position.LW, 18, 67, 68, 29, 78, 82, 91));
        players.add(createPlayer("Jorge Campos", "México", Position.GK, null, 20, 45, 55, 75, 82, 51, 92));
        players.add(createPlayer("Freddy Rincón", "Colômbia", Position.CM, Position.CAM, 20, 63, 70, 56, 89, 73, 93));
        players.add(createPlayer("Ulf Kirsten", "Alemanha Oriental", Position.ST, Position.RW, 21, 73, 57, 31, 86, 71, 92));
        players.add(createPlayer("Thomas Helmer", "Alemanha Ocidental", Position.CB, Position.CDM, 21, 39, 63, 72, 85, 57, 92));
        players.add(createPlayer("Franck Sauzée", "França", Position.CM, Position.CDM, 21, 57, 72, 66, 83, 67, 92));
        players.add(createPlayer("Pedro Troglio", "Argentina", Position.CM, Position.RM, 21, 58, 68, 59, 84, 68, 90));
        players.add(createPlayer("Massimo Crippa", "Itália", Position.CM, Position.CDM, 21, 52, 67, 64, 85, 63, 89));
        players.add(createPlayer("John Harkes", "EUA", Position.CM, Position.RM, 19, 57, 69, 56, 83, 70, 89));
        players.add(createPlayer("Osmar Donizete", "Brasil", Position.ST, Position.LW, 18, 69, 57, 28, 86, 73, 89));

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
