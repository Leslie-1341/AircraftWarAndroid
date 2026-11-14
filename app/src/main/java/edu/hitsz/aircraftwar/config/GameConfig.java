package edu.hitsz.aircraftwar.config;

/**
 * Android 版游戏逻辑坐标与更新参数。
 * 第一阶段沿用 PC 版 512x768、40ms 的逻辑参数。
 */
public final class GameConfig {
    public static final int LOGICAL_WIDTH = 512;
    public static final int LOGICAL_HEIGHT = 768;
    public static final int UPDATE_INTERVAL_MS = 40;

    private GameConfig() {
        throw new AssertionError("No instances");
    }
}
