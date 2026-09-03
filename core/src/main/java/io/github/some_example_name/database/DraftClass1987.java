package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1987. */
public final class DraftClass1987 {
    private DraftClass1987() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Dennis Bergkamp", "Holanda", "CF", "CAM", 76, 79, 30, 76, 88, 98);
        add(players, "Gabriel Batistuta", "Argentina", "ST", null, 78, 57, 29, 88, 72, 97);
        add(players, "Fernando Redondo", "Argentina", "CDM", "CM", 51, 78, 73, 81, 75, 97);
        add(players, "Oliver Kahn", "Alemanha Ocidental", "GK", null, 11, 40, 79, 89, 18, 97);
        add(players, "Brian Laudrup", "Dinamarca", "RW", "CF", 71, 74, 31, 84, 85, 96);
        add(players, "Robert Prosinečki", "Iugoslávia", "CAM", "CM", 64, 81, 41, 71, 86, 96);
        add(players, "Predrag Mijatović", "Iugoslávia", "ST", "CF", 74, 65, 28, 80, 78, 95);
        add(players, "Siniša Mihajlović", "Iugoslávia", "CB", "LB", 47, 70, 73, 86, 61, 95);
        add(players, "Bixente Lizarazu", "França", "LB", "LM", 50, 67, 73, 86, 70, 95);
        add(players, "Andrei Kanchelskis", "URSS", "RW", "RM", 69, 67, 36, 89, 82, 94);
        add(players, "Antonio Conte", "Itália", "CM", "CDM", 55, 70, 67, 87, 65, 94);
        add(players, "Hong Myung-bo", "Coreia do Sul", "CB", "SW", 42, 70, 76, 82, 60, 94);
        add(players, "Gary Speed", "País de Gales", "LM", "CM", 62, 72, 58, 86, 72, 94);
        add(players, "Santiago Cañizares", "Espanha", "GK", null, 11, 39, 77, 85, 17, 94);
        add(players, "Jens Lehmann", "Alemanha Ocidental", "GK", null, 12, 41, 77, 87, 18, 94);
        add(players, "Valery Karpin", "URSS", "RM", "CM", 63, 73, 49, 84, 76, 94);
        add(players, "Tomas Brolin", "Suécia", "CF", "CAM", 72, 72, 34, 82, 81, 94);
        add(players, "Stéphane Chapuisat", "Suíça", "ST", "CF", 73, 62, 29, 82, 74, 93);
        add(players, "Ali Daei", "Irã", "ST", null, 73, 54, 29, 87, 66, 93);
        add(players, "Viktor Onopko", "URSS", "CB", "SW", 39, 64, 74, 84, 57, 93);
        add(players, "Ilie Dumitrescu", "Romênia", "LW", "CAM", 68, 71, 32, 80, 82, 93);
        add(players, "Igor Shalimov", "URSS", "CAM", "CM", 63, 76, 45, 76, 80, 93);
        add(players, "Pierluigi Casiraghi", "Itália", "ST", null, 72, 55, 30, 88, 66, 93);
        add(players, "Marc Wilmots", "Bélgica", "CAM", "CF", 67, 70, 49, 89, 72, 93);
        add(players, "Lucas Radebe", "África do Sul", "CB", "CDM", 39, 62, 74, 88, 55, 93);
        add(players, "Carlos Roa", "Argentina", "GK", null, 11, 38, 76, 84, 17, 92);
        add(players, "Kasey Keller", "EUA", "GK", null, 11, 38, 76, 86, 17, 92);
        add(players, "Steve Staunton", "Irlanda", "LB", "CB", 48, 66, 71, 84, 65, 92);
        add(players, "Paulo Bento", "Portugal", "CDM", "CM", 48, 68, 69, 83, 61, 92);
        add(players, "Leonardo", "Brasil", "LB", "LM", 57, 73, 66, 80, 79, 94);
        add(players, "Fernando Cáceres", "Argentina", "CB", "RB", 38, 61, 73, 84, 55, 92);
        add(players, "Luís Oliveira", "Bélgica", "ST", "CF", 71, 62, 29, 83, 75, 92);
        add(players, "Earnie Stewart", "EUA", "RW", "ST", 68, 63, 34, 84, 75, 91);
        add(players, "Dariusz Wosz", "Alemanha Oriental", "CAM", "CM", 60, 76, 40, 72, 80, 92);
        add(players, "Ilya Tsymbalar", "URSS", "CAM", "LM", 63, 75, 42, 76, 81, 92);
        add(players, "Sergey Yuran", "URSS", "ST", null, 72, 55, 29, 86, 69, 91);
        add(players, "Stig Inge Bjørnebye", "Noruega", "LB", null, 46, 66, 70, 83, 64, 91);
        add(players, "Arthur Numan", "Holanda", "LB", "CB", 47, 65, 70, 84, 65, 91);
        add(players, "Thomas Linke", "Alemanha Ocidental", "CB", null, 34, 58, 72, 87, 50, 91);
        add(players, "Stefan Schwarz", "Suécia", "CM", "CDM", 53, 70, 65, 86, 65, 91);
        add(players, "Pontus Kåmark", "Suécia", "RB", "CB", 44, 61, 70, 84, 60, 90);
        add(players, "Eric Wynalda", "EUA", "ST", "CF", 70, 58, 29, 84, 70, 90);
        add(players, "Tony Meola", "EUA", "GK", null, 11, 37, 74, 86, 17, 90);
        add(players, "Túlio Maravilha", "Brasil", "ST", null, 72, 53, 26, 83, 68, 90);
        add(players, "Pierre van Hooijdonk", "Holanda", "ST", null, 72, 55, 28, 87, 66, 91);
        add(players, "Dion Dublin", "Inglaterra", "ST", "CB", 69, 53, 41, 89, 62, 89);
        add(players, "Dean Windass", "Inglaterra", "CF", "CAM", 68, 64, 35, 86, 72, 89);
        add(players, "Jörg Heinrich", "Alemanha Oriental", "LB", "LM", 50, 67, 67, 86, 68, 90);
        add(players, "Juan Reynoso", "Peru", "CB", null, 36, 59, 71, 84, 52, 90);
        add(players, "Motohiro Yamaguchi", "Japão", "CM", "CDM", 54, 69, 62, 80, 67, 88);
        add(players, "Wagner Lopes", "Brasil", "ST", "CF", 69, 58, 28, 82, 71, 88);
        add(players, "Viola", "Brasil", "ST", null, 70, 52, 27, 86, 66, 89);
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

