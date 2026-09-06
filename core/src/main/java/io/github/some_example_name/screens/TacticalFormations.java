package io.github.some_example_name.screens;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.model.Formation;

import java.util.List;

public class TacticalFormations {

    public static Vector2[] getPositions(String formation, MatchPhase phase) {
        if (formation == null) return get433(phase);

        Formation definedFormation = Formation.fromName(formation);
        if (usesGeneratedLayout(definedFormation)) {
            return getRoleBasedPositions(definedFormation.getPositionSlots(), phase);
        }

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

    /** As formações novas usam os próprios slots para que cada variação tenha desenho distinto. */
    private static boolean usesGeneratedLayout(Formation formation) {
        if (formation == null) return false;
        switch (formation) {
            case F_3142:
            case F_3412:
            case F_3421:
            case F_41212_2:
            case F_4132:
            case F_4141:
            case F_4213:
            case F_4222:
            case F_4231_2:
            case F_4312:
            case F_4321:
            case F_433_2:
            case F_433_3:
            case F_433_FALSE9:
            case F_4411_2:
            case F_442_2:
            case F_451_2:
            case F_5212:
            case F_523:
            case F_541:
                return true;
            default:
                return false;
        }
    }

    private static Vector2[] getRoleBasedPositions(List<String> slots, MatchPhase phase) {
        if (phase == MatchPhase.ESCANTEIO) return getCornerPositions();

        Vector2[] result = new Vector2[slots.size()];
        for (int slot = 0; slot < slots.size(); slot++) {
            String position = slots.get(slot);
            float baseX = roleDepth(position);
            float x = phase == MatchPhase.DEFESA
                ? 0.04f + Math.max(0f, baseX - 0.08f) * 0.50f
                : phase == MatchPhase.ATAQUE
                    ? Math.min(0.92f, 0.18f + Math.max(0f, baseX - 0.08f) * 1.13f)
                    : baseX;
            float y = roleWidth(position, slots, slot);
            if (phase == MatchPhase.DEFESA) y = 0.50f + (y - 0.50f) * 0.84f;
            result[slot] = new Vector2(x, y);
        }
        return result;
    }

    private static float roleDepth(String rawPosition) {
        String position = rawPosition == null ? "CM" : rawPosition.toUpperCase();
        if (position.equals("GK")) return 0.08f;
        if (position.equals("CB") || position.equals("SW")) return 0.22f;
        if (position.equals("LB") || position.equals("RB")) return 0.30f;
        if (position.equals("LWB") || position.equals("RWB")) return 0.43f;
        if (position.equals("CDM")) return 0.40f;
        if (position.equals("CM") || position.equals("LM") || position.equals("RM")) return 0.52f;
        if (position.equals("CAM")) return 0.63f;
        if (position.equals("LW") || position.equals("RW")) return 0.72f;
        if (position.equals("CF")) return 0.73f;
        return 0.79f;
    }

    private static float roleWidth(String rawPosition, List<String> slots, int slotIndex) {
        String position = rawPosition == null ? "CM" : rawPosition.toUpperCase();
        if (position.equals("LB") || position.equals("LWB") || position.equals("LM") || position.equals("LW")) return 0.12f;
        if (position.equals("RB") || position.equals("RWB") || position.equals("RM") || position.equals("RW")) return 0.88f;
        if (position.equals("GK")) return 0.50f;

        int count = 0;
        int index = 0;
        for (int slot = 0; slot < slots.size(); slot++) {
            if (position.equalsIgnoreCase(slots.get(slot))) {
                if (slot < slotIndex) index++;
                count++;
            }
        }
        return (index + 1f) / (count + 1f);
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
