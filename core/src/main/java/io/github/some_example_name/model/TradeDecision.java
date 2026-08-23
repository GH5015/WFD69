package io.github.some_example_name.model;

public class TradeDecision {

    public enum Status {
        ACCEPTED("PROPOSTA ACEITA", "2ECC71"),
        CONSIDERED("PROPOSTA CONSIDERADA", "F1C40F"),
        REJECTED("PROPOSTA REJEITADA", "E74C3C");

        private final String label;
        private final String hexColor;

        Status(String label, String hexColor) {
            this.label = label;
            this.hexColor = hexColor;
        }

        public String getLabel() { return label; }
        public String getHexColor() { return hexColor; }
    }

    private final Status status;
    private final String feedbackMessage;
    private final long offeredValue;
    private final long expectedValue;
    private final TradeOffer counterOffer; // Nulo se for aceito ou rejeitado sumariamente

    public TradeDecision(Status status, String feedbackMessage, long offeredValue, long expectedValue, TradeOffer counterOffer) {
        this.status = status;
        this.feedbackMessage = feedbackMessage;
        this.offeredValue = offeredValue;
        this.expectedValue = expectedValue;
        this.counterOffer = counterOffer;
    }

    public Status getStatus() { return status; }
    public String getFeedbackMessage() { return feedbackMessage; }
    public long getOfferedValue() { return offeredValue; }
    public long getExpectedValue() { return expectedValue; }
    public TradeOffer getCounterOffer() { return counterOffer; }
    public boolean hasCounterOffer() { return counterOffer != null; }
}
