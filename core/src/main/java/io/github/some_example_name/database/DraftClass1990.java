package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica do WFL Draft de 1990, sem repetir Daniel Borimirov (1988). */
public final class DraftClass1990 {
    private DraftClass1990() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Zinedine Zidane", "França", "CAM", "CM", 70, 82, 41, 77, 88, 99);
        add(players, "Rivaldo", "Brasil", "CAM", "CF", 75, 77, 31, 81, 87, 98);
        add(players, "Luís Figo", "Portugal", "RW", "CAM", 70, 78, 36, 82, 86, 98);
        add(players, "Pavel Nedvěd", "Tchecoslováquia", "LM", "CM", 66, 75, 57, 89, 79, 97);
        add(players, "Lilian Thuram", "França", "RB", "CB", 43, 62, 78, 90, 61, 97);
        add(players, "Rui Costa", "Portugal", "CAM", "CM", 64, 81, 39, 73, 84, 97);
        add(players, "Jaap Stam", "Holanda", "CB", null, 34, 57, 78, 93, 50, 96);
        add(players, "Roberto Abbondanzieri", "Argentina", "GK", null, 11, 39, 78, 86, 18, 95);
        add(players, "Karel Poborský", "Tchecoslováquia", "RW", "RM", 67, 70, 38, 84, 83, 95);
        add(players, "Steve McManaman", "Inglaterra", "RW", "CAM", 66, 73, 37, 83, 84, 95);
        add(players, "Christian Ziege", "Alemanha", "LB", "LM", 54, 69, 72, 86, 70, 95);
        add(players, "Mauricio Pochettino", "Argentina", "CB", null, 36, 61, 74, 88, 53, 94);
        add(players, "Giuseppe Favalli", "Itália", "LB", "CB", 43, 63, 72, 85, 61, 94);
        add(players, "Christophe Dugarry", "França", "ST", "LW", 70, 64, 30, 83, 76, 94);
        add(players, "Darren Anderton", "Inglaterra", "RM", "CM", 62, 70, 47, 83, 76, 93);
        add(players, "Giovanni", "Brasil", "CAM", "CF", 69, 77, 33, 77, 82, 94);
        add(players, "Giovane Élber", "Brasil", "ST", "CF", 73, 58, 29, 85, 72, 94);
        add(players, "Haim Revivo", "Israel", "CAM", "LM", 65, 74, 37, 76, 81, 93);
        add(players, "Ugo Ehiogu", "Inglaterra", "CB", null, 35, 58, 73, 89, 50, 93);
        add(players, "Markus Babbel", "Alemanha", "RB", "CB", 43, 62, 72, 88, 60, 93);
        add(players, "Darío Silva", "Uruguai", "ST", "RW", 72, 57, 29, 89, 72, 93);
        add(players, "Mário Stanić", "Iugoslávia", "RM", "CM", 62, 69, 54, 87, 72, 92);
        add(players, "Eyal Berkovic", "Israel", "CAM", "CM", 60, 77, 39, 72, 79, 93);
        add(players, "Peter Møller", "Dinamarca", "ST", null, 71, 54, 29, 85, 66, 91);
        add(players, "Ebbe Sand", "Dinamarca", "ST", null, 73, 54, 28, 87, 66, 92);
        add(players, "Jimmy Floyd Hasselbaink", "Holanda", "ST", null, 74, 54, 28, 90, 67, 93);
        add(players, "Nuno Capucho", "Portugal", "RW", "RM", 64, 69, 40, 82, 76, 91);
        add(players, "Daniel Prodan", "Romênia", "CB", null, 34, 57, 72, 88, 49, 91);
        add(players, "Péter Lipcsei", "Hungria", "CM", "CAM", 59, 72, 53, 81, 72, 91);
        add(players, "Andriy Husin", "URSS", "CDM", "CM", 53, 68, 67, 87, 64, 91);
        add(players, "Marcus Hahnemann", "EUA", "GK", null, 11, 38, 74, 87, 17, 90);
        add(players, "Radosław Majdan", "Polônia", "GK", null, 11, 37, 73, 85, 17, 89);
        add(players, "João Carlos dos Santos", "Brasil", "CB", null, 36, 59, 71, 85, 53, 90);
        add(players, "Goran Vlaović", "Iugoslávia", "ST", null, 71, 54, 28, 83, 68, 91);
        add(players, "Esteban Valencia", "Chile", "CAM", "CM", 61, 72, 42, 78, 77, 90);
        add(players, "Ezra Hendrickson", "São Vicente e Granadinas", "RB", "CB", 42, 60, 69, 86, 59, 89);
        add(players, "Alen Peternac", "Iugoslávia", "ST", null, 70, 53, 27, 82, 67, 89);
        add(players, "Wilmer Velásquez", "Honduras", "ST", null, 70, 53, 27, 84, 68, 89);
        add(players, "Richard Sosa", "Uruguai", "CB", null, 38, 59, 69, 83, 54, 87);
        add(players, "Jean-Paul van Gastel", "Holanda", "CM", "CDM", 55, 69, 63, 84, 65, 90);
        add(players, "Darren Ferguson", "Escócia", "CM", null, 57, 69, 51, 79, 69, 88);
        add(players, "Gurban Gurbanov", "URSS", "ST", null, 69, 54, 27, 82, 66, 88);
        add(players, "Lê Huỳnh Đức", "Vietnã", "ST", null, 69, 53, 27, 85, 65, 89);
        add(players, "Manuel Martínez", "México", "CM", null, 55, 67, 55, 81, 67, 87);
        add(players, "Derlis Gómez", "Paraguai", "GK", null, 11, 37, 72, 84, 17, 87);
        add(players, "Eduardo Pereira", "Timor-Leste", "CB", null, 37, 58, 68, 83, 53, 86);
        add(players, "Ruben Bagger", "Dinamarca", "ST", null, 68, 52, 27, 82, 64, 87);
        add(players, "Yuri Drozdov", "URSS", "CM", "CDM", 52, 67, 61, 81, 64, 87);
        add(players, "Losseni Konaté", "Costa do Marfim", "GK", null, 11, 36, 71, 84, 17, 87);
        add(players, "Mariano Bombarda", "Argentina", "ST", null, 68, 52, 27, 82, 65, 87);
        add(players, "Christophe Revault", "França", "GK", null, 11, 38, 73, 84, 17, 89);
        add(players, "Abel Xavier", "Portugal", "RB", "CB", 47, 63, 69, 86, 64, 91);
        add(players, "Marc Hottiger", "Suíça", "RB", "CB", 44, 61, 69, 84, 61, 89);
        add(players, "Marco Osio", "Itália", "CM", "CAM", 59, 69, 49, 79, 70, 88);
        add(players, "Alfred Schreuder", "Holanda", "CM", "CDM", 50, 65, 62, 82, 62, 86);
        add(players, "Joseph Oosting", "Holanda", "CM", null, 54, 67, 55, 81, 66, 86);
        add(players, "Christos Kiourkos", "Grécia", "CB", null, 37, 58, 68, 82, 53, 85);
        add(players, "Gytis Padimanskas", "URSS", "GK", null, 11, 35, 69, 82, 16, 85);
        add(players, "Roberto Baldassari", "Suíça", "CB", null, 36, 57, 67, 81, 52, 84);
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

