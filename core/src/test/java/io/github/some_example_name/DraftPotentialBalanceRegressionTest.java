package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassGenerator;
import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.model.LeagueExpansionService;
import io.github.some_example_name.model.Player;

import java.lang.reflect.Method;
import java.util.List;

/** Confere a curva global contra todas as classes históricas e uma classe futura. */
public final class DraftPotentialBalanceRegressionTest {
    public static void main(String[] args) throws Exception {
        int checkedPlayers = 0;
        for (int year = 1970; year <= 2006; year++) {
            Class<?> draftClass = Class.forName(
                "io.github.some_example_name.database.DraftClass" + year
            );
            Method getPlayers = draftClass.getMethod("getPlayers");
            @SuppressWarnings("unchecked")
            List<Player> raw = (List<Player>) getPlayers.invoke(null);
            raw = DraftClassGenerator.ensureMinimumProspects(
                raw,
                year,
                LeagueExpansionService.projectedClubCount(year) * 2
            );
            checkedPlayers += verify(year, raw, DraftClassRepository.getClassForYear(year), year >= 2003);
        }

        int futureYear = 2035;
        List<Player> rawFuture = DraftClassGenerator.ensureMinimumProspects(
            DraftClassGenerator.generateProceduralClass(futureYear),
            futureYear,
            LeagueExpansionService.projectedClubCount(futureYear) * 2
        );
        checkedPlayers += verify(
            futureYear,
            rawFuture,
            DraftClassRepository.getClassForYear(futureYear), false
        );

        System.out.println(
            "Draft potential balance: " + checkedPlayers
                + " prospects checked across historical and procedural classes."
        );
    }

    private static int verify(int year, List<Player> raw, List<Player> balanced, boolean alreadyBalanced) {
        require(raw.size() == balanced.size(), "Size mismatch in " + year);
        for (int index = 0; index < raw.size(); index++) {
            Player before = raw.get(index);
            Player after = balanced.get(index);
            int rank = index + 1;
            int reduction = rank <= 10 ? 0 : rank <= 20 ? 5
                : rank <= 30 ? 9 : rank <= 40 ? 13 : rank <= 50 ? 17 : 21;
            int floor = Math.min(before.getTruePotential(), before.getOverall() + 2);
            int expected = alreadyBalanced ? before.getTruePotential()
                : Math.max(floor, before.getTruePotential() - reduction);
            require(after.getTruePotential() == expected,
                "Wrong potential in " + year + " rank " + rank + " (" + after.getName() + ")");
            require(after.getPotential() == expected,
                "Perceived potential was not recalibrated for " + after.getName());
            if (alreadyBalanced || rank <= 10) {
                require(after.getTruePotential() == before.getTruePotential(),
                    "Top 10 changed in " + year);
            } else {
                require(after.getTruePotential() <= before.getTruePotential(),
                    "Potential increased in " + year);
            }
        }
        return raw.size();
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
