package io.github.some_example_name.simulation;

import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.League;

public class PlayoffSimulator {
    private MatchEngine matchEngine;
    private League league;

    public PlayoffSimulator(MatchEngine engine, League league) {
        this.matchEngine = engine;
        this.league = league;
    }

    public void startPlayoffs() {
        league.beginPlayoffs();
    }
}
