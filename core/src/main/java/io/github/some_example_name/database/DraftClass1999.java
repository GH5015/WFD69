package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe histórica completa do WFL Draft de 1999. */
public final class DraftClass1999 {
    private DraftClass1999() { }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        add(players, "Zlatan Ibrahimović", "Suécia", "ST", "CF", 78, 68, 29, 89, 84, 98);
        add(players, "Samuel Eto'o", "Camarões", "ST", "RW", 79, 62, 28, 92, 82, 98);
        add(players, "Iker Casillas", "Espanha", "GK", null, 12, 43, 82, 88, 19, 98);
        add(players, "David Villa", "Espanha", "ST", "CF", 77, 64, 27, 85, 79, 97);
        add(players, "Xabi Alonso", "Espanha", "CM", "CDM", 56, 82, 68, 81, 72, 97);
        add(players, "Nemanja Vidić", "Iugoslávia", "CB", null, 37, 59, 80, 93, 51, 97);
        add(players, "Maicon", "Brasil", "RB", "RWB", 57, 69, 73, 92, 77, 96);
        add(players, "Andrea Barzagli", "Itália", "CB", null, 35, 61, 79, 88, 53, 96);
        add(players, "Patrice Evra", "França", "LB", "LM", 56, 69, 74, 91, 78, 96);
        add(players, "Dimitar Berbatov", "Bulgária", "ST", "CF", 76, 69, 27, 82, 82, 96);
        add(players, "Javier Saviola", "Argentina", "ST", "CF", 76, 65, 27, 88, 83, 95);
        add(players, "Joaquín", "Espanha", "RW", "RM", 68, 72, 36, 87, 86, 95);
        add(players, "Park Ji-sung", "Coreia do Sul", "RM", "CM", 63, 70, 57, 94, 75, 95);
        add(players, "Andrés D'Alessandro", "Argentina", "CAM", "CM", 67, 79, 37, 75, 87, 95);
        add(players, "Lucho González", "Argentina", "CM", "CAM", 63, 76, 60, 84, 75, 95);
        add(players, "Maxi Rodríguez", "Argentina", "RM", "RW", 70, 70, 49, 87, 78, 94);
        add(players, "Tiago Mendes", "Portugal", "CM", "CDM", 57, 75, 66, 85, 70, 94);
        add(players, "Júlio Baptista", "Brasil", "CAM", "CF", 68, 70, 57, 94, 73, 94);
        add(players, "Luisão", "Brasil", "CB", null, 40, 60, 77, 93, 52, 94);
        add(players, "Bruno Alves", "Portugal", "CB", null, 40, 62, 76, 93, 52, 94);
        add(players, "Heurelho Gomes", "Brasil", "GK", null, 11, 39, 76, 92, 18, 94);
        add(players, "Maxwell", "Brasil", "LB", "LM", 53, 71, 71, 86, 73, 94);
        add(players, "Elano", "Brasil", "CAM", "CM", 65, 77, 46, 78, 78, 94);
        add(players, "Andrei Arshavin", "Rússia", "CAM", "LW", 69, 73, 32, 86, 84, 94);
        add(players, "Michael Carrick", "Inglaterra", "CM", "CDM", 54, 78, 67, 81, 70, 94);
        add(players, "Joe Cole", "Inglaterra", "CAM", "LW", 67, 75, 38, 81, 85, 94);
        add(players, "Owen Hargreaves", "Inglaterra", "CDM", "CM", 50, 69, 72, 91, 66, 94);
        add(players, "Kolo Touré", "Costa do Marfim", "CB", "RB", 40, 62, 75, 92, 58, 94);
        add(players, "Nicolás Burdisso", "Argentina", "CB", "RB", 39, 62, 75, 89, 56, 93);
        add(players, "Roman Pavlyuchenko", "Rússia", "ST", null, 73, 57, 29, 89, 68, 93);
        add(players, "John O'Shea", "Irlanda", "CB", "RB", 43, 65, 72, 91, 61, 93);
        add(players, "Gareth Barry", "Inglaterra", "CM", "CDM", 55, 72, 68, 86, 67, 93);
        add(players, "Milan Baroš", "Tchéquia", "ST", "CF", 74, 59, 27, 90, 75, 93);
        add(players, "Djibril Cissé", "França", "ST", "RW", 74, 55, 27, 95, 74, 93);
        add(players, "Peter Crouch", "Inglaterra", "ST", null, 71, 59, 30, 90, 64, 92);
        add(players, "Roque Santa Cruz", "Paraguai", "ST", "CF", 72, 59, 30, 90, 69, 93);
        add(players, "Shaun Wright-Phillips", "Inglaterra", "RW", "RM", 67, 66, 35, 94, 82, 92);
        add(players, "El Hadji Diouf", "Senegal", "RW", "CF", 68, 66, 32, 89, 80, 92);
        add(players, "Andreas Isaksson", "Suécia", "GK", null, 11, 39, 76, 91, 17, 93);
        add(players, "Volkan Demirel", "Turquia", "GK", null, 11, 38, 75, 92, 17, 92);
        add(players, "Cristian Zaccardo", "Itália", "RB", "CB", 46, 62, 72, 87, 61, 92);
        add(players, "Khalid Boulahrouz", "Holanda", "CB", "RB", 37, 59, 74, 91, 52, 92);
        add(players, "Giourkas Seitaridis", "Grécia", "RB", "RWB", 51, 66, 71, 90, 67, 92);
        add(players, "Vicente Rodríguez", "Espanha", "LW", "LM", 68, 71, 37, 89, 83, 93);
        add(players, "Asier del Horno", "Espanha", "LB", "LWB", 52, 65, 71, 89, 67, 92);
        add(players, "Pablo Ibáñez", "Espanha", "CB", null, 35, 59, 73, 91, 50, 92);
        add(players, "Carlos Cuéllar", "Espanha", "CB", "RB", 37, 60, 72, 90, 53, 91);
        add(players, "Mahamadou Diarra", "Mali", "CDM", "CM", 50, 67, 71, 93, 62, 93);
        add(players, "Morten Gamst Pedersen", "Noruega", "LM", "LW", 64, 71, 46, 83, 76, 92);
        add(players, "Brede Hangeland", "Noruega", "CB", null, 35, 60, 73, 93, 51, 92);
        add(players, "Mirel Rădoi", "Romênia", "CB", "CDM", 42, 65, 72, 91, 59, 92);
        add(players, "Răzvan Raț", "Romênia", "LB", "LM", 50, 65, 70, 87, 67, 91);
        add(players, "Daisuke Matsui", "Japão", "CAM", "LM", 62, 72, 38, 79, 81, 91);
        add(players, "Mika Väyrynen", "Finlândia", "CM", "CAM", 60, 72, 55, 82, 72, 91);
        add(players, "Ebi Smolarek", "Polônia", "ST", "RW", 70, 59, 29, 88, 73, 91);
        add(players, "Mladen Petrić", "Croácia", "ST", "CF", 71, 62, 29, 85, 71, 91);
        add(players, "Johan Elmander", "Suécia", "ST", "RW", 70, 60, 31, 91, 69, 91);
        add(players, "Afonso Alves", "Brasil", "ST", null, 73, 55, 27, 86, 68, 90);
        add(players, "Humberto Suazo", "Chile", "ST", "CF", 73, 61, 29, 84, 72, 91);
        add(players, "Mauro Rosales", "Argentina", "RW", "RM", 67, 68, 36, 87, 78, 90);
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
