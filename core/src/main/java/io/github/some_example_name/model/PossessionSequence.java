package io.github.some_example_name.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A história e o estado esportivo de um ataque. Cada passo guarda sua fase,
 * o número do passe e a resolução que permitiu (ou impediu) a progressão.
 */
public class PossessionSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    /** Seis momentos resolvidos mais, no máximo, o resultado do chute. */
    public static final int MAX_RELEVANT_STEPS = 7;

    public enum Phase {
        RECUPERACAO,
        CONSTRUCAO,
        PROGRESSAO,
        ULTIMO_TERCO,
        CRIACAO,
        FINALIZACAO,
        RESULTADO
    }

    public enum Action {
        RECOVER,
        PASS,
        CARRY,
        SHOT,
        RESULT
    }

    /** Instrução visual definida pelo motor e reproduzida pelo campo. */
    public enum VisualCue {
        CONTROL,
        QUICK_RECOVERY,
        SHORT_PASS,
        LONG_PASS,
        SWITCH_PLAY,
        ONE_TWO,
        THROUGH_BALL,
        SECOND_BALL,
        DRIBBLE,
        OVERLAP,
        CROSS,
        CUTBACK,
        PLACED_SHOT,
        POWER_SHOT,
        FIRST_TIME_SHOT,
        HOLD
    }

    public static final class Step implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int second;
        private final Phase phase;
        private final Action action;
        private final String description;
        private final int fromSlot;
        private final int toSlot;
        private final String opponentName;
        private final int opponentSlot;
        private FieldSector fromSector;
        private FieldSector toSector;
        private VisualCue visualCue;
        private NarrativeCategory narrativeCategory;
        private MatchPhase matchPhase;
        private int attackingScore;
        private int defendingScore;
        private boolean resolvedSuccessfully = true;
        private int sequenceIndex;
        private int passNumber;

        public Step(
            int second,
            Phase phase,
            Action action,
            String description,
            int fromSlot,
            int toSlot
        ) {
            this(second, phase, action, description, fromSlot, toSlot, null, -1);
        }

        public Step(
            int second,
            Phase phase,
            Action action,
            String description,
            int fromSlot,
            int toSlot,
            String opponentName,
            int opponentSlot
        ) {
            this.second = Math.max(0, Math.min(59, second));
            this.phase = phase;
            this.action = action;
            this.description = description == null ? "" : description;
            this.fromSlot = fromSlot;
            this.toSlot = toSlot;
            this.opponentName = opponentName;
            this.opponentSlot = opponentSlot;
            this.narrativeCategory = defaultNarrativeCategory(phase, action);
            this.matchPhase = mapMatchPhase(phase);
        }

        public int getSecond() { return second; }
        public Phase getPhase() { return phase; }
        public Action getAction() { return action; }
        public String getDescription() { return description; }
        public int getFromSlot() { return fromSlot; }
        public int getToSlot() { return toSlot; }
        public String getOpponentName() { return opponentName; }
        public int getOpponentSlot() { return opponentSlot; }
        public VisualCue getVisualCue() {
            return visualCue != null ? visualCue : defaultVisualCue(action);
        }
        public FieldSector getFromSector() {
            return fromSector != null ? fromSector : defaultSector(phase, false);
        }
        public FieldSector getToSector() {
            return toSector != null ? toSector : defaultSector(phase, true);
        }
        public MatchPhase getMatchPhase() {
            return matchPhase != null ? matchPhase : mapMatchPhase(phase);
        }
        public int getAttackingScore() { return attackingScore; }
        public int getDefendingScore() { return defendingScore; }
        public boolean wasResolvedSuccessfully() { return resolvedSuccessfully; }
        public int getSequenceIndex() { return sequenceIndex; }
        public int getPassNumber() { return passNumber; }

        public Step withNarrativeCategory(NarrativeCategory category) {
            this.narrativeCategory = category;
            return this;
        }

        /** Define as zonas lógicas que a animação deve representar. */
        public Step withSectors(FieldSector from, FieldSector to) {
            this.fromSector = from == null ? defaultSector(phase, false) : from;
            this.toSector = to == null ? this.fromSector : to;
            return this;
        }

        public Step withVisualCue(VisualCue cue) {
            this.visualCue = cue == null ? defaultVisualCue(action) : cue;
            return this;
        }

        public NarrativeCategory getNarrativeCategory() {
            return narrativeCategory != null
                ? narrativeCategory
                : defaultNarrativeCategory(phase, action);
        }

        /** Guarda a pequena disputa que permitiu à posse alcançar esta fase. */
        public Step withResolution(int attack, int defense, boolean successful) {
            this.attackingScore = Math.max(0, attack);
            this.defendingScore = Math.max(0, defense);
            this.resolvedSuccessfully = successful;
            return this;
        }

        public Step withProgress(int index, int completedPasses) {
            this.sequenceIndex = Math.max(0, index);
            this.passNumber = Math.max(0, completedPasses);
            return this;
        }

        private static MatchPhase mapMatchPhase(Phase phase) {
            if (phase == null) return MatchPhase.RESET;
            switch (phase) {
                case RECUPERACAO: return MatchPhase.DEFENSIVE_BUILDUP;
                case CONSTRUCAO: return MatchPhase.BUILDUP;
                case PROGRESSAO: return MatchPhase.MIDFIELD;
                case ULTIMO_TERCO: return MatchPhase.FINAL_THIRD;
                case CRIACAO: return MatchPhase.CHANCE;
                case FINALIZACAO: return MatchPhase.SHOT;
                case RESULTADO:
                default: return MatchPhase.RESET;
            }
        }

        private static FieldSector defaultSector(Phase phase, boolean destination) {
            if (phase == null) return FieldSector.MID_CENTER;
            switch (phase) {
                case RECUPERACAO:
                    return FieldSector.DEF_CENTER;
                case CONSTRUCAO:
                    return destination ? FieldSector.MID_CENTER : FieldSector.DEF_CENTER;
                case PROGRESSAO:
                    return FieldSector.MID_CENTER;
                case ULTIMO_TERCO:
                    return destination ? FieldSector.ATT_CENTER : FieldSector.MID_CENTER;
                case CRIACAO:
                case FINALIZACAO:
                case RESULTADO:
                default:
                    return FieldSector.ATT_CENTER;
            }
        }

        private static NarrativeCategory defaultNarrativeCategory(Phase phase, Action action) {
            if (action == Action.SHOT || phase == Phase.FINALIZACAO) {
                return NarrativeCategory.CHANCE;
            }
            if (phase == Phase.ULTIMO_TERCO) return NarrativeCategory.DUELO;
            if (phase == Phase.RESULTADO) return NarrativeCategory.CHANCE;
            return NarrativeCategory.CONSTRUCAO;
        }

        private static VisualCue defaultVisualCue(Action action) {
            if (action == null) return VisualCue.HOLD;
            switch (action) {
                case RECOVER: return VisualCue.CONTROL;
                case PASS: return VisualCue.SHORT_PASS;
                case CARRY: return VisualCue.DRIBBLE;
                case SHOT: return VisualCue.POWER_SHOT;
                case RESULT:
                default: return VisualCue.HOLD;
            }
        }
    }

    private final String teamName;
    private final boolean homeTeam;
    private final String startZone;
    private final String attackType;
    private FieldSector.Lane attackingLane;
    private final String outcome;
    private final int passes;
    private final float xG;
    private final List<String> players;
    private final List<Step> steps;

    public PossessionSequence(
        String teamName,
        boolean homeTeam,
        String startZone,
        String attackType,
        String outcome,
        int passes,
        float xG,
        List<String> players,
        List<Step> steps
    ) {
        this(
            teamName, homeTeam, startZone, attackType, FieldSector.Lane.CENTER,
            outcome, passes, xG, players, steps
        );
    }

    public PossessionSequence(
        String teamName,
        boolean homeTeam,
        String startZone,
        String attackType,
        FieldSector.Lane attackingLane,
        String outcome,
        int passes,
        float xG,
        List<String> players,
        List<Step> steps
    ) {
        this.teamName = teamName == null ? "" : teamName;
        this.homeTeam = homeTeam;
        this.startZone = startZone == null ? "campo defensivo" : startZone;
        this.attackType = attackType == null ? "ataque posicional" : attackType;
        this.attackingLane = attackingLane == null ? FieldSector.Lane.CENTER : attackingLane;
        this.outcome = outcome == null ? "" : outcome;
        this.passes = Math.max(0, passes);
        this.xG = Math.max(0f, xG);
        this.players = players == null
            ? Collections.<String>emptyList()
            : Collections.unmodifiableList(new ArrayList<>(players));
        if (steps == null || steps.isEmpty()) {
            this.steps = Collections.emptyList();
        } else {
            int relevantSteps = Math.min(MAX_RELEVANT_STEPS, steps.size());
            this.steps = Collections.unmodifiableList(
                new ArrayList<>(steps.subList(0, relevantSteps))
            );
        }
    }

    public String getTeamName() { return teamName; }
    public boolean isHomeTeam() { return homeTeam; }
    public String getStartZone() { return startZone; }
    public String getAttackType() { return attackType; }
    public FieldSector.Lane getAttackingLane() {
        return attackingLane != null ? attackingLane : FieldSector.Lane.CENTER;
    }
    public String getOutcome() { return outcome; }
    public int getPasses() { return passes; }
    public float getXG() { return xG; }
    public List<String> getPlayers() { return players; }
    public List<Step> getSteps() { return steps; }

    public MatchPhase getPhaseAtStep(int stepIndex) {
        if (steps.isEmpty()) return MatchPhase.RESET;
        int safeIndex = Math.max(0, Math.min(stepIndex, steps.size() - 1));
        return steps.get(safeIndex).getMatchPhase();
    }

    public int getPassNumberAtStep(int stepIndex) {
        if (steps.isEmpty()) return 0;
        int safeIndex = Math.max(0, Math.min(stepIndex, steps.size() - 1));
        return steps.get(safeIndex).getPassNumber();
    }
}
