package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1993, sem nomes usados em anos anteriores. */
public final class DraftClass1993 {
    private DraftClass1993() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "David Beckham", "Inglaterra", "RM", "CM", 18, 68, 84, 47, 80, 80, 97);
        add(players, "Juan Sebastián Verón", "Argentina", "CM", "CAM", 18, 65, 83, 58, 82, 79, 96);
        add(players, "Hernán Crespo", "Argentina", "ST", "CF", 18, 78, 60, 28, 85, 75, 96);
        add(players, "Juninho Pernambucano", "Brasil", "CAM", "CM", 18, 67, 82, 46, 76, 84, 96);
        add(players, "Edgar Davids", "Holanda", "CDM", "CM", 20, 60, 76, 75, 89, 78, 96);
        add(players, "Robbie Fowler", "Inglaterra", "ST", "CF", 18, 77, 58, 28, 82, 74, 95);
        add(players, "Roy Makaay", "Holanda", "ST", null, 18, 74, 58, 28, 84, 72, 95);
        add(players, "Giovanni van Bronckhorst", "Holanda", "LB", "LM", 18, 58, 73, 68, 84, 76, 95);
        add(players, "Gary Neville", "Inglaterra", "RB", "CB", 18, 45, 65, 74, 87, 62, 94);
        add(players, "Míchel Salgado", "Espanha", "RB", "RWB", 18, 48, 64, 73, 89, 66, 94);
        add(players, "Christian Wörns", "Alemanha", "CB", null, 21, 38, 62, 78, 89, 54, 94);
        add(players, "Marc-Vivien Foé", "Camarões", "CM", "CDM", 18, 57, 70, 68, 91, 66, 93);
        add(players, "Mark Viduka", "Austrália", "ST", "CF", 18, 74, 60, 31, 89, 70, 93);
        add(players, "Stefano Fiore", "Itália", "CM", "CAM", 18, 63, 77, 50, 77, 79, 93);
        add(players, "Nicky Butt", "Inglaterra", "CDM", "CM", 18, 50, 68, 72, 87, 64, 93);
        add(players, "Dario Šimić", "Croácia", "RB", "CB", 18, 42, 63, 75, 88, 59, 93);
        add(players, "Kevin Phillips", "Inglaterra", "ST", "CF", 20, 74, 55, 27, 84, 70, 92);
        add(players, "Laurent Robert", "França", "LW", "LM", 18, 69, 72, 34, 85, 82, 92);
        add(players, "Jussi Jääskeläinen", "Finlândia", "GK", null, 18, 11, 39, 76, 88, 17, 92);
        add(players, "John Hartson", "País de Gales", "ST", null, 18, 73, 55, 31, 92, 65, 92);
        add(players, "Craig Moore", "Austrália", "CB", null, 18, 38, 60, 73, 88, 53, 92);
        add(players, "George Boateng", "Holanda", "CDM", "CM", 18, 50, 68, 70, 89, 65, 92);
        add(players, "Andrés Palop", "Espanha", "GK", null, 20, 11, 39, 75, 86, 17, 91);
        add(players, "Paul Okon", "Austrália", "CDM", "CB", 21, 49, 70, 70, 83, 65, 91);
        add(players, "Keith Gillespie", "Irlanda do Norte", "RW", "RM", 18, 67, 67, 36, 88, 80, 91);
        add(players, "Valérien Ismaël", "França", "CB", null, 18, 37, 60, 72, 89, 52, 91);
        add(players, "Fábio Luciano", "Brasil", "CB", null, 18, 38, 60, 72, 86, 54, 91);
        add(players, "Oktay Derelioğlu", "Turquia", "ST", "CF", 18, 72, 57, 27, 84, 72, 91);
        add(players, "İlhan Mansız", "Turquia", "ST", null, 18, 71, 55, 28, 85, 70, 90);
        add(players, "Quim", "Portugal", "GK", null, 18, 11, 38, 75, 86, 17, 91);
        add(players, "Dimitar Ivankov", "Bulgária", "GK", null, 18, 11, 39, 74, 87, 18, 90);
        add(players, "Aleksandrs Koļinko", "Letônia", "GK", null, 18, 11, 38, 73, 87, 18, 89);
        add(players, "Bartosz Bosacki", "Polônia", "CB", null, 18, 36, 59, 71, 85, 52, 90);
        add(players, "Ivaylo Petkov", "Bulgária", "LB", "CB", 18, 45, 62, 69, 85, 62, 89);
        add(players, "Marek Špilár", "Eslováquia", "CB", null, 18, 35, 59, 70, 84, 51, 89);
        add(players, "Ieroklis Stoltidis", "Grécia", "CDM", "CM", 18, 50, 67, 69, 87, 62, 90);
        add(players, "Josip Skoko", "Austrália", "CM", "CAM", 18, 58, 72, 58, 84, 72, 90);
        add(players, "Jonatan Johansson", "Finlândia", "ST", "RW", 18, 69, 60, 28, 86, 73, 90);
        add(players, "Kaba Diawara", "Guiné", "ST", null, 18, 70, 57, 27, 86, 70, 89);
        add(players, "Christopher Wreh", "Libéria", "ST", "CF", 18, 69, 54, 28, 86, 68, 89);
        add(players, "Bruno Ribeiro", "Portugal", "LB", "LM", 18, 49, 65, 67, 82, 68, 88);
        add(players, "Alessandro Pistone", "Itália", "LB", "CB", 18, 47, 63, 70, 86, 65, 90);
        add(players, "Ivan Jurić", "Croácia", "CM", "CDM", 18, 55, 70, 64, 86, 68, 90);
        add(players, "Darren Eadie", "Inglaterra", "LW", "LM", 18, 66, 67, 35, 86, 79, 89);
        add(players, "Paul Agostino", "Austrália", "ST", null, 18, 70, 54, 29, 87, 67, 89);
        add(players, "Bruce Dyer", "Inglaterra", "ST", null, 18, 69, 53, 28, 88, 66, 88);
        add(players, "Rick Hoogendorp", "Holanda", "ST", "CF", 18, 71, 55, 27, 82, 69, 89);
        add(players, "Joakim Persson", "Suécia", "CDM", "CM", 18, 52, 66, 65, 84, 64, 88);
        add(players, "Dennis Gentenaar", "Holanda", "GK", null, 18, 11, 37, 72, 84, 17, 88);
        add(players, "Erol Bulut", "Turquia", "LB", "LM", 18, 48, 64, 67, 85, 66, 88);
        add(players, "Walid Regragui", "Marrocos", "RB", null, 18, 44, 61, 68, 84, 60, 88);
        add(players, "Washington", "Brasil", "ST", null, 18, 72, 54, 27, 87, 67, 90);
        add(players, "Yusuf Şimşek", "Turquia", "CAM", "CM", 18, 63, 72, 43, 78, 77, 89);
        add(players, "Dele Adebola", "Nigéria", "ST", null, 18, 69, 53, 29, 90, 64, 88);
        add(players, "Corrado Grabbi", "Itália", "ST", "CF", 18, 68, 54, 27, 81, 67, 87);
        add(players, "Bengt Sæternes", "Noruega", "ST", null, 18, 70, 53, 29, 88, 64, 88);
        add(players, "Fabio Celestini", "Suíça", "CM", "CDM", 18, 53, 70, 64, 82, 67, 89);
        add(players, "José Manuel Pinto", "Espanha", "GK", null, 18, 11, 39, 74, 84, 17, 90);
        add(players, "Henrique Hilário", "Portugal", "GK", null, 18, 11, 37, 71, 85, 17, 88);
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
