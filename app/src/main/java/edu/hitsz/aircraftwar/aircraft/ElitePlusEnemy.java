package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.factory.PropFactory;
import edu.hitsz.aircraftwar.prop.AbstractProp;
import edu.hitsz.aircraftwar.strategy.StraightShootStrategy;

import java.util.ArrayList;
import java.util.List;

public class ElitePlusEnemy extends AbstractEnemy {
    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        shootNum = 2;
        power = 20;
        direction = 1;
        shootStrategy = new StraightShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        if (locationX <= getWidth() / 2) speedX = Math.abs(speedX);
        else if (locationX >= GameConfig.LOGICAL_WIDTH - getWidth() / 2) speedX = -Math.abs(speedX);
        if (locationY >= GameConfig.LOGICAL_HEIGHT) vanish();
    }

    @Override public int getScore() { return 30; }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> result = new ArrayList<>();
        if (Math.random() < 0.5) {
            double p = Math.random();
            String type = p < 0.5 ? "Blood" : (p < 0.8 ? "Bullet" : "BulletPlus");
            result.add(PropFactory.createProp(type, locationX, locationY, 0, 5));
        }
        return result;
    }

    @Override public void onIceActive() { freezeWithRecovery(3000); }
}
