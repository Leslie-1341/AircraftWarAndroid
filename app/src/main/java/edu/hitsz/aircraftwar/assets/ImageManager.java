package edu.hitsz.aircraftwar.assets;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.hitsz.aircraftwar.aircraft.BossEnemy;
import edu.hitsz.aircraftwar.aircraft.EliteEnemy;
import edu.hitsz.aircraftwar.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraftwar.aircraft.EliteProEnemy;
import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.aircraft.MobEnemy;
import edu.hitsz.aircraftwar.bullet.EnemyBullet;
import edu.hitsz.aircraftwar.bullet.HeroBullet;
import edu.hitsz.aircraftwar.prop.BloodSupply;
import edu.hitsz.aircraftwar.prop.BombSupply;
import edu.hitsz.aircraftwar.prop.BulletPlusSupply;
import edu.hitsz.aircraftwar.prop.BulletSupply;
import edu.hitsz.aircraftwar.prop.FreezeSupply;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Android 图片资源管理器。
 * 图片统一放在 app/src/main/assets/images/，通过 Bitmap 加载。
 * 必须在创建飞机、子弹、道具或调用碰撞检测前 initialize(Context)。
 */
public final class ImageManager {
    private static final String IMAGE_DIR = "images/";
    private static final Map<Class<?>, Bitmap> CLASS_IMAGE_MAP = new HashMap<>();
    private static boolean initialized = false;

    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap BACKGROUND_IMAGE_EASY;
    public static Bitmap BACKGROUND_IMAGE_NORMAL;
    public static Bitmap BACKGROUND_IMAGE_HARD;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ELITE_PLUS_ENEMY_IMAGE;
    public static Bitmap ELITE_PRO_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap PROP_BLOOD_IMAGE;
    public static Bitmap PROP_BULLET_IMAGE;
    public static Bitmap PROP_BULLET_PLUS_IMAGE;
    public static Bitmap PROP_BOMB_IMAGE;
    public static Bitmap PROP_FREEZE_IMAGE;

    private ImageManager() {
        throw new AssertionError("No instances");
    }

    public static synchronized void initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        if (initialized) {
            return;
        }
        AssetManager assets = context.getApplicationContext().getAssets();
        try {
            BACKGROUND_IMAGE = load(assets, "bg.jpg");
            BACKGROUND_IMAGE_EASY = load(assets, "bg2.jpg");
            BACKGROUND_IMAGE_NORMAL = load(assets, "bg3.jpg");
            BACKGROUND_IMAGE_HARD = load(assets, "bg5.jpg");
            HERO_IMAGE = load(assets, "hero.png");
            MOB_ENEMY_IMAGE = load(assets, "mob.png");
            ELITE_ENEMY_IMAGE = load(assets, "elite.png");
            ELITE_PLUS_ENEMY_IMAGE = load(assets, "elitePlus.png");
            ELITE_PRO_ENEMY_IMAGE = load(assets, "elitePro.png");
            BOSS_ENEMY_IMAGE = load(assets, "boss.png");
            HERO_BULLET_IMAGE = load(assets, "bullet_hero.png");
            ENEMY_BULLET_IMAGE = load(assets, "bullet_enemy.png");
            PROP_BLOOD_IMAGE = load(assets, "prop_blood.png");
            PROP_BULLET_IMAGE = load(assets, "prop_bullet.png");
            PROP_BULLET_PLUS_IMAGE = load(assets, "prop_bulletPlus.png");
            PROP_BOMB_IMAGE = load(assets, "prop_bomb.png");
            PROP_FREEZE_IMAGE = load(assets, "prop_freeze.png");

            CLASS_IMAGE_MAP.put(HeroAircraft.class, HERO_IMAGE);
            CLASS_IMAGE_MAP.put(MobEnemy.class, MOB_ENEMY_IMAGE);
            CLASS_IMAGE_MAP.put(EliteEnemy.class, ELITE_ENEMY_IMAGE);
            CLASS_IMAGE_MAP.put(ElitePlusEnemy.class, ELITE_PLUS_ENEMY_IMAGE);
            CLASS_IMAGE_MAP.put(EliteProEnemy.class, ELITE_PRO_ENEMY_IMAGE);
            CLASS_IMAGE_MAP.put(BossEnemy.class, BOSS_ENEMY_IMAGE);
            CLASS_IMAGE_MAP.put(HeroBullet.class, HERO_BULLET_IMAGE);
            CLASS_IMAGE_MAP.put(EnemyBullet.class, ENEMY_BULLET_IMAGE);
            CLASS_IMAGE_MAP.put(BloodSupply.class, PROP_BLOOD_IMAGE);
            CLASS_IMAGE_MAP.put(BulletSupply.class, PROP_BULLET_IMAGE);
            CLASS_IMAGE_MAP.put(BulletPlusSupply.class, PROP_BULLET_PLUS_IMAGE);
            CLASS_IMAGE_MAP.put(BombSupply.class, PROP_BOMB_IMAGE);
            CLASS_IMAGE_MAP.put(FreezeSupply.class, PROP_FREEZE_IMAGE);
            initialized = true;
        } catch (IOException | RuntimeException exception) {
            CLASS_IMAGE_MAP.clear();
            initialized = false;
            throw new IllegalStateException(
                    "Failed to initialize game images. Check app/src/main/assets/images/", exception);
        }
    }

    private static Bitmap load(AssetManager assets, String fileName) throws IOException {
        try (InputStream input = assets.open(IMAGE_DIR + fileName)) {
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            if (bitmap == null) {
                throw new IOException("BitmapFactory returned null for " + fileName);
            }
            return bitmap;
        }
    }

    public static Bitmap get(Class<?> type) {
        if (!initialized) {
            throw new IllegalStateException("ImageManager.initialize(Context) must be called first");
        }
        Bitmap bitmap = CLASS_IMAGE_MAP.get(type);
        if (bitmap == null) {
            throw new IllegalArgumentException("No image registered for " + type.getName());
        }
        return bitmap;
    }

    public static Bitmap get(String className) {
        if (!initialized) {
            throw new IllegalStateException("ImageManager.initialize(Context) must be called first");
        }
        for (Map.Entry<Class<?>, Bitmap> entry : CLASS_IMAGE_MAP.entrySet()) {
            if (entry.getKey().getName().equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Bitmap get(Object object) {
        if (object == null) {
            return null;
        }
        return get(object.getClass());
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
