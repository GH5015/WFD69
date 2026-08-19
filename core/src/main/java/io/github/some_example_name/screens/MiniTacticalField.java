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
    private Runnable onShotCompletedCallback = null;

    private boolean isGoalFrozen = false;
    private float goalAnimationTimer = 0f;
    private float shakeIntensity = 0f;

    private final Color COLOR_GRASS = new Color(0.12f, 0.45f, 0.22f, 1f);
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
            ballPos.set(passStartPos).interpolate(passTargetPos, alpha, Interpolation.linear);

            if (passProgress >= 1.0f) {
                ballPos.set(passTargetPos);
                currentCarrierIndex = targetReceiverIndex;
                ballState = BallState.CARRIED;
                passSpeedMultiplier = 1.0f;
            }
        }
        else if (ballState == BallState.IN_SHOT) {
            passProgress += delta * 0.55f * shotSpeedMultiplier * animationSpeed;
            float progressClamped = Math.min(1.0f, passProgress);

            float alpha = Interpolation.pow2Out.apply(progressClamped);
            ballPos.set(passStartPos).interpolate(passTargetPos, alpha, Interpolation.linear);

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
        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        currentCarrierIndex = fromIndex;
        targetReceiverIndex = toIndex;

        passStartPos.set(team[fromIndex]);
        passTargetPos.set(team[toIndex]);
        passProgress = 0f;
        this.passSpeedMultiplier = speedMultiplier;
        ballState = BallState.IN_PASS;
        showActionVector = false;
    }

    private void startShot(int shooterIndex, float targetX, float targetY, float speedMultiplier, Runnable onComplete) {
        Vector2[] team = isHomeBallPossession ? homePlayers : awayPlayers;
        currentCarrierIndex = shooterIndex;

        passStartPos.set(team[shooterIndex]);
        passTargetPos.set(targetX, targetY);
        passProgress = 0f;
        this.shotSpeedMultiplier = speedMultiplier;
        this.onShotCompletedCallback = onComplete;
        ballState = BallState.IN_SHOT;
        showActionVector = true;
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
        showActionVector = false;

        boolean eventHomeTeam = event.isHomeTeam;

        if (this.isHomeBallPossession != eventHomeTeam) {
            this.isHomeBallPossession = eventHomeTeam;
            this.currentCarrierIndex = MathUtils.random(6, 10);
            this.ballState = BallState.CARRIED;
            applyFormations();
        }

        if ("GOL".equals(event.type)) {
            setMatchPhase(MatchPhase.ATAQUE);

            int defenderOrMid = MathUtils.random(2, 6);
            int builderIndex = MathUtils.random(5, 7);
            int shooterIndex = MathUtils.random(8, 10);

            startPass(defenderOrMid, builderIndex, 0.55f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    startPass(builderIndex, shooterIndex, 0.60f);

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            float goalTargetX = eventHomeTeam ? 1.035f : -0.035f;
                            float goalTargetY = MathUtils.random(0.42f, 0.58f);

                            startShot(shooterIndex, goalTargetX, goalTargetY, 0.40f, onGoalNetHitCallback);
                            pendingPossessionChange = true;
                        }
                    }, 1.0f);
                }
            }, 0.9f);
        }
        else if ("CHUTE".equals(event.type)) {
            setMatchPhase(MatchPhase.ATAQUE);

            int builderIndex = MathUtils.random(4, 7);
            int shooterIndex = MathUtils.random(7, 10);

            startPass(currentCarrierIndex, builderIndex, 0.55f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    float shotTargetX = eventHomeTeam ? 0.97f : 0.03f;
                    float shotTargetY = MathUtils.random(0.28f, 0.72f);

                    startShot(shooterIndex, shotTargetX, shotTargetY, 0.45f, null);
                    if (MathUtils.randomBoolean(0.6f)) pendingPossessionChange = true;
                }
            }, 0.9f);
        }
        // ANIMAÇÃO DE ESCANTEIO (Velocidade Cadenciada)
        else if ("ESCANTEIO".equals(event.type)) {
            setMatchPhase(MatchPhase.ESCANTEIO);

            float cornerX = eventHomeTeam ? 0.98f : 0.02f;
            float cornerY = MathUtils.randomBoolean() ? 0.05f : 0.95f;

            ballPos.set(cornerX, cornerY);
            currentCarrierIndex = 10;

            // 1.2s de pausa para os times se posicionarem na área
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Passe suave/parabólico para o centro da área (Multiplicador 0.22f)
                    startPass(10, 9, 0.22f);

                    // 2.0s de viagem da bola pelo ar
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            // Cabeceio/Chute após a bola chegar (Multiplicador 0.25f)
                            float goalTargetX = eventHomeTeam ? 1.02f : -0.02f;
                            float goalTargetY = MathUtils.random(0.40f, 0.60f);

                            startShot(9, goalTargetX, goalTargetY, 0.25f, null);
                        }
                    }, 2.0f);
                }
            }, 1.2f);
        }
        // ANIMAÇÃO DE TIRO LIVRE / FALTA (Velocidade Cadenciada)
        else if ("FALTA".equals(event.type) || "TIRO_LIVRE".equals(event.type)) {
            setMatchPhase(MatchPhase.ATAQUE);

            float freeKickX = eventHomeTeam ? 0.75f : 0.25f;
            float freeKickY = 0.50f + MathUtils.random(-0.18f, 0.18f);

            ballPos.set(freeKickX, freeKickY);
            currentCarrierIndex = 8;

            // 1.5s para arrumar a barreira e posicionar o cobrador
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    float goalTargetX = eventHomeTeam ? 1.02f : -0.02f;
                    float goalTargetY = MathUtils.random(0.42f, 0.58f);

                    // Cobrança direta com trajetória lenta e visível (Multiplicador 0.22f)
                    startShot(8, goalTargetX, goalTargetY, 0.22f, null);
                }
            }, 1.5f);
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

        // 1. GRAMADO
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_GRASS);
        shapeRenderer.rect(x, y, w, h);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x - 7, y + (h * 0.36f), 7, h * 0.28f);
        shapeRenderer.rect(x + w, y + (h * 0.36f), 7, h * 0.28f);
        shapeRenderer.end();

        // 2. LINHAS DE CAMPO
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.75f));
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.line(x + w / 2f, y, x + w / 2f, y + h);
        shapeRenderer.circle(x + w / 2f, y + h / 2f, h * 0.14f);

        shapeRenderer.rect(x, y + h * 0.20f, w * 0.16f, h * 0.60f);
        shapeRenderer.rect(x + w - (w * 0.16f), y + h * 0.20f, w * 0.16f, h * 0.60f);
        shapeRenderer.end();

        // 3. ANIMAÇÃO DE GOL NO RADAR
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

        // 4. VETOR DE CHUTE
        if (showActionVector && !isGoalFrozen) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(COLOR_HIGHLIGHT);

            float tailLengthFraction = 0.35f;
            float tailProgress = Math.max(0f, (passProgress - tailLengthFraction) / (1.0f - tailLengthFraction));

            Vector2 currentLineStartPos = new Vector2(passStartPos).interpolate(passTargetPos, tailProgress, Interpolation.linear);

            shapeRenderer.line(
                x + currentLineStartPos.x * w,
                y + currentLineStartPos.y * h,
                x + ballPos.x * w,
                y + ballPos.y * h
            );

            shapeRenderer.end();
        }

        // 5. JOGADORES E BOLA
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float playerRadius = 6.0f;

        for (int i = 0; i < 11; i++) {
            float px = x + (homePlayers[i].x * w);
            float py = y + (homePlayers[i].y * h);
            shapeRenderer.setColor(i == 0 ? COLOR_HOME_GK : COLOR_HOME);
            shapeRenderer.circle(px, py, playerRadius);
        }

        for (int i = 0; i < 11; i++) {
            float px = x + (awayPlayers[i].x * w);
            float py = y + (awayPlayers[i].y * h);
            shapeRenderer.setColor(i == 0 ? COLOR_AWAY_GK : COLOR_AWAY);
            shapeRenderer.circle(px, py, playerRadius);
        }

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(x + (ballPos.x * w), y + (ballPos.y * h), isGoalFrozen ? 8.5f : 5.5f);
        shapeRenderer.end();

        batch.begin();
    }
}
