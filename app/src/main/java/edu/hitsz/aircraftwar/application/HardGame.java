package edu.hitsz.aircraftwar.application;

import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.factory.EliteFactory;
import edu.hitsz.aircraftwar.factory.ElitePlusFactory;
import edu.hitsz.aircraftwar.factory.EliteProFactory;
import edu.hitsz.aircraftwar.factory.EnemyFactory;
import edu.hitsz.aircraftwar.factory.MobFactory;

/** 困难难度：更快生成敌机，每 10 秒提升一次难度，Boss 血量递增。 */
public class HardGame extends AbstractGame {
    private int lastLevelUpTime;

    public HardGame(String difficulty, boolean musicEnabled) {
        this(difficulty, musicEnabled, null);
    }

    public HardGame(String difficulty, boolean musicEnabled, GameResultListener listener) {
        super(difficulty, musicEnabled, listener);
        backgroundImage = ImageManager.BACKGROUND_IMAGE_HARD;
        enemyMaxNumber = 6;
        enemySpawnCycle = 15;
        bossThreshold = 300;
        bossHp = 800;
    }

    public HardGame(boolean musicEnabled, GameResultListener listener) {
        this("HARD", musicEnabled, listener);
    }

    @Override
    protected void levelUpLogic() {
        if (timeCount - lastLevelUpTime >= 10000) {
            lastLevelUpTime = timeCount;
            if (enemySpawnCycle > 5) {
                enemySpawnCycle -= 3;
            }
        }
    }

    @Override protected boolean supportBoss() { return true; }

    @Override
    protected void adjustBossHp() {
        bossHp += 200;
    }

    @Override
    protected EnemyFactory createEnemyFactory() {
        double randomValue = Math.random();
        if (randomValue < 0.20) return new EliteProFactory();
        if (randomValue < 0.50) return new ElitePlusFactory();
        if (randomValue < 0.80) return new EliteFactory();
        return new MobFactory();
    }
}
