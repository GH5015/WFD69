package io.github.some_example_name.model;

/** Snapshot of a completed signing, independent of subsequent transfers. */
public class FreeAgencySigning implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public int season;
    public long date;
    public String playerName;
    public String clubName;
    public String position;
    public int overall;
    public long annualSalary;
    public int years;

    public FreeAgencySigning() { }

    public FreeAgencySigning(League league, Player player, Club club, long salary, int duration) {
        season = league.getCurrentSeason();
        date = league.getCurrentDate() == null ? 0 : league.getCurrentDate().getTime();
        playerName = player.getName();
        clubName = club.getName();
        position = player.getPosition();
        overall = player.getOverall();
        annualSalary = salary;
        years = duration;
    }
}
