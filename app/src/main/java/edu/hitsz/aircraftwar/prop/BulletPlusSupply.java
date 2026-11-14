package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.strategy.RingShootStrategy;

public class BulletPlusSupply extends AbstractProp {
    public BulletPlusSupply(int x, int y, int speedX, int speedY) { super(x, y, speedX, speedY); }
    @Override public void active(HeroAircraft heroAircraft) {
        heroAircraft.activateTemporaryShootMode(new RingShootStrategy(), 10, 5000L);
    }
}
