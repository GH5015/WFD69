package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.ClubFinance;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.TechnicalAttributes;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MatchEngine {
    private final Random random = new Random();

    public void simulate(Match match) {
        prepareMatchLineups(match);

        for (int min = 1; min <= 90; min++) {
            simulateMinute(match, min);
        }
        finalizeMatch(match);
    }

    /** Atualiza a formação ideal dos clubes da IA antes de cada partida. */
    public void prepareMatchLineups(Match match) {
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

        if (club.isUserControlled()) {
            club.removeUnavailablePlayersFromStartingXI();
        } else {
            club.autoSelectBestFormationAndXI();
        }
    }

    public MatchEvent simulateMinute(Match match, int minute) {
        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        List<Player> hStarters = home.getStartingXI();
        List<Player> aStarters = away.getStartingXI();

        // 1. Modificadores Táticos calculados via TacticalEngine
        TacticalModifiers hMods = TacticalEngine.calculateModifiers(
            home.getTempo(), home.getMentalityValue(), home.getPassing(), home.getWidth(), home.getPressure()
        );
        TacticalModifiers aMods = TacticalEngine.calculateModifiers(
            away.getTempo(), away.getMentalityValue(), away.getPassing(), away.getWidth(), away.getPressure()
        );

        // 2. Processamento de Lesões (frequência aumentada)
        MatchEvent homeInjury = processInjuryCheck(hStarters, home, minute, hMods, true);
        if (homeInjury != null) return homeInjury;

        MatchEvent awayInjury = processInjuryCheck(aStarters, away, minute, aMods, false);
        if (awayInjury != null) return awayInjury;

        // 3. Determinação de Posse de Bola delegada à PossessionEngine
        Club possessionClub = PossessionEngine.determinePossession(home, hMods, away, aMods);
        boolean homeAttacking = possessionClub.equals(home);

        // Atualização do Momentum e da Posse no objeto Match
        double hMidPower = calculateSectorPower(hStarters, "passe") * 1.08 * hMods.possessionMultiplier;
        double aMidPower = calculateSectorPower(aStarters, "passe") * aMods.possessionMultiplier;
        double totalMid = Math.max(1, hMidPower + aMidPower);

        float momentum = (float) (hMidPower / totalMid) + (random.nextFloat() * 0.2f - 0.10f);
        match.setMomentum(Math.max(0.05f, Math.min(0.95f, momentum)));
        match.setPossession((int) (match.getHomeMomentum() * 100));

        // Filtro para frequência de eventos narrativos
        if (random.nextDouble() > 0.85) return null;

        Club attacker = homeAttacking ? home : away;
        Club defender = homeAttacking ? away : home;

        TacticalModifiers attMods = homeAttacking ? hMods : aMods;
        TacticalModifiers defMods = homeAttacking ? aMods : hMods;

        List<Player> attStarters = homeAttacking ? hStarters : aStarters;
        List<Player> defStarters = homeAttacking ? aStarters : hStarters;

        // 4. Cálculo da Força Ofensiva e Defensiva delegados aos sub-motores
        boolean isCounterAttack = random.nextDouble() < (0.20 * attMods.counterAttackMultiplier);
        double atkPower = AttackEngine.calculateAttackPower(attacker, attMods, isCounterAttack);
        double defPower = DefenseEngine.calculateDefensePower(defender, defMods);

        /* Fadiga, moral e mando de campo influenciam todos os lances. */
        atkPower *= calculateLineupCondition(attStarters);
        defPower *= calculateLineupCondition(defStarters);
        if (homeAttacking) {
            atkPower *= 1.04;
        } else {
            defPower *= 1.04;
        }

        double actionRoll = random.nextDouble();

        // --- MÓDULO A: FINALIZAÇÕES E GOLS ---
        if (actionRoll < 0.35) {
            double overallDiff = attacker.getOverall() - defender.getOverall();
            double shotTriggerChance = 0.40 + (overallDiff > 0 ? (overallDiff / 80.0) : 0);

            if (random.nextDouble() < shotTriggerChance) {
                return processShotSequence(match, minute, homeAttacking, attacker, defender, attStarters, defStarters, atkPower, defPower, attMods);
            }
        }

        // --- MÓDULO B: DISPUTAS FÍSICAS, FALTAS E CARTÕES ---
        if (actionRoll >= 0.35 && actionRoll < 0.65) {
            String disciplineOutcome = DefenseEngine.checkFoulOrCard(defMods);
            if (!disciplineOutcome.equals("NENHUM") || random.nextDouble() < 0.30) {
                return processFoulSequence(match, minute, homeAttacking, attacker, defender, attStarters, defStarters, defMods);
            }
        }

        // --- MÓDULO C: ESCANTEIOS E CRUZAMENTOS ---
        if (actionRoll >= 0.65 && actionRoll < 0.80) {
            if (AttackEngine.isCrossingPlay(attMods)) {
                if (homeAttacking) match.addHomeCorner(); else match.addAwayCorner();

                return new MatchEvent(
                    minute,
                    "Cruzamento perigoso cortado pela zaga! Escanteio para o " + attacker.getName() + ".",
                    "ESCANTEIO",
                    homeAttacking
                );
            }
        }

        // --- MÓDULO D: CONSTRUÇÃO DE JOGADA E RETENÇÃO DE POSSE ---
        Player passer = getPasserPlayer(attStarters);
        String passerName = passer != null ? passer.getName() : "O meio-campo";

        if (random.nextDouble() < 0.50) {
            return new MatchEvent(
                minute,
                attacker.getName() + " troca passes no campo de ataque (" + attMods.detectedStyle + ").",
                "POSSE",
                homeAttacking
            );
        } else {
            return new MatchEvent(
                minute,
                passerName + " (" + attacker.getName() + ") trabalha a bola na intermediária organizando a equipe.",
                "CONSTRUCAO",
                homeAttacking
            );
        }
    }

    private MatchEvent processInjuryCheck(List<Player> starters, Club club, int minute, TacticalModifiers mods, boolean isHomeTeam) {
        if (random.nextDouble() > 0.015) return null;

        List<Player> activeStarters = starters.stream()
            .filter(p -> p.getMatchRedCards() == 0 && !p.isInjured())
            .collect(Collectors.toList());

        if (activeStarters.isEmpty()) return null;

        Player victim = activeStarters.get(random.nextInt(activeStarters.size()));

        double fatigueRisk = (100.0 - victim.getFatigue()) / 100.0;
        double totalRisk = fatigueRisk * mods.fatigueMultiplier;

        if (random.nextDouble() < totalRisk || random.nextDouble() < 0.35) {
            int gamesOut = 1 + random.nextInt(5);
            victim.setInjuryDuration(gamesOut);
            club.removeUnavailablePlayersFromStartingXI();

            return new MatchEvent(
                minute,
                "LESÃO! " + victim.getName() + " (" + club.getName() + ") sente dores musculares e precisa deixar o gramado! (Fora por " + gamesOut + " jogos)",
                "LESIONADO",
                isHomeTeam
            );
        }

        return null;
    }

    private MatchEvent processShotSequence(Match match, int minute, boolean homeAttacking, Club attacker, Club defender,
                                           List<Player> attStarters, List<Player> defStarters,
                                           double atkPower, double defPower, TacticalModifiers attMods) {

        Player shooter = getBestAvailableAttacker(attStarters);
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

        double atkOverall = attacker.getOverall();
        double defOverall = defender.getOverall();
        double overallDiff = atkOverall - defOverall;

        double disparityMultiplier = 1.0;
        if (overallDiff > 0) {
            disparityMultiplier += Math.pow(overallDiff / 8.0, 1.35) * 0.18;
        } else {
            disparityMultiplier = Math.max(0.15, 1.0 - (Math.abs(overallDiff) / 20.0));
        }

        double shotQuality = (atkPower / Math.max(1, atkPower + defPower)) * (shooterAtkAttr / 100.0) * disparityMultiplier;
        float xGValue = (float) Math.min(0.85, Math.max(0.04, shotQuality * (0.30 + random.nextDouble() * 0.30)));

        if (homeAttacking) {
            match.addHomeShot(true);
            match.addHomeXG(xGValue);
        } else {
            match.addAwayShot(true);
            match.addAwayXG(xGValue);
        }

        boolean onTarget = random.nextDouble() < (shotQuality * 0.50 + 0.30);

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

                Player assister = getRandomAssister(attStarters, shooter);
                if (assister != null && random.nextDouble() < 0.65) {
                    match.addAssister(assister);
                    assister.addAssist();
                }

                String typeShot = (xGValue > 0.3f) ? " numa bomba de dentro da área!" : " de fora da área no ângulo!";
                return new MatchEvent(
                    minute,
                    "GOOOOOL DO " + attacker.getName().toUpperCase() + "! " + shooter.getName() + typeShot,
                    "GOL",
                    homeAttacking
                );
            } else {
                if (random.nextDouble() < 0.35) {
                    if (homeAttacking) match.addHomeCorner(); else match.addAwayCorner();
                    return new MatchEvent(
                        minute,
                        "DEFESAÇA! " + (goalkeeper != null ? goalkeeper.getName() : "O goleiro") + " espalma o chute de " + shooter.getName() + " para escanteio!",
                        "ESCANTEIO",
                        homeAttacking
                    );
                }
                return new MatchEvent(
                    minute,
                    shooter.getName() + " finaliza forte no meio do gol, mas " + (goalkeeper != null ? goalkeeper.getName() : "o goleiro") + " encaixa sem dar rebote.",
                    "CHUTE",
                    homeAttacking
                );
            }
        }

        return new MatchEvent(
            minute,
            shooter.getName() + " (" + attacker.getName() + ") arrisca a finalização, mas a bola sai longe da meta.",
            "CHUTE",
            homeAttacking
        );
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
                );
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

            if (
                p.isInjured() &&
                    p.getInjuryDuration() > 0 &&
                    !p.wasInjuredInCurrentMatch()
            ) {
                p.decreaseInjury();
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

            if (
                p.isInjured() &&
                    p.getInjuryDuration() > 0 &&
                    !p.wasInjuredInCurrentMatch()
            ) {
                p.decreaseInjury();
            }

            p.resetMatchStats();
        }

        // ==============================
        // FADIGA
        // ==============================

        for (
            Player p :
            home.getStartingXI()
        ) {
            p.applyMatchFatigue();
        }

        for (
            Player p :
            away.getStartingXI()
        ) {
            p.applyMatchFatigue();
        }

        // ==============================
        // RESULTADO
        // ==============================

        int homeGoals =
            match.getHomeGoals();

        int awayGoals =
            match.getAwayGoals();

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

        match.setResult(
            homeGoals,
            awayGoals
        );

        match.setPlayed(true);

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

        double homeOvr =
            home.getOverall();

        double awayOvr =
            away.getOverall();

        Club winner = null;

        if (
            homeGoals >
                awayGoals
        ) {

            home.updateSquadMorale(
                1,
                awayOvr
            );

            away.updateSquadMorale(
                -1,
                homeOvr
            );

            winner = home;

        } else if (
            awayGoals >
                homeGoals
        ) {

            home.updateSquadMorale(
                -1,
                awayOvr
            );

            away.updateSquadMorale(
                1,
                homeOvr
            );

            winner = away;

        } else {

            home.updateSquadMorale(
                0,
                awayOvr
            );

            away.updateSquadMorale(
                0,
                homeOvr
            );
        }

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
    }

    private void registerSeasonPerformance(
        Club club,
        int goalsScored,
        int goalsConceded,
        Match match
    ) {

        for (
            Player player :
            club.getStartingXI()
        ) {

            player.addSeasonAppearance();

            if (
                goalsConceded == 0 &&
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
}
