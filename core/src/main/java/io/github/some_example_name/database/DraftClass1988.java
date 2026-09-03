package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1988. */
public final class DraftClass1988 {
    private DraftClass1988() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Cafu", "Brasil", "RB", "RWB", 55, 69, 72, 89, 75, 98);
        add(players, "Alan Shearer", "Inglaterra", "ST", null, 78, 56, 30, 89, 69, 97);
        add(players, "Edwin van der Sar", "Holanda", "GK", null, 12, 45, 78, 86, 20, 97);
        add(players, "Frank de Boer", "Holanda", "CB", "LB", 43, 71, 77, 83, 62, 96);
        add(players, "Luis Enrique", "Espanha", "CM", "RW", 68, 73, 58, 87, 77, 95);
        add(players, "Emmanuel Petit", "França", "CDM", "CM", 53, 72, 72, 86, 66, 95);
        add(players, "Diego Simeone", "Argentina", "CDM", "CM", 58, 69, 73, 88, 66, 95);
        add(players, "Mehmet Scholl", "Alemanha Ocidental", "CAM", "LM", 65, 78, 38, 73, 84, 95);
        add(players, "Alen Bokšić", "Iugoslávia", "ST", "CF", 74, 59, 28, 87, 75, 95);
        add(players, "Christian Karembeu", "França", "CDM", "RM", 50, 68, 70, 91, 65, 94);
        add(players, "Ronald de Boer", "Holanda", "CF", "CAM", 70, 72, 38, 81, 81, 94);
        add(players, "Phillip Cocu", "Holanda", "CM", "LM", 58, 72, 66, 84, 71, 94);
        add(players, "Angelo Peruzzi", "Itália", "GK", null, 11, 39, 78, 87, 18, 94);
        add(players, "Ciriaco Sforza", "Suíça", "CM", "CAM", 58, 76, 57, 80, 73, 94);
        add(players, "Noureddine Naybet", "Marrocos", "CB", null, 38, 61, 76, 86, 54, 94);
        add(players, "Patrick M'Boma", "Camarões", "ST", "CF", 73, 58, 29, 88, 70, 94);
        add(players, "Enrico Chiesa", "Itália", "ST", "RW", 73, 61, 29, 84, 75, 93);
        add(players, "Gareth Southgate", "Inglaterra", "CB", "CDM", 39, 65, 74, 83, 57, 93);
        add(players, "Marco Etcheverry", "Bolívia", "CAM", "CF", 66, 77, 35, 75, 84, 93);
        add(players, "Roberto Di Matteo", "Itália", "CM", "CDM", 59, 73, 64, 82, 70, 93);
        add(players, "Gianluca Pessotto", "Itália", "LB", "RB", 47, 65, 72, 84, 65, 92);
        add(players, "David James", "Inglaterra", "GK", null, 11, 38, 76, 89, 17, 92);
        add(players, "Alexi Lalas", "EUA", "CB", null, 38, 58, 72, 88, 53, 92);
        add(players, "Tugay Kerimoğlu", "Turquia", "CM", "CDM", 56, 75, 59, 79, 73, 92);
        add(players, "Alain Boghossian", "França", "CM", "CDM", 55, 70, 64, 86, 66, 92);
        add(players, "Pär Zetterberg", "Suécia", "CAM", "CM", 59, 77, 41, 74, 79, 92);
        add(players, "Stéphane Guivarc'h", "França", "ST", null, 72, 53, 28, 83, 65, 91);
        add(players, "Tony Vidmar", "Austrália", "LB", "CB", 44, 61, 70, 85, 61, 91);
        add(players, "Winston Bogarde", "Holanda", "CB", "LB", 37, 60, 72, 89, 57, 91);
        add(players, "Celso Ayala", "Paraguai", "CB", null, 40, 62, 73, 87, 54, 91);
        add(players, "Gert Verheyen", "Bélgica", "RW", "ST", 68, 64, 35, 84, 73, 91);
        add(players, "Igor Cvitanović", "Iugoslávia", "ST", null, 72, 54, 27, 81, 68, 91);
        add(players, "Cobi Jones", "EUA", "RW", "RM", 65, 65, 39, 88, 78, 90);
        add(players, "Sergei Kiriakov", "URSS", "ST", "LW", 69, 63, 30, 82, 75, 90);
        add(players, "Roar Strand", "Noruega", "CM", "RM", 57, 69, 59, 86, 68, 90);
        add(players, "Yves Vanderhaeghe", "Bélgica", "CDM", "CM", 48, 65, 68, 86, 60, 90);
        add(players, "Chris Coleman", "País de Gales", "CB", "RB", 38, 59, 71, 86, 54, 90);
        add(players, "David Weir", "Escócia", "CB", null, 35, 59, 72, 85, 51, 90);
        add(players, "Barry van Galen", "Holanda", "CAM", "CM", 62, 72, 42, 77, 79, 90);
        add(players, "Gaston Taument", "Holanda", "RW", "RM", 66, 65, 35, 85, 78, 90);
        add(players, "Kevin Campbell", "Inglaterra", "ST", null, 70, 52, 29, 87, 65, 90);
        add(players, "Miguel Ramírez", "Chile", "CB", "LB", 39, 61, 70, 84, 57, 89);
        add(players, "Rodrigo Barrera", "Chile", "ST", "RW", 68, 58, 29, 84, 71, 89);
        add(players, "Mark Pembridge", "País de Gales", "CM", "LM", 56, 69, 55, 82, 68, 89);
        add(players, "Chris Henderson", "EUA", "RM", "CM", 58, 67, 48, 84, 71, 88);
        add(players, "Mark Chung", "EUA", "LM", "CM", 58, 68, 47, 82, 72, 88);
        add(players, "Erik ten Hag", "Holanda", "CB", "CDM", 37, 62, 68, 80, 55, 87);
        add(players, "Fernando Vergara", "Chile", "ST", "CF", 67, 55, 28, 82, 67, 87);
        add(players, "Mike Lapper", "EUA", "CB", null, 34, 57, 69, 83, 50, 87);
        add(players, "Guido Alvarenga", "Paraguai", "CAM", "CM", 59, 70, 43, 76, 73, 87);
        add(players, "Kim Byung-ji", "Coreia do Sul", "GK", null, 12, 40, 71, 85, 20, 88);
        add(players, "Daniel Borimirov", "Bulgária", "CM", "RM", 57, 68, 54, 82, 68, 87);
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

