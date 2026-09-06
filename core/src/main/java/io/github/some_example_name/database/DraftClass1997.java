package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1997. */
public final class DraftClass1997 {
    private DraftClass1997() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Michael Owen", "Inglaterra", "ST", "CF", 80, 58, 27, 90, 81, 98);
        add(players, "Andrea Pirlo", "Itália", "CM", "CAM", 61, 82, 48, 74, 81, 98);
        add(players, "Diego Forlán", "Uruguai", "ST", "CF", 75, 66, 30, 86, 77, 97);
        add(players, "Rafael Márquez", "México", "CB", "CDM", 45, 69, 78, 86, 62, 97);
        add(players, "Pablo Aimar", "Argentina", "CAM", "CF", 69, 79, 35, 74, 87, 96);
        add(players, "Nicolas Anelka", "França", "ST", "CF", 77, 59, 28, 88, 78, 96);
        add(players, "Júlio César Soares", "Brasil", "GK", null, 12, 42, 79, 87, 18, 96);
        add(players, "Adrian Mutu", "Romênia", "CF", "LW", 72, 69, 31, 82, 82, 95);
        add(players, "Eric Abidal", "França", "LB", "CB", 42, 63, 73, 90, 61, 95);
        add(players, "Diego Milito", "Argentina", "ST", "CF", 73, 58, 29, 83, 71, 95);
        add(players, "Juan", "Brasil", "CB", null, 38, 65, 76, 86, 57, 94);
        add(players, "Damien Duff", "Irlanda", "LW", "RW", 68, 70, 34, 89, 84, 94);
        add(players, "Tim Cahill", "Austrália", "CAM", "CM", 67, 69, 61, 90, 70, 94);
        add(players, "Carlos Marchena", "Espanha", "CB", "CDM", 42, 66, 74, 86, 59, 94);
        add(players, "Simão", "Portugal", "RW", "LW", 69, 73, 34, 85, 84, 94);
        add(players, "Paulo Ferreira", "Portugal", "RB", "CB", 48, 65, 72, 87, 64, 94);
        add(players, "Sergei Ignashevich", "Rússia", "CB", null, 43, 65, 76, 88, 56, 94);
        add(players, "Tim Howard", "EUA", "GK", null, 12, 42, 77, 90, 18, 94);
        add(players, "John Carew", "Noruega", "ST", "CF", 72, 57, 30, 92, 69, 93);
        add(players, "Stiliyan Petrov", "Bulgária", "CM", "CAM", 60, 75, 58, 85, 74, 93);
        add(players, "Craig Bellamy", "País de Gales", "ST", "RW", 70, 61, 29, 91, 77, 93);
        add(players, "David Pizarro", "Chile", "CM", "CAM", 59, 78, 55, 78, 80, 93);
        add(players, "Arne Friedrich", "Alemanha", "CB", "RB", 39, 62, 74, 87, 57, 93);
        add(players, "Stipe Pletikosa", "Croácia", "GK", null, 11, 40, 76, 89, 18, 93);
        add(players, "Mickaël Landreau", "França", "GK", null, 12, 41, 77, 85, 18, 93);
        add(players, "Zoltán Gera", "Hungria", "CAM", "RM", 64, 74, 47, 84, 80, 93);
        add(players, "David Suazo", "Honduras", "ST", "RW", 73, 57, 28, 93, 74, 93);
        add(players, "Nihat Kahveci", "Turquia", "ST", "RW", 72, 62, 29, 86, 77, 93);
        add(players, "Wes Brown", "Inglaterra", "CB", "RB", 37, 61, 75, 88, 55, 93);
        add(players, "Richard Dunne", "Irlanda", "CB", null, 35, 58, 75, 91, 51, 93);
        add(players, "Fabian Ernst", "Alemanha", "CM", "CDM", 54, 70, 69, 87, 65, 92);
        add(players, "Brett Emerton", "Austrália", "RM", "RB", 61, 67, 60, 89, 74, 92);
        add(players, "Martin Petrov", "Bulgária", "LW", "LM", 68, 70, 37, 90, 82, 92);
        add(players, "Timo Hildebrand", "Alemanha", "GK", null, 11, 39, 76, 86, 17, 93);
        add(players, "Vyacheslav Malafeev", "Rússia", "GK", null, 11, 38, 75, 88, 17, 92);
        add(players, "Anthony Réveillère", "França", "RB", "LB", 49, 66, 71, 87, 66, 92);
        add(players, "Mariusz Lewandowski", "Polônia", "CDM", "CB", 48, 66, 70, 90, 62, 92);
        add(players, "Vincenzo Iaquinta", "Itália", "ST", "RW", 72, 57, 31, 90, 68, 92);
        add(players, "Grafite", "Brasil", "ST", "CF", 72, 58, 30, 90, 68, 92);
        add(players, "Alexander Frei", "Suíça", "ST", "CF", 72, 58, 28, 83, 70, 93);
        add(players, "Gerardo Torrado", "México", "CDM", "CM", 52, 68, 71, 89, 64, 92);
        add(players, "Steve Cherundolo", "EUA", "RB", "RWB", 48, 64, 70, 87, 65, 92);
        add(players, "Andy van der Meyde", "Holanda", "RW", "RM", 68, 68, 36, 87, 82, 92);
        add(players, "Pascal Chimbonda", "França", "RB", "CB", 48, 63, 71, 90, 64, 91);
        add(players, "Massimo Maccarone", "Itália", "ST", "CF", 70, 56, 29, 84, 69, 90);
        add(players, "Naohiro Takahara", "Japão", "ST", "CF", 70, 57, 29, 86, 71, 91);
        add(players, "Seol Ki-hyeon", "Coreia do Sul", "RW", "ST", 68, 63, 34, 90, 76, 91);
        add(players, "Daniel Aranzubia", "Espanha", "GK", null, 11, 39, 74, 86, 17, 91);
        add(players, "Matthew Upson", "Inglaterra", "CB", null, 35, 61, 72, 88, 53, 91);
        add(players, "David Jarolím", "Tchéquia", "CM", "RM", 56, 71, 59, 84, 70, 91);
        add(players, "Teemu Tainio", "Finlândia", "CM", "RM", 58, 71, 59, 85, 72, 91);
        add(players, "Ludovic Magnin", "Suíça", "LB", "LM", 48, 65, 69, 86, 65, 90);
        add(players, "Tobias Linderoth", "Suécia", "CDM", "CM", 49, 68, 69, 86, 62, 91);
        add(players, "Vince Grella", "Austrália", "CDM", "CM", 50, 67, 68, 87, 63, 90);
        add(players, "Jay DeMerit", "EUA", "CB", null, 35, 58, 70, 88, 51, 90);
        add(players, "Danny Gabbidon", "País de Gales", "CB", "RB", 37, 59, 70, 87, 54, 89);
        add(players, "Luke Young", "Inglaterra", "RB", "CB", 46, 63, 69, 86, 63, 90);
        add(players, "Kim Yong-dae", "Coreia do Sul", "GK", null, 11, 38, 72, 86, 17, 90);
        add(players, "Boubacar Barry", "Costa do Marfim", "GK", null, 11, 39, 73, 88, 18, 90);
        add(players, "Jean-Alain Boumsong", "França", "CB", null, 37, 60, 72, 91, 53, 91);
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
