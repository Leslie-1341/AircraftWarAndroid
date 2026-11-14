package edu.hitsz.aircraftwar.application;

import android.graphics.Bitmap;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.aircraft.AbstractEnemy;
import edu.hitsz.aircraftwar.aircraft.BossEnemy;
import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.factory.BossFactory;
import edu.hitsz.aircraftwar.factory.EnemyFactory;
import edu.hitsz.aircraftwar.observer.PropObserver;
import edu.hitsz.aircraftwar.prop.AbstractProp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 飞机大战核心逻辑。该类不再继承 Swing JPanel，也不负责 Android 绘制、音频或弹窗。
 * 游戏状态由 GameLoop 定时推进，画面由 GameView 根据 createRenderState() 绘制。
 */
public abstract class AbstractGame {
    private int backgroundTop;
    private final HeroAircraft heroAircraft;
    private final List<AbstractEnemy> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    protected double shootCycle = 10.0;
    private int shootCounter;
    private int enemySpawnCounter;
    private int score;
    private int lastBossScore;
    private boolean hasBoss;

    private final String difficulty;
    private final boolean musicEnabled;
    private final GameResultListener gameResultListener;
    private boolean gameOverCallbackSent;
    private GameState gameState = GameState.READY;

    protected int enemyMaxNumber;
    protected int enemySpawnCycle;
    protected int bossThreshold;
    protected int bossHp;
    protected int timeCount;
    protected Bitmap backgroundImage;

    protected AbstractGame(String difficulty, boolean musicEnabled) {
        this(difficulty, musicEnabled, null);
    }

    protected AbstractGame(
            String difficulty,
            boolean musicEnabled,
            GameResultListener gameResultListener) {
        if (!ImageManager.isInitialized()) {
            throw new IllegalStateException(
                    "Call ImageManager.initialize(context) before creating a game.");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("difficulty must not be empty");
        }

        this.difficulty = difficulty;
        this.musicEnabled = musicEnabled;
        this.gameResultListener = gameResultListener;
        this.heroAircraft = HeroAircraft.getInstance();
        this.heroAircraft.reset(
                GameConfig.LOGICAL_WIDTH / 2,
                GameConfig.LOGICAL_HEIGHT - 100);
        this.enemyAircrafts = new LinkedList<>();
        this.heroBullets = new LinkedList<>();
        this.enemyBullets = new LinkedList<>();
        this.props = new LinkedList<>();
    }

    /** 难度随游戏时间递增的模板方法钩子。 */
    protected abstract void levelUpLogic();

    /** 当前难度是否允许生成 Boss。 */
    protected abstract boolean supportBoss();

    /** 每次生成 Boss 前调整 Boss 血量。 */
    protected abstract void adjustBossHp();

    /** 按当前难度选择普通敌机工厂。 */
    protected abstract EnemyFactory createEnemyFactory();

    public synchronized void start() {
        if (gameState == GameState.READY) {
            gameState = GameState.RUNNING;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
        }
    }

    public synchronized void pause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
        }
    }

    public synchronized void resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
        }
    }

    /** 永久停止当前游戏实例；停止后不能通过 resume() 继续。 */
    public synchronized void stop() {
        if (gameState != GameState.GAME_OVER) {
            gameState = GameState.STOPPED;
        }
    }

    /**
     * 推进一帧游戏逻辑。必须由 GameLoop 按 GameConfig.UPDATE_INTERVAL_MS 调用。
     * 结束回调在释放游戏锁之后执行，避免回调中的 UI 操作阻塞状态更新。
     */
    public void update() {
        boolean notifyGameOver = false;
        int finalScore = 0;
        synchronized (this) {
            if (gameState != GameState.RUNNING) {
                return;
            }

            timeCount += GameConfig.UPDATE_INTERVAL_MS;
            heroAircraft.updateTimedEffects();
            levelUpLogic();
            spawnEnemyIfNeeded();
            shootAction();
            bulletsMoveAction();
            aircraftsMoveAction();
            propsMoveAction();
            crashCheckAction();
            postProcessAction();

            backgroundTop++;
            if (backgroundTop >= GameConfig.LOGICAL_HEIGHT) {
                backgroundTop = 0;
            }

            if (heroAircraft.getHp() <= 0) {
                gameState = GameState.GAME_OVER;
                notifyGameOver = !gameOverCallbackSent;
                gameOverCallbackSent = true;
                finalScore = score;
            }
        }

        if (notifyGameOver && gameResultListener != null) {
            gameResultListener.onGameOver(finalScore, difficulty);
        }
    }

    private void spawnEnemyIfNeeded() {
        enemySpawnCounter++;
        if (enemySpawnCounter < enemySpawnCycle || enemyAircrafts.size() >= enemyMaxNumber) {
            return;
        }
        enemySpawnCounter = 0;

        if (score >= lastBossScore + bossThreshold) {
            if (supportBoss() && !hasBoss) {
                adjustBossHp();
                EnemyFactory bossFactory = new BossFactory(bossHp);
                enemyAircrafts.add(bossFactory.createEnemy());
                hasBoss = true;
                lastBossScore += bossThreshold;
            }
            return;
        }

        EnemyFactory enemyFactory = createEnemyFactory();
        if (enemyFactory != null) {
            enemyAircrafts.add(enemyFactory.createEnemy());
        }
    }

    private void shootAction() {
        shootCounter++;
        if (shootCounter < shootCycle) {
            return;
        }
        shootCounter = 0;
        heroBullets.addAll(heroAircraft.shoot());
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid()) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    private void crashCheckAction() {
        // 英雄子弹攻击敌机。
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        if (enemyAircraft instanceof BossEnemy) {
                            hasBoss = false;
                        }
                        enemyAircraft.vanish();
                        score += enemyAircraft.getScore();
                        List<AbstractProp> droppedProps = enemyAircraft.dropProps();
                        if (droppedProps != null) {
                            props.addAll(droppedProps);
                        }
                    }
                }
            }
        }

        // 英雄机与敌机相撞。
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) {
                continue;
            }
            if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                enemyAircraft.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        // 敌机子弹攻击英雄机。
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄机拾取道具，并通过观察者模式通知敌机和敌机子弹。
        for (AbstractProp prop : props) {
            if (prop.notValid() || !heroAircraft.crash(prop)) {
                continue;
            }
            for (AbstractEnemy enemy : enemyAircrafts) {
                if (enemy instanceof PropObserver) {
                    prop.addObserver((PropObserver) enemy);
                }
            }
            for (BaseBullet bullet : enemyBullets) {
                if (bullet instanceof PropObserver) {
                    prop.addObserver((PropObserver) bullet);
                }
            }
            prop.active(heroAircraft);
            prop.vanish();
        }
    }

    private void postProcessAction() {
        removeInvalid(enemyBullets);
        removeInvalid(heroBullets);
        removeInvalid(enemyAircrafts);
        removeInvalid(props);
    }

    private static void removeInvalid(List<? extends AbstractFlyingObject> objects) {
        Iterator<? extends AbstractFlyingObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().notValid()) {
                iterator.remove();
            }
        }
    }

    private static List<GameRenderState.Sprite> createSprites(
            List<? extends AbstractFlyingObject> objects) {
        List<GameRenderState.Sprite> sprites = new ArrayList<>(objects.size());
        for (AbstractFlyingObject object : objects) {
            if (object.notValid()) {
                continue;
            }
            Bitmap bitmap = object.getImage();
            if (bitmap != null) {
                sprites.add(new GameRenderState.Sprite(
                        bitmap, object.getLocationX(), object.getLocationY()));
            }
        }
        return sprites;
    }

    /** 在同一把锁内生成不可变渲染快照，避免 UI 线程直接遍历游戏线程的集合。 */
    public synchronized GameRenderState createRenderState() {
        Bitmap heroBitmap = heroAircraft.getImage();
        GameRenderState.Sprite heroSprite = heroBitmap == null ? null
                : new GameRenderState.Sprite(
                        heroBitmap, heroAircraft.getLocationX(), heroAircraft.getLocationY());
        return new GameRenderState(
                backgroundImage,
                backgroundTop,
                heroSprite,
                createSprites(enemyBullets),
                createSprites(heroBullets),
                createSprites(enemyAircrafts),
                createSprites(props),
                score,
                heroAircraft.getHp(),
                difficulty,
                gameState);
    }

    public synchronized HeroAircraft getHeroAircraft() {
        return heroAircraft;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized String getDifficulty() {
        return difficulty;
    }

    public synchronized GameState getGameState() {
        return gameState;
    }

    public synchronized int getEnemyCount() {
        return enemyAircrafts.size();
    }

    public synchronized int getHeroBulletCount() {
        return heroBullets.size();
    }

    public synchronized int getEnemyBulletCount() {
        return enemyBullets.size();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}
