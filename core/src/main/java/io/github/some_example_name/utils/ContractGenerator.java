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
        // Faixas iniciais da WFL por perfil etário. A variação dentro da
        // faixa cria contratos diferentes no elenco sem fugir da regra.
        if (age <= 20) {
            return randomBetween(3, 5);
        }

        if (age <= 24) {
            return randomBetween(2, 5);
        }

        if (age <= 29) {
            return randomBetween(2, 4);
        }

        if (age <= 33) {
            return randomBetween(1, 3);
        }

        return randomBetween(1, 2);
    }

    private static int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
