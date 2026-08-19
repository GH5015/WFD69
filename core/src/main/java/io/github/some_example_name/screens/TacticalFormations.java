package io.github.some_example_name.screens;

import com.badlogic.gdx.math.Vector2;

public class TacticalFormations {

    public static Vector2[] getPositions(String formation, MatchPhase phase) {
        if (formation == null) return get433(phase);

        String normalized = formation.toLowerCase().replaceAll("[^a-z0-9]", "");

        if (normalized.contains("424")) return get424(phase);
        if (normalized.contains("442")) return get442(phase);
        if (normalized.contains("4231")) return get4231(phase);
        if (normalized.contains("41212") || normalized.contains("diamond")) return get41212(phase);
        if (normalized.contains("352")) return get352(phase);
        if (normalized.contains("532")) return get532(phase);
        if (normalized.contains("343")) return get343(phase);
        if (normalized.contains("451")) return get451(phase);
        if (normalized.contains("falso9") || normalized.contains("false9")) return get433False9(phase);

        return get433(phase);
    }

    // Posicionamento padrão para situações de bola parada (Escanteio)
    private static Vector2[] getCornerPositions() {
        return new Vector2[]{
            new Vector2(0.04f, 0.50f), // 0: Goleiro
            new Vector2(0.12f, 0.35f), new Vector2(0.12f, 0.65f), // Zagueiros na sobra
            new Vector2(0.20f, 0.15f), new Vector2(0.20f, 0.85f), // Laterais
            new Vector2(0.82f, 0.38f), new Vector2(0.82f, 0.62f), // Jogadores de área
            new Vector2(0.86f, 0.45f), new Vector2(0.86f, 0.55f),
            new Vector2(0.88f, 0.50f), // Centroavante
            new Vector2(0.96f, 0.08f)  // Batedor no escanteio
        };
    }

    // ==========================================
    // 1. 4-3-3
    // ==========================================
    private static Vector2[] get433(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f), // GK
                    new Vector2(0.15f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.15f, 0.85f), // Defesa Compacta
                    new Vector2(0.22f, 0.50f), // Volante
                    new Vector2(0.28f, 0.35f), new Vector2(0.28f, 0.65f), // Meias
                    new Vector2(0.38f, 0.18f), new Vector2(0.40f, 0.50f), new Vector2(0.38f, 0.82f)  // Atacantes recompondo
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f), // GK adiantado
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f), // Defesa alta
                    new Vector2(0.62f, 0.50f), // Volante subindo
                    new Vector2(0.76f, 0.32f), new Vector2(0.76f, 0.68f), // Meias criando
                    new Vector2(0.88f, 0.15f), new Vector2(0.92f, 0.50f), new Vector2(0.88f, 0.85f)  // Trio de ataque DENTRO da área
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.38f, 0.50f),
                    new Vector2(0.52f, 0.32f), new Vector2(0.52f, 0.68f),
                    new Vector2(0.68f, 0.18f), new Vector2(0.72f, 0.50f), new Vector2(0.68f, 0.82f)
                };
        }
    }

    // ==========================================
    // 2. 4-4-2
    // ==========================================
    private static Vector2[] get442(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.25f, 0.15f), new Vector2(0.24f, 0.38f), new Vector2(0.24f, 0.62f), new Vector2(0.25f, 0.85f),
                    new Vector2(0.38f, 0.38f), new Vector2(0.38f, 0.62f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.80f, 0.12f), new Vector2(0.72f, 0.35f), new Vector2(0.72f, 0.65f), new Vector2(0.80f, 0.88f),
                    new Vector2(0.90f, 0.38f), new Vector2(0.90f, 0.62f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.50f, 0.15f), new Vector2(0.45f, 0.35f), new Vector2(0.45f, 0.65f), new Vector2(0.50f, 0.85f),
                    new Vector2(0.70f, 0.38f), new Vector2(0.70f, 0.62f)
                };
        }
    }

    // ==========================================
    // 3. 4-2-4
    // ==========================================
    private static Vector2[] get424(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.25f, 0.38f), new Vector2(0.25f, 0.62f),
                    new Vector2(0.38f, 0.15f), new Vector2(0.40f, 0.38f), new Vector2(0.40f, 0.62f), new Vector2(0.38f, 0.85f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.68f, 0.35f), new Vector2(0.68f, 0.65f),
                    new Vector2(0.88f, 0.12f), new Vector2(0.92f, 0.38f), new Vector2(0.92f, 0.62f), new Vector2(0.88f, 0.88f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.45f, 0.35f), new Vector2(0.45f, 0.65f),
                    new Vector2(0.70f, 0.15f), new Vector2(0.75f, 0.38f), new Vector2(0.75f, 0.62f), new Vector2(0.70f, 0.85f)
                };
        }
    }

    // ==========================================
    // 4. 4-2-3-1
    // ==========================================
    private static Vector2[] get4231(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f),
                    new Vector2(0.32f, 0.18f), new Vector2(0.30f, 0.50f), new Vector2(0.32f, 0.82f),
                    new Vector2(0.42f, 0.50f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.65f, 0.35f), new Vector2(0.65f, 0.65f),
                    new Vector2(0.85f, 0.15f), new Vector2(0.85f, 0.50f), new Vector2(0.85f, 0.85f),
                    new Vector2(0.92f, 0.50f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.40f, 0.35f), new Vector2(0.40f, 0.65f),
                    new Vector2(0.58f, 0.18f), new Vector2(0.58f, 0.50f), new Vector2(0.58f, 0.82f),
                    new Vector2(0.75f, 0.50f)
                };
        }
    }

    // ==========================================
    // 5. 4-1-2-1-2 (Diamond)
    // ==========================================
    private static Vector2[] get41212(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.20f, 0.50f),
                    new Vector2(0.28f, 0.28f), new Vector2(0.28f, 0.72f),
                    new Vector2(0.32f, 0.50f),
                    new Vector2(0.42f, 0.38f), new Vector2(0.42f, 0.62f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.60f, 0.50f),
                    new Vector2(0.76f, 0.20f), new Vector2(0.76f, 0.80f),
                    new Vector2(0.82f, 0.50f),
                    new Vector2(0.90f, 0.38f), new Vector2(0.90f, 0.62f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.38f, 0.50f),
                    new Vector2(0.50f, 0.22f), new Vector2(0.50f, 0.78f),
                    new Vector2(0.60f, 0.50f),
                    new Vector2(0.74f, 0.38f), new Vector2(0.74f, 0.62f)
                };
        }
    }

    // ==========================================
    // 6. 3-5-2
    // ==========================================
    private static Vector2[] get352(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.12f, 0.28f), new Vector2(0.10f, 0.50f), new Vector2(0.12f, 0.72f),
                    new Vector2(0.20f, 0.12f),
                    new Vector2(0.22f, 0.38f), new Vector2(0.22f, 0.62f),
                    new Vector2(0.20f, 0.88f),
                    new Vector2(0.32f, 0.50f),
                    new Vector2(0.42f, 0.38f), new Vector2(0.42f, 0.62f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.42f, 0.25f), new Vector2(0.38f, 0.50f), new Vector2(0.42f, 0.75f),
                    new Vector2(0.82f, 0.10f),
                    new Vector2(0.68f, 0.35f), new Vector2(0.68f, 0.65f),
                    new Vector2(0.82f, 0.90f),
                    new Vector2(0.82f, 0.50f),
                    new Vector2(0.90f, 0.38f), new Vector2(0.90f, 0.62f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.22f, 0.25f), new Vector2(0.20f, 0.50f), new Vector2(0.22f, 0.75f),
                    new Vector2(0.48f, 0.12f),
                    new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f),
                    new Vector2(0.48f, 0.88f),
                    new Vector2(0.60f, 0.50f),
                    new Vector2(0.74f, 0.38f), new Vector2(0.74f, 0.62f)
                };
        }
    }

    // ==========================================
    // 7. 5-3-2
    // ==========================================
    private static Vector2[] get532(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.12f), new Vector2(0.11f, 0.30f), new Vector2(0.10f, 0.50f), new Vector2(0.11f, 0.70f), new Vector2(0.14f, 0.88f),
                    new Vector2(0.24f, 0.30f), new Vector2(0.22f, 0.50f), new Vector2(0.24f, 0.70f),
                    new Vector2(0.40f, 0.38f), new Vector2(0.40f, 0.62f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.65f, 0.10f), new Vector2(0.42f, 0.28f), new Vector2(0.38f, 0.50f), new Vector2(0.42f, 0.72f), new Vector2(0.65f, 0.90f),
                    new Vector2(0.75f, 0.30f), new Vector2(0.70f, 0.50f), new Vector2(0.75f, 0.70f),
                    new Vector2(0.90f, 0.38f), new Vector2(0.90f, 0.62f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.38f, 0.12f), new Vector2(0.22f, 0.28f), new Vector2(0.20f, 0.50f), new Vector2(0.22f, 0.72f), new Vector2(0.38f, 0.88f),
                    new Vector2(0.48f, 0.30f), new Vector2(0.42f, 0.50f), new Vector2(0.48f, 0.70f),
                    new Vector2(0.72f, 0.38f), new Vector2(0.72f, 0.62f)
                };
        }
    }

    // ==========================================
    // 8. 3-4-3
    // ==========================================
    private static Vector2[] get343(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.12f, 0.28f), new Vector2(0.10f, 0.50f), new Vector2(0.12f, 0.72f),
                    new Vector2(0.24f, 0.15f), new Vector2(0.22f, 0.38f), new Vector2(0.22f, 0.62f), new Vector2(0.24f, 0.85f),
                    new Vector2(0.38f, 0.20f), new Vector2(0.40f, 0.50f), new Vector2(0.38f, 0.80f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.42f, 0.25f), new Vector2(0.38f, 0.50f), new Vector2(0.42f, 0.75f),
                    new Vector2(0.78f, 0.12f), new Vector2(0.70f, 0.35f), new Vector2(0.70f, 0.65f), new Vector2(0.78f, 0.88f),
                    new Vector2(0.90f, 0.15f), new Vector2(0.92f, 0.50f), new Vector2(0.90f, 0.85f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.22f, 0.25f), new Vector2(0.20f, 0.50f), new Vector2(0.22f, 0.75f),
                    new Vector2(0.48f, 0.15f), new Vector2(0.44f, 0.35f), new Vector2(0.44f, 0.65f), new Vector2(0.48f, 0.85f),
                    new Vector2(0.68f, 0.18f), new Vector2(0.72f, 0.50f), new Vector2(0.68f, 0.82f)
                };
        }
    }

    // ==========================================
    // 9. 4-5-1
    // ==========================================
    private static Vector2[] get451(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.24f, 0.15f), new Vector2(0.25f, 0.35f), new Vector2(0.20f, 0.50f), new Vector2(0.25f, 0.65f), new Vector2(0.24f, 0.85f),
                    new Vector2(0.38f, 0.50f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.82f, 0.15f), new Vector2(0.78f, 0.35f), new Vector2(0.65f, 0.50f), new Vector2(0.78f, 0.65f), new Vector2(0.82f, 0.85f),
                    new Vector2(0.92f, 0.50f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.52f, 0.15f), new Vector2(0.55f, 0.35f), new Vector2(0.38f, 0.50f), new Vector2(0.55f, 0.65f), new Vector2(0.52f, 0.85f),
                    new Vector2(0.72f, 0.50f)
                };
        }
    }

    // ==========================================
    // 10. 4-3-3 (Falso 9)
    // ==========================================
    private static Vector2[] get433False9(MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        switch (phase) {
            case DEFESA:
                return new Vector2[]{
                    new Vector2(0.04f, 0.50f),
                    new Vector2(0.14f, 0.15f), new Vector2(0.12f, 0.38f), new Vector2(0.12f, 0.62f), new Vector2(0.14f, 0.85f),
                    new Vector2(0.22f, 0.50f),
                    new Vector2(0.28f, 0.32f), new Vector2(0.28f, 0.68f),
                    new Vector2(0.38f, 0.20f), new Vector2(0.32f, 0.50f), new Vector2(0.38f, 0.80f)
                };
            case ATAQUE:
                return new Vector2[]{
                    new Vector2(0.18f, 0.50f),
                    new Vector2(0.55f, 0.12f), new Vector2(0.42f, 0.35f), new Vector2(0.42f, 0.65f), new Vector2(0.55f, 0.88f),
                    new Vector2(0.60f, 0.50f),
                    new Vector2(0.76f, 0.30f), new Vector2(0.76f, 0.70f),
                    new Vector2(0.90f, 0.18f), new Vector2(0.78f, 0.50f), new Vector2(0.90f, 0.82f)
                };
            default: // CONSTRUÇÃO
                return new Vector2[]{
                    new Vector2(0.08f, 0.50f),
                    new Vector2(0.30f, 0.15f), new Vector2(0.22f, 0.35f), new Vector2(0.22f, 0.65f), new Vector2(0.30f, 0.85f),
                    new Vector2(0.38f, 0.50f),
                    new Vector2(0.52f, 0.32f), new Vector2(0.52f, 0.68f),
                    new Vector2(0.68f, 0.18f), new Vector2(0.58f, 0.50f), new Vector2(0.68f, 0.82f)
                };
        }
    }
}
