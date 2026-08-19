package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.IconTextButton;
import io.github.some_example_name.utils.StyleFactory;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public final class CareerOverlay extends WidgetGroup {
    private static final Map<String, Texture> LOGOS = new HashMap<>();
    private final Main game;
    private final Club club;
    private final Table matchCard = new Table();
    private final Table dateCard = new Table();
    private final ImageTextButton advanceButton;

    public CareerOverlay(Main game, Club club) {
        this.game = game;
        this.club = club;
        setTouchable(Touchable.childrenOnly);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        matchCard.setSize(400, 96);
        matchCard.setPosition(Math.max(12, Gdx.graphics.getWidth() - 420),
            Math.max(12, Gdx.graphics.getHeight() - 116));
        addActor(matchCard);

        dateCard.setSize(220, 52);
        dateCard.setPosition(240, 18);
        addActor(dateCard);

        advanceButton = IconTextButton.create("AVANÇAR DATA", game.skin, "Icons8/icons8-relógio-50.png");
        advanceButton.setSize(260, 52);
        advanceButton.setPosition(Math.max(12, Gdx.graphics.getWidth() - 280), 18);
        advanceButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                advanceOneDay(game, club);
                com.badlogic.gdx.Screen currentScreen = game.getScreen();
                if (currentScreen != null) {
                    currentScreen.show();
                } else {
                    refresh();
                }
            }
        });
        addActor(advanceButton);
        refresh();
    }

    public static CareerOverlay attach(Stage stage, Main game, Club club) {
        CareerOverlay overlay = new CareerOverlay(game, club);
        stage.addActor(overlay);
        return overlay;
    }

    @Override
    public void act(float delta) {
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        matchCard.setPosition(Math.max(12, Gdx.graphics.getWidth() - 420),
            Math.max(12, Gdx.graphics.getHeight() - 116));
        advanceButton.setPosition(Math.max(12, Gdx.graphics.getWidth() - 280), 18);
        dateCard.setPosition(240, 18);
        super.act(delta);
    }

    private void refresh() {
        dateCard.clear();
        dateCard.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.06f, 0.96f), StyleFactory.GOLD));
        dateCard.pad(6, 16, 6, 16);

        Table seasonInfo = new Table();
        Label seasonLabel = new Label("TEMPORADA " + game.league.getCurrentSeason(), game.skin, "font-bold");
        seasonLabel.setFontScale(0.82f);
        seasonLabel.setColor(StyleFactory.GOLD);
        seasonInfo.add(seasonLabel).left().row();

        Date currentDate = game.league.getCurrentDate();
        String dateStr = currentDate != null ? new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(currentDate) : "DATA N/A";
        Label dateLabel = new Label(dateStr, game.skin);
        dateLabel.setFontScale(0.78f);
        dateLabel.setColor(StyleFactory.CREME_AGED);
        seasonInfo.add(dateLabel).left();

        dateCard.add(seasonInfo).left();

        matchCard.clear();
        matchCard.background(StyleFactory.createRoundedPanel(new Color(0.04f, 0.08f, 0.06f, 0.96f), StyleFactory.GOLD));
        Match next = game.league.getNextMatchForClub(club);

        if (next == null) {
            boolean isPlayoffs = "PLAYOFFS".equals(game.league.getCurrentStage());
            String text = isPlayoffs ? "PLAYOFFS FINALIZADOS" : "FASE REGULAR CONCLUÍDA";
            matchCard.add(new Label(text, game.skin, "font-bold")).center();

            advanceButton.setText(isPlayoffs ? "NOVA TEMPORADA 🏆" : "INICIAR PLAYOFFS ⚡");
            advanceButton.setDisabled(false);
            return;
        }

        Club opponent = next.getHomeTeam() == club ? next.getAwayTeam() : next.getHomeTeam();
        Table home = teamCell(club);
        Table away = teamCell(opponent);
        matchCard.add(home).width(160).center();

        Label vsLabel = new Label("VS", game.skin, "font-bold");
        vsLabel.setColor(StyleFactory.GOLD);
        vsLabel.setFontScale(0.9f);
        matchCard.add(vsLabel).width(36).center();

        matchCard.add(away).width(160).center();

        boolean canPlay = isMatchDay(next);
        advanceButton.setText(canPlay ? "JOGAR PARTIDA ⚽" : "AVANÇAR DIA 📅");
        advanceButton.setDisabled(false);
    }

    private Table teamCell(Club team) {
        Table cell = new Table();
        Image teamLogo = new Image(new TextureRegionDrawable(logo(team)));
        teamLogo.setScaling(Scaling.fit);
        cell.add(teamLogo).size(76, 42).row();
        Label name = new Label(team.getName(), game.skin, "font-bold");
        name.setFontScale(0.70f);
        name.setColor(StyleFactory.CREME_AGED);
        cell.add(name).width(150).center();
        return cell;
    }

    private boolean isMatchDay(Match userMatch) {
        return isMatchDay(game, club);
    }

    public static boolean isMatchDay(Main game, Club club) {
        Match userMatch = game.league.getNextMatchForClub(club);
        if (userMatch == null || game.league.getCurrentDate() == null) return false;
        Match nextGlobal = game.league.getNextMatch();
        boolean dateReached = !game.league.getCurrentDate().before(userMatch.getDate());
        return (nextGlobal == userMatch) && dateReached;
    }

    public static void advanceOneDay(Main game, Club club) {
        Match userMatch = game.league.getNextMatchForClub(club);

        // Se não há mais partidas para o clube na fase atual, transiciona de fase
        if (userMatch == null) {
            game.league.checkAndAdvanceStage();
            return;
        }

        // Se já está no dia do jogo -> abre a Tela de Pré-Jogo
        if (isMatchDay(game, club)) {
            game.setScreen(new PreMatchScreen(game, userMatch, club));
            return;
        }

        // Avança 1 dia
        game.league.advanceDateOneDay();

        // Recupera fadiga dos atletas
        recoverAllPlayers(game, 1);

        // Processa os jogos neutros que caem neste dia
        processDueMatches(game, club);
    }

    private static void processDueMatches(Main game, Club playerClub) {
        Match due = game.league.getNextMatch();
        Match userMatch = game.league.getNextMatchForClub(playerClub);
        while (due != null && due != userMatch && game.league.getCurrentDate() != null
            && !due.getDate().after(game.league.getCurrentDate())) {
            game.matchEngine.simulate(due);
            game.league.advanceMatch();
            due = game.league.getNextMatch();
        }
    }

    private static void recoverAllPlayers(Main game, int days) {
        for (Club team : game.league.getClubs()) {
            for (Player player : team.getSquad()) player.recover(days);
        }
    }

    private static Texture logo(Club team) {
        String path = team.getLogoPath();
        Texture texture = LOGOS.get(path);
        if (texture == null) {
            try { texture = new Texture(Gdx.files.internal(path)); }
            catch (Exception e) { texture = new Texture(Gdx.files.internal("libgdx.png")); }
            LOGOS.put(path, texture);
        }
        return texture;
    }

    public static void disposeAssets() {
        for (Texture texture : LOGOS.values()) texture.dispose();
        LOGOS.clear();
    }
}
