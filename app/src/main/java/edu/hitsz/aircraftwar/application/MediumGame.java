package edu.hitsz.aircraftwar.application;

import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.factory.EliteFactory;
import edu.hitsz.aircraftwar.factory.ElitePlusFactory;
import edu.hitsz.aircraftwar.factory.EliteProFactory;
import edu.hitsz.aircraftwar.factory.EnemyFactory;
import edu.hitsz.aircraftwar.factory.MobFactory;

/** 普通难度：每 10 秒提升一次敌机生成速度，允许生成 Boss。 */
public class MediumGame extends AbstractGame {
    private int lastLevelUpTime;

    public MediumGame(String difficulty, boolean musicEnabled) {
        this(difficulty, musicEnabled, null);
    }

    public MediumGame(String difficulty, boolean musicEnabled, GameResultListener listener) {
        super(difficulty, musicEnabled, listener);
        backgroundImage = ImageManager.BACKGROUND_IMAGE_NORMAL;
        enemyMaxNumber = 5;
        enemySpawnCycle = 20;
        bossThreshold = 400;
        bossHp = 500;
    }

    public MediumGame(boolean musicEnabled, GameResultListener listener) {
        this("MEDIUM", musicEnabled, listener);
    }

    @Override
    protected void levelUpLogic() {
        if (timeCount - lastLevelUpTime >= 10000) {
            lastLevelUpTime = timeCount;
            if (enemySpawnCycle > 10) {
                enemySpawnCycle -= 2;
            }
        }
    }

    @Override protected boolean supportBoss() { return true; }
    @Override protected void adjustBossHp() { }

    @Override
    protected EnemyFactory createEnemyFactory() {
        double randomValue = Math.random();
        if (randomValue < 0.10) return new EliteProFactory();
        if (randomValue < 0.25) return new ElitePlusFactory();
        if (randomValue < 0.50) return new EliteFactory();
        return new MobFactory();
    }
}
