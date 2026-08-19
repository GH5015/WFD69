package io.github.some_example_name.screens;

public enum MatchPhase {
    DEFESA(2.0f),
    CONSTRUCAO(3.0f),
    ATAQUE(3.5f),
    CONTRA_ATAQUE(6.0f), // Alta velocidade de transição
    ESCANTEIO(4.0f),
    FALTA(2.5f);

    public final float speed;

    MatchPhase(float speed) {
        this.speed = speed;
    }
}
