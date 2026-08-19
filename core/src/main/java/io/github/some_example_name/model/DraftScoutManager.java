package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;

public class DraftScoutManager {
    private static final int MAX_SCOUTED_PLAYERS = 5; // Limite de 5 simultâneos
    private final List<ScoutTarget> activeTargets;
    private int scoutStars;

    public DraftScoutManager(int scoutStars) {
        this.activeTargets = new ArrayList<>();
        this.scoutStars = Math.max(1, Math.min(5, scoutStars));
    }

    /**
     * Taxa diária de progresso baseada no nível do Olheiro:
     * 1★ = +2.0% | 2★ = +3.0% | 3★ = +3.8% | 4★ = +4.5% | 5★ = +6.0%
     */
    public double getDailyProgressRate() {
        switch (scoutStars) {
            case 1: return 2.0;
            case 2: return 3.0;
            case 3: return 3.8;
            case 4: return 4.5;
            case 5: return 6.0;
            default: return 3.0;
        }
    }

    public void advanceDay() {
        double rate = getDailyProgressRate();
        for (ScoutTarget target : activeTargets) {
            target.advanceKnowledge(rate);
        }
    }

    public boolean addTarget(Player player) {
        if (isFull()) return false;
        for (ScoutTarget target : activeTargets) {
            if (target.getPlayer().getId().equals(player.getId())) return false;
        }
        activeTargets.add(new ScoutTarget(player));
        return true;
    }

    public void removeTarget(ScoutTarget target) {
        activeTargets.remove(target);
    }

    public int getOverallClassKnowledge(int totalDraftPlayers) {
        if (totalDraftPlayers == 0) return 0;
        double sum = 0;
        for (ScoutTarget target : activeTargets) {
            sum += target.getKnowledgePercentage();
        }
        return (int) (sum / totalDraftPlayers);
    }

    public List<ScoutTarget> getActiveTargets() { return activeTargets; }
    public boolean isFull() { return activeTargets.size() >= MAX_SCOUTED_PLAYERS; }
    public int getScoutStars() { return scoutStars; }
}
