package io.github.some_example_name;
import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.*;
import java.util.*;
public final class DraftClass2000RegressionTest {
 public static void main(String[] args){
  require(DraftClassRepository.hasHistoricalClass(2000),"2000 not registered as historical"); List<Player> ps=DraftClassRepository.getClassForYear(2000); require(ps.size()==60,"2000 should provide 60 prospects");
  Set<String> earlier=new HashSet<>(); for(int y=1970;y<=1999;y++)for(Player p:DraftClassRepository.getClassForYear(y))earlier.add(norm(p.getName()));
  Map<String,Player> by=new HashMap<>(); for(Player p:ps){require(p.getAge()==18,p.getName()+" age");require(!earlier.contains(norm(p.getName())),"Repeated: "+p.getName());require(by.put(p.getName(),p)==null,"Duplicate: "+p.getName());}
  expect(by,"Kaká",Position.CAM,77,81,36,86,88,98); expect(by,"Adriano",Position.ST,80,61,29,94,78,98); expect(by,"Petr Čech",Position.GK,11,42,81,91,18,98); expect(by,"Yasser Al-Qahtani",Position.ST,71,58,28,85,73,90);
  require(by.get("Kaká").getSecondaryPosition()==Position.CF,"Kaká secondary");System.out.println("Draft class 2000: 60 unique eligible prospects, attributes and positions OK.");
 }
 private static String norm(String v){return v==null?"":v.trim().toLowerCase(Locale.ROOT);} private static void expect(Map<String,Player>b,String n,Position pos,int a,int p,int d,int f,int dr,int pot){Player x=b.get(n);require(x!=null&&x.getPrimaryPosition()==pos&&x.getPotential()==pot&&x.getTechnicalAttributes().getAtaque()==a&&x.getTechnicalAttributes().getPasse()==p&&x.getTechnicalAttributes().getDefesa()==d&&x.getTechnicalAttributes().getFisico()==f&&x.getTechnicalAttributes().getDrible()==dr,"Incorrect data for "+n);} private static void require(boolean b,String m){if(!b)throw new AssertionError(m);}
}
