package io.github.some_example_name.model;

import java.util.*;
import java.util.stream.Collectors;

/** Calendário de expansão e seleção de veteranos; nunca consome a classe rookie. */
public final class LeagueExpansionService {
    public static final int PROTECTED_PLAYERS = 15;
    public static final int MAX_LOSSES_PER_CLUB = 3;
    public static final int TARGET_ROSTER = 20;

    public static final class Franchise {
        public final int year, reputation, capacity, condition;
        public final long balance;
        public final String name, country, conference, city, identity, colors, stadium, logo;

        private Franchise(int year, String name, String country, String conference, String city,
                          String identity, String colors, String stadium, String logo,
                          int reputation, long balance, int capacity, int condition) {
            this.year = year; this.name = name; this.country = country; this.conference = conference;
            this.city = city; this.identity = identity; this.colors = colors; this.stadium = stadium;
            this.logo = logo; this.reputation = reputation; this.balance = balance;
            this.capacity = capacity; this.condition = condition;
        }
    }

    private static final List<Franchise> FRANCHISES = Collections.unmodifiableList(Arrays.asList(
        new Franchise(1974, "Mexico City Aztecs", "México", "Ocidental", "Cidade do México", "Tradição asteca e ambição monumental", "Verde, dourado e vermelho", "Azteca Imperial", "mexico.png", 84, 42_000_000L, 87_000, 84),
        new Franchise(1974, "Cairo Pharaohs", "Egito", "Oriental", "Cairo", "Orgulho faraônico e a estreia africana", "Preto, dourado e branco", "Nile Stadium", "cairo.png", 80, 25_000_000L, 74_000, 80),
        new Franchise(1978, "Shanghai Dragons", "China", "Oriental", "Xangai", "Dragões e um novo mercado mundial", "Vermelho e dourado", "Dragon Stadium", "shanghai.png", 75, 45_000_000L, 55_000, 78),
        new Franchise(1978, "Sydney Southern Cross", "Austrália", "Oriental", "Sydney", "Cruzeiro do Sul e a chegada à Oceania", "Azul-marinho, branco e dourado", "Southern Cross Stadium", "sidney.png", 77, 38_000_000L, 50_000, 82),
        new Franchise(1982, "New York Empire", "EUA", "Ocidental", "Nova York", "Dinheiro, mídia e ambição sem títulos herdados", "Azul-marinho, branco e dourado", "Empire Stadium", "newyork.png", 86, 90_000_000L, 78_000, 92),
        new Franchise(1982, "Riyadh Falcons", "Arábia Saudita", "Oriental", "Riyadh", "Falcões, infraestrutura e investimento", "Verde, branco e dourado", "Falcons Stadium", "riyadh.png", 78, 75_000_000L, 60_000, 90),
        new Franchise(1986, "Bangkok Elephants", "Tailândia", "Oriental", "Bangkok", "Formação de talentos e crescimento paciente", "Vinho, dourado e branco", "Royal Elephant Stadium", "bangkok.png", 72, 22_000_000L, 45_000, 75),
        new Franchise(1986, "Bombay Tigers", "Índia", "Oriental", "Bombay", "Tigres e grande potencial comercial", "Laranja, azul e branco", "Tigers Stadium", "bombay.png", 73, 50_000_000L, 62_000, 78),
        new Franchise(1990, "Marseille Méditerranée", "França", "Ocidental", "Marseille", "Paixão mediterrânea e o clássico francês", "Azul-celeste e branco", "Stade Méditerranée", "marseille.png", 83, 48_000_000L, 60_000, 87),
        new Franchise(1990, "Jakarta Garudas", "Indonésia", "Oriental", "Jakarta", "Garuda e a força do Sudeste Asiático", "Vermelho, branco e preto", "Garuda Stadium", "jakarta.png", 74, 35_000_000L, 58_000, 80)
    ));

    private LeagueExpansionService() { }
    public static List<Franchise> allFranchises() { return FRANCHISES; }

    /** Instância independente para prévia; não insere a franquia na liga. */
    public static Club createClub(Franchise f) {
        Club club = new Club(f.name, f.country, f.conference, f.reputation, f.balance, f.stadium, f.logo);
        club.setStartYear(f.year);
        club.setCurrentYear(f.year);
        club.setStadiumCapacity(f.capacity);
        club.setStadiumCondition(f.condition);
        club.setPhilosophy(f.identity);
        club.getFinance().setBalance(f.balance);
        club.replaceExpiredStaff(f.year);
        return club;
    }
    public static List<Franchise> forYear(int year) {
        return FRANCHISES.stream().filter(f -> f.year == year).collect(Collectors.toList());
    }
    public static Franchise forClub(String name) {
        return FRANCHISES.stream().filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }
    public static boolean isExpansionYear(int year) { return !forYear(year).isEmpty(); }
    public static int projectedClubCount(int year) {
        return 20 + (int) FRANCHISES.stream().filter(f -> f.year <= year).count();
    }
    public static boolean isPending(League league) {
        return league != null && "OFFSEASON".equals(league.getCurrentStage())
            && isExpansionYear(league.getCurrentSeason() + 1)
            && !league.isExpansionDraftCompleted(league.getCurrentSeason() + 1);
    }

    /** Idempotente: não recria clubes, picks negociadas ou elencos ao reabrir a tela. */
    public static List<Club> prepare(League league, int year) {
        List<Club> newcomers = new ArrayList<>();
        for (Franchise f : forYear(year)) {
            Club club = league.getClubs().stream().filter(c -> f.name.equals(c.getName())).findFirst().orElse(null);
            if (club == null) {
                club = createClub(f);
                league.addClub(club);
            }
            newcomers.add(club);
        }
        DraftOrderService.initializeDraftPicks(league, year);
        return newcomers;
    }

    /** Após contratos expirarem, elencos curtos também oferecem três desprotegidos. */
    public static int protectionLimit(Club club) {
        return Math.min(PROTECTED_PLAYERS, Math.max(0, club.getSquad().size() - MAX_LOSSES_PER_CLUB));
    }

    public static List<Player> suggestedProtection(Club club) {
        return club.getSquad().stream().sorted(Comparator
            .comparingInt((Player p) -> p.getOverall() * 4 + p.getPotential()).reversed()
            .thenComparing(Player::getName)).limit(protectionLimit(club)).collect(Collectors.toList());
    }

    /** Candidatos para uma franquia nova: somente atletas desprotegidos com contrato vigente. */
    public static List<Player> availablePlayers(League league) {
        List<Club> newcomers = prepare(league, league.getCurrentSeason() + 1);
        List<Player> players = new ArrayList<>(availablePool(league, newcomers, null, Collections.emptyList()).keySet());
        players.sort(Comparator.comparingInt(Player::getOverall).reversed().thenComparing(Player::getName));
        return players;
    }

    public static int requiredSelections(Club club) {
        return Math.max(0, TARGET_ROSTER - club.getSquad().size());
    }

    /** Sugestão editável; não transfere ninguém nem encerra o evento. */
    public static List<Player> suggestedSelections(League league, Club newcomer) {
        if (!isPending(league)) return Collections.emptyList();
        DraftPlan plan = planDraft(league, null, Collections.emptyList(), null);
        List<Player> picks = plan.selections.get(newcomer);
        if (picks == null) throw new IllegalArgumentException("O clube não participa desta expansão.");
        return new ArrayList<>(picks);
    }

    public static List<String> runDraft(League league, Club userClub, Collection<Player> protectedPlayers) {
        return runDraft(league, userClub, protectedPlayers, null);
    }

    /** Valida e planeja os dois elencos completos antes de aplicar qualquer transferência. */
    public static List<String> runDraft(League league, Club userClub, Collection<Player> protectedPlayers,
                                        Collection<Player> selectedPlayers) {
        if (!isPending(league)) return league.getExpansionDraftLog();
        DraftPlan plan = planDraft(league, userClub, protectedPlayers, selectedPlayers);
        List<String> log = new ArrayList<>();
        for (Map.Entry<Club, List<Player>> entry : plan.selections.entrySet()) {
            for (Player player : entry.getValue()) {
                Club source = plan.sources.get(player);
                source.getStartingXI().remove(player);
                source.getTacticsMap().entrySet().removeIf(e -> e.getValue() == player);
                player.transferTo(entry.getKey());
                log.add(entry.getKey().getName() + " ← " + player.getName() + " (" + source.getName() + ")");
            }
        }
        for (Club c : plan.selections.keySet()) log.add(c.getName() + ": " + c.getSquad().size()
            + " jogadores. Continue a montagem na Free Agency e no Draft regular.");
        league.completeExpansionDraft(league.getCurrentSeason() + 1, log);
        return log;
    }

    private static DraftPlan planDraft(League league, Club userClub, Collection<Player> protectedPlayers,
                                        Collection<Player> selectedPlayers) {
        int year = league.getCurrentSeason() + 1;
        List<Club> newcomers = prepare(league, year);
        if (userClub != null && !league.getClubs().contains(userClub))
            throw new IllegalArgumentException("Clube fora desta liga.");
        boolean manual = newcomers.contains(userClub);
        if (manual) {
            int required = requiredSelections(userClub);
            if (selectedPlayers == null || selectedPlayers.size() != required
                || new HashSet<>(selectedPlayers).size() != required)
                throw new IllegalArgumentException("Escolha " + required + " jogadores diferentes para sua franquia.");
        } else if (userClub != null && (protectedPlayers == null
            || protectedPlayers.size() != protectionLimit(userClub)
            || new HashSet<>(protectedPlayers).size() != protectedPlayers.size()
            || !userClub.getSquad().containsAll(protectedPlayers))) {
            throw new IllegalArgumentException("Selecione " + protectionLimit(userClub)
                + " jogadores do seu elenco para proteger.");
        }
        Map<Player, Club> pool = availablePool(league, newcomers, manual ? null : userClub, protectedPlayers);
        DraftPlan plan = new DraftPlan(newcomers, pool);
        // As 20 escolhas confirmadas pelo usuário têm prioridade; a IA monta a outra franquia em seguida.
        if (manual) for (Player player : selectedPlayers) plan.reserve(userClub, player);
        for (int round = 0; round < TARGET_ROSTER; round++) {
            for (int turn = 0; turn < newcomers.size(); turn++) {
                Club recipient = newcomers.get(round % 2 == 0 ? turn : newcomers.size() - 1 - turn);
                int remaining = requiredSelections(recipient) - plan.selections.get(recipient).size();
                if (remaining <= 0) continue;
                Player selected = plan.available.keySet().stream()
                    .filter(p -> plan.canFinishAfter(recipient, p, remaining))
                    .max(Comparator.comparingDouble((Player p) -> selectionScore(recipient, p, plan.selections.get(recipient)))
                        .thenComparing(Player::getName)).orElse(null);
                if (selected == null) throw new IllegalArgumentException("Não há atletas elegíveis ou espaço no Hard Cap para completar os "
                    + TARGET_ROSTER + " jogadores do " + recipient.getName() + ". Revise as escolhas; nenhuma transferência foi realizada.");
                plan.reserve(recipient, selected);
            }
        }
        return plan;
    }

    private static Map<Player, Club> availablePool(League league, List<Club> newcomers, Club userClub,
                                                  Collection<Player> protectedPlayers) {
        int year = league.getCurrentSeason() + 1;
        Map<Player, Club> available = new LinkedHashMap<>();
        for (Club source : league.getClubs()) {
            if (newcomers.contains(source)) continue;
            Collection<Player> protectedSet = source == userClub ? protectedPlayers : suggestedProtection(source);
            for (Player player : source.getSquad()) {
                // Um contrato que termina no ano da estreia ainda cobre essa temporada.
                if (!protectedSet.contains(player) && player.getContractEndYear() >= year) available.put(player, source);
            }
        }
        return available;
    }

    private static final class DraftPlan {
        final Map<Club, List<Player>> selections = new LinkedHashMap<>();
        final Map<Player, Club> sources;
        final Map<Player, Club> available;
        final Map<Club, Integer> losses = new HashMap<>();
        final Map<Club, Long> payroll = new HashMap<>();
        DraftPlan(List<Club> clubs, Map<Player, Club> pool) {
            sources = new LinkedHashMap<>(pool);
            available = new LinkedHashMap<>(pool);
            for (Club club : clubs) {
                selections.put(club, new ArrayList<>());
                payroll.put(club, club.getFinance().getAnnualPayroll());
            }
        }
        void reserve(Club recipient, Player player) {
            Club source = available.get(player);
            if (source == null) throw new IllegalArgumentException("Jogador protegido, sem contrato vigente ou já selecionado.");
            if (losses.getOrDefault(source, 0) >= MAX_LOSSES_PER_CLUB)
                throw new IllegalArgumentException("Limite de " + MAX_LOSSES_PER_CLUB + " saídas atingido: " + source.getName() + ".");
            long total = payroll.get(recipient) + player.getAnnualSalary();
            if (total > recipient.getFinance().getHardCap())
                throw new IllegalArgumentException("As escolhas ultrapassam o Hard Cap do " + recipient.getName() + ".");
            available.remove(player);
            losses.put(source, losses.getOrDefault(source, 0) + 1);
            payroll.put(recipient, total);
            selections.get(recipient).add(player);
        }
        boolean canFinishAfter(Club recipient, Player player, int remaining) {
            Club source = available.get(player);
            if (losses.getOrDefault(source, 0) >= MAX_LOSSES_PER_CLUB) return false;
            long cost = payroll.get(recipient) + player.getAnnualSalary();
            long cap = recipient.getFinance().getHardCap();
            if (cost > cap) return false;
            Map<Club, Integer> reservedLosses = new HashMap<>(losses);
            reservedLosses.put(source, reservedLosses.getOrDefault(source, 0) + 1);
            List<Player> cheapest = new ArrayList<>(available.keySet());
            cheapest.remove(player);
            cheapest.sort(Comparator.comparingLong(Player::getAnnualSalary));
            int needed = remaining - 1;
            for (Player candidate : cheapest) {
                if (needed == 0) break;
                Club origin = available.get(candidate);
                if (reservedLosses.getOrDefault(origin, 0) >= MAX_LOSSES_PER_CLUB) continue;
                reservedLosses.put(origin, reservedLosses.getOrDefault(origin, 0) + 1);
                cost += candidate.getAnnualSalary();
                if (cost > cap) return false;
                needed--;
            }
            return needed == 0;
        }
    }

    private static double selectionScore(Club club, Player player, List<Player> planned) {
        long samePosition = club.getSquad().stream().filter(p -> p.getPrimaryPosition() == player.getPrimaryPosition()).count()
            + planned.stream().filter(p -> p.getPrimaryPosition() == player.getPrimaryPosition()).count();
        double need = player.getPrimaryPosition() == Position.GK ? (samePosition == 0 ? 45 : -50) : -samePosition * 12;
        return player.getOverall() + player.getPotential() * .12 + need;
    }
}
