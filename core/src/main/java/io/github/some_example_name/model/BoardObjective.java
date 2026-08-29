package io.github.some_example_name.model;

/** Uma expectativa sazonal da diretoria, com categoria e peso definidos. */
public final class BoardObjective {
    public enum Category {
        SPORTING("ESPORTIVO"), SQUAD("ELENCO"), DEVELOPMENT("DESENVOLVIMENTO"),
        FINANCIAL("FINANCEIRO"), DRAFT("DRAFT"), LONG_TERM("LONGO PRAZO");
        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Priority {
        CRITICAL("CRÍTICO", 3, 40), IMPORTANT("IMPORTANTE", 2, 20), SECONDARY("SECUNDÁRIO", 1, 10);
        private final String label;
        private final int stars;
        private final int weight;
        Priority(String label, int stars, int weight) {
            this.label = label; this.stars = stars; this.weight = weight;
        }
        public String getLabel() { return label; }
        public int getStars() { return stars; }
        public int getWeight() { return weight; }
    }

    public enum Type {
        CHAMPION, REACH_FINAL, MAKE_PLAYOFFS, TABLE_POSITION, WIN_RATE,
        ATTACK_RANK, DEFENSE_RANK, DEVELOP_YOUNG, SALARY_CAP,
        DEVELOP_YOUNG_OVR, DRAFTED_MINUTES, HIGH_POTENTIAL_PROSPECT,
        FIRST_ROUND_PICKS, AVERAGE_AGE, ROSTER_SIZE, TEAM_OVERALL, STADIUM_PROJECT
    }

    private final String title;
    private final Category category;
    private final Priority priority;
    private final Type type;
    private final double target;

    public BoardObjective(String title, Category category, Priority priority, Type type, double target) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.type = type;
        this.target = target;
    }

    public String getTitle() { return title; }
    public Category getCategory() { return category; }
    public Priority getPriority() { return priority; }
    public Type getType() { return type; }
    public double getTarget() { return target; }
}
