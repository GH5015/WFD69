package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Television-style ceremony; animations only reveal the league's single authoritative draw. */
public class DraftLotteryScreen implements Screen {
    private enum Phase { READY, ANTICIPATION, CELEBRATION, COMPLETE }
    private static final Color GOLD = Color.valueOf("D8AE4F"), INK = Color.valueOf("071A13"), BORDER = Color.valueOf("75602C");
    private final Main game;
    private final Club club;
    private final Stage stage;
    private final Map<Club, Integer> odds = new LinkedHashMap<>();
    private final Map<Club, Double> topThree = new LinkedHashMap<>();
    private final Map<String, Texture> logos = new LinkedHashMap<>();
    private final List<Club> projected = new ArrayList<>();
    private final Drawable panelBackground, rowBackground, mineBackground;
    private final Texture pixel;
    private List<Club> result = new ArrayList<>();
    private Phase phase = Phase.READY;
    private int revealed;
    private float elapsed, clock;
    private boolean automatic, disposed;
    private Label countdown;
    private Table hero;
    private Table envelope;
    private ScrollPane revealScroll;
    private double ownTopThree;

    public DraftLotteryScreen(Main game, Club club) {
        this.game = game; this.club = club;
        stage = new Stage(new ResponsiveViewport());
        odds.putAll(game.league.getDraftLotteryOdds()); projected.addAll(odds.keySet());
        List<Club> owned = new ArrayList<>();
        for (Club origin : projected) {
            topThree.put(origin, DraftLotteryOdds.topThree(odds, Collections.singleton(origin)));
            if (ownsPick(origin)) owned.add(origin);
        }
        ownTopThree = DraftLotteryOdds.topThree(odds, owned);
        panelBackground = StyleFactory.createRoundedPanel(INK, BORDER);
        rowBackground = StyleFactory.createRoundedPanel(Color.valueOf("0D2119"), Color.valueOf("23362B"));
        mineBackground = StyleFactory.createRoundedPanel(Color.valueOf("393015"), GOLD);
        rowBackground.setMinHeight(0); rowBackground.setMinWidth(0);
        mineBackground.setMinHeight(0); mineBackground.setMinWidth(0);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE); pixmap.fill(); pixel = new Texture(pixmap); pixmap.dispose();
        if (game.league.isDraftLotteryCompleted()) {
            result = lotteryOrder(); revealed = result.size(); phase = Phase.COMPLETE;
        }
    }
    @Override public void show() { Gdx.input.setInputProcessor(stage); refresh(); }
    private void refresh() {
        stage.clear(); countdown = null; hero = null; envelope = null;
        Stack root = new Stack(); root.setFillParent(true); stage.addActor(root);
        Table page = new Table(); page.pad(20, 28, 18, 28);
        page.add(header()).growX().height(90).padBottom(8).row();
        page.add(steps()).growX().height(55).padBottom(16).row();
        Table columns = new Table();
        columns.add(probabilities()).width(540).growY().padRight(14);
        columns.add(ceremony()).grow().minWidth(600).padRight(14);
        columns.add(franchise()).width(490).growY();
        page.add(columns).grow().minHeight(570).padBottom(14).row();
        Table bottom = new Table();
        bottom.add(rules()).width(540).growY().padRight(14); bottom.add(orderBoard()).grow();
        page.add(bottom).growX().height(145).padBottom(12).row();
        page.add(footer()).growX().height(52); root.add(page);
    }
    private Table header() {
        Table table = new Table(); table.add(label("WFL", 1.0f, GOLD)).width(104);
        Table title = new Table(); title.left();
        title.add(label("WFL DRAFT LOTTERY", 1.18f, StyleFactory.SOFT_YELLOW)).left().row();
        title.add(label("OFF SEASON " + game.league.getCurrentSeason() + "  •  ORDEM DO DRAFT " + (game.league.getCurrentSeason() + 1), .46f, Color.LIGHT_GRAY)).left().padTop(5);
        table.add(title).growX();
        table.add(wrapped(club.getName().toUpperCase(), .62f, GOLD)).width(350).right().padRight(10);
        table.add(crest(club)).size(100); return table;
    }
    private Table steps() {
        Table table = panel(); table.pad(4);
        String[] steps = {"STAFF", "FREE AGENCY", "LOTERIA DO DRAFT", "SCOUTING", "DRAFT"};
        for (int i = 0; i < steps.length; i++) {
            Table step = new Table(); if (i == 2) step.background(mineBackground);
            step.add(label(String.format("%02d", i + 1), .46f, GOLD)).padRight(12);
            step.add(label(steps[i], .48f, i == 2 ? StyleFactory.SOFT_YELLOW : Color.LIGHT_GRAY));
            table.add(step).growX().uniformX().height(44);
        }
        return table;
    }
    private Table probabilities() {
        Table panel = panel(); panel.top();
        panel.add(section("PROBABILIDADES DA LOTERIA")).growX().left().padBottom(12).row();
        Table headings = new Table();
        headings.add(label("PROJ.", .34f, Color.LIGHT_GRAY)).width(42);
        headings.add(label("FRANQUIA / ORIGEM", .34f, Color.LIGHT_GRAY)).growX().left();
        headings.add(label("PICK #1", .34f, Color.LIGHT_GRAY)).width(68);
        headings.add(label("TOP 3", .34f, Color.LIGHT_GRAY)).width(66);
        panel.add(headings).growX().height(28).row();
        Table list = new Table(); list.top(); int rank = 1;
        for (Club origin : projected) {
            boolean mine = ownsPick(origin);
            Table row = new Table(); row.background(mine ? mineBackground : rowBackground); row.pad(3);
            row.add(label(String.valueOf(rank++), .46f, mine ? GOLD : Color.WHITE)).width(36);
            row.add(crest(origin)).size(30).padRight(3);
            Label name = label(origin.getName(), .42f, mine ? StyleFactory.SOFT_YELLOW : Color.WHITE); name.setEllipsis(true); name.setAlignment(Align.left);
            row.add(name).growX().minWidth(0).left();
            row.add(label(odds.get(origin) + "%", .44f, Color.WHITE)).width(66);
            row.add(label(percent(topThree.get(origin)), .44f, GOLD)).width(64);
            list.add(row).growX().height(38).padBottom(2).row();
        }
        if (projected.isEmpty()) list.add(wrapped("Nenhuma franquia elegível nesta temporada.", .45f, Color.LIGHT_GRAY)).growX();
        ScrollPane scroll = scroll(list); scroll.setName("lottery-odds-scroll");
        panel.add(scroll).grow().minHeight(0).padBottom(7).row();
        panel.add(wrapped("Chances pela campanha regular. Dourado: picks que pertencem à sua franquia.", .35f, Color.LIGHT_GRAY)).growX().height(39);
        return panel;
    }
    private Table ceremony() {
        Table panel = panel(); panel.top(); panel.setName("lottery-ceremony");
        panel.add(label("CERIMÔNIA OFICIAL", .43f, GOLD)).padBottom(2).row();
        Stack spotlight = new Stack(); spotlight.add(new CeremonyEffects());
        hero = new Table(); hero.setTransform(true);
        int pick = currentPick(); boolean waiting = phase == Phase.ANTICIPATION;
        String title = phase == Phase.COMPLETE ? "ORDEM DEFINIDA" : result.isEmpty() ? "O FUTURO COMEÇA AQUI" : "PICK #" + pick;
        Label rank = label(title, result.isEmpty() || phase == Phase.COMPLETE ? .90f : 2.0f, StyleFactory.SOFT_YELLOW);
        rank.setName("lottery-pick-title"); hero.add(rank).growX().center().padTop(8).row();
        String subtitle = waiting ? (pick <= 3 ? "TOP 3 • TODOS OS OLHARES AQUI" : "ABRINDO O ENVELOPE")
            : phase == Phase.COMPLETE ? "A PRÓXIMA GERAÇÃO JÁ TEM DESTINO" : revealed == 0 ? "UMA ESCOLHA PODE MUDAR TUDO" : pick == 1 ? "A PRIMEIRA ESCOLHA DO DRAFT!" : "ESCOLHA REVELADA";
        hero.add(label(subtitle, .40f, GOLD)).padTop(3).row();
        if (waiting || revealed == 0) {
            envelope = new Table(); envelope.setTransform(true); envelope.background(mineBackground);
            countdown = label("WFL", 1.12f, GOLD);
            Stack seal = new Stack(); seal.add(new EnvelopeGraphic());
            Table sealText = new Table(); sealText.add(countdown).expand().bottom().padBottom(12); seal.add(sealText);
            envelope.add(seal).grow();
            hero.add(envelope).width(190).height(107).padTop(16).padBottom(9).row();
            hero.add(label(waiting ? "O destino de uma franquia está neste envelope." : "Revelação da última escolha até a Pick #1.", .38f, Color.LIGHT_GRAY)).row();
        } else if (!result.isEmpty()) {
            Club origin = result.get(phase == Phase.COMPLETE ? 0 : pick - 1);
            Table winner = new Table(); winner.add(crest(pickOwner(origin))).size(pick <= 3 ? 125 : 110).padRight(12);
            winner.add(wrapped(pickOwner(origin).getName().toUpperCase(), 1.03f, ownsPick(origin) ? StyleFactory.SOFT_YELLOW : Color.WHITE)).growX().minWidth(0);
            hero.add(winner).growX().height(132).padTop(4).row();
            int actual = phase == Phase.COMPLETE ? 1 : pick;
            String detail = "Projetada: #" + (projected.indexOf(origin) + 1) + "   →   Resultado: #" + actual + "   " + movement(actual, projected.indexOf(origin) + 1);
            if (pickOwner(origin) != origin) detail += "\nVia " + origin.getName();
            hero.add(wrapped(detail, .38f, GOLD)).growX().height(40).row();
        }
        spotlight.add(hero); panel.add(spotlight).growX().height(315).row();
        panel.add(section("ORDEM REVELADA ATÉ AGORA")).padTop(6).padBottom(8).row();
        Table list = new Table(); list.top(); int half = (projected.size() + 1) / 2;
        for (int row = 0; row < half; row++) {
            for (int column = 0; column < 2; column++) {
                int index = projected.size() - 1 - row - column * half;
                if (index < 0) { list.add().growX(); continue; }
                list.add(orderRow(index, isKnown(index), false)).growX().uniformX().pad(2);
            }
            list.row();
        }
        revealScroll = scroll(list); revealScroll.setName("lottery-revealed-scroll");
        panel.add(revealScroll).grow().minHeight(0).row();
        Table controls = new Table(); boolean busy = phase == Phase.ANTICIPATION || phase == Phase.CELEBRATION;
        TextButton next = button(result.isEmpty() ? "INICIAR LOTERIA" : phase == Phase.COMPLETE ? "LOTERIA CONCLUÍDA" : "REVELAR PRÓXIMA", this::revealNext, true);
        next.setName("lottery-next"); next.setDisabled(busy || phase == Phase.COMPLETE);
        controls.add(next).growX().uniformX().height(46).padRight(8);
        TextButton auto = button(automatic ? "PAUSAR AUTOMÁTICO" : "MODO AUTOMÁTICO", () -> {
            automatic = !automatic;
            if (automatic && phase == Phase.READY) revealNext(); else refresh();
        }, false);
        auto.setName("lottery-auto"); auto.setDisabled(phase == Phase.COMPLETE);
        controls.add(auto).growX().uniformX().height(46); panel.add(controls).growX().padTop(10); return panel;
    }
    private Table franchise() {
        Table right = new Table(); Table own = panel(); own.top();
        own.add(section("SUA FRANQUIA")).colspan(2).left().padBottom(8).row();
        own.add(crest(club)).size(136).padRight(10);
        Table facts = new Table();
        facts.add(wrapped(club.getName().toUpperCase(), .58f, Color.WHITE)).growX().padBottom(8).row();
        List<Club> origins = new ArrayList<>(); int firstChance = 0;
        for (Club origin : projected) if (ownsPick(origin)) { origins.add(origin); firstChance += odds.get(origin); }
        fact(facts, "Melhor projeção", origins.isEmpty() ? "Fora da loteria" : "#" + (projected.indexOf(origins.get(0)) + 1));
        fact(facts, "Chance Pick #1", firstChance + "%"); fact(facts, "Chance Top 3", percent(ownTopThree));
        fact(facts, "Caixa", String.format(java.util.Locale.US, "WFL$ %.1fM", club.getFinance().getBalance() / 1_000_000d));
        own.add(facts).growX().minWidth(0).row();
        own.add(wrapped(ownedPicks(), .38f, GOLD)).colspan(2).growX().height(55).padTop(6).row();
        Table status = new Table(); status.background(rowBackground); status.pad(10);
        status.add(wrapped(ownStatus(origins), .47f, StyleFactory.SOFT_YELLOW)).growX();
        own.add(status).colspan(2).growX().height(90).padTop(6);
        right.add(own).growX().height(373).padBottom(12).row();
        Table remaining = panel(); remaining.top();
        remaining.add(section(revealed == result.size() && !result.isEmpty() ? "PÓDIO DA LOTERIA" : "RESTANTES NA LOTERIA")).left().padBottom(9).row();
        Table list = new Table(); list.top();
        if (revealed == result.size() && !result.isEmpty()) {
            for (int i = 0; i < Math.min(3, result.size()); i++) list.add(orderRow(i, true, true)).growX().height(52).padBottom(5).row();
        } else for (Club origin : projected) if (!wasRevealed(origin)) {
            Table row = new Table(); row.add(crest(origin)).size(42).padRight(7);
            row.add(wrapped(pickLabel(origin), .43f, ownsPick(origin) ? GOLD : Color.WHITE)).growX();
            list.add(row).growX().height(49).row();
        }
        remaining.add(scroll(list)).grow().minHeight(0); right.add(remaining).grow().minHeight(0); return right;
    }
    private Table rules() {
        Table table = panel(); table.top(); table.add(section("COMO FUNCIONA")).left().padBottom(9).row();
        table.add(wrapped("• Participam as franquias fora dos playoffs.\n• O sorteio define o top 4; as demais seguem a campanha.\n• Picks trocadas mantêm seu proprietário atual.", .40f, Color.LIGHT_GRAY)).growX(); return table;
    }
    private Table orderBoard() {
        Table panel = panel(); panel.top();
        panel.add(section(phase == Phase.COMPLETE ? "ORDEM OFICIAL DA LOTERIA" : "PROJEÇÃO • POSIÇÕES NÃO REVELADAS NÃO SÃO FINAIS")).left().padBottom(8).row();
        List<Club> provisional = new ArrayList<>();
        for (Club origin : projected) if (!wasRevealed(origin)) provisional.add(origin);
        for (int i = result.size() - revealed; i < result.size(); i++) if (i >= 0) provisional.add(result.get(i));
        Table rows = new Table();
        for (int i = 0; i < provisional.size(); i++) {
            Club origin = provisional.get(i);
            Label cell = label((i + 1) + ". " + pickLabel(origin), .36f, ownsPick(origin) ? GOLD : Color.LIGHT_GRAY); cell.setEllipsis(true);
            rows.add(cell).growX().uniformX().minWidth(0).left().height(25).padRight(12); if (i % 4 == 3) rows.row();
        }
        panel.add(scroll(rows)).grow().minHeight(0); return panel;
    }
    private Table footer() {
        Table table = new Table();
        table.add(button("VOLTAR À OFF SEASON", () -> game.setScreen(new OffSeasonScreen(game, club)), false)).width(315).height(48).padRight(15);
        table.add(wrapped("Próxima etapa: SCOUTING E WORKOUTS • 2–19 DEZ", .39f, Color.LIGHT_GRAY)).growX();
        TextButton skip = button("PULAR ANIMAÇÕES", this::skip, false); skip.setName("lottery-skip"); skip.setDisabled(phase == Phase.COMPLETE);
        table.add(skip).width(250).height(45).padRight(10);
        table.add(button("REGRAS", () -> LeagueRulesDialog.show(stage, game), false)).width(145).height(45); return table;
    }
    private void revealNext() {
        if (phase != Phase.READY || !ensureResult()) return;
        if (revealed >= result.size()) { phase = Phase.COMPLETE; refresh(); return; }
        phase = Phase.ANTICIPATION; elapsed = 0; refresh();
    }
    private boolean ensureResult() {
        if (!result.isEmpty()) return true;
        try { game.league.runDraftLottery(); result = lotteryOrder(); return true; }
        catch (IllegalStateException error) {
            automatic = false; Dialog dialog = new Dialog("LOTERIA INDISPONÍVEL", game.skin);
            dialog.text(error.getMessage()); dialog.button("VOLTAR"); dialog.show(stage); return false;
        }
    }
    private List<Club> lotteryOrder() {
        List<Club> order = new ArrayList<>();
        for (Club origin : game.league.getDraftLotteryOrder()) if (projected.contains(origin)) order.add(origin); return order;
    }
    private void skip() {
        if (!ensureResult()) return;
        revealed = result.size(); automatic = false; phase = Phase.COMPLETE; elapsed = 0; refresh();
    }
    private int currentPick() { return phase == Phase.ANTICIPATION ? result.size() - revealed : Math.max(1, result.size() - revealed + 1); }
    private boolean isKnown(int index) { return index >= 0 && !result.isEmpty() && index >= result.size() - revealed; }
    private boolean wasRevealed(Club origin) { return !result.isEmpty() && isKnown(result.indexOf(origin)); }
    private String ownStatus(List<Club> origins) {
        if (origins.isEmpty()) return "SEM PICK NA LOTERIA\nSuas escolhas seguem a ordem da campanha.";
        List<String> known = new ArrayList<>(); int remaining = 0;
        for (Club origin : origins) { if (wasRevealed(origin)) known.add("#" + (result.indexOf(origin) + 1)); else remaining++; }
        if (remaining > 0 && result.size() - revealed <= 3 && !result.isEmpty()) return "TOP 3 GARANTIDO!\nSua franquia continua na disputa.";
        if (remaining > 0) return (known.isEmpty() ? "SUA PICK AINDA NÃO FOI REVELADA" : "PICKS REVELADAS: " + String.join(", ", known)) + "\nTOP 3 AINDA POSSÍVEL";
        return "SUAS ESCOLHAS: " + String.join(", ", known) + "\nDestino definido para o próximo Draft.";
    }
    private String ownedPicks() {
        List<String> rounds = new ArrayList<>();
        for (int round = 1; round <= 2; round++) {
            List<Integer> known = new ArrayList<>(); int unknown = 0;
            for (Club holder : game.league.getClubs()) for (DraftPick pick : holder.getDraftPicks()) {
                if (pick.getRound() != round || pick.getCurrentOwner() != club || pick.getYear() != game.league.getCurrentSeason() + 1) continue;
                int position = phase == Phase.COMPLETE ? game.league.getDraftLotteryOrder().indexOf(pick.getOriginalOwner()) : result.indexOf(pick.getOriginalOwner());
                if (position >= 0 && (phase == Phase.COMPLETE || isKnown(position))) known.add(position + 1 + (round - 1) * DraftOrderService.picksPerRound(game.league, pick.getYear()));
                else unknown++;
            }
            Collections.sort(known); List<String> labels = new ArrayList<>();
            for (int i = 0; i < Math.min(3, known.size()); i++) labels.add("#" + known.get(i));
            if (known.size() > 3) labels.add("+" + (known.size() - 3) + " picks");
            if (unknown > 0) labels.add(unknown + " a definir");
            if (!labels.isEmpty()) rounds.add("R" + round + ": " + String.join(", ", labels));
        }
        return "PICKS ATUAIS: " + (rounds.isEmpty() ? "Nenhuma" : String.join(" | ", rounds));
    }
    private Club pickOwner(Club origin) {
        for (Club holder : game.league.getClubs()) for (DraftPick pick : holder.getDraftPicks())
            if (pick.getYear() == game.league.getCurrentSeason() + 1 && pick.getRound() == 1 && pick.getOriginalOwner() == origin) return pick.getCurrentOwner();
        return origin;
    }
    private boolean ownsPick(Club origin) { return pickOwner(origin) == club; }
    private String pickLabel(Club origin) { Club owner = pickOwner(origin); return owner == origin ? origin.getName() : owner.getName() + " (via " + origin.getName() + ")"; }
    private Table orderRow(int index, boolean known, boolean roomy) {
        Table row = new Table(); row.pad(3); Club origin = known ? result.get(index) : null;
        row.background(origin != null && ownsPick(origin) ? mineBackground : rowBackground);
        row.add(label("#" + (index + 1), .42f, GOLD)).width(42);
        if (origin != null) row.add(crest(pickOwner(origin))).size(roomy ? 38 : 23).padRight(3);
        Label name = label(origin == null ? "A REVELAR" : pickLabel(origin), roomy ? .40f : .34f, Color.WHITE); name.setEllipsis(true); name.setAlignment(Align.left);
        row.add(name).growX().minWidth(0).left(); return row;
    }
    private Table panel() { Table table = new Table(); table.background(panelBackground); table.pad(15); return table; }
    private Label section(String text) { return label(text, .48f, GOLD); }
    private Label label(String text, float scale, Color color) {
        Label label = ScreenUI.createBoldValue(game.skin, text, color, Align.center); label.setFontScale(scale * 1.3f); return label;
    }
    private Label wrapped(String text, float scale, Color color) { Label label = label(text, scale, color); label.setWrap(true); label.setAlignment(Align.left); return label; }
    private ScrollPane scroll(Table content) { ScrollPane scroll = new ScrollPane(content, game.skin); scroll.setScrollingDisabled(true, false); scroll.setFadeScrollBars(false); return scroll; }
    private void fact(Table facts, String name, String value) {
        Table row = new Table(); row.add(label(name, .38f, Color.LIGHT_GRAY)).expandX().left(); row.add(label(value, .40f, GOLD)).right(); facts.add(row).growX().height(29).row();
    }
    private TextButton button(String title, Runnable action, boolean primary) {
        TextButton button = primary ? ScreenUI.createPrimaryButton(game.skin, title) : ScreenUI.createInteractiveButton(title, game.skin); button.getLabel().setFontScale(.43f);
        button.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { if (!button.isDisabled()) action.run(); } }); return button;
    }
    private Actor crest(Club team) {
        String path = team.getLogoPath(); if (path == null || !Gdx.files.internal(path).exists()) return label("WFL", .5f, GOLD);
        Texture texture = logos.get(path); if (texture == null) { texture = ClubLogoAssets.load(path); logos.put(path, texture); }
        Image image = new Image(texture); image.setScaling(Scaling.fit); return image;
    }
    private String percent(double chance) { return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.1f%%", chance * 100); }
    private String movement(int actual, int projection) { int delta = projection - actual; return delta > 0 ? "SUBIU " + delta : delta < 0 ? "CAIU " + -delta : "MANTEVE"; }
    @Override public void render(float delta) {
        float dt = Math.min(.1f, delta); clock += dt; elapsed += dt;
        if (phase == Phase.ANTICIPATION) {
            int pick = currentPick(); float duration = pick == 1 ? 5.2f : pick <= 3 ? 3.8f : 1.55f;
            if (countdown != null) countdown.setText(pick <= 3 ? String.valueOf(Math.max(1, (int) Math.ceil((duration - elapsed) / (duration / 3)))) : "WFL");
            if (envelope != null) {
                envelope.setOrigin(Align.center); envelope.setRotation(MathUtils.sin(elapsed * 10) * (pick <= 3 ? 2.5f : 1.2f));
                envelope.setScale(1 + .035f * MathUtils.sin(elapsed * 5));
            }
            if (elapsed >= duration) { revealed++; phase = Phase.CELEBRATION; elapsed = 0; refresh(); }
        } else if (phase == Phase.CELEBRATION) {
            float duration = currentPick() == 1 ? 3.8f : currentPick() <= 3 ? 2.7f : 1.2f;
            if (elapsed >= duration) {
                phase = revealed == result.size() ? Phase.COMPLETE : Phase.READY; elapsed = 0; refresh();
                if (automatic && phase == Phase.READY) revealNext();
            }
        }
        if (hero != null) {
            hero.setOrigin(Align.center);
            hero.setScale(phase == Phase.CELEBRATION ? 1 + .07f * (float) Math.exp(-elapsed * 4) * MathUtils.sin(elapsed * 12) : 1);
            hero.getColor().a = phase == Phase.CELEBRATION ? Math.min(1, .2f + elapsed * 4) : 1;
        }
        Gdx.gl.glClearColor(.015f, .04f, .025f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(dt); stage.draw();
        if (revealScroll != null && !result.isEmpty() && currentPick() <= 3) {
            revealScroll.setScrollPercentY(1); revealScroll.updateVisualScroll();
        }
    }
    private class EnvelopeGraphic extends Actor {
        EnvelopeGraphic() { setTouchable(Touchable.disabled); }
        @Override public void draw(Batch batch, float parentAlpha) {
            Color saved = new Color(batch.getColor()); batch.setColor(GOLD.r, GOLD.g, GOLD.b, .75f * parentAlpha);
            float x = getX(), y = getY(), w = getWidth(), h = getHeight();
            line(batch, x + 6, y + h - 6, x + w / 2, y + h * .56f);
            line(batch, x + w / 2, y + h * .56f, x + w - 6, y + h - 6);
            batch.setColor(saved);
        }
    }
    private void line(Batch batch, float x, float y, float endX, float endY) {
        float dx = endX - x, dy = endY - y;
        batch.draw(pixel, x, y, 0, 0, (float) Math.sqrt(dx * dx + dy * dy), 1.5f, 1, 1,
            MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees, 0, 0, 1, 1, false, false);
    }
    /** Gold particles, orbiting suspense, moving light and a top-three confetti burst. */
    private class CeremonyEffects extends Actor {
        CeremonyEffects() { setTouchable(Touchable.disabled); }
        @Override public void draw(Batch batch, float parentAlpha) {
            Color saved = new Color(batch.getColor()); float cx = getX() + getWidth() / 2, cy = getY() + getHeight() / 2;
            boolean suspense = phase == Phase.ANTICIPATION, celebration = phase == Phase.CELEBRATION;
            int pick = currentPick(), particles = pick <= 3 && celebration ? (pick == 1 ? 95 : 60) : 25;
            for (int i = 0; i < particles; i++) {
                float angle = i * 2.39996f + clock * (suspense ? 1.7f : .18f);
                float radius = suspense ? 75 + i % 6 * 16 : 45 + i % 9 * 28;
                float x = cx + MathUtils.cos(angle) * radius, y = cy + MathUtils.sin(angle) * radius * .40f;
                float alpha = suspense ? .25f + .3f * MathUtils.sin(clock * 4 + i) : .14f;
                if (celebration) {
                    float progress = elapsed / (pick <= 3 ? 3.8f : 1.2f);
                    float speed = .35f + (i * 37 % 101) / 100f;
                    x = cx + MathUtils.cos(i * 2.39996f) * (25 + progress * 290 * speed);
                    y = cy + MathUtils.sin(i * 2.39996f) * (35 + progress * 175 * speed) - progress * progress * 110;
                    alpha = Math.max(0, 1 - progress);
                }
                if (celebration && pick == 2) batch.setColor(.75f, .83f, .90f, Math.max(0, alpha) * parentAlpha);
                else if (celebration && pick == 3) batch.setColor(.88f, .62f, .32f, Math.max(0, alpha) * parentAlpha);
                else batch.setColor(i % 3 == 0 ? 1f : GOLD.r, i % 3 == 0 ? .90f : GOLD.g, GOLD.b, Math.max(0, alpha) * parentAlpha);
                batch.draw(pixel, x, y, celebration && pick <= 3 ? 5 : 3, celebration && pick <= 3 ? 9 : 3);
            }
            if (suspense) {
                float sweep = (clock * 170) % (getWidth() + 90) - 90;
                for (int i = 0; i < 18; i++) {
                    batch.setColor(GOLD.r, GOLD.g, GOLD.b, .018f * (1 - i / 18f)); float x = getX() + sweep + i * 3;
                    if (x >= getX() && x < getRight()) batch.draw(pixel, x, getY() + 25, 3, getHeight() - 50);
                }
            }
            batch.setColor(saved);
        }
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { automatic = false; }
    @Override public void dispose() { if (disposed) return; disposed = true; stage.dispose(); pixel.dispose(); for (Texture logo : logos.values()) logo.dispose(); }
}
