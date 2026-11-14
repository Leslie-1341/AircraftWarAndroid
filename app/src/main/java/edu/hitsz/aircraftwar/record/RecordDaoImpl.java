package edu.hitsz.aircraftwar.record;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Android SQLite 版排行榜 DAO。
 * 构造函数改为 RecordDaoImpl(Context, String difficulty)，接口方法保持不变。
 */
public class RecordDaoImpl extends SQLiteOpenHelper implements RecordDao {
    private static final String DATABASE_NAME = "aircraftwar_records.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DIFFICULTY = "difficulty";
    private static final String COLUMN_USER_NAME = "userName";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME = "time";

    private final String difficulty;

    public RecordDaoImpl(Context context, String difficulty) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        if (!"EASY".equals(difficulty) && !"MEDIUM".equals(difficulty) && !"HARD".equals(difficulty)) {
            throw new IllegalArgumentException("Unsupported difficulty: " + difficulty);
        }
        this.difficulty = difficulty;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RECORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DIFFICULTY + " TEXT NOT NULL, "
                + COLUMN_USER_NAME + " TEXT NOT NULL, "
                + COLUMN_SCORE + " INTEGER NOT NULL, "
                + COLUMN_TIME + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 后续如需升级表结构，在这里增加迁移脚本；当前版本不丢弃历史数据。
    }

    @Override
    public synchronized List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_RECORDS,
                new String[]{COLUMN_USER_NAME, COLUMN_SCORE, COLUMN_TIME},
                COLUMN_DIFFICULTY + "=?", new String[]{difficulty},
                null, null, COLUMN_SCORE + " DESC, " + COLUMN_ID + " ASC")) {
            while (cursor.moveToNext()) {
                records.add(new Record(
                        cursor.getString(0), cursor.getInt(1), cursor.getString(2)));
            }
        }
        return records;
    }

    @Override
    public synchronized void doAdd(Record record) {
        if (record == null) throw new IllegalArgumentException("record must not be null");
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_USER_NAME, record.getUserName() == null ? "" : record.getUserName());
        values.put(COLUMN_SCORE, record.getScore());
        values.put(COLUMN_TIME, record.getTime() == null ? "" : record.getTime());
        getWritableDatabase().insertOrThrow(TABLE_RECORDS, null, values);
    }

    @Override
    public synchronized void doDelete(Record record) {
        if (record == null) return;
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_DIFFICULTY + "=? AND " + COLUMN_USER_NAME + "=? AND "
                + COLUMN_SCORE + "=? AND " + COLUMN_TIME + "=?";
        String[] args = {difficulty,
                record.getUserName() == null ? "" : record.getUserName(),
                String.valueOf(record.getScore()),
                record.getTime() == null ? "" : record.getTime()};
        long rowId = -1L;
        try (Cursor cursor = db.query(TABLE_RECORDS, new String[]{COLUMN_ID},
                selection, args, null, null, COLUMN_ID + " ASC", "1")) {
            if (cursor.moveToFirst()) rowId = cursor.getLong(0);
        }
        if (rowId >= 0L) db.delete(TABLE_RECORDS, COLUMN_ID + "=?", new String[]{String.valueOf(rowId)});
    }
}
