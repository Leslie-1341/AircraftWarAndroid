package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.strategy.ShootStrategy;
import edu.hitsz.aircraftwar.strategy.StraightShootStrategy;

/** 玩家飞机，使用线程安全的懒加载单例。 */
public class HeroAircraft extends AbstractAircraft {
    private static volatile HeroAircraft instance;
    private static final int DEFAULT_HP = 500;
    private static final int DEFAULT_SHOOT_NUM = 1;
    private static final int DEFAULT_POWER = 30;
    private static final int DEFAULT_DIRECTION = -1;

    private long temporaryShootModeUntilMs;

    private HeroAircraft(int x, int y, int speedX, int speedY, int hp) {
        super(x, y, speedX, speedY, hp);
        restoreDefaultShootMode();
    }

    public static HeroAircraft getInstance() {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(
                            GameConfig.LOGICAL_WIDTH / 2,
                            GameConfig.LOGICAL_HEIGHT - 100,
                            0, 0, DEFAULT_HP);
                }
            }
        }
        return instance;
    }

    @Override public void forward() { /* 玩家飞机由触摸输入控制。 */ }

    public void reset(int x, int y) {
        locationX = x;
        locationY = y;
        speedX = 0;
        speedY = 0;
        isValid = true;
        hp = maxHp;
        temporaryShootModeUntilMs = 0L;
        restoreDefaultShootMode();
    }

    /** 激活限时射击模式；重复拾取时从本次激活时间重新计时。 */
    public void activateTemporaryShootMode(ShootStrategy strategy, int bulletsPerShot, long durationMs) {
        if (strategy == null || bulletsPerShot <= 0 || durationMs < 0) {
            throw new IllegalArgumentException("Invalid temporary shoot mode");
        }
        shootStrategy = strategy;
        shootNum = bulletsPerShot;
        temporaryShootModeUntilMs = System.currentTimeMillis() + durationMs;
    }

    /** 由游戏更新循环每帧调用，负责恢复过期火力。 */
    public void updateTimedEffects() {
        if (temporaryShootModeUntilMs > 0L
                && System.currentTimeMillis() >= temporaryShootModeUntilMs) {
            temporaryShootModeUntilMs = 0L;
            restoreDefaultShootMode();
        }
    }

    private void restoreDefaultShootMode() {
        shootNum = DEFAULT_SHOOT_NUM;
        power = DEFAULT_POWER;
        direction = DEFAULT_DIRECTION;
        shootStrategy = new StraightShootStrategy();
    }
}
