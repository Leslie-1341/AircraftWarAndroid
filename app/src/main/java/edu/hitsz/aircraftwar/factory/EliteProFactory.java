package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.EliteProEnemy;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

public class EliteProFactory implements EnemyFactory {
    @Override public AbstractEnemy createEnemy() {
        int width = ImageManager.get(EliteProEnemy.class).getWidth();
        int speedX = Math.random() < 0.5 ? 4 : -4;
        return new EliteProEnemy((int) (Math.random() * (GameConfig.LOGICAL_WIDTH - width)),
                (int) (Math.random() * GameConfig.LOGICAL_HEIGHT * 0.05), speedX, 7, 120);
    }
}
