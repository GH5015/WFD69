package io.github.some_example_name.engine;

public class TacticalModifiers {
    // Multiplicadores base (1.0 = 100% normal)
    public double attackMultiplier = 1.0;
    public double defenseMultiplier = 1.0;
    public double possessionMultiplier = 1.0;
    public double fatigueMultiplier = 1.0;
    public double counterAttackMultiplier = 1.0;
    public double crossingMultiplier = 1.0;
    public double pressingEfficiency = 1.0;
    public double cardRiskMultiplier = 1.0;

    // Ritmo: volume, perdas e velocidade das transições.
    public double eventFrequencyMultiplier = 1.0;
    public double turnoverRiskMultiplier = 1.0;
    public double transitionSpeedMultiplier = 1.0;

    // Mentalidade: risco estrutural, não apenas bônus linear.
    public int playersCommittedForward = 6;
    public double boxPresenceMultiplier = 1.0;
    public double defensiveCoverageMultiplier = 1.0;
    public double counterVulnerabilityMultiplier = 1.0;

    // Adequação do passe e ocupação dos corredores ao XI escalado.
    public double passRetentionMultiplier = 1.0;
    public double centralCreationMultiplier = 1.0;
    public double aerialThreatMultiplier = 1.0;
    public double flankThreatMultiplier = 1.0;
    public double spacingPenaltyMultiplier = 1.0;

    // Pressão: recuperação, erro forçado e risco de quebra da primeira linha.
    public double regainChance = 0.12;
    public double highRegainChance = 0.0;
    public double opponentErrorMultiplier = 1.0;
    public double pressBreakRisk = 0.08;
    public double pressBreakDefensePenalty = 0.0;
    public float tempoSetting = 50f;
    public float mentalitySetting = 50f;
    public float passingSetting = 50f;
    public float widthSetting = 50f;
    public float pressureSetting = 50f;

    // Nome do estilo emergente detectado (para UI / Narração)
    public String detectedStyle = "Equilibrado";
}
