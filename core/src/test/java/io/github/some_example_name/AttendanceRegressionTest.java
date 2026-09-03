package io.github.some_example_name;

import io.github.some_example_name.model.*;

public final class AttendanceRegressionTest {
    public static void main(String[] args) {
        Club home = team("Casa", 90), away = team("Visitante", 80);
        Match regular = new Match(home, away);
        regular.setStage("REGULAR");
        int base = AttendanceService.estimateDemand(null, regular);
        require(base >= 18_000 && base <= 24_000, "Regular demand outside stricter baseline: " + base);
        require(base == AttendanceService.estimateDemand(null, regular), "Preview changed without simulation changes");
        int reference = home.getSuggestedTicketPrice();
        home.setAverageTicketPrice(reference * 2);
        int expensive = AttendanceService.estimateDemand(null, regular);
        require(expensive < base * .4, "High prices should substantially reduce demand");
        home.setAverageTicketPrice(10);
        require(AttendanceService.estimateDemand(null, regular) <= base * 1.09, "Discounts gave excessive attendance");
        int previous = Integer.MAX_VALUE;
        for (int price = 10; price <= 100; price++) {
            home.setAverageTicketPrice(price);
            int demand = AttendanceService.estimateDemand(null, regular);
            require(demand <= previous && demand >= 1_000, "Price response must be monotonic and bounded");
            previous = demand;
        }
        home.setAverageTicketPrice(reference);
        home.recordMatchResult(2, 0);
        int firstWin = AttendanceService.estimateDemand(null, regular);
        require(firstWin > base && firstWin - base < 2_000, "One win should not create an instant attendance boom");
        for (int i = 1; i < 10; i++) home.recordMatchResult(2, 0);
        require(AttendanceService.estimateDemand(null, regular) > firstWin, "Sustained success must attract fans");
        Match finalMatch = new Match(home, away);
        finalMatch.setStage("FINAL");
        AttendanceService.ensureAttendance(null, finalMatch);
        require(finalMatch.getAttendance() == 40_000, "Strong finals should still be capable of selling out");
        home.setAverageTicketPrice(100);
        AttendanceService.ensureAttendance(null, finalMatch);
        require(finalMatch.getAttendance() == 40_000 && finalMatch.getAverageTicketPrice() == reference,
            "Already calculated attendance must stay frozen");
        require(home.getStadiumCapacity() == 40_000, "Rebalance changed stadium capacity");
        Club losing = team("Casa", 90);
        for (int i = 0; i < 10; i++) losing.recordMatchResult(0, 2);
        require(AttendanceService.estimateDemand(null, new Match(losing, away)) < base, "Poor form must reduce attendance");
        require(AttendanceService.estimateDemand(null, null) == 0, "Missing match must be safe");
        System.out.println("Attendance: stricter demand, prices, form, finals, capacity and frozen results OK.");
    }

    private static Club team(String name, int reputation) {
        Club club = new Club(name, "Brasil", "Ocidental", reputation, 40_000_000, "Arena", "santos.png");
        club.setStadiumCapacity(40_000);
        return club;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
