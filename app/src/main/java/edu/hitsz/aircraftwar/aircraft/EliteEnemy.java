package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.factory.PropFactory;
import edu.hitsz.aircraftwar.prop.AbstractProp;
import edu.hitsz.aircraftwar.strategy.StraightShootStrategy;

import java.util.ArrayList;
import java.util.List;

public class EliteEnemy extends AbstractEnemy {
    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        shootNum = 1;
        power = 10;
        direction = 1;
        shootStrategy = new StraightShootStrategy();
    }

    @Override public void forward() { super.forward(); }
    @Override public int getScore() { return 20; }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> result = new ArrayList<>();
        if (Math.random() < 0.5) {
            String[] types = {"Blood", "Bullet", "BulletPlus"};
            String type = types[(int) (Math.random() * types.length)];
            result.add(PropFactory.createProp(type, locationX, locationY, 0, 5));
        }
        return result;
    }

    @Override public void onIceActive() { freezeWithRecovery(4000); }
}
