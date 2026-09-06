package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1998. */
public final class DraftClass1998 {
    private DraftClass1998() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Ronaldinho", "Brasil", "CAM", "LW", 78, 82, 29, 81, 94, 99);
        add(players, "Xavi", "Espanha", "CM", "CAM", 61, 84, 54, 73, 83, 98);
        add(players, "Steven Gerrard", "Inglaterra", "CM", "CAM", 66, 79, 63, 88, 77, 98);
        add(players, "John Terry", "Inglaterra", "CB", null, 39, 63, 80, 91, 54, 97);
        add(players, "Ashley Cole", "Inglaterra", "LB", "LWB", 55, 70, 76, 91, 79, 97);
        add(players, "Esteban Cambiasso", "Argentina", "CDM", "CM", 53, 75, 75, 87, 69, 96);
        add(players, "Robbie Keane", "Irlanda", "ST", "CF", 77, 64, 29, 86, 81, 96);
        add(players, "Tomáš Rosický", "Tchéquia", "CAM", "CM", 66, 79, 42, 78, 84, 96);
        add(players, "Cristian Chivu", "Romênia", "CB", "LB", 48, 69, 77, 87, 64, 95);
        add(players, "Dirk Kuyt", "Holanda", "ST", "RW", 74, 62, 38, 92, 72, 95);
        add(players, "Seydou Keita", "Mali", "CM", "CDM", 60, 74, 70, 89, 71, 95);
        add(players, "Florent Malouda", "França", "LW", "LM", 69, 71, 38, 89, 82, 95);
        add(players, "Emre Belözoğlu", "Turquia", "CM", "CAM", 61, 78, 55, 82, 82, 95);
        add(players, "Luís Fabiano", "Brasil", "ST", "CF", 76, 57, 28, 88, 74, 95);
        add(players, "Ledley King", "Inglaterra", "CB", "CDM", 40, 65, 77, 91, 59, 95);
        add(players, "Sébastien Frey", "França", "GK", null, 11, 40, 79, 88, 18, 94);
        add(players, "Roman Weidenfeller", "Alemanha", "GK", null, 11, 40, 77, 90, 17, 94);
        add(players, "Diego Lugano", "Uruguai", "CB", null, 40, 61, 77, 92, 53, 94);
        add(players, "Christoph Metzelder", "Alemanha", "CB", null, 35, 61, 76, 91, 52, 94);
        add(players, "Christian Poulsen", "Dinamarca", "CDM", "CM", 49, 67, 72, 90, 62, 94);
        add(players, "John Arne Riise", "Noruega", "LB", "LM", 60, 69, 70, 91, 72, 94);
        add(players, "Sebastian Kehl", "Alemanha", "CDM", "CB", 49, 68, 72, 88, 63, 94);
        add(players, "Yossi Benayoun", "Israel", "CAM", "RM", 64, 76, 40, 76, 82, 93);
        add(players, "Scott Parker", "Inglaterra", "CM", "CDM", 54, 69, 69, 89, 66, 93);
        add(players, "Nikola Žigić", "Iugoslávia", "ST", null, 72, 54, 30, 94, 63, 93);
        add(players, "Alan Smith", "Inglaterra", "ST", "CM", 71, 59, 48, 89, 69, 93);
        add(players, "Enzo Maresca", "Itália", "CM", "CAM", 58, 74, 57, 82, 72, 93);
        add(players, "Zdeněk Grygera", "Tchéquia", "RB", "CB", 46, 63, 73, 88, 61, 93);
        add(players, "Jonathan Woodgate", "Inglaterra", "CB", null, 36, 62, 76, 88, 53, 93);
        add(players, "Wayne Bridge", "Inglaterra", "LB", "LM", 51, 67, 72, 88, 69, 93);
        add(players, "Artur Boruc", "Polônia", "GK", null, 11, 39, 76, 89, 17, 93);
        add(players, "Geovanni", "Brasil", "CAM", "RM", 66, 74, 38, 79, 81, 92);
        add(players, "Mancini", "Brasil", "RM", "RB", 65, 69, 53, 87, 80, 92);
        add(players, "Clemens Fritz", "Alemanha", "RB", "RM", 51, 65, 70, 88, 67, 92);
        add(players, "Sébastien Squillaci", "França", "CB", null, 35, 60, 74, 88, 51, 92);
        add(players, "Jan Kromkamp", "Holanda", "RB", "RM", 49, 65, 70, 87, 65, 92);
        add(players, "Paul Scharner", "Áustria", "CB", "CDM", 46, 64, 70, 92, 60, 92);
        add(players, "Jason Čulina", "Austrália", "CM", "RM", 59, 72, 57, 84, 72, 92);
        add(players, "Ricardo Osorio", "México", "RB", "CB", 45, 62, 70, 88, 62, 92);
        add(players, "Paulo Assunção", "Brasil", "CDM", "CM", 47, 66, 70, 87, 61, 92);
        add(players, "Federico Insúa", "Argentina", "CAM", "CM", 65, 75, 39, 76, 80, 92);
        add(players, "Steed Malbranque", "França", "CAM", "RM", 63, 73, 45, 79, 79, 92);
        add(players, "Cha Du-ri", "Coreia do Sul", "RB", "RM", 57, 64, 65, 93, 70, 91);
        add(players, "César Peixoto", "Portugal", "LB", "LM", 54, 68, 68, 84, 70, 91);
        add(players, "Ernesto Farías", "Argentina", "ST", null, 73, 55, 27, 83, 68, 91);
        add(players, "Luciano Galletti", "Argentina", "RW", "RM", 68, 68, 35, 88, 78, 91);
        add(players, "Javier Chevantón", "Uruguai", "ST", "CF", 73, 58, 28, 87, 72, 91);
        add(players, "Rade Prica", "Suécia", "ST", null, 71, 55, 29, 89, 66, 91);
        add(players, "Moisés Muñoz", "México", "GK", null, 11, 39, 74, 87, 17, 91);
        add(players, "Ceará", "Brasil", "RB", "RWB", 49, 64, 69, 87, 66, 91);
        add(players, "Ibrahim Yattara", "Guiné", "RW", "CAM", 67, 67, 34, 87, 79, 90);
        add(players, "Darius Vassell", "Inglaterra", "ST", "RW", 69, 55, 28, 91, 72, 90);
        add(players, "Marcin Wasilewski", "Polônia", "RB", "CB", 44, 60, 69, 91, 58, 90);
        add(players, "Keith Andrews", "Irlanda", "CM", "CDM", 54, 68, 61, 84, 66, 89);
        add(players, "Robbie Neilson", "Escócia", "RB", "CB", 44, 60, 68, 87, 59, 89);
        add(players, "Abdoulaye Méïté", "Costa do Marfim", "CB", null, 35, 58, 71, 92, 50, 90);
        add(players, "Zat Knight", "Inglaterra", "CB", null, 34, 57, 69, 94, 49, 89);
        add(players, "Amauri", "Brasil", "ST", null, 71, 55, 29, 91, 68, 90);
        add(players, "Robert Green", "Inglaterra", "GK", null, 11, 38, 73, 86, 17, 89);
        add(players, "Peer Kluge", "Alemanha", "CM", "CDM", 53, 68, 61, 83, 66, 88);
        return players;
    }

    private static void add(List<Player> players, String name, String nationality, String primary,
                            String secondary, int attack, int passing, int defense, int physical,
                            int dribbling, int potential) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("ataque", attack); attributes.put("passe", passing);
        attributes.put("defesa", defense); attributes.put("fisico", physical);
        attributes.put("drible", dribbling);
        double salary = 8_000d + Math.max(0, potential - 78) * 800d;
        players.add(new Player(name, nationality, Position.valueOf(primary),
            secondary == null ? null : Position.valueOf(secondary), 18,
            new TechnicalAttributes(attributes), potential, salary));
    }
}
