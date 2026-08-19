package io.github.some_example_name.screens;

import com.badlogic.gdx.math.Vector2;

public class TacticalPostures {

    public enum Posture {
        DEFESA,
        CONSTRUCAO,
        ATAQUE
    }

    public static Vector2[] get433(Posture posture) {
        Vector2[] pos = new Vector2[11];

        switch (posture) {
            case DEFESA:
                // 0: Goleiro
                pos[0] = new Vector2(0.50f, 0.05f);
                // 1, 2: Zagueiros Centrais
                pos[1] = new Vector2(0.38f, 0.15f);
                pos[2] = new Vector2(0.62f, 0.15f);
                // 3, 4: Laterais (Baixados)
                pos[3] = new Vector2(0.15f, 0.18f);
                pos[4] = new Vector2(0.85f, 0.18f);
                // 5: Volante Central (Proteção da zaga)
                pos[5] = new Vector2(0.50f, 0.28f);
                // 6, 7: Meias / Volantes de contenção
                pos[6] = new Vector2(0.32f, 0.35f);
                pos[7] = new Vector2(0.68f, 0.35f);
                // 8, 9: Pontas / Meias abertos
                pos[8] = new Vector2(0.20f, 0.45f);
                pos[9] = new Vector2(0.80f, 0.45f);
                // 10: Centroavante (Primeiro combate / Pressão)
                pos[10] = new Vector2(0.50f, 0.52f);
                break;

            case CONSTRUCAO:
                // 0: Goleiro
                pos[0] = new Vector2(0.50f, 0.08f);
                // 1, 2: Zagueiros Centrais (Subindo linha)
                pos[1] = new Vector2(0.35f, 0.25f);
                pos[2] = new Vector2(0.65f, 0.25f);
                // 3, 4: Laterais
                pos[3] = new Vector2(0.12f, 0.38f);
                pos[4] = new Vector2(0.88f, 0.38f);
                // 5: Volante Central (Distribuição)
                pos[5] = new Vector2(0.50f, 0.42f);
                // 6, 7: Meias de Transição
                pos[6] = new Vector2(0.32f, 0.55f);
                pos[7] = new Vector2(0.68f, 0.55f);
                // 8, 9: Pontas
                pos[8] = new Vector2(0.18f, 0.68f);
                pos[9] = new Vector2(0.82f, 0.68f);
                // 10: Centroavante
                pos[10] = new Vector2(0.50f, 0.72f);
                break;

            case ATAQUE:
                // 0: Goleiro (Adiantado)
                pos[0] = new Vector2(0.50f, 0.12f);
                // 1, 2: Zagueiros Centrais (Linha Alta)
                pos[1] = new Vector2(0.35f, 0.40f);
                pos[2] = new Vector2(0.65f, 0.40f);
                // 3, 4: Laterais Ofensivos
                pos[3] = new Vector2(0.10f, 0.58f);
                pos[4] = new Vector2(0.90f, 0.58f);
                // 5: Volante Central (Sobra)
                pos[5] = new Vector2(0.50f, 0.55f);
                // 6, 7: Meias na Entrada da Área
                pos[6] = new Vector2(0.30f, 0.72f);
                pos[7] = new Vector2(0.70f, 0.72f);
                // 8, 9: Pontas Agudos
                pos[8] = new Vector2(0.15f, 0.82f);
                pos[9] = new Vector2(0.85f, 0.82f);
                // 10: Centroavante (Dentro da Área / Referência)
                pos[10] = new Vector2(0.50f, 0.86f);
                break;
        }

        return pos;
    }
}
