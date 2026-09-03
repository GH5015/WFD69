package io.github.some_example_name.model;

/** Funcionário com qualidade interna, contrato simples e exibição por estrelas. */
public class StaffMember {
    private final StaffRole role;
    private final String name;
    private final int quality;
    private final long annualSalary;
    private final int contractEndYear;
    private final String nationality;
    private final String specialty;

    public StaffMember(StaffRole role, String name, int quality, long annualSalary, int contractEndYear) {
        this(role, name, quality, annualSalary, contractEndYear, "Internacional", role.getLabel());
    }

    public StaffMember(StaffRole role, String name, int quality, long annualSalary, int contractEndYear,
                       String nationality, String specialty) {
        this.role = role;
        this.name = name;
        this.quality = Math.max(50, Math.min(100, quality));
        this.annualSalary = Math.max(0L, annualSalary);
        this.contractEndYear = contractEndYear;
        this.nationality = nationality != null ? nationality : "Internacional";
        this.specialty = specialty != null ? specialty : role.getLabel();
    }

    public StaffRole getRole() { return role; }
    public String getName() { return name; }
    public int getQuality() { return quality; }
    public long getAnnualSalary() { return annualSalary; }
    public int getContractEndYear() { return contractEndYear; }
    public String getNationality() { return nationality; }
    public String getSpecialty() { return specialty; }
    public int getRemainingYears(int currentYear) { return Math.max(0, contractEndYear - currentYear); }
    public boolean isExpired(int currentYear) { return contractEndYear < currentYear; }

    /** Nível utilizado pelos sistemas, sem expor a nota interna à interface. */
    public int getEffectLevel() {
        if (quality >= 90) return 5;
        if (quality >= 80) return 4;
        if (quality >= 70) return 3;
        if (quality >= 60) return 2;
        return 1;
    }

    /** Qualidade apresentada em passos de meia estrela, mantendo a nota interna oculta. */
    public float getDisplayRating() {
        if (quality >= 95) return 5f;
        if (quality >= 88) return 4.5f;
        if (quality >= 80) return 4f;
        if (quality >= 72) return 3.5f;
        if (quality >= 64) return 3f;
        if (quality >= 58) return 2.5f;
        return 2f;
    }

    public String getStars() {
        float rating = getDisplayRating();
        StringBuilder stars = new StringBuilder();
        int full = (int) rating;
        for (int index = 0; index < full; index++) stars.append('★');
        if (rating - full >= .5f) stars.append('½');
        for (int index = (int) Math.ceil(rating); index < 5; index++) stars.append('☆');
        return stars.toString();
    }
}
