package io.github.some_example_name;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Position;
import io.github.some_example_name.model.TechnicalAttributes;

public final class PositionOverallRegressionTest {
    public static void main(String[] args) {
        Player fullback = new Player(
            "Lateral de teste", "Brasil", Position.LB, Position.RB, 24,
            new TechnicalAttributes(50, 70, 80, 80, 70, 10), 85, 10_000
        );

        int original = fullback.getOverall();
        require(fullback.getEffectiveOverallForPosition(Position.RB) >= original - 2,
            "A posicao secundaria perdeu mais de 2 pontos");
        require(fullback.getEffectiveOverallForPosition(Position.LWB) >= original - 4,
            "Uma posicao naturalmente proxima perdeu mais de 4 pontos");
        require(fullback.getEffectiveOverallForPosition(Position.LW) >= original - 7,
            "Uma posicao relacionada perdeu mais de 7 pontos");
        require(fullback.getEffectiveOverallForPosition(Position.ST) < original - 7,
            "Uma posicao incompatível ficou sem penalidade relevante");
        require(fullback.getEffectiveOverallForPosition(Position.GK) <= original / 4,
            "A separacao entre goleiro e jogador de linha foi enfraquecida");

        Player libero = new Player(
            "Libero de teste", "Italia", Position.SW, null, 24,
            new TechnicalAttributes(40, 65, 82, 86, 55, 10), 88, 10_000
        );
        require(libero.getOverall() == libero.calculateOverallForPosition(Position.CB),
            "Libero deve usar a mesma base tecnica de zagueiro");
        require(libero.getEffectiveOverallForPosition(Position.CB) >= libero.getOverall() - 4,
            "A transicao SW/CB perdeu overall demais");

        System.out.println("Position overall: secondary, adjacent, related and incompatible penalties OK.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
