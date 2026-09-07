package io.github.some_example_name;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

public final class SquadNumberRegressionTest {
    public static void main(String[] args) throws Exception {
        Club club = new Club();
        Player first = player("Primeiro");
        first.setSquadNumber(10);
        first.transferTo(club);

        Player conflict = player("Conflito");
        conflict.setSquadNumber(10);
        conflict.transferTo(club);

        Player third = player("Terceiro");
        third.transferTo(club);

        club.ensureSquadNumbers();
        require(uniqueNumbers(club), "Atribuição inicial criou números duplicados.");
        require(first.getSquadNumber() == 10, "Número livre não foi preservado.");
        require(club.changeSquadNumber(third, 7), "Número livre foi recusado.");
        require(!club.changeSquadNumber(third, 10), "Número já utilizado foi aceito.");
        require(third.getSquadNumber() == 7, "Tentativa inválida alterou o número atual.");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(club);
        }
        Club restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Club) input.readObject();
        }
        restored.ensureSquadNumbers();
        require(uniqueNumbers(restored), "Salvamento não preservou números únicos.");
        require(restored.getSquad().stream().anyMatch(p -> p.getName().equals("Terceiro") && p.getSquadNumber() == 7),
            "Número alterado não persistiu no save.");

        Club destination = new Club();
        Player ownerOfTen = player("Dono do 10");
        ownerOfTen.setSquadNumber(10);
        ownerOfTen.transferTo(destination);
        first.transferTo(destination);
        require(uniqueNumbers(destination), "Transferência criou números duplicados.");
        require(ownerOfTen.getSquadNumber() == 10 && first.getSquadNumber() != 10,
            "Conflito de transferência não foi resolvido para o recém-chegado.");

        System.out.println("Squad numbers: assignment, editing, uniqueness, transfer and save persistence OK.");
    }

    private static Player player(String name) {
        return new Player(
            name,
            "Brasil",
            Position.CM,
            null,
            24,
            new TechnicalAttributes(65, 70, 62, 76, 68, 20),
            82,
            20_000
        );
    }

    private static boolean uniqueNumbers(Club club) {
        Set<Integer> numbers = new HashSet<>();
        return club.getSquad().stream().allMatch(player -> {
            int number = player.getSquadNumber();
            return number >= 1 && number <= 99 && numbers.add(number);
        });
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
