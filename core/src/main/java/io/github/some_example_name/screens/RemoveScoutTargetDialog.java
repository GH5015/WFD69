package io.github.some_example_name.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.ScoutTarget;
import io.github.some_example_name.utils.StyleFactory;

public class RemoveScoutTargetDialog extends Dialog {
    private final ScoutTarget target;
    private final DraftScoutManager scoutManager;
    private final Runnable onRemovedCallback;

    public RemoveScoutTargetDialog(Skin skin, ScoutTarget target, DraftScoutManager scoutManager, Runnable onRemovedCallback) {
        super("", skin);
        this.target = target;
        this.scoutManager = scoutManager;
        this.onRemovedCallback = onRemovedCallback;

        buildUI(skin);
    }

    private void buildUI(Skin skin) {
        Table content = getContentTable();
        content.pad(20);

        Label nameLabel = new Label(target.getPlayer().getName().toUpperCase(), skin, "font-title");
        nameLabel.setFontScale(0.8f);
        nameLabel.setColor(StyleFactory.GOLD);
        nameLabel.setAlignment(Align.center);
        content.add(nameLabel).growX().padBottom(10).row();

        Label posLabel = new Label("Posição: " + target.getPlayer().getPrimaryPosition().name(), skin);
        Label knowLabel = new Label("Conhecimento Atual: " + target.getKnowledgePercentage() + "%", skin, "font-bold");
        knowLabel.setColor(StyleFactory.SOFT_YELLOW);

        content.add(posLabel).padBottom(5).row();
        content.add(knowLabel).padBottom(20).row();

        TextButton removeBtn = new TextButton("REMOVER DO SCOUTING", skin);
        removeBtn.getLabel().setColor(com.badlogic.gdx.graphics.Color.valueOf("FF4D4D"));
        
        button(removeBtn, true);
        button("CANCELAR", false);
        padBottom(15);
    }

    @Override
    protected void result(Object object) {
        if (Boolean.TRUE.equals(object)) {
            scoutManager.removeTarget(target);
            onRemovedCallback.run();
        }
    }
}
