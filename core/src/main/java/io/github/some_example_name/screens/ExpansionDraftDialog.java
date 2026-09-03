package io.github.some_example_name.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.ScreenUI;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/** Proteção manual do clube do usuário; as outras franquias escolhem automaticamente. */
public final class ExpansionDraftDialog {
    private ExpansionDraftDialog() { }

    public static void show(Stage stage, Main game, Club club, Runnable completed) {
        int year = game.league.getCurrentSeason() + 1;
        boolean newcomer = LeagueExpansionService.prepare(game.league, year).contains(club);
        if (newcomer) {
            ExpansionRosterDialog.show(stage, game, club, completed);
            return;
        }
        Set<Player> protectedPlayers = new HashSet<>(LeagueExpansionService.suggestedProtection(club));
        int required = LeagueExpansionService.protectionLimit(club);
        Dialog dialog = new Dialog("WFL EXPANSION • " + year, game.skin);
        Table content = dialog.getContentTable();
        content.pad(16);
        String names = LeagueExpansionService.forYear(year).stream().map(f -> f.name).collect(Collectors.joining(" + "));
        Label description = new Label(names
            + "\nProteja " + required + " jogadores. Cada clube pode perder no máximo 3 atletas.\n"
            + "Até 15 proteções, deixando ao menos 3 atletas desprotegidos nos elencos menores.\n"
            + "As novas franquias formarão elencos de 20 jogadores.\n"
            + "Contratos vigentes são transferidos; jogadores livres ficam para a Free Agency.", game.skin);
        description.setWrap(true);
        content.add(description).width(820).padBottom(12).row();
        Label counter = new Label("Protegidos: " + protectedPlayers.size() + "/" + required, game.skin);
        counter.setWrap(true);
        content.add(counter).width(820).left().padBottom(8).row();
        Table roster = new Table();
        List<Player> players = new ArrayList<>(club.getSquad());
        players.sort(Comparator.comparingInt(Player::getOverall).reversed().thenComparing(Player::getName));
        for (Player player : players) {
            TextButton toggle = ScreenUI.createInteractiveButton(protectionText(player, protectedPlayers.contains(player)), game.skin);
            toggle.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (!protectedPlayers.remove(player)) {
                        if (protectedPlayers.size() >= required) {
                            counter.setText("Remova uma proteção antes de selecionar outro jogador (" + required + "/" + required + ").");
                            return;
                        }
                        protectedPlayers.add(player);
                    }
                    toggle.setText(protectionText(player, protectedPlayers.contains(player)));
                    counter.setText("Protegidos: " + protectedPlayers.size() + "/" + required);
                }
            });
            roster.add(toggle).growX().height(36).padBottom(3).row();
        }
        ScrollPane scroll = new ScrollPane(roster, game.skin);
        scroll.setScrollingDisabled(true, false);
        content.add(scroll).width(820).height(Math.min(390, players.size() * 39)).row();
        TextButton confirm = ScreenUI.createPrimaryButton(game.skin, "CONFIRMAR PROTEÇÕES E REALIZAR EXPANSION DRAFT");
        confirm.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (protectedPlayers.size() != required) {
                    counter.setText("Selecione " + required + " jogadores antes de confirmar.");
                    return;
                }
                List<String> log;
                try { log = LeagueExpansionService.runDraft(game.league, club, protectedPlayers); }
                catch (IllegalArgumentException failure) { counter.setText(failure.getMessage()); return; }
                dialog.hide();
                completed.run();
                Dialog result = new Dialog("WFL EXPANSION CONCLUÍDO", game.skin);
                Label text = new Label(String.join("\n", log), game.skin);
                text.setWrap(true);
                Table report = new Table(); report.add(text).width(820).pad(12);
                ScrollPane reportScroll = new ScrollPane(report, game.skin);
                reportScroll.setScrollingDisabled(true, false);
                result.getContentTable().add(reportScroll).width(860).height(460);
                result.button("CONTINUAR OFF SEASON");
                result.show(stage);
            }
        });
        dialog.getButtonTable().add(confirm).width(640).height(46);
        dialog.button("VOLTAR");
        dialog.show(stage);
    }

    private static String protectionText(Player player, boolean protectedPlayer) {
        return (protectedPlayer ? "[PROTEGIDO]  " : "[DISPONÍVEL]  ") + player.getName()
            + "  •  " + player.getPrimaryPosition() + "  •  OVR " + player.getOverall() + "  •  " + player.getAge() + " anos";
    }
}
