package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;

public final class DraftLotteryOddsRegressionTest {
    public static void main(String[] args) {
        Map<Club, Integer> odds = new LinkedHashMap<>();
        for (int i = 0; i < 4; i++) odds.put(new Club(), 25);
        List<Club> clubs = new ArrayList<>(odds.keySet());
        close(DraftLotteryOdds.topThree(odds, Collections.singleton(clubs.get(0))), .75);
        close(DraftLotteryOdds.topThree(odds, clubs.subList(0, 2)), 1);
        close(DraftLotteryOdds.topThree(odds, Collections.emptyList()), 0);
        odds.put(clubs.get(0), 70); odds.put(clubs.get(1), 10); odds.put(clubs.get(2), 10); odds.put(clubs.get(3), 10);
        // The only failure is all three 10% clubs being drawn first, in any order.
        close(DraftLotteryOdds.topThree(odds, Collections.singleton(clubs.get(0))), 1 - 6 * .1 * (10d / 90) * (10d / 80));
        double sum = 0; for (Club club : clubs) sum += DraftLotteryOdds.topThree(odds, Collections.singleton(club));
        close(sum, 3);
        odds.remove(clubs.get(3));
        close(DraftLotteryOdds.topThree(odds, Collections.singleton(clubs.get(0))), 1);
        close(DraftLotteryOdds.topThree(Collections.emptyMap(), clubs), 0);
        System.out.println("Lottery odds: weighted top three, multiple owned picks, total probability and empty field OK.");
    }
    private static void close(double value, double expected) { if (Math.abs(value - expected) > 1e-9) throw new AssertionError(value + " != " + expected); }
}
