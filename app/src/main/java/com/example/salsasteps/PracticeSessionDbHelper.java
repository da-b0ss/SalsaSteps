package com.example.salsasteps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PracticeSessionDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PracticeSessions.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SESSIONS = "practice_sessions";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_STEPS = "steps";

    public PracticeSessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SESSIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_DURATION + " INTEGER NOT NULL, " +
                COLUMN_STEPS + " INTEGER NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }

    public void addSession(String date, long durationInMillis, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DURATION, durationInMillis);
        values.put(COLUMN_STEPS, steps);
        db.insert(TABLE_SESSIONS, null, values);
    }

    public void deleteSessionsByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSIONS, COLUMN_DATE + " = ?", new String[]{date});
    }

    public PracticeStats getStatsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        PracticeStats stats = new PracticeStats();

        Cursor cursor = db.query(TABLE_SESSIONS,
                new String[]{"SUM(" + COLUMN_DURATION + ")", "SUM(" + COLUMN_STEPS + ")"},
                COLUMN_DATE + " = ?",
                new String[]{date},
                null, null, null);

        if (cursor.moveToFirst()) {
            stats.totalDuration = cursor.getLong(0);
            stats.totalSteps = cursor.getInt(1);
        }
        cursor.close();
        return stats;
    }

    public static class PracticeStats {
        public long totalDuration = 0;
        public int totalSteps = 0;
    }
}