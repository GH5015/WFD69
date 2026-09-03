package io.github.some_example_name.database;

import io.github.some_example_name.model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Resolve classes históricas e gera automaticamente qualquer ano futuro. */
public final class DraftClassRepository {
    private static final Map<Integer, Supplier<List<Player>>> HISTORICAL_CLASSES =
        new HashMap<>();

    static {
        HISTORICAL_CLASSES.put(1970, DraftClass1970::getPlayers);
        HISTORICAL_CLASSES.put(1971, DraftClass1971::getPlayers);
        HISTORICAL_CLASSES.put(1972, DraftClass1972::getPlayers);
        HISTORICAL_CLASSES.put(1973, DraftClass1973::getPlayers);
        HISTORICAL_CLASSES.put(1974, DraftClass1974::getPlayers);
        HISTORICAL_CLASSES.put(1975, DraftClass1975::getPlayers);
        HISTORICAL_CLASSES.put(1976, DraftClass1976::getPlayers);
        HISTORICAL_CLASSES.put(1977, DraftClass1977::getPlayers);
        HISTORICAL_CLASSES.put(1978, DraftClass1978::getPlayers);
        HISTORICAL_CLASSES.put(1979, DraftClass1979::getPlayers);
        HISTORICAL_CLASSES.put(1980, DraftClass1980::getPlayers);
        HISTORICAL_CLASSES.put(1981, DraftClass1981::getPlayers);
        HISTORICAL_CLASSES.put(1982, DraftClass1982::getPlayers);
        HISTORICAL_CLASSES.put(1983, DraftClass1983::getPlayers);
        HISTORICAL_CLASSES.put(1984, DraftClass1984::getPlayers);
        HISTORICAL_CLASSES.put(1985, DraftClass1985::getPlayers);
        HISTORICAL_CLASSES.put(1986, DraftClass1986::getPlayers);
        HISTORICAL_CLASSES.put(1987, DraftClass1987::getPlayers);
        HISTORICAL_CLASSES.put(1988, DraftClass1988::getPlayers);
        HISTORICAL_CLASSES.put(1989, DraftClass1989::getPlayers);
        HISTORICAL_CLASSES.put(1990, DraftClass1990::getPlayers);
        HISTORICAL_CLASSES.put(1991, DraftClass1991::getPlayers);
    }

    private DraftClassRepository() {
    }

    public static List<Player> getClassForYear(int year) {
        Supplier<List<Player>> historical = HISTORICAL_CLASSES.get(year);
        List<Player> players = historical != null
            ? historical.get()
            : DraftClassGenerator.generateProceduralClass(year);
        return DraftClassGenerator.ensureMinimumProspects(players, year,
            io.github.some_example_name.model.LeagueExpansionService.projectedClubCount(year) * 2);
    }

    public static boolean hasHistoricalClass(int year) {
        return HISTORICAL_CLASSES.containsKey(year);
    }
}
