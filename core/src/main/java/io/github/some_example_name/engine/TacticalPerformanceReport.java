package io.github.some_example_name.engine;

import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Avaliação explicável da execução tática de um clube em uma partida concluída. */
public final class TacticalPerformanceReport {
    private final double rating;
    private final int fitScore;
    private final String style;
    private final List<String> strengths;
    private final List<String> weaknesses;

    private TacticalPerformanceReport(
        double rating,
        int fitScore,
        String style,
        List<String> strengths,
        List<String> weaknesses
    ) {
        this.rating = rating;
        this.fitScore = fitScore;
        this.style = style;
        this.strengths = Collections.unmodifiableList(strengths);
        this.weaknesses = Collections.unmodifiableList(weaknesses);
    }

    public static TacticalPerformanceReport analyze(Match match, Club club) {
        TacticalPerformanceReport saved = match.getFinalTacticalReport(club);
        if (saved != null) return saved;
        Club opponent = match.getHomeTeam() == club ? match.getAwayTeam() : match.getHomeTeam();
        boolean home = match.getHomeTeam() == club;
        int goals = home ? match.getHomeGoals() : match.getAwayGoals();
        int opponentGoals = home ? match.getAwayGoals() : match.getHomeGoals();
        int shots = home ? match.getHomeShots() : match.getAwayShots();
        int opponentShots = home ? match.getAwayShots() : match.getHomeShots();
        int possession = home ? match.getHomePossession() : match.getAwayPossession();
        int highRegains = home ? match.getHomeHighRegains() : match.getAwayHighRegains();
        int opponentHighRegains = home ? match.getAwayHighRegains() : match.getHomeHighRegains();
        int transitions = home ? match.getHomeTransitions() : match.getAwayTransitions();
        int opponentTransitions = home ? match.getAwayTransitions() : match.getHomeTransitions();

        float averageTempo = match.getAverageTacticalTempo(club);
        float averageMentality = match.getAverageTacticalMentality(club);
        float averagePassing = match.getAverageTacticalPassing(club);
        float averageWidth = match.getAverageTacticalWidth(club);
        float averagePressure = match.getAverageTacticalPressure(club);
        TacticalModifiers base = TacticalEngine.calculateModifiers(
            averageTempo, averageMentality, averagePassing, averageWidth, averagePressure
        );
        TacticalSuitabilityEvaluator.Profile fit = TacticalSuitabilityEvaluator.evaluate(
            club, club.getStartingXI()
        );
        int fitScore = match.getAverageTacticalFit(club, fit.getBaseOverallFitScore(club));
        double endSustainability = TacticalSuitabilityEvaluator.calculateSustainability(
            fit.getAveragePhysical(), averageTempo, averagePressure, 85
        );
        int intensityDropMinute = match.getIntensityDropMinute(club);
        FormationShapeEvaluator.Shape shape = FormationShapeEvaluator.evaluate(club);
        FormationShapeEvaluator.Shape opponentShape = FormationShapeEvaluator.evaluate(opponent);

        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        if (highRegains > 0) add(strengths, highRegains + " recuperações no campo ofensivo");
        if (possession >= 55) add(strengths, possession + "% de posse de bola");
        if (shots >= opponentShots + 3) add(strengths, "vantagem de " + (shots - opponentShots) + " finalizações");
        if (transitions >= 3 && transitions > opponentTransitions) {
            add(strengths, transitions + " transições ofensivas criadas");
        }
        if (
            averageWidth >= 60f &&
                shape.getCorridors() >= opponentShape.getCorridors() &&
                fit.getWideQuality() >= 70
        ) add(strengths, "superioridade e boa ocupação pelos lados");
        if (fitScore >= 78) add(strengths, "elenco adequado ao plano de jogo (" + fitScore + "%)");
        if (goals > opponentGoals) add(strengths, "plano convertido em vitória");

        if (opponentTransitions >= 3) add(weaknesses, opponentTransitions + " contra-ataques cedidos");
        if (intensityDropMinute > 0 || endSustainability < .86d) {
            int dropMinute = intensityDropMinute > 0
                ? intensityDropMinute : endSustainability < .76d ? 65 : 70;
            add(weaknesses, "intensidade caiu após " + dropMinute + "'");
        }
        if (opponentHighRegains >= 2) {
            add(weaknesses, opponentHighRegains + " perdas sob pressão no campo defensivo");
        }
        if (opponentShots >= shots + 3) add(weaknesses, "adversário finalizou " + (opponentShots - shots) + " vezes a mais");
        if (averagePressure >= 75f && highRegains < 2) {
            add(weaknesses, "pressão alta gerou poucas recuperações perigosas");
        }
        if (averageWidth >= 75f && fit.getNaturalWidePlayers() < 2) {
            add(weaknesses, "amplitude excessiva para os jogadores escalados");
        }
        if (possession <= 42 && averagePassing <= 40f) {
            add(weaknesses, "passe curto não conseguiu controlar a posse");
        }

        if (strengths.isEmpty()) add(strengths, "execução equilibrada sem vantagem dominante");
        if (weaknesses.isEmpty()) add(weaknesses, "nenhum problema estrutural grave identificado");

        double rating = 6d;
        rating += clamp((goals - opponentGoals) * .55d, -1.10d, 1.10d);
        rating += clamp((shots - opponentShots) * .08d, -.65d, .65d);
        rating += clamp((possession - 50d) * .018d, -.45d, .45d);
        rating += clamp(highRegains * .07d, 0d, .60d);
        rating += clamp((transitions - opponentTransitions) * .05d, -.45d, .45d);
        rating += clamp((fitScore - 70d) * .025d, -.60d, .60d);
        if (endSustainability < .86d) rating -= (.86d - endSustainability) * 2.5d;
        rating = Math.round(clamp(rating, 2.5d, 10d) * 10d) / 10d;

        return new TacticalPerformanceReport(
            rating, fitScore, friendlyStyle(base.detectedStyle), strengths, weaknesses
        );
    }

    private static String friendlyStyle(String style) {
        if (style == null) return "Equilibrado";
        if (style.startsWith("Gegenpressing")) return "Pressão ofensiva";
        if (style.startsWith("Posse")) return "Controle com posse";
        if (style.startsWith("Retranca")) return "Bloco baixo e contra-ataque";
        if (style.startsWith("Ataque Total")) return "Ataque amplo e agressivo";
        if (style.startsWith("Jogo Apoiado")) return "Construção central apoiada";
        if (style.startsWith("Transições")) return "Transições rápidas";
        return style;
    }

    private static void add(List<String> list, String text) {
        if (!list.contains(text) && list.size() < 4) list.add(text);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public double getRating() { return rating; }
    public int getFitScore() { return fitScore; }
    public String getStyle() { return style; }
    public List<String> getStrengths() { return strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public int getStars() { return Math.max(1, Math.min(5, (int) Math.round(rating / 2d))); }
    public String getStarsText() {
        StringBuilder text = new StringBuilder();
        for (int i = 1; i <= 5; i++) text.append(i <= getStars() ? '★' : '☆');
        return text.toString();
    }
}
