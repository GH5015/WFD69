package io.github.some_example_name.utils;

import io.github.some_example_name.model.Player;
import java.util.concurrent.ThreadLocalRandom;

public class ContractGenerator {

    public static void generateInitialContract(Player player, int currentYear) {
        int years = calculateContractLength(player);

        // Se o salário ainda não foi atribuído no construtor (está <= 0), 
        // sincroniza com a fórmula do método financeiro getMonthlySalary()
        if (player.getSalary() <= 0) {
            player.setSalary(player.getMonthlySalary());
        }

        player.setContractYears(years);
        player.setContractEndYear(currentYear + years);
    }

    private static int calculateContractLength(Player player) {
        int age = player.getAge();
        int potentialGap = player.getPotential() - player.getOverall();

        // Promessas e Jovens (Até 22 anos)
        if (age <= 22) {
            if (player.getPotential() >= 85 || potentialGap >= 12) {
                return randomBetween(4, 5); // Segura o ativo por mais tempo
            }
            return randomBetween(2, 4);
        }

        // Jogadores no Auge (23 a 29 anos)
        if (age <= 29) {
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
