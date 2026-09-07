package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.FieldSector;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.MatchPhase;
import io.github.some_example_name.model.NarrativeCategory;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.PossessionSequence;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.utils.MatchNarrator;

import java.util.ArrayList;
import java.util.List;

public final class PossessionSequenceRegressionTest {
    public static void main(String[] args) {
        Club club = team("Santos Atlântico", 88);
        Club defender = team("Milano Calcio", 84);
        List<Player> starters = club.getStartingXI();
        List<Player> defenders = defender.getStartingXI();
        Player shooter = club.getTacticsMap().get(9);
        Player goalkeeper = defender.getTacticsMap().get(0);
        TacticalModifiers modifiers = TacticalEngine.calculateModifiers(82f, 65f, 76f, 80f, 78f);
        MatchEvent event = new MatchEvent(
            21,
            shooter.getName() + " finaliza, mas o goleiro faz a defesa.",
            "CHUTE",
            true
        );

        PossessionSequence sequence = new MatchEngine().buildPossessionSequence(
            21, event, club, defender, starters, defenders, shooter, goalkeeper, modifiers,
            false, false, "defesa do goleiro", 0.18f
        );

        require(sequence != null, "Finalização não gerou uma sequência de posse");
        require(sequence.getXG() == 0.18f, "xG não foi preservado");
        require(sequence.getPlayers().size() >= 3, "Poucos jogadores participam da jogada");
        require(sequence.getPasses() == 2,
            "Passe longo não reduziu a quantidade de passes da progressão direta");
        require("ataque posicional".equals(sequence.getAttackType()), "Tipo de ataque incorreto");
        require("campo defensivo".equals(sequence.getStartZone()), "Zona inicial incorreta");

        PossessionSequence.Phase[] expected = {
            PossessionSequence.Phase.RECUPERACAO,
            PossessionSequence.Phase.CONSTRUCAO,
            PossessionSequence.Phase.PROGRESSAO,
            PossessionSequence.Phase.ULTIMO_TERCO,
            PossessionSequence.Phase.CRIACAO,
            PossessionSequence.Phase.FINALIZACAO
        };
        int previousSecond = -1;
        List<PossessionSequence.Step> steps = sequence.getSteps();
        require(steps.size() >= expected.length, "Sequência incompleta");
        require(steps.size() <= PossessionSequence.MAX_RELEVANT_STEPS,
            "A posse exibiu passes demais em vez de resumir os momentos relevantes");
        for (int index = 0; index < expected.length; index++) {
            PossessionSequence.Step step = steps.get(index);
            require(step.getPhase() == expected[index], "Fase fora da ordem cronológica");
            require(step.getSecond() > previousSecond && step.getSecond() <= 59,
                "Instantes da jogada inválidos");
            require(!step.getDescription().isEmpty(), "Passo sem narração");
            require(step.getVisualCue() != null,
                "Passo da posse sem instrução visual definida pelo motor");
            previousSecond = step.getSecond();
        }
        require(steps.get(1).getVisualCue() == PossessionSequence.VisualCue.LONG_PASS,
            "Passe direto não produziu lançamento na animação");
        require(steps.get(2).getVisualCue() == PossessionSequence.VisualCue.SECOND_BALL,
            "Jogo direto não representou a disputa pela segunda bola");
        require(steps.get(0).getMatchPhase() == MatchPhase.DEFENSIVE_BUILDUP,
            "Recuperação não iniciou a fase defensiva da posse");
        require(steps.get(2).getMatchPhase() == MatchPhase.MIDFIELD,
            "Terceiro passo não foi identificado como fase de meio-campo");
        require(steps.get(5).getMatchPhase() == MatchPhase.SHOT,
            "Finalização não alcançou a fase SHOT");
        require(steps.get(0).getFromSector() == FieldSector.DEF_CENTER,
            "A posse não começou na zona defensiva simulada");
        require(steps.get(1).getFromSector() == FieldSector.DEF_CENTER
                && steps.get(1).getToSector() == FieldSector.MID_CENTER,
            "A construção não preservou sua transição entre setores");
        require(steps.get(3).getFromSector() == FieldSector.MID_CENTER
                && steps.get(3).getToSector() == FieldSector.ATT_CENTER,
            "O último terço não preservou as zonas usadas pela visualização");
        steps.get(1).withResolution(86, 75, true);
        require(steps.get(1).getAttackingScore() == 86
                && steps.get(1).getDefendingScore() == 75
                && steps.get(1).wasResolvedSuccessfully(),
            "A pequena resolução matemática da fase não foi preservada");

        PossessionSequence.Step duel = steps.get(3);
        require(duel.getOpponentName() != null && !duel.getOpponentName().isEmpty(),
            "Duelo no último terço não identifica o marcador");
        require(duel.getOpponentSlot() >= 0, "Duelo não aponta o marcador no campo");
        require(duel.getDescription().contains(duel.getOpponentName()),
            "Narração não cita o adversário do duelo");
        require(sequence.getPlayers().contains(duel.getOpponentName()),
            "Marcador não foi incluído entre os personagens do ataque");

        TacticalModifiers shortPassing = TacticalEngine.calculateModifiers(
            45f, 55f, 25f, 45f, 55f
        );
        PossessionSequence supportedBuild = new MatchEngine().buildPossessionSequence(
            22, event, club, defender, starters, defenders, shooter, goalkeeper, shortPassing,
            false, false, "defesa do goleiro", .16f
        );
        require(supportedBuild.getPasses() == 3,
            "Passe curto não produziu uma construção mais apoiada");
        require(supportedBuild.getSteps().get(1).getVisualCue()
                    == PossessionSequence.VisualCue.SHORT_PASS
                || supportedBuild.getSteps().get(1).getVisualCue()
                    == PossessionSequence.VisualCue.ONE_TWO,
            "Construção apoiada não gerou passe curto ou tabela");

        require(event.getNarrativeCategory() == NarrativeCategory.CHANCE,
            "Finalização não recebeu a categoria CHANCE");
        MatchEvent pressureEvent = new MatchEvent(30, "Pressão contextual.", "CONSTRUCAO", true)
            .withNarrativeCategory(NarrativeCategory.PRESSAO);
        String formatted = MatchNarrator.generateCommentary(pressureEvent, null);
        require(formatted.contains("PRESSÃO") && formatted.contains("Pressão contextual."),
            "Categoria editorial não aparece junto do contexto do lance");
        require(new MatchEvent(31, "Lesão contextual.", "LESIONADO", true)
                .getNarrativeCategory() == NarrativeCategory.LESAO,
            "Lesão não foi categorizada corretamente");
        require(new MatchEvent(32, "Defesa contextual.", "DEFESA", false)
                .getNarrativeCategory() == NarrativeCategory.DEFESA,
            "Defesa não foi categorizada corretamente");

        Player assister = club.getTacticsMap().get(10);
        MatchEvent milestoneGoal = new MatchEvent(37, "Gol contextual.", "GOL", true)
            .withGoalDetails(shooter, assister, 6, 100);
        require(milestoneGoal.getGoalScorer() == shooter,
            "Celebração não preservou o autor do gol");
        require(milestoneGoal.getGoalAssister() == assister,
            "Celebração não preservou a assistência do gol");
        require(milestoneGoal.getScorerSeasonGoals() == 6,
            "Celebração não preservou os gols da temporada");
        require(milestoneGoal.hasCareerGoalMilestone(),
            "100º gol na WFL não foi reconhecido como marco");
        require(!new MatchEvent(38, "Gol comum.", "GOL", true)
                .withGoalDetails(shooter, null, 7, 101).hasCareerGoalMilestone(),
            "Gol comum foi marcado incorretamente como recorde");

        MatchEvent injury = new MatchEvent(
            71,
            shooter.getName() + " cai no gramado. Não parece conseguir continuar.",
            "LESIONADO",
            true
        ).withInjuryDetails(
            shooter,
            "LESÃO MUSCULAR SUSPEITA",
            "DISTENSÃO MUSCULAR",
            14
        );
        require(!injury.description.contains("DISTENSÃO") && !injury.description.contains("14"),
            "O diagnóstico vazou para a narração durante a partida");
        require("LESÃO MUSCULAR SUSPEITA".equals(injury.getSuspectedInjury()),
            "A condição preliminar da lesão não foi preservada");
        require(injury.hasInjuryDiagnosis()
                && "DISTENSÃO MUSCULAR".equals(injury.getDiagnosedInjury())
                && injury.getDiagnosedDaysOut() == 14,
            "O laudo pós-partida não preservou diagnóstico e prazo");

        PossessionSequence transition = new MatchEngine().buildPossessionSequence(
            33, event, club, defender, starters, defenders, shooter, goalkeeper, modifiers,
            true, true, "defesa do goleiro", 0.22f
        );
        require("contra-ataque".equals(transition.getAttackType()), "Transição não identificada");
        require("campo ofensivo".equals(transition.getStartZone()), "Recuperação alta sem zona correta");
        PossessionSequence.Step pressureRecovery = transition.getSteps().get(0);
        require(pressureRecovery.getOpponentName() != null,
            "Pressão alta não identifica quem perdeu a bola");
        require(pressureRecovery.getDescription().contains(pressureRecovery.getOpponentName()),
            "Pressão alta não explica o confronto que gerou a recuperação");
        require(pressureRecovery.getNarrativeCategory() == NarrativeCategory.PRESSAO,
            "Recuperação alta não recebeu a categoria PRESSÃO dentro da sequência");
        require(transition.getSteps().get(5).getNarrativeCategory() == NarrativeCategory.CHANCE,
            "Finalização da sequência não recebeu a categoria CHANCE");
        require(transition.getSteps().get(0).getVisualCue()
                    == PossessionSequence.VisualCue.QUICK_RECOVERY
                && transition.getSteps().get(5).getVisualCue()
                    == PossessionSequence.VisualCue.FIRST_TIME_SHOT,
            "Transição após pressão não ganhou recuperação e chute rápidos");

        TacticalModifiers defendingModifiers = TacticalEngine.calculateModifiers(
            60f, 50f, 55f, 48f, 76f
        );
        MatchEngine statefulEngine = new MatchEngine();
        require(MatchEngine.strategicPositionBonus("RW", FieldSector.Lane.RIGHT, "PROGRESS")
                > MatchEngine.strategicPositionBonus("CM", FieldSector.Lane.RIGHT, "PROGRESS"),
            "Jogo pela direita não priorizou RW/RM/RB/RWB");
        require(MatchEngine.strategicPositionBonus("LW", FieldSector.Lane.LEFT, "PROGRESS")
                > MatchEngine.strategicPositionBonus("RW", FieldSector.Lane.LEFT, "PROGRESS"),
            "Jogo pela esquerda não diferenciou os dois corredores");
        require(MatchEngine.strategicPositionBonus("CAM", FieldSector.Lane.CENTER, "PROGRESS")
                > MatchEngine.strategicPositionBonus("LW", FieldSector.Lane.CENTER, "PROGRESS"),
            "Jogo central não priorizou CM/CAM/CF/ST");

        Player rightProgressor = statefulEngine.bestSequencePlayer(
            starters, club, FieldSector.Lane.RIGHT, "PROGRESS", shooter
        );
        Player centralProgressor = statefulEngine.bestSequencePlayer(
            starters, club, FieldSector.Lane.CENTER, "PROGRESS", shooter
        );
        require(rightProgressor == club.getTacticsMap().get(10),
            "A posse pela direita não escolheu o atleta escalado como RW");
        require(centralProgressor == club.getTacticsMap().get(5)
                || centralProgressor == club.getTacticsMap().get(6)
                || centralProgressor == club.getTacticsMap().get(7),
            "A posse central não escolheu um atleta escalado no meio central");

        MatchEngine.PossessionResolution rightResolution = null;
        for (int attempt = 0; attempt < 100; attempt++) {
            MatchEngine.PossessionResolution candidate = statefulEngine.resolvePossessionSequence(
                club, defender, starters, defenders, shooter, modifiers, defendingModifiers,
                FieldSector.Lane.RIGHT, false, false
            );
            if (candidate.reachedShot()) {
                rightResolution = candidate;
                break;
            }
        }
        require(rightResolution != null,
            "Não foi possível validar uma posse completa pelo corredor direito");
        require(rightResolution.getAttackingPlayer(3) == club.getTacticsMap().get(10),
            "O atleta da progressão não corresponde ao RW escolhido pelo motor");
        for (int stage = 0; stage < MatchEngine.POSSESSION_DUEL_STAGE_COUNT; stage++) {
            require(rightResolution.wasRolled(stage),
                "Uma etapa da posse completa não recebeu seu sorteio independente");
        }
        PossessionSequence rightSequence = statefulEngine.buildPossessionSequence(
            41, event, club, defender, starters, defenders, shooter, goalkeeper, modifiers,
            false, false, "defesa do goleiro", .18f, rightResolution
        );
        PossessionSequence.Step rightAdvance = rightSequence.getSteps().get(3);
        require(rightAdvance.getFromSlot() == 10
                && rightAdvance.getFromSector() == FieldSector.MID_RIGHT
                && rightAdvance.getToSector() == FieldSector.ATT_RIGHT,
            "A animação não recebeu o jogador e as zonas da posse simulada");
        require(rightAdvance.getVisualCue() == PossessionSequence.VisualCue.OVERLAP
                || rightAdvance.getVisualCue() == PossessionSequence.VisualCue.DRIBBLE,
            "Progressão lateral não recebeu drible ou ultrapassagem visual");
        PossessionSequence.VisualCue rightCreation = rightSequence.getSteps().get(4).getVisualCue();
        require(rightCreation == PossessionSequence.VisualCue.CROSS
                || rightCreation == PossessionSequence.VisualCue.CUTBACK,
            "Ataque pelo corredor não terminou em cruzamento ou passe para trás");
        require(rightSequence.getPlayers().contains(rightProgressor.getName()),
            "O personagem escolhido pelo motor não chegou à sequência visual");

        double favorableDuel = MatchEngine.phaseDuelSuccessChance(83, 78);
        double unfavorableDuel = MatchEngine.phaseDuelSuccessChance(78, 83);
        require(favorableDuel > unfavorableDuel,
            "Vantagem no duelo não aumentou a probabilidade de sucesso");
        require(favorableDuel < 1d && unfavorableDuel > 0d,
            "O duelo virou resultado automático em vez de probabilidade");
        double favorableShot = MatchEngine.shotDuelSuccessChance(83, 78);
        double unfavorableShot = MatchEngine.shotDuelSuccessChance(78, 83);
        require(favorableShot > unfavorableShot
                && favorableShot < 1d && unfavorableShot > 0d,
            "A finalização não preservou vantagem probabilística e possibilidade de erro");

        int originalMorale = shooter.getMorale();
        shooter.setMorale(100);
        int highMoraleScore = statefulEngine.conditionedSequenceScore(80, shooter);
        shooter.setMorale(0);
        int lowMoraleScore = statefulEngine.conditionedSequenceScore(80, shooter);
        shooter.setMorale(originalMorale);
        require(highMoraleScore > lowMoraleScore,
            "Moral individual não influenciou a pontuação do duelo");

        boolean sawInterruptedAttack = false;
        boolean sawShotReached = false;
        for (int attempt = 0; attempt < 400; attempt++) {
            MatchEngine.PossessionResolution resolution = statefulEngine.resolvePossessionSequence(
                club, defender, starters, defenders, shooter, modifiers, defendingModifiers,
                FieldSector.Lane.CENTER, false, false
            );
            require(resolution.getQualityMultiplier() >= .72d
                    && resolution.getQualityMultiplier() <= 1.28d,
                "Qualidade acumulada da posse saiu dos limites");
            require(resolution.getAttackingScore(1) > 0
                    && resolution.getDefendingScore(1) > 0,
                "Construção não resolveu PAS e pressão adversária");
            int lastResolvedStage = resolution.reachedShot()
                ? 4
                : resolution.getFailedStage();
            for (int stage = 0; stage <= lastResolvedStage; stage++) {
                require(resolution.wasRolled(stage),
                    "Etapa alcançada sem resolução aleatória própria");
                require(resolution.getAttackingScore(stage)
                        == resolution.getAttackingBaseScore(stage)
                            + resolution.getAttackingVariation(stage),
                    "Pontuação ofensiva não separou base e variação");
                require(resolution.getDefendingScore(stage)
                        == resolution.getDefendingBaseScore(stage)
                            + resolution.getDefendingVariation(stage),
                    "Pontuação defensiva não separou base e variação");
                require(Math.abs(resolution.getAttackingVariation(stage)) <= 4
                        && Math.abs(resolution.getDefendingVariation(stage)) <= 4,
                    "Variação aleatória deixou de ser pequena");
                require(resolution.getSuccessChance(stage) >= .62d
                        && resolution.getSuccessChance(stage) <= .96d,
                    "Probabilidade do duelo saiu dos limites não determinísticos");
            }
            if (resolution.reachedShot()) {
                require(resolution.wasRolled(5),
                    "Chute alcançado sem sorteio próprio");
                require(resolution.getAttackingScore(5)
                        == resolution.getAttackingBaseScore(5)
                            + resolution.getAttackingVariation(5)
                        && resolution.getDefendingScore(5)
                        == resolution.getDefendingBaseScore(5)
                            + resolution.getDefendingVariation(5),
                    "Finalização não separou base e variação do duelo");
                require(Math.abs(resolution.getAttackingVariation(5)) <= 4
                        && Math.abs(resolution.getDefendingVariation(5)) <= 4,
                    "Variação aleatória da finalização deixou de ser pequena");
                require(resolution.getSuccessChance(5) >= .25d
                        && resolution.getSuccessChance(5) <= .82d,
                    "Finalização saiu dos limites não determinísticos");
            }
            sawShotReached |= resolution.reachedShot();
            sawInterruptedAttack |= !resolution.reachedShot()
                && resolution.getFailedStage() >= 0
                && resolution.getFailedStage() < 5;
        }
        require(sawInterruptedAttack,
            "A máquina de fases nunca interrompeu uma posse antes do chute");
        require(sawShotReached,
            "A máquina de fases nunca permitiu que uma posse chegasse ao chute");

        FieldSectorProfile wideShape = FieldSectorProfile.from(Formation.F_433);
        FieldSectorProfile narrowDiamond = FieldSectorProfile.from(Formation.F_41212_2);
        require(wideShape.get(FieldSector.Line.ATTACK, FieldSector.Lane.LEFT) > 0f
                && wideShape.get(FieldSector.Line.ATTACK, FieldSector.Lane.RIGHT) > 0f,
            "O 4-3-3 não ocupou os dois corredores de ataque");
        require(narrowDiamond.get(FieldSector.Line.ATTACK, FieldSector.Lane.LEFT) == 0f
                && narrowDiamond.get(FieldSector.Line.ATTACK, FieldSector.Lane.RIGHT) == 0f,
            "O losango estreito ganhou amplitude ofensiva inexistente");
        float[] narrowPlay = wideShape.laneProbabilities(25f);
        float[] widePlay = wideShape.laneProbabilities(80f);
        require(narrowPlay[1] > .50f && narrowPlay[1] > narrowPlay[0],
            "Amplitude baixa não priorizou o corredor central");
        require(widePlay[0] + widePlay[2] > widePlay[1],
            "Amplitude alta não priorizou os corredores laterais");
        float[] diamondWidePlay = narrowDiamond.laneProbabilities(80f);
        require(diamondWidePlay[1] > widePlay[1],
            "A formação estreita não preservou sua preferência central");

        TacticalModifiers slowTempo = TacticalEngine.calculateModifiers(25f, 50f, 35f, 50f, 50f);
        TacticalModifiers fastTempo = TacticalEngine.calculateModifiers(85f, 50f, 35f, 50f, 50f);
        require(fastTempo.eventFrequencyMultiplier > slowTempo.eventFrequencyMultiplier
                && fastTempo.turnoverRiskMultiplier > slowTempo.turnoverRiskMultiplier
                && fastTempo.fatigueMultiplier > slowTempo.fatigueMultiplier,
            "Ritmo não alterou decisões, perdas e desgaste estruturalmente");
        TacticalModifiers defensiveMindset = TacticalEngine.calculateModifiers(50f, 20f, 50f, 50f, 50f);
        TacticalModifiers attackingMindset = TacticalEngine.calculateModifiers(50f, 85f, 50f, 50f, 50f);
        require(attackingMindset.playersCommittedForward > defensiveMindset.playersCommittedForward
                && attackingMindset.boxPresenceMultiplier > defensiveMindset.boxPresenceMultiplier
                && attackingMindset.defensiveCoverageMultiplier < defensiveMindset.defensiveCoverageMultiplier,
            "Mentalidade não alterou apoio, presença e cobertura");

        System.out.println("Match narrative: categories, injuries, goal celebration, pressure, duels and xG OK.");
    }

    private static Club team(String name, int quality) {
        Club club = new Club(name, "Brasil", "Ocidental", quality, 40_000_000,
            "Arena", "santos.png");
        club.setUserControlled(true);
        club.setFormation(Formation.F_433);

        Position[] positions = {
            Position.GK, Position.LB, Position.CB, Position.CB, Position.RB,
            Position.CDM, Position.CM, Position.CAM, Position.LW, Position.ST, Position.RW
        };
        for (int index = 0; index < 23; index++) {
            Position position = index < positions.length ? positions[index] : Position.CM;
            int attack = position == Position.ST ? quality + 4 : quality;
            Player player = new Player(
                name + " Jogador " + index, "Brasil", position, null, 24,
                new TechnicalAttributes(attack, quality, quality, quality, quality, quality),
                Math.min(99, quality + 5), 10_000
            );
            player.transferTo(club);
            if (index < 11) club.getTacticsMap().put(index, player);
        }
        club.setStartingXI(new ArrayList<>(club.getTacticsMap().values()));
        return club;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
