package io.github.some_example_name.model;

import java.util.Calendar;
import java.util.Date;

/** Regulamento anual da WFL e suas janelas de movimentação. */
public final class SeasonCalendar {
    public static final int TRADE_DEADLINE_MONTH = Calendar.SEPTEMBER;
    public static final int TRADE_DEADLINE_DAY = 15;

    private SeasonCalendar() {
    }

    public static boolean isTradeWindowOpen(League league, Club club) {
        if (league == null) return true;
        if ("OFFSEASON".equals(league.getCurrentStage())) return true;
        // Negociações só reabrem depois da final, evitando movimentação no
        // meio dos playoffs por clubes já eliminados.
        if ("PLAYOFFS".equals(league.getCurrentStage())) return false;

        Date date = league.getCurrentDate();
        if (date == null) return true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.MONTH) < TRADE_DEADLINE_MONTH) return true;
        if (calendar.get(Calendar.MONTH) > TRADE_DEADLINE_MONTH) return false;
        return calendar.get(Calendar.DAY_OF_MONTH) <= TRADE_DEADLINE_DAY;
    }

    public static boolean isRenewalWindowOpen(League league) {
        if (league == null) return true;
        return !"PLAYOFFS".equals(league.getCurrentStage());
    }

    public static boolean isFreeAgencyOpen(League league) {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage())) return false;
        Date date = league.getCurrentDate();
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        return (month == Calendar.NOVEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 6)
            || month == Calendar.DECEMBER;
    }

    /** Após a principal Free Agency, agentes sem clube seguem contratáveis até os playoffs. */
    public static boolean isFreeAgentSigningOpen(League league) {
        if (league == null) return true;
        if ("PLAYOFFS".equals(league.getCurrentStage())) return false;
        return isFreeAgencyOpen(league) || "REGULAR".equals(league.getCurrentStage());
    }

    /** Novembro reserva uma última janela para o clube renovar os próprios FAs. */
    public static boolean isExclusiveOwnFreeAgentRenewalPeriod(League league) {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage())) return false;
        Date date = league.getCurrentDate();
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == Calendar.NOVEMBER
            && calendar.get(Calendar.DAY_OF_MONTH) <= 5;
    }

    public static boolean isDraftOpen(League league) {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage())) return false;
        Date date = league.getCurrentDate();
        if (date == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER
            && calendar.get(Calendar.DAY_OF_MONTH) >= 20;
    }

    public static boolean isDraftLotteryOpen(League league) {
        if (league == null || !"OFFSEASON".equals(league.getCurrentStage()) || league.getCurrentDate() == null) return false;
        Calendar calendar = Calendar.getInstance(); calendar.setTime(league.getCurrentDate());
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 1;
    }

    /** A classe pode ser acompanhada da metade de janeiro até o Draft. */
    public static boolean isScoutingOpen(League league) {
        if (league == null || league.getCurrentDate() == null) return true;
        if ("OFFSEASON".equals(league.getCurrentStage())) return !league.isDraftFinalized();
        return true;
    }

    public static String getTradeStatus(League league, Club club) {
        if (isTradeWindowOpen(league, club)) {
            return "TROCAS ABERTAS";
        }
        if ("PLAYOFFS".equals(league.getCurrentStage())) return "BLOQUEADO ATÉ O FIM DOS PLAYOFFS";
        return "TRADE DEADLINE ENCERRADA";
    }

    public static String getFreeAgencyStatus(League league) {
        if (isFreeAgencyOpen(league)) return "FREE AGENCY ABERTA";
        if (isExclusiveOwnFreeAgentRenewalPeriod(league)) return "PERÍODO DE RETENÇÃO • 1–5 NOV";
        if (isFreeAgentSigningOpen(league)) return "AGENTES LIVRES DISPONÍVEIS";
        return "MERCADO PRINCIPAL ENCERRADO";
    }
}
