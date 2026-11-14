package edu.hitsz.aircraftwar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import edu.hitsz.aircraftwar.application.AbstractGame;
import edu.hitsz.aircraftwar.application.GameRenderState;
import edu.hitsz.aircraftwar.application.GameState;
import edu.hitsz.aircraftwar.config.GameConfig;
import edu.hitsz.aircraftwar.input.HeroController;

/** Android Canvas 游戏画布。绘制逻辑坐标 512x768，并按比例居中适配屏幕。 */
public class GameView extends View {
    private final AbstractGame game;
    private final HeroController heroController;
    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect sourceRect = new Rect();
    private final RectF destinationRect = new RectF();

    private float viewportScale = 1.0f;
    private float viewportLeft;
    private float viewportTop;

    public GameView(Context context, AbstractGame game) {
        super(context);
        if (game == null) {
            throw new IllegalArgumentException("game must not be null");
        }
        this.game = game;
        this.heroController = new HeroController(game.getHeroAircraft());
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(22.0f);
        textPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setColor(Color.argb(170, 0, 0, 0));
        overlayPaint.setStyle(Paint.Style.FILL);
    }

    private void updateViewport() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            viewportScale = 1.0f;
            viewportLeft = 0.0f;
            viewportTop = 0.0f;
            return;
        }
        float scaleX = getWidth() / (float) GameConfig.LOGICAL_WIDTH;
        float scaleY = getHeight() / (float) GameConfig.LOGICAL_HEIGHT;
        viewportScale = Math.min(scaleX, scaleY);
        viewportLeft = (getWidth() - GameConfig.LOGICAL_WIDTH * viewportScale) / 2.0f;
        viewportTop = (getHeight() - GameConfig.LOGICAL_HEIGHT * viewportScale) / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        updateViewport();

        GameRenderState state = game.createRenderState();
        int saveCount = canvas.save();
        canvas.translate(viewportLeft, viewportTop);
        canvas.scale(viewportScale, viewportScale);

        drawBackground(canvas, state.backgroundImage, state.backgroundTop);
        drawSprites(canvas, state.enemyBullets);
        drawSprites(canvas, state.heroBullets);
        drawSprites(canvas, state.enemies);
        drawSprites(canvas, state.props);
        if (state.hero != null) {
            drawSprite(canvas, state.hero);
        }
        drawHud(canvas, state);

        if (state.gameState == GameState.GAME_OVER) {
            drawGameOver(canvas, state.score);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawBackground(Canvas canvas, Bitmap bitmap, int backgroundTop) {
        if (bitmap == null) return;
        sourceRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        destinationRect.set(
                0.0f,
                backgroundTop - GameConfig.LOGICAL_HEIGHT,
                GameConfig.LOGICAL_WIDTH,
                backgroundTop);
        canvas.drawBitmap(bitmap, sourceRect, destinationRect, bitmapPaint);
        destinationRect.set(
                0.0f,
                backgroundTop,
                GameConfig.LOGICAL_WIDTH,
                backgroundTop + GameConfig.LOGICAL_HEIGHT);
        canvas.drawBitmap(bitmap, sourceRect, destinationRect, bitmapPaint);
    }

    private void drawSprites(Canvas canvas, Iterable<GameRenderState.Sprite> sprites) {
        for (GameRenderState.Sprite sprite : sprites) {
            drawSprite(canvas, sprite);
        }
    }

    private void drawSprite(Canvas canvas, GameRenderState.Sprite sprite) {
        Bitmap bitmap = sprite.bitmap;
        if (bitmap == null || bitmap.isRecycled()) return;
        canvas.drawBitmap(
                bitmap,
                sprite.centerX - bitmap.getWidth() / 2.0f,
                sprite.centerY - bitmap.getHeight() / 2.0f,
                bitmapPaint);
    }

    private void drawHud(Canvas canvas, GameRenderState state) {
        textPaint.setColor(Color.RED);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(22.0f);
        canvas.drawText("SCORE: " + state.score, 10.0f, 25.0f, textPaint);
        canvas.drawText("LIFE: " + state.heroHp, 10.0f, 48.0f, textPaint);
    }

    private void drawGameOver(Canvas canvas, int finalScore) {
        canvas.drawRect(
                0.0f, 0.0f,
                GameConfig.LOGICAL_WIDTH, GameConfig.LOGICAL_HEIGHT,
                overlayPaint);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(38.0f);
        canvas.drawText("GAME OVER", GameConfig.LOGICAL_WIDTH / 2.0f,
                GameConfig.LOGICAL_HEIGHT / 2.0f, textPaint);
        textPaint.setTextSize(24.0f);
        canvas.drawText("SCORE: " + finalScore, GameConfig.LOGICAL_WIDTH / 2.0f,
                GameConfig.LOGICAL_HEIGHT / 2.0f + 42.0f, textPaint);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            updateViewport();
            if (viewportScale > 0.0f) {
                float logicalX = (event.getX() - viewportLeft) / viewportScale;
                float logicalY = (event.getY() - viewportTop) / viewportScale;
                if (logicalX >= 0.0f && logicalX <= GameConfig.LOGICAL_WIDTH
                        && logicalY >= 0.0f && logicalY <= GameConfig.LOGICAL_HEIGHT
                        && game.getGameState() == GameState.RUNNING) {
                    heroController.moveTo(Math.round(logicalX), Math.round(logicalY));
                    postInvalidate();
                }
            }
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            performClick();
            return true;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
