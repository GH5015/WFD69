package io.github.some_example_name;

import io.github.some_example_name.model.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ExpansionRosterRegressionTest {
    public static void main(String[] args) {
        for (int userIndex = 0; userIndex < 2; userIndex++) testManual(userIndex);
        League sparse = fixture(1);
        reject(() -> LeagueExpansionService.runDraft(sparse, null, Collections.emptyList()));
        require(LeagueExpansionService.isPending(sparse), "Escassez não deve encerrar o evento");
        require(sparse.getClubs().stream().allMatch(c -> c.getSquad().size() == 1 || c.getSquad().isEmpty()), "Escassez transferiu atletas");
        League shortRosters = fixture(14);
        LeagueExpansionService.runDraft(shortRosters, null, Collections.emptyList());
        for (Club c : LeagueExpansionService.prepare(shortRosters, 1974)) require(c.getSquad().size() == 20, "Elencos curtos bloquearam expansão");
        System.out.println("Manual expansion: choices, protection, quotas, contracts, cap, cancellation and atomicity OK.");
    }
    private static League fixture(int rosterSize) {
        League league = new League("WFL", 1973);
        for (int i = 0; i < 20; i++) {
            Club club = new Club("Original " + i, "Brasil", i < 15 ? "Ocidental" : "Oriental", 80, 40_000_000, "Arena", "santos.png");
            league.addClub(club);
            for (int j = 0; j < rosterSize; j++) {
                Player p = new Player("Jogador " + i + "/" + j, "Brasil", j % 7 == 0 ? Position.GK : Position.CM,
                    null, 24, new TechnicalAttributes(55 + j, 55 + j, 55 + j, 70, 65, 60), 90, 10_000);
                p.renewContract(240_000, 1, 1973); // Contrato até 1974 também é elegível.
                p.transferTo(club);
            }
        }
        league.setCurrentStage("OFFSEASON");
        LeagueExpansionService.prepare(league, 1974);
        return league;
    }
    private static void testManual(int index) {
        League league = fixture(25);
        List<Club> newcomers = LeagueExpansionService.prepare(league, 1974);
        Club user = newcomers.get(index); user.setUserControlled(true);
        List<Player> available = LeagueExpansionService.availablePlayers(league);
        List<Player> suggested = LeagueExpansionService.suggestedSelections(league, user);
        require(suggested.size() == 20 && user.getSquad().isEmpty(), "Sugestão deve ser reversível");
        reject(() -> LeagueExpansionService.runDraft(league, user, Collections.emptyList()));
        List<Player> chosen = new ArrayList<>();
        // Preferências manuais diferentes da sugestão: os menores overalls disponíveis.
        available.sort(Comparator.comparingInt(Player::getOverall));
        Map<Club, Integer> picked = new HashMap<>();
        for (Player p : available) {
            Club source = p.getCurrentClub();
            if (picked.getOrDefault(source, 0) == 3) continue;
            chosen.add(p); picked.put(source, picked.getOrDefault(source, 0) + 1);
            if (chosen.size() == 20) break;
        }
        require(!new HashSet<>(chosen).equals(new HashSet<>(suggested)), "Fixture precisa testar escolhas manuais");
        List<Player> invalid = new ArrayList<>(chosen);
        invalid.set(0, invalid.get(1));
        reject(() -> LeagueExpansionService.runDraft(league, user, Collections.emptyList(), invalid));
        invalid.set(0, LeagueExpansionService.suggestedProtection(league.getClubs().get(0)).get(0));
        reject(() -> LeagueExpansionService.runDraft(league, user, Collections.emptyList(), invalid));
        Club source = available.get(0).getCurrentClub();
        List<Player> tooManyFromClub = available.stream().filter(p -> p.getCurrentClub() == source).limit(4).collect(Collectors.toList());
        for (Player p : chosen) if (p.getCurrentClub() != source && tooManyFromClub.size() < 20) tooManyFromClub.add(p);
        reject(() -> LeagueExpansionService.runDraft(league, user, Collections.emptyList(), tooManyFromClub));
        for (Player p : chosen) p.renewContract(2_000_000, 1, 1973);
        reject(() -> LeagueExpansionService.runDraft(league, user, Collections.emptyList(), chosen));
        for (Player p : chosen) p.renewContract(240_000, 1, 1973);
        require(user.getSquad().isEmpty() && newcomers.get(1 - index).getSquad().isEmpty(), "Tentativa inválida transferiu jogadores");
        require(league.getClubs().stream().filter(c -> !newcomers.contains(c)).allMatch(c -> c.getSquad().size() == 25), "Origem alterada antes de confirmar");
        Map<Player, Club> original = new HashMap<>();
        for (Player p : available) original.put(p, p.getCurrentClub());
        List<String> log = LeagueExpansionService.runDraft(league, user, Collections.emptyList(), chosen);
        require(new HashSet<>(user.getSquad()).equals(new HashSet<>(chosen)), "Escolhas do usuário substituídas");
        for (Club club : newcomers) {
            require(club.getSquad().size() == 20, "Elenco deve ter 20");
            require(club.getFinance().getAnnualPayroll() <= club.getFinance().getHardCap(), "Hard cap violado");
            for (Player p : club.getSquad()) require(p.getAnnualSalary() == 240_000 && p.getContractEndYear() == 1974, "Contrato alterado");
            require(club.getDraftPicks().stream().filter(p -> p.getYear() == 1974).count() == 2, "Picks regulares alteradas");
        }
        for (Club club : league.getClubs()) if (!newcomers.contains(club)) require(club.getSquad().size() >= 22, "Mais de 3 perdas");
        require(log.equals(LeagueExpansionService.runDraft(league, user, Collections.emptyList(), chosen)), "Draft repetido");
    }
    private static void reject(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Escolha inválida aceita");
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
