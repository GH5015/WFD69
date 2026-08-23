package io.github.some_example_name.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClubNeedEvaluator {

    public enum TeamPhase {
        CONTENDER,    // Time forte (OVR Alto + Elenco Maduro) -> Busca o título AGORA
        BUYER,        // Time emergente (OVR Médio/Alto + Elenco Jovem) -> Precisa de peças finais
        SELLER,       // Time mediano/estagnado (OVR Médio) -> Aceita vender se a oferta for boa
        REBUILDING    // Time fraco (OVR Baixo) -> Vende veteranos por Picks/Jovens
    }

    /**
     * Calcula o nível de necessidade (1 a 5 estrelas) do clube para cada posição
     */
    public static Map<String, Integer> calculatePositionNeeds(Club club) {
        Map<String, Integer> needs = new HashMap<>();
        String[] positions = {
            "GK", "CB", "LB", "RB", "LWB", "RWB",
            "CDM", "CM", "CAM", "LW", "RW", "CF", "ST"
        };

        for (String pos : positions) {
            List<Player> posPlayers = club.getSquad().stream()
                .filter(p -> p.getPosition().equals(pos))
                .sorted((a, b) -> Integer.compare(b.getOverall(), a.getOverall()))
                .collect(Collectors.toList());

            if (posPlayers.isEmpty()) {
                needs.put(pos, 5); // Urgência máxima: posição vazia
                continue;
            }

            int topOvr = posPlayers.get(0).getOverall();
            int count = posPlayers.size();

            // Avalia carência com base na qualidade do titular e profundidade do banco
            if (topOvr >= 86 && count >= 2) {
                needs.put(pos, 1); // Carência nula / Excesso de qualidade
            } else if (topOvr >= 82) {
                needs.put(pos, 2);
            } else if (topOvr >= 76) {
                needs.put(pos, 3);
            } else if (topOvr >= 70) {
                needs.put(pos, 4);
            } else {
                needs.put(pos, 5); // Urgência extrema
            }
        }
        return needs;
    }

    /**
     * Determina a fase estratégica da franquia com base no Overall e Idade Médios do Top 11
     */
    public static TeamPhase getTeamPhase(Club club) {
        if (club == null || club.getSquad() == null || club.getSquad().isEmpty()) {
            return TeamPhase.REBUILDING;
        }

        // 1. Média do Overall Top 11 (Titulares Principais)
        double top11OvrAvg = club.getSquad().stream()
            .mapToInt(Player::getOverall)
            .boxed()
            .sorted((a, b) -> Integer.compare(b, a))
            .limit(11)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(70.0);

        // 2. Média de Idade dos Titulares (Top 11 por Overall)
        double top11AgeAvg = club.getSquad().stream()
            .sorted((a, b) -> Integer.compare(b.getOverall(), a.getOverall()))
            .limit(11)
            .mapToInt(Player::getAge)
            .average()
            .orElse(25.0);

        // Classificação dinâmica por momento de mercado
        if (top11OvrAvg >= 83.0) {
            // Elenco forte: Se for experiente vai pro título agora, se for jovem busca montar dinastia
            return (top11AgeAvg >= 26.5) ? TeamPhase.CONTENDER : TeamPhase.BUYER;
        } else if (top11OvrAvg >= 77.0) {
            // Elenco mediano: Se for jovem está em ascensão, se for velho estagnou e aceita vender
            return (top11AgeAvg <= 25.0) ? TeamPhase.BUYER : TeamPhase.SELLER;
        } else {
            // Overall baixo (Top 11 < 77) -> Reconstrução Total
            return TeamPhase.REBUILDING;
        }
    }
}
