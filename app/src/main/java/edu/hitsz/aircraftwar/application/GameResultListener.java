package edu.hitsz.aircraftwar.application;

/** 游戏结束回调。回调发生在游戏循环线程，更新 Android UI 时应切回主线程。 */
public interface GameResultListener {
    void onGameOver(int finalScore, String difficulty);
}
