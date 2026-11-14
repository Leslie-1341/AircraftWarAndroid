package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.strategy.ScatterShootStrategy;

public class BulletSupply extends AbstractProp {
    public BulletSupply(int x, int y, int speedX, int speedY) { super(x, y, speedX, speedY); }
    @Override public void active(HeroAircraft heroAircraft) {
        heroAircraft.activateTemporaryShootMode(new ScatterShootStrategy(), 3, 5000L);
    }
}
