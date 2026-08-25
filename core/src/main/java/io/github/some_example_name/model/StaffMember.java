package io.github.some_example_name.model;

/** Funcionário com qualidade interna, contrato simples e exibição por estrelas. */
public class StaffMember {
    private final StaffRole role;
    private final String name;
    private final int quality;
    private final long annualSalary;
    private final int contractEndYear;

    public StaffMember(StaffRole role, String name, int quality, long annualSalary, int contractEndYear) {
        this.role = role;
        this.name = name;
        this.quality = Math.max(50, Math.min(100, quality));
        this.annualSalary = Math.max(0L, annualSalary);
        this.contractEndYear = contractEndYear;
    }

    public StaffRole getRole() { return role; }
    public String getName() { return name; }
    public int getQuality() { return quality; }
    public long getAnnualSalary() { return annualSalary; }
    public int getContractEndYear() { return contractEndYear; }
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

    public String getStars() {
        if (quality >= 90) return "★★★★★";
        if (quality >= 80) return "★★★★☆";
        if (quality >= 70) return "★★★★";
        if (quality >= 60) return "★★★☆";
        return "★★★";
    }
}
