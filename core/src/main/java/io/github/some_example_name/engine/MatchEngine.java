package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.FieldSector;
import io.github.some_example_name.model.AttendanceService;
import io.github.some_example_name.model.AutomaticInjurySubstitutionService;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.LeagueHistory;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.NarrativeCategory;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.PossessionSequence;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.model.StaffImpact;
import io.github.some_example_name.model.TechnicalAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class MatchEngine {
    /** Chance-base por equipe/minuto; fadiga e intensidade continuam pesando. */
    private static final double INJURY_CHECK_CHANCE = 0.0115d;
    static final int POSSESSION_DUEL_STAGE_COUNT = 6;
    private static final int DUEL_VARIATION_LIMIT = 4;

    private final Random random = new Random();
    private final League league;
    private final Map<NarrativeCategory, Integer> lastNarrativeVariant =
        new EnumMap<>(NarrativeCategory.class);
    private final Map<Match, Integer> lastFatigueNarrationMinute = new WeakHashMap<>();

    public MatchEngine() {
        this(null);
    }

    public MatchEngine(League league) {
        this.league = league;
    }

    public void simulate(Match match) {
        AttendanceService.ensureAttendance(league, match);
        prepareMatchLineups(match);

        int homeInjurySubstitutions = 0;
        int awayInjurySubstitutions = 0;
        List<Player> homeSubstitutedPlayers = new ArrayList<>();
        List<Player> awaySubstitutedPlayers = new ArrayList<>();

        for (int min = 1; min <= 90; min++) {
            Map<Integer, Player> homeBeforeMinute = new HashMap<>(match.getHomeTeam().getTacticsMap());
            Map<Integer, Player> awayBeforeMinute = new HashMap<>(match.getAwayTeam().getTacticsMap());
            MatchEvent event = simulateMinute(match, min);

            if (event == null || !"LESIONADO".equals(event.type)) continue;

            if (event.isHomeTeam && homeInjurySubstitutions < 5) {
                AutomaticInjurySubstitutionService.Result result = replaceInjuredPlayerDuringFastSimulation(
                    match, match.getHomeTeam(), homeBeforeMinute, homeSubstitutedPlayers, min
                );
                if (result != null) {
                    homeInjurySubstitutions++;
                    homeSubstitutedPlayers.add(result.injured);
                }
            } else if (!event.isHomeTeam && awayInjurySubstitutions < 5) {
                AutomaticInjurySubstitutionService.Result result = replaceInjuredPlayerDuringFastSimulation(
                    match, match.getAwayTeam(), awayBeforeMinute, awaySubstitutedPlayers, min
                );
                if (result != null) {
                    awayInjurySubstitutions++;
                    awaySubstitutedPlayers.add(result.injured);
                }
            }
        }
        finalizeMatch(match);
    }

    /** Executa a troca obrigatória usada pela simulação integral, sem abrir a tela de táticas. */
    static AutomaticInjurySubstitutionService.Result replaceInjuredPlayerDuringFastSimulation(
        Match match,
        Club club,
        Map<Integer, Player> lineupBeforeMinute,
        List<Player> alreadySubstituted,
        int minute
    ) {
        if (club == null) return null;
        return AutomaticInjurySubstitutionService.replace(
            match,
            club,
            lineupBeforeMinute,
            club.getBenchPlayers(),
            alreadySubstituted,
            minute
        );
    }

    /**
     * Valida as escalações antes do apito inicial. A IA sempre escolhe a
     * melhor formação disponível; o clube do usuário preserva sua escalação
     * manual quando ela já possui 11 atletas, mas é recomposto se alguma
     * lesão, suspensão ou alteração anterior tiver deixado uma vaga vazia.
     */
    public void prepareMatchLineups(Match match) {
        if (match == null) {
            return;
        }

        prepareLineupsForPreview(match);
        resetInMatchFatigueAccumulators(match.getHomeTeam());
        resetInMatchFatigueAccumulators(match.getAwayTeam());
        match.capturePrematchTactics();
        match.recordStartingLineups(
            new java.util.ArrayList<>(match.getHomeTeam().getStartingXI()),
            new java.util.ArrayList<>(match.getAwayTeam().getStartingXI())
        );
    }

    /**
     * Atualiza as escalações que aparecem na tela pré-jogo sem registrar
     * definitivamente os atletas. Assim a IA já é exibida com onze nomes e
     * o usuário ainda pode alterar sua equipe antes do pontapé inicial.
     */
    public void prepareLineupsForPreview(Match match) {
        if (match == null) {
            return;
        }

        prepareClubLineup(match.getHomeTeam());
        prepareClubLineup(match.getAwayTeam());
    }

    private void prepareClubLineup(Club club) {
        if (club == null) {
            return;
        }

        if (!club.isUserControlled()) {
            club.autoSelectBestFormationAndXI();
            return;
        }

        club.removeUnavailablePlayersFromStartingXI();
        if (club.getStartingXI().size() < 11) {
            club.autoSelectBestFormationAndXI();
        }
    }

    public MatchEvent simulateMinute(Match match, int minute) {
        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        List<Player> hStarters = home.getStartingXI();
        List<Player> aStarters = away.getStartingXI();

        // 1. Modificadores Táticos calculados via TacticalEngine
        TacticalContextEngine.ContextPair tacticalContext = TacticalContextEngine.apply(
            home, hStarters, away, aStarters, match, minute
        );
        TacticalModifiers hMods = tacticalContext.getHomeModifiers();
        TacticalModifiers aMods = tacticalContext.getAwayModifiers();
        applyMinuteFatigue(home, hStarters, hMods.fatigueMultiplier);
        applyMinuteFatigue(away, aStarters, aMods.fatigueMultiplier);
        match.recordTacticalSample(
            true,
            home,
            tacticalContext.getHomeFit().getOverallFitScore(home),
            tacticalContext.getHomeSustainability(),
            minute
        );
        match.recordTacticalSample(
            false,
            away,
            tacticalContext.getAwayFit().getOverallFitScore(away),
            tacticalContext.getAwaySustainability(),
            minute
        );

        // 2. Processamento de lesões
        MatchEvent homeInjury = processInjuryCheck(match, hStarters, home, minute, hMods, true);
        if (homeInjury != null) return homeInjury;

        MatchEvent awayInjury = processInjuryCheck(match, aStarters, away, minute, aMods, false);
        if (awayInjury != null) return awayInjury;

        // 3. Determinação de Posse de Bola delegada à PossessionEngine
        Club possessionClub = PossessionEngine.determinePossession(home, hMods, away, aMods);
        boolean homeAttacking = possessionClub.equals(home);

        // Atualização do Momentum e da Posse no objeto Match
        double hMidPower = calculateSectorPower(hStarters, "passe") * 1.08 * hMods.possessionMultiplier * coachMultiplier(home);
        double aMidPower = calculateSectorPower(aStarters, "passe") * aMods.possessionMultiplier * coachMultiplier(away);
        double totalMid = Math.max(1, hMidPower + aMidPower);

        float momentum = (float) (hMidPower / totalMid) + (random.nextFloat() * 0.2f - 0.10f);
        match.setMomentum(Math.max(0.05f, Math.min(0.95f, momentum)));
        match.setPossession((int) (match.getHomeMomentum() * 100));

        Club attacker = homeAttacking ? home : away;
        Club defender = homeAttacking ? away : home;

        TacticalModifiers attMods = homeAttacking ? hMods : aMods;
        TacticalModifiers defMods = homeAttacking ? aMods : hMods;

        List<Player> attStarters = homeAttacking ? hStarters : aStarters;
        List<Player> defStarters = homeAttacking ? aStarters : hStarters;

        // O ritmo controla quantos minutos se transformam em ações reais.
        // Em ritmo 30 a partida é deliberadamente mais fechada; acima de 75
        // o crescimento acelera e produz um jogo muito mais caótico.
        double maximumEventChance = clamp(
            .70d + Math.max(0d, attMods.tempoSetting - 85d) * .01d,
            .70d,
            .85d
        );
        double eventChance = clamp(.58d * attMods.eventFrequencyMultiplier, .28d, maximumEventChance);
        if (random.nextDouble() > eventChance) return null;

        boolean forcedTransition = false;
        boolean highRegain = false;
        boolean pressBroken = false;
        boolean pressureAttempted = false;

        // O time sem a bola tenta recuperar. Pressão bem executada vira uma
        // transição curta; se a primeira linha falhar, a defesa fica exposta.
        if (random.nextDouble() < defMods.regainChance) {
            pressureAttempted = true;
            double regainSuccess = clamp(
                .46d * defMods.pressingEfficiency / Math.max(.72d, attMods.passRetentionMultiplier),
                .24d,
                .72d
            );
            if (random.nextDouble() < regainSuccess) {
                highRegain = random.nextDouble() < defMods.highRegainChance;
                Club previousAttacker = attacker;
                attacker = defender;
                defender = previousAttacker;
                TacticalModifiers previousAttMods = attMods;
                attMods = defMods;
                defMods = previousAttMods;
                List<Player> previousAttStarters = attStarters;
                attStarters = defStarters;
                defStarters = previousAttStarters;
                homeAttacking = !homeAttacking;
                forcedTransition = true;
            } else if (random.nextDouble() < defMods.pressBreakRisk) {
                pressBroken = true;
            }
        }

        // Ritmo, inadequação do passe e pressão rival aumentam as perdas.
        // A perda muda de fato o sentido da jogada e cria um contra-ataque.
        if (!forcedTransition) {
            double turnoverChance = clamp(
                .07d * attMods.turnoverRiskMultiplier * defMods.opponentErrorMultiplier
                    / Math.max(.78d, attMods.passRetentionMultiplier),
                .025d,
                .22d
            );
            if (random.nextDouble() < turnoverChance) {
                pressBroken = false;
                Club previousAttacker = attacker;
                attacker = defender;
                defender = previousAttacker;
                TacticalModifiers previousAttMods = attMods;
                attMods = defMods;
                defMods = previousAttMods;
                List<Player> previousAttStarters = attStarters;
                attStarters = defStarters;
                defStarters = previousAttStarters;
                homeAttacking = !homeAttacking;
                forcedTransition = true;
            }
        }

        // 4. Cálculo da Força Ofensiva e Defensiva delegados aos sub-motores
        boolean isCounterAttack = forcedTransition
            || random.nextDouble() < clamp(.16d * attMods.counterAttackMultiplier, .06d, .40d);
        if (forcedTransition) {
            if (homeAttacking) match.addHomeTransition(); else match.addAwayTransition();
        }
        if (highRegain) {
            if (homeAttacking) match.addHomeHighRegain(); else match.addAwayHighRegain();
        }
        double atkPower = AttackEngine.calculateAttackPower(attacker, attMods, isCounterAttack);
        double defPower = DefenseEngine.calculateDefensePower(defender, defMods);
        if (isCounterAttack) {
            defPower /= Math.max(.72d, defMods.counterVulnerabilityMultiplier);
        }
        if (pressBroken) {
            defPower *= 1d - defMods.pressBreakDefensePenalty;
        }
        if (highRegain) {
            atkPower *= 1.08d;
        }
        atkPower *= coachMultiplier(attacker);
        defPower *= coachMultiplier(defender);

        /* Fadiga, moral e mando de campo influenciam todos os lances. */
        atkPower *= calculateLineupCondition(attStarters);
        defPower *= calculateLineupCondition(defStarters);
        if (homeAttacking) {
            atkPower *= 1.04;
        } else {
            defPower *= 1.04;
        }

        double actionRoll = random.nextDouble();
        double shotActionEnd = clamp(
            .35d + (attMods.eventFrequencyMultiplier - 1d) * .08d
                + (attMods.playersCommittedForward - 6) * .025d,
            .27d,
            .52d
        );
        double foulActionEnd = Math.min(.78d, shotActionEnd + .28d);
        double crossingActionEnd = Math.min(.90d, foulActionEnd + .14d);

        // --- MÓDULO A: ATAQUE EM FASES ---
        // Não há mais um segundo sorteio perguntando se este evento "vira"
        // chute. A posse precisa vencer saída, construção, meio, último terço
        // e criação; só então processShotSequence registra uma finalização.
        if (actionRoll < shotActionEnd) {
            return processShotSequence(match, minute, homeAttacking, attacker, defender,
                attStarters, defStarters, atkPower, defPower, attMods, defMods,
                isCounterAttack, highRegain);
        }

        // --- MÓDULO B: DISPUTAS FÍSICAS, FALTAS E CARTÕES ---
        if (actionRoll >= shotActionEnd && actionRoll < foulActionEnd) {
            String disciplineOutcome = DefenseEngine.checkFoulOrCard(defMods);
            if (!disciplineOutcome.equals("NENHUM") || random.nextDouble() < 0.30) {
                return processFoulSequence(match, minute, homeAttacking, attacker, defender, attStarters, defStarters, defMods);
            }
        }

        // --- MÓDULO C: ESCANTEIOS E CRUZAMENTOS ---
        if (actionRoll >= foulActionEnd && actionRoll < crossingActionEnd) {
            if (AttackEngine.isCrossingPlay(attMods)) {
                if (homeAttacking) match.addHomeCorner(); else match.addAwayCorner();

                Player crosser = bestSequencePlayer(attStarters, "WIDE");
                Player marker = bestSequencePlayer(defStarters, "DEFEND");

                return new MatchEvent(
                    minute,
                    narrateCrossingDuel(crosser, marker, attacker, defender),
                    "ESCANTEIO",
                    homeAttacking
                ).withNarrativeCategory(NarrativeCategory.DUELO);
            }
        }

        // --- MÓDULO D: CONSTRUÇÃO DE JOGADA E RETENÇÃO DE POSSE ---
        Player passer = getPasserPlayer(attStarters);
        Player receiver = bestSequencePlayer(attStarters, "BUILD", passer);
        Player runner = bestSequencePlayer(attStarters, "PROGRESS", passer, receiver);
        Player ballWinner = bestSequencePlayer(attStarters, "RECOVER", passer, receiver, runner);
        Player opponent = bestSequencePlayer(defStarters, "DEFEND");
        Player presser = bestSequencePlayer(defStarters, "RECOVER");

        passer = firstNonNull(passer, receiver, runner, ballWinner);
        receiver = firstNonNull(receiver, runner, passer, ballWinner);
        runner = firstNonNull(runner, receiver, passer, ballWinner);
        ballWinner = firstNonNull(ballWinner, passer, receiver, runner);

        if (highRegain) {
            return new MatchEvent(
                minute,
                narratePressureSuccess(ballWinner, opponent, attacker, defender),
                "RECUPERACAO_ALTA",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.PRESSAO);
        } else if (forcedTransition) {
            return new MatchEvent(
                minute,
                narrateTransition(ballWinner, opponent, runner, attacker, defender),
                "TRANSICAO",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.TRANSICAO);
        } else if (pressBroken) {
            return new MatchEvent(
                minute,
                narrateBrokenPress(passer, receiver, presser, attacker, defender),
                "CONSTRUCAO",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.PRESSAO);
        } else if (pressureAttempted) {
            return new MatchEvent(
                minute,
                narrateContainedPressure(presser, passer, receiver, attacker, defender),
                "CONSTRUCAO",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.PRESSAO);
        }

        MatchEvent fatigueContext = createFatigueContextEvent(
            match, minute, homeAttacking, attacker, defender, attStarters, defStarters
        );
        if (fatigueContext != null) {
            return fatigueContext;
        } else if (attMods.passingSetting <= 40f && random.nextDouble() < 0.58) {
            return new MatchEvent(
                minute,
                narrateShortBuild(passer, receiver, runner, attacker),
                "POSSE",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.CONSTRUCAO);
        } else if (attMods.passingSetting >= 60f && random.nextDouble() < 0.58) {
            return new MatchEvent(
                minute,
                narrateDirectBuild(passer, runner, opponent, attacker, defender),
                "CONSTRUCAO",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.CONSTRUCAO);
        } else if (random.nextDouble() < 0.50) {
            return new MatchEvent(
                minute,
                narratePatientBuild(passer, receiver, opponent, attacker, defender),
                "POSSE",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.CONSTRUCAO);
        } else {
            return new MatchEvent(
                minute,
                narrateMidfieldDuel(passer, receiver, opponent, attacker, defender),
                "CONSTRUCAO",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.DUELO);
        }
    }

    private String narrateCrossingDuel(Player crosser, Player marker, Club attacker, Club defender) {
        String wide = playerName(crosser, attacker.getName());
        String guard = playerName(marker, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.DUELO, 3)) {
            case 0:
                return wide + " encara " + guard
                    + ", ganha a linha de fundo e cruza. O marcador consegue cortar para escanteio.";
            case 1:
                return guard + " acompanha " + wide + " até o fundo. "
                    + wide + " força o cruzamento e a bola desvia para escanteio.";
            default:
                return wide + " muda de direção diante de " + guard
                    + ". O duelo termina com o corte da defesa para escanteio.";
        }
    }

    private String narratePressureSuccess(Player winner, Player victim, Club attacker, Club defender) {
        String hunter = playerName(winner, attacker.getName());
        String pressed = playerName(victim, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.PRESSAO, 3)) {
            case 0:
                return hunter + " aperta " + pressed + " e rouba no campo ofensivo. "
                    + attacker.getName() + " pega a defesa desmontada!";
            case 1:
                return hunter + " fecha a linha de passe de " + pressed
                    + " e recupera perto da área. " + defender.getName() + " ainda tenta se reorganizar.";
            default:
                return pressed + " hesita sob a pressão de " + hunter + ". "
                    + attacker.getName() + " recupera alto e acelera antes da recomposição.";
        }
    }

    private String narrateTransition(
        Player winner,
        Player victim,
        Player runner,
        Club attacker,
        Club defender
    ) {
        String thief = playerName(winner, attacker.getName());
        String dispossessed = playerName(victim, defender.getName());
        String outlet = playerName(runner, attacker.getName());
        switch (nextNarrativeVariant(NarrativeCategory.TRANSICAO, 3)) {
            case 0:
                return thief + " toma a bola de " + dispossessed + " e aciona " + outlet
                    + ". " + attacker.getName() + " parte contra uma defesa em recuo.";
            case 1:
                return thief + " vence o duelo com " + dispossessed + ". " + outlet
                    + " já dispara pelo espaço deixado por " + defender.getName() + ".";
            default:
                return dispossessed + " perde a posse para " + thief + ". " + outlet
                    + " acelera a transição antes que a defesa se recomponha.";
        }
    }

    private String narrateBrokenPress(
        Player passer,
        Player receiver,
        Player presser,
        Club attacker,
        Club defender
    ) {
        String first = playerName(passer, attacker.getName());
        String target = playerName(receiver, attacker.getName());
        String hunter = playerName(presser, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.PRESSAO, 3)) {
            case 0:
                return first + " escapa da pressão de " + hunter + " e encontra " + target
                    + ". " + attacker.getName() + " vê espaço às costas do meio-campo.";
            case 1:
                return hunter + " salta para pressionar, mas " + first + " acha " + target
                    + " por dentro. A primeira linha de " + defender.getName() + " foi quebrada.";
            default:
                return first + " atrai " + hunter + " e solta a bola no momento certo. "
                    + target + " recebe livre além da pressão.";
        }
    }

    private String narrateContainedPressure(
        Player presser,
        Player passer,
        Player receiver,
        Club attacker,
        Club defender
    ) {
        String hunter = playerName(presser, defender.getName());
        String carrier = playerName(passer, attacker.getName());
        String support = playerName(receiver, attacker.getName());
        switch (nextNarrativeVariant(NarrativeCategory.PRESSAO, 3)) {
            case 0:
                return hunter + " aperta " + carrier + " e força o passe para trás. "
                    + attacker.getName() + " precisa recomeçar.";
            case 1:
                return hunter + " encurta rápido sobre " + carrier + ". " + support
                    + " oferece apoio, mas recebe de costas para o ataque.";
            default:
                return defender.getName() + " sobe o bloco. " + hunter + " fecha o espaço de "
                    + carrier + " e impede a progressão.";
        }
    }

    private String narrateShortBuild(Player passer, Player receiver, Player runner, Club attacker) {
        String first = playerName(passer, attacker.getName());
        String second = playerName(receiver, attacker.getName());
        String third = playerName(runner, attacker.getName());
        switch (nextNarrativeVariant(NarrativeCategory.CONSTRUCAO, 3)) {
            case 0:
                return first + " encontra " + second + " entre as linhas. "
                    + second + " toca de primeira para " + third + ".";
            case 1:
                return first + " aproxima o jogo com " + second + ". " + third
                    + " se oferece por dentro e dá continuidade à posse.";
            default:
                return second + " recua para receber de " + first + " e atrai a marcação. "
                    + third + " ocupa o espaço que se abre à frente.";
        }
    }

    private String narrateDirectBuild(
        Player passer,
        Player runner,
        Player opponent,
        Club attacker,
        Club defender
    ) {
        String first = playerName(passer, attacker.getName());
        String target = playerName(runner, attacker.getName());
        String marker = playerName(opponent, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.CONSTRUCAO, 3)) {
            case 0:
                return first + " levanta a cabeça e lança " + target + " nas costas de " + marker + ".";
            case 1:
                return target + " ataca o espaço atrás de " + marker + ". " + first
                    + " percebe o movimento e estica a jogada.";
            default:
                return first + " troca o corredor com um passe longo para " + target
                    + ". " + marker + " precisa correr em direção ao próprio gol.";
        }
    }

    private String narratePatientBuild(
        Player passer,
        Player receiver,
        Player opponent,
        Club attacker,
        Club defender
    ) {
        String first = playerName(passer, attacker.getName());
        String target = playerName(receiver, attacker.getName());
        String marker = playerName(opponent, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.CONSTRUCAO, 3)) {
            case 0:
                return first + " encontra " + target + " entre as linhas. " + target
                    + " protege a bola diante de " + marker + ".";
            case 1:
                return first + " pausa, espera o movimento e serve " + target + ". " + marker
                    + " fecha o caminho para a área.";
            default:
                return target + " recebe de costas após passe de " + first + ". "
                    + defender.getName() + " mantém o bloco compacto ao redor da bola.";
        }
    }

    private String narrateMidfieldDuel(
        Player passer,
        Player receiver,
        Player opponent,
        Club attacker,
        Club defender
    ) {
        String first = playerName(passer, attacker.getName());
        String target = playerName(receiver, attacker.getName());
        String marker = playerName(opponent, defender.getName());
        switch (nextNarrativeVariant(NarrativeCategory.DUELO, 3)) {
            case 0:
                return first + " toca de primeira para " + target + ", que enfrenta " + marker
                    + " na intermediária.";
            case 1:
                return target + " recebe de " + first + " e tenta girar sobre " + marker
                    + ". O marcador acompanha de perto.";
            default:
                return marker + " encurta o espaço de " + target + " após o passe de " + first
                    + ". O duelo trava a progressão por dentro.";
        }
    }

    private MatchEvent createFatigueContextEvent(
        Match match,
        int minute,
        boolean homeAttacking,
        Club attacker,
        Club defender,
        List<Player> attackers,
        List<Player> defenders
    ) {
        if (minute < 55) return null;

        Player tired = lowestConditionOutfieldPlayer(defenders);
        if (tired == null || tired.getFatigue() > 72) return null;

        Integer lastMinute = lastFatigueNarrationMinute.get(match);
        if (lastMinute != null && minute - lastMinute < 12) return null;

        double chance = clamp(.05d + (72d - tired.getFatigue()) * .018d, .05d, .24d);
        if (random.nextDouble() >= chance) return null;

        lastFatigueNarrationMinute.put(match, minute);
        Player exploiter = bestSequencePlayer(attackers, "PROGRESS");
        String weary = tired.getName();
        String threat = playerName(exploiter, attacker.getName());
        String description;
        switch (nextNarrativeVariant(NarrativeCategory.ERRO, 3)) {
            case 0:
                description = weary + " demora a recompor. " + threat
                    + " percebe o espaço; a intensidade começa a cobrar seu preço.";
                break;
            case 1:
                description = weary + " perde alguns metros na volta. " + attacker.getName()
                    + " acelera sobre o lado cansado de " + defender.getName() + ".";
                break;
            default:
                description = weary + " chega atrasado à cobertura e " + threat
                    + " recebe livre. O desgaste já altera o posicionamento defensivo.";
                break;
        }

        return new MatchEvent(minute, description, "FADIGA", homeAttacking)
            .withNarrativeCategory(NarrativeCategory.ERRO);
    }

    private Player lowestConditionOutfieldPlayer(List<Player> players) {
        Player tired = null;
        if (players == null) return null;
        for (Player player : players) {
            if (player == null || !player.canPlay() || player.getPrimaryPosition().isGoalkeeper()) continue;
            if (tired == null || player.getFatigue() < tired.getFatigue()) tired = player;
        }
        return tired;
    }

    private int nextNarrativeVariant(NarrativeCategory category, int count) {
        if (count <= 1) return 0;
        int selected = random.nextInt(count);
        Integer previous = lastNarrativeVariant.get(category);
        if (previous != null && selected == previous) {
            selected = (selected + 1 + random.nextInt(count - 1)) % count;
        }
        lastNarrativeVariant.put(category, selected);
        return selected;
    }

    private MatchEvent processInjuryCheck(Match match, List<Player> starters, Club club, int minute, TacticalModifiers mods, boolean isHomeTeam) {
        List<Player> activeStarters = starters.stream()
            .filter(p -> p.getMatchRedCards() == 0 && !p.isInjured())
            .collect(Collectors.toList());

        if (activeStarters.isEmpty()) return null;

        Player victim = activeStarters.get(random.nextInt(activeStarters.size()));

        int doctorLevel = club.getStaffLevel(StaffRole.DOCTOR);
        double doctorRisk = StaffImpact.injuryRiskMultiplier(doctorLevel);
        double relapseRisk = victim.getRelapseRiskDays() > 0
            ? StaffImpact.relapseRiskMultiplier(doctorLevel) : 1d;
        if (random.nextDouble() > INJURY_CHECK_CHANCE * doctorRisk * relapseRisk) return null;

        double fatigueRisk = (100.0 - victim.getFatigue()) / 100.0;
        double totalRisk = fatigueRisk * mods.fatigueMultiplier;

        if (random.nextDouble() < totalRisk || random.nextDouble() < 0.35) {
            InjuryProfile injury = drawInjuryProfile(fatigueRisk);
            victim.injureForDays(injury.daysOut, injury.type);
            match.registerPlayerExit(victim, minute);
            club.removeUnavailablePlayersFromStartingXI();

            return new MatchEvent(
                minute,
                victim.getName() + " cai no gramado. Não parece conseguir continuar.",
                "LESIONADO",
                isHomeTeam
            ).withInjuryDetails(
                victim,
                suspectedCondition(injury.type),
                injury.type,
                injury.daysOut
            );
        }

        return null;
    }

    private String suspectedCondition(String diagnosis) {
        if (diagnosis == null) return "LESÃO A SER AVALIADA";

        String normalized = diagnosis.toUpperCase();
        if (normalized.contains("MUSCULAR")) return "LESÃO MUSCULAR SUSPEITA";
        if (normalized.contains("TORNOZELO")) return "TRAUMA NO TORNOZELO SUSPEITO";
        if (normalized.contains("JOELHO") || normalized.contains("LIGAMENTAR")) {
            return "LESÃO NO JOELHO SUSPEITA";
        }
        if (normalized.contains("FRATURA")) return "TRAUMA ÓSSEO SUSPEITO";
        if (normalized.contains("CONTUSÃO")) return "CONTUSÃO SUSPEITA";
        return "LESÃO A SER AVALIADA";
    }

    private double coachMultiplier(Club club) {
        int level = club != null ? club.getStaffLevel(StaffRole.COACH) : 3;
        return StaffImpact.coachPerformance(level);
    }

    /** Soma a temporada em andamento ao histórico já fechado da WFL. */
    private int getWflCareerGoals(Player player) {
        if (player == null) return 0;

        int completedGoals = 0;
        boolean historyIncludesCurrentSeason = false;
        if (league != null && league.getHistory() != null) {
            String playerKey = player.getId() != null ? player.getId() : player.getName();
            LeagueHistory.PlayerCareer career = league.getHistory().getPlayerCareer(playerKey);
            if (career != null) {
                completedGoals = career.getGoals();
                for (LeagueHistory.PlayerSeason season : career.getSeasons()) {
                    if (season.getYear() == league.getCurrentSeason()) {
                        historyIncludesCurrentSeason = true;
                        break;
                    }
                }
            }
        }

        return completedGoals + (historyIncludesCurrentSeason ? 0 : player.getSeasonGoals());
    }

    /**
     * Sorteia diagnóstico e prazo. A fadiga aumenta apenas a chance de
     * diagnósticos graves, mantendo contusões curtas como o resultado mais comum.
     */
    private InjuryProfile drawInjuryProfile(double fatigueRisk) {
        double severityRoll = Math.min(
            0.999d,
            random.nextDouble() + Math.min(0.16d, Math.max(0d, fatigueRisk) * 0.20d)
        );

        if (severityRoll < 0.22d) return injury("CONTUSÃO", 1, 4);
        if (severityRoll < 0.42d) return injury("TORÇÃO NO TORNOZELO", 4, 10);
        if (severityRoll < 0.62d) return injury("DISTENSÃO MUSCULAR", 7, 16);
        if (severityRoll < 0.78d) return injury("ESTIRAMENTO MUSCULAR", 12, 24);
        if (severityRoll < 0.90d) return injury("ENTORSE NO JOELHO", 18, 35);
        if (severityRoll < 0.97d) return injury("LESÃO LIGAMENTAR", 35, 65);
        return injury("FRATURA", 60, 120);
    }

    private InjuryProfile injury(String type, int minimumDays, int maximumDays) {
        int daysOut = minimumDays + random.nextInt(maximumDays - minimumDays + 1);
        return new InjuryProfile(type, daysOut);
    }

    private static final class InjuryProfile {
        private final String type;
        private final int daysOut;

        private InjuryProfile(String type, int daysOut) {
            this.type = type;
            this.daysOut = daysOut;
        }
    }

    private MatchEvent processShotSequence(Match match, int minute, boolean homeAttacking, Club attacker, Club defender,
                                           List<Player> attStarters, List<Player> defStarters,
                                           double atkPower, double defPower, TacticalModifiers attMods,
                                           TacticalModifiers defMods,
                                           boolean counterAttack, boolean highRegain) {

        FieldSectorProfile attackingShape = FieldSectorProfile.from(attacker);
        FieldSector.Lane attackingLane = attackingShape.chooseLane(attMods.widthSetting, random);
        Player shooter = getBestAvailableAttacker(attStarters, attacker, attackingLane);
        Player goalkeeper = getGoalkeeper(defStarters);

        if (shooter == null) {
            if (homeAttacking) {
                match.addHomeShot(false);
            } else {
                match.addAwayShot(false);
            }

            return new MatchEvent(
                minute,
                attacker.getName() + " chega ao ataque, mas não encontra ninguém em condições de finalizar.",
                "CHUTE",
                homeAttacking
            );
        }

        int shooterAtkAttr = shooter.getTechnicalAttributes().getAtaque();
        PossessionResolution possession = resolvePossessionSequence(
            attacker, defender, attStarters, defStarters, shooter, attMods, defMods,
            attackingLane, counterAttack, highRegain
        );

        if (!possession.reachedShot()) {
            String interruption = narratePossessionInterruption(
                possession.failedStage,
                possession.attackingPlayers,
                possession.defendingPlayers,
                attacker,
                defender
            );
            MatchEvent interrupted = new MatchEvent(
                minute,
                interruption,
                "PERDA_BOLA",
                homeAttacking
            ).withNarrativeCategory(NarrativeCategory.ERRO);
            return interrupted.withPossessionSequence(
                buildPossessionSequence(
                    minute, interrupted, attacker, defender, attStarters, defStarters,
                    shooter, goalkeeper, attMods, counterAttack, highRegain,
                    "ataque interrompido", 0f, possession
                )
            );
        }

        double shotQuality = (atkPower / Math.max(1, atkPower + defPower))
            * (shooterAtkAttr / 100.0)
            * attMods.boxPresenceMultiplier
            * possession.qualityMultiplier;
        float xGValue = (float) Math.min(0.85, Math.max(0.04, shotQuality * (0.30 + random.nextDouble() * 0.30)));

        // A etapa SHOT já resolveu ATA + apoio + condição contra DEF + FIS
        // + cobertura. O goleiro só entra depois, quando o chute vence esse duelo.
        boolean onTarget = possession.wasSuccessful(5);

        if (homeAttacking) {
            match.addHomeShot(onTarget);
            match.addHomeXG(xGValue);
        } else {
            match.addAwayShot(onTarget);
            match.addAwayXG(xGValue);
        }

        String transitionPrefix = shotContextPrefix(attacker, highRegain, counterAttack);

        if (onTarget) {
            int gkReflexes = goalkeeper != null ? goalkeeper.getTechnicalAttributes().getGoleiro() : 60;

            double goalkeeperFactor = Math.max(
                0.72,
                1.12 - ((gkReflexes - 60) / 180.0)
            );
            double goalProbability = Math.min(
                0.62,
                Math.max(0.03, xGValue * goalkeeperFactor)
            );

            if (random.nextDouble() < goalProbability) {
                if (homeAttacking) match.setHomeGoals(match.getHomeGoals() + 1);
                else match.setAwayGoals(match.getAwayGoals() + 1);

                match.addGoalScorer(shooter);
                shooter.addGoal();

                Player assisterCandidate = getRandomAssister(attStarters, shooter);
                Player creditedAssister = null;
                if (assisterCandidate != null && random.nextDouble() < 0.65) {
                    creditedAssister = assisterCandidate;
                    match.addAssister(creditedAssister);
                    creditedAssister.addAssist();
                }

                return createSequencedShotEvent(
                    minute, transitionPrefix + narrateGoal(shooter, attacker, xGValue),
                    "GOL", "gol", homeAttacking, attacker, defender,
                    attStarters, defStarters, shooter, goalkeeper, attMods,
                    counterAttack, highRegain, xGValue, possession
                ).withGoalDetails(
                    shooter,
                    creditedAssister,
                    shooter.getSeasonGoals(),
                    getWflCareerGoals(shooter)
                );
            } else {
                double reboundChance = clamp(
                    .08d * attMods.boxPresenceMultiplier
                        * (attMods.playersCommittedForward / 6d),
                    .04d,
                    .22d
                );
                if (random.nextDouble() < reboundChance) {
                    if (homeAttacking) {
                        match.addHomeShot(false);
                        match.addHomeXG(.05f);
                    } else {
                        match.addAwayShot(false);
                        match.addAwayXG(.05f);
                    }
                    return createSequencedShotEvent(
                        minute, transitionPrefix + narrateBlockedRebound(shooter, goalkeeper, attacker),
                        "REBOTE_OFENSIVO", "rebote e chute bloqueado", homeAttacking,
                        attacker, defender, attStarters, defStarters, shooter, goalkeeper, attMods,
                        counterAttack, highRegain, xGValue, possession
                    );
                }
                if (random.nextDouble() < 0.35) {
                    if (homeAttacking) match.addHomeCorner(); else match.addAwayCorner();
                    return createSequencedShotEvent(
                        minute, transitionPrefix + narrateSaveToCorner(shooter, goalkeeper),
                        "ESCANTEIO", "defesa para escanteio", homeAttacking,
                        attacker, defender, attStarters, defStarters, shooter, goalkeeper, attMods,
                        counterAttack, highRegain, xGValue, possession
                    );
                }
                return createSequencedShotEvent(
                    minute, transitionPrefix + narrateHandledShot(shooter, goalkeeper),
                    "CHUTE", "defesa do goleiro", homeAttacking, attacker,
                    defender, attStarters, defStarters, shooter, goalkeeper, attMods, counterAttack,
                    highRegain, xGValue, possession
                );
            }
        }

        return createSequencedShotEvent(
            minute, transitionPrefix + narrateMissedShot(shooter, attacker),
            "CHUTE", "finalização para fora", homeAttacking, attacker,
            defender, attStarters, defStarters, shooter, goalkeeper, attMods, counterAttack,
            highRegain, xGValue, possession
        );
    }

    /**
     * Resolve as pequenas disputas que formam o ataque usando somente os
     * atributos e modificadores já existentes. Cada vantagem ou desvantagem
     * altera a qualidade da chance que chega à finalização.
     */
    PossessionResolution resolvePossessionSequence(
        Club attackingClub,
        Club defendingClub,
        List<Player> attackers,
        List<Player> defenders,
        Player shooter,
        TacticalModifiers attackModifiers,
        TacticalModifiers defenseModifiers,
        FieldSector.Lane attackingLane,
        boolean counterAttack,
        boolean highRegain
    ) {
        FieldSectorProfile attackingShape = FieldSectorProfile.from(attackingClub);
        FieldSectorProfile defendingShape = FieldSectorProfile.from(defendingClub);
        FieldSector.Lane defendingLane = oppositeLane(attackingLane);
        Player progressor = bestSequencePlayer(
            attackers, attackingClub, attackingLane, "PROGRESS", shooter
        );
        Player builder = bestSequencePlayer(
            attackers, attackingClub, attackingLane, "BUILD", progressor, shooter
        );
        Player recoverer = bestSequencePlayer(
            attackers, attackingClub, attackingLane, "RECOVER", builder, progressor, shooter
        );
        Player presser = bestSequencePlayer(
            defenders, defendingClub, defendingLane, "RECOVER"
        );
        Player marker = bestSequencePlayer(
            defenders, defendingClub, defendingLane, "DEFEND"
        );

        recoverer = firstNonNull(recoverer, builder, progressor, shooter);
        builder = firstNonNull(builder, recoverer, progressor, shooter);
        progressor = firstNonNull(progressor, builder, recoverer, shooter);
        presser = firstNonNull(presser, marker);
        marker = firstNonNull(marker, presser);

        TechnicalAttributes recover = recoverer.getTechnicalAttributes();
        TechnicalAttributes build = builder.getTechnicalAttributes();
        TechnicalAttributes progress = progressor.getTechnicalAttributes();
        TechnicalAttributes finish = shooter.getTechnicalAttributes();
        TechnicalAttributes press = presser != null ? presser.getTechnicalAttributes() : null;
        TechnicalAttributes defend = marker != null ? marker.getTechnicalAttributes() : null;

        int pressureScore = press != null
            ? (press.getDefesa() * 2 + press.getFisico()) / 3
            : 60;
        pressureScore += Math.round((defenseModifiers.pressureSetting - 50f) * .14f);
        pressureScore += Math.round((float) ((defenseModifiers.pressingEfficiency - 1d) * 22d));
        pressureScore = conditionedSequenceScore(pressureScore, presser);

        int defensiveScore = defend != null
            ? (defend.getDefesa() * 2 + defend.getFisico()) / 3
            : 60;
        defensiveScore = conditionedSequenceScore(defensiveScore, marker);

        boolean directPlay = attackModifiers.passingSetting >= 60f;
        int passingStyleControl = directPlay
            ? -Math.round((attackModifiers.passingSetting - 50f) * .10f)
            : Math.round((50f - attackModifiers.passingSetting) * .08f);
        int tacticalPassing = passingStyleControl
            + Math.round((float) ((attackModifiers.passRetentionMultiplier - 1d) * 30d));
        int tacticalTempo = counterAttack
            ? Math.round((attackModifiers.tempoSetting - 50f) * .10f)
            : -Math.round((attackModifiers.tempoSetting - 50f) * .05f);
        int tacticalWidth = Math.round((attackModifiers.widthSetting - 50f) * .08f)
            + Math.round((float) ((attackModifiers.flankThreatMultiplier - 1d) * 22d))
            + Math.round((float) ((attackModifiers.centralCreationMultiplier - 1d) * 18d));

        float attackBuildOccupation = attackingShape.get(FieldSector.Line.DEFENSE, attackingLane)
            + attackingShape.get(FieldSector.Line.MIDFIELD, attackingLane) * .40f;
        float defensePressOccupation = defendingShape.get(FieldSector.Line.ATTACK, attackingLane)
            + defendingShape.get(FieldSector.Line.MIDFIELD, attackingLane) * .25f;
        float attackMidOccupation = attackingShape.get(FieldSector.Line.MIDFIELD, attackingLane);
        float defenseMidOccupation = defendingShape.get(FieldSector.Line.MIDFIELD, attackingLane);
        float attackFinalOccupation = attackingShape.attackingSupport(attackingLane);
        float defenseFinalOccupation = defendingShape.defensiveCoverage(attackingLane)
            * (float) defenseModifiers.defensiveCoverageMultiplier;
        // Mantemos apoio ofensivo e cobertura defensiva separados. Além de deixar
        // o duelo legível, isso impede que a formação seja tratada como um bônus
        // genérico de OVR: cada lado leva para a conta quem realmente ocupa a zona.
        int buildAttackSupport = Math.round(attackBuildOccupation * 4f);
        int buildDefensePressure = Math.round(defensePressOccupation * 4f);
        int midfieldAttackSupport = Math.round(attackMidOccupation * 5f);
        int midfieldDefenseCoverage = Math.round(defenseMidOccupation * 5f);
        int finalThirdAttackSupport = Math.round(attackFinalOccupation * 6f);
        int finalThirdDefenseCoverage = Math.round(defenseFinalOccupation * 6f);
        int mentalitySupport = (attackModifiers.playersCommittedForward - 5) * 2;

        int buildBase = directPlay
            ? (build.getPasse() + build.getFisico() + finish.getAtaque()) / 3
            : (build.getPasse() * 2 + build.getDrible()) / 3;
        int progressBase = directPlay
            ? (build.getPasse() + progress.getFisico() + finish.getAtaque()) / 3
            : (build.getPasse() + progress.getDrible() * 2) / 3;

        int[] attackScores = {
            conditionedSequenceScore(
                (recover.getDefesa() + recover.getFisico() + recover.getPasse()) / 3
                    + (highRegain ? 7 : 0) + buildAttackSupport, recoverer),
            conditionedSequenceScore(
                buildBase + tacticalPassing + buildAttackSupport, builder),
            conditionedSequenceScore(
                progressBase + tacticalTempo + midfieldAttackSupport, progressor),
            conditionedSequenceScore(
                (progress.getDrible() * 2 + progress.getFisico()) / 3
                    + (counterAttack ? 6 : 0) + finalThirdAttackSupport, progressor),
            conditionedSequenceScore(
                (progress.getPasse() + progress.getDrible()) / 2 + tacticalWidth
                    + finalThirdAttackSupport + mentalitySupport, progressor),
            conditionedSequenceScore(
                finish.getAtaque() + finalThirdAttackSupport + mentalitySupport, shooter)
        };
        int[] defenseScores = {
            pressureScore + buildDefensePressure,
            pressureScore + buildDefensePressure,
            (pressureScore + defensiveScore) / 2 + midfieldDefenseCoverage,
            defensiveScore + finalThirdDefenseCoverage,
            defensiveScore + finalThirdDefenseCoverage,
            defensiveScore + finalThirdDefenseCoverage
        };

        int[] attackBaseScores = attackScores.clone();
        int[] defenseBaseScores = defenseScores.clone();
        int[] attackVariations = new int[attackScores.length];
        int[] defenseVariations = new int[defenseScores.length];
        double[] successChances = new double[attackScores.length];
        boolean[] stageRolled = new boolean[attackScores.length];

        Player[] attackingPlayers = {
            recoverer, builder, builder, progressor, progressor, shooter
        };
        Player[] defendingPlayers = {
            presser, presser, marker, marker, marker, marker
        };
        boolean[] successful = new boolean[attackScores.length];
        int failedStage = -1;
        double edgeSum = 0d;

        // A etapa SHOT tem sua própria resolução contra zaga e goleiro logo
        // abaixo; as cinco etapas anteriores precisam ser vencidas primeiro.
        for (int index = 0; index < attackScores.length - 1; index++) {
            PhaseDuelRoll duel = rollPhaseDuel(
                attackScores[index], defenseScores[index], false
            );
            attackVariations[index] = duel.attackVariation;
            defenseVariations[index] = duel.defenseVariation;
            attackScores[index] = duel.attackScore;
            defenseScores[index] = duel.defenseScore;
            successChances[index] = duel.successChance;
            successful[index] = duel.successful;
            stageRolled[index] = true;
            double edge = clamp(duel.attackScore - duel.defenseScore, -22d, 22d);
            edgeSum += edge;
            if (!successful[index]) {
                failedStage = index;
                break;
            }
        }

        int shotStage = attackScores.length - 1;
        if (failedStage < 0) {
            PhaseDuelRoll shotDuel = rollPhaseDuel(
                attackScores[shotStage], defenseScores[shotStage], true
            );
            attackVariations[shotStage] = shotDuel.attackVariation;
            defenseVariations[shotStage] = shotDuel.defenseVariation;
            attackScores[shotStage] = shotDuel.attackScore;
            defenseScores[shotStage] = shotDuel.defenseScore;
            successChances[shotStage] = shotDuel.successChance;
            successful[shotStage] = shotDuel.successful;
            stageRolled[shotStage] = true;
            double shotEdge = clamp(
                attackScores[shotStage] - defenseScores[shotStage], -22d, 22d
            );
            edgeSum += shotEdge;
        }

        int resolvedContests = failedStage >= 0 ? failedStage + 1 : attackScores.length;
        double averageEdge = edgeSum / Math.max(1, resolvedContests);
        double qualityMultiplier = clamp(1d + averageEdge / 75d, .72d, 1.28d);
        return new PossessionResolution(
            attackScores,
            defenseScores,
            attackBaseScores,
            defenseBaseScores,
            attackVariations,
            defenseVariations,
            successChances,
            stageRolled,
            successful,
            failedStage,
            qualityMultiplier,
            attackingPlayers,
            defendingPlayers,
            attackingLane
        );
    }

    int conditionedSequenceScore(int score, Player player) {
        if (player == null) return Math.max(1, score);
        int fatigue = Math.max(0, Math.min(100, player.getFatigue()));
        int morale = Math.max(0, Math.min(100, player.getMorale()));
        int fatigueAdjustment = Math.round((fatigue - 100f) * .12f);
        int moraleAdjustment = Math.round((morale - 50f) * .08f);
        return Math.max(1, score + fatigueAdjustment + moraleAdjustment);
    }

    /**
     * A vantagem aumenta a chance de vencer a etapa, mas os limites mantêm
     * zebra e erro possíveis nos dois sentidos. Cinco duelos ainda precisam
     * ser encadeados para que a posse alcance a finalização.
     */
    static double phaseDuelSuccessChance(int attackScore, int defenseScore) {
        double edge = Math.max(-22d, Math.min(22d, attackScore - defenseScore));
        return Math.max(.62d, Math.min(.96d, .86d + edge / 200d));
    }

    static double shotDuelSuccessChance(int attackScore, int defenseScore) {
        double edge = Math.max(-22d, Math.min(22d, attackScore - defenseScore));
        return Math.max(.25d, Math.min(.82d, .55d + edge / 150d));
    }

    private PhaseDuelRoll rollPhaseDuel(
        int attackBaseScore,
        int defenseBaseScore,
        boolean shot
    ) {
        int attackVariation = random.nextInt(DUEL_VARIATION_LIMIT * 2 + 1)
            - DUEL_VARIATION_LIMIT;
        int defenseVariation = random.nextInt(DUEL_VARIATION_LIMIT * 2 + 1)
            - DUEL_VARIATION_LIMIT;
        int attackScore = attackBaseScore + attackVariation;
        int defenseScore = defenseBaseScore + defenseVariation;
        double chance = shot
            ? shotDuelSuccessChance(attackScore, defenseScore)
            : phaseDuelSuccessChance(attackScore, defenseScore);
        return new PhaseDuelRoll(
            attackScore,
            defenseScore,
            attackVariation,
            defenseVariation,
            chance,
            random.nextDouble() < chance
        );
    }

    private String narratePossessionInterruption(
        int failedStage,
        Player[] attackers,
        Player[] defenders,
        Club attacker,
        Club defender
    ) {
        int stage = Math.max(0, Math.min(failedStage, 4));
        String attackingName = playerName(attackers[stage], attacker.getName());
        String defendingName = playerName(defenders[stage], defender.getName());
        switch (stage) {
            case 0:
                return defendingName + " pressiona " + attackingName
                    + " na saída e força a perda da bola.";
            case 1:
                return attackingName + " tenta construir por dentro, mas " + defendingName
                    + " fecha a linha de passe.";
            case 2:
                return defendingName + " lê o passe de " + attackingName
                    + " no meio-campo e interrompe a progressão.";
            case 3:
                return attackingName + " parte para o duelo, mas " + defendingName
                    + " usa corpo e tempo de bola para desarmar.";
            case 4:
            default:
                return defendingName + " acompanha " + attackingName
                    + " no último terço e impede a criação da chance.";
        }
    }

    static final class PossessionResolution {
        private final int[] attackScores;
        private final int[] defenseScores;
        private final int[] attackBaseScores;
        private final int[] defenseBaseScores;
        private final int[] attackVariations;
        private final int[] defenseVariations;
        private final double[] successChances;
        private final boolean[] stageRolled;
        private final boolean[] successful;
        private final int failedStage;
        private final double qualityMultiplier;
        private final Player[] attackingPlayers;
        private final Player[] defendingPlayers;
        private final FieldSector.Lane attackingLane;

        private PossessionResolution(
            int[] attackScores,
            int[] defenseScores,
            int[] attackBaseScores,
            int[] defenseBaseScores,
            int[] attackVariations,
            int[] defenseVariations,
            double[] successChances,
            boolean[] stageRolled,
            boolean[] successful,
            int failedStage,
            double qualityMultiplier,
            Player[] attackingPlayers,
            Player[] defendingPlayers,
            FieldSector.Lane attackingLane
        ) {
            this.attackScores = attackScores;
            this.defenseScores = defenseScores;
            this.attackBaseScores = attackBaseScores;
            this.defenseBaseScores = defenseBaseScores;
            this.attackVariations = attackVariations;
            this.defenseVariations = defenseVariations;
            this.successChances = successChances;
            this.stageRolled = stageRolled;
            this.successful = successful;
            this.failedStage = failedStage;
            this.qualityMultiplier = qualityMultiplier;
            this.attackingPlayers = attackingPlayers;
            this.defendingPlayers = defendingPlayers;
            this.attackingLane = attackingLane == null
                ? FieldSector.Lane.CENTER
                : attackingLane;
        }

        boolean reachedShot() { return failedStage < 0; }
        int getFailedStage() { return failedStage; }
        double getQualityMultiplier() { return qualityMultiplier; }
        int getAttackingScore(int stage) { return attackScores[stage]; }
        int getDefendingScore(int stage) { return defenseScores[stage]; }
        int getAttackingBaseScore(int stage) { return attackBaseScores[stage]; }
        int getDefendingBaseScore(int stage) { return defenseBaseScores[stage]; }
        int getAttackingVariation(int stage) { return attackVariations[stage]; }
        int getDefendingVariation(int stage) { return defenseVariations[stage]; }
        double getSuccessChance(int stage) { return successChances[stage]; }
        boolean wasRolled(int stage) { return stageRolled[stage]; }
        boolean wasSuccessful(int stage) { return successful[stage]; }
        Player getAttackingPlayer(int stage) { return attackingPlayers[stage]; }
        Player getDefendingPlayer(int stage) { return defendingPlayers[stage]; }
    }

    private static final class PhaseDuelRoll {
        private final int attackScore;
        private final int defenseScore;
        private final int attackVariation;
        private final int defenseVariation;
        private final double successChance;
        private final boolean successful;

        private PhaseDuelRoll(
            int attackScore,
            int defenseScore,
            int attackVariation,
            int defenseVariation,
            double successChance,
            boolean successful
        ) {
            this.attackScore = attackScore;
            this.defenseScore = defenseScore;
            this.attackVariation = attackVariation;
            this.defenseVariation = defenseVariation;
            this.successChance = successChance;
            this.successful = successful;
        }
    }

    private String shotContextPrefix(Club attacker, boolean highRegain, boolean counterAttack) {
        if (highRegain) {
            return nextNarrativeVariant(NarrativeCategory.PRESSAO, 2) == 0
                ? attacker.getName() + " recupera perto da área e ataca antes da recomposição. "
                : "A pressão alta recupera a posse e deixa o caminho aberto. ";
        }
        if (counterAttack) {
            return nextNarrativeVariant(NarrativeCategory.TRANSICAO, 2) == 0
                ? attacker.getName() + " acelera contra uma defesa desorganizada. "
                : "A bola muda de dono e o contra-ataque ganha velocidade. ";
        }
        return "";
    }

    private String narrateGoal(Player shooter, Club attacker, float xG) {
        String scorer = playerName(shooter, attacker.getName());
        switch (nextNarrativeVariant(NarrativeCategory.GOL, 3)) {
            case 0:
                return "GOOOL DE " + attacker.getName().toUpperCase() + "! " + scorer
                    + (xG >= .30f ? " invade a área e bate firme no canto!" : " arrisca de longe e acerta o ângulo!");
            case 1:
                return scorer + " DECIDE! " + (xG >= .30f
                    ? "Ele aparece entre os zagueiros e conclui sem dar chance ao goleiro."
                    : "Ele encontra um espaço mínimo e coloca a bola onde o goleiro não alcança.");
            default:
                return "A rede balança! " + scorer + (xG >= .30f
                    ? " ganha o duelo dentro da área e completa a jogada de " + attacker.getName() + "."
                    : " transforma uma chance difícil em um golaço para " + attacker.getName() + ".");
        }
    }

    private String narrateBlockedRebound(Player shooter, Player goalkeeper, Club attacker) {
        String scorer = playerName(shooter, attacker.getName());
        String keeper = playerName(goalkeeper, "O goleiro");
        switch (nextNarrativeVariant(NarrativeCategory.DEFESA, 3)) {
            case 0:
                return keeper + " rebate a finalização de " + scorer
                    + ". A segunda bola fica com " + attacker.getName() + ", mas a defesa bloqueia o novo chute.";
            case 1:
                return scorer + " obriga " + keeper + " a soltar a bola. A zaga se joga na sobra e evita o rebote.";
            default:
                return keeper + " salva no primeiro chute; " + scorer
                    + " tenta aproveitar o rebote, mas encontra um defensor no caminho.";
        }
    }

    private String narrateSaveToCorner(Player shooter, Player goalkeeper) {
        String finisher = playerName(shooter, "O atacante");
        String keeper = playerName(goalkeeper, "O goleiro");
        switch (nextNarrativeVariant(NarrativeCategory.DEFESA, 3)) {
            case 0:
                return keeper + " lê a finalização de " + finisher + " e espalma para escanteio!";
            case 1:
                return finisher + " busca o canto, mas " + keeper + " voa e desvia a bola para fora.";
            default:
                return "Grande defesa de " + keeper + "! O chute de " + finisher + " tinha endereço.";
        }
    }

    private String narrateHandledShot(Player shooter, Player goalkeeper) {
        String finisher = playerName(shooter, "O atacante");
        String keeper = playerName(goalkeeper, "O goleiro");
        switch (nextNarrativeVariant(NarrativeCategory.DEFESA, 3)) {
            case 0:
                return finisher + " finaliza firme, mas " + keeper + " encaixa sem dar rebote.";
            case 1:
                return keeper + " fecha o ângulo e segura a tentativa de " + finisher + ".";
            default:
                return finisher + " tenta surpreender de primeira. " + keeper
                    + " estava bem colocado e controla a bola.";
        }
    }

    private String narrateMissedShot(Player shooter, Club attacker) {
        String finisher = playerName(shooter, attacker.getName());
        switch (nextNarrativeVariant(NarrativeCategory.CHANCE, 3)) {
            case 0:
                return finisher + " busca o canto, mas abre demais o pé e manda para fora.";
            case 1:
                return finisher + " recebe em condição de finalizar; o chute sobe além do travessão.";
            default:
                return finisher + " arrisca antes da marcação chegar, mas não acerta a meta.";
        }
    }

    private MatchEvent createSequencedShotEvent(
        int minute,
        String description,
        String type,
        String outcome,
        boolean homeAttacking,
        Club attacker,
        Club defender,
        List<Player> starters,
        List<Player> defenders,
        Player shooter,
        Player goalkeeper,
        TacticalModifiers modifiers,
        boolean counterAttack,
        boolean highRegain,
        float xG,
        PossessionResolution resolution
    ) {
        MatchEvent event = new MatchEvent(minute, description, type, homeAttacking);
        if ("GOL".equals(type)) {
            event.withNarrativeCategory(NarrativeCategory.GOL);
        } else if (outcome != null && (outcome.contains("defesa")
            || outcome.contains("goleiro") || outcome.contains("bloqueado"))) {
            event.withNarrativeCategory(NarrativeCategory.DEFESA);
        } else {
            event.withNarrativeCategory(NarrativeCategory.CHANCE);
        }
        PossessionSequence sequence = buildPossessionSequence(
            minute, event, attacker, defender, starters, defenders, shooter, goalkeeper, modifiers,
            counterAttack, highRegain, outcome, xG, resolution
        );
        return event.withPossessionSequence(sequence);
    }

    /** Monta a representação cronológica das fases já resolvidas pelo motor. */
    PossessionSequence buildPossessionSequence(
        int minute,
        MatchEvent event,
        Club attacker,
        Club defender,
        List<Player> starters,
        List<Player> defenders,
        Player shooter,
        Player goalkeeper,
        TacticalModifiers modifiers,
        boolean counterAttack,
        boolean highRegain,
        String outcome,
        float xG
    ) {
        return buildPossessionSequence(
            minute, event, attacker, defender, starters, defenders, shooter, goalkeeper,
            modifiers, counterAttack, highRegain, outcome, xG, null
        );
    }

    PossessionSequence buildPossessionSequence(
        int minute,
        MatchEvent event,
        Club attacker,
        Club defender,
        List<Player> starters,
        List<Player> defenders,
        Player shooter,
        Player goalkeeper,
        TacticalModifiers modifiers,
        boolean counterAttack,
        boolean highRegain,
        String outcome,
        float xG,
        PossessionResolution resolution
    ) {
        if (resolution != null && resolution.attackingPlayers[5] != null) {
            shooter = resolution.attackingPlayers[5];
        }
        FieldSector.Lane lane = resolution != null
            ? resolution.attackingLane
            : FieldSector.Lane.CENTER;
        FieldSector.Lane defendingLane = oppositeLane(lane);
        Player progressor = resolution != null
            ? resolution.attackingPlayers[3]
            : bestSequencePlayer(starters, attacker, lane, "PROGRESS", shooter);
        Player builder = resolution != null
            ? resolution.attackingPlayers[1]
            : bestSequencePlayer(starters, attacker, lane, "BUILD", progressor, shooter);
        Player recoverer = resolution != null
            ? resolution.attackingPlayers[0]
            : bestSequencePlayer(starters, attacker, lane, "RECOVER", builder, progressor, shooter);
        Player marker = resolution != null
            ? resolution.defendingPlayers[3]
            : bestSequencePlayer(defenders, defender, defendingLane, "DEFEND");

        recoverer = firstNonNull(recoverer, builder, progressor, shooter);
        builder = firstNonNull(builder, recoverer, progressor, shooter);
        progressor = firstNonNull(progressor, builder, recoverer, shooter);

        int recovererSlot = findTacticalSlot(attacker, recoverer, 4);
        int builderSlot = findTacticalSlot(attacker, builder, 6);
        int progressorSlot = findTacticalSlot(attacker, progressor, 8);
        int shooterSlot = findTacticalSlot(attacker, shooter, 9);
        int markerSlot = findTacticalSlot(defender, marker, 3);
        String corridor = laneDescription(lane);
        boolean directPlay = modifiers.passingSetting >= 60f;
        FieldSector defensiveSector = FieldSector.of(FieldSector.Line.DEFENSE, lane);
        FieldSector midfieldSector = FieldSector.of(FieldSector.Line.MIDFIELD, lane);
        FieldSector attackingSector = FieldSector.of(FieldSector.Line.ATTACK, lane);
        FieldSector.Lane shooterLane = laneOfPosition(tacticalPositionOf(attacker, shooter));
        FieldSector finishingSector = FieldSector.of(FieldSector.Line.ATTACK, shooterLane);
        boolean wideAttack = lane != FieldSector.Lane.CENTER;
        int visualVariant = random.nextInt(3);
        boolean diagonalSwitch = wideAttack
            && laneOfPosition(tacticalPositionOf(attacker, recoverer)) != lane
            && visualVariant == 2;
        PossessionSequence.VisualCue recoveryCue = highRegain
            ? PossessionSequence.VisualCue.QUICK_RECOVERY
            : PossessionSequence.VisualCue.CONTROL;
        PossessionSequence.VisualCue constructionCue = diagonalSwitch
            ? PossessionSequence.VisualCue.SWITCH_PLAY
            : directPlay
                ? PossessionSequence.VisualCue.LONG_PASS
                : visualVariant == 0
                    ? PossessionSequence.VisualCue.ONE_TWO
                    : PossessionSequence.VisualCue.SHORT_PASS;
        PossessionSequence.VisualCue progressionCue = directPlay
            ? PossessionSequence.VisualCue.SECOND_BALL
            : counterAttack || modifiers.tempoSetting >= 68f
                ? PossessionSequence.VisualCue.THROUGH_BALL
                : visualVariant == 1
                    ? PossessionSequence.VisualCue.SHORT_PASS
                    : PossessionSequence.VisualCue.ONE_TWO;
        PossessionSequence.VisualCue finalThirdCue = wideAttack && visualVariant != 1
            ? PossessionSequence.VisualCue.OVERLAP
            : PossessionSequence.VisualCue.DRIBBLE;
        PossessionSequence.VisualCue creationCue = wideAttack
            ? visualVariant == 0
                ? PossessionSequence.VisualCue.CUTBACK
                : PossessionSequence.VisualCue.CROSS
            : visualVariant == 2
                ? PossessionSequence.VisualCue.THROUGH_BALL
                : PossessionSequence.VisualCue.ONE_TWO;
        PossessionSequence.VisualCue shotCue = counterAttack || highRegain
            ? PossessionSequence.VisualCue.FIRST_TIME_SHOT
            : xG >= .24f || visualVariant == 1
                ? PossessionSequence.VisualCue.POWER_SHOT
                : PossessionSequence.VisualCue.PLACED_SHOT;

        int beat = Math.max(2, Math.min(6, Math.round(6f - modifiers.tempoSetting / 25f)));
        int second = 4 + random.nextInt(9);
        List<PossessionSequence.Step> steps = new ArrayList<>();

        String recoveryDescription;
        if (highRegain) {
            recoveryDescription = narratePressureSuccess(recoverer, marker, attacker, defender);
            steps.add(sequenceDuelStep(second, PossessionSequence.Phase.RECUPERACAO,
                PossessionSequence.Action.RECOVER, recoveryDescription,
                recovererSlot, recovererSlot, marker, markerSlot)
                .withSectors(defensiveSector, defensiveSector)
                .withVisualCue(recoveryCue)
                .withNarrativeCategory(NarrativeCategory.PRESSAO));
        } else if (counterAttack) {
            recoveryDescription = playerName(recoverer, attacker.getName()) + " toma a bola de "
                + playerName(marker, defender.getName()) + " e inicia a transição.";
            steps.add(sequenceDuelStep(second, PossessionSequence.Phase.RECUPERACAO,
                PossessionSequence.Action.RECOVER, recoveryDescription,
                recovererSlot, recovererSlot, marker, markerSlot)
                .withSectors(defensiveSector, defensiveSector)
                .withVisualCue(recoveryCue)
                .withNarrativeCategory(NarrativeCategory.TRANSICAO));
        } else {
            recoveryDescription = playerName(recoverer, attacker.getName())
                + " recolhe a sobra e levanta a cabeça para organizar o ataque.";
            steps.add(sequenceStep(second, PossessionSequence.Phase.RECUPERACAO,
                PossessionSequence.Action.RECOVER, recoveryDescription,
                recovererSlot, recovererSlot)
                .withSectors(defensiveSector, defensiveSector)
                .withVisualCue(recoveryCue));
        }

        second = nextSequenceSecond(second, beat);
        steps.add(sequenceStep(second, PossessionSequence.Phase.CONSTRUCAO,
            PossessionSequence.Action.PASS,
            diagonalSwitch
                ? playerName(recoverer, attacker.getName()) + " inverte o jogo e encontra "
                    + playerName(progressor, attacker.getName()) + " livre no " + corridor + "."
                : directPlay
                ? playerName(recoverer, attacker.getName()) + " procura "
                    + playerName(progressor, attacker.getName()) + " com passe longo pelo " + corridor + "."
                : playerName(recoverer, attacker.getName()) + " → "
                    + playerName(builder, attacker.getName()) + " pelo " + corridor + ".",
            recovererSlot, diagonalSwitch || directPlay ? progressorSlot : builderSlot)
            .withSectors(defensiveSector, midfieldSector)
            .withVisualCue(constructionCue));

        second = nextSequenceSecond(second, beat);
        steps.add(sequenceStep(second, PossessionSequence.Phase.PROGRESSAO,
            directPlay ? PossessionSequence.Action.CARRY : PossessionSequence.Action.PASS,
            directPlay
                ? playerName(progressor, attacker.getName())
                    + " disputa a segunda bola e tenta acelerar a progressão."
                : playerName(builder, attacker.getName()) + " rompe a primeira linha e encontra "
                    + playerName(progressor, attacker.getName()) + ".",
            directPlay ? progressorSlot : builderSlot, progressorSlot)
            .withSectors(midfieldSector, midfieldSector)
            .withVisualCue(progressionCue));

        second = nextSequenceSecond(second, beat);
        steps.add(sequenceDuelStep(second, PossessionSequence.Phase.ULTIMO_TERCO,
            PossessionSequence.Action.CARRY,
            playerName(progressor, attacker.getName()) + " encara "
                + playerName(marker, defender.getName()) + " e conduz pelo " + corridor + ".",
            progressorSlot, progressorSlot, marker, markerSlot)
            .withSectors(midfieldSector, attackingSector)
            .withVisualCue(finalThirdCue));

        second = nextSequenceSecond(second, beat);
        steps.add(sequenceStep(second, PossessionSequence.Phase.CRIACAO,
            PossessionSequence.Action.PASS,
            creationDescription(creationCue, progressor, shooter, attacker, corridor),
            progressorSlot, shooterSlot)
            .withSectors(attackingSector, finishingSector)
            .withVisualCue(creationCue)
            .withNarrativeCategory(NarrativeCategory.CHANCE));

        second = nextSequenceSecond(second, Math.max(2, beat - 1));
        steps.add(sequenceDuelStep(second, PossessionSequence.Phase.FINALIZACAO,
            PossessionSequence.Action.SHOT,
            playerName(shooter, attacker.getName()) + " ganha meio metro de "
                + playerName(marker, defender.getName()) + " e FINALIZA!",
            shooterSlot, shooterSlot, marker, markerSlot)
            .withSectors(finishingSector, finishingSector)
            .withVisualCue(shotCue)
            .withNarrativeCategory(NarrativeCategory.CHANCE));

        if (!"GOL".equals(event.type)) {
            second = nextSequenceSecond(second, 2);
            steps.add(sequenceStep(second, PossessionSequence.Phase.RESULTADO,
                PossessionSequence.Action.RESULT, event.description, shooterSlot, shooterSlot)
                .withSectors(finishingSector, finishingSector)
                .withVisualCue(PossessionSequence.VisualCue.HOLD)
                .withNarrativeCategory(event.getNarrativeCategory()));
        }

        if (resolution != null) {
            int resolvedSteps = Math.min(6, steps.size());
            for (int index = 0; index < resolvedSteps; index++) {
                steps.get(index).withResolution(
                    resolution.attackScores[index],
                    resolution.defenseScores[index],
                    resolution.successful[index]
                );
            }

            if (!resolution.reachedShot()) {
                int failedIndex = Math.max(0, Math.min(resolution.failedStage, steps.size() - 1));
                PossessionSequence.Step failed = steps.get(failedIndex);
                PossessionSequence.Step interruption = new PossessionSequence.Step(
                    failed.getSecond(),
                    failed.getPhase(),
                    PossessionSequence.Action.RESULT,
                    event.description,
                    failed.getFromSlot(),
                    failed.getToSlot(),
                    failed.getOpponentName(),
                    failed.getOpponentSlot()
                ).withNarrativeCategory(NarrativeCategory.ERRO)
                    .withSectors(failed.getFromSector(), failed.getToSector())
                    .withVisualCue(failed.getVisualCue())
                    .withResolution(
                        resolution.attackScores[failedIndex],
                        resolution.defenseScores[failedIndex],
                        false
                    );

                List<PossessionSequence.Step> interrupted = new ArrayList<>(
                    steps.subList(0, failedIndex + 1)
                );
                interrupted.set(failedIndex, interruption);
                steps = interrupted;
            }

            if (steps.size() > 6) {
                int goalkeeperScore = goalkeeper != null
                    ? goalkeeper.getTechnicalAttributes().getGoleiro()
                    : 60;
                steps.get(6).withResolution(
                    shooter.getTechnicalAttributes().getAtaque(),
                    goalkeeperScore,
                    "GOL".equals(event.type)
                );
            }
        }

        int completedPasses = 0;
        for (int index = 0; index < steps.size(); index++) {
            PossessionSequence.Step step = steps.get(index);
            if (step.getAction() == PossessionSequence.Action.PASS
                && step.wasResolvedSuccessfully()) {
                completedPasses++;
            }
            step.withProgress(index, completedPasses);
        }

        List<String> involved = new ArrayList<>();
        addPlayerName(involved, recoverer);
        addPlayerName(involved, builder);
        addPlayerName(involved, progressor);
        addPlayerName(involved, shooter);
        addPlayerName(involved, marker);
        addPlayerName(involved, goalkeeper);

        return new PossessionSequence(
            attacker.getName(), event.isHomeTeam,
            highRegain ? "campo ofensivo" : "campo defensivo",
            counterAttack ? "contra-ataque" : "ataque posicional",
            resolution != null ? resolution.attackingLane : FieldSector.Lane.CENTER,
            outcome, completedPasses, xG, involved, steps
        );
    }

    private PossessionSequence.Step sequenceStep(
        int second,
        PossessionSequence.Phase phase,
        PossessionSequence.Action action,
        String description,
        int fromSlot,
        int toSlot
    ) {
        return new PossessionSequence.Step(second, phase, action, description, fromSlot, toSlot);
    }

    private String laneDescription(FieldSector.Lane lane) {
        if (lane == FieldSector.Lane.LEFT) return "corredor esquerdo";
        if (lane == FieldSector.Lane.RIGHT) return "corredor direito";
        return "corredor central";
    }

    private String creationDescription(
        PossessionSequence.VisualCue cue,
        Player creator,
        Player shooter,
        Club attacker,
        String corridor
    ) {
        String creatorName = playerName(creator, attacker.getName());
        String shooterName = playerName(shooter, attacker.getName());
        switch (cue) {
            case CROSS:
                return creatorName + " chega pelo " + corridor + " e cruza para "
                    + shooterName + ".";
            case CUTBACK:
                return creatorName + " alcança o fundo e toca para trás. "
                    + shooterName + " aparece de frente para o gol.";
            case THROUGH_BALL:
                return creatorName + " enfia a bola entre os defensores para "
                    + shooterName + ".";
            case ONE_TWO:
                return creatorName + " tabela em velocidade e devolve para "
                    + shooterName + " atacar o espaço.";
            default:
                return creatorName + " encontra " + shooterName
                    + " no espaço criado pelo " + corridor + ".";
        }
    }

    private PossessionSequence.Step sequenceDuelStep(
        int second,
        PossessionSequence.Phase phase,
        PossessionSequence.Action action,
        String description,
        int fromSlot,
        int toSlot,
        Player opponent,
        int opponentSlot
    ) {
        return new PossessionSequence.Step(
            second, phase, action, description, fromSlot, toSlot,
            opponent == null ? null : opponent.getName(), opponentSlot
        );
    }

    private int nextSequenceSecond(int current, int beat) {
        return Math.min(58, current + beat + random.nextInt(2));
    }

    private Player bestSequencePlayer(List<Player> starters, String role, Player... excluded) {
        Player best = null;
        int bestScore = Integer.MIN_VALUE;
        if (starters == null) return null;

        for (Player player : starters) {
            if (player == null || !player.canPlay() || isExcluded(player, excluded)
                || "GK".equalsIgnoreCase(player.getPosition())) continue;

            TechnicalAttributes a = player.getTechnicalAttributes();
            int score;
            if ("RECOVER".equals(role)) {
                score = a.getDefesa() * 3 + a.getFisico() + a.getPasse();
            } else if ("DEFEND".equals(role)) {
                score = a.getDefesa() * 4 + a.getFisico() * 2 + a.getPasse();
            } else if ("WIDE".equals(role)) {
                String position = player.getPosition();
                int wideBonus = position != null && position.matches("(?i)LW|RW|LM|RM|LB|RB|LWB|RWB")
                    ? 120 : 0;
                score = a.getDrible() * 3 + a.getPasse() * 2 + a.getFisico() + wideBonus;
            } else if ("BUILD".equals(role)) {
                score = a.getPasse() * 3 + a.getDefesa() + a.getDrible();
            } else if ("PROGRESS".equals(role)) {
                score = a.getDrible() * 2 + a.getFisico() + a.getPasse() * 2;
            } else {
                score = a.getPasse() * 3 + a.getDrible() * 2 + a.getAtaque();
            }

            if (score > bestScore) {
                best = player;
                bestScore = score;
            }
        }
        return best;
    }

    Player bestSequencePlayer(
        List<Player> starters,
        Club club,
        FieldSector.Lane lane,
        String role,
        Player... excluded
    ) {
        Player best = null;
        int bestScore = Integer.MIN_VALUE;
        if (starters == null) return null;

        for (Player player : starters) {
            if (player == null || !player.canPlay() || isExcluded(player, excluded)
                || player.getPrimaryPosition().isGoalkeeper()) continue;

            int score = sequenceRoleScore(player, role)
                + strategicPositionBonus(tacticalPositionOf(club, player), lane, role);
            if (score > bestScore) {
                best = player;
                bestScore = score;
            }
        }
        return best;
    }

    private int sequenceRoleScore(Player player, String role) {
        TechnicalAttributes a = player.getTechnicalAttributes();
        if ("RECOVER".equals(role)) {
            return a.getDefesa() * 3 + a.getFisico() + a.getPasse();
        }
        if ("DEFEND".equals(role)) {
            return a.getDefesa() * 4 + a.getFisico() * 2 + a.getPasse();
        }
        if ("BUILD".equals(role)) {
            return a.getPasse() * 3 + a.getDefesa() + a.getDrible();
        }
        if ("PROGRESS".equals(role)) {
            return a.getDrible() * 2 + a.getFisico() + a.getPasse() * 2;
        }
        return a.getAtaque() * 4 + a.getDrible() * 2 + a.getPasse() + a.getFisico();
    }

    /** Bônus de adequação da função ao corredor efetivamente escolhido. */
    static int strategicPositionBonus(
        String rawPosition,
        FieldSector.Lane lane,
        String role
    ) {
        String position = rawPosition == null ? "CM" : rawPosition.toUpperCase();
        FieldSector.Lane desiredLane = lane == null ? FieldSector.Lane.CENTER : lane;
        boolean left = position.matches("LB|LWB|LM|LW");
        boolean right = position.matches("RB|RWB|RM|RW");
        boolean central = position.matches("CB|CDM|CM|CAM|CF|ST");
        boolean buildupRole = "RECOVER".equals(role) || "BUILD".equals(role)
            || "DEFEND".equals(role);

        if (desiredLane == FieldSector.Lane.CENTER) {
            if (!central) return -20;
            if (position.matches("CB|CDM")) return buildupRole ? 170 : 75;
            if (position.matches("CM|CAM")) return buildupRole ? 145 : 180;
            return buildupRole ? 45 : 185;
        }

        boolean correctSide = desiredLane == FieldSector.Lane.LEFT ? left : right;
        boolean oppositeSide = desiredLane == FieldSector.Lane.LEFT ? right : left;
        if (correctSide) {
            boolean fullback = position.matches("LB|LWB|RB|RWB");
            return buildupRole
                ? (fullback ? 180 : 135)
                : (fullback ? 140 : 190);
        }
        if (oppositeSide) return -35;
        return central ? 20 : 0;
    }

    private FieldSector.Lane oppositeLane(FieldSector.Lane lane) {
        if (lane == FieldSector.Lane.LEFT) return FieldSector.Lane.RIGHT;
        if (lane == FieldSector.Lane.RIGHT) return FieldSector.Lane.LEFT;
        return FieldSector.Lane.CENTER;
    }

    private boolean isExcluded(Player player, Player... excluded) {
        if (excluded == null) return false;
        for (Player value : excluded) if (player == value) return true;
        return false;
    }

    private Player firstNonNull(Player... players) {
        if (players != null) {
            for (Player player : players) if (player != null) return player;
        }
        return null;
    }

    private int findTacticalSlot(Club club, Player player, int fallback) {
        if (club != null && player != null) {
            for (Map.Entry<Integer, Player> entry : club.getTacticsMap().entrySet()) {
                if (entry.getValue() == player && entry.getKey() != null) {
                    return Math.max(0, Math.min(10, entry.getKey()));
                }
            }
        }
        return Math.max(0, Math.min(10, fallback));
    }

    private String playerName(Player player, String fallback) {
        return player != null ? player.getName() : fallback;
    }

    private void addPlayerName(List<String> names, Player player) {
        if (player != null && !names.contains(player.getName())) names.add(player.getName());
    }

    private MatchEvent processFoulSequence(Match match, int minute, boolean homeAttacking, Club attacker, Club defender,
                                           List<Player> attStarters, List<Player> defStarters, TacticalModifiers defMods) {

        List<Player> availableDefenders = defStarters.stream()
            .filter(p -> p.getMatchRedCards() == 0 && !p.isInjured())
            .collect(Collectors.toList());

        if (availableDefenders.isEmpty()) return null;

        Player foulCommitter = availableDefenders.get(random.nextInt(availableDefenders.size()));
        boolean isDefenderHome = !homeAttacking;

        if (isDefenderHome) match.addHomeFoul(); else match.addAwayFoul();

        double fatiguePenalty = (100.0 - foulCommitter.getFatigue()) / 100.0;
        double severityRoll = (random.nextDouble() + (fatiguePenalty * 0.30)) * defMods.cardRiskMultiplier;

        if (random.nextDouble() < 0.22) {
            Player taker = getBestAvailableAttacker(attStarters);

            if (taker == null) {
                return new MatchEvent(
                    minute,
                    "Falta perigosa para o " + attacker.getName() + ", mas a cobrança não leva perigo.",
                    "TIRO_LIVRE",
                    homeAttacking
                );
            }

            boolean isGoalFromFreeKick = random.nextDouble() <
                (0.035 + (taker.getTechnicalAttributes().getAtaque() / 1200.0));

            if (isGoalFromFreeKick) {
                if (homeAttacking) match.setHomeGoals(match.getHomeGoals() + 1);
                else match.setAwayGoals(match.getAwayGoals() + 1);

                match.addGoalScorer(taker);
                taker.addGoal();

                return new MatchEvent(
                    minute,
                    "GOLAÇO DE FALTA! " + taker.getName() + " (" + attacker.getName() + ") cobra com perfeição por cima da barreira!",
                    "GOL",
                    homeAttacking
                ).withGoalDetails(taker, null, taker.getSeasonGoals(), getWflCareerGoals(taker));
            } else {
                return new MatchEvent(
                    minute,
                    "Falta perigosa! " + taker.getName() + " cobra com efeito e a bola tira tinta do travessão!",
                    "TIRO_LIVRE",
                    homeAttacking
                );
            }
        }

        if (severityRoll > 1.20) {
            foulCommitter.addRedCard();
            match.registerPlayerExit(foulCommitter, minute);
            defender.removeUnavailablePlayersFromStartingXI();
            match.addCard(foulCommitter, "Vermelho");
            if (isDefenderHome) match.addHomeRedCard(); else match.addAwayRedCard();

            return new MatchEvent(
                minute,
                "CARTÃO VERMELHO DIRETO! " + foulCommitter.getName() + " (" + defender.getName() + ") entra de forma desproporcional e é EXPULSO!",
                "CARTAO",
                isDefenderHome
            );
        }

        if (foulCommitter.getYellowCards() >= 1 && severityRoll >= 0.50) {
            foulCommitter.addYellowCard();
            match.registerPlayerExit(foulCommitter, minute);
            defender.removeUnavailablePlayersFromStartingXI();
            match.addCard(foulCommitter, "Vermelho");
            if (isDefenderHome) match.addHomeRedCard(); else match.addAwayRedCard();

            return new MatchEvent(
                minute,
                "SEGUNDO AMARELO! " + foulCommitter.getName() + " (" + defender.getName() + ") chega atrasado, comete a falta e recebe o CARTÃO VERMELHO!",
                "CARTAO",
                isDefenderHome
            );
        }

        if (foulCommitter.getYellowCards() == 0 && severityRoll >= 0.40) {
            foulCommitter.addYellowCard();
            match.addCard(foulCommitter, "Amarelo");

            return new MatchEvent(
                minute,
                "Cartão Amarelo para " + foulCommitter.getName() + " (" + defender.getName() + ") por parar o ataque com falta.",
                "CARTAO",
                isDefenderHome
            );
        }

        return new MatchEvent(
            minute,
            "Falta tática cometida por " + foulCommitter.getName() + " (" + defender.getName() + ") no meio de campo.",
            "FALTA",
            isDefenderHome
        );
    }

    public void finalizeMatch(Match match) {

        Club home =
            match.getHomeTeam();

        Club away =
            match.getAwayTeam();

        // ==============================
        // SUSPENSÕES / LESÕES
        // ==============================

        for (Player p : home.getSquad()) {

            if (
                p.getMatchRedCards() == 0 &&
                    p.isSuspended()
            ) {
                p.decreaseSuspension();
            }

            p.resetMatchStats();
        }

        for (Player p : away.getSquad()) {

            if (
                p.getMatchRedCards() == 0 &&
                    p.isSuspended()
            ) {
                p.decreaseSuspension();
            }

            p.resetMatchStats();
        }

        // ==============================
        // FADIGA
        // ==============================

        match.finishPlayerMinuteTracking();

        // ==============================
        // RESULTADO
        // ==============================

        int homeGoals =
            match.getHomeGoals();

        int awayGoals =
            match.getAwayGoals();

        if (match.isPlayoffs() && homeGoals == awayGoals && !match.hasPenaltyShootout()) {
            simulatePenaltyShootout(match);
        }

        registerSeasonPerformance(
            home,
            homeGoals,
            awayGoals,
            match
        );

        registerSeasonPerformance(
            away,
            awayGoals,
            homeGoals,
            match
        );

        // Recordes de clube precisam sobreviver à virada de temporada e às
        // transferências. Registra os participantes e eventos desta partida
        // antes de qualquer fluxo posterior poder alterar o elenco.
        home.recordPlayerMatchStatistics(match);
        away.recordPlayerMatchStatistics(match);

        match.setResult(
            homeGoals,
            awayGoals
        );

        match.setPlayed(true);

        if (!match.isGateRevenueRecorded()) {
            long grossGateRevenue = (long) match.getAttendance() * match.getAverageTicketPrice();
            long gateRevenue = Math.round(grossGateRevenue * ClubFinance.CLUB_GATE_REVENUE_SHARE);
            home.getFinance().recordGateRevenue(gateRevenue);
            match.recordGateRevenue(gateRevenue);
        }

        // O desgaste pertence ao estádio que recebeu a partida. Como toda
        // simulação termina neste ponto, jogos do usuário e da IA seguem a
        // mesma regra sem risco de esquecer algum fluxo de calendário.
        home.recordHomeMatchStadiumWear();

        // ==============================
        // ESTATÍSTICAS HISTÓRICAS CLUBES
        // ==============================

        /*
         * Isso alimenta:
         *
         * jogos
         * vitórias
         * derrotas
         * empates
         * gols feitos
         * gols sofridos
         * maior vitória
         * invencibilidade
         */

        home.recordMatchResult(
            homeGoals,
            awayGoals
        );

        away.recordMatchResult(
            awayGoals,
            homeGoals
        );

        // ==============================
        // MORAL
        // ==============================

        match.applyPostMatchMorale();
        Club winner = match.getWinningClub();

        // ==============================
        // PREMIAÇÕES WFL
        // ==============================

        if (winner != null) {

            // Vitória normal.
            winner
                .getFinance()
                .addPrizeMoney(
                    ClubFinance.PRIZE_MATCH_WIN
                );

            // Vitória em playoffs.
            if (
                match.isPlayoffs()
            ) {

                winner
                    .getFinance()
                    .addPrizeMoney(
                        ClubFinance.PRIZE_PLAYOFFS_QUAL
                    );
            }

            // Vitória na Final.
            if (
                match.isFinalMatch()
            ) {

                winner
                    .getFinance()
                    .addPrizeMoney(
                        ClubFinance.PRIZE_FINAL_QUAL
                    );

                winner
                    .getFinance()
                    .addPrizeMoney(
                        ClubFinance.PRIZE_CHAMPION
                    );
            }
        }
        match.restorePrematchTactics();
    }

    /** Resolve obrigatoriamente todo empate eliminatório em uma disputa de pênaltis. */
    private void simulatePenaltyShootout(Match match) {
        int homePenalties = 0;
        int awayPenalties = 0;

        double homeChance = penaltyConversionChance(match.getHomeTeam());
        double awayChance = penaltyConversionChance(match.getAwayTeam());

        for (int kick = 0; kick < 5; kick++) {
            if (random.nextDouble() < homeChance) homePenalties++;
            if (random.nextDouble() < awayChance) awayPenalties++;
        }

        int suddenDeathRounds = 0;
        while (homePenalties == awayPenalties && suddenDeathRounds < 12) {
            boolean homeScored = random.nextDouble() < homeChance;
            boolean awayScored = random.nextDouble() < awayChance;
            if (homeScored) homePenalties++;
            if (awayScored) awayPenalties++;
            suddenDeathRounds++;
        }

        // Proteção extrema contra uma sequência indefinida de cobranças iguais.
        if (homePenalties == awayPenalties) {
            if (random.nextBoolean()) homePenalties++;
            else awayPenalties++;
        }

        match.setPenaltyShootout(homePenalties, awayPenalties);
    }

    private double penaltyConversionChance(Club club) {
        if (club == null || club.getStartingXI().isEmpty()) return .72d;
        double attackingQuality = club.getStartingXI().stream()
            .filter(player -> player != null && player.canPlay())
            .mapToInt(player -> player.getTechnicalAttributes().getAtaque())
            .average()
            .orElse(65d);
        return clamp(.68d + (attackingQuality - 55d) * .0025d, .66d, .84d);
    }

    private void resetInMatchFatigueAccumulators(Club club) {
        if (club == null) return;
        for (Player player : club.getSquad()) {
            player.resetInMatchFatigueAccumulator();
        }
    }

    private double physicalFatigueResistance(Player player) {
        if (player == null || player.getTechnicalAttributes() == null) return 1d;
        int physical = player.getTechnicalAttributes().getFisico();
        return clamp(1.15d - (physical - 50d) * .006d, .78d, 1.22d);
    }

    private void applyMinuteFatigue(
        Club club,
        List<Player> starters,
        double tacticalLoad
    ) {
        if (club == null || starters == null) return;
        double staffAdjustedLoad = tacticalLoad * StaffImpact.matchFatigueMultiplier(
            club.getStaffLevel(StaffRole.FITNESS_COACH)
        );
        for (Player player : starters) {
            if (player == null) continue;
            double fullMatchLoss = player.getPrimaryPosition().isGoalkeeper() ? 10d : 34.5d;
            player.applyInMatchFatigue(
                fullMatchLoss * staffAdjustedLoad * physicalFatigueResistance(player) / 90d
            );
        }
    }

    private void registerSeasonPerformance(
        Club club,
        int goalsScored,
        int goalsConceded,
        Match match
    ) {

        for (java.util.Map.Entry<Player, Integer> participant :
            match.getPlayerMinutesForClub(club).entrySet()) {

            Player player = participant.getKey();
            int minutes = participant.getValue();

            player.addSeasonAppearance();
            player.addSeasonMinutes(minutes);

            if (
                goalsConceded == 0 &&
                    minutes >= 60 &&
                    player.getPosition() != null &&
                    player.getPosition()
                        .matches(
                            "GK|CB|LB|RB|LWB|RWB"
                        )
            ) {

                player.addCleanSheet();
            }

            player.addSeasonRating(
                calculateSeasonMatchRating(
                    player,
                    goalsScored,
                    goalsConceded,
                    match
                )
            );
        }
    }

    private double calculateSeasonMatchRating(
        Player player,
        int goalsScored,
        int goalsConceded,
        Match match
    ) {
        double rating = 6.0;
        if (goalsScored > goalsConceded) rating += 0.5;
        if (goalsScored < goalsConceded) rating -= 0.3;

        long goals = match.getGoalScorers().stream().filter(player::equals).count();
        long assists = match.getAssisters().stream().filter(player::equals).count();
        rating += goals * 1.4 + assists * 0.8;

        String position = player.getPosition();
        boolean goalkeeper = "GK".equalsIgnoreCase(position);
        boolean defender = position != null && position.matches("CB|LB|RB|LWB|RWB");
        if (goalsConceded == 0 && goalkeeper) rating += 1.2;
        else if (goalsConceded == 0 && defender) rating += 0.6;
        else if (goalkeeper) rating -= goalsConceded * 0.4;
        else if (defender) rating -= goalsConceded * 0.2;

        String card = match.getCards().get(player);
        if ("Amarelo".equalsIgnoreCase(card) || "YELLOW".equalsIgnoreCase(card)) rating -= 0.6;
        if ("Vermelho".equalsIgnoreCase(card) || "RED".equalsIgnoreCase(card)) rating -= 2.2;
        return Math.max(1.0, Math.min(10.0, rating));
    }

    private double calculateSectorPower(List<Player> starters, String sectorAttr) {
        return starters.stream()
            .filter(p -> p.getMatchRedCards() == 0 && !p.isInjured())
            .mapToDouble(p -> {
                TechnicalAttributes t = p.getTechnicalAttributes();
                int baseVal = t != null ? t.getOrDefault(sectorAttr, 50) : 50;
                double fatigueImpact = 0.70 + (0.30 * (p.getFatigue() / 100.0));
                return baseVal * fatigueImpact;
            })
            .average().orElse(50.0);
    }

    private double calculateLineupCondition(List<Player> starters) {
        if (starters == null || starters.isEmpty()) {
            return 0.75;
        }

        return starters.stream()
            .filter(player -> player.getMatchRedCards() == 0 && !player.isInjured())
            .mapToDouble(player -> {
                double fitness = 0.68 + (0.32 * player.getFatigue() / 100.0);
                double morale = 0.90 + (0.10 * player.getMorale() / 100.0);
                return fitness * morale;
            })
            .average()
            .orElse(0.75);
    }

    private Player getBestAvailableAttacker(List<Player> starters) {
        if (starters == null || starters.isEmpty()) {
            return null;
        }

        List<Player> attackers = starters.stream()
            .filter(p -> p != null && p.getMatchRedCards() == 0 && !p.isInjured() && p.getPosition() != null && p.getPosition().matches("(?i)ST|CF|RW|LW|CAM"))
            .collect(Collectors.toList());

        if (attackers.isEmpty()) {
            List<Player> nonRed = starters.stream()
                .filter(p -> p != null && p.getMatchRedCards() == 0 && !p.isInjured())
                .collect(Collectors.toList());
            return nonRed.isEmpty() ? null : nonRed.get(random.nextInt(nonRed.size()));
        }
        return attackers.get(random.nextInt(attackers.size()));
    }

    private Player getBestAvailableAttacker(
        List<Player> starters,
        Club club,
        FieldSector.Lane desiredLane
    ) {
        Player best = null;
        int bestScore = Integer.MIN_VALUE;
        if (starters == null) return null;

        for (Player player : starters) {
            if (player == null || player.getMatchRedCards() > 0 || player.isInjured()
                || player.getPrimaryPosition().isGoalkeeper()) continue;

            TechnicalAttributes attributes = player.getTechnicalAttributes();
            String tacticalPosition = tacticalPositionOf(club, player);
            int laneBonus = strategicPositionBonus(tacticalPosition, desiredLane, "FINISH");
            int score = attributes.getAtaque() * 4
                + attributes.getDrible() * 2
                + attributes.getPasse()
                + attributes.getFisico()
                + laneBonus;
            if (score > bestScore) {
                bestScore = score;
                best = player;
            }
        }
        return best;
    }

    private String tacticalPositionOf(Club club, Player player) {
        if (club != null && club.getFormation() != null && player != null) {
            List<String> slots = club.getFormation().getPositionSlots();
            for (Map.Entry<Integer, Player> entry : club.getTacticsMap().entrySet()) {
                Integer index = entry.getKey();
                if (entry.getValue() == player && index != null && index >= 0 && index < slots.size()) {
                    return slots.get(index);
                }
            }
        }
        return player != null ? player.getPosition() : "CM";
    }

    private FieldSector.Lane laneOfPosition(String position) {
        if (position != null && position.matches("(?i)LB|LWB|LM|LW")) {
            return FieldSector.Lane.LEFT;
        }
        if (position != null && position.matches("(?i)RB|RWB|RM|RW")) {
            return FieldSector.Lane.RIGHT;
        }
        return FieldSector.Lane.CENTER;
    }

    private Player getPasserPlayer(List<Player> starters) {
        List<Player> midfielders = starters.stream()
            .filter(p -> p.getMatchRedCards() == 0 && !p.isInjured() && p.getPosition().matches("(?i)CM|CDM|CAM|LM|RM"))
            .collect(Collectors.toList());

        if (midfielders.isEmpty()) {
            List<Player> nonRed = starters.stream().filter(p -> p.getMatchRedCards() == 0 && !p.isInjured()).collect(Collectors.toList());
            return nonRed.isEmpty() ? null : nonRed.get(random.nextInt(nonRed.size()));
        }
        return midfielders.get(random.nextInt(midfielders.size()));
    }

    private Player getGoalkeeper(List<Player> starters) {
        return starters.stream()
            .filter(p -> p.getPosition().equalsIgnoreCase("GK") && p.getMatchRedCards() == 0 && !p.isInjured())
            .findFirst()
            .orElse(null);
    }

    private Player getRandomAssister(List<Player> starters, Player scorer) {
        List<Player> passers = starters.stream()
            .filter(p -> p != scorer && p.getMatchRedCards() == 0 && !p.isInjured() && p.getPosition().matches("(?i)CM|CAM|LM|RM|RB|LB|LWB|RWB"))
            .collect(Collectors.toList());

        if (passers.isEmpty()) return null;
        return passers.get(random.nextInt(passers.size()));
    }

    private double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
