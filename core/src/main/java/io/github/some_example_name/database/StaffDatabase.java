package io.github.some_example_name.database;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.model.StaffSalaryScale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Base internacional de profissionais disponíveis no mercado anual da WFL. */
public final class StaffDatabase {
    private static final Map<StaffRole, List<Profile>> MARKET = new EnumMap<>(StaffRole.class);

    static {
        add(StaffRole.COACH,
            p("Jorge Vasconcelos", "Portugal", "Posse e circulação", 94, 3),
            p("Arrigo Bellini", "Itália", "Organização defensiva", 92, 3),
            p("Ernst Weber", "Alemanha", "Pressão e intensidade", 90, 3),
            p("Marcel Fontaine", "França", "Transições ofensivas", 88, 2),
            p("Alberto Ferreira", "Brasil", "Gestão de estrelas", 86, 2),
            p("Rinus van Daal", "Holanda", "Futebol total", 85, 3),
            p("Héctor Villalba", "Argentina", "Ataque vertical", 83, 2),
            p("Masato Nakamura", "Japão", "Desenvolvimento coletivo", 81, 2),
            p("Rui Monteiro", "Portugal", "Equilíbrio tático", 78, 2),
            p("Kemal Demir", "Turquia", "Bloco defensivo", 75, 2),
            p("Samuel Okoro", "Nigéria", "Intensidade física", 71, 2),
            p("Nabil Haddad", "Marrocos", "Contra-ataque", 66, 1));

        add(StaffRole.SCOUT,
            p("Johan de Vries", "Holanda", "Jovens europeus", 96, 3),
            p("Antonio Rossi", "Itália", "Defensores", 92, 3),
            p("Hans Becker", "Alemanha", "Mercado central europeu", 89, 3),
            p("Sergio Duarte", "Brasil", "Talento sul-americano", 87, 2),
            p("Pierre Martin", "França", "Atletas africanos", 85, 2),
            p("Yuto Kobayashi", "Japão", "Mercado asiático", 84, 3),
            p("Diego Salazar", "Uruguai", "Meias criativos", 82, 2),
            p("George Thompson", "Inglaterra", "Atletas físicos", 79, 2),
            p("Karim Mansour", "Egito", "Mercado árabe", 77, 2),
            p("Milan Horváth", "Hungria", "Prospectos sub-20", 74, 2),
            p("Elias Papadopoulos", "Grécia", "Jogadores livres", 70, 1),
            p("Omar Al-Khatib", "Jordânia", "Mercados alternativos", 65, 1));

        add(StaffRole.DEVELOPMENT_DIRECTOR,
            p("Kenji Watanabe", "Japão", "Evolução técnica", 96, 3),
            p("Sergio Valdés", "Espanha", "Academia de base", 93, 3),
            p("Willem Jansen", "Holanda", "Inteligência tática", 91, 3),
            p("Paulo Amaral", "Brasil", "Atacantes jovens", 88, 3),
            p("Lorenzo Marchetti", "Itália", "Defensores jovens", 86, 2),
            p("François Leclerc", "França", "Planos individuais", 84, 2),
            p("Lee Joon-ho", "Coreia do Sul", "Evolução física", 82, 2),
            p("Alejandro Quiroga", "Argentina", "Técnica individual", 80, 2),
            p("Bence Szabó", "Hungria", "Meio-campistas", 77, 2),
            p("David Cohen", "Israel", "Transição ao profissional", 74, 2),
            p("Reza Mahdavi", "Irã", "Formação mental", 70, 1),
            p("Tariq Nasser", "Iraque", "Fundamentos", 66, 1));

        add(StaffRole.FITNESS_COACH,
            p("Karl Hoffmann", "Alemanha", "Prevenção de fadiga", 96, 3),
            p("Miguel Santos", "Portugal", "Recuperação pós-jogo", 93, 3),
            p("Alberto Lima", "Brasil", "Condicionamento", 90, 3),
            p("Marco De Luca", "Itália", "Resistência", 88, 2),
            p("James Cooper", "Inglaterra", "Alta intensidade", 86, 2),
            p("Sven de Jong", "Holanda", "Periodização", 84, 3),
            p("Park Sung-ho", "Coreia do Sul", "Explosão muscular", 82, 2),
            p("Nicolás Pereyra", "Uruguai", "Recuperação ativa", 79, 2),
            p("Farid Azizi", "Irã", "Resistência aeróbica", 76, 2),
            p("Luc Moreau", "França", "Controle de carga", 73, 2),
            p("Akira Fujimoto", "Japão", "Mobilidade", 70, 1),
            p("Adnan Karim", "Iraque", "Preparação geral", 65, 1));

        add(StaffRole.DOCTOR,
            p("Dra. Elena Costa", "Espanha", "Medicina esportiva", 97, 3),
            p("Dr. Ricardo Mello", "Brasil", "Lesões musculares", 94, 3),
            p("Dra. Sofia Laurent", "França", "Prevenção de recaídas", 92, 3),
            p("Dr. Klaus Richter", "Alemanha", "Joelho e ligamentos", 89, 3),
            p("Dr. Matteo Ferraro", "Itália", "Traumatologia", 87, 2),
            p("Dra. Hannah Wilson", "Inglaterra", "Reabilitação", 85, 2),
            p("Dr. Koji Yamamoto", "Japão", "Recuperação acelerada", 83, 2),
            p("Dra. Inés Cabrera", "Argentina", "Fisioterapia", 80, 2),
            p("Dr. Min-woo Kang", "Coreia do Sul", "Controle de carga", 78, 2),
            p("Dr. Arman Daryan", "Irã", "Diagnóstico clínico", 74, 2),
            p("Dra. Yael Shamir", "Israel", "Prevenção", 71, 1),
            p("Dr. Samir Rahman", "Iraque", "Medicina geral", 66, 1));
        expandInternationalMarket();
    }

    private StaffDatabase() { }

    /** Forty additional, stable fictional identities per role; no yearly name recycling.
     * Even with 30 employers, each role retains more than ten free candidates. */
    private static void expandInternationalMarket() {
        String[][] regions = {
            {"Brasil", "Augusto", "Davi", "Caio", "Murilo", "Renato", "Peixoto", "Barreto", "Figueira", "Seixas", "Torquato"},
            {"Argentina", "Esteban", "Ramiro", "Ignacio", "Leandro", "Facundo", "Funes", "Peralta", "Arce", "Correa", "Medina"},
            {"Itália", "Cesare", "Enrico", "Fabrizio", "Giorgio", "Vittorio", "Moretti", "Rinaldi", "Galli", "Santoro", "Lombardi"},
            {"Alemanha", "Wolfgang", "Bertram", "Jochen", "Manfred", "Ulrich", "Bergmann", "Seidel", "Hartmann", "Brandt", "Voss"},
            {"França", "Étienne", "Rémi", "Olivier", "Pascal", "Thierry", "Renaud", "Perrin", "Garnier", "Chevalier", "Delorme"},
            {"Inglaterra", "Graham", "Colin", "Martin", "Trevor", "Philip", "Hawthorne", "Mercer", "Baxter", "Whitaker", "Bradley"},
            {"Japão", "Hiroshi", "Takumi", "Satoshi", "Naoki", "Takeshi", "Ishikawa", "Matsuda", "Hasegawa", "Ogawa", "Mori"},
            {"Egito", "Hassan", "Youssef", "Khaled", "Mahmoud", "Tamer", "Farouk", "Saleh", "Hamdi", "Fahmy", "Sabri"}
        };
        String[][] specialties = {
            {"Posse e circulação", "Organização defensiva", "Transições ofensivas", "Gestão de elenco", "Pressão e intensidade"},
            {"Jovens promessas", "Defensores", "Atacantes", "Mercados internacionais", "Análise de potencial"},
            {"Evolução técnica", "Academia de base", "Planos individuais", "Inteligência tática", "Formação de jovens"},
            {"Resistência", "Recuperação ativa", "Controle de carga", "Mobilidade", "Condicionamento"},
            {"Medicina esportiva", "Lesões musculares", "Prevenção de recaídas", "Reabilitação", "Diagnóstico clínico"}
        };
        for (StaffRole role : StaffRole.values()) {
            List<Profile> expanded = new ArrayList<>(MARKET.get(role));
            int r = role == StaffRole.COACH ? 0 : role == StaffRole.SCOUT ? 1
                : role == StaffRole.DEVELOPMENT_DIRECTOR ? 2 : role == StaffRole.FITNESS_COACH ? 3 : 4;
            for (int country = 0; country < regions.length; country++) {
                String[] region = regions[country];
                for (int family = 0; family < 5; family++) {
                    String name = (role == StaffRole.DOCTOR ? "Dr. " : "") + region[1 + r] + " " + region[6 + family];
                    int quality = 54 + Math.floorMod(country * 13 + family * 9 + r * 7, 44);
                    expanded.add(p(name, region[0], specialties[r][family], quality, 1 + (country + family + r) % 3));
                }
            }
            MARKET.put(role, Collections.unmodifiableList(expanded));
        }
    }

    public static List<StaffMember> getOffseasonCandidates(StaffRole role, int year,
                                                            Collection<Club> clubs, Club requestingClub,
                                                            int limit) {
        List<Profile> profiles = new ArrayList<>(MARKET.getOrDefault(role, Collections.<Profile>emptyList()));
        Set<String> employed = new HashSet<>();
        if (clubs != null) for (Club club : clubs) for (StaffRole staffRole : StaffRole.values()) {
            StaffMember member = club.getStaffMember(staffRole);
            if (member != null) employed.add(member.getName());
        }
        final String seed = year + ":" + (requestingClub != null ? requestingClub.getName() : "WFL");
        profiles.removeIf(profile -> employed.contains(profile.name));
        profiles.sort(Comparator.comparingInt(profile -> stableOrder(profile.name, seed)));

        List<StaffMember> result = new ArrayList<>();
        for (Profile profile : profiles) {
            if (result.size() >= Math.max(1, limit)) break;
            int qualityVariation = Math.floorMod((profile.name + year).hashCode(), 5) - 2;
            int quality = Math.max(50, Math.min(99, profile.quality + qualityVariation));
            long salary = salaryFor(quality, role);
            result.add(new StaffMember(role, profile.name, quality, salary, year + profile.contractYears,
                profile.nationality, profile.specialty));
        }
        return result;
    }

    public static StaffMember getAutomaticReplacement(StaffRole role, String clubName, int year) {
        List<Profile> profiles = MARKET.get(role);
        if (profiles == null || profiles.isEmpty()) {
            return new StaffMember(role, "Interino " + role.getLabel(), 62, salaryFor(62, role), year + 1);
        }
        int index = Math.floorMod((clubName + role.name() + year).hashCode(), profiles.size());
        Profile profile = profiles.get(index);
        int quality = Math.min(72, Math.max(60, profile.quality - 18));
        return new StaffMember(role, profile.name, quality, salaryFor(quality, role), year + 2,
            profile.nationality, profile.specialty);
    }

    public static int getMarketSize() {
        int total = 0;
        for (List<Profile> profiles : MARKET.values()) total += profiles.size();
        return total;
    }

    private static long salaryFor(int quality, StaffRole role) {
        return StaffSalaryScale.annualSalary(role, quality);
    }

    private static int stableOrder(String name, String seed) {
        return Math.floorMod((name + ":" + seed).hashCode(), Integer.MAX_VALUE);
    }

    private static void add(StaffRole role, Profile... profiles) {
        List<Profile> list = new ArrayList<>();
        Collections.addAll(list, profiles);
        MARKET.put(role, Collections.unmodifiableList(list));
    }

    private static Profile p(String name, String nationality, String specialty, int quality, int years) {
        return new Profile(name, nationality, specialty, quality, years);
    }

    private static final class Profile {
        private final String name;
        private final String nationality;
        private final String specialty;
        private final int quality;
        private final int contractYears;
        private Profile(String name, String nationality, String specialty, int quality, int contractYears) {
            this.name = name;
            this.nationality = nationality;
            this.specialty = specialty;
            this.quality = quality;
            this.contractYears = contractYears;
        }
    }
}
