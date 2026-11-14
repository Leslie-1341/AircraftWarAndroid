package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.BossEnemy;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

public class BossFactory implements EnemyFactory {
    private final int hp;
    public BossFactory(int hp) { this.hp = hp; }
    @Override public AbstractEnemy createEnemy() {
        return new BossEnemy(GameConfig.LOGICAL_WIDTH / 2,
                ImageManager.get(BossEnemy.class).getHeight() / 2, 3, 0, hp);
    }
}
