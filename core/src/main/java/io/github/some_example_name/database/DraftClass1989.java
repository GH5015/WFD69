package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1989 (sem repetir Thomas Helmer, já usado em 1986). */
public final class DraftClass1989 {
    private DraftClass1989() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Roy Keane", "Irlanda", "CM", "CDM", 57, 70, 72, 91, 66, 97);
        add(players, "Pep Guardiola", "Espanha", "CDM", "CM", 48, 78, 68, 73, 68, 96);
        add(players, "Jari Litmanen", "Finlândia", "CAM", "CF", 70, 79, 39, 77, 84, 96);
        add(players, "Henrik Larsson", "Suécia", "ST", "CF", 74, 62, 29, 84, 77, 96);
        add(players, "Demetrio Albertini", "Itália", "CM", "CDM", 55, 78, 65, 79, 70, 95);
        add(players, "Carlos Gamarra", "Paraguai", "CB", null, 36, 62, 77, 86, 54, 95);
        add(players, "Fabien Barthez", "França", "GK", null, 12, 43, 78, 87, 20, 95);
        add(players, "Francesco Toldo", "Itália", "GK", null, 11, 39, 77, 89, 17, 95);
        add(players, "Edmundo", "Brasil", "CF", "ST", 74, 67, 28, 82, 84, 94);
        add(players, "Zlatko Zahovič", "Iugoslávia", "CAM", "CF", 67, 77, 35, 76, 81, 94);
        add(players, "Paolo Montero", "Uruguai", "CB", "LB", 40, 61, 76, 90, 55, 94);
        add(players, "Patrik Andersson", "Suécia", "CB", "SW", 39, 65, 76, 86, 57, 94);
        add(players, "Andy Cole", "Inglaterra", "ST", null, 75, 55, 27, 86, 72, 94);
        add(players, "Dwight Yorke", "Trinidad e Tobago", "ST", "CF", 72, 65, 29, 84, 78, 94);
        add(players, "Marcelinho Carioca", "Brasil", "CAM", "RM", 66, 78, 39, 74, 83, 93);
        add(players, "Finidi George", "Nigéria", "RW", "RM", 68, 68, 38, 87, 80, 93);
        add(players, "Luigi Di Biagio", "Itália", "CM", "CDM", 54, 71, 68, 85, 65, 93);
        add(players, "Thomas Helveg", "Dinamarca", "RB", "RM", 50, 67, 71, 87, 66, 93);
        add(players, "Hakan Şükür", "Turquia", "ST", null, 73, 55, 29, 88, 67, 93);
        add(players, "Johan Mjällby", "Suécia", "CB", "CDM", 39, 61, 73, 89, 54, 93);
        add(players, "Niko Kovač", "Iugoslávia", "CM", "CDM", 55, 70, 67, 85, 66, 93);
        add(players, "Mustapha Hadji", "Marrocos", "CAM", "RW", 65, 72, 37, 80, 82, 93);
        add(players, "Yoo Sang-chul", "Coreia do Sul", "CM", "CB", 60, 68, 68, 89, 67, 93);
        add(players, "Francisco Arce", "Paraguai", "RB", "RWB", 51, 71, 70, 82, 68, 92);
        add(players, "Antonis Nikopolidis", "Grécia", "GK", null, 11, 38, 75, 84, 17, 92);
        add(players, "Ulrich van Gobbel", "Holanda", "RB", "CB", 43, 59, 70, 91, 62, 92);
        add(players, "Giuseppe Pancaro", "Itália", "LB", "RB", 47, 64, 70, 84, 64, 92);
        add(players, "Jörg Albertz", "Alemanha", "CM", "LM", 61, 73, 59, 86, 71, 92);
        add(players, "Allan Nielsen", "Dinamarca", "CM", "RM", 57, 70, 59, 85, 68, 92);
        add(players, "Joachim Björklund", "Suécia", "CB", null, 35, 59, 73, 86, 51, 92);
        add(players, "Gilles De Bilde", "Bélgica", "ST", "CF", 72, 61, 28, 82, 73, 92);
        add(players, "Håkan Mild", "Suécia", "CM", "CDM", 53, 68, 66, 87, 64, 92);
        add(players, "Neil Lennon", "Irlanda do Norte", "CDM", "CM", 46, 67, 69, 84, 59, 92);
        add(players, "Sergi Barjuán", "Espanha", "LB", "LWB", 51, 68, 71, 86, 70, 92);
        add(players, "Niclas Alexandersson", "Suécia", "RM", "RB", 60, 68, 53, 86, 73, 91);
        add(players, "Jorge Costa", "Portugal", "CB", null, 34, 57, 73, 89, 48, 91);
        add(players, "Theodoros Zagorakis", "Grécia", "CM", "CDM", 53, 69, 65, 86, 65, 91);
        add(players, "José Francisco Cevallos", "Equador", "GK", null, 11, 38, 74, 85, 17, 91);
        add(players, "Miguel Calero", "Colômbia", "GK", null, 12, 40, 75, 88, 19, 91);
        add(players, "Mikel Lasa", "Espanha", "LB", "RB", 47, 63, 70, 84, 63, 91);
        add(players, "Martin Reim", "Estônia", "CM", "RM", 57, 69, 54, 82, 69, 90);
        add(players, "Jerzy Brzęczek", "Polônia", "CM", "RM", 57, 69, 55, 83, 69, 90);
        add(players, "Liviu Ciobotariu", "Romênia", "CB", null, 36, 59, 71, 85, 52, 90);
        add(players, "Francisco Gabriel de Anda", "México", "CB", "CDM", 40, 62, 70, 86, 55, 90);
        add(players, "Enrique Romero", "Espanha", "LB", null, 47, 64, 69, 86, 64, 90);
        add(players, "Edílson", "Brasil", "ST", "RW", 71, 61, 27, 82, 78, 91);
        add(players, "Jovan Stanković", "Iugoslávia", "LM", "LW", 63, 69, 38, 81, 76, 89);
        add(players, "Bert Konterman", "Holanda", "CB", "CDM", 39, 63, 70, 84, 57, 89);
        add(players, "Jesper Jansson", "Suécia", "CB", "CDM", 39, 64, 69, 83, 58, 89);
        add(players, "Juan Carlos Plata", "Guatemala", "ST", null, 70, 54, 27, 81, 67, 89);
        add(players, "Slobodan Komljenović", "Iugoslávia", "CB", "RB", 38, 59, 69, 84, 55, 88);
        add(players, "Elivélton", "Brasil", "LW", "LM", 65, 67, 34, 80, 78, 88);
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

