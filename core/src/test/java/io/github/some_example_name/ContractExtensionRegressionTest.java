package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class ContractExtensionRegressionTest {
    public static void main(String[] args) {
        Club club = new Club("Casa", "Brasil", "Ocidental", 80, 40_000_000, "Arena", "santos.png");
        Player player = new Player("Atleta", "Brasil", Position.CM, null, 25,
            new TechnicalAttributes(70, 70, 65, 72, 68, 60), 82, 20_000);
        player.transferTo(club);
        player.signContract(300_000, 2, 1970);
        require(player.getContractEndYear() == 1972, "Initial contract end is wrong");

        player.renewContract(420_000, 3, 1970);
        require(player.getContractEndYear() == 1975, "Renewal replaced instead of extending the current term");
        require(player.getRemainingContractYears(1970) == 5, "Remaining duration did not include the extension");
        require(player.getNextContractNegotiationYear() == 1973, "Next negotiation window ignores extended end year");

        player.transferTo(null);
        player.signContract(500_000, 2, 1971);
        require(player.getContractEndYear() == 1973, "New free-agent deal inherited the previous contract term");
        System.out.println("Contracts: renewals extend the existing term; new signings start from the current year OK.");
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
