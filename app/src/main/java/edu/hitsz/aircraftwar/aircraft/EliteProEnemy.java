package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.factory.PropFactory;
import edu.hitsz.aircraftwar.prop.AbstractProp;
import edu.hitsz.aircraftwar.strategy.ScatterShootStrategy;

import java.util.ArrayList;
import java.util.List;

public class EliteProEnemy extends AbstractEnemy {
    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        shootNum = 3;
        power = 30;
        direction = 1;
        shootStrategy = new ScatterShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        if (locationX <= getWidth() / 2) speedX = Math.abs(speedX);
        else if (locationX >= GameConfig.LOGICAL_WIDTH - getWidth() / 2) speedX = -Math.abs(speedX);
        if (locationY >= GameConfig.LOGICAL_HEIGHT) vanish();
    }

    @Override public int getScore() { return 50; }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> result = new ArrayList<>();
        if (Math.random() < 0.5) {
            double p = Math.random();
            String type;
            if (p < 0.4) type = "Blood";
            else if (p < 0.6) type = "Bullet";
            else if (p < 0.8) type = "Bomb";
            else if (p < 0.9) type = "Freeze";
            else type = "BulletPlus";
            result.add(PropFactory.createProp(type, locationX, locationY, 0, 5));
        }
        return result;
    }

    @Override public void onBombActive() { decreaseHp(50); }
    @Override public void onIceActive() { slowYWithRecovery(5000, 0.5); }
}
