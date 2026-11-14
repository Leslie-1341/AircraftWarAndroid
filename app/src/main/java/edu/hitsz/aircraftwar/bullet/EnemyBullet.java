package edu.hitsz.aircraftwar.bullet;

import edu.hitsz.aircraftwar.observer.PropObserver;

public class EnemyBullet extends BaseBullet implements PropObserver {
    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }
    @Override public void onBombActive() { vanish(); }
    @Override public void onIceActive() { freezeFor(5000); }
}
