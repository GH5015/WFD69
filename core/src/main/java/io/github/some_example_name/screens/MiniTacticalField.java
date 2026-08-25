package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.MatchEvent;

public class MiniTacticalField extends Actor {

    private enum BallState {
        CARRIED,
        IN_PASS,
        IN_SHOT
    }

    private final ShapeRenderer shapeRenderer;

    private Club homeClub;
    private Club awayClub;
    private MatchPhase currentPhase = MatchPhase.CONSTRUCAO;

    private String homeFormation = "4-3-3";
    private String awayFormation = "4-3-3";

    private final Vector2[] homePlayers = new Vector2[11];
    private final Vector2[] homeTargets = new Vector2[11];

    private final Vector2[] awayPlayers = new Vector2[11];
    private final Vector2[] awayTargets = new Vector2[11];

    private final Vector2 ballPos = new Vector2(0.5f, 0.5f);
    private final Vector2 passStartPos = new Vector2();
    private final Vector2 passTargetPos = new Vector2();
    private final Vector2 actionControlPos = new Vector2();
    private final Vector2 curveSampleA = new Vector2();
    private final Vector2 curveSampleB = new Vector2();
    private float passProgress = 0f;
    private BallState ballState = BallState.CARRIED;

    private boolean isHomeBallPossession = true;
    private boolean pendingPossessionChange = false;
    private int currentCarrierIndex = 9;
    private int targetReceiverIndex = -1;

    private float targetHomePossessionPercent = 50.0f;
    private float currentDisplayedPossession = 50.0f;

    private float timeUntilNextPass = 1.8f;
    private boolean showActionVector = false;
    private float animationSpeed = 1.0f;
    private float organicTimer = 0f;

    private float shotSpeedMultiplier = 1.0f;
    private float passSpeedMultiplier = 1.0f;
    private Runnable onPassCompletedCallback = null;
    private Runnable onShotCompletedCallback = null;
    private boolean eventSequenceRunning = false;

    private boolean isGoalFrozen = false;
    private float goalAnimationTimer = 0f;
    private float shakeIntensity = 0f;

    private final Color COLOR_GRASS = new Color(0.12f, 0.45f, 0.22f, 1f);
    private final Color COLOR_GRASS_ALT = new Color(0.10f, 0.39f, 0.19f, 1f);
    private final Color COLOR_GRASS_LIGHT = new Color(0.18f, 0.52f, 0.25f, 1f);
    private final Color COLOR_PITCH_EDGE = new Color(0.015f, 0.05f, 0.025f, 0.92f);
    private final Color COLOR_PITCH_LINE = new Color(0.88f, 0.96f, 0.89f, 0.78f);
    private final Color COLOR_NET = new Color(0.95f, 1f, 0.96f, 0.26f);
    private final Color COLOR_PLAYER_SHADOW = new Color(0f, 0.05f, 0.02f, 0.42f);
    private final Color COLOR_HOME = new Color(0.15f, 0.55f, 0.98f, 1f);
    private final Color COLOR_HOME_GK = new Color(0.0f, 0.8f, 0.8f, 1f);
    private final Color COLOR_AWAY = new Color(0.95f, 0.25f, 0.25f, 1f);
    private final Color COLOR_AWAY_GK = new Color(0.95f, 0.60f, 0.1f, 1f);
    private final Color COLOR_HIGHLIGHT = new Color(1.0f, 0.9f, 0.2f, 0.8f);
    private final Color COLOR_GOAL_FLASH = new Color(1.0f, 0.84f, 0.0f, 0.6f);

    public MiniTacticalField() {
        this.shapeRenderer = new ShapeRenderer();

        for (int i = 0; i < 11; i++) {
            homePlayers[i] = new Vector2();
            homeTargets[i] = new Vector2();
            awayPlayers[i] = new Vector2();
            awayTargets[i] = new Vector2();
        }

        applyFormations();
        snapToTargets();
    }

    public void setTeams(Club home, Club away) {
        this.homeClub = home;
        this.awayClub = away;

        if (home != null && home.getFormation() != null) {
            this.homeFormation = home.getFormation().toString();
        }
        if (away != null && away.getFormation() != null) {
            this.awayFormation = away.getFormation().toString();
        }

        applyFormations();
        snapToTargets();
    }

    public void setHomeFormation(String formation) {
        if (formation != null && !formation.isEmpty()) {
            this.homeFormation = formation;
            applyFormations();
        }
    }

    public void setAwayFormation(String formation) {
        if (formation != null && !formation.isEmpty()) {
            this.awayFormation = formation;
            applyFormations();
        }
    }

    public void setMatchPhase(MatchPhase phase) {
        this.currentPhase = phase;
        applyFormations();
    }

    public void applyFormations() {
        if (currentPhase == MatchPhase.ESCANTEIO) {
            applyCornerKickFormations();
            return;
        }

        MatchPhase homePhase = isHomeBallPossession ? currentPhase : MatchPhase.DEFESA;
        MatchPhase awayPhase = !isHomeBallPossession ? currentPhase : MatchPhase.DEFESA;

        Vector2[] hPositions = TacticalFormations.getPositions(homeFormation, homePhase);
        Vector2[] aPositions = TacticalFormations.getPositions(awayFormation, awayPhase);

        for (int i = 0; i < 11; i++) {
            float homeX = hPositions[i].x;
            if (!isHomeBallPossession) {
                homeX = Math.max(0.05f, homeX * 0.82f);
            } else if (currentPhase == MatchPhase.ATAQUE) {
                homeX = Math.min(0.88f, homeX * 1.15f);
            }

            homeTargets[i].set(MathUtils.clamp(homeX, 0.05f, 0.88f), hPositions[i].y);

            float mirroredX = 1.0f - aPositions[i].x;
            if (isHomeBallPossession) {
                mirroredX = 1.0f - ((1.0f - mirroredX) * 0.82f);
            } else if (currentPhase == MatchPhase.ATAQUE) {
                mirroredX = Math.max(0.18f, mirroredX * 0.85f);
            }

            awayTargets[i].set(MathUtils.clamp(mirroredX, 0.18f, 0.95f), aPositions[i].y);
        }
    }

    private void applyCornerKickFormations() {
        boolean homeAttacking = isHomeBallPossession;
        float targetBoxX = homeAttacking ? 0.88f : 0.12f;

        if (homeAttacking) {
            homeTargets[10].set(0.98f, 0.05f);
            for (int i = 1; i < 10; i++) {
                homeTargets[i].set(targetBoxX + MathUtils.random(-0.08f, 0.02f), 0.35f + (i * 0.035f));
                awayTargets[i].set(targetBoxX + MathUtils.random(-0.04f, 0.04f), 0.35f + (i * 0.035f));
            }
        } else {
            awayTargets[10].set(0.02f, 0.95f);
            for (int i = 1; i < 10; i++) {
                awayTargets[i].set(targetBoxX + MathUtils.random(-0.02f, 0.08f), 0.35f + (i * 0.035f));
                homeTargets[i].set(targetBoxX + MathUtils.random(-0.04f, 0.04f), 0.35f + (i * 0.035f));
            }
        }
    }

    private void snapToTargets() {
        for (int i = 0; i < 11; i++) {
            homePlayers[i].set(homeTargets[i]);
            awayPlayers[i].set(awayTargets[i]);
        }
        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        ballPos.set(team[currentCarrierIndex]);
    }

    public void triggerGoalCelebration() {
        this.isGoalFrozen = true;
        this.goalAnimationTimer = 0f;
        this.shakeIntensity = 10.0f;
    }

    public void resumeFromGoal() {
        this.isGoalFrozen = false;
        this.shakeIntensity = 0f;
        showActionVector = false;
        applyFormations();
        snapToTargets();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isGoalFrozen) {
            goalAnimationTimer += delta;
            if (shakeIntensity > 0) {
                shakeIntensity = Math.max(0, shakeIntensity - delta * 8f);
            }
            return;
        }

        organicTimer += delta * 1.1f;

        currentDisplayedPossession = MathUtils.lerp(
            currentDisplayedPossession,
            targetHomePossessionPercent,
            Math.min(1.0f, delta * 1.2f * animationSpeed)
        );

        float lerpFactor = Math.min(1.0f, delta * 1.8f * animationSpeed);

        float homeAdvance = 0f;
        float awayAdvance = 0f;

        if (currentPhase == MatchPhase.ATAQUE) {
            homeAdvance = isHomeBallPossession ? 0.07f : -0.06f;
            awayAdvance = !isHomeBallPossession ? -0.07f : 0.06f;
        } else if (currentPhase == MatchPhase.CONSTRUCAO) {
            homeAdvance = isHomeBallPossession ? 0.04f : -0.03f;
            awayAdvance = !isHomeBallPossession ? -0.04f : 0.03f;
        }

        for (int i = 0; i < 11; i++) {
            float waveX = MathUtils.sin(organicTimer * 1.1f + i * 1.7f) * 0.006f;
            float waveY = MathUtils.cos(organicTimer * 1.3f + i * 1.3f) * 0.008f;

            if (i == 0) { waveX = 0f; waveY = 0f; }

            float extraHomeDef = (!isHomeBallPossession && i >= 1 && i <= 4) ? -0.03f : 0f;
            float extraAwayDef = (isHomeBallPossession && i >= 1 && i <= 4) ? 0.03f : 0f;

            float hX = MathUtils.clamp(homeTargets[i].x + waveX + homeAdvance + extraHomeDef, 0.05f, 0.85f);
            float hY = MathUtils.clamp(homeTargets[i].y + waveY, 0.05f, 0.95f);
            homePlayers[i].lerp(new Vector2(hX, hY), lerpFactor);

            float aX = MathUtils.clamp(awayTargets[i].x + waveX + awayAdvance + extraAwayDef, 0.18f, 0.95f);
            float aY = MathUtils.clamp(awayTargets[i].y + waveY, 0.05f, 0.95f);
            awayPlayers[i].lerp(new Vector2(aX, aY), lerpFactor);
        }

        float minDist = 0.038f;
        resolvePlayerCollisions(homePlayers, minDist);
        resolvePlayerCollisions(awayPlayers, minDist);
        resolveInterTeamCollisions(homePlayers, awayPlayers, minDist);

        Vector2[] teamInPossession = isHomeBallPossession ? homePlayers : awayPlayers;
        Vector2 currentCarrierPos = teamInPossession[currentCarrierIndex];

        if (ballState == BallState.CARRIED) {
            float dribbleOffsetX = (isHomeBallPossession ? 0.01f : -0.01f) * MathUtils.sin(organicTimer * 4f);
            float dribbleOffsetY = 0.005f * MathUtils.cos(organicTimer * 3f);

            Vector2 targetBallFootPos = new Vector2(currentCarrierPos.x + dribbleOffsetX, currentCarrierPos.y + dribbleOffsetY);
            ballPos.lerp(targetBallFootPos, Math.min(1.0f, delta * 10f));

            float activePossession = isHomeBallPossession ? targetHomePossessionPercent : (100f - targetHomePossessionPercent);
            float passDelayFactor = MathUtils.clamp(2.5f - (activePossession / 50f), 0.8f, 2.0f);

            timeUntilNextPass -= delta * animationSpeed;
            if (timeUntilNextPass <= 0) {
                timeUntilNextPass = passDelayFactor;
                triggerSmartPass();
            }
        }
        else if (ballState == BallState.IN_PASS) {
            passTargetPos.set(teamInPossession[targetReceiverIndex]);
            passProgress += delta * 0.85f * passSpeedMultiplier * animationSpeed;
            float progressClamped = Math.min(1.0f, passProgress);

            float alpha = Interpolation.pow2Out.apply(progressClamped);
            evaluateTrajectory(alpha, ballPos);

            if (passProgress >= 1.0f) {
                ballPos.set(passTargetPos);
                currentCarrierIndex = targetReceiverIndex;
                ballState = BallState.CARRIED;
                passSpeedMultiplier = 1.0f;

                if (onPassCompletedCallback != null) {
                    Runnable callback = onPassCompletedCallback;
                    onPassCompletedCallback = null;
                    callback.run();
                }
            }
        }
        else if (ballState == BallState.IN_SHOT) {
            passProgress += delta * 0.55f * shotSpeedMultiplier * animationSpeed;
            float progressClamped = Math.min(1.0f, passProgress);

            float alpha = Interpolation.pow2Out.apply(progressClamped);
            evaluateTrajectory(alpha, ballPos);

            if (passProgress >= 1.0f) {
                ballPos.set(passTargetPos);
                ballState = BallState.CARRIED;
                showActionVector = false;

                if (onShotCompletedCallback != null) {
                    Runnable cb = onShotCompletedCallback;
                    onShotCompletedCallback = null;
                    cb.run();
                }

                if (pendingPossessionChange) {
                    switchPossession();
                    pendingPossessionChange = false;
                } else {
                    currentCarrierIndex = selectRealisticNextCarrier();
                }
            }
        }
    }

    public void setHomePossessionPercent(float percent) {
        this.targetHomePossessionPercent = MathUtils.clamp(percent, 0f, 100f);

        boolean dominantIsHome = targetHomePossessionPercent >= 50f;
        if (this.isHomeBallPossession != dominantIsHome && MathUtils.randomBoolean(0.3f)) {
            triggerDisarmEvent();
        }
    }

    private void resolvePlayerCollisions(Vector2[] players, float minDist) {
        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {
                float dist = players[i].dst(players[j]);
                if (dist < minDist && dist > 0) {
                    float overlap = (minDist - dist) * 0.5f;
                    Vector2 pushDir = new Vector2(players[i]).sub(players[j]).nor();

                    players[i].add(pushDir.x * overlap, pushDir.y * overlap);
                    players[j].sub(pushDir.x * overlap, pushDir.y * overlap);
                }
            }
        }
    }

    private void resolveInterTeamCollisions(Vector2[] home, Vector2[] away, float minDist) {
        for (int i = 0; i < home.length; i++) {
            for (int j = 0; j < away.length; j++) {
                float dist = home[i].dst(away[j]);
                if (dist < minDist && dist > 0) {
                    float overlap = (minDist - dist) * 0.5f;
                    Vector2 pushDir = new Vector2(home[i]).sub(away[j]).nor();

                    home[i].add(pushDir.x * overlap, pushDir.y * overlap);
                    away[j].sub(pushDir.x * overlap, pushDir.y * overlap);
                }
            }
        }
    }

    public void triggerDisarmEvent() {
        if (ballState != BallState.CARRIED) return;

        showActionVector = false;

        Vector2[] currentTeam = isHomeBallPossession ? homePlayers : awayPlayers;
        Vector2[] defendingTeam = isHomeBallPossession ? awayPlayers : homePlayers;

        Vector2 carrierPos = currentTeam[currentCarrierIndex];

        int tacklerIndex = 1;
        float minDistance = Float.MAX_VALUE;

        for (int i = 1; i < defendingTeam.length; i++) {
            float dist = defendingTeam[i].dst(carrierPos);
            if (dist < minDistance) {
                minDistance = dist;
                tacklerIndex = i;
            }
        }

        final int winningPlayerIndex = tacklerIndex;

        defendingTeam[winningPlayerIndex].lerp(carrierPos, 0.65f);

        switchPossession();

        currentCarrierIndex = winningPlayerIndex;
        ballState = BallState.CARRIED;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                targetReceiverIndex = MathUtils.random(1, 6);
                if (targetReceiverIndex == currentCarrierIndex) {
                    targetReceiverIndex = (currentCarrierIndex % 10) + 1;
                }

                passStartPos.set(ballPos);
                passTargetPos.set(currentTeam[targetReceiverIndex]);
                configureTrajectory(0.040f, false);
                passProgress = 0f;
                passSpeedMultiplier = 1.1f;
                ballState = BallState.IN_PASS;
            }
        }, 0.25f);
    }

    public void triggerSmartPass() {
        if (ballState != BallState.CARRIED) return;

        int receiver;

        if (currentCarrierIndex <= 4) {
            receiver = MathUtils.randomBoolean(0.7f) ? MathUtils.random(5, 7) : MathUtils.random(1, 4);
        } else if (currentCarrierIndex <= 7) {
            float rand = MathUtils.random();
            if (rand < 0.30f) {
                receiver = MathUtils.random(1, 4);
            } else if (rand < 0.70f) {
                receiver = MathUtils.random(5, 7);
            } else {
                receiver = MathUtils.random(8, 10);
            }
        } else {
            receiver = MathUtils.randomBoolean(0.6f) ? MathUtils.random(5, 7) : MathUtils.random(8, 10);
        }

        if (receiver == currentCarrierIndex) {
            receiver = (currentCarrierIndex % 10) + 1;
        }

        startPass(currentCarrierIndex, receiver, 0.85f);
    }

    private int selectRealisticNextCarrier() {
        return MathUtils.random(1, 10);
    }

    public void startPass(int fromIndex, int toIndex, float speedMultiplier) {
        startPass(fromIndex, toIndex, speedMultiplier, null);
    }

    private void startPass(
        int fromIndex,
        int toIndex,
        float speedMultiplier,
        Runnable onComplete
    ) {
        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        currentCarrierIndex = fromIndex;
        targetReceiverIndex = toIndex;

        passStartPos.set(team[fromIndex]);
        passTargetPos.set(team[toIndex]);
        configureTrajectory(
            MathUtils.random(0.018f, 0.050f),
            false
        );
        passProgress = 0f;
        this.passSpeedMultiplier = speedMultiplier;
        this.onPassCompletedCallback = onComplete;
        ballState = BallState.IN_PASS;
        showActionVector = false;
    }

    private void startShot(int shooterIndex, float targetX, float targetY, float speedMultiplier, Runnable onComplete) {
        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        currentCarrierIndex = shooterIndex;

        passStartPos.set(team[shooterIndex]);
        passTargetPos.set(targetX, targetY);
        configureTrajectory(
            MathUtils.random(0.055f, 0.105f),
            true
        );
        passProgress = 0f;
        this.shotSpeedMultiplier = speedMultiplier;
        this.onShotCompletedCallback = () -> {
            eventSequenceRunning = false;
            if (onComplete != null) {
                onComplete.run();
            }
        };
        ballState = BallState.IN_SHOT;
        showActionVector = true;
    }

    /**
     * Define uma curva quadrática. O desvio lateral deixa os passes mais
     * orgânicos; nos chutes, um arco maior dá a sensação de efeito/elevação.
     */
    private void configureTrajectory(
        float curveAmount,
        boolean shot
    ) {

        float dx = passTargetPos.x - passStartPos.x;
        float dy = passTargetPos.y - passStartPos.y;
        float length = Math.max(0.001f, (float) Math.sqrt(dx * dx + dy * dy));
        float direction = MathUtils.randomBoolean() ? 1f : -1f;

        float lateralX = -dy / length * curveAmount * direction;
        float lateralY = dx / length * curveAmount * direction;

        actionControlPos.set(
            (passStartPos.x + passTargetPos.x) * 0.5f + lateralX,
            (passStartPos.y + passTargetPos.y) * 0.5f + lateralY
        );

        if (shot) {
            // Chutes direcionados tendem a "subir" na visualização antes de cair no alvo.
            actionControlPos.y += MathUtils.random(-0.028f, 0.045f);
        }
    }

    /** Avalia a curva de Bézier quadrática no ponto solicitado. */
    private Vector2 evaluateTrajectory(
        float progress,
        Vector2 output
    ) {

        float inverse = 1f - progress;
        return output.set(
            inverse * inverse * passStartPos.x
                + 2f * inverse * progress * actionControlPos.x
                + progress * progress * passTargetPos.x,
            inverse * inverse * passStartPos.y
                + 2f * inverse * progress * actionControlPos.y
                + progress * progress * passTargetPos.y
        );
    }

    /** Desenha um trecho da trajetória real no ShapeRenderer já em modo Line. */
    private void drawCurvedTrajectory(
        float originX,
        float originY,
        float width,
        float height,
        float start,
        float end
    ) {

        int segments = 16;
        evaluateTrajectory(start, curveSampleA);

        for (int index = 1; index <= segments; index++) {
            float progress = start + (end - start) * index / segments;
            evaluateTrajectory(progress, curveSampleB);
            shapeRenderer.line(
                originX + curveSampleA.x * width,
                originY + curveSampleA.y * height,
                originX + curveSampleB.x * width,
                originY + curveSampleB.y * height
            );
            curveSampleA.set(curveSampleB);
        }
    }

    private void switchPossession() {
        isHomeBallPossession = !isHomeBallPossession;
        currentCarrierIndex = 0;
        ballState = BallState.CARRIED;
    }

    public float getHomePossessionPercent() { return currentDisplayedPossession; }
    public void setPossessionPercent(float homePercent) { this.targetHomePossessionPercent = MathUtils.clamp(homePercent, 10.0f, 90.0f); }
    public void setAnimationSpeed(float speed) { this.animationSpeed = speed; }

    public void onMatchEvent(MatchEvent event, Runnable onGoalNetHitCallback) {
        if (event == null) return;

        boolean isGoalEvent = "GOL".equals(event.type);

        /*
         * Um gol sempre tem prioridade visual: encerra uma animação menor
         * pendente para que a construção e a finalização do gol apareçam.
         */
        if (isGoalEvent && (eventSequenceRunning || ballState != BallState.CARRIED)) {
            cancelCurrentActionSequence();
        } else if (eventSequenceRunning || ballState != BallState.CARRIED) {
            // Não deixa uma nova narração cortar um passe ou chute ainda visível.
            return;
        }

        showActionVector = false;

        boolean eventHomeTeam = event.isHomeTeam;

        if (this.isHomeBallPossession != eventHomeTeam) {
            this.isHomeBallPossession = eventHomeTeam;
            this.currentCarrierIndex = MathUtils.random(6, 10);
            this.ballState = BallState.CARRIED;
            applyFormations();
        }

        if (isGoalEvent) {
            setMatchPhase(MatchPhase.ATAQUE);
            eventSequenceRunning = true;

            int defenderOrMid = MathUtils.random(2, 6);
            int builderIndex = MathUtils.random(5, 7);
            int shooterIndex = MathUtils.random(8, 10);

            startPass(defenderOrMid, builderIndex, 0.80f, () ->
                startPass(builderIndex, shooterIndex, 0.80f, () -> {
                    float goalTargetX = eventHomeTeam ? 1.035f : -0.035f;
                    float goalTargetY = MathUtils.random(0.42f, 0.58f);
                    startShot(shooterIndex, goalTargetX, goalTargetY, 0.95f, onGoalNetHitCallback);
                    pendingPossessionChange = true;
                })
            );
        }
        else if ("CHUTE".equals(event.type)) {
            setMatchPhase(MatchPhase.ATAQUE);
            eventSequenceRunning = true;

            int builderIndex = MathUtils.random(4, 7);
            int shooterIndex = MathUtils.random(7, 10);

            startPass(currentCarrierIndex, builderIndex, 0.80f, () -> {
                float shotTargetX = eventHomeTeam ? 0.97f : 0.03f;
                float shotTargetY = MathUtils.random(0.28f, 0.72f);
                startShot(shooterIndex, shotTargetX, shotTargetY, 0.95f, null);
                if (MathUtils.randomBoolean(0.6f)) pendingPossessionChange = true;
            });
        }
        // ANIMAÇÃO DE ESCANTEIO (Velocidade Cadenciada)
        else if ("ESCANTEIO".equals(event.type)) {
            setMatchPhase(MatchPhase.ESCANTEIO);
            eventSequenceRunning = true;

            float cornerX = eventHomeTeam ? 0.98f : 0.02f;
            float cornerY = MathUtils.randomBoolean() ? 0.05f : 0.95f;

            ballPos.set(cornerX, cornerY);
            currentCarrierIndex = 10;

            startPass(10, 9, 0.42f, () -> {
                float goalTargetX = eventHomeTeam ? 1.02f : -0.02f;
                float goalTargetY = MathUtils.random(0.40f, 0.60f);
                startShot(9, goalTargetX, goalTargetY, 0.65f, null);
            });
        }
        // ANIMAÇÃO DE TIRO LIVRE / FALTA (Velocidade Cadenciada)
        else if ("FALTA".equals(event.type) || "TIRO_LIVRE".equals(event.type)) {
            setMatchPhase(MatchPhase.ATAQUE);
            eventSequenceRunning = true;

            float freeKickX = eventHomeTeam ? 0.75f : 0.25f;
            float freeKickY = 0.50f + MathUtils.random(-0.18f, 0.18f);

            ballPos.set(freeKickX, freeKickY);
            currentCarrierIndex = 8;

            float goalTargetX = eventHomeTeam ? 1.02f : -0.02f;
            float goalTargetY = MathUtils.random(0.42f, 0.58f);
            startShot(8, goalTargetX, goalTargetY, 0.60f, null);
        }
        else if ("ROUBADA".equals(event.type) || "DESARME".equals(event.type) || "INTERCEPTACAO".equals(event.type)) {
            setMatchPhase(MatchPhase.CONSTRUCAO);
            triggerDisarmEvent();
        }
        else {
            setMatchPhase(MatchPhase.CONSTRUCAO);
            triggerSmartPass();
        }
    }

    private void cancelCurrentActionSequence() {
        eventSequenceRunning = false;
        onPassCompletedCallback = null;
        onShotCompletedCallback = null;
        pendingPossessionChange = false;
        showActionVector = false;
        passProgress = 0f;
        ballState = BallState.CARRIED;

        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        currentCarrierIndex = MathUtils.clamp(currentCarrierIndex, 0, team.length - 1);
        ballPos.set(team[currentCarrierIndex]);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        float offsetX = (shakeIntensity > 0) ? MathUtils.random(-shakeIntensity, shakeIntensity) : 0f;
        float offsetY = (shakeIntensity > 0) ? MathUtils.random(-shakeIntensity, shakeIntensity) : 0f;

        float x = getX() + offsetX;
        float y = getY() + offsetY;
        float w = getWidth();
        float h = getHeight();

        float playerRadius = MathUtils.clamp(
            Math.min(w, h) * 0.016f,
            8f,
            13f
        );
        float goalDepth = Math.max(10f, w * 0.010f);
        float goalHeight = h * 0.28f;
        float goalY = y + (h - goalHeight) / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // 1. GRAMADO COM FAIXAS E BORDA DE TRANSMISSÃO
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_PITCH_EDGE);
        shapeRenderer.rect(x - 4f, y - 4f, w + 8f, h + 8f);

        shapeRenderer.setColor(COLOR_GRASS);
        shapeRenderer.rect(x, y, w, h);

        float stripeWidth = w / 12f;
        for (int stripe = 0; stripe < 12; stripe++) {
            shapeRenderer.setColor(stripe % 2 == 0 ? COLOR_GRASS_ALT : COLOR_GRASS_LIGHT);
            shapeRenderer.rect(x + stripe * stripeWidth, y, stripeWidth, h);
        }

        // Tom mais escuro nas áreas para criar leitura de profundidade.
        shapeRenderer.setColor(new Color(0f, 0.12f, 0.045f, 0.14f));
        shapeRenderer.rect(x, y + h * 0.20f, w * 0.16f, h * 0.60f);
        shapeRenderer.rect(x + w * 0.84f, y + h * 0.20f, w * 0.16f, h * 0.60f);

        // Fundo das redes, para dar profundidade aos gols.
        shapeRenderer.setColor(new Color(0.95f, 1f, 0.96f, 0.16f));
        shapeRenderer.rect(x - goalDepth, goalY, goalDepth, goalHeight);
        shapeRenderer.rect(x + w, goalY, goalDepth, goalHeight);
        shapeRenderer.end();

        // 2. LINHAS DE CAMPO
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_PITCH_LINE);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.line(x + w / 2f, y, x + w / 2f, y + h);
        shapeRenderer.circle(x + w / 2f, y + h / 2f, h * 0.14f);

        shapeRenderer.rect(x, y + h * 0.20f, w * 0.16f, h * 0.60f);
        shapeRenderer.rect(x + w - (w * 0.16f), y + h * 0.20f, w * 0.16f, h * 0.60f);
        shapeRenderer.rect(x, y + h * 0.36f, w * 0.055f, h * 0.28f);
        shapeRenderer.rect(x + w - (w * 0.055f), y + h * 0.36f, w * 0.055f, h * 0.28f);
        shapeRenderer.rect(x - goalDepth, goalY, goalDepth, goalHeight);
        shapeRenderer.rect(x + w, goalY, goalDepth, goalHeight);

        drawGoalNet(x - goalDepth, goalY, goalDepth, goalHeight, false);
        drawGoalNet(x + w, goalY, goalDepth, goalHeight, true);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_PITCH_LINE);
        shapeRenderer.circle(x + w / 2f, y + h / 2f, 2.5f);
        shapeRenderer.circle(x + w * 0.11f, y + h / 2f, 2.5f);
        shapeRenderer.circle(x + w * 0.89f, y + h / 2f, 2.5f);
        shapeRenderer.end();

        // 3. TRAJETÓRIA DA JOGADA: passes e chutes ficam legíveis no radar.
        if ((ballState == BallState.IN_PASS || ballState == BallState.IN_SHOT) && !isGoalFrozen) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Color actionColor = isHomeBallPossession ? COLOR_HOME : COLOR_AWAY;
            shapeRenderer.setColor(new Color(actionColor.r, actionColor.g, actionColor.b, 0.72f));
            drawCurvedTrajectory(x, y, w, h, 0f, 1f);
            shapeRenderer.circle(x + passTargetPos.x * w, y + passTargetPos.y * h, playerRadius * 0.56f);
            shapeRenderer.end();
        }

        // 4. ANIMAÇÃO DE GOL NO RADAR
        if (isGoalFrozen) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            float pulseRadius = 15.0f + (goalAnimationTimer * 50.0f) % 45.0f;
            float alphaPulse = MathUtils.clamp(1.0f - (pulseRadius / 60.0f), 0.1f, 0.85f);

            COLOR_GOAL_FLASH.a = alphaPulse;
            shapeRenderer.setColor(COLOR_GOAL_FLASH);
            shapeRenderer.circle(x + (ballPos.x * w), y + (ballPos.y * h), pulseRadius);
            shapeRenderer.end();
        }

        // 5. VETOR DE CHUTE
        if (showActionVector && !isGoalFrozen) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(COLOR_HIGHLIGHT);

            float tailLengthFraction = 0.35f;
            float tailProgress = Math.max(0f, (passProgress - tailLengthFraction) / (1.0f - tailLengthFraction));

            float ballProgress = Math.min(1f, passProgress);
            drawCurvedTrajectory(x, y, w, h, tailProgress, ballProgress);

            shapeRenderer.circle(
                x + ballPos.x * w,
                y + ballPos.y * h,
                playerRadius * 0.80f
            );

            shapeRenderer.end();
        }

        // 6. SOMBRA E DESTAQUE DO PORTADOR DA BOLA
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < 11; i++) {
            shapeRenderer.setColor(COLOR_PLAYER_SHADOW);
            shapeRenderer.circle(
                x + homePlayers[i].x * w + 2f,
                y + homePlayers[i].y * h - 2f,
                playerRadius + 2f
            );
            shapeRenderer.circle(
                x + awayPlayers[i].x * w + 2f,
                y + awayPlayers[i].y * h - 2f,
                playerRadius + 2f
            );
        }

        Vector2[] possessionTeam = isHomeBallPossession
            ? homePlayers
            : awayPlayers;
        Vector2 carrier = possessionTeam[currentCarrierIndex];
        shapeRenderer.setColor(new Color(COLOR_HIGHLIGHT.r, COLOR_HIGHLIGHT.g, COLOR_HIGHLIGHT.b, 0.24f));
        shapeRenderer.circle(
            x + carrier.x * w,
            y + carrier.y * h,
            playerRadius + 7f
        );

        // 7. JOGADORES E BOLA

        for (int i = 0; i < 11; i++) {
            float px = x + (homePlayers[i].x * w);
            float py = y + (homePlayers[i].y * h);
            drawPlayerMarker(px, py, playerRadius, i == 0 ? COLOR_HOME_GK : COLOR_HOME);
        }

        for (int i = 0; i < 11; i++) {
            float px = x + (awayPlayers[i].x * w);
            float py = y + (awayPlayers[i].y * h);
            drawPlayerMarker(px, py, playerRadius, i == 0 ? COLOR_AWAY_GK : COLOR_AWAY);
        }

        float ballX = x + ballPos.x * w;
        float ballY = y + ballPos.y * h;
        float ballLift = 0f;

        if (ballState == BallState.IN_PASS || ballState == BallState.IN_SHOT) {
            float arc = MathUtils.sin(Math.min(1f, passProgress) * MathUtils.PI);
            ballLift = arc * playerRadius * (ballState == BallState.IN_SHOT ? 1.30f : 0.46f);
        }

        shapeRenderer.setColor(COLOR_PLAYER_SHADOW);
        shapeRenderer.circle(ballX + 2f, ballY - 2f, playerRadius * 0.62f);
        shapeRenderer.setColor(Color.valueOf("E8EFE8"));
        shapeRenderer.circle(ballX, ballY + ballLift, isGoalFrozen ? playerRadius * 0.82f : playerRadius * 0.62f);
        shapeRenderer.setColor(Color.valueOf("26352A"));
        shapeRenderer.circle(ballX, ballY + ballLift, playerRadius * 0.21f);
        shapeRenderer.circle(ballX + playerRadius * 0.23f, ballY + ballLift + playerRadius * 0.08f, playerRadius * 0.09f);
        shapeRenderer.circle(ballX - playerRadius * 0.19f, ballY + ballLift - playerRadius * 0.13f, playerRadius * 0.08f);
        shapeRenderer.end();

        // Anel fino acima de todos os marcadores: identifica instantaneamente quem conduz a bola.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_HIGHLIGHT);
        shapeRenderer.circle(x + carrier.x * w, y + carrier.y * h, playerRadius + 5f + MathUtils.sin(organicTimer * 5f) * 1.5f);
        shapeRenderer.end();

        batch.begin();
    }

    /** Desenha a malha leve do gol dentro do ShapeType.Line já ativo. */
    private void drawGoalNet(
        float goalX,
        float goalY,
        float goalWidth,
        float goalHeight,
        boolean rightSide
    ) {

        shapeRenderer.setColor(COLOR_NET);

        for (int line = 1; line < 4; line++) {
            float netX = goalX + goalWidth * line / 4f;
            shapeRenderer.line(netX, goalY, netX, goalY + goalHeight);
        }

        for (int line = 1; line < 5; line++) {
            float netY = goalY + goalHeight * line / 5f;
            shapeRenderer.line(goalX, netY, goalX + goalWidth, netY);
        }

        // Profundidade sutil na face externa da rede.
        float outsideX = rightSide ? goalX + goalWidth : goalX;
        float inset = rightSide ? -goalWidth * 0.24f : goalWidth * 0.24f;
        shapeRenderer.line(outsideX, goalY, outsideX + inset, goalY + goalHeight * 0.10f);
        shapeRenderer.line(outsideX, goalY + goalHeight, outsideX + inset, goalY + goalHeight * 0.90f);
    }

    /** Marcador com aro claro, cor do clube e reflexo para separar os atletas do gramado. */
    private void drawPlayerMarker(
        float x,
        float y,
        float radius,
        Color color
    ) {

        shapeRenderer.setColor(new Color(0.96f, 1f, 0.97f, 0.88f));
        shapeRenderer.circle(x, y, radius + 1.6f);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.setColor(new Color(0f, 0.05f, 0.02f, 0.20f));
        shapeRenderer.circle(x + radius * 0.16f, y - radius * 0.18f, radius * 0.65f);
        shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.42f));
        shapeRenderer.circle(x - radius * 0.25f, y + radius * 0.27f, radius * 0.34f);
    }

    public void dispose() {

        shapeRenderer.dispose();
    }
}
