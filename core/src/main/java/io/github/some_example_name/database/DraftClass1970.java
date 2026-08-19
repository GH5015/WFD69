package database;

import model.Player;
import model.Position;
import model.TechnicalAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraftClass1970 {

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        // Mapeamento dos jogadores da Draft Class 1970 (Nome, País, Primária, Secundária, Idade, ATA, PAS, DEF, FIS, DRI, Potencial, Salário)
        players.add(createPlayer("Neeskens", "Holanda", Position.CM, Position.CDM, 18, 76, 79, 74, 86, 74, 88, 15000));
        players.add(createPlayer("Hoeneß", "Alemanha", Position.ST, Position.CF, 18, 78, 70, 45, 88, 79, 87, 14000));
        players.add(createPlayer("Simonsen", "Dinamarca", Position.RW, Position.CF, 17, 74, 75, 35, 72, 91, 89, 13000));
        players.add(createPlayer("Rep", "Holanda", Position.RW, Position.CF, 18, 77, 66, 34, 82, 84, 86, 12000));
        players.add(createPlayer("Bonhof", "Alemanha", Position.CDM, Position.CM, 18, 68, 76, 63, 91, 69, 86, 12000));
        players.add(createPlayer("Causio", "Itália", Position.RW, Position.RM, 21, 79, 82, 38, 71, 86, 87, 18000));
        players.add(createPlayer("Deyna", "Polônia", Position.CAM, Position.CM, 22, 78, 88, 44, 69, 78, 88, 20000));
        players.add(createPlayer("Haan", "Holanda", Position.CDM, Position.CB, 21, 65, 78, 67, 78, 65, 84, 15000));
        players.add(createPlayer("Jansen", "Holanda", Position.LB, Position.LM, 21, 61, 77, 73, 83, 67, 83, 14000));
        players.add(createPlayer("Hölzenbein", "Alemanha", Position.CF, Position.CAM, 23, 76, 67, 39, 83, 73, 84, 16000));
        players.add(createPlayer("Chinaglia", "Itália", Position.ST, Position.CF, 23, 84, 55, 32, 89, 66, 85, 17000));
        players.add(createPlayer("Trésor", "França", Position.CB, Position.CDM, 20, 43, 62, 81, 84, 58, 87, 16000));
        players.add(createPlayer("Brooking", "Inglaterra", Position.CM, Position.CAM, 22, 66, 80, 42, 67, 76, 84, 15000));
        players.add(createPlayer("Clemence", "Inglaterra", Position.GK, null, 21, 18, 40, 88, 79, 20, 86, 16000));
        players.add(createPlayer("Leão", "Brasil", Position.GK, null, 20, 15, 38, 91, 82, 19, 88, 17000));
        players.add(createPlayer("Bettega", "Itália", Position.ST, Position.CF, 19, 75, 54, 31, 76, 66, 85, 14000));
        players.add(createPlayer("Dirceu Lopes", "Brasil", Position.CAM, Position.CF, 23, 82, 84, 38, 69, 81, 89, 22000));
        players.add(createPlayer("Cubillas", "Peru", Position.CAM, Position.CF, 20, 82, 79, 35, 74, 85, 90, 21000));
        players.add(createPlayer("Ahumada", "Argentina", Position.CDM, Position.CM, 20, 63, 65, 38, 80, 67, 81, 11000));
        players.add(createPlayer("Kapellmann", "Alemanha", Position.CM, Position.CDM, 21, 60, 72, 64, 82, 62, 82, 12000));
        players.add(createPlayer("Buljan", "Iugoslávia", Position.CB, Position.CDM, 22, 39, 55, 82, 87, 54, 84, 14000));
        players.add(createPlayer("Dalglish", "Escócia", Position.CF, Position.ST, 18, 72, 71, 31, 73, 82, 91, 18000));
        players.add(createPlayer("Croy", "Alemanha Oriental", Position.GK, null, 23, 14, 38, 89, 80, 17, 87, 18000));
        players.add(createPlayer("Herzog", "Alemanha Oriental", Position.CM, Position.CAM, 23, 68, 62, 37, 78, 62, 80, 10000));
        players.add(createPlayer("R. van de Kerkhof", "Holanda", Position.RW, Position.RM, 18, 69, 63, 32, 91, 82, 85, 13000));
        players.add(createPlayer("W. van de Kerkhof", "Holanda", Position.RM, Position.RW, 18, 61, 69, 57, 90, 74, 85, 13000));
        players.add(createPlayer("Lato", "Polônia", Position.RW, Position.CF, 19, 70, 60, 34, 92, 77, 88, 16000));
        players.add(createPlayer("Zé Maria", "Brasil", Position.RB, Position.RM, 21, 50, 61, 79, 88, 66, 85, 15000));
        players.add(createPlayer("McQueen", "Escócia", Position.CB, Position.CDM, 17, 34, 45, 79, 89, 42, 83, 10000));
        players.add(createPlayer("Okudera", "Japão", Position.RM, Position.CM, 18, 55, 63, 43, 82, 67, 81, 9000));
        players.add(createPlayer("Rote Jr.", "EUA", Position.RM, Position.CM, 21, 62, 55, 35, 86, 76, 79, 8000));
        players.add(createPlayer("Hellström", "Suécia", Position.GK, null, 20, 12, 37, 85, 80, 18, 85, 14000));
        players.add(createPlayer("Panenka", "Tchecoslováquia", Position.CAM, Position.CM, 21, 67, 89, 31, 68, 73, 86, 16000));
        players.add(createPlayer("Džajić", "Iugoslávia", Position.LW, Position.CF, 23, 83, 79, 32, 77, 92, 89, 21000));
        players.add(createPlayer("M. Olsen", "Dinamarca", Position.CB, Position.CDM, 20, 41, 65, 71, 81, 58, 84, 13000));
        players.add(createPlayer("Lund", "Dinamarca", Position.LW, Position.CF, 21, 78, 72, 29, 76, 83, 83, 13000));
        players.add(createPlayer("Bonev", "Bulgária", Position.CAM, Position.CF, 23, 82, 84, 35, 73, 83, 86, 17000));
        players.add(createPlayer("Asensi", "Espanha", Position.CM, Position.CAM, 20, 61, 76, 48, 70, 72, 83, 12000));
        players.add(createPlayer("António Oliveira", "Portugal", Position.CAM, Position.CM, 17, 47, 72, 34, 65, 73, 82, 9000));
        players.add(createPlayer("Morena", "Uruguai", Position.ST, Position.CF, 18, 69, 52, 28, 77, 68, 84, 11000));
        players.add(createPlayer("Caszely", "Chile", Position.ST, Position.CF, 20, 76, 54, 30, 86, 72, 85, 14000));

        return players;
    }

    private static Player createPlayer(String name, String nationality, Position primaryPos, Position secondaryPos, 
                                       int age, int atk, int pas, int def, int fis, int dri, 
                                       int pot, double salary) {
        Map<String, Integer> attrs = new HashMap<>();
        attrs.put("ataque", atk);
        attrs.put("passe", pas);
        attrs.put("defesa", def);
        attrs.put("fisico", fis);
        attrs.put("drible", dri);

        return new Player(name, nationality, primaryPos, secondaryPos, age, new TechnicalAttributes(attrs), pot, salary);
    }
}
