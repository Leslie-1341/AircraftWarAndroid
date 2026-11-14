package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.observer.PropObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProp extends AbstractFlyingObject {
    protected final List<PropObserver> observers = new ArrayList<>();

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    public abstract void active(HeroAircraft heroAircraft);

    @Override public void forward() { super.forward(); }

    public void addObserver(PropObserver observer) {
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    public void removeObserver(PropObserver observer) { observers.remove(observer); }

    protected void notifyBombObservers() {
        for (PropObserver observer : new ArrayList<>(observers)) observer.onBombActive();
    }

    protected void notifyIceObservers() {
        for (PropObserver observer : new ArrayList<>(observers)) observer.onIceActive();
    }
}
