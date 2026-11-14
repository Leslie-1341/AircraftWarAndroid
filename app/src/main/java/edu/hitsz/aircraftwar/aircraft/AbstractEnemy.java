package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.observer.PropObserver;
import edu.hitsz.aircraftwar.prop.AbstractProp;

import java.util.List;

/** 敌机抽象父类，同时作为炸弹/冰冻道具的观察者。 */
public abstract class AbstractEnemy extends AbstractAircraft implements PropObserver {
    private boolean timedSpeedEffectActive;
    private long timedSpeedEffectUntilMs;
    private int speedXBeforeEffect;
    private int speedYBeforeEffect;

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    public abstract int getScore();
    public abstract List<AbstractProp> dropProps();

    @Override
    public void onBombActive() {
        vanish();
    }

    /** 临时冻结：由游戏循环推进计时，不创建额外线程。 */
    protected void freezeWithRecovery(int durationMs) {
        applyTimedSpeedEffect(durationMs, 0.0, 0.0);
    }

    /** 临时减速：factor=0.5 表示速度减半。 */
    protected void slowWithRecovery(int durationMs, double factor) {
        applyTimedSpeedEffect(durationMs, factor, factor);
    }

    /** 仅减慢纵向速度，保留王牌敌机原有的横向移动。 */
    protected void slowYWithRecovery(int durationMs, double factor) {
        applyTimedSpeedEffect(durationMs, 1.0, factor);
    }

    private void applyTimedSpeedEffect(int durationMs, double factorX, double factorY) {
        if (!timedSpeedEffectActive) {
            speedXBeforeEffect = speedX;
            speedYBeforeEffect = speedY;
        }
        timedSpeedEffectActive = true;
        timedSpeedEffectUntilMs = System.currentTimeMillis() + Math.max(0, durationMs);
        speedX = (int) Math.round(speedXBeforeEffect * factorX);
        speedY = (int) Math.round(speedYBeforeEffect * factorY);
    }

    @Override
    public void forward() {
        if (timedSpeedEffectActive) {
            if (System.currentTimeMillis() < timedSpeedEffectUntilMs) {
                return;
            }
            speedX = speedXBeforeEffect;
            speedY = speedYBeforeEffect;
            timedSpeedEffectActive = false;
        }
        super.forward();
    }
}
