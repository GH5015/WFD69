package io.github.some_example_name.model;

public class DraftPick {
    private int year;
    private int round;
    private Club originalOwner;
    private Club currentOwner;
    private int projectedPosition = 15; // Valor padrão (ex: meio de tabela / 15º lugar)
    private double projectedPositionConfidence = 0.35;

    public DraftPick() {
    }

    public DraftPick(int year, int round, Club originalOwner) {
        this.year = year;
        this.round = round;
        this.originalOwner = originalOwner;
        this.currentOwner = originalOwner;
    }

    public DraftPick(int year, int round, Club originalOwner, int projectedPosition) {
        this(year, round, originalOwner);
        this.projectedPosition = projectedPosition;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Club getOriginalOwner() {
        return originalOwner;
    }

    public void setOriginalOwner(Club originalOwner) {
        this.originalOwner = originalOwner;
    }

    public Club getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(Club currentOwner) {
        this.currentOwner = currentOwner;
    }

    // --- MÉTODOS QUE FALTAVAM ---
    public int getProjectedPosition() {
        return projectedPosition;
    }

    public void setProjectedPosition(int projectedPosition) {
        this.projectedPosition = projectedPosition;
    }

    public double getProjectedPositionConfidence() {
        return projectedPositionConfidence;
    }

    public void setProjectedPositionConfidence(double confidence) {
        this.projectedPositionConfidence = Math.max(0.0, Math.min(1.0, confidence));
    }

    @Override
    public String toString() {
        String ownerName = (originalOwner != null) ? originalOwner.getName() : "Desconhecido";
        return year + " - " + round + "ª Rodada (Proj. #" + projectedPosition + ")";
    }
}
