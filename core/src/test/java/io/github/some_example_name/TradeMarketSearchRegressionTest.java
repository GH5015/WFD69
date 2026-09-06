package io.github.some_example_name;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;
import io.github.some_example_name.model.TradeMarketSearchEvaluator;

public final class TradeMarketSearchRegressionTest {
    private static final int SEASON = 1975;

    public static void main(String[] args) {
        Club user = club("Usuário");
        add(user, player("Atacante 1", Position.ST, 82, 88), 1980);
        add(user, player("Atacante 2", Position.ST, 80, 84), 1980);

        Player striker = player("Alvo atacante", Position.ST, 78, 88);
        Player defender = player("Alvo zagueiro", Position.CB, 78, 88);
        require(
            TradeMarketSearchEvaluator.fitScore(user, defender)
                > TradeMarketSearchEvaluator.fitScore(user, striker),
            "posição carente não recebeu prioridade de encaixe"
        );

        Club seller = club("Vendedor");
        Player opportunity = player("Oportunidade", Position.CM, 76, 82);
        add(seller, opportunity, SEASON + 1);
        add(seller, player("Reposição 1", Position.CM, 86, 86), 1980);
        add(seller, player("Reposição 2", Position.CM, 84, 84), 1980);

        int available = TradeMarketSearchEvaluator.availabilityScore(seller, opportunity, SEASON);
        require(available >= 70, "contrato curto e elenco com reposição não viraram alvo negociável");
        require(
            TradeMarketSearchEvaluator.matchesProfile(
                "NEGOCIÁVEIS", user, seller, opportunity, SEASON
            ),
            "perfil negociáveis descartou um alvo viável"
        );

        opportunity.setTradeBlockedDays(12);
        require(
            TradeMarketSearchEvaluator.availabilityScore(seller, opportunity, SEASON) == 0,
            "jogador bloqueado continuou disponível"
        );
        require(
            !TradeMarketSearchEvaluator.matchesProfile(
                "NEGOCIÁVEIS", user, seller, opportunity, SEASON
            ),
            "filtro negociáveis manteve jogador bloqueado"
        );

        System.out.println("Trade market search: fit, availability and profile filters OK.");
    }

    private static Club club(String name) {
        return new Club(name, "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
    }

    private static Player player(String name, Position position, int attribute, int potential) {
        TechnicalAttributes attributes;
        if (position == Position.CB) {
            attributes = new TechnicalAttributes(45, 67, attribute, 84, 58, 15);
        } else if (position == Position.ST) {
            attributes = new TechnicalAttributes(attribute, 64, 32, 82, 72, 15);
        } else {
            attributes = new TechnicalAttributes(61, attribute, 66, 82, 71, 15);
        }
        return new Player(name, "Brasil", position, null, 25, attributes, potential, 30_000);
    }

    private static void add(Club club, Player player, int contractEndYear) {
        player.setContractEndYear(contractEndYear);
        player.transferTo(club);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
