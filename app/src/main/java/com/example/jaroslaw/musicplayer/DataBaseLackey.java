package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseLackey extends SQLiteOpenHelper {

    private static final String TAG = "*********";

    private static final String DATABASE_NAME = "DATABASE_NAME";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRACKS = "TRACKS";
    private static final String FIELD_ROW_ID = "_id";
    private static final String FIELD_ARTIST = "ARTIST";
    private static final String FIELD_TITLE = "TITLE";
    private static final String FIELD_DATA = "DATA";
    private static final String FIELD_DISPLAY_NAME = "DISPLAY_NAME";
    private static final String FIELD_DURATION = "DURATION";

    private static final String INDEX_ON_TITLE = "INDEX_ON_TITLE";
    private static final String INDEX_ON_DATA = "INDEX_ON_DATA";

    public DataBaseLackey(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DataBaseLackey(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        upgradeDataBase(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        upgradeDataBase(sqLiteDatabase, 0, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        upgradeDataBase(sqLiteDatabase, oldVersion, newVersion);
    }

    private void upgradeDataBase(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            String tracksCreateQuery = "CREATE TABLE " + TABLE_TRACKS + " (" +
                    FIELD_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_ARTIST + " TEXT NULL," +
                    FIELD_TITLE + " TEXT NOT NULL, " +
                    FIELD_DATA + " TEXT UNIQUE, " +
                    FIELD_DISPLAY_NAME + " TEXT NULL," +
                    FIELD_DURATION + " LONG NOT NULL" +
                    ");";
            sqLiteDatabase.execSQL(tracksCreateQuery);
            Log.d(TAG, "upgradeDataBase: " + tracksCreateQuery);
            String indexOnNameQuery = "CREATE INDEX " + INDEX_ON_TITLE +
                    " ON " + TABLE_TRACKS + "(" + FIELD_TITLE +
                    ");";
            sqLiteDatabase.execSQL(indexOnNameQuery);
            Log.d(TAG, "upgradeDataBase: " + indexOnNameQuery);
            String indexOnPathQuery = "CREATE INDEX " + INDEX_ON_DATA +
                    " ON " + TABLE_TRACKS + "(" + FIELD_DATA +
                    ");";
            sqLiteDatabase.execSQL(indexOnPathQuery);
            Log.d(TAG, "upgradeDataBase: " + indexOnPathQuery);
        }
    }
}
