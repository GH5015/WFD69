package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1992. */
public final class DraftClass1992 {
    private DraftClass1992() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Alessandro Del Piero", "Itália", "CF", "CAM", 75, 78, 29, 76, 88, 98);
        add(players, "Paul Scholes", "Inglaterra", "CM", "CAM", 62, 80, 54, 78, 79, 97);
        add(players, "Sol Campbell", "Inglaterra", "CB", null, 37, 60, 80, 91, 54, 96);
        add(players, "Ariel Ortega", "Argentina", "CAM", "RW", 70, 77, 32, 78, 89, 96);
        add(players, "Marcelo Salas", "Chile", "ST", "CF", 77, 59, 29, 86, 74, 96);
        add(players, "Gaizka Mendieta", "Espanha", "CM", "RM", 61, 76, 58, 84, 76, 95);
        add(players, "Zé Roberto", "Brasil", "LM", "LB", 61, 74, 63, 86, 80, 95);
        add(players, "Claudio López", "Argentina", "ST", "LW", 74, 60, 28, 90, 79, 95);
        add(players, "Sávio", "Brasil", "LW", "CAM", 67, 73, 34, 81, 85, 94);
        add(players, "Márcio Amoroso", "Brasil", "ST", "CF", 75, 60, 28, 84, 78, 94);
        add(players, "Sylvain Wiltord", "França", "RW", "ST", 71, 63, 30, 87, 77, 94);
        add(players, "Serhiy Rebrov", "Ucrânia", "ST", "CAM", 73, 66, 31, 82, 77, 94);
        add(players, "Nolberto Solano", "Peru", "RM", "RB", 62, 74, 60, 80, 75, 94);
        add(players, "Jens Nowotny", "Alemanha", "CB", null, 35, 62, 77, 87, 53, 94);
        add(players, "Sylvinho", "Brasil", "LB", "LM", 52, 70, 71, 84, 74, 94);
        add(players, "Nii Lamptey", "Gana", "CAM", "RW", 66, 72, 34, 77, 85, 94);
        add(players, "Vincenzo Montella", "Itália", "ST", "CF", 74, 55, 27, 80, 72, 93);
        add(players, "Robert Kovač", "Croácia", "CB", null, 36, 61, 76, 88, 54, 93);
        add(players, "Jens Jeremies", "Alemanha", "CDM", "CM", 49, 66, 72, 89, 60, 93);
        add(players, "Sérgio Conceição", "Portugal", "RW", "RM", 68, 68, 35, 87, 79, 93);
        add(players, "Costinha", "Portugal", "CDM", "CM", 47, 68, 71, 86, 61, 93);
        add(players, "Vampeta", "Brasil", "CM", "CDM", 58, 73, 65, 81, 73, 93);
        add(players, "Karim Bagheri", "Irã", "CM", "CDM", 61, 73, 68, 88, 69, 93);
        add(players, "Hans-Jörg Butt", "Alemanha", "GK", null, 20, 48, 76, 87, 19, 93);
        add(players, "Carsten Ramelow", "Alemanha", "CDM", "CB", 45, 65, 71, 87, 59, 92);
        add(players, "Taribo West", "Nigéria", "CB", "LB", 37, 57, 75, 91, 53, 92);
        add(players, "Jordi Cruyff", "Holanda", "CAM", "CF", 67, 72, 39, 79, 77, 92);
        add(players, "Jesper Blomqvist", "Suécia", "LW", "LM", 67, 68, 36, 88, 82, 92);
        add(players, "Olivier Dacourt", "França", "CM", "CDM", 53, 69, 69, 87, 64, 92);
        add(players, "Nuno Valente", "Portugal", "LB", "LWB", 47, 65, 72, 84, 64, 92);
        add(players, "Iván Campo", "Espanha", "CB", "CDM", 41, 63, 73, 88, 57, 92);
        add(players, "Stéphane Henchoz", "Suíça", "CB", null, 34, 59, 76, 86, 50, 92);
        add(players, "Gary Kelly", "Irlanda", "RB", "RWB", 48, 64, 71, 89, 66, 92);
        add(players, "Stelios Giannakopoulos", "Grécia", "RW", "CAM", 67, 70, 40, 82, 78, 92);
        add(players, "Nick Barmby", "Inglaterra", "CAM", "LM", 66, 72, 40, 78, 78, 92);
        add(players, "Ulises de la Cruz", "Equador", "RB", "RM", 55, 65, 69, 91, 70, 92);
        add(players, "Julio Ricardo Cruz", "Argentina", "ST", null, 73, 55, 29, 90, 65, 92);
        add(players, "Muzzy Izzet", "Turquia", "CM", "CAM", 58, 73, 53, 81, 72, 91);
        add(players, "Matt Holland", "Irlanda", "CM", "CDM", 57, 69, 61, 86, 68, 91);
        add(players, "Agustín Delgado", "Equador", "ST", null, 72, 54, 30, 91, 65, 91);
        add(players, "Mark Fish", "África do Sul", "CB", null, 36, 60, 73, 88, 53, 91);
        add(players, "Vladimir Beschastnykh", "Rússia", "ST", null, 71, 54, 28, 86, 67, 91);
        add(players, "Tümer Metin", "Turquia", "CAM", "RW", 64, 73, 39, 79, 77, 91);
        add(players, "Denis Caniza", "Paraguai", "CB", "RB", 39, 62, 72, 87, 58, 91);
        add(players, "Jörg Böhme", "Alemanha", "LM", "LB", 59, 69, 56, 86, 74, 91);
        add(players, "Roman Berezovsky", "Armênia", "GK", null, 11, 39, 75, 86, 17, 91);
        add(players, "Khalilou Fadiga", "Senegal", "LM", "CAM", 64, 72, 42, 82, 79, 91);
        add(players, "Lee Carsley", "Irlanda", "CDM", "CM", 49, 66, 68, 86, 61, 90);
        add(players, "Robbie Savage", "País de Gales", "CM", "CDM", 54, 67, 61, 88, 66, 90);
        add(players, "Yaw Preko", "Gana", "ST", "RW", 69, 59, 30, 86, 72, 90);
        add(players, "Daniel Nannskog", "Suécia", "ST", null, 70, 53, 27, 84, 67, 90);
        add(players, "Hámilton Ricard", "Colômbia", "ST", null, 71, 53, 28, 90, 66, 90);
        add(players, "Pascal Cygan", "França", "CB", null, 33, 57, 72, 88, 49, 90);
        add(players, "Ivo Ulich", "Tchéquia", "LM", "CAM", 62, 70, 44, 82, 75, 90);
        add(players, "Bernard Diomède", "França", "LW", "LM", 64, 65, 38, 86, 76, 90);
        add(players, "Dominic Matteo", "Escócia", "LB", "CB", 42, 62, 70, 87, 58, 90);
        add(players, "Dejan Stefanović", "Iugoslávia", "CB", "RB", 38, 61, 71, 86, 56, 90);
        add(players, "Sander Westerveld", "Holanda", "GK", null, 11, 39, 75, 87, 17, 90);
        add(players, "Mario Frick", "Liechtenstein", "ST", "CF", 69, 55, 29, 87, 66, 89);
        add(players, "Nuno Espírito Santo", "Portugal", "GK", null, 11, 38, 72, 85, 17, 88);
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
