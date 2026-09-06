package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.GameSettings;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

/** Painel de configurações acessível pela tela inicial. */
final class SettingsDialog {
    private static final String WINDOWED = "JANELA";
    private static final String FULLSCREEN = "TELA CHEIA";
    private static final String ENABLED = "ATIVADO";
    private static final String DISABLED = "DESATIVADO";

    private SettingsDialog() { }

    static void show(Main game, Stage stage) {
        Dialog dialog = new Dialog("", game.skin);
        dialog.setName("settings-dialog");
        dialog.setModal(true);
        dialog.setMovable(false);

        Table content = dialog.getContentTable();
        content.pad(28f, 34f, 22f, 34f);
        content.setBackground(StyleFactory.createMetallicBoard(760, 610, Color.valueOf("081611")));

        Label title = ScreenUI.createSectionTitle(game.skin, "CONFIGURAÇÕES");
        title.setAlignment(Align.center);
        title.setFontScale(1.18f);
        content.add(title).colspan(2).growX().padBottom(5f).row();
        Label subtitle = ScreenUI.createSubtitle(game.skin, "Ajuste a exibição e o desempenho do jogo.");
        subtitle.setAlignment(Align.center);
        content.add(subtitle).colspan(2).growX().padBottom(22f).row();

        SelectBox<String> displayMode = select(game, WINDOWED, FULLSCREEN);
        displayMode.setName("settings-display-mode");
        displayMode.setSelected(GameSettings.isFullscreen() ? FULLSCREEN : WINDOWED);

        SelectBox<ResolutionOption> resolution = new SelectBox<>(game.skin);
        resolution.setName("settings-resolution");
        Array<ResolutionOption> resolutions = new Array<>();
        for (int[] option : GameSettings.RESOLUTIONS) {
            resolutions.add(new ResolutionOption(option[0], option[1]));
        }
        resolution.setItems(resolutions);
        selectResolution(resolution, GameSettings.getWindowWidth(), GameSettings.getWindowHeight());

        SelectBox<String> vsync = select(game, ENABLED, DISABLED);
        vsync.setName("settings-vsync");
        vsync.setSelected(GameSettings.isVsyncEnabled() ? ENABLED : DISABLED);

        SelectBox<FpsOption> fps = new SelectBox<>(game.skin);
        fps.setName("settings-fps-limit");
        Array<FpsOption> fpsOptions = new Array<>();
        for (int value : GameSettings.FPS_LIMITS) fpsOptions.add(new FpsOption(value));
        fps.setItems(fpsOptions);
        selectFps(fps, GameSettings.getFpsLimit());

        SelectBox<VolumeOption> volume = new SelectBox<>(game.skin);
        volume.setName("settings-volume");
        Array<VolumeOption> volumeOptions = new Array<>();
        for (int value : GameSettings.VOLUME_LEVELS) volumeOptions.add(new VolumeOption(value));
        volume.setItems(volumeOptions);
        selectVolume(volume, GameSettings.getVolumePercent());

        addRow(game, content, "MODO DE EXIBIÇÃO", displayMode);
        addRow(game, content, "RESOLUÇÃO DA JANELA", resolution);
        addRow(game, content, "SINCRONIZAÇÃO VERTICAL", vsync);
        addRow(game, content, "LIMITE DE QUADROS", fps);
        addRow(game, content, "VOLUME DA INTRODUÇÃO", volume);

        Label note = ScreenUI.createSubtitle(
            game.skin,
            "A resolução é usada no modo janela. Em tela cheia, o jogo utiliza a resolução atual do monitor."
        );
        note.setWrap(true);
        note.setAlignment(Align.center);
        content.add(note).colspan(2).growX().height(58f).padTop(12f).row();

        TextButton defaults = ScreenUI.createInteractiveButton("RESTAURAR PADRÃO", game.skin);
        defaults.setName("settings-restore-defaults");
        defaults.addListener(action(() -> {
            displayMode.setSelected(WINDOWED);
            selectResolution(resolution, 1280, 720);
            vsync.setSelected(ENABLED);
            selectFps(fps, 120);
            selectVolume(volume, 100);
        }));

        TextButton cancel = ScreenUI.createInteractiveButton("CANCELAR", game.skin);
        cancel.setName("settings-cancel");
        cancel.addListener(action(dialog::hide));

        TextButton apply = ScreenUI.createPrimaryButton(game.skin, "APLICAR");
        apply.setName("settings-apply");
        apply.addListener(action(() -> {
            ResolutionOption selectedResolution = resolution.getSelected();
            GameSettings.saveAndApply(
                FULLSCREEN.equals(displayMode.getSelected()),
                ENABLED.equals(vsync.getSelected()),
                selectedResolution.width,
                selectedResolution.height,
                fps.getSelected().value,
                volume.getSelected().value
            );
            dialog.hide();
        }));

        dialog.getButtonTable().add(defaults).width(235f).height(48f).pad(10f, 8f, 12f, 8f);
        dialog.getButtonTable().add(cancel).width(165f).height(48f).pad(10f, 8f, 12f, 8f);
        dialog.getButtonTable().add(apply).width(185f).height(48f).pad(10f, 8f, 12f, 8f);
        dialog.show(stage);
        dialog.setSize(760f, 610f);
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) / 2f, (stage.getHeight() - dialog.getHeight()) / 2f);
    }

    private static SelectBox<String> select(Main game, String... options) {
        SelectBox<String> select = new SelectBox<>(game.skin);
        select.setItems(options);
        return select;
    }

    private static void addRow(Main game, Table table, String title, Actor control) {
        Label label = ScreenUI.createBoldValue(game.skin, title, StyleFactory.CREME_AGED, Align.left);
        table.add(label).growX().left().height(50f).padRight(22f);
        table.add(control).width(300f).height(42f).right().padBottom(8f).row();
    }

    private static void selectResolution(SelectBox<ResolutionOption> select, int width, int height) {
        ResolutionOption closest = select.getItems().first();
        long distance = Long.MAX_VALUE;
        for (ResolutionOption option : select.getItems()) {
            long dx = option.width - width;
            long dy = option.height - height;
            long candidate = dx * dx + dy * dy;
            if (candidate < distance) {
                closest = option;
                distance = candidate;
            }
        }
        select.setSelected(closest);
    }

    private static void selectFps(SelectBox<FpsOption> select, int requested) {
        FpsOption closest = select.getItems().first();
        for (FpsOption option : select.getItems()) {
            if (Math.abs(option.value - requested) < Math.abs(closest.value - requested)) closest = option;
        }
        select.setSelected(closest);
    }

    private static void selectVolume(SelectBox<VolumeOption> select, int requested) {
        VolumeOption closest = select.getItems().first();
        for (VolumeOption option : select.getItems()) {
            if (Math.abs(option.value - requested) < Math.abs(closest.value - requested)) closest = option;
        }
        select.setSelected(closest);
    }

    private static ChangeListener action(Runnable runnable) {
        return new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { runnable.run(); }
        };
    }

    private static final class ResolutionOption {
        final int width;
        final int height;

        ResolutionOption(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override public String toString() { return width + " × " + height; }
    }

    private static final class FpsOption {
        final int value;

        FpsOption(int value) { this.value = value; }

        @Override public String toString() { return value + " FPS"; }
    }

    private static final class VolumeOption {
        final int value;

        VolumeOption(int value) { this.value = value; }

        @Override public String toString() { return value + "%"; }
    }
}
