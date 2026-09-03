package io.github.some_example_name.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Identidade institucional usada na apresentação das franquias. Os valores
 * táticos continuam vindo de {@link Club}; este catálogo reúne apenas dados
 * históricos e administrativos que não existiam na base original.
 */
public final class ClubProfile {

    private static final Map<String, ClubProfile> PROFILES = new LinkedHashMap<>();
    private static final Map<String, RivalryInfo> RIVALRIES = new LinkedHashMap<>();

    static {
        add("Santos Atlântico", "Santos / Atlântico", "santos", "Criatividade e futebol-arte",
            "Futebol-arte como filosofia. Liberdade, posse e talento ofensivo definem uma franquia criada para encantar o mundo.",
            "Chegar aos playoffs", new String[]{"Classificar para os playoffs", "Desenvolver jovens talentos", "Manter saúde financeira"},
            new String[]{"Rio Imperial", "Amsterdã Total"}, 6, 2, 12, 5, 5, 5, 4);
        add("Rio Imperial", "Rio de Janeiro", "rio", "Grandeza, técnica e pressão por títulos",
            "Uma potência brasileira construída para dominar a bola, revelar craques e competir por todas as taças.",
            "Disputar o título da WFL", new String[]{"Chegar à final da conferência", "Manter o núcleo de estrelas", "Valorizar atletas brasileiros"},
            new String[]{"Santos Atlântico", "Buenos Aires Plata"}, 1, 0, 11, 5, 4, 5, 4);
        add("Milano Calcio", "Milano", "milano", "Defesa, disciplina e vitória",
            "Organização sem a bola, leitura de jogo e eficiência. Cada resultado nasce de uma estrutura defensiva de elite.",
            "Disputar o título da WFL", new String[]{"Ter uma das melhores defesas", "Chegar às semifinais", "Preservar o equilíbrio financeiro"},
            new String[]{"Bavaria München", "Amsterdã Total"}, 9, 1, 1, 5, 5, 4, 5);
        add("Bavaria München", "München", "bavaria", "Intensidade e mentalidade vencedora",
            "Pressão, força física e ambição permanente fazem da Bavaria uma candidata natural a qualquer competição.",
            "Ser campeão da WFL", new String[]{"Conquistar a WFL", "Liderar em gols marcados", "Manter elenco de elite"},
            new String[]{"Milano Calcio", "Manchester Albion"}, 1, 0, 4, 5, 5, 5, 5);
        add("Manchester Albion", "Manchester", "manchester", "Força coletiva e ritmo inglês",
            "Uma franquia de jogo vertical, intensidade alta e compromisso coletivo do primeiro ao último minuto.",
            "Disputar o título da WFL", new String[]{"Chegar às semifinais", "Vencer o clássico inglês", "Reforçar o setor criativo"},
            new String[]{"London Royals", "Belfast Northern Stars"}, 7, 0, 3, 5, 5, 4, 4);
        add("London Royals", "London", "london", "Tradição, estrelas e jogo pelos lados",
            "Talento individual e amplitude ofensiva sustentam a identidade de uma das marcas mais tradicionais da liga.",
            "Classificar para os playoffs", new String[]{"Voltar aos playoffs", "Desenvolver pontas", "Equilibrar a folha salarial"},
            new String[]{"Manchester Albion", "Paris Lumière"}, 2, 0, 6, 5, 4, 4, 4);
        add("Amsterdã Total", "Amsterdam", "amsterdam", "Movimento, posse e futebol total",
            "Todos atacam e todos defendem. Mobilidade, inteligência e formação de talentos orientam cada decisão.",
            "Disputar o título da WFL", new String[]{"Chegar à final", "Liderar a liga em posse", "Promover jovens da academia"},
            new String[]{"Santos Atlântico", "Milano Calcio"}, 13, 0, 4, 5, 5, 5, 4);
        add("Madrid Castilla", "Madrid", "madrid", "Talento de elite e ambição continental",
            "Grandes jogadores, protagonismo e pressão por vitórias. Em Madrid, competir não basta: é preciso vencer.",
            "Disputar o título da WFL", new String[]{"Chegar às semifinais", "Vencer o clássico espanhol", "Contratar uma estrela"},
            new String[]{"Barcelona Mediterrâneo", "Lisboa Atlântica"}, 14, 1, 11, 5, 5, 4, 5);
        add("Barcelona Mediterrâneo", "Barcelona", "barcelona", "Posse, técnica e formação",
            "A bola e o espaço são o centro do projeto. O clube aposta em controle, criatividade e atletas formados em casa.",
            "Classificar para os playoffs", new String[]{"Chegar aos playoffs", "Ter posse média superior", "Desenvolver jovens espanhóis"},
            new String[]{"Madrid Castilla", "Paris Lumière"}, 8, 0, 16, 5, 5, 5, 4);
        add("Budapest Danube", "Budapest", "budapest", "Disciplina coletiva e escola técnica",
            "Uma franquia que combina a tradição húngara de criação com organização e movimentos coordenados.",
            "Classificar para os playoffs", new String[]{"Brigar pelos playoffs", "Melhorar a profundidade", "Valorizar a escola húngara"},
            new String[]{"Bavaria München", "Milano Calcio"}, 21, 0, 12, 4, 4, 4, 4);
        add("Lisboa Atlântica", "Lisboa", "lisboa", "Formar, competir e valorizar talentos",
            "Scouting internacional e desenvolvimento técnico transformam jovens promissores em protagonistas.",
            "Chegar aos playoffs", new String[]{"Classificar para os playoffs", "Desenvolver dois jovens", "Manter saldo positivo"},
            new String[]{"Madrid Castilla", "Santos Atlântico"}, 17, 0, 14, 5, 5, 5, 4);
        add("Buenos Aires Plata", "Buenos Aires", "buenosaires", "Talento, coragem e alma competitiva",
            "Técnica sul-americana e intensidade emocional fazem de cada partida uma afirmação de identidade.",
            "Chegar aos playoffs", new String[]{"Vencer o clássico do Prata", "Classificar para os playoffs", "Reforçar a defesa"},
            new String[]{"Montevideo Oriental", "Rio Imperial"}, 14, 0, 5, 5, 4, 5, 4);
        add("Montevideo Oriental", "Montevideo", "montevideo", "Competitividade e resiliência",
            "Marcação firme, inteligência e capacidade de sobreviver aos jogos difíceis são marcas da escola uruguaia.",
            "Brigar pelos playoffs", new String[]{"Terminar na metade superior", "Vencer o clássico do Prata", "Manter defesa competitiva"},
            new String[]{"Buenos Aires Plata", "Santos Atlântico"}, 21, 2, 8, 4, 4, 4, 5);
        add("Paris Lumière", "Paris", "paris", "Estrelas globais e expressão ofensiva",
            "Uma vitrine internacional que combina espetáculo, velocidade e ambição para ocupar o centro do futebol mundial.",
            "Classificar para os playoffs", new String[]{"Chegar aos playoffs", "Aumentar a reputação mundial", "Equilibrar estrelas e elenco"},
            new String[]{"London Royals", "Barcelona Mediterrâneo"}, 5, 0, 2, 5, 4, 4, 4);
        add("Belfast Northern Stars", "Belfast", "belfast", "Jogo direto, físico e leal",
            "Bolas aéreas, entrega e força coletiva mantêm a franquia competitiva diante de adversários mais técnicos.",
            "Terminar na metade superior", new String[]{"Alcançar a metade superior", "Melhorar a criação", "Fortalecer o elenco"},
            new String[]{"Manchester Albion", "London Royals"}, 24, 0, 18, 4, 3, 3, 4);
        add("Tokyo Rising Sun", "Tokyo", "tokyo", "A próxima geração começa aqui",
            "Tecnologia, scouting e desenvolvimento de jovens sustentam um projeto paciente com enorme potencial futuro.",
            "Desenvolver o projeto jovem", new String[]{"Dar minutos aos jovens", "Melhorar a campanha", "Concluir cinco relatórios de scout"},
            new String[]{"Seoul Tigers", "Tel Aviv Stars"}, 4, 0, 2, 4, 5, 5, 4);
        add("Seoul Tigers", "Seoul", "seoul", "Intensidade, velocidade e evolução",
            "Pressão agressiva e preparação física transformam um grupo jovem em uma equipe difícil de acompanhar.",
            "Terminar na metade superior", new String[]{"Alcançar a metade superior", "Ser referência em intensidade", "Desenvolver titulares jovens"},
            new String[]{"Tokyo Rising Sun", "Tehran Lions"}, 3, 0, 2, 4, 4, 5, 4);
        add("Tehran Lions", "Tehran", "tehran", "Eficiência, disciplina e reconstrução",
            "Um projeto pragmático que busca competitividade por meio de defesa organizada e decisões inteligentes no mercado.",
            "Reconstruir o elenco", new String[]{"Sair das últimas posições", "Reduzir gols sofridos", "Adquirir escolhas de Draft"},
            new String[]{"Baghdad Mesopotamia", "Tel Aviv Stars"}, 2, 0, 5, 3, 3, 3, 4);
        add("Baghdad Mesopotamia", "Baghdad", "baghdad", "Identidade local e organização",
            "Coesão, jogo coletivo e responsabilidade financeira formam a base para crescer de maneira sustentável.",
            "Reconstruir o elenco", new String[]{"Desenvolver o elenco", "Melhorar a infraestrutura", "Preservar espaço no cap"},
            new String[]{"Tehran Lions", "Tel Aviv Stars"}, 2, 0, 3, 3, 3, 3, 3);
        add("Tel Aviv Stars", "Tel Aviv", "telaviv", "Equilíbrio competitivo e inteligência",
            "Uma franquia adaptável que procura neutralizar o adversário e explorar com precisão cada oportunidade.",
            "Terminar na metade superior", new String[]{"Disputar vaga nos playoffs", "Melhorar o ataque", "Manter estabilidade financeira"},
            new String[]{"Tehran Lions", "Baghdad Mesopotamia"}, 10, 0, 10, 4, 4, 4, 4);

        // Rivalidades históricas e esportivas da WFL. O cadastro explícito
        // impede que a intensidade dependa apenas da ordem visual da lista.
        rivalry("Santos Atlântico", "Rio Imperial", 5, "CLÁSSICO BRASILEIRO");
        rivalry("Santos Atlântico", "Amsterdã Total", 4, "DUELO MUNDIAL HISTÓRICO");
        rivalry("Santos Atlântico", "Lisboa Atlântica", 4, "CLÁSSICO LUSÓFONO");
        rivalry("Santos Atlântico", "Montevideo Oriental", 3, "DUELO SUL-AMERICANO");
        rivalry("Rio Imperial", "Buenos Aires Plata", 4, "CLÁSSICO SUL-AMERICANO");

        rivalry("Milano Calcio", "Bavaria München", 5, "CLÁSSICO EUROPEU");
        rivalry("Milano Calcio", "Amsterdã Total", 4, "DUELO DE ESCOLAS");
        rivalry("Milano Calcio", "Budapest Danube", 3, "CONFRONTO CONTINENTAL");
        rivalry("Bavaria München", "Manchester Albion", 4, "DUELO DE POTÊNCIAS");
        rivalry("Bavaria München", "Budapest Danube", 3, "CLÁSSICO CENTRO-EUROPEU");
        rivalry("Manchester Albion", "London Royals", 5, "CLÁSSICO INGLÊS");
        rivalry("Manchester Albion", "Belfast Northern Stars", 4, "DERBY BRITÂNICO");
        rivalry("London Royals", "Paris Lumière", 4, "CLÁSSICO INTERNACIONAL");
        rivalry("London Royals", "Belfast Northern Stars", 3, "DUELO BRITÂNICO");

        rivalry("Madrid Castilla", "Barcelona Mediterrâneo", 5, "EL CLÁSICO DA WFL");
        rivalry("Madrid Castilla", "Lisboa Atlântica", 4, "CLÁSSICO IBÉRICO");
        rivalry("Barcelona Mediterrâneo", "Paris Lumière", 4, "RIVALIDADE TÉCNICA");
        rivalry("Buenos Aires Plata", "Montevideo Oriental", 5, "CLÁSSICO DO PRATA");

        rivalry("Tokyo Rising Sun", "Seoul Tigers", 5, "CLÁSSICO DO ORIENTE");
        rivalry("Tokyo Rising Sun", "Tel Aviv Stars", 3, "DUELO INTERCONTINENTAL");
        rivalry("Seoul Tigers", "Tehran Lions", 3, "DUELO ASIÁTICO");
        rivalry("Tehran Lions", "Baghdad Mesopotamia", 5, "CLÁSSICO REGIONAL");
        rivalry("Tehran Lions", "Tel Aviv Stars", 5, "RIVALIDADE HISTÓRICA");
        rivalry("Baghdad Mesopotamia", "Tel Aviv Stars", 4, "CLÁSSICO REGIONAL");
        rivalry("Paris Lumière", "Marseille Méditerranée", 5, "CLÁSSICO FRANCÊS");
        for (int year : new int[]{1974, 1978, 1982, 1986, 1990}) {
            for (LeagueExpansionService.Franchise franchise : LeagueExpansionService.forYear(year)) {
                PROFILES.put(franchise.name, new ClubProfile(franchise));
            }
        }
    }

    public final String city;
    public final int founded;
    public final String uniformKey;
    public final String tagline;
    public final String identity;
    public final String boardObjective;
    public final String[] expectations;
    public final String[] rivals;
    public final int wflTitles;
    public final int worldCups;
    public final int regionalTitles;
    public final int stadiumRating;
    public final int trainingRating;
    public final int academyRating;
    public final int medicalRating;

    private ClubProfile(LeagueExpansionService.Franchise f) {
        city = f.city; founded = f.year;
        uniformKey = f.logo.replace(".png", "");
        tagline = f.identity;
        identity = f.identity + ". Franquia de expansão de " + f.year + ". Cores: " + f.colors + ".";
        boardObjective = "Construir uma temporada competitiva";
        expectations = new String[]{"Consolidar o elenco", "Desenvolver jovens", "Manter equilíbrio financeiro"};
        rivals = f.name.equals("Marseille Méditerranée") ? new String[]{"Paris Lumière"} : new String[0];
        wflTitles = 0; worldCups = 0; regionalTitles = 0;
        stadiumRating = f.condition >= 90 ? 5 : 4;
        trainingRating = f.balance >= 70_000_000L ? 5 : f.reputation >= 80 ? 4 : 3;
        academyRating = f.name.equals("Bangkok Elephants") ? 4 : 3;
        medicalRating = f.balance >= 70_000_000L ? 5 : 3;
    }

    private ClubProfile(String city, String uniformKey, String tagline, String identity,
                        String boardObjective, String[] expectations, String[] rivals,
                        int wflTitles, int worldCups, int regionalTitles,
                        int stadiumRating, int trainingRating, int academyRating, int medicalRating) {
        this.city = city;
        this.founded = 1969;
        this.uniformKey = uniformKey;
        this.tagline = tagline;
        this.identity = identity;
        this.boardObjective = boardObjective;
        this.expectations = expectations;
        this.rivals = rivals;
        this.wflTitles = wflTitles;
        this.worldCups = worldCups;
        this.regionalTitles = regionalTitles;
        this.stadiumRating = stadiumRating;
        this.trainingRating = trainingRating;
        this.academyRating = academyRating;
        this.medicalRating = medicalRating;
    }

    private static void add(String name, String city, String uniformKey, String tagline, String identity,
                            String objective, String[] expectations, String[] rivals,
                            int wfl, int world, int regional, int stadium, int training, int academy, int medical) {
        PROFILES.put(name, new ClubProfile(city, uniformKey, tagline, identity, objective,
            expectations, rivals, wfl, world, regional, stadium, training, academy, medical));
    }

    private static void rivalry(String first, String second, int level, String label) {
        RIVALRIES.put(rivalryKey(first, second), new RivalryInfo(Math.max(1, Math.min(5, level)), label));
    }

    private static String rivalryKey(String first, String second) {
        String a = first == null ? "" : first.trim().toLowerCase();
        String b = second == null ? "" : second.trim().toLowerCase();
        return a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
    }

    public static ClubProfile forClub(Club club) {
        ClubProfile profile = club == null ? null : PROFILES.get(club.getName());
        return profile != null ? profile : new ClubProfile(
            club == null ? "WFL" : club.getCountry(), "club", "Uma nova história começa aqui",
            "Franquia da Liga Mundial preparada para construir sua própria identidade.",
            "Construir uma temporada competitiva", new String[]{"Melhorar o elenco", "Competir com regularidade", "Manter equilíbrio financeiro"},
            new String[]{"A definir", "A definir"}, 0, 0, 0, 3, 3, 3, 3
        );
    }

    public static Map<String, ClubProfile> all() {
        return Collections.unmodifiableMap(PROFILES);
    }

    /** Intensidade histórica bilateral usada em todas as telas da WFL. */
    public static int rivalryLevel(Club first, Club second) {
        if (first == null || second == null || first == second) return 0;
        RivalryInfo registered = RIVALRIES.get(rivalryKey(first.getName(), second.getName()));
        if (registered != null) return registered.level;
        return Math.max(oneWayRivalryLevel(first, second), oneWayRivalryLevel(second, first));
    }

    public static String rivalryLabel(Club first, Club second) {
        if (first == null || second == null) return "RIVALIDADE";
        RivalryInfo registered = RIVALRIES.get(rivalryKey(first.getName(), second.getName()));
        if (registered != null) return registered.label;
        int level = rivalryLevel(first, second);
        if (level >= 5) return "RIVALIDADE HISTÓRICA";
        if (level == 4) return "CLÁSSICO INTERNACIONAL";
        if (level == 3) return "RIVALIDADE FORTE";
        if (level == 2) return "RIVALIDADE REGIONAL";
        return "RIVALIDADE EM FORMAÇÃO";
    }

    private static int oneWayRivalryLevel(Club source, Club target) {
        ClubProfile profile = PROFILES.get(source.getName());
        if (profile == null || profile.rivals == null) return 0;
        for (int index = 0; index < profile.rivals.length; index++) {
            if (profile.rivals[index] != null && profile.rivals[index].equalsIgnoreCase(target.getName())) {
                return Math.max(1, 5 - index);
            }
        }
        return 0;
    }

    private static final class RivalryInfo {
        final int level;
        final String label;

        RivalryInfo(int level, String label) {
            this.level = level;
            this.label = label == null ? "RIVALIDADE" : label;
        }
    }
}
