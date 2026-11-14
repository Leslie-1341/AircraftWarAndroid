package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.EliteEnemy;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

public class EliteFactory implements EnemyFactory {
    @Override public AbstractEnemy createEnemy() {
        int width = ImageManager.get(EliteEnemy.class).getWidth();
        return new EliteEnemy((int) (Math.random() * (GameConfig.LOGICAL_WIDTH - width)),
                (int) (Math.random() * GameConfig.LOGICAL_HEIGHT * 0.05), 0, 5, 60);
    }
}
