package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.prop.AbstractProp;
import edu.hitsz.aircraftwar.strategy.NoneShootStrategy;

import java.util.Collections;
import java.util.List;

public class MobEnemy extends AbstractEnemy {
    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        shootStrategy = new NoneShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= GameConfig.LOGICAL_HEIGHT) vanish();
    }

    @Override public int getScore() { return 10; }
    @Override public List<AbstractProp> dropProps() { return Collections.emptyList(); }

    @Override
    public void onIceActive() {
        // 普通敌机的冰冻效果为永久冻结。
        setSpeedX(0);
        setSpeedY(0);
    }
}
