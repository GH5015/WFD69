package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.NewsEvent;
import io.github.some_example_name.model.StandingsRow;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Edição ampliada, alinhada às regiões impressas da folha. */
public final class WflNewsDialog {
    private static final Color INK = Color.valueOf("0A2E40");
    private static final Color GREEN = Color.valueOf("075535");
    private static final Color BODY = Color.valueOf("25382F");
    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final float PAPER_WIDTH = 1122f, PAPER_HEIGHT = 1402f;

    private WflNewsDialog() { }

    public static boolean showPending(Stage stage, Main game) {
        if (stage == null || game == null || game.league == null || !game.league.isWeeklyNewsPending()) return false;
        List<NewsEvent> edition = game.league.getLatestNewsEdition();
        if (edition.isEmpty()) return false;
        show(stage, game, edition);
        game.league.markWeeklyNewsDisplayed();
        return true;
    }

    private static void show(Stage stage, Main game, List<NewsEvent> edition) {
        final Texture paperTexture = new Texture("jornal.png");
        paperTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Dialog dialog = new Dialog("", game.skin) {
            private boolean disposed;
            @Override public boolean remove() {
                boolean removed = super.remove();
                // A textura ainda é usada pelo fade de fechamento.
                if (removed && !disposed) { disposed = true; paperTexture.dispose(); }
                return removed;
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.getTitleTable().clear();
        dialog.setBackground(StyleFactory.createRoundedPanel(Color.valueOf("071A12"), StyleFactory.GOLD));
        dialog.getContentTable().clear();
        dialog.getContentTable().pad(12f);

        NewspaperPage newspaper = createNewspaperContent(game, edition, paperTexture);
        float paperWidth = Math.min(1100f, stage.getWidth() - 110f);
        float viewportHeight = Math.min(1150f, stage.getHeight() - 150f);
        newspaper.setReadingWidth(paperWidth);
        Table paperContainer = new Table();
        paperContainer.add(newspaper).size(paperWidth, newspaper.getPrefHeight());
        ScrollPane scroll = new ScrollPane(paperContainer, game.skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setOverscroll(false, false);
        scroll.setName("news-scroll");
        dialog.getContentTable().add(scroll).width(paperWidth + 20f).height(viewportHeight);

        Table footer = dialog.getButtonTable();
        footer.pad(6f, 18f, 14f, 18f);
        Label hint = new Label("ROLE PARA LER • RESULTADOS E TOP 4 NO RODAPÉ", game.skin);
        hint.setFontScale(.55f);
        hint.setColor(StyleFactory.CREME_AGED);
        footer.add(hint).expandX().left().padRight(18f);
        TextButton close = ScreenUI.createPrimaryButton(game.skin, "FECHAR JORNAL");
        close.getLabel().setFontScale(.62f);
        footer.add(close).width(280f).height(52f);
        close.addListener(new ClickListener() {
            @Override public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) { dialog.hide(); }
        });
        dialog.show(stage);
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) / 2f, (stage.getHeight() - dialog.getHeight()) / 2f);
        stage.setScrollFocus(scroll);
        scroll.setScrollY(0f);
    }

    private static NewspaperPage createNewspaperContent(Main game, List<NewsEvent> edition, Texture paper) {
        NewsEvent lead = edition.get(0);
        NewsEvent featured = selectOther(edition, lead, null, NewsEvent.Category.DESTAQUE);
        NewsEvent secondary = selectOther(edition, lead, featured, NewsEvent.Category.HISTORIA);
        List<Match> matches = recentMatches(game, lead.getDate());
        List<StandingsRow> standings = game.league.getFullStandings(null);
        NewspaperPage page = new NewspaperPage(paper);

        // Coordenadas da arte original, medidas a partir do topo.
        page.text("edition-date", formatDate(lead.getDate()) + "   •   EDIÇÃO SEMANAL", game, true, 22, 18, INK, Align.center, 240, 280, 620, 43);
        page.text("lead-headline", lead.getHeadline(), game, true, 46, 29, INK, Align.center, 92, 350, 678, 147);
        String subtitle = lead.getCategory() == NewsEvent.Category.RESULTADO ? lead.getBody()
            : lead.getCategory().getLabel() + " • BALANÇO DA SEMANA";
        page.text("lead-subtitle", subtitle, game, true, 23, 17, GREEN, Align.center, 99, 507, 666, 45);
        page.text("lead-body", lead.getBody() + "\n\n" + weeklySummary(matches), game, false, 25, 18, BODY, Align.topLeft, 99, 623, 313, 359);
        page.text("weekly-context", weeklyContext(matches, standings), game, false, 25, 18, BODY, Align.topLeft, 458, 623, 309, 359);
        addSideArticle(page, game, featured, true);
        addSideArticle(page, game, secondary, false);
        page.ribbon("results-title", "RESULTADOS DA SEMANA", game, GREEN, 224, 1019, 280, 29);
        if (matches.isEmpty()) {
            page.text("no-results", "Nenhuma partida disputada nesta semana.", game, true, 22, 18, BODY, Align.center, 100, 1070, 527, 174);
        } else {
            for (int i = 0; i < Math.min(4, matches.size()); i++) {
                Match match = matches.get(i);
                float top = 1059 + i * 51;
                page.text("result-home-" + i, match.getHomeTeam().getName().toUpperCase(PT_BR), game, true, 20, 16, INK, Align.left, 102, top, 208, 45);
                page.text("result-score-" + i, match.getHomeGoals() + " x " + match.getAwayGoals(), game, true, 25, 20, GREEN, Align.center, 317, top, 83, 45);
                page.text("result-away-" + i, match.getAwayTeam().getName().toUpperCase(PT_BR), game, true, 20, 16, INK, Align.right, 407, top, 221, 45);
            }
        }
        page.ribbon("standings-title", "TOP 4 DA WFL", game, INK, 776, 1085, 182, 28);
        for (int i = 0; i < Math.min(4, standings.size()); i++) {
            StandingsRow row = standings.get(i);
            float top = 1122 + i * 37;
            page.text("standing-rank-" + i, String.valueOf(i + 1), game, true, 22, 20, GREEN, Align.center, 680, top, 27, 35);
            page.text("standing-club-" + i, row.club.getName().toUpperCase(PT_BR), game, true, 20, 15, INK, Align.left, 716, top, 225, 35);
            page.text("standing-points-" + i, row.points + " PTS", game, true, 19, 15, INK, Align.right, 951, top, 77, 35);
        }
        return page;
    }

    private static void addSideArticle(NewspaperPage page, Main game, NewsEvent event, boolean featured) {
        String prefix = featured ? "featured" : "secondary";
        Color color = featured ? GREEN : INK;
        page.ribbon(prefix + "-category", event == null ? "WFL" : event.getCategory().getLabel(), game, color,
            856, featured ? 368 : 790, 144, 28);
        if (event == null) {
            page.text(prefix + "-empty", "SEM OUTROS DESTAQUES", game, true, 26, 20, color, Align.center,
                824, featured ? 414 : 835, 211, 112);
            return;
        }
        page.text(prefix + "-headline", event.getHeadline(), game, true, 28, 19, color, Align.center,
            824, featured ? 411 : 833, 211, featured ? 116 : 121);
        page.text(prefix + "-body", event.getBody(), game, false, 19, 14, BODY, Align.topLeft,
            824, featured ? 672 : 961, 211, featured ? 81 : 87);
    }

    private static String weeklySummary(List<Match> matches) {
        StringBuilder text = new StringBuilder();
        if (matches.isEmpty()) text.append("A semana não teve partidas disputadas na WFL.");
        else {
            int goals = 0, draws = 0;
            for (Match match : matches) {
                goals += match.getHomeGoals() + match.getAwayGoals();
                if (match.getHomeGoals() == match.getAwayGoals()) draws++;
            }
            text.append("BALANÇO DOS GRAMADOS\n\n").append(matches.size()).append(" partidas e ").append(goals)
                .append(" gols na semana. ");
            if (draws == 0) text.append("Nenhum empate registrado.");
            else text.append(draws).append(draws == 1 ? " confronto terminou empatado." : " confrontos terminaram empatados.");
        }
        return text.toString();
    }

    private static String weeklyContext(List<Match> matches, List<StandingsRow> standings) {
        StringBuilder text = new StringBuilder();
        if (!standings.isEmpty() && standings.get(0).matches > 0) {
            StandingsRow leader = standings.get(0);
            text.append("NA CLASSIFICAÇÃO\n\n").append(leader.club.getName()).append(" lidera com ")
                .append(leader.points).append(" pontos em ").append(leader.matches)
                .append(leader.matches == 1 ? " jogo da fase regular." : " jogos da fase regular.");
        } else {
            text.append("A fase regular ainda não tem resultados registrados na classificação.");
        }
        if (!matches.isEmpty()) {
            Match mostGoals = matches.stream().max(Comparator.comparingInt(m -> m.getHomeGoals() + m.getAwayGoals())).get();
            text.append("\n\nJOGO COM MAIS GOLS\n\n").append(mostGoals.getHomeTeam().getName()).append(" ")
                .append(mostGoals.getHomeGoals()).append(" x ").append(mostGoals.getAwayGoals()).append(" ")
                .append(mostGoals.getAwayTeam().getName()).append(".");
        }
        if (matches.size() > 4) text.append("\n\nO quadro abaixo mostra os quatro resultados mais recentes.");
        return text.toString();
    }

    private static List<Match> recentMatches(Main game, Date date) {
        Date end = date == null ? game.league.getCurrentDate() : date;
        List<Match> matches = new ArrayList<>();
        if (end == null) return matches;
        Calendar start = Calendar.getInstance();
        start.setTime(end);
        start.add(Calendar.DATE, -7);
        for (Match match : game.league.getSchedule()) {
            if (match.isPlayed() && match.getDate() != null
                && !match.getDate().before(start.getTime()) && !match.getDate().after(end)) matches.add(match);
        }
        matches.sort(Comparator.comparing(Match::getDate).reversed());
        return matches;
    }

    private static NewsEvent selectOther(List<NewsEvent> edition, NewsEvent lead, NewsEvent used, NewsEvent.Category preferred) {
        for (NewsEvent event : edition) if (event != lead && event != used && event.getCategory() == preferred) return event;
        for (NewsEvent event : edition) if (event != lead && event != used) return event;
        return null;
    }

    private static String formatDate(Date date) {
        return date == null ? "WFL NEWS" : new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", PT_BR).format(date).toUpperCase(PT_BR);
    }

    /** O fundo e todos os campos usam a mesma transformação, sem deformar o papel. */
    private static final class NewspaperPage extends WidgetGroup {
        private final Group sheet = new Group();
        private float readingWidth = 1100f;
        NewspaperPage(Texture paper) {
            setName("newspaper-page");
            Image image = new Image(new TextureRegionDrawable(paper));
            image.setSize(PAPER_WIDTH, PAPER_HEIGHT);
            sheet.addActor(image);
            addActor(sheet);
        }
        void setReadingWidth(float width) { readingWidth = width; invalidateHierarchy(); }
        @Override public float getPrefWidth() { return readingWidth; }
        @Override public float getPrefHeight() { return readingWidth * PAPER_HEIGHT / PAPER_WIDTH; }
        @Override public void layout() { sheet.setScale(getWidth() / PAPER_WIDTH, getHeight() / PAPER_HEIGHT); }
        void ribbon(String name, String text, Main game, Color background, float x, float top, float width, float height) {
            Image cover = new Image(StyleFactory.createSolid(background));
            place(cover, x, top, width, height);
            text(name, text, game, true, 19, 15, Color.WHITE, Align.center, x, top, width, height);
        }
        void text(String name, String text, Main game, boolean bold, float size, float minSize, Color color,
                  int align, float x, float top, float width, float height) {
            FittedLabel label = new FittedLabel(text, game, bold, size, minSize);
            label.setName(name);
            label.setColor(color);
            label.setAlignment(align);
            place(label, x, top, width, height);
        }
        private void place(Actor actor, float x, float top, float width, float height) {
            actor.setBounds(x, PAPER_HEIGHT - top - height, width, height);
            sheet.addActor(actor);
        }
    }

    private static final class FittedLabel extends Label {
        private final float desiredScale, minimumScale;
        private final String fullText;
        private boolean fitting;
        FittedLabel(String text, Main game, boolean bold, float size, float minSize) {
            super(text == null ? "" : text, game.skin, bold ? "font-bold" : "default");
            desiredScale = size / (bold ? 31f : 28f);
            minimumScale = minSize / (bold ? 31f : 28f);
            fullText = text == null ? "" : text;
            setWrap(true);
        }
        @Override public void layout() {
            if (!fitting) {
                fitting = true;
                setEllipsis(null);
                setText(fullText);
                float scale = desiredScale;
                setFontScale(scale);
                while (getPrefHeight() > getHeight() && scale > minimumScale) {
                    scale = Math.max(minimumScale, scale - .025f);
                    setFontScale(scale);
                }
                if (getPrefHeight() > getHeight()) {
                    int low = 0, high = fullText.length();
                    while (low < high) {
                        int middle = (low + high + 1) / 2;
                        setText(fullText.substring(0, middle).trim() + "…");
                        if (getPrefHeight() <= getHeight()) low = middle;
                        else high = middle - 1;
                    }
                    int end = fullText.lastIndexOf(' ', low);
                    setText(fullText.substring(0, end > 0 ? end : low).trim() + "…");
                }
                fitting = false;
            }
            super.layout();
        }
    }
}
