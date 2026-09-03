package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Gera a edição semanal sem IA generativa, apenas com templates e dados da liga. */
public final class NewsGenerator {
    private NewsGenerator() { }

    public static List<NewsEvent> generateWeekly(League league) {
        List<NewsEvent> news = new ArrayList<>();
        if (league == null || league.getCurrentDate() == null) return news;
        Date current = league.getCurrentDate();
        Date start = daysBefore(current, 7);

        int expansionYear = league.getCurrentSeason() + 1;
        List<LeagueExpansionService.Franchise> expansion = LeagueExpansionService.forYear(expansionYear);
        if (!expansion.isEmpty() && league.markExpansionAnnounced(expansionYear)) {
            news.add(new NewsEvent(current, NewsEvent.Category.LIGA,
                "WFL ANUNCIA EXPANSÃO PARA " + expansionYear,
                expansion.get(0).name + " e " + expansion.get(1).name + " estreiam na próxima temporada. "
                    + "A liga terá " + LeagueExpansionService.projectedClubCount(expansionYear)
                    + " clubes. No WFL Expansion, haverá até 15 proteções e no máximo 3 saídas por clube. "
                    + "As novas franquias formarão elencos de 20 jogadores antes da Free Agency e do Draft."));
        }

        List<Match> weeklyMatches = new ArrayList<>();
        for (Match match : league.getSchedule()) {
            if (match.isPlayed() && match.getDate() != null
                && !match.getDate().before(start) && !match.getDate().after(current)) {
                weeklyMatches.add(match);
            }
        }

        Match featured = selectFeaturedMatch(weeklyMatches, weekKey(current));
        if (featured != null) {
            news.add(new NewsEvent(current, NewsEvent.Category.RESULTADO, resultHeadline(featured, weekKey(current)),
                featured.getHomeTeam().getName() + " " + featured.getHomeGoals() + " x "
                    + featured.getAwayGoals() + " " + featured.getAwayTeam().getName() + "."));
        }

        Map<Player, Integer> scorerCount = new HashMap<>();
        for (Match match : weeklyMatches) {
            for (Player scorer : match.getGoalScorers()) {
                scorerCount.put(scorer, scorerCount.getOrDefault(scorer, 0) + 1);
            }
        }
        Map.Entry<Player, Integer> scorer = scorerCount.entrySet().stream()
            .max(Map.Entry.comparingByValue()).orElse(null);
        if (scorer != null && scorer.getValue() >= 2) {
            Player player = scorer.getKey();
            news.add(new NewsEvent(current, NewsEvent.Category.DESTAQUE,
                player.getName().toUpperCase() + " É O NOME DA SEMANA",
                player.getName() + " marcou " + scorer.getValue() + " gols nos últimos jogos"
                    + (player.getCurrentClub() == null ? "." : " pelo " + player.getCurrentClub().getName() + ".")));
        }

        TradeRecord latestTrade = null;
        for (TradeRecord record : league.getTradeHistory()) {
            if (record.getDate() != null && !record.getDate().before(start) && !record.getDate().after(current)) {
                latestTrade = record;
                break;
            }
        }
        if (latestTrade != null) {
            news.add(new NewsEvent(current, NewsEvent.Category.TRADE,
                latestTrade.getFirstClub().getName().toUpperCase() + " E "
                    + latestTrade.getSecondClub().getName().toUpperCase() + " FECHAM TROCA",
                tradeSummary(latestTrade)));
        }

        if (news.size() < 3) {
            for (Club club : league.getClubs()) {
                String headline = club.getName().toUpperCase() + " ENCERRA CICLO DO TREINADOR";
                if (club.getFinalBoardEvaluationSeason() == league.getCurrentSeason()
                    && club.isFinalBoardDismissed() && !alreadyPublished(league, headline)) {
                    news.add(new NewsEvent(current, NewsEvent.Category.DIRETORIA, headline,
                        "A diretoria decidiu mudar o comando após a avaliação final da temporada."));
                    break;
                }
            }
        }

        if (news.size() < 3) {
            NewsEvent pulse = leaguePulse(league, current);
            if (pulse != null) news.add(pulse);
        }

        if (news.size() < 3) {
            List<PlayerStats> scorers = league.getPlayerStats("Gols");
            if (!scorers.isEmpty()) {
                PlayerStats leader = scorers.get(0);
                news.add(new NewsEvent(current, NewsEvent.Category.HISTORIA,
                    leader.player.getName().toUpperCase() + " LIDERA A ARTILHARIA",
                    leader.goals + " gols na temporada colocam o atacante no topo da WFL."));
            }
        }

        if (news.isEmpty()) {
            news.add(new NewsEvent(current, NewsEvent.Category.LIGA,
                "A WFL SE PREPARA PARA UMA NOVA SEMANA",
                stageText(league.getCurrentStage(), league.getCurrentSeason())));
        }
        return new ArrayList<>(news.subList(0, Math.min(3, news.size())));
    }

    public static long weekKey(Date date) {
        if (date == null) return Long.MIN_VALUE;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) * 100L + calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private static Date daysBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    private static Club winningClub(Match match) {
        return match.getHomeGoals() > match.getAwayGoals() ? match.getHomeTeam() : match.getAwayTeam();
    }

    private static Match selectFeaturedMatch(List<Match> matches, long key) {
        if (matches.isEmpty()) return null;
        int mode = Math.floorMod((int) key, 4);
        Comparator<Match> comparator;
        if (mode == 1) {
            comparator = Comparator.comparingInt(NewsGenerator::goalMargin)
                .thenComparingInt(match -> match.getHomeGoals() + match.getAwayGoals());
        } else if (mode == 2) {
            Match draw = matches.stream()
                .filter(match -> match.getHomeGoals() == match.getAwayGoals())
                .max(Comparator.comparingInt(match -> match.getHomeGoals() + match.getAwayGoals()))
                .orElse(null);
            if (draw != null) return draw;
            comparator = Comparator.comparing(Match::getDate);
        } else if (mode == 3) {
            Match cleanSheet = matches.stream()
                .filter(match -> match.getHomeGoals() == 0 ^ match.getAwayGoals() == 0)
                .max(Comparator.comparingInt(NewsGenerator::goalMargin))
                .orElse(null);
            if (cleanSheet != null) return cleanSheet;
            comparator = Comparator.comparingInt(NewsGenerator::goalMargin);
        } else {
            comparator = Comparator.comparingInt(match -> match.getHomeGoals() + match.getAwayGoals());
        }
        return matches.stream().max(comparator).orElse(matches.get(0));
    }

    private static String resultHeadline(Match match, long key) {
        if (match.getHomeGoals() == match.getAwayGoals()) {
            return match.getHomeGoals() + match.getAwayGoals() >= 4
                ? "EMPATE ELETRIZANTE MOVIMENTA A RODADA"
                : "EQUILÍBRIO MARCA DUELO NA WFL";
        }
        Club winner = winningClub(match);
        int margin = goalMargin(match);
        if (match.getHomeGoals() == 0 || match.getAwayGoals() == 0) {
            return winner.getName().toUpperCase() + " VENCE SEM SOFRER GOLS";
        }
        if (margin >= 3) return winner.getName().toUpperCase() + " DOMINA E GOLEIA NA RODADA";
        return Math.floorMod((int) key, 2) == 0
            ? winner.getName().toUpperCase() + " VENCE EM GRANDE JOGO"
            : winner.getName().toUpperCase() + " DECIDE PARTIDA APERTADA";
    }

    private static int goalMargin(Match match) {
        return Math.abs(match.getHomeGoals() - match.getAwayGoals());
    }

    private static NewsEvent leaguePulse(League league, Date current) {
        List<StandingsRow> standings = new ArrayList<>(league.getFullStandings(null));
        standings.removeIf(row -> row.matches == 0);
        if (standings.isEmpty()) return null;
        int mode = Math.floorMod((int) weekKey(current), 4);
        StandingsRow subject;
        if (mode == 1) {
            subject = standings.stream().max(Comparator.comparingInt(row -> row.goalsFor)).orElse(standings.get(0));
            return new NewsEvent(current, NewsEvent.Category.LIGA,
                subject.club.getName().toUpperCase() + " TEM O MELHOR ATAQUE",
                subject.goalsFor + " gols marcados fazem do clube a principal força ofensiva da WFL.");
        }
        if (mode == 2) {
            subject = standings.stream().min(Comparator.comparingInt(row -> row.goalsAgainst)).orElse(standings.get(0));
            return new NewsEvent(current, NewsEvent.Category.LIGA,
                subject.club.getName().toUpperCase() + " FECHA A DEFESA",
                "A equipe sofreu apenas " + subject.goalsAgainst + " gols e possui a defesa mais segura da liga.");
        }
        if (mode == 3) {
            subject = standings.stream().max(Comparator.comparingInt(row -> row.cleanSheets)).orElse(standings.get(0));
            return new NewsEvent(current, NewsEvent.Category.DESTAQUE,
                subject.club.getName().toUpperCase() + " SE DESTACA SEM SOFRER GOLS",
                subject.cleanSheets + " partidas sem ser vazado reforçam a consistência defensiva do clube.");
        }
        subject = standings.get(0);
        return new NewsEvent(current, NewsEvent.Category.LIGA,
            subject.club.getName().toUpperCase() + " COMANDA A CLASSIFICAÇÃO",
            subject.points + " pontos mantêm a franquia na liderança da WFL nesta semana.");
    }

    private static String tradeSummary(TradeRecord trade) {
        String asset = !trade.getSecondClubAssets().isEmpty()
            ? trade.getSecondClubAssets().get(0)
            : !trade.getFirstClubAssets().isEmpty() ? trade.getFirstClubAssets().get(0) : "novos ativos";
        return trade.getFirstClub().getName() + " recebe " + asset + " em negociação com "
            + trade.getSecondClub().getName() + ".";
    }

    private static String stageText(String stage, int season) {
        if ("OFFSEASON".equals(stage)) return "Mercado, scouting e Draft movimentam a Off Season de " + season + ".";
        if ("PLAYOFFS".equals(stage)) return "Os playoffs entram em uma semana decisiva na luta pelo título.";
        return "Clubes ajustam seus elencos para a sequência da temporada " + season + ".";
    }

    private static boolean alreadyPublished(League league, String headline) {
        for (NewsEvent event : league.getNewsHistory()) {
            if (headline.equals(event.getHeadline())) return true;
        }
        return false;
    }
}
