package edu.hitsz.aircraftwar.bullet;

import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.config.GameConfig;

public abstract class BaseBullet extends AbstractFlyingObject {
    private final int power;
    private boolean frozen;
    private long frozenUntilMs;
    private int speedXBeforeFreeze;
    private int speedYBeforeFreeze;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    protected void freezeFor(int durationMs) {
        if (!frozen) {
            speedXBeforeFreeze = speedX;
            speedYBeforeFreeze = speedY;
        }
        frozen = true;
        frozenUntilMs = System.currentTimeMillis() + Math.max(0, durationMs);
        speedX = 0;
        speedY = 0;
    }

    @Override
    public void forward() {
        if (frozen) {
            if (System.currentTimeMillis() < frozenUntilMs) return;
            speedX = speedXBeforeFreeze;
            speedY = speedYBeforeFreeze;
            frozen = false;
        }
        super.forward();
        if (locationX <= 0 || locationX >= GameConfig.LOGICAL_WIDTH
                || locationY >= GameConfig.LOGICAL_HEIGHT || locationY <= 0) {
            vanish();
        }
    }

    public int getPower() { return power; }
}
