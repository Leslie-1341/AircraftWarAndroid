package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

public class ElitePlusFactory implements EnemyFactory {
    @Override public AbstractEnemy createEnemy() {
        int width = ImageManager.get(ElitePlusEnemy.class).getWidth();
        int speedX = Math.random() < 0.5 ? 3 : -3;
        return new ElitePlusEnemy((int) (Math.random() * (GameConfig.LOGICAL_WIDTH - width)),
                (int) (Math.random() * GameConfig.LOGICAL_HEIGHT * 0.05), speedX, 8, 90);
    }
}
