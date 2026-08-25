package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Dados persistentes e progresso fracionado do desenvolvimento de um atleta. */
public class PlayerDevelopment {
    private final int initialTruePotential;
    private int truePotential;
    private int perceivedPotential;
    private DevelopmentFocus focus = DevelopmentFocus.BALANCED;
    private final DevelopmentCurve curve;

    private boolean initialized;
    private double attackProgress;
    private double passingProgress;
    private double defenseProgress;
    private double physicalProgress;
    private double dribblingProgress;
    private double goalkeeperProgress;

    private int lastAttackChange;
    private int lastPassingChange;
    private int lastDefenseChange;
    private int lastPhysicalChange;
    private int lastDribblingChange;

    private final List<Integer> weeklyOverallHistory = new ArrayList<>();
    private final Map<Integer, Integer> yearlyOverallHistory = new LinkedHashMap<>();

    public PlayerDevelopment(int potential, DevelopmentCurve curve) {
        this.initialTruePotential = clamp(potential);
        this.truePotential = clamp(potential);
        this.perceivedPotential = clamp(potential);
        this.curve = curve != null ? curve : DevelopmentCurve.NORMAL;
    }

    public void initialize(TechnicalAttributes attributes, int overall) {
        if (initialized || attributes == null) return;
        initialized = true;
        attackProgress = attributes.getAtaque();
        passingProgress = attributes.getPasse();
        defenseProgress = attributes.getDefesa();
        physicalProgress = attributes.getFisico();
        dribblingProgress = attributes.getDrible();
        goalkeeperProgress = attributes.getGoleiro();
        recordWeeklyOverall(overall);
    }

    public void applyAttributeGrowth(
        TechnicalAttributes attributes,
        double attack,
        double passing,
        double defense,
        double physical,
        double dribbling,
        double goalkeeper
    ) {
        if (attributes == null) return;
        initialize(attributes, 0);

        int oldAttack = attributes.getAtaque();
        int oldPassing = attributes.getPasse();
        int oldDefense = attributes.getDefesa();
        int oldPhysical = attributes.getFisico();
        int oldDribbling = attributes.getDrible();

        attackProgress = clampAttribute(attackProgress + attack);
        passingProgress = clampAttribute(passingProgress + passing);
        defenseProgress = clampAttribute(defenseProgress + defense);
        physicalProgress = clampAttribute(physicalProgress + physical);
        dribblingProgress = clampAttribute(dribblingProgress + dribbling);
        goalkeeperProgress = clampAttribute(goalkeeperProgress + goalkeeper);

        attributes.setAtaque((int) Math.floor(attackProgress));
        attributes.setPasse((int) Math.floor(passingProgress));
        attributes.setDefesa((int) Math.floor(defenseProgress));
        attributes.setFisico((int) Math.floor(physicalProgress));
        attributes.setDrible((int) Math.floor(dribblingProgress));
        attributes.setGoleiro((int) Math.floor(goalkeeperProgress));

        lastAttackChange = attributes.getAtaque() - oldAttack;
        lastPassingChange = attributes.getPasse() - oldPassing;
        lastDefenseChange = attributes.getDefesa() - oldDefense;
        lastPhysicalChange = attributes.getFisico() - oldPhysical;
        lastDribblingChange = attributes.getDrible() - oldDribbling;
    }

    public void recordWeeklyOverall(int overall) {
        if (weeklyOverallHistory.isEmpty() || weeklyOverallHistory.get(weeklyOverallHistory.size() - 1) != overall) {
            weeklyOverallHistory.add(overall);
        }
        while (weeklyOverallHistory.size() > 5) weeklyOverallHistory.remove(0);
    }

    public void recordYearOverall(int year, int overall) {
        yearlyOverallHistory.put(year, overall);
    }

    public int getDevelopmentPercent(int overall) {
        int range = Math.max(1, truePotential - 45);
        return Math.max(0, Math.min(100, (int) Math.round(((overall - 45d) / range) * 100d)));
    }

    public void adjustTruePotential(int change) {
        truePotential = Math.max(initialTruePotential - 3, Math.min(initialTruePotential + 4, truePotential + change));
        perceivedPotential = Math.max(40, Math.min(99, perceivedPotential + change));
    }

    private int clamp(int value) { return Math.max(40, Math.min(99, value)); }
    private double clampAttribute(double value) { return Math.max(1d, Math.min(99d, value)); }

    public int getTruePotential() { return truePotential; }
    public int getPerceivedPotential() { return perceivedPotential; }
    public DevelopmentFocus getFocus() { return focus; }
    public void setFocus(DevelopmentFocus focus) { this.focus = focus != null ? focus : DevelopmentFocus.BALANCED; }
    public DevelopmentCurve getCurve() { return curve; }
    public int getLastAttackChange() { return lastAttackChange; }
    public int getLastPassingChange() { return lastPassingChange; }
    public int getLastDefenseChange() { return lastDefenseChange; }
    public int getLastPhysicalChange() { return lastPhysicalChange; }
    public int getLastDribblingChange() { return lastDribblingChange; }
    public List<Integer> getWeeklyOverallHistory() { return Collections.unmodifiableList(weeklyOverallHistory); }
    public Map<Integer, Integer> getYearlyOverallHistory() { return Collections.unmodifiableMap(yearlyOverallHistory); }
}
