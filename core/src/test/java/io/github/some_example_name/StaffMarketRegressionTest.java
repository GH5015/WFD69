package io.github.some_example_name;

import io.github.some_example_name.database.StaffDatabase;
import io.github.some_example_name.model.*;
import java.util.*;

public final class StaffMarketRegressionTest {
    public static void main(String[] args) {
        testSalaryScale();
        League league = new League("WFL", 1990);
        for (int i = 0; i < 30; i++) league.addClub(new Club("Employer " + i, "Brasil", "Ocidental", 80,
            40_000_000, "Arena", "santos.png"));
        Club user = league.getClubs().get(0);
        require(StaffDatabase.getMarketSize() == 260, "Market expansion missing");
        // Deliberately occupy 30 distinct candidates in every role, rather than relying on random replacements.
        Set<String> employed = new HashSet<>();
        for (Club club : league.getClubs()) for (StaffRole role : StaffRole.values()) {
            StaffMember hire = StaffDatabase.getOffseasonCandidates(role, 1990, league.getClubs(), club, 1).get(0);
            club.hireStaff(hire);
            require(employed.add(hire.getName()), "Duplicate employed identity");
        }
        for (int year : new int[]{1969, 1990, 2020}) for (StaffRole role : StaffRole.values()) {
            List<StaffMember> available = StaffDatabase.getOffseasonCandidates(role, year, league.getClubs(), user, 15);
            require(available.size() == 15, "Too few free candidates: " + role);
            Set<String> unique = new HashSet<>();
            for (StaffMember member : available) {
                require(unique.add(member.getName()) && !employed.contains(member.getName()), "Unavailable/duplicate candidate");
                require(member.getRole() == role, "Wrong role");
                require(member.getAnnualSalary() == StaffSalaryScale.annualSalary(role, member.getQuality()), "Market salary does not follow quality");
            }
            require(available.get(0).getName().equals(StaffDatabase.getOffseasonCandidates(role, year, league.getClubs(), user, 15).get(0).getName()), "Unstable refresh");
        }
        StaffMember candidate = StaffDatabase.getOffseasonCandidates(StaffRole.COACH, 1990, league.getClubs(), user, 15).get(0);
        StaffMember original = user.getStaffMember(StaffRole.COACH);
        long payroll = user.getFinance().getAnnualStaffPayroll();
        long balance = user.getFinance().getBalance();
        require(!StaffNegotiationService.submit(league, user, candidate, 2_000_000, 3).accepted, "In-season signing allowed");
        league.setCurrentStage("OFFSEASON");
        StaffNegotiationService.Result low = StaffNegotiationService.submit(league, user, candidate, 10_000, 4);
        require(!low.accepted && low.counterSalary > 10_000, "Missing counteroffer");
        require(user.getStaffMember(StaffRole.COACH) == original, "Rejected offer changed staff");
        require(!StaffNegotiationService.submit(league, user, candidate, -1, 3).accepted, "Negative salary accepted");
        require(!StaffNegotiationService.submit(league, user, candidate, Long.MAX_VALUE, 3).accepted, "Overflow salary accepted");
        require(!StaffNegotiationService.submit(league, user, candidate, 2_000_000, 6).accepted, "Invalid duration accepted");
        require(StaffNegotiationService.requestedSalary(candidate, 1990, 5) <= StaffNegotiationService.requestedSalary(candidate, 1990, 1), "Duration does not affect negotiation");
        require(StaffNegotiationService.submit(league, user, candidate, low.counterSalary, 4).accepted, "Counteroffer refused");
        StaffMember signed = user.getStaffMember(StaffRole.COACH);
        require(signed.getAnnualSalary() == low.counterSalary && signed.getContractEndYear() == 1994, "Negotiated terms lost");
        require(signed.getQuality() == candidate.getQuality() && signed.getSpecialty().equals(candidate.getSpecialty()), "Identity lost");
        long expected = payroll - original.getAnnualSalary() + low.counterSalary;
        require(user.getFinance().getAnnualStaffPayroll() == expected && user.getFinance().getStaffExpense() == expected / 12, "Real staff expenses incorrect");
        require(user.getFinance().getBalance() == balance, "Salary unexpectedly charged upfront");
        require(!StaffNegotiationService.submit(league, league.getClubs().get(1), candidate, 3_000_000, 3).accepted, "Stale candidate hired twice");
        require(StaffDatabase.getOffseasonCandidates(StaffRole.COACH, 1990, league.getClubs(), user, 100).stream().noneMatch(s -> s.getName().equals(signed.getName())), "Hired professional still on market");
        System.out.println("Staff market: 260 profiles, 15 available per role after 150 hires, negotiation/counteroffers, validation and actual expenses OK.");
    }
    private static void testSalaryScale() {
        for (StaffRole role : StaffRole.values()) {
            long previous = 0;
            for (int quality = 50; quality <= 100; quality++) {
                long salary = StaffSalaryScale.annualSalary(role, quality);
                require(salary >= previous && salary > 0 && salary % 10_000 == 0, "Invalid/non-monotonic salary curve");
                previous = salary;
            }
            require(StaffSalaryScale.annualSalary(role, 95) > StaffSalaryScale.annualSalary(role, 64) * 4, "Quality spread still too small");
            require(StaffSalaryScale.annualSalary(role, 100) - StaffSalaryScale.annualSalary(role, 95)
                > StaffSalaryScale.annualSalary(role, 60) - StaffSalaryScale.annualSalary(role, 55), "No elite premium");
            require(StaffSalaryScale.annualSalary(role, -1) == StaffSalaryScale.annualSalary(role, 50), "Lower quality boundary");
            require(StaffSalaryScale.annualSalary(role, 101) == StaffSalaryScale.annualSalary(role, 100), "Upper quality boundary");
            StaffMember interim = StaffDatabase.getAutomaticReplacement(role, "Santos Atlântico", 1990);
            require(interim.getAnnualSalary() == StaffSalaryScale.annualSalary(role, interim.getQuality()), "Replacement salary mismatch");
        }
        for (Club club : Arrays.asList(new Club(), new Club("Santos Atlântico", "Brasil", "Ocidental", 80,
            40_000_000, "Arena", "santos.png"))) {
            for (StaffRole role : StaffRole.values()) {
                StaffMember member = club.getStaffMember(role);
                require(member.getAnnualSalary() == StaffSalaryScale.annualSalary(role, member.getQuality()), "Initial salary mismatch");
            }
        }
        Club contracted = new Club();
        contracted.hireStaff(new StaffMember(StaffRole.COACH, "Contrato negociado", 95, 1_870_000, 1995));
        contracted.setStaffLevel(StaffRole.COACH, 4);
        require(contracted.getStaffMember(StaffRole.COACH).getAnnualSalary() == 1_870_000, "Signed salary changed with quality");
        System.out.println("Staff salary curve: quality, elite premium, role, initial staff, market and signed contract preservation OK.");
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
