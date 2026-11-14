package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.MobEnemy;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

public class MobFactory implements EnemyFactory {
    @Override public AbstractEnemy createEnemy() {
        int width = ImageManager.get(MobEnemy.class).getWidth();
        return new MobEnemy((int) (Math.random() * (GameConfig.LOGICAL_WIDTH - width)),
                (int) (Math.random() * GameConfig.LOGICAL_HEIGHT * 0.05), 0, 10, 30);
    }
}
