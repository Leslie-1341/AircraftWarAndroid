package edu.hitsz.aircraftwar.application;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单帧渲染快照。GameView 只读取该快照，不直接遍历正在被游戏线程修改的集合。
 */
public final class GameRenderState {
    public final Bitmap backgroundImage;
    public final int backgroundTop;
    public final Sprite hero;
    public final List<Sprite> enemyBullets;
    public final List<Sprite> heroBullets;
    public final List<Sprite> enemies;
    public final List<Sprite> props;
    public final int score;
    public final int heroHp;
    public final String difficulty;
    public final GameState gameState;

    public GameRenderState(
            Bitmap backgroundImage,
            int backgroundTop,
            Sprite hero,
            List<Sprite> enemyBullets,
            List<Sprite> heroBullets,
            List<Sprite> enemies,
            List<Sprite> props,
            int score,
            int heroHp,
            String difficulty,
            GameState gameState) {
        this.backgroundImage = backgroundImage;
        this.backgroundTop = backgroundTop;
        this.hero = hero;
        this.enemyBullets = immutableCopy(enemyBullets);
        this.heroBullets = immutableCopy(heroBullets);
        this.enemies = immutableCopy(enemies);
        this.props = immutableCopy(props);
        this.score = score;
        this.heroHp = heroHp;
        this.difficulty = difficulty;
        this.gameState = gameState;
    }

    private static List<Sprite> immutableCopy(List<Sprite> source) {
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    public static final class Sprite {
        public final Bitmap bitmap;
        public final int centerX;
        public final int centerY;

        public Sprite(Bitmap bitmap, int centerX, int centerY) {
            this.bitmap = bitmap;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }
}
