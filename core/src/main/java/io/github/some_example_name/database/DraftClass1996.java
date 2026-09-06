package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1996. */
public final class DraftClass1996 {
    private DraftClass1996() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Gianluigi Buffon", "Itália", "GK", null, 12, 43, 83, 89, 19, 98);
        add(players, "Juan Román Riquelme", "Argentina", "CAM", "CM", 69, 83, 33, 73, 88, 97);
        add(players, "Frank Lampard", "Inglaterra", "CM", "CAM", 65, 78, 56, 84, 75, 97);
        add(players, "Carles Puyol", "Espanha", "CB", "RB", 40, 61, 78, 90, 58, 97);
        add(players, "Rio Ferdinand", "Inglaterra", "CB", null, 41, 66, 77, 88, 62, 97);
        add(players, "Miroslav Klose", "Alemanha", "ST", null, 74, 58, 30, 87, 68, 96);
        add(players, "Didier Drogba", "Costa do Marfim", "ST", "CF", 73, 58, 29, 92, 70, 96);
        add(players, "Ricardo Carvalho", "Portugal", "CB", null, 37, 64, 78, 86, 57, 96);
        add(players, "Lúcio", "Brasil", "CB", "CDM", 46, 66, 77, 92, 65, 96);
        add(players, "Gennaro Gattuso", "Itália", "CDM", "CM", 48, 67, 74, 92, 63, 95);
        add(players, "Dejan Stanković", "Iugoslávia", "CM", "CAM", 63, 76, 60, 85, 77, 95);
        add(players, "Jamie Carragher", "Inglaterra", "CB", "RB", 36, 62, 75, 87, 53, 94);
        add(players, "Harry Kewell", "Austrália", "LW", "CAM", 70, 69, 34, 89, 83, 94);
        add(players, "Eiður Guðjohnsen", "Islândia", "CF", "CAM", 72, 70, 33, 85, 78, 94);
        add(players, "Claudio Pizarro", "Peru", "ST", "CF", 74, 59, 29, 90, 69, 94);
        add(players, "Kakha Kaladze", "Geórgia", "LB", "CB", 43, 65, 75, 89, 60, 94);
        add(players, "Ali Karimi", "Irã", "CAM", "RW", 68, 75, 36, 82, 86, 94);
        add(players, "Mohamed Aboutrika", "Egito", "CAM", "CF", 68, 78, 38, 78, 83, 94);
        add(players, "Joan Capdevila", "Espanha", "LB", "LM", 52, 67, 72, 87, 68, 93);
        add(players, "Daniel Van Buyten", "Bélgica", "CB", null, 41, 60, 76, 93, 54, 93);
        add(players, "Tomáš Ujfaluši", "Tchéquia", "CB", "RB", 42, 64, 74, 88, 59, 93);
        add(players, "Papa Bouba Diop", "Senegal", "CDM", "CM", 52, 65, 70, 94, 62, 93);
        add(players, "Mohammed Noor", "Arábia Saudita", "CM", "CAM", 61, 74, 54, 85, 77, 93);
        add(players, "Emile Heskey", "Inglaterra", "ST", "CF", 70, 55, 31, 93, 67, 92);
        add(players, "Barry Ferguson", "Escócia", "CM", "CDM", 59, 74, 61, 83, 72, 92);
        add(players, "Jérôme Rothen", "França", "LM", "LW", 63, 75, 40, 79, 79, 92);
        add(players, "Dennis Rommedahl", "Dinamarca", "RW", "RM", 67, 64, 32, 93, 79, 92);
        add(players, "Edu Gaspar", "Brasil", "CM", "CDM", 58, 72, 66, 84, 70, 92);
        add(players, "Yuji Nakazawa", "Japão", "CB", null, 38, 60, 75, 90, 55, 92);
        add(players, "Bonaventure Kalou", "Costa do Marfim", "LW", "CAM", 69, 64, 30, 86, 77, 92);
        add(players, "Josip Šimunić", "Croácia", "CB", null, 36, 61, 75, 92, 53, 92);
        add(players, "Kieron Dyer", "Inglaterra", "CM", "RM", 64, 68, 46, 92, 78, 92);
        add(players, "Nicolás Olivera", "Uruguai", "CAM", "LW", 66, 73, 32, 78, 81, 92);
        add(players, "Bogdan Lobonț", "Romênia", "GK", null, 12, 40, 76, 88, 18, 92);
        add(players, "Tinga", "Brasil", "CM", "RM", 58, 70, 62, 87, 72, 91);
        add(players, "Lionel Scaloni", "Argentina", "RB", "RM", 50, 66, 69, 87, 64, 91);
        add(players, "Luciano Zauri", "Itália", "RB", "LB", 50, 65, 70, 86, 66, 91);
        add(players, "Ivan Leko", "Croácia", "CM", "CAM", 57, 73, 55, 80, 73, 91);
        add(players, "Yazid Mansouri", "Argélia", "CDM", "CM", 54, 69, 67, 87, 65, 91);
        add(players, "Abdoulaye Faye", "Senegal", "CB", "CDM", 38, 58, 72, 94, 52, 91);
        add(players, "James Beattie", "Inglaterra", "ST", null, 72, 55, 31, 89, 66, 91);
        add(players, "Lucas Neill", "Austrália", "RB", "CB", 43, 62, 71, 91, 59, 91);
        add(players, "Marko Pantelić", "Iugoslávia", "ST", "CF", 71, 58, 30, 86, 71, 91);
        add(players, "Gerald Asamoah", "Alemanha", "ST", "RW", 69, 57, 31, 91, 70, 91);
        add(players, "Marcelo Zalayeta", "Uruguai", "ST", "CF", 70, 56, 28, 90, 68, 91);
        add(players, "Aimo Diana", "Itália", "RM", "RB", 56, 67, 64, 86, 71, 90);
        add(players, "Gustavo Munúa", "Uruguai", "GK", null, 11, 40, 74, 86, 18, 90);
        add(players, "Tim de Cler", "Holanda", "LB", null, 47, 64, 69, 84, 65, 90);
        add(players, "Erik Edman", "Suécia", "LB", null, 48, 66, 69, 84, 64, 90);
        add(players, "Eric Addo", "Gana", "CB", "CDM", 42, 64, 70, 88, 60, 90);
        add(players, "Mista", "Espanha", "ST", "CF", 69, 60, 29, 83, 71, 90);
        add(players, "Archie Thompson", "Austrália", "ST", "RW", 70, 58, 28, 89, 73, 90);
        add(players, "Jan Vennegoor of Hesselink", "Holanda", "ST", null, 70, 56, 30, 91, 66, 90);
        add(players, "Facundo Quiroga", "Argentina", "CB", "RB", 38, 61, 72, 88, 54, 90);
        add(players, "Mehdi Nafti", "Tunísia", "CDM", "CM", 50, 67, 66, 87, 62, 89);
        add(players, "Jimmy Bullard", "Inglaterra", "CM", "CAM", 58, 71, 55, 83, 72, 89);
        add(players, "Carlos Edwards", "Trinidad e Tobago", "RM", "RB", 58, 66, 53, 89, 73, 89);
        add(players, "Victor Agali", "Nigéria", "ST", null, 69, 54, 28, 90, 65, 89);
        add(players, "John Oster", "País de Gales", "RM", "CAM", 61, 71, 42, 80, 76, 89);
        add(players, "Michael Ricketts", "Inglaterra", "ST", null, 69, 53, 28, 90, 64, 88);
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
