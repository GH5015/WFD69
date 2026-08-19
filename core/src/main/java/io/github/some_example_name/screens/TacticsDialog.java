package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.StyleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TacticsDialog extends Dialog {
    private static final int MAX_SUBSTITUTIONS = 5;

    private final Main game;
    private final Club club;
    private final Runnable onCloseCallback;
    private final BiConsumer<Player, Player> onSubstitutionListener;
    private final Runnable onTacticsChangedListener;

    private Player selectedPlayer = null;
    private Integer selectedSlot = null;
    private Texture pitchTexture;
    private Texture kitTexture;
    private Table contentTable;
    private Label subCounterLabel;

    private final List<Player> substitutedPlayers;
    private int substitutionsUsed;

    public TacticsDialog(Main game, Club club,
                         int initialSubstitutionsUsed,
                         List<Player> substitutedPlayers,
                         Runnable onCloseCallback,
                         BiConsumer<Player, Player> onSubstitutionListener,
                         Runnable onTacticsChangedListener) {
        super("", game.skin);
        this.game = game;
        this.club = club;
        this.substitutionsUsed = initialSubstitutionsUsed;
        this.substitutedPlayers = substitutedPlayers != null ? substitutedPlayers : new ArrayList<>();
        this.onCloseCallback = onCloseCallback;
        this.onSubstitutionListener = onSubstitutionListener;
        this.onTacticsChangedListener = onTacticsChangedListener;

        if (Gdx.files.internal("uniforme_santos.png").exists()) {
            kitTexture = new Texture(Gdx.files.internal("uniforme_santos.png"));
        }
        generate2DPitchTexture();

        ensureStartersPopulated();

        setModal(true);
        setMovable(false);
        buildUI();
    }

    private void ensureStartersPopulated() {
        Formation f = club.getFormation();
        if (f == null && Formation.values().length > 0) {
            f = Formation.values()[0];
            club.setFormation(f);
        }

        Map<Integer, Player> map = club.getTacticsMap();
        List<Player> startingXI = club.getStartingXI();

        for (int i = 0; i < 11; i++) {
            if (!map.containsKey(i) || map.get(i) == null) {
                if (i < startingXI.size()) {
                    map.put(i, startingXI.get(i));
                } else if (i < club.getSquad().size()) {
                    map.put(i, club.getSquad().get(i));
                }
            }
        }
    }

    private void buildUI() {
        getContentTable().clear();

        Table root = new Table();
        root.background(StyleFactory.createRoundedPanel(StyleFactory.PRUSSIAN_GREEN, StyleFactory.GOLD));
        root.pad(14);

        Table headerTable = new Table();
        Label titleLbl = new Label("ESCALAÇÃO & SUBSTITUIÇÕES", game.skin, "font-bold", Color.WHITE);
        titleLbl.setFontScale(1.05f);
        headerTable.add(titleLbl).left().padRight(15);

        headerTable.add(new Label("FORMAÇÃO:", game.skin, "font-bold", StyleFactory.GOLD)).padRight(6);

        final SelectBox<Formation> selectBox = new SelectBox<>(game.skin);
        selectBox.setItems(Formation.values());
        if (club.getFormation() != null) {
            selectBox.setSelected(club.getFormation());
        }
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                club.setFormation(selectBox.getSelected());
                if (onTacticsChangedListener != null) {
                    onTacticsChangedListener.run();
                }
                refreshContent();
            }
        });
        headerTable.add(selectBox).width(130).padRight(15);

        subCounterLabel = new Label("SUBS: " + substitutionsUsed + "/" + MAX_SUBSTITUTIONS, game.skin, "font-bold",
            substitutionsUsed >= MAX_SUBSTITUTIONS ? Color.RED : StyleFactory.SOFT_YELLOW);
        headerTable.add(subCounterLabel).left();

        headerTable.add().expandX();

        TextButton btnClose = new TextButton(" X ", game.skin);
        btnClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        });
        headerTable.add(btnClose).right().height(34);

        root.add(headerTable).growX().padBottom(12).row();

        contentTable = new Table();
        root.add(contentTable).width(1120).height(580).row();
        refreshContent();

        getContentTable().add(root);
    }

    private void refreshContent() {
        if (subCounterLabel != null) {
            subCounterLabel.setText("SUBS: " + substitutionsUsed + "/" + MAX_SUBSTITUTIONS);
            subCounterLabel.setColor(substitutionsUsed >= MAX_SUBSTITUTIONS ? Color.RED : StyleFactory.SOFT_YELLOW);
        }

        contentTable.clear();

        Table leftArea = new Table();
        leftArea.add(createPitchLayout2D()).grow().padBottom(8).row();
        leftArea.add(createSelectedPlayerPanel()).growX().height(80);

        contentTable.add(leftArea).expand().fill().padRight(12);
        contentTable.add(createSquadPanelContent()).width(380).growY();
    }

    private Table createPitchLayout2D() {
        Table pitch = new Table();
        pitch.background(new TextureRegionDrawable(new TextureRegion(pitchTexture)));
        pitch.pad(8);

        Formation f = club.getFormation();
        if (f == null || f.getPositionSlots() == null) return pitch;

        List<String> slots = f.getPositionSlots();

        Table stRow = createRowTable();
        Table wingRow = createRowTable();
        Table camRow = createRowTable();
        Table midRow = createRowTable();
        Table cdmRow = createRowTable();
        Table wingbackRow = createRowTable();
        Table defRow = createRowTable();
        Table gkRow = createRowTable();

        for (int i = 0; i < Math.min(11, slots.size()); i++) {
            final int slotIndex = i;
            String targetPos = slots.get(slotIndex);
            Player p = club.getTacticsMap().get(slotIndex);

            Table card = createMiniPlayerCard(p, targetPos, slotIndex);
            int layer = getPositionDepthLayer(targetPos);

            switch (layer) {
                case 7: stRow.add(card).pad(2, 5, 2, 5); break;
                case 6: wingRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 5: camRow.add(card).pad(2, 5, 2, 5); break;
                case 4: midRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 3: cdmRow.add(card).pad(2, 5, 2, 5); break;
                case 2: wingbackRow.add(card).expandX().pad(2, 4, 2, 4); break;
                case 1: defRow.add(card).expandX().pad(2, 4, 2, 4); break;
                default: gkRow.add(card).pad(2); break;
            }
        }

        pitch.add(stRow).expand().fillX().center().row();
        pitch.add(wingRow).expand().fillX().center().row();
        pitch.add(camRow).expand().center().row();
        pitch.add(midRow).expand().fillX().center().row();
        pitch.add(cdmRow).expand().center().row();
        pitch.add(wingbackRow).expand().fillX().center().row();
        pitch.add(defRow).expand().fillX().center().row();
        pitch.add(gkRow).expand().center().row();

        return pitch;
    }

    private Table createRowTable() {
        Table row = new Table();
        row.center();
        return row;
    }

    private Table createMiniPlayerCard(final Player p, String targetPos, final int slotIndex) {
        Table outerCard = new Table();
        outerCard.setTransform(true);
        outerCard.setOrigin(Align.center);

        boolean isSelected = (p != null && p == selectedPlayer);
        boolean isSubstitutedOut = (p != null && substitutedPlayers.contains(p));
        boolean isInjured = (p != null && p.isInjured());
        boolean isSuspended = (p != null && p.isSuspended());

        Color borderColor = isSelected ? StyleFactory.GOLD : StyleFactory.DARK_GOLD;

        Stack stack = new Stack();

        Table kitLayer = new Table();
        kitLayer.top();
        if (p != null && kitTexture != null) {
            Image kitImg = new Image(kitTexture);
            kitLayer.add(kitImg).size(76, 76).padTop(-38).center();
        }

        Table cardFront = new Table();
        Color cardBg = (isSubstitutedOut || isInjured || isSuspended) ? Color.valueOf("2A1A1A") : StyleFactory.METAL_DARK;
        cardFront.background(StyleFactory.createRoundedPanel(cardBg, borderColor));
        cardFront.pad(3);

        if (p != null) {
            int effOvr = p.getEffectiveOverallForPosition(targetPos);
            boolean isOutOfPosition = !p.getPosition().equalsIgnoreCase(targetPos) || effOvr < p.getOverall();

            String displayName = p.getName();
            if (isSubstitutedOut) displayName += " 🔄";
            else if (isInjured) displayName += " 🚑";
            else if (isSuspended) displayName += " ⛔";
            else if (isOutOfPosition) displayName += " ⚠";

            Color nameColor = (isInjured || isSuspended) ? Color.valueOf("FF4D4D") : (isSubstitutedOut ? Color.GRAY : (isOutOfPosition ? Color.valueOf("FF9800") : Color.WHITE));

            Label nameLbl = new Label(displayName, game.skin, "font-bold", nameColor);
            nameLbl.setFontScale(0.65f);
            nameLbl.setEllipsis(true);
            cardFront.add(nameLbl).width(80).center().padTop(1).padBottom(2).row();

            Label posOvrLbl = new Label(
                targetPos.toUpperCase() + " " + effOvr,
                game.skin,
                "font-bold",
                (isInjured || isSuspended) ? Color.GRAY : StyleFactory.SOFT_YELLOW
            );
            posOvrLbl.setFontScale(0.65f);
            cardFront.add(posOvrLbl).padBottom(2).row();

            float fatigue = p.getFatigue();
            cardFront.add(createFatigueBar(fatigue)).width(70).height(4).row();
        } else {
            Label posLbl = new Label(targetPos.toUpperCase(), game.skin, "font-bold", Color.GRAY);
            posLbl.setFontScale(0.75f);
            cardFront.add(posLbl).padBottom(1).row();

            Label emptyLbl = new Label("[ VAZIO ]", game.skin, "font-label", Color.LIGHT_GRAY);
            emptyLbl.setFontScale(0.60f);
            cardFront.add(emptyLbl).row();
        }

        stack.add(kitLayer);
        stack.add(cardFront);
        outerCard.add(stack).grow();

        outerCard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (p != null && substitutedPlayers.contains(p)) return;

                if (selectedPlayer != null && selectedSlot == null) {
                    if (substitutionsUsed >= MAX_SUBSTITUTIONS) return;

                    if (selectedPlayer.canPlay() && !substitutedPlayers.contains(selectedPlayer)) {
                        Player oldStarter = club.getTacticsMap().get(slotIndex);
                        if (oldStarter != null) {
                            substitutedPlayers.add(oldStarter);
                            substitutionsUsed++;

                            if (onSubstitutionListener != null) {
                                onSubstitutionListener.accept(oldStarter, selectedPlayer);
                            }
                        }

                        club.assignPlayerToSlot(slotIndex, selectedPlayer);
                        if (onTacticsChangedListener != null) {
                            onTacticsChangedListener.run();
                        }
                        selectedPlayer = null;
                        selectedSlot = null;
                    }
                }
                else if (selectedSlot != null && selectedSlot != slotIndex) {
                    Player p1 = club.getTacticsMap().get(selectedSlot);
                    Player p2 = club.getTacticsMap().get(slotIndex);

                    if ((p1 == null || !substitutedPlayers.contains(p1)) && (p2 == null || !substitutedPlayers.contains(p2))) {
                        club.assignPlayerToSlot(selectedSlot, p2);
                        club.assignPlayerToSlot(slotIndex, p1);
                        if (onTacticsChangedListener != null) {
                            onTacticsChangedListener.run();
                        }
                    }
                    selectedPlayer = null;
                    selectedSlot = null;
                }
                else if (p != null) {
                    selectedPlayer = p;
                    selectedSlot = slotIndex;
                } else {
                    selectedPlayer = null;
                    selectedSlot = null;
                }
                refreshContent();
            }
        });

        return outerCard;
    }

    private Table createSelectedPlayerPanel() {
        Table panel = new Table();
        panel.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        panel.pad(6);

        if (selectedPlayer == null) {
            String msg = (substitutionsUsed >= MAX_SUBSTITUTIONS)
                ? "Limite de 5 substituições atingido nesta partida"
                : "Clique em um titular no campo para trocar por um reserva do banco";
            Label noSel = new Label(msg, game.skin, "font-label", Color.LIGHT_GRAY);
            noSel.setFontScale(0.82f);
            panel.add(noSel).center();
            return panel;
        }

        Table infoTable = new Table();
        infoTable.defaults().left().padRight(12);

        Label nameLbl = new Label(selectedPlayer.getName().toUpperCase(), game.skin, "font-bold", StyleFactory.GOLD);
        nameLbl.setFontScale(0.88f);

        Label posLbl = new Label("Posição: " + selectedPlayer.getPosition(), game.skin, "font-label", Color.WHITE);
        posLbl.setFontScale(0.78f);

        Label ovrLbl = new Label("OVR: " + selectedPlayer.getOverall(), game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        ovrLbl.setFontScale(0.78f);

        String statusText = "Pronto";
        Color statusColor = Color.GREEN;
        if (selectedPlayer.isInjured()) {
            statusText = "LESIONADO (" + selectedPlayer.getInjuryDuration() + " jogos)";
            statusColor = Color.valueOf("FF4D4D");
        } else if (selectedPlayer.isSuspended()) {
            statusText = "SUSPENSO (" + selectedPlayer.getSuspendedMatches() + " jogos)";
            statusColor = Color.valueOf("FF4D4D");
        }

        Label statusLbl = new Label("Status: " + statusText, game.skin, "font-bold", statusColor);
        statusLbl.setFontScale(0.78f);

        infoTable.add(nameLbl).colspan(2).padBottom(2).row();
        infoTable.add(posLbl);
        infoTable.add(ovrLbl).row();
        infoTable.add(statusLbl).colspan(2);

        panel.add(infoTable).expand().fill();
        return panel;
    }

    private Table createSquadPanelContent() {
        Table content = new Table();
        content.background(StyleFactory.createRoundedPanel(StyleFactory.METAL_DARK, StyleFactory.GOLD));
        content.pad(8);

        List<Player> starters = new ArrayList<>(club.getTacticsMap().values());

        List<Player> benchPlayers = new ArrayList<>();
        for (Player p : club.getSquad()) {
            if (!starters.contains(p) && !substitutedPlayers.contains(p) && p.canPlay()) {
                benchPlayers.add(p);
                if (benchPlayers.size() == 7) break;
            }
        }

        Table listContent = new Table();
        listContent.top().defaults().growX().padBottom(5);

        Label benchTitle = new Label("BANCO DE RESERVAS (" + (MAX_SUBSTITUTIONS - substitutionsUsed) + " SUBS RESTANTES)", game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        benchTitle.setFontScale(0.82f);
        listContent.add(benchTitle).left().padTop(4).padBottom(6).row();

        if (benchPlayers.isEmpty()) {
            Label emptyLbl = new Label("Nenhum reserva disponível", game.skin, "font-label", Color.GRAY);
            emptyLbl.setFontScale(0.80f);
            listContent.add(emptyLbl).left().padTop(10).row();
        } else {
            for (Player p : benchPlayers) {
                listContent.add(createSquadRow(p)).row();
            }
        }

        ScrollPane scroll = new ScrollPane(listContent, game.skin);
        scroll.setFadeScrollBars(false);
        content.add(scroll).grow();

        return content;
    }

    private Table createSquadRow(final Player p) {
        Table row = new Table();
        row.setTransform(true);
        row.setOrigin(Align.center);

        boolean isSelected = (p == selectedPlayer);
        Color bg = isSelected ? StyleFactory.WINE_RED : StyleFactory.METAL_DARK;
        row.background(StyleFactory.createRoundedPanel(bg, isSelected ? StyleFactory.GOLD : Color.CLEAR));
        row.pad(6);

        Label posLbl = new Label(p.getPosition(), game.skin, "font-bold", StyleFactory.GOLD);
        posLbl.setFontScale(0.82f);

        String displayName = p.getName();
        if (p.isInjured()) displayName += " 🚑";
        else if (p.isSuspended()) displayName += " ⛔";

        Label nameLbl = new Label(displayName, game.skin, "font-label", p.canPlay() ? Color.WHITE : Color.valueOf("FF4D4D"));
        nameLbl.setFontScale(0.82f);
        nameLbl.setEllipsis(true);

        Label ovrLbl = new Label(String.valueOf(p.getOverall()), game.skin, "font-bold", StyleFactory.SOFT_YELLOW);
        ovrLbl.setFontScale(0.82f);

        row.add(posLbl).width(40).left();
        row.add(nameLbl).expandX().left().padLeft(6);
        row.add(ovrLbl).right().width(35);

        row.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (substitutedPlayers.contains(p)) return;

                if (selectedSlot != null && p.canPlay()) {
                    if (substitutionsUsed >= MAX_SUBSTITUTIONS) return;

                    Player oldStarter = club.getTacticsMap().get(selectedSlot);
                    if (oldStarter != null) {
                        substitutedPlayers.add(oldStarter);

                        if (onSubstitutionListener != null) {
                            onSubstitutionListener.accept(oldStarter, p);
                        }
                    }

                    club.assignPlayerToSlot(selectedSlot, p);
                    if (onTacticsChangedListener != null) {
                        onTacticsChangedListener.run();
                    }
                    selectedPlayer = null;
                    selectedSlot = null;
                }
                else {
                    if (selectedPlayer == p) {
                        selectedPlayer = null;
                    } else if (p.canPlay()) {
                        selectedPlayer = p;
                    }
                    selectedSlot = null;
                }
                refreshContent();
            }
        });

        return row;
    }

    private void generate2DPitchTexture() {
        if (pitchTexture != null) pitchTexture.dispose();
        int w = 550, h = 650;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("1E4620"));
        pixmap.fill();
        pixmap.setColor(Color.valueOf("1B3E1C"));
        int stripeHeight = h / 10;
        for (int i = 0; i < 10; i += 2) {
            pixmap.fillRectangle(0, i * stripeHeight, w, stripeHeight);
        }
        pixmap.setColor(new Color(1f, 1f, 1f, 0.4f));
        pixmap.drawRectangle(10, 10, w - 20, h - 20);
        int midY = h / 2;
        pixmap.drawLine(10, midY, w - 10, midY);
        pixmap.drawCircle(w / 2, midY, 55);
        pitchTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private Table createFatigueBar(float fatiguePercent) {
        Table outerBar = new Table();
        outerBar.background(getSolidDrawable(Color.DARK_GRAY));
        Color barColor = fatiguePercent >= 70 ? Color.valueOf("4CAF50") : (fatiguePercent >= 40 ? Color.valueOf("FFC107") : Color.valueOf("F44336"));
        Table fillBar = new Table();
        fillBar.background(getSolidDrawable(barColor));
        float normalized = Math.max(0, Math.min(100, fatiguePercent)) / 100f;
        outerBar.add(fillBar).width(70 * normalized).height(4).left().expandX();
        return outerBar;
    }

    private TextureRegionDrawable getSolidDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private int getPositionDepthLayer(String pos) {
        if (pos == null) return 0;
        String p = pos.trim().toUpperCase();
        if (p.equals("ST") || p.equals("CF") || p.equals("SS") || p.equals("RF") || p.equals("LF")) return 7;
        if (p.equals("LW") || p.equals("RW")) return 6;
        if (p.contains("CAM") || p.equals("RAM") || p.equals("LAM") || p.equals("AM")) return 5;
        if (p.contains("CM") || p.equals("LM") || p.equals("RM")) return 4;
        if (p.contains("DM")) return 3;
        if (p.contains("WB")) return 2;
        if (p.contains("CB") || p.equals("LB") || p.equals("RB") || p.equals("SW")) return 1;
        return 0;
    }

    @Override
    public boolean remove() {
        if (pitchTexture != null) pitchTexture.dispose();
        if (kitTexture != null) kitTexture.dispose();
        return super.remove();
    }
}
