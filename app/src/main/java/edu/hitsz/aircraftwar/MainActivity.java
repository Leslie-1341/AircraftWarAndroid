package edu.hitsz.aircraftwar;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import edu.hitsz.aircraftwar.application.AbstractGame;
import edu.hitsz.aircraftwar.application.EasyGame;
import edu.hitsz.aircraftwar.application.GameLoop;
import edu.hitsz.aircraftwar.application.GameResultListener;
import edu.hitsz.aircraftwar.application.HardGame;
import edu.hitsz.aircraftwar.application.MediumGame;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.view.GameView;

/**
 * Android 游戏入口。
 * 第三阶段先直接启动简单难度，用于验证 GameView、GameLoop 和原有游戏逻辑的集成。
 * 开始菜单、音频和排行榜将在后续阶段接入。
 */
public class MainActivity extends AppCompatActivity implements GameResultListener {

    /** 首次联调默认使用简单难度；后续接入开始菜单时由菜单选择难度。 */
    private static final String START_DIFFICULTY = "EASY";

    private GameLoop gameLoop;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 原项目采用竖屏逻辑画布 512x768，首轮迁移固定竖屏便于对照测试。
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowCompat.enableEdgeToEdge(getWindow());

        // 必须先加载 Bitmap，再创建飞机、子弹、道具和游戏对象。
        ImageManager.initialize(getApplicationContext());

        AbstractGame game = createGame(START_DIFFICULTY);
        gameView = new GameView(this, game);
        setContentView(gameView);

        gameLoop = new GameLoop(game, gameView);
        gameLoop.start();

        hideSystemBars();
    }

    /** 根据统一难度字符串创建对应游戏实现。 */
    private AbstractGame createGame(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return new EasyGame("EASY", false, this);
            case "MEDIUM":
                return new MediumGame("MEDIUM", false, this);
            case "HARD":
                return new HardGame("HARD", false, this);
            default:
                throw new IllegalArgumentException("Unsupported difficulty: " + difficulty);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemBars();
        if (gameLoop != null) {
            gameLoop.resume();
        }
    }

    @Override
    protected void onPause() {
        if (gameLoop != null) {
            gameLoop.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        gameView = null;
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    /** 沉浸式显示游戏画面；用户从边缘滑动时系统栏可临时显示。 */
    private void hideSystemBars() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    /** 游戏结束回调由 GameLoop 工作线程触发，Toast 必须切回 UI 线程。 */
    @Override
    public void onGameOver(int finalScore, String difficulty) {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            Toast.makeText(
                    MainActivity.this,
                    "游戏结束，难度：" + difficulty + "，得分：" + finalScore,
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}
