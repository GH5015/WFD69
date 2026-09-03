package io.github.some_example_name.model;

import java.util.Date;

/** Uma matéria curta gerada por templates a partir do estado real da liga. */
public final class NewsEvent {
    public enum Category {
        RESULTADO("RODADA"), DESTAQUE("DESTAQUE"), TRADE("TRADE"),
        HISTORIA("HISTÓRIA"), DIRETORIA("DIRETORIA"), LIGA("WFL");
        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final Date date;
    private final Category category;
    private final String headline;
    private final String body;

    public NewsEvent(Date date, Category category, String headline, String body) {
        this.date = date == null ? null : new Date(date.getTime());
        this.category = category == null ? Category.LIGA : category;
        this.headline = headline == null ? "WFL NEWS" : headline;
        this.body = body == null ? "" : body;
    }

    public Date getDate() { return date == null ? null : new Date(date.getTime()); }
    public Category getCategory() { return category; }
    public String getHeadline() { return headline; }
    public String getBody() { return body; }
}
