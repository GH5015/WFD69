package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import java.util.*;

/** Classe histórica completa do WFL Draft de 2000. */
public final class DraftClass2000 {
    private DraftClass2000() { }
    public static List<Player> getPlayers() {
        List<Player> p = new ArrayList<>();
        add(p,"Kaká","Brasil","CAM","CF",77,81,36,86,88,98); add(p,"Adriano","Brasil","ST","CF",80,61,29,94,78,98);
        add(p,"Petr Čech","Tchéquia","GK",null,11,42,81,91,18,98); add(p,"Michael Essien","Gana","CM","CDM",60,72,75,94,70,97);
        add(p,"Antonio Cassano","Itália","CF","CAM",74,78,29,76,88,96); add(p,"Pepe Reina","Espanha","GK",null,12,48,78,88,20,96);
        add(p,"Darijo Srna","Croácia","RB","RM",58,73,72,91,75,96); add(p,"Mikel Arteta","Espanha","CM","CAM",61,80,56,78,79,95);
        add(p,"Víctor Valdés","Espanha","GK",null,12,45,77,87,20,95); add(p,"Thiago Motta","Itália","CM","CDM",57,75,70,89,70,95);
        add(p,"Landon Donovan","EUA","CAM","CF",71,72,38,90,80,95); add(p,"Alberto Gilardino","Itália","ST",null,76,57,28,86,70,95);
        add(p,"Philippe Mexès","França","CB",null,39,63,77,89,56,95); add(p,"Alex","Brasil","CB",null,44,63,76,93,58,95);
        add(p,"Naldo","Brasil","CB",null,46,61,75,94,56,94); add(p,"José Bosingwa","Portugal","RB","RWB",54,68,71,92,73,94);
        add(p,"Kim Källström","Suécia","CM","CAM",63,77,57,83,76,94); add(p,"Jermain Defoe","Inglaterra","ST",null,76,55,26,91,76,94);
        add(p,"Rodrigo Palacio","Argentina","ST","RW",73,63,28,90,79,94); add(p,"Joleon Lescott","Inglaterra","CB","LB",39,61,75,92,55,94);
        add(p,"Steven Pienaar","África do Sul","CAM","RM",65,76,46,82,82,94); add(p,"Vincent Enyeama","Nigéria","GK",null,12,42,77,89,19,94);
        add(p,"Fabricio Coloccini","Argentina","CB",null,40,65,74,87,58,93); add(p,"Hélder Postiga","Portugal","ST","CF",73,59,28,84,71,93);
        add(p,"Phil Jagielka","Inglaterra","CB","CDM",41,62,73,90,56,93); add(p,"Simon Rolfes","Alemanha","CM","CDM",56,72,67,86,67,93);
        add(p,"Thomas Hitzlsperger","Alemanha","CM","LM",62,72,59,88,68,93); add(p,"Hamit Altıntop","Turquia","CM","RM",64,73,58,88,73,93);
        add(p,"Andreas Hinkel","Alemanha","RB","RWB",49,64,71,88,65,92); add(p,"Tuncay Şanlı","Turquia","ST","RW",71,62,34,89,74,92);
        add(p,"DaMarcus Beasley","EUA","LW","LM",65,65,40,94,80,92); add(p,"Oguchi Onyewu","EUA","CB",null,35,56,72,95,49,92);
        add(p,"Madjid Bougherra","Argélia","CB","RB",39,60,72,91,55,92); add(p,"Houssine Kharja","Marrocos","CM","CAM",60,72,56,84,72,92);
        add(p,"Mathieu Bodmer","França","CM","CDM",62,75,64,88,72,92); add(p,"Julius Aghahowa","Nigéria","ST","RW",72,55,27,94,75,92);
        add(p,"Marco Amelia","Itália","GK",null,11,40,75,87,18,92); add(p,"Allan McGregor","Escócia","GK",null,11,39,76,90,17,92);
        add(p,"Albert Riera","Espanha","LM","LW",65,71,43,84,77,92); add(p,"Andoni Iraola","Espanha","RB","RM",53,68,70,87,68,92);
        add(p,"Christian Maggio","Itália","RB","RM",58,67,69,92,71,92); add(p,"Aleksandr Kerzhakov","Rússia","ST",null,74,56,27,88,70,92);
        add(p,"Marat Izmailov","Rússia","CAM","RM",63,74,47,81,79,92); add(p,"Vasili Berezutski","Rússia","CB","RB",38,61,72,91,53,91);
        add(p,"Aleksei Berezutski","Rússia","CB","LB",38,61,72,90,53,91); add(p,"Aleksandr Anyukov","Rússia","RB","RWB",51,65,70,89,66,91);
        add(p,"Markus Rosenberg","Suécia","ST","CF",71,58,29,86,71,91); add(p,"Tobias Hysén","Suécia","LW","ST",68,64,36,90,76,91);
        add(p,"John Alvbåge","Suécia","GK",null,11,38,73,87,17,90); add(p,"Per Nilsson","Suécia","CB",null,36,59,71,89,51,90);
        add(p,"Wes Hoolahan","Irlanda","CAM","CM",60,75,39,75,80,91); add(p,"Andy Reid","Irlanda","CAM","LM",61,74,42,79,77,90);
        add(p,"Chris Baird","Irlanda do Norte","RB","CB",44,62,69,87,58,90); add(p,"Rickie Lambert","Inglaterra","ST",null,72,60,30,90,66,90);
        add(p,"Steve Sidwell","Inglaterra","CM","CDM",57,69,64,88,66,90); add(p,"Tal Ben Haim","Israel","CB","RB",37,59,71,91,52,90);
        add(p,"Gary Caldwell","Escócia","CB",null,37,61,71,90,53,90); add(p,"Boaz Myhill","País de Gales","GK",null,11,38,72,88,17,89);
        add(p,"Daniel Braaten","Noruega","RW","ST",67,62,33,93,77,90); add(p,"Yasser Al-Qahtani","Arábia Saudita","ST","CF",71,58,28,85,73,90);
        return p;
    }
    private static void add(List<Player> ps,String n,String nat,String pri,String sec,int a,int pa,int d,int f,int dr,int pot){
        Map<String,Integer> at=new HashMap<>(); at.put("ataque",a);at.put("passe",pa);at.put("defesa",d);at.put("fisico",f);at.put("drible",dr);
        ps.add(new Player(n,nat,Position.valueOf(pri),sec==null?null:Position.valueOf(sec),18,new TechnicalAttributes(at),pot,8_000d+Math.max(0,pot-78)*800d));
    }
}
