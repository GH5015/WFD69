package io.github.some_example_name.model;

public enum StaffRole {
    COACH("Treinador"),
    SCOUT("Scout"),
    FITNESS_COACH("Preparador físico"),
    DOCTOR("Médico"),
    DEVELOPMENT_DIRECTOR("Diretor de desenvolvimento");

    private final String label;
    StaffRole(String label) { this.label = label; }
    public String getLabel() { return label; }
}
