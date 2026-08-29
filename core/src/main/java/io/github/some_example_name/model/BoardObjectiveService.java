package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Define e avalia as expectativas próprias das vinte diretorias da WFL. */
public final class BoardObjectiveService {
    public static final class ObjectiveProgress {
        private final BoardObjective objective;
        private final double percentage;
        private final String detail;
        ObjectiveProgress(BoardObjective objective, double percentage, String detail) {
            this.objective = objective;
            this.percentage = clamp(percentage);
            this.detail = detail;
        }
        public BoardObjective getObjective() { return objective; }
        public double getPercentage() { return percentage; }
        public String getDetail() { return detail; }
        public String getState() {
            if (detail != null && detail.startsWith("Aguardando")) return "AGUARDANDO";
            if (percentage >= 99.5d) return "CUMPRIDO";
            if (percentage >= 45d) return "DENTRO DA EXPECTATIVA";
            if (percentage >= 25d) return "ABAIXO DA EXPECTATIVA";
            return "EM RISCO";
        }
    }

    public static final class Evaluation {
        private final List<ObjectiveProgress> objectives;
        private final int confidence;
        Evaluation(List<ObjectiveProgress> objectives, int confidence) {
            this.objectives = objectives;
            this.confidence = Math.max(0, Math.min(100, confidence));
        }
        public List<ObjectiveProgress> getObjectives() { return objectives; }
        public int getConfidence() { return confidence; }
        public String getStatus() {
            if (confidence >= 80) return "EXCELENTE";
            if (confidence >= 65) return "ALTA";
            if (confidence >= 45) return "ESTÁVEL";
            if (confidence >= 25) return "SOB PRESSÃO";
            return "CRÍTICA";
        }
    }

    private BoardObjectiveService() { }

    public static Evaluation evaluate(League league, Club club) {
        List<ObjectiveProgress> progress = new ArrayList<>();
        double points = 0d;
        int totalWeight = 0;
        for (BoardObjective objective : objectivesFor(club)) {
            ObjectiveProgress result = evaluateObjective(league, club, objective);
            progress.add(result);
            int weight = objective.getPriority().getWeight();
            points += result.getPercentage() * weight;
            totalWeight += weight;
        }
        int confidence = totalWeight == 0 ? 50 : (int) Math.round(points / totalWeight);
        return new Evaluation(progress, confidence);
    }

    public static List<BoardObjective> objectivesFor(Club club) {
        String name = club != null ? club.getName() : "";
        switch (name) {
            case "Santos Atlântico": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Fazer 1 jovem de até 22 anos evoluir +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 1),
                o("Manter a folha dentro do Salary Cap", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Terminar entre os 8 melhores ataques", C.SPORTING, P.SECONDARY, T.ATTACK_RANK, 8));
            case "Rio Imperial": return list(
                o("Disputar a Final da WFL", C.SPORTING, P.CRITICAL, T.REACH_FINAL, 1),
                o("Terminar a temporada regular no Top 4", C.SPORTING, P.IMPORTANT, T.TABLE_POSITION, 4),
                o("Fazer 1 jovem de até 22 anos evoluir +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 1),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Milano Calcio": return list(
                o("Disputar a Final da WFL", C.SPORTING, P.CRITICAL, T.REACH_FINAL, 1),
                o("Ter uma defesa Top 3", C.SPORTING, P.IMPORTANT, T.DEFENSE_RANK, 3),
                o("Manter a folha dentro do cap", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Manter idade média abaixo de 29 anos", C.SQUAD, P.SECONDARY, T.AVERAGE_AGE, 29));
            case "Bavaria München": return list(
                o("Ser campeão da WFL", C.SPORTING, P.CRITICAL, T.CHAMPION, 1),
                o("Terminar a temporada regular no Top 4", C.SPORTING, P.IMPORTANT, T.TABLE_POSITION, 4),
                o("Ter um ataque Top 5", C.SPORTING, P.IMPORTANT, T.ATTACK_RANK, 5),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Manchester Albion": return list(
                o("Disputar a Final da WFL", C.SPORTING, P.CRITICAL, T.REACH_FINAL, 1),
                o("Terminar a temporada regular no Top 4", C.SPORTING, P.IMPORTANT, T.TABLE_POSITION, 4),
                o("Superar 55% de aproveitamento", C.SPORTING, P.IMPORTANT, T.WIN_RATE, 55),
                o("Manter a folha dentro do cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "London Royals": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Terminar entre os 8 melhores ataques", C.SPORTING, P.IMPORTANT, T.ATTACK_RANK, 8),
                o("Dar minutos a 1 jovem", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG, 1),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Amsterdã Total": return list(
                o("Ser campeão da WFL", C.SPORTING, P.CRITICAL, T.CHAMPION, 1),
                o("Terminar a temporada regular no Top 4", C.SPORTING, P.IMPORTANT, T.TABLE_POSITION, 4),
                o("Fazer 2 jovens de até 22 anos evoluírem +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 2),
                o("Ter um ataque Top 5", C.SPORTING, P.SECONDARY, T.ATTACK_RANK, 5));
            case "Madrid Castilla": return list(
                o("Disputar a Final da WFL", C.SPORTING, P.CRITICAL, T.REACH_FINAL, 1),
                o("Terminar a temporada regular no Top 4", C.SPORTING, P.IMPORTANT, T.TABLE_POSITION, 4),
                o("Manter overall de elenco 84+", C.SQUAD, P.IMPORTANT, T.TEAM_OVERALL, 84),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Barcelona Mediterrâneo": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Fazer 2 jovens de até 22 anos evoluírem +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 2),
                o("Ter um ataque Top 5", C.SPORTING, P.IMPORTANT, T.ATTACK_RANK, 5),
                o("Reduzir a idade média para 27 anos", C.SQUAD, P.SECONDARY, T.AVERAGE_AGE, 27));
            case "Budapest Danube": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Alcançar 45% de aproveitamento", C.SPORTING, P.IMPORTANT, T.WIN_RATE, 45),
                o("Fazer 1 jovem de até 22 anos evoluir +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 1),
                o("Manter a folha dentro do cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Lisboa Atlântica": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Dar 800 minutos a jogadores dos 2 últimos Drafts", C.DEVELOPMENT, P.IMPORTANT, T.DRAFTED_MINUTES, 800),
                o("Preservar uma escolha de 1ª rodada", C.DRAFT, P.IMPORTANT, T.FIRST_ROUND_PICKS, 1),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Buenos Aires Plata": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Ter um ataque Top 6", C.SPORTING, P.IMPORTANT, T.ATTACK_RANK, 6),
                o("Superar 50% de aproveitamento", C.SPORTING, P.IMPORTANT, T.WIN_RATE, 50),
                o("Manter a folha dentro do cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Montevideo Oriental": return list(
                o("Brigar por uma vaga nos playoffs", C.SPORTING, P.CRITICAL, T.TABLE_POSITION, 10),
                o("Ter uma defesa Top 8", C.SPORTING, P.IMPORTANT, T.DEFENSE_RANK, 8),
                o("Manter a folha dentro do cap", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Manter idade média abaixo de 28 anos", C.SQUAD, P.SECONDARY, T.AVERAGE_AGE, 28));
            case "Paris Lumière": return list(
                o("Classificar para os playoffs", C.SPORTING, P.CRITICAL, T.MAKE_PLAYOFFS, 8),
                o("Ter um ataque Top 6", C.SPORTING, P.IMPORTANT, T.ATTACK_RANK, 6),
                o("Manter overall de elenco 82+", C.SQUAD, P.IMPORTANT, T.TEAM_OVERALL, 82),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.SECONDARY, T.SALARY_CAP, 100));
            case "Belfast Northern Stars": return list(
                o("Brigar por uma vaga nos playoffs", C.SPORTING, P.CRITICAL, T.TABLE_POSITION, 10),
                o("Manter pelo menos 23 jogadores", C.SQUAD, P.IMPORTANT, T.ROSTER_SIZE, 23),
                o("Respeitar o Salary Cap", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Dar 600 minutos a jogadores dos 2 últimos Drafts", C.DEVELOPMENT, P.SECONDARY, T.DRAFTED_MINUTES, 600));
            case "Tokyo Rising Sun": return list(
                o("Fazer 2 jovens de até 22 anos evoluírem +2 OVR", C.DEVELOPMENT, P.CRITICAL, T.DEVELOP_YOUNG_OVR, 2),
                o("Preservar uma escolha de 1ª rodada", C.DRAFT, P.IMPORTANT, T.FIRST_ROUND_PICKS, 1),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Adicionar um prospecto com potencial 85+", C.DRAFT, P.SECONDARY, T.HIGH_POTENTIAL_PROSPECT, 85));
            case "Seoul Tigers": return list(
                o("Fazer 2 jovens de até 22 anos evoluírem +2 OVR", C.DEVELOPMENT, P.CRITICAL, T.DEVELOP_YOUNG_OVR, 2),
                o("Alcançar 45% de aproveitamento", C.SPORTING, P.IMPORTANT, T.WIN_RATE, 45),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Brigar por uma vaga nos playoffs", C.SPORTING, P.SECONDARY, T.TABLE_POSITION, 10));
            case "Tehran Lions": return list(
                o("Dar 1.000 minutos a jogadores dos 2 últimos Drafts", C.DEVELOPMENT, P.CRITICAL, T.DRAFTED_MINUTES, 1000),
                o("Preservar uma escolha de 1ª rodada", C.DRAFT, P.IMPORTANT, T.FIRST_ROUND_PICKS, 1),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Adicionar um prospecto com potencial 85+", C.DRAFT, P.SECONDARY, T.HIGH_POTENTIAL_PROSPECT, 85));
            case "Baghdad Mesopotamia": return list(
                o("Fazer 2 jovens de até 22 anos evoluírem +2 OVR", C.DEVELOPMENT, P.CRITICAL, T.DEVELOP_YOUNG_OVR, 2),
                o("Preservar uma escolha de 1ª rodada", C.DRAFT, P.IMPORTANT, T.FIRST_ROUND_PICKS, 1),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Iniciar uma reforma no estádio", C.LONG_TERM, P.SECONDARY, T.STADIUM_PROJECT, 1));
            case "Tel Aviv Stars": return list(
                o("Brigar por uma vaga nos playoffs", C.SPORTING, P.CRITICAL, T.TABLE_POSITION, 10),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Fazer 1 jovem de até 22 anos evoluir +2 OVR", C.DEVELOPMENT, P.IMPORTANT, T.DEVELOP_YOUNG_OVR, 1),
                o("Construir uma defesa Top 10", C.SPORTING, P.SECONDARY, T.DEFENSE_RANK, 10));
            default: return list(
                o("Brigar por uma vaga nos playoffs", C.SPORTING, P.CRITICAL, T.TABLE_POSITION, 10),
                o("Manter o Salary Cap saudável", C.FINANCIAL, P.IMPORTANT, T.SALARY_CAP, 100),
                o("Desenvolver um jogador jovem", C.DEVELOPMENT, P.SECONDARY, T.DEVELOP_YOUNG, 1));
        }
    }

    private static ObjectiveProgress evaluateObjective(League league, Club club, BoardObjective objective) {
        StandingsRow row = standingsRow(league, club);
        int position = position(league, club);
        int matches = row != null ? row.matches : 0;
        double target = objective.getTarget();
        switch (objective.getType()) {
            case MAKE_PLAYOFFS:
                return result(objective, matches == 0 ? 50 : playoffProgress(position),
                    matches == 0 ? "Aguardando início da temporada" : position + "º lugar");
            case TABLE_POSITION:
                return result(objective, matches == 0 ? 50 : rankProgress(position, (int) target),
                    matches == 0 ? "Aguardando início da temporada" : position + "º lugar");
            case WIN_RATE: {
                double rate = matches == 0 ? 0 : (row.wins * 100d + row.draws * 50d) / matches;
                return result(objective, matches == 0 ? 50 : rate * 100d / target,
                    matches == 0 ? "Aguardando início da temporada"
                        : String.format(Locale.US, "%.0f%% de aproveitamento", rate));
            }
            case ATTACK_RANK: {
                int rank = metricRank(league, club, true);
                return result(objective, matches == 0 ? 50 : rankProgress(rank, (int) target),
                    matches == 0 ? "Aguardando início da temporada" : rank + "º ataque");
            }
            case DEFENSE_RANK: {
                int rank = metricRank(league, club, false);
                return result(objective, matches == 0 ? 50 : rankProgress(rank, (int) target),
                    matches == 0 ? "Aguardando início da temporada" : rank + "ª defesa");
            }
            case DEVELOP_YOUNG: {
                int requiredAppearances = Math.max(1, (int) Math.ceil(matches * .25d));
                int developed = 0;
                for (Player player : club.getSquad())
                    if (player.getAge() <= 23 && player.getSeasonAppearances() >= requiredAppearances) developed++;
                double pct = matches == 0 ? 50 : developed * 100d / target;
                return result(objective, pct, developed + "/" + (int) target + " jovens com minutos");
            }
            case DEVELOP_YOUNG_OVR: {
                int completed = 0;
                List<Double> individualProgress = new ArrayList<>();
                for (Player player : club.getSquad()) {
                    if (player.getAge() > 22) continue;
                    int growth = Math.max(0, player.getSeasonOverallGrowth());
                    if (growth >= 2) completed++;
                    individualProgress.add(Math.min(1d, growth / 2d));
                }
                individualProgress.sort(Comparator.reverseOrder());
                double progressUnits = 0d;
                for (int i = 0; i < Math.min((int) target, individualProgress.size()); i++) {
                    progressUnits += individualProgress.get(i);
                }
                double pct = matches == 0 ? 50 : progressUnits * 100d / target;
                String detail = matches == 0
                    ? "Aguardando início da temporada"
                    : Math.min(completed, (int) target) + "/" + (int) target + " jovens com +2 OVR";
                return result(objective, pct, detail);
            }
            case DRAFTED_MINUTES: {
                int minutes = 0;
                for (Player player : club.getSquad()) {
                    if (player.wasDraftedWithin(league.getCurrentSeason(), 2)) {
                        minutes += player.getSeasonMinutes();
                    }
                }
                double pct = matches == 0 ? 50 : minutes * 100d / target;
                String detail = matches == 0
                    ? "Aguardando início da temporada"
                    : minutes + " / " + (int) target + " minutos";
                return result(objective, pct, detail);
            }
            case HIGH_POTENTIAL_PROSPECT: {
                Player best = null;
                for (Player player : club.getSquad()) {
                    if (!player.wasDraftedWithin(league.getCurrentSeason(), 2)) continue;
                    if (best == null || player.getTruePotential() > best.getTruePotential()) best = player;
                }
                int potential = best != null ? best.getTruePotential() : 0;
                double pct = potential <= 0 ? 0 : potential * 100d / target;
                String detail = best == null
                    ? "Aguardando Draft"
                    : best.getName() + " • POT " + potential;
                return result(objective, pct, detail);
            }
            case SALARY_CAP: {
                long cap = club.getFinance().getSalaryCap();
                long payroll = club.getFinance().getAnnualPayroll();
                double usage = cap <= 0 ? 2d : payroll / (double) cap;
                double pct = usage <= 1d ? 100 : usage <= 1.05 ? 70 : usage <= 1.10 ? 40 : usage <= 1.15 ? 20 : 0;
                return result(objective, pct, money(payroll) + " / " + money(cap)
                    + (usage <= 1d ? " • CUMPRIDO" : " • EM RISCO"));
            }
            case FIRST_ROUND_PICKS: {
                int count = 0;
                for (DraftPick pick : club.getDraftPicks())
                    if (pick.getRound() == 1 && pick.getYear() >= league.getCurrentSeason() + 1
                        && pick.getCurrentOwner() == club) count++;
                return result(objective, count * 100d / target, count + " escolha(s) de 1ª rodada");
            }
            case AVERAGE_AGE: {
                double average = club.getSquad().stream().mapToInt(Player::getAge).average().orElse(0d);
                double over = average - target;
                double pct = over <= 0 ? 100 : Math.max(0, 100 - over * 25d);
                return result(objective, pct, String.format(Locale.US, "%.1f anos", average));
            }
            case ROSTER_SIZE:
                return result(objective, club.getSquad().size() * 100d / target,
                    club.getSquad().size() + "/" + (int) target + " jogadores");
            case TEAM_OVERALL:
                return result(objective, club.getOverall() * 100d / target,
                    "Overall " + Math.round(club.getOverall()) + " / " + (int) target);
            case STADIUM_PROJECT: {
                double pct = club.getStadiumCapacity() > 30_000 ? 100 : club.isStadiumRenovationInProgress() ? 70 : 0;
                return result(objective, pct, club.isStadiumRenovationInProgress()
                    ? club.getStadiumRenovationDaysRemaining() + " dias restantes" : "Nenhuma obra iniciada");
            }
            case REACH_FINAL:
                return result(objective, playoffRoundProgress(league, club, false, position, matches),
                    matches == 0 ? "Aguardando início da temporada" : playoffDetail(league, club));
            case CHAMPION:
                return result(objective, playoffRoundProgress(league, club, true, position, matches),
                    matches == 0 ? "Aguardando início da temporada" : playoffDetail(league, club));
            default:
                return result(objective, 50, "Em avaliação");
        }
    }

    private static double playoffRoundProgress(League league, Club club, boolean champion,
                                                int position, int matches) {
        if (matches == 0) return 50;
        double best = rankProgress(position, 4) * .65d;
        for (PlayoffSeries series : league.getPlayoffSeries()) {
            if (series.getFirstSeed() != club && series.getSecondSeed() != club) continue;
            if ("FINAL".equals(series.getRound())) {
                if (!champion) return 100;
                return series.getWinner() == club ? 100 : series.isComplete() ? 65 : 85;
            }
            if ("SEMIFINAIS".equals(series.getRound())) best = Math.max(best, champion ? 55 : 75);
            else best = Math.max(best, champion ? 35 : 55);
        }
        return best;
    }

    private static String playoffDetail(League league, Club club) {
        for (PlayoffSeries series : league.getPlayoffSeries()) {
            if (series.getFirstSeed() == club || series.getSecondSeed() == club) {
                if ("FINAL".equals(series.getRound()) && series.getWinner() == club) return "Campeão da WFL";
                return "Fase alcançada: " + series.getRound();
            }
        }
        return "Em projeção pela campanha";
    }

    private static StandingsRow standingsRow(League league, Club club) {
        for (StandingsRow row : league.getFullStandings(null)) if (row.club == club) return row;
        return null;
    }

    private static int position(League league, Club club) {
        List<StandingsRow> rows = league.getFullStandings(null);
        for (int i = 0; i < rows.size(); i++) if (rows.get(i).club == club) return i + 1;
        return rows.size();
    }

    private static int metricRank(League league, Club club, boolean attack) {
        List<StandingsRow> rows = new ArrayList<>(league.getFullStandings(null));
        if (attack) rows.sort(Comparator.comparingInt((StandingsRow r) -> r.goalsFor).reversed());
        else rows.sort(Comparator.comparingInt((StandingsRow r) -> r.goalsAgainst));
        for (int i = 0; i < rows.size(); i++) if (rows.get(i).club == club) return i + 1;
        return rows.size();
    }

    private static double playoffProgress(int position) {
        if (position <= 8) return 100;
        if (position == 9) return 75;
        if (position == 10) return 60;
        if (position == 11) return 40;
        if (position == 12) return 20;
        return 0;
    }

    private static double rankProgress(int position, int target) {
        if (position <= target) return 100;
        int difference = position - target;
        if (difference == 1) return 75;
        if (difference == 2) return 60;
        if (difference == 3) return 40;
        if (difference == 4) return 20;
        return 0;
    }

    private static ObjectiveProgress result(BoardObjective objective, double pct, String detail) {
        return new ObjectiveProgress(objective, pct, detail);
    }
    private static BoardObjective o(String title, C category, P priority, T type, double target) {
        return new BoardObjective(title, category.value, priority.value, type.value, target);
    }
    private static List<BoardObjective> list(BoardObjective... objectives) { return Arrays.asList(objectives); }
    private enum C {
        SPORTING(BoardObjective.Category.SPORTING), SQUAD(BoardObjective.Category.SQUAD),
        DEVELOPMENT(BoardObjective.Category.DEVELOPMENT), FINANCIAL(BoardObjective.Category.FINANCIAL),
        DRAFT(BoardObjective.Category.DRAFT), LONG_TERM(BoardObjective.Category.LONG_TERM);
        final BoardObjective.Category value; C(BoardObjective.Category value) { this.value = value; }
    }
    private enum P {
        CRITICAL(BoardObjective.Priority.CRITICAL), IMPORTANT(BoardObjective.Priority.IMPORTANT),
        SECONDARY(BoardObjective.Priority.SECONDARY);
        final BoardObjective.Priority value; P(BoardObjective.Priority value) { this.value = value; }
    }
    private enum T {
        CHAMPION(BoardObjective.Type.CHAMPION), REACH_FINAL(BoardObjective.Type.REACH_FINAL),
        MAKE_PLAYOFFS(BoardObjective.Type.MAKE_PLAYOFFS), TABLE_POSITION(BoardObjective.Type.TABLE_POSITION),
        WIN_RATE(BoardObjective.Type.WIN_RATE), ATTACK_RANK(BoardObjective.Type.ATTACK_RANK),
        DEFENSE_RANK(BoardObjective.Type.DEFENSE_RANK), DEVELOP_YOUNG(BoardObjective.Type.DEVELOP_YOUNG),
        DEVELOP_YOUNG_OVR(BoardObjective.Type.DEVELOP_YOUNG_OVR),
        DRAFTED_MINUTES(BoardObjective.Type.DRAFTED_MINUTES),
        HIGH_POTENTIAL_PROSPECT(BoardObjective.Type.HIGH_POTENTIAL_PROSPECT),
        SALARY_CAP(BoardObjective.Type.SALARY_CAP), FIRST_ROUND_PICKS(BoardObjective.Type.FIRST_ROUND_PICKS),
        AVERAGE_AGE(BoardObjective.Type.AVERAGE_AGE), ROSTER_SIZE(BoardObjective.Type.ROSTER_SIZE),
        TEAM_OVERALL(BoardObjective.Type.TEAM_OVERALL), STADIUM_PROJECT(BoardObjective.Type.STADIUM_PROJECT);
        final BoardObjective.Type value; T(BoardObjective.Type value) { this.value = value; }
    }
    private static double clamp(double value) { return Math.max(0d, Math.min(100d, value)); }
    private static String money(long value) {
        return value >= 1_000_000L ? String.format(Locale.US, "WFL$ %.1fM", value / 1_000_000d)
            : String.format(Locale.US, "WFL$ %.0fK", value / 1_000d);
    }
}
