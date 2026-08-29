package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Estado persistente da carreira do treinador controlado pelo usuário. */
public final class ManagerCareer {

    public static final class JobOffer {
        private final Club club;
        private final int contractYears;

        JobOffer(Club club, int contractYears) {
            this.club = club;
            this.contractYears = contractYears;
        }

        public Club getClub() { return club; }
        public int getContractYears() { return contractYears; }
    }

    private final List<ManagerSeasonRecord> history = new ArrayList<>();
    private final List<JobOffer> jobOffers = new ArrayList<>();
    private boolean jobOffersGenerated;
    private int reputation = 50;
    private int awardsWon;
    private int unemployedDays;
    private String currentClubName;
    private String lastDismissedClubName;

    public int getReputation() { return Math.max(0, Math.min(100, reputation)); }
    public int getReputationStars() {
        if (reputation >= 85) return 5;
        if (reputation >= 70) return 4;
        if (reputation >= 50) return 3;
        if (reputation >= 30) return 2;
        return 1;
    }
    public String getReputationLabel() {
        if (reputation >= 85) return "MUNDIAL";
        if (reputation >= 70) return "CONSAGRADO";
        if (reputation >= 50) return "RESPEITADO";
        if (reputation >= 30) return "EM ASCENSÃO";
        return "INICIANTE";
    }
    public int getUnemployedDays() { return unemployedDays; }
    public int getAwardsWon() { return awardsWon; }
    public int getTitlesWon() {
        int titles = 0;
        for (ManagerSeasonRecord record : history) if (record.isChampion()) titles++;
        return titles;
    }
    public boolean isUnemployed() { return currentClubName == null; }
    public List<ManagerSeasonRecord> getHistory() { return Collections.unmodifiableList(history); }

    /** Três títulos recentes pelo mesmo clube produzem exatamente +15. */
    public int calculateHistoryConfidenceBonus(Club club) {
        return calculateHistoryConfidenceBonus(club, Integer.MAX_VALUE);
    }

    /** Considera somente temporadas anteriores à avaliação informada. */
    public int calculateHistoryConfidenceBonus(Club club, int evaluatedSeason) {
        if (club == null) return 0;
        int bonus = 0;
        int considered = 0;
        for (ManagerSeasonRecord record : history) {
            if (!club.getName().equals(record.getClubName())) continue;
            if (record.getSeason() >= evaluatedSeason) continue;
            if (considered++ >= 3) break;
            if (record.isChampion()) bonus += 5;
            else if (record.hasReachedPlayoffs()) bonus += 3;
            else if (record.getFinalScore() >= 65) bonus += 2;
        }
        return Math.min(15, bonus);
    }

    public void startJob(Club club) {
        currentClubName = club == null ? null : club.getName();
        unemployedDays = 0;
        jobOffers.clear();
        jobOffersGenerated = false;
    }

    public void recordSeason(League league, Club club,
                             BoardObjectiveService.Evaluation evaluation,
                             int finalScore, boolean dismissed) {
        if (league == null || club == null || evaluation == null) return;
        int season = league.getCurrentSeason();
        for (ManagerSeasonRecord record : history) {
            if (record.getSeason() == season && club.getName().equals(record.getClubName())) return;
        }

        boolean champion = false;
        boolean playoffs = false;
        for (PlayoffSeries series : league.getPlayoffSeries()) {
            if (series.getFirstSeed() == club || series.getSecondSeed() == club) playoffs = true;
            if ("FINAL".equals(series.getRound()) && series.getWinner() == club) champion = true;
        }

        int completed = 0;
        for (BoardObjectiveService.ObjectiveProgress progress : evaluation.getObjectives()) {
            if (progress.getPercentage() >= 99.5d) completed++;
        }
        int developed = 0;
        for (Player player : club.getSquad()) {
            if (player.getAge() <= 23 && player.getSeasonOverallGrowth() >= 2) developed++;
        }

        history.add(0, new ManagerSeasonRecord(
            season, club.getName(), champion, playoffs, completed,
            developed, finalScore, dismissed
        ));
        if (finalScore >= 85) awardsWon++;
        updateReputation(champion, playoffs, completed, developed, finalScore, dismissed);

        if (dismissed) {
            lastDismissedClubName = club.getName();
            currentClubName = null;
            unemployedDays = 0;
            jobOffers.clear();
            jobOffersGenerated = false;
        }
    }

    private void updateReputation(boolean champion, boolean playoffs, int completed,
                                  int developed, int finalScore, boolean dismissed) {
        int delta = champion ? 12 : playoffs ? 5 : 0;
        delta += Math.min(8, completed * 2);
        delta += Math.min(4, developed);
        if (finalScore >= 80) delta += 4;
        else if (finalScore >= 65) delta += 2;
        else if (finalScore < 35) delta -= 6;
        if (dismissed) delta -= 8;
        if (finalScore >= 85) delta += 3;
        reputation = Math.max(5, Math.min(100, reputation + delta));
    }

    public List<JobOffer> getJobOffers(League league) {
        if (!jobOffersGenerated) generateJobOffers(league);
        return Collections.unmodifiableList(jobOffers);
    }

    public void rejectOffer(Club club) {
        jobOffers.removeIf(offer -> offer.getClub() == club);
    }

    public boolean hasOfferFrom(League league, Club club) {
        if (club == null) return false;
        for (JobOffer offer : getJobOffers(league)) if (offer.getClub() == club) return true;
        return false;
    }

    public JobOffer getOfferFrom(League league, Club club) {
        for (JobOffer offer : getJobOffers(league)) if (offer.getClub() == club) return offer;
        return null;
    }

    public void advanceUnemployedTime(League league, int days) {
        if (!isUnemployed() || league == null) return;
        int safeDays = Math.max(1, days);
        unemployedDays += safeDays;
        if (league.getCurrentDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(league.getCurrentDate());
            calendar.add(Calendar.DATE, safeDays);
            league.setCurrentDate(calendar.getTime());
        }
        jobOffers.clear();
        jobOffersGenerated = false;
    }

    private void generateJobOffers(League league) {
        jobOffers.clear();
        jobOffersGenerated = true;
        if (league == null) return;

        List<Club> candidates = new ArrayList<>();
        for (Club club : league.getClubs()) {
            if (club == null || club.isUserControlled()) continue;
            if (club.getName().equals(lastDismissedClubName)) continue;
            if (reputation >= minimumReputation(club)) candidates.add(club);
        }

        candidates.sort(Comparator
            .comparingInt((Club club) -> offerFit(club, league)).reversed()
            .thenComparing(Club::getName));

        if (candidates.size() < 3) {
            List<Club> fallback = new ArrayList<>(league.getClubs());
            fallback.sort(Comparator.comparingInt(Club::getReputation));
            for (Club club : fallback) {
                if (club.getName().equals(lastDismissedClubName) || candidates.contains(club)) continue;
                candidates.add(club);
                if (candidates.size() >= 3) break;
            }
        }

        int amount = Math.min(5, candidates.size());
        for (int index = 0; index < amount; index++) {
            Club club = candidates.get(index);
            ClubNeedEvaluator.TeamPhase phase = ClubNeedEvaluator.getTeamPhase(club);
            int years = phase == ClubNeedEvaluator.TeamPhase.REBUILDING ? 3
                : phase == ClubNeedEvaluator.TeamPhase.SELLER ? 2 : 2;
            jobOffers.add(new JobOffer(club, years));
        }
    }

    private int minimumReputation(Club club) {
        int clubReputation = club.getReputation();
        if (clubReputation >= 93) return 75;
        if (clubReputation >= 89) return 60;
        if (clubReputation >= 85) return 45;
        return 25;
    }

    private int offerFit(Club club, League league) {
        int phaseBonus = ClubNeedEvaluator.getTeamPhase(club) == ClubNeedEvaluator.TeamPhase.REBUILDING ? 18 : 0;
        int confidencePressure = Math.max(0, 55 - club.getBoardConfidence());
        int deterministicVariation = Math.floorMod(
            (club.getName() + league.getCurrentSeason() + unemployedDays).hashCode(), 17
        );
        return phaseBonus + confidencePressure + deterministicVariation - Math.abs(club.getReputation() - reputation);
    }
}
