package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1984. */
public final class DraftClass1984 {

    private DraftClass1984() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Romário", "Brasil", Position.ST, Position.CF, 18, 80, 62, 26, 78, 86, 98));
        players.add(createPlayer("Hristo Stoichkov", "Bulgária", Position.LW, Position.ST, 18, 75, 69, 34, 86, 82, 97));
        players.add(createPlayer("George Weah", "Libéria", Position.ST, Position.CF, 18, 72, 57, 28, 91, 76, 97));
        players.add(createPlayer("Eric Cantona", "França", Position.CF, Position.CAM, 18, 72, 73, 34, 87, 81, 96));
        players.add(createPlayer("Thomas Häßler", "Alemanha Ocidental", Position.CAM, Position.CM, 18, 62, 78, 45, 76, 84, 95));
        players.add(createPlayer("Gianfranco Zola", "Itália", Position.CF, Position.CAM, 18, 68, 73, 29, 71, 85, 95));
        players.add(createPlayer("Tony Adams", "Inglaterra", Position.CB, null, 18, 34, 58, 77, 89, 49, 95));
        players.add(createPlayer("David Platt", "Inglaterra", Position.CM, Position.CAM, 18, 62, 71, 57, 83, 70, 94));
        players.add(createPlayer("Teddy Sheringham", "Inglaterra", Position.ST, Position.CF, 18, 71, 65, 30, 78, 70, 94));
        players.add(createPlayer("Stefan Reuter", "Alemanha Ocidental", Position.RB, Position.RM, 18, 50, 65, 71, 89, 67, 94));
        players.add(createPlayer("José Mari Bakero", "Espanha", Position.CAM, Position.CF, 21, 68, 74, 48, 82, 74, 93));
        players.add(createPlayer("Txiki Begiristain", "Espanha", Position.LW, Position.RW, 20, 68, 70, 33, 79, 82, 93));
        players.add(createPlayer("Flemming Povlsen", "Dinamarca", Position.ST, Position.RW, 18, 71, 59, 31, 86, 72, 92));
        players.add(createPlayer("Miguel Ángel Nadal", "Espanha", Position.CB, Position.CDM, 18, 42, 62, 72, 88, 58, 92));
        players.add(createPlayer("Thomas Doll", "Alemanha Oriental", Position.CAM, Position.CF, 18, 66, 72, 39, 80, 79, 92));
        players.add(createPlayer("Rommel Fernández", "Panamá", Position.ST, null, 18, 72, 52, 28, 89, 65, 91));
        players.add(createPlayer("Rob Witschge", "Holanda", Position.LM, Position.CM, 18, 59, 72, 48, 78, 76, 91));
        players.add(createPlayer("Guillermo Sanguinetti", "Uruguai", Position.RB, Position.CB, 18, 44, 60, 71, 84, 60, 90));
        players.add(createPlayer("Henk Fräser", "Holanda", Position.CB, Position.RB, 18, 36, 58, 72, 87, 52, 90));
        players.add(createPlayer("Albeiro Usuriaga", "Colômbia", Position.ST, Position.RW, 18, 70, 57, 28, 88, 76, 91));
        players.add(createPlayer("Vladimir Quesada", "Costa Rica", Position.RB, Position.CB, 18, 45, 60, 69, 82, 61, 88));
        players.add(createPlayer("Ramiro Castillo", "Bolívia", Position.CAM, Position.CM, 18, 61, 71, 40, 76, 77, 89));
        players.add(createPlayer("Wilfred Agbonavbare", "Nigéria", Position.GK, null, 18, 11, 36, 74, 87, 17, 88));
        players.add(createPlayer("Byron Tenorio", "Equador", Position.LB, Position.CB, 18, 41, 59, 68, 83, 57, 87));
        players.add(createPlayer("Miguel Miranda", "Peru", Position.GK, null, 18, 11, 37, 72, 82, 17, 87));
        players.add(createPlayer("Michel van Oostrum", "Holanda", Position.ST, null, 18, 69, 52, 26, 80, 65, 87));
        players.add(createPlayer("Massimo Brambati", "Itália", Position.CB, Position.RB, 18, 36, 58, 68, 82, 53, 86));
        players.add(createPlayer("José Luis Carranza", "Peru", Position.CDM, Position.CM, 20, 46, 63, 68, 85, 59, 89));
        players.add(createPlayer("Stefano Borgonovo", "Itália", Position.ST, null, 20, 70, 53, 27, 82, 66, 89));
        players.add(createPlayer("Danny Wallace", "Inglaterra", Position.LW, Position.ST, 20, 68, 62, 31, 85, 77, 90));
        players.add(createPlayer("Dean Saunders", "País de Gales", Position.ST, null, 20, 71, 54, 28, 86, 65, 90));
        players.add(createPlayer("Clayton Blackmore", "País de Gales", Position.LB, Position.RB, 20, 47, 63, 68, 83, 64, 89));
        players.add(createPlayer("Ernesto Valverde", "Espanha", Position.LW, Position.CF, 20, 67, 65, 32, 78, 76, 88));
        players.add(createPlayer("Alberigo Evani", "Itália", Position.LM, Position.CM, 21, 58, 72, 53, 78, 73, 90));
        players.add(createPlayer("Srečko Katanec", "Iugoslávia", Position.CDM, Position.CB, 21, 46, 67, 72, 86, 60, 92));
        players.add(createPlayer("Neil Webb", "Inglaterra", Position.CM, Position.CAM, 21, 61, 73, 55, 81, 71, 91));
        players.add(createPlayer("Roland Nilsson", "Suécia", Position.RB, Position.CB, 21, 45, 63, 70, 84, 63, 91));
        players.add(createPlayer("Dražen Ladić", "Iugoslávia", Position.GK, null, 21, 11, 39, 77, 84, 18, 91));
        players.add(createPlayer("Stan Valckx", "Holanda", Position.CB, Position.RB, 21, 38, 60, 71, 84, 55, 89));
        players.add(createPlayer("Mario Been", "Holanda", Position.CAM, Position.CM, 21, 61, 72, 42, 76, 77, 89));

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
