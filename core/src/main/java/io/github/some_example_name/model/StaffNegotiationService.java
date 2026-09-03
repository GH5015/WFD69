package io.github.some_example_name.model;

/** Salary/duration negotiations for staff. Staff payroll is an operating expense, not player cap. */
public final class StaffNegotiationService {
    public static final int MIN_YEARS = 1;
    public static final int MAX_YEARS = 5;
    private StaffNegotiationService() { }

    public static int preferredYears(StaffMember candidate, int year) {
        return Math.max(MIN_YEARS, Math.min(MAX_YEARS, candidate.getContractEndYear() - year));
    }

    /** Longer security can earn a small discount; shorter commitments cost more. */
    public static long requestedSalary(StaffMember candidate, int year, int years) {
        if (years < MIN_YEARS || years > MAX_YEARS) throw new IllegalArgumentException("Escolha de 1 a 5 anos.");
        int difference = years - preferredYears(candidate, year);
        double multiplier = difference >= 0 ? 1d - Math.min(.08d, difference * .025d) : 1d - difference * .05d;
        return Math.max(10_000L, (long) Math.ceil(candidate.getAnnualSalary() * multiplier / 10_000d) * 10_000L);
    }

    public static Result submit(League league, Club club, StaffMember candidate, long salary, int years) {
        if (league == null || club == null || candidate == null || !league.getClubs().contains(club))
            return new Result(false, 0, "Profissional ou clube inválido.");
        if (!"OFFSEASON".equals(league.getCurrentStage()))
            return new Result(false, 0, "Negociações disponíveis somente na off-season.");
        if (salary <= 0 || salary > Long.MAX_VALUE / MAX_YEARS || years < MIN_YEARS || years > MAX_YEARS)
            return new Result(false, 0, "Informe um salário válido e duração de 1 a 5 anos.");
        for (Club employer : league.getClubs()) for (StaffRole role : StaffRole.values()) {
            StaffMember employed = employer.getStaffMember(role);
            if (employed != null && employed.getName().equals(candidate.getName())
                && (employer != club || role != candidate.getRole()))
                return new Result(false, 0, "Este profissional já está contratado por outro clube.");
        }
        long requested = requestedSalary(candidate, league.getCurrentSeason(), years);
        if (salary < requested)
            return new Result(false, requested, "Contraproposta: o profissional solicita um salário maior para essa duração.");
        club.hireStaff(new StaffMember(candidate.getRole(), candidate.getName(), candidate.getQuality(), salary,
            league.getCurrentSeason() + years, candidate.getNationality(), candidate.getSpecialty()));
        return new Result(true, 0, "Contrato assinado. O salário negociado já integra as despesas da comissão técnica.");
    }

    public static final class Result {
        public final boolean accepted;
        public final long counterSalary;
        public final String message;
        private Result(boolean accepted, long counterSalary, String message) {
            this.accepted = accepted;
            this.counterSalary = counterSalary;
            this.message = message;
        }
    }
}
