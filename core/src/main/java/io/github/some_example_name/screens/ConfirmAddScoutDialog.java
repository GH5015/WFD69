package io.github.some_example_name.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;

public class ConfirmAddScoutDialog extends Dialog {
    private final Player player;
    private final DraftScoutManager scoutManager;
    private final Runnable onAddedCallback;

    public ConfirmAddScoutDialog(Skin skin, Player player, DraftScoutManager scoutManager, Runnable onAddedCallback) {
        super("", skin);
        this.player = player;
        this.scoutManager = scoutManager;
        this.onAddedCallback = onAddedCallback;

        buildUI(skin);
    }

    private void buildUI(Skin skin) {
        Table content = getContentTable();
        content.pad(20);

        Label nameLabel = new Label(player.getName().toUpperCase(), skin, "font-title");
        nameLabel.setFontScale(0.8f);
        nameLabel.setColor(StyleFactory.GOLD);
        nameLabel.setAlignment(Align.center);
        content.add(nameLabel).growX().padBottom(10).row();

        Label knowLabel = new Label("Conhecimento inicial: 0%", skin, "font-bold");
        knowLabel.setColor(StyleFactory.CREME_AGED);
        content.add(knowLabel).padBottom(10).row();

        Label questionLabel = new Label("Iniciar observação?", skin);
        content.add(questionLabel).padBottom(20).row();

        button("SIM", true);
        button("NÃO", false);
        padBottom(15);
    }

    @Override
    protected void result(Object object) {
        if (Boolean.TRUE.equals(object)) {
            if (scoutManager.addTarget(player)) {
                onAddedCallback.run();
            }
        }
    }
}
