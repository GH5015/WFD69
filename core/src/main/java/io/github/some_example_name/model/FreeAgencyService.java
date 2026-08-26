package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.github.some_example_name.utils.NameGenerator;

/**
 * Mercado enxuto de agentes livres. A proposta do usuário é processada no
 * próximo avanço de dia para que salário não seja o único fator da decisão.
 */
public class FreeAgencyService {
    public enum OfferStatus {
        PENDING("AGUARDANDO"),
        ACCEPTED("ACEITOU"),
        REJECTED("RECUSOU");

        private final String label;

        OfferStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static final class Offer {
        private final Player player;
        private final long annualSalary;
        private final int years;
        private final int interestStars;
        private final int estimatedChance;
        private final int competingOffers;
        private OfferStatus status;
        private String decisionMessage;

        private Offer(
            Player player,
            long annualSalary,
            int years,
            int interestStars,
            int estimatedChance,
            int competingOffers
        ) {
            this.player = player;
            this.annualSalary = annualSalary;
            this.years = years;
            this.interestStars = interestStars;
            this.estimatedChance = estimatedChance;
            this.competingOffers = competingOffers;
            this.status = OfferStatus.PENDING;
            this.decisionMessage = "Aguardando a decisão do atleta.";
        }

        public Player getPlayer() { return player; }
        public long getAnnualSalary() { return annualSalary; }
        public int getYears() { return years; }
        public int getInterestStars() { return interestStars; }
        public int getEstimatedChance() { return estimatedChance; }
        public int getCompetingOffers() { return competingOffers; }
        public OfferStatus getStatus() { return status; }
        public String getDecisionMessage() { return decisionMessage; }
    }

    public static final class Submission {
        private final boolean accepted;
        private final String message;
        private final Offer offer;

        private Submission(boolean accepted, String message, Offer offer) {
            this.accepted = accepted;
            this.message = message;
            this.offer = offer;
        }

        public boolean isAccepted() { return accepted; }
        public String getMessage() { return message; }
        public Offer getOffer() { return offer; }
    }

    private static final int MIN_SQUAD_SIZE = TradeRulesValidator.MIN_ROSTER_SIZE;
    private static final int MAX_SQUAD_SIZE = TradeRulesValidator.MAX_ROSTER_SIZE;
    private static final int MARKET_TARGET_SIZE = 50;
    private static final String[] MARKET_NATIONALITIES = {
        "Brasil", "Argentina", "Uruguai", "Chile", "Colômbia", "México",
        "Inglaterra", "Escócia", "Irlanda", "França", "Itália", "Espanha",
        "Portugal", "Holanda", "Alemanha", "Suécia", "Polônia", "Hungria",
        "Iugoslávia", "Grécia", "Turquia", "Japão", "Coreia do Sul", "Irã",
        "Nigéria", "Senegal", "Marrocos", "Gana", "Austrália", "Canadá"
    };
    private final League league;
    private final List<Player> freeAgents = new ArrayList<>();
    private final List<Offer> userOffers = new ArrayList<>();
    private final Set<String> favourites = new HashSet<>();
    private final Random random = new Random(1969L);

    public FreeAgencyService(League league) {
        this.league = league;
        collectExpiredPlayers();
        ensureMarketDepth();
    }

    public List<Player> getFreeAgents() {
        collectExpiredPlayers();
        ensureMarketDepth();
        return Collections.unmodifiableList(freeAgents);
    }

    /** Prospectos não escolhidos passam diretamente ao mercado de Free Agency. */
    public void addUndraftedFreeAgents(List<Player> players) {
        if (players == null) return;
        for (Player player : players) {
            if (player != null && player.getCurrentClub() == null && !freeAgents.contains(player)) freeAgents.add(player);
        }
    }

    public List<Offer> getUserOffers() {
        return Collections.unmodifiableList(userOffers);
    }

    public boolean isFavourite(Player player) {
        return player != null && favourites.contains(player.getId());
    }

    public void toggleFavourite(Player player) {
        if (player == null) return;
        if (!favourites.add(player.getId())) {
            favourites.remove(player.getId());
        }
    }

    public long getRequestedAnnualSalary(Player player) {
        if (player == null) return 0L;
        return roundAnnual(Math.max(360_000L, player.getAnnualSalary() * 108L / 100L));
    }

    public int getPreferredYears(Player player) {
        if (player == null) return 3;
        if (player.getAge() <= 21) return 5;
        if (player.getAge() <= 27) return 4;
        if (player.getAge() <= 32) return 3;
        return 2;
    }

    public int getInterestStars(Player player, Club club) {
        if (player == null || club == null) return 1;
        double score = 2.15;
        score += (club.getReputation() - 85) / 20.0;
        score += (club.getOverall() - 75.0) / 24.0;
        score += (getStarterChance(player, club) - 50) / 85.0;
        score += ((Math.abs(player.getId().hashCode() + club.getName().hashCode()) % 5) - 2) * 0.22;
        return clamp((int) Math.round(score), 1, 5);
    }

    /** Probabilidade aproximada de o jogador ter minutos relevantes no clube. */
    public int getStarterChance(Player player, Club club) {
        if (player == null || club == null) return 0;
        int betterInRole = 0;
        for (Player squadPlayer : club.getSquad()) {
            if (sameRole(player, squadPlayer) && squadPlayer.getOverall() >= player.getOverall()) {
                betterInRole++;
            }
        }
        int chance = 92 - (betterInRole * 24);
        if (club.getSquad().size() < 18) chance += 8;
        return clamp(chance, 12, 96);
    }

    public int estimateAcceptanceChance(Player player, Club club, long annualSalary, int years) {
        if (player == null || club == null || annualSalary <= 0) return 0;
        double score = scoreOffer(player, club, annualSalary, years, 0.0);
        double expectedBestCompetitor = 70.0 + Math.max(0, player.getOverall() - 76) * 0.45;
        return clamp((int) Math.round(42 + (score - expectedBestCompetitor) * 1.75), 3, 96);
    }

    public long getProjectedPayroll(Club club, long annualSalary) {
        return club == null ? 0L : club.getFinance().getAnnualPayroll() + Math.max(0L, annualSalary);
    }

    public long getLuxuryTax(Club club, long projectedPayroll) {
        if (club == null) return 0L;
        return Math.max(0L, projectedPayroll - club.getFinance().getSalaryCap()) / 2L;
    }

    public Offer findOffer(Player player) {
        if (player == null) return null;
        for (Offer offer : userOffers) {
            if (offer.player == player) return offer;
        }
        return null;
    }

    public Submission submitOffer(Club club, Player player, long annualSalary, int years) {
        if (!SeasonCalendar.isFreeAgentSigningOpen(league)) {
            return new Submission(false, "Jogadores sem contrato só podem ser contratados até o início dos playoffs.", null);
        }
        if (club == null || player == null || !freeAgents.contains(player)) {
            return new Submission(false, "Este jogador não está mais disponível no mercado.", null);
        }
        if (club.getSquad().size() >= MAX_SQUAD_SIZE) {
            return new Submission(false, "O elenco já atingiu o limite de " + MAX_SQUAD_SIZE + " jogadores.", null);
        }
        Offer existing = findOffer(player);
        if (existing != null && existing.status == OfferStatus.PENDING) {
            return new Submission(false, "Já existe uma proposta aguardando resposta para " + player.getName() + ".", existing);
        }
        if (annualSalary < getRequestedAnnualSalary(player) * 65L / 100L) {
            return new Submission(false, player.getName() + " considera a oferta muito abaixo do mercado.", null);
        }

        int safeYears = clamp(years, 1, 5);
        Offer offer = new Offer(
            player,
            roundAnnual(annualSalary),
            safeYears,
            getInterestStars(player, club),
            estimateAcceptanceChance(player, club, annualSalary, safeYears),
            estimateCompetingOffers(player, club)
        );
        userOffers.add(offer);
        return new Submission(true, player.getName() + " recebeu sua proposta e responderá após o próximo avanço de dia.", offer);
    }

    /** Resolve as propostas pendentes quando o calendário avança. */
    public void processPendingOffers(Club userClub, int currentYear) {
        if (!SeasonCalendar.isFreeAgentSigningOpen(league)) return;
        collectExpiredPlayers();
        for (Offer offer : userOffers) {
            if (offer.status != OfferStatus.PENDING || !freeAgents.contains(offer.player)) continue;
            if (userClub == null || userClub.getSquad().size() >= MAX_SQUAD_SIZE) {
                offer.status = OfferStatus.REJECTED;
                offer.decisionMessage = "A proposta expirou porque o elenco atingiu o limite de jogadores.";
                continue;
            }

            List<AiOffer> competitors = buildCompetitors(offer.player, userClub);
            double userScore = scoreOffer(offer.player, userClub, offer.annualSalary, offer.years, randomSwing());
            AiOffer best = competitors.isEmpty() ? null : Collections.max(competitors, new Comparator<AiOffer>() {
                @Override public int compare(AiOffer first, AiOffer second) {
                    return Double.compare(first.score, second.score);
                }
            });

            if (userScore >= 52.0 && (best == null || userScore >= best.score)) {
                offer.player.transferTo(userClub);
                offer.player.renewContract(offer.annualSalary, offer.years, currentYear);
                offer.player.setTradeBlockedDays(60);
                freeAgents.remove(offer.player);
                offer.status = OfferStatus.ACCEPTED;
                offer.decisionMessage = offer.player.getName() + " aceitou: " + offer.years + " anos por " + formatMillions(offer.annualSalary) + "/ano.";
            } else {
                offer.status = OfferStatus.REJECTED;
                if (best != null && best.score >= 52.0) {
                    offer.player.transferTo(best.club);
                    offer.player.renewContract(best.salary, best.years, currentYear);
                    offer.player.setTradeBlockedDays(60);
                    freeAgents.remove(offer.player);
                    offer.decisionMessage = offer.player.getName() + " escolheu " + best.club.getName() + ". A proposta financeira não foi o único fator.";
                } else {
                    offer.decisionMessage = offer.player.getName() + " decidiu aguardar uma proposta mais adequada.";
                }
            }
        }
    }

    public static String formatMillions(long amount) {
        return String.format(java.util.Locale.US, "WFL$ %.1fM", amount / 1_000_000.0);
    }

    private List<AiOffer> buildCompetitors(Player player, Club userClub) {
        List<Club> candidates = new ArrayList<>();
        for (Club club : league.getClubs()) {
            if (club != userClub && club.getSquad().size() < MAX_SQUAD_SIZE) candidates.add(club);
        }
        Collections.shuffle(candidates, random);
        List<AiOffer> offers = new ArrayList<>();
        int limit = Math.min(estimateCompetingOffers(player, userClub), candidates.size());
        for (int index = 0; index < candidates.size() && offers.size() < limit; index++) {
            Club club = candidates.get(index);
            long salary = roundAnnual((long) (getRequestedAnnualSalary(player) * (0.88 + random.nextDouble() * 0.35)));
            int years = clamp(getPreferredYears(player) + random.nextInt(3) - 1, 1, 5);
            double score = scoreOffer(player, club, salary, years, randomSwing());
            if (score >= 55.0 || offers.isEmpty()) offers.add(new AiOffer(club, salary, years, score));
        }
        return offers;
    }

    private int estimateCompetingOffers(Player player, Club userClub) {
        int interest = getInterestStars(player, userClub);
        int count = 1 + Math.max(0, player.getOverall() - 73) / 7 + (interest >= 4 ? 1 : 0);
        return clamp(count, 1, 4);
    }

    private double scoreOffer(Player player, Club club, long salary, int years, double randomFactor) {
        double requested = Math.max(1L, getRequestedAnnualSalary(player));
        double salaryScore = Math.min(1.30, salary / requested) * 34.0;
        double durationScore = Math.max(0.0, 12.0 - Math.abs(years - getPreferredYears(player)) * 4.0);
        double reputationScore = club.getReputation() * 0.13;
        double interestScore = getInterestStars(player, club) * 3.6;
        double starterScore = getStarterChance(player, club) * 0.13;
        double teamStrengthScore = club.getOverall() * 0.09;
        return salaryScore + durationScore + reputationScore + interestScore + starterScore + teamStrengthScore + randomFactor;
    }

    /**
     * Libera os contratos vencidos no instante em que a liga entra na Off Season.
     * O método é idempotente para poder ser chamado pela transição e pela tela
     * de Free Agency sem duplicar atletas no mercado.
     */
    public int releaseExpiredContractsAtOffseasonStart() {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage())) return 0;

        int released = 0;
        int year = league.getCurrentSeason();
        for (Club club : league.getClubs()) {
            List<Player> expired = new ArrayList<>();
            for (Player player : club.getSquad()) {
                if (player.isFreeAgent(year)) expired.add(player);
            }

            for (Player player : expired) {
                // A saída precisa limpar também a escalação e o mapa tático;
                // transferTo remove o vínculo e o elenco do clube de origem.
                club.getStartingXI().remove(player);
                club.getTacticsMap().entrySet().removeIf(entry -> entry.getValue() == player);
                player.transferTo(null);

                if (!freeAgents.contains(player)) {
                    freeAgents.add(player);
                    released++;
                }
            }
        }
        return released;
    }

    private void collectExpiredPlayers() {
        releaseExpiredContractsAtOffseasonStart();
    }

    /**
     * Barreira obrigatória para a virada de temporada. Todos os clubes,
     * inclusive o controlado pelo usuário, entram na temporada com 23–26
     * atletas. A Free Agency cobre qualquer lacuna que reste após as trocas
     * e negociações da Off Season.
     */
    public int enforceRosterLimitsForNewSeason() {
        if (league == null) return 0;

        collectExpiredPlayers();
        int moves = 0;

        // Elencos acima do teto liberam as opções menos necessárias ao mercado.
        for (Club club : league.getClubs()) {
            while (club.getSquad().size() > MAX_SQUAD_SIZE) {
                Player surplus = selectSurplusPlayer(club);
                if (surplus == null) break;
                releasePlayerToFreeAgency(club, surplus);
                moves++;
            }
        }

        // A cada clube com déficit, garante mercado suficiente e contrata uma
        // opção que prioriza a posição mais carente.
        for (Club club : league.getClubs()) {
            while (club.getSquad().size() < MIN_SQUAD_SIZE) {
                ensureMarketDepth();
                Player signing = selectBestRosterFill(club);
                if (signing == null) break;

                signing.transferTo(club);
                signing.renewContract(
                    getRequestedAnnualSalary(signing),
                    Math.max(1, getPreferredYears(signing)),
                    league.getCurrentSeason()
                );
                signing.setTradeBlockedDays(60);
                freeAgents.remove(signing);
                moves++;
            }
        }

        return moves;
    }

    private void releasePlayerToFreeAgency(Club club, Player player) {
        if (club == null || player == null) return;
        club.getStartingXI().remove(player);
        club.getTacticsMap().entrySet().removeIf(entry -> entry.getValue() == player);
        player.transferTo(null);
        if (!freeAgents.contains(player)) freeAgents.add(player);
    }

    private Player selectBestRosterFill(Club club) {
        Player best = null;
        int bestScore = Integer.MIN_VALUE;

        for (Player candidate : freeAgents) {
            if (candidate == null || candidate.getCurrentClub() != null) continue;
            int score = rosterNeedScore(club, candidate);
            if (
                best == null ||
                score > bestScore ||
                (score == bestScore && candidate.getOverall() > best.getOverall())
            ) {
                best = candidate;
                bestScore = score;
            }
        }
        return best;
    }

    private Player selectSurplusPlayer(Club club) {
        Player surplus = null;
        int highestReleaseScore = Integer.MIN_VALUE;

        for (Player player : club.getSquad()) {
            int roleCount = countRole(club, role(player.getPosition()));
            int target = targetRoleCount(role(player.getPosition()));
            int releaseScore = Math.max(0, roleCount - target) * 1_000
                - player.getOverall() * 10
                + player.getAge();

            if (surplus == null || releaseScore > highestReleaseScore) {
                surplus = player;
                highestReleaseScore = releaseScore;
            }
        }
        return surplus;
    }

    private int rosterNeedScore(Club club, Player candidate) {
        String candidateRole = role(candidate.getPosition());
        int deficit = Math.max(0, targetRoleCount(candidateRole) - countRole(club, candidateRole));
        return deficit * 1_000 + candidate.getOverall() * 10 + candidate.getPotential() - candidate.getAge();
    }

    private int countRole(Club club, String targetRole) {
        int count = 0;
        for (Player player : club.getSquad()) {
            if (targetRole.equals(role(player.getPosition()))) count++;
        }
        return count;
    }

    private int targetRoleCount(String role) {
        if ("GK".equals(role)) return 2;
        if ("DEF".equals(role)) return 8;
        if ("MID".equals(role)) return 7;
        return 6;
    }

    /** Mantém uma lista inicial de cinquenta nomes sem inflar a qualidade do mercado. */
    private void ensureMarketDepth() {
        int attempts = 0;
        while (freeAgents.size() < MARKET_TARGET_SIZE && attempts++ < 250) {
            Player player = createRandomFreeAgent(freeAgents.size());
            if (player != null) freeAgents.add(player);
        }
    }

    private Player createRandomFreeAgent(int marketIndex) {
        Position position = Position.values()[random.nextInt(Position.values().length)];
        int targetOverall = 54 + random.nextInt(17); // 54–70; margem segura até OVR 73.
        int age = 18 + random.nextInt(17);
        int variation = random.nextInt(7) - 3;
        int attack = clamp(targetOverall + variation, 35, 73);
        int passing = clamp(targetOverall + random.nextInt(7) - 3, 35, 73);
        int defense = clamp(targetOverall + random.nextInt(7) - 3, 35, 73);
        int physical = clamp(targetOverall + random.nextInt(7) - 3, 42, 73);
        int dribbling = clamp(targetOverall + random.nextInt(7) - 3, 35, 73);
        int goalkeeper = position == Position.GK
            ? clamp(targetOverall + random.nextInt(5) - 2, 50, 73)
            : 25;
        int potential = clamp(targetOverall + 4 + random.nextInt(15), 60, 88);

        String name = NameGenerator.generateName();
        int nameAttempts = 0;
        while (containsFreeAgentNamed(name) && nameAttempts++ < 20) {
            name = NameGenerator.generateName();
        }
        if (containsFreeAgentNamed(name)) return null;

        String nationality = MARKET_NATIONALITIES[marketIndex % MARKET_NATIONALITIES.length];
        Player player = new Player(
            name,
            nationality,
            position,
            null,
            age,
            new TechnicalAttributes(attack, passing, defense, physical, dribbling, goalkeeper),
            potential,
            7_000 + random.nextInt(16_000)
        );
        return player.getOverall() <= 73 ? player : null;
    }

    private boolean containsFreeAgentNamed(String name) {
        for (Player player : freeAgents) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private boolean sameRole(Player first, Player second) {
        return first != null && second != null && role(first.getPosition()).equals(role(second.getPosition()));
    }

    private String role(String position) {
        if ("GK".equals(position)) return "GK";
        if (position != null && position.matches("CB|LB|RB|LWB|RWB")) return "DEF";
        if (position != null && position.matches("CDM|CM|CAM|LM|RM")) return "MID";
        return "ATT";
    }

    private double randomSwing() {
        return -3.0 + random.nextDouble() * 6.0;
    }

    private long roundAnnual(long amount) {
        return Math.max(120_000L, Math.round(amount / 10_000.0) * 10_000L);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class AiOffer {
        private final Club club;
        private final long salary;
        private final int years;
        private final double score;

        private AiOffer(Club club, long salary, int years, double score) {
            this.club = club;
            this.salary = salary;
            this.years = years;
            this.score = score;
        }
    }
}