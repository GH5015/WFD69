package io.github.some_example_name.utils;

import io.github.some_example_name.model.Match;
import io.github.some_example_name.model.MatchEvent;
import java.util.Random;

public class MatchNarrator {

    private static final Random random = new Random();

    private static final String[] GOAL_PREFIXES = {
        "⚽ GOOOOOOOOLAAAAAAAAÇO!",
        "⚽ É GOL! É GOL! É GOL!",
        "⚽ TÁ LÁ NO FUNDO DA REDE!",
        "⚽ BALANÇOU A REDE! QUE JOGADA!"
    };

    private static final String[] SHOT_PREFIXES = {
        "🎯 Bateu direto!",
        "🎯 Arriscou pro gol!",
        "🎯 Finalização perigosa!",
        "🎯 Mandou a bomba!"
    };

    private static final String[] CORNER_PREFIXES = {
        "🚩 Vai levantar na área!",
        "🚩 Bola alçada na marca do pênalti!",
        "🚩 Cobrança de escanteio!",
        "🚩 Bola parada perigosa na área!"
    };

    private static final String[] CARD_PREFIXES = {
        "🟨 O árbitro apita e mostra o cartão!",
        "🟨 Chegou forte e tomou cartão!",
        "🟨 Advertência pesada do juiz!",
        "🟨 Amarelo justo pela jogada!"
    };

    private static final String[] RED_CARD_PREFIXES = {
        "🔴 EXPULSO! VAI PRO CHUVEIRO MAIS CEDO!",
        "🔴 CARTÃO VERMELHO! RUA!",
        "🔴 ENTRADA DURÍSSIMA! EXPULSÃO DIRETA!"
    };

    private static final String[] FOUL_PREFIXES = {
        "🛑 Falta assinalada pelo árbitro.",
        "🛑 Jogo paralisado por infração.",
        "🛑 Entrada forte, o juiz apita.",
        "🛑 Parou a jogada com falta!"
    };

    private static final String[] SUBSTITUTION_PREFIXES = {
        "🔄 MUDANÇA NA EQUIPE!",
        "🔄 ALTERAÇÃO TÁTICA!",
        "🔄 O TREINADOR MEXE NO TIME!",
        "🔄 SUBSTITUIÇÃO CONFIRMADA!"
    };

    private static final String[] BUILDUP_PREFIXES = {
        "🔄",
        "🧠",
        "⚙️",
        "📐"
    };

    private static final String[] POSSESSION_PREFIXES = {
        "⚽",
        "🟢",
        "📋",
        "⏱️"
    };

    public static String generateCommentary(MatchEvent event, Match match) {
        if (event == null || event.description == null) return "";

        String desc = event.description;

        switch (event.type) {
            case "GOL":
                return getRandomPhrase(GOAL_PREFIXES) + " " + desc;

            case "CHUTE":
                return getRandomPhrase(SHOT_PREFIXES) + " " + desc;

            case "ESCANTEIO":
                return getRandomPhrase(CORNER_PREFIXES) + " " + desc;

            case "CARTAO":
                if (desc.toUpperCase().contains("VERMELHO") || desc.toUpperCase().contains("EXPULSO")) {
                    return getRandomPhrase(RED_CARD_PREFIXES) + " " + desc;
                }
                return getRandomPhrase(CARD_PREFIXES) + " " + desc;

            case "TIRO_LIVRE":
                return "🎯 FALTA PERIGOSA! " + desc;

            case "FALTA":
                return getRandomPhrase(FOUL_PREFIXES) + " " + desc;

            case "SUBSTITUICAO":
                return getRandomPhrase(SUBSTITUTION_PREFIXES) + " " + desc;

            case "CONSTRUCAO":
                return getRandomPhrase(BUILDUP_PREFIXES) + " " + desc;

            case "POSSE":
                return getRandomPhrase(POSSESSION_PREFIXES) + " " + desc;

            default:
                return desc;
        }
    }

    private static String getRandomPhrase(String[] phrases) {
        return phrases[random.nextInt(phrases.length)];
    }
}
