package edu.hitsz.aircraftwar.input;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.config.GameConfig;

/** 将触摸位置转换为游戏逻辑坐标，并限制英雄机不超出逻辑画布。 */
public final class HeroController {
    private final HeroAircraft heroAircraft;

    public HeroController(HeroAircraft heroAircraft) {
        if (heroAircraft == null) {
            throw new IllegalArgumentException("heroAircraft must not be null");
        }
        this.heroAircraft = heroAircraft;
    }

    public void moveTo(int logicalX, int logicalY) {
        int halfWidth = Math.max(1, heroAircraft.getWidth() / 2);
        int halfHeight = Math.max(1, heroAircraft.getHeight() / 2);
        int minX = halfWidth;
        int maxX = Math.max(minX, GameConfig.LOGICAL_WIDTH - halfWidth);
        int minY = halfHeight;
        int maxY = Math.max(minY, GameConfig.LOGICAL_HEIGHT - halfHeight);

        int clampedX = Math.max(minX, Math.min(maxX, logicalX));
        int clampedY = Math.max(minY, Math.min(maxY, logicalY));
        heroAircraft.setLocation(clampedX, clampedY);
    }
}
