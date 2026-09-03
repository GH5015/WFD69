package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1985. */
public final class DraftClass1985 {

    private DraftClass1985() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT.
        players.add(createPlayer("Roberto Baggio", "Itália", Position.CF, Position.CAM, 18, 76, 79, 29, 72, 89, 98));
        players.add(createPlayer("Matthias Sammer", "Alemanha Oriental", Position.CDM, Position.CB, 18, 51, 72, 77, 86, 67, 97));
        players.add(createPlayer("Paul Gascoigne", "Inglaterra", Position.CAM, Position.CM, 18, 64, 79, 45, 78, 86, 96));
        players.add(createPlayer("Claudio Caniggia", "Argentina", Position.RW, Position.ST, 18, 71, 66, 28, 91, 84, 95));
        players.add(createPlayer("Iván Zamorano", "Chile", Position.ST, null, 18, 73, 54, 29, 87, 67, 95));
        players.add(createPlayer("Andreas Möller", "Alemanha Ocidental", Position.CAM, Position.CM, 18, 64, 77, 40, 80, 82, 95));
        players.add(createPlayer("Ciro Ferrara", "Itália", Position.CB, Position.RB, 18, 37, 61, 77, 86, 54, 95));
        players.add(createPlayer("Bodo Illgner", "Alemanha Ocidental", Position.GK, null, 18, 11, 39, 78, 85, 17, 94));
        players.add(createPlayer("David Ginola", "França", Position.LW, Position.CAM, 18, 67, 70, 33, 82, 84, 94));
        players.add(createPlayer("Aron Winter", "Holanda", Position.CM, Position.CDM, 18, 53, 70, 67, 83, 69, 93));
        players.add(createPlayer("Paul Ince", "Inglaterra", Position.CDM, Position.CM, 18, 51, 65, 72, 89, 63, 93));
        players.add(createPlayer("Nicola Berti", "Itália", Position.CM, Position.RM, 18, 59, 70, 62, 87, 70, 93));
        players.add(createPlayer("Guillermo Amor", "Espanha", Position.CM, Position.CAM, 18, 58, 75, 56, 79, 74, 93));
        players.add(createPlayer("Andrés Escobar", "Colômbia", Position.CB, null, 18, 35, 61, 76, 84, 54, 93));
        players.add(createPlayer("Luc Nilis", "Bélgica", Position.ST, Position.CF, 18, 72, 63, 28, 78, 76, 93));
        players.add(createPlayer("Jocelyn Angloma", "França", Position.RB, Position.RWB, 18, 51, 65, 72, 88, 68, 92));
        players.add(createPlayer("José Luis Caminero", "Espanha", Position.CM, Position.CAM, 18, 60, 72, 55, 82, 74, 92));
        players.add(createPlayer("Philippe Albert", "Bélgica", Position.CB, Position.SW, 18, 42, 64, 74, 85, 58, 92));
        players.add(createPlayer("Michael Thomas", "Inglaterra", Position.CM, Position.RM, 18, 58, 68, 62, 86, 67, 92));
        players.add(createPlayer("Uche Okechukwu", "Nigéria", Position.CB, null, 18, 34, 57, 74, 88, 49, 92));
        players.add(createPlayer("Gus Poyet", "Uruguai", Position.CAM, Position.CM, 18, 64, 72, 52, 83, 72, 92));
        players.add(createPlayer("Igor Štimac", "Iugoslávia", Position.CB, null, 18, 37, 61, 74, 86, 53, 91));
        players.add(createPlayer("Kennet Andersson", "Suécia", Position.ST, null, 18, 70, 53, 31, 89, 62, 91));
        players.add(createPlayer("David Rocastle", "Inglaterra", Position.RM, Position.CM, 18, 62, 70, 49, 83, 79, 91));
        players.add(createPlayer("Kubilay Türkyılmaz", "Suíça", Position.ST, Position.CF, 18, 71, 59, 27, 82, 71, 91));
        players.add(createPlayer("Jan Åge Fjørtoft", "Noruega", Position.ST, null, 18, 70, 53, 28, 86, 65, 90));
        players.add(createPlayer("Kazuyoshi Miura", "Japão", Position.ST, Position.LW, 18, 68, 61, 28, 81, 76, 90));
        players.add(createPlayer("Bogdan Stelea", "Romênia", Position.GK, null, 18, 11, 38, 75, 85, 17, 90));
        players.add(createPlayer("Tim Flowers", "Inglaterra", Position.GK, null, 18, 11, 37, 74, 84, 17, 90));
        players.add(createPlayer("Zvonimir Soldo", "Iugoslávia", Position.CDM, Position.CB, 18, 46, 65, 70, 85, 60, 90));
        players.add(createPlayer("Palhinha", "Brasil", Position.CAM, Position.CF, 18, 66, 71, 34, 77, 78, 90));
        players.add(createPlayer("Aurelio Vidmar", "Austrália", Position.CAM, Position.CF, 18, 64, 68, 37, 79, 74, 89));
        players.add(createPlayer("Pieter Huistra", "Holanda", Position.LW, Position.LM, 18, 63, 67, 36, 82, 77, 89));
        players.add(createPlayer("Jorge Dely Valdés", "Panamá", Position.ST, null, 18, 69, 52, 27, 84, 65, 89));
        players.add(createPlayer("Carlos Muñoz", "Equador", Position.ST, null, 18, 70, 54, 26, 83, 67, 89));
        players.add(createPlayer("José del Solar", "Peru", Position.CDM, Position.CM, 18, 48, 66, 68, 82, 61, 89));
        players.add(createPlayer("Efan Ekoku", "Nigéria", Position.ST, null, 18, 68, 53, 29, 86, 66, 88));
        players.add(createPlayer("Stuart Ripley", "Inglaterra", Position.RW, Position.RM, 18, 62, 65, 38, 85, 74, 88));
        players.add(createPlayer("Ian Woan", "Inglaterra", Position.LM, Position.LW, 18, 62, 68, 39, 80, 74, 87));
        players.add(createPlayer("Nando", "Espanha", Position.CB, Position.LB, 18, 39, 61, 69, 82, 58, 87));

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
