package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1994. */
public final class DraftClass1994 {
    private DraftClass1994() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Ronaldo", "Brasil", "ST", "CF", 18, 83, 65, 28, 90, 92, 99);
        add(players, "Francesco Totti", "Itália", "CF", "CAM", 18, 74, 82, 35, 78, 86, 98);
        add(players, "Andriy Shevchenko", "Ucrânia", "ST", "LW", 18, 78, 62, 29, 88, 78, 97);
        add(players, "Clarence Seedorf", "Holanda", "CM", "CAM", 18, 65, 80, 65, 87, 82, 97);
        add(players, "Alessandro Nesta", "Itália", "CB", "SW", 18, 38, 62, 81, 88, 58, 97);
        add(players, "Patrick Vieira", "França", "CM", "CDM", 18, 53, 72, 76, 92, 68, 96);
        add(players, "Michael Ballack", "Alemanha", "CM", "CAM", 18, 64, 75, 68, 88, 72, 96);
        add(players, "Patrick Kluivert", "Holanda", "ST", "CF", 18, 78, 63, 30, 85, 77, 96);
        add(players, "Javier Zanetti", "Argentina", "RB", "RM", 21, 62, 74, 77, 91, 76, 96);
        add(players, "Ruud van Nistelrooy", "Holanda", "ST", null, 18, 77, 58, 28, 84, 72, 95);
        add(players, "Álvaro Recoba", "Uruguai", "CF", "CAM", 18, 74, 77, 30, 78, 88, 95);
        add(players, "Nwankwo Kanu", "Nigéria", "CF", "CAM", 18, 72, 70, 29, 86, 84, 95);
        add(players, "Guti", "Espanha", "CAM", "CM", 18, 66, 80, 44, 75, 83, 95);
        add(players, "Gilberto Silva", "Brasil", "CDM", "CM", 18, 54, 70, 74, 87, 66, 94);
        add(players, "Emerson", "Brasil", "CDM", "CM", 18, 58, 72, 73, 89, 70, 94);
        add(players, "Marcos Senna", "Brasil", "CDM", "CM", 18, 57, 74, 72, 85, 69, 94);
        add(players, "Juan Pablo Sorín", "Argentina", "LB", "LM", 18, 58, 72, 75, 89, 73, 94);
        add(players, "Iván Córdoba", "Colômbia", "CB", "RB", 18, 38, 61, 78, 91, 58, 94);
        add(players, "Samuel Kuffour", "Gana", "CB", null, 18, 40, 60, 78, 92, 56, 94);
        add(players, "Marcelo Gallardo", "Argentina", "CAM", "CM", 18, 65, 79, 42, 75, 84, 94);
        add(players, "Mauro Camoranesi", "Argentina", "RM", "RW", 18, 67, 72, 45, 84, 81, 93);
        add(players, "Boudewijn Zenden", "Holanda", "LM", "LB", 18, 67, 72, 50, 88, 80, 93);
        add(players, "Torsten Frings", "Alemanha", "CM", "CDM", 18, 60, 71, 67, 91, 69, 93);
        add(players, "Edmílson", "Brasil", "CB", "CDM", 18, 46, 67, 76, 88, 63, 93);
        add(players, "Roque Júnior", "Brasil", "CB", null, 18, 38, 60, 76, 90, 54, 93);
        add(players, "Fernando Morientes", "Espanha", "ST", null, 18, 75, 57, 31, 87, 68, 93);
        add(players, "Nuno Gomes", "Portugal", "ST", "CF", 18, 74, 60, 28, 83, 74, 93);
        add(players, "Jon Dahl Tomasson", "Dinamarca", "ST", "CF", 18, 72, 63, 35, 85, 72, 93);
        add(players, "Thomas Gravesen", "Dinamarca", "CM", "CDM", 18, 56, 69, 68, 92, 66, 92);
        add(players, "Shay Given", "Irlanda", "GK", null, 18, 11, 39, 78, 88, 17, 92);
        add(players, "Thomas Sørensen", "Dinamarca", "GK", null, 18, 11, 39, 77, 90, 17, 92);
        add(players, "Rigobert Song", "Camarões", "CB", "RB", 18, 40, 61, 77, 92, 57, 92);
        add(players, "Massimo Oddo", "Itália", "RB", "RWB", 18, 50, 68, 71, 85, 66, 92);
        add(players, "Juliano Belletti", "Brasil", "RB", "RM", 18, 54, 69, 70, 88, 72, 92);
        add(players, "Steve Finnan", "Irlanda", "RB", "RM", 18, 47, 66, 72, 87, 65, 92);
        add(players, "Paulo Wanchope", "Costa Rica", "ST", "CF", 18, 73, 57, 29, 90, 73, 92);
        add(players, "Sebastián Abreu", "Uruguai", "ST", null, 18, 72, 56, 28, 89, 66, 92);
        add(players, "Simone Inzaghi", "Itália", "ST", "CF", 18, 73, 55, 27, 82, 68, 92);
        add(players, "Diego Tristán", "Espanha", "ST", "CF", 18, 72, 60, 28, 84, 73, 91);
        add(players, "Iván de la Peña", "Espanha", "CM", "CAM", 18, 59, 81, 40, 72, 85, 92);
        add(players, "Anders Svensson", "Suécia", "CM", "CAM", 18, 61, 75, 55, 81, 74, 91);
        add(players, "Jacek Krzynówek", "Polônia", "LM", "LW", 18, 64, 70, 50, 88, 77, 91);
        add(players, "Denny Landzaat", "Holanda", "CM", "CDM", 18, 55, 73, 64, 82, 70, 91);
        add(players, "Lars Ricken", "Alemanha", "CAM", "CF", 18, 66, 71, 41, 82, 76, 91);
        add(players, "Santiago Solari", "Argentina", "LM", "CAM", 18, 64, 72, 50, 83, 78, 91);
        add(players, "Amado Guevara", "Honduras", "CAM", "CM", 18, 64, 76, 45, 79, 80, 91);
        add(players, "Francisco Rufete", "Espanha", "RW", "RM", 18, 66, 68, 36, 85, 78, 90);
        add(players, "Timmy Simons", "Bélgica", "CDM", "CB", 18, 48, 66, 71, 89, 61, 90);
        add(players, "Stephen Carr", "Irlanda", "RB", "RWB", 18, 48, 63, 72, 89, 64, 90);
        add(players, "Steffen Iversen", "Noruega", "ST", null, 18, 71, 56, 30, 89, 65, 90);
        add(players, "Stern John", "Trinidad e Tobago", "ST", null, 18, 70, 55, 28, 86, 67, 89);
        add(players, "Aliou Cissé", "Senegal", "CDM", "CB", 18, 46, 65, 69, 88, 60, 90);
        add(players, "Hasan Şaş", "Turquia", "LW", "RM", 18, 67, 67, 39, 87, 79, 90);
        add(players, "Pál Dárdai", "Hungria", "CM", "CDM", 18, 53, 70, 64, 85, 67, 90);
        add(players, "Krisztián Lisztes", "Hungria", "CAM", "CM", 18, 61, 75, 49, 78, 78, 90);
        add(players, "Ümit Özat", "Turquia", "RB", "LB", 18, 48, 64, 70, 86, 64, 89);
        add(players, "John Aloisi", "Austrália", "ST", null, 18, 70, 55, 29, 88, 66, 89);
        add(players, "Marcelo Bordon", "Brasil", "CB", null, 18, 40, 60, 72, 90, 53, 90);
        add(players, "Duilio Davino", "México", "CB", null, 18, 38, 59, 70, 87, 52, 88);
        add(players, "Seigo Narazaki", "Japão", "GK", null, 18, 11, 38, 74, 86, 17, 90);
        return players;
    }

    private static void add(List<Player> players, String name, String nationality, String primary,
                            String secondary, int age, int attack, int passing, int defense,
                            int physical, int dribbling, int potential) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("ataque", attack); attributes.put("passe", passing);
        attributes.put("defesa", defense); attributes.put("fisico", physical);
        attributes.put("drible", dribbling);
        double salary = 8_000d + Math.max(0, potential - 78) * 800d;
        players.add(new Player(name, nationality, Position.valueOf(primary),
            secondary == null ? null : Position.valueOf(secondary), age,
            new TechnicalAttributes(attributes), potential, salary));
    }
}
