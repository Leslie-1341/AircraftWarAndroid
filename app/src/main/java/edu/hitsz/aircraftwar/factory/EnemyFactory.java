package edu.hitsz.aircraftwar.factory;

import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;

/** 工厂方法模式：统一创建敌机的接口。 */
public interface EnemyFactory {
    AbstractEnemy createEnemy();
}
