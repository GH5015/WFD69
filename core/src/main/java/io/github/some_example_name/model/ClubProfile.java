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

    static {
        add("Santos Atlântico", "Santos / Atlântico", "santos", "Criatividade e futebol-arte",
            "Futebol-arte como filosofia. Liberdade, posse e talento ofensivo definem uma franquia criada para encantar o mundo.",
            "Chegar aos playoffs", new String[]{"Classificar para os playoffs", "Desenvolver jovens talentos", "Manter saúde financeira"},
            new String[]{"Rio Imperial", "Amsterdã Total"}, 3, 2, 12, 5, 5, 5, 4);
        add("Rio Imperial", "Rio de Janeiro", "rio", "Grandeza, técnica e pressão por títulos",
            "Uma potência brasileira construída para dominar a bola, revelar craques e competir por todas as taças.",
            "Disputar o título da WFL", new String[]{"Chegar à final da conferência", "Manter o núcleo de estrelas", "Valorizar atletas brasileiros"},
            new String[]{"Santos Atlântico", "Buenos Aires Plata"}, 4, 2, 11, 5, 4, 5, 4);
        add("Milano Calcio", "Milano", "milano", "Defesa, disciplina e vitória",
            "Organização sem a bola, leitura de jogo e eficiência. Cada resultado nasce de uma estrutura defensiva de elite.",
            "Disputar o título da WFL", new String[]{"Ter uma das melhores defesas", "Chegar às semifinais", "Preservar o equilíbrio financeiro"},
            new String[]{"Bavaria München", "Madrid Castilla"}, 5, 3, 14, 5, 5, 4, 5);
        add("Bavaria München", "München", "bavaria", "Intensidade e mentalidade vencedora",
            "Pressão, força física e ambição permanente fazem da Bavaria uma candidata natural a qualquer competição.",
            "Ser campeão da WFL", new String[]{"Conquistar a WFL", "Liderar em gols marcados", "Manter elenco de elite"},
            new String[]{"Milano Calcio", "Manchester Albion"}, 6, 3, 15, 5, 5, 5, 5);
        add("Manchester Albion", "Manchester", "manchester", "Força coletiva e ritmo inglês",
            "Uma franquia de jogo vertical, intensidade alta e compromisso coletivo do primeiro ao último minuto.",
            "Disputar o título da WFL", new String[]{"Chegar às semifinais", "Vencer o clássico inglês", "Reforçar o setor criativo"},
            new String[]{"London Royals", "Belfast Northern Stars"}, 4, 2, 13, 5, 5, 4, 4);
        add("London Royals", "London", "london", "Tradição, estrelas e jogo pelos lados",
            "Talento individual e amplitude ofensiva sustentam a identidade de uma das marcas mais tradicionais da liga.",
            "Classificar para os playoffs", new String[]{"Voltar aos playoffs", "Desenvolver pontas", "Equilibrar a folha salarial"},
            new String[]{"Manchester Albion", "Paris Lumière"}, 3, 2, 10, 5, 4, 4, 4);
        add("Amsterdã Total", "Amsterdam", "amsterdam", "Movimento, posse e futebol total",
            "Todos atacam e todos defendem. Mobilidade, inteligência e formação de talentos orientam cada decisão.",
            "Disputar o título da WFL", new String[]{"Chegar à final", "Liderar a liga em posse", "Promover jovens da academia"},
            new String[]{"Santos Atlântico", "Milano Calcio"}, 5, 3, 16, 5, 5, 5, 4);
        add("Madrid Castilla", "Madrid", "madrid", "Talento de elite e ambição continental",
            "Grandes jogadores, protagonismo e pressão por vitórias. Em Madrid, competir não basta: é preciso vencer.",
            "Disputar o título da WFL", new String[]{"Chegar às semifinais", "Vencer o clássico espanhol", "Contratar uma estrela"},
            new String[]{"Barcelona Mediterrâneo", "Lisboa Atlântica"}, 6, 4, 17, 5, 5, 4, 5);
        add("Barcelona Mediterrâneo", "Barcelona", "barcelona", "Posse, técnica e formação",
            "A bola e o espaço são o centro do projeto. O clube aposta em controle, criatividade e atletas formados em casa.",
            "Classificar para os playoffs", new String[]{"Chegar aos playoffs", "Ter posse média superior", "Desenvolver jovens espanhóis"},
            new String[]{"Madrid Castilla", "Paris Lumière"}, 4, 2, 14, 5, 5, 5, 4);
        add("Budapest Danube", "Budapest", "budapest", "Disciplina coletiva e escola técnica",
            "Uma franquia que combina a tradição húngara de criação com organização e movimentos coordenados.",
            "Classificar para os playoffs", new String[]{"Brigar pelos playoffs", "Melhorar a profundidade", "Valorizar a escola húngara"},
            new String[]{"Bavaria München", "Milano Calcio"}, 2, 2, 9, 4, 4, 4, 4);
        add("Lisboa Atlântica", "Lisboa", "lisboa", "Formar, competir e valorizar talentos",
            "Scouting internacional e desenvolvimento técnico transformam jovens promissores em protagonistas.",
            "Chegar aos playoffs", new String[]{"Classificar para os playoffs", "Desenvolver dois jovens", "Manter saldo positivo"},
            new String[]{"Madrid Castilla", "Barcelona Mediterrâneo"}, 3, 2, 12, 5, 5, 5, 4);
        add("Buenos Aires Plata", "Buenos Aires", "buenosaires", "Talento, coragem e alma competitiva",
            "Técnica sul-americana e intensidade emocional fazem de cada partida uma afirmação de identidade.",
            "Chegar aos playoffs", new String[]{"Vencer o clássico do Prata", "Classificar para os playoffs", "Reforçar a defesa"},
            new String[]{"Montevideo Oriental", "Rio Imperial"}, 4, 3, 15, 5, 4, 5, 4);
        add("Montevideo Oriental", "Montevideo", "montevideo", "Competitividade e resiliência",
            "Marcação firme, inteligência e capacidade de sobreviver aos jogos difíceis são marcas da escola uruguaia.",
            "Brigar pelos playoffs", new String[]{"Terminar na metade superior", "Vencer o clássico do Prata", "Manter defesa competitiva"},
            new String[]{"Buenos Aires Plata", "Santos Atlântico"}, 3, 3, 13, 4, 4, 4, 5);
        add("Paris Lumière", "Paris", "paris", "Estrelas globais e expressão ofensiva",
            "Uma vitrine internacional que combina espetáculo, velocidade e ambição para ocupar o centro do futebol mundial.",
            "Classificar para os playoffs", new String[]{"Chegar aos playoffs", "Aumentar a reputação mundial", "Equilibrar estrelas e elenco"},
            new String[]{"London Royals", "Barcelona Mediterrâneo"}, 3, 2, 11, 5, 4, 4, 4);
        add("Belfast Northern Stars", "Belfast", "belfast", "Jogo direto, físico e leal",
            "Bolas aéreas, entrega e força coletiva mantêm a franquia competitiva diante de adversários mais técnicos.",
            "Terminar na metade superior", new String[]{"Alcançar a metade superior", "Melhorar a criação", "Fortalecer o elenco"},
            new String[]{"Manchester Albion", "London Royals"}, 1, 1, 7, 4, 3, 3, 4);
        add("Tokyo Rising Sun", "Tokyo", "tokyo", "A próxima geração começa aqui",
            "Tecnologia, scouting e desenvolvimento de jovens sustentam um projeto paciente com enorme potencial futuro.",
            "Desenvolver o projeto jovem", new String[]{"Dar minutos aos jovens", "Melhorar a campanha", "Concluir cinco relatórios de scout"},
            new String[]{"Seoul Tigers", "Tel Aviv Stars"}, 1, 1, 8, 4, 5, 5, 4);
        add("Seoul Tigers", "Seoul", "seoul", "Intensidade, velocidade e evolução",
            "Pressão agressiva e preparação física transformam um grupo jovem em uma equipe difícil de acompanhar.",
            "Terminar na metade superior", new String[]{"Alcançar a metade superior", "Ser referência em intensidade", "Desenvolver titulares jovens"},
            new String[]{"Tokyo Rising Sun", "Tehran Lions"}, 1, 1, 7, 4, 4, 5, 4);
        add("Tehran Lions", "Tehran", "tehran", "Eficiência, disciplina e reconstrução",
            "Um projeto pragmático que busca competitividade por meio de defesa organizada e decisões inteligentes no mercado.",
            "Reconstruir o elenco", new String[]{"Sair das últimas posições", "Reduzir gols sofridos", "Adquirir escolhas de Draft"},
            new String[]{"Baghdad Mesopotamia", "Tel Aviv Stars"}, 1, 1, 6, 3, 3, 3, 4);
        add("Baghdad Mesopotamia", "Baghdad", "baghdad", "Identidade local e organização",
            "Coesão, jogo coletivo e responsabilidade financeira formam a base para crescer de maneira sustentável.",
            "Reconstruir o elenco", new String[]{"Desenvolver o elenco", "Melhorar a infraestrutura", "Preservar espaço no cap"},
            new String[]{"Tehran Lions", "Tel Aviv Stars"}, 1, 1, 5, 3, 3, 3, 3);
        add("Tel Aviv Stars", "Tel Aviv", "telaviv", "Equilíbrio competitivo e inteligência",
            "Uma franquia adaptável que procura neutralizar o adversário e explorar com precisão cada oportunidade.",
            "Terminar na metade superior", new String[]{"Disputar vaga nos playoffs", "Melhorar o ataque", "Manter estabilidade financeira"},
            new String[]{"Tehran Lions", "Seoul Tigers"}, 2, 1, 8, 4, 4, 4, 4);
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
}
