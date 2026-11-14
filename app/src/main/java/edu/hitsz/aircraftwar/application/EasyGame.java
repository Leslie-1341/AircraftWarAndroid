package edu.hitsz.aircraftwar.application;

import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.factory.EliteFactory;
import edu.hitsz.aircraftwar.factory.ElitePlusFactory;
import edu.hitsz.aircraftwar.factory.EliteProFactory;
import edu.hitsz.aircraftwar.factory.EnemyFactory;
import edu.hitsz.aircraftwar.factory.MobFactory;

/** 简单难度：不随时间提升难度，不生成 Boss。 */
public class EasyGame extends AbstractGame {
    public EasyGame(String difficulty, boolean musicEnabled) {
        this(difficulty, musicEnabled, null);
    }

    public EasyGame(String difficulty, boolean musicEnabled, GameResultListener listener) {
        super(difficulty, musicEnabled, listener);
        backgroundImage = ImageManager.BACKGROUND_IMAGE_EASY;
        enemyMaxNumber = 5;
        enemySpawnCycle = 20;
        bossThreshold = Integer.MAX_VALUE;
        bossHp = 0;
    }

    public EasyGame(boolean musicEnabled, GameResultListener listener) {
        this("EASY", musicEnabled, listener);
    }

    @Override protected void levelUpLogic() { }
    @Override protected boolean supportBoss() { return false; }
    @Override protected void adjustBossHp() { }

    @Override
    protected EnemyFactory createEnemyFactory() {
        double randomValue = Math.random();
        if (randomValue < 0.05) return new EliteProFactory();
        if (randomValue < 0.15) return new ElitePlusFactory();
        if (randomValue < 0.40) return new EliteFactory();
        return new MobFactory();
    }
}
