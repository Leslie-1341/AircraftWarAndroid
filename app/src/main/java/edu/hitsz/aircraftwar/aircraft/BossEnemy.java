package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.factory.PropFactory;
import edu.hitsz.aircraftwar.prop.AbstractProp;
import edu.hitsz.aircraftwar.strategy.RingShootStrategy;

import java.util.ArrayList;
import java.util.List;

public class BossEnemy extends AbstractEnemy {
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        shootNum = 20;
        power = 10;
        direction = 1;
        shootStrategy = new RingShootStrategy();
    }

    @Override
    public void forward() {
        super.forward();
        int rightLimit = GameConfig.LOGICAL_WIDTH - getWidth() / 2;
        if (locationX <= getWidth() / 2) {
            speedX = Math.abs(speedX);
        } else if (locationX >= rightLimit) {
            speedX = -Math.abs(speedX);
        }
    }

    @Override public int getScore() { return 100; }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double probability = Math.random();
            String type;
            if (probability < 0.4) type = "Blood";
            else if (probability < 0.6) type = "Bullet";
            else if (probability < 0.8) type = "Bomb";
            else if (probability < 0.9) type = "Freeze";
            else type = "BulletPlus";
            int dropX = locationX + (i - 1) * 30;
            result.add(PropFactory.createProp(type, dropX, locationY, 0, 5));
        }
        return result;
    }

    @Override public void onBombActive() { /* Boss 不受炸弹影响。 */ }
    @Override public void onIceActive() { /* Boss 不受冰冻影响。 */ }
}
