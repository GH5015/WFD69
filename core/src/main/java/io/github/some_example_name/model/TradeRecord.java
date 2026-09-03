package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** Registro imutável exibido no histórico do mercado de trocas. */
public final class TradeRecord {
    private final int season;
    private final Date date;
    private final Club firstClub;
    private final Club secondClub;
    private final List<String> firstClubAssets;
    private final List<String> secondClubAssets;

    private TradeRecord(
        int season,
        Date date,
        Club firstClub,
        Club secondClub,
        List<String> firstClubAssets,
        List<String> secondClubAssets
    ) {
        this.season = season;
        this.date = date == null ? null : new Date(date.getTime());
        this.firstClub = firstClub;
        this.secondClub = secondClub;
        this.firstClubAssets = Collections.unmodifiableList(new ArrayList<>(firstClubAssets));
        this.secondClubAssets = Collections.unmodifiableList(new ArrayList<>(secondClubAssets));
    }

    public static TradeRecord fromOffer(TradeOffer offer, League league) {
        return new TradeRecord(
            league != null ? league.getCurrentSeason() : 0,
            league != null ? league.getCurrentDate() : null,
            offer.getUserClub(),
            offer.getTargetClub(),
            describeAssets(offer.getUserPlayers(), offer.getUserPicks()),
            describeAssets(offer.getTargetPlayers(), offer.getTargetPicks())
        );
    }

    private static List<String> describeAssets(List<Player> players, List<DraftPick> picks) {
        List<String> assets = new ArrayList<>();
        for (Player player : players) {
            assets.add(player.getName() + " (OVR " + player.getOverall() + ")");
        }
        for (DraftPick pick : picks) {
            assets.add(pick.getYear() + " • " + pick.getRound() + "ª rodada (geral #" + pick.getProjectedOverallPosition() + ")");
        }
        return assets;
    }

    public int getSeason() { return season; }
    public Date getDate() { return date == null ? null : new Date(date.getTime()); }
    public Club getFirstClub() { return firstClub; }
    public Club getSecondClub() { return secondClub; }
    public List<String> getFirstClubAssets() { return firstClubAssets; }
    public List<String> getSecondClubAssets() { return secondClubAssets; }
    public boolean involves(Club club) { return firstClub == club || secondClub == club; }
}
