package io.github.some_example_name.engine;

public class TacticalEngine {

    public static TacticalModifiers calculateModifiers(float tempo, float mentality, float passing, float width, float pressure) {
        TacticalModifiers mod = new TacticalModifiers();

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
        mod.defenseMultiplier = 1.0 - (dMentality * 0.30) - (dWidth * 0.10) + (dPressure < 0 ? Math.abs(dPressure) * 0.15 : 0);

        // Posse de Bola: favorecida por Passe Curto, Tempo baixo e Mentalidade Equilibrada
        mod.possessionMultiplier = 1.0 - (dPassing * 0.30) - (dTempo * 0.20) - (Math.abs(dMentality) * 0.10);

        // Contra-Ataques: favorecidos por Passe Longo e Mentalidade Defensiva/Transição
        mod.counterAttackMultiplier = 1.0 + (dPassing * 0.40) - (dMentality * 0.20);

        // Cruzamentos: favorecidos por Amplitude alta
        mod.crossingMultiplier = 1.0 + (dWidth * 0.50);

        // Eficiência de Pressão: alta pressão e ritmo elevado
        mod.pressingEfficiency = 1.0 + (dPressure * 0.45) + (dTempo * 0.15);

        // Desgaste Físico (Fadiga): afetado diretamente por Pressão e Ritmo
        mod.fatigueMultiplier = 1.0 + (dPressure * 0.40) + (dTempo * 0.30);

        // Risco de Cartões / Faltas: afetado por Pressão e Desespero Ofensivo
        mod.cardRiskMultiplier = 1.0 + (dPressure * 0.50) + (dMentality > 0.5 ? dMentality * 0.20 : 0);

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
}
