package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import io.github.some_example_name.model.DraftPick;

public class Club {
    private String name;
    private String nickname = "";
    private String country;
    private String confederation;
    private int reputation;
    private double budget;
    private String stadiumName;
    private int stadiumCapacity = 30000;
    private String philosophy = "Desenvolver Jovens";
    private String logoPath;
    private boolean userControlled = false;
    private int winStreak = 0;
    private int lossStreak = 0;

    private List<Player> squad;
    private List<Player> startingXI;
    private List<DraftPick> draftPicks = new ArrayList<>();
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

    private List<SeasonHistory> seasonHistories = new ArrayList<>();

    /**
     * Atualiza a moral do elenco com base no resultado, favoritismo e sequências.
     * @param resultType 1 = Vitória, 0 = Empate, -1 = Derrota
     * @param opponentOverall Overall do time adversário
     */
    public void updateSquadMorale(int resultType, double opponentOverall) {
        // Difference > 0 significa que o seu clube é superior ao adversário
        double overallDiff = this.getOverall() - opponentOverall;

        for (Player player : this.squad) {
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
        staffMembers.put(StaffRole.COACH, new StaffMember(StaffRole.COACH, "Treinador principal", 82, 850_000L, 1971));
        staffMembers.put(StaffRole.SCOUT, new StaffMember(StaffRole.SCOUT, "Chefe de scouting", 76, 720_000L, 1971));
        staffMembers.put(StaffRole.FITNESS_COACH, new StaffMember(StaffRole.FITNESS_COACH, "Preparador físico", 84, 810_000L, 1971));
        staffMembers.put(StaffRole.DOCTOR, new StaffMember(StaffRole.DOCTOR, "Médico do clube", 72, 680_000L, 1971));
        staffMembers.put(StaffRole.DEVELOPMENT_DIRECTOR, new StaffMember(StaffRole.DEVELOPMENT_DIRECTOR, "Diretor de desenvolvimento", 70, 700_000L, 1971));
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
            current != null ? current.getAnnualSalary() : 700_000L,
            current != null ? current.getContractEndYear() : currentYear + 2
        ));
    }
    public void hireStaff(StaffMember member) { if (member != null) staffMembers.put(member.getRole(), member); }

    /** Renovação simples de staff: dois anos, sem slider ou contraoferta. */
    public void renewStaff(StaffRole role, int currentYear) {
        StaffMember current = staffMembers.get(role);
        if (current == null) return;
        staffMembers.put(role, new StaffMember(role, current.getName(), current.getQuality(),
            current.getAnnualSalary(), currentYear + 2));
    }

    /** Evita que um clube inicie a temporada sem um profissional em alguma função. */
    public void replaceExpiredStaff(int currentYear) {
        for (StaffRole role : StaffRole.values()) {
            StaffMember current = staffMembers.get(role);
            if (current == null || current.isExpired(currentYear)) {
                staffMembers.put(role, new StaffMember(role, "Interino " + role.getLabel(), 62,
                    620_000L, currentYear + 2));
            }
        }
    }

    private void applyStaffIdentity() {
        if ("Tokyo Rising Sun".equals(name)) {
            staffMembers.put(StaffRole.COACH, new StaffMember(StaffRole.COACH, "Hiroshi Tanaka", 66, 760_000L, 1971));
            staffMembers.put(StaffRole.SCOUT, new StaffMember(StaffRole.SCOUT, "Kenji Mori", 94, 1_050_000L, 1971));
            staffMembers.put(StaffRole.DEVELOPMENT_DIRECTOR, new StaffMember(StaffRole.DEVELOPMENT_DIRECTOR, "Akira Sato", 92, 1_020_000L, 1971));
            staffMembers.put(StaffRole.FITNESS_COACH, new StaffMember(StaffRole.FITNESS_COACH, "Daichi Ito", 66, 700_000L, 1971));
            staffMembers.put(StaffRole.DOCTOR, new StaffMember(StaffRole.DOCTOR, "Yuki Kato", 67, 710_000L, 1971));
        } else if ("Milano Calcio".equals(name)) {
            staffMembers.put(StaffRole.COACH, new StaffMember(StaffRole.COACH, "Giovanni Bianchi", 95, 1_080_000L, 1971));
            staffMembers.put(StaffRole.SCOUT, new StaffMember(StaffRole.SCOUT, "Luca Romano", 68, 710_000L, 1971));
            staffMembers.put(StaffRole.DEVELOPMENT_DIRECTOR, new StaffMember(StaffRole.DEVELOPMENT_DIRECTOR, "Marco Conti", 70, 740_000L, 1971));
            staffMembers.put(StaffRole.FITNESS_COACH, new StaffMember(StaffRole.FITNESS_COACH, "Paolo Ricci", 84, 880_000L, 1971));
            staffMembers.put(StaffRole.DOCTOR, new StaffMember(StaffRole.DOCTOR, "Franco Gallo", 84, 880_000L, 1971));
        }
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
        this.mentality = mentality;
        this.tempo = tempo;
        this.passing = passing;
        this.width = width;
        this.pressure = pressure;
    }

    public Club(String name) {
        this();
        this.name = name;
        applyStaffIdentity();
        applyTacticalIdentity();
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
        applyStaffIdentity();
        applyTacticalIdentity();
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
    public void setMentality(String mentality) { this.mentality = mentality; }

    public float getMentalityValue() {
        if (mentality == null) return 50f;
        switch (mentality.toLowerCase()) {
            case "ultra defensiva":
            case "retranca": return 10f;
            case "defensiva": return 30f;
            case "equilibrada": return 50f;
            case "ofensiva": return 70f;
            case "ultra ofensiva":
            case "ataque total": return 90f;
            default:
                try { return Float.parseFloat(mentality); }
                catch (NumberFormatException e) { return 50f; }
        }
    }

    public float getTempo() { return tempo; }
    public void setTempo(float tempo) { this.tempo = tempo; }

    public float getPassing() { return passing; }
    public void setPassing(float passing) { this.passing = passing; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getPressure() { return pressure; }
    public void setPressure(float pressure) { this.pressure = pressure; }

    public int getCurrentYear() { return currentYear; }
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }

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
            player.applyMatchFatigue();
        }
    }

    @Override
    public String toString() {
        return name != null ? name : "Clube Sem Nome";
    }
}
