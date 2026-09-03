package io.github.some_example_name.utils;

import java.util.Random;

public class NameGenerator {

    private static final String[] NATIONALITIES = {
        "Argentina", "Brasil", "Alemanha", "Espanha", "França", "Holanda",
        "Inglaterra", "Itália", "Japão", "México", "Polônia", "Portugal",
        "Suécia", "Uruguai", "Estados Unidos", "Iugoslávia", "Dinamarca", "Peru"
    };
    private static final String[] FIRST_NAMES = {
        "Carlos", "José", "Mário", "Roberto", "Wilson", "Ricardo", "Paulo", "Fernando", "Antônio", "Luiz",
        "João", "Edson", "Gilberto", "Ademir", "Jair", "Gerson", "Tostão", "Rivellino", "Clodoaldo", "Piazza"
    };

    private static final String[] LAST_NAMES = {
        "Silva", "Santos", "Oliveira", "Souza", "Pereira", "Costa", "Rodrigues", "Almeida", "Nascimento", "Lopes",
        "Barbosa", "Martins", "Araújo", "Cardoso", "Ribeiro", "Mendes", "Fernandes", "Carvalho", "Gomes", "Teixeira"
    };

    private static final Random RANDOM = new Random();

    public static String generateName() {
        return generateName(RANDOM);
    }

    public static String generateNationality() {
        return generateNationality(RANDOM);
    }

    public static String generateName(Random random) {
        Random source = random != null ? random : RANDOM;
        return FIRST_NAMES[source.nextInt(FIRST_NAMES.length)] + " "
            + LAST_NAMES[source.nextInt(LAST_NAMES.length)];
    }

    public static String generateNationality(Random random) {
        Random source = random != null ? random : RANDOM;
        return NATIONALITIES[source.nextInt(NATIONALITIES.length)];
    }
}
