package io.github.some_example_name.utils;

import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.NarrativeCategory;

public final class MatchNarrator {
    private MatchNarrator() {}

    public static String generateCommentary(MatchEvent event, Match match) {
        if (event == null || event.description == null) return "";

        NarrativeCategory category = event.getNarrativeCategory();
        return category.getIcon() + "  " + category.getLabel() + "  •  " + event.description;
    }
}
