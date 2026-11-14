package edu.hitsz.aircraftwar.basic;

import android.graphics.Bitmap;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.assets.ImageManager;
import edu.hitsz.aircraftwar.config.GameConfig;

/**
 * 所有可飞行对象的基类。坐标始终表示图片中心点。
 */
public abstract class AbstractFlyingObject {
    // HeroController writes these from the UI thread; volatile makes updates visible to GameLoop.
    protected volatile int locationX;
    protected volatile int locationY;
    protected int speedX;
    protected int speedY;
    protected Bitmap image;
    protected int width = -1;
    protected int height = -1;
    protected boolean isValid = true;

    public AbstractFlyingObject() { }

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /** 按速度移动；触碰逻辑画布左右边界时反向。 */
    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= GameConfig.LOGICAL_WIDTH) {
            speedX = -speedX;
        }
    }

    /**
     * 矩形碰撞检测。飞机的纵向碰撞范围按原桌面版规则缩小一半。
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        if (flyingObject == null || flyingObject.notValid() || this.notValid()) {
            return false;
        }
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int otherFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;
        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int combinedWidth = flyingObject.getWidth() + getWidth();
        int combinedHeight = flyingObject.getHeight() / otherFactor + getHeight() / factor;
        return x + combinedWidth / 2 > locationX
                && x - combinedWidth / 2 < locationX
                && y + combinedHeight / 2 > locationY
                && y - combinedHeight / 2 < locationY;
    }

    public int getLocationX() { return locationX; }
    public int getLocationY() { return locationY; }

    public void setLocation(double locationX, double locationY) {
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedX() { return speedX; }
    public int getSpeedY() { return speedY; }
    public void setSpeedX(int speedX) { this.speedX = speedX; }
    public void setSpeedY(int speedY) { this.speedY = speedY; }

    public Bitmap getImage() {
        if (image == null) {
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width < 0) {
            Bitmap bitmap = getImage();
            width = bitmap.getWidth();
        }
        return width;
    }

    public int getHeight() {
        if (height < 0) {
            Bitmap bitmap = getImage();
            height = bitmap.getHeight();
        }
        return height;
    }

    public void vanish() { isValid = false; }
    public boolean notValid() { return !isValid; }
}
