package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;

public class BombSupply extends AbstractProp {
    public BombSupply(int x, int y, int speedX, int speedY) { super(x, y, speedX, speedY); }
    public void notifyObservers() { notifyBombObservers(); }
    @Override public void active(HeroAircraft heroAircraft) { notifyObservers(); }
}
