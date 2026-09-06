package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1995. */
public final class DraftClass1995 {
    private DraftClass1995() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Thierry Henry", "França", "ST", "LW", 78, 68, 30, 88, 86, 98);
        add(players, "Raúl", "Espanha", "CF", "ST", 79, 70, 31, 82, 83, 98);
        add(players, "David Trezeguet", "França", "ST", null, 78, 56, 28, 87, 70, 96);
        add(players, "Gianluca Zambrotta", "Itália", "RB", "LM", 59, 69, 73, 90, 76, 96);
        add(players, "Deco", "Portugal", "CAM", "CM", 67, 81, 48, 78, 84, 96);
        add(players, "Alex de Souza", "Brasil", "CAM", "CF", 71, 82, 37, 76, 86, 95);
        add(players, "Mark van Bommel", "Holanda", "CDM", "CM", 59, 75, 71, 88, 69, 95);
        add(players, "Hidetoshi Nakata", "Japão", "CAM", "CM", 65, 79, 45, 82, 82, 95);
        add(players, "Fredrik Ljungberg", "Suécia", "RM", "CAM", 67, 71, 44, 89, 81, 95);
        add(players, "Antonio Di Natale", "Itália", "ST", "LW", 74, 66, 28, 83, 80, 95);
        add(players, "Luca Toni", "Itália", "ST", null, 75, 54, 30, 91, 65, 95);
        add(players, "William Gallas", "França", "CB", "LB", 38, 61, 77, 91, 55, 95);
        add(players, "Hasan Salihamidžić", "Bósnia", "RM", "RB", 63, 68, 63, 91, 77, 94);
        add(players, "Massimo Ambrosini", "Itália", "CDM", "CM", 51, 69, 71, 89, 63, 94);
        add(players, "David Albelda", "Espanha", "CDM", "CM", 48, 68, 74, 87, 61, 94);
        add(players, "Willy Sagnol", "França", "RB", "RWB", 51, 70, 73, 87, 67, 94);
        add(players, "Maniche", "Portugal", "CM", "CAM", 62, 75, 61, 87, 72, 94);
        add(players, "Benni McCarthy", "África do Sul", "ST", "CF", 75, 62, 29, 85, 74, 94);
        add(players, "Olof Mellberg", "Suécia", "CB", "RB", 36, 60, 76, 91, 52, 94);
        add(players, "Frédéric Kanouté", "Mali", "ST", "CF", 73, 63, 30, 91, 73, 94);
        add(players, "Denílson de Oliveira", "Brasil", "LW", "LM", 66, 70, 31, 88, 89, 94);
        add(players, "Fabio Grosso", "Itália", "LB", "LM", 54, 68, 72, 86, 69, 93);
        add(players, "Simone Perrotta", "Itália", "CM", "CAM", 60, 72, 64, 88, 70, 93);
        add(players, "Mehdi Mahdavikia", "Irã", "RM", "RB", 64, 69, 54, 91, 78, 93);
        add(players, "Marek Jankulovski", "Tchéquia", "LB", "LM", 58, 71, 68, 87, 74, 93);
        add(players, "Mikaël Silvestre", "França", "LB", "CB", 44, 62, 73, 90, 61, 93);
        add(players, "Lauren", "Camarões", "RB", "RM", 55, 67, 71, 89, 70, 93);
        add(players, "Levan Kobiashvili", "Geórgia", "LB", "LM", 55, 70, 68, 86, 72, 92);
        add(players, "Lee Young-pyo", "Coreia do Sul", "LB", "RB", 52, 68, 71, 88, 72, 93);
        add(players, "Kim Nam-il", "Coreia do Sul", "CDM", "CM", 47, 67, 70, 87, 61, 92);
        add(players, "Ian Harte", "Irlanda", "LB", "LM", 55, 71, 69, 84, 66, 92);
        add(players, "Phil Neville", "Inglaterra", "RB", "CDM", 45, 64, 71, 88, 62, 92);
        add(players, "Jesper Grønkjær", "Dinamarca", "LW", "RW", 66, 67, 35, 91, 81, 92);
        add(players, "Danny Murphy", "Inglaterra", "CM", "CAM", 61, 74, 54, 81, 72, 92);
        add(players, "Lee Bowyer", "Inglaterra", "CM", "RM", 59, 69, 61, 89, 69, 92);
        add(players, "Pablo García", "Uruguai", "CDM", "CM", 51, 68, 70, 89, 62, 92);
        add(players, "Hakan Yakin", "Suíça", "CAM", "CF", 66, 76, 38, 76, 79, 92);
        add(players, "Marek Mintál", "Eslováquia", "CAM", "ST", 68, 73, 39, 80, 76, 92);
        add(players, "Morgan De Sanctis", "Itália", "GK", null, 11, 40, 76, 87, 17, 92);
        add(players, "Christian Abbiati", "Itália", "GK", null, 11, 39, 77, 90, 17, 93);
        add(players, "Robert Enke", "Alemanha", "GK", null, 11, 40, 76, 87, 17, 92);
        add(players, "Luís Boa Morte", "Portugal", "LW", "ST", 67, 63, 36, 90, 77, 91);
        add(players, "Quinton Fortune", "África do Sul", "LM", "LB", 58, 68, 61, 89, 72, 91);
        add(players, "Sun Jihai", "China", "RB", "CB", 46, 62, 70, 87, 62, 91);
        add(players, "Raphaël Wicky", "Suíça", "CDM", "LB", 48, 65, 69, 86, 62, 91);
        add(players, "Iván Kaviedes", "Equador", "ST", "CF", 72, 58, 27, 83, 72, 91);
        add(players, "Fatih Tekke", "Turquia", "ST", null, 72, 56, 29, 85, 69, 91);
        add(players, "Francesco Coco", "Itália", "LB", "LWB", 50, 65, 69, 87, 68, 91);
        add(players, "Sylvain Distin", "França", "CB", "LB", 35, 59, 72, 92, 51, 91);
        add(players, "Manuel Almunia", "Espanha", "GK", null, 11, 38, 74, 87, 17, 91);
        add(players, "Noel Valladares", "Honduras", "GK", null, 11, 38, 74, 86, 17, 90);
        add(players, "Artur Wichniarek", "Polônia", "ST", null, 71, 54, 28, 86, 66, 90);
        add(players, "Fatih Akyel", "Turquia", "RB", "CB", 44, 61, 69, 87, 59, 90);
        add(players, "Marco Storari", "Itália", "GK", null, 11, 37, 73, 86, 17, 90);
        add(players, "Richard Wright", "Inglaterra", "GK", null, 11, 38, 73, 86, 17, 90);
        add(players, "Arnold Bruggink", "Holanda", "CAM", "CF", 63, 71, 40, 79, 74, 90);
        add(players, "Tarik Sektioui", "Marrocos", "RW", "CAM", 65, 69, 36, 83, 78, 90);
        add(players, "Kiki Musampa", "Holanda", "LM", "CAM", 62, 69, 43, 82, 76, 89);
        add(players, "Adriano Gabiru", "Brasil", "CAM", "RM", 63, 71, 40, 79, 77, 89);
        add(players, "Roy Carroll", "Irlanda do Norte", "GK", null, 11, 37, 72, 88, 17, 89);
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
