package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;

public class FreezeSupply extends AbstractProp {
    public FreezeSupply(int x, int y, int speedX, int speedY) { super(x, y, speedX, speedY); }
    public void notifyObservers() { notifyIceObservers(); }
    @Override public void active(HeroAircraft heroAircraft) { notifyObservers(); }
}
