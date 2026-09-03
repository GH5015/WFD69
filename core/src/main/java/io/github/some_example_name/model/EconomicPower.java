package io.github.some_example_name.model;

import com.badlogic.gdx.graphics.Color;

public class EconomicPower {

    /**
     * Calcula o valor estimado de mercado do clube (Valuation).
     */
    public static long calculateClubValuation(Club club) {
        ClubFinance fin = club.getFinance();

        // Ativo 1: Valor total dos jogadores do elenco
        long squadValuation = club.getSquad().stream()
            .mapToLong(p -> (long) (Math.pow(p.getOverall(), 3.1) * 350))
            .sum();

        // Ativo 2: Receita Anual projetada x Multiplicador de mercado
        long revenueValuation = (long) (fin.getTotalAnnualRevenue() * 2.5);

        // Ativo 3: Patrimônio e Estádio
        long stadiumValuation = (long) (club.getStadiumCapacity() * 4500L);

        // Ativo 4: Caixa Líquido
        long cashValuation = Math.max(0, fin.getBalance());

        return squadValuation + revenueValuation + stadiumValuation + cashValuation;
    }

    /**
     * Retorna a quantidade de estrelas de Poder Econômico (1 a 5).
     */
    public static int getStarRating(Club club) {
        long valuation = calculateClubValuation(club);

        if (valuation >= 800_000_000L) return 5;
        if (valuation >= 550_000_000L) return 4;
        if (valuation >= 350_000_000L) return 3;
        if (valuation >= 200_000_000L) return 2;
        return 1;
    }

    public static float getDisplayStarRating(Club club) {
        long valuation = calculateClubValuation(club);
        if (valuation >= 800_000_000L) return 5f;
        if (valuation >= 675_000_000L) return 4.5f;
        if (valuation >= 550_000_000L) return 4f;
        if (valuation >= 450_000_000L) return 3.5f;
        if (valuation >= 350_000_000L) return 3f;
        if (valuation >= 275_000_000L) return 2.5f;
        if (valuation >= 200_000_000L) return 2f;
        if (valuation >= 125_000_000L) return 1.5f;
        return 1f;
    }

    /**
     * Retorna a string visual formatada em estrelas (fallback para logs/debug).
     */
    public static String getFormattedStars(Club club) {
        float rating = getDisplayStarRating(club);
        StringBuilder sb = new StringBuilder();
        int full = (int) rating;
        for (int index = 0; index < full; index++) sb.append('★');
        if (rating - full >= .5f) sb.append('½');
        for (int index = (int) Math.ceil(rating); index < 5; index++) sb.append('☆');
        return sb.toString();
    }
}
