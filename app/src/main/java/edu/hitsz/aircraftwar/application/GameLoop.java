package edu.hitsz.aircraftwar.application;

import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.view.GameView;

/** 固定更新周期的游戏循环。游戏逻辑在工作线程运行，View 通过 postInvalidate 请求重绘。 */
public final class GameLoop implements Runnable {
    private final AbstractGame game;
    private final GameView gameView;
    private final Object pauseLock = new Object();

    private volatile boolean running;
    private volatile boolean paused;
    private Thread workerThread;

    public GameLoop(AbstractGame game, GameView gameView) {
        if (game == null || gameView == null) {
            throw new IllegalArgumentException("game and gameView must not be null");
        }
        this.game = game;
        this.gameView = gameView;
    }

    public synchronized void start() {
        if (running) return;
        GameState state = game.getGameState();
        if (state == GameState.GAME_OVER || state == GameState.STOPPED) return;

        game.start();
        running = true;
        paused = false;
        workerThread = new Thread(this, "AircraftWar-GameLoop");
        workerThread.start();
    }

    public void pause() {
        synchronized (pauseLock) {
            if (!running) return;
            paused = true;
            game.pause();
        }
    }

    public void resume() {
        synchronized (pauseLock) {
            if (!running) return;
            paused = false;
            game.resume();
            pauseLock.notifyAll();
        }
    }

    public void stop() {
        Thread threadToInterrupt;
        synchronized (pauseLock) {
            running = false;
            paused = false;
            game.stop();
            pauseLock.notifyAll();
            threadToInterrupt = workerThread;
        }
        if (threadToInterrupt != null) {
            threadToInterrupt.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        final long frameNanos = GameConfig.UPDATE_INTERVAL_MS * 1_000_000L;
        try {
            while (running) {
                synchronized (pauseLock) {
                    while (running && paused) {
                        pauseLock.wait();
                    }
                }
                if (!running) break;

                long frameStart = System.nanoTime();
                game.update();
                gameView.postInvalidate();

                GameState state = game.getGameState();
                if (state == GameState.GAME_OVER || state == GameState.STOPPED) {
                    running = false;
                    break;
                }

                long remainingNanos = frameNanos - (System.nanoTime() - frameStart);
                if (remainingNanos > 0L) {
                    long millis = remainingNanos / 1_000_000L;
                    int nanos = (int) (remainingNanos % 1_000_000L);
                    Thread.sleep(millis, nanos);
                }
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;
        }
    }
}
