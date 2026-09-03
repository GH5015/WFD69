package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1991. */
public final class DraftClass1991 {
    private DraftClass1991() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Ryan Giggs", "País de Gales", "LW", "LM", 70, 75, 38, 86, 88, 97);
        add(players, "Roberto Carlos", "Brasil", "LB", "LWB", 60, 70, 73, 91, 79, 97);
        add(players, "Fabio Cannavaro", "Itália", "CB", null, 35, 59, 79, 88, 52, 97);
        add(players, "Claude Makélélé", "França", "CDM", "CM", 47, 69, 74, 86, 65, 96);
        add(players, "Jay-Jay Okocha", "Nigéria", "CAM", "RW", 67, 75, 32, 77, 89, 96);
        add(players, "Filippo Inzaghi", "Itália", "ST", null, 77, 53, 25, 80, 68, 95);
        add(players, "Robert Pirès", "França", "LW", "CAM", 67, 74, 38, 81, 83, 95);
        add(players, "Christian Vieri", "Itália", "ST", null, 76, 55, 29, 90, 67, 95);
        add(players, "Ole Gunnar Solskjær", "Noruega", "ST", "CF", 74, 60, 28, 81, 72, 94);
        add(players, "Sami Hyypiä", "Finlândia", "CB", null, 35, 62, 77, 88, 51, 94);
        add(players, "Roberto Ayala", "Argentina", "CB", null, 36, 61, 77, 87, 52, 94);
        add(players, "Jan Koller", "Tchecoslováquia", "ST", null, 73, 54, 29, 94, 62, 94);
        add(players, "Dida", "Brasil", "GK", null, 11, 41, 78, 87, 18, 94);
        add(players, "Juninho Paulista", "Brasil", "CAM", "CM", 66, 77, 37, 72, 84, 94);
        add(players, "Bernd Schneider", "Alemanha", "RM", "CAM", 62, 74, 48, 79, 78, 93);
        add(players, "Shota Arveladze", "URSS", "ST", "CF", 73, 59, 28, 83, 72, 93);
        add(players, "Patrik Berger", "Tchecoslováquia", "LM", "CAM", 65, 73, 43, 83, 79, 93);
        add(players, "Vladimír Šmicer", "Tchecoslováquia", "CAM", "RW", 66, 74, 37, 79, 80, 93);
        add(players, "Johan Micoud", "França", "CAM", "CM", 63, 77, 40, 72, 80, 93);
        add(players, "Cuauhtémoc Blanco", "México", "CF", "CAM", 69, 71, 31, 78, 82, 93);
        add(players, "Marco Materazzi", "Itália", "CB", null, 41, 60, 73, 91, 51, 93);
        add(players, "Mário Jardel", "Brasil", "ST", null, 76, 52, 27, 89, 64, 93);
        add(players, "Tore André Flo", "Noruega", "ST", null, 72, 57, 29, 88, 69, 92);
        add(players, "Victor Ikpeba", "Nigéria", "ST", "LW", 72, 59, 28, 88, 74, 92);
        add(players, "Tijjani Babangida", "Nigéria", "RW", null, 66, 63, 33, 92, 80, 92);
        add(players, "Michael Reiziger", "Holanda", "RB", "CB", 46, 63, 72, 87, 62, 92);
        add(players, "Vincent Candela", "França", "LB", "RB", 52, 67, 71, 85, 68, 92);
        add(players, "Tomáš Galásek", "Tchecoslováquia", "CDM", "CM", 48, 68, 70, 84, 61, 92);
        add(players, "Rogério Ceni", "Brasil", "GK", null, 22, 54, 75, 84, 24, 92);
        add(players, "Essam El-Hadary", "Egito", "GK", null, 12, 39, 76, 88, 18, 92);
        add(players, "Aílton", "Brasil", "ST", null, 73, 55, 27, 87, 70, 91);
        add(players, "Pauleta", "Portugal", "ST", null, 73, 54, 27, 84, 68, 92);
        add(players, "Martín Palermo", "Argentina", "ST", null, 74, 53, 29, 90, 64, 92);
        add(players, "Claudio Reyna", "EUA", "CM", "CAM", 57, 74, 57, 80, 72, 92);
        add(players, "Bart Goor", "Bélgica", "LM", "CM", 62, 70, 52, 84, 72, 91);
        add(players, "Oliver Neuville", "Alemanha", "ST", "CF", 71, 60, 29, 84, 72, 91);
        add(players, "Jared Borgetti", "México", "ST", null, 72, 52, 28, 89, 63, 91);
        add(players, "Jesús Arellano", "México", "RW", "RM", 65, 67, 35, 88, 78, 91);
        add(players, "Oswaldo Sánchez", "México", "GK", null, 11, 38, 75, 86, 17, 91);
        add(players, "Carlos Pavón", "Honduras", "ST", null, 71, 54, 27, 84, 67, 90);
        add(players, "Fabián O'Neill", "Uruguai", "CAM", "CM", 62, 74, 42, 76, 78, 91);
        add(players, "Milan Rapaić", "Iugoslávia", "LW", "CAM", 65, 70, 37, 80, 79, 91);
        add(players, "Demis Nikolaidis", "Grécia", "ST", "CF", 72, 57, 28, 83, 72, 91);
        add(players, "Chris Sutton", "Inglaterra", "ST", "CF", 71, 58, 35, 89, 66, 91);
        add(players, "Tomasz Radzinski", "Canadá", "ST", "RW", 69, 60, 28, 87, 73, 90);
        add(players, "Tony Popović", "Austrália", "CB", null, 36, 59, 71, 87, 52, 90);
        add(players, "Marcus Allbäck", "Suécia", "ST", null, 70, 55, 28, 84, 67, 90);
        add(players, "Steven Pressley", "Escócia", "CB", null, 34, 58, 71, 88, 49, 90);
        add(players, "Igli Tare", "Albânia", "ST", null, 69, 52, 29, 90, 62, 89);
        add(players, "Zisis Vryzas", "Grécia", "ST", "CF", 68, 54, 29, 86, 65, 89);
        add(players, "Rafael Dudamel", "Venezuela", "GK", null, 11, 39, 73, 86, 18, 89);
        add(players, "Pablo Paz", "Argentina", "CB", null, 35, 58, 70, 85, 51, 89);
        add(players, "Valerio Bertotto", "Itália", "RB", "CB", 43, 60, 70, 84, 59, 89);
        add(players, "Youssef Chippo", "Marrocos", "CM", "CDM", 54, 68, 62, 83, 66, 89);
        add(players, "Petar Miloševski", "Iugoslávia", "GK", null, 11, 37, 72, 84, 17, 88);
        add(players, "Lee Woon-jae", "Coreia do Sul", "GK", null, 11, 38, 73, 85, 17, 90);
        add(players, "Peter Hoekstra", "Holanda", "LW", "RW", 64, 66, 34, 82, 76, 88);
        add(players, "Zé Maria", "Brasil", "RB", "RWB", 48, 63, 69, 86, 65, 89);
        add(players, "Marcos", "Brasil", "GK", null, 11, 38, 74, 87, 17, 91);
        add(players, "Ray Parlour", "Inglaterra", "CM", "RM", 56, 68, 60, 87, 67, 90);
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

