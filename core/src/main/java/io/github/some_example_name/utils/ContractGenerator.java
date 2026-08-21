package io.github.some_example_name.utils;

import io.github.some_example_name.model.Player;
import java.util.concurrent.ThreadLocalRandom;

public class ContractGenerator {

    public static void generateInitialContract(Player player, int currentYear) {
        int years = calculateContractLength(player);

        player.setContractYears(years);
        player.setContractEndYear(currentYear + years);
    }

    private static int calculateContractLength(Player player) {
        int age = player.getAge();
        int potentialGap = player.getPotential() - player.getOverall(); // Margem de evolução

        // Jogadores Jovens (Até 22 anos)
        if (age <= 22) {
            // Alta promessa (ex: POT 90+ com margem de crescimento alta): contrato longo (4-5 anos)
            if (player.getPotential() >= 85 || potentialGap >= 12) {
                return randomBetween(4, 5);
            }
            return randomBetween(2, 4);
        }

        // Jogadores no auge (23 a 29 anos)
        if (age <= 29) {
            // Estrelas ou titulares consolidados
            if (player.getOverall() >= 80) {
                return randomBetween(3, 5);
            }
            return randomBetween(2, 4);
        }

        // Veteranos (30 a 33 anos)
        if (age <= 33) {
            return randomBetween(1, 3);
        }

        // Final de carreira (34+ anos)
        return randomBetween(1, 2);
    }

    private static int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
