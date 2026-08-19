package io.github.some_example_name.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

public class DraftSelectionDialog extends Dialog {
    private final DraftScoutManager scoutManager;
    private final List<Player> availableDraftPlayers;
    private final Runnable onPlayerAddedCallback;
    private final Stage parentStage;

    public DraftSelectionDialog(Skin skin, Stage stage, DraftScoutManager scoutManager, List<Player> draftPlayers, Runnable onPlayerAddedCallback) {
        super("BANCO DE CANDIDATOS", skin);
        this.parentStage = stage;
        this.scoutManager = scoutManager;
        this.availableDraftPlayers = draftPlayers;
        this.onPlayerAddedCallback = onPlayerAddedCallback;

        buildUI(skin);
    }

    private void buildUI(Skin skin) {
        Table contentTable = getContentTable();
        contentTable.pad(10);

        Table listTable = new Table();
        ScrollPane scrollPane = new ScrollPane(listTable, skin);

        for (Player player : availableDraftPlayers) {
            boolean alreadyScouted = scoutManager.getActiveTargets().stream()
                    .anyMatch(t -> t.getPlayer().getId().equals(player.getId()));

            if (!alreadyScouted) {
                TextButton playerBtn = new TextButton("+ " + player.getName() + " (" + player.getPrimaryPosition().name() + ")", skin);
                playerBtn.getLabel().setColor(StyleFactory.CREME_AGED);
                
                playerBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        hide();
                        ConfirmAddScoutDialog confirmDialog = new ConfirmAddScoutDialog(skin, player, scoutManager, onPlayerAddedCallback);
                        confirmDialog.show(parentStage);
                    }
                });

                listTable.add(playerBtn).growX().pad(4).row();
            }
        }

        contentTable.add(scrollPane).width(380).height(280).row();
        button("FECHAR", false);
    }
}
