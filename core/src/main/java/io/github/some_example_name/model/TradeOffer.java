package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;

public class TradeOffer {
    /**
     * The interface and the negotiation rules both use this limit. Keeping it
     * here prevents a counteroffer or future screen from bypassing the five
     * available trade slots.
     */
    public static final int MAX_ASSETS_PER_SIDE = 5;

    private final Club userClub;
    private final Club targetClub;

    private final List<Player> userPlayers = new ArrayList<>();
    private final List<DraftPick> userPicks = new ArrayList<>();

    private final List<Player> targetPlayers = new ArrayList<>();
    private final List<DraftPick> targetPicks = new ArrayList<>();

    public TradeOffer(Club userClub, Club targetClub) {
        this.userClub = userClub;
        this.targetClub = targetClub;
    }

    // Métodos para adicionar/remover ativos
    public void addPlayerToGive(Player p) { if (!userPlayers.contains(p)) userPlayers.add(p); }
    public void removePlayerToGive(Player p) { userPlayers.remove(p); }

    public void addPlayerToReceive(Player p) { if (!targetPlayers.contains(p)) targetPlayers.add(p); }
    public void removePlayerToReceive(Player p) { targetPlayers.remove(p); }

    public void addPickToGive(DraftPick pick) { if (!userPicks.contains(pick)) userPicks.add(pick); }
    public void removePickToGive(DraftPick pick) { userPicks.remove(pick); }

    public void addPickToReceive(DraftPick pick) { if (!targetPicks.contains(pick)) targetPicks.add(pick); }
    public void removePickToReceive(DraftPick pick) { targetPicks.remove(pick); }

    // Getters
    public Club getUserClub() { return userClub; }
    public Club getTargetClub() { return targetClub; }
    public List<Player> getUserPlayers() { return userPlayers; }
    public List<Player> getTargetPlayers() { return targetPlayers; }
    public List<DraftPick> getUserPicks() { return userPicks; }
    public List<DraftPick> getTargetPicks() { return targetPicks; }

    public int getUserAssetCount() { return userPlayers.size() + userPicks.size(); }
    public int getTargetAssetCount() { return targetPlayers.size() + targetPicks.size(); }
}
