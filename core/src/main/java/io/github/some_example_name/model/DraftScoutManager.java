package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class DraftScoutManager {
    private static final double DAILY_PROGRESS_RATE = 1.5;
    private final List<ScoutTarget> activeTargets;
    private final List<ScoutTarget> completedTargets;
    private int scoutStars = 3;
    private final Set<String> favoritePlayerIds = new HashSet<>();

    public DraftScoutManager(int scoutStars) {
        this.activeTargets = new ArrayList<>();
        this.completedTargets = new ArrayList<>();
        this.scoutStars = Math.max(1, Math.min(5, scoutStars));
    }

    /** Cada jogador observado avança exatamente 1,5% por dia. */
    public double getDailyProgressRate() {
        return DAILY_PROGRESS_RATE;
    }

    public void advanceDay() {
        double rate = getDailyProgressRate();
        Iterator<ScoutTarget> iterator = activeTargets.iterator();
        while (iterator.hasNext()) {
            ScoutTarget target = iterator.next();
            target.advanceKnowledge(rate);
            if (target.isFullyScouted()) {
                iterator.remove();
                completedTargets.add(target);
            }
        }
    }

    public boolean addTarget(Player player) {
        if (isFull()) return false;
        if (containsPlayer(player)) return false;
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
        for (ScoutTarget target : completedTargets) {
            sum += target.getKnowledgePercentage();
        }
        return (int) (sum / totalDraftPlayers);
    }

    public List<ScoutTarget> getActiveTargets() { return activeTargets; }
    public List<ScoutTarget> getCompletedTargets() { return completedTargets; }
    public boolean isFull() { return activeTargets.size() >= getMaxScoutedPlayers(); }
    public boolean isActiveTarget(ScoutTarget target) { return activeTargets.contains(target); }
    public boolean containsPlayer(Player player) {
        if (player == null) return false;
        for (ScoutTarget target : activeTargets) {
            if (target.getPlayer().getId().equals(player.getId())) return true;
        }
        for (ScoutTarget target : completedTargets) {
            if (target.getPlayer().getId().equals(player.getId())) return true;
        }
        return false;
    }
    public int getScoutStars() { return scoutStars; }
    public void setScoutStars(int stars) { scoutStars = Math.max(1, Math.min(5, stars)); }
    public int getMaxScoutedPlayers() { return 2 + scoutStars; }
    public boolean isFavorite(Player player) { return player != null && favoritePlayerIds.contains(player.getId()); }
    public void toggleFavorite(Player player) { if (player == null) return; if (!favoritePlayerIds.add(player.getId())) favoritePlayerIds.remove(player.getId()); }
}
