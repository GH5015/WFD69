package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubstitutionEngine {

    /**
     * Processa substituições de emergência (lesão) ou táticas (cansaço / risco de expulsão).
     */
    public static MatchEvent checkSubstitutions(Club club, int minute, boolean isHome) {
        List<Player> starters = club.getStartingXI(); //[cite: 20]
        List<Player> bench = club.getSquad().stream() //[cite: 20]
            .filter(p -> !starters.contains(p) && p.canPlay()) //[cite: 25]
            .collect(Collectors.toList());

        if (bench.isEmpty()) return null;

        // 1. Substituição obrigatória por lesão
        Optional<Player> injuredStarter = starters.stream()
            .filter(Player::isInjured) //[cite: 25]
            .findFirst();

        if (injuredStarter.isPresent()) {
            Player outPlayer = injuredStarter.get();
            Player inPlayer = findBestReplacement(bench, outPlayer.getPosition()); //[cite: 25]
            if (inPlayer != null) {
                swapPlayers(starters, bench, outPlayer, inPlayer);
                return new MatchEvent(minute, "SUBSTITUIÇÃO (" + club.getName() + "): Sai " + outPlayer.getName() + " (lesionado) e entra " + inPlayer.getName() + ".", "SUBSTITUICAO", isHome); //[cite: 20, 24, 25]
            }
        }

        // 2. Substituições táticas após o minuto 60
        if (minute >= 60) {
            // Jogador exausto (fadiga < 40) ou pendurado com cartão amarelo
            Optional<Player> tiredOrAtRisk = starters.stream()
                .filter(p -> p.getFatigue() < 40 || p.getYellowCards() >= 1) //[cite: 25]
                .findFirst();

            if (tiredOrAtRisk.isPresent()) {
                Player outPlayer = tiredOrAtRisk.get();
                Player inPlayer = findBestReplacement(bench, outPlayer.getPosition()); //[cite: 25]

                if (inPlayer != null) {
                    swapPlayers(starters, bench, outPlayer, inPlayer);
                    return new MatchEvent(minute, "SUBSTITUIÇÃO TÁTICA (" + club.getName() + "): Sai " + outPlayer.getName() + " para a entrada de " + inPlayer.getName() + ".", "SUBSTITUICAO", isHome); //[cite: 20, 24, 25]
                }
            }
        }

        return null;
    }

    /**
     * Reação da IA ao Placar nos minutos finais (75'+)
     */
    public static void adaptTacticsToScoreline(Club club, int goalDifference, int minute) {
        if (minute < 75) return;

        if (goalDifference < 0) {
            // Perdendo: Aumenta ritmo, mentalidade e pressão total
            club.setMentality("Ofensiva"); //[cite: 20]
            club.setTempo(Math.min(100, club.getTempo() + 15)); //[cite: 20]
            club.setPressure(Math.min(100, club.getPressure() + 15)); //[cite: 20]
        } else if (goalDifference > 0) {
            // Ganhando: Recua o time e desacelera o jogo (Tiki-Taka / Retranca)
            club.setMentality("Defensiva"); //[cite: 20]
            club.setTempo(Math.max(20, club.getTempo() - 15)); //[cite: 20]
            club.setPassing(Math.max(10, club.getPassing() - 15)); //[cite: 20]
        }
    }

    private static Player findBestReplacement(List<Player> bench, String targetPosition) {
        return bench.stream()
            .filter(p -> p.getPosition().equalsIgnoreCase(targetPosition)) //[cite: 25]
            .findFirst()
            .orElse(bench.get(0));
    }

    private static void swapPlayers(List<Player> starters, List<Player> bench, Player outPlayer, Player inPlayer) {
        starters.remove(outPlayer);
        starters.add(inPlayer);
        bench.remove(inPlayer);
    }
}
