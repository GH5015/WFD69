package io.github.some_example_name.database;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.NameGenerator;
import io.github.some_example_name.utils.ContractGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import io.github.some_example_name.model.TechnicalAttributes;

public class GameDatabase {
    private List<Club> clubs;
    private Random random = new Random();

    private void applyInitialContractsAndBindClubs(int currentYear) {
        for (Club club : clubs) {
            for (Player player : club.getSquad()) {
                // Associa o clube ao jogador
                player.setCurrentClub(club);

                // Gera contrato considerando idade + potencial
                ContractGenerator.generateInitialContract(player, currentYear);
            }
        }
    }

    public GameDatabase() {
        this.clubs = new ArrayList<>();
        initializeClubs();
        initializeSantosSquad();
        initializeRioImperialSquad();
        initializeMilanoCalcioSquad();
        initializeBavariaSquad();
        initializeManchesterAlbionSquad();
        initializeLondonRoyalsSquad();
        initializeAmsterdaTotalSquad();
        initializeMadridCastillaSquad();
        initializeBarcelonaMediterraneoSquad();
        initializeBudapestDanubeSquad();
        initializeLisboaAtlanticaSquad();
        initializeBuenosAiresPlataSquad();
        initializeMontevideoOrientalSquad();
        initializeParisLumiereSquad();
        initializeBelfastNorthernStarsSquad();
        initializeTokyoRisingSunSquad();
        initializeSeoulTigersSquad();
        initializeTehranLionsSquad();
        initializeBaghdadMesopotamiaSquad();
        initializeTelAvivStarsSquad();
        fillRemainingSquads();
    }

    private void initializeClubs() {
        addClub("Santos Atlântico", "Brasil", "Ocidental", 95, "santos.png");
        addClub("Rio Imperial", "Brasil", "Ocidental", 92, "rio.png");
        addClub("Milano Calcio", "Itália", "Ocidental", 93, "milano.png");
        addClub("Bavaria München", "Alemanha", "Ocidental", 94, "bavaria.png");
        addClub("Manchester Albion", "Inglaterra", "Ocidental", 93, "manchester.png");
        addClub("London Royals", "Inglaterra", "Ocidental", 91, "london.png");
        addClub("Amsterdã Total", "Holanda", "Ocidental", 93, "amsterdam_total.png");
        addClub("Madrid Castilla", "Espanha", "Ocidental", 92, "madrid.png");
        addClub("Barcelona Mediterrâneo", "Espanha", "Ocidental", 91, "barcelona.png");
        addClub("Budapest Danube", "Hungria", "Ocidental", 90, "budapest.png");
        addClub("Lisboa Atlântica", "Portugal", "Ocidental", 93, "lisboa.png");
        addClub("Buenos Aires Plata", "Argentina", "Ocidental", 91, "buenosaires.png");
        addClub("Montevideo Oriental", "Uruguai", "Ocidental", 91, "montevideo.png");
        addClub("Paris Lumière", "França", "Ocidental", 90, "paris.png");
        addClub("Belfast Northern Stars", "Irlanda do Norte", "Ocidental", 88, "belfast.png");
        addClub("Tokyo Rising Sun", "Japão", "Oriental", 85, "tokyo.png");
        addClub("Seoul Tigers", "Coreia do Sul", "Oriental", 85, "seoul.png");
        addClub("Tehran Lions", "Irã", "Oriental", 80, "tehran.png");
        addClub("Baghdad Mesopotamia", "Iraque", "Oriental", 80, "baghdad.png");
        addClub("Tel Aviv Stars", "Israel", "Oriental", 85, "telaviv.png");
    }

    private void addClub(String name, String country, String conf, int rep, String logo) {
        clubs.add(new Club(name, country, conf, rep, rep * 50000.0, name + " Arena", logo));
    }

    private void initializeSantosSquad() {
        Club s = findClub("Santos Atlântico");
        s.addPlayerToSquad(createPlayer("Gilmar", "Brasil", "GK", 39, 20, 65, 92, 92, 50000));
        s.addPlayerToSquad(createPlayer("Agnaldo", "Brasil", "GK", 24, 20, 55, 70, 78, 10000));
        s.addPlayerToSquad(createPlayer("Carlos Alberto Torres", "Brasil", "RB", 24, 82, 85, 85, 94, 45000));
        s.addPlayerToSquad(createPlayer("Joel Camargo", "Brasil", "CB", 25, 45, 70, 82, 84, 30000));
        s.addPlayerToSquad(createPlayer("Djalma Dias", "Brasil", "CB", 30, 40, 65, 84, 84, 28000));
        s.addPlayerToSquad(createPlayer("Rildo", "Brasil", "LB", 25, 72, 75, 78, 82, 25000));
        s.addPlayerToSquad(createPlayer("Zito", "Brasil", "CDM", 36, 55, 82, 90, 90, 35000));
        s.addPlayerToSquad(createPlayer("Clodoaldo", "Brasil", "CM", 20, 70, 85, 80, 92, 20000));
        s.addPlayerToSquad(createPlayer("Gérson", "Brasil", "CM", 28, 82, 97, 55, 95, 48000));
        s.addPlayerToSquad(createPlayer("Rivellino", "Brasil", "CAM", 23, 88, 90, 45, 95, 42000));
        s.addPlayerToSquad(createPlayer("Pelé", "Brasil", "ST", 28, 100, 96, 50, 100, 500000));
        s.addPlayerToSquad(createPlayer("Jairzinho", "Brasil", "RW", 25, 92, 82, 45, 96, 45000));
        s.addPlayerToSquad(createPlayer("Edu", "Brasil", "LW", 19, 78, 80, 40, 93, 20000));
        s.addPlayerToSquad(createPlayer("Paulo César Caju", "Brasil", "LW", 20, 75, 78, 42, 90, 22000));
        s.addPlayerToSquad(createPlayer("Tostão", "Brasil", "CF", 22, 90, 88, 35, 95, 40000));
        s.addPlayerToSquad(createPlayer("Toninho Guerreiro", "Brasil", "ST", 26, 82, 65, 30, 84, 28000));
    }

    private void initializeRioImperialSquad() {
        Club r = findClub("Rio Imperial");
        r.addPlayerToSquad(createPlayer("Félix", "Brasil", "GK", 31, 20, 65, 88, 88, 32000));
        r.addPlayerToSquad(createPlayer("Ubirajara", "Brasil", "GK", 32, 20, 60, 75, 76, 12000));
        r.addPlayerToSquad(createPlayer("Brito", "Brasil", "CB", 30, 35, 65, 86, 86, 28000));
        r.addPlayerToSquad(createPlayer("Piazza", "Brasil", "CB", 26, 55, 82, 88, 92, 38000));
        r.addPlayerToSquad(createPlayer("Carlos Alberto Pintinho", "Brasil", "CDM", 22, 55, 78, 76, 85, 18000));
        r.addPlayerToSquad(createPlayer("Marco Antônio", "Brasil", "LB", 19, 65, 72, 72, 88, 15000));
        r.addPlayerToSquad(createPlayer("Joel", "Brasil", "RB", 28, 60, 68, 75, 77, 14000));
        r.addPlayerToSquad(createPlayer("Paulo César Carpegiani", "Brasil", "CM", 20, 65, 78, 60, 88, 16000));
        r.addPlayerToSquad(createPlayer("Ademir da Guia", "Brasil", "CM", 27, 78, 94, 60, 94, 45000));
        r.addPlayerToSquad(createPlayer("Dirceu Lopes", "Brasil", "CAM", 23, 86, 90, 45, 93, 40000));
        r.addPlayerToSquad(createPlayer("Edu Coimbra", "Brasil", "CAM", 26, 75, 88, 40, 85, 25000));
        r.addPlayerToSquad(createPlayer("Jair da Costa", "Brasil", "RW", 29, 78, 72, 35, 80, 22000));
        r.addPlayerToSquad(createPlayer("Rogério", "Brasil", "ST", 24, 80, 70, 35, 83, 20000));
    }

    private void initializeMilanoCalcioSquad() {
        Club m = findClub("Milano Calcio");
        m.addPlayerToSquad(createPlayer("Dino Zoff", "Itália", "GK", 27, 20, 70, 90, 98, 40000));
        m.addPlayerToSquad(createPlayer("Enrico Albertosi", "Itália", "GK", 29, 20, 65, 87, 90, 30000));
        m.addPlayerToSquad(createPlayer("Giacinto Facchetti", "Itália", "LB", 26, 82, 84, 88, 96, 48000));
        m.addPlayerToSquad(createPlayer("Tarcisio Burgnich", "Itália", "CB", 30, 35, 65, 90, 90, 32000));
        m.addPlayerToSquad(createPlayer("Cesare Maldini", "Itália", "CB", 36, 30, 70, 86, 86, 25000));
        m.addPlayerToSquad(createPlayer("Armando Picchi", "Itália", "CB", 33, 35, 85, 92, 94, 35000));
        m.addPlayerToSquad(createPlayer("Roberto Rosato", "Itália", "CB", 25, 30, 72, 87, 91, 28000));
        m.addPlayerToSquad(createPlayer("Gianni Rivera", "Itália", "CAM", 26, 86, 98, 40, 97, 55000));
        m.addPlayerToSquad(createPlayer("Sandro Mazzola", "Itália", "ST", 26, 88, 90, 45, 95, 52000));
        m.addPlayerToSquad(createPlayer("Giancarlo De Sisti", "Itália", "CM", 25, 65, 90, 72, 90, 30000));
        m.addPlayerToSquad(createPlayer("Giovanni Lodetti", "Itália", "CDM", 27, 50, 75, 85, 86, 22000));
        m.addPlayerToSquad(createPlayer("Mario Corso", "Itália", "LM", 28, 80, 88, 40, 88, 28000));
        m.addPlayerToSquad(createPlayer("Luigi Riva", "Itália", "ST", 24, 94, 75, 35, 98, 50000));
        m.addPlayerToSquad(createPlayer("Roberto Boninsegna", "Itália", "ST", 26, 88, 70, 35, 91, 35000));
        m.addPlayerToSquad(createPlayer("Pietro Anastasi", "Itália", "ST", 21, 78, 68, 30, 90, 25000));
        m.addPlayerToSquad(createPlayer("Pierino Prati", "Itália", "ST", 22, 84, 70, 30, 92, 26000));
        m.addPlayerToSquad(createPlayer("Fabio Capello", "Itália", "CM", 23, 72, 78, 60, 88, 24000));
    }

    private void initializeBavariaSquad() {
        Club b = findClub("Bavaria München");
        b.addPlayerToSquad(createPlayer("Sepp Maier", "Alemanha", "GK", 25, 20, 75, 93, 98, 45000));
        b.addPlayerToSquad(createPlayer("Wolfgang Kleff", "Alemanha", "GK", 22, 20, 65, 76, 88, 15000));
        b.addPlayerToSquad(createPlayer("Franz Beckenbauer", "Alemanha", "CB", 23, 78, 95, 94, 100, 60000));
        b.addPlayerToSquad(createPlayer("Georg Schwarzenbeck", "Alemanha", "CB", 21, 35, 72, 88, 94, 25000));
        b.addPlayerToSquad(createPlayer("Paul Breitner", "Alemanha", "LB", 18, 68, 78, 72, 98, 20000));
        b.addPlayerToSquad(createPlayer("Hans-Georg Schwarzenbeck", "Alemanha", "CB", 21, 30, 70, 86, 90, 20000));
        b.addPlayerToSquad(createPlayer("Franz Roth", "Alemanha", "CDM", 22, 55, 75, 82, 90, 22000));
        b.addPlayerToSquad(createPlayer("Günter Netzer", "Alemanha", "CAM", 24, 84, 97, 40, 98, 55000));
        b.addPlayerToSquad(createPlayer("Herbert Wimmer", "Alemanha", "CM", 24, 55, 82, 84, 88, 26000));
        b.addPlayerToSquad(createPlayer("Wolfgang Overath", "Alemanha", "CM", 25, 80, 94, 45, 96, 48000));
        b.addPlayerToSquad(createPlayer("Jürgen Grabowski", "Alemanha", "RW", 25, 82, 86, 40, 92, 35000));
        b.addPlayerToSquad(createPlayer("Gerd Müller", "Alemanha", "ST", 23, 99, 70, 30, 100, 58000));
        b.addPlayerToSquad(createPlayer("Hannes Löhr", "Alemanha", "ST", 27, 86, 70, 35, 88, 30000));
        b.addPlayerToSquad(createPlayer("Sigfried Held", "Alemanha", "LW", 27, 80, 75, 35, 85, 28000));
        b.addPlayerToSquad(createPlayer("Reinhard Libuda", "Alemanha", "RW", 25, 83, 78, 35, 88, 32000));
    }

    private void initializeManchesterAlbionSquad() {
        Club m = findClub("Manchester Albion");
        m.addPlayerToSquad(createPlayer("Gordon Banks", "Inglaterra", "GK", 31, 20, 75, 94, 96, 45000));
        m.addPlayerToSquad(createPlayer("Alex Stepney", "Inglaterra", "GK", 27, 20, 65, 82, 86, 20000));
        m.addPlayerToSquad(createPlayer("Bobby Moore", "Inglaterra", "CB", 28, 45, 92, 94, 98, 55000));
        m.addPlayerToSquad(createPlayer("Jack Charlton", "Inglaterra", "CB", 34, 40, 65, 88, 88, 25000));
        m.addPlayerToSquad(createPlayer("Ray Wilson", "Inglaterra", "LB", 34, 55, 72, 84, 84, 22000));
        m.addPlayerToSquad(createPlayer("George Cohen", "Inglaterra", "RB", 30, 60, 75, 86, 88, 24000));
        m.addPlayerToSquad(createPlayer("Nobby Stiles", "Inglaterra", "CDM", 27, 40, 70, 91, 92, 30000));
        m.addPlayerToSquad(createPlayer("Bobby Charlton", "Inglaterra", "CAM", 31, 86, 91, 50, 97, 58000));
        m.addPlayerToSquad(createPlayer("Martin Peters", "Inglaterra", "CM", 25, 78, 84, 65, 94, 38000));
        m.addPlayerToSquad(createPlayer("Alan Mullery", "Inglaterra", "CDM", 27, 55, 78, 83, 86, 26000));
        m.addPlayerToSquad(createPlayer("Colin Bell", "Inglaterra", "CM", 23, 80, 87, 72, 96, 48000));
        m.addPlayerToSquad(createPlayer("Geoff Hurst", "Inglaterra", "ST", 27, 88, 72, 35, 92, 45000));
        m.addPlayerToSquad(createPlayer("Jimmy Greaves", "Inglaterra", "ST", 29, 93, 75, 30, 96, 50000));
        m.addPlayerToSquad(createPlayer("Francis Lee", "Inglaterra", "ST", 25, 84, 78, 40, 91, 35000));
        m.addPlayerToSquad(createPlayer("Roger Hunt", "Inglaterra", "ST", 31, 82, 70, 35, 85, 28000));
    }

    private void initializeLondonRoyalsSquad() {
        Club l = findClub("London Royals");
        l.addPlayerToSquad(createPlayer("Peter Bonetti", "Inglaterra", "GK", 28, 20, 78, 88, 92, 35000));
        l.addPlayerToSquad(createPlayer("Gordon West", "Inglaterra", "GK", 25, 20, 65, 78, 82, 18000));
        l.addPlayerToSquad(createPlayer("Dave Mackay", "Escócia", "CB", 35, 45, 80, 88, 88, 30000));
        l.addPlayerToSquad(createPlayer("Terry Venables", "Inglaterra", "CM", 26, 60, 85, 70, 88, 32000));
        l.addPlayerToSquad(createPlayer("Keith Newton", "Inglaterra", "RB", 28, 55, 72, 82, 85, 20000));
        l.addPlayerToSquad(createPlayer("Terry Cooper", "Inglaterra", "LB", 25, 75, 78, 78, 90, 28000));
        l.addPlayerToSquad(createPlayer("Ron Harris", "Inglaterra", "CB", 24, 35, 65, 86, 88, 22000));
        l.addPlayerToSquad(createPlayer("Alan Ball", "Inglaterra", "CM", 23, 78, 84, 75, 95, 45000));
        l.addPlayerToSquad(createPlayer("Martin Chivers", "Inglaterra", "ST", 24, 80, 75, 40, 90, 34000));
        l.addPlayerToSquad(createPlayer("Peter Osgood", "Inglaterra", "CF", 26, 87, 85, 45, 94, 48000));
        l.addPlayerToSquad(createPlayer("Bobby Tambling", "Inglaterra", "ST", 28, 86, 68, 30, 88, 35000));
        l.addPlayerToSquad(createPlayer("Geoff Astle", "Inglaterra", "ST", 26, 82, 65, 35, 87, 28000));
        l.addPlayerToSquad(createPlayer("Cliff Jones", "País de Gales", "RW", 33, 75, 75, 35, 78, 20000));
    }

    private void initializeAmsterdaTotalSquad() {
        Club a = findClub("Amsterdã Total");
        a.addPlayerToSquad(createPlayer("Jan Jongbloed", "Holanda", "GK", 28, 20, 82, 82, 90, 25000));
        a.addPlayerToSquad(createPlayer("Heinz Stuy", "Holanda", "GK", 26, 20, 70, 84, 90, 22000));
        a.addPlayerToSquad(createPlayer("Ruud Krol", "Holanda", "LB", 20, 72, 88, 84, 98, 45000));
        a.addPlayerToSquad(createPlayer("Wim Suurbier", "Holanda", "RB", 24, 78, 82, 78, 90, 30000));
        a.addPlayerToSquad(createPlayer("Barry Hulshoff", "Holanda", "CB", 22, 40, 80, 84, 92, 28000));
        a.addPlayerToSquad(createPlayer("Frits Soetekouw", "Holanda", "CB", 32, 35, 68, 82, 82, 18000));
        a.addPlayerToSquad(createPlayer("Theo van Duivenbode", "Holanda", "RB", 27, 55, 75, 80, 84, 20000));
        a.addPlayerToSquad(createPlayer("Wim Jansen", "Holanda", "CDM", 22, 50, 86, 82, 94, 32000));
        a.addPlayerToSquad(createPlayer("Gerrie Mühren", "Holanda", "CM", 23, 72, 90, 55, 94, 35000));
        a.addPlayerToSquad(createPlayer("Henk Groot", "Holanda", "CAM", 31, 80, 85, 45, 85, 25000));
        a.addPlayerToSquad(createPlayer("Johan Cruyff", "Holanda", "CF", 22, 94, 96, 55, 100, 500000));
        a.addPlayerToSquad(createPlayer("Piet Keizer", "Holanda", "LW", 25, 84, 88, 40, 94, 45000));
        a.addPlayerToSquad(createPlayer("Sjaak Swart", "Holanda", "RW", 30, 78, 82, 35, 82, 28000));
        a.addPlayerToSquad(createPlayer("Dick van Dijk", "Holanda", "ST", 24, 82, 68, 30, 88, 30000));
        a.addPlayerToSquad(createPlayer("Rinus Israël", "Holanda", "CB", 27, 35, 72, 87, 90, 32000));
    }

    private void initializeMadridCastillaSquad() {
        Club m = findClub("Madrid Castilla");
        m.addPlayerToSquad(createPlayer("José Ángel Iribar", "Espanha", "GK", 26, 20, 72, 90, 96, 42000));
        m.addPlayerToSquad(createPlayer("Miguel Ángel González", "Espanha", "GK", 22, 20, 65, 76, 94, 15000));
        m.addPlayerToSquad(createPlayer("José Santamaría", "Espanha", "CB", 39, 30, 82, 88, 88, 30000));
        m.addPlayerToSquad(createPlayer("Manuel Sanchís pai", "Espanha", "CB", 33, 35, 70, 84, 84, 25000));
        m.addPlayerToSquad(createPlayer("José Antonio Camacho", "Espanha", "LB", 23, 65, 75, 84, 96, 28000));
        m.addPlayerToSquad(createPlayer("José Luis López Peinado", "Espanha", "RB", 23, 60, 72, 78, 88, 20000));
        m.addPlayerToSquad(createPlayer("Ignacio Zoco", "Espanha", "CDM", 33, 45, 76, 88, 88, 26000));
        m.addPlayerToSquad(createPlayer("Amancio Amaro", "Espanha", "RW", 30, 88, 88, 45, 94, 50000));
        m.addPlayerToSquad(createPlayer("Pirri", "Espanha", "CM", 24, 70, 82, 82, 94, 45000));
        m.addPlayerToSquad(createPlayer("Luis del Sol", "Espanha", "CM", 33, 65, 86, 65, 84, 28000));
        m.addPlayerToSquad(createPlayer("Francisco Gento", "Espanha", "LW", 35, 82, 78, 35, 85, 30000));
        m.addPlayerToSquad(createPlayer("Manuel Velázquez", "Espanha", "CM", 28, 72, 90, 50, 92, 35000));
        m.addPlayerToSquad(createPlayer("Luis Aragonés", "Espanha", "CAM", 30, 75, 85, 45, 88, 32000));
        m.addPlayerToSquad(createPlayer("José Eulogio Gárate", "Espanha", "ST", 25, 88, 75, 30, 94, 40000));
        m.addPlayerToSquad(createPlayer("Pedro Amancio", "Espanha", "ST", 23, 82, 75, 35, 88, 25000));
    }

    private void initializeBarcelonaMediterraneoSquad() {
        Club b = findClub("Barcelona Mediterrâneo");
        b.addPlayerToSquad(createPlayer("Salvador Sadurní", "Espanha", "GK", 25, 20, 72, 87, 92, 32000));
        b.addPlayerToSquad(createPlayer("Reina", "Espanha", "GK", 24, 20, 70, 84, 90, 20000));
        b.addPlayerToSquad(createPlayer("Eladio Silvestre", "Espanha", "CB", 29, 35, 70, 85, 87, 22000));
        b.addPlayerToSquad(createPlayer("Gallego", "Espanha", "CB", 28, 35, 72, 84, 86, 21000));
        b.addPlayerToSquad(createPlayer("Ferran Olivella", "Espanha", "CB", 30, 40, 78, 87, 88, 25000));
        b.addPlayerToSquad(createPlayer("Benítez", "Espanha", "RB", 23, 65, 75, 78, 88, 18000));
        b.addPlayerToSquad(createPlayer("Josep Maria Fusté", "Espanha", "CM", 26, 70, 85, 65, 90, 30000));
        b.addPlayerToSquad(createPlayer("Carles Rexach", "Espanha", "RW", 22, 78, 88, 40, 95, 40000));
        b.addPlayerToSquad(createPlayer("Joaquim Rifé", "Espanha", "CM", 27, 60, 82, 75, 86, 22000));
        b.addPlayerToSquad(createPlayer("Josep Maria Martí Filosía", "Espanha", "CM", 25, 55, 80, 72, 84, 18000));
        b.addPlayerToSquad(createPlayer("Luis Suárez Miramontes", "Espanha", "CAM", 35, 78, 95, 45, 90, 45000));
        b.addPlayerToSquad(createPlayer("Salvador Artigas", "Espanha", "CM", 35, 65, 82, 50, 82, 15000));
        b.addPlayerToSquad(createPlayer("Chus Pereda", "Espanha", "CAM", 31, 76, 86, 40, 85, 24000));
        b.addPlayerToSquad(createPlayer("Evaristo de Macedo", "Brasil", "ST", 35, 80, 70, 30, 82, 18000));
        b.addPlayerToSquad(createPlayer("Pedro Zaballa", "Espanha", "RW", 31, 75, 72, 35, 78, 16000));
        b.addPlayerToSquad(createPlayer("Marcial Pina", "Espanha", "ST", 26, 82, 70, 30, 88, 26000));
    }

    private void initializeBudapestDanubeSquad() {
        Club b = findClub("Budapest Danube");
        b.addPlayerToSquad(createPlayer("József Szentmihályi", "Hungria", "GK", 33, 20, 70, 84, 84, 20000));
        b.addPlayerToSquad(createPlayer("Antal Dunai", "Hungria", "GK", 26, 20, 65, 76, 82, 15000));
        b.addPlayerToSquad(createPlayer("László Sárosi", "Hungria", "CB", 35, 40, 82, 84, 84, 25000));
        b.addPlayerToSquad(createPlayer("Lajos Szűcs", "Hungria", "CB", 26, 40, 75, 86, 92, 30000));
        b.addPlayerToSquad(createPlayer("József Bozsik", "Hungria", "CM", 44, 45, 90, 60, 70, 10000));
        b.addPlayerToSquad(createPlayer("Antal Dunai II", "Hungria", "RB", 26, 68, 78, 72, 85, 20000));
        b.addPlayerToSquad(createPlayer("Károly Sándor", "Hungria", "RB", 35, 55, 75, 75, 78, 18000));
        b.addPlayerToSquad(createPlayer("Flórián Albert", "Hungria", "CAM", 27, 90, 94, 45, 98, 55000));
        b.addPlayerToSquad(createPlayer("József Varga", "Hungria", "CM", 23, 65, 82, 70, 88, 22000));
        b.addPlayerToSquad(createPlayer("István Sándor", "Hungria", "CM", 24, 70, 84, 55, 88, 20000));
        b.addPlayerToSquad(createPlayer("Ferenc Bene", "Hungria", "ST", 24, 89, 75, 35, 94, 48000));
        b.addPlayerToSquad(createPlayer("Lajos Tichy", "Hungria", "ST", 34, 82, 72, 30, 82, 25000));
        b.addPlayerToSquad(createPlayer("Zoltán Varga", "Hungria", "RW", 24, 80, 86, 40, 92, 35000));
        b.addPlayerToSquad(createPlayer("János Farkas", "Hungria", "RW", 25, 84, 76, 35, 88, 30000));
    }

    private void initializeLisboaAtlanticaSquad() {
        Club l = findClub("Lisboa Atlântica");
        l.addPlayerToSquad(createPlayer("Manuel Bento", "Portugal", "GK", 20, 20, 65, 76, 94, 15000));
        l.addPlayerToSquad(createPlayer("José Henrique", "Portugal", "GK", 26, 20, 70, 86, 90, 25000));
        l.addPlayerToSquad(createPlayer("Humberto Coelho", "Portugal", "CB", 18, 40, 75, 78, 96, 22000));
        l.addPlayerToSquad(createPlayer("Germano", "Portugal", "CB", 34, 35, 72, 86, 86, 20000));
        l.addPlayerToSquad(createPlayer("António Morais", "Portugal", "RB", 30, 55, 75, 82, 84, 18000));
        l.addPlayerToSquad(createPlayer("Hilário", "Portugal", "LB", 34, 60, 78, 80, 80, 18000));
        l.addPlayerToSquad(createPlayer("Artur Jorge", "Portugal", "CB", 22, 35, 70, 75, 88, 20000));
        l.addPlayerToSquad(createPlayer("Mário Coluna", "Portugal", "CM", 33, 70, 94, 78, 94, 45000));
        l.addPlayerToSquad(createPlayer("Jaime Graça", "Portugal", "CDM", 27, 55, 82, 85, 88, 28000));
        l.addPlayerToSquad(createPlayer("António Simões", "Portugal", "LW", 26, 84, 90, 40, 95, 42000));
        l.addPlayerToSquad(createPlayer("José Augusto", "Portugal", "RW", 30, 80, 82, 40, 84, 25000));
        l.addPlayerToSquad(createPlayer("Eusébio", "Portugal", "ST", 27, 98, 82, 35, 100, 500000));
        l.addPlayerToSquad(createPlayer("José Torres", "Portugal", "ST", 29, 84, 70, 35, 86, 32000));
        l.addPlayerToSquad(createPlayer("Joaquim Santana", "Portugal", "ST", 30, 78, 72, 30, 80, 18000));
    }

    private void initializeBuenosAiresPlataSquad() {
        Club b = findClub("Buenos Aires Plata");
        b.addPlayerToSquad(createPlayer("Antonio Roma", "Argentina", "GK", 35, 20, 68, 86, 86, 30000));
        b.addPlayerToSquad(createPlayer("Agustín Cejas", "Argentina", "GK", 23, 20, 70, 82, 92, 25000));
        b.addPlayerToSquad(createPlayer("Roberto Perfumo", "Argentina", "CB", 27, 40, 88, 91, 96, 45000));
        b.addPlayerToSquad(createPlayer("Daniel Onega", "Argentina", "CM", 25, 55, 80, 70, 84, 28000));
        b.addPlayerToSquad(createPlayer("Silvio Marzolini", "Argentina", "LB", 28, 72, 84, 88, 94, 40000));
        b.addPlayerToSquad(createPlayer("Rubén Suñé", "Argentina", "CDM", 22, 45, 75, 78, 90, 22000));
        b.addPlayerToSquad(createPlayer("Roberto Mouzo", "Argentina", "CB", 18, 30, 65, 65, 92, 15000));
        b.addPlayerToSquad(createPlayer("Carlos Babington", "Argentina", "CAM", 19, 70, 85, 45, 93, 20000));
        b.addPlayerToSquad(createPlayer("Miguel Ángel Brindisi", "Argentina", "CM", 18, 68, 82, 55, 95, 25000));
        b.addPlayerToSquad(createPlayer("Roberto Telch", "Argentina", "CDM", 24, 50, 78, 82, 88, 22000));
        b.addPlayerToSquad(createPlayer("Rubén Ayala", "Argentina", "ST", 18, 65, 78, 45, 90, 18000));
        b.addPlayerToSquad(createPlayer("Oscar Más", "Argentina", "LW", 22, 86, 78, 35, 92, 35000));
        b.addPlayerToSquad(createPlayer("Héctor Yazalde", "Argentina", "ST", 22, 85, 72, 30, 94, 38000));
        b.addPlayerToSquad(createPlayer("Carlos Bianchi", "Argentina", "ST", 20, 75, 65, 25, 95, 24000));
        b.addPlayerToSquad(createPlayer("Raúl Savoy", "Argentina", "ST", 29, 76, 70, 30, 80, 20000));
    }

    private void initializeMontevideoOrientalSquad() {
        Club m = findClub("Montevideo Oriental");
        m.addPlayerToSquad(createPlayer("Ladislao Mazurkiewicz", "Uruguai", "GK", 25, 20, 78, 92, 97, 42000));
        m.addPlayerToSquad(createPlayer("Roberto Sosa", "Uruguai", "GK", 28, 20, 65, 78, 82, 18000));
        m.addPlayerToSquad(createPlayer("Roberto Matosas", "Uruguai", "CB", 31, 35, 75, 88, 88, 28000));
        m.addPlayerToSquad(createPlayer("Juan Carlos González", "Uruguai", "CB", 32, 30, 68, 85, 85, 20000));
        m.addPlayerToSquad(createPlayer("Julio César Cortés", "Uruguai", "CM", 25, 65, 82, 72, 88, 22000));
        m.addPlayerToSquad(createPlayer("Nelson Díaz", "Uruguai", "RB", 26, 55, 70, 80, 84, 18000));
        m.addPlayerToSquad(createPlayer("Atilio Ancheta", "Uruguai", "CB", 18, 35, 72, 75, 95, 25000));
        m.addPlayerToSquad(createPlayer("Pedro Rocha", "Uruguai", "CAM", 26, 86, 94, 45, 98, 50000));
        m.addPlayerToSquad(createPlayer("Luis Cubilla", "Uruguai", "RW", 28, 82, 84, 40, 90, 35000));
        m.addPlayerToSquad(createPlayer("Omar Caetano", "Uruguai", "CDM", 29, 45, 72, 87, 88, 24000));
        m.addPlayerToSquad(createPlayer("Julio César Morales", "Uruguai", "CM", 24, 70, 82, 55, 88, 22000));
        m.addPlayerToSquad(createPlayer("Rubén Bareño", "Uruguai", "CM", 23, 55, 76, 70, 85, 18000));
        m.addPlayerToSquad(createPlayer("Héctor Silva", "Uruguai", "ST", 24, 82, 68, 30, 87, 26000));
        m.addPlayerToSquad(createPlayer("Alberto Spencer", "Equador", "ST", 32, 88, 72, 30, 90, 48000));
        m.addPlayerToSquad(createPlayer("Julio César Abbadie", "Uruguai", "RW", 34, 75, 76, 35, 78, 16000));
    }

    private void initializeParisLumiereSquad() {
        Club p = findClub("Paris Lumière");
        p.addPlayerToSquad(createPlayer("Georges Carnus", "França", "GK", 26, 20, 75, 88, 94, 30000));
        p.addPlayerToSquad(createPlayer("Jean Djorkaeff", "França", "GK", 30, 20, 65, 76, 80, 15000));
        p.addPlayerToSquad(createPlayer("Robert Herbin", "França", "CB", 30, 55, 86, 86, 92, 35000));
        p.addPlayerToSquad(createPlayer("Bernard Bosquier", "França", "CB", 27, 35, 75, 88, 92, 32000));
        p.addPlayerToSquad(createPlayer("Bernard Mendy", "França", "RB", 24, 60, 76, 78, 86, 18000));
        p.addPlayerToSquad(createPlayer("Aimé Jacquet", "França", "CDM", 27, 40, 75, 84, 88, 25000));
        p.addPlayerToSquad(createPlayer("Jean-Michel Larqué", "França", "CM", 27, 75, 91, 60, 94, 42000));
        p.addPlayerToSquad(createPlayer("Henri Michel", "França", "CM", 22, 68, 88, 55, 93, 38000));
        p.addPlayerToSquad(createPlayer("Georges Bereta", "França", "LM", 25, 75, 82, 45, 88, 28000));
        p.addPlayerToSquad(createPlayer("Jacques Novi", "França", "CDM", 28, 45, 75, 82, 84, 22000));
        p.addPlayerToSquad(createPlayer("Raymond Kopa", "França", "CAM", 37, 75, 95, 35, 85, 35000));
        p.addPlayerToSquad(createPlayer("Hervé Revelli", "França", "ST", 23, 87, 70, 30, 94, 45000));
        p.addPlayerToSquad(createPlayer("Georges Lech", "França", "RW", 26, 80, 78, 35, 86, 26000));
        p.addPlayerToSquad(createPlayer("Bernard Blanchet", "França", "LW", 25, 78, 72, 35, 84, 22000));
    }

    private void initializeBelfastNorthernStarsSquad() {
        Club b = findClub("Belfast Northern Stars");
        b.addPlayerToSquad(createPlayer("Pat Jennings", "Irlanda do Norte", "GK", 23, 20, 78, 90, 97, 35000));
        b.addPlayerToSquad(createPlayer("Harry Gregg", "Irlanda do Norte", "GK", 37, 20, 70, 84, 84, 15000));
        b.addPlayerToSquad(createPlayer("Danny Blanchflower", "Irlanda do Norte", "CB", 42, 45, 90, 70, 75, 10000));
        b.addPlayerToSquad(createPlayer("John Cushley", "Escócia", "CB", 29, 30, 65, 82, 84, 18000));
        b.addPlayerToSquad(createPlayer("Martin O'Neill", "Irlanda do Norte", "CM", 18, 45, 72, 70, 90, 12000));
        b.addPlayerToSquad(createPlayer("Jimmy Nicholl", "Irlanda do Norte", "RB", 22, 60, 70, 78, 88, 15000));
        b.addPlayerToSquad(createPlayer("Terry Neill", "Irlanda do Norte", "CB", 27, 35, 70, 83, 86, 22000));
        b.addPlayerToSquad(createPlayer("George Best", "Irlanda do Norte", "LW", 23, 96, 92, 45, 100, 500000));
        b.addPlayerToSquad(createPlayer("Dave Clements", "Irlanda do Norte", "CM", 22, 60, 78, 75, 88, 18000));
        b.addPlayerToSquad(createPlayer("Billy Hamilton", "Irlanda do Norte", "ST", 18, 65, 72, 40, 88, 10000));
        b.addPlayerToSquad(createPlayer("Derek Dougan", "Irlanda do Norte", "ST", 36, 75, 72, 35, 78, 12000));
        b.addPlayerToSquad(createPlayer("Jimmy McIlroy", "Irlanda do Norte", "CAM", 36, 70, 85, 40, 78, 11000));
        b.addPlayerToSquad(createPlayer("Joe Baker", "Inglaterra", "ST", 28, 84, 70, 30, 86, 25000));
        b.addPlayerToSquad(createPlayer("Trevor Anderson", "Irlanda do Norte", "RW", 21, 75, 65, 30, 88, 14000));
        b.addPlayerToSquad(createPlayer("Sammy Morgan", "Irlanda do Norte", "ST", 24, 78, 68, 30, 82, 16000));
    }

    private void initializeTokyoRisingSunSquad() {
        Club t = findClub("Tokyo Rising Sun");
        t.addPlayerToSquad(createPlayer("Kenzo Yokoyama", "Japão", "GK", 27, 20, 72, 84, 90, 15000));
        t.addPlayerToSquad(createPlayer("Daijiro Takakuwa", "Japão", "GK", 25, 20, 65, 76, 84, 10000));
        t.addPlayerToSquad(createPlayer("Masao Uchino", "Japão", "CB", 27, 30, 70, 82, 88, 12000));
        t.addPlayerToSquad(createPlayer("Yoshikazu Nagai", "Japão", "RB", 23, 55, 72, 78, 88, 11000));
        t.addPlayerToSquad(createPlayer("Ryoichi Hasegawa", "Japão", "CB", 25, 35, 68, 80, 85, 10000));
        t.addPlayerToSquad(createPlayer("Takaji Mori", "Japão", "LB", 27, 60, 75, 78, 86, 11000));
        t.addPlayerToSquad(createPlayer("Shigeo Yaegashi", "Japão", "CM", 34, 60, 84, 65, 82, 14000));
        t.addPlayerToSquad(createPlayer("Aritatsu Ogi", "Japão", "CM", 25, 65, 80, 70, 88, 13000));
        t.addPlayerToSquad(createPlayer("Saburo Kawabuchi", "Japão", "RM", 32, 55, 78, 68, 80, 10000));
        t.addPlayerToSquad(createPlayer("Kunishige Kamamoto", "Japão", "ST", 25, 90, 78, 35, 96, 45000));
        t.addPlayerToSquad(createPlayer("Yasuyuki Kuwahara", "Japão", "RW", 23, 75, 80, 40, 90, 12000));
        t.addPlayerToSquad(createPlayer("Ryuichi Sugiyama", "Japão", "LW", 28, 78, 86, 45, 90, 18000));
        t.addPlayerToSquad(createPlayer("Shunichiro Okano", "Japão", "CF", 32, 72, 70, 30, 78, 9000));
    }

    private void initializeSeoulTigersSquad() {
        Club s = findClub("Seoul Tigers");
        s.addPlayerToSquad(createPlayer("Lee Se-yeon", "Coreia do Sul", "GK", 26, 20, 68, 84, 90, 15000));
        s.addPlayerToSquad(createPlayer("Kim Yong-bae", "Coreia do Sul", "GK", 24, 20, 62, 76, 82, 10000));
        s.addPlayerToSquad(createPlayer("Kim Ho", "Coreia do Sul", "CB", 26, 30, 65, 82, 88, 12000));
        s.addPlayerToSquad(createPlayer("Lee Kang-jo", "Coreia do Sul", "CB", 29, 35, 68, 84, 85, 11000));
        s.addPlayerToSquad(createPlayer("Park Soo-il", "Coreia do Sul", "RB", 24, 55, 70, 78, 88, 10000));
        s.addPlayerToSquad(createPlayer("Choi Jong-deok", "Coreia do Sul", "LB", 25, 60, 72, 77, 86, 11000));
        s.addPlayerToSquad(createPlayer("Kim Jung-nam", "Coreia do Sul", "CDM", 26, 35, 75, 82, 90, 13000));
        s.addPlayerToSquad(createPlayer("Lee Hoe-taik", "Coreia do Sul", "CAM", 23, 75, 84, 50, 93, 20000));
        s.addPlayerToSquad(createPlayer("Park Lee-chun", "Coreia do Sul", "CM", 28, 65, 80, 70, 88, 15000));
        s.addPlayerToSquad(createPlayer("Kim Sam-rak", "Coreia do Sul", "CDM", 25, 45, 72, 82, 86, 11000));
        s.addPlayerToSquad(createPlayer("Choi Chung-min", "Coreia do Sul", "CAM", 33, 72, 80, 40, 82, 14000));
        s.addPlayerToSquad(createPlayer("Kim Yong-sik", "Coreia do Sul", "ST", 35, 70, 65, 30, 75, 9000));
        s.addPlayerToSquad(createPlayer("Shin Dong-woo", "Coreia do Sul", "RW", 24, 73, 70, 35, 84, 11000));
        s.addPlayerToSquad(createPlayer("Kim Jin-kook", "Coreia do Sul", "ST", 22, 75, 65, 30, 87, 12000));
    }

    private void initializeTehranLionsSquad() {
        Club t = findClub("Tehran Lions");
        t.addPlayerToSquad(createPlayer("Nasser Hejazi", "Irã", "GK", 20, 20, 75, 78, 97, 18000));
        t.addPlayerToSquad(createPlayer("Parviz Ghelichkhani", "Irã", "GK", 24, 20, 65, 74, 84, 12000));
        t.addPlayerToSquad(createPlayer("Mohammad Reza Adelkhani", "Irã", "RB", 25, 65, 72, 76, 88, 15000));
        t.addPlayerToSquad(createPlayer("Hassan Habibi", "Irã", "CB", 28, 35, 75, 84, 90, 18000));
        t.addPlayerToSquad(createPlayer("Mansour Rashidi", "Irã", "CB", 24, 30, 68, 80, 85, 12000));
        t.addPlayerToSquad(createPlayer("Ali Jabbari", "Irã", "CM", 24, 60, 82, 78, 92, 20000));
        t.addPlayerToSquad(createPlayer("Ali Parvin", "Irã", "CAM", 23, 75, 86, 45, 96, 30000));
        t.addPlayerToSquad(createPlayer("Homayoun Behzadi", "Irã", "CF", 25, 82, 76, 35, 90, 22000));
        t.addPlayerToSquad(createPlayer("Hossein Kalani", "Irã", "ST", 22, 80, 68, 30, 93, 20000));
        t.addPlayerToSquad(createPlayer("Gholam Hossein Mazloomi", "Irã", "ST", 19, 78, 65, 30, 95, 18000));
        t.addPlayerToSquad(createPlayer("Mahmoud Khordbin", "Irã", "RW", 22, 72, 74, 35, 88, 15000));
        t.addPlayerToSquad(createPlayer("Parviz Mazloomi", "Irã", "CF", 21, 65, 72, 40, 90, 14000));
    }

    private void initializeBaghdadMesopotamiaSquad() {
        Club b = findClub("Baghdad Mesopotamia");
        b.addPlayerToSquad(createPlayer("Jalil Zand", "Iraque", "GK", 26, 20, 65, 80, 87, 12000));
        b.addPlayerToSquad(createPlayer("Shaker Ismail", "Iraque", "GK", 23, 20, 60, 73, 82, 9000));
        b.addPlayerToSquad(createPlayer("Ammo Baba", "Iraque", "CAM", 31, 70, 88, 65, 90, 15000));
        b.addPlayerToSquad(createPlayer("Abdul Kadhim", "Iraque", "CB", 27, 30, 68, 82, 86, 11000));
        b.addPlayerToSquad(createPlayer("Ali Kadhim", "Iraque", "RB", 22, 60, 70, 75, 88, 10000));
        b.addPlayerToSquad(createPlayer("Ismail Mohammed", "Iraque", "CB", 25, 35, 65, 78, 83, 10000));
        b.addPlayerToSquad(createPlayer("Falah Hassan", "Iraque", "LB", 24, 65, 75, 72, 88, 11000));
        b.addPlayerToSquad(createPlayer("Rahim Hameed", "Iraque", "CDM", 24, 45, 72, 80, 87, 12000));
        b.addPlayerToSquad(createPlayer("Kadhim Waal", "Iraque", "CM", 23, 60, 78, 65, 87, 11000));
        b.addPlayerToSquad(createPlayer("Hadi Ahmed", "Iraque", "CAM", 21, 70, 80, 40, 92, 14000));
        b.addPlayerToSquad(createPlayer("Mufeed Assem", "Iraque", "ST", 25, 78, 65, 30, 84, 11000));
        b.addPlayerToSquad(createPlayer("Natiq Hashim", "Iraque", "ST", 26, 76, 68, 30, 82, 10000));
    }

    private void initializeTelAvivStarsSquad() {
        Club t = findClub("Tel Aviv Stars");
        t.addPlayerToSquad(createPlayer("Itzhak Vissoker", "Israel", "GK", 25, 20, 72, 84, 90, 15000));
        t.addPlayerToSquad(createPlayer("Yosef Weiner", "Israel", "GK", 28, 20, 65, 76, 80, 10000));
        t.addPlayerToSquad(createPlayer("Tzvika Rosen", "Israel", "CB", 23, 45, 78, 85, 93, 18000));
        t.addPlayerToSquad(createPlayer("Menachem Memi Turyem", "Israel", "CB", 27, 35, 70, 82, 86, 13000));
        t.addPlayerToSquad(createPlayer("Shmuel Rosenthal", "Israel", "RB", 24, 60, 80, 75, 90, 16000));
        t.addPlayerToSquad(createPlayer("Arie Radler", "Israel", "LB", 26, 55, 72, 78, 84, 12000));
        t.addPlayerToSquad(createPlayer("Yochanan Vollach", "Israel", "CB", 24, 35, 75, 86, 92, 17000));
        t.addPlayerToSquad(createPlayer("Mordechai Spiegler", "Israel", "CAM", 25, 88, 90, 40, 96, 45000));
        t.addPlayerToSquad(createPlayer("Giora Spiegel", "Israel", "CM", 24, 75, 87, 50, 94, 35000));
        t.addPlayerToSquad(createPlayer("Itzhak Shum", "Israel", "CDM", 21, 45, 76, 75, 88, 14000));
        t.addPlayerToSquad(createPlayer("Rony Kalderon", "Israel", "CAM", 18, 68, 80, 40, 92, 15000));
        t.addPlayerToSquad(createPlayer("Yehoshua Feigenbaum", "Israel", "ST", 23, 80, 70, 30, 88, 25000));
        t.addPlayerToSquad(createPlayer("Shlomo Scharf", "Israel", "ST", 25, 74, 65, 30, 82, 12000));
        t.addPlayerToSquad(createPlayer("David Primo", "Israel", "RW", 27, 72, 75, 35, 84, 11000));
    }

    private Club findClub(String name) {
        return clubs.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    private Player createPlayer(String name, String nationality, String pos, int age, int atk, int pas, int def, int pot, double salary) {
        Map<String, Integer> attrs = new HashMap<>();
        attrs.put("ataque", atk);
        attrs.put("passe", pas);
        attrs.put("defesa", def);
        return new Player(name, nationality, pos, null, age, new TechnicalAttributes(attrs), pot, salary);
    }

    private void fillRemainingSquads() {
        String[] positions = {"GK", "RB", "CB", "CB", "LB", "CDM", "CM", "CM", "CAM", "ST", "ST"};
        for (Club club : clubs) {
            int currentSize = club.getSquad().size();
            for (int i = 0; i < 23 - currentSize; i++) {
                String pos = positions[i % positions.length];
                int base = 45 + random.nextInt(20);
                club.addPlayerToSquad(createPlayer(NameGenerator.generateName(), club.getCountry(), pos, 17 + random.nextInt(18), base + random.nextInt(10), base + random.nextInt(10), base + random.nextInt(10), base + 10 + random.nextInt(15), 500 + random.nextInt(2000)));
            }
        }
    }

    public List<Club> getClubs() { return clubs; }
}
