package io.github.some_example_name.engine;

public class TacticalEngine {

    public static TacticalModifiers calculateModifiers(float tempo, float mentality, float passing, float width, float pressure) {
        TacticalModifiers mod = new TacticalModifiers();

        tempo = clampValue(tempo);
        mentality = clampValue(mentality);
        passing = clampValue(passing);
        width = clampValue(width);
        pressure = clampValue(pressure);
        mod.tempoSetting = tempo;
        mod.mentalitySetting = mentality;
        mod.passingSetting = passing;
        mod.widthSetting = width;
        mod.pressureSetting = pressure;

        // Centralizando a variação em relação ao ponto neutro (50)
        double dTempo = (tempo - 50) / 50.0;          // -1.0 a +1.0
        double dMentality = (mentality - 50) / 50.0;  // -1.0 a +1.0
        double dPassing = (passing - 50) / 50.0;      // -1.0 a +1.0 (Curto -> Longo)
        double dWidth = (width - 50) / 50.0;          // -1.0 a +1.0
        double dPressure = (pressure - 50) / 50.0;    // -1.0 a +1.0

        // -----------------------------------------------------------------
        // CÁLCULO DOS MULTIPLICADORES BASE
        // -----------------------------------------------------------------

        // Ataque: ganha com Tempo alto, Mentalidade Ofensiva e Amplitude
        mod.attackMultiplier = 1.0 + (dTempo * 0.25) + (dMentality * 0.35) + (dWidth * 0.10);

        // Defesa: perde com Mentalidade Ofensiva e Amplitude exagerada; ganha com Recuo
        mod.defenseMultiplier = 1.0 - (dMentality * 0.30) - (dWidth * 0.10)
            + (dPressure < 0 ? Math.abs(dPressure) * 0.15 : 0)
            - extremeExposurePenalty(mentality, .20d)
            - extremeExposurePenalty(width, .12d);

        // Posse de Bola: favorecida por Passe Curto, Tempo baixo e Mentalidade Equilibrada
        mod.possessionMultiplier = 1.0 - (dPassing * 0.30) - (dTempo * 0.20)
            - (Math.abs(dMentality) * 0.10)
            - extremeExposurePenalty(passing, .12d)
            - extremeExposurePenalty(tempo, .08d);

        // Contra-Ataques: favorecidos por Passe Longo e Mentalidade Defensiva/Transição
        mod.counterAttackMultiplier = 1.0 + (dPassing * 0.40) - (dMentality * 0.20);

        // Cruzamentos: favorecidos por Amplitude alta
        mod.crossingMultiplier = 1.0 + (dWidth * 0.50);

        // Eficiência de Pressão: alta pressão e ritmo elevado
        mod.pressingEfficiency = 1.0 + (dPressure * 0.45) + (dTempo * 0.15);

        // Desgaste Físico (Fadiga): afetado diretamente por Pressão e Ritmo
        mod.fatigueMultiplier = calculatePhysicalLoadMultiplier(tempo, pressure);

        // Risco de Cartões / Faltas: afetado por Pressão e Desespero Ofensivo
        mod.cardRiskMultiplier = 1.0 + (dPressure * 0.50)
            + (dMentality > 0.5 ? dMentality * 0.20 : 0)
            + extremeExposurePenalty(pressure, .38d)
            + extremeExposurePenalty(mentality, .16d);

        // Ritmo muda a quantidade e a natureza das ações, com aceleração
        // perceptível acima de 75 em vez de apenas aumentar a força ofensiva.
        mod.eventFrequencyMultiplier = eventFrequencyMultiplier(tempo);
        mod.turnoverRiskMultiplier = 1d + dTempo * .16d
            + extremeExposurePenalty(tempo, .32d);
        mod.transitionSpeedMultiplier = 1d + dTempo * .28d
            + extremeExposurePenalty(tempo, .30d);

        // Mentalidade representa quantos jogadores abandonam a estrutura para
        // participar da jogada. Em 85+, a cobertura cai muito mais rapidamente.
        mod.playersCommittedForward = Math.max(3, Math.min(8, Math.round(3f + mentality / 20f)));
        mod.boxPresenceMultiplier = 1d + dMentality * .24d
            + extremeExposurePenalty(mentality, .22d);
        mod.defensiveCoverageMultiplier = 1d - dMentality * .16d
            - extremeExposurePenalty(mentality, .22d);
        mod.counterVulnerabilityMultiplier = 1d + dMentality * .24d
            + extremeExposurePenalty(mentality, .48d)
            + extremeExposurePenalty(tempo, .22d);

        // Pressionar oferece recuperações em zonas mais altas, mas o bônus
        // defensivo direto desaparece: quando a primeira linha é quebrada há
        // espaço real para o adversário atacar.
        mod.regainChance = clamp(.045d + pressure * .0017d, .035d, .23d);
        mod.highRegainChance = clamp(Math.max(0d, pressure - 50d) * .005625d, 0d, .29d);
        mod.opponentErrorMultiplier = 1d + Math.max(0d, pressure - 50d) * .0028d;
        mod.pressBreakRisk = clamp(.04d + pressure * .0008d
            + extremeExposurePenalty(pressure, .22d), .04d, .34d);
        mod.pressBreakDefensePenalty = clamp(Math.max(0d, pressure - 60d) * .005d, 0d, .20d);

        mod.attackMultiplier = clamp(mod.attackMultiplier, .55d, 1.75d);
        mod.defenseMultiplier = clamp(mod.defenseMultiplier, .45d, 1.55d);
        mod.possessionMultiplier = clamp(mod.possessionMultiplier, .45d, 1.55d);
        mod.counterAttackMultiplier = clamp(mod.counterAttackMultiplier, .50d, 1.65d);
        mod.crossingMultiplier = clamp(mod.crossingMultiplier, .50d, 1.60d);
        mod.pressingEfficiency = clamp(mod.pressingEfficiency, .50d, 1.65d);
        mod.cardRiskMultiplier = clamp(mod.cardRiskMultiplier, .55d, 2.25d);
        mod.turnoverRiskMultiplier = clamp(mod.turnoverRiskMultiplier, .72d, 1.55d);
        mod.transitionSpeedMultiplier = clamp(mod.transitionSpeedMultiplier, .65d, 1.65d);
        mod.boxPresenceMultiplier = clamp(mod.boxPresenceMultiplier, .68d, 1.55d);
        mod.defensiveCoverageMultiplier = clamp(mod.defensiveCoverageMultiplier, .55d, 1.35d);
        mod.counterVulnerabilityMultiplier = clamp(mod.counterVulnerabilityMultiplier, .65d, 1.85d);

        // -----------------------------------------------------------------
        // RECONHECIMENTO DE ESTILOS EMERGENTES (POO / DESIGN ADAPTATIVO)
        // -----------------------------------------------------------------
        if (pressure >= 70 && tempo >= 70) {
            mod.detectedStyle = "Gegenpressing (Pressão Alta)";
        } else if (tempo <= 40 && passing <= 35 && mentality >= 40 && mentality <= 65) {
            mod.detectedStyle = "Posse de Bola / Tiki-Taka";
        } else if (mentality <= 35 && passing >= 65) {
            mod.detectedStyle = "Retranca & Contra-Ataque";
        } else if (mentality >= 75 && width >= 70) {
            mod.detectedStyle = "Ataque Total pelas Pontas";
        } else if (passing <= 40 && width <= 40) {
            mod.detectedStyle = "Jogo Apoiado / Central";
        } else if (tempo >= 75 && passing >= 70) {
            mod.detectedStyle = "Transições Rápidas / Direct Football";
        } else {
            mod.detectedStyle = "Equilibrado";
        }

        return mod;
    }

    /** Faixas compartilhadas pela interface e pela simulação. */
    public static String interpretLevel(float value) {
        value = clampValue(value);
        if (value <= 25f) return "MUITO BAIXO";
        if (value <= 40f) return "BAIXO";
        if (value <= 59f) return "EQUILIBRADO";
        if (value <= 74f) return "ALTO";
        if (value <= 89f) return "MUITO ALTO";
        return "EXTREMO";
    }

    /**
     * Pressão segue a curva de referência: 50 = normal, 70 = +8%,
     * 85 = +20% e 100 = +38%. O ritmo adiciona sua própria carga, menor.
     */
    public static double calculatePhysicalLoadMultiplier(float tempo, float pressure) {
        return clamp(1d + pressureLoadAdjustment(pressure) + tempoLoadAdjustment(tempo), .82d, 1.72d);
    }

    public static int calculatePhysicalLoadPercent(float tempo, float pressure) {
        return (int) Math.round((calculatePhysicalLoadMultiplier(tempo, pressure) - 1d) * 100d);
    }

    public static int calculatePressureLoadPercent(float pressure) {
        return (int) Math.round(pressureLoadAdjustment(pressure) * 100d);
    }

    private static double pressureLoadAdjustment(float value) {
        value = clampValue(value);
        if (value <= 50f) return (value - 50f) * .0015d;
        if (value <= 74f) return (value - 50f) * .004d;
        if (value <= 89f) return .10d + (value - 75f) * .01d;
        return .26d + (value - 90f) * .012d;
    }

    private static double tempoLoadAdjustment(float value) {
        value = clampValue(value);
        if (value <= 50f) return (value - 50f) * .001d;
        if (value <= 74f) return (value - 50f) * .002d;
        if (value <= 89f) return .05d + (value - 75f) * .007d;
        return .16d + (value - 90f) * .01d;
    }

    private static double extremeExposurePenalty(float value, double maximumPenalty) {
        value = clampValue(value);
        if (value < 75f) return 0d;
        double progress = (value - 75d) / 25d;
        return maximumPenalty * progress * progress;
    }

    private static double eventFrequencyMultiplier(float value) {
        value = clampValue(value);
        if (value <= 50f) return .55d + value * .009d;
        if (value <= 74f) return 1d + (value - 50f) * .009d;
        if (value <= 89f) return 1.23d + (value - 75f) * .016d;
        return 1.48d + (value - 90f) * .022d;
    }

    private static float clampValue(float value) {
        return Math.max(0f, Math.min(100f, value));
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
