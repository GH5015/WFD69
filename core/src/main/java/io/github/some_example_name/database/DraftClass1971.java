package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Classe definitiva do WFL Draft de 1971. */
public final class DraftClass1971 {

    private DraftClass1971() {
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Nome, país, primária, secundária, idade, ATA, PAS, DEF, FIS, DRI, POT, salário.
        players.add(createPlayer("Kevin Keegan", "Inglaterra", Position.CF, Position.RW, 20, 76, 72, 39, 88, 82, 95, 22000));
        players.add(createPlayer("Peter Shilton", "Inglaterra", Position.GK, null, 21, 12, 42, 88, 85, 17, 95, 22000));
        players.add(createPlayer("Hans-Jürgen Dörner", "Alemanha Oriental", Position.CB, Position.CDM, 20, 40, 66, 80, 80, 57, 93, 20000));
        players.add(createPlayer("Andrzej Szarmach", "Polônia", Position.ST, null, 20, 76, 56, 30, 84, 68, 92, 19000));
        players.add(createPlayer("Joachim Streich", "Alemanha Oriental", Position.ST, null, 20, 78, 61, 28, 80, 69, 92, 19000));
        players.add(createPlayer("Nelinho", "Brasil", Position.RB, null, 21, 62, 71, 70, 84, 71, 92, 19000));
        players.add(createPlayer("Zdeněk Nehoda", "Tchecoslováquia", Position.ST, null, 19, 71, 59, 29, 81, 68, 91, 18000));
        players.add(createPlayer("José Velásquez", "Peru", Position.CDM, Position.CM, 19, 53, 71, 73, 85, 65, 91, 18000));
        players.add(createPlayer("Kazimierz Kmiecik", "Polônia", Position.ST, null, 20, 77, 60, 28, 79, 71, 90, 17000));
        players.add(createPlayer("Juan Carlos Oblitas", "Peru", Position.LW, null, 20, 75, 69, 35, 79, 82, 90, 17000));
        players.add(createPlayer("Joe Jordan", "Escócia", Position.ST, null, 19, 69, 52, 33, 89, 59, 90, 17000));
        players.add(createPlayer("Dudu Georgescu", "Romênia", Position.ST, null, 20, 73, 54, 29, 84, 65, 92, 19000));
        players.add(createPlayer("Vicente del Bosque", "Espanha", Position.CM, Position.CDM, 20, 55, 72, 67, 78, 64, 88, 15000));
        players.add(createPlayer("Dirceu", "Brasil", Position.CM, Position.LW, 19, 65, 74, 46, 77, 78, 92, 19000));
        players.add(createPlayer("Waldir Peres", "Brasil", Position.GK, null, 20, 13, 40, 80, 78, 18, 90, 17000));
        players.add(createPlayer("Mirandinha", "Brasil", Position.ST, null, 19, 72, 56, 29, 82, 69, 87, 14000));
        players.add(createPlayer("Abel Braga", "Brasil", Position.CB, null, 19, 35, 55, 73, 84, 49, 86, 13000));
        players.add(createPlayer("Gil", "Brasil", Position.RW, Position.ST, 20, 71, 61, 31, 84, 77, 88, 15000));
        players.add(createPlayer("Ralf Edström", "Suécia", Position.ST, null, 18, 72, 57, 31, 90, 64, 93, 20000));
        players.add(createPlayer("Roland Sandberg", "Suécia", Position.ST, Position.LW, 19, 70, 62, 34, 81, 75, 88, 15000));
        players.add(createPlayer("Per Røntved", "Dinamarca", Position.CB, Position.SW, 21, 38, 65, 77, 86, 56, 91, 18000));
        players.add(createPlayer("Johnny Hansen", "Dinamarca", Position.RB, Position.CB, 18, 43, 59, 71, 82, 58, 85, 12000));
        players.add(createPlayer("Teitur Þórðarson", "Islândia", Position.ST, null, 19, 68, 54, 29, 80, 64, 84, 11000));
        players.add(createPlayer("Don Givens", "Irlanda", Position.ST, null, 21, 70, 55, 31, 82, 65, 86, 13000));
        players.add(createPlayer("Liam O'Kane", "Irlanda do Norte", Position.LB, null, 19, 46, 59, 71, 80, 61, 84, 11000));
        players.add(createPlayer("Martin Buchan", "Escócia", Position.CB, null, 21, 34, 60, 78, 83, 51, 89, 16000));
        players.add(createPlayer("John Robertson", "Escócia", Position.LW, null, 18, 67, 67, 33, 75, 80, 90, 17000));
        players.add(createPlayer("David Hay", "Escócia", Position.CM, Position.RB, 23, 55, 70, 70, 81, 63, 87, 15000));
        players.add(createPlayer("John Blackley", "Escócia", Position.CB, null, 23, 33, 58, 76, 82, 48, 85, 13000));
        players.add(createPlayer("Alan Rough", "Escócia", Position.GK, null, 19, 12, 36, 75, 78, 17, 86, 13000));
        players.add(createPlayer("Trevor Cherry", "Inglaterra", Position.LB, Position.CB, 23, 43, 60, 75, 83, 55, 86, 13000));
        players.add(createPlayer("Phil Thompson", "Inglaterra", Position.CB, null, 17, 31, 58, 70, 73, 48, 91, 18000));
        players.add(createPlayer("Ray Kennedy", "Inglaterra", Position.ST, Position.CM, 19, 72, 67, 42, 86, 69, 91, 18000));
        players.add(createPlayer("David Nish", "Inglaterra", Position.LB, null, 23, 48, 65, 73, 79, 61, 85, 13000));
        players.add(createPlayer("John Richards", "Inglaterra", Position.ST, null, 20, 71, 54, 27, 78, 66, 87, 14000));
        players.add(createPlayer("Alan Sunderland", "Inglaterra", Position.RW, Position.ST, 18, 65, 60, 35, 80, 73, 87, 14000));
        players.add(createPlayer("Brian Talbot", "Inglaterra", Position.CM, null, 18, 54, 65, 59, 82, 62, 86, 13000));
        players.add(createPlayer("Felix Magath", "Alemanha Ocidental", Position.CAM, Position.CM, 18, 61, 75, 43, 71, 76, 91, 18000));
        players.add(createPlayer("Bernd Hölzenbein", "Alemanha Ocidental", Position.LW, Position.ST, 25, 78, 69, 40, 84, 76, 89, 16000));
        players.add(createPlayer("Paul Mariner", "Inglaterra", Position.ST, null, 18, 65, 50, 30, 84, 60, 88, 15000));

        return players;
    }

    private static Player createPlayer(String name, String nationality, Position primaryPos, Position secondaryPos,
                                       int age, int atk, int pas, int def, int fis, int dri,
                                       int potential, double salary) {
        Map<String, Integer> attrs = new HashMap<>();
        attrs.put("ataque", atk);
        attrs.put("passe", pas);
        attrs.put("defesa", def);
        attrs.put("fisico", fis);
        attrs.put("drible", dri);

        return new Player(name, nationality, primaryPos, secondaryPos, age,
            new TechnicalAttributes(attrs), potential, salary);
    }
}
