package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import io.github.some_example_name.model.DraftPick;
import io.github.some_example_name.database.StaffDatabase;

public class Club {
    private String name;
    private String nickname = "";
    private String country;
    private String confederation;
    private int reputation;
    private double budget;
    private String stadiumName;
    private int stadiumCapacity = 30000;
    private int stadiumCondition = 100;
    private int averageTicketPrice;
    private String stadiumRenovationName;
    private int stadiumRenovationTargetCapacity;
    private int stadiumRenovationTotalDays;
    private int stadiumRenovationDaysRemaining;
    private long stadiumRenovationCost;
    private int stadiumRenovationTemporaryCapacity;
    private String philosophy = "Desenvolver Jovens";
    private String logoPath;
    private boolean userControlled = false;
    private int winStreak = 0;
    private int lossStreak = 0;
    private int boardObjectiveSeason = -1;
    private int boardConfidence = 50;
    private int previousBoardConfidence = 50;
    private Set<String> shownBoardReviews = new HashSet<>();
    private int finalBoardEvaluationSeason = -1;
    private int finalBoardPreviousConfidence = 50;
    private int finalBoardScore = 50;
    private boolean finalBoardDismissed;

    private List<Player> squad;
    private List<Player> startingXI;
    private List<Player> selectedBench = new ArrayList<>();
    public static final int BENCH_SIZE = 7;

    /** Persistent pre-match bench; replace only slots invalidated by lineup/availability changes. */
    public List<Player> getBenchPlayers() {
        if (selectedBench == null) selectedBench = new ArrayList<>();
        List<Player> starters = new ArrayList<>(tacticsMap.values());
        if (starters.isEmpty()) starters.addAll(startingXI);
        Set<Player> seen = new HashSet<>();
        selectedBench.removeIf(p -> p == null || !squad.contains(p) || starters.contains(p) || !p.canPlay() || !seen.add(p));
        while (selectedBench.size() > BENCH_SIZE) selectedBench.remove(selectedBench.size() - 1);
        List<Player> available = new ArrayList<>(squad);
        available.sort((a, b) -> Integer.compare(b.getOverall(), a.getOverall()));
        for (Player player : available) {
            if (selectedBench.size() >= BENCH_SIZE) break;
            if (!starters.contains(player) && player.canPlay() && !selectedBench.contains(player)) selectedBench.add(player);
        }
        return new ArrayList<>(selectedBench);
    }

    /** Accept either click order, but only one bench player and one eligible unselected player. */
    public boolean swapBenchPlayers(Player first, Player second) {
        if (first == null || second == null || first == second) return false;
        List<Player> bench = getBenchPlayers();
        if (bench.contains(first) == bench.contains(second)) return false;
        Player outgoing = bench.contains(first) ? first : second;
        Player incoming = outgoing == first ? second : first;
        if (!squad.contains(incoming) || !incoming.canPlay() || tacticsMap.containsValue(incoming)
            || (tacticsMap.isEmpty() && startingXI.contains(incoming))) return false;
        selectedBench.set(selectedBench.indexOf(outgoing), incoming);
        return true;
    }
    private List<DraftPick> draftPicks = new ArrayList<>();

    /** Apenas escolhas táticas: não copia condição física, moral nem estatísticas. */
    public static final class TacticalSetup {
        private Formation formation;
        private List<Player> starters, bench;
        private Map<Integer, Player> slots;
        private String mentality;
        private float mentalityValue, tempo, passing, width, pressure;
        public TacticalSetup() { }
    }

    public TacticalSetup captureTacticalSetup() {
        TacticalSetup setup = new TacticalSetup();
        setup.formation = formation;
        setup.starters = new ArrayList<>(startingXI);
        setup.slots = new HashMap<>(tacticsMap);
        setup.bench = new ArrayList<>(getBenchPlayers());
        setup.mentality = mentality;
        setup.mentalityValue = getMentalityValue();
        setup.tempo = tempo; setup.passing = passing;
        setup.width = width; setup.pressure = pressure;
        return setup;
    }

    public void restoreTacticalSetup(TacticalSetup setup) {
        if (setup == null) return;
        formation = setup.formation;
        startingXI = new ArrayList<>(setup.starters);
        tacticsMap = new HashMap<>(setup.slots);
        selectedBench = new ArrayList<>(setup.bench);
        setMentalityValue(setup.mentalityValue);
        mentality = setup.mentality;
        tempo = setup.tempo; passing = setup.passing;
        width = setup.width; pressure = setup.pressure;
        removeUnavailablePlayersFromStartingXI();
    }
    public List<DraftPick> getDraftPicks() {
        return draftPicks;
    }

    public void setDraftPicks(List<DraftPick> draftPicks) {
        this.draftPicks = draftPicks;
    }

    // Táticas e Formação
    private Formation formation = null;
    private Map<Integer, Player> tacticsMap;

    // Atributos Táticos
    private String mentality = "Equilibrada";
    private float mentalityValue = 50f;
    private float tempo = 50f;
    private float passing = 50f;
    private float width = 50f;
    private float pressure = 50f;

    // Estatísticas Históricas
    private int startYear = 1969;
    private int currentYear = 1969;
    private int titlesCount = 0;

    private int totalGames = 0;
    private int totalWins = 0;
    private int totalDraws = 0;
    private int totalLosses = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;

    private int maxUnbeatenStreak = 0;
    private int currentUnbeatenStreak = 0;
    private String biggestWin = "-";
    private int maxWinMargin = 0;

    private String topScorerName = "Sem registros";
    private int topScorerGoals = 0;

    private String mostGamesPlayerName = "Sem registros";
    private int mostGamesCount = 0;

    private String topAssisterName = "Sem registros";
    private int topAssisterCount = 0;
    private ClubFinance finance;
    private final Map<StaffRole, StaffMember> staffMembers = new EnumMap<>(StaffRole.class);

    public ClubFinance getFinance() {
        if (finance == null) {
            finance = new ClubFinance(this);
        }
        return finance;
    }

    /** Abre um novo ciclo anual da diretoria e fixa o OVR-base dos atletas. */
    public void beginBoardSeason(int season) {
        if (boardObjectiveSeason == season) return;
        boolean firstTrackedSeason = boardObjectiveSeason < 0;
        boardObjectiveSeason = season;
        previousBoardConfidence = boardConfidence;
        shownBoardReviews = new HashSet<>();
        for (Player player : squad) {
            /* A base histórica não possuía procedência de Draft. Jovens já
             * existentes recebem uma classe inicial estável para que as metas
             * de minutos também funcionem na temporada inaugural. */
            if (firstTrackedSeason && player.getDraftedYear() < 0 && player.getAge() <= 21) {
                int offset = Math.abs(player.getId().hashCode()) % 2;
                player.setDraftedYear(season - offset);
            }
            player.beginSeasonTracking(season);
        }
    }

    public int getBoardObjectiveSeason() { return boardObjectiveSeason; }
    public int getBoardConfidence() { return Math.max(0, Math.min(100, boardConfidence)); }
    public int getPreviousBoardConfidence() { return Math.max(0, Math.min(100, previousBoardConfidence)); }
    public void updateBoardConfidence(int confidence) {
        previousBoardConfidence = getBoardConfidence();
        boardConfidence = Math.max(0, Math.min(100, confidence));
    }
    public boolean hasShownBoardReview(int season, String checkpoint) {
        return shownBoardReviews != null && shownBoardReviews.contains(season + ":" + checkpoint);
    }
    public void markBoardReviewShown(int season, String checkpoint) {
        if (shownBoardReviews == null) shownBoardReviews = new HashSet<>();
        shownBoardReviews.add(season + ":" + checkpoint);
    }
    public void recordFinalBoardEvaluation(int season, int score, boolean dismissed) {
        if (finalBoardEvaluationSeason == season) return;
        finalBoardEvaluationSeason = season;
        finalBoardPreviousConfidence = getBoardConfidence();
        finalBoardScore = Math.max(0, Math.min(100, score));
        finalBoardDismissed = dismissed;
        updateBoardConfidence(finalBoardScore);
        markBoardReviewShown(season, "FINAL");
    }
    public int getFinalBoardEvaluationSeason() { return finalBoardEvaluationSeason; }
    public int getFinalBoardPreviousConfidence() { return finalBoardPreviousConfidence; }
    public int getFinalBoardScore() { return finalBoardScore; }
    public boolean isFinalBoardDismissed() { return finalBoardDismissed; }

    private List<SeasonHistory> seasonHistories = new ArrayList<>();

    /**
     * Atualiza a moral do elenco com base no resultado, favoritismo e sequências.
     * @param resultType 1 = Vitória, 0 = Empate, -1 = Derrota
     * @param opponentOverall Overall do time adversário
     */
    public void updateSquadMorale(int resultType, double opponentOverall, java.util.Set<Player> participants) {
        // Difference > 0 significa que o seu clube é superior ao adversário
        double overallDiff = this.getOverall() - opponentOverall;

        for (Player player : this.squad) {
            if (!participants.contains(player)) {
                player.adjustMorale(-2);
                continue;
            }
            int currentMorale = player.getMorale();
            int moraleChange = 0;

            if (resultType > 0) {
                // --- VITÓRIA ---
                if (overallDiff < -3.0) {
                    // Vitória sendo zebra dá um grande ganho de moral
                    moraleChange = 8 + (int) (Math.abs(overallDiff) * 0.5);
                } else if (overallDiff > 5.0) {
                    // Vitória muito esperada dá ganho leve
                    moraleChange = 3;
                } else {
                    moraleChange = 5;
                }
            } else if (resultType == 0) {
                // --- EMPATE ---
                if (overallDiff > 6.0) {
                    // Empate contra time muito inferior é visto como tropeço
                    moraleChange = - (int) (overallDiff * 0.4);
                } else if (overallDiff < -6.0) {
                    // Empate contra time bem mais forte dá um pequeno ganho
                    moraleChange = 3;
                }
            } else {
                // --- DERROTA ---
                if (overallDiff > 0) {
                    // Derrota para time pior (Zebra sofrida): Perda base mais penalidade por diferença
                    int zebraPenalty = (int) (overallDiff * 2.85);
                    moraleChange = -(7 + zebraPenalty);
                } else {
                    // Derrota esperada (para time superior)
                    int mitigation = (int) (Math.abs(overallDiff) * 0.3);
                    moraleChange = -Math.max(2, 7 - mitigation);
                }
            }

            // Aplica o ajuste garantindo que a moral fique entre 0 e 100
            int finalMorale = Math.max(0, Math.min(100, currentMorale + moraleChange));
            player.setMorale(finalMorale);
        }
    }

    public int getWinStreak() { return winStreak; }
    public int getLossStreak() { return lossStreak; }


    public Club() {
        this.squad = new ArrayList<>();
        this.startingXI = new ArrayList<>();
        this.tacticsMap = new HashMap<>();
        putInitialStaff(StaffRole.COACH, "Treinador principal", 82, 1971);
        putInitialStaff(StaffRole.SCOUT, "Chefe de scouting", 76, 1971);
        putInitialStaff(StaffRole.FITNESS_COACH, "Preparador físico", 84, 1971);
        putInitialStaff(StaffRole.DOCTOR, "Médico do clube", 72, 1971);
        putInitialStaff(StaffRole.DEVELOPMENT_DIRECTOR, "Diretor de desenvolvimento", 70, 1971);
    }

    public StaffMember getStaffMember(StaffRole role) { return staffMembers.get(role); }
    public int getStaffLevel(StaffRole role) {
        StaffMember member = staffMembers.get(role);
        return member != null ? member.getEffectLevel() : 3;
    }
    public void setStaffLevel(StaffRole role, int level) {
        int[] representativeQuality = {54, 64, 74, 84, 94};
        int safeLevel = Math.max(1, Math.min(5, level));
        StaffMember current = staffMembers.get(role);
        staffMembers.put(role, new StaffMember(
            role,
            current != null ? current.getName() : role.getLabel(),
            representativeQuality[safeLevel - 1],
            current != null ? current.getAnnualSalary() : StaffSalaryScale.annualSalary(role, representativeQuality[safeLevel - 1]),
            current != null ? current.getContractEndYear() : currentYear + 2,
            current != null ? current.getNationality() : "Internacional",
            current != null ? current.getSpecialty() : role.getLabel()
        ));
    }
    public void hireStaff(StaffMember member) { if (member != null) staffMembers.put(member.getRole(), member); }

    /** Renovação simples de staff: dois anos, sem slider ou contraoferta. */
    public void renewStaff(StaffRole role, int currentYear) {
        StaffMember current = staffMembers.get(role);
        if (current == null) return;
        staffMembers.put(role, new StaffMember(role, current.getName(), current.getQuality(),
            current.getAnnualSalary(), currentYear + 2, current.getNationality(), current.getSpecialty()));
    }

    /** Evita que um clube inicie a temporada sem um profissional em alguma função. */
    public void replaceExpiredStaff(int currentYear) {
        for (StaffRole role : StaffRole.values()) {
            StaffMember current = staffMembers.get(role);
            if (current == null || current.isExpired(currentYear)) {
                staffMembers.put(role, StaffDatabase.getAutomaticReplacement(role, getName(), currentYear));
            }
        }
    }

    private void applyStaffIdentity() {
        if (name == null) return;

        /*
         * Cada franquia nasce com uma comissão própria e persistente. Além de
         * aparecer na apresentação do clube, estes níveis são consumidos pelos
         * sistemas de scouting, recuperação, lesões e desenvolvimento.
         */
        switch (name) {
            case "Santos Atlântico":
                setInitialStaff("Mário Costa", 96, "Carlos Mendes", 88, "João Nogueira", 94,
                    "Alberto Lima", 84, "Dr. Renato Azevedo", 86);
                break;
            case "Rio Imperial":
                setInitialStaff("Luiz Tavares", 91, "Sérgio Matos", 86, "Paulo Rezende", 89,
                    "Roberto Diniz", 83, "Dr. Henrique Moura", 85);
                break;
            case "Milano Calcio":
                setInitialStaff("Giovanni Bianchi", 97, "Luca Romano", 82, "Marco Conti", 78,
                    "Paolo Ricci", 92, "Dr. Franco Gallo", 90);
                break;
            case "Bavaria München":
                setInitialStaff("Hans Keller", 96, "Dieter Vogel", 91, "Klaus Werner", 88,
                    "Otto Baumann", 95, "Dr. Friedrich Weiss", 93);
                break;
            case "Manchester Albion":
                setInitialStaff("Arthur Bennett", 92, "William Carter", 84, "George Whitmore", 83,
                    "Edward Collins", 94, "Dr. James Foster", 89);
                break;
            case "London Royals":
                setInitialStaff("Henry Clarke", 88, "Charles Reed", 87, "Alfred Morgan", 82,
                    "Thomas Hughes", 86, "Dr. Peter Wallace", 84);
                break;
            case "Amsterdã Total":
                setInitialStaff("Johan de Boer", 98, "Pieter Vos", 93, "Willem Smit", 97,
                    "Henk Dijkstra", 88, "Dr. Bram Meijer", 84);
                break;
            case "Madrid Castilla":
                setInitialStaff("Miguel Navarro", 94, "José Martín", 92, "Carlos Serrano", 86,
                    "Antonio Vega", 88, "Dr. Javier Ortiz", 91);
                break;
            case "Barcelona Mediterrâneo":
                setInitialStaff("Jordi Ferrer", 91, "Miquel Serra", 94, "Oriol Puig", 98,
                    "Ramón Soler", 83, "Dr. Enric Vidal", 87);
                break;
            case "Budapest Danube":
                setInitialStaff("László Farkas", 89, "István Nagy", 85, "Béla Kovács", 91,
                    "András Tóth", 82, "Dr. Miklós Varga", 84);
                break;
            case "Lisboa Atlântica":
                setInitialStaff("Manuel Ferreira", 87, "António Ribeiro", 96, "Rui Almeida", 97,
                    "Joaquim Lopes", 82, "Dr. Miguel Costa", 84);
                break;
            case "Buenos Aires Plata":
                setInitialStaff("Ernesto Salvatierra", 92, "Ricardo Luna", 88, "Osvaldo Ríos", 90,
                    "Héctor Gálvez", 91, "Dr. Julio Acosta", 83);
                break;
            case "Montevideo Oriental":
                setInitialStaff("Roque Bentancur", 90, "Martín Pereyra", 81, "Gustavo Silva", 84,
                    "Eduardo Cabrera", 94, "Dr. Andrés Sosa", 88);
                break;
            case "Paris Lumière":
                setInitialStaff("Pierre Laurent", 89, "Jean Moreau", 95, "Luc Bernard", 86,
                    "Alain Dubois", 84, "Dr. Étienne Garnier", 91);
                break;
            case "Belfast Northern Stars":
                setInitialStaff("Patrick O'Neill", 79, "Sean McKenna", 73, "Liam Campbell", 70,
                    "Brian Kelly", 90, "Dr. Colin Murphy", 78);
                break;
            case "Tokyo Rising Sun":
                setInitialStaff("Hiroshi Tanaka", 78, "Kenji Mori", 98, "Akira Sato", 96,
                    "Daichi Ito", 80, "Dr. Yuki Kato", 82);
                break;
            case "Seoul Tigers":
                setInitialStaff("Park Min-jun", 84, "Kim Tae-ho", 90, "Lee Dong-wook", 93,
                    "Choi Hyun-soo", 97, "Dr. Han Ji-won", 87);
                break;
            case "Tehran Lions":
                setInitialStaff("Reza Farhadi", 82, "Amir Hosseini", 84, "Darius Karimi", 79,
                    "Farid Azadi", 87, "Dr. Navid Rahimi", 85);
                break;
            case "Baghdad Mesopotamia":
                setInitialStaff("Khalid Al-Samarrai", 74, "Omar Nasser", 76, "Youssef Hamid", 78,
                    "Tariq Abbas", 82, "Dr. Samir Haddad", 77);
                break;
            case "Tel Aviv Stars":
                setInitialStaff("David Ben-Ami", 86, "Moshe Levi", 89, "Ariel Cohen", 84,
                    "Eitan Shalev", 86, "Dr. Noam Rosen", 92);
                break;
            default:
                break;
        }
    }

    private void setInitialStaff(
        String coach, int coachQuality,
        String scout, int scoutQuality,
        String development, int developmentQuality,
        String fitness, int fitnessQuality,
        String doctor, int doctorQuality
    ) {
        putInitialStaff(StaffRole.COACH, coach, coachQuality, 1972);
        putInitialStaff(StaffRole.SCOUT, scout, scoutQuality, 1971);
        putInitialStaff(StaffRole.DEVELOPMENT_DIRECTOR, development, developmentQuality, 1972);
        putInitialStaff(StaffRole.FITNESS_COACH, fitness, fitnessQuality, 1971);
        putInitialStaff(StaffRole.DOCTOR, doctor, doctorQuality, 1972);
    }

    private void putInitialStaff(StaffRole role, String memberName, int quality, int contractEndYear) {
        long annualSalary = StaffSalaryScale.annualSalary(role, quality);
        staffMembers.put(role, new StaffMember(role, memberName, quality, annualSalary, contractEndYear,
            country != null ? country : "Internacional", initialStaffSpecialty(role)));
    }

    private String initialStaffSpecialty(StaffRole role) {
        switch (role) {
            case COACH: return philosophy != null ? philosophy : "Tática";
            case SCOUT: return scoutingSpecialty();
            case DEVELOPMENT_DIRECTOR: return developmentSpecialty();
            case FITNESS_COACH: return fitnessSpecialty();
            case DOCTOR: default: return medicalSpecialty();
        }
    }

    private String scoutingSpecialty() {
        if ("Tokyo Rising Sun".equals(name)) return "Tecnologia e mercado asiático";
        if ("Lisboa Atlântica".equals(name)) return "Rede global de prospecção";
        if ("Paris Lumière".equals(name) || "Madrid Castilla".equals(name)) return "Talento internacional";
        if ("Amsterdã Total".equals(name) || "Barcelona Mediterrâneo".equals(name)) return "Academias europeias";
        if ("Santos Atlântico".equals(name) || "Rio Imperial".equals(name)
            || "Buenos Aires Plata".equals(name) || "Montevideo Oriental".equals(name)) {
            return "Talento sul-americano";
        }
        return "Mercado regional e jovens";
    }

    private String developmentSpecialty() {
        if ("Amsterdã Total".equals(name)) return "Futebol total e inteligência";
        if ("Barcelona Mediterrâneo".equals(name)) return "Formação técnica e posse";
        if ("Lisboa Atlântica".equals(name)) return "Lapidação e valorização";
        if ("Tokyo Rising Sun".equals(name)) return "Planos individuais e tecnologia";
        if ("Seoul Tigers".equals(name)) return "Evolução física de jovens";
        if ("Budapest Danube".equals(name)) return "Escola técnica húngara";
        if ("Santos Atlântico".equals(name) || "Rio Imperial".equals(name)) return "Criatividade e técnica individual";
        return "Desenvolvimento de potencial";
    }

    private String fitnessSpecialty() {
        if ("Bavaria München".equals(name) || "Manchester Albion".equals(name)) return "Pressão e alta intensidade";
        if ("Seoul Tigers".equals(name)) return "Velocidade e explosão";
        if ("Montevideo Oriental".equals(name) || "Belfast Northern Stars".equals(name)) return "Resistência e duelos físicos";
        if ("Milano Calcio".equals(name)) return "Disciplina e controle de carga";
        return "Condicionamento e recuperação";
    }

    private String medicalSpecialty() {
        if ("Bavaria München".equals(name) || "Milano Calcio".equals(name)) return "Prevenção e reabilitação de elite";
        if ("Madrid Castilla".equals(name) || "Paris Lumière".equals(name)) return "Gestão médica de estrelas";
        if ("Tel Aviv Stars".equals(name)) return "Diagnóstico e prevenção";
        return "Medicina esportiva";
    }

    /**
     * Identidade inicial de cada franquia. Os cinco valores já alimentam o
     * TacticalEngine, portanto as mentalidades da IA também têm efeito real
     * na simulação, e não são apenas texto de apresentação.
     */
    private void applyTacticalIdentity() {

        if (name == null) return;

        switch (name) {
            case "Santos Atlântico":
                setIdentity("Futebol ofensivo e técnico", "Ofensiva", 65f, 30f, 72f, 62f);
                break;
            case "Rio Imperial":
                setIdentity("Desenvolver jovens", "Equilibrada", 55f, 42f, 60f, 55f);
                break;
            case "Milano Calcio":
                setIdentity("Disciplina defensiva", "Defensiva", 42f, 42f, 38f, 68f);
                break;
            case "Bavaria München":
                setIdentity("Intensidade e mentalidade vencedora", "Ofensiva", 70f, 48f, 58f, 72f);
                break;
            case "Manchester Albion":
                setIdentity("Força coletiva", "Equilibrada", 62f, 55f, 76f, 66f);
                break;
            case "London Royals":
                setIdentity("Estrelas e jogo pelos lados", "Ofensiva", 64f, 55f, 78f, 56f);
                break;
            case "Amsterdã Total":
                setIdentity("Futebol total", "Ofensiva", 76f, 25f, 82f, 82f);
                break;
            case "Madrid Castilla":
                setIdentity("Talento de elite", "Ofensiva", 62f, 38f, 68f, 58f);
                break;
            case "Barcelona Mediterrâneo":
                setIdentity("Posse e formação", "Ofensiva", 52f, 20f, 66f, 72f);
                break;
            case "Budapest Danube":
                setIdentity("Disciplina coletiva", "Equilibrada", 54f, 42f, 46f, 66f);
                break;
            case "Lisboa Atlântica":
                setIdentity("Formar e valorizar talentos", "Equilibrada", 58f, 32f, 64f, 60f);
                break;
            case "Buenos Aires Plata":
                setIdentity("Talento e garra", "Ofensiva", 66f, 36f, 66f, 72f);
                break;
            case "Montevideo Oriental":
                setIdentity("Competitividade e resiliência", "Defensiva", 48f, 48f, 42f, 72f);
                break;
            case "Paris Lumière":
                setIdentity("Estrelas globais", "Ofensiva", 66f, 52f, 76f, 56f);
                break;
            case "Belfast Northern Stars":
                setIdentity("Jogo direto e físico", "Equilibrada", 64f, 82f, 52f, 68f);
                break;
            case "Tokyo Rising Sun":
                setIdentity("Desenvolvimento de jovens", "Ofensiva", 72f, 34f, 70f, 82f);
                break;
            case "Seoul Tigers":
                setIdentity("Intensidade e desenvolvimento", "Ofensiva", 76f, 50f, 66f, 86f);
                break;
            case "Tehran Lions":
                setIdentity("Eficiência no mercado", "Defensiva", 46f, 68f, 40f, 56f);
                break;
            case "Baghdad Mesopotamia":
                setIdentity("Identidade local e organização", "Defensiva", 44f, 72f, 36f, 62f);
                break;
            case "Tel Aviv Stars":
                setIdentity("Equilíbrio competitivo", "Equilibrada", 56f, 46f, 56f, 62f);
                break;
            default:
                break;
        }
    }

    private void setIdentity(
        String philosophy,
        String mentality,
        float tempo,
        float passing,
        float width,
        float pressure
    ) {

        this.philosophy = philosophy;
        setMentality(mentality);
        this.tempo = tempo;
        this.passing = passing;
        this.width = width;
        this.pressure = pressure;
    }

    public Club(String name) {
        this();
        this.name = name;
        applyTacticalIdentity();
        applyStaffIdentity();
    }

    public Club(String name, String country, String confederation, int reputation, double budget, String stadiumName, String logoPath) {
        this();
        this.name = name;
        this.country = country;
        this.confederation = confederation;
        this.reputation = reputation;
        this.budget = budget;
        this.stadiumName = stadiumName;
        this.logoPath = logoPath;
        applyTacticalIdentity();
        applyStaffIdentity();
    }

    public void recordMatchResult(int goalsScored, int goalsConceded) {
        this.totalGames++;
        this.goalsFor += goalsScored;
        this.goalsAgainst += goalsConceded;

        if (goalsScored > goalsConceded) {
            this.totalWins++;
            this.currentUnbeatenStreak++;
            if (this.currentUnbeatenStreak > this.maxUnbeatenStreak) {
                this.maxUnbeatenStreak = this.currentUnbeatenStreak;
            }

            int margin = goalsScored - goalsConceded;
            if (margin > this.maxWinMargin) {
                this.maxWinMargin = margin;
                this.biggestWin = goalsScored + " x " + goalsConceded;
            }
        } else if (goalsScored == goalsConceded) {
            this.totalDraws++;
            this.currentUnbeatenStreak++;
            if (this.currentUnbeatenStreak > this.maxUnbeatenStreak) {
                this.maxUnbeatenStreak = this.currentUnbeatenStreak;
            }
        } else {
            this.totalLosses++;
            this.currentUnbeatenStreak = 0;
        }
    }

    public void finishSeason(String ligaResult, String copaResult) {
        seasonHistories.add(0, new SeasonHistory(this.currentYear, ligaResult, copaResult));
        this.currentYear++;
    }

    public void addPlayerToSquad(Player player) {
        if (player == null) return;
        this.squad.add(player);
        if (this.startingXI.size() < 11) {
            this.startingXI.add(player);
            this.tacticsMap.put(this.startingXI.size() - 1, player);
        }
    }

    public void assignPlayerToSlot(int targetSlot, Player player) {
        if (player == null) {
            tacticsMap.remove(targetSlot);
            syncStartingXIFromTacticsMap();
            return;
        }

        if (!player.canPlay()) {
            return;
        }

        Integer currentSlotOfPlayer = null;
        for (Map.Entry<Integer, Player> entry : tacticsMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(player)) {
                currentSlotOfPlayer = entry.getKey();
                break;
            }
        }

        Player occupantInTarget = tacticsMap.get(targetSlot);

        if (currentSlotOfPlayer != null) {
            if (occupantInTarget != null) {
                tacticsMap.put(currentSlotOfPlayer, occupantInTarget);
            } else {
                tacticsMap.remove(currentSlotOfPlayer);
            }
        }

        tacticsMap.put(targetSlot, player);
        syncStartingXIFromTacticsMap();
    }

    /**
     * Seleciona automaticamente o melhor XI para a formação atual.
     *
     * Prioridade:
     * 1) posição principal exatamente igual ao slot;
     * 2) posição secundária exatamente igual ao slot;
     * 3) posição compatível/relacionada;
     * 4) melhor jogador restante, sem colocar goleiro na linha
     *    nem jogador de linha no gol.
     *
     * Jogadores lesionados ou suspensos são ignorados.
     */
    public void autoSelectXI() {
        tacticsMap.clear();
        startingXI.clear();

        if (
            formation == null ||
            formation.getPositionSlots() == null ||
            squad == null ||
            squad.isEmpty()
        ) {
            return;
        }

        List<String> slots = formation.getPositionSlots();
        int slotLimit = Math.min(11, slots.size());

        Set<Player> usedPlayers = new HashSet<>();

        // -----------------------------------------------------
        // FASE 1: posição principal exata
        // -----------------------------------------------------
        fillAutoLineupPhase(
            slots,
            slotLimit,
            usedPlayers,
            1
        );

        // -----------------------------------------------------
        // FASE 2: posição secundária exata
        // -----------------------------------------------------
        fillAutoLineupPhase(
            slots,
            slotLimit,
            usedPlayers,
            2
        );

        // -----------------------------------------------------
        // FASE 3: posição relacionada/compatível
        // -----------------------------------------------------
        fillAutoLineupPhase(
            slots,
            slotLimit,
            usedPlayers,
            3
        );

        // -----------------------------------------------------
        // FASE 4: último recurso
        // -----------------------------------------------------
        fillAutoLineupPhase(
            slots,
            slotLimit,
            usedPlayers,
            4
        );

        fillEmergencyLineupSlots(
            slots,
            slotLimit,
            usedPlayers
        );

        /* Última garantia: se o elenco possui onze atletas disponíveis, uma
         * composição incomum (por exemplo, excesso de goleiros) não pode
         * deixar o clube com menos de onze. A adaptação é penalizada pelo OVR
         * efetivo, mas é sempre preferível a uma vaga vazia. */
        fillAnyRemainingLineupSlots(
            slots,
            slotLimit,
            usedPlayers
        );

        syncStartingXIFromTacticsMap();
    }

    /**
     * Garante um XI completo quando o elenco não tem uma correspondência
     * perfeita para algum slot. A prioridade continua sendo o jogador mais
     * adequado; o último recurso evita vagas vazias na escalação da IA.
     */
    private void fillEmergencyLineupSlots(
        List<String> slots,
        int slotLimit,
        Set<Player> usedPlayers
    ) {
        for (int slotIndex = 0; slotIndex < slotLimit; slotIndex++) {
            if (tacticsMap.get(slotIndex) != null) {
                continue;
            }

            String targetPosition = slots.get(slotIndex);
            boolean goalkeeperSlot = "GK".equalsIgnoreCase(targetPosition);
            Player bestPlayer = null;
            int bestScore = Integer.MIN_VALUE;

            for (Player player : squad) {
                if (player == null || usedPlayers.contains(player) || !player.canPlay()) {
                    continue;
                }

                boolean goalkeeper = player.getPrimaryPosition() == Position.GK;
                if (goalkeeperSlot != goalkeeper) {
                    continue;
                }

                int score = calculateAutoSelectionScore(player, targetPosition);
                if (score > bestScore) {
                    bestScore = score;
                    bestPlayer = player;
                }
            }

            /*
             * Caso todos os goleiros estejam indisponíveis, o time ainda
             * entra com 11 atletas em vez de deixar o slot em branco.
             */
            if (bestPlayer == null && goalkeeperSlot) {
                for (Player player : squad) {
                    if (player == null || usedPlayers.contains(player) || !player.canPlay()) {
                        continue;
                    }

                    int score = calculateAutoSelectionScore(player, targetPosition);
                    if (score > bestScore) {
                        bestScore = score;
                        bestPlayer = player;
                    }
                }
            }

            if (bestPlayer != null) {
                tacticsMap.put(slotIndex, bestPlayer);
                usedPlayers.add(bestPlayer);
            }
        }
    }

    private void fillAnyRemainingLineupSlots(
        List<String> slots,
        int slotLimit,
        Set<Player> usedPlayers
    ) {
        for (int slotIndex = 0; slotIndex < slotLimit; slotIndex++) {
            if (tacticsMap.get(slotIndex) != null) {
                continue;
            }

            String targetPosition = slots.get(slotIndex);
            Player bestPlayer = null;
            int bestScore = Integer.MIN_VALUE;

            for (Player player : squad) {
                if (player == null || usedPlayers.contains(player) || !player.canPlay()) {
                    continue;
                }

                int score = calculateAutoSelectionScore(player, targetPosition);
                if (score > bestScore) {
                    bestScore = score;
                    bestPlayer = player;
                }
            }

            if (bestPlayer != null) {
                tacticsMap.put(slotIndex, bestPlayer);
                usedPlayers.add(bestPlayer);
            }
        }
    }

    /**
     * Escolhe, para clubes controlados pela IA, a formação que obtém o
     * melhor rendimento posicional do elenco disponível e escala o XI.
     */
    public void autoSelectBestFormationAndXI() {
        if (squad == null || squad.isEmpty()) {
            return;
        }

        Formation bestFormation = null;
        Map<Integer, Player> bestTactics = null;
        int bestScore = Integer.MIN_VALUE;

        for (Formation candidate : Formation.values()) {
            formation = candidate;
            autoSelectXI();

            int score = scoreCurrentTactics(candidate);
            if (score > bestScore) {
                bestScore = score;
                bestFormation = candidate;
                bestTactics = new HashMap<>(tacticsMap);
            }
        }

        if (bestFormation != null && bestTactics != null) {
            formation = bestFormation;
            tacticsMap.clear();
            tacticsMap.putAll(bestTactics);
            syncStartingXIFromTacticsMap();
        }
    }

    private int scoreCurrentTactics(Formation candidate) {
        int score = 0;
        List<String> slots = candidate.getPositionSlots();

        for (int slotIndex = 0; slotIndex < slots.size(); slotIndex++) {
            Player player = tacticsMap.get(slotIndex);
            if (player == null) {
                score -= 100000;
                continue;
            }

            String targetPosition = slots.get(slotIndex);
            String primaryPosition = player.getPrimaryPosition() != null
                ? player.getPrimaryPosition().name()
                : "";
            String secondaryPosition = player.getSecondaryPosition() != null
                ? player.getSecondaryPosition().name()
                : "";

            score += player.getEffectiveOverallForPosition(targetPosition) * 100;

            if (primaryPosition.equalsIgnoreCase(targetPosition)) {
                score += 120;
            } else if (secondaryPosition.equalsIgnoreCase(targetPosition)) {
                score += 70;
            } else if (isRelatedPosition(primaryPosition, targetPosition)) {
                score += 25;
            }
        }

        return score;
    }

    private void fillAutoLineupPhase(
        List<String> slots,
        int slotLimit,
        Set<Player> usedPlayers,
        int phase
    ) {
        for (
            int slotIndex = 0;
            slotIndex < slotLimit;
            slotIndex++
        ) {
            if (tacticsMap.get(slotIndex) != null) {
                continue;
            }

            String targetPosition = slots.get(slotIndex);

            Player bestPlayer = findBestPlayerForAutoSlot(
                targetPosition,
                usedPlayers,
                phase
            );

            if (bestPlayer != null) {
                tacticsMap.put(
                    slotIndex,
                    bestPlayer
                );

                usedPlayers.add(
                    bestPlayer
                );
            }
        }
    }

    private Player findBestPlayerForAutoSlot(
        String targetPosition,
        Set<Player> usedPlayers,
        int phase
    ) {
        Player bestPlayer = null;
        int bestScore = Integer.MIN_VALUE;

        for (Player player : squad) {
            if (
                player == null ||
                usedPlayers.contains(player) ||
                !player.canPlay()
            ) {
                continue;
            }

            String primaryPosition =
                player.getPrimaryPosition() != null
                    ? player.getPrimaryPosition().name()
                    : "";

            String secondaryPosition =
                player.getSecondaryPosition() != null
                    ? player.getSecondaryPosition().name()
                    : "";

            boolean eligible = false;

            switch (phase) {
                case 1:
                    eligible =
                        primaryPosition.equalsIgnoreCase(
                            targetPosition
                        );
                    break;

                case 2:
                    eligible =
                        secondaryPosition.equalsIgnoreCase(
                            targetPosition
                        );
                    break;

                case 3:
                    eligible =
                        isRelatedPosition(
                            primaryPosition,
                            targetPosition
                        ) ||
                        (
                            !secondaryPosition.isEmpty() &&
                            isRelatedPosition(
                                secondaryPosition,
                                targetPosition
                            )
                        );
                    break;

                case 4:
                    boolean targetIsGoalkeeper =
                        "GK".equalsIgnoreCase(
                            targetPosition
                        );

                    boolean playerIsGoalkeeper =
                        "GK".equalsIgnoreCase(
                            primaryPosition
                        );

                    eligible =
                        targetIsGoalkeeper ==
                            playerIsGoalkeeper;
                    break;

                default:
                    break;
            }

            if (!eligible) {
                continue;
            }

            int score =
                calculateAutoSelectionScore(
                    player,
                    targetPosition
                );

            if (score > bestScore) {
                bestScore = score;
                bestPlayer = player;
            }
        }

        return bestPlayer;
    }

    private int calculateAutoSelectionScore(
        Player player,
        String targetPosition
    ) {
        int effectiveOverall =
            player.getEffectiveOverallForPosition(
                targetPosition
            );

        /*
         * O effective overall já leva em conta a adaptação de
         * posição e a fadiga. O OVR base funciona como desempate.
         */
        return
            effectiveOverall * 100 +
            player.getOverall();
    }

    private boolean isRelatedPosition(
        String playerPosition,
        String targetPosition
    ) {
        if (
            playerPosition == null ||
            targetPosition == null
        ) {
            return false;
        }

        String from =
            playerPosition
                .trim()
                .toUpperCase();

        String to =
            targetPosition
                .trim()
                .toUpperCase();

        if (from.equals(to)) {
            return true;
        }

        // Goleiro nunca é adaptado para a linha ou vice-versa.
        if (
            from.equals("GK") ||
            to.equals("GK")
        ) {
            return false;
        }

        switch (from) {
            case "LB":
                return
                    to.equals("LWB") ||
                    to.equals("LM");

            case "LWB":
                return
                    to.equals("LB") ||
                    to.equals("LM") ||
                    to.equals("LW");

            case "RB":
                return
                    to.equals("RWB") ||
                    to.equals("RM");

            case "RWB":
                return
                    to.equals("RB") ||
                    to.equals("RM") ||
                    to.equals("RW");

            case "CB":
                return
                    to.equals("CDM");

            case "CDM":
                return
                    to.equals("CM") ||
                    to.equals("CB");

            case "CM":
                return
                    to.equals("CDM") ||
                    to.equals("CAM") ||
                    to.equals("LM") ||
                    to.equals("RM");

            case "CAM":
                return
                    to.equals("CM") ||
                    to.equals("CF");

            case "LM":
                return
                    to.equals("LW") ||
                    to.equals("CM") ||
                    to.equals("LWB");

            case "RM":
                return
                    to.equals("RW") ||
                    to.equals("CM") ||
                    to.equals("RWB");

            case "LW":
                return
                    to.equals("LM") ||
                    to.equals("CF");

            case "RW":
                return
                    to.equals("RM") ||
                    to.equals("CF");

            case "CF":
                return
                    to.equals("ST") ||
                    to.equals("CAM") ||
                    to.equals("LW") ||
                    to.equals("RW");

            case "ST":
                return
                    to.equals("CF");

            default:
                return false;
        }
    }

    private void syncStartingXIFromTacticsMap() {
        startingXI.clear();
        for (Player p : tacticsMap.values()) {
            if (p != null && !startingXI.contains(p)) {
                startingXI.add(p);
            }
        }
    }

    /**
     * Remove da escalação jogadores que não podem continuar na partida,
     * como lesionados, suspensos ou expulsos.
     */
    public void removeUnavailablePlayersFromStartingXI() {
        boolean lineupChanged = startingXI.removeIf(
            player -> player == null || !player.canPlay()
        );

        boolean tacticsChanged = false;

        for (
            Map.Entry<Integer, Player> entry :
            tacticsMap.entrySet()
        ) {

            Player player =
                entry.getValue();

            if (
                player != null &&
                    !player.canPlay()
            ) {

                entry.setValue(
                    null
                );

                tacticsChanged =
                    true;
            }
        }

        if (lineupChanged || tacticsChanged) {
            syncStartingXIFromTacticsMap();
        }
    }

    // Getters & Setters
    public List<Player> getPlayers() { return getSquad(); } // Alias de compatibilidade
    public List<Player> getSquad() { return squad; }
    public void setSquad(List<Player> squad) { this.squad = squad; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isUserControlled() { return userControlled; }
    public void setUserControlled(boolean userControlled) { this.userControlled = userControlled; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getConfederation() { return confederation; }
    public String getConference() { return confederation; }
    public void setConfederation(String confederation) { this.confederation = confederation; }

    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getStadiumName() { return stadiumName; }
    public String getStadium() { return stadiumName != null ? stadiumName : "Estádio Municipal"; }
    public void setStadiumName(String stadiumName) { this.stadiumName = stadiumName; }

    public int getStadiumCapacity() { return stadiumCapacity; }
    public void setStadiumCapacity(int stadiumCapacity) { this.stadiumCapacity = stadiumCapacity; }

    /**
     * Capacidade realmente liberada em dias de jogo. Uma obra fecha parte das
     * arquibancadas até a entrega, portanto público e bilheteria usam este
     * valor enquanto a capacidade patrimonial continua sendo exibida à parte.
     */
    public int getOperationalStadiumCapacity() {
        if (!isStadiumRenovationInProgress()) return stadiumCapacity;
        if (stadiumRenovationTemporaryCapacity > 0) return stadiumRenovationTemporaryCapacity;
        int expansion = Math.max(0, stadiumRenovationTargetCapacity - stadiumCapacity);
        return calculateTemporaryStadiumCapacity(expansion);
    }

    public int previewTemporaryStadiumCapacity(StadiumRenovationPlan plan) {
        return plan == null ? stadiumCapacity
            : calculateTemporaryStadiumCapacity(plan.getAdditionalCapacity());
    }

    private int calculateTemporaryStadiumCapacity(int additionalCapacity) {
        /* Obras agora fecham pelo menos 18% do estádio. Projetos de maior
         * porte também interditam 60% dos novos lugares planejados, o que
         * faz um novo anel comprometer muito mais setores que uma ampliação
         * simples. */
        int closedSeats = Math.max(
            5_000,
            Math.max(
                Math.round(stadiumCapacity * .18f),
                Math.round(additionalCapacity * .60f)
            )
        );
        return Math.max(5_000, stadiumCapacity - closedSeats);
    }

    /** Preço médio praticado pelo clube mandante, em WFL$. */
    public int getAverageTicketPrice() {
        return averageTicketPrice > 0 ? averageTicketPrice : getSuggestedTicketPrice();
    }

    public void setAverageTicketPrice(int price) {
        averageTicketPrice = Math.max(10, Math.min(100, price));
    }

    /** Referência sustentável baseada no poder de atração histórico do clube. */
    public int getSuggestedTicketPrice() {
        return Math.max(18, Math.min(45, Math.round(20f + (getReputation() - 75) * .8f)));
    }

    public int getStadiumCondition() { return Math.max(0, Math.min(100, stadiumCondition)); }
    public void setStadiumCondition(int condition) { stadiumCondition = Math.max(0, Math.min(100, condition)); }

    /** O gramado do mandante perde dois pontos a cada partida concluída. */
    public void recordHomeMatchStadiumWear() { setStadiumCondition(getStadiumCondition() - 2); }

    public long getPitchTreatmentCost() {
        return Math.max(350_000L, stadiumCapacity * 18L);
    }

    public long getPitchReplacementCost() {
        return Math.max(1_200_000L, stadiumCapacity * 52L);
    }

    /** Tratamento localizado: recuperação parcial de 15 pontos. */
    public boolean treatStadiumPitch() {
        if (getStadiumCondition() >= 100 || !getFinance().spend(getPitchTreatmentCost())) return false;
        setStadiumCondition(getStadiumCondition() + 15);
        return true;
    }

    /** Troca integral do gramado: restaura a condição máxima. */
    public boolean replaceStadiumPitch() {
        if (getStadiumCondition() >= 100 || !getFinance().spend(getPitchReplacementCost())) return false;
        setStadiumCondition(100);
        return true;
    }

    /** Mantém as franquias da IA sujeitas às mesmas despesas e decisões. */
    public void autoMaintainStadiumPitch() {
        if (isUserControlled()) return;
        if (getStadiumCondition() <= 50 && getFinance().getBalance() >= getPitchReplacementCost()) {
            replaceStadiumPitch();
        } else if (getStadiumCondition() <= 72 && getFinance().getBalance() >= getPitchTreatmentCost()) {
            treatStadiumPitch();
        }
    }

    public boolean startStadiumRenovation(StadiumRenovationPlan plan) {
        if (plan == null || isStadiumRenovationInProgress()) return false;
        int target = stadiumCapacity + plan.getAdditionalCapacity();
        if (target > StadiumRenovationPlan.MAX_CAPACITY) return false;
        if (!getFinance().spend(plan.getCost())) return false;

        stadiumRenovationName = plan.getDisplayName();
        stadiumRenovationTargetCapacity = target;
        stadiumRenovationTotalDays = plan.getDurationDays();
        stadiumRenovationDaysRemaining = plan.getDurationDays();
        stadiumRenovationCost = plan.getCost();
        stadiumRenovationTemporaryCapacity = previewTemporaryStadiumCapacity(plan);
        return true;
    }

    /** Avança a obra e entrega a nova capacidade somente na conclusão. */
    public boolean advanceStadiumRenovationDay() {
        if (!isStadiumRenovationInProgress()) return false;
        stadiumRenovationDaysRemaining--;
        if (stadiumRenovationDaysRemaining <= 0) {
            stadiumCapacity = stadiumRenovationTargetCapacity;
            stadiumRenovationDaysRemaining = 0;
            stadiumRenovationTemporaryCapacity = 0;
            return true;
        }
        return false;
    }

    public boolean isStadiumRenovationInProgress() { return stadiumRenovationDaysRemaining > 0; }
    public String getStadiumRenovationName() { return stadiumRenovationName; }
    public int getStadiumRenovationTargetCapacity() { return stadiumRenovationTargetCapacity; }
    public int getStadiumRenovationTotalDays() { return stadiumRenovationTotalDays; }
    public int getStadiumRenovationDaysRemaining() { return stadiumRenovationDaysRemaining; }
    public long getStadiumRenovationCost() { return stadiumRenovationCost; }
    public int getStadiumRenovationTemporaryCapacity() { return getOperationalStadiumCapacity(); }
    public double getStadiumRenovationProgress() {
        if (stadiumRenovationTotalDays <= 0) return 0d;
        return 100d * (stadiumRenovationTotalDays - stadiumRenovationDaysRemaining)
            / stadiumRenovationTotalDays;
    }

    public String getPhilosophy() { return philosophy; }
    public void setPhilosophy(String philosophy) { this.philosophy = philosophy; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public List<Player> getStartingXI() {
        removeUnavailablePlayersFromStartingXI();
        if (startingXI.isEmpty() && !tacticsMap.isEmpty()) {
            syncStartingXIFromTacticsMap();
        }
        return startingXI;
    }

    public void setStartingXI(List<Player> startingXI) {
        this.startingXI = startingXI;
    }

    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }

    public Map<Integer, Player> getTacticsMap() {
        removeUnavailablePlayersFromStartingXI();
        return tacticsMap;
    }
    public void setTacticsMap(Map<Integer, Player> tacticsMap) {
        this.tacticsMap = tacticsMap;
        syncStartingXIFromTacticsMap();
    }

    public String getMentality() { return mentality; }
    public void setMentality(String mentality) {
        this.mentality = mentality == null ? "Equilibrada" : mentality;
        switch (this.mentality.toLowerCase()) {
            case "ultra defensiva":
            case "retranca": mentalityValue = 10f; break;
            case "defensiva": mentalityValue = 20f; break;
            case "cautelosa": mentalityValue = 35f; break;
            case "equilibrada": mentalityValue = 50f; break;
            case "positiva": mentalityValue = 65f; break;
            case "ofensiva": mentalityValue = 82f; break;
            case "ultra ofensiva":
            case "ataque total": mentalityValue = 100f; break;
            default:
                try { setMentalityValue(Float.parseFloat(this.mentality)); }
                catch (NumberFormatException ignored) { mentalityValue = 50f; }
        }
    }

    public void setMentalityValue(float value) {
        mentalityValue = Math.max(0f, Math.min(100f, value));
        if (mentalityValue <= 25f) mentality = "Defensiva";
        else if (mentalityValue <= 40f) mentality = "Cautelosa";
        else if (mentalityValue <= 59f) mentality = "Equilibrada";
        else if (mentalityValue <= 74f) mentality = "Positiva";
        else if (mentalityValue <= 89f) mentality = "Ofensiva";
        else mentality = "Ultra Ofensiva";
    }

    public float getMentalityValue() {
        return Math.max(0f, Math.min(100f, mentalityValue));
    }

    public float getTempo() { return tempo; }
    public void setTempo(float tempo) { this.tempo = Math.max(0f, Math.min(100f, tempo)); }

    public float getPassing() { return passing; }
    public void setPassing(float passing) { this.passing = Math.max(0f, Math.min(100f, passing)); }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = Math.max(0f, Math.min(100f, width)); }

    public float getPressure() { return pressure; }
    public void setPressure(float pressure) { this.pressure = Math.max(0f, Math.min(100f, pressure)); }

    public int getCurrentYear() { return currentYear; }
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }
    public void setStartYear(int startYear) { this.startYear = startYear; }
    public int getStartYear() { return startYear; }

    public int getTitlesCount() { return titlesCount; }
    public void setTitlesCount(int titlesCount) { this.titlesCount = titlesCount; }

    public int getTotalGames() { return totalGames; }
    public int getTotalWins() { return totalWins; }
    public int getTotalDraws() { return totalDraws; }
    public int getTotalLosses() { return totalLosses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalsFor - goalsAgainst; }

    public int getWinPercentage() {
        if (totalGames == 0) return 0;
        return (int) (((totalWins * 3.0 + totalDraws) / (totalGames * 3.0)) * 100);
    }

    public int getMaxUnbeatenStreak() { return maxUnbeatenStreak; }
    public String getBiggestWin() { return biggestWin; }

    public String getTopScorerName() { return topScorerName; }
    public int getTopScorerGoals() { return topScorerGoals; }
    public void setTopScorer(String name, int goals) { this.topScorerName = name; this.topScorerGoals = goals; }

    public String getMostGamesPlayerName() { return mostGamesPlayerName; }
    public int getMostGamesCount() { return mostGamesCount; }
    public void setMostGamesPlayer(String name, int games) { this.mostGamesPlayerName = name; this.mostGamesCount = games; }

    public String getTopAssisterName() { return topAssisterName; }
    public int getTopAssisterCount() { return topAssisterCount; }
    public void setTopAssister(String name, int assists) { this.topAssisterName = name; this.topAssisterCount = assists; }

    public List<SeasonHistory> getSeasonHistories() { return seasonHistories; }

    public double getOverall() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream().mapToInt(Player::getOverall).average().orElse(60.0);
    }

    public double getAttackingRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPrimaryPosition() != null && p.getPrimaryPosition().name().matches("ST|CF|RW|LW|CAM"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(0.0);
    }

    public double getDefensiveRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPosition() != null && p.getPosition().matches("CB|RB|LB|CDM|GK"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(getOverall());
    }

    public double getMidfieldRating() {
        List<Player> starters = getStartingXI();
        if (starters.isEmpty()) return 60.0;
        return starters.stream()
            .filter(p -> p.getPosition() != null && p.getPosition().matches("CM|CAM|CDM|LM|RM"))
            .mapToInt(Player::getOverall)
            .average()
            .orElse(getOverall());
    }

    public void drainSquadFatigue(float amount) {
        for (Player player : getStartingXI()) {
            player.applyMatchFatigue(
                StaffImpact.matchFatigueMultiplier(getStaffLevel(StaffRole.FITNESS_COACH))
            );
        }
    }

    @Override
    public String toString() {
        return name != null ? name : "Clube Sem Nome";
    }
}
