package io.github.some_example_name.model;

import java.util.*;

/** Exact chance of at least one target winning a top-three pick, without replacement. */
public final class DraftLotteryOdds {
    private DraftLotteryOdds() { }
    public static double topThree(Map<Club, Integer> weights, Collection<Club> targets) {
        List<Club> clubs = new ArrayList<>(weights.keySet());
        double total = 0;
        for (int weight : weights.values()) total += weight;
        return probability(clubs, weights, new HashSet<>(targets), new HashSet<>(), total, 3);
    }
    private static double probability(List<Club> clubs, Map<Club, Integer> weights, Set<Club> targets,
                                      Set<Club> drawn, double total, int remaining) {
        if (remaining == 0 || total <= 0) return 0;
        double chance = 0;
        for (Club club : clubs) {
            if (drawn.contains(club)) continue;
            int weight = weights.get(club);
            if (weight <= 0) continue;
            double branch = weight / total;
            if (targets.contains(club)) chance += branch;
            else {
                drawn.add(club);
                chance += branch * probability(clubs, weights, targets, drawn, total - weight, remaining - 1);
                drawn.remove(club);
            }
        }
        return chance;
    }
}
