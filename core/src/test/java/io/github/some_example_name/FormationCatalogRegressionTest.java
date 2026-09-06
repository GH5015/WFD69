package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.model.Formation;
import io.github.some_example_name.screens.MatchPhase;
import io.github.some_example_name.screens.TacticalFormations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class FormationCatalogRegressionTest {
    public static void main(String[] args) {
        String[] expectedNames = {
            "3-1-4-2", "3-4-1-2", "3-4-2-1", "3-4-3", "3-5-2",
            "4-1-2-1-2", "4-1-2-1-2 (2)", "4-1-3-2", "4-1-4-1", "4-2-1-3",
            "4-2-2-2", "4-2-3-1", "4-2-3-1 (2)", "4-2-4", "4-3-1-2",
            "4-3-2-1", "4-3-3", "4-3-3 (2)", "4-3-3 (3)", "4-3-3 (4)",
            "4-4-1-1 (2)", "4-4-2", "4-4-2 (2)", "4-5-1", "4-5-1 (2)",
            "5-2-1-2", "5-2-3", "5-3-2", "5-4-1"
        };

        require(Formation.values().length == expectedNames.length, "O catalogo deve ter 29 formacoes");
        Set<String> actualNames = new HashSet<>();
        for (Formation formation : Formation.values()) {
            actualNames.add(formation.getName());
            require(formation.getPositionSlots().size() == 11,
                formation.getName() + " nao possui 11 slots");
            require(formation.getPositionSlots().stream().filter("GK"::equals).count() == 1,
                formation.getName() + " deve possuir um goleiro");

            Vector2[] positions = TacticalFormations.getPositions(formation.getName(), MatchPhase.CONSTRUCAO);
            require(positions.length == 11, formation.getName() + " nao desenha 11 jogadores");
            for (Vector2 position : positions) {
                require(position.x >= 0f && position.x <= 1f && position.y >= 0f && position.y <= 1f,
                    formation.getName() + " desenha jogador fora do campo");
            }
        }
        require(actualNames.equals(new HashSet<>(Arrays.asList(expectedNames))),
            "Ha formacoes ausentes ou inesperadas no catalogo");

        require(!Formation.F_41212.getPositionSlots().equals(Formation.F_41212_2.getPositionSlots()),
            "As variacoes do 4-1-2-1-2 devem ser diferentes");
        require(!Formation.F_4231.getPositionSlots().equals(Formation.F_4231_2.getPositionSlots()),
            "As variacoes do 4-2-3-1 devem ser diferentes");
        require(!Formation.F_433.getPositionSlots().equals(Formation.F_433_2.getPositionSlots()),
            "4-3-3 base e (2) devem ser diferentes");
        require(!Formation.F_433_2.getPositionSlots().equals(Formation.F_433_3.getPositionSlots()),
            "4-3-3 (2) e (3) devem ser diferentes");
        require(!Formation.F_433_3.getPositionSlots().equals(Formation.F_433_FALSE9.getPositionSlots()),
            "4-3-3 (3) e (4) devem ser diferentes");
        require(Formation.fromName("4-3-3 (2)") == Formation.F_433_2,
            "A busca por nome nao identificou uma variacao");
        require(Formation.fromName("4-3-3 (Falso 9)") == Formation.F_433_FALSE9,
            "O nome legado do Falso 9 deixou de ser compativel");

        System.out.println("Formation catalog: 29 formations, distinct variants and pitch layouts OK.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
